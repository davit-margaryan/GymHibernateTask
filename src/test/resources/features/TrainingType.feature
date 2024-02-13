@FetchTrainingTypes
Feature: Fetch all Training Types

  @ValidFetchRequest
  Scenario: Fetch all training types with a valid request
    Given an authenticated request is made for all training types
    When the training types are being retrieved
    Then all training types are fetched successfully

  @UnauthenticatedRequest
  Scenario: Unauthenticated user fetches all training types
    When an unauthenticated user tries to fetch all training types
    Then the server responds with a Forbidden status code