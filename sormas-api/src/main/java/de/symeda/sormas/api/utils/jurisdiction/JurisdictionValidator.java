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

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.user.JurisdictionLevel;

public abstract class JurisdictionValidator<T> {

	private List<? extends JurisdictionValidator<T>> associatedJurisdictionValidators;

	public JurisdictionValidator(List<? extends JurisdictionValidator<T>> associatedJurisdictionValidators) {
		this.associatedJurisdictionValidators = associatedJurisdictionValidators;
	}

	public T inJurisdictionOrOwned() {
		if (associatedJurisdictionValidators != null && !associatedJurisdictionValidators.isEmpty()) {
			final List<T> jurisdictionTypes = new ArrayList<>();
			jurisdictionTypes.add(isInJurisdictionOrOwned());
			for (JurisdictionValidator<T> jurisdictionValidator : associatedJurisdictionValidators) {
				if (jurisdictionValidator != null) {
					jurisdictionTypes.add(jurisdictionValidator.isInJurisdictionOrOwned());
				}
			}
			return or(jurisdictionTypes);
		} else {
			return isInJurisdictionOrOwned();
		}
	}

	public T inJurisdiction() {
		if (associatedJurisdictionValidators != null && !associatedJurisdictionValidators.isEmpty()) {
			final List<T> jurisdictionTypes = new ArrayList<>();
			jurisdictionTypes.add(isInJurisdiction());
			for (JurisdictionValidator<T> jurisdictionValidator : associatedJurisdictionValidators) {
				if (jurisdictionValidator != null) {
					jurisdictionTypes.add(jurisdictionValidator.isInJurisdiction());
				}
			}
			return or(jurisdictionTypes);
		} else {
			return isInJurisdiction();
		}
	}

	protected abstract T isInJurisdiction();

	protected abstract T isInJurisdictionOrOwned();

	protected T isInJurisdictionByJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {

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
			return whenLaboratoryLevel();
		case EXTERNAL_LABORATORY:
			return whenNotAllowed();
		case POINT_OF_ENTRY:
			return whenPointOfEntryLevel();
		default:
			return whenNotAllowed();
		}
	}

	protected abstract T or(List<T> jurisdictionTypes);

	protected abstract T whenNotAllowed();

	protected abstract T whenNationalLevel();

	protected abstract T whenRegionalLevel();

	protected abstract T whenDistrictLevel();

	protected abstract T whenCommunityLevel();

	protected abstract T whenFacilityLevel();

	protected abstract T whenPointOfEntryLevel();

	protected abstract T whenLaboratoryLevel();
}
