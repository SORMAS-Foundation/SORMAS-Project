package org.sormas.e2etests.pages.application.messages;

import org.openqa.selenium.By;

public class MessagesDirectoryPage {
  public static final By FETCH_MESSAGES_BUTTON = By.cssSelector("[id='externalMessageFetch']");
  public static final By MESSAGES_TABLE_DATA = By.tagName("td");
  public static final By MESSAGES_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By MESSAGES_DETAILED_TABLE_ROWS =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By MESSAGE_EYE_ICON =
      By.xpath("(//div//span[@class='v-icon v-icon-eye'])[1]");
  public static final By MESSAGE_UUID_TEXT = By.id("uuid");
  public static final By MESSAGE_POPUP_HEADER =
      By.xpath("//div[@class='popupContent']//div[@class='v-window-header' and text()='Meldung']");
  public static final By UUID_HEADER = By.xpath("//thead//tr//th[2]");
  public static final By SEARCH_MESSAGE_INPUT = By.id("searchFieldLike");
  public static final By VERARBEITEN_BUTTON = By.id("externalMessageProcess");

  public static final By RELATED_FORWARDED_MESSAGE_HEADER =
      By.xpath("//*[contains(text(),'Zugeh\u00F6rige weitergeleitete Meldung(en) gefunden')]");
  public static final By POPUP_CONFIRM_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static final By CHOOSE_OR_CREATE_ENTRY_HEADER =
      By.xpath("//*[contains(text(),'Eintrag ausw\u00E4hlen oder erstellen')]");
  public static final By UPDATE_THE_DISEASE_VARIANT_HEADER =
      By.xpath("//*[contains(text(),'Krankheitsvariante des Falls aktualisieren')]");
  public static final By CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_CONTACT_HEADER_DE =
      By.xpath(
          "//*[contains(text(),'Soll ein Fall aus dem Kontakt mit positivem Testresultat erstellt werden?')]");

  public static final By CREATE_A_NEW_CASE_WITH_POSITIVE_TEST_EVENT_PARTICIPANT_HEADER_DE =
      By.xpath(
          "//*[contains(text(),'M\u00F6chten Sie einen Fall f\u00FCr den Ereignisteilnehmer mit postitivem Testresultat erstellen?')]");
  public static final By SAVE_POPUP_CONTENT_FIRST_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[1]");
  public static final By SAVE_POPUP_CONTENT_SECOND_BUTTON =
      By.xpath("(//div[@class='popupContent']//div[@id='commit'])[2]");
  public static final By NO_NEW_REPORTS_POPUP =
      By.xpath("//*[contains(text(),'Keine neuen Meldungen verf\u00FCgbar')]");
  public static final By CLOSE_POPUP = By.xpath("//div[@class='v-window-closebox']");
  public static final By DIAGNOSED_AT_DATE_DE =
      By.xpath(
          "//div[@location='samples']//div[contains(text(),'Datum und Uhrzeit des Ergebnisses:')]\n");
  public static final By GET_NEW_MESSAGES_POPUP =
      By.xpath("//*[contains(text(),'Neue Meldungen abrufen')]");
  public static final By FIRST_TIME_FETCH_MESSAGE_POPUP =
      By.xpath("//*[contains(text(), 'Dies ist das erste Mal, dass Meldungen abgerufen werden.')]");
  public static final By ACTION_YES_BUTTON = By.id("actionYes");
  public static final By FETCH_MESSAGES_NULL_DATE = By.cssSelector(".v-window #null_date input");
  public static final By FETCH_MESSAGES_NULL_TIME_COMBOBOX =
      By.cssSelector(".v-window #null_time div");

  public static By checkMappedValueSelector(String value) {
    return By.xpath(
        String.format(
            "//div[@class='v-label v-widget bold v-label-bold uppercase v-label-uppercase v-label-undef-w' and contains(text(), '%s')]",
            value));
  }

  public static By getProcessMessageButtonByIndex(int index) {
    return By.xpath(
        String.format("//table/tbody[1]/tr[%s]//div[@id='externalMessageProcess']", index));
  }

  public static By getProcessStatusByIndex(int index) {
    return By.xpath(String.format("//table/tbody[1]/tr[%s]/td[13]", index));
  }

  public static final By CREATE_NEW_CASE_POPUP_WINDOW_DE =
      By.xpath("//div[@class= 'v-window-header'][text()='Neuen Fall erstellen']");
  public static final By CREATE_NEW_SAMPLE_POPUP_WINDOW_DE =
      By.xpath("//div[@class= 'v-window-header'][text()='Neue Probe erstellen']");
  public static final By UPDATE_CASE_DISEASE_VARIANT_CONFIRM_BUTTON =
      By.cssSelector(".popupContent #actionConfirm");
  public static final By POPUP_WINDOW_CANCEL_BUTTON = By.xpath("(//*[@id='discard'])[1]");
  public static final By POPUP_WINDOW_SAVE_AND_OPEN_CASE_BUTTON =
      By.cssSelector("#saveAndOpenEntryButton");
  public static final By POPUP_WINDOW_DISCARD_BUTTON = By.xpath("//*[@id='discard']");
  public static final By POPUP_WINDOW_SAVE_BUTTON = By.cssSelector("#commit");
  public static final By MESSAGE_DIRECTORY_HEADER_DE =
      By.xpath("//div[@class='v-slot v-slot-view-header']//div[text()='Meldungsverzeichnis']");
  public static final By PATHOGEN_DETECTION_REPORTING_PROCESS_HEADER_DE =
      By.xpath(
          "//div[@location='externalMessageDetails']//h1[text()='Erregernachweismeldevorgang']");
  public static final By ONE_TEST_IN_SAMPLES_DE =
      By.xpath("//div[@location='samples']//div[text()='Anzahl der Tests: 1']");
  public static final By MESSAGE_DELETE_BUTTON = By.cssSelector("#actionDelete");
  public static final By TOTAL_MESSAGE_COUNTER = By.cssSelector("#Alle");
  public static final By RESET_FILTER_BUTTON = By.cssSelector("#actionResetFilters");
  public static final By MESSAGE_DATE_FROM_INPUT = By.cssSelector("#messageDateFrom_date input");
  public static final By MESSAGE_TIME_FROM_COMBOBOX = By.cssSelector("#messageDateFrom_time div");
  public static final By APPLY_FILTER_MESSAGE = By.id("actionApplyFilters");
  public static final By PATIENT_BIRTHDAY_FROM_INPUT =
      By.xpath("//div[@location='birthDateFrom']//div[@id='birthDateFrom']//input");
  public static final By PATIENT_BIRTHDAY_TO_INPUT =
      By.xpath("//div[@location='birthDateTo']//div[@id='birthDateTo']//input");
  public static final By MARK_AS_UNCLEAR_BUTTON = By.cssSelector("#actionUnclearLabMessage");
  public static final By UNCLEAR_MESSAGE_COUNTER = By.cssSelector("#Unklar");
  public static final By MARK_AS_FORWARDED_BUTTON =
      By.cssSelector("#actionManualForwardLabMessage");
  public static final By FORWARDED_MESSAGE_COUNTER = By.cssSelector("#Weitergeleitet");
  public static final By NEW_SAMPLE_DATE_OF_REPORT_INPUT = By.cssSelector("#reportDate input");
  public static final By NEW_SAMPLE_TEST_RESULT_INPUT = By.cssSelector("#testResult input");
  public static final By NEW_SAMPLE_SPECIMEN_CONDITION_INPUT =
      By.cssSelector("#specimenCondition input");
  public static final By NEW_SAMPLE_TEST_RESULT_VERIFIED_RADIOBUTTON =
      By.cssSelector("#testResultVerified");
  public static final By NEW_SAMPLE_TEST_RESULT_VERIFIED_SELECTED_VALUE =
      By.cssSelector("#testResultVerified input[checked] + label");
  public static final By NEW_SAMPLE_TESTED_DISEASE_INPUT = By.cssSelector("#testedDisease input");
  public static final By NEW_CASE_EMAIL_ADDRESS_INPUT = By.cssSelector("#emailAddress");
  public static final By NEW_CASE_PHONE_NUMBER_INPUT = By.cssSelector("#phone");
  public static final By UNPROCESSED_QUICK_FILTER_BUTTON = By.cssSelector("#Unverarbeitet");
  public static final By PROCESSED_QUICK_FILTER_BUTTON = By.cssSelector("#Verarbeitet");
  public static final By UNCLEAR_QUICK_FILTER_BUTTON = By.cssSelector("#Unklar");
  public static final By FORWARDED_QUICK_FILTER_BUTTON = By.cssSelector("#Weitergeleitet");
  public static final By ALL_QUICK_FILTER_COUNTER = By.cssSelector("#Alle .badge");
  public static final By UNPROCESSED_QUICK_FILTER_COUNTER = By.cssSelector("#Unverarbeitet .badge");
  public static final By PROCESSED_QUICK_FILTER_COUNTER = By.cssSelector("#Verarbeitet .badge");
  public static final By UNCLEAR_QUICK_FILTER_COUNTER = By.cssSelector("#Unklar .badge");
  public static final By FORWARDED_QUICK_FILTER_COUNTER = By.cssSelector("#Weitergeleitet .badge");
  public static final By CREATE_NEW_PERSON_RADIOBUTTON_DE =
      By.xpath("//*[text()='Neuen Kontakt erstellen']");
  public static final By CREATE_NEW_EVENT_PARTICIPANT_RADIOBUTTON_DE =
      By.xpath("//*[text()='Neuen Ereignisteilnehmer erstellen']");
  public static final By LABORATORY_INPUT = By.cssSelector("#lab input");
  public static final By LABORATORY_DETAILS_INPUT = By.cssSelector("#labDetails");
  public static final By FIRST_PATHOGEN_LABORATORY_INPUT =
      By.xpath("(//div[contains(@id, 'lab')]//input)[2]");
  public static final By FIRST_RECORD_DISEASE_VARIANT = By.xpath("//table/tbody/tr[1]/td[8]");
  public static final By NEW_CASE_FORM_DISEASE_VARIANT_INPUT =
      By.cssSelector(".popupContent #diseaseVariant input");
  public static final By NEW_SAMPLE_FORM_FIRST_PATHOGEN_DISEASE_VARIANT_INPUT =
      By.xpath("(//div[contains(@id, 'testedDiseaseVariant')]//input)[1]");
  public static final By NEW_SAMPLE_FORM_SECOND_PATHOGEN_DISEASE_VARIANT_INPUT =
      By.xpath("(//div[contains(@id, 'testedDiseaseVariant')]//input)[2]");
  public static final By NEW_SAMPLE_FORM_LABORATORY_NAME =
      By.xpath("(//input[contains(@id, 'labDetails')])[1]");
  public static final By NEW_SAMPLE_FORM_FIRST_PATHOGEN_LABORATORY_NAME =
      By.xpath("(//input[contains(@id, 'labDetails')])[2]");
  public static final By NEW_SAMPLE_FORM_SECOND_PATHOGEN_LABORATORY_NAME =
      By.xpath("(//input[contains(@id, 'labDetails')])[3]");
  public static final By MULTIPLE_SAMPLES_HEADER =
      By.xpath("//div[@class= 'v-window-header'][text()='Mehrere Proben']");
  public static final By CREATE_NEW_SAMPLE_CHECKBOX =
      By.xpath(
          "//span[@class= 'v-radiobutton v-select-option']//label[text()='Neue Probe erstellen']");
  public static final By NEW_SAMPLE_FORM_FIRST_PATHOGEN_TEST_TYPE_INPUT =
      By.xpath("(//div[contains(@id, 'testType')]//input)[1]");
  public static final By NEW_SAMPLE_FORM_SECOND_PATHOGEN_TEST_TYPE_INPUT =
      By.xpath("(//div[contains(@id, 'testType')]//input)[2]");
  public static final By POPUP_WINDOW_SAVE_AND_OPEN_PHYSICIAN_REPORT_BUTTON =
      By.id("actionSaveAndOpenCase");
  public static final By POPUP_WINDOW_BACK_BUTTON = By.id("actionBack");
  public static final By NEXT_BUTTON = By.id("actionNext");
  public static final By CASE_SAVED_POPUP_DE = By.cssSelector(".warning .v-Notification-caption");
  public static final By TYPE_OF_MESSAGE_COMBOBOX =
      By.cssSelector("[id='type'] [class='v-filterselect-button']");
  public static final By GRID_RESULTS_TYPE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(3)");
  public static final By STATUS_GRID_RESULTS_TYPE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(13)");
  public static final By CURRENT_HOSPITALIZATION_HEADER =
      By.cssSelector("[location='hospitalizationHeadingLoc'] div");
  public static final By CLINICAL_MEASUREMENT_HEADER =
      By.cssSelector("[location='clinicalMeasurementsHeadingLoc'] div");
  public static final By EXPOSURE_INVESTIGATION_HEADER =
      By.cssSelector("[location='locExposureInvestigationHeading'] div");
  public static final By ADD_VACCINATION_BUTTON = By.id("physiciansReportCaseAddVaccination");
  public static final By DOWNLOAD_PROCESSED_BUTTON = By.xpath("//table/tbody/tr[1]/td[15]/div");
  public static final By DOWNLOAD_UNPROCESSED_BUTTON = By.xpath("//table/tbody/tr[1]/td[16]/div");
  public static final By GRID_MESSAGE_UUID_TITLE =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(2) a");
  public static final By ASSIGN_BUTTON = By.xpath("//table/tbody/tr[1]/td[14]/div");
  public static final By ASSIGNEE_LABEL = By.cssSelector(".component-wrap .v-label");
  public static final By EDIT_ASSIGNEE_FILTER_SELECT_BUTTON =
      By.cssSelector(".popupContent .v-filterselect-button");
  public static final By SEND_TO_ANOTHER_ORGANIZATION_BUTTON =
      By.id("sormasToSormasSendLabMessage");
  public static final By HEADER_OF_ENTRY_LINK = By.cssSelector(".HeaderOfEntry");
  public static final By BULK_ACTIONS_MESSAGES_VALUES = By.id("actionEnterBulkEditMode");
  public static final By BULK_ACTIONS_LEAVE_MESSAGES_VALUES = By.id("actionLeaveBulkEditMode");
  public static final By BULK_ACTIONS_MESSAGES_DIRECTORY = By.id("bulkActions-2");
  public static final By BULK_DELETE_MESSAGES_BUTTON = By.id("bulkActions-3");
}
