/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class OutbreakFieldVisibilityChecker implements FieldVisibilityCheckers.Checker {
	private final ViewMode viewMode;

	public OutbreakFieldVisibilityChecker(ViewMode viewMode) {
		this.viewMode = viewMode;
	}


	@Override
	public boolean isVisible(Class<?> parentType, String propertyId) {
		return viewMode != ViewMode.SIMPLE ||
				Outbreaks.OutbreaksConfiguration.isDefined(parentType, propertyId);
	}
}
