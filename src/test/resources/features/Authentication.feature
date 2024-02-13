@Authentication
Feature: Authentication

  @ValidTraineeRegisterRequest
  Scenario: Successful registration
    Given a valid trainee registration request
    When submit the trainee registration request
    Then the registration should be successful

  @NullFirstNameTraineeRegisterRequest
  Scenario: Registration with null firstName
    Given a trainee registration request with a null firstName
    Then the registration should fail with a bad request error

  @ValidTrainerRegisterRequest
  Scenario: Successful registration for trainer
    Given a valid trainer registration request
    When submit the trainer registration request
    Then the trainer registration should be successful


  @NullFirstNameTrainerRegisterRequest
  Scenario: Registration for trainer with null firstName
    Given a trainer registration request with a null firstName
    Then the trainer registration should fail with a bad request error

  @ValidLogin
  Scenario: Successful login
    Given a valid authentication request
    When submit the authentication request
    Then the authentication should be successful

  @InvalidOldPasswordChangePassword
  Scenario: Password change with invalid old password
    Given a change password request with invalid old password
    Then the password change should fail with a bad request error