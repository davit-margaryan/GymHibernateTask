@FeatureManageTrainerWorkload
Feature: Manage trainer workload and get trainer summary

  @ValidWorkloadRequest
  Scenario: Manage workload for a trainer with a valid request
    Given a valid trainer workload request is made
    When the workload is being assigned
    Then the trainer workload is managed successfully

  @InvalidWorkloadRequest
  Scenario: Attempt to manage workload for a trainer with an invalid request
    Given an invalid trainer workload request is made
    When an attempt is made to assign the workload
    Then an error is returned indicating the workload request is invalid


  @ValidSummaryRequest
  Scenario: Retrieve summary for a trainer with a valid request
    Given a valid trainer summary request is made
    When the summary is being retrieved
    Then the trainer summary is retrieved successfully

  @InvalidSummaryRequest
  Scenario: Attempt to retrieve summary for a trainer with an invalid request
    Given an invalid trainer summary request is made
    When an attempt is made to retrieve the summary
    Then an error is returned indicating the summary request is invalid