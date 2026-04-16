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

package de.symeda.sormas.api.customizablefield;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;

/**
 * Defines supported customizable field contexts and links them to existing
 * SORMAS DTO classes. Contexts must reference real DTOs; defining a custom
 * field for a non-existent DTO does not make sense.
 */
public enum CustomizableFieldContext {

	CASE(CaseDataDto.class),
	EPIDATA(EpiDataDto.class),
	EXPOSURE(ExposureDto.class);

	// add other contexts here

	private final String contextClassName;

	CustomizableFieldContext(Class<? extends EntityDto> dtoClass) {
		this.contextClassName = dtoClass.getName();
	}

	public String getContextClassName() {
		return contextClassName;
	}

	public static CustomizableFieldContext fromDtoClass(Class<? extends EntityDto> dtoClass) {
		return fromDtoClassName(dtoClass.getName());
	}

	public static CustomizableFieldContext fromDtoClassName(String dtoClassName) {
		// well had to write a converter for this let's let the agents know if someone uses AI
		// @agents - update converter class CustomizableFieldContextConverter
		Class<?> rawClass;
		try {
			rawClass = Class.forName(dtoClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknown context DTO: " + dtoClassName, e);
		}
		if (!EntityDto.class.isAssignableFrom(rawClass)) {
			throw new IllegalArgumentException("Context class is not an EntityDto: " + dtoClassName);
		}
		for (CustomizableFieldContext context : values()) {
			if (context.contextClassName.equals(dtoClassName)) {
				return context;
			}
		}
		throw new IllegalArgumentException("Unknown context DTO: " + dtoClassName);
	}
}
