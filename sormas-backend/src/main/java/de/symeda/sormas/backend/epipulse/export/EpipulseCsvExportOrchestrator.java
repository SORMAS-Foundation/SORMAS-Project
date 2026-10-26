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

package de.symeda.sormas.backend.epipulse.export;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

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
import de.symeda.sormas.backend.epipulse.EpipulseExport;
import de.symeda.sormas.backend.epipulse.EpipulseExportFacadeEjb;
import de.symeda.sormas.backend.epipulse.EpipulseExportService;
import de.symeda.sormas.backend.epipulse.fieldmodel.ExportLayout;
import de.symeda.sormas.backend.epipulse.fieldmodel.FieldModel;

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
	 * @param fieldModelSupplier
	 *            builds the field model that defines the CSV columns and row writing; called inside
	 *            the export's error handling, so a model that fails to build marks the export FAILED
	 *            instead of leaving it PENDING
	 * @param exportFunction
	 *            the function that performs the disease-specific data export
	 */
	public void orchestrateExport(String uuid, Supplier<FieldModel> fieldModelSupplier, ExportFunction exportFunction) {

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
				logger.error("EpipulseExport with uuid {} not found", uuid);
				return;
			}

			// Atomic status claim: try to update from PENDING to IN_PROGRESS
			boolean claimed = diseaseExportService.tryClaimExportForProcessing(uuid);
			if (!claimed) {
				logger.info("Export {} not claimed - either already processing or not in PENDING status", uuid);
				return;
			}

			shouldUpdateStatus = true;

			FieldModel fieldModel = fieldModelSupplier.get();

			// Load configuration
			EpipulseExportDto exportDto = epipulseExportEjb.toEpipulseExportDto(epipulseExport);
			String serverCountryCode = configFacadeEjb.getCountryCode();
			String serverCountryName = configFacadeEjb.getCountryName();

			// Setup file path
			String generatedFilesPath = configFacadeEjb.getGeneratedFilesPath();
			exportFileName = diseaseExportService.generateDownloadFileName(exportDto, epipulseExport.getId());
			exportFilePath = generatedFilesPath + "/" + exportFileName;

			// Execute disease-specific export
			EpipulseDiseaseExportResult exportResult = exportFunction.execute(fieldModel, exportDto, serverCountryCode, serverCountryName);
			totalRecords = exportResult.getExportEntryList().size();

			// Setup CSV writer with try-with-resources for automatic resource management
			try (FileOutputStream fos = new FileOutputStream(exportFilePath);
				OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				CSVWriter writer = CSVUtils.createCSVWriter(osw, configFacadeEjb.getCsvSeparator())) {

				// Measure the repeatable columns against this export's cases, then write. The
				// layout owns both the header and the rows, so a row cannot disagree with the
				// header it was sized against. Each column's value comes from the EpipulseMapping
				// method its ValueDef names, so nothing is derived here.
				ExportLayout layout = fieldModel.layout(exportResult.getExportEntryList());

				List<String> columnNames = layout.columnNames();
				writer.writeNext(columnNames.toArray(new String[columnNames.size()]));

				for (EpipulseDiseaseExportEntryDto entry : exportResult.getExportEntryList()) {
					writer.writeNext(layout.row(entry));
				}
			}

			exportStatus = EpipulseExportStatus.COMPLETED;
		} catch (Exception e) {
			exportStatus = EpipulseExportStatus.FAILED;
			logger.error("Error during export with uuid {}: {}", uuid, e.getMessage(), e);
		} finally {
			// Cleanup of partial files when export fails
			if (exportStatus != EpipulseExportStatus.COMPLETED && exportFilePath != null) {
				try {
					Files.deleteIfExists(Paths.get(exportFilePath));
				} catch (Exception e) {
					logger.warn("Failed to delete partial export file for uuid {}: {}", uuid, e.getMessage(), e);
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
					logger.error("CRITICAL: Failed to update export status for uuid {}: {}", uuid, e.getMessage(), e);
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
		 * @param fieldModel
		 *            the field model that selects and reads the rows
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
		EpipulseDiseaseExportResult execute(FieldModel fieldModel, EpipulseExportDto dto, String countryCode, String countryName) throws SQLException;
	}
}
