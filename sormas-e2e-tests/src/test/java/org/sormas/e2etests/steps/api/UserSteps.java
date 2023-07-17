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
package org.sormas.e2etests.steps.api;

import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import cucumber.api.java8.En;
import java.io.FileReader;
import javax.inject.Inject;
import org.json.simple.parser.JSONParser;
import org.sormas.e2etests.entities.services.api.UserApiService;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.api.sormasrest.UserHelper;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.state.ApiState;
import org.testng.asserts.SoftAssert;

public class UserSteps implements En {

  private String responseBody;
  private String regexUpdatedResponseBody;
  private String expectedUserRights;

  @Inject
  public UserSteps(
      UserHelper userHelper,
      UserApiService userApiService,
      SoftAssert softly,
      ApiState apiState,
      RunningConfiguration runningConfiguration) {

    When(
        "API:I Login into Environment",
        () -> {
          EnvironmentManager environmentManager = userApiService.loginIntoEnvironment();
        });

    When(
        "^API: I get response with ([^\"]*) user permission rights$",
        (String option) -> {
          switch (option) {
            case "Admin User":
              userHelper.getUserByRightsPermissions(
                  runningConfiguration
                      .getUserByRole(locale, UserRoles.AdminUser.getRole())
                      .getUuid());
              break;
            case "National User":
              userHelper.getUserByRightsPermissions(
                  runningConfiguration
                      .getUserByRole(locale, UserRoles.NationalUser.getRole())
                      .getUuid());
              break;
            case "Survnet":
              userHelper.getUserByRightsPermissions(
                  runningConfiguration
                      .getUserByRole(locale, UserRoles.SurvnetUser.getRole())
                      .getUuid());
              break;
            case "S2S":
              userHelper.getUserByRightsPermissions(
                  runningConfiguration
                      .getUserByRole(locale, UserRoles.SormasToSormasUser.getRole())
                      .getUuid());
              break;
          }
          responseBody = apiState.getResponse().getBody().asString();
          regexUpdatedResponseBody = responseBody.replaceAll("\\s+", "");
        });

    When(
        "^I prepare collection of ([^\"]*) rights based on json files$",
        (String option) -> {
          JSONParser parser = new JSONParser();
          ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
          Object objectToParse;
          switch (option) {
            case "Admin User":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/AutomationAdminUserRights.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
            case "National User":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/NationalUserRights.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
            case "Survnet":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/SurvnetUserRights.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
            case "S2S":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/SormasToSormasUserRights.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
          }
        });

    When(
        "^I prepare collection of ([^\"]*) rights based on json files for De version$",
        (String option) -> {
          JSONParser parser = new JSONParser();
          ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
          Object objectToParse;
          switch (option) {
            case "Admin User":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/AutomationAdminUserRightsDE.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
            case "National User":
              objectToParse =
                  parser.parse(
                      new FileReader(
                          "src/main/resources/userRightsJsonTemplates/NationalUserRightsDE.json"));
              expectedUserRights = ow.writeValueAsString(objectToParse).replaceAll("\\s+", "");
              break;
          }
        });

    When(
        "I check that user rights are complete",
        () -> {
          softly.assertEquals(
              expectedUserRights,
              regexUpdatedResponseBody,
              "The user rights are no correct granted");
          softly.assertAll();
        });
  }
}
