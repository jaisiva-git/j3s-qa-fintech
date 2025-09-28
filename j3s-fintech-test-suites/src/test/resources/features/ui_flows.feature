Feature: UI flows on mock frontend

  Background:
    Given the mock UI server is running
    And the backend base URL is set

  Scenario: User registration flow
    When I open the mock UI
    And I register user "Jane Roe" with "jane@example.com" as "premium"
    Then I should see a status message containing "User created"

  Scenario: Transaction creation flow
    When I open the mock UI
    And I ensure users exist for a transfer
    And I submit a transfer of 10.5 between the last two created users
    Then I should see the respective transaction rows

  Scenario: Transfer to invalid recipient
    When I open the mock UI
    And I submit a transfer to a invalid recipient
    Then I should see a status message containing "Invalid userId or recipientId"