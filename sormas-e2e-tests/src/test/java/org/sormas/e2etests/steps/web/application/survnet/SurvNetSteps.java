package org.sormas.e2etests.steps.web.application.survnet;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.parsers.XMLParser;
import org.sormas.e2etests.steps.web.application.cases.CreateNewCaseSteps;
import org.testng.asserts.SoftAssert;

@Slf4j
public class SurvNetSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final SoftAssert softly;
  public static final Document xmlFile =
      XMLParser.getDocument(
          "/srv/dockerdata/jenkins_new/sormas-files/report.xml");

  @Inject
  public SurvNetSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;
    this.softly = softly;

    Then(
        "I check if date of report in SurvNet generated XML file is correct",
        () -> {
          LocalDate expectedDate =  CreateNewCaseSteps.survnetCase.getDateOfReport();
          LocalDate dateOfReport = getReportingDate(xmlFile);
          softly.assertEquals(dateOfReport, expectedDate, "Date of report is incorrect!");
          softly.assertAll();
        });

    And(
        "^I check if sex in SurvNet generated XML file is correct$",
        () -> {
          String sex = getSexDE(xmlFile);
          //add an assertion
        });
  }

  private LocalDate getReportingDate(Document xmlFile) {
    String reportingDate =
        xmlFile
            .getRootElement()
            .getChildren()
            .get(0)
            .getAttribute("ReportingDate")
            .getValue()
            .substring(0, 10);
    return LocalDate.parse(reportingDate, DATE_FORMATTER);
  }

  private String getSexDE(Document xmlFile) {
    String sexDE = null;
    String sexIndex =
        xmlFile
            .getRootElement()
            .getChildren()
            .get(0)
            .getChildren()
            .get(2)
            .getAttribute("Value")
            .getValue();

    switch (sexIndex) {
      case ("1"):
        sexDE = "Männlich";
        break;
      case ("2"):
        sexDE = "Weiblich";
        break;
      case ("3"):
        sexDE = "Divers";
        break;
      case ("4"):
        sexDE = "Unbekannt";
        break;
    }
    return sexDE;
  }
}
