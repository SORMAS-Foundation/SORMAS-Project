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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * The result status of a sequencing/molecular test run.
 *
 * <ul>
 * <li>{@link #PENDING} - The test run has not yet produced a result.</li>
 * <li>{@link #COMPLETED} - The test run finished successfully and produced a valid result (equivalent to a positive result for this kind of
 * test run).</li>
 * <li>{@link #FAILED} - The test run did not produce a usable result (equivalent to a negative result for this kind of test run).</li>
 * <li>{@link #INDETERMINATE} - The test run finished but the result could not be clearly determined.</li>
 * </ul>
 */
public enum TestRunStatus {

	PENDING,
	COMPLETED,
	FAILED,
	INDETERMINATE;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
