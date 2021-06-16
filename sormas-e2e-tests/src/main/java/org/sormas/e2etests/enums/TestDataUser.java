/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum TestDataUser {
  NATIONAL_USER("NatUser", "NatUser38118", "National User"),
  CONTACT_SUPERVISOR("ContSup", "ContSup38118", "Contact Supervisor"),
  SURVEILLANCE_OFFICER("SurvOff", "SurvOff38118", "Surveillance Officer"),
  LABORATORY_OFFICER("LabOff", "LabOff38118", "Laboratory Officer"),
  ADMIN_USER("automation_admin", "DbXC5Yimro9m", "Admin User");

  private final String username;
  private final String password;
  private final String userRole;

  TestDataUser(String username, String password, String userRole) {
    this.username = username;
    this.password = password;
    this.userRole = userRole;
  }

  public static Stream<TestDataUser> stream() {
    return Stream.of(TestDataUser.values());
  }

  public static TestDataUser gertUserByRole(String userRole) {
    return stream()
        .filter(testDataUser -> testDataUser.getUserRole().contentEquals(userRole))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(userRole + " this type is not recognized"));
  }
}
