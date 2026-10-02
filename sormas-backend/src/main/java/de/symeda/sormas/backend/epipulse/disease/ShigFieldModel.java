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
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseShigPathogenRef;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseHospitalisationSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.ShigSql;

/**
 * Field model for SHIG.
 *
 * <p>
 * Reuses the enteric pattern from SALM with SHIG-specific pathogen and susceptibility columns.
 */
final class ShigFieldModel {

	private ShigFieldModel() {
	}

	/**
	 * Builds SHIG fields with hospitalisation and place-of-infection SQL fragments.
	 */
	static FieldModel create() {

		return new FieldModel(
			fields(),
			ShigSql.spec()
				.plus(EpipulseHospitalisationSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.LOWEST_LEVEL)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);
		HospitalisationFields.addHospitalisationFields(fields);

		// The shared catalogue covers more than SHIG reports, so each column is kept only if
		// EpiPulse defines it for this subject code - the same filter DefaultFieldModel applies.
		for (ValueDef field : CommonFields.genericCsvDefs()) {
			if (EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.SHIG, field.getVariable())) {
				fields.add(field);
			}
		}

		addEpidemiology(fields);
		addIsolateCharacterisation(fields);
		addSusceptibilities(fields);

		return fields;
	}

	/**
	 * The four columns describing how and where the infection was acquired.
	 */
	private static void addEpidemiology(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"infection_source",
				"shig_epidata.infectionsource",
				FieldReaders.suspectedVehicle("infection_source"),
				EpipulseVariable.SUSPECTED_VEHICLE,
				EpipulseMapping::suspectedVehicle));

		fields.add(
			ValueDef.direct(
				"mode_of_transmission",
				"shig_epidata.modeoftransmission",
				ShigFieldModel::readModeOfTransmission,
				EpipulseVariable.MODE_OF_TRANSMISSION,
				EpipulseMapping::modeOfTransmission));

		fields.add(
			ValueDef.direct(
				"imported_case",
				"shig_epidata.importedcase",
				FieldReaders.imported("imported_case"),
				EpipulseVariable.IMPORTED,
				EpipulseMapping::imported));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));
	}

	/**
	 * Adds isolate-characterization columns.
	 */
	private static void addIsolateCharacterisation(List<ValueDef> fields) {

		// REVIEW - IsolateId: "this variable allows the linking of isolate records to the case
		// record; use the NationalRecordId of the isolate".
		fields.add(ValueDef.repeated(EpipulseVariable.ISOLATE_ID, dto -> ValueDef.atLeastOneColumn(Collections.emptyList())));

		fields.add(
			ValueDef.direct(
				"pathogen_specie",
				"shig_pathogen.pathogen_specie",
				ShigFieldModel::readPathogen,
				EpipulseVariable.PATHOGEN,
				EpipulseMapping::pathogenCode));

		// REVIEW - Serotype: serotype of the pathogen which is the cause of the reported disease.

		fields.add(ValueDef.blank(EpipulseVariable.SEROTYPE));

		fields.add(
			ValueDef.direct(
				"diagnostic_specimen",
				"shig_specimen.diagnostic_specimen",
				ShigFieldModel::readSpecimen,
				EpipulseVariable.SPECIMEN,
				EpipulseMapping::specimen));
	}

	/**
	 * {@code ECOFF_AZM} and the five {@code SIR_*} columns, in EpiPulse's order.
	 */
	private static void addSusceptibilities(List<ValueDef> fields) {

		// REVIEW - ECOFF_AZM: "acquired resistance to Azithromycin", as a BOOL. SORMAS does record
		// azithromycin - drugsusceptibility has azithromycinmic, azithromycinsusceptibility and
		// azithromycinmethod - so this is not a missing field, and it is deliberately not derived
		// from either of them.
		fields.add(ValueDef.blank(EpipulseVariable.ECOFF_AZM));

		addSir(fields, "amp", EpipulseVariable.SIR_AMP, EpipulseMapping::sir_AMP, (dto, sir) -> dto.setSir_AMP(sir));
		addSir(fields, "caz", EpipulseVariable.SIR_CAZ, EpipulseMapping::sir_CAZ, (dto, sir) -> dto.setSir_CAZ(sir));
		addSir(fields, "cip", EpipulseVariable.SIR_CIP, EpipulseMapping::sir_CIP, (dto, sir) -> dto.setSir_CIP(sir));
		addSir(fields, "ctx", EpipulseVariable.SIR_CTX, EpipulseMapping::sir_CTX, (dto, sir) -> dto.setSir_CTX(sir));
		addSir(fields, "sxt", EpipulseVariable.SIR_SXT, EpipulseMapping::sir_SXT, (dto, sir) -> dto.setSir_SXT(sir));
	}

	/**
	 * One antibiotic's {@code SIR_*} column, from the panel {@link ShigSql} selected. Kept to one
	 * line per call so that EpiPulse's order stays readable down the method above.
	 *
	 * @param key
	 *            the antibiotic's alias in {@code shig_ast_data}, as {@link ShigSql} declares it
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
			"shig_ast." + key + "_susceptibility",
			variable,
			csvValue,
			(dto, sir) -> setter.accept(dto, EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));
	}

	private static void readModeOfTransmission(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		ModeOfTransmission modeOfTransmission = FieldReaders.parseEnum(ModeOfTransmission.class, (String) row.get("mode_of_transmission"));

		dto.setModeOfTransmission(EpipulseModeOfTransmissionRef.codeFor(modeOfTransmission));
	}

	/**
	 * Maps one {@code Shigella} species to SHIG pathogen codes.
	 */
	private static void readPathogen(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		PathogenSpecie specie = FieldReaders.parseEnum(PathogenSpecie.class, (String) row.get("pathogen_specie"));
		String code = EpipulseShigPathogenRef.codeFor(specie);

		dto.setPathogen(code == null ? Collections.emptyList() : Collections.singletonList(code));
	}

	private static void readSpecimen(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		SampleMaterial sampleMaterial = FieldReaders.parseEnum(SampleMaterial.class, (String) row.get("diagnostic_specimen"));

		dto.setSpecimen(EpipulseLaboratoryMapper.mapSampleMaterialToEntericSpecimenCode(sampleMaterial));
	}
}
