Feature: API error handling and (placeholder) auth tests

  Background:
    Given the backend base URL is set

  Scenario: Validation errors for user creation
    When I try to create a user with invalid email "not-an-email"
    Then the response code should be 400

  Scenario: Insufficient funds
    Given I create a user with name "Low" email "low@example.com" type "standard"
    And I create a user with name "Rich" email "rich@example.com" type "standard"
    When I create a transfer of 2000.00 from the last user to the previous user
    Then the response code should be 400
    And the response message should contain "Insufficient funds"

  Scenario: Placeholder auth test (no auth required in sample backend)
    When I create a user with name "NoAuth" email "noauth@example.com" type "standard"
    Then the response code should be 200
    And the response json path "success" should be true