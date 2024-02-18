package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.dto.TrainerResponseDto;
import com.example.gymhibernatetask.dto.TrainingDto;
import com.example.gymhibernatetask.dto.UpdateTrainerRequestDto;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.TrainingType;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingTypeRepository;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.Message;
import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GymHibernateTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TrainerSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService service;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private ResultActions resultActions;

    private String jwt;

    private TransactionStatus status;

    private final static String API_URL = "/trainers/";

    private static BrokerService broker;

    @BeforeClass
    public static void setUpClass() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        broker.stop();
    }

    @Before
    public void authenticateUser() throws AccountLockedException {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Optional<Token> tokenOpt = tokenRepository.findByUserUsernameAndTokenType("admin", TokenType.BEARER);
        if (tokenOpt.isPresent() && !tokenOpt.get().isExpired() && !tokenOpt.get().isRevoked()) {
            jwt = tokenOpt.get().getToken();
        } else {
            AuthenticationRequest request = new AuthenticationRequest("admin", "admin");
            AuthenticationResponse response = service.authenticate(request);
            jwt = response.getAccessToken();
        }
    }

    @After
    public void rollbackTransaction() {
        transactionManager.rollback(status);
    }

    @Given("authentication is made for fetching Trainers")
    public void anAuthenticatedRequestIsMade() {
        this.jwt = "Bearer " + jwt;
    }

    @When("request for trainer profile with valid username")
    public void requestForTrainerProfileWithValidUsername() throws Exception {
        resultActions = mockMvc.perform(get(API_URL + "trainer1")
                        .header("Authorization", this.jwt))
                .andExpect(status().isOk());
    }

    @Then("should receive trainer profile in response")
    public void shouldReceiveTrainerProfileInResponse() throws Exception {
        String response = resultActions.andReturn().getResponse().getContentAsString();
        TrainerResponseDto trainer = objectMapper.readValue(response, TrainerResponseDto.class);

        assertNotNull(trainer);
        assertEquals("Trainer1", trainer.getFirstName());
        assertEquals("User6", trainer.getLastName());
    }


    @When("request for trainer profile with invalid username")
    public void requestForTrainerProfileWithInvalidUsername() throws Exception {
        resultActions = mockMvc.perform(get(API_URL + "NonExistingTrainer")
                        .header("Authorization", this.jwt))
                .andExpect(status().isNotFound());
    }

    @Then("should receive error message that trainer is not found")
    public void shouldReceiveErrorMessageThatTrainerIsNotFound() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        assertTrue(responseBody.contains("Trainer not found"));
    }

    @When("request to update trainer details with valid username and valid details")
    public void requestToUpdateTrainerDetailsWithValidUsernameAndValidDetails() throws Exception {
        Optional<TrainingType> boxing = trainingTypeRepository.findByTrainingTypeName("Boxing");
        assert boxing.isPresent();
        UpdateTrainerRequestDto updateRequestDto = new
                UpdateTrainerRequestDto("newUsername", "newFirst", "newLast", boxing.get(), true);

        resultActions = mockMvc.perform(put("/trainers")
                        .header("Authorization", this.jwt)
                        .param("username", "trainer5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk());
    }

    @Then("trainer details should be successfully updated")
    public void trainerDetailsShouldBeSuccessfullyUpdated() throws Exception {
        String response = resultActions.andReturn().getResponse().getContentAsString();
        TrainerResponseDto trainer = objectMapper.readValue(response, TrainerResponseDto.class);

        assertNotNull(trainer);
        assertEquals("newFirst", trainer.getFirstName());
        assertEquals("newLast", trainer.getLastName());
        assertEquals("Boxing", trainer.getSpecialization().getTrainingTypeName());
    }

    @When("request to update trainer details with invalid username")
    public void requestToUpdateTrainerDetailsWithInvalidUsernameAndValidDetails() throws Exception {
        UpdateTrainerRequestDto updateRequestDto = new
                UpdateTrainerRequestDto("New", "first", "last", new TrainingType(), true);

        resultActions = mockMvc.perform(put("/trainers")
                        .header("Authorization", this.jwt)
                        .param("username", "NonExisting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound());
    }

    @When("request for a list of trainer trainings")
    public void requestForListOfTrainerTrainings() throws Exception {
        String trainerUsername = "trainer1";

        resultActions = mockMvc.perform(get("/trainers/{trainerUsername}/trainings", trainerUsername)
                        .header("Authorization", this.jwt)
                        .param("periodFrom", LocalDate.of(2024, 2, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .param("periodTo", LocalDate.of(2024, 6, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect((status().isOk()));
    }

    @Then("should get a list of trainer trainings within that period")
    public void getTrainerTrainingsWithinPeriod() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainingDto> trainings = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertFalse(trainings.isEmpty());

        for (TrainingDto trainingDto : trainings) {
            Date trainingDate = trainingDto.getDate();
            assertTrue(trainingDate.after(Date.from(LocalDate.of(2024, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
            assertTrue(trainingDate.before(Date.from(LocalDate.of(2024, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }
    }

    @When("request for training list of non-existing trainer")
    public void requestForTrainingListOfNonExistingTrainer() throws Exception {
        resultActions = mockMvc.perform(get("/trainers/nonExistTrainer/trainings")
                        .header("Authorization", this.jwt)
                        .param("periodFrom", LocalDate.of(2024, 2, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .param("periodTo", LocalDate.of(2024, 6, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect((status().isNotFound()));
    }

    @When("request to change the active status of trainer with valid username")
    public void requestToChangeActiveStatusOfTrainerWithValidUsername() throws Exception {
        resultActions = mockMvc.perform(patch("/trainers/change-active-status")
                        .header("Authorization", this.jwt)
                        .param("username", "trainer5")
                        .param("activeStatus", Boolean.toString(false)))
                .andExpect(status().isNoContent());
    }

    @Then("the trainer active status should be successfully changed")
    public void checkTrainerActiveStatusChanged() {
        Optional<Trainer> trainerByUserUsername = trainerRepository.getTrainerByUserUsername("trainer5");
        assertTrue(trainerByUserUsername.isPresent() && !trainerByUserUsername.get().getUser().isActive());
    }

    @When("request to change the active status of trainer with invalid username")
    public void requestToChangeActiveStatusOfTrainerWithInvalidUsername() throws Exception {
        resultActions = mockMvc.perform(patch("/trainers/change-active-status")
                        .header("Authorization", this.jwt)
                        .param("username", "NonExistingTrainer")
                        .param("activeStatus", Boolean.toString(true)))
                .andExpect(status().isNotFound());
    }

    @When("a get request for trainer summary is made")
    public void aGetRequestForTrainerSummaryIsMade() throws Exception {
        this.resultActions = mockMvc.perform(get("/trainers/summary/trainer4")
                .header("Authorization", this.jwt)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Then("verify the queue has received the message")
    public void aMessageHasBeenSentToTheQueue() throws InterruptedException {
        Thread.sleep(1000);

        Message message = jmsTemplate.receive("getTrainerSummary.queue");

        assertNotNull(message);
    }
}