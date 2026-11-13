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

import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.epipulse.EpipulseMapping;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCode;
import de.symeda.sormas.api.epipulse.EpipulseSubjectCodeVariables;
import de.symeda.sormas.api.epipulse.EpipulseVariable;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubeHivStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubePathogenRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubePrevTreatmentCompletionRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseTubeSiteOfTbRef;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.epipulse.fieldmodel.EpipulseRow;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;
import de.symeda.sormas.backend.epipulse.fieldmodel.ValueDef;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfBirthSql;
import de.symeda.sormas.backend.epipulse.sql.EpipulseCountryOfNationalitySql;
import de.symeda.sormas.backend.epipulse.sql.TubeSql;

/**
 * Field model for TUBE.
 *
 * <p>
 * TUBE uses dedicated clinical/laboratory derivations and only a subset of shared columns.
 * Outcome-month columns, {@code EnrolledToTreatment}, and {@code IsolateId} are declared but
 * intentionally blank.
 */
final class TubeFieldModel {

	private TubeFieldModel() {
	}

	/**
	 * Builds TUBE fields with the TB SQL plus the two country-of-origin joins.
	 */
	static FieldModel create() {

		return new FieldModel(fields(), TubeSql.spec().plus(EpipulseCountryOfBirthSql.spec()).plus(EpipulseCountryOfNationalitySql.spec()), 0);
	}

	private static List<ValueDef> fields() {

		List<ValueDef> fields = new ArrayList<>();

		CommonFields.addOperationalFields(fields);

		// Keep only shared columns declared for TUBE; TB-specific columns are added in metadata order.
		CommonFields.genericCsvDefs()
			.stream()
			.filter(field -> EpipulseSubjectCodeVariables.defines(EpipulseSubjectCode.TUBE, field.getVariable()))
			.forEach(fields::add);

		addOrigin(fields);
		addTreatmentOutcome(fields);
		addClinicalHistory(fields);
		addLaboratory(fields);
		addSusceptibilities(fields);

		return fields;
	}

	/**
	 * Origin block: reporting-country booleans, country columns, and date of entry.
	 */
	private static void addOrigin(List<ValueDef> fields) {

		// Both booleans compare the EpiPulse location codes of the countries to the one of the exporting server's country.
		fields.add(ValueDef.calculated(EpipulseVariable.BORN_REPORTING_COUNTRY, EpipulseMapping::bornReportingCountry));
		CountryOfBirthFields.addCountryOfBirth(fields);
		fields.add(ValueDef.calculated(EpipulseVariable.NATIONALITY_REPORTING_COUNTRY, EpipulseMapping::nationalityReportingCountry));
		CountryOfNationalityFields.addCountryOfNationality(fields);

		fields.add(
			ValueDef.direct(
				"person_entry_date",
				"cast(person.entrydate as date)",
				(dto, row) -> dto.setDateOfEntry((Date) row.get("person_entry_date")),
				EpipulseVariable.DATE_OF_ENTRY,
				EpipulseMapping::dateOfEntry));
	}

	/**
	 * The four treatment-outcome columns, all blank.
	 */
	private static void addTreatmentOutcome(List<ValueDef> fields) {

		// REVIEW - Outcome6Months: patient's first outcome at 6 months from the start of treatment.
		// SORMAS records CaseDataDto.outcome, which is the outcome at case report data and hass no link to the
		// start of treatment, so none of the four columns can be derived from it.
		fields.add(ValueDef.blank(EpipulseVariable.OUTCOME6_MONTHS));
		// Same reason as above.
		fields.add(ValueDef.blank(EpipulseVariable.OUTCOME12_MONTHS));
		// Same reason as above.
		fields.add(ValueDef.blank(EpipulseVariable.OUTCOME24_MONTHS));
		// Same reason as above.
		fields.add(ValueDef.blank(EpipulseVariable.OUTCOME36_MONTHS));
	}

	/**
	 * Site of disease, HIV status, and what is known about earlier episodes.
	 */
	private static void addClinicalHistory(List<ValueDef> fields) {

		fields.add(
			ValueDef.direct(
				"major_site",
				"symptom.majorsite",
				(dto, row) -> dto.setMajorSiteOfTB(
					EpipulseTubeSiteOfTbRef.codeForMajorSite(FieldReaders.parseEnum(InfectionSite.class, (String) row.get("major_site")))),
				EpipulseVariable.MAJOR_SITE_OF_TB,
				EpipulseMapping::majorSiteOfTB));

		fields.add(
			ValueDef.direct(
				"minor_site",
				"symptom.minorsite",
				(dto, row) -> dto.setMinorSiteOfTB(
					EpipulseTubeSiteOfTbRef.codeForMinorSite(FieldReaders.parseEnum(InfectionSite.class, (String) row.get("minor_site")))),
				EpipulseVariable.MINOR_SITE_OF_TB,
				EpipulseMapping::minorSiteOfTB));

		fields.add(
			ValueDef.direct(
				"hiv_status",
				"tube_health.hiv",
				(dto, row) -> dto
					.setHivStatus(EpipulseTubeHivStatusRef.codeFor(FieldReaders.parseEnum(YesNoUnknown.class, (String) row.get("hiv_status")))),
				EpipulseVariable.HIV_STATUS,
				EpipulseMapping::hivStatus));

		// postMortem is primitive boolean, so this derived value is never null.
		fields.add(
			ValueDef.direct(
				"post_mortem",
				"c.postmortem",
				(dto, row) -> dto.setDiagnosedAnteMortem(!Boolean.TRUE.equals(row.get("post_mortem"))),
				EpipulseVariable.DIAGNOSED_ANTE_MORTEM,
				EpipulseMapping::diagnosedAnteMortem));

		// One source year feeds both PrevDiagnosis and PrevDiagnosisYear.
		fields.add(
			ValueDef.sqlOnly(
				"tuberculosis_infection_year",
				"tube_health.tuberculosisinfectionyear",
				// int8, so the driver hands back a BigInteger rather than an Integer
				(dto, row) -> {
					Number year = (Number) row.get("tuberculosis_infection_year");
					dto.setTuberculosisInfectionYear(year == null ? null : year.intValue());
				}));
		fields.add(ValueDef.calculated(EpipulseVariable.PREV_DIAGNOSIS, EpipulseMapping::prevDiagnosis));
		fields.add(ValueDef.calculated(EpipulseVariable.PREV_DIAGNOSIS_YEAR, EpipulseMapping::prevDiagnosisYear));

		// REVIEW - EnrolledToTreatment: "patient started appropriate TB treatment according to
		// international recommendations. Note: For MDR-TB cases should be reported the start of
		// anti-TB treatment with second line drugs." c.treatmentstarted is too weak a statement
		// compared to that: it records that treatment began, not that it was the appropriate one,
		// and it names no drugs, so it cannot answer the second-line question at all.
		fields.add(ValueDef.blank(EpipulseVariable.ENROLLED_TO_TREATMENT));

		fields.add(
			ValueDef.direct(
				"previous_tuberculosis_treatment",
				"tube_health.previoustuberculosistreatment",
				FieldReaders.yesNoUnknown("previous_tuberculosis_treatment", EpipulseDiseaseExportEntryDto::setPrevTreatment),
				EpipulseVariable.PREV_TREATMENT,
				EpipulseMapping::prevTreatment));

		fields.add(
			ValueDef.direct(
				"compliance_with_treatment",
				"tube_health.compliancewithtreatment",
				(dto, row) -> dto.setPrevTreatmentCompletion(
					EpipulseTubePrevTreatmentCompletionRef
						.codeFor(FieldReaders.parseEnum(ComplianceWithTreatment.class, (String) row.get("compliance_with_treatment")))),
				EpipulseVariable.PREV_TREATMENT_COMPLETION,
				EpipulseMapping::prevTreatmentCompletion));
	}

	/**
	 * Isolate, species and the three test-result columns.
	 *
	 * <p>
	 * Result columns summarize verified tests by family and use {@code NOTEST} when none qualifies.
	 */
	private static void addLaboratory(List<ValueDef> fields) {

		// REVIEW - IsolateId: "this variable allows the linking of isolate records to the case
		// record; use the NationalRecordId of the isolate".
		fields.add(ValueDef.repeated(EpipulseVariable.ISOLATE_ID, dto -> ValueDef.atLeastOneColumn(Collections.emptyList())));

		fields.add(
			ValueDef.direct(
				"pathogen_specie",
				"tube_pathogen.pathogen_specie",
				TubeFieldModel::readPathogen,
				EpipulseVariable.PATHOGEN,
				EpipulseMapping::pathogenCode));

		fields.add(
			ValueDef.direct(
				"culture_result",
				"tube_culture.test_result",
				(dto, row) -> dto.setResultCulture((String) row.get("culture_result")),
				EpipulseVariable.RESULT_CULTURE,
				EpipulseMapping::resultCulture));

		fields.add(
			ValueDef.direct(
				"microscopy_result",
				"tube_microscopy.test_result",
				(dto, row) -> dto.setResultMicroscopyAFB((String) row.get("microscopy_result")),
				EpipulseVariable.RESULT_MICROSCOPY_AFB,
				EpipulseMapping::resultMicroscopyAFB));

		// ResultOtherTest is derived from nucleic-acid and histopathology summaries.
		fields.add(
			ValueDef.sqlOnly(
				"nucleic_acid_result",
				"tube_nucleic_acid.test_result",
				(dto, row) -> dto.setNucleicAcidResult((String) row.get("nucleic_acid_result"))));
		fields.add(
			ValueDef.sqlOnly(
				"histopathology_result",
				"tube_histopathology.test_result",
				(dto, row) -> dto.setHistopathologyResult((String) row.get("histopathology_result"))));
		fields.add(ValueDef.calculated(EpipulseVariable.RESULT_OTHER_TEST, EpipulseMapping::resultOtherTest));
	}

	/**
	 * The fourteen {@code SIR_*} columns, in EpiPulse's order.
	 */
	private static void addSusceptibilities(List<ValueDef> fields) {

		addSir(fields, "amk", EpipulseVariable.SIR_AMK, EpipulseMapping::sir_AMK, EpipulseDiseaseExportEntryDto::setSir_AMK);
		addSir(fields, "bdq", EpipulseVariable.SIR_BDQ, EpipulseMapping::sir_BDQ, EpipulseDiseaseExportEntryDto::setSir_BDQ);
		addSir(fields, "clf", EpipulseVariable.SIR_CLF, EpipulseMapping::sir_CLF, EpipulseDiseaseExportEntryDto::setSir_CLF);
		addSir(fields, "dlm", EpipulseVariable.SIR_DLM, EpipulseMapping::sir_DLM, EpipulseDiseaseExportEntryDto::setSir_DLM);
		addSir(fields, "eth", EpipulseVariable.SIR_ETH, EpipulseMapping::sir_ETH, EpipulseDiseaseExportEntryDto::setSir_ETH);
		addSir(fields, "eti", EpipulseVariable.SIR_ETI, EpipulseMapping::sir_ETI, EpipulseDiseaseExportEntryDto::setSir_ETI);
		addSir(fields, "inh", EpipulseVariable.SIR_INH, EpipulseMapping::sir_INH, EpipulseDiseaseExportEntryDto::setSir_INH);
		addSir(fields, "lnz", EpipulseVariable.SIR_LNZ, EpipulseMapping::sir_LNZ, EpipulseDiseaseExportEntryDto::setSir_LNZ);
		addSir(fields, "lvx", EpipulseVariable.SIR_LVX, EpipulseMapping::sir_LVX, EpipulseDiseaseExportEntryDto::setSir_LVX);
		addSir(fields, "mfx", EpipulseVariable.SIR_MFX, EpipulseMapping::sir_MFX, EpipulseDiseaseExportEntryDto::setSir_MFX);
		addSir(fields, "ptm", EpipulseVariable.SIR_PTM, EpipulseMapping::sir_PTM, EpipulseDiseaseExportEntryDto::setSir_PTM);
		addSir(fields, "pza", EpipulseVariable.SIR_PZA, EpipulseMapping::sir_PZA, EpipulseDiseaseExportEntryDto::setSir_PZA);
		addSir(fields, "rif", EpipulseVariable.SIR_RIF, EpipulseMapping::sir_RIF, EpipulseDiseaseExportEntryDto::setSir_RIF);
		addSir(fields, "str", EpipulseVariable.SIR_STR, EpipulseMapping::sir_STR, EpipulseDiseaseExportEntryDto::setSir_STR);
	}

	/**
	 * One antibiotic's {@code SIR_*} column from the AST panel selected by {@link TubeSql}.
	 *
	 * <p>
	 * Unlike shared {@code AstFields.addSir}, empty values are still mapped because TUBE declares
	 * {@code NOTEST}.
	 *
	 * @param key
	 *            the antibiotic's alias in {@code tube_ast_data}, as {@link TubeSql} declares it
	 */
	private static void addSir(
		List<ValueDef> fields,
		String key,
		EpipulseVariable variable,
		Function<EpipulseDiseaseExportEntryDto, String> csvValue,
		BiConsumer<EpipulseDiseaseExportEntryDto, String> setter) {

		String alias = "sir_" + key;

		fields.add(
			ValueDef.direct(
				alias,
				"tube_ast." + key + "_susceptibility",
				(dto, row) -> setter.accept(dto, EpipulseLaboratoryMapper.mapDrugSusceptibilityToTbSIR((String) row.get(alias))),
				variable,
				csvValue));
	}

	/**
	 * Maps one M. tuberculosis complex species to TUBE pathogen codes.
	 *
	 * <p>
	 * DTO stores {@code Pathogen} as a list; TUBE uses a single-element list (or empty list).
	 */
	private static void readPathogen(EpipulseDiseaseExportEntryDto dto, EpipulseRow row) {

		PathogenSpecie specie = FieldReaders.parseEnum(PathogenSpecie.class, (String) row.get("pathogen_specie"));
		String code = EpipulseTubePathogenRef.codeFor(specie);

		dto.setPathogen(code == null ? Collections.emptyList() : Collections.singletonList(code));
	}
}
