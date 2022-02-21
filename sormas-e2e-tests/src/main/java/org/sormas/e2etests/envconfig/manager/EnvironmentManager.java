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
package org.sormas.e2etests.envconfig.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.sormas.e2etests.envconfig.configprovider.ConfigFileReader;
import org.sormas.e2etests.envconfig.dto.EnvUser;
import org.sormas.e2etests.envconfig.dto.Environment;
import org.sormas.e2etests.envconfig.dto.Environments;

@Slf4j
public class EnvironmentManager {

  private ObjectMapper objectMapper;
  private static Environments environments;

  @SneakyThrows
  public EnvironmentManager() {
    objectMapper = new ObjectMapper();
    environments =
        objectMapper.readValue(ConfigFileReader.getConfigurationFile(), Environments.class);
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
}
