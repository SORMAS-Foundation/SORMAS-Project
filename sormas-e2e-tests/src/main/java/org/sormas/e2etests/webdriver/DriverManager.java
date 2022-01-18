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

package org.sormas.e2etests.webdriver;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class DriverManager {
  private final Map<String, RemoteWebDriver> webDriverByScenarioName;
  private final DriverFactory driverFactory;

  @Inject
  public DriverManager(DriverFactory driverFactory) {
    this.driverFactory = driverFactory;
    this.webDriverByScenarioName = new HashMap<>();
  }

  public RemoteWebDriver borrowRemoteWebDriver(String scenarioName) {
    RemoteWebDriver remoteWebDriver = driverFactory.getRemoteWebDriver();
    webDriverByScenarioName.put(scenarioName, remoteWebDriver);
    return remoteWebDriver;
  }

  public void releaseRemoteWebDriver(String scenarioName) {
    log.info("Removing scenario from WebDriver instance");
    RemoteWebDriver webDriver = webDriverByScenarioName.remove(scenarioName);
    if (webDriver == null) {
      throw new IllegalArgumentException(scenarioName + " There is no driver!");
    }
    log.info("Closing WebDriver instance");
    webDriver.close();
    webDriver.quit();
  }
}
