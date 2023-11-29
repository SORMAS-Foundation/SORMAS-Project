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

package org.sormas.e2etests.pages.application.users;

import org.openqa.selenium.By;

public class UserRolesPage {
  public static By USER_RIGHTS_INPUT = By.cssSelector("#userRights input");
  public static By USER_RIGHTS_COMBOBOX = By.cssSelector("#userRights div");
  public static By JURISDICTION_LEVEL_COMBOBOX = By.cssSelector("#jurisdictionLevel div");
  public static final By USER_ROLE_GRID_RESULTS_ROWS = By.cssSelector("[role=rowgroup] tr a");
  public static By NEW_USER_ROLE_BUTTON = By.cssSelector("div #userRoleNewUserRole");
  public static By USER_ROLE_TEMPLATE_COMBOBOX =
      By.cssSelector(".popupContent #templateUserRole div");
  public static By CAPTION_INPUT = By.cssSelector(".popupContent #caption");
  public static By POPUP_SAVE_BUTTON = By.cssSelector(".popupContent #commit");
  public static By POPUP_DISCARD_BUTTON = By.cssSelector(".popupContent #discard");
  public static By ARCHIVE_CASES_CHECKBOX = By.xpath("//label[text()='Archive cases']");
  public static By ARCHIVE_CONTACTS_CHECKBOX = By.xpath("//label[text()='Archive contacts']");
  public static By VIEW_EXISTING_USERS_CHECKBOX = By.xpath("//label[text()='View existing users']");
  public static By EDIT_EXISTING_USERS_CHECKBOX = By.xpath("//label[text()='Edit existing users']");
  public static By CREATE_NEW_USERS_CHECKBOX = By.xpath("//label[text()='Create new users']");
  public static By VIEW_EXISTING_USER_ROLES_CHECKBOX =
      By.xpath("//label[text()='View existing user roles']");
  public static By EDIT_EXISTING_USER_ROLES_CHECKBOX =
      By.xpath("//label[text()='Edit existing user roles']");
  public static By DELETE_USER_ROLES_FROM_THE_SYSTEM_CHECKBOX =
      By.xpath("//label[text()='Delete user roles from the system']");
  public static By SAVE_BUTTON = By.cssSelector("#commit");
  public static By DISCARD_BUTTON = By.cssSelector("#discard");
  public static By USER_ROLE_LIST = By.cssSelector("#tab-user-userroles");
  public static By EDIT_EXISTING_CASES_CHECKBOX = By.xpath("//label[text()='Edit existing cases']");
  public static By EDIT_EXISTING_CASES_CHECKBOX_VALUE =
      By.xpath("//label[text()='Edit existing cases']/preceding-sibling::input");
  public static By EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX =
      By.xpath("//label[text()='Edit case investigation status']");
  public static By EDIT_CASE_INVESTIGATION_STATUS_CHECKBOX_VALUE =
      By.xpath("//label[text()='Edit case investigation status']/preceding-sibling::input");
  public static By EDIT_CASE_DISEASE_CHECKBOX = By.xpath("//label[text()='Edit case disease']");
  public static By EDIT_CASE_DISEASE_CHECKBOX_VALUE =
      By.xpath("//label[text()='Edit case disease']/preceding-sibling::input");
  public static By TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX =
      By.xpath("//label[text()='Transfer cases to another region/district/facility']");
  public static By TRANSFER_CASES_TO_ANOTHER_REGION_DISTRICT_FACILITY_CHECKBOX_VALUE =
      By.xpath(
          "//label[text()='Transfer cases to another region/district/facility']/preceding-sibling::input");
  public static By EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX =
      By.xpath("//label[text()='Edit case classification and outcome']");
  public static By EDIT_CASE_CLASSIFICATION_AND_OUTCOME_CHECKBOX_VALUE =
      By.xpath("//label[text()='Edit case classification and outcome']/preceding-sibling::input");
  public static By EDIT_CASE_EPID_NUMBER_CHECKBOX =
      By.xpath("//label[text()='Edit case epid number']");
  public static By EDIT_CASE_EPID_NUMBER_CHECKBOX_VALUE =
      By.xpath("//label[text()='Edit case epid number']/preceding-sibling::input");
  public static By REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX =
      By.xpath("//label[text()='Refer case from point of entry']");
  public static By REFER_CASE_FROM_POINT_OF_ENTRY_CHECKBOX_VALUE =
      By.xpath("//label[text()='Refer case from point of entry']/preceding-sibling::input");
  public static By CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX =
      By.xpath("//label[text()='Can be responsible for a case']");
  public static By CAN_BE_RESPONSIBLE_FOR_A_CASE_CHECKBOX_VALUE =
      By.xpath("//label[text()='Can be responsible for a case']/preceding-sibling::input");
  public static By WORK_WITH_MESSAGE_CHECKBOX = By.xpath("//label[text()='Work with messages']");
  public static By WORK_WITH_MESSAGE_CHECKBOX_VALUE =
      By.xpath("//label[text()='Work with messages']/preceding-sibling::input");
  public static final By GRID_RESULTS_FIRST_UUID =
      By.cssSelector("tr:nth-of-type(1) > td:nth-of-type(1)");

  public static By getUserRoleCaptionByText(String caption) {
    return By.xpath(String.format("//td[contains(text(), '%s')]", caption));
  }

  public static By USER_MANAGEMENT_TAB = By.cssSelector("div#tab-user-users");
  public static By USER_ROLE_DISABLE_BUTTON = By.cssSelector("#actionDisable");
  public static By USER_ROLE_ENABLE_BUTTON = By.cssSelector("#actionEnable");
  public static By ENABLED_DISABLED_SEARCH_COMBOBOX = By.cssSelector("#enabled div");
  public static By DELETE_USER_ROLE_BUTTON = By.cssSelector("#deleteRestore");
  public static By DELETE_CONFIRMATION_BUTTON = By.cssSelector(".popupContent #actionConfirm");
  public static By CANNOT_DELETE_USER_ROLE_POPUP =
      By.xpath("//div[contains(text(), 'Cannot delete user role')]");
  public static By EXPORT_USER_ROLES_BUTTON = By.id("exportUserRoles");
  public static By CANNOT_DELETE_USER_ROLE_POPUP_OKAY_BUTTON = By.cssSelector("#actionOkay");
}
