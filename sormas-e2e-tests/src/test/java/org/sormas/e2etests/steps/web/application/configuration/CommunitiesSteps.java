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

package org.sormas.e2etests.steps.web.application.configuration;

import static org.sormas.e2etests.pages.application.cases.CaseDirectoryPage.NAME_UUID_EPID_NUMBER_LIKE_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.CASE_ORIGIN_OPTIONS;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DATE_OF_REPORT_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.DISEASE_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.FIRST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.LAST_NAME_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_DESCRIPTION_INPUT;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.PLACE_OF_STAY;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.RESPONSIBLE_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.CreateNewCasePage.SAVE_BUTTON;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CASE_SAVED_POPUP;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_COMBOBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.CREATE_NEW_PERSON_CHECKBOX;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISEASE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.DISTRICT_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.EXTERNAL_ID_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PICK_OR_CREATE_PERSON_POPUP_HEADER;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.PLACE_OF_STAY_SELECTED_VALUE;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REGION_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.REPORT_DATE_INPUT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.SAVE_POPUP_CONTENT;
import static org.sormas.e2etests.pages.application.cases.EditCasePage.USER_INFORMATION;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.ARCHIVE_COMMUNITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.COMMUNITIES_NEW_ENTRY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.COMMUNITY_FILTER_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_ARCHIVING_COMMUNITY_TEXT;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_ARCHIVING_YES_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CONFIRM_DEARCHIVING_COMMUNITY_TEXT;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CREATE_NEW_ENTRY_COMMUNITIES_DISTRICT_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CREATE_NEW_ENTRY_COMMUNITIES_NAME_INPUT;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.CREATE_NEW_ENTRY_COMMUNITIES_REGION_COMBOBOX;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.DEARCHIVE_COMMUNITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.EDIT_COMMUNITY_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.RESET_FILTERS_COMMUNITIES_BUTTON;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.SAVE_NEW_ENTRY_COMMUNITIES;
import static org.sormas.e2etests.pages.application.configuration.CommunitiesTabPage.SEARCH_COMMUNITY_INPUT;
import static org.sormas.e2etests.pages.application.configuration.ConfigurationTabsPage.CONFIGURATION_COMMUNITIES_TAB;
import static org.sormas.e2etests.pages.application.contacts.CreateNewContactPage.SEX_COMBOBOX;
import static org.sormas.e2etests.pages.application.contacts.EditContactPage.UUID_INPUT;

import com.google.inject.Inject;
import cucumber.api.java8.En;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.sormas.e2etests.entities.pojo.helpers.ComparisonHelper;
import org.sormas.e2etests.entities.pojo.web.Case;
import org.sormas.e2etests.entities.pojo.web.Communities;
import org.sormas.e2etests.entities.services.CaseService;
import org.sormas.e2etests.entities.services.CommunitiesService;
import org.sormas.e2etests.helpers.WebDriverHelpers;
import org.sormas.e2etests.pages.application.cases.CreateNewCasePage;
import org.sormas.e2etests.pages.application.cases.EditCasePage;
import org.testng.asserts.SoftAssert;

public class CommunitiesSteps implements En {

  private final WebDriverHelpers webDriverHelpers;
  protected Communities communities;
  protected static Case caze;
  protected static Case collectedCase;
  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

  @Inject
  public CommunitiesSteps(
      WebDriverHelpers webDriverHelpers,
      CommunitiesService communitiesService,
      CaseService caseService,
      SoftAssert softly) {
    this.webDriverHelpers = webDriverHelpers;

    When(
        "I click on Communities button in Configuration tab",
        () -> webDriverHelpers.clickOnWebElementBySelector(CONFIGURATION_COMMUNITIES_TAB));

    When(
        "I click on New Entry button in Communities tab in Configuration",
        () -> webDriverHelpers.clickOnWebElementBySelector(COMMUNITIES_NEW_ENTRY_BUTTON));

    When(
        "I fill new community with specific data",
        () -> {
          communities = communitiesService.buildSpecificCommunity();
          fillCommunityName(communities.getCommunityName());
          selectCommunityRegion(communities.getRegion());
          selectCommunityDistrict(communities.getDistrict());
          webDriverHelpers.clickOnWebElementBySelector(SAVE_NEW_ENTRY_COMMUNITIES);
        });

    When(
        "I create new case with created community",
        () -> {
          caze = caseService.buildGeneratedCase();
          fillSpecificCaseFields(caze, communities);
          webDriverHelpers.clickOnWebElementBySelector(SAVE_BUTTON);
          if (webDriverHelpers.isElementVisibleWithTimeout(PICK_OR_CREATE_PERSON_POPUP_HEADER, 5)) {
            webDriverHelpers.clickOnWebElementBySelector(CREATE_NEW_PERSON_CHECKBOX);
            webDriverHelpers.clickOnWebElementBySelector(SAVE_POPUP_CONTENT);
            TimeUnit.SECONDS.sleep(1);
          }
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(20);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(EditCasePage.REPORT_DATE_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(CASE_SAVED_POPUP);
        });

    When(
        "I check if created case with specific community is created correctly",
        () -> {
          collectedCase = collectCasePersonData();
          ComparisonHelper.compareEqualFieldsOfEntities(
              caze,
              collectedCase,
              List.of(
                  "dateOfReport",
                  "disease",
                  "placeOfStay",
                  "placeDescription",
                  "firstName",
                  "lastName",
                  "dateOfBirth"));
          softly.assertEquals(
              collectedCase.getResponsibleRegion(),
              communities.getRegion(),
              "Districts are not equal");
          softly.assertEquals(
              collectedCase.getResponsibleDistrict(),
              communities.getDistrict(),
              "Regions are not equal");
          softly.assertEquals(
              collectedCase.getResponsibleCommunity(),
              communities.getCommunityName(),
              "Communities are not equal");
          softly.assertAll();
        });

    When(
        "I filter by last created community",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              SEARCH_COMMUNITY_INPUT, communities.getCommunityName());
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(RESET_FILTERS_COMMUNITIES_BUTTON);
        });

    When(
        "I click on edit button for filtered community",
        () -> webDriverHelpers.clickOnWebElementBySelector(EDIT_COMMUNITY_BUTTON));

    When(
        "I archive chosen community",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CREATE_NEW_ENTRY_COMMUNITIES_NAME_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(ARCHIVE_COMMUNITY_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(CONFIRM_ARCHIVING_COMMUNITY_TEXT);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ARCHIVING_YES_BUTTON);
        });

    When(
        "I filter last created Case by external ID",
        () -> {
          webDriverHelpers.fillAndSubmitInWebElement(
              NAME_UUID_EPID_NUMBER_LIKE_INPUT, caze.getExternalId());
          TimeUnit.SECONDS.sleep(2); // wait for filter
          webDriverHelpers.waitForPageLoadingSpinnerToDisappear(40);
        });

    When(
        "I check if community chosen in case is changed to inactive",
        () -> {
          softly.assertEquals(
              webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT),
              communities.getCommunityName() + " (Inactive)",
              "Community is not inactive");
          softly.assertAll();
        });

    When(
        "I clear Community from Responsible Community in Case Edit Tab",
        () -> webDriverHelpers.selectFromCombobox(COMMUNITY_COMBOBOX, " "));

    When(
        "I check if archived community is unavailable in Case Edit Tab",
        () -> {
          softly.assertFalse(
              webDriverHelpers.checkIfElementExistsInCombobox(
                  COMMUNITY_COMBOBOX, communities.getCommunityName()));
          softly.assertAll();
        });

    When(
        "I filter Communities by ([^\"]*)",
        (String option) -> {
          webDriverHelpers.selectFromCombobox(COMMUNITY_FILTER_COMBOBOX, option);
          TimeUnit.SECONDS.sleep(2); // wait for filter
        });

    When(
        "I de-archive chosen community",
        () -> {
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CREATE_NEW_ENTRY_COMMUNITIES_NAME_INPUT);
          webDriverHelpers.clickOnWebElementBySelector(DEARCHIVE_COMMUNITY_BUTTON);
          webDriverHelpers.waitUntilElementIsVisibleAndClickable(
              CONFIRM_DEARCHIVING_COMMUNITY_TEXT);
          webDriverHelpers.clickOnWebElementBySelector(CONFIRM_ARCHIVING_YES_BUTTON);
        });

    When(
        "I set last created community in Responsible Community in Case Edit tab",
        () ->
            webDriverHelpers.selectFromCombobox(
                COMMUNITY_COMBOBOX, communities.getCommunityName()));
  }

  private void fillSpecificCaseFields(Case caze, Communities communities) {
    selectCaseOrigin(caze.getCaseOrigin());
    fillDisease(caze.getDisease());
    fillExternalId(caze.getExternalId());
    selectResponsibleRegion(communities.getRegion());
    selectResponsibleDistrict(communities.getDistrict());
    selectResponsibleCommunity(communities.getCommunityName());
    selectPlaceOfStay(caze.getPlaceOfStay());
    fillFirstName(caze.getFirstName());
    fillLastName(caze.getLastName());
    fillDateOfBirth(caze.getDateOfBirth(), Locale.ENGLISH);
    selectSex(caze.getSex());
    fillDateOfReport(caze.getDateOfReport(), Locale.ENGLISH);
    fillPlaceDescription(caze.getPlaceDescription());
  }

  private Case collectCasePersonData() {
    Case userInfo = getUserInformation();

    return Case.builder()
        .dateOfReport(getDateOfReport())
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .dateOfBirth(userInfo.getDateOfBirth())
        .externalId(webDriverHelpers.getValueFromWebElement(EXTERNAL_ID_INPUT))
        .uuid(webDriverHelpers.getValueFromWebElement(UUID_INPUT))
        .disease(webDriverHelpers.getValueFromWebElement(DISEASE_INPUT))
        .responsibleRegion(webDriverHelpers.getValueFromWebElement(REGION_INPUT))
        .responsibleDistrict(webDriverHelpers.getValueFromWebElement(DISTRICT_INPUT))
        .responsibleCommunity(webDriverHelpers.getValueFromWebElement(COMMUNITY_INPUT))
        .placeOfStay(webDriverHelpers.getTextFromWebElement(PLACE_OF_STAY_SELECTED_VALUE))
        .placeDescription(
            webDriverHelpers.getValueFromWebElement(EditCasePage.PLACE_DESCRIPTION_INPUT))
        .build();
  }

  private Case getUserInformation() {
    String userInfo = webDriverHelpers.getTextFromWebElement(USER_INFORMATION);
    String[] userInfos = userInfo.split(" ");
    LocalDate localDate = LocalDate.parse(userInfos[3].replace(")", ""), DATE_FORMATTER);
    return Case.builder()
        .firstName(userInfos[0])
        .lastName(userInfos[1])
        .dateOfBirth(localDate)
        .build();
  }

  public void fillCommunityName(String communityName) {
    webDriverHelpers.fillInWebElement(CREATE_NEW_ENTRY_COMMUNITIES_NAME_INPUT, communityName);
  }

  private void selectCommunityRegion(String region) {
    webDriverHelpers.selectFromCombobox(CREATE_NEW_ENTRY_COMMUNITIES_REGION_COMBOBOX, region);
  }

  private void selectCommunityDistrict(String district) {
    webDriverHelpers.selectFromCombobox(CREATE_NEW_ENTRY_COMMUNITIES_DISTRICT_COMBOBOX, district);
  }

  private LocalDate getDateOfReport() {
    String dateOfReport = webDriverHelpers.getValueFromWebElement(REPORT_DATE_INPUT);
    return LocalDate.parse(dateOfReport, DATE_FORMATTER);
  }

  private void selectCaseOrigin(String caseOrigin) {
    webDriverHelpers.clickWebElementByText(CASE_ORIGIN_OPTIONS, caseOrigin);
  }

  private void fillDateOfReport(LocalDate date, Locale locale) {
    DateTimeFormatter formatter;
    if (locale.equals(Locale.GERMAN)) formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    else formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    webDriverHelpers.fillInWebElement(DATE_OF_REPORT_INPUT, formatter.format(date));
  }

  private void fillDateOfBirth(LocalDate localDate, Locale locale) {
    webDriverHelpers.selectFromCombobox(
        CreateNewCasePage.DATE_OF_BIRTH_YEAR_COMBOBOX, String.valueOf(localDate.getYear()));
    webDriverHelpers.selectFromCombobox(
        CreateNewCasePage.DATE_OF_BIRTH_MONTH_COMBOBOX,
        localDate.getMonth().getDisplayName(TextStyle.FULL, locale));
    webDriverHelpers.selectFromCombobox(
        CreateNewCasePage.DATE_OF_BIRTH_DAY_COMBOBOX, String.valueOf(localDate.getDayOfMonth()));
  }

  private void fillDisease(String disease) {
    webDriverHelpers.selectFromCombobox(DISEASE_COMBOBOX, disease);
  }

  private void selectSex(String sex) {
    webDriverHelpers.selectFromCombobox(SEX_COMBOBOX, sex);
  }

  private void selectResponsibleRegion(String selectResponsibleRegion) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_REGION_COMBOBOX, selectResponsibleRegion);
  }

  private void selectResponsibleDistrict(String responsibleDistrict) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_DISTRICT_COMBOBOX, responsibleDistrict);
  }

  private void selectResponsibleCommunity(String responsibleCommunity) {
    webDriverHelpers.selectFromCombobox(RESPONSIBLE_COMMUNITY_COMBOBOX, responsibleCommunity);
  }

  private void selectPlaceOfStay(String placeOfStay) {
    webDriverHelpers.clickWebElementByText(PLACE_OF_STAY, placeOfStay);
  }

  private void fillPlaceDescription(String placeDescription) {
    webDriverHelpers.fillInWebElement(PLACE_DESCRIPTION_INPUT, placeDescription);
  }

  private void fillFirstName(String firstName) {
    webDriverHelpers.fillInWebElement(FIRST_NAME_INPUT, firstName);
  }

  private void fillLastName(String lastName) {
    webDriverHelpers.fillInWebElement(LAST_NAME_INPUT, lastName);
  }

  private void fillExternalId(String externalId) {
    webDriverHelpers.fillInWebElement(EXTERNAL_ID_INPUT, externalId);
  }
}
