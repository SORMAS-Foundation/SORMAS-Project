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

package org.sormas.e2etests.steps;

import com.google.inject.Inject;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import customreport.chartbuilder.ReportChartBuilder;
import customreport.data.TableDataManager;
import customreport.reportbuilder.CustomReportBuilder;
import io.qameta.allure.listener.StepLifecycleListener;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sormas.e2etests.steps.web.application.NavBarSteps;
import org.sormas.e2etests.ui.DriverManager;
import recorders.StepsLogger;

@Slf4j
public class BaseSteps implements StepLifecycleListener {

  public static RemoteWebDriver driver;
  private final DriverManager driverManager;

  @Inject
  public BaseSteps(DriverManager driverManager) {
    this.driverManager = driverManager;
  }

  public RemoteWebDriver getDriver() {
    return driver;
  }

  @Before(value = "@UI")
  public void beforeScenario(Scenario scenario) {
    if (isNonApiScenario(scenario)) {
      driver = driverManager.borrowRemoteWebDriver(scenario.getName());
      StepsLogger.setRemoteWebDriver(driver);
      WebDriver.Options options = driver.manage();
      options.timeouts().setScriptTimeout(Duration.ofMinutes(2));
      options.timeouts().pageLoadTimeout(Duration.ofMinutes(2));
      log.info("Browser's resolution: " + driver.manage().window().getSize().toString());
      log.info("Starting test: " + scenario.getName());
    }
  }

  @Before(value = "@API")
  static void setup() {
    RestAssured.registerParser("text/html", Parser.JSON);
  }

  @After(value = "@UI")
  public void afterScenario(Scenario scenario) {
    if (isNonApiScenario(scenario)) {
      driverManager.releaseRemoteWebDriver(scenario.getName());
    }
    log.info("Finished test: " + scenario.getName());
  }

  @After(value = "@PagesMeasurements")
  public void afterPageLoadTests(Scenario scenario) {
    String testName = scenario.getName().replace("Check", "").replace("loading time", "");
    log.info(scenario.getName() + " done, adding page meassurement result into TableDataManager");
    TableDataManager.addRowEntity(testName, NavBarSteps.elapsedTime);
  }

  @After(value = "@PublishCustomReport")
  public void generateMeasurementsReport() {
    log.info("Creating Chart for UI Meassurements report");
    ReportChartBuilder.buildChartForData(TableDataManager.getTableRowsDataList());
    log.info("Generating Sormas Custom report");
    CustomReportBuilder.generateReport(TableDataManager.getTableRowsAsHtml());
    log.info("Custom report was created!");
  }

  private static boolean isNonApiScenario(Scenario scenario) {
    return !scenario.getSourceTagNames().contains("@API");
  }
}
