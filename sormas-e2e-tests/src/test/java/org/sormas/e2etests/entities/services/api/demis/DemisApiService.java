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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
  private final String HOST_ADDR_HEADER = "Host";
  private final String ACCEPT_HEADER = "Accept";
  private final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
  private final String ACCEPT_ENCODING_TYPE = "'gzip, deflate, br'";
  private final String ACCEPT_ALL_HEADER = "* / *";
  private final String CONNECTION_HEADER = "Connection";
  private final String CONNECTION_TYPE = "keep-alive";
  private final String CONTENT_LENGTH_HEADER = "Content-Length";
  private final String CONTENT_LENGTH_VALUE = "99";
  private final String CACHE_CONTROL_HEADER = "Cache-Control";
  private final String CACHE_CONTROL_NO_CACHE = "no-cache";
  private final String AUTHORIZATION_HEADER = "Authorization";
  private final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
  private JSONObject jsonObject;
  public static final String specimenUUID = UUID.randomUUID().toString();
  public static final String secondSpecimenUUID = UUID.randomUUID().toString();

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
            .addHeader(HOST_ADDR_HEADER, demisData.getDemisUrl().substring(8))
            .addHeader(ACCEPT_HEADER, ACCEPT_ALL_HEADER)
            .addHeader(CONNECTION_HEADER, CONNECTION_TYPE)
            .addHeader(ACCEPT_ENCODING_HEADER, ACCEPT_ENCODING_TYPE)
            .addHeader(CONTENT_LENGTH_HEADER, CONTENT_LENGTH_VALUE)
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

  @SneakyThrows
  public Boolean sendLabRequest(String data, String loginToken) {

    DemisData demisData = runningConfiguration.getDemisData(locale);

    OkHttpClient client =
        SormasOkHttpClient.getClient(
            demisData.getCertificatePath(), demisData.getCertificatePassword());

    MediaType JSON = MediaType.parse(CONTENT_TYPE_APPLICATION_JSON + "; charset=utf-8");

    Request request =
        new Request.Builder()
            .url(demisData.getDemisUrl() + demisData.getAdapterPath())
            .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_NO_CACHE)
            .addHeader(AUTHORIZATION_HEADER, "Bearer " + loginToken)
            .addHeader(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
            .post(RequestBody.create(data, JSON))
            .build();
    try {
      Response response = client.newCall(request).execute();
      jsonObject = (JSONObject) JSONValue.parse(response.body().string());
      if (response.code() == 200) {
        return true;
      } else return false;
    } catch (SocketTimeoutException socketTimeoutException) {
      throw new Exception(
          String.format(
              "Unable to get response from Demis. Please make sure you are connected to VPN. [%s]",
              socketTimeoutException.getLocalizedMessage()));
    }
  }

  @SneakyThrows
  public String prepareLabNotificationFile(String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplate.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithLoinc(
      String patientFirstName, String patientLastName, String loincCode) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationWithLoincTemplate.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("\"<lonic_code_to_change>\"", "\"" + loincCode + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithTelcom(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplateTelcom.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithOtherFacility(
      String patientFirstName,
      String patientLastName,
      String otherFacilityId,
      String otherFacilityName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplateOtherFacility.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("\"<facility_id_to_change>\"", "\"" + otherFacilityId + "\"");
    json = json.replace("\"<facility_name_to_change>\"", "\"" + otherFacilityName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithTwoFacilities(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplateTwoFacilities.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithTwoPathogens(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplateTwoPathogens.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<second_observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithOnePathogen(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationTemplateOnePathogen.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithTwoSamples(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file = "src/main/resources/demisJsonTemplates/labNotificationMultipleSamples.json";
    String json = readFileAsString(file);
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("\"<second_person_last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<second_person_first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", specimenUUID);
    json = json.replace("<second_specimen_UUID_to_change>", secondSpecimenUUID);
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<second_observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<third_observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithOneExistingFacility(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file =
        "src/main/resources/demisJsonTemplates/labNotificationTemplateWithExistingFacility.json";
    String json = readFileAsString(file);
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("<specimen_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileWithMultiplePathogenOneSample(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file =
        "src/main/resources/demisJsonTemplates/labNotificationTemplateMultiplePathogen.json";
    String json = readFileAsString(file);
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    json = json.replace("<specimen_UUID_to_change>", specimenUUID);
    json = json.replace("<observation_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("<second_observation_UUID_to_change>", UUID.randomUUID().toString());
    return json;
  }

  public String prepareLabNotificationFileForPhysicianReport(
      String patientFirstName, String patientLastName) {
    DemisData demisData = runningConfiguration.getDemisData(locale);
    String file =
        "src/main/resources/demisJsonTemplates/labNotificationTemplatePhysicianReport.json";
    String json = readFileAsString(file);
    json = json.replace("<report_UUID_to_change>", UUID.randomUUID().toString());
    json = json.replace("\"<postal_code_to_change>\"", "\"" + demisData.getPostalCode() + "\"");
    json = json.replace("\"<last_name_to_change>\"", "\"" + patientLastName + "\"");
    json = json.replace("\"<first_name_to_change>\"", "\"" + patientFirstName + "\"");
    return json;
  }

  /** Delete method once we start adding tests */
  @SneakyThrows
  public String loginRequest() {
    return getAuthToken();
  }

  @SneakyThrows
  public static String readFileAsString(String file) {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
