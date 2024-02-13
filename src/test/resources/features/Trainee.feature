@Trainee
Feature: Fetching Trainees

  @SuccessfullyTraineeDelete
  Scenario: Successfully delete a trainee
    Given Authentication to request
    When request to delete a trainee's profile
    Then the response status should be 204

  @SuccessfullyFetchedTraineesProfile
  Scenario: Fetch Trainee profile Successfully
    Given Authentication to request
    When request to fetch a trainee's profile
    Then trainee's profile is returned

  @NonExistentTraineesUsername
  Scenario: Fail to fetch a non-existent trainee's profile
    Given Authentication to request
    When I request to fetch a non-existent trainee's profile
    Then an error message is returned

  @SuccessfullyUpdateTrainee
  Scenario: Update Trainee profile Successfully
    Given Authentication to request
    When request to update a trainee's profile
    Then the trainee's profile should be updated

  @FailToUpdateTrainee
  Scenario: Fail to update a non-existent trainee's profile
    Given Authentication to request
    When request to update a non-existent trainee's profile
    Then an error should occur

  Scenario: Get a list of trainings successfully
    Given Authentication to request
    When request for a list of trainings
    Then should get a list of trainings within that period

