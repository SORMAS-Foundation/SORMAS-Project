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

package org.sormas.e2etests.helpers;

import static junit.framework.TestCase.fail;
import static org.awaitility.Awaitility.await;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static recorders.StepsLogger.PROCESS_ID_STRING;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.ThrowingRunnable;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class AssertHelpers {

  @SneakyThrows
  public void assertWithPoll(ThrowingRunnable throwingRunnable, int seconds) {
    try {
      await()
          .pollInterval(fibonacci(TimeUnit.MILLISECONDS))
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(Duration.ofSeconds(seconds))
          .untilAsserted(throwingRunnable);
    } catch (ConditionTimeoutException e) {
      log.error(PROCESS_ID_STRING + e.getMessage());
      log.error(PROCESS_ID_STRING + Arrays.toString(e.getStackTrace()));
      // takeScreenshot(driver);
      fail(e.getCause().getLocalizedMessage());
    }
  }

  public void assertWithPoll20Second(ThrowingRunnable throwingRunnable) {
    assertWithPoll(throwingRunnable, 20);
  }

  @SneakyThrows
  static void takeScreenshot(RemoteWebDriver remoteWebDriver) {
    File srcFile = remoteWebDriver.getScreenshotAs(OutputType.FILE);
    String projectDir = System.getProperty("user.dir");
    File destFileName =
        new File(
            projectDir + File.separator + "screenshots/" + System.currentTimeMillis() + ".jpg");
    log.error("{} screenshot with name: {}", PROCESS_ID_STRING, destFileName.getName());
    FileUtils.copyFile(srcFile, destFileName);
  }
}
