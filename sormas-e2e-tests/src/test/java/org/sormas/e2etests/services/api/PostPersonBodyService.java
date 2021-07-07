package org.sormas.e2etests.services.api;

import static org.sormas.e2etests.constants.api.JsonResourcesLocations.POST_PERSON_JSON_BODY;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.state.BodyResources;
import org.sormas.e2etests.utils.JsonUtils;

@Slf4j
public class PostPersonBodyService {
  private JsonUtils jsonUtils;
  private BodyResources bodyResources;

  @Inject
  public PostPersonBodyService(JsonUtils jsonUtils, BodyResources bodyResources) {
    this.jsonUtils = jsonUtils;
    this.bodyResources = bodyResources;
  }

  public String generatePostPersonBody() {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          jsonUtils.deserializeFromJson(new File(POST_PERSON_JSON_BODY));
      String personUUID = UUID.randomUUID().toString();
      postBodyJson.put("uuid", personUUID);
      bodyResources.setPersonUUID(personUUID);
      postBodyJson.put(
          "firstName", "FNautomation" + UUID.randomUUID().toString().replaceAll("-", ""));
      postBodyJson.put(
          "lastName", "LNautomation" + UUID.randomUUID().toString().replaceAll("-", ""));

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);

    } catch (Exception e) {
      log.error("Couldn't generate person body for POST request: " + e.getMessage());
    }
    return postBody;
  }
}
