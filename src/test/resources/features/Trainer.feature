@Trainer
Feature: Trainers Management

  @SuccessfullyFetchedTrainerProfile
  Scenario: Get trainer profile for valid username
    Given authentication is made for fetching Trainers
    When request for trainer profile with valid username
    Then should receive trainer profile in response

  @NonExistentTraineesUsername
  Scenario: Get trainer profile for invalid username
    Given authentication is made for fetching Trainers
    When request for trainer profile with invalid username
    Then should receive error message that trainer is not found

  @SuccessfullyUpdateTrainer
  Scenario: Update trainer details for valid username
    Given authentication is made for fetching Trainers
    When request to update trainer details with valid username and valid details
    Then trainer details should be successfully updated

  @FailToUpdateTrainer
  Scenario: Update trainer details for invalid username
    Given authentication is made for fetching Trainers
    When request to update trainer details with invalid username
    Then should receive error message that trainer is not found

  @SuccessfullyGetListOfTrainer'sTrainings
  Scenario: Get trainer training list for valid period
    Given authentication is made for fetching Trainers
    When request for a list of trainer trainings
    Then should get a list of trainer trainings within that period

  @FailToGetListOfTrainer'sTrainings
  Scenario: Get trainer training list for non-existing trainer
    Given authentication is made for fetching Trainers
    When request for training list of non-existing trainer
    Then should receive error message that trainer is not found

  @SuccessfullyChangeActiveStatusOfTrainer
  Scenario: Change active status of trainer
    Given authentication is made for fetching Trainers
    When request to change the active status of trainer with valid username
    Then the trainer active status should be successfully changed

  @FailTOChangeActiveStatusOfTrainer
  Scenario: Try to change active status of a non-existing trainer
    Given authentication is made for fetching Trainers
    When request to change the active status of trainer with invalid username
    Then should receive error message that trainer is not found

  @SuccessfullyChangeActiveStatusOfTrainer
  Scenario: Get Trainer Summary Data
    Given authentication is made for fetching Trainers
    When a get request for trainer summary is made
    Then verify the queue has received the message
