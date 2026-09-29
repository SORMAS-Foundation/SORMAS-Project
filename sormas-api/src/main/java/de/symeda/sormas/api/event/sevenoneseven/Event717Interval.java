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
package de.symeda.sormas.api.event.sevenoneseven;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * The three intervals of the 7-1-7 target with their fixed target durations in days.
 */
public enum Event717Interval {

	/** From emergence to detection. */
	DETECTION(7),
	/** From detection to notification. */
	NOTIFICATION(1),
	/** From notification to completion of the last early response action. */
	RESPONSE(7);

	private final int targetDays;

	Event717Interval(int targetDays) {
		this.targetDays = targetDays;
	}

	public int getTargetDays() {
		return targetDays;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
