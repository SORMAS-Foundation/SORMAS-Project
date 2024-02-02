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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;

public class LuxembourgNationalHealthIdValidatorTest {

	public static final String VALID_LU_NATIONAL_HEALTH_ID = "1980010145728";

	@Test
	public void testIsValid() {
		assertThat(LuxembourgNationalHealthIdValidator.isValid("19800101", 1980, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980Jan0145728", 1980, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980Ja0145728", 1980, 1, 1), is(false));

		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, 1), is(true));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, null, 1, 1), is(true));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, null), is(true));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, null, 1), is(true));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, null, null), is(true));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, null, null, null), is(true));

		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1981, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 2, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1980, 1, 2), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid(VALID_LU_NATIONAL_HEALTH_ID, 1981, 1, null), is(false));

		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145628", 1980, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145718", 1980, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010145723", 1980, 1, 1), is(false));
		assertThat(LuxembourgNationalHealthIdValidator.isValid("1980010345728", 1980, 1, null), is(false));
	}
}
