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

package de.symeda.sormas.ui.configuration.customizableenums;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import elemental.json.JsonValue;

public class CustomizableEnumsDiseasesRenderer extends TextRenderer {

	@Override
	public JsonValue encode(Object value) {

		if (!(value instanceof List)) {
			return super.encode(I18nProperties.getCaption(Captions.customizableEnumValueAllDiseases));
		}

		List<Disease> diseases = (List<Disease>) value;

		if (diseases.size() == 0) {
			return super.encode(I18nProperties.getCaption(Captions.customizableEnumValueAllDiseases));
		}

		if (diseases.size() > 10) {
			return super.encode(String.format(I18nProperties.getCaption(Captions.customizableEnumValueDiseaseCount), diseases.size()));
		}

		return super.encode(diseases.stream().map(Disease::toString).collect(Collectors.joining(", ")));
	}

}
