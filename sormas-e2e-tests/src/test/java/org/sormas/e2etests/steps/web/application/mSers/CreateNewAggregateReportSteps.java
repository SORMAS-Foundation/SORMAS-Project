package org.sormas.e2etests.steps.web.application.mSers;

import static org.sormas.e2etests.pages.application.cases.LineListingPopup.LINE_LISTING_SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.DELETE_AGGREGATED_REPORT_BUTTON;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.DISTRICT_COMBOBOX_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.DISTRICT_COMBOBOX_POPUP_DIV;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.DUPLICATE_DETECTION_TEXT;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.EPI_WEEK_COMBOBOX_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.EPI_WEEK_INPUT_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.ERROR_MESSAGE;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.POPUP_MESSAGE_WINDOW;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.REGION_COMBOBOX_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.REGION_COMBOBOX_POPUP_DIV;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.SNAKE_BITE_SUSPECTED_CASES_INPUT;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.WEEK_RADIOBUTTON;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.YEAR_COMBOBOX_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.YEAR_INPUT_POPUP;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getAgeGroupDiseaseByDiseaseAndAgeGroup;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getCasesInputByDisease;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getCasesInputByDiseaseAndAgeGroup;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getDeathInputByDisease;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getDeathInputByDiseaseAndAgeGroup;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getLabConfirmationsInputByDisease;
import static org.sormas.e2etests.pages.application.mSers.CreateNewAggreagateReportPage.getLabConfirmationsInputByDiseaseAndAgeGroup;

import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import javax.inject.Inject;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.AggregateReport;
import org.sormas.e2etests.entities.services.AggregateReportService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class CreateNewAggregateReportSteps implements En {

  protected WebDriverHelpers webDriverHelpers;
  protected static AggregateReport report;
  protected static AggregateReport collectedReport;
  protected static AggregateReport duplicateReport;
  protected static AggregateReport duplicateReportWithJurisdiction;

  @Inject
  public CreateNewAggregateReportSteps(
      WebDriverHelpers webDriverHelpers,
      SoftAssert softly,
      AggregateReportService aggregateReportService) {
    this.webDriverHelpers = webDriverHelpers;
    duplicateReport = aggregateReportService.buildAggredateReportsForDuplicates();
    duplicateReportWithJurisdiction =
        aggregateReportService.buildAggregateReportsForDuplicatesWithJurisdiction();
    When(
        "I check if Region combobox is set to {string} and is not editable in Create New Aggregate Report popup",
        (String region) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(REGION_COMBOBOX_POPUP), region);
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(REGION_COMBOBOX_POPUP),
              "Region combobox is not disabled");
          softly.assertAll();
        });

    When(
        "I check if District combobox is set to {string} and is not editable in Create New Aggregate Report popup",
        (String region) -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(DISTRICT_COMBOBOX_POPUP), region);
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(DISTRICT_COMBOBOX_POPUP),
              "District combobox is not disabled");
          softly.assertAll();
        });
    When(
        "I set Region combobox to {string} in Create New Aggregate Report popup",
        (String region) -> webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_POPUP_DIV, region));
    When(
        "I set District combobox to {string} in Create New Aggregate Report popup",
        (String region) ->
            webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX_POPUP_DIV, region));
    When(
        "^I fill a new aggregate report with specific data$",
        () -> {
          report = aggregateReportService.buildGeneratedAggregateReport();
          fillAllFieldsForAggregateReport(report);
        });
    When(
        "^I fill a new aggregate report with specific data for duplicates$",
        () -> fillFieldsForDuplicateReport(duplicateReport));
    When(
        "^I fill a new aggregate report with specific data for duplicates with different disease$",
        () -> fillFieldsForDuplicateReportWithDifferentDisease(duplicateReport));
    When(
        "^I fill a new aggregate report with specific data for one disease",
        () -> {
          report = aggregateReportService.buildAggredateReportsForOneDisease();
          fillFieldsForOneDisease(report);
        });
    When(
        "I fill a new aggregate report with {string} to provoke error and check error message",
        (String data) -> {
          String expectedMessage =
              "Please check the entered data. At least one of the fields where an integer was expected contains something else.";
          webDriverHelpers.fillInWebElement(
              getCasesInputByDisease("Acute Viral Hepatitis"), String.valueOf(data));
          webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_SAVE_BUTTON);
          softly.assertEquals(
              webDriverHelpers.getTextFromWebElement(ERROR_MESSAGE),
              expectedMessage,
              "Error message is not visible");
          softly.assertAll();
          webDriverHelpers.clickOnWebElementBySelector(ERROR_MESSAGE);
        });
    When(
        "^I change all fields of aggregate report$",
        () -> {
          report = aggregateReportService.buildEditAggregateReport();
          fillAllFieldsForEditAggregateReport(report);
        });
    When(
        "^I check if message about duplicated reports is visible$",
        () -> {
          boolean visible =
              webDriverHelpers.isElementVisibleWithTimeout(DUPLICATE_DETECTION_TEXT, 5);
          softly.assertTrue(
              visible, "Duplicate detection is not visible in new Aggregate report form");
          softly.assertAll();
        });

    When(
        "^I click to save aggregated report$",
        () -> webDriverHelpers.clickOnWebElementBySelector(LINE_LISTING_SAVE_BUTTON));
    When(
        "^I click to delete aggregated report$",
        () -> webDriverHelpers.clickOnWebElementBySelector(DELETE_AGGREGATED_REPORT_BUTTON));
    When(
        "^I click on ([^\"]*) Radiobutton in Create Aggregated Report form$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(WEEK_RADIOBUTTON, buttonName));
    When(
        "I check the created data is correctly displayed in new Aggregate Report form",
        () -> {
          collectedReport = collectAggregateReportData();
          AggregateReport createdReport = CreateNewAggregateReportSteps.report;
          ComparisonHelper.compareEqualEntities(collectedReport, createdReport);
        });
    When(
        "I check the edited data is correctly displayed in new Aggregate Report form",
        () -> {
          collectedReport = collectEditedAggregateReport();
          AggregateReport createdReport = CreateNewAggregateReportSteps.report;
          ComparisonHelper.compareEqualEntities(collectedReport, createdReport);
        });
    And(
        "^I set report period to \"([^\"]*)\" on Create a new aggregated report form$",
        (String buttonName) ->
            webDriverHelpers.clickWebElementByText(WEEK_RADIOBUTTON, buttonName));
    And(
        "^I check if Epi week filed is enabled on Create a new aggregated report form$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(EPI_WEEK_INPUT_POPUP);
          Assert.assertTrue(
              webDriverHelpers.isElementEnabled(EPI_WEEK_INPUT_POPUP), "Year field is not enabled");
        });
    Then(
        "^I check if last listed week from Epi week combobox is the current week of the year$",
        () -> {
          int currentWeek = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
          int nextYear = LocalDate.now().getYear() + 1;

          if (currentWeek < 52) {
            int nextWeek = currentWeek + 1;
            String nextEpiWeek = "Wk " + nextWeek + "-" + LocalDate.now().getYear();
            Assert.assertFalse(
                webDriverHelpers.checkIfElementExistsInCombobox(
                    EPI_WEEK_COMBOBOX_POPUP, nextEpiWeek));
          } else if (currentWeek == 52) {
            int nextWeek = 1;
            String nextEpiWeek = "Wk " + nextWeek + "-" + nextYear;
            Assert.assertFalse(
                webDriverHelpers.checkIfElementExistsInCombobox(
                    EPI_WEEK_COMBOBOX_POPUP, nextEpiWeek));
          }
          String currentEpiWeek = "Wk " + currentWeek + "-" + LocalDate.now().getYear();
          webDriverHelpers.selectFromCombobox(EPI_WEEK_COMBOBOX_POPUP, currentEpiWeek);
        });

    And(
        "^I set (\\d+) as the quantity for Snake Bite suspected cases in Create a new aggregated report$",
        (Integer quantity) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(SNAKE_BITE_SUSPECTED_CASES_INPUT);
          webDriverHelpers.fillInWebElement(
              SNAKE_BITE_SUSPECTED_CASES_INPUT, String.valueOf(quantity));
        });
    When(
        "^I close popup message window in Create New Aggregate Report popup$",
        () -> {
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(POPUP_MESSAGE_WINDOW);
          webDriverHelpers.clickOnWebElementBySelector(POPUP_MESSAGE_WINDOW);
        });
    And(
        "^I check that District combobox is disabled in Create New Aggregate Report popup$",
        () -> {
          softly.assertFalse(
              webDriverHelpers.isElementEnabled(DISTRICT_COMBOBOX_POPUP),
              "District combobox is enabled");
          softly.assertAll();
        });
    And(
        "^I fill a new aggregate report with specific data for duplicates with jurisdiction$",
        () -> {
          fillFieldsForDuplicateReportWithJurisdiction(duplicateReportWithJurisdiction);
        });
    And(
        "^I check if age groups are visible for \"([^\"]*)\"$",
        (String disease) -> {
          webDriverHelpers.waitUntilIdentifiedElementIsVisibleAndClickable(
              DISTRICT_COMBOBOX_POPUP_DIV);
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "0-28 days"));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "29 days - 2 months"));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "3-12 months"));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "1-4 years"));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "5-15 years"));
          webDriverHelpers.waitUntilIdentifiedElementIsPresent(
              getAgeGroupDiseaseByDiseaseAndAgeGroup(disease, "16+ years"));
        });
    And(
        "^I fill a new aggregate report with specific age groups$",
        () -> {
          report = aggregateReportService.buildAggregateReportForSpecificAgeGroups();
          fillSpecificFieldsForAggregateReportAgeGroups(report);
        });
  }

  private void fillAllFieldsForEditAggregateReport(AggregateReport report) {
    fillCasesFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisCases());
    fillLabConfirmationsFor(
        "Acute Viral Hepatitis", report.getAcuteViralHepatitisLabConfirmations());
    fillDeathsFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisDeaths());
    fillCasesFor("Buruli Ulcer", report.getBuruliUlcerCases());
    fillLabConfirmationsFor("Buruli Ulcer", report.getBuruliUlcerLabConfirmations());
    fillDeathsFor("Buruli Ulcer", report.getBuruliUlcerDeaths());
    fillCasesFor("Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodCases());
    fillLabConfirmationsFor(
        "Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodLabConfirmations());
    fillDeathsFor("Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodDeaths());
    fillCasesFor("Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationCases());
    fillLabConfirmationsFor(
        "Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationLabConfirmations());
    fillDeathsFor("Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationDeaths());
    fillCasesFor("Diphteria", report.getDiphteriaCases());
    fillLabConfirmationsFor("Diphteria", report.getDiphteriaLabConfirmations());
    fillDeathsFor("Diphteria", report.getDiphteriaDeaths());
    fillCasesFor("HIV", report.getHivCases());
    fillLabConfirmationsFor("HIV", report.getHivLabConfirmations());
    fillDeathsFor("HIV", report.getHivDeaths());
    fillCasesFor("Leprosy", report.getLeprosyCases());
    fillLabConfirmationsFor("Leprosy", report.getLeprosyLabConfirmations());
    fillDeathsFor("Leprosy", report.getLeprosyDeaths());
    fillCasesFor("Lymphatic Filariasis", report.getLymphaticFilariasisCases());
    fillLabConfirmationsFor(
        "Lymphatic Filariasis", report.getLymphaticFilariasisLabConfirmations());
    fillDeathsFor("Lymphatic Filariasis", report.getLymphaticFilariasisDeaths());
    fillCasesFor("Malaria", report.getMalariaCases());
    fillLabConfirmationsFor("Malaria", report.getMalariaLabConfirmations());
    fillDeathsFor("Malaria", report.getMalariaDeaths());
    fillCasesFor("Maternal Deaths", report.getMaternalDeathsCases());
    fillLabConfirmationsFor("Maternal Deaths", report.getMaternalDeathsLabConfirmations());
    fillDeathsFor("Maternal Deaths", report.getMaternalDeathsDeaths());
    fillCasesFor("Neonatal Tetanus", report.getNeonatalTetanusCases());
    fillLabConfirmationsFor("Neonatal Tetanus", report.getNeonatalTetanusLabConfirmations());
    fillDeathsFor("Neonatal Tetanus", report.getNeonatalTetanusDeaths());
    fillCasesFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusCases());
    fillLabConfirmationsFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusLabConfirmations());
    fillDeathsFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusDeaths());
    fillCasesFor("Onchocerciasis", report.getOnchocerciasisCases());
    fillLabConfirmationsFor("Onchocerciasis", report.getOnchocerciasisLabConfirmations());
    fillDeathsFor("Onchocerciasis", report.getOnchocerciasisDeaths());
    fillCasesFor("Perinatal Deaths", report.getPerinatalDeathsCases());
    fillLabConfirmationsFor("Perinatal Deaths", report.getPerinatalDeathsLabConfirmations());
    fillDeathsFor("Perinatal Deaths", report.getPerinatalDeathsDeaths());
    fillCasesFor("Pertussis", report.getPertussisCases());
    fillLabConfirmationsFor("Pertussis", report.getPertussisLabConfirmations());
    fillDeathsFor("Pertussis", report.getPertussisDeaths());
    fillCasesFor("Rubella", report.getRubellaCases());
    fillLabConfirmationsFor("Rubella", report.getRubellaLabConfirmations());
    fillDeathsFor("Rubella", report.getRubellaDeaths());
    fillCasesFor("Schistosomiasis", report.getSchistosomiasisCases());
    fillLabConfirmationsFor("Schistosomiasis", report.getSchistosomiasisLabConfirmations());
    fillDeathsFor("Schistosomiasis", report.getSchistosomiasisDeaths());
    fillCasesFor("Snake Bite", report.getSnakeBiteCases());
    fillLabConfirmationsFor("Snake Bite", report.getSnakeBiteLabConfirmations());
    fillDeathsFor("Snake Bite", report.getSnakeBiteDeaths());
    fillCasesFor("Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsCases());
    fillLabConfirmationsFor(
        "Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsLabConfirmations());
    fillDeathsFor("Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsDeaths());
    fillCasesFor("Trachoma", report.getTrachomaCases());
    fillLabConfirmationsFor("Trachoma", report.getTrachomaLabConfirmations());
    fillDeathsFor("Trachoma", report.getTrachomaDeaths());
    fillCasesFor("Trypanosomiasis", report.getTrypanosomiasisCases());
    fillLabConfirmationsFor("Trypanosomiasis", report.getTrypanosomiasisLabConfirmations());
    fillDeathsFor("Trypanosomiasis", report.getTrypanosomiasisDeaths());
    fillCasesFor("Tuberculosis", report.getTuberculosisCases());
    fillLabConfirmationsFor("Tuberculosis", report.getTuberculosisConfirmations());
    fillDeathsFor("Tuberculosis", report.getTuberculosisDeaths());
    fillCasesFor("Typhoid Fever", report.getTyphoidFeverCases());
    fillLabConfirmationsFor("Typhoid Fever", report.getTyphoidFeverLabConfirmations());
    fillDeathsFor("Typhoid Fever", report.getTyphoidFeverDeaths());
    fillCasesFor("Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisCases());
    fillLabConfirmationsFor(
        "Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisLabConfirmations());
    fillDeathsFor("Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisDeaths());
  }

  private void fillFieldsForDuplicateReport(AggregateReport report) {
    fillYear(report.getYear());
    fillEpiWeek(report.getEpiWeek());
    fillCasesFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisCases());
  }

  private void fillFieldsForDuplicateReportWithJurisdiction(AggregateReport report) {
    fillYear(report.getYear());
    fillEpiWeek(report.getEpiWeek());
    fillRegion(report.getRegion());
    fillDistrict(report.getDistrict());
    fillCasesFor("Malaria", report.getMalariaCases());
  }

  private void fillFieldsForDuplicateReportWithDifferentDisease(AggregateReport report) {
    fillYear(report.getYear());
    fillEpiWeek(report.getEpiWeek());
    fillCasesFor("Buruli Ulcer", report.getBuruliUlcerCases());
  }

  private void fillFieldsForOneDisease(AggregateReport report) {
    fillCasesFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisCases());
  }

  private void fillAllFieldsForAggregateReport(AggregateReport report) {
    fillYear(report.getYear());
    fillEpiWeek(report.getEpiWeek());
    fillCasesFor("ARI (Acute Respiratory Infections)", report.getAcuteRespiratoryInfectionsCases());
    fillLabConfirmationsFor(
        "ARI (Acute Respiratory Infections)",
        report.getAcuteRespiratoryInfectionsLabConfirmations());
    fillDeathsFor(
        "ARI (Acute Respiratory Infections)", report.getAcuteRespiratoryInfectionsDeaths());
    fillCasesFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisCases());
    fillLabConfirmationsFor(
        "Acute Viral Hepatitis", report.getAcuteViralHepatitisLabConfirmations());
    fillDeathsFor("Acute Viral Hepatitis", report.getAcuteViralHepatitisDeaths());
    fillCasesFor("Buruli Ulcer", report.getBuruliUlcerCases());
    fillLabConfirmationsFor("Buruli Ulcer", report.getBuruliUlcerLabConfirmations());
    fillDeathsFor("Buruli Ulcer", report.getBuruliUlcerDeaths());
    fillCasesFor("Chikungunya", report.getChikungunyaCases());
    fillLabConfirmationsFor("Chikungunya", report.getChikungunyaLabConfirmations());
    fillDeathsFor("Chikungunya", report.getChikungunyaDeaths());
    fillCasesFor("Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodCases());
    fillLabConfirmationsFor(
        "Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodLabConfirmations());
    fillDeathsFor("Diarrhea w/ Blood (Shigella)", report.getDiarrheaBloodDeaths());
    fillCasesFor("Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationCases());
    fillLabConfirmationsFor(
        "Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationLabConfirmations());
    fillDeathsFor("Diarrhea w/ Dehydration (< 5)", report.getDiarrheaDehydrationDeaths());
    fillCasesFor("Diphteria", report.getDiphteriaCases());
    fillLabConfirmationsFor("Diphteria", report.getDiphteriaLabConfirmations());
    fillDeathsFor("Diphteria", report.getDiphteriaDeaths());
    fillCasesFor(
        "FHA (Functional Hypothalamic Amenorrhea)",
        report.getFunctionalHypothalamicAmenorrheaCases());
    fillLabConfirmationsFor(
        "FHA (Functional Hypothalamic Amenorrhea)",
        report.getFunctionalHypothalamicAmenorrheaLabConfirmations());
    fillDeathsFor(
        "FHA (Functional Hypothalamic Amenorrhea)",
        report.getFunctionalHypothalamicAmenorrheaDeaths());
    fillCasesFor("HIV", report.getHivCases());
    fillLabConfirmationsFor("HIV", report.getHivLabConfirmations());
    fillDeathsFor("HIV", report.getHivDeaths());
    fillCasesFor("Leprosy", report.getLeprosyCases());
    fillLabConfirmationsFor("Leprosy", report.getLeprosyLabConfirmations());
    fillDeathsFor("Leprosy", report.getLeprosyDeaths());
    fillCasesFor("Lymphatic Filariasis", report.getLymphaticFilariasisCases());
    fillLabConfirmationsFor(
        "Lymphatic Filariasis", report.getLymphaticFilariasisLabConfirmations());
    fillDeathsFor("Lymphatic Filariasis", report.getLymphaticFilariasisDeaths());
    fillCasesFor("Malaria", report.getMalariaCases());
    fillLabConfirmationsFor("Malaria", report.getMalariaLabConfirmations());
    fillDeathsFor("Malaria", report.getMalariaDeaths());
    fillCasesFor("Maternal Deaths", report.getMaternalDeathsCases());
    fillLabConfirmationsFor("Maternal Deaths", report.getMaternalDeathsLabConfirmations());
    fillDeathsFor("Maternal Deaths", report.getMaternalDeathsDeaths());
    fillCasesFor("Neonatal Tetanus", report.getNeonatalTetanusCases());
    fillLabConfirmationsFor("Neonatal Tetanus", report.getNeonatalTetanusLabConfirmations());
    fillDeathsFor("Neonatal Tetanus", report.getNeonatalTetanusDeaths());
    fillCasesFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusCases());
    fillLabConfirmationsFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusLabConfirmations());
    fillDeathsFor("Non-Neonatal Tetanus", report.getNonNeonatalTetanusDeaths());
    fillCasesFor("Onchocerciasis", report.getOnchocerciasisCases());
    fillLabConfirmationsFor("Onchocerciasis", report.getOnchocerciasisLabConfirmations());
    fillDeathsFor("Onchocerciasis", report.getOnchocerciasisDeaths());
    fillCasesFor("Perinatal Deaths", report.getPerinatalDeathsCases());
    fillLabConfirmationsFor("Perinatal Deaths", report.getPerinatalDeathsLabConfirmations());
    fillDeathsFor("Perinatal Deaths", report.getPerinatalDeathsDeaths());
    fillCasesFor("Pertussis", report.getPertussisCases());
    fillLabConfirmationsFor("Pertussis", report.getPertussisLabConfirmations());
    fillDeathsFor("Pertussis", report.getPertussisDeaths());
    fillCasesFor(
        "Post-immunization adverse events mild",
        report.getPostImmunizationAdverseEventsMildCases());
    fillLabConfirmationsFor(
        "Post-immunization adverse events mild",
        report.getPostImmunizationAdverseEventsMildLabConfirmations());
    fillDeathsFor(
        "Post-immunization adverse events mild",
        report.getPostImmunizationAdverseEventsMildDeaths());
    fillCasesFor(
        "Post-immunization adverse events severe",
        report.getPostImmunizationAdverseEventsSevereCases());
    fillLabConfirmationsFor(
        "Post-immunization adverse events severe",
        report.getPostImmunizationAdverseEventsSevereLabConfirmations());
    fillDeathsFor(
        "Post-immunization adverse events severe",
        report.getPostImmunizationAdverseEventsSevereDeaths());
    fillCasesFor("Rubella", report.getRubellaCases());
    fillLabConfirmationsFor("Rubella", report.getRubellaLabConfirmations());
    fillDeathsFor("Rubella", report.getRubellaDeaths());
    fillCasesFor("Schistosomiasis", report.getSchistosomiasisCases());
    fillLabConfirmationsFor("Schistosomiasis", report.getSchistosomiasisLabConfirmations());
    fillDeathsFor("Schistosomiasis", report.getSchistosomiasisDeaths());
    fillCasesFor("Snake Bite", report.getSnakeBiteCases());
    fillLabConfirmationsFor("Snake Bite", report.getSnakeBiteLabConfirmations());
    fillDeathsFor("Snake Bite", report.getSnakeBiteDeaths());
    fillCasesFor("Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsCases());
    fillLabConfirmationsFor(
        "Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsLabConfirmations());
    fillDeathsFor("Soil-Transmitted Helminths", report.getSoliTransmittedHelminthsDeaths());
    fillCasesFor("Trachoma", report.getTrachomaCases());
    fillLabConfirmationsFor("Trachoma", report.getTrachomaLabConfirmations());
    fillDeathsFor("Trachoma", report.getTrachomaDeaths());
    fillCasesFor("Trypanosomiasis", report.getTrypanosomiasisCases());
    fillLabConfirmationsFor("Trypanosomiasis", report.getTrypanosomiasisLabConfirmations());
    fillDeathsFor("Trypanosomiasis", report.getTrypanosomiasisDeaths());
    fillCasesFor("Tuberculosis", report.getTuberculosisCases());
    fillLabConfirmationsFor("Tuberculosis", report.getTuberculosisConfirmations());
    fillDeathsFor("Tuberculosis", report.getTuberculosisDeaths());
    fillCasesFor("Typhoid Fever", report.getTyphoidFeverCases());
    fillLabConfirmationsFor("Typhoid Fever", report.getTyphoidFeverLabConfirmations());
    fillDeathsFor("Typhoid Fever", report.getTyphoidFeverDeaths());
    fillCasesFor("Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisCases());
    fillLabConfirmationsFor(
        "Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisLabConfirmations());
    fillDeathsFor("Yaws and Endemic Syphilis", report.getYawsAndEndemicSyphilisDeaths());
  }

  private void fillSpecificFieldsForAggregateReportAgeGroups(AggregateReport report) {
    fillYear(report.getYear());
    fillEpiWeek(report.getEpiWeek());
    fillRegion(report.getRegion());
    fillDistrict(report.getDistrict());
    fillCasesWithAgeGroupFor("Malaria", report.getAgeGroupForMalaria(), report.getMalariaCases());
    fillLabConfirmationsWithAgeGroupFor(
        "Acute Viral Hepatitis", "16+ years", report.getAcuteViralHepatitisLabConfirmations());
    fillDeathsWithAgeGroupFor("HIV", "0-28 days", report.getHivDeaths());
  }

  private AggregateReport collectEditedAggregateReport() {
    return AggregateReport.builder()
        .acuteViralHepatitisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Acute Viral Hepatitis"))))
        .acuteViralHepatitisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Acute Viral Hepatitis"))))
        .acuteViralHepatitisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Acute Viral Hepatitis"))))
        .buruliUlcerCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Buruli Ulcer"))))
        .buruliUlcerLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Buruli Ulcer"))))
        .buruliUlcerDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Buruli Ulcer"))))
        .diarrheaBloodCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaBloodLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaBloodDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaDehydrationCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diarrheaDehydrationLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diarrheaDehydrationDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diphteriaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Diphteria"))))
        .diphteriaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diphteria"))))
        .diphteriaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Diphteria"))))
        .hivCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("HIV"))))
        .hivLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getLabConfirmationsInputByDisease("HIV"))))
        .hivDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("HIV"))))
        .leprosyCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Leprosy"))))
        .leprosyLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Leprosy"))))
        .leprosyDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Leprosy"))))
        .lymphaticFilariasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Lymphatic Filariasis"))))
        .lymphaticFilariasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Lymphatic Filariasis"))))
        .lymphaticFilariasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Lymphatic Filariasis"))))
        .malariaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Malaria"))))
        .malariaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Malaria"))))
        .malariaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Malaria"))))
        .maternalDeathsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Maternal Deaths"))))
        .maternalDeathsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Maternal Deaths"))))
        .maternalDeathsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Maternal Deaths"))))
        .neonatalTetanusCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Neonatal Tetanus"))))
        .neonatalTetanusDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Neonatal Tetanus"))))
        .neonatalTetanusLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Neonatal Tetanus"))))
        .nonNeonatalTetanusCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Non-Neonatal Tetanus"))))
        .nonNeonatalTetanusLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Non-Neonatal Tetanus"))))
        .nonNeonatalTetanusDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Non-Neonatal Tetanus"))))
        .onchocerciasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Onchocerciasis"))))
        .onchocerciasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Onchocerciasis"))))
        .onchocerciasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Onchocerciasis"))))
        .perinatalDeathsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Perinatal Deaths"))))
        .perinatalDeathsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Perinatal Deaths"))))
        .perinatalDeathsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Perinatal Deaths"))))
        .pertussisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Pertussis"))))
        .pertussisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Pertussis"))))
        .pertussisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Pertussis"))))
        .rubellaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Rubella"))))
        .rubellaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Rubella"))))
        .rubellaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Rubella"))))
        .schistosomiasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Schistosomiasis"))))
        .schistosomiasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Schistosomiasis"))))
        .schistosomiasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Schistosomiasis"))))
        .snakeBiteCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Snake Bite"))))
        .snakeBiteLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Snake Bite"))))
        .snakeBiteDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Snake Bite"))))
        .soliTransmittedHelminthsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Soil-Transmitted Helminths"))))
        .soliTransmittedHelminthsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Soil-Transmitted Helminths"))))
        .soliTransmittedHelminthsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Soil-Transmitted Helminths"))))
        .trachomaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Trachoma"))))
        .trachomaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Trachoma"))))
        .trachomaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Trachoma"))))
        .trypanosomiasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Trypanosomiasis"))))
        .trypanosomiasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Trypanosomiasis"))))
        .trypanosomiasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Trypanosomiasis"))))
        .typhoidFeverCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Typhoid Fever"))))
        .typhoidFeverLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Typhoid Fever"))))
        .typhoidFeverDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Typhoid Fever"))))
        .tuberculosisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Tuberculosis"))))
        .tuberculosisConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Tuberculosis"))))
        .tuberculosisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Tuberculosis"))))
        .yawsAndEndemicSyphilisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Yaws and Endemic Syphilis"))))
        .yawsAndEndemicSyphilisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Yaws and Endemic Syphilis"))))
        .yawsAndEndemicSyphilisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Yaws and Endemic Syphilis"))))
        .build();
  }

  private AggregateReport collectAggregateReportData() {
    return AggregateReport.builder()
        .year(webDriverHelpers.getValueFromWebElement(YEAR_INPUT_POPUP))
        .epiWeek(webDriverHelpers.getValueFromWebElement(EPI_WEEK_INPUT_POPUP))
        .acuteRespiratoryInfectionsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("ARI (Acute Respiratory Infections)"))))
        .acuteRespiratoryInfectionsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("ARI (Acute Respiratory Infections)"))))
        .acuteRespiratoryInfectionsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("ARI (Acute Respiratory Infections)"))))
        .acuteViralHepatitisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Acute Viral Hepatitis"))))
        .acuteViralHepatitisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Acute Viral Hepatitis"))))
        .acuteViralHepatitisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Acute Viral Hepatitis"))))
        .buruliUlcerCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Buruli Ulcer"))))
        .buruliUlcerLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Buruli Ulcer"))))
        .buruliUlcerDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Buruli Ulcer"))))
        .chikungunyaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Chikungunya"))))
        .chikungunyaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Chikungunya"))))
        .chikungunyaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Chikungunya"))))
        .diarrheaBloodCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaBloodLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaBloodDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Diarrhea w/ Blood (Shigella)"))))
        .diarrheaDehydrationCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diarrheaDehydrationLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diarrheaDehydrationDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Diarrhea w/ Dehydration (< 5)"))))
        .diphteriaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Diphteria"))))
        .diphteriaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Diphteria"))))
        .diphteriaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Diphteria"))))
        .functionalHypothalamicAmenorrheaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("FHA (Functional Hypothalamic Amenorrhea)"))))
        .functionalHypothalamicAmenorrheaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("FHA (Functional Hypothalamic Amenorrhea)"))))
        .functionalHypothalamicAmenorrheaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("FHA (Functional Hypothalamic Amenorrhea)"))))
        .hivCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("HIV"))))
        .hivLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getLabConfirmationsInputByDisease("HIV"))))
        .hivDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("HIV"))))
        .leprosyCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Leprosy"))))
        .leprosyLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Leprosy"))))
        .leprosyDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Leprosy"))))
        .lymphaticFilariasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Lymphatic Filariasis"))))
        .lymphaticFilariasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Lymphatic Filariasis"))))
        .lymphaticFilariasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Lymphatic Filariasis"))))
        .malariaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Malaria"))))
        .malariaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Malaria"))))
        .malariaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Malaria"))))
        .maternalDeathsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Maternal Deaths"))))
        .maternalDeathsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Maternal Deaths"))))
        .maternalDeathsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Maternal Deaths"))))
        .neonatalTetanusCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Neonatal Tetanus"))))
        .neonatalTetanusDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Neonatal Tetanus"))))
        .neonatalTetanusLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Neonatal Tetanus"))))
        .nonNeonatalTetanusCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Non-Neonatal Tetanus"))))
        .nonNeonatalTetanusLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Non-Neonatal Tetanus"))))
        .nonNeonatalTetanusDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Non-Neonatal Tetanus"))))
        .onchocerciasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Onchocerciasis"))))
        .onchocerciasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Onchocerciasis"))))
        .onchocerciasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Onchocerciasis"))))
        .perinatalDeathsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Perinatal Deaths"))))
        .perinatalDeathsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Perinatal Deaths"))))
        .perinatalDeathsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Perinatal Deaths"))))
        .pertussisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Pertussis"))))
        .pertussisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Pertussis"))))
        .pertussisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Pertussis"))))
        .postImmunizationAdverseEventsMildCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Post-immunization adverse events mild"))))
        .postImmunizationAdverseEventsMildLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Post-immunization adverse events mild"))))
        .postImmunizationAdverseEventsMildDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Post-immunization adverse events mild"))))
        .postImmunizationAdverseEventsSevereCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Post-immunization adverse events severe"))))
        .postImmunizationAdverseEventsSevereLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Post-immunization adverse events severe"))))
        .postImmunizationAdverseEventsSevereDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Post-immunization adverse events severe"))))
        .rubellaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Rubella"))))
        .rubellaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Rubella"))))
        .rubellaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Rubella"))))
        .schistosomiasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Schistosomiasis"))))
        .schistosomiasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Schistosomiasis"))))
        .schistosomiasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Schistosomiasis"))))
        .snakeBiteCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Snake Bite"))))
        .snakeBiteLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Snake Bite"))))
        .snakeBiteDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Snake Bite"))))
        .soliTransmittedHelminthsCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Soil-Transmitted Helminths"))))
        .soliTransmittedHelminthsLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Soil-Transmitted Helminths"))))
        .soliTransmittedHelminthsDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Soil-Transmitted Helminths"))))
        .trachomaCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Trachoma"))))
        .trachomaLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Trachoma"))))
        .trachomaDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Trachoma"))))
        .trypanosomiasisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Trypanosomiasis"))))
        .trypanosomiasisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Trypanosomiasis"))))
        .trypanosomiasisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Trypanosomiasis"))))
        .typhoidFeverCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Typhoid Fever"))))
        .typhoidFeverLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Typhoid Fever"))))
        .typhoidFeverDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Typhoid Fever"))))
        .tuberculosisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getCasesInputByDisease("Tuberculosis"))))
        .tuberculosisConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Tuberculosis"))))
        .tuberculosisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(getDeathInputByDisease("Tuberculosis"))))
        .yawsAndEndemicSyphilisCases(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getCasesInputByDisease("Yaws and Endemic Syphilis"))))
        .yawsAndEndemicSyphilisLabConfirmations(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getLabConfirmationsInputByDisease("Yaws and Endemic Syphilis"))))
        .yawsAndEndemicSyphilisDeaths(
            Integer.parseInt(
                webDriverHelpers.getValueFromWebElement(
                    getDeathInputByDisease("Yaws and Endemic Syphilis"))))
        .build();
  }

  private void fillCasesFor(String disease, int numberOfCases) {
    webDriverHelpers.fillInWebElement(
        getCasesInputByDisease(disease), String.valueOf(numberOfCases));
  }

  private void fillCasesWithAgeGroupFor(String disease, String ageGroup, int numberOfCases) {
    webDriverHelpers.fillInWebElement(
        getCasesInputByDiseaseAndAgeGroup(disease, ageGroup), String.valueOf(numberOfCases));
  }

  private void fillLabConfirmationsFor(String disease, int numberOfLabConfirmations) {
    webDriverHelpers.fillInWebElement(
        getLabConfirmationsInputByDisease(disease), String.valueOf(numberOfLabConfirmations));
  }

  private void fillLabConfirmationsWithAgeGroupFor(
      String disease, String ageGroup, int numberOfLabConfirmations) {
    webDriverHelpers.fillInWebElement(
        getLabConfirmationsInputByDiseaseAndAgeGroup(disease, ageGroup),
        String.valueOf(numberOfLabConfirmations));
  }

  private void fillDeathsFor(String disease, int numberOfDeaths) {
    webDriverHelpers.fillInWebElement(
        getDeathInputByDisease(disease), String.valueOf(numberOfDeaths));
  }

  private void fillDeathsWithAgeGroupFor(String disease, String ageGroup, int numberOfDeaths) {
    webDriverHelpers.fillInWebElement(
        getDeathInputByDiseaseAndAgeGroup(disease, ageGroup), String.valueOf(numberOfDeaths));
  }

  private void fillYear(String year) {
    webDriverHelpers.scrollToElement(YEAR_COMBOBOX_POPUP);
    webDriverHelpers.selectFromCombobox(YEAR_COMBOBOX_POPUP, year);
  }

  private void fillEpiWeek(String epiWeek) {
    webDriverHelpers.clearComboboxInput(EPI_WEEK_INPUT_POPUP);
    webDriverHelpers.selectFromCombobox(EPI_WEEK_COMBOBOX_POPUP, epiWeek);
  }

  private void fillRegion(String region) {
    webDriverHelpers.selectFromCombobox(REGION_COMBOBOX_POPUP_DIV, region);
  }

  private void fillDistrict(String district) {
    webDriverHelpers.selectFromCombobox(DISTRICT_COMBOBOX_POPUP_DIV, district);
  }
}
