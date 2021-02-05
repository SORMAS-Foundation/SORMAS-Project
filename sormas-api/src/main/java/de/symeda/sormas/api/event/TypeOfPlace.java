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
package de.symeda.sormas.api.event;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum TypeOfPlace {

	FACILITY(true),
	FESTIVITIES(false),
	HOME(true),
	MEANS_OF_TRANSPORT(false),
	PUBLIC_PLACE(false),
	SCATTERED(false),
	UNKNOWN(false),
	OTHER(false);

	private static List<TypeOfPlace> typesUsableForCases = null;

	private boolean usableForCases;

	TypeOfPlace(boolean usableForCases) {
		this.usableForCases = usableForCases;
	}

	public boolean isUsableForCases() {
		return usableForCases;
	}

	public static List<TypeOfPlace> getTypesOfPlaceForCases() {
		if (typesUsableForCases == null) {
			typesUsableForCases = new ArrayList<>();
			for (TypeOfPlace typeOfPlace : values()) {
				if (typeOfPlace.isUsableForCases()) {
					typesUsableForCases.add(typeOfPlace);
				}
			}
		}
		return typesUsableForCases;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
