/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

public class MergeCasesViewConfiguration {

	/**
	 * Used to block the initial loading of the duplicates and show a warning message
	 * until the user edits the filters and loads the list of cases
	 */
	private boolean filtersApplied;

	public MergeCasesViewConfiguration() {
	}

	public MergeCasesViewConfiguration(boolean filtersApplied) {
		this.filtersApplied = filtersApplied;
	}

	public boolean isFiltersApplied() {
		return filtersApplied;
	}

	public void setFiltersApplied(boolean filtersApplied) {
		this.filtersApplied = filtersApplied;
	}
}
