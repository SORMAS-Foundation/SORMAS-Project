package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum ExposureDetailsRole {
  PASSENGER("Passenger"),
  STAFF("Staff"),
  NURSING_STAFF("Nursing staff");


  private String role;

  ExposureDetailsRole(String role) {
    this.role = role;
  }
}
