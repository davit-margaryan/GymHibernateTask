@Trainee
Feature: Trainees Management

  @SuccessfullyTraineeDelete
  Scenario: Successfully delete a trainee
    Given Authentication to request
    When request to delete a trainee's profile
    Then the response status should be NoContent

  @SuccessfullyFetchedTraineesProfile
  Scenario: Fetch Trainee profile Successfully
    Given Authentication to request
    When request to fetch a trainee's profile
    Then trainee's profile is returned

  @NonExistentTraineesUsername
  Scenario: Fail to fetch a non-existent trainee's profile
    Given Authentication to request
    When I request to fetch a non-existent trainee's profile
    Then should receive an error message that trainee not found

  @SuccessfullyUpdateTrainee
  Scenario: Update Trainee profile Successfully
    Given Authentication to request
    When request to update a trainee's profile
    Then the trainee's profile should be updated

  @FailToUpdateTrainee
  Scenario: Fail to update a non-existent trainee's profile
    Given Authentication to request
    When request to update a non-existent trainee's profile
    Then should receive an error message that trainee not found

  @SuccessfullyGetListOfTrainee'sTrainings
  Scenario: Get a list of trainings successfully
    Given Authentication to request
    When request for a list of trainings
    Then should get a list of trainings within that period

  @SuccessfullyGetListOfAvailableTrainersForTrainee
  Scenario: Get a list of available trainers successfully
    Given Authentication to request
    When request for a list of available trainers
    Then should get a list of available trainers

  @FailToGetAListOfAvailableTrainers
  Scenario: Fail to get a list of available trainers for non-existent trainee
    Given Authentication to request
    When request for a list of available trainers for a non-existent trainee
    Then should receive an error message that trainee not found

  @SuccessfullyUpdateTheListOfTrainers
  Scenario: Update the list of trainers successfully
    Given Authentication to request
    When request to update my list of trainers
    Then list of trainers should be updated

  @FailToUpdateTheListOfTrainers
  Scenario: Update list of trainers for non-existent user
    Given Authentication to request
    When request to update trainers for non-existing trainee
    Then should receive an error message that trainee not found

  @SuccessfullyChangeTraineeStatus
  Scenario: Change active status of trainee
    Given Authentication to request
    When request to change the active status of my trainee profile
    Then the trainee active status should be successfully changed