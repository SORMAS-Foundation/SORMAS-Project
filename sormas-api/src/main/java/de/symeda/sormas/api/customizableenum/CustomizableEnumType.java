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

package de.symeda.sormas.api.customizableenum;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.SpecificRisk;
import de.symeda.sormas.api.person.OccupationType;

/**
 * An enum storing all enumerations that support customization.
 */
public enum CustomizableEnumType {

	DISEASE_VARIANT(DiseaseVariant.class),
	SPECIFIC_EVENT_RISK(SpecificRisk.class),
	OCCUPATION_TYPE(OccupationType.class);

	private static final Logger logger = LoggerFactory.getLogger(CustomizableEnumType.class);

	private final Class<? extends CustomizableEnum> enumClass;

	CustomizableEnumType(Class<? extends CustomizableEnum> enumClass) {
		this.enumClass = enumClass;
	}

	public static CustomizableEnumType getByEnumClass(Class<? extends CustomizableEnum> enumClass) {
		for (CustomizableEnumType enumType : values()) {
			if (enumType.getEnumClass() == enumClass) {
				return enumType;
			}
		}

		return null;
	}

	public Class<? extends CustomizableEnum> getEnumClass() {
		return enumClass;
	}

	@Override
	public String toString() {
		return WordUtils.capitalize(name().toLowerCase(), '_').replace("_", "");
	}

}
