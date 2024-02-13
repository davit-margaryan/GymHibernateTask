package com.example.gymhibernatetask.integration;

import com.example.gymhibernatetask.GymHibernateTaskApplication;
import com.example.gymhibernatetask.auth.AuthenticationRequest;
import com.example.gymhibernatetask.auth.AuthenticationResponse;
import com.example.gymhibernatetask.auth.AuthenticationService;
import com.example.gymhibernatetask.token.Token;
import com.example.gymhibernatetask.token.TokenRepository;
import com.example.gymhibernatetask.token.TokenType;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CucumberContextConfiguration
@SpringBootTest(classes = GymHibernateTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TrainingTypeSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService service;

    @Autowired
    private TokenRepository tokenRepository;

    private String authToken;

    private TransactionStatus status;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Before
    public void authenticateUser() throws AccountLockedException {
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        Optional<Token> tokenOpt = tokenRepository.findByUserUsernameAndTokenType("admin", TokenType.BEARER);
        if (tokenOpt.isPresent() && !tokenOpt.get().isExpired() && !tokenOpt.get().isRevoked()) {
            authToken = tokenOpt.get().getToken();
        } else {
            AuthenticationRequest request = new AuthenticationRequest("admin", "admin");
            AuthenticationResponse response = service.authenticate(request);
            authToken = response.getAccessToken();
        }
    }

    @After
    public void rollbackTransaction() {
        transactionManager.rollback(status);
    }

    @Given("an authenticated request is made for all training types")
    public void anAuthenticatedRequestIsMade() throws Exception {
        this.authToken = "Bearer " + authToken;
    }

    @When("the training types are being retrieved")
    public void theTrainingTypesAreBeingRetrieved() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/training-types").header("Authorization", this.authToken))
                .andReturn();
    }

    @Then("all training types are fetched successfully")
    public void theResponseIsSuccessfulAndAListOfTrainingTypesIsReturned() throws Exception {
        this.mockMvc.perform(get("/training-types").header("Authorization", this.authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(List.class)));
    }

    @When("an unauthenticated user tries to fetch all training types")
    public void unauthenticatedUserTriesToFetch() throws Exception {
        mockMvc.perform(get("/training-types"))
                .andReturn();
    }

    @Then("the server responds with a Forbidden status code")
    public void theServerRespondsWithForbidden() throws Exception {
        mockMvc.perform(get("/training-types"))
                .andExpect(status().isForbidden());
    }
}