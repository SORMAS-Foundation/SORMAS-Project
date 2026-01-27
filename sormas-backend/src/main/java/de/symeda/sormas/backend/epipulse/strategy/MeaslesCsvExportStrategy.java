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
 * CSV export strategy for Measles disease.
 * Handles 41+ columns with 5 repeatable field types:
 * - TypeOfSpecimenCollected (virus detection)
 * - TypeOfSpecimenForSerologicalAnalysis (serology)
 * - ClusterSetting
 * - ComplicationDiagnosis
 * - PlaceOfInfection
 */
@Stateless
@LocalBean
public class MeaslesCsvExportStrategy implements CsvExportStrategy {

	@Override
	public List<String> buildColumnNames(EpipulseDiseaseExportResult exportResult) {
		List<String> columnNames = new ArrayList<>(
			List.of(
				"Disease",
				"ReportingCountry",
				"Status",
				"SubjectCode",
				"NationalRecordId",
				"DataSource",
				"DateUsedForStatistics",
				"Age",
				"AgeMonth",
				"Gender",
				"CaseClassification",
				"DateOfOnset",
				"DateOfNotification",
				"Hospitalisation",
				"Outcome",
				"PlaceOfNotification",
				"PlaceOfResidence",
				"DateOfSpecimen",
				"DateOfLaboratoryResult"));

		// Repeatable field: TypeOfSpecimenCollected (virus detection)
		if (exportResult.getMaxSpecimenVirDetect() > 0) {
			for (int i = 1; i <= exportResult.getMaxSpecimenVirDetect(); i++) {
				columnNames.add("TypeOfSpecimenCollected");
			}
		}

		columnNames.addAll(List.of("ResultOfVirusDetection", "Genotype"));

		// Repeatable field: TypeOfSpecimenForSerologicalAnalysis
		if (exportResult.getMaxSpecimenSero() > 0) {
			for (int i = 1; i <= exportResult.getMaxSpecimenSero(); i++) {
				columnNames.add("TypeOfSpecimenForSerologicalAnalysis");
			}
		}

		columnNames.addAll(List.of("ResultIgG", "ResultIgM", "DateOfInvestigation", "ClusterRelated", "ClusterIdentification"));

		// Repeatable field: ClusterSetting
		if (exportResult.getMaxClusterSettings() > 0) {
			for (int i = 1; i <= exportResult.getMaxClusterSettings(); i++) {
				columnNames.add("ClusterSetting");
			}
		}

		columnNames.add("ImportedStatus");

		// Repeatable field: ComplicationDiagnosis
		if (exportResult.getMaxComplicationDiagnosis() > 0) {
			for (int i = 1; i <= exportResult.getMaxComplicationDiagnosis(); i++) {
				columnNames.add("ComplicationDiagnosis");
			}
		}

		columnNames.add("ClinicalCriteriaStatus");

		// Repeatable field: PlaceOfInfection
		if (exportResult.getMaxPlaceOfInfection() > 0) {
			for (int i = 1; i <= exportResult.getMaxPlaceOfInfection(); i++) {
				columnNames.add("PlaceOfInfection");
			}
		}

		columnNames.add("CauseOfDeath");

		// Add vaccination columns
		if (exportResult.getMaxImmunizations() > 0) {
			columnNames.add("DateOfLastVaccination");
		}

		columnNames.add("VaccinationStatus");

		return columnNames;
	}

	@Override
	public int writeEntryRow(EpipulseDiseaseExportEntryDto dto, String[] exportLine, EpipulseDiseaseExportResult exportResult) {
		int index = -1;

		// Write fixed columns
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
		exportLine[++index] = dto.getDateOfOnsetForCsv();
		exportLine[++index] = dto.getDateOfNotificationForCsv();
		exportLine[++index] = dto.getHospitalizationForCsv();
		exportLine[++index] = dto.getOutcomeForCsv();
		exportLine[++index] = dto.getPlaceOfNotificationForCsv();
		exportLine[++index] = dto.getPlaceOfResidenceForCsv();

		// Laboratory fields
		exportLine[++index] = dto.getDateOfSpecimenForCsv();
		exportLine[++index] = dto.getDateOfLaboratoryResultForCsv();

		// Repeatable: TypeOfSpecimenCollected (virus detection)
		if (exportResult.getMaxSpecimenVirDetect() > 0) {
			List<String> specimenCollected = dto.getTypeOfSpecimenCollectedForCsv(exportResult.getMaxSpecimenVirDetect());
			for (String specimen : specimenCollected) {
				exportLine[++index] = specimen;
			}
		}

		exportLine[++index] = dto.getResultOfVirusDetectionForCsv();
		exportLine[++index] = dto.getGenotypeForCsv();

		// Repeatable: TypeOfSpecimenForSerologicalAnalysis
		if (exportResult.getMaxSpecimenSero() > 0) {
			List<String> specimenSerology = dto.getTypeOfSpecimenSerologyForCsv(exportResult.getMaxSpecimenSero());
			for (String specimen : specimenSerology) {
				exportLine[++index] = specimen;
			}
		}

		exportLine[++index] = dto.getResultIgGForCsv();
		exportLine[++index] = dto.getResultIgMForCsv();

		// Clinical and epidemiology fields
		exportLine[++index] = dto.getDateOfInvestigationForCsv();
		exportLine[++index] = dto.getClusterRelatedForCsv();
		exportLine[++index] = dto.getClusterIdentificationForCsv();

		// Repeatable: ClusterSetting
		if (exportResult.getMaxClusterSettings() > 0) {
			List<String> clusterSettings = dto.getClusterSettingForCsv(exportResult.getMaxClusterSettings());
			for (String setting : clusterSettings) {
				exportLine[++index] = setting;
			}
		}

		exportLine[++index] = dto.getImportedStatusForCsv();

		// Repeatable: ComplicationDiagnosis
		if (exportResult.getMaxComplicationDiagnosis() > 0) {
			List<String> complications = dto.getComplicationDiagnosisForCsv(exportResult.getMaxComplicationDiagnosis());
			for (String complication : complications) {
				exportLine[++index] = complication;
			}
		}

		exportLine[++index] = dto.getClinicalCriteriaStatusForCsv();

		// Repeatable: PlaceOfInfection
		if (exportResult.getMaxPlaceOfInfection() > 0) {
			List<String> placesOfInfection = dto.getPlaceOfInfectionForCsv(exportResult.getMaxPlaceOfInfection());
			for (String place : placesOfInfection) {
				exportLine[++index] = place;
			}
		}

		exportLine[++index] = dto.getCauseOfDeathForCsv();

		// Vaccination columns
		if (exportResult.getMaxImmunizations() > 0) {
			exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
		}

		exportLine[++index] = dto.getVaccinationStatusForCsv();

		return index;
	}
}
