/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.sormas.e2etests.helpers;

import static com.google.common.truth.Truth.assertWithMessage;
import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;
import static org.sormas.e2etests.steps.BaseSteps.driver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sormas.e2etests.common.TimerLite;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.Assert;

@Slf4j
public class WebDriverHelpers {

  private static String PID = "PID [" + BaseSteps.PROCESS_ID + "] ->";
  public static final int FLUENT_WAIT_TIMEOUT_SECONDS = 20;
  public static final By CHECKBOX_TEXT_LABEL = By.xpath("ancestor::span//label");
  public static final By TABLE_SCROLLER =
      By.xpath("//div[@class='v-grid-scroller v-grid-scroller-vertical']");

  private final BaseSteps baseSteps;
  private final AssertHelpers assertHelpers;

  private static final String SCROLL_TO_WEB_ELEMENT_SCRIPT =
      "arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"center\"});";
  private static final String CLICK_ELEMENT_SCRIPT = "arguments[0].click();";
  private static final String TABLE_SCROLL_SCRIPT = "arguments[0].scrollTop+=%s";

  @Inject
  public WebDriverHelpers(BaseSteps baseSteps, AssertHelpers assertHelpers) {
    this.baseSteps = baseSteps;
    this.assertHelpers = assertHelpers;
  }

  public static String waitForSpinnerNotVisible(int timeOutInSeconds) {
    driver.getCurrentUrl();
    if ((driver == null) || (driver.getCurrentUrl() == null) || driver.getCurrentUrl().isEmpty()) {

      return "Wrong usage of waitForSpinnerNotVisible()";
    }
    try {
      (new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds)))
          .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(driver.getCurrentUrl())));
      return null;
    } catch (TimeoutException e) {
      return "Too short time for waitForSpinnerNotVisible()";
    }
  }

  public static void waitForElementPresent(By selector, int timeOutInSeconds) {
    driver.getCurrentUrl();
    new WebDriverWait(driver, Duration.ofSeconds(timeOutInSeconds))
        .until(ExpectedConditions.presenceOfElementLocated(selector));
  }

  public void ClickAndWaitForNewFormLoaded(By selector, By newForm) {
    try {
      driver.getCurrentUrl();
      driver.findElement(selector).click();
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(35));
      wait.until(ExpectedConditions.visibilityOfElementLocated(newForm));
    } catch (NoSuchElementException e) {
      System.err.println("Element not found:" + e.getMessage());
    } catch (TimeoutException e) {
      System.err.println("Timeout waiting for element: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("An excepted error occurred: " + e.getMessage());
    }
  }

  private WebElement findElement(By selector) {
    try {
      return baseSteps.getDriver().findElement(selector);
    } catch (StaleElementReferenceException stale) {
      return baseSteps.getDriver().findElement(selector);
    }
  }

  public void waitForPageLoaded() {
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertEquals(
                baseSteps.getDriver().executeScript("return document.readyState").toString(),
                "complete",
                "Page HTML wasn't loaded under 20s"));
  }

  public void waitUntilElementIsVisibleAndClickable(By selector) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void waitUntilIdentifiedElementIsVisibleAndClickable(final Object selector) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void waitUntilIdentifiedElementIsVisibleAndClickable(final WebElement selector) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void waitUntilIdentifiedElementIsVisibleAndClickable(final Object selector, int seconds) {
    if (selector instanceof By) {
      assertHelpers.assertWithPoll(
          () -> {
            scrollToElement(selector);
            Assert.assertTrue(
                findElement((By) selector).isDisplayed(),
                String.format("The element: %s was not visible", selector));
            Assert.assertTrue(
                findElement((By) selector).isEnabled(),
                String.format("The element: %s was not enabled", selector));
          },
          seconds);
    } else if (selector instanceof WebElement) {
      assertHelpers.assertWithPoll20Second(
          () -> {
            scrollToElement(selector);
            Assert.assertTrue(
                ((WebElement) selector).isDisplayed(),
                String.format("The element: %s was not displayed", selector));
            Assert.assertTrue(
                ((WebElement) selector).isEnabled(),
                String.format("The element: %s was not enabled", selector));
          });
    } else {
      throw new NotFoundException(String.format("This type: %s is not available", selector));
    }
  }

  public void waitUntilIdentifiedElementDisappear(final Object selector) {
    waitUntilIdentifiedElementDisappear(selector, FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void waitUntilIdentifiedElementDisappear(final Object selector, int seconds) {
    if (selector instanceof By) {
      assertHelpers.assertWithPoll(
          () -> {
            Assert.assertFalse(
                findElement((By) selector).isDisplayed(),
                String.format("The element: %s is still visible", selector));
          },
          seconds);
    } else if (selector instanceof WebElement) {
      assertHelpers.assertWithPoll20Second(
          () -> {
            Assert.assertFalse(
                ((WebElement) selector).isDisplayed(),
                String.format("The element: %s is still visible", selector));
          });
    } else {
      throw new NotFoundException(String.format("This type: %s is not available", selector));
    }
  }

  public void waitUntilAListOfWebElementsAreNotEmpty(final By selector) {
    log.info(PID + "Waiting after list of elements [{}] to be populated", selector);
    assertHelpers.assertWithPoll(
        () -> {
          List<String> webElementsTexts =
              baseSteps.getDriver().findElements(selector).stream()
                  .map(
                      webElement -> {
                        scrollToElement(webElement);
                        return webElement.getText();
                      })
                  .collect(Collectors.toList());
          webElementsTexts.forEach(
              text ->
                  Assert.assertFalse(
                      text.isEmpty(),
                      String.format("The element: %s was empty or null: %s", selector, text)));
        },
        FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void fillInWebElement(By selector, String text) {
    try {
      await()
          .pollInterval(ONE_HUNDRED_MILLISECONDS)
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
          .untilAsserted(
              () -> {
                Assert.assertTrue(
                    findElement(selector).isDisplayed(),
                    String.format("The element: %s was not visible", selector));
                Assert.assertTrue(
                    findElement(selector).isEnabled(),
                    String.format("The element: %s was not enabled", selector));
                scrollToElement(selector);
                clearWebElement(selector);
                Assert.assertEquals(
                    getValueFromWebElement(selector),
                    "",
                    String.format("Field %s wasn't cleared", selector));
                log.info(PID + "Filling element [ {} ] with text [ {} ]", selector, text);
                findElement(selector).sendKeys(text);
                String valueFromWebElement = getValueFromWebElement(selector);
                Assert.assertEquals(
                    valueFromWebElement,
                    text,
                    String.format("The expected text %s was not %s", valueFromWebElement, text));
              });

    } catch (ConditionTimeoutException ignored) {
      log.error(
          PID + "Unable to fill on element identified by locator: {} and text {}", selector, text);
      throw new TimeoutException(
          "Unable to fill on element identified by locator: " + selector + " and text : " + text);
    }
  }

  public void fillInAndLeaveWebElement(By selector, String text) {
    try {
      await()
          .pollInterval(ONE_HUNDRED_MILLISECONDS)
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
          .untilAsserted(
              () -> {
                assertWithMessage("The element: %s was not enabled", selector)
                    .that(findElement(selector).isEnabled())
                    .isTrue();
                assertWithMessage("The element: %s was not displayed", selector)
                    .that(findElement(selector).isDisplayed())
                    .isTrue();
                scrollToElement(selector);
                clearWebElement(selector);
                assertWithMessage("Field %s wasn't cleared", selector)
                    .that(getValueFromWebElement(selector))
                    .isEqualTo("");
                log.info(PID + "Filling element [ {} ] with text [ {} ]", selector, text);
                findElement(selector).sendKeys(text);
                findElement(By.cssSelector("body")).sendKeys(Keys.TAB);
                String valueFromWebElement = getValueFromWebElement(selector);
                assertWithMessage("The expected text %s was not %s", valueFromWebElement, text)
                    .that(valueFromWebElement)
                    .isEqualTo(text);
              });
    } catch (ConditionTimeoutException ignored) {
      log.error(
          PID + "Unable to fill on element identified by locator: {} and text {}", selector, text);
      throw new TimeoutException(
          "Unable to fill on element identified by locator: " + selector + " and text : " + text);
    }
  }

  public void fillAndSubmitInWebElement(By selector, String text) {
    fillInWebElement(selector, text);
    submitInWebElement(selector);
  }

  public void submitInWebElement(By selector) {
    waitUntilElementIsVisibleAndClickable(selector);
    findElement(selector).sendKeys(Keys.chord(Keys.ENTER));
  }

  public void clearAndFillInWebElement(By selector, String text) {
    clearWebElement(selector);
    fillInWebElement(selector, text);
  }

  @SneakyThrows
  public void selectFromCombobox(By selector, String text) {
    clickOnWebElementBySelector(selector);
    WebElement comboboxInput =
        findElement(selector).findElement(By.xpath("preceding-sibling::input"));
    String comboBoxItemWithText =
        "//td[@role='listitem']/span[ contains(text(), '"
            + text
            + "') or starts-with(text(), '\" + text + \"') ]";
    waitUntilIdentifiedElementIsVisibleAndClickable(comboboxInput);
    comboboxInput.sendKeys(text);
    //    waitUntilElementIsVisibleAndClickable(By.className("v-filterselect-suggestmenu"));
    //    waitUntilANumberOfElementsAreVisibleAndClickable(By.xpath("//td[@role='listitem']/span"),
    // 1);
    By dropDownValueXpath = By.xpath(comboBoxItemWithText);
    waitUntilANumberOfElementsAreVisibleAndClickable(dropDownValueXpath, 1);
    TimeUnit.SECONDS.sleep(1);
    clickOnWebElementBySelector(dropDownValueXpath);
    await()
        .pollInterval(ONE_HUNDRED_MILLISECONDS)
        .ignoreExceptions()
        .catchUncaughtExceptions()
        .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
        .untilAsserted(
            () -> {
              Assert.assertTrue(
                  findElement(selector)
                      .findElement(By.xpath("preceding-sibling::input"))
                      .getAttribute("value")
                      .contains(text),
                  String.format("Option %s wasn't selected from dropdown %s", text, selector));
            });
  }

  @SneakyThrows
  public void selectFromComboboxEqual(By selector, String text) {
    clickOnWebElementBySelector(selector);
    WebElement comboboxInput =
        findElement(selector).findElement(By.xpath("preceding-sibling::input"));
    String comboBoxItemWithText =
        "//td[@role='listitem']/span[text()='"
            + text
            + "' or starts-with(text(), '\" + text + \"') ]";
    waitUntilIdentifiedElementIsVisibleAndClickable(comboboxInput);
    comboboxInput.sendKeys(text);
    waitUntilElementIsVisibleAndClickable(By.className("v-filterselect-suggestmenu"));
    waitUntilANumberOfElementsAreVisibleAndClickable(By.xpath("//td[@role='listitem']/span"), 1);
    By dropDownValueXpath = By.xpath(comboBoxItemWithText);
    TimeUnit.MILLISECONDS.sleep(700);
    clickOnWebElementBySelector(dropDownValueXpath);
    await()
        .pollInterval(ONE_HUNDRED_MILLISECONDS)
        .ignoreExceptions()
        .catchUncaughtExceptions()
        .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
        .untilAsserted(
            () -> {
              Assert.assertTrue(
                  findElement(selector)
                      .findElement(By.xpath("preceding-sibling::input"))
                      .getAttribute("value")
                      .contains(text),
                  String.format("Option %s wasn't selected from dropdown %s", text, selector));
            });
  }

  @SneakyThrows
  public boolean checkIfElementExistsInCombobox(By selector, String text) {
    clickOnWebElementBySelector(selector);
    WebElement comboboxInput =
        findElement(selector).findElement(By.xpath("preceding-sibling::input"));
    String comboBoxItemWithText =
        "//td[@role='listitem']/span[ contains(text(), '"
            + text
            + "') or starts-with(text(), '\" + text + \"') ]";
    waitUntilIdentifiedElementIsVisibleAndClickable(comboboxInput);
    comboboxInput.sendKeys(text);
    waitUntilElementIsVisibleAndClickable(By.className("v-filterselect-suggestmenu"));
    return isElementVisibleWithTimeout(By.xpath(comboBoxItemWithText), 2);
  }

  public void clickOnWebElementBySelector(By selector) {
    clickOnWebElementBySelectorAndIndex(selector, 0);
  }

  public void doubleClickOnWebElementBySelector(By selector) {
    doubleClickOnWebElementBySelectorAndIndex(selector, 0);
  }

  public void clickWhileOtherButtonIsDisplayed(By clickedElement, By waitedSelector) {
    TimerLite timer = TimerLite.of(ofSeconds(30));
    do {
      clickOnWebElementWhichMayNotBePresent(clickedElement, 0);
      if (timer.isTimeUp()) {
        throw new RuntimeException("The element was not displayed");
      }
    } while (!isElementVisibleWithTimeout(waitedSelector, 2));
  }

  public void clickOnWebElementBySelectorAndIndex(By selector, int index) {
    scrollToElementUntilIsVisible(selector);
    try {
      await()
          .pollInterval(ONE_HUNDRED_MILLISECONDS)
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
          .untilAsserted(
              () -> {
                scrollToElement(selector);
                Assert.assertTrue(
                    baseSteps.getDriver().findElements(selector).get(index).isDisplayed(),
                    String.format("The element: %s is not displayed", selector));
                Assert.assertTrue(
                    baseSteps.getDriver().findElements(selector).get(index).isEnabled(),
                    String.format("The element: %s was not enabled", selector));
                scrollToElement(selector);
                log.info(PID + "Clicking on element [ {} ]", selector);
                findElement(selector).click();
                waitForPageLoaded();
              });

    } catch (ConditionTimeoutException ignored) {
      log.error(PID + "Unable to click on element identified by locator: {}", selector);
      throw new TimeoutException(
          String.format("Unable to click on element identified by locator: %s", selector));
    }
  }

  public void doubleClickOnWebElementBySelectorAndIndex(By selector, int index) {
    WebElement element = findElement(selector);
    Actions actions = new Actions(baseSteps.getDriver());
    scrollToElementUntilIsVisible(selector);
    try {
      await()
          .pollInterval(ONE_HUNDRED_MILLISECONDS)
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
          .untilAsserted(
              () -> {
                scrollToElement(selector);
                Assert.assertTrue(
                    baseSteps.getDriver().findElements(selector).get(index).isDisplayed(),
                    String.format("The element: %s is not displayed", selector));
                Assert.assertTrue(
                    baseSteps.getDriver().findElements(selector).get(index).isEnabled(),
                    String.format("The element: %s was not enabled", selector));
                scrollToElement(selector);
                log.info(PID + "Double-Clicking on element [ {} ]", selector);
                actions.doubleClick(element).perform();
                waitForPageLoaded();
              });

    } catch (ConditionTimeoutException ignored) {
      log.error(PID + "Unable to double click on element identified by locator: {}", selector);
      throw new TimeoutException(
          String.format("Unable to double click on element identified by locator: %s", selector));
    }
  }

  public void checkWebElementContainsText(By selector, String text) {
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertTrue(
                findElement(selector).getText().contains(text),
                String.format("Element %s doesn't contain text: %s", selector, text)));
  }

  public void accessWebSite(String url) {
    log.info(PID + "Navigating to: {} ", url);
    baseSteps.getDriver().get(url);
    waitForPageLoaded();
  }

  public void accessWebSiteWithNewTab(String url) {
    log.info(PID + "Navigating to: {} ", url);
    baseSteps.getDriver().switchTo().newWindow(WindowType.TAB);
    baseSteps.getDriver().get(url);
  }

  public void switchToTheTabNumber(int no) {
    ArrayList<String> tab = new ArrayList<>(baseSteps.getDriver().getWindowHandles());
    baseSteps.getDriver().switchTo().window(tab.get(no - 1));
  }

  public boolean isElementVisibleWithTimeout(By selector, int seconds) {
    try {
      assertHelpers.assertWithPoll(
          () ->
              Assert.assertTrue(
                  findElement(selector).isDisplayed(),
                  String.format("Element: %s is not visible under: %s seconds", selector, seconds)),
          seconds);
      return true;
    } catch (Throwable ignored) {
      return false;
    }
  }

  public boolean isElementEnabled(By selector) {
    scrollToElement(selector);
    return findElement(selector).isEnabled();
  }

  public boolean isElementEnabledAtAttributeLevel(By elementLocator) {
    scrollToElement(elementLocator);
    boolean isDisabled = getAttributeFromWebElement(elementLocator, "class").contains("disabled");
    if (isDisabled) {
      return false;
    }
    return true;
  }

  public boolean isElementChecked(By elementLocator) {
    scrollToElement(elementLocator);
    boolean isChecked = elementContainsAttribute(elementLocator, "checked");
    if (isChecked) {
      return true;
    }
    return false;
  }

  public boolean elementContainsAttribute(By elementLocator, String attribute) {
    scrollToElement(elementLocator);
    boolean result = false;
    String value = findElement(elementLocator).getAttribute(attribute);
    if (value != null) {
      result = true;
    }
    return result;
  }

  public boolean isElementPresent(By elementLocator) {
    Boolean isPresent = baseSteps.getDriver().findElements(elementLocator).size() > 0;
    return isPresent;
  }

  public void verifyListContainsText(final By selector, String value) {
    assertHelpers.assertWithPoll(
        () -> {
          List<String> webElementsTexts =
              baseSteps.getDriver().findElements(selector).stream()
                  .map(
                      webElement -> {
                        scrollToElement(webElement);
                        return webElement.getText();
                      })
                  .collect(Collectors.toList());
          Assert.assertTrue(
              webElementsTexts.contains(value),
              String.format(
                  "The element: %s values %s doesn't contain text: %s",
                  selector, webElementsTexts, value));
        },
        FLUENT_WAIT_TIMEOUT_SECONDS);
  }

  public void clickOnWebElementWhichMayNotBePresent(final By byObject, final int index) {
    try {
      log.info(PID + "Clicking on element: {}", byObject);
      baseSteps.getDriver().findElements(byObject).get(index).click();
    } catch (Exception exception) {
      log.warn(
          PID + "Unable to click on element:  {}, at index {}, due to: {}",
          byObject,
          index,
          exception.getMessage());
    }
  }

  public void clickElementSeveralTimesUntilNextElementIsDisplayed(
      By elementToClick, By elementToWaitAfter, int maxNumberOfClicks) {
    int attempts = 0;
    do {
      javaScriptClickElement(elementToClick);
      attempts++;
    } while (attempts < maxNumberOfClicks && isElementVisibleWithTimeout(elementToWaitAfter, 10));
  }

  public void scrollToElement(final Object selector) {
    JavascriptExecutor javascriptExecutor = baseSteps.getDriver();
    try {
      if (selector instanceof WebElement) {
        javascriptExecutor.executeScript(SCROLL_TO_WEB_ELEMENT_SCRIPT, selector);
      } else {
        javascriptExecutor.executeScript(SCROLL_TO_WEB_ELEMENT_SCRIPT, findElement((By) selector));
      }
    } catch (Exception ignored) {
    }
    waitForPageLoaded();
  }

  public void hoverToElement(By selector) {
    WebElement element = findElement(selector);
    Actions actions = new Actions(baseSteps.getDriver());
    try {
      assertHelpers.assertWithPoll20Second(
          () -> {
            scrollToElement(selector);
            actions.moveToElement(element).perform();
            waitUntilIdentifiedElementIsVisibleAndClickable(selector);
          });
    } catch (ConditionTimeoutException ignored) {
      throw new TimeoutException("Unable to hover on element identified by locator: " + selector);
    }
  }

  public void javaScriptClickElement(final Object selector) {
    JavascriptExecutor javascriptExecutor = baseSteps.getDriver();
    waitUntilIdentifiedElementIsPresent(selector);
    try {
      if (selector instanceof WebElement) {
        javascriptExecutor.executeScript(CLICK_ELEMENT_SCRIPT, selector);
      } else {
        waitUntilIdentifiedElementIsPresent(selector);
        log.info(PID + "Clicking on element {} via javascript", selector);
        javascriptExecutor.executeScript(CLICK_ELEMENT_SCRIPT, findElement((By) selector));
      }
    } catch (Exception ignored) {
      log.error(PID + "Unable to click on element {} via javascript", selector);
    }
  }

  // TODO replace regular scroll wth this one
  public void scrollToElementUntilIsVisible(final Object selector) {
    JavascriptExecutor javascriptExecutor = baseSteps.getDriver();
    try {
      if (selector instanceof WebElement) {
        assertHelpers.assertWithPoll20Second(
            () -> {
              javascriptExecutor.executeScript(SCROLL_TO_WEB_ELEMENT_SCRIPT, selector);
              Assert.assertTrue(
                  ((WebElement) selector).isDisplayed(),
                  String.format("Element: %s is not displayed", selector));
            });
      } else {
        assertHelpers.assertWithPoll20Second(
            () -> {
              javascriptExecutor.executeScript(
                  SCROLL_TO_WEB_ELEMENT_SCRIPT, findElement((By) selector));
              Assert.assertTrue(
                  findElement((By) selector).isDisplayed(),
                  String.format("Element: %s is not displayed", selector));
            });
      }
    } catch (Exception ignored) {
    }
    waitForPageLoaded();
  }

  private WebElement getWebElementByText(
      By selector, Predicate<WebElement> webElementPredicate, String text) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector);
    return baseSteps.getDriver().findElements(selector).stream()
        .filter(webElementPredicate)
        .findFirst()
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Cannot select by visible text: ["
                        + text
                        + "] from locator -> "
                        + selector.toString()));
    // LET THIS CODE COMMENTED until we check a regression and make sure this change doesn't have
    // any impact over execution
    //    assertHelpers.assertWithPoll20Second(
    //        () -> {
    //          waitUntilIdentifiedElementIsVisibleAndClickable(selector);
    //          assertWithMessage("Unable to find element based on: %s ", webElementPredicate)
    //              .that(
    //                  baseSteps.getDriver().findElements(selector).stream()
    //                      .anyMatch(webElementPredicate))
    //              .isTrue();
    //        });
    //    return baseSteps.getDriver().findElements(selector).stream()
    //        .filter(webElementPredicate)
    //        .findFirst()
    //        .orElseThrow(
    //            () -> new NotFoundException("The selector containing text has not been found"));
  }

  public void waitUntilAListOfElementsHasText(By selector, String text) {
    waitForPageLoaded();
    try {
      assertHelpers.assertWithPoll(
          () -> {
            List<WebElement> webElements = baseSteps.getDriver().findElements(selector);
            scrollToElement(webElements.get(0));
            Assert.assertTrue(
                webElements.size() >= 2,
                String.format(
                    "There are no more than %s found elements for text %s",
                    webElements.size(), text));
            for (WebElement element : webElements) {
              Assert.assertEquals(
                  element.getText(),
                  text,
                  String.format("Element: %s doesn't have the correct text: %s", element, text));
            }
          },
          3);
    } catch (Throwable ignored) {
    }
  }

  public void waitUntilAListOfElementsIsPresent(By selector, int number) {
    waitForPageLoaded();
    try {
      assertHelpers.assertWithPoll(
          () -> {
            List<WebElement> webElements = baseSteps.getDriver().findElements(selector);
            scrollToElement(webElements.get(0));
            Assert.assertTrue(
                webElements.size() >= number,
                String.format(
                    "Couldn't identify %s of expected elements based on locator: %s",
                    number, selector));
          },
          4);
    } catch (Throwable ignored) {
    }
  }

  public String getTextFromListElement(By selector, int index) {
    scrollToElement(selector);
    return baseSteps.getDriver().findElements(selector).get(index).getText();
  }

  public WebElement getWebElementBySelectorAndText(final By selector, final String text) {
    return getWebElementByText(
        selector, webElement -> webElement.getText().contentEquals(text), text);
  }

  @SneakyThrows
  public void clickWebElementByText(final By selector, final String text) {
    scrollToElement(selector);
    getWebElementBySelectorAndText(selector, text).click();
    TimeUnit.MILLISECONDS.sleep(200);
  }

  public String getValueFromWebElement(By byObject) {
    scrollToElementUntilIsVisible(byObject);
    return getAttributeFromWebElement(byObject, "value");
  }

  public String getValueFromCombobox(By selector) {
    waitUntilElementIsVisibleAndClickable(selector);
    WebElement comboboxInput =
        findElement(selector).findElement(By.xpath("preceding-sibling::input"));
    String value = comboboxInput.getAttribute("value");
    Assert.assertNotNull(value, String.format("Value from element: %s is null", selector));
    return comboboxInput.getAttribute("value");
  }

  public String getAttributeFromWebElement(By byObject, String attribute) {
    scrollToElement(byObject);
    waitUntilIdentifiedElementHasANonNullValue(byObject, attribute, FLUENT_WAIT_TIMEOUT_SECONDS);
    return findElement(byObject).getAttribute(attribute);
  }

  public void waitUntilIdentifiedElementHasANonNullValue(
      final By selector, String attribute, int seconds) {
    assertHelpers.assertWithPoll(
        () -> {
          scrollToElement(selector);
          Assert.assertNotNull(
              findElement(selector).getAttribute(attribute),
              String.format(
                  "The element with selector %s does not have the attribute %s",
                  selector, attribute));
        },
        seconds);
  }

  public String getTextFromWebElement(By byObject) {
    waitUntilIdentifiedElementIsVisibleAndClickable(byObject);
    scrollToElement(byObject);
    return findElement(byObject).getText();
  }

  public String getSrcFromWebElement(By byObject) {
    waitUntilIdentifiedElementIsVisibleAndClickable(byObject);
    scrollToElement(byObject);
    return findElement(byObject).getAttribute("src");
  }

  public int getNumberOfElements(By byObject) {
    try {
      return baseSteps.getDriver().findElements(byObject).size();
    } catch (Exception e) {
      log.warn(
          "Exception caught while getting the number of elements for locator: {} : {}",
          byObject,
          e.getMessage());
      throw new WebDriverException(String.format("No elements found for element: %s", byObject));
    }
  }

  public WebElement getWebElement(By byObject) {
    try {
      log.info(PID + "Returning WebElement for provided locator [ {} ]", byObject);
      return findElement(byObject);
    } catch (Exception any) {
      throw new WebDriverException(
          String.format(
              "No elements found for locator [ %s ] : [ %s ]", byObject, any.getMessage()));
    }
  }

  public String getTextFromPresentWebElement(By byObject) {
    scrollToElement(byObject);
    try {
      return findElement(byObject).getText();
    } catch (Exception any) {
      throw new WebDriverException(
          String.format(
              "Cannot get text from WebElement with locator [ %s ] : [ %s ]",
              byObject, any.getMessage()));
    }
  }

  public void waitUntilIdentifiedElementIsPresent(final Object selector) {
    try {
      if (selector instanceof WebElement) {
        WebElement element = (WebElement) selector;
        assertHelpers.assertWithPoll20Second(
            () -> {
              scrollToElement(selector);
              Assert.assertTrue(
                  element.isDisplayed(),
                  String.format("Webelement: %s is not displayed within 20s", element));
            });
      } else {
        assertHelpers.assertWithPoll20Second(
            () -> {
              scrollToElement(selector);
              Assert.assertTrue(
                  findElement((By) selector).isDisplayed(),
                  String.format("Locator: %s is not displayed within 20s", selector));
            });
      }
    } catch (StaleElementReferenceException staleEx) {
      log.warn(PID + "StaleElement found for: [ {} ]", selector);
      log.info(PID + "Performing again wait until element is present action");
      waitUntilIdentifiedElementIsPresent(selector);
    }
  }

  public void waitUntilWebElementHasAttributeWithValue(
      final By selector, String attribute, String value) {
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertEquals(
                getAttributeFromWebElement(selector, attribute),
                value,
                String.format(
                    "Element: %s, attribute: %s, is not: %s", selector, attribute, value)));
  }

  public void waitUntilANumberOfElementsAreVisibleAndClickable(By selector, int number) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertTrue(
                getNumberOfElements(selector) >= number,
                String.format("Number of identified element should be %s", number)));
  }

  public void waitUntilNumberOfElementsIsReduceToGiven(By selector, int given) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertTrue(
                getNumberOfElements(selector) < given,
                String.format("Number of identified element should be %s", given)));
  }

  public void waitUntilNumberOfElementsIsExactlyOrLess(By selector, int given) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertTrue(
                getNumberOfElements(selector) <= given,
                String.format("Number of identified element should be %s", given)));
  }

  public void waitUntilNumberOfElementsIsExactly(By selector, int given) {
    waitUntilIdentifiedElementIsVisibleAndClickable(selector, 15);
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertEquals(
                getNumberOfElements(selector),
                given,
                String.format("Number of identified element should be %s", given)));
  }

  public String getCheckedOptionFromHorizontalOptionGroup(By options) {
    waitUntilIdentifiedElementIsPresent(options);
    scrollToElement(options);
    final By SELECTED_RADIO_BUTTON =
        By.xpath(
            "ancestor::div[contains(@role,'group')]//input[@checked]/following-sibling::label");
    return findElement(options).findElement(SELECTED_RADIO_BUTTON).getText();
  }

  public String getCheckedDisabledOptionFromHorizontalOptionGroup(By options) {
    waitUntilIdentifiedElementIsPresent(options);
    scrollToElement(options);
    final By SELECTED_RADIO_DISABLED_AND_CHECKED_BUTTON =
        By.xpath(
            "//div[contains(@class,'v-select-optiongroup')]//input[@checked and @disabled]/following-sibling::label");
    return findElement(options).findElement(SELECTED_RADIO_DISABLED_AND_CHECKED_BUTTON).getText();
  }

  public void clearWebElement(By selector) {
    Instant start = Instant.now();
    waitUntilElementIsVisibleAndClickable(selector);
    scrollToElement(selector);
    WebElement webElement = findElement(selector);
    while (!"".contentEquals(getValueFromWebElement(selector))) {
      log.info(PID + "Deleted char: {}", getValueFromWebElement(selector));
      webElement.clear();
      webElement.sendKeys(Keys.LEFT_CONTROL);
      webElement.sendKeys("A");
      webElement.sendKeys((Keys.BACK_SPACE));
      webElement.click();
      if (Instant.now().isAfter(start.plus(1, ChronoUnit.MINUTES))) {
        throw new Error("The field " + selector + "didn't clear");
      }
    }
  }

  public void clearComboboxInput(By selector) {
    Instant start = Instant.now();
    waitUntilElementIsVisibleAndClickable(selector);
    scrollToElement(selector);
    WebElement webElement = findElement(selector);
    while (!"".contentEquals(getValueFromWebElement(selector))) {
      log.info(PID + "Deleted char: {}", getValueFromWebElement(selector));
      webElement.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.BACK_SPACE));
      webElement.click();
      if (Instant.now().isAfter(start.plus(1, ChronoUnit.MINUTES))) {
        throw new Error("The field " + selector + "didn't clear");
      }
    }
  }

  public String getTextFromLabelIfCheckboxIsChecked(By checkbox) {
    scrollToElement(checkbox);
    if (getAttributeFromWebElement(checkbox, "checked").equals("true")) {
      return findElement(checkbox).findElement(CHECKBOX_TEXT_LABEL).getText();
    } else {
      throw new Error(String.format("checked was found as NULL for %s: ", checkbox));
    }
  }

  public boolean checkCheckboxIsCheckedByHTMLFromParent(
      final By selector, final String text, final String expected) {
    try {
      return getWebElementBySelectorAndText(selector, text)
          .findElement((By.xpath("./..")))
          .getAttribute("outerHTML")
          .contains(expected);
    } catch (ConditionTimeoutException ignored) {
      throw new NoSuchElementException(String.format("Element: %s not found", selector));
    }
  }

  public boolean isElementDisplayedIn20SecondsOrThrowException(Object selector) {
    if (selector instanceof WebElement) {
      try {
        await()
            .pollInterval(ONE_HUNDRED_MILLISECONDS)
            .ignoreExceptions()
            .catchUncaughtExceptions()
            .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
            .until(((WebElement) selector)::isDisplayed);
        return true;
      } catch (ConditionTimeoutException ignored) {
        throw new NoSuchElementException(String.format("Element: %s is not visible", selector));
      }
    } else {
      try {
        await()
            .pollInterval(ONE_HUNDRED_MILLISECONDS)
            .ignoreExceptions()
            .catchUncaughtExceptions()
            .timeout(ofSeconds(FLUENT_WAIT_TIMEOUT_SECONDS))
            .until(() -> findElement((By) selector).isDisplayed());
        return true;
      } catch (ConditionTimeoutException ignored) {
        throw new NoSuchElementException(String.format("Element: %s is not visible", selector));
      }
    }
  }

  /**
   * Description: this method is used to perform scrolling in tables Parameters: scrollingStep -
   * where we can set how many rows we want per scroll action
   */
  @SneakyThrows
  public void scrollInTable(int scrollingStep) {
    int scrollingSpeed = scrollingStep * 39;
    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) baseSteps.getDriver();
    javascriptExecutor.executeScript(
        String.format(TABLE_SCROLL_SCRIPT, scrollingSpeed), findElement(TABLE_SCROLLER));
  }

  public void waitForPageLoadingSpinnerToDisappear(int maxWaitingTime) {
    By loadingSpinner =
        By.xpath(
            "//div[@class='v-loading-indicator third v-loading-indicator-wait' or contains(@class, 'v-loading-indicator')]");
    boolean isSpinnerDisplayed;
    try {
      assertHelpers.assertWithPollWithoutFail(
          () ->
              Assert.assertTrue(
                  findElement(loadingSpinner).isDisplayed(), "Loading spinner isn't displayed"),
          5);
      isSpinnerDisplayed = true;
    } catch (ConditionTimeoutException ignored) {
      isSpinnerDisplayed = false;
    }
    try {
      if (isSpinnerDisplayed)
        assertHelpers.assertWithPoll(
            () ->
                Assert.assertTrue(
                    getAttributeFromWebElement(loadingSpinner, "style").contains("display: none"),
                    String.format(
                        "Loading spinner is still displayed after %s seconds", maxWaitingTime)),
            maxWaitingTime);
    } catch (Throwable ignored) {
      if (!getAttributeFromWebElement(loadingSpinner, "style").contains("display: none"))
        throw new TimeoutException(
            String.format("Loading spinner didn't disappeared after %s seconds", maxWaitingTime));
    }
  }

  public void waitForRowToBeSelected(By rowLocator) {
    assertHelpers.assertWithPoll20Second(
        () ->
            Assert.assertTrue(
                getAttributeFromWebElement(rowLocator, "class").contains("row-selected"),
                String.format("Row element: %s wasn't selected within 20s", rowLocator)));
  }

  public void refreshCurrentPage() throws InterruptedException {
    baseSteps.refreshCurrentPage();
    TimeUnit.SECONDS.sleep(3);
    waitForPageLoadingSpinnerToDisappear(10);
  }

  public void isElementGreyedOut(By elementLocator) {
    try {
      assertHelpers.assertWithPoll20Second(
          () ->
              Assert.assertTrue(
                  getAttributeFromWebElement(elementLocator, "class").contains("disabled")));
    } catch (Throwable ignored) {
      if (!getAttributeFromWebElement(elementLocator, "class").contains("disabled"))
        throw new TimeoutException(
            String.format("The element: %s is not greyed out", elementLocator));
    }
  }

  @SneakyThrows
  public void sendFile(By selector, String filePath) {
    try {
      findElement(selector).sendKeys(filePath);
    } catch (Exception any) {
      throw new Exception(
          String.format("Unable to send file [ %s ] to [ %s }", filePath, selector));
    }
  }

  public boolean isElementDisplayedAndNoLoadingSpinnerOrThrowException(By selector) {
    By loadingSpinner =
        By.xpath(
            "//div[@class='v-loading-indicator third v-loading-indicator-wait' or contains(@class, 'v-loading-indicator')]");
    try {
      await()
          .pollInterval(ONE_HUNDRED_MILLISECONDS)
          .ignoreExceptions()
          .catchUncaughtExceptions()
          .timeout(ofSeconds(60))
          .until(
              () ->
                  findElement(selector).isDisplayed()
                      && !baseSteps.getDriver().findElement(loadingSpinner).isDisplayed());
      return true;
    } catch (ConditionTimeoutException ignored) {
      throw new NoSuchElementException(
          String.format(
              "Element: %s is not visible under 60 seconds or loading spinner didn't finished",
              selector));
    }
  }

  public String javascriptGetElementContent(String selector) {
    JavascriptExecutor javascriptExecutor = baseSteps.getDriver();
    String script =
        "return window.getComputedStyle(document.querySelector("
            + selector
            + ").getPropertyValue('content')";
    String content = javascriptExecutor.executeScript(script).toString();
    return content;
  }

  public String returnURL() {
    return baseSteps.getDriver().getCurrentUrl();
  }

  public void switchToOtherWindow() {
    String parent = baseSteps.getDriver().getWindowHandle();
    Set<String> S = baseSteps.getDriver().getWindowHandles();
    if (S.size() > 1) {
      for (String actual : S) {
        if (!actual.equalsIgnoreCase(parent)) {
          baseSteps.getDriver().switchTo().window(actual);
          break;
        }
      }
    } else {
      throw new NotFoundException("Cannot switch window because only one is available!");
    }
  }

  public void closeActiveWindow() {
    var tabs = new ArrayList<>(baseSteps.getDriver().getWindowHandles());
    if (tabs.size() > 1) {
      baseSteps.getDriver().close();
      baseSteps.getDriver().switchTo().window(tabs.get(0));
    } else {
      throw new NotFoundException(
          "Cannot close active window and switch to parent window because only one is available!");
    }
  }

  public boolean isFileExists(Path file_path) {
    return Files.exists(file_path);
  }

  public void waitForFileExists(Path file_path, int seconds) {
    assertHelpers.assertWithPoll(
        () ->
            Assert.assertTrue(
                Files.exists(file_path),
                String.format("File %s not exists after %s", file_path, seconds)),
        seconds);
  }

  public void checkIsPopupContainsList(By popup, List popupElements) {
    String popupElementXPath;
    String popupXPath = popup.toString();
    By lookingElement;
    for (Object popupMessage : popupElements) {
      popupElementXPath = popupXPath + "//li[contains(text(),'" + popupMessage + "')]";
      lookingElement = By.xpath(popupElementXPath.replace("By.xpath:", ""));
      Assert.assertTrue(
          isElementVisibleWithTimeout(lookingElement, 10),
          "Popup do not contains expected list of items!");
    }
  }
}
