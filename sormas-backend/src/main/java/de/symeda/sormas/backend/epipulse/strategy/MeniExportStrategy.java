/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.epipulse.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epidata.CaseImportedStatus;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.symptoms.SymptomState;

/**
 * Export strategy for MENI (Invasive Meningococcal Infection) disease exports.
 * MENI follows the EpiPulse metadata specification with 43 variables including:
 * - 28 common fields (demographic, case identification, hospitalization, outcome)
 * - MENI-specific fields:
 * - Serogroup (A/B/C/W/X/Y/Z/29E/NGA/OTH)
 * - IsolateId (repeatable)
 * - MainPathogenDetectionMethod (repeatable)
 * - SecondPathogenDetectionMethod (repeatable)
 * - Molecular typing: MLST (repeatable), PorA1, PorA2, FetVR
 * - ClinicalCriteria (MENI/MENISEPTI/SEPTI/PNEU/OTH)
 * - ImportedStatus, ReportedEMERTII
 * - AST: CIP, CTX_CFX, PEN, RIF (MENI-specific: Rifampicin)
 */
@Stateless
@LocalBean
public class MeniExportStrategy extends AbstractEpipulseDiseaseExportStrategy {

	private static final String COLLECTION_SPLIT_CHARACTER = "#";

	@Override
	protected String buildDiseaseExportQuery() {
		// Common CTEs + MENI-specific CTEs (include epidata fields for ImportedStatus)
		String ctes = sqlCteBuilder.joinCtes(
			sqlCteBuilder.buildVariablesCte(),
			sqlCteBuilder.buildConfigDataCte(),
			sqlCteBuilder.buildFilteredCasesCte(true),
			sqlCteBuilder.buildPreviousHospitalizationsCte(),
			sqlCteBuilder.buildSamplesCte(),
			sqlCteBuilder.buildPathogenTestsCte(),
			sqlCteBuilder.buildImmunizationsCte(),
			sqlCteBuilder.buildVaccinationsCte(),
			buildMeniSerogroupCte(),
			buildMeniDetectionMethodsCte(),
			buildMeniMolecularTypingCte(),
			buildMeniIsolateIdsCte(),
			buildMeniClinicalCriteriaCte(),
			buildMeniEpidataCte(),
			buildMeniVaccinationDetailsCte(),
			buildMeniAstDataCte());

		// Main SELECT clause
		return ctes + buildMeniSelectClause();
	}

	@Override
	protected void mapDiseaseSpecificFields(EpipulseDiseaseExportEntryDto dto, Object[] row, int startIndex) {
		int index = startIndex;

		// MENI-specific fields mapping

		// Field: DateOfOnset (from symptoms - already in common fields via symptom_onsetdate)
		// DateOfOnset is handled by common fields

		// Field: DateOfDiagnosis
		Date dateOfDiagnosis = (Date) row[++index];
		dto.setDateOfDiagnosis(dateOfDiagnosis);

		// Field: ClinicalCriteria symptoms
		String meningitisRaw = (String) row[++index];
		String septicaemiaRaw = (String) row[++index];
		String pneumoniaRaw = (String) row[++index];

		SymptomState meningitis = parseSymptomState(meningitisRaw);
		SymptomState septicaemia = parseSymptomState(septicaemiaRaw);
		SymptomState pneumonia = parseSymptomState(pneumoniaRaw);

		String clinicalCriteria = EpipulseLaboratoryMapper.mapSymptomsToClinicalCriteriaMeni(meningitis, septicaemia, pneumonia);
		dto.setClinicalCriteria(clinicalCriteria);

		// Field: Serogroup
		String serogroupRaw = (String) row[++index];
		if (!StringUtils.isBlank(serogroupRaw)) {
			dto.setSerogroup(EpipulseLaboratoryMapper.normalizeSerogroupForMeni(serogroupRaw));
		}

		// Field: IsolateIds (aggregated/repeatable)
		String isolateIdsRaw = (String) row[++index];
		dto.setIsolateIds(parseStringList(isolateIdsRaw));

		// Field: MainPathogenDetectionMethods (aggregated/repeatable)
		String mainDetectionMethodsRaw = (String) row[++index];
		dto.setMainPathogenDetectionMethods(parseAndMapDetectionMethods(mainDetectionMethodsRaw));

		// Field: SecondPathogenDetectionMethods (aggregated/repeatable)
		String secondDetectionMethodsRaw = (String) row[++index];
		dto.setSecondPathogenDetectionMethods(parseAndMapDetectionMethods(secondDetectionMethodsRaw));

		// Field: MLST results (aggregated/repeatable)
		String mlstResultsRaw = (String) row[++index];
		dto.setResultMlst(parseAndMapMlstResults(mlstResultsRaw));

		// Field: PorA1
		String porA1Raw = (String) row[++index];
		dto.setResultPorA1(porA1Raw);

		// Field: PorA2
		String porA2Raw = (String) row[++index];
		dto.setResultPorA2(porA2Raw);

		// Field: FetVR
		String fetVRRaw = (String) row[++index];
		dto.setResultFetVR(fetVRRaw);

		// Field: ImportedStatus
		String importedStatusRaw = (String) row[++index];
		if (!StringUtils.isBlank(importedStatusRaw)) {
			try {
				CaseImportedStatus importedStatus = CaseImportedStatus.valueOf(importedStatusRaw);
				dto.setImportedStatus(EpipulseLaboratoryMapper.mapMeniImportedStatus(importedStatus));
			} catch (IllegalArgumentException e) {
				logger.warn("Invalid CaseImportedStatus value '{}', treating as null", importedStatusRaw);
			}
		}

		// Field: ReportedEMERTII
		Boolean reportedEmertii = (Boolean) row[++index];
		dto.setReportedEmertii(reportedEmertii);

		// Field: DateOfLastVaccination
		Date dateOfLastVaccination = (Date) row[++index];
		dto.setDateOfLastVaccination(dateOfLastVaccination);

		// Fields: PlaceOfInfection (aggregated/repeatable) - uses existing placeOfInfection field
		String placeOfInfectionRaw = (String) row[++index];
		dto.setPlaceOfInfection(parseStringList(placeOfInfectionRaw));

		// AST Fields - MENI uses CIP, CTX_CFX, PEN, RIF
		// MIC signs are derived from values based on dilution range boundaries

		// CIP (Ciprofloxacin) — MIC is free text since #14036; parse its numeric part
		Float micValueCIP = EpipulseLaboratoryMapper.parseMicValue((String) row[++index]);
		dto.setMicSign_CIP(EpipulseLaboratoryMapper.mapMicSign(micValueCIP));
		dto.setMicValueAST_CIP(micValueCIP != null ? micValueCIP.doubleValue() : null);

		String sirCIPRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirCIPRaw)) {
			dto.setSir_CIP(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirCIPRaw));
		}

		// CTX/CFX (Ceftriaxone/Cefotaxime)
		Float micValueCTX = EpipulseLaboratoryMapper.parseMicValue((String) row[++index]);
		dto.setMicSign_CTX_CFX(EpipulseLaboratoryMapper.mapMicSign(micValueCTX));
		dto.setMicValueAST_CTX_CFX(micValueCTX != null ? micValueCTX.doubleValue() : null);

		String sirCTXRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirCTXRaw)) {
			dto.setSir_CTX_CFX(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirCTXRaw));
		}

		// PEN (Penicillin)
		Float micValuePEN = EpipulseLaboratoryMapper.parseMicValue((String) row[++index]);
		dto.setMicSign_PEN(EpipulseLaboratoryMapper.mapMicSign(micValuePEN));
		dto.setMicValueAST_PEN(micValuePEN != null ? micValuePEN.doubleValue() : null);

		String sirPENRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirPENRaw)) {
			dto.setSir_PEN(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirPENRaw));
		}

		// RIF (Rifampicin) - MENI-specific
		Float micValueRIF = EpipulseLaboratoryMapper.parseMicValue((String) row[++index]);
		dto.setMicSign_RIF(EpipulseLaboratoryMapper.mapMicSign(micValueRIF));
		dto.setMicValueAST_RIF(micValueRIF != null ? micValueRIF.doubleValue() : null);

		String sirRIFRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirRIFRaw)) {
			dto.setSir_RIF(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirRIFRaw));
		}
	}

	@Override
	protected void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {
		int maxIsolateIds = 0;
		int maxMainDetectionMethods = 0;
		int maxSecondDetectionMethods = 0;
		int maxResultMlst = 0;
		int maxPlaceOfInfection = 0;

		for (EpipulseDiseaseExportEntryDto entry : entries) {
			if (entry.getIsolateIds() != null) {
				maxIsolateIds = Math.max(maxIsolateIds, entry.getIsolateIds().size());
			}
			if (entry.getMainPathogenDetectionMethods() != null) {
				maxMainDetectionMethods = Math.max(maxMainDetectionMethods, entry.getMainPathogenDetectionMethods().size());
			}
			if (entry.getSecondPathogenDetectionMethods() != null) {
				maxSecondDetectionMethods = Math.max(maxSecondDetectionMethods, entry.getSecondPathogenDetectionMethods().size());
			}
			if (entry.getResultMlst() != null) {
				maxResultMlst = Math.max(maxResultMlst, entry.getResultMlst().size());
			}
			if (entry.getPlaceOfInfection() != null) {
				maxPlaceOfInfection = Math.max(maxPlaceOfInfection, entry.getPlaceOfInfection().size());
			}
		}

		// Ensure at least 1 column for each repeatable field
		result.setMaxIsolateIds(Math.max(1, maxIsolateIds));
		result.setMaxMainPathogenDetectionMethods(Math.max(1, maxMainDetectionMethods));
		result.setMaxSecondPathogenDetectionMethods(Math.max(1, maxSecondDetectionMethods));
		result.setMaxResultMlst(Math.max(1, maxResultMlst));
		result.setMaxPlaceOfInfection(Math.max(1, maxPlaceOfInfection));
	}

	// MENI-specific CTE builders

	/**
	 * CTE for serogroup data from pathogen tests.
	 * Uses SEROGROUPING or SLIDE_AGGLUTINATION test types.
	 */
	private String buildMeniSerogroupCte() {
		//@formatter:off
		return "meni_serogroup AS (" +
			   "    SELECT c.id as case_id," +
			   "           (SELECT pt.typingid " +
			   "            FROM samples s " +
			   "            JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "            WHERE s.associatedcase_id = c.id " +
			   "              AND s.deleted = false " +
			   "              AND pt.testtype IN ('SEROGROUPING', 'SLIDE_AGGLUTINATION') " +
			   "              AND pt.typingid IS NOT NULL " +
			   "            ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC " +
			   "            LIMIT 1) as serogroup " +
			   "    FROM filtered_cases c" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for aggregated detection methods from pathogen tests (repeatable).
	 */
	private String buildMeniDetectionMethodsCte() {
		//@formatter:off
		return "meni_detection_methods AS (" +
				"    SELECT c.id as case_id," +
				"           STRING_AGG(DISTINCT CAST(pt.testtype AS text), '#' ORDER BY CAST(pt.testtype AS text)) " +
				"               FILTER (WHERE pt.testresult = 'POSITIVE') as main_detection_methods," +
				"           STRING_AGG(DISTINCT CAST(pt.testtype AS text), '#' ORDER BY CAST(pt.testtype AS text)) " +
				"               FILTER (WHERE pt.testresult IS NOT NULL AND pt.testresult <> 'POSITIVE') as second_detection_methods " +
				"    FROM filtered_cases c " +
				"    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
				"    LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
				"    GROUP BY c.id" +
				")";
		//@formatter:on
	}

	/**
	 * CTE for molecular typing results (MLST, PorA1, PorA2, FetVR).
	 * <p>
	 * Note: SORMAS doesn't have dedicated PorA/FetA test types. These typing results
	 * are typically stored in the typingid field of MULTILOCUS_SEQUENCE_TYPING,
	 * SEQUENCING, or WHOLE_GENOME_SEQUENCING tests. The extraction logic uses
	 * pattern matching on typingid values:
	 * - PorA1: values starting with "P1." (e.g., P1.5, P1.7)
	 * - PorA2: numeric values or values not starting with P1/F (e.g., 2, 4, 10)
	 * - FetA: values starting with "F" (e.g., F1-5, F3-3)
	 */
	private String buildMeniMolecularTypingCte() {
		//@formatter:off
		return "meni_molecular_typing AS (" +
			   "    SELECT c.id as case_id," +
			   "           STRING_AGG(DISTINCT CASE WHEN pt.testtype = 'MULTILOCUS_SEQUENCE_TYPING' THEN pt.typingid END, '#') as mlst_results," +
			   // PorA1: actual value if found, NST if test was done but indeterminate
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid LIKE 'P1.%' THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as pora1," +
			   // PorA2: actual value if found, NST if test was done but indeterminate
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid ~ '^[0-9]+$' OR (pt.typingid NOT LIKE 'P1.%' AND pt.typingid NOT LIKE 'F%' AND pt.typingid NOT LIKE 'ST-%' AND pt.typingid IS NOT NULL) THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as pora2," +
			   // FetVR: actual value if found, NST if test was done but indeterminate
			   "           COALESCE(" +
			   "               MAX(CASE WHEN pt.typingid LIKE 'F%' AND pt.typingid NOT LIKE 'FALSE' THEN pt.typingid END)," +
			   "               MAX(CASE WHEN pt.testtype IN ('SEQUENCING', 'WHOLE_GENOME_SEQUENCING') AND pt.testresult = 'INDETERMINATE' THEN 'NST' END)" +
			   "           ) as fetvr " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "        AND pt.testtype IN ('MULTILOCUS_SEQUENCE_TYPING', 'SEQUENCING', 'WHOLE_GENOME_SEQUENCING') " +
			   "    GROUP BY c.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for aggregated isolate IDs (repeatable).
	 * IsolateId corresponds to the EMERT II identifier - only includes non-empty labsampleid values.
	 */
	private String buildMeniIsolateIdsCte() {
		//@formatter:off
		return "meni_isolate_ids AS (" +
			   "    SELECT c.id as case_id," +
			   "           STRING_AGG(DISTINCT s.labsampleid, '#' ORDER BY s.labsampleid) as isolate_ids " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false AND s.labsampleid IS NOT NULL AND TRIM(s.labsampleid) != '' " +
			   "    GROUP BY c.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for clinical criteria from symptoms.
	 */
	private String buildMeniClinicalCriteriaCte() {
		//@formatter:off
		return "meni_clinical_criteria AS (" +
			   "    SELECT c.id as case_id," +
			   "           CAST(s.meningitis AS text) as meningitis," +
			   "           CAST(s.septicaemia AS text) as septicaemia," +
			   "           CAST(s.pneumoniaclinicalorradiologic AS text) as pneumonia " +
			   "    FROM filtered_cases c " +
			   "    JOIN symptoms s ON c.symptoms_id = s.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for epidemiology data (ImportedStatus, ReportedEMERTII, PlaceOfInfection).
	 * ReportedEMERTII is derived from the presence of non-empty isolate IDs (labsampleid) - if an isolate
	 * with a valid ID exists for the case, it indicates the isolate was reported to EMERT II.
	 */
	private String buildMeniEpidataCte() {
		//@formatter:off
		return "meni_epidata AS (" +
			   "    SELECT c.id as case_id," +
			   "           CAST(e.caseimportedstatus AS text) as imported_status," +
			   "           EXISTS(SELECT 1 FROM samples s WHERE s.associatedcase_id = c.id AND s.deleted = false AND s.labsampleid IS NOT NULL AND TRIM(s.labsampleid) != '') as reported_emertii," +
			   "           STRING_AGG(DISTINCT COALESCE(exp_region.nutscode, exp_country.nutscode), '#') as place_of_infection " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN epidata e ON c.epidata_id = e.id " +
			   "    LEFT JOIN exposures exp ON exp.epidata_id = e.id AND exp.typeofplace IS NOT NULL " +
			   "    LEFT JOIN location exp_loc ON exp.location_id = exp_loc.id " +
			   "    LEFT JOIN region exp_region ON exp_loc.region_id = exp_region.id " +
			   "    LEFT JOIN country exp_country ON exp_loc.country_id = exp_country.id " +
			   "    GROUP BY c.id, e.caseimportedstatus" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for vaccination details (DateOfLastVaccination).
	 */
	private String buildMeniVaccinationDetailsCte() {
		//@formatter:off
		return "meni_vaccination_details AS (" +
			   "    SELECT c.id as case_id," +
			   "           MAX(v.vaccinationdate) as date_of_last_vaccination " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN immunization i ON i.person_id = c.person_id " +
			   "        AND i.disease = 'INVASIVE_MENINGOCOCCAL_INFECTION' " +
			   "        AND i.meansofimmunization IN ('VACCINATION', 'VACCINATION_RECOVERY') " +
			   "    LEFT JOIN vaccination v ON v.immunization_id = i.id " +
			   "    GROUP BY c.id" +
			   ")";
		//@formatter:on
	}

	/**
	 * CTE for AST (Antimicrobial Susceptibility Testing) data from DrugSusceptibility.
	 * Retrieves the most recent antibiotic susceptibility test results for each case.
	 * <p>
	 * MENI uses these antibiotics:
	 * - CIP: Ciprofloxacin
	 * - CTX/CFX: Ceftriaxone
	 * - PEN: Penicillin
	 * - RIF: Rifampicin
	 */
	private String buildMeniAstDataCte() {
		//@formatter:off
		return "meni_ast_data AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           ds.ciprofloxacinmic," +
			   "           CAST(ds.ciprofloxacinsusceptibility AS text) as cip_susceptibility," +
			   "           ds.ceftriaxonemic," +
			   "           CAST(ds.ceftriaxonesusceptibility AS text) as ctx_susceptibility," +
			   "           ds.penicillinmic," +
			   "           CAST(ds.penicillinsusceptibility AS text) as pen_susceptibility," +
			   "           ds.rifampicinmic," +
			   "           CAST(ds.rifampicinsusceptibility AS text) as rif_susceptibility " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "        AND pt.testtype = 'ANTIBIOTIC_SUSCEPTIBILITY' " +
			   "    LEFT JOIN drugsusceptibility ds ON pt.drugsusceptibility_id = ds.id " +
			   "    ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC NULLS LAST, pt.id DESC" +
			   ")";
		//@formatter:on
	}

	/**
	 * Builds SELECT clause with all MENI-specific fields.
	 */
	private String buildMeniSelectClause() {
		StringBuilder select = new StringBuilder();
		//@formatter:off
		select.append("SELECT ")
		      .append(sqlCteBuilder.buildCommonSelectFields())
		      .append(",")
		      // MENI-specific fields
		      .append("       c.reportdate as date_of_diagnosis,") // Using report date as diagnosis date
		      .append("       meni_clin.meningitis,")
		      .append("       meni_clin.septicaemia,")
		      .append("       meni_clin.pneumonia,")
		      .append("       meni_sero.serogroup,")
		      .append("       meni_iso.isolate_ids,")
		      .append("       meni_detect.main_detection_methods,")
		      .append("       meni_detect.second_detection_methods,")
		      .append("       meni_mol.mlst_results,")
		      .append("       meni_mol.pora1,")
		      .append("       meni_mol.pora2,")
		      .append("       meni_mol.fetvr,")
		      .append("       meni_epi.imported_status,")
		      .append("       meni_epi.reported_emertii,")
		      .append("       meni_vax.date_of_last_vaccination,")
		      .append("       meni_epi.place_of_infection,")
		      // AST fields - CIP, CTX_CFX, PEN, RIF from drugsusceptibility table
		      // MIC signs are derived in Java using EUCAST breakpoint logic
		      .append("       meni_ast.ciprofloxacinmic as mic_cip,")
		      .append("       meni_ast.cip_susceptibility as sir_cip,")
		      .append("       meni_ast.ceftriaxonemic as mic_ctx,")
		      .append("       meni_ast.ctx_susceptibility as sir_ctx,")
		      .append("       meni_ast.penicillinmic as mic_pen,")
		      .append("       meni_ast.pen_susceptibility as sir_pen,")
		      .append("       meni_ast.rifampicinmic as mic_rif,")
		      .append("       meni_ast.rif_susceptibility as sir_rif ");

		select.append(sqlCteBuilder.buildCommonFromAndJoins())
		      .append(" LEFT JOIN meni_serogroup meni_sero ON meni_sero.case_id = c.id")
		      .append(" LEFT JOIN meni_detection_methods meni_detect ON meni_detect.case_id = c.id")
		      .append(" LEFT JOIN meni_molecular_typing meni_mol ON meni_mol.case_id = c.id")
		      .append(" LEFT JOIN meni_isolate_ids meni_iso ON meni_iso.case_id = c.id")
		      .append(" LEFT JOIN meni_clinical_criteria meni_clin ON meni_clin.case_id = c.id")
		      .append(" LEFT JOIN meni_epidata meni_epi ON meni_epi.case_id = c.id")
		      .append(" LEFT JOIN meni_vaccination_details meni_vax ON meni_vax.case_id = c.id")
		      .append(" LEFT JOIN meni_ast_data meni_ast ON meni_ast.case_id = c.id");

		select.append(" ORDER BY c.reportdate");
		//@formatter:on

		return select.toString();
	}

	// Helper methods

	private SymptomState parseSymptomState(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return SymptomState.valueOf(value);
		} catch (IllegalArgumentException e) {
			// Handle integer values (0=NO, 1=YES, 2=UNKNOWN) stored in some databases
			switch (value) {
			case "0":
				return SymptomState.NO;
			case "1":
				return SymptomState.YES;
			case "2":
				return SymptomState.UNKNOWN;
			default:
				logger.warn("Invalid SymptomState value '{}', treating as null", value);
				return null;
			}
		}
	}

	private List<String> parseStringList(String aggregatedString) {
		if (StringUtils.isBlank(aggregatedString)) {
			return new ArrayList<>();
		}
		return new ArrayList<>(Arrays.asList(aggregatedString.split(COLLECTION_SPLIT_CHARACTER)));
	}

	private List<String> parseAndMapDetectionMethods(String aggregatedString) {
		if (StringUtils.isBlank(aggregatedString)) {
			return new ArrayList<>();
		}
		List<String> methods = new ArrayList<>();
		for (String method : aggregatedString.split(COLLECTION_SPLIT_CHARACTER)) {
			String mapped = EpipulseLaboratoryMapper.mapToMeniDetectionMethod(method);
			if (mapped != null && !methods.contains(mapped)) {
				methods.add(mapped);
			}
		}
		return methods;
	}

	private List<String> parseAndMapMlstResults(String aggregatedString) {
		if (StringUtils.isBlank(aggregatedString)) {
			return new ArrayList<>();
		}
		List<String> results = new ArrayList<>();
		for (String result : aggregatedString.split(COLLECTION_SPLIT_CHARACTER)) {
			String normalized = EpipulseLaboratoryMapper.normalizeMlstForMeni(result);
			if (normalized != null && !results.contains(normalized)) {
				results.add(normalized);
			}
		}
		return results;
	}
}
