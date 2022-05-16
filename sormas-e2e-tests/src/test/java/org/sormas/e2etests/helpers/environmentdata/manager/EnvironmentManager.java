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
package org.sormas.e2etests.helpers.environmentdata.manager;

import static org.sormas.e2etests.constants.api.Endpoints.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Method;
import io.restassured.response.Response;
import java.util.List;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.dto.*;

@Slf4j
public class EnvironmentManager {

  private RestAssuredClient restAssuredClient;
  private ObjectMapper objectMapper;
  private Response response;

  @Inject
  public EnvironmentManager(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  @SneakyThrows
  public String getCountryUUID(String countryName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(COUNTRIES_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<Country> countries =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), Country[].class));
    return countries.stream()
        .filter(country -> country.getDefaultName().equalsIgnoreCase(countryName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getRegionUUID(String regionName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(REGIONS_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<Region> regions =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), Region[].class));
    return regions.stream()
        .filter(region -> region.getName().equalsIgnoreCase(regionName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getDistrictUUID(String districtName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(DISTRICTS_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<District> districts =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), District[].class));
    return districts.stream()
        .filter(district -> district.getName().equalsIgnoreCase(districtName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getContinentUUID(String continentName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(CONTINENTS_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<Continent> continents =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), Continent[].class));
    return continents.stream()
        .filter(continent -> continent.getDefaultName().equalsIgnoreCase(continentName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String geSubcontinentUUID(String subcontinentName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(SUBCONTINENTS_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<Subcontinent> subcontinents =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), Subcontinent[].class));
    return subcontinents.stream()
        .filter(subcontinent -> subcontinent.getDefaultName().equalsIgnoreCase(subcontinentName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getCommunityUUID(String communityName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(COMMUNITIES_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<Community> communities =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), Community[].class));
    return communities.stream()
        .filter(community -> community.getName().equalsIgnoreCase(communityName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getHealthFacilityUUID(String regionName, String healthFacilityName) {
    String path = FACILITIES_PATH + "/region/" + getRegionUUID(regionName) + "/0";
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(path).build());
    objectMapper = getNewObjMapper();
    List<HealthFacility> healthFacilities =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), HealthFacility[].class));
    return healthFacilities.stream()
        .filter(healthFacility -> healthFacility.getName().equalsIgnoreCase(healthFacilityName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getLaboratoryUUID(String regionName, String laboratoryName) {
    String path = FACILITIES_PATH + "/region/" + getRegionUUID(regionName) + "/0";
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(path).build());
    objectMapper = getNewObjMapper();
    List<HealthFacility> healthFacilities =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), HealthFacility[].class));
    return healthFacilities.stream()
        .filter(laboratory -> laboratory.getName().equalsIgnoreCase(laboratoryName))
        .findFirst()
        .get()
        .getUuid();
  }

  @SneakyThrows
  public String getUserUUIDByFullName(String fullName) {
    response =
        restAssuredClient.sendRequestAndGetResponse(
            Request.builder().method(Method.GET).path(USERS_PATH + ALL_FROM_0).build());
    objectMapper = getNewObjMapper();
    List<User> users =
        List.of(objectMapper.readValue(response.getBody().asInputStream(), User[].class));
    return users.stream()
        .filter(user -> (user.getFirstName() + " " + user.getLastName()).equalsIgnoreCase(fullName))
        .findFirst()
        .get()
        .getUuid();
  }

  private ObjectMapper getNewObjMapper() {
    ObjectMapper objMapper = new ObjectMapper();
    objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return objMapper;
  }
}
