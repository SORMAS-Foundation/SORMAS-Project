package org.sormas.e2etests.steps.web.application;

import static org.sormas.e2etests.pages.application.NavBarPage.DASHBOARD_BUTTON;
import static org.sormas.e2etests.pages.application.NavBarPage.ERROR_NOTIFICATION_CAPTION;
import static org.sormas.e2etests.pages.application.NavBarPage.ERROR_NOTIFICATION_CAPTION_DE;
import static org.sormas.e2etests.pages.application.NavBarPage.ERROR_NOTIFICATION_DESCRIPTION;
import static org.sormas.e2etests.pages.application.NavBarPage.ERROR_NOTIFICATION_DESCRIPTION_DE;
import static org.sormas.e2etests.pages.application.messages.MessagesDirectoryPage.UUID_HEADER;

import cucumber.api.java8.En;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.asserts.SoftAssert;

@Slf4j
public class ColumnSteps implements En {

  private final WebDriverHelpers webDriverHelpers;

  @Inject
  public ColumnSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click the header of column {int}",
        (Integer col) -> {
          webDriverHelpers.clickOnWebElementBySelector(
              By.xpath("//thead//tr//th[" + col.toString() + "]"));
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I click the header UUID of column",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(UUID_HEADER);
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
        });

    When(
        "I check that error not appear",
        () -> {
          TimeUnit.SECONDS.sleep(10); // For preventing premature data collection
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION, 3));
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_DESCRIPTION, 3));
          softly.assertAll();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DASHBOARD_BUTTON);
        });

    When(
        "I check that error not appear for DE version",
        () -> {
          TimeUnit.SECONDS.sleep(10); // For preventing premature data collection
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_CAPTION_DE, 3));
          softly.assertFalse(
              webDriverHelpers.isElementVisibleWithTimeout(ERROR_NOTIFICATION_DESCRIPTION_DE, 3));
          softly.assertAll();
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(DASHBOARD_BUTTON);
        });

    When(
        "I check that column {int} is sorted alphabetically in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(5); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> element.toLowerCase());
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by last name in ascending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> getLastName(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by last name in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> getLastName(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by age in ascending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> getAge(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by age in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> getAge(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date in ascending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateSortable(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateSortable(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by German date in ascending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateSortableDE(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by German date in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateSortableDE(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date and time in ascending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateTimeSortable(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData,
              ascColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date and time in ascending order DE",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateTimeSortableDE(element));
          List<String> ascColumnData = new ArrayList<>(rawColumnData);
          ascColumnData.sort(Comparator.nullsLast(Comparator.naturalOrder()));
          softly.assertEquals(
              rawColumnData, ascColumnData, "Column " + col + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date and time in descending order",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateTimeSortable(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
          softly.assertEquals(
              rawColumnData,
              desColumnData,
              "Column " + col.toString() + " is not correctly sorted!");
          softly.assertAll();
        });

    When(
        "I check that column {int} is sorted by date and time in descending order DE",
        (Integer col) -> {
          TimeUnit.SECONDS.sleep(3); // For preventing premature data collection
          List<String> rawColumnData = getTableColumnDataByIndex(col, 10);
          rawColumnData.replaceAll(element -> nullifyEmptyString(element));
          rawColumnData.replaceAll(element -> makeDateTimeSortableDE(element));
          List<String> desColumnData = new ArrayList<>(rawColumnData);
          desColumnData.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
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

  private List<String> getTableColumnDataByIndex(int col, int maxRows) {
    List<String> list = new ArrayList<>();
    for (int i = 1; i < maxRows + 1; i++) {
      list.add(
          webDriverHelpers.getTextFromWebElement(
              By.xpath("//tbody//tr[" + i + "]//td[" + col + "]")));
    }
    return list;
  }

  private String nullifyEmptyString(String string) {
    if (string.isEmpty()) {
      string = null;
    }
    return string;
  }

  private String makeDateSortable(String date) {
    if (date != null) {
      date =
          LocalDate.parse(date, DateTimeFormatter.ofPattern("M/d/yyyy"))
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    return date;
  }

  private String makeDateSortableDE(String date) {
    if (date != null) {
      date =
          LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    return date;
  }

  private String makeDateTimeSortable(String dateTime) {
    if (dateTime != null) {
      dateTime =
          LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"))
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    return dateTime;
  }

  private String makeDateTimeSortableDE(String dateTime) {
    if (dateTime != null) {
      dateTime =
          LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    return dateTime;
  }

  private String getLastName(String string) {
    if (string != null) {
      string = string.substring(string.indexOf(" ") + 1, string.indexOf("(") - 1);
    }
    return string;
  }

  private String getAge(String string) {
    if (string != null) {
      string = string.substring(0, string.indexOf(" "));
    }
    return string;
  }
}
