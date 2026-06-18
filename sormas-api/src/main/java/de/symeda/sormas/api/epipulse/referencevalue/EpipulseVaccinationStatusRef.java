/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.epipulse.referencevalue;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpipulseVaccinationStatusRef {

	TEN_DOSE("10DOSE"),
	ONE_DOSE("1DOSE"),
	TWO_DOSE("2DOSE"),
	THREE_DOSE("3DOSE"),
	FOUR_DOSE("4DOSE"),
	FIVE_DOSE("5DOSE"),
	SIX_DOSE("6DOSE"),
	SEVEN_DOSE("7DOSE"),
	EIGHT_DOSE("8DOSE"),
	NINE_DOSE("9DOSE"),
	NOTVACC("NOTVACC"),
	UNKDOSE("UNKDOSE");

	private final String code;

	EpipulseVaccinationStatusRef(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
