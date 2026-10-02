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

package de.symeda.sormas.api.epipulse;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.SyphilisPresentation;

/**
 * Subset selector for diseases where multiple EpiPulse subject codes share one SORMAS disease.
 *
 * <p>
 * Example: {@code SYPH} and {@code CONSYPH} both map to syphilis and are separated by
 * {@link CaseDataDto#SYPHILIS_PRESENTATION}.
 *
 * <p>
 * Each enum value defines the case column and value used in SQL filtering. Column names are fixed
 * by this enum; values must still be passed as query parameters.
 *
 * <p>
 * A null discriminator value matches no subset.
 *
 * @apiNote Currently used for syphilis subsets.
 */
public enum EpipulseCaseSubset {

	/** Acquired syphilis ({@code SYPH}). */
	SYPHILIS_ACQUIRED("syphilispresentation", SyphilisPresentation.ACQUIRED.name()),

	/** Congenital syphilis ({@code CONSYPH}). */
	SYPHILIS_CONGENITAL("syphilispresentation", SyphilisPresentation.CONGENITAL.name());

	private final String caseColumn;
	private final String value;

	EpipulseCaseSubset(String caseColumn, String value) {
		this.caseColumn = caseColumn;
		this.value = value;
	}

	/**
	 * @return the column on {@code cases} that distinguishes the subsets of this disease
	 */
	public String getCaseColumn() {
		return caseColumn;
	}

	/**
	 * @return the value that column holds for this subset
	 */
	public String getValue() {
		return value;
	}
}
