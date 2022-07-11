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
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */
package org.sormas.e2etests.entities.services.api.demis;

import java.net.SocketTimeoutException;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.api.demis.okhttpclient.SormasOkHttpClient;

@Slf4j
public class DemisApiService {

  private final RunningConfiguration runningConfiguration;

  @Inject
  public DemisApiService(RunningConfiguration runningConfiguration) {
    this.runningConfiguration = runningConfiguration;
  }

  @SneakyThrows
  public String getAuthToken() {

    OkHttpClient client =
        SormasOkHttpClient.getClient(
            "C:\\Users\\Razvan\\Downloads\\demis\\DEMIS-Adapter-2.0.1\\config\\DEMIS-test-lab999_CSM026304641.p12",
            "W7JDGJOVJ7");

    Request request =
        new Request.Builder()
            .url("https://10.210.11.214:443/auth/realms/LAB/protocol/openid-connect/token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(
                RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded"),
                    "client_id=demis-adapter&client_secret=secret_client_secret&username=test-lab999&grant_type=password"))
            .build();
    try {
      Response response = client.newCall(request).execute();
      //System.out.println("Response body: " + );
      JSONObject jsonObject = (JSONObject) JSONValue.parse(response.body().string());
      return (String) jsonObject.get("token");

    } catch (SocketTimeoutException socketTimeoutException) {
      throw new Exception(
          String.format(
              "Unable to get response from Demis. Please make sure you are connected to VPN. [%s]",
              socketTimeoutException.getLocalizedMessage()));
    }
  }

  @SneakyThrows
  public void loginRequest() {
    getAuthToken();
  }
}
