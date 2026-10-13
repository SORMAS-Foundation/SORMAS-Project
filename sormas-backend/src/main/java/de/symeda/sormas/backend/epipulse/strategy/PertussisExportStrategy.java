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
 * Export strategy for Pertussis (PERT) disease exports.
 * Pertussis uses only the common fields and CTEs - no disease-specific extensions.
 */
@Stateless
@LocalBean
public class PertussisExportStrategy extends AbstractEpipulseDiseaseExportStrategy {

	@Override
	protected String buildDiseaseExportQuery() {
		// Build query using common CTEs only (no epidata fields needed for Pertussis)
		String ctes = sqlCteBuilder.joinCtes(
			sqlCteBuilder.buildVariablesCte(),
			sqlCteBuilder.buildConfigDataCte(),
			sqlCteBuilder.buildFilteredCasesCte(false),
			sqlCteBuilder.buildPreviousHospitalizationsCte(),
			sqlCteBuilder.buildSamplesCte(),
			sqlCteBuilder.buildPathogenTestsCte(),
			sqlCteBuilder.buildImmunizationsCte(),
			sqlCteBuilder.buildVaccinationsCte());

		// Main SELECT clause with common fields only
		return ctes + sqlCteBuilder.buildMainSelectClause();
	}

	@Override
	protected void mapDiseaseSpecificFields(EpipulseDiseaseExportEntryDto dto, Object[] row, int startIndex) {
		// Pertussis has no disease-specific fields beyond the common 0-27
		// This method is intentionally empty
	}

	@Override
	protected void calculateDiseaseSpecificMaxCounts(List<EpipulseDiseaseExportEntryDto> entries, EpipulseDiseaseExportResult result) {
		// Pertussis has no disease-specific max counts
		// Only maxPathogenTests and maxImmunizations are tracked (handled by common mapper)
	}
}
