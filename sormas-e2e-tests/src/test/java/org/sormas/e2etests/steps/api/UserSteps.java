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

import cucumber.api.java8.En;
import javax.inject.Inject;
import org.json.simple.JSONObject;
import org.sormas.e2etests.entities.services.api.TaskApiService;
import org.sormas.e2etests.entities.services.api.UserApiService;
import org.sormas.e2etests.helpers.api.sormasrest.TaskHelper;
import org.sormas.e2etests.helpers.api.sormasrest.UserHelper;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;
import org.sormas.e2etests.state.ApiState;

public class UserSteps implements En {

  private JSONObject jsonObject;

  @Inject
  public UserSteps(
      UserHelper userHelper,
      TaskHelper taskHelper,
      TaskApiService taskApiService,
      UserApiService userApiService,
      ApiState apiState) {

    When(
        "API:I Login into Environment",
        () -> {
          EnvironmentManager loginIntoEnvironment = userApiService.loginIntoEnvironment();
        });

    When(
        "API: I get json file with user permission file",
        () -> {
          userHelper.getUserByRightsPermissions("W5QCZW-XLFVFT-E5MK66-O3SUKE7E");
          String responseBody = apiState.getResponse().getBody().asString();

          // System.out.print(responseBody);
          // ToDO back to encode to asci
          userApiService.convertToJson(responseBody);
          // System.out.print(
          // "First json : " + userApiService.convertToJson(regexUpdatedResponseBody));

          //  System.out.print("Json file from: " + userApiService.convertToJson(responseBody));
          // System.out.print("Response body in json file: " + responseBody);

          //  jsonObject = (JSONObject) JSONValue.parse(responseBody);
          // return (String) jsonObject.get(TOKEN_IDENTIFIER);
          ///  return jsonObject;
          // System.out.print("  Print json object:  " + jsonObject);
        });

    When(
        "I create template for Automation admin User Rights",
        () -> {
          userApiService.prepareJsonFileRightsForAutomationAdmin();
          System.out.print(
              "Json file: " + userApiService.prepareJsonFileRightsForAutomationAdmin());
        });
  }
}
