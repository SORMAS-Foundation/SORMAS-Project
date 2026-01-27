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

import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportEntryDto;
import de.symeda.sormas.api.epipulse.EpipulseDiseaseExportResult;

/**
 * Strategy interface for disease-specific CSV export generation.
 * Implementations define column names and row writing logic for each disease.
 */
public interface CsvExportStrategy {

	/**
	 * Builds the CSV column names list for this disease.
	 *
	 * @param exportResult the export result containing max counts for repeatable fields
	 * @return ordered list of column names
	 */
	List<String> buildColumnNames(EpipulseDiseaseExportResult exportResult);

	/**
	 * Writes one export entry to the exportLine array.
	 *
	 * @param dto the entry to write
	 * @param exportLine the output array (pre-sized to column count)
	 * @param exportResult the export result containing max counts
	 * @return the final index written (for validation)
	 */
	int writeEntryRow(EpipulseDiseaseExportEntryDto dto, String[] exportLine, EpipulseDiseaseExportResult exportResult);
}
