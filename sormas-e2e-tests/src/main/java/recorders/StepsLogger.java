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

package recorders;

import com.google.common.base.Stopwatch;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;

@Slf4j
public class StepsLogger implements StepLifecycleListener {
  private final Stopwatch stopwatch = Stopwatch.createUnstarted();
  private static final String PROCESS_ID =
      String.valueOf(ManagementFactory.getRuntimeMXBean().getPid());
  public static final String PROCESS_ID_STRING = String.format("[PROCESS_ID:%s]", PROCESS_ID);
  public final List<LogEntry> allLogEntries = new ArrayList<>();
  private static RemoteWebDriver driver;
  private static boolean isScreenshotEnabled = true;

  public static void setRemoteWebDriver(RemoteWebDriver remoteWebDriver) {
    driver = remoteWebDriver;
  }

  public static void setIsScreenshotEnabled(boolean isScreenshotEnabled) {
    StepsLogger.isScreenshotEnabled = isScreenshotEnabled;
  }

  @Override
  public void afterStepStart(final StepResult result) {
    stopwatch.reset();
    stopwatch.start();
    log.info(PROCESS_ID_STRING + " Starting step: " + result.getName());
  }

  @Override
  public void afterStepUpdate(final StepResult result) {
    if (isScreenshotEnabled && driver != null) {
      takeScreenshotAfter();
      attachConsoleLog();
    }
    stopwatch.stop();
    isScreenshotEnabled = true;
    log.info(
        " {} Finishing step: " + result.getName() + " and took: " + stopwatch, PROCESS_ID_STRING);
  }

  @Attachment(value = "After step screenshot", type = "image/png")
  public void takeScreenshotAfter() {
    byte[] screenShot = driver.getScreenshotAs(OutputType.BYTES);
    Allure.getLifecycle()
        .addAttachment(
            "Screenshot at :"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy hh:mm:ss")),
            "image/png",
            "png",
            screenShot);
  }

  @Attachment("Browser console log")
  private void attachConsoleLog() {
    List<String> consoleLogs = consoleAllLogs(driver);
    StringBuilder consoleLog = new StringBuilder("CONSOLE LOG: ");

    if (consoleLogs.isEmpty()) {
      consoleLog.append(" NO CONSOLE LOGS DETECTED!");
    } else {
      for (Object log : consoleLogs) {
        consoleLog.append(log);
      }
      Allure.getLifecycle()
          .addAttachment(
              "Console log at :"
                  + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy_hh:mm:ss")),
              "text/json",
              "text",
              consoleLog.toString().getBytes());
    }
  }

  public List<String> consoleAllLogs(RemoteWebDriver webDriver) {
    LogEntries logEntries = Objects.requireNonNull(webDriver).manage().logs().get(LogType.BROWSER);
    List<String> consoleLogs = new ArrayList<>();

    logEntries.forEach(
        entry -> {
          consoleLogs.add(
              new Date(entry.getTimestamp())
                  + " "
                  + entry.getLevel()
                  + " "
                  + entry.getMessage()
                  + "\n");
          allLogEntries.add(entry);
        });
    return consoleLogs;
  }
}
