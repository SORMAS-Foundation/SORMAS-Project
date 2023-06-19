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

package org.sormas.e2etests.entities.services.api;

import static org.sormas.e2etests.entities.services.api.demis.DemisApiService.readFileAsString;

import com.google.inject.Inject;
import java.io.*;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.sormas.e2etests.enums.*;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class UserApiService {
  private RestAssuredClient restAssuredClient;
  private JSONObject jsonObject;

  @Inject
  public UserApiService(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  @SneakyThrows
  public EnvironmentManager loginIntoEnvironment() {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return environmentManager;
  }

  @SneakyThrows
  public String convertToJson(String responseBody) {
    // jsonObject = (JSONObject) JSONValue.parse(responseBody);
    // jsonObject = new JSONObject();
    // jsonObject.put(responseBody);
    String jsonString = readJsonFile(responseBody);
    String encodedFilePath = "path/to/your/encoded-file.txt";
    // String encodedString = encodeToJsonAscii(jsonString);
    // saveEncodedStringToFile(encodedString, encodedFilePath);
    return jsonString;
  }

  @SneakyThrows
  public String prepareJsonFileRightsForAutomationAdmin() {
    String file = "src/main/resources/userRightsJsonTemplates/AutomationAdminUserRights.json";
    String json = readFileAsString(file);
    return json;
  }

  // private static String encodeToJsonAscii(String jsonString) {
  // JSONObject jsonObject = new JSONObject(jsonString);
  // return jsonObject.toString();
  // }

  private static String readJsonFile(String filePath) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    }
    return sb.toString();
  }

  private static void saveEncodedStringToFile(String encodedString, String filePath)
      throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      writer.write(encodedString);
    }
  }
}
