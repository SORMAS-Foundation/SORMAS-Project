package org.sormas.e2etests.utils;

import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.DeserializationFeature;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.sormas.e2etests.state.BodyResources;

public class TestUtils {

  @Getter @Setter @Inject BodyResources bodyResources;

  public void readJsonToString(String path) {
    String json = "";
    try {
      json = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    bodyResources.setBody(json);
  }

  public <T> T deserializeFromJson(String jsonString, Class<T> pojo) throws IOException {
    return new ObjectMapper()
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .readValue(jsonString, pojo);
  }

  public Map<String, Object> deserializeFromJson(File path) throws IOException {
    Map<String, Object> map = new ObjectMapper().readValue(path, HashMap.class);
    return map;
  }

  public void logError(String customMessage, AssertionError ae) throws Throwable {
    String errorMsg = customMessage + "->" + getLogMessage(ae);
    // add to logg
    throw ae;
  }

  public String getLogMessage(AssertionError ae) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ae.printStackTrace(pw);

    return ae.getMessage() + sw.toString();
  }
}
