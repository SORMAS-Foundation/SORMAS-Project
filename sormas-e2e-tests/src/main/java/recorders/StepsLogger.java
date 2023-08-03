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

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

@Slf4j
public class StepsLogger implements StepLifecycleListener {
  private static final String PROCESS_ID =
      String.valueOf(ManagementFactory.getRuntimeMXBean().getPid());
  public static final String PROCESS_ID_STRING = String.format("[PROCESS_ID:%s] ->", PROCESS_ID);
  private static RemoteWebDriver driver;
  private static boolean isScreenshotEnabled = true;
  private static boolean takeScreenshotAfterStep = false;
  private final String jpgExtension = "jpg";
  private final String imageType = "image/jpg";

  public static void setRemoteWebDriver(RemoteWebDriver remoteWebDriver) {
    driver = remoteWebDriver;
  }

  public static void setIsScreenshotEnabled(boolean isScreenshotEnabled) {
    StepsLogger.isScreenshotEnabled = isScreenshotEnabled;
  }

  @Override
  public void afterStepStart(final StepResult result) {
    log.info("{} -> Starting step -> {}", PROCESS_ID_STRING, result.getName());
  }

  @Override
  public void afterStepUpdate(final StepResult stepResult) {
    if (takeScreenshotAfterStep) {
      takeScreenshot();
    }
    List<InputStream> uiWarnings = collectWarningElements();
    if (!uiWarnings.isEmpty()) {
      for (InputStream input : collectWarningElements()) {
        Allure.getLifecycle().addAttachment("Warning element", imageType, jpgExtension, input);
      }
    }
    boolean logData = false;
    try {
      logData = Boolean.parseBoolean(System.getProperty("generateLogs"));
    } catch (Exception any) {
    }
    boolean isDriverNotNull = driver != null;
    boolean isStepFailed = !stepResult.getStatus().value().contains("pass");
    if (isScreenshotEnabled && logData && isDriverNotNull && isStepFailed) {
      attachConsoleLog();
    }
    isScreenshotEnabled = true;
    log.info("{} -> Finished step -> {}", PROCESS_ID_STRING, stepResult.getName());
  }

  @SneakyThrows
  @Attachment(value = "Browser console log", type = "text/json")
  private void attachConsoleLog() {
    try {
      Allure.getLifecycle()
          .addAttachment(
              "Execution logs", "text/json", "txt", new FileInputStream("logs/file.log"));
    } catch (Exception any) {
      log.error("Failed to attach logs to Allure report due to: {}", any.getCause());
    }
  }

  @Attachment(value = "After step screenshot", type = "image/jpg")
  public void takeScreenshot() {
    byte[] screenShot = driver.getScreenshotAs(OutputType.BYTES);
    Allure.getLifecycle()
        .addAttachment(
            "Screenshot at :"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yy hh:mm:ss")),
            "image/jpg",
            "jpg",
            screenShot);
  }

  @SneakyThrows
  private List<InputStream> collectWarningElements() {
    List<WebElement> warningsList = driver.findElements(By.xpath("//*[contains(@class,'error')]"));
    List<InputStream> attachments = new ArrayList<>();
    String fileName = "warning_element%s.jpg";
    for (int i = 0; i < warningsList.size(); i++) {
      String file = String.format(fileName, i) + "." + jpgExtension;
      Screenshot screenshot = new AShot().takeScreenshot(driver, warningsList.get(i));
      ImageIO.write(screenshot.getImage(), jpgExtension.toUpperCase(), new File("build\\" + file));
      attachments.add(new FileInputStream("build\\" + file));
    }
    return attachments;
  }
}
