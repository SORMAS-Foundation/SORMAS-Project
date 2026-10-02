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

package de.symeda.sormas.backend.epipulse.fieldmodel;

import java.util.List;

import de.symeda.sormas.api.sample.PathogenTestType;

/** Export-level context passed to readers: server NUTS code, subject-code pathogen test types. */
public final class ExportContext {

	private final String serverCountryNutsCode;
	private final List<PathogenTestType> subjectCodePathogenTestTypes;

	public ExportContext(String serverCountryNutsCode, List<PathogenTestType> subjectCodePathogenTestTypes) {
		this.serverCountryNutsCode = serverCountryNutsCode;
		this.subjectCodePathogenTestTypes = subjectCodePathogenTestTypes;
	}

	/** Server's own NUTS code (not a column). */
	public String getServerCountryNutsCode() {
		return serverCountryNutsCode;
	}

	/** Pathogen test types valid for this subject code. */
	public List<PathogenTestType> getSubjectCodePathogenTestTypes() {
		return subjectCodePathogenTestTypes;
	}
}
