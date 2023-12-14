/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalemail.luxembourg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.symeda.sormas.backend.person.Person;

public class NationalHealthIdValidator {

    /**
     * - AAAA = année de naissance
     * - MM = mois de naissance
     * - JJ = jour de naissance
     * - XXX = numéro aléatoire unique par date de naissance
     * - C1 = numéro de contrôle calculé sur AAAAMMJJXXX suivant l’algorithme LUHN 10
     * - C2 = numéro de contrôle calculé sur AAAAMMJJXXX suivant l’algorithme VERHOEFF
     */
    private static final Pattern NATIONAL_HEALTH_ID_PATTERN = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})(\\d{3})(\\d)(\\d)");

    public static boolean isValid(String nationalHealthId, Person person) {
        if (nationalHealthId == null) {
            return false;
        }

        Matcher patternMatcher = NATIONAL_HEALTH_ID_PATTERN.matcher(nationalHealthId);
        if (!patternMatcher.matches()) {
            return false;
        }

        String yyyy = patternMatcher.group(1);
        String mm = patternMatcher.group(2);
        String dd = patternMatcher.group(3);
        String xxx = patternMatcher.group(4);
        String c1 = patternMatcher.group(5);
        String c2 = patternMatcher.group(6);

        if (isNullOrEquals(person.getBirthdateYYYY(), Integer.parseInt(yyyy))
                && isNullOrEquals(person.getBirthdateMM(), Integer.parseInt(mm))
                && isNullOrEquals(person.getBirthdateDD(), Integer.parseInt(dd))) {
            String iNumber = yyyy + mm + dd + xxx;

            if (CheckDigitLuhn.checkDigit(iNumber + c1) && CheckDigitVerhoeff.checkDigit(iNumber + c2)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isNullOrEquals(Integer personBirthdateFieldValue, int yyyy) {
        return personBirthdateFieldValue == null || personBirthdateFieldValue == yyyy;
    }

}
