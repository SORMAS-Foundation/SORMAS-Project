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

public class TestUtils {

  public static String readJsonToString(String path) {
    String json = "";
    try {
      json = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return json;
  }

  public static <T> T deserializeFromJson(String jsonString, Class<T> pojo) throws IOException {
    return new ObjectMapper()
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .readValue(jsonString, pojo);
  }

  public static Map<String, Object> deserializeFromJson(File path) throws IOException {
    return new com.fasterxml.jackson.databind.ObjectMapper().readValue(path, HashMap.class);
  }

  public static void logError(String customMessage, AssertionError ae) throws Throwable {
    String errorMsg = customMessage + "->" + getLogMessage(ae);
    // add to logg
    throw ae;
  }

  public static String getLogMessage(AssertionError ae) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    ae.printStackTrace(pw);

    return ae.getMessage() + sw.toString();
  }
}
