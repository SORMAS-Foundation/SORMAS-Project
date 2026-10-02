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

import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseMeningococcalSerogroupRef;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulsePlaceOfInfectionSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;
import de.symeda.sormas.backend.epipulse.sql.MeniSql;

/**
 * MENI (Invasive Meningococcal Infection) - four disease CTEs, vaccination/place-of-infection, AST panel.
 * 
 * PlaceOfInfection floored (unlike MEAS groups). Five blank columns: MainPathogenDetectionMethod
 * (specimen rank not recordable), SecondPathogenDetectionMethod (same), IsolateId (sample ID is not isolate),
 * ReportedEMERTII (no submission tracking), ResultMLST.
 */
final class MeniFieldModel {

	private MeniFieldModel() {
	}

	/**
	 * The field list below is the CSV side. The SQL side -- the CTEs that produce the columns it
	 * selects, and the joins and aliases they are read through -- is {@link MeniSql}. A select
	 * expression here such as an alias-qualified column is declared there.
	 */
	static FieldModel create() {
		return new FieldModel(
			fields(),
			MeniSql.spec()
				.plus(EpipulseVaccinationSql.spec())
				.plus(EpipulsePlaceOfInfectionSql.spec(EpipulsePlaceOfInfectionSql.Rule.IMPORTED_COUNTRY)),
			0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = CommonFields.defs();
		VaccinationFields.addVaccinationFields(fields);

		fields.add(CommonFields.DATE_OF_ONSET);

		fields.add(CommonFields.DATE_OF_DIAGNOSIS);

		// --- clinical criteria: the symptom columns collapse into one CSV column, shared with PNEU
		// and with no other subject code ---
		InvasiveClinicalCriteriaFields.addClinicalCriteria(fields, InvasiveClinicalCriteriaFields.MENI_PNEUMONIA_CODE);

		SerogroupFields.addSerogroup(fields, "meni_sero.serogroup", EpipulseMeningococcalSerogroupRef::codeFor);

		addDetectionMethodBlanks(fields);

		fields.add(
			ValueDef.direct(
				"pora1",
				"meni_mol.pora1",
				(dto, row) -> dto.setResultPorA1((String) row.get("pora1")),
				EpipulseVariable.RESULT_POR_A1,
				EpipulseMapping::resultPorA1));
		fields.add(
			ValueDef.direct(
				"pora2",
				"meni_mol.pora2",
				(dto, row) -> dto.setResultPorA2((String) row.get("pora2")),
				EpipulseVariable.RESULT_POR_A2,
				EpipulseMapping::resultPorA2));
		fields.add(
			ValueDef.direct(
				"fetvr",
				"meni_mol.fetvr",
				(dto, row) -> dto.setResultFetVR((String) row.get("fetvr")),
				EpipulseVariable.RESULT_FET_VR,
				EpipulseMapping::resultFetVR));

		// REVIEW - ResultMLST: the multilocus sequence typing clonal complex of the strain, from the
		// same neisseria.org scheme as the three columns above and read from the same free-text
		// typingid.
		fields.add(ValueDef.blank(EpipulseVariable.RESULT_MLST));

		addIsolateBlanks(fields);

		fields.add(
			ValueDef.direct(
				"imported_status",
				"meni_epi.imported_status",
				FieldReaders.importedStatus("imported_status"),
				EpipulseVariable.IMPORTED_STATUS,
				EpipulseMapping::importedStatus));

		PlaceOfInfectionFields.addPlaceOfInfectionSource(fields);
		fields.add(ValueDef.repeated(EpipulseVariable.PLACE_OF_INFECTION, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.placeOfInfection(dto))));

		fields.add(CommonFields.DATE_OF_LAST_VACCINATION);
		fields.add(CommonFields.vaccinationStatus(4));

		AstFields.addMic(
			fields,
			"cip",
			"meni_ast.ciprofloxacinmic",
			EpipulseVariable.MIC_SIGN_CIP,
			EpipulseVariable.MIC_VALUE_AST_CIP,
			EpipulseMapping::micSign_CIP,
			EpipulseMapping::micValueAST_CIP,
			(dto, mic) -> {
				dto.setMicSign_CIP(mic.getSign());
				dto.setMicValueAST_CIP(mic.getValue());
			});

		AstFields.addSir(
			fields,
			"cip",
			"meni_ast.cip_susceptibility",
			EpipulseVariable.SIR_CIP,
			EpipulseMapping::sir_CIP,
			(dto, sir) -> dto.setSir_CIP(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		AstFields.addMic(
			fields,
			"ctx",
			"meni_ast.ceftriaxonemic",
			EpipulseVariable.MIC_SIGN_CTX_CFX,
			EpipulseVariable.MIC_VALUE_AST_CTX_CFX,
			EpipulseMapping::micSign_CTX_CFX,
			EpipulseMapping::micValueAST_CTX_CFX,
			(dto, mic) -> {
				dto.setMicSign_CTX_CFX(mic.getSign());
				dto.setMicValueAST_CTX_CFX(mic.getValue());
			});

		AstFields.addSir(
			fields,
			"ctx",
			"meni_ast.ctx_susceptibility",
			EpipulseVariable.SIR_CTX_CFX,
			EpipulseMapping::sir_CTX_CFX,
			(dto, sir) -> dto.setSir_CTX_CFX(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		AstFields.addMic(
			fields,
			"pen",
			"meni_ast.penicillinmic",
			EpipulseVariable.MIC_SIGN_PEN,
			EpipulseVariable.MIC_VALUE_AST_PEN,
			EpipulseMapping::micSign_PEN,
			EpipulseMapping::micValueAST_PEN,
			(dto, mic) -> {
				dto.setMicSign_PEN(mic.getSign());
				dto.setMicValueAST_PEN(mic.getValue());
			});

		AstFields.addSir(
			fields,
			"pen",
			"meni_ast.pen_susceptibility",
			EpipulseVariable.SIR_PEN,
			EpipulseMapping::sir_PEN,
			(dto, sir) -> dto.setSir_PEN(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		AstFields.addMic(
			fields,
			"rif",
			"meni_ast.rifampicinmic",
			EpipulseVariable.MIC_SIGN_RIF,
			EpipulseVariable.MIC_VALUE_AST_RIF,
			EpipulseMapping::micSign_RIF,
			EpipulseMapping::micValueAST_RIF,
			(dto, mic) -> {
				dto.setMicSign_RIF(mic.getSign());
				dto.setMicValueAST_RIF(mic.getValue());
			});

		AstFields.addSir(
			fields,
			"rif",
			"meni_ast.rif_susceptibility",
			EpipulseVariable.SIR_RIF,
			EpipulseMapping::sir_RIF,
			(dto, sir) -> dto.setSir_RIF(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		return fields;
	}

	/**
	 * {@code MainPathogenDetectionMethod} and {@code SecondPathogenDetectionMethod}, declared and
	 * blank.
	 *
	 * <p>
	 * EpiPulse splits these two by <em>specimen</em>: the main one is the method used on "the primary
	 * laboratory specimen with a positive result", the second the method used on "the second type of
	 * laboratory specimen with a positive result (if taken)".
	 *
	 * <p>
	 * They were derived, from a {@code meni_detection_methods} CTE that split the case's test types
	 * by <em>result</em> instead: positive tests to the main column, everything with a non-null
	 * non-positive result to the second. That is a different question from the one asked, and it put
	 * a negative test's technique in a column the spec defines as carrying a positive one. The reader
	 * had already been stubbed out to return nothing, so the columns were blank in fact while looking
	 * derived; this states it instead. The CTE is deleted rather than left unread -- reviving it is
	 * not the fix, since the split it performs is the wrong one.
	 */
	private static void addDetectionMethodBlanks(List<ValueDef> fields) {

		// REVIEW - needs implementation after behavior confirmation
		fields.add(ValueDef.blank(EpipulseVariable.MAIN_PATHOGEN_DETECTION_METHOD));

		// REVIEW - needs implementation after behavior confirmation
		fields.add(ValueDef.blank(EpipulseVariable.SECOND_PATHOGEN_DETECTION_METHOD));
	}

	/**
	 * {@code IsolateId} and {@code ReportedEMERTII}, declared and blank. Both ask about the isolate which is not SORMAS record.
	 */
	private static void addIsolateBlanks(List<ValueDef> fields) {

		fields.add(ValueDef.blank(EpipulseVariable.ISOLATE_ID));
		fields.add(ValueDef.blank(EpipulseVariable.REPORTED_EMERTII));
	}
}
