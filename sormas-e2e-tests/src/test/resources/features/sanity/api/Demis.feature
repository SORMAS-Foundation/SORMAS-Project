@Sanity @DEMIS
Feature: Check Demis functionalities

  @env_d2s
  Scenario: Check demis token
    Given API : Login to DEMIS server

  @env_d2s
  Scenario: Send basic demis request
    Given API : Login to DEMIS server
    Then Send lab message with "testLabRequestFile.json"

  @env_d2s @testIt
  Scenario: Read Json
    Given Read json file