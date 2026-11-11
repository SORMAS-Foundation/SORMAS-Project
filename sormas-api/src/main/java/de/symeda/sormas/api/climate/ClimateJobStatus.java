/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.climate;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Where a climate job stands. QUEUED, RUNNING and PAUSED_AT_LIMIT jobs are worked through by the job processor; the others
 * are finished.
 */
public enum ClimateJobStatus {

	QUEUED,
	RUNNING,
	/** OpenWeather's daily limit was reached; the job resumes after the limit resets (00:00 UTC). */
	PAUSED_AT_LIMIT,
	/** The nightly fetch stopped at OpenWeather's daily limit; the next night's fetch covers the same days again. */
	STOPPED_AT_LIMIT,
	DONE,
	FAILED,
	CANCELLED;

	public boolean isOpen() {
		return this == QUEUED || this == RUNNING || this == PAUSED_AT_LIMIT;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
