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

import java.util.List;

import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;

/**
 * ClinicalCriteria column for MENI and PNEU only (each of twelve codes has own value set).
 * Both classify same symptoms in same order; differ on one value: pneumonia code (MENI='PNEU', PNEU='BACTERPNEUMO').
 * Not in CommonFields: no shared generalization across twelve codes.
 */
final class InvasiveClinicalCriteriaFields {

	/** MENI's code for a pneumonic presentation. Its value set has no {@code BACTERPNEUMO}. */
	static final String MENI_PNEUMONIA_CODE = "PNEU";

	/** PNEU's code for the same thing. Its value set has no plain {@code PNEU}. */
	static final String PNEU_PNEUMONIA_CODE = "BACTERPNEUMO";

	private static final ValueDef CLINICAL_CRITERIA = ValueDef.calculated(EpipulseVariable.CLINICAL_CRITERIA, EpipulseMapping::clinicalCriteria);

	private InvasiveClinicalCriteriaFields() {
	}

	/**
	 * ClinicalCriteria column and symptom columns from common query's symptom row (no new CTE).
	 * Asymptomatic already handled in base block (suppresses DateOfOnset); reader accesses by alias.
	 * Reader hangs off pneumonia column (position irrelevant; all values addressable by alias).
	 *
	 * @param pneumoniaCode
	 *            {@link #MENI_PNEUMONIA_CODE} or {@link #PNEU_PNEUMONIA_CODE}
	 */
	static void addClinicalCriteria(List<ValueDef> fields, String pneumoniaCode) {

		fields.add(FieldReaders.addressable("meningitis", "symptom.meningitis"));
		fields.add(FieldReaders.addressable("septicaemia", "symptom.septicaemia"));
		fields.add(
			ValueDef.sqlOnly(
				"pneumonia",
				"symptom.pneumoniaclinicalorradiologic",
				(dto, row) -> dto.setClinicalCriteria(
					clinicalCriteria(
						FieldReaders.parseSymptomState((String) row.get("meningitis")),
						FieldReaders.parseSymptomState((String) row.get("septicaemia")),
						FieldReaders.parseSymptomState((String) row.get("pneumonia")),
						FieldReaders.parseSymptomState((String) row.get("asymptomatic")),
						pneumoniaCode))));
		fields.add(CLINICAL_CRITERIA);
	}

	/**
	 * Clinical presentation code (stops at first match): asymptomatic=>null; meningitis+septicaemia=>MENISEPTI;
	 * meningitis=>MENI; septicaemia=>SEPTI; pneumonia=>pneumoniaCode; else=>null.
	 * OTH not derived (would require testing all Symptoms fields). Only SymptomState.YES counts as present.
	 *
	 * @param asymptomatic
	 *            blanks column outright, whatever else recorded
	 * @param pneumoniaCode
	 *            reporting disease's code for pneumonic presentation
	 */
	static String clinicalCriteria(
		SymptomState meningitis,
		SymptomState septicaemia,
		SymptomState pneumonia,
		SymptomState asymptomatic,
		String pneumoniaCode) {

		if (asymptomatic == SymptomState.YES) {
			return null;
		}

		boolean hasMeningitis = meningitis == SymptomState.YES;
		boolean hasSepticaemia = septicaemia == SymptomState.YES;

		if (hasMeningitis && hasSepticaemia) {
			return "MENISEPTI";
		} else if (hasMeningitis) {
			return "MENI";
		} else if (hasSepticaemia) {
			return "SEPTI";
		} else if (pneumonia == SymptomState.YES) {
			return pneumoniaCode;
		}

		return null;
	}
}
