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

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum TestDataUser {
  NATIONAL_USER("NatUser", "NatUser38118", "National User", "XXFIKA-I653UK-XTLOIT-VWZYKGXY"),
  CONTACT_SUPERVISOR(
      "ContSup", "ContSup38118", "Contact Supervisor", "U77AIW-PC5LLO-VHEUOW-K3KZKF6E"),
  SURVEILLANCE_OFFICER(
      "SurvOff", "SurvOff38118", "Surveillance Officer", "Q2IYCN-TNYTOY-4OAYCA-DW662MTA"),
  LABORATORY_OFFICER(
      "LabOff", "LabOff38118", "Laboratory Officer", "UAAXB6-G6KRR2-YD7IDA-GJV2SM3A"),
  POINT_OF_ENTRY_SUPERVISOR(
      "PoeSup", "PoeSup38118", "Point of Entry Supervisor", "SJDRMA-6MGKJ4-I2GYDU-U3HCSE3M"),
  ADMIN_USER("automation_admin", "DbXC5Yimro9m", "Admin User", "W5QCZW-XLFVFT-E5MK66-O3SUKE7E"),
  REST_AUTOMATION("RestAuto", "umpQyGMSq4zy", "Rest AUTOMATION", "QLW4AN-TGWLRA-3UQVEM-WCDFCIVM");

  private final String username;
  private final String password;
  private final String userRole;
  private final String uuid;

  TestDataUser(String username, String password, String userRole, String uuid) {
    this.username = username;
    this.password = password;
    this.userRole = userRole;
    this.uuid = uuid;
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
