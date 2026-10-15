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
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMalaModeOfTransmissionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMalaPathogenRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseOccupationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePurposeOfTravelRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.exposure.ProphylaxisAdherence;
import de.symeda.sormas.api.exposure.TravelPurpose;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.MalaSql;

/**
 * Field model for MALA.
 *
 * <p>
 * Uses metadata-filtered shared fields and adds malaria-specific epidemiology, travel, and
 * pathogen mapping. Repeatable groups include {@code PlaceOfInfection} and {@code Pathogen}.
 */
final class MalaFieldModel {

	private MalaFieldModel() {
	}

	/**
	 * Builds MALA fields and SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			MalaSql.spec()
				.plus(EpipulseHospitalisationSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);
		HospitalisationFields.addHospitalisationFields(fields);

		// The shared catalogue covers more than MALA reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.MALA, field.getVariable())) {
				fields.add(field);
			}
		}

		addPerson(fields);
		addClinical(fields);
		addEpidemiology(fields);
		addTravel(fields);
		addLaboratory(fields);

		return fields;
	}

	/**
	 * {@code Occupation} - the only column MALA reads off the person rather than the case.
	 *
	 * <p>
	 * {@code person} is joined by the common query, so this needs nothing from {@link MalaSql}.
	 * The column holds a {@code CustomizableEnum} value rather than a Java enum constant, which is
	 * why it is read as a string and passed to the lookup as one; see
	 * {@link EpipulseOccupationRef}.
	 */
	private static void addPerson(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"occupation_type",
				"person.occupationtype",
				MalaFieldModel::readOccupation,
				EpipulseVariable.OCCUPATION,
				EpipulseMapping::occupation));
	}

	/**
	 * {@code ClinicalCriteriaStatus} - whether the criteria for a clinical picture of malaria are
	 * met.
	 *
	 * <p>
	 * From {@code cases.clinicalconfirmation}, which the common {@code filtered_cases} already
	 * carries, and read exactly as {@link MeasFieldModel} reads it. Note that this is the plain
	 * BOOL variable and not {@code ClinicalCriteria}, the REF variable PNEU and MENI report against
	 * a per-code value set - same words, different question.
	 */
	private static void addClinical(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"clinical_confirmation",
				"c.clinicalconfirmation",
				FieldReaders.clinicalCriteriaStatus("clinical_confirmation"),
				EpipulseVariable.CLINICAL_CRITERIA_STATUS,
				EpipulseMapping::clinicalCriteriaStatus));
	}

	/**
	 * The three columns describing how and where the infection was acquired.
	 */
	private static void addEpidemiology(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"imported_case",
				"mala_epidata.importedcase",
				FieldReaders.imported("imported_case"),
				EpipulseVariable.IMPORTED,
				EpipulseMapping::imported));

		fields.add(
			ValueDef.direct(
				"mode_of_transmission",
				"mala_epidata.modeoftransmission",
				MalaFieldModel::readModeOfTransmission,
				EpipulseVariable.MODE_OF_TRANSMISSION,
				EpipulseMapping::modeOfTransmission));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));
	}

	/**
	 * The two columns the case's travel answers, from the one journey {@link MalaSql} picked.
	 *
	 * <p>
	 * They are added together because they describe one trip. Splitting them between two methods,
	 * or reading them from two selections, would make it possible for the file to carry a purpose
	 * from one journey beside an adherence from another.
	 */
	private static void addTravel(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"prophylaxis_adherence",
				"mala_travel.prophylaxis_adherence",
				MalaFieldModel::readProphylaxis,
				EpipulseVariable.PROPHYLAXIS,
				EpipulseMapping::prophylaxis));

		fields.add(
			ValueDef.direct(
				"travel_purpose",
				"mala_travel.travel_purpose",
				MalaFieldModel::readPurposeOfTravel,
				EpipulseVariable.PURPOSE_OF_TRAVEL,
				EpipulseMapping::purposeOfTravel));
	}

	/**
	 * {@code Pathogen} - the species found, one column each.
	 *
	 * <p>
	 * The source column and the repeatable group are separate definitions for the same reason
	 * {@code PlaceOfInfection}'s are: the aggregate has to reach the DTO before the group can be
	 * rendered from it, and only the second of the two emits a column.
	 */
	private static void addLaboratory(List<ValueDef> fields) {

		fields.add(ValueDef.sqlOnly("pathogens", "mala_path.pathogens", MalaFieldModel::readPathogens));
		fields.add(ValueDef.repeated(EpipulseVariable.PATHOGEN, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.pathogen(dto))));
	}

	/**
	 * {@code Occupation} - the stored occupation value, as the code MALA spells it with.
	 */
	private static void readOccupation(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		dto.setOccupation(EpipulseOccupationRef.codeFor((String) row.get("occupation_type")));
	}

	private static void readModeOfTransmission(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ModeOfTransmission modeOfTransmission = FieldReaders.parseEnum(ModeOfTransmission.class, (String) row.get("mode_of_transmission"));

		dto.setModeOfTransmission(EpipulseMalaModeOfTransmissionRef.codeFor(modeOfTransmission));
	}

	/**
	 * Maps prophylaxis adherence to EpiPulse BOOL; unknown/other remain blank.
	 */
	private static void readProphylaxis(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ProphylaxisAdherence adherence = FieldReaders.parseEnum(ProphylaxisAdherence.class, (String) row.get("prophylaxis_adherence"));

		if (adherence == null || adherence == ProphylaxisAdherence.UNKNOWN || adherence == ProphylaxisAdherence.OTHER) {
			return;
		}

		dto.setProphylaxis(adherence == ProphylaxisAdherence.PROPHYLAXIS_COMPLETED);
	}

	private static void readPurposeOfTravel(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		TravelPurpose travelPurpose = FieldReaders.parseEnum(TravelPurpose.class, (String) row.get("travel_purpose"));

		dto.setPurposeOfTravel(EpipulsePurposeOfTravelRef.codeFor(travelPurpose));
	}

	/**
	 * Parses aggregated pathogen species, maps to MALA codes, and de-duplicates.
	 *
	 * <p>
	 * {@code PLASSPP} ("species not specified") is dropped when a named species is also reported:
	 * an unspeciated screening result next to an identified species is the same infection, and
	 * reporting both reads as a co-infection with a second, unidentified Plasmodium.
	 */
	private static void readPathogens(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		List<String> codes = new ArrayList<>();

		for (String specieName : FieldReaders.splitCollection((String) row.get("pathogens"))) {

			PathogenSpecie specie = FieldReaders.parseEnum(PathogenSpecie.class, specieName);
			String code = EpipulseMalaPathogenRef.codeFor(specie);

			if (code != null && !codes.contains(code)) {
				codes.add(code);
			}
		}

		if (codes.size() > 1) {
			codes.remove(EpipulseMalaPathogenRef.PLASSPP.name());
		}

		dto.setPathogen(codes);
	}
}
