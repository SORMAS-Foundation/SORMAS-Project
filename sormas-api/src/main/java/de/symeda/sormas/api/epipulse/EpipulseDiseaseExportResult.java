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

package de.symeda.sormas.api.epipulse;

import java.util.List;

public class EpipulseDiseaseExportResult {

	private int maxPathogenTests;
	private int maxImmunizations;
	private List<EpipulseDiseaseExportEntryDto> exportEntryList;

	public int getMaxPathogenTests() {
		return maxPathogenTests;
	}

	public void setMaxPathogenTests(int maxPathogenTests) {
		this.maxPathogenTests = maxPathogenTests;
	}

	public int getMaxImmunizations() {
		return maxImmunizations;
	}

	public void setMaxImmunizations(int maxImmunizations) {
		this.maxImmunizations = maxImmunizations;
	}

	public List<EpipulseDiseaseExportEntryDto> getExportEntryList() {
		return exportEntryList;
	}

	public void setExportEntryList(List<EpipulseDiseaseExportEntryDto> exportEntryList) {
		this.exportEntryList = exportEntryList;
	}
}
