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
import java.sql.SQLException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.epipulse.strategy.CsvExportStrategy;

/**
 * Orchestrator service that handles the common export flow for all disease-specific CSV exports.
 * It is responsible for extracting setup, validation, error handling, and finalization logic.
 */
@Stateless
@LocalBean
public class EpipulseCsvExportOrchestrator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private EpipulseExportFacadeEjb.EpipulseExportFacadeEjbLocal epipulseExportEjb;

	@EJB
	private EpipulseExportService epipulseExportService;

	@EJB
	private EpipulseDiseaseExportService diseaseExportService;

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	/**
	 * Orchestrates the complete export flow for a disease-specific CSV export.
	 *
	 * @param uuid
	 *            the UUID of the export
	 * @param exportFunction
	 *            the function that performs the disease-specific data export
	 * @param csvStrategy
	 *            the strategy that defines CSV columns and row writing
	 */
	public void orchestrateExport(String uuid, ExportFunction exportFunction, CsvExportStrategy csvStrategy) {

		CSVWriter writer = null;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		EpipulseExport epipulseExport = null;
		EpipulseExportStatus exportStatus = EpipulseExportStatus.FAILED;
		boolean shouldUpdateStatus = false;

		Integer totalRecords = null;
		BigDecimal exportFileSizeBytes = null;
		String exportFileName = null;
		String exportFilePath = null;

		try {
			// Validation
			epipulseExport = epipulseExportService.getByUuid(uuid);

			if (epipulseExport == null) {
				logger.error("EpipulseExport with uuid " + uuid + " not found");
				return;
			}

			// Atomic status claim: try to update from PENDING to IN_PROGRESS
			boolean claimed = diseaseExportService.tryClaimExportForProcessing(uuid);
			if (!claimed) {
				logger.info("Export {} not claimed - either already processing or not in PENDING status", uuid);
				return;
			}

			shouldUpdateStatus = true;

			// Load configuration
			EpipulseExportDto exportDto = epipulseExportEjb.toEpipulseExportDto(epipulseExport);
			String serverCountryCode = configFacadeEjb.getCountryCode();
			String serverCountryName = configFacadeEjb.getCountryName();

			// Setup file path
			String generatedFilesPath = configFacadeEjb.getGeneratedFilesPath();
			exportFileName = diseaseExportService.generateDownloadFileName(exportDto, epipulseExport.getId());
			exportFilePath = generatedFilesPath + "/" + exportFileName;

			// Execute disease-specific export
			EpipulseDiseaseExportResult exportResult = exportFunction.execute(exportDto, serverCountryCode, serverCountryName);
			totalRecords = exportResult.getExportEntryList().size();

			// Setup CSV writer with explicit stream management
			fos = new FileOutputStream(exportFilePath);
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			writer = CSVUtils.createCSVWriter(osw, configFacadeEjb.getCsvSeparator());

			// Build column names using strategy
			List<String> columnNames = csvStrategy.buildColumnNames(exportResult);

			// Write headers
			writer.writeNext(columnNames.toArray(new String[columnNames.size()]));

			// Write entries using strategy
			for (EpipulseDiseaseExportEntryDto dto : exportResult.getExportEntryList()) {
				String[] exportLine = new String[columnNames.size()];
				csvStrategy.writeEntryRow(dto, exportLine, exportResult);
				writer.writeNext(exportLine);
			}

			exportStatus = EpipulseExportStatus.COMPLETED;
		} catch (Exception e) {
			exportStatus = EpipulseExportStatus.FAILED;
			logger.error("Error during export with uuid " + uuid + ": " + e.getMessage(), e);
		} finally {
			// Close resources in reverse order
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to close CSVWriter for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to close OutputStreamWriter for uuid " + uuid + ": " + e.getMessage(), e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					logger.error("CRITICAL: Failed to close FileOutputStream for uuid " + uuid + ": " + e.getMessage(), e);
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

			// Update final status
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

	/**
	 * Functional interface for disease-specific export operations.
	 */
	@FunctionalInterface
	public interface ExportFunction {

		/**
		 * Executes the disease-specific data export.
		 *
		 * @param dto
		 *            the export DTO
		 * @param countryCode
		 *            the country code
		 * @param countryName
		 *            the country name
		 * @return the export result containing entries and max counts
		 * @throws SQLException
		 *             if database error occurs
		 */
		EpipulseDiseaseExportResult execute(EpipulseExportDto dto, String countryCode, String countryName) throws SQLException;
	}
}
