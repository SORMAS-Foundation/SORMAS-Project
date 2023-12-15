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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.backend.person.Person;

public class NationalHealthIdValidatorTest {

    public static final String VALID_LU_NATIONAL_HEALTH_ID = "1980010145728";

    @Test
    public void testIsValid() {
        assertThat(NationalHealthIdValidator.isValid("19800101", createPerson(1980, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid("1980Jan0145728", createPerson(1980, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid("1980Ja0145728", createPerson(1980, 1, 1)), is(false));

        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, 1, 1)), is(true));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(null, 1, 1)), is(true));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, 1, null)), is(true));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, null, 1)), is(true));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, null, null)), is(true));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(null, null, null)), is(true));

        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1981, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, 2, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1980, 1, 2)), is(false));
        assertThat(NationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, createPerson(1981, 1, null)), is(false));

        assertThat(NationalHealthIdValidator.isValid("1980010145628", createPerson(1980, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid("1980010145718", createPerson(1980, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid("1980010145723", createPerson(1980, 1, 1)), is(false));
        assertThat(NationalHealthIdValidator.isValid("1980010345728", createPerson(1980, 1, null)), is(false));
    }

    @NotNull
    private static Person createPerson(Integer birthdateYYYY, Integer birthdateMM, Integer birthdateDD) {
        Person person = new Person();
        person.setBirthdateYYYY(birthdateYYYY);
        person.setBirthdateMM(birthdateMM);
        person.setBirthdateDD(birthdateDD);

        return person;
    }
}
