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
 * PNEU follows the EpiPulse metadata specification exactly with 49 fixed columns (no repeatable fields).
 * <p>
 * Metadata specification:
 * - 13 common demographic fields
 * - 5 clinical/diagnostic fields (including DateOfDiagnosis, ClinicalCriteria)
 * - 2 laboratory fields (PathogenDetectionMethod, Serotype)
 * - 23 vaccination fields (detailed PCV1-4 and PPV tracking)
 * - 9 AST fields (antimicrobial susceptibility for CTX/CFX, ERY, PEN)
 */
@Stateless
@LocalBean
public class IpiExportStrategy extends AbstractEpipulseDiseaseExportStrategy {

	@Override
	protected String buildDiseaseExportQuery() {
		StringBuilder query = new StringBuilder();

		// Common CTEs (no epidata fields for PNEU metadata)
		query.append(sqlCteBuilder.buildVariablesCte());
		query.append(sqlCteBuilder.buildConfigDataCte());
		query.append(sqlCteBuilder.buildFilteredCasesCte(false)); // No epidata fields needed
		query.append(sqlCteBuilder.buildPreviousHospitalizationsCte());
		query.append(sqlCteBuilder.buildSamplesCte());
		query.append(sqlCteBuilder.buildPathogenTestsCte());
		query.append(sqlCteBuilder.buildImmunizationsCte());
		query.append(sqlCteBuilder.buildVaccinationsCte());

		// PNEU-specific CTEs (following metadata specification)
		query.append(buildPneuSerotypingDataCte());
		query.append(buildPneuPathogenDetectionMethodCte());
		query.append(buildPneuClinicalCriteriaCte());
		query.append(buildPneuVaccinationDetailsCte());
		// Note: AST data not currently stored in SORMAS for PNEU - fields will be NULL
		// query.append(buildPneuAstDataCte());

		// Main SELECT clause with all 49 PNEU fields
		query.append(buildPneuSelectClause());

		return query.toString();
	}

	/**
	 * Maps all 21 PNEU-specific fields from SQL result row to DTO
	 * (Common fields already mapped by parent class - 28 fields)
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
		Date datePCV1 = (Date) row[++index];
		dto.setDatePCV1(datePCV1);
		dto.setDosePCV1(datePCV1 != null);

		String brandPCV1Raw = (String) row[++index];
		if (!StringUtils.isBlank(brandPCV1Raw)) {
			dto.setBrandPCV1(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV1Raw));
		}

		Date datePCV2 = (Date) row[++index];
		dto.setDatePCV2(datePCV2);
		dto.setDosePCV2(datePCV2 != null);

		String brandPCV2Raw = (String) row[++index];
		if (!StringUtils.isBlank(brandPCV2Raw)) {
			dto.setBrandPCV2(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV2Raw));
		}

		Date datePCV3 = (Date) row[++index];
		dto.setDatePCV3(datePCV3);
		dto.setDosePCV3(datePCV3 != null);

		String brandPCV3Raw = (String) row[++index];
		if (!StringUtils.isBlank(brandPCV3Raw)) {
			dto.setBrandPCV3(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV3Raw));
		}

		Date datePCV4 = (Date) row[++index];
		dto.setDatePCV4(datePCV4);
		dto.setDosePCV4(datePCV4 != null);

		String brandPCV4Raw = (String) row[++index];
		if (!StringUtils.isBlank(brandPCV4Raw)) {
			dto.setBrandPCV4(EpipulseLaboratoryMapper.mapVaccineToEpipulseCode(brandPCV4Raw));
		}

		// PCV dose count
		Number pcvDosesNum = (Number) row[++index];
		dto.setPcvDoses(pcvDosesNum != null ? pcvDosesNum.intValue() : null);

		// Fields 49-50: Vaccination details - PPV doses
		Date datePPV = (Date) row[++index];
		dto.setDatePPV(datePPV);
		dto.setDosePPV(datePPV != null);

		Number ppvDosesNum = (Number) row[++index];
		dto.setPpvDoses(ppvDosesNum != null ? ppvDosesNum.intValue() : null);

		// Fields 51-60: AST (Antimicrobial Susceptibility Testing) data
		// AST Method (not stored in SORMAS currently)
		String astMethod = (String) row[++index];
		dto.setAstMethod(astMethod);

		// CTX/CFX (Ceftriaxone/Cefotaxime)
		String micSignCTX = (String) row[++index];
		dto.setMicSign_CTX_CFX(micSignCTX);

		Double micValueCTX = (Double) row[++index];
		dto.setMicValueAST_CTX_CFX(micValueCTX);

		String sirCTXRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirCTXRaw)) {
			dto.setSir_CTX_CFX(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirCTXRaw));
		}

		// ERY (Erythromycin)
		String micSignERY = (String) row[++index];
		dto.setMicSign_ERY(micSignERY);

		Double micValueERY = (Double) row[++index];
		dto.setMicValueAST_ERY(micValueERY);

		String sirERYRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirERYRaw)) {
			dto.setSir_ERY(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirERYRaw));
		}

		// PEN (Penicillin)
		String micSignPEN = (String) row[++index];
		dto.setMicSign_PEN(micSignPEN);

		Double micValuePEN = (Double) row[++index];
		dto.setMicValueAST_PEN(micValuePEN);

		String sirPENRaw = (String) row[++index];
		if (!StringUtils.isBlank(sirPENRaw)) {
			dto.setSir_PEN(EpipulseLaboratoryMapper.mapDrugSusceptibilityToSIR(sirPENRaw));
		}
	}

	@Override
	protected void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {
		// PNEU has no repeatable fields according to metadata specification
		// All 49 columns are fixed - no dynamic column generation needed
		// Common repeatable fields (pathogenTests, immunizations) are handled by parent class
	}

	// Helper methods for PNEU-specific CTEs (following metadata specification)

	/**
	 * CTE for serotype data from pathogen tests
	 */
	private String buildPneuSerotypingDataCte() {
		//@formatter:off
		return ", pneu_serotyping AS (SELECT c.id as case_id," +
			   "                             (SELECT pt.typingid " +
			   "                              FROM samples s " +
			   "                              JOIN pathogentest pt ON pt.sample_id = s.id " +
			   "                              WHERE s.associatedcase_id = c.id " +
			   "                                AND s.deleted = false " +
			   "                                AND pt.testtype IN ('SEROGROUPING', 'GENOTYPING', 'WHOLE_GENOME_SEQUENCING') " +
			   "                                AND pt.typingid IS NOT NULL " +
			   "                              ORDER BY pt.testdatetime DESC " +
			   "                              LIMIT 1) as serotype " +
			   "                      FROM filtered_cases c), ";
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
			   "                                   ORDER BY pt.testdatetime DESC " +
			   "                                   LIMIT 1) as detection_method " +
			   "                           FROM filtered_cases c), ";
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
			   "                            JOIN symptoms s ON c.symptoms_id = s.id), ";
		//@formatter:on
	}

	/**
	 * CTE for detailed vaccination data (PCV1-4, PPV doses)
	 */
	private String buildPneuVaccinationDetailsCte() {
		//@formatter:off
		return "pneu_vaccination_details AS (" +
			   "    SELECT c.id as case_id," +
			   "           MAX(CASE WHEN pcv_row_num = 1 THEN v.vaccinationdate END) as date_pcv1," +
			   "           MAX(CASE WHEN pcv_row_num = 1 THEN CAST(v.vaccinename AS text) END) as brand_pcv1," +
			   "           MAX(CASE WHEN pcv_row_num = 2 THEN v.vaccinationdate END) as date_pcv2," +
			   "           MAX(CASE WHEN pcv_row_num = 2 THEN CAST(v.vaccinename AS text) END) as brand_pcv2," +
			   "           MAX(CASE WHEN pcv_row_num = 3 THEN v.vaccinationdate END) as date_pcv3," +
			   "           MAX(CASE WHEN pcv_row_num = 3 THEN CAST(v.vaccinename AS text) END) as brand_pcv3," +
			   "           MAX(CASE WHEN pcv_row_num = 4 THEN v.vaccinationdate END) as date_pcv4," +
			   "           MAX(CASE WHEN pcv_row_num = 4 THEN CAST(v.vaccinename AS text) END) as brand_pcv4," +
			   "           SUM(CASE WHEN is_pcv THEN 1 ELSE 0 END) as pcv_doses," +
			   "           MAX(CASE WHEN ppv_row_num = 1 THEN v.vaccinationdate END) as date_ppv," +
			   "           SUM(CASE WHEN is_ppv THEN 1 ELSE 0 END) as ppv_doses " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN vaccination v ON v.immunization_id IN (SELECT i.id FROM immunization i WHERE i.person_id = c.person_id AND i.disease = 'INVASIVE_PNEUMOCOCCAL_INFECTION') " +
			   "    LEFT JOIN LATERAL (" +
			   "        SELECT CASE " +
			   "                 WHEN CAST(v.vaccinename AS text) LIKE '%PCV%' OR CAST(v.vaccinename AS text) LIKE '%CONJUGATE%' THEN true " +
			   "                 ELSE false " +
			   "               END as is_pcv," +
			   "               CASE " +
			   "                 WHEN CAST(v.vaccinename AS text) LIKE '%PPV%' OR CAST(v.vaccinename AS text) LIKE '%POLYSACCHARIDE%' THEN true " +
			   "                 ELSE false " +
			   "               END as is_ppv," +
			   "               ROW_NUMBER() OVER (PARTITION BY c.id, " +
			   "                   CASE WHEN CAST(v.vaccinename AS text) LIKE '%PCV%' THEN 1 ELSE 0 END " +
			   "                   ORDER BY v.vaccinationdate) as pcv_row_num," +
			   "               ROW_NUMBER() OVER (PARTITION BY c.id, " +
			   "                   CASE WHEN CAST(v.vaccinename AS text) LIKE '%PPV%' THEN 1 ELSE 0 END " +
			   "                   ORDER BY v.vaccinationdate) as ppv_row_num " +
			   "    ) vax_info ON true " +
			   "    GROUP BY c.id) ";
		//@formatter:on
	}

	/**
	 * CTE for antimicrobial susceptibility testing (AST) data
	 * Maps to CTX/CFX (Ceftriaxone), ERY (Erythromycin), PEN (Penicillin)
	 */
	private String buildPneuAstDataCte() {
		//@formatter:off
		return "pneu_ast_data AS (" +
			   "    SELECT c.id as case_id," +
			   "           MAX(CASE WHEN antibiotic = 'CEFTRIAXONE' THEN mic_value END) as mic_ctx," +
			   "           MAX(CASE WHEN antibiotic = 'CEFTRIAXONE' THEN CAST(susceptibility AS text) END) as sir_ctx," +
			   "           MAX(CASE WHEN antibiotic = 'ERYTHROMYCIN' THEN mic_value END) as mic_ery," +
			   "           MAX(CASE WHEN antibiotic = 'ERYTHROMYCIN' THEN CAST(susceptibility AS text) END) as sir_ery," +
			   "           MAX(CASE WHEN antibiotic = 'PENICILLIN' THEN mic_value END) as mic_pen," +
			   "           MAX(CASE WHEN antibiotic = 'PENICILLIN' THEN CAST(susceptibility AS text) END) as sir_pen " +
			   "    FROM filtered_cases c " +
			   "    LEFT JOIN samples s ON s.associatedcase_id = c.id AND s.deleted = false " +
			   "    LEFT JOIN pathogentest pt ON pt.sample_id = s.id AND pt.testtype = 'ANTIBIOTIC_SUSCEPTIBILITY' " +
			   "    LEFT JOIN LATERAL (" +
			   "        SELECT 'CEFTRIAXONE' as antibiotic, pt.ceftriaxonemic as mic_value, pt.ceftriaxonesusceptibility as susceptibility " +
			   "        UNION ALL " +
			   "        SELECT 'ERYTHROMYCIN', pt.erythromycinmic, pt.erythromycinsusceptibility " +
			   "        UNION ALL " +
			   "        SELECT 'PENICILLIN', pt.penicillinmic, pt.penicillinsusceptibility " +
			   "    ) ast_data ON true " +
			   "    GROUP BY c.id) ";
		//@formatter:on
	}

	/**
	 * Builds SELECT clause with all 49 PNEU fields following metadata specification
	 */
	private String buildPneuSelectClause() {
		StringBuilder select = new StringBuilder();
		//@formatter:off
		// Use common SELECT fields (28 fields) and append PNEU-specific fields (21 fields)
		select.append("SELECT ")
		      .append(sqlCteBuilder.buildCommonSelectFields())
		      .append(",")
		      // PNEU-specific fields start here
		      // Demographics: NRLData (field 29)
		      .append("       NULL as nrl_data,") // Not tracked in SORMAS currently
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
		      // AST fields (fields 51-60) - Not currently stored in SORMAS
		      .append("       NULL as ast_method,")
		      .append("       NULL as mic_sign_ctx,")
		      .append("       NULL as mic_ctx,")
		      .append("       NULL as sir_ctx,")
		      .append("       NULL as mic_sign_ery,")
		      .append("       NULL as mic_ery,")
		      .append("       NULL as sir_ery,")
		      .append("       NULL as mic_sign_pen,")
		      .append("       NULL as mic_pen,")
		      .append("       NULL as sir_pen ");

		// Use common FROM and JOINs, then append PNEU-specific joins
		select.append(sqlCteBuilder.buildCommonFromAndJoins())
		      .append(" LEFT JOIN pneu_serotyping pneu_sero ON pneu_sero.case_id = c.id")
		      .append(" LEFT JOIN pneu_detection_method pneu_detect ON pneu_detect.case_id = c.id")
		      .append(" LEFT JOIN pneu_clinical_criteria pneu_clin ON pneu_clin.case_id = c.id")
		      .append(" LEFT JOIN pneu_vaccination_details pneu_vax ON pneu_vax.case_id = c.id");
		      // Note: pneu_ast_data CTE not used - AST fields returned as NULL

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
			logger.warn("Invalid SymptomState value '{}', treating as null", value);
			return null;
		}
	}
}
