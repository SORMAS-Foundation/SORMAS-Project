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

package org.sormas.e2etests.pages.application.events;

import org.openqa.selenium.By;

public class EditEventPage {
  public static final By EVENT_PARTICIPANTS_TAB =
      By.cssSelector("#tab-events-eventparticipants span");
  public static final By UUID_EDIT_EVENT = By.cssSelector("div[location='uuid']");
  public static final By FIRST_EVENT_PARTICIPANT = By.xpath("//table/tbody/tr[1]/td[1]//a");
  public static final By FIRST_EVENT_PARTICIPANT_AFTER_IMPORT =
      By.xpath("//table/tbody/tr[1]/td[2]//a");
  public static final By FIRST_RESULT_IN_EVENT_PARTICIPANT_TABLE = By.xpath("//td//a");
  public static final By FIRST_ARCHIVED_EVENT_PARTICIPANT = By.xpath("//table/tbody/tr[1]/td[1]");
  public static final By EVENT_ACTIONS_TAB = By.cssSelector("#tab-events-eventactions span");
  public static final By UUID_INPUT = By.id("uuid");
  public static final By TITLE_INPUT = By.cssSelector("#eventTitle");
  public static final By SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static final By SAVE_BUTTON_FOR_POPUP_WINDOWS = By.cssSelector(".popupContent #commit");
  public static final By EDIT_EVENT_PAGE_SAVE_BUTTON = By.cssSelector("#commit");
  public static final By EVENT_STATUS_OPTIONS =
      By.cssSelector("#eventStatus .v-select-option label");
  public static final By RISK_LEVEL_INPUT = By.cssSelector(" #riskLevel input");
  public static final By START_DATA_INPUT = By.cssSelector(" #startDate input");
  public static final By DISEASE_INPUT = By.cssSelector("#disease input");
  public static final By SOURCE_TYPE_INPUT = By.cssSelector(" #srcType input");
  public static final By TYPE_OF_PLACE_INPUT = By.cssSelector("#typeOfPlace input");
  public static final By REPORT_DATE_INPUT = By.cssSelector("#reportDateTime input");
  public static final By EVENT_DATA_SAVED_MESSAGE = By.cssSelector(".v-Notification-caption");
  public static final By RISK_LEVEL_COMBOBOX = By.cssSelector("#riskLevel div");
  public static final By EVENT_MANAGEMENT_STATUS_OPTIONS =
      By.cssSelector("#eventManagementStatus .v-select-option label");
  public static final By EVENT_INVESTIGATION_STATUS_OPTIONS =
      By.cssSelector("#eventInvestigationStatus label");
  public static final By DISEASE_COMBOBOX = By.cssSelector("#disease div");
  public static final By FACILITY_TYPE_COMBOBOX = By.cssSelector("#facilityType div");
  public static final By FACILITY_CATEGORY_COMBOBOX = By.cssSelector("#typeGroup div");
  public static final By PLACE_OF_STAY_COMBOBOX = By.cssSelector("#typeOfPlace div");
  public static final By SOURCE_TYPE_COMBOBOX = By.cssSelector("#srcType div");
  public static final By TYPE_OF_PLACE_COMBOBOX = By.cssSelector(" #typeOfPlace div");
  public static final By NEW_ACTION_BUTTON = By.id("actionNewAction");
  public static final By EVENTS_ACTIONS_TAB = By.cssSelector("#tab-events-eventactions");
  public static final By EDIT_EVENT_ACTION = By.cssSelector("div[id^='edit-action']");
  public static final By NEW_TASK_BUTTON = By.cssSelector("[id='New task']");
  public static final By EDIT_FIRST_TASK = By.cssSelector("#edit0");
  public static final By DELETE_BUTTON_DISABLED =
      By.xpath("//div[@id='delete' and @aria-disabled='true']");
  public static final By UNDO_DELETION_BUTTON = By.id("deleteRestore");
  public static final By DELETE_EVENT_ACTION_BUTTON = By.cssSelector("#deleteRestore");
  public static final By CONFIRM_EVENT_DELETE_ACTION_BUTTON = By.cssSelector("#actionConfirm");
  public static final By NO_EVENT_ACTION_CAPTION =
      By.xpath("//div[contains(text(), 'There are no actions for this Event')]");
  public static final By LINK_EVENT_GROUP_BUTTON = By.cssSelector("div#Link\\ event\\ group");
  public static final By NEW_EVENT_GROUP_RADIOBUTTON =
      By.xpath("//*[contains(text(),'New event group')]/..");
  public static final By SELECT_EVENT_GROUP_RADIOBUTTON =
      By.xpath("//*[contains(text(),'Select event group')]/..");
  public static final By GROUP_EVENT_NAME_POPUP_INPUT = By.cssSelector(".popupContent #name");
  public static final By GROUP_EVENT_UUID =
      By.xpath("//*[contains(text(),'Group id')]/../following-sibling::input[1]");
  public static final By NEW_GROUP_EVENT_CREATED_MESSAGE =
      By.xpath(
          "//*[contains(text(),'The selected event was successfully linked to this event group')]");
  public static final By CREATE_DOCUMENT_BUTTON = By.cssSelector("[id='Create']");
  public static final By EVENT_HANDOUT_COMBOBOX =
      By.cssSelector(".popupContent div[role='combobox'] div");
  public static final By GPS_LATITUDE_INPUT_EDIT_EVENT = By.id("latitude");
  public static final By GPS_LONGITUDE_INPUT_EDIT_EVENT = By.id("longitude");
  public static final By MAP_CONTAINER = By.cssSelector("[class*='leaflet-container']");
  public static final By EVENT_SAVED_POPUP = By.cssSelector(".v-Notification-caption");
  public static final By CREATE_EVENT_HANDOUT_BUTTON =
      By.cssSelector(".popupContent [id='Create']");
  public static final By CANCEL_EVENT_HANDOUT_BUTTON =
      By.cssSelector(".popupContent [id='Cancel']");
  public static final By UNLINK_EVENT_BUTTON = By.id("unlink-event-1");
  public static final By EDIT_EVENT_GROUP_BUTTON = By.id("add-event-0");
  public static final By NAVIGATE_TO_EVENT_DIRECTORY_EVENT_GROUP_BUTTON = By.id("list-events-0");
  public static final By NAVIGATE_TO_EVENT_DIRECTORY_LIST_GROUP_BUTTON = By.id("tab-events");
  public static final By NAVIGATE_TO_EVENT_GROUP_BUTTON = By.id("tab-events-data");
  public static final By NAVIGATE_TO_EVENT_PARTICIPANTS_BUTTON =
      By.id("tab-events-eventparticipants");
  public static final By NAVIGATE_TO_EVENT_DATA_BUTTON = By.id("tab-events-data");
  public static final By EVENT_PARTICIPANT_HEADER =
      By.xpath(
          "//div[@class='v-label v-widget h2 v-label-h2 primary v-label-primary vspace-none v-label-vspace-none vspace-top-none v-label-vspace-top-none caption-truncated v-label-caption-truncated v-label-undef-w']");
  public static final By SAVE_BUTTON_FOR_EDIT_EVENT_GROUP = By.id("commit");
  public static final By FIRST_GROUP_ID = By.xpath("//table/tbody/tr[1]/td[2]");
  public static final By TOTAL_ACTIONS_COUNTER = By.cssSelector(".badge");
  public static final By CREATE_CONTACTS_BULK_EDIT_BUTTON = By.id("bulkActions-3");
  public static final By DELETE_BULK_EDIT_BUTTON_EVENT_PARTICIPANT = By.id("bulkActions-4");
  public static final By CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT = By.id("bulkActions-5");
  public static final By EVENT_MANAGEMENT_STATUS_CHECK =
      By.cssSelector("#eventManagementStatus input:checked[type='checkbox'] ~ label");
  public static final By EVENT_CLUSTER_EDIT = By.xpath("//span[.='Cluster']");
  public static final By PRIMARY_MODE_OF_TRANSMISSION_COMBOBOX =
      By.cssSelector("[id='diseaseTransmissionMode'] [class='v-filterselect-button']");
  public static final By EPIDEMIOLOGICAL_EVIDENCE_OPTIONS =
      By.cssSelector("#epidemiologicalEvidence .v-select-option");
  public static final By LABORATORY_DIAGNOSTIC_EVIDENCE_OPTIONS =
      By.cssSelector("#laboratoryDiagnosticEvidence .v-select-option");
  public static final By STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Studie']");
  public static final By CASE_CONTROL_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Fall-Kontroll-Studie']");
  public static final By COHORT_STUDY_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Kohorten-Studie']");
  public static final By EXPLORATIVE_SURVEY_OF_AFFECTED_PEOPLE_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Explorative Befragung der Betroffenen']");
  public static final By CONTACT_TO_SICK_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Kontakt zu Erkrankten']");
  public static final By CONTACT_TO_CONTAMINATED_MATERIALS_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Kontakt zu kontaminierten Gegenst\u00E4nden']");
  public static final By
      DESCRIPTIVE_ANALYSIS_OF_ASCETAINED_DATA_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
          By.xpath("//label[text()='Deskriptive Auswertung der ermittelten Daten']");
  public static final By TEMPORAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath(
          "//label[text()='Zeitlich: zeitliches Auftreten der Erkrankung deutet auf gemeinsame Infektionsquelle hin']");
  public static final By SPATIAL_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath(
          "//label[text()='R\u00E4umlich: Mehrzahl der F\u00E4lle war im angenommenen Infektionszeitraum am selben Ort']");
  public static final By PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath(
          "//label[text()='Person: F\u00E4lle haben direkten oder indirekten Kontakt miteinander gehabt']");
  public static final By SUSPICION_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Vermutung']");
  public static final By EXPRESSED_BY_THE_DISEASE_PERSON_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Von den erkrankten Personen ge\u00E4u\u00DFert']");
  public static final By EXPRESSED_BY_THE_HEALTH_DEPARTMENT_EPIDEMIOLOGICAL_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Des Gesundheitsamtes']");
  public static final By
      VERIFICATION_OF_AT_LEAST_TWO_INFECTED_OR_DISEASED_PERSONS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
          By.xpath(
              "//label[text()='Nachweis bei mindestens zwei infizierten bzw. erkrankten Personen']");
  public static final By COMPLIANT_PATHOGEN_FINE_TYPING_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Erreger-Feintypisierung stimmt \u00FCberein']");
  public static final By VERIFICATION_ON_MATERIALS_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Nachweis an Gegenst\u00E4nden']");
  public static final By IMPRESSION_TEST_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Abklatschprobe (z.B. Kan\u00FClen, Katheter)']");
  public static final By WATER_SAMPLE_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Wasserprobe']");
  public static final By OTHER_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
      By.xpath("//label[text()='Sonstiges']");
  public static final By COUNTRY_INFO_POPUP_TEXT = By.xpath("//div[@class='v-tooltip-text']");
  public static final By COUNTRY_INFO_ICON =
      By.xpath("//span[contains(@class, 'v-icon-info_circle')]");
  public static final By COUNTRY_COMBOBOX =
      By.cssSelector("[id='country'] [class='v-filterselect-button']");
  public static final By DEFAULT_COMBOBOX_VALUE = By.cssSelector(".v-filterselect-suggestmenu td");
  public static final By COUNTRY_COMBOBOX_INPUT = By.cssSelector("#country input");
  public static final By COUNTRY_COMBOBOX_DIABLED =
      By.xpath("//div[@id=\"country\" and contains(@class, 'v-disabled')]");
  public static final By REASON_FOR_DELETION_MARK =
      By.cssSelector(".popupContent span[class='v-errorindicator v-errorindicator-error']");
  public static By REASON_FOR_DELETION_INPUT = By.cssSelector("#deletionReason input");
  public static final By REASON_FOR_DELETION_MESSAGE =
      By.xpath("//div[@class='v-errormessage v-errormessage-error']");
  public static final By
      PATHOGEN_FINE_TYPING_COMPLIANT_WITH_THE_ONE_OF_CASES_LABORATORY_DIAGNOSTIC_EVIDENCE_BUTTON_DE =
          By.xpath(
              "//label[text()='Erreger-Feintypisierung stimmt mit der der F\u00E4lle \u00FCberein']");
  public static final By EVENT_DATE_OF_REPORT_EXCLAMATION_MARK =
      By.xpath("//span[@class='v-errorindicator v-errorindicator-error']");
  public static final By EVENT_DATE_OF_REPORT_EXCLAMATION_MARK_MESSAGE =
      By.xpath("//div[@class='v-errormessage v-errormessage-error']");
  public static final By EVENT_ERROR_POPUP_MESSAGE_WITH_INPUT_DATA_TITLE =
      By.cssSelector(".popupContent h1");
  public static final By EVENT_ERROR_POPUP_FIRST_MESSAGE =
      By.xpath(
          "//div[@class='popupContent']//li[text()='Date of report has to be after or on the same day as Start date']");
  public static final By EVENT_ERROR_POPUP_SECOND_MESSAGE =
      By.xpath(
          "//div[@class='popupContent']//li[text()='Start date has to be before or on the same day as Date of report']");
  public static final By VACCINATION_STATUS_COMBOBOX =
      By.xpath("//div[@id='vaccinationStatus']/div");
  public static final By NEW_IMMUNIZATION_BUTTON = By.cssSelector("[id='New immunization']");
  public static final By NEW_IMMUNIZATION_CARD_WITH_COVID =
      By.xpath("//div[@location='immunizations']//*[contains(text(),'COVID-19')]");
  public static final By EYE_ICON =
      By.cssSelector("[class*='v-caption-on-top']>[class*='v-popupview']");
  public static final By EVENT_PARTICIPANT_STATUS =
      By.xpath("//*[@id='relevanceStatusFilter']/div");

  public static By getGroupEventName(String groupEventName) {
    return By.xpath("//*[contains(text(),\"" + groupEventName + "\")]");
  }

  public static By getEventParticipantUUIDbyIndex(int index) {
    return By.xpath(
        String.format(
            "(//input[@type=\"checkbox\"])[%x]/../../following-sibling::td/a", index + 2));
  }

  public static final By FIRST_EVENT_PARTICIPANT_FROM_LIST = By.cssSelector("tr td a");
  public static final By CREATE_CASE_IN_EVENT_PARTICIPANT_LIST_BUTTON =
      By.xpath("(//tr//td//a)[3]");
  public static final By FIRST_PERSON_ID_IN_EVENT_PARTICIPANT_TAB = By.xpath("//td[2]//a");
  public static final By LINE_LISTING_HEADER = By.xpath("//div[contains(text(), 'Line listing')]");
  public static final By CREATE_QUARANTINE_ORDER_EVENT_PARTICIPANT_BY_TEXT =
      By.xpath("//span[contains(text(), 'Create quarantine order documents')]");
  public static final By EVENT_EXTERNAL_ID_INPUT = By.id("externalId");
}
