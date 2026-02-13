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

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.symptoms.SymptomState;

/**
 * Export strategy for PNEU (Invasive Pneumococcal Infection) disease exports.
 * PNEU follows the EpiPulse metadata specification with 56 total columns (28 common + 28 PNEU-specific).
 * <p>
 * Column breakdown:
 * - 28 common fields (demographic, case identification, hospitalization, outcome)
 * - 28 PNEU-specific fields:
 * - 1 NRL field (NRLData)
 * - 5 clinical/diagnostic fields (DateOfDiagnosis, meningitis, septicaemia, pneumonia -> ClinicalCriteria)
 * - 2 laboratory fields (PathogenDetectionMethod, Serotype)
 * - 11 PCV vaccination fields (DatePCV1-4, BrandPCV1-4, DosePCV1-4 derived, PcvDoses)
 * - 2 PPV vaccination fields (DatePPV, PpvDoses)
 * - 10 AST fields (AstMethod, MicSign/MicValue/SIR for CTX_CFX, ERY, PEN)
 */
@Stateless
@LocalBean
public class IpiExportStrategy extends AbstractEpipulseDiseaseExportStrategy {

	@Override
	protected String buildDiseaseExportQuery() {
		// Common CTEs + PNEU-specific CTEs (no epidata fields needed)
		String ctes = sqlCteBuilder.joinCtes(
			sqlCteBuilder.buildVariablesCte(),
			sqlCteBuilder.buildConfigDataCte(),
			sqlCteBuilder.buildFilteredCasesCte(false),
			sqlCteBuilder.buildPreviousHospitalizationsCte(),
			sqlCteBuilder.buildSamplesCte(),
			sqlCteBuilder.buildPathogenTestsCte(),
			sqlCteBuilder.buildImmunizationsCte(),
			sqlCteBuilder.buildVaccinationsCte(),
			sqlCteBuilder.buildNrlDataCte(),
			buildPneuSerotypingDataCte(),
			buildPneuPathogenDetectionMethodCte(),
			buildPneuClinicalCriteriaCte(),
			buildPneuVaccinationDetailsCte(),
			buildPneuAstDataCte());

		// Main SELECT clause with all 56 columns (28 common + 28 PNEU-specific)
		return ctes + buildPneuSelectClause();
	}

	/**
	 * Maps all 28 PNEU-specific fields from SQL result row to DTO.
	 * Common fields (28) are already mapped by parent class, giving 56 total columns.
	 */
	@Override
	protected void mapDiseaseSpecificFields(EpipulseDiseaseExportEntryDto dto, Object[] row, int startIndex) {
		int index = startIndex;

		// Field 29: NRLData (Boolean) - currently not tracked
		Boolean nrlData = (Boolean) row[++index];
		dto.setNrlData(nrlData);

		// Field 30: DateOfDiagnosis (Date)
		Date dateOfDiagnosis = (Date) row[++index];
		dto.setDateOfDiagnosis(dateOfDiagnosis);

		// Fields 31-33: Clinical criteria symptoms (for mapping to ClinicalCriteria)
		String meningitisRaw = (String) row[++index];
		String septicaemiaRaw = (String) row[++index];
		String pneumoniaRaw = (String) row[++index];

		SymptomState meningitis = parseSymptomState(meningitisRaw);
		SymptomState septicaemia = parseSymptomState(septicaemiaRaw);
		SymptomState pneumonia = parseSymptomState(pneumoniaRaw);

		// Map symptoms to ClinicalCriteria codes (BACTERPNEUMO, MENI, MENISEPTI, SEPTI)
		String clinicalCriteria = EpipulseLaboratoryMapper.mapSymptomsToClinicalCriteria(meningitis, septicaemia, pneumonia);
		dto.setClinicalCriteria(clinicalCriteria);

		// Field 34: PathogenDetectionMethod (String - test type)
		String detectionMethodRaw = (String) row[++index];
		if (!StringUtils.isBlank(detectionMethodRaw)) {
			String detectionMethod = EpipulseLaboratoryMapper.mapPathogenTestTypeToDetectionMethod(detectionMethodRaw);
			dto.setPathogenDetectionMethod(detectionMethod);
		}

		// Field 35: Serotype (String)
		String serotypeRaw = (String) row[++index];
		if (!StringUtils.isBlank(serotypeRaw)) {
			dto.setSerotype(EpipulseLaboratoryMapper.normalizeSerotypeForEpipulse(serotypeRaw));
		}

		// Fields 36-48: Vaccination details - PCV doses 1-4
		// DosePCVx is true if either date or brand is present (vaccinationdate can be null)
		Date datePCV1 = (Date) row[++index];
		dto.setDatePCV1(datePCV1);
		String brandPCV1Raw = (String) row[++index];
		dto.setDosePCV1(datePCV1 != null || !StringUtils.isBlank(brandPCV1Raw));
		if (!StringUtils.isBlank(brandPCV1Raw)) {
			dto.setBrandPCV1(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV1Raw));
		}

		Date datePCV2 = (Date) row[++index];
		dto.setDatePCV2(datePCV2);
		String brandPCV2Raw = (String) row[++index];
		dto.setDosePCV2(datePCV2 != null || !StringUtils.isBlank(brandPCV2Raw));
		if (!StringUtils.isBlank(brandPCV2Raw)) {
			dto.setBrandPCV2(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV2Raw));
		}

		Date datePCV3 = (Date) row[++index];
		dto.setDatePCV3(datePCV3);
		String brandPCV3Raw = (String) row[++index];
		dto.setDosePCV3(datePCV3 != null || !StringUtils.isBlank(brandPCV3Raw));
		if (!StringUtils.isBlank(brandPCV3Raw)) {
			dto.setBrandPCV3(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV3Raw));
		}

		Date datePCV4 = (Date) row[++index];
		dto.setDatePCV4(datePCV4);
		String brandPCV4Raw = (String) row[++index];
		dto.setDosePCV4(datePCV4 != null || !StringUtils.isBlank(brandPCV4Raw));
		if (!StringUtils.isBlank(brandPCV4Raw)) {
			dto.setBrandPCV4(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV4Raw));
		}

		// PCV dose count
		Number pcvDosesNum = (Number) row[++index];
		dto.setPcvDoses(pcvDosesNum != null ? pcvDosesNum.intValue() : null);

		// Fields 49-50: Vaccination details - PPV doses
		// DosePPV is true if date is present or ppvDoses count > 0
		Date datePPV = (Date) row[++index];
		dto.setDatePPV(datePPV);
		Number ppvDosesNum = (Number) row[++index];
		dto.setPpvDoses(ppvDosesNum != null ? ppvDosesNum.intValue() : null);
		dto.setDosePPV(datePPV != null || (ppvDosesNum != null && ppvDosesNum.intValue() > 0));

		// Set the summary Vaccine field from the most recent vaccine brand (PCV4 > PCV3 > PCV2 > PCV1 > PPV)
		String vaccineCode = determineVaccineCode(brandPCV4Raw, brandPCV3Raw, brandPCV2Raw, brandPCV1Raw);
		if (vaccineCode != null) {
			dto.setVaccine(vaccineCode);
		}

		// Fields 51-57: AST (Antimicrobial Susceptibility Testing) data
		// MIC signs are derived from values based on dilution range boundaries

		// AST Method (not stored in SORMAS currently)
		String astMethod = (String) row[++index];
		dto.setAstMethod(astMethod);

		// CTX/CFX (Ceftriaxone/Cefotaxime)
		Float micValueCTX = row[++index] != null ? ((Number) row[index]).floatValue() : null;
		dto.setMicSign_CTX_CFX(EpipulseLaboratoryMapper.mapMicSign(micValueCTX));
		dto.setMicValueAST_CTX_CFX(micValueCTX != null ? micValueCTX.doubleValue() : null);

		String sirCTXRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirCTXRaw)) {
			dto.setSir_CTX_CFX(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirCTXRaw));
		}

		// ERY (Erythromycin)
		Float micValueERY = row[++index] != null ? ((Number) row[index]).floatValue() : null;
		dto.setMicSign_ERY(EpipulseLaboratoryMapper.mapMicSign(micValueERY));
		dto.setMicValueAST_ERY(micValueERY != null ? micValueERY.doubleValue() : null);

		String sirERYRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirERYRaw)) {
			dto.setSir_ERY(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirERYRaw));
		}

		// PEN (Penicillin)
		Float micValuePEN = row[++index] != null ? ((Number) row[index]).floatValue() : null;
		dto.setMicSign_PEN(EpipulseLaboratoryMapper.mapMicSign(micValuePEN));
		dto.setMicValueAST_PEN(micValuePEN != null ? micValuePEN.doubleValue() : null);

		String sirPENRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirPENRaw)) {
			dto.setSir_PEN(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirPENRaw));
		}
	}

	@Override
	protected void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {
		// PNEU has no repeatable fields - all 56 columns are fixed (no dynamic column generation needed)
		// Common repeatable fields (pathogenTests, immunizations) are handled by parent class
	}

	// Helper methods for PNEU-specific CTEs (following metadata specification)

	/**
	 * CTE for serotype data from pathogen tests
	 */
	private String buildPneuSerotypingDataCte() {
		//@formatter:off
		return "pneu_serotyping AS (SELECT c.id as case_id," +
			   "                             (SELECT pt.typingid " +
			   "                              FROM samples s " +
			   "                              JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "                              WHERE s.associatedcase_id = c.id " +
			   "                                AND s.deleted = false " +
			   "                                AND pt.testtype IN ('SEROGROUPING', 'GENOTYPING', 'WHOLE_GENOME_SEQUENCING') " +
			   "                                AND pt.typingid IS NOT NULL " +
			   "                              ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC " +
			   "                              LIMIT 1) as serotype " +
			   "                      FROM filtered_cases c)";
		//@formatter:on
	}

	/**
	 * CTE for pathogen detection method from pathogen tests
	 */
	private String buildPneuPathogenDetectionMethodCte() {
		//@formatter:off
		return "pneu_detection_method AS (SELECT c.id as case_id," +
			   "                                  (SELECT CAST(pt.testtype AS text) " +
			   "                                   FROM samples s " +
			   "                                   JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "                                   WHERE s.associatedcase_id = c.id " +
			   "                                     AND s.deleted = false " +
			   "                                     AND pt.testresultverified = true " +
			   "                                   ORDER BY COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC " +
			   "                                   LIMIT 1) as detection_method " +
			   "                           FROM filtered_cases c)";
		//@formatter:on
	}

	/**
	 * CTE for clinical criteria from symptoms
	 */
	private String buildPneuClinicalCriteriaCte() {
		//@formatter:off
		return "pneu_clinical_criteria AS (SELECT c.id as case_id," +
			   "                                   CAST(s.meningitis AS text) as meningitis," +
			   "                                   CAST(s.septicaemia AS text) as septicaemia," +
			   "                                   CAST(s.pneumoniaclinicalorradiologic AS text) as pneumonia " +
			   "                            FROM filtered_cases c " +
			   "                            JOIN symptoms s ON c.symptoms_id = s.id)";
		//@formatter:on
	}

	/**
	 * CTE for detailed vaccination data (PCV1-4, PPV doses)
	 */
	private String buildPneuVaccinationDetailsCte() {
		// PCV vaccine name patterns: PCV, CONJUGATE, PREVENAR/PREVNAR, SYNFLORIX, VAXNEUVANCE
		// PPV vaccine name patterns: PPV, POLYSACCHARIDE, PNEUMOVAX
		//@formatter:off
        return "pneu_vaccination_details AS (" +
                "    SELECT vax.case_id," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 1 THEN vax.vaccinationdate END) as date_pcv1," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 1 THEN CAST(vax.vaccinename AS text) END) as brand_pcv1," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 2 THEN vax.vaccinationdate END) as date_pcv2," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 2 THEN CAST(vax.vaccinename AS text) END) as brand_pcv2," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 3 THEN vax.vaccinationdate END) as date_pcv3," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 3 THEN CAST(vax.vaccinename AS text) END) as brand_pcv3," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 4 THEN vax.vaccinationdate END) as date_pcv4," +
                "           MAX(CASE WHEN vax.is_pcv AND vax.pcv_row_num = 4 THEN CAST(vax.vaccinename AS text) END) as brand_pcv4," +
                "           SUM(CASE WHEN vax.is_pcv THEN 1 ELSE 0 END) as pcv_doses," +
                "           MAX(CASE WHEN vax.is_ppv AND vax.ppv_row_num = 1 THEN vax.vaccinationdate END) as date_ppv," +
                "           SUM(CASE WHEN vax.is_ppv THEN 1 ELSE 0 END) as ppv_doses " +
                "    FROM (" +
                "        SELECT c.id as case_id," +
                "               v.vaccinationdate," +
                "               v.vaccinename," +
                "               CASE " +
                "                 WHEN CAST(v.vaccinename AS text) ILIKE '%PCV%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%CONJUGATE%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%PREVENAR%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%PREVNAR%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%SYNFLORIX%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%VAXNEUVANCE%' THEN true " +
                "                 ELSE false " +
                "               END as is_pcv," +
                "               CASE " +
                "                 WHEN CAST(v.vaccinename AS text) ILIKE '%PPV%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%POLYSACCHARIDE%' " +
                "                   OR CAST(v.vaccinename AS text) ILIKE '%PNEUMOVAX%' THEN true " +
                "                 ELSE false " +
                "               END as is_ppv," +
                "               SUM(CASE WHEN CAST(v.vaccinename AS text) ILIKE '%PCV%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%CONJUGATE%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%PREVENAR%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%PREVNAR%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%SYNFLORIX%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%VAXNEUVANCE%' THEN 1 ELSE 0 END) " +
                "                   OVER (PARTITION BY c.id ORDER BY v.vaccinationdate) as pcv_row_num," +
                "               SUM(CASE WHEN CAST(v.vaccinename AS text) ILIKE '%PPV%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%POLYSACCHARIDE%' " +
                "                          OR CAST(v.vaccinename AS text) ILIKE '%PNEUMOVAX%' THEN 1 ELSE 0 END) " +
                "                   OVER (PARTITION BY c.id ORDER BY v.vaccinationdate) as ppv_row_num " +
                "        FROM filtered_cases c " +
                "        LEFT JOIN vaccination v ON v.immunization_id IN (SELECT i.id FROM immunization i WHERE i.person_id = c.person_id AND i.disease = 'INVASIVE_PNEUMOCOCCAL_INFECTION') " +
                "    ) vax " +
                "    GROUP BY vax.case_id)";
		//@formatter:on
	}

	/**
	 * CTE for AST (Antimicrobial Susceptibility Testing) data from DrugSusceptibility.
	 * Retrieves the most recent antibiotic susceptibility test results for each case.
	 * <p>
	 * PNEU uses these antibiotics:
	 * - CTX/CFX: Ceftriaxone
	 * - ERY: Erythromycin
	 * - PEN: Penicillin
	 */
	private String buildPneuAstDataCte() {
		//@formatter:off
		return "pneu_ast_data AS (" +
			   "    SELECT DISTINCT ON (c.id) c.id as case_id," +
			   "           ds.ceftriaxonemic," +
			   "           CAST(ds.ceftriaxonesusceptibility AS text) as ctx_susceptibility," +
			   "           ds.erythromycinmic," +
			   "           CAST(ds.erythromycinsusceptibility AS text) as ery_susceptibility," +
			   "           ds.penicillinmic," +
			   "           CAST(ds.penicillinsusceptibility AS text) as pen_susceptibility " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "        AND pt.testtype = 'ANTIBIOTIC_SUSCEPTIBILITY' " +
			   "    LEFT JOIN drugsusceptibility ds ON pt.drugsusceptibility_id = ds.id " +
			   "    ORDER BY c.id, COALESCE(pt.testdatetime, pt.reportdate, pt.creationdate) DESC" +
			   ")";
		//@formatter:on
	}

	/**
	 * Builds SELECT clause with all 56 columns (28 common + 28 PNEU-specific).
	 */
	private String buildPneuSelectClause() {
		StringBuilder select = new StringBuilder();
		//@formatter:off
		// Use common SELECT fields (28 fields) and append PNEU-specific fields (28 fields)
		select.append("SELECT ")
		      .append(sqlCteBuilder.buildCommonSelectFields())
		      .append(",")
		      // PNEU-specific fields start here
		      // Demographics: NRLData (field 29)
		      .append("       nrl.nrl_data,")
		      // Clinical/Diagnostic: DateOfDiagnosis, ClinicalCriteria (fields 30-31)
		      .append("       c.reportdate as date_of_diagnosis,") // Using report date as diagnosis date
		      .append("       pneu_clin.meningitis,")
		      .append("       pneu_clin.septicaemia,")
		      .append("       pneu_clin.pneumonia,")
		      // Laboratory: PathogenDetectionMethod, Serotype (fields 32-33)
		      .append("       pneu_detect.detection_method,")
		      .append("       pneu_sero.serotype,")
		      // Vaccination summary: DateOfLastVaccination, Vaccine, VaccinationStatus (fields 34-36)
		      // These come from common immunizations/vaccinations CTEs
		      // Vaccination details: PCV doses 1-4 (fields 37-48)
		      .append("       pneu_vax.date_pcv1,")
		      .append("       pneu_vax.brand_pcv1,")
		      .append("       pneu_vax.date_pcv2,")
		      .append("       pneu_vax.brand_pcv2,")
		      .append("       pneu_vax.date_pcv3,")
		      .append("       pneu_vax.brand_pcv3,")
		      .append("       pneu_vax.date_pcv4,")
		      .append("       pneu_vax.brand_pcv4,")
		      .append("       pneu_vax.pcv_doses,")
		      // Vaccination details: PPV doses (fields 49-50)
		      .append("       pneu_vax.date_ppv,")
		      .append("       pneu_vax.ppv_doses,")
		      // AST fields (fields 51-60) - from drugsusceptibility table
		      // MIC signs are derived in Java using mapMicSign() based on dilution range boundaries
		      .append("       NULL as ast_method,") // ASTMethod not stored in SORMAS
		      // CTX/CFX (Ceftriaxone/Cefotaxime)
		      .append("       pneu_ast.ceftriaxonemic as mic_ctx,")
		      .append("       pneu_ast.ctx_susceptibility as sir_ctx,")
		      // ERY (Erythromycin)
		      .append("       pneu_ast.erythromycinmic as mic_ery,")
		      .append("       pneu_ast.ery_susceptibility as sir_ery,")
		      // PEN (Penicillin)
		      .append("       pneu_ast.penicillinmic as mic_pen,")
		      .append("       pneu_ast.pen_susceptibility as sir_pen ");

		// Use common FROM and JOINs, then append PNEU-specific joins
		select.append(sqlCteBuilder.buildCommonFromAndJoins())
		      .append(" LEFT JOIN nrl_data_cte nrl ON nrl.case_id = c.id")
		      .append(" LEFT JOIN pneu_serotyping pneu_sero ON pneu_sero.case_id = c.id")
		      .append(" LEFT JOIN pneu_detection_method pneu_detect ON pneu_detect.case_id = c.id")
		      .append(" LEFT JOIN pneu_clinical_criteria pneu_clin ON pneu_clin.case_id = c.id")
		      .append(" LEFT JOIN pneu_vaccination_details pneu_vax ON pneu_vax.case_id = c.id")
		      .append(" LEFT JOIN pneu_ast_data pneu_ast ON pneu_ast.case_id = c.id");

		select.append(" ORDER BY c.reportdate");
		//@formatter:on

		return select.toString();
	}

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

	/**
	 * Determines the summary Vaccine code from the most recent PCV brand.
	 * Uses the highest-numbered PCV dose that has a brand value.
	 */
	private String determineVaccineCode(String brandPCV4, String brandPCV3, String brandPCV2, String brandPCV1) {
		// Return the most recent (highest numbered) non-blank vaccine brand
		if (!StringUtils.isBlank(brandPCV4)) {
			return EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV4);
		}
		if (!StringUtils.isBlank(brandPCV3)) {
			return EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV3);
		}
		if (!StringUtils.isBlank(brandPCV2)) {
			return EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV2);
		}
		if (!StringUtils.isBlank(brandPCV1)) {
			return EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV1);
		}
		return null;
	}
}
