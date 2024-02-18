package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.dto.CreateTrainingRequestDto;
import com.example.gymhibernatetask.models.Training;
import com.example.gymhibernatetask.repository.TrainingRepository;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.security.auth.login.AccountLockedException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GymHibernateTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CreateTrainingSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService service;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Date futureDate;

    private CreateTrainingRequestDto requestDto;

    private String jwt;

    private TransactionStatus status;

    private ResultActions resultActions;

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

    @Given("an authenticated request")
    public void anAuthenticatedRequestIsMadeForCreatingTraining() {
        this.jwt = "Bearer " + jwt;
    }

    @When("create a training")
    public void createATraining() throws Exception {
        requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername("trainee4");
        requestDto.setTrainerUsername("trainer4");
        requestDto.setTrainingName("Bodybuilding");

        LocalDateTime date = LocalDateTime.now().plusWeeks(1);
        futureDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        requestDto.setDate(futureDate);

        requestDto.setDuration(60);

        mockMvc.perform(post("/trainings")
                .header("Authorization", this.jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
    }

    @Then("the response status should be created")
    public void trainingShouldBeCreated() throws InterruptedException {
        Optional<Training> optionalTraining = trainingRepository
                .findFirstByTraineeUserUsernameAndTrainerUserUsernameOrderByIdDesc(requestDto.getTraineeUsername(), requestDto.getTrainerUsername());

        assertTrue(optionalTraining.isPresent());
        Training training = optionalTraining.get();

        assertEquals(requestDto.getTraineeUsername(), training.getTrainee().getUser().getUsername());
        assertEquals(requestDto.getTrainerUsername(), training.getTrainer().getUser().getUsername());

        Thread.sleep(1000);

        Message message = jmsTemplate.receive("manageTrainerWorkload.queue");

        assertNotNull(message);
    }

    @When("try to create a training with a non-existent username")
    public void tryToCreateATrainingWithNonExistentUsername() throws Exception {
        requestDto = new CreateTrainingRequestDto();
        requestDto.setTraineeUsername("nonExistentTraineeUsername");
        requestDto.setTrainerUsername("nonExistentTrainerUsername");
        requestDto.setTrainingName("Bodybuilding");

        LocalDateTime date = LocalDateTime.now().plusWeeks(1);
        futureDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        requestDto.setDate(futureDate);

        requestDto.setDuration(60);

        resultActions = mockMvc.perform(post("/trainings")
                        .header("Authorization", this.jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Then("the response status should be 400")
    public void responseStatusShouldBe404() throws UnsupportedEncodingException {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        assertTrue(responseBody.contains("Trainee or Trainer does not exists"));
    }
}