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
package gatling.envconfig.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import gatling.envconfig.configprovider.ConfigFileReader;
import gatling.envconfig.dto.EnvUser;
import gatling.envconfig.dto.Environment;
import gatling.envconfig.dto.Environments;
import gatling.envconfig.dto.demis.DemisData;
import lombok.SneakyThrows;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;

public class RunningConfiguration {

  private ObjectMapper objectMapper;
  private static Environments environments;
  private static boolean wasJsonChecked;

  @SneakyThrows
  public RunningConfiguration() {
    objectMapper = new ObjectMapper();
    environments =
        objectMapper.readValue(ConfigFileReader.getConfigurationFile(), Environments.class);
    if (!wasJsonChecked) {
      validateJsonData();
    }
  }


  @SneakyThrows
  public String getEnvironmentUrlForMarket(String identifier) {
    try {
      return environments.getEnvironments().stream()
          .filter(env -> env.getIdentifier().equalsIgnoreCase(identifier))
          .findFirst()
          .get()
          .getUrl();
    } catch (NullPointerException e) {
      throw new Exception(String.format("Unable to get Environment for market: %s", identifier));
    }
  }

  @SneakyThrows
  public String getEnvironmentLanguageForMarket(String identifier) {
    try {
      return environments.getEnvironments().stream()
          .filter(env -> env.getIdentifier().equalsIgnoreCase(identifier))
          .findFirst()
          .get()
          .getDefaultLanguage();
    } catch (NullPointerException e) {
      throw new Exception(String.format("Unable to get Language for market: %s", identifier));
    }
  }

  @SneakyThrows
  public DemisData getDemisData(String identifier) {
    try {
      return environments.getEnvironments().stream()
          .filter(env -> env.getIdentifier().equalsIgnoreCase(identifier))
          .findFirst()
          .get()
          .getDemisData();
    } catch (NullPointerException e) {
      throw new Exception(
          String.format("Unable to get DemisData of Environment for market: %s", identifier));
    }
  }

  @SneakyThrows
  public String getSurvnetResponsible(String identifier) {
    try {
      return environments.getEnvironments().stream()
          .filter(env -> env.getIdentifier().equalsIgnoreCase(identifier))
          .findFirst()
          .get()
          .getSurvnetResponsible();
    } catch (NullPointerException e) {
      throw new Exception(
          String.format(
              "Unable to get Survnet Responsible of Environment for market: %s", identifier));
    }
  }

  @SneakyThrows
  public EnvUser getUserByRole(String identifier, String role) {
    try {
      List<EnvUser> users = getEnvironment(identifier).getUsers();
      return users.stream()
          .filter(user -> user.getUserRole().equalsIgnoreCase(role))
          .findFirst()
          .get();
    } catch (NullPointerException e) {
      throw new Exception(
          String.format(
              "Unable to get Environment User for market: %s, and role: %s", identifier, role));
    }
  }

  private Environment getEnvironment(String identifier) {
    return environments.getEnvironments().stream()
        .filter(env -> env.getIdentifier().equalsIgnoreCase(identifier))
        .findFirst()
        .get();
  }

  private void validateJsonData() {
    environments.getEnvironments().stream()
        .forEach(
            environment -> {
              Assert.assertFalse(
                  environment.getIdentifier().isEmpty(),
                  "Environment identifier field cannot be empty!");
              Assert.assertFalse(
                  environment.getName().isEmpty(), "Environment name field cannot be empty!");
              Assert.assertFalse(
                  environment.getUrl().isEmpty(), "Environment url field cannot be empty!");
              Assert.assertFalse(
                  environment.getUsers().isEmpty(), "Environment users list cannot be empty!");
              environment.getUsers().stream()
                  .forEach(
                      envUser -> {
                        Assert.assertFalse(
                            envUser.getUserRole().isEmpty(), "User role field cannot be empty!");
                        Assert.assertFalse(
                            envUser.getUsername().isEmpty(), "User name field cannot be empty!");
                        Assert.assertFalse(
                            envUser.getUuid().isEmpty(), "User uuid field cannot be empty!");
                        Assert.assertFalse(
                            envUser.getPassword().isEmpty(),
                            "User password field cannot be empty!");
                      });
            });
    wasJsonChecked = true;
  }
}
