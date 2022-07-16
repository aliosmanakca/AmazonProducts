Feature: QA assessment

  @Test1
  Scenario: completing a form
    Given user goes to "https://www.tutorialspoint.com/selenium/selenium_automation_practice.htm" web page
    Then enters first name
    Then enters last name
    Then chooses sex
    Then chooses years of experience
    Then enters date
    Then chooses profession
    Then chooses flavours of selenium
    Then selects continent
    Then selects selenium command
    And clicks the button