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

package org.sormas.e2etests.helpers;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.truth.Truth;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.sormas.e2etests.common.TimerLite;
import org.sormas.e2etests.steps.BaseSteps;

@Slf4j
public class WebDriverHelpers {

  public static final By SELECTED_RADIO_BUTTOn =
      By.xpath("ancestor::div[@role='radiogroup']//input[@checked]/following-sibling::label");
  public static final int FLUENT_WAIT_TIMEOUT_SECONDS = 10;

  private final BaseSteps baseSteps;
  private final AssertHelpers assertHelpers;

  @Inject
  public WebDriverHelpers(BaseSteps baseSteps, AssertHelpers assertHelpers) {
    this.baseSteps = baseSteps;
    this.assertHelpers = assertHelpers;
  }

  public void waitForPageLoaded() {
    try {
      assertHelpers.assertWithPoll15Second(
          () ->
              assertThat(
                      baseSteps
                          .getDriver()
                          .executeScript("return document.readyState")
                          .toString()
                          .contentEquals("complete"))
                  .isTrue());
    } catch (Throwable ignored) {

    }
  }

  public void waitUntilElementIsVisibleAndClickable(By selector) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
  }

  public void waitUntilIdentifiedElementIsVisibleAndClickable(final Object selector) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void waitUntilIdentifiedElementIsVisibleAndClickable(final Object selector, int seconds) {

    if (selector instanceof By) {
      assertHelpers.assertWithPoll(
          () -> {
            assertThat(baseSteps.getDriver().findElement((By) selector).isEnabled()).isTrue();
            assertThat(baseSteps.getDriver().findElement((By) selector).isDisplayed()).isTrue();
          },
          seconds);
    } else if (selector instanceof WebElement) {
      assertHelpers.assertWithPoll15Second(
          () -> {
            assertThat(((WebElement) selector).isEnabled()).isTrue();
            assertThat(((WebElement) selector).isDisplayed()).isTrue();
          });
    } else {
      throw new NotFoundException("This type is not available");
    }
  }

  public void fillInWebElement(By selector, String text) {
    waitUntilElementIsVisibleAndClickable(selector);
    WebElement webElement = baseSteps.getDriver().findElement(selector);
    webElement.sendKeys(text);
  }

  public void clearAndFillInWebElement(By selector, String text) {
    clearWebElement(selector);
    fillInWebElement(selector, text);
  }

  @SneakyThrows
  public void selectFromCombobox(By selector, String text) {
    clickOnWebElementBySelector(selector);
    String comboBoxItemWithText = "//td[@role='listitem']/span[ contains(text(), '" + text + "')]";
    By dropDownValueXpath = By.xpath(comboBoxItemWithText);
    waitUntilElementIsVisibleAndClickable(By.className("v-filterselect-suggestpopup"));
    waitUntilANumberOfElementsAreVisibleAndClickable(By.xpath("//td[@role='listitem']/span"), 1);
    clickOnWebElementBySelector(dropDownValueXpath);
  }

  public void clickOnWebElementBySelector(By selector) {
    clickOnWebElementBySelectorAndIndex(selector, 0);
  }

  public void clickWhileOtherButtonIsDisplayed(By clickedElement, By waitedSelector) {
    TimerLite timer = TimerLite.of(Duration.ofSeconds(30));
    do {
      clickOnWebElementWhichMayNotBePresent(clickedElement, 0);
      if (timer.isTimeUp()) {
        throw new RuntimeException("The element was not displayed");
      }
    } while (!isElementVisibleWithTimeout(waitedSelector, 2));
  }

  public void clickOnWebElementBySelectorAndIndex(By selector, int index) {
    waitForPageLoaded();
    waitUntilElementIsVisibleAndClickable(selector);
    WebElement webElement = baseSteps.getDriver().findElements(selector).get(index);
    scrollToElement(webElement);
    webElement.click();
    waitForPageLoaded();
  }

  public void checkWebElementContainsText(By selector, String text) {
    assertHelpers.assertWithPoll15Second(
        () ->
            Truth.assertThat(baseSteps.getDriver().findElement(selector).getText()).contains(text));
  }

  public void accessWebSite(String url) {
    baseSteps.getDriver().navigate().to(url);
  }

  public boolean isElementVisibleWithTimeout(By selector, int seconds) {
    try {
      assertHelpers.assertWithPoll(
          () ->
              Truth.assertThat(baseSteps.getDriver().findElement(selector).isDisplayed()).isTrue(),
          seconds);
    } catch (Exception ignored) {
      return false;
    }
    return true;
  }

  public void clickOnWebElementWhichMayNotBePresent(final By byObject, final int index) {
    try {
      baseSteps.getDriver().findElements(byObject).get(index).click();
    } catch (Exception ignored) {
    }
  }

  public void scrollToElement(final Object selector) {
    JavascriptExecutor javascriptExecutor = baseSteps.getDriver();
    if (selector instanceof WebElement) {
      javascriptExecutor.executeScript(
          "arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
          selector);
    } else {
      waitUntilIdentifiedElementIsPresent((By) selector);
      javascriptExecutor.executeScript(
          "arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});",
          baseSteps.getDriver().findElement((By) selector));
    }
    waitForPageLoaded();
  }

  private WebElement getWebElementByText(By selector, Predicate<WebElement> webElementPredicate) {
    waitForPageLoaded();
    assertHelpers.assertWithPoll15Second(
        () -> {
          waitUntilIdentifiedElementIsVisibleAndClickable(selector);
          assertThat(
                  baseSteps.getDriver().findElements(selector).stream()
                      .anyMatch(webElementPredicate))
              .isTrue();
        });
    return baseSteps.getDriver().findElements(selector).stream()
        .filter(webElementPredicate)
        .findFirst()
        .orElseThrow(
            () -> new NotFoundException("The selector containing text has not been found"));
  }

  public WebElement getWebElementBySelectorAndText(final By selector, final String text) {
    return getWebElementByText(selector, webElement -> webElement.getText().contentEquals(text));
  }

  public void clickWebElementByText(final By selector, final String text) {
    getWebElementBySelectorAndText(selector, text).click();
  }

  public String getValueFromWebElement(By byObject) {
    waitUntilIdentifiedElementIsVisibleAndClickable(byObject);
    scrollToElement(byObject);
    return baseSteps.getDriver().findElement(byObject).getAttribute("value");
  }

  public String getTextFromWebElement(By byObject) {
    waitUntilIdentifiedElementIsVisibleAndClickable(byObject);
    scrollToElement(byObject);
    return baseSteps.getDriver().findElement(byObject).getText();
  }

  public int getNumberOfElements(By byObject) {
    try {
      return baseSteps.getDriver().findElements(byObject).size();
    } catch (Exception e) {
      return 0;
    }
  }

  public String getTextFromPresentWebElement(By byObject) {
    waitUntilIdentifiedElementIsPresent(byObject);
    scrollToElement(byObject);
    return baseSteps.getDriver().findElement(byObject).getText();
  }

  public void waitUntilIdentifiedElementIsPresent(final By selector) {
    assertHelpers.assertWithPoll15Second(
        () -> assertThat(getNumberOfElements(selector) > 0).isTrue());
  }

  public void waitUntilANumberOfElementsAreVisibleAndClickable(By selector, int number) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
    assertHelpers.assertWithPoll15Second(
        () ->
            assertWithMessage("Number of identified element should be %s", number)
                .that(getNumberOfElements(selector))
                .isAtLeast(number));
  }

  public String getCheckedOptionFromHorizontalOptionGroup(By options) {
    waitUntilIdentifiedElementIsPresent(options);
    return baseSteps.getDriver().findElement(options).findElement(SELECTED_RADIO_BUTTOn).getText();
  }

  public void clearWebElement(By selector) {
    Instant start = Instant.now();
    waitUntilElementIsVisibleAndClickable(selector);
    WebElement webElement = baseSteps.getDriver().findElement(selector);
    while (!"".contentEquals(getValueFromWebElement(selector))) {
      log.debug("Deleted char: {}", getValueFromWebElement(selector));
      webElement.clear();
      webElement.sendKeys((Keys.chord(Keys.SHIFT, Keys.END)));
      webElement.sendKeys(Keys.chord(Keys.BACK_SPACE));
      webElement.click();
      if (Instant.now().isAfter(start.plus(1, ChronoUnit.MINUTES))) {
        throw new Error("The field didn't clear");
      }
    }
  }
}
