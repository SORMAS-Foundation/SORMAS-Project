package org.sormas.e2etests.helpers.api;

import static org.sormas.e2etests.constants.api.Endpoints.FACILITIES_PATH;

import io.restassured.http.Method;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.api.Request;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class FacilityHelper {

  private final RestAssuredClient restAssuredClient;

  @Inject
  public FacilityHelper(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  public void getFacilitiesByRegion(String specificPath, String regionUuid, Integer since) {
    restAssuredClient.sendRequest(
        Request.builder()
            .method(Method.GET)
            .path(FACILITIES_PATH + specificPath + regionUuid + "/" + since)
            .build());
  }
}
