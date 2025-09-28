Feature: API CRUD and data validation

  Scenario: Create and list usersx
    Given the backend base URL is set
    When I create a user with name "John Doe" email "john@j3s.com" type "premium"
    Then the response code should be 200
    And the response should contain field "data.id"
    When I request the list of users
    Then the response code should be 200
    And the list should contain at least 1 user

  Scenario: Create transaction between two users
    Given the backend base URL is set
    And I create a user with name "Alice" email "alice@j3s.com" type "standard"
    And I create a user with name "Bob" email "bob@j3s.com" type "standard"
    When I create a transfer of 50.25 from the last user to the previous user
    Then the response code should be 200
    And the response message should contain "Transfer successful"