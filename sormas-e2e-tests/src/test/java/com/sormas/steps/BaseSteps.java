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

package com.sormas.steps;

import com.google.inject.Inject;
import com.sormas.ui.DriverManager;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.qameta.allure.listener.StepLifecycleListener;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import recorders.StepsLogger;

@Slf4j
public class BaseSteps implements StepLifecycleListener {

  private static RemoteWebDriver driver;
  private final DriverManager driverManager;

  @Inject
  public BaseSteps(DriverManager driverManager) {
    this.driverManager = driverManager;
  }

  public RemoteWebDriver getDriver() {
    return driver;
  }

  @Before
  public void beforeScenario(Scenario scenario) {
    driver = driverManager.borrowRemoteWebDriver(scenario.getName());
    StepsLogger.setRemoteWebDriver(driver);
    WebDriver.Options options = driver.manage();
    options.window().maximize();
    options.timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
    options.timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
  }

  @After
  public void afterScenario(Scenario scenario) {
    driverManager.releaseRemoteWebDriver(scenario.getName());
  }
}
