package org.sormas.e2etests.services.API;

import static org.sormas.e2etests.constants.api.ResourceFiles.POST_PERSON_JSON_BODY;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import org.sormas.e2etests.state.BodyResources;
import org.sormas.e2etests.utils.TestUtils;

public class PostPersonBodyService {
  private TestUtils testUtils;
  private BodyResources bodyResources;

  @Inject
  public PostPersonBodyService(TestUtils testUtils, BodyResources bodyResources) {
    this.testUtils = testUtils;
    this.bodyResources = bodyResources;
  }

  public String generatePostPersonBody() {
    String postBody = null;
    try {
      Map<String, Object> postBodyJson =
          testUtils.deserializeFromJson(new File(POST_PERSON_JSON_BODY));
      String personUUID = UUID.randomUUID().toString();
      postBodyJson.put("uuid", personUUID);
      bodyResources.setPersonUUID(personUUID);
      postBodyJson.put(
          "firstName", "FNautomation" + UUID.randomUUID().toString().replaceAll("-", ""));
      postBodyJson.put(
          "lastName", "LNautomation" + UUID.randomUUID().toString().replaceAll("-", ""));

      postBody =
          new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(postBodyJson);
      System.out.println(postBody);

    } catch (IOException ioe) {
      System.out.println(ioe.getMessage());
      // testUtils.logError("Could not build the post json body", new AssertionError(ioe));
    }
    return postBody;
  }
}
