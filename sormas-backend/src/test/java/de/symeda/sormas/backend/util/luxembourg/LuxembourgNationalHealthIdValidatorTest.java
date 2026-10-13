/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.util.luxembourg;

import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LuxembourgNationalHealthIdValidatorTest {

    public static final String VALID_LU_NATIONAL_HEALTH_ID = "1980010145728";

    @Test
    public void testIsValid() {
        Assertions.assertAll(
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("19800101", 1980, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980Jan0145728", 1980, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980Ja0145728", 1980, 1, 1), is(false)),

                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, 1), is(true)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, null, 1, 1), is(true)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, null), is(true)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, null, 1), is(true)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, null, null), is(true)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, null, null, null), is(true)),

                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1981, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 2, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, 2), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1981, 1, null), is(false)),

                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145628", 1980, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145718", 1980, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145723", 1980, 1, 1), is(false)),
                () -> assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010345728", 1980, 1, null), is(false))
        );
    }

    @Test
    void test_non_standard_validation() {
        Optional<LuxembourgNationalHealthIdValidator.FailureCause> validWithCause = LuxembourgNationalHealthIdValidator.isValidWithCause("4005021384333", 2005, 2, 13);

        Assertions.assertTrue(validWithCause.isEmpty());
    }

    @Test
    void test_non_standard_validation_wrong_birthdate() {
        Assertions.assertAll(
                // year
                () -> assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.BIRTHDATE, LuxembourgNationalHealthIdValidator.isValidWithCause("4005021384333", 2004, 2, 13).orElseThrow()),
                // month
                () -> assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.BIRTHDATE, LuxembourgNationalHealthIdValidator.isValidWithCause("4005021384333", 2005, 9, 13).orElseThrow()),
                // day
                () -> assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.BIRTHDATE, LuxembourgNationalHealthIdValidator.isValidWithCause("4005021384333", 2005, 2, 20).orElseThrow())
        );
    }


    @Test
    void test_empty() {
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.EMPTY, LuxembourgNationalHealthIdValidator.isValidWithCause(null).orElseThrow());
    }

    @Test
    void test_pattern_missing_check_digit() {
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.PATTERN, LuxembourgNationalHealthIdValidator.isValidWithCause("40050213843").orElseThrow());
    }

    @Test
    void test_pattern_empty() {
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.PATTERN, LuxembourgNationalHealthIdValidator.isValidWithCause("").orElseThrow());
    }

    @Test
    void test_pattern_unrelated() {
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.PATTERN, LuxembourgNationalHealthIdValidator.isValidWithCause("abcdefghijklm").orElseThrow());
    }

    @Test
    void test_without_birthdate() {
        Optional<LuxembourgNationalHealthIdValidator.FailureCause> validWithCause = LuxembourgNationalHealthIdValidator.isValidWithCause("3934052492435");

        Assertions.assertTrue(validWithCause.isEmpty());
    }

    @Test
    void test_check_digit_second_to_last() {
        // 3 replaced with 1
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.CHECK_DIGITS, LuxembourgNationalHealthIdValidator.isValidWithCause("3934052492415").orElseThrow());
    }

    @Test
    void test_check_digit_last() {
        // 5 replaced with 8
        assertEquals(LuxembourgNationalHealthIdValidator.FailureCause.CHECK_DIGITS, LuxembourgNationalHealthIdValidator.isValidWithCause("3934052492438").orElseThrow());
    }
}
