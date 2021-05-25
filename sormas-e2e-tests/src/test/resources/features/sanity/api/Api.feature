@Sanity @Person @API
Feature: Check Person RestApi endpoints

  Scenario: Create a new event
    Given API: I receive all person ids
    Given API: I create a new person
    Given API: I create a new contact
    Given API: I create a new person
    Given API: I create a new case
    Given API: I create a new event
    Given API: I create a new sample
    Given API: I create a new task
