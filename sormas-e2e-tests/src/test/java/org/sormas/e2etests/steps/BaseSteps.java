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

package org.sormas.e2etests.steps;

import com.google.inject.Inject;
import cucumber.api.*;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import customreport.chartbuilder.ReportChartBuilder;
import customreport.data.TableDataManager;
import customreport.reportbuilder.CustomReportBuilder;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.listener.StepLifecycleListener;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.sormas.e2etests.steps.nonBDDactions.BackupSteps;
import org.sormas.e2etests.webdriver.DriverManager;
import recorders.StepsLogger;

@Slf4j
public class BaseSteps implements StepLifecycleListener {

  public static final String PROCESS_ID =
      String.valueOf(ManagementFactory.getRuntimeMXBean().getPid());
  public static RemoteWebDriver driver;
  public static String locale;
  private final DriverManager driverManager;
  private final String imageType = "image/png";
  private final String jpgValue = "jpg";

  @Inject
  public BaseSteps(DriverManager driverManager) {
    this.driverManager = driverManager;
  }

  public RemoteWebDriver getDriver() {
    return driver;
  }

  @Before(order = 0)
  public void setRunningLocale(Scenario scenario) {
    setLocale(scenario);
  }

  @Before(value = "@UI")
  public void beforeScenario(Scenario scenario) {
    if (isNonApiScenario(scenario)) {
      driver = driverManager.borrowRemoteWebDriver(scenario.getName());
      StepsLogger.setRemoteWebDriver(driver);
      WebDriver.Options options = driver.manage();
      options.timeouts().scriptTimeout(Duration.ofMinutes(2));
      options.timeouts().pageLoadTimeout(Duration.ofMinutes(2));
      log.info("Starting test: {} with process ID [ {} ]", scenario.getName(), PROCESS_ID);
    }
  }

  @SneakyThrows
  @After(value = "@UI")
  public void afterScenario(Scenario scenario) {
    if (isLanguageRiskScenario(scenario) && scenario.isFailed()) {
      BackupSteps.setAppLanguageToDefault(locale);
    }
    if (isNonApiScenario(scenario)) {
      if (scenario.isFailed()) {
        takeScreenshot();
      }
      driverManager.releaseRemoteWebDriver(scenario.getName());
    }
    if (scenario.getStatus().toString().equalsIgnoreCase("UNDEFINED")) {
      Assert.fail("Test has invalid/missing steps");
    }
    log.info("Finished test: {}", scenario.getName());
  }

  @After(value = "@PublishPagesCustomReport")
  public void generatePagesMeasurementsReport() {
    log.info("Parsing results collected in results.txt and converting them into Row Objects");
    TableDataManager.convertPagesData();
    log.info("Creating Chart for UI Measurements report");
    ReportChartBuilder.buildChartForData(TableDataManager.getTablePageRowsDataList());
    log.info("Generating Sormas Custom report");
    CustomReportBuilder.generatePagesMeasurementsReport(TableDataManager.getPageRowsAsHtml());
    log.info("Custom report was created!");
  }

  @After(value = "@PublishApiCustomReport")
  public void generateApiMeasurementsReport() {
    log.info("Parsing results collected in results.txt and converting them into Row Objects");
    TableDataManager.convertApiData();
    log.info("Generating Sormas Custom report");
    CustomReportBuilder.generateApiMeasurementsReport(TableDataManager.getApiRowsAsHtml());
    log.info("Custom report was created!");
  }

  private static boolean isNonApiScenario(Scenario scenario) {
    return !scenario.getSourceTagNames().contains("@API");
  }

  private static boolean isLanguageRiskScenario(Scenario scenario) {
    return scenario.getSourceTagNames().contains("@LanguageRisk");
  }

  private void setLocale(Scenario scenario) {
    Collection<String> tags = scenario.getSourceTagNames();
    checkDeclaredEnvironment(tags);
    String localeTag = tags.stream().filter(value -> value.startsWith("@env")).findFirst().get();
    int indexOfSubstring = localeTag.indexOf("_");
    locale = localeTag.substring(indexOfSubstring + 1);
  }

  private void checkDeclaredEnvironment(Collection<String> tags) {
    AtomicBoolean foundEnvironment = new AtomicBoolean(false);
    tags.stream()
        .forEach(
            tag -> {
              if (foundEnvironment.get() && tag.startsWith("@env")) {
                Assert.fail("Cannot have more than one environment declared per test!");
              }
              if (tag.startsWith("@env")) {
                foundEnvironment.set(true);
              }
            });
  }

  @SneakyThrows
  @Attachment(value = "After failed test screenshot", type = imageType)
  private void takeScreenshot() {
    byte[] screenShot = driver.getScreenshotAs(OutputType.BYTES);
    Allure.getLifecycle()
        .addAttachment(
            "Screenshot at :"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy hh:mm:ss")),
            imageType,
                jpgValue,
            screenShot);
  }

  public void refreshCurrentPage() {
    driver.navigate().refresh();
  }
}
