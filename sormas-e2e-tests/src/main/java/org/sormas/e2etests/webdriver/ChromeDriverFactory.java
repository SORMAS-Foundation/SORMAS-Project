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

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Named;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ChromeDriverFactory implements DriverFactory {

  private final boolean headless;
  private final DesiredCapabilities desiredCapabilities;
  private final String userDirProperty;
  private final boolean isRemoteDriver;

  @Inject
  public ChromeDriverFactory(
      @Named("HEADLESS") boolean headless,
      DesiredCapabilities desiredCapabilities,
      @Named("REMOTE_DRIVER") boolean isRemoteDriver,
      @Named("USER_DIR") String userDirProperty) {
    this.headless = headless;
    this.desiredCapabilities = desiredCapabilities;
    this.userDirProperty = userDirProperty;
    this.isRemoteDriver = isRemoteDriver;
  }

  @Override
  public RemoteWebDriver getRemoteWebDriver() {
    WebDriverManager.chromedriver().setup();
    System.setProperty("webdriver.chrome.silentOutput", "true");
    System.setProperty("javascript.enabled", "true");
    final HashMap<String, Object> chromePreferences = new HashMap<>();
    chromePreferences.put("profile.password_manager_enabled", Boolean.FALSE);
    chromePreferences.put("download.default_directory", userDirProperty + "/downloads");
    final ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    options.merge(desiredCapabilities);
    options.setHeadless(headless);
    options.addArguments("--no-default-browser-check");
    options.addArguments("--window-size=1920,1080");
    options.setCapability("javascript.enabled", true);
    options.setExperimentalOption("prefs", chromePreferences);
    options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
    return new ChromeDriver(options);
  }
}
