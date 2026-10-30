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

import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;
import de.symeda.sormas.backend.epipulse.sql.SqlQueryModel;

/**
 * Default model for subject codes without a dedicated implementation.
 *
 * <p>
 * Uses shared fields filtered by subject-code metadata, plus optional hospitalisation/vaccination
 * SQL blocks when declared. This keeps all subject codes exportable while allowing dedicated
 * models where shared logic is insufficient.
 */
final class DefaultFieldModel {

	private DefaultFieldModel() {
	}

	static FieldModel create(EpipulseSubjectCode subjectCode) {

		List<ValueDef> fields = new ArrayList<>();
		CommonFields.addOperationalFields(fields);

		// Optional blocks are metadata-driven and bring both columns and SQL together.
		SqlQueryModel sql = SqlQueryModel.none();

		if (EpipulseSubjectCodeVariables.defines(subjectCode, EpipulseVariable.HOSPITALISATION)) {
			HospitalisationFields.addHospitalisationFields(fields);
			sql = sql.plus(EpipulseHospitalisationSql.spec());
		}

		if (EpipulseSubjectCodeVariables.defines(subjectCode, EpipulseVariable.DATE_OF_LAST_VACCINATION)
			|| EpipulseSubjectCodeVariables.defines(subjectCode, EpipulseVariable.VACCINATION_STATUS)) {
			VaccinationFields.addVaccinationFields(fields);
			sql = sql.plus(EpipulseVaccinationSql.spec());
		}

		// PathogenDetectionMethod is intentionally excluded here: mapping is subject-code specific.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(subjectCode, field.getVariable())) {
				fields.add(field);
			}
		}

		return new FieldModel(fields, sql, 0);
	}
}
