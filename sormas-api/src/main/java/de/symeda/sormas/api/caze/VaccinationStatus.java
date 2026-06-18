/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum VaccinationStatus {

	VACCINATED,

	UNVACCINATED,

	// Legacy alias kept for backward compatibility with existing data and integrations.
	@Diseases()
	RECOVERED,

	HAD_THE_DISEASE,

	OTHER,

	UNKNOWN;

	@Override
	public String toString() {
		// Keep RECOVERED and HAD_THE_DISEASE visually aligned in the UI.
		return this == RECOVERED ? I18nProperties.getEnumCaption(HAD_THE_DISEASE) : I18nProperties.getEnumCaption(this);
	}

	/**
	 * Checks if this vaccination status indicates the person is vaccinated.
	 *
	 * @return true if the status is VACCINATED; false otherwise
	 */
	public boolean isVaccinated() {
		return this == VACCINATED;
	}

	/**
	 * Checks whether this status represents immunity through prior disease.
	 */
	public boolean isHadTheDisease() {
		return this == HAD_THE_DISEASE || this == RECOVERED;
	}

	/**
	 * Normalizes legacy RECOVERED to HAD_THE_DISEASE.
	 */
	public VaccinationStatus normalize() {
		return this == RECOVERED ? HAD_THE_DISEASE : this;
	}
}
