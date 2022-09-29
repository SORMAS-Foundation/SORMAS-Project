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

package org.sormas.e2etests.enums;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public enum EnvLangsTranslations {
  en("English", "Deutsch"),
  de("English", "Deutsch"),
  ur("\u0627\u0646\u06AF\u0631\u06CC\u0632\u06CC", "\u062C\u0631\u0645\u0646");

  private final String englishLang;
  private final String germanLang;

  EnvLangsTranslations(String englishLang, String germanLang) {
    this.englishLang = englishLang;
    this.germanLang = germanLang;
  }

  @SneakyThrows
  public static EnvLangsTranslations getValueFor(String option) {
    EnvLangsTranslations[] caseOutcomeOptions = EnvLangsTranslations.values();
    for (EnvLangsTranslations value : caseOutcomeOptions) {
      if (value.toString().equalsIgnoreCase(option)) {
        return value;
      }
    }
    throw new Exception("Unable to find values for language " + option);
  }
}
