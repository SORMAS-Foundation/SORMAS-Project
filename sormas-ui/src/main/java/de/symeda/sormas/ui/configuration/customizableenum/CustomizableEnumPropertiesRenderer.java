/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.customizableenum;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import elemental.json.JsonValue;

public class CustomizableEnumPropertiesRenderer extends TextRenderer {

	public JsonValue encode(Object value) {

		if (!(value instanceof Map)) {
			return super.encode(I18nProperties.getCaption(Captions.customizableEnumValueNoProperties));
		}

		Map<String, Object> properties = (Map<String, Object>) value;

		// Remove boolean properties that are set to false
		Set<String> propertiesToRemove = properties.keySet().stream().filter(k -> {
			Object propertyValue = properties.get(k);
			return propertyValue instanceof Boolean && !((Boolean) propertyValue);
		}).collect(Collectors.toSet());
		propertiesToRemove.forEach(properties::remove);

		if (properties.size() == 0) {
			return super.encode(I18nProperties.getCaption(Captions.customizableEnumValueNoProperties));
		}

		return super.encode(
			properties.keySet()
				.stream()
				.map(k -> I18nProperties.getPrefixCaptionShort(CustomizableEnum.I18N_PREFIX, k))
				.collect(Collectors.joining(", ")));
	}

}
