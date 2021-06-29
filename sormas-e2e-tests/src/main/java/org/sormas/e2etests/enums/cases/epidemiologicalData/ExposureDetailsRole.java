package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum ExposureDetailsRole {
  PASSENGER("Passenger"),
  STAFF("Staff"),
  NURSING_STAFF("Nursing staff"),
  MEDICAL_STAFF("Medical staff"),
  VISITOR("Visitor"),
  GUEST("Guest"),
  CUSTOMER("Customer"),
  CONSERVATEE("Conservatee"),
  PACIENT("Pacient"),
  TEACHER("Teacher"),
  UNKNOWN("Unknown"),
  OTHER("Other");

  private String role;

  ExposureDetailsRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public static ExposureDetailsRole fromString(String role) {
    for (ExposureDetailsRole exposureDetailsRole : ExposureDetailsRole.values()) {
      if (exposureDetailsRole.role.equalsIgnoreCase(role)) {
        return exposureDetailsRole;
      }
    }
    System.out.println("Could not map " + role + " to ExposureDetailsRole");
    return null;
  }
}
