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

package de.symeda.sormas.backend.epipulse;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportFacade;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

@Stateless(name = "EpipulseDiseaseExportFacade")
public class EpipulseDiseaseExportFacadeEjb implements EpipulseDiseaseExportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private EpipulseDiseaseExportService diseaseExportService;

	@EJB
	private EpipulseExportFacadeEjb.EpipulseExportFacadeEjbLocal epipulseExportEjb;

	@EJB
	private EpipulseExportService epipulseExportService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	public void startPertussisExport(String uuid) {

		CSVWriter writer = null;
		EpipulseExport epipulseExport = null;
		EpipulseExportStatus exportStatus = EpipulseExportStatus.FAILED;
		boolean shouldUpdateStatus = false;

		Integer totalRecords = null;
		BigDecimal exportFileSizeBytes = null;
		String exportFileName = null;
		String exportFilePath = null;

		try {
			epipulseExport = epipulseExportService.getByUuid(uuid);

			if (epipulseExport == null) {
				logger.error("EpipulseExport with uuid " + uuid + " not found");
				return;
			}

			if (epipulseExport.getStatus() != EpipulseExportStatus.PENDING) {
				logger.error("EpipulseExport with uuid " + uuid + " is not in status PENDING");
				return;
			}

			shouldUpdateStatus = true;

			diseaseExportService.updateStatusForBackgroundProcess(uuid, EpipulseExportStatus.IN_PROGRESS, null, null, null);

			EpipulseExportDto exportDto = epipulseExportEjb.toEpipulseExportDto(epipulseExport);

			String serverCountryLocale = configFacadeEjb.getCountryLocale();
			String serverCountryCode = configFacadeEjb.getCountryCode();
			String serverCountryName = configFacadeEjb.getCountryName();

			String generatedFilesPath = configFacadeEjb.getGeneratedFilesPath();
			exportFileName = diseaseExportService.generateDownloadFileName(exportDto, epipulseExport.getId());
			exportFilePath = generatedFilesPath + "/" + exportFileName;

			EpipulseDiseaseExportResult exportResult = diseaseExportService.exportPertussisCaseBased(exportDto, serverCountryCode, serverCountryName);
			totalRecords = exportResult.getExportEntryList().size();

			//logger.info("Total records found for export: " + exportResult.getExportEntryList().size() + "");

			writer = CSVUtils.createCSVWriter(
				new OutputStreamWriter(new FileOutputStream(exportFilePath), StandardCharsets.UTF_8),
				configFacadeEjb.getCsvSeparator());

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

			if (exportResult.getMaxPathogenTests() > 0) {
				for (int i = 0; i < exportResult.getMaxPathogenTests(); i++) {
					columnNames.add("PathogenDetectionMethod");
				}
			}

			if (exportResult.getMaxImmunizations() > 0) {
				columnNames.add("DateOfLastVaccination");
			}

			columnNames.add("VaccinationStatus");

			//columnNames.add("VaccinationStatusMaternal");
			//columnNames.add("GestationalAgeAtVaccination");

			//write the headers
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));

			//write entries
			String[] exportLine = new String[columnNames.size()];
			List<String> pathogenDetectionMethods = new ArrayList<>();
			int index;
			for (EpipulseDiseaseExportEntryDto dto : exportResult.getExportEntryList()) {
				index = -1;

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

				if (exportResult.getMaxPathogenTests() > 0) {
					pathogenDetectionMethods = dto.getPathogenDetectionMethodsForCsv(exportResult.getMaxPathogenTests());
					for (String pathogenDetectionMethod : pathogenDetectionMethods) {
						exportLine[++index] = pathogenDetectionMethod;
					}
				}

				if (exportResult.getMaxImmunizations() > 0) {
					exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
				}

				exportLine[++index] = dto.getVaccinationStatusForCsv();

				//exportLine[++index] = dto.getVaccinationStatusMaternalForCsv();
				//exportLine[++index] = dto.getGestationalAgeAtVaccinationForCsv();

				writer.writeNext(exportLine);
			}

			exportStatus = EpipulseExportStatus.COMPLETED;
		} catch (Exception e) {
			exportStatus = EpipulseExportStatus.FAILED;

			logger.error("Error during export with uuid " + uuid + ": " + e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to close CSVWriter for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}

			// Calculate file size after writer is closed
			if (exportFilePath != null && exportStatus == EpipulseExportStatus.COMPLETED) {
				try {
					long fileSizeInBytes = Files.size(Paths.get(exportFilePath));
					exportFileSizeBytes = new BigDecimal(fileSizeInBytes);
					logger.info("Export file size for uuid {}: {} bytes", uuid, fileSizeInBytes);
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to calculate file size for uuid {}: {}", uuid, e.getMessage(), e);
				}
			}

			if (shouldUpdateStatus && epipulseExport != null) {
				try {
					diseaseExportService
						.updateStatusForBackgroundProcess(epipulseExport.getUuid(), exportStatus, totalRecords, exportFileName, exportFileSizeBytes);
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to update export status for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}
		}
	}

	public void startMeaslesExport(String uuid) {

		CSVWriter writer = null;
		EpipulseExport epipulseExport = null;
		EpipulseExportStatus exportStatus = EpipulseExportStatus.FAILED;
		boolean shouldUpdateStatus = false;

		Integer totalRecords = null;
		BigDecimal exportFileSizeBytes = null;
		String exportFileName = null;
		String exportFilePath = null;

		try {
			epipulseExport = epipulseExportService.getByUuid(uuid);

			if (epipulseExport == null) {
				logger.error("EpipulseExport with uuid " + uuid + " not found");
				return;
			}

			if (epipulseExport.getStatus() != EpipulseExportStatus.PENDING) {
				logger.error("EpipulseExport with uuid " + uuid + " is not in status PENDING");
				return;
			}

			shouldUpdateStatus = true;

			diseaseExportService.updateStatusForBackgroundProcess(uuid, EpipulseExportStatus.IN_PROGRESS, null, null, null);

			EpipulseExportDto exportDto = epipulseExportEjb.toEpipulseExportDto(epipulseExport);

			String serverCountryLocale = configFacadeEjb.getCountryLocale();
			String serverCountryCode = configFacadeEjb.getCountryCode();
			String serverCountryName = configFacadeEjb.getCountryName();

			String generatedFilesPath = configFacadeEjb.getGeneratedFilesPath();
			exportFileName = diseaseExportService.generateDownloadFileName(exportDto, epipulseExport.getId());
			exportFilePath = generatedFilesPath + "/" + exportFileName;

			EpipulseDiseaseExportResult exportResult = diseaseExportService.exportMeaslesCaseBased(exportDto, serverCountryCode, serverCountryName);
			totalRecords = exportResult.getExportEntryList().size();

			writer = CSVUtils.createCSVWriter(
				new OutputStreamWriter(new FileOutputStream(exportFilePath), StandardCharsets.UTF_8),
				configFacadeEjb.getCsvSeparator());

			// MEAS CSV columns - including Phase 2 laboratory fields and Phase 3 clinical/epidemiology fields
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

			// Repeatable field: TypeOfSpecimenCollected
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

			if (exportResult.getMaxImmunizations() > 0) {
				columnNames.add("DateOfLastVaccination");
			}

			columnNames.add("VaccinationStatus");

			//write the headers
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));

			//write entries
			String[] exportLine = new String[columnNames.size()];
			int index;
			for (EpipulseDiseaseExportEntryDto dto : exportResult.getExportEntryList()) {
				index = -1;

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

				// Phase 2: Laboratory fields
				exportLine[++index] = dto.getDateOfSpecimenForCsv();
				exportLine[++index] = dto.getDateOfLaboratoryResultForCsv();

				// Repeatable: TypeOfSpecimenCollected
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

				// Phase 3: Clinical and epidemiology fields
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

				if (exportResult.getMaxImmunizations() > 0) {
					exportLine[++index] = dto.getDateOfLastVaccinationForCsv();
				}

				exportLine[++index] = dto.getVaccinationStatusForCsv();

				writer.writeNext(exportLine);
			}

			exportStatus = EpipulseExportStatus.COMPLETED;
		} catch (Exception e) {
			exportStatus = EpipulseExportStatus.FAILED;

			logger.error("Error during export with uuid " + uuid + ": " + e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to close CSVWriter for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}

			// Calculate file size after writer is closed
			if (exportFilePath != null && exportStatus == EpipulseExportStatus.COMPLETED) {
				try {
					long fileSizeInBytes = Files.size(Paths.get(exportFilePath));
					exportFileSizeBytes = new BigDecimal(fileSizeInBytes);
					logger.info("Export file size for uuid {}: {} bytes", uuid, fileSizeInBytes);
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to calculate file size for uuid {}: {}", uuid, e.getMessage(), e);
				}
			}

			if (shouldUpdateStatus && epipulseExport != null) {
				try {
					diseaseExportService
						.updateStatusForBackgroundProcess(epipulseExport.getUuid(), exportStatus, totalRecords, exportFileName, exportFileSizeBytes);
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to update export status for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}
		}
	}

	@LocalBean
	@Stateless
	public static class EpipulseDiseaseExportFacadeEjbLocal extends EpipulseDiseaseExportFacadeEjb {

	}
}
