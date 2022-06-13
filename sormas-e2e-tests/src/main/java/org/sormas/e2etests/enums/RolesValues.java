/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.enums;

import lombok.Getter;

@Getter
public enum RolesValues {
  PASSANGER("Passanger"),
  STUFF("Stuff"),
  NURSING_STAFF("Nursing Staff"),
  MEDICAL_STAFF("Medical Staff"),
  VISITOR("Visitor"),
  GUEST("Guest"),
  CUSTOMER("Customer"),
  CONSERVATEE("Conservatee"),
  PATIENT("Patient"),
  EDUCATOR("Educator"),
  TRAINEE("Trainee"),
  PUPIL("Pupil"),
  STUDENT("Student"),
  PARENT("Parent"),
  TEACHER("Teacher"),
  UNKNOWN("Unknown"),
  OTHER("Other");

  private final String role;

  RolesValues(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return this.role;
  }
}
