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

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;

/**
 * CSV export strategy for PNEU (Invasive Pneumococcal Infection) disease.
 * Follows EpiPulse metadata specification exactly with 49 fixed columns (no repeatable fields).
 * <p>
 * Column structure:
 * - 13 common demographic fields
 * - 5 clinical/diagnostic fields (including DateOfDiagnosis, ClinicalCriteria)
 * - 2 laboratory fields (PathogenDetectionMethod, Serotype)
 * - 23 vaccination fields (detailed PCV1-4 and PPV tracking)
 * - 9 AST fields (antimicrobial susceptibility for CTX/CFX, ERY, PEN)
 * <p>
 * Total: 49 fixed columns
 */
@Stateless
@LocalBean
public class IpiCsvExportStrategy implements CsvExportStrategy {

	@Override
	public List<String> buildColumnNames(EpipulseDiseaseExportResult exportResult) {
		// PNEU has 49 fixed columns - no repeatable fields according to metadata
		return List.of(
			// Common demographic fields (13)
			"Disease",
			"ReportingCountry",
			"Status",
			"SubjectCode",
			"NationalRecordId",
			"DataSource",
			"DateUsedForStatistics",
			"NRLData",
			"Age",
			"AgeMonth",
			"Gender",
			"PlaceOfResidence",
			"PlaceOfNotification",
			// Clinical/Diagnostic fields (5)
			"CaseClassification",
			"DateOfDiagnosis",
			"DateOfNotification",
			"Outcome",
			"ClinicalCriteria",
			// Laboratory fields (2)
			"PathogenDetectionMethod",
			"Serotype",
			// Vaccination fields (23)
			"DateOfLastVaccination",
			"Vaccine",
			"VaccinationStatus",
			"DosePCV1",
			"DatePCV1",
			"BrandPCV1",
			"DosePCV2",
			"DatePCV2",
			"BrandPCV2",
			"DosePCV3",
			"DatePCV3",
			"BrandPCV3",
			"DosePCV4",
			"DatePCV4",
			"BrandPCV4",
			"PCVDoses",
			"DosePPV",
			"DatePPV",
			"PPVDoses",
			// AST fields (9)
			"ASTMethod",
			"MICSign_CTX_CFX",
			"MICValueAST_CTX_CFX",
			"SIR_CTX_CFX",
			"MICSign_ERY",
			"MICValueAST_ERY",
			"SIR_ERY",
			"MICSign_PEN",
			"MICValueAST_PEN",
			"SIR_PEN");
	}

	@Override
	public int writeEntryRow(EpipulseDiseaseExportEntryDto dto, String[] exportLine, EpipulseDiseaseExportResult exportResult) {
		int index = -1;

		// Common demographic fields (13)
		exportLine[++index] = dto.getDiseaseForCsv();
		exportLine[++index] = dto.getReportingCountryForCsv();
		exportLine[++index] = dto.getStatusForCsv();
		exportLine[++index] = dto.getSubjectCodeForCsv();
		exportLine[++index] = dto.getNationalRecordIdForCsv();
		exportLine[++index] = dto.getDataSourceForCsv();
		exportLine[++index] = dto.getDateUsedForStatisticsCsv();
		exportLine[++index] = dto.getNrlDataForCsv();
		exportLine[++index] = dto.getAgeForCsv();
		exportLine[++index] = dto.getAgeMonthForCsv();
		exportLine[++index] = dto.getGenderForCsv();
		exportLine[++index] = dto.getPlaceOfResidenceForCsv();
		exportLine[++index] = dto.getPlaceOfNotificationForCsv();

		// Clinical/Diagnostic fields (5)
		exportLine[++index] = dto.getCaseClassificationForCsv();
		exportLine[++index] = dto.getDateOfDiagnosisForCsv();
		exportLine[++index] = dto.getDateOfNotificationForCsv();
		exportLine[++index] = dto.getOutcomeForCsv();
		exportLine[++index] = dto.getClinicalCriteriaForCsv();

		// Laboratory fields (2)
		exportLine[++index] = dto.getPathogenDetectionMethodForCsv();
		exportLine[++index] = dto.getSerotypeForCsv();

		// Vaccination fields (23)
		exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
		exportLine[++index] = dto.getVaccineForCsv();
		exportLine[++index] = dto.getVaccinationStatusForCsv();

		// PCV doses 1-4
		exportLine[++index] = dto.getDosePCV1ForCsv();
		exportLine[++index] = dto.getDatePCV1ForCsv();
		exportLine[++index] = dto.getBrandPCV1ForCsv();
		exportLine[++index] = dto.getDosePCV2ForCsv();
		exportLine[++index] = dto.getDatePCV2ForCsv();
		exportLine[++index] = dto.getBrandPCV2ForCsv();
		exportLine[++index] = dto.getDosePCV3ForCsv();
		exportLine[++index] = dto.getDatePCV3ForCsv();
		exportLine[++index] = dto.getBrandPCV3ForCsv();
		exportLine[++index] = dto.getDosePCV4ForCsv();
		exportLine[++index] = dto.getDatePCV4ForCsv();
		exportLine[++index] = dto.getBrandPCV4ForCsv();
		exportLine[++index] = dto.getPcvDosesForCsv();

		// PPV doses
		exportLine[++index] = dto.getDosePPVForCsv();
		exportLine[++index] = dto.getDatePPVForCsv();
		exportLine[++index] = dto.getPpvDosesForCsv();

		// AST fields (9)
		exportLine[++index] = dto.getAstMethodForCsv();

		// CTX/CFX (Cefotaxime/Ceftriaxone)
		exportLine[++index] = dto.getMicSign_CTX_CFXForCsv();
		exportLine[++index] = dto.getMicValueAST_CTX_CFXForCsv();
		exportLine[++index] = dto.getSir_CTX_CFXForCsv();

		// ERY (Erythromycin)
		exportLine[++index] = dto.getMicSign_ERYForCsv();
		exportLine[++index] = dto.getMicValueAST_ERYForCsv();
		exportLine[++index] = dto.getSir_ERYForCsv();

		// PEN (Penicillin)
		exportLine[++index] = dto.getMicSign_PENForCsv();
		exportLine[++index] = dto.getMicValueAST_PENForCsv();
		exportLine[++index] = dto.getSir_PENForCsv();

		return index;
	}
}
