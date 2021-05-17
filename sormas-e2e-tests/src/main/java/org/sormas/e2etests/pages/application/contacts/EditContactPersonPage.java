/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

public class EditContactPersonPage {
  public static final By CONTACT_PERSON_TAB = By.cssSelector("div#tab-contacts-person");
  public static final By PERSON_UUID = By.cssSelector("#uuid");
  public static final By USER_INFORMATION = By.cssSelector(".v-slot.v-slot-h2.v-slot-vspace-top-none.v-slot-primary");
  public static final By FIRST_NAME_INPUT = By.cssSelector("[location='firstName'] input");
  public static final By LAST_NAME_INPUT = By.cssSelector("[location='lastName'] input");
  public static final By DATE_OF_BIRTH_YEAR_INPUT = By.cssSelector("#birthdateYYYY input");
  public static final By DATE_OF_BIRTH_MONTH_INPUT = By.cssSelector("#birthdateMM input");
  public static final By DATE_OF_BIRTH_DAY_INPUT = By.cssSelector("#birthdateDD input");
  public static final By SEX_INPUT = By.cssSelector("#sex input");
  public static final By PASSPORT_NUMBER_INPUT = By.cssSelector("#passportNumber");
  public static final By NATIONAL_HEALTH_ID_INPUT = By.cssSelector("#nationalHealthId");
  public static final By EMAIL_FIELD = By.xpath("//div[contains(text(), 'Email')]//following::div[1]");
  public static final By PHONE_FIELD = By.xpath("//div[contains(text(), 'Phone')]//following::div[1]");

}
