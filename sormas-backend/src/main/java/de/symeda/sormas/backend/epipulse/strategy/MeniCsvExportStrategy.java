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
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;

/**
 * CSV export strategy for MENI (Invasive Meningococcal Infection) disease.
 * Follows EpiPulse metadata specification with dynamic columns for 5 repeatable field types:
 * - IsolateId
 * - MainPathogenDetectionMethod
 * - SecondPathogenDetectionMethod
 * - ResultMLST
 * - PlaceOfInfection
 * <p>
 * Fixed columns include:
 * - Common demographic fields (Disease, ReportingCountry, Status, SubjectCode, etc.)
 * - DateOfOnset, DateOfDiagnosis, DateOfNotification
 * - ClinicalCriteria (MENI/MENISEPTI/SEPTI/PNEU/OTH)
 * - Serogroup (NEIMENI_A/B/C/W/X/Y/Z/29E/NGA/OTH)
 * - Molecular typing: ResultPorA1, ResultPorA2, ResultFetVR
 * - ImportedStatus, ReportedEMERTII
 * - Vaccination fields: DateOfLastVaccination, VaccinationStatus
 * - AST fields: CIP, CTX_CFX, PEN, RIF (MENI-specific: Rifampicin)
 */
@Stateless
@LocalBean
public class MeniCsvExportStrategy implements CsvExportStrategy {

	@Override
	public List<String> buildColumnNames(EpipulseDiseaseExportResult exportResult) {
		List<String> columnNames = new ArrayList<>();

		// Common demographic fields (following EpiPulse metadata order)
		columnNames.add("Disease");
		columnNames.add("ReportingCountry");
		columnNames.add("Status");
		columnNames.add("SubjectCode");
		columnNames.add("NationalRecordId");
		columnNames.add("DataSource");
		columnNames.add("DateUsedForStatistics");
		columnNames.add("Age");
		columnNames.add("AgeMonth");
		columnNames.add("Gender");
		columnNames.add("CaseClassification");

		// Clinical/temporal fields
		columnNames.add("DateOfOnset");
		columnNames.add("DateOfDiagnosis");
		columnNames.add("DateOfNotification");
		columnNames.add("Outcome");
		columnNames.add("ClinicalCriteria");

		// Laboratory: Serogroup
		columnNames.add("Serogroup");

		// Repeatable: IsolateId
		for (int i = 1; i <= exportResult.getMaxIsolateIds(); i++) {
			columnNames.add("IsolateId");
		}

		// Repeatable: MainPathogenDetectionMethod
		for (int i = 1; i <= exportResult.getMaxMainPathogenDetectionMethods(); i++) {
			columnNames.add("MainPathogenDetectionMethod");
		}

		// Repeatable: SecondPathogenDetectionMethod
		for (int i = 1; i <= exportResult.getMaxSecondPathogenDetectionMethods(); i++) {
			columnNames.add("SecondPathogenDetectionMethod");
		}

		// Molecular typing results
		// Repeatable: ResultMLST
		for (int i = 1; i <= exportResult.getMaxResultMlst(); i++) {
			columnNames.add("ResultMLST");
		}

		// Fixed molecular typing fields
		columnNames.add("ResultPorA1");
		columnNames.add("ResultPorA2");
		columnNames.add("ResultFetVR");

		// Epidemiology fields
		columnNames.add("ImportedStatus");
		columnNames.add("ReportedEMERTII");

		// Repeatable: PlaceOfInfection
		for (int i = 1; i <= exportResult.getMaxPlaceOfInfection(); i++) {
			columnNames.add("PlaceOfInfection");
		}

		// Geographic fields
		columnNames.add("PlaceOfNotification");
		columnNames.add("PlaceOfResidence");

		// Vaccination fields
		columnNames.add("DateOfLastVaccination");
		columnNames.add("VaccinationStatus");

		// AST fields - MENI uses CIP, CTX_CFX, PEN, RIF
		// CIP (Ciprofloxacin)
		columnNames.add("MICSign_CIP");
		columnNames.add("MICValueAST_CIP");
		columnNames.add("SIR_CIP");

		// CTX_CFX (Ceftriaxone/Cefotaxime)
		columnNames.add("MICSign_CTX_CFX");
		columnNames.add("MICValueAST_CTX_CFX");
		columnNames.add("SIR_CTX_CFX");

		// PEN (Penicillin)
		columnNames.add("MICSign_PEN");
		columnNames.add("MICValueAST_PEN");
		columnNames.add("SIR_PEN");

		// RIF (Rifampicin) - MENI-specific antibiotic
		columnNames.add("MICSign_RIF");
		columnNames.add("MICValueAST_RIF");
		columnNames.add("SIR_RIF");

		return columnNames;
	}

	@Override
	public int writeEntryRow(EpipulseDiseaseExportEntryDto dto, String[] exportLine, EpipulseDiseaseExportResult exportResult) {
		int index = -1;

		// Common demographic fields
		exportLine[++index] = dto.getDiseaseForCsv();
		exportLine[++index] = dto.getReportingCountryForCsv();
		exportLine[++index] = dto.getStatusForCsv();
		exportLine[++index] = dto.getSubjectCodeForCsv();
		exportLine[++index] = dto.getNationalRecordIdForCsv();
		exportLine[++index] = dto.getDataSourceForCsv();
		exportLine[++index] = dto.getDateUsedForStatisticsCsv();
		exportLine[++index] = dto.getAgeForCsv();
		exportLine[++index] = dto.getAgeMonthForCsv();
		exportLine[++index] = dto.getGenderForCsv();
		exportLine[++index] = dto.getCaseClassificationForCsv();

		// Clinical/temporal fields
		exportLine[++index] = dto.getDateOfOnsetForCsv();
		exportLine[++index] = dto.getDateOfDiagnosisForCsv();
		exportLine[++index] = dto.getDateOfNotificationForCsv();
		exportLine[++index] = dto.getOutcomeForCsv();
		exportLine[++index] = dto.getClinicalCriteriaForCsv();

		// Laboratory: Serogroup
		exportLine[++index] = dto.getSerogroupForCsv();

		// Repeatable: IsolateId
		List<String> isolateIds = dto.getIsolateIdsForCsv(exportResult.getMaxIsolateIds());
		for (String isolateId : isolateIds) {
			exportLine[++index] = isolateId;
		}

		// Repeatable: MainPathogenDetectionMethod
		if (exportResult.getMaxMainPathogenDetectionMethods() > 0) {
			List<String> mainDetectionMethods = dto.getMainPathogenDetectionMethodsForCsv(exportResult.getMaxMainPathogenDetectionMethods());
			for (String method : mainDetectionMethods) {
				exportLine[++index] = method;
			}
		}

		// Repeatable: SecondPathogenDetectionMethod
		if (exportResult.getMaxSecondPathogenDetectionMethods() > 0) {
			List<String> secondDetectionMethods = dto.getSecondPathogenDetectionMethodsForCsv(exportResult.getMaxSecondPathogenDetectionMethods());
			for (String method : secondDetectionMethods) {
				exportLine[++index] = method;
			}
		}

		// Repeatable: ResultMLST
		if (exportResult.getMaxResultMlst() > 0) {
			List<String> mlstResults = dto.getResultMlstForCsv(exportResult.getMaxResultMlst());
			for (String mlst : mlstResults) {
				exportLine[++index] = mlst;
			}
		}

		// Fixed molecular typing fields
		exportLine[++index] = dto.getResultPorA1ForCsv();
		exportLine[++index] = dto.getResultPorA2ForCsv();
		exportLine[++index] = dto.getResultFetVRForCsv();

		// Epidemiology fields
		exportLine[++index] = dto.getImportedStatusForCsv();
		exportLine[++index] = dto.getReportedEmertiiForCsv();

		// Repeatable: PlaceOfInfection
		if (exportResult.getMaxPlaceOfInfection() > 0) {
			List<String> placesOfInfection = dto.getPlaceOfInfectionForCsv(exportResult.getMaxPlaceOfInfection());
			for (String place : placesOfInfection) {
				exportLine[++index] = place;
			}
		}

		// Geographic fields
		exportLine[++index] = dto.getPlaceOfNotificationForCsv();
		exportLine[++index] = dto.getPlaceOfResidenceForCsv();

		// Vaccination fields - use MENI-specific vaccination status (max 4 doses)
		exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
		exportLine[++index] = dto.getVaccinationStatusMeniForCsv();

		// AST fields - CIP
		exportLine[++index] = dto.getMicSign_CIPForCsv();
		exportLine[++index] = dto.getMicValueAST_CIPForCsv();
		exportLine[++index] = dto.getSir_CIPForCsv();

		// AST fields - CTX_CFX
		exportLine[++index] = dto.getMicSign_CTX_CFXForCsv();
		exportLine[++index] = dto.getMicValueAST_CTX_CFXForCsv();
		exportLine[++index] = dto.getSir_CTX_CFXForCsv();

		// AST fields - PEN
		exportLine[++index] = dto.getMicSign_PENForCsv();
		exportLine[++index] = dto.getMicValueAST_PENForCsv();
		exportLine[++index] = dto.getSir_PENForCsv();

		// AST fields - RIF (MENI-specific)
		exportLine[++index] = dto.getMicSign_RIFForCsv();
		exportLine[++index] = dto.getMicValueAST_RIFForCsv();
		exportLine[++index] = dto.getSir_RIFForCsv();

		return index;
	}
}
