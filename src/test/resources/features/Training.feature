@Training
Feature: Create Training

  @SuccessfullyCreatingTraining
  Scenario: Successfully creating a new training
    Given an authenticated request
    When create a training
    Then the response status should be created

  @FailToCreateTraining
  Scenario: Fail to create a new training
    Given an authenticated request
    When try to create a training with a non-existent username
    Then the response status should be 400