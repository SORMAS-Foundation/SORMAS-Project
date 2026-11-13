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

import static de.symeda.sormas.backend.epipulse.disease.CommonFields.DATE_OF_LAST_VACCINATION;
import static de.symeda.sormas.backend.epipulse.disease.CommonFields.DATE_OF_ONSET;
import static de.symeda.sormas.backend.epipulse.disease.CommonFields.HOSPITALISATION;

import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePathogenTestSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;

/**
 * PERT (Pertussis) export: the simplest model - no CTEs, no joins, no SELECT columns of its own.
 * 
 * <p>
 * All fields are {@link ValueDef#calculated} over common DTO state.
 * <p>
 * {@code PathogenDetectionMethod} repeats (floored at one column) over qualifying tests.
 * {@code VaccinationStatusMaternal} reads the patient's {@code MATERNAL_VACCINATION} immunizations.
 *
 */
final class PertussisFieldModel {

	private PertussisFieldModel() {
	}

	static FieldModel create() {
		return new FieldModel(
			fields(),
			EpipulseHospitalisationSql.spec().plus(EpipulseVaccinationSql.spec()).plus(EpipulsePathogenTestSql.spec()),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = CommonFields.defs();
		HospitalisationFields.addHospitalisationFields(fields);
		VaccinationFields.addVaccinationFields(fields);
		PathogenTestFields.addPathogenTestFields(fields);

		Collections.addAll(fields, DATE_OF_ONSET, HOSPITALISATION);

		// Floored to one column: blank when no qualifying test, not absent
		// (EpiPulse validates declared columns must be present)
		fields.add(
			ValueDef.repeated(
				EpipulseVariable.PATHOGEN_DETECTION_METHOD,
				dto -> ValueDef.atLeastOneColumn(EpipulseMapping.pathogenDetectionMethods(dto))));

		// DateOfLastVaccination is Repeatable=No (one column, always present)
		// It was repeatable, causing the column to disappear when no case had immunization (CONF-004)
		fields.add(DATE_OF_LAST_VACCINATION);
		fields.add(CommonFields.vaccinationStatus(CommonFields.STANDARD_MAX_DOSES));

		// True for a patient under 2 with a MATERNAL_VACCINATION immunization, otherwise blank.
		// Read from the immunizations EpipulseVaccinationSql already loads.
		fields.add(ValueDef.calculated(EpipulseVariable.VACCINATION_STATUS_MATERNAL, EpipulseMapping::vaccinationStatusMaternal));

		// TODO: no SORMAS field holds the gestational week of the maternal vaccination.
		// GestationalAgeCategory bands the birth (AT_TERM, PREMATURE_*). A possible route is
		// person.gestationAgeAtBirth minus the weeks from a dose recorded under the MATERNAL_VACCINATION
		// immunization to the birth date - but that would be too much inferring to be sure.
		fields.add(ValueDef.blank(EpipulseVariable.GESTATIONAL_AGE_AT_VACCINATION));

		return fields;
	}
}
