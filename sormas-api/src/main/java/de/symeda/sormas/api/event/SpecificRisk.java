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

package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;

/**
 * A customizable enum containing specific risks different than the one specified in {@link RiskLevel}.
 */
public class SpecificRisk extends CustomizableEnum implements Serializable {

	private static final long serialVersionUID = 7727639877710862924L;

	@Override
	public void setProperties(Map<String, Object> properties) {
		// No properties
	}

	@Override
	public boolean matchPropertyValue(String property, Object value) {
		return false;
	}

	@Override
	public Map<String, Class<?>> getAllProperties() {
		return Collections.emptyMap();
	}
}
