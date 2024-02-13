package com.example.reportmicro.integration;

import com.example.reportmicro.ReportMicroApplication;
import com.example.reportmicro.dto.TrainerWorkloadRequest;
import com.example.reportmicro.exception.InvalidInputException;
import com.example.reportmicro.model.TrainerSummary;
import com.example.reportmicro.model.TrainerWorkload;
import com.example.reportmicro.repo.TrainerSummaryRepository;
import com.example.reportmicro.repo.TrainerWorkloadRepository;
import com.example.reportmicro.service.TrainerWorkloadService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@CucumberContextConfiguration
@SpringBootTest(classes = ReportMicroApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerWorkloadSteps {

    @SpyBean
    private TrainerWorkloadService service;
    private String username;
    private Exception exception;

    private TrainerWorkloadRequest request;

    @SpyBean
    private TrainerWorkloadRepository repository;

    @SpyBean
    private TrainerSummaryRepository summaryRepository;

    private String correlationId;

    @Given("a valid trainer workload request is made")
    public void aValidTrainerWorkloadRequestIsMade() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date futureDate = calendar.getTime();

        request = new TrainerWorkloadRequest();
        request.setUsername("newUsername");
        request.setFirstName("FirstName");
        request.setLastName("LastName");
        request.setTraineeUsername("Trainee");
        request.setActive(true);
        request.setTrainingDate(futureDate);
        request.setTrainingDuration(2);
        request.setActionType("ADD");

        correlationId = "sampleCorrelationId";
    }

    @When("the workload is being assigned")
    public void theWorkloadIsBeingAssigned() {
        service.manageTrainerWorkload(request, correlationId);
    }

    @Then("the trainer workload is managed successfully")
    public void theTrainerWorkloadIsManagedSuccessfully() {
        Mockito.verify(service, Mockito.times(1)).manageTrainerWorkload(request, correlationId);
        List<TrainerWorkload> workloads = repository.getAllByUsername(request.getUsername());
        assertEquals(1, workloads.size());
        TrainerWorkload workload = workloads.get(0);

        assertEquals(request.getUsername(), workload.getUsername());
        assertEquals(request.getFirstName(), workload.getFirstName());
    }

    @Given("an invalid trainer workload request is made")
    public void anInvalidTrainerWorkloadRequestIsMade() {
        request = new TrainerWorkloadRequest();
        request.setActionType("ActionType");
    }

    @When("an attempt is made to assign the workload")
    public void anAttemptIsMadeToAssignTheWorkload() {
        try {
            service.manageTrainerWorkload(request, correlationId);
        } catch (IllegalArgumentException e) {
            exception = e;
        }
    }

    @Then("an error is returned indicating the workload request is invalid")
    public void anErrorIsReturnedIndicatingTheWorkloadRequestIsInvalid() {
        assertNotNull(exception);
        assertInstanceOf(IllegalArgumentException.class, exception);
    }


    @Given("a valid trainer summary request is made")
    public void aValidTrainerSummaryRequestIsMade() {
        username = "trainer1";
    }

    @When("the summary is being retrieved")
    public void theSummaryIsBeingRetrieved() {
        service.calculateSummary(username, "sampleCorrelationId");
    }

    @Then("the trainer summary is retrieved successfully")
    public void theTrainerSummaryIsRetrievedSuccessfully() {
        Mockito.verify(service, Mockito.times(1)).calculateSummary(username, "sampleCorrelationId");

        Optional<TrainerSummary> summary = summaryRepository.findByUsername(username);
        assertTrue(summary.isPresent());
        assertEquals("Trainer1", summary.get().getFirstName());
        assertEquals("User6", summary.get().getLastName());

    }

    @Given("an invalid trainer summary request is made")
    public void anInvalidTrainerSummaryRequestIsMade() {
        username = "";
    }

    @When("an attempt is made to retrieve the summary")
    public void anAttemptIsMadeToRetrieveTheSummary() {
        try {
            service.calculateSummary(username, "sampleCorrelationId");
        } catch (InvalidInputException e) {
            exception = e;
        }
    }

    @Then("an error is returned indicating the summary request is invalid")
    public void anErrorIsReturnedIndicatingTheSummaryRequestIsInvalid() {
        assertNotNull(exception);
        assertInstanceOf(InvalidInputException.class, exception);
    }
}