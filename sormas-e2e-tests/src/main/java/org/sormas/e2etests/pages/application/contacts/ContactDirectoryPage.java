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

package org.sormas.e2etests.pages.application.contacts;

import org.openqa.selenium.By;

public class ContactDirectoryPage {
  public static final By NEW_CONTACT_PAGE_BUTTON = By.id("contactNewContact");
  public static final By LINE_LISTING = By.id("lineListing");
  public static final By MULTIPLE_OPTIONS_SEARCH_INPUT = By.cssSelector("#contactOrCaseLike");
  public static final By PERSON_LIKE_SEARCH_INPUT = By.cssSelector("#personLike");
  public static final By APPLY_FILTERS_BUTTON = By.id("actionApplyFilters");
  public static final String CONTACT_RESULTS_UUID_LOCATOR = "[title = '%s']";
  public static final By CONTACT_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static final By CONTACT_DIRECTORY_DETAILED_RADIOBUTTON =
      By.cssSelector("div#contactsViewSwitcher span:nth-child(2) > label");
  public static final By CONTACT_DIRECTORY_DETAILED_PAGE_FILTER_INPUT =
      By.cssSelector("input#contactOrCaseLike");
  public static final By ACTION_MERGE_CONTACT_DIRECTORY = By.cssSelector("#actionMerge");
  public static final By CONTACTS_DETAILED_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CONTACTS_COLUMN_HEADERS =
      By.cssSelector("thead .v-grid-column-default-header-content");
  public static final By CONTACTS_DETAILED_FIRST_TABLE_ROW =
      By.cssSelector("div.v-grid-tablewrapper tbody tr");
  public static final By CONTACTS_DETAILED_TABLE_DATA = By.cssSelector("[role=gridcell]");
  public static final By DISEASE_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(3)");
  public static final By FIRST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(7)");
  public static final By LAST_NAME_COLUMNS = By.cssSelector("[role=rowgroup] tr>td:nth-child(8)");
  public static final By TYPE_OF_CONTACT_COLUMNS =
      By.cssSelector("[role=rowgroup] tr>td:nth-child(9)");
  public static final By FOLLOW_UP_VISITS_BUTTON =
      By.cssSelector("#contactsViewSwitcher span:nth-child(3)");
  public static final By FROM_INPUT = By.cssSelector("#fromReferenceDateField input");
  public static final By GRID_HEADERS = By.xpath("//thead//tr//th");
  public static final String RESULTS_GRID_HEADER = "//div[contains(text(), '%s')]";
  public static final By FIRST_CONTACT_ID_BUTTON = By.cssSelector(".v-grid-row-has-data a[title]");
  public static final By EPIDEMIOLOGICAL_DATA_TAB = By.cssSelector("#tab-contacts-epidata");
  public static final By CONTACT_DISEASE_FILTER_COMBOBOX =
      By.cssSelector("[id='disease'] [class='v-filterselect-button']");
  public static final By CONTACT_DISEASE_VARIANT_FILTER_COMBOBOX =
      By.cssSelector("[id='diseaseVariant'] [class='v-filterselect-button']");
  public static final By CONTACT_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='contactClassification'] [class='v-filterselect-button']");
  public static final By CONTACT_CASE_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='caseClassification'] [class='v-filterselect-button']");
  public static final By CONTACT_CATEGORY_CLASSIFICATION_FILTER_COMBOBOX =
      By.cssSelector("[id='contactCategory'] [class='v-filterselect-button']");
  public static final By CONTACT_FOLLOW_UP_FILTER_COMBOBOX =
      By.cssSelector("[id='followUpStatus'] [class='v-filterselect-button']");
  public static final By CONTACT_DIRECTORY_DETAILED_PAGE_APPLY_FILTER_BUTTON =
      By.cssSelector("div#actionApplyFilters");
  public static final By CONTACT_DIRECTORY_DETAILED_PAGE_CONFIRM_FILTER_BUTTON =
      By.cssSelector("div#actionConfirmFilters");
  public static final By GRID_RESULTS_COUNTER_CONTACT_DIRECTORY = By.cssSelector(".badge");
  public static final By CONTACTS_WITH_QUARANTINE_ORDERED_VERBALLY_CHECKBOX =
      By.id("quarantineOrderedVerbally");
  public static final By CONTACTS_WITH_QUARANTINE_ORDERED_BY_OFFICIAL_DOCUMENT_CHECKBOX =
      By.id("quarantineOrderedOfficialDocument");
  public static final By CONTACTS_WITH_NO_QUARANTINE_ORDERED_CHECKBOX =
      By.id("quarantineNotOrdered");
  public static final By CONTACTS_WITH_HELP_NEEDED_IN_QUARANTINE_ORDERED_CHECKBOX =
      By.id("onlyQuarantineHelpNeeded");
  public static final By CONTACTS_ONLY_HIGH_PRIOROTY_CHECKBOX = By.id("onlyHighPriorityContacts");
  public static final By CONTACTS_WITH_EXTENDED_QUARANTINE_CHECKBOX =
      By.id("withExtendedQuarantine");
  public static final By CONTACTS_WITH_REDUCED_QUARANTINE_CHECKBOX = By.id("withReducedQuarantine");
  public static final By CONTACTS_FROM_OTHER_INSTANCES_CHECKBOX =
      By.id("onlyContactsFromOtherInstances");
  public static final By ALL_BUTTON_CONTACT = By.id("All");
  public static final By ALL_BUTTON_CONTACT_DE = By.id("Alle");
  public static final By ACTIVE_CONTACT_BUTTON = By.id("status-Active contact");
  public static final By CONVERTED_TO_CASE_BUTTON = By.id("status-Converted to case");
  public static final By DROPPED_BUTTON = By.id("status-Dropped");
  public static final By BULK_ACTIONS_CONTACT_VALUES = By.id("bulkActions-11");
  public static final By CONTACT_DATA_TAB = By.cssSelector("#tab-contacts-data");
  public static final By NEW_ENTRY_EPIDEMIOLOGICAL_DATA = By.id("actionNewEntry");
  public static final By FIRST_PERSON_ID = By.xpath("//td[6]//a");
  public static final By FIRST_CONTACT_ID = By.xpath("//td[1]//a");
  public static final By BULK_CREATE_QUARANTINE_ORDER = By.id("bulkActions-10");
  public static final By BULK_DELETE_BUTTON_CONTACT_PAGE = By.id("bulkActions-6");
  public static final By CONTACT_MERGE_DUPLICATES = By.id("contactMergeDuplicates");
  public static final By CONTACT_DISPLAY_FILTER_COMBOBOX =
      By.cssSelector("[id='relevanceStatus'] [class='v-filterselect-button']");
  public static final By RELATIONSHIP_WITH_CASE_COMBOBOX = By.cssSelector("#relationToCase div");
  public static final By CONTACT_VACCINATION_STATUS_FILTER_COMBOBOX =
      By.cssSelector("[id='vaccinationStatus'] [class='v-filterselect-button']");
  public static final By FIRST_CONTACT_ID_FROM_CASE = By.xpath("(//td//a)[1]");
  public static final By CREATE_CASE_FROM_POSITIVE_TEST_RESULT_HEADER_DE =
      By.xpath(
          "//*[contains(text(),'Soll ein Fall aus dem Kontakt mit positivem Testresultat erstellt werden?')]");

  public static By getCheckboxByUUID(String uuid) {
    return By.xpath(
        String.format(
            "//td//a[text()=\"%s\"]/../preceding-sibling::td//input[@type=\"checkbox\"]",
            uuid.substring(0, 6).toUpperCase()));
  }

  public static By getVaccinationStatusContactsByText(String status) {
    return By.xpath(String.format("//td[contains(text(), '%s')]", status));
  }

  public static By getContactsByUUID(String uuid) {
    return By.xpath(String.format("//a[@title='%s']", uuid));
  }
}
