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

package de.symeda.sormas.api.caze;

import javax.annotation.Nullable;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum Vaccine {

	PFIZER_BIONTECH(VaccineManufacturer.PFIZER_BIONTECH),
	MODERNA(VaccineManufacturer.MODERNA),
	OXFORD_ASTRA_ZENECA(VaccineManufacturer.ASTRA_ZENECA),
	Ad26_COV2_S(VaccineManufacturer.JOHNSON_JOHNSON),
	NVX_COV_2373(VaccineManufacturer.NOVAVAX),
	SANOFI_GSK(VaccineManufacturer.SANOFI_GLAXOSMITHKLINE),
	UNKOWN,
	OTHER;

	@Nullable
	private VaccineManufacturer manufacturer;

	Vaccine() {
	}

	Vaccine(VaccineManufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public VaccineManufacturer getManufacturer() {
		return manufacturer;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
