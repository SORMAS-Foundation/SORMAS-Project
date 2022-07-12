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

import static org.sormas.e2etests.steps.BaseSteps.locale;

import java.net.SocketTimeoutException;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sormas.e2etests.envconfig.dto.demis.DemisData;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.api.demis.okhttpclient.SormasOkHttpClient;

@Slf4j
public class DemisApiService {

  private final RunningConfiguration runningConfiguration;
  private final String FORM_PARAM_TYPE = "application/x-www-form-urlencoded";
  private final String CONTENT_TYPE = "Content-Type";
  private final String TOKEN_IDENTIFIER = "access_token";

  private JSONObject jsonObject;

  @Inject
  public DemisApiService(RunningConfiguration runningConfiguration) {
    this.runningConfiguration = runningConfiguration;
  }

  @SneakyThrows
  public String getAuthToken() {
    DemisData demisData = runningConfiguration.getDemisData(locale);

    OkHttpClient client =
        SormasOkHttpClient.getClient(
            demisData.getCertificatePath(), demisData.getCertificatePassword());

    Request request =
        new Request.Builder()
            .url(demisData.getDemisUrl() + demisData.getAuthPath())
            .addHeader(CONTENT_TYPE, FORM_PARAM_TYPE)
            .post(
                RequestBody.create(
                    MediaType.parse(FORM_PARAM_TYPE), demisData.getAuthRequestBody()))
            .build();
    try {
      Response response = client.newCall(request).execute();
      jsonObject = (JSONObject) JSONValue.parse(response.body().string());
      return (String) jsonObject.get(TOKEN_IDENTIFIER);

    } catch (SocketTimeoutException socketTimeoutException) {
      throw new Exception(
          String.format(
              "Unable to get response from Demis. Please make sure you are connected to VPN. [%s]",
              socketTimeoutException.getLocalizedMessage()));
    }
  }

  /** Delete method once we start adding tests */
  @SneakyThrows
  public String loginRequest() {
    return getAuthToken();
  }
}
