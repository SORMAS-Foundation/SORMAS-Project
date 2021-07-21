package org.sormas.e2etests.helpers.api;

import static org.sormas.e2etests.constants.api.Endpoints.COMMUNITIES_PATH;
// import org.sormas.e2etests.pojo.Request;

import io.restassured.http.Method;
import javax.inject.Inject;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.pojo.api.Request;

public class CommunityHelper {
  private final RestAssuredClient restAssuredClient;

  @Inject
  public CommunityHelper(RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
  }

  public void getCommunitiesSince(Integer since) {
    restAssuredClient.sendRequest(
        Request.builder().method(Method.GET).path(COMMUNITIES_PATH + "all/" + since).build());
  }
}
