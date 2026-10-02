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

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDengueSerotypeRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.sample.Serotype;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.DengueSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePathogenTestSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;

/**
 * Field model for DENGUE.
 *
 * <p>
 * Uses metadata-filtered shared fields, then adds dengue-specific clinical, epidemiology, and lab
 * mappings. Several mapped fields are currently empty on dengue forms but remain wired so exports
 * start filling automatically if form visibility changes.
 */
final class DengueFieldModel {

	private DengueFieldModel() {
	}

	/**
	 * Builds DENGUE CSV fields and merged SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			DengueSql.spec()
				.plus(EpipulseHospitalisationSql.spec())
				.plus(EpipulsePathogenTestSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);
		HospitalisationFields.addHospitalisationFields(fields);

		// The shared catalogue covers more than DENGUE reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.DENGUE, field.getVariable())) {
				fields.add(field);
			}
		}

		addClinical(fields);
		addEpidemiology(fields);
		addLaboratory(fields);

		return fields;
	}

	/**
	 * The two clinical columns, which ask different questions of different fields.
	 */
	private static void addClinical(List<ValueDef> fields) {

		// cases.clinicalconfirmation carries no @Diseases annotation, so unlike the epidemiology
		// columns this one is answerable on a dengue case today
		fields.add(
			ValueDef.direct(
				"clinical_confirmation",
				"c.clinicalconfirmation",
				FieldReaders.clinicalCriteriaStatus("clinical_confirmation"),
				EpipulseVariable.CLINICAL_CRITERIA_STATUS,
				EpipulseMapping::clinicalCriteriaStatus));

		// No select column of its own: asymptomatic is already in every model's base block, where
		// it suppresses DateOfOnset, and this reads the DTO field that block populates.
		fields.add(ValueDef.calculated(EpipulseVariable.CLINICAL_CRITERIA, DengueFieldModel::clinicalCriteria));
	}

	/**
	 * The four columns describing how and where the infection was acquired, and the cluster it
	 * belongs to.
	 */
	private static void addEpidemiology(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"imported_case",
				"dengue_epidata.importedcase",
				FieldReaders.imported("imported_case"),
				EpipulseVariable.IMPORTED,
				EpipulseMapping::imported));

		fields.add(
			ValueDef.direct(
				"mode_of_transmission",
				"dengue_epidata.modeoftransmission",
				DengueFieldModel::readModeOfTransmission,
				EpipulseVariable.MODE_OF_TRANSMISSION,
				EpipulseMapping::modeOfTransmission));

		// EpiPulse asks for this "only for autochthonous cases", which is guidance on when the
		// answer is informative rather than a condition on the column; a recorded identifier is
		// reported whatever the case's travel history, with Imported and PlaceOfInfection beside
		// it. Measles reads epidata.clustertypetext into the same DTO field - a different column
		// for a different disease, which is why the choice sits in each model rather than shared.
		fields.add(
			ValueDef.direct(
				"cluster_identifier",
				"dengue_epidata.clusteridentifier",
				(dto, row) -> dto.setClusterIdentification((String) row.get("cluster_identifier")),
				EpipulseVariable.CLUSTER_ID,
				EpipulseMapping::clusterIdentification));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));
	}

	/**
	 * {@code Serotype} and {@code PathogenDetectionMethod}.
	 */
	private static void addLaboratory(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"serotype",
				"dengue_serotype.serotype",
				DengueFieldModel::readSerotype,
				EpipulseVariable.SEROTYPE,
				EpipulseMapping::serotype));

		PathogenTestFields.addPathogenTestFields(fields);

		fields.add(
			ValueDef.sqlOnly(
				"seroconversion_or_titre_rise",
				"dengue_seroconversion.seroconversion_or_titre_rise",
				(dto, row) -> dto.setSeroconversionOrTitreRise((Boolean) row.get("seroconversion_or_titre_rise"))));

		fields.add(
			ValueDef
				.repeated(EpipulseVariable.PATHOGEN_DETECTION_METHOD, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.dengueDetectionMethods(dto))));
	}

	/**
	 * Maps dengue {@code ClinicalCriteria}; currently only {@code ASY} can be derived reliably.
	 */
	private static String clinicalCriteria(EpipulseDiseaseExportEntryDto entry) {
		return entry.getAsymptomatic() == SymptomState.YES ? "ASY" : null;
	}

	private static void readModeOfTransmission(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ModeOfTransmission modeOfTransmission = FieldReaders.parseEnum(ModeOfTransmission.class, (String) row.get("mode_of_transmission"));

		dto.setModeOfTransmission(EpipulseDengueModeOfTransmissionRef.codeFor(modeOfTransmission));
	}

	private static void readSerotype(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		Serotype serotype = FieldReaders.parseEnum(Serotype.class, (String) row.get("serotype"));

		dto.setSerotype(EpipulseDengueSerotypeRef.codeFor(serotype));
	}
}
