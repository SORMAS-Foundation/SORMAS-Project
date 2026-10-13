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
 * CSV export strategy for Pertussis disease.
 * Handles 34 columns maximum (17 fixed + dynamic pathogen tests + vaccination).
 */
@Stateless
@LocalBean
public class PertussisCsvExportStrategy implements CsvExportStrategy {

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
				"PlaceOfResidence",
				"PlaceOfNotification",
				"CaseClassification",
				"DateOfOnset",
				"DateOfNotification",
				"Hospitalisation",
				"Outcome"));

		// Add repeatable PathogenDetectionMethod columns
		if (exportResult.getMaxPathogenTests() > 0) {
			for (int i = 0; i < exportResult.getMaxPathogenTests(); i++) {
				columnNames.add("PathogenDetectionMethod");
			}
		}

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
		exportLine[++index] = dto.getPlaceOfResidenceForCsv();
		exportLine[++index] = dto.getPlaceOfNotificationForCsv();
		exportLine[++index] = dto.getCaseClassificationForCsv();
		exportLine[++index] = dto.getDateOfOnsetForCsv();
		exportLine[++index] = dto.getDateOfNotificationForCsv();
		exportLine[++index] = dto.getHospitalizationForCsv();
		exportLine[++index] = dto.getOutcomeForCsv();

		// Write repeatable pathogen detection methods
		if (exportResult.getMaxPathogenTests() > 0) {
			List<String> pathogenDetectionMethods = dto.getPathogenDetectionMethodsForCsv(exportResult.getMaxPathogenTests());
			for (String pathogenDetectionMethod : pathogenDetectionMethods) {
				exportLine[++index] = pathogenDetectionMethod;
			}
		}

		// Write vaccination columns
		if (exportResult.getMaxImmunizations() > 0) {
			exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
		}

		exportLine[++index] = dto.getVaccinationStatusForCsv();

		return index;
	}
}
