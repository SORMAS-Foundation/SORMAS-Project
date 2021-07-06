package org.sormas.e2etests.helpers.api;

import static org.sormas.e2etests.constants.api.Endpoints.FACILITIES_PATH;

import io.restassured.http.Method;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.api.Request;

public class CountryHelper {

  private final RestAssuredClient restAssuredClient;

  @Inject
  public CountryHelper(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  public void getAllFacilitiesFromRegion(String regionUUID) {
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.GET)
            .path(FACILITIES_PATH + "region/" + regionUUID)
            .build());
  }
}
