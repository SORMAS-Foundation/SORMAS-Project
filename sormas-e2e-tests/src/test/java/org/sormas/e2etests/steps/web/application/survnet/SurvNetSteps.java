package org.sormas.e2etests.steps.web.application.survnet;

import cucumber.api.java8.En;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.helpers.parsers.XMLParser;
import org.sormas.e2etests.pages.application.NavBarPage;
import org.sormas.e2etests.steps.web.application.cases.CreateNewCaseSteps;
import org.sormas.e2etests.steps.web.application.cases.EditCaseSteps;
import org.sormas.e2etests.steps.web.application.persons.EditPersonSteps;
import org.testng.asserts.SoftAssert;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.sormas.e2etests.helpers.SchemaValidator.XMLSchemaValidator.validateXMLSchema;
import static org.sormas.e2etests.helpers.comparison.XMLComparison.compareXMLFiles;
import static org.sormas.e2etests.helpers.comparison.XMLComparison.extractDiffNodes;
import static org.sormas.e2etests.pages.application.AboutPage.SORMAS_VERSION_LINK;

@Slf4j
public class SurvNetSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private String sormasActualVersion;
  private static Document singleXmlFile;

  @Inject
  public SurvNetSteps(WebDriverHelpers webDriverHelpers, SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    Then(
        "I check if {string} in SORMAS generated XML file is correct",
        (String typeOfDate) -> {
//          Document xmlFile =
//              XMLParser.getDocument(
//                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
//                      + EditCaseSteps.externalUUID.substring(1, 37)
//                      + ".xml");
          LocalDate expectedDate = CreateNewCaseSteps.survnetCase.getDateOfReport();

          switch (typeOfDate) {
            case "date of report":
              LocalDate dateOfReport = getReportingDate(singleXmlFile);
              softly.assertEquals(dateOfReport, expectedDate, "Date of report is incorrect!");
              softly.assertAll();
              break;
            case "change at date":
              LocalDate changeAtDate = getChangedAt(singleXmlFile);
              softly.assertEquals(changeAtDate, expectedDate, "Change at date is incorrect!");
              softly.assertAll();
              break;
            case "created at date":
              LocalDate createdAt = getCreatedAt(singleXmlFile);
              softly.assertEquals(createdAt, expectedDate, "Created at date is incorrect!");
              softly.assertAll();
              break;
            case "tracked at date":
              LocalDate trackedAt = getTrackedAt(singleXmlFile);
              softly.assertEquals(trackedAt, expectedDate, "Tracked at date is incorrect!");
              softly.assertAll();
              break;
          }
        });

    And(
        "^I check if sex in SORMAS generated XML file is correct$",
        () -> {
//          Document xmlFile =
//              XMLParser.getDocument(
//                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
//                      + EditCaseSteps.externalUUID.substring(1, 37)
//                      + ".xml");
          String sex = getSexDE(singleXmlFile);
          String expectedSex = CreateNewCaseSteps.survnetCase.getSex();
          softly.assertEquals(sex, expectedSex, "Sex is incorrect!");
          softly.assertAll();
        });

    And(
        "^I compare the SORMAS generated XML file with the example one$",
        () -> {
          List<String> diffs =
              compareXMLFiles(
                  "src/main/resources/survnetXMLTemplates/controlXml.xml",
                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
                      + EditCaseSteps.externalUUID.substring(1, 37)
                      + ".xml");
          List<String> nodes = extractDiffNodes(diffs, "/[Transport][^\\s]+");

          softly.assertTrue(nodes.size() < 12, "Number of differences is incorrect!");
          softly.assertAll();

          List<String> expectedList = new ArrayList<>();
          expectedList.add("/Transport[1]/@CreatedAt");
          expectedList.add("/Transport[1]/@GuidTransport");
          expectedList.add("/Transport[1]/@TransportNumber");
          expectedList.add("/Transport[1]/CVD[1]/@ChangedAt");
          expectedList.add("/Transport[1]/CVD[1]/@GuidRecord");
          expectedList.add("/Transport[1]/CVD[1]/@ReportingDate");
          expectedList.add("/Transport[1]/CVD[1]/@Token");
          expectedList.add("/Transport[1]/CVD[1]/Field[1]/@Value");
          expectedList.add("/Transport[1]/CVD[1]/Track[1]/@GuidTrack");
          expectedList.add("/Transport[1]/CVD[1]/Track[1]/@TrackedAt");

          if (!expectedList.containsAll(nodes)) {
            expectedList.add("/Transport[1]/CVD[1]/Track[1]/@Software");
            softly.assertTrue(
                expectedList.containsAll(nodes),
                "The expected differences in the XML files are different");
            softly.assertAll();
          }
        });

    And(
        "^I check the SORMAS generated XML file structure with XSD Schema file$",
        () -> {
          softly.assertTrue(
              validateXMLSchema(
                  "src/main/resources/survnetXMLTemplates/xmlSchema.xsd",
                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
                      + EditCaseSteps.externalUUID.substring(1, 37)
                      + ".xml"),
              "Generated XML file does not match an example XSD schema");
          softly.assertAll();
        });

    And(
        "^I check if software info in SORMAS generated XML file is correct$",
        () -> {
          Document xmlFile =
              XMLParser.getDocument(
                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
                      + EditCaseSteps.externalUUID.substring(1, 37)
                      + ".xml");

          String softwareInfo = getSoftwareInfo(xmlFile).substring(15, 30);
          String expectedSoftwareInfo = sormasActualVersion;
          softly.assertEquals(softwareInfo, expectedSoftwareInfo, "Software info is incorrect!");
          softly.assertAll();
        });

    And(
        "^I collect SORMAS VERSION from About page$",
        () -> {
          webDriverHelpers.clickOnWebElementBySelector(NavBarPage.ABOUT_BUTTON);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SORMAS_VERSION_LINK);
          sormasActualVersion =
              webDriverHelpers.getTextFromWebElement(SORMAS_VERSION_LINK).substring(9, 24);
        });

    And(
        "^I check if external person uuid in SORMAS generated XML file is correct$",
        () -> {
          Document xmlFile =
              XMLParser.getDocument(
                  "/srv/dockerdata/jenkins_new/sormas-files/test_"
                      + EditCaseSteps.externalUUID.substring(1, 37)
                      + ".xml");
          String externalUUID = getGuidPatient(xmlFile);
          String expectedExternalUUID = EditPersonSteps.externalPersonUUID.substring(1, 37);
          softly.assertEquals(
              externalUUID, expectedExternalUUID, "Person external UUID is incorrect!");
          softly.assertAll();
        });

    And(
        "^I open SORMAS generated XML file for single message$",
        () -> {
          singleXmlFile = XMLParser.getDocument(
              "/srv/dockerdata/jenkins_new/sormas-files/test_"
              + EditCaseSteps.externalUUID.substring(1, 37)
              + ".xml");
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

  private LocalDate getChangedAt(Document xmlFile) {
    String reportingDate =
        xmlFile
            .getRootElement()
            .getChildren()
            .get(0)
            .getAttribute("ChangedAt")
            .getValue()
            .substring(0, 10);
    return LocalDate.parse(reportingDate, DATE_FORMATTER);
  }

  private LocalDate getCreatedAt(Document xmlFile) {
    String createdAt =
        xmlFile.getRootElement().getAttribute("CreatedAt").getValue().substring(0, 10);
    return LocalDate.parse(createdAt, DATE_FORMATTER);
  }

  private String getGuidPatient(Document xmlFile) {
    return xmlFile
        .getRootElement()
        .getChildren()
        .get(0)
        .getChildren()
        .get(0)
        .getAttribute("Value")
        .getValue()
        .substring(1, 37);
  }

  private LocalDate getTrackedAt(Document xmlFile) {
    String trackedAt =
        xmlFile
            .getRootElement()
            .getChildren()
            .get(0)
            .getChildren()
            .get(78)
            .getAttribute("TrackedAt")
            .getValue()
            .substring(0, 10);
    return LocalDate.parse(trackedAt, DATE_FORMATTER);
  }

  private String getSoftwareInfo(Document xmlFile) {
    String softwareInfo =
        xmlFile
            .getRootElement()
            .getChildren()
            .get(0)
            .getChildren()
            .get(78)
            .getAttribute("Software")
            .getValue();
    return softwareInfo;
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
