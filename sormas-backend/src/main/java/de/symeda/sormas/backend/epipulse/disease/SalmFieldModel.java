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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseModeOfTransmissionRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.SalmSql;

/**
 * Field model for SALM.
 *
 * <p>
 * Uses metadata-filtered shared fields and adds enteric epidemiology/isolate/AST columns.
 * Some declared columns remain intentionally blank where SORMAS has no matching data.
 */
final class SalmFieldModel {

	private SalmFieldModel() {
	}

	/**
	 * Builds SALM fields with hospitalization and place-of-infection SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			SalmSql.spec()
				.plus(EpipulseHospitalisationSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);
		HospitalisationFields.addHospitalisationFields(fields);

		// The shared catalogue covers more than SALM reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.SALM, field.getVariable())) {
				fields.add(field);
			}
		}

		addEpidemiology(fields);
		addIsolateCharacterisation(fields);
		addReferenceLaboratory(fields);
		addSusceptibilities(fields);

		return fields;
	}

	/**
	 * The five columns describing how and where the infection was acquired, in EpiPulse's order.
	 */
	private static void addEpidemiology(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"infection_source",
				"salm_epidata.infectionsource",
				FieldReaders.suspectedVehicle("infection_source"),
				EpipulseVariable.SUSPECTED_VEHICLE,
				EpipulseMapping::suspectedVehicle));

		fields.add(
			ValueDef.direct(
				"mode_of_transmission",
				"salm_epidata.modeoftransmission",
				SalmFieldModel::readModeOfTransmission,
				EpipulseVariable.MODE_OF_TRANSMISSION,
				EpipulseMapping::modeOfTransmission));

		// REVIEW - Serotype: salmonellosis serotype
		fields.add(ValueDef.blank(EpipulseVariable.SEROTYPE));

		fields.add(
			ValueDef.direct(
				"imported_case",
				"salm_epidata.importedcase",
				FieldReaders.imported("imported_case"),
				EpipulseVariable.IMPORTED,
				EpipulseMapping::imported));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));
	}

	/**
	 * Adds isolate-characterization columns not currently represented in SORMAS.
	 */
	private static void addIsolateCharacterisation(List<ValueDef> fields) {

		// REVIEW - IsolateId: "this variable allows the linking of isolate records to the case
		// record; use the NationalRecordId of the isolate".
		fields.add(ValueDef.repeated(EpipulseVariable.ISOLATE_ID, dto -> ValueDef.atLeastOneColumn(Collections.emptyList())));

		// REVIEW - SequenceType: MLST 7 gene sequence type, as a number.
		fields.add(ValueDef.blank(EpipulseVariable.SEQUENCE_TYPE));
	}

	/**
	 * Adds source/reference laboratory columns.
	 */
	private static void addReferenceLaboratory(List<ValueDef> fields) {

		// REVIEW - DateOfReceiptSourceLab: date of receipt in the source laboratory. SORMAS record
		// samples.receiveddate would be the right date, but nothing marks a sample as
		// the source laboratory's.
		fields.add(ValueDef.blank(EpipulseVariable.DATE_OF_RECEIPT_SOURCE_LAB));

		fields.add(
			ValueDef.direct(
				"reference_lab_receipt_date",
				"salm_lab.reference_lab_receipt_date",
				(dto, row) -> dto.setDateOfReceiptReferenceLab((Date) row.get("reference_lab_receipt_date")),
				EpipulseVariable.DATE_OF_RECEIPT_REFERENCE_LAB,
				EpipulseMapping::dateOfReceiptReferenceLab));

		fields.add(
			ValueDef.direct(
				"reference_lab_specimen",
				"salm_lab.reference_lab_specimen",
				SalmFieldModel::readSpecimen,
				EpipulseVariable.SPECIMEN,
				EpipulseMapping::specimen));

		// REVIEW - ESBL: ESBL and/or AmpC confirmed with phenotypic or genotypic tests. REF: AmpC,
		// ESBL, ESBL_AmpC, NEG.
		fields.add(ValueDef.blank(EpipulseVariable.ESBL));

		// REVIEW - ResultCarbapenemase: carbapenemase confirmed with phenotypic or genotypic tests.
		fields.add(ValueDef.blank(EpipulseVariable.RESULT_CARBAPENEMASE));
	}

	private static void addSusceptibilities(List<ValueDef> fields) {

		addSir(fields, "amp", EpipulseVariable.SIR_AMP, EpipulseMapping::sir_AMP, (dto, sir) -> dto.setSir_AMP(sir));
		addSir(fields, "caz", EpipulseVariable.SIR_CAZ, EpipulseMapping::sir_CAZ, (dto, sir) -> dto.setSir_CAZ(sir));

		// REVIEW - SIR_CHL: Chloramphenicol. No drugsusceptibility column.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_CHL));

		addSir(fields, "cip", EpipulseVariable.SIR_CIP, EpipulseMapping::sir_CIP, (dto, sir) -> dto.setSir_CIP(sir));
		addSir(fields, "ctx", EpipulseVariable.SIR_CTX, EpipulseMapping::sir_CTX, (dto, sir) -> dto.setSir_CTX(sir));

		// REVIEW - SIR_GEN: Gentamicin. No drugsusceptibility column.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_GEN));

		// REVIEW - SIR_NAL: Nalidixic Acid. No drugsusceptibility column.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_NAL));

		// REVIEW - SIR_MEM: Meropenem. No drugsusceptibility column.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_MEM));

		// REVIEW - SIR_SMX: Sulfamethoxazole alone. No drugsusceptibility column;
		// trimethoprimsulfamethoxazole is the combination and answers SIR_SXT below, not this.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_SMX));

		addSir(fields, "sxt", EpipulseVariable.SIR_SXT, EpipulseMapping::sir_SXT, (dto, sir) -> dto.setSir_SXT(sir));

		// REVIEW - SIR_TCY: Tetracyclines. No drugsusceptibility column.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_TCY));

		// REVIEW - SIR_TMP: Trimethoprim alone. No drugsusceptibility column;
		// trimethoprimsulfamethoxazole is the combination and answers SIR_SXT above, not this.
		fields.add(ValueDef.blank(EpipulseVariable.SIR_TMP));
	}

	/**
	 * One antibiotic's {@code SIR_*} column, from the panel {@link SalmSql} selected. Kept to one
	 * line per call so that EpiPulse's order stays readable down the method above.
	 *
	 * @param key
	 *            the antibiotic's alias in {@code salm_ast_data}, as {@link SalmSql} declares it
	 */
	private static void addSir(
		List<ValueDef> fields,
		String key,
		EpipulseVariable variable,
		Function<EpipulseDiseaseExportEntryDto, String> csvValue,
		BiConsumer<EpipulseDiseaseExportEntryDto, String> setter) {

		AstFields.addSir(
			fields,
			key,
			"salm_ast." + key + "_susceptibility",
			variable,
			csvValue,
			(dto, sir) -> setter.accept(dto, EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));
	}

	private static void readModeOfTransmission(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ModeOfTransmission modeOfTransmission = FieldReaders.parseEnum(ModeOfTransmission.class, (String) row.get("mode_of_transmission"));

		dto.setModeOfTransmission(EpipulseModeOfTransmissionRef.codeFor(modeOfTransmission));
	}

	private static void readSpecimen(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		SampleMaterial sampleMaterial = FieldReaders.parseEnum(SampleMaterial.class, (String) row.get("reference_lab_specimen"));

		dto.setSpecimen(EpipulseLaboratoryMapper.mapSampleMaterialToEntericSpecimenCode(sampleMaterial));
	}
}
