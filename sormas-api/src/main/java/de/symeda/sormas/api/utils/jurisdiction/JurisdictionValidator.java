/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.jurisdiction;

import de.symeda.sormas.api.user.JurisdictionLevel;

public abstract class JurisdictionValidator<T> {

	public T isInJurisdiction(JurisdictionLevel jurisdictionLevel) {

		switch (jurisdictionLevel) {

		case NONE:
			return whenNotAllowed();
		case NATION:
			return whenNationalLevel();
		case REGION:
			return whenRegionalLevel();
		case DISTRICT:
			return whenDistrictLevel();
		case COMMUNITY:
			return whenCommunityLevel();
		case HEALTH_FACILITY:
			return whenFacilityLevel();
		case LABORATORY:
			return whenNotAllowed();
		case EXTERNAL_LABORATORY:
			return whenNotAllowed();
		case POINT_OF_ENTRY:
			return whenPointOfEntryLevel();
		default:
			return whenNotAllowed();
		}
	}

	protected abstract T whenNotAllowed();

	protected abstract T whenNationalLevel();

	protected abstract T whenRegionalLevel();

	protected abstract T whenDistrictLevel();

	protected abstract T whenCommunityLevel();

	protected abstract T whenFacilityLevel();

	protected abstract T whenPointOfEntryLevel();
}
