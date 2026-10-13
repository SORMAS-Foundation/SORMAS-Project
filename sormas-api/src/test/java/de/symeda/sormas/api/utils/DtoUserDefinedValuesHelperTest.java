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
package de.symeda.sormas.api.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;

class DtoUserDefinedValuesHelperTest {

	@Test
	void hasAnyUserDefinedValuesIgnoresInheritedProperties() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setPseudonymized(true);
		symptoms.setInJurisdiction(true);

		assertFalse(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(symptoms, SymptomsDto.class, List.of(SymptomsDto.SYMPTOMATIC)));
	}

	@Test
	void hasAnyUserDefinedValuesIgnoresExcludedProperties() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setSymptomatic(true);

		assertFalse(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(symptoms, SymptomsDto.class, List.of(SymptomsDto.SYMPTOMATIC)));
	}

	@Test
	void hasAnyUserDefinedValuesReturnsTrueWhenBusinessPropertyIsSet() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setFever(SymptomState.NO);

		assertTrue(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(symptoms, SymptomsDto.class, List.of(SymptomsDto.SYMPTOMATIC)));
	}

	@Test
	void hasAnyUserDefinedValuesTreatsUnknownSymptomStateAsUserDefinedValueByDefault() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setFever(SymptomState.UNKNOWN);

		assertTrue(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(symptoms, SymptomsDto.class, List.of(SymptomsDto.SYMPTOMATIC)));
	}

	@Test
	void hasAnyUserDefinedValuesIgnoringUnknownTreatsUnknownSymptomStateAsUnset() {
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setFever(SymptomState.UNKNOWN);

		assertFalse(DtoUserDefinedValuesHelper.hasAnyUserDefinedValuesIgnoringUnknown(symptoms, SymptomsDto.class, List.of(SymptomsDto.SYMPTOMATIC)));
	}

	@Test
	void hasAnyUserDefinedValuesIgnoresBaseEntityDataForHospitalization() {
		HospitalizationDto hospitalization = HospitalizationDto.build();

		assertFalse(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(hospitalization, HospitalizationDto.class));
	}

	@Test
	void hasAnyUserDefinedValuesDetectsHospitalizationBusinessValues() {
		HospitalizationDto hospitalization = HospitalizationDto.build();
		hospitalization.setAdmittedToHealthFacility(YesNoUnknown.NO);

		assertTrue(DtoUserDefinedValuesHelper.hasAnyUserDefinedValues(hospitalization, HospitalizationDto.class));
	}

	@Test
	void hasAnyUserDefinedValuesIgnoringUnknownTreatsUnknownYesNoUnknownAsUnset() {
		HospitalizationDto hospitalization = HospitalizationDto.build();
		hospitalization.setAdmittedToHealthFacility(YesNoUnknown.UNKNOWN);

		assertFalse(DtoUserDefinedValuesHelper.hasAnyUserDefinedValuesIgnoringUnknown(hospitalization, HospitalizationDto.class));
	}
}
