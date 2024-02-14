package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.dto.*;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.activemq.command.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.security.auth.login.AccountLockedException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GymHibernateTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TraineSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService service;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JmsTemplate jmsTemplate;

    private UpdateTraineeRequestDto updateRequestDto;

    private MvcResult result;

    private ResultActions resultActions;

    private String jwt;

    private TransactionStatus status;


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
        jmsTemplate.browse("manageTrainerWorkload.queue", (session, browser) -> {
            Enumeration<?> enumeration = browser.getEnumeration();
            while (enumeration.hasMoreElements()) {
                Message message = (Message) enumeration.nextElement();
                jmsTemplate.receiveSelected("manageTrainerWorkload.queue", "JMSMessageID = '" + message.getCorrelationId() + "'");
            }
            return null;
        });
    }

    @Given("Authentication to request")
    public void anAuthenticatedRequestIsMadeForFetchingTrainees() throws Exception {
        this.jwt = "Bearer " + jwt;
    }

    @When("request to delete a trainee's profile")
    public void requestToDeleteTraineeProfile() throws Exception {
        String deleteUsername = "trainee3";

        transactionManager.getTransaction(new DefaultTransactionDefinition());
        mockMvc.perform(delete("/trainees")
                        .header("Authorization", this.jwt)
                        .param("deleteUsername", deleteUsername))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Then("the response status should be 204")
    public void responseStatusShouldBe204() {
        TrainerWorkloadRequest trainerWorkloadRequest = (TrainerWorkloadRequest) jmsTemplate.receiveAndConvert("manageTrainerWorkload.queue");

        assert trainerWorkloadRequest != null;
        assertEquals("trainee3", trainerWorkloadRequest.getTraineeUsername());
    }

    @When("request to fetch a trainee's profile")
    public void fetchTraineeProfile() throws Exception {
        String searchUsername = "trainee4";

        result = mockMvc.perform(get("/trainees/{searchUsername}", searchUsername)
                        .header("Authorization", this.jwt))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("trainee's profile is returned")
    public void traineeProfileIsReturned() throws UnsupportedEncodingException, JsonProcessingException {
        String response = result.getResponse().getContentAsString();
        TraineeResponseDto traineeResponseDto = objectMapper.readValue(response, TraineeResponseDto.class);

        assertEquals("Trainee4", traineeResponseDto.getFirstName());
        assertEquals("User4", traineeResponseDto.getLastName());
    }

    @When("I request to fetch a non-existent trainee's profile")
    public void requestToFetchNonExistentTraineeProfile() throws Exception {
        String searchUsername = "nonExistentTraineeUsername";

        result = mockMvc.perform(get("/trainees/{searchUsername}", searchUsername)
                        .header("Authorization", this.jwt))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Then("should receive an error message that trainee not found")
    public void errorThrew() {
        assertEquals(result.getResponse().getStatus(), HttpStatus.NOT_FOUND.value());
    }


    @When("request to update a trainee's profile")
    public void updateTraineeProfile() throws Exception {
        String username = "trainee3";

        updateRequestDto = new UpdateTraineeRequestDto();
        updateRequestDto.setUsername("NewUsername");
        updateRequestDto.setAddress("NewAddress");
        updateRequestDto.setActive(true);
        updateRequestDto.setDateOfBirth(new Date());
        updateRequestDto.setLastName("Last");
        updateRequestDto.setFirstName("First");

        mockMvc.perform(put("/trainees")
                        .header("Authorization", this.jwt)
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("the trainee's profile should be updated")
    public void traineeProfileShouldBeUpdated() {
        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername("NewUsername");

        assertTrue(traineeByUserUsername.isPresent());
        assertEquals("NewUsername", traineeByUserUsername.get().getUser().getUsername());
        assertEquals("NewAddress", traineeByUserUsername.get().getAddress());

    }

    @When("request to update a non-existent trainee's profile")
    public void updateNonExistentTraineeProfile() throws Exception {
        String username = "nonExistentTraineeUsername";

        updateRequestDto = new UpdateTraineeRequestDto();

        result = mockMvc.perform(put("/trainees")
                        .header("Authorization", this.jwt)
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @When("request for a list of trainings")
    public void requestForListOfTrainings() throws Exception {
        String traineeUsername = "trainee2";

        resultActions = mockMvc.perform(get("/trainees/{traineeUsername}/trainings", traineeUsername)
                        .header("Authorization", this.jwt)
                        .param("periodFrom", LocalDate.of(2024, 2, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .param("periodTo", LocalDate.of(2024, 6, 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect((status().isOk()));
    }

    @Then("should get a list of trainings within that period")
    public void getTrainingsWithinPeriod() throws Exception {
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

    @When("request for a list of available trainers")
    public void requestForListOfAvailableTrainers() throws Exception {
        String traineeUsername = "trainee3";

        resultActions = mockMvc.perform(get("/trainees/{traineeUsername}/available-trainers", traineeUsername)
                        .header("Authorization", this.jwt))
                .andExpect(status().isOk());
    }

    @Then("should get a list of available trainers")
    public void getListOfAvailableTrainers() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainerListResponseDto> trainers = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assertFalse(trainers.isEmpty());
    }

    @When("request for a list of available trainers for a non-existent trainee")
    public void requestForListOfAvailableTrainersForNonExistentTrainee() throws Exception {
        String traineeUsername = "nonExistentTraineeUsername";

        result = mockMvc.perform(get("/trainees/{traineeUsername}/available-trainers", traineeUsername)
                        .header("Authorization", this.jwt))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @When("request to update my list of trainers")
    public void requestToUpdateListOfTrainers() throws Exception {
        String traineeUsername = "trainee4";

        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");

        resultActions = mockMvc.perform(put("/trainees/update-trainers")
                        .header("Authorization", this.jwt)
                        .param("username", traineeUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUsernames)))
                .andExpect(status().isOk());
    }

    @Then("list of trainers should be updated")
    public void checkListOfTrainersAreUpdated() throws Exception {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        List<TrainerListResponseDto> updatedTrainers = objectMapper.readValue(responseBody, new TypeReference<List<TrainerListResponseDto>>() {
        });

        assertFalse(updatedTrainers.isEmpty());
        assertEquals(2, updatedTrainers.size());
    }

    @When("request to update trainers for non-existing trainee")
    public void requestToUpdateTrainersForNonExistingTrainee() throws Exception {
        String nonExistTraineeUsername = "nonExistTrainee";

        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");

        result = mockMvc.perform(put("/trainees/update-trainers")
                        .header("Authorization", this.jwt)
                        .param("username", nonExistTraineeUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerUsernames)))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @When("request to change the active status of my trainee profile")
    public void requestToChangeActiveStatus() throws Exception {
        String traineeUsername = "trainee1";
        boolean newStatus = false;

        resultActions = mockMvc.perform(patch("/trainees/change-active-status")
                        .header("Authorization", this.jwt)
                        .param("username", traineeUsername)
                        .param("activeStatus", Boolean.toString(newStatus))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Then("the trainee active status should be successfully changed")
    public void checkActiveStatusChanged() {
        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername("trainee1");
        assertTrue(traineeByUserUsername.isPresent() && !traineeByUserUsername.get().getUser().isActive());
    }
}
