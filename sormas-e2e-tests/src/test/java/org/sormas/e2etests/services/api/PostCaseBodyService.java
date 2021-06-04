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

package org.sormas.e2etests.services.api;

import static org.sormas.e2etests.constants.api.JsonResourcesLocations.POST_CASES_BASIC_JSON_BODY;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.inject.Inject;
import org.sormas.e2etests.state.BodyResources;
import org.sormas.e2etests.utils.IncorrectDataException;
import org.sormas.e2etests.utils.JsonUtils;

public class PostCaseBodyService {
  private JsonUtils jsonUtils;
  private BodyResources bodyResources;

  @Inject
  public PostCaseBodyService(JsonUtils jsonUtils, BodyResources bodyResources) {
    this.jsonUtils = jsonUtils;
    this.bodyResources = bodyResources;
  }

  public String generatePostCaseBodyValid() throws IncorrectDataException {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          jsonUtils.deserializeFromJson(new File(POST_CASES_BASIC_JSON_BODY));

      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      df.setTimeZone(tz);
      String nowAsISO = df.format(new Date());

      postBodyJson.put("reportDate", nowAsISO);
      String caseUUID = UUID.randomUUID().toString();
      postBodyJson.put("uuid", caseUUID);
      bodyResources.setCaseUUID(caseUUID);

      Map<String, Object> clinicalCourse = (Map<String, Object>) postBodyJson.get("clinicalCourse");
      clinicalCourse.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> healthConditions =
          (Map<String, Object>) clinicalCourse.get("healthConditions");
      healthConditions.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> hospitalization =
          (Map<String, Object>) postBodyJson.get("hospitalization");
      hospitalization.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> therapy = (Map<String, Object>) postBodyJson.get("therapy");
      therapy.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> symptoms = (Map<String, Object>) postBodyJson.get("symptoms");
      symptoms.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> epiData = (Map<String, Object>) postBodyJson.get("epiData");
      epiData.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> person = (Map<String, Object>) postBodyJson.get("person");
      person.put("uuid", bodyResources.getPersonUUID());

      Map<String, Object> portHealthInfo = (Map<String, Object>) postBodyJson.get("portHealthInfo");
      portHealthInfo.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> maternalHistory =
          (Map<String, Object>) postBodyJson.get("maternalHistory");
      maternalHistory.put("uuid", UUID.randomUUID().toString());

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);

    } catch (IOException | NullPointerException ioe) {
      throw new IncorrectDataException(
          "Could not build the post json body, either the json file was not found or the property is not reachable ",
          ioe);
    }
    return postBody;
  }

  public String generatePostCaseBodyTooOld() throws IncorrectDataException {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          jsonUtils.deserializeFromJson(new File(POST_CASES_BASIC_JSON_BODY));

      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      df.setTimeZone(tz);
      String nowAsISO = df.format(new Date());

      postBodyJson.put("reportDate", nowAsISO);

      Map<String, Object> clinicalCourse = (Map<String, Object>) postBodyJson.get("clinicalCourse");
      clinicalCourse.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> healthConditions =
          (Map<String, Object>) clinicalCourse.get("healthConditions");
      healthConditions.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> hospitalization =
          (Map<String, Object>) postBodyJson.get("hospitalization");
      hospitalization.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> therapy = (Map<String, Object>) postBodyJson.get("therapy");
      therapy.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> symptoms = (Map<String, Object>) postBodyJson.get("symptoms");
      postBodyJson.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> epiData = (Map<String, Object>) postBodyJson.get("epiData");
      epiData.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> portHealthInfo = (Map<String, Object>) postBodyJson.get("portHealthInfo");
      portHealthInfo.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> maternalHistory =
          (Map<String, Object>) postBodyJson.get("maternalHistory");
      maternalHistory.put("uuid", UUID.randomUUID().toString());

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);

    } catch (IOException | NullPointerException ioe) {
      throw new IncorrectDataException(
          "Could not build the post json body, either the json file was not found or the property is not reachable ",
          ioe);
    }
    return postBody;
  }

  public String generatePostCaseBodyUnknownDisease() throws IncorrectDataException {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          jsonUtils.deserializeFromJson(new File(POST_CASES_BASIC_JSON_BODY));

      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      df.setTimeZone(tz);
      String nowAsISO = df.format(new Date());

      postBodyJson.put("reportDate", nowAsISO);
      String caseUUID = UUID.randomUUID().toString();
      postBodyJson.put("uuid", caseUUID);
      bodyResources.setCaseUUID(caseUUID);

      String disease = (String) postBodyJson.get("disease");
      postBodyJson.put("disease", disease.toLowerCase(Locale.ROOT));

      Map<String, Object> clinicalCourse = (Map<String, Object>) postBodyJson.get("clinicalCourse");
      clinicalCourse.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> healthConditions =
          (Map<String, Object>) clinicalCourse.get("healthConditions");
      healthConditions.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> hospitalization =
          (Map<String, Object>) postBodyJson.get("hospitalization");
      hospitalization.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> therapy = (Map<String, Object>) postBodyJson.get("therapy");
      therapy.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> symptoms = (Map<String, Object>) postBodyJson.get("symptoms");
      symptoms.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> epiData = (Map<String, Object>) postBodyJson.get("epiData");
      epiData.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> person = (Map<String, Object>) postBodyJson.get("person");
      person.put("uuid", bodyResources.getPersonUUID());

      Map<String, Object> portHealthInfo = (Map<String, Object>) postBodyJson.get("portHealthInfo");
      portHealthInfo.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> maternalHistory =
          (Map<String, Object>) postBodyJson.get("maternalHistory");
      maternalHistory.put("uuid", UUID.randomUUID().toString());

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);

    } catch (IOException | NullPointerException ioe) {
      throw new IncorrectDataException(
          "Could not build the post json body, either the json file was not found or the property is not reachable ",
          ioe);
    }
    return postBody;
  }

  public String generatePostCaseBodyWithInvalidFacility() throws IncorrectDataException {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          jsonUtils.deserializeFromJson(new File(POST_CASES_BASIC_JSON_BODY));

      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      df.setTimeZone(tz);
      String nowAsISO = df.format(new Date());

      postBodyJson.put("reportDate", nowAsISO);
      String caseUUID = UUID.randomUUID().toString();
      postBodyJson.put("uuid", caseUUID);
      bodyResources.setCaseUUID(caseUUID);

      Map<String, Object> clinicalCourse = (Map<String, Object>) postBodyJson.get("clinicalCourse");
      clinicalCourse.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> healthConditions =
          (Map<String, Object>) clinicalCourse.get("healthConditions");
      healthConditions.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> hospitalization =
          (Map<String, Object>) postBodyJson.get("hospitalization");
      hospitalization.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> therapy = (Map<String, Object>) postBodyJson.get("therapy");
      therapy.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> symptoms = (Map<String, Object>) postBodyJson.get("symptoms");
      symptoms.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> epiData = (Map<String, Object>) postBodyJson.get("epiData");
      epiData.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> person = (Map<String, Object>) postBodyJson.get("person");
      person.put("uuid", bodyResources.getPersonUUID());

      Map<String, Object> healthFacility = (Map<String, Object>) postBodyJson.get("healthFacility");
      String correctValueForHFacilityUUID = (String) healthFacility.get("uuid");
      healthFacility.put("uuid", correctValueForHFacilityUUID.toLowerCase(Locale.ROOT));

      Map<String, Object> portHealthInfo = (Map<String, Object>) postBodyJson.get("portHealthInfo");
      portHealthInfo.put("uuid", UUID.randomUUID().toString());

      Map<String, Object> maternalHistory =
          (Map<String, Object>) postBodyJson.get("maternalHistory");
      maternalHistory.put("uuid", UUID.randomUUID().toString());

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);

    } catch (IOException | NullPointerException ioe) {
      throw new IncorrectDataException(
          "Could not build the post json body, either the json file was not found or the property is not reachable ",
          ioe);
    }
    return postBody;
  }
}
