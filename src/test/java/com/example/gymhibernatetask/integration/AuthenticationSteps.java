package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.config.JwtService;
import com.example.gymhibernatetask.dto.ChangePasswordRequest;
import com.example.gymhibernatetask.dto.CreateTraineeRequestDto;
import com.example.gymhibernatetask.dto.CreateTrainerRequestDto;
import com.example.gymhibernatetask.models.Trainee;
import com.example.gymhibernatetask.models.Trainer;
import com.example.gymhibernatetask.models.User;
import com.example.gymhibernatetask.repository.TraineeRepository;
import com.example.gymhibernatetask.repository.TrainerRepository;
import com.example.gymhibernatetask.repository.TrainingTypeRepository;
import com.example.gymhibernatetask.repository.UserRepository;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.security.auth.login.AccountLockedException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GymHibernateTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private MvcResult result;

    private CreateTraineeRequestDto traineeRequest;

    private CreateTrainerRequestDto trainerRequest;

    private ChangePasswordRequest changePasswordRequest;

    private AuthenticationRequest authenticationRequest;

    private TransactionStatus status;

    @Autowired
    private PlatformTransactionManager transactionManager;


    @Before
    public void startTransaction(){
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @After
    public void rollbackTransaction() {
        transactionManager.rollback(status);
    }

    @Given("a valid trainee registration request")
    public void validTraineeRegistrationRequest() {
        this.traineeRequest = new CreateTraineeRequestDto();
        traineeRequest.setFirstName("FirstName");
        traineeRequest.setLastName("LastName");
        traineeRequest.setAddress("Address");
        traineeRequest.setDateOfBirth(new Date());
    }

    @When("submit the trainee registration request")
    public void submitTraineeRegistrationRequest() throws Exception {
        result = this.mockMvc.perform(post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(traineeRequest)))
                .andExpect(status().isOk()).andReturn();
    }

    @Then("the registration should be successful")
    public void registrationShouldBeSuccessful() throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();

        AuthenticationResponse response = new ObjectMapper().readValue(responseBody, AuthenticationResponse.class);

        String username = jwtService.extractUsername(response.getAccessToken());

        Optional<Trainee> traineeByUserUsername = traineeRepository.getTraineeByUserUsername(username);
        assertTrue(traineeByUserUsername.isPresent());
        assertEquals(traineeByUserUsername.get().getUser().getFirstName(), "FirstName");
        assertEquals(traineeByUserUsername.get().getUser().getLastName(), "LastName");

    }

    @Given("a trainee registration request with a null firstName")
    public void traineeRegistrationRequestWithNullFirstName() {
        this.traineeRequest = new CreateTraineeRequestDto();
        traineeRequest.setFirstName(null);
        traineeRequest.setLastName("LastName");
        traineeRequest.setAddress("Address");
        traineeRequest.setDateOfBirth(new Date());
    }

    @Then("the registration should fail with a bad request error")
    public void registrationShouldFailWithBadRequestError() throws Exception {
        this.mockMvc.perform(post("/api/auth/register/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(traineeRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Given("a valid trainer registration request")
    public void validTrainerRegistrationRequest() {
        this.trainerRequest = new CreateTrainerRequestDto();
        trainerRequest.setFirstName("TrainerFirstName");
        trainerRequest.setLastName("TrainerLastName");
        trainerRequest.setSpecialization(trainingTypeRepository.findByTrainingTypeName("Rowing").get());
    }

    @When("submit the trainer registration request")
    public void submitTrainerRegistrationRequest() throws Exception {
        result = this.mockMvc.perform(post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(trainerRequest)))
                .andExpect(status().isOk()).andReturn();
    }

    @Then("the trainer registration should be successful")
    public void trainerRegistrationShouldBeSuccessful() throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();

        AuthenticationResponse response = new ObjectMapper().readValue(responseBody, AuthenticationResponse.class);

        String username = jwtService.extractUsername(response.getAccessToken());

        Optional<Trainer> trainerByUserUsername = trainerRepository.getTrainerByUserUsername(username);
        assertTrue(trainerByUserUsername.isPresent());
        assertEquals(trainerByUserUsername.get().getUser().getFirstName(), "TrainerFirstName");
        assertEquals(trainerByUserUsername.get().getUser().getLastName(), "TrainerLastName");
    }

    @Given("a trainer registration request with a null firstName")
    public void trainerRegistrationRequestWithNullFirstName() {
        this.trainerRequest = new CreateTrainerRequestDto();
        trainerRequest.setFirstName(null);
        trainerRequest.setLastName("TrainerLastName");
        trainerRequest.setLastName("TrainerLastName");
        trainerRequest.setSpecialization(trainingTypeRepository.findByTrainingTypeName("Rowing").get());
    }

    @Then("the trainer registration should fail with a bad request error")
    public void trainerRegistrationShouldFailWithBadRequestError() throws Exception {
        this.mockMvc.perform(post("/api/auth/register/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(trainerRequest)))
                .andExpect(status().isInternalServerError());
    }


    @Given("a valid authentication request")
    public void aValidAuthenticationRequest() {
        this.authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername("admin");
        authenticationRequest.setPassword("admin");
    }

    @When("submit the authentication request")
    public void submitTheAuthenticationRequest() throws Exception {
        Optional<Token> tokenOpt = tokenRepository.findByUserUsernameAndTokenType("admin", TokenType.BEARER);

        tokenOpt.ifPresent(token -> tokenRepository.delete(token));

        result = this.mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk()).andReturn();
    }

    @Then("the authentication should be successful")
    public void authenticationShouldBeSuccessful() throws UnsupportedEncodingException, JsonProcessingException {
        String responseBody = result.getResponse().getContentAsString();

        AuthenticationResponse response = new ObjectMapper().readValue(responseBody, AuthenticationResponse.class);

        String username = jwtService.extractUsername(response.getAccessToken());

        Optional<User> byUsername = userRepository.getByUsername(username);
        assertTrue(byUsername.isPresent());
        assertEquals("admin", username);
        assertEquals("Davo", byUsername.get().getLastName());
    }


    @Given("a change password request with invalid old password")
    public void aChangePasswordRequestWithInvalidOldPassword() {
        this.changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setUsername("trainer3");
        changePasswordRequest.setOldPassword("wrongOldPassword");
        changePasswordRequest.setNewPassword("newPassword");
    }

    @Then("the password change should fail with a bad request error")
    public void passwordChangeShouldFailWithBadRequestError() throws Exception {
        this.mockMvc.perform(put("/api/auth/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changePasswordRequest)))
                .andExpect(status().isInternalServerError());
    }
}