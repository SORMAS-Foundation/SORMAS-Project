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

package org.sormas.e2etests.webdriver;

import java.io.File;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

@Slf4j
public class RemoteDriverFactory implements DriverFactory {

  private final String userDirProperty;
  private final DesiredCapabilities desiredCapabilities;
  private final DriverMetaData driverMetaData;
  private final String userDirectory = System.getProperty("user.dir");
  private final String remoteDriverPath = "/usr/lib64/chromium-browser/chromedriver";

  @Inject
  public RemoteDriverFactory(
      @Named("USER_DIR") String userDirProperty,
      DesiredCapabilities desiredCapabilities,
      DriverMetaData driverMetaData) {
    this.userDirProperty = userDirProperty;
    this.desiredCapabilities = desiredCapabilities;
    this.driverMetaData = driverMetaData;
  }

  @SneakyThrows
  @Override
  public ChromeDriver getRemoteWebDriver() {
    checkIfDriverExists();
    System.setProperty("webdriver.chrome.driver", remoteDriverPath);
    final ChromeOptions options = new ChromeOptions();
    final HashMap<String, Object> chromePreferences = new HashMap<>();
    chromePreferences.put("download.default_directory", userDirectory + "/downloads");
    options.merge(desiredCapabilities);
    options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
    options.addArguments("disable-infobars");
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-browser-side-navigation");
    options.addArguments("--disable-gpu-sandbox");
    options.addArguments("--disable-gpu");
    options.addArguments("--disable-gpu-watchdog");
    options.addArguments("--disable-new-content-rendering-timeout");
    options.addArguments("--disable-browser-side-navigation");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--allow-running-insecure-content");
    options.addArguments("enable-automation");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--ignore-ssl-errors");
    options.setCapability("javascript.enabled", true);
    options.setExperimentalOption("prefs", chromePreferences);
    options.addArguments("--window-size=1920,1080");
    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
    options.addArguments("--remote-allow-origins=*");
    return new ChromeDriver(options);
  }

  @SneakyThrows
  private void checkIfDriverExists() {
    File driverPath = new File(remoteDriverPath);
    if (!driverPath.exists()) {
      throw new Exception("Unable to find chromedriver!");
    }
  }
}
