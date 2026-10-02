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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseAstMethodRef;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.therapy.SusceptibilityMethod;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseVaccinationSql;
import de.symeda.sormas.backend.epipulse.sql.PneuSql;

/**
 * Field model for PNEU.
 *
 * <p>
 * Keeps detailed vaccination columns declared but blank for now; shared vaccination aggregates are
 * still used for status/last-date fields.
 */
final class PneuFieldModel {

	private PneuFieldModel() {
	}

	/**
	 * Builds PNEU fields with disease and vaccination SQL fragments. No pathogen-test fragment:
	 * PNEU's PathogenDetectionMethod is the serotyping technique from {@link PneuSql}.
	 */
	static FieldModel create() {
		return new FieldModel(fields(), PneuSql.spec().plus(EpipulseVaccinationSql.spec()), 0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = CommonFields.defs();
		VaccinationFields.addVaccinationFields(fields);

		fields.add(
			ValueDef.direct(
				"nrl_data",
				"nrl.nrl_data",
				(dto, row) -> dto.setNrlData((Boolean) row.get("nrl_data")),
				EpipulseVariable.NRL_DATA,
				EpipulseMapping::nrlData));

		fields.add(CommonFields.DATE_OF_DIAGNOSIS);

		// the symptom columns collapse into one CSV column, shared with MENI and with no other
		// subject code
		InvasiveClinicalCriteriaFields.addClinicalCriteria(fields, InvasiveClinicalCriteriaFields.PNEU_PNEUMONIA_CODE);

		addSerotypingMethods(fields);

		// REVIEW: Serotype: the pneumococcal serotype the case was typed as. SORMAS has the field,
		// pathogentest.serotype, but the Serotype enum carries Dengue and Yersiniosis constants
		// only.
		fields.add(ValueDef.blank(EpipulseVariable.SEROTYPE));

		fields.add(DATE_OF_LAST_VACCINATION);
		fields.add(CommonFields.vaccinationStatus(CommonFields.STANDARD_MAX_DOSES));

		addVaccinationDetailBlank(fields);

		// --- AST: each MIC column yields both a sign and a value ---
		addAstMethod(fields);

		AstFields.addMic(
			fields,
			"ctx",
			"pneu_ast.ceftriaxonemic",
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
			"pneu_ast.ctx_susceptibility",
			EpipulseVariable.SIR_CTX_CFX,
			EpipulseMapping::sir_CTX_CFX,
			(dto, sir) -> dto.setSir_CTX_CFX(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		AstFields.addMic(
			fields,
			"ery",
			"pneu_ast.erythromycinmic",
			EpipulseVariable.MIC_SIGN_ERY,
			EpipulseVariable.MIC_VALUE_AST_ERY,
			EpipulseMapping::micSign_ERY,
			EpipulseMapping::micValueAST_ERY,
			(dto, mic) -> {
				dto.setMicSign_ERY(mic.getSign());
				dto.setMicValueAST_ERY(mic.getValue());
			});

		AstFields.addSir(
			fields,
			"ery",
			"pneu_ast.ery_susceptibility",
			EpipulseVariable.SIR_ERY,
			EpipulseMapping::sir_ERY,
			(dto, sir) -> dto.setSir_ERY(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		AstFields.addMic(
			fields,
			"pen",
			"pneu_ast.penicillinmic",
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
			"pneu_ast.pen_susceptibility",
			EpipulseVariable.SIR_PEN,
			EpipulseMapping::sir_PEN,
			(dto, sir) -> dto.setSir_PEN(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sir)));

		return fields;
	}

	/**
	 * Adds declared per-dose vaccination detail columns as blanks.
	 */
	private static void addVaccinationDetailBlank(List<ValueDef> fields) {

		fields.add(ValueDef.blank(EpipulseVariable.VACCINE));

		fields.add(ValueDef.blank(EpipulseVariable.DOSE_PCV1));
		fields.add(ValueDef.blank(EpipulseVariable.DOSE_PCV2));
		fields.add(ValueDef.blank(EpipulseVariable.DOSE_PCV3));
		fields.add(ValueDef.blank(EpipulseVariable.DOSE_PCV4));

		fields.add(ValueDef.blank(EpipulseVariable.DATE_PCV1));
		fields.add(ValueDef.blank(EpipulseVariable.DATE_PCV2));
		fields.add(ValueDef.blank(EpipulseVariable.DATE_PCV3));
		fields.add(ValueDef.blank(EpipulseVariable.DATE_PCV4));

		fields.add(ValueDef.blank(EpipulseVariable.BRAND_PCV1));
		fields.add(ValueDef.blank(EpipulseVariable.BRAND_PCV2));
		fields.add(ValueDef.blank(EpipulseVariable.BRAND_PCV3));
		fields.add(ValueDef.blank(EpipulseVariable.BRAND_PCV4));

		fields.add(ValueDef.blank(EpipulseVariable.PCV_DOSES));

		fields.add(ValueDef.blank(EpipulseVariable.DOSE_PPV));
		fields.add(ValueDef.blank(EpipulseVariable.DATE_PPV));
		fields.add(ValueDef.blank(EpipulseVariable.PPV_DOSES));
	}

	/**
	 * Adds repeatable serotyping methods used for {@code PathogenDetectionMethod}.
	 */
	private static void addSerotypingMethods(List<ValueDef> fields) {

		fields.add(
			ValueDef.sqlOnly(
				"serotyping_methods",
				"pneu_sero.serotyping_methods",
				(dto, row) -> dto.setSerotypingMethods(parseSerotypingMethods((String) row.get("serotyping_methods")))));

		fields.add(
			ValueDef.repeated(EpipulseVariable.PATHOGEN_DETECTION_METHOD, dto -> ValueDef.atLeastOneColumn(EpipulseMapping.serotypingMethods(dto))));
	}

	/**
	 * Parses aggregated serotyping methods from {@code #}-separated SQL output.
	 */
	static List<SerotypingMethod> parseSerotypingMethods(String dbSerotypingMethodStr) {

		List<SerotypingMethod> methods = new ArrayList<>();

		for (String methodStr : FieldReaders.splitCollection(dbSerotypingMethodStr)) {

			try {
				methods.add(SerotypingMethod.valueOf(methodStr));
			} catch (IllegalArgumentException e) {
				// not a technique this version knows; see the javadoc
			}
		}

		return methods;
	}

	/**
	 * Adds AST method using the per-drug methods collapsed to one case-level value.
	 */
	private static void addAstMethod(List<ValueDef> fields) {

		fields.add(FieldReaders.addressable("method_ctx", "pneu_astm.ctx_method"));
		fields.add(FieldReaders.addressable("method_ery", "pneu_astm.ery_method"));
		fields.add(
			ValueDef.sqlOnly(
				"method_pen",
				"pneu_astm.pen_method",
				(dto, row) -> dto.setAstMethod(
					EpipulseAstMethodRef
						.resolve(Arrays.asList(readMethod(row, "method_ctx"), readMethod(row, "method_ery"), readMethod(row, "method_pen"))))));
		fields.add(ValueDef.calculated(EpipulseVariable.AST_METHOD, EpipulseMapping::astMethod));
	}

	/**
	 * One drug's recorded method, or {@code null} when it has none -- which
	 * {@link EpipulseAstMethodRef#resolve} skips rather than treats as a disagreement.
	 */
	private static SusceptibilityMethod readMethod(EpipulseRow row, String alias) {
		return FieldReaders.parseEnum(SusceptibilityMethod.class, (String) row.get(alias));
	}
}
