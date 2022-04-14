package org.sormas.e2etests.steps.web.application.persons;

import static org.sormas.e2etests.pages.application.persons.PersonDirectoryPage.PERSON_DETAILED_COLUMN_HEADERS;

import cucumber.api.java8.En;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.steps.BaseSteps;
import org.testng.asserts.SoftAssert;

@Slf4j
public class PersonDetailedTableViewSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  private static BaseSteps baseSteps;
  static Map<String, Integer> headersMap;

  @Inject
  public PersonDetailedTableViewSteps(
      WebDriverHelpers webDriverHelpers, BaseSteps baseSteps, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.baseSteps = baseSteps;

    When(
        "I check that the Person table structure is correct",
        () -> {
          headersMap = extractColumnHeadersHashMap();
          String headers = headersMap.toString();
          softly.assertTrue(
              headers.contains("PERSON ID=0"), "The PERSON ID column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("FIRST NAME=1"),
              "The FIRST NAME column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("LAST NAME=2"), "The LAST NAME column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("AGE AND BIRTH DATE=3"),
              "The PERSON ID column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("SEX=4"), "The SEX column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("DISTRICT=5"), "The DISTRICT column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("STREET=6"), "The STREET column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("HOUSE NUMBER=7"),
              "The HOUSE NUMBER column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("POSTAL CODE=8"),
              "The POSTAL CODE column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("CITY=9"), "The CITY column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("PRIMARY PHONE NUMBER=10"),
              "The PRIMARY PHONE NUMBER column is not correctly displayed!");
          softly.assertTrue(
              headers.contains("PRIMARY EMAIL ADDRESS=11"),
              "The PRIMARY EMAIL ADDRESS column is not correctly displayed!");
          softly.assertAll();
        });

    When(
        "I click the header of column {int} in Person directory",
        (Integer col) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//thead//tr//th[" + col.toString() + "]"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(5);
        });

    When(
        "I check that column {int} is sorted in ascending order",
        (Integer col) -> {
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.naturalOrder());
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted in descending order",
        (Integer col) -> {
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.reverseOrder());
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that an upwards arrow appears in the header of column {int}",
        (Integer col) -> {
          String upArrowBytes = "[34, -17, -125, -98, 32, 34]";
          String content =
              webDriverHelpers.javascriptGetElementContent(
                  "'table > thead > tr > th:nth-of-type(" + col + ")'),'::after'");
          byte[] array = content.getBytes(StandardCharsets.UTF_8);
          softly.assertEquals(
              Arrays.toString(array), upArrowBytes, "The upwards arrow is not displayed!");
          softly.assertAll();
        });

    When(
        "I check that a downwards arrow appears in the header of column {int}",
        (Integer col) -> {
          String downArrowBytes = "[34, -17, -125, -99, 32, 34]";
          String content =
              webDriverHelpers.javascriptGetElementContent(
                  "'table > thead > tr > th:nth-of-type(" + col + ")'),'::after'");
          byte[] array = content.getBytes(StandardCharsets.UTF_8);
          softly.assertEquals(
              Arrays.toString(array), downArrowBytes, "The downwards arrow is not displayed!");
          softly.assertAll();
        });
  }

  private Map<String, Integer> extractColumnHeadersHashMap() {
    AtomicInteger atomicInt = new AtomicInteger();
    HashMap<String, Integer> headerHashmap = new HashMap<>();
    webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
        PERSON_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.waitUntilAListOfWebElementsAreNotEmpty(PERSON_DETAILED_COLUMN_HEADERS);
    webDriverHelpers.scrollToElementUntilIsVisible(PERSON_DETAILED_COLUMN_HEADERS);
    baseSteps
        .getDriver()
        .findElements(PERSON_DETAILED_COLUMN_HEADERS)
        .forEach(
            webElement -> {
              webDriverHelpers.scrollToElementUntilIsVisible(webElement);
              headerHashmap.put(webElement.getText(), atomicInt.getAndIncrement());
            });
    return headerHashmap;
  }

  private List<String> getTableColumnDataByIndex(int col, int maxRows) {
    List<String> list = new ArrayList<>();
    for (int i = 1; i < maxRows + 1; i++) {
      list.add(
          webDriverHelpers.getTextFromWebElement(
              By.xpath("//tbody//tr[" + i + "]//td[" + col + "]")));
    }
    return list;
  }
}
