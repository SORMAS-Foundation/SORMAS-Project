@Sanity @Person @API
Feature: Check Person RestApi endpoints

  Scenario: Create the creation flow of person, contact, case, event, sample and task
    Given API: I receive all person ids
    Given API: I create a new person
    Given API: I create a new contact
    Given API: I create a new person
    Given API: I create a new case
    Given API: I create a new event
    Given API: I create a new sample
    Given API: I create a new task
