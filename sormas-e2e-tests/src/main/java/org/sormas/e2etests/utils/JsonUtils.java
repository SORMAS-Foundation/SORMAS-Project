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

package org.sormas.e2etests.utils;

import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.DeserializationFeature;
import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.sormas.e2etests.state.BodyResources;

public class JsonUtils {

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
}
