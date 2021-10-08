package org.sormas.e2etests.enums.cases.epidemiologicalData;

public enum ActivityDetailsRole {
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
  EDUCATOR("Educator"),
  TRAINEE_TEACHER("Trainee Teacher"),
  PUPIL("Pupil"),
  STUDENT("Student"),
  UNKNOWN("Unknown"),
  OTHER("Other");

  private String role;

  ActivityDetailsRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
}
