package org.sormas.e2etests.helpers.api;

import io.restassured.http.Method;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.Request;

import javax.inject.Inject;

import static org.sormas.e2etests.constants.api.Endpoints.COUNTRIES;

public class CountryHelper {

  private final RestAssuredClient restAssuredClient;

  @Inject
  public CountryHelper(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  public void getAllCountriesSince(Integer since) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(COUNTRIES + "all/" + since).build());
  }


}
