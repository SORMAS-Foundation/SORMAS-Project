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

import static org.openqa.selenium.remote.CapabilityType.PROXY;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import javax.inject.Named;
import lombok.SneakyThrows;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sormas.e2etests.enums.OperatingSystems;

public class DriverModule extends PrivateModule {

  public static final String CHROME = "chrome";

  @Override
  protected void configure() {}

  @SneakyThrows
  @Provides
  DriverMetaData provideDesiredCapabilities(
      @Named("OS_NAME") String operatingSystem,
      @Named("OS_ARCH") String systemArchitecture,
      @Named("USER_DIR") String userDirProperty,
      @Named("REMOTE_DRIVER") boolean isRemoteDriverEnabled,
      @Named("BROWSER") String browser) {

    return DriverMetaData.builder()
        .operatingSystem(OperatingSystems.from(operatingSystem.toUpperCase()))
        .systemArchitecture(systemArchitecture)
        .isRemoteWebDriverEnabled(isRemoteDriverEnabled)
        .userDirProperty(userDirProperty)
        .browser(browser)
        .build();
  }

  @SneakyThrows
  @Provides
  DesiredCapabilities provideDesiredCapabilities(DriverMetaData driverMetaData) {
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    if (driverMetaData.isProxyEnabled()) {
      Proxy proxy = new Proxy();
      proxy.setProxyType(Proxy.ProxyType.MANUAL);
      proxy.setHttpProxy(driverMetaData.getProxyDetail());
      proxy.setSslProxy(driverMetaData.getProxyDetail());
      desiredCapabilities.setCapability(PROXY, proxy);
    }

    if (driverMetaData.isRemoteWebDriverEnabled()) {
      desiredCapabilities.setBrowserName(driverMetaData.getBrowser());
    }
    return desiredCapabilities;
  }

  @Provides
  @Exposed
  DriverFactory provideDriverFactory(
      @Named("BROWSER") String browser,
      Provider<ChromeDriverFactory> chromeDriverFactoryProvider,
      DriverMetaData driverMetaData,
      Provider<RemoteDriverFactory> remoteDriverFactoryProvider) {
    if (driverMetaData.isRemoteWebDriverEnabled()) {
      return remoteDriverFactoryProvider.get();
    } else if (CHROME.equals(browser)) {
      return chromeDriverFactoryProvider.get();
    }
    throw new IllegalStateException("No browser provided: " + browser);
  }
}
