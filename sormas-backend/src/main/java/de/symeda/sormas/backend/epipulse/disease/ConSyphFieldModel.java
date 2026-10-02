/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse.disease;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.ConSyphSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfBirthSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfNationalitySql;

/**
 * Field model for congenital syphilis (CONSYPH).
 *
 * <p>
 * CONSYPH and SYPH both select {@code Disease.SYPHILIS} but are separated by
 * {@code EpipulseCaseSubset}. This model uses shared fields filtered by CONSYPH metadata and adds
 * mother-related columns from case {@code EpiData}; it has no repeatable groups.
 */
final class ConSyphFieldModel {

	private ConSyphFieldModel() {
	}

	static FieldModel create() {
		return new FieldModel(fields(), EpipulseCountryOfBirthSql.spec().plus(EpipulseCountryOfNationalitySql.spec()).plus(ConSyphSql.spec()), 0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);

		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.CONSYPH, field.getVariable())) {
				fields.add(field);
			}
		}

		CountryOfBirthFields.addCountryOfBirth(fields);
		addCountryOfBirthOfMother(fields);
		CountryOfNationalityFields.addCountryOfNationality(fields);
		addCountryOfNationalityOfMother(fields);

		fields.add(ValueDef.calculated(EpipulseVariable.PATHOGEN_DETECTION_RESULT, EpipulseMapping::pathogenDetectionResult));

		addDeclaredButUnanswered(fields);

		return fields;
	}

	/**
	 * Adds {@code CountryOfBirthOfMother}; nationality is added from the same source row.
	 */
	private static void addCountryOfBirthOfMother(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"mother_birth_country_nutscode",
				"consyph_mother_birth_country.nutscode",
				(dto, row) -> dto.setMotherBirthCountryNutsCode((String) row.get("mother_birth_country_nutscode")),
				EpipulseVariable.COUNTRY_OF_BIRTH_OF_MOTHER,
				EpipulseMapping::countryOfBirthOfMother));
	}

	/**
	 * Adds {@code CountryOfNationalityOfMother} from {@code EpiData.motherCitizenship}.
	 */
	private static void addCountryOfNationalityOfMother(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"mother_citizenship_nutscode",
				"consyph_mother_citizenship.nutscode",
				(dto, row) -> dto.setMotherCitizenshipCountryNutsCode((String) row.get("mother_citizenship_nutscode")),
				EpipulseVariable.COUNTRY_OF_NATIONALITY_OF_MOTHER,
				EpipulseMapping::countryOfNationalityOfMother));
	}

	/**
	 * Declared CONSYPH columns that currently remain unanswered.
	 */
	private static void addDeclaredButUnanswered(List<ValueDef> fields) {

		// REVIEW - ClinicalCriteriaStatus
		fields.add(ValueDef.blank(EpipulseVariable.CLINICAL_CRITERIA_STATUS));

		// REVIEW - EpiLinked
		fields.add(ValueDef.blank(EpipulseVariable.EPI_LINKED));
	}
}
