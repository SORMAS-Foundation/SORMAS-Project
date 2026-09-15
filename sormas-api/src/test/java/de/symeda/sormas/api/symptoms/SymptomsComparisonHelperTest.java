/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
 *******************************************************************************/
package de.symeda.sormas.api.symptoms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SymptomsComparisonHelperTest {

	@Test
	void hasAnyUserDefinedSymptomsReturnsFalseForUntouchedSymptoms() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setPseudonymized(true);
		symptoms.setInJurisdiction(true);
		symptoms.setSymptomatic(false);
		symptoms.setSymptomsComments("   ");

		assertFalse(SymptomsComparisonHelper.hasAnyUserDefinedSymptoms(symptoms));
	}

	@Test
	void hasAnyUserDefinedSymptomsReturnsTrueWhenAnyBusinessFieldIsSet() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setFever(SymptomState.NO);

		assertTrue(SymptomsComparisonHelper.hasAnyUserDefinedSymptoms(symptoms));
	}

	@Test
	void hasAnyUserDefinedSymptomsTreatsUnknownAsUnset() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setFever(SymptomState.UNKNOWN);

		assertFalse(SymptomsComparisonHelper.hasAnyUserDefinedSymptoms(symptoms));
	}
}
