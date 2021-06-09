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

package org.sormas.e2etests.pages.application.cases;

import org.openqa.selenium.By;

public class HospitalizationTabPage {
  public static final By DATE_OF_VISIT_OR_ADMISSION_INPUT = By.cssSelector("#admissionDate input");
  public static final By DATE_OF_DISCHARGE_OR_TRANSFER_INPUT =
      By.cssSelector("#dischargeDate input");
  public static final By REASON_FOR_HOSPITALIZATION_COMBOBOX =
      By.cssSelector("#hospitalizationReason div");
  public static final By STAY_IN_THE_INTENSIVE_CARE_UNIT_OPTIONS =
      By.cssSelector("#intensiveCareUnit label");
  public static final By START_OF_STAY_DATE_INPUT = By.cssSelector("#intensiveCareUnitStart input");
  public static final By END_OF_STAY_DATE_INPUT = By.cssSelector("#intensiveCareUnitEnd input");
  public static final By ISOLATION_OPTIONS = By.cssSelector("#isolated label");
  public static final By DATE_OF_ISOLATION_INPUT = By.cssSelector("#isolationDate input");
  public static final By WAS_THE_PATIENT_HOSPITALIZED_PREVIOUSLY_OPTIONS =
      By.cssSelector("#hospitalizedPreviously label");
  public static final By WAS_THE_PATIENT_ADMITTED_AS_IMPATIENT_OPTIONS =
      By.cssSelector("#admittedToHealthFacility label");
  public static final By LEFT_AGAINST_MEDICAL_ADVICE_OPTIONS =
      By.cssSelector("#leftAgainstAdvice label");
  public static final By SPECIFY_REASON_INPUT = By.id("otherHospitalizationReason");
  public static final By NEW_ENTRY_LINK = By.id("actionNewEntry");
  public static final By SAVE_BUTTON = By.id("commit");
  public static final By FIRST_PREVIOUS_HOSPITALIZATION_ENTRY =
      By.xpath("//div[contains(@id, 'de.symeda.sormas.api.hospitalization')]");
  public static final By SUCCESSFUL_SAVE_POPUP = By.xpath("//h1[text()='Case saved']");
}
