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

package de.symeda.sormas.ui.events;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.event.EventIndexDto.EventIndexLocation;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.ui.UiUtil;
import elemental.json.JsonValue;

public class EventIndexLocationRenderer extends TextRenderer {

	@Override
	public JsonValue encode(Object value) {

		if (value != null && (value.getClass().equals(EventIndexLocation.class))) {
			if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
				return super.encode(((EventIndexLocation) value).getAddress());
			} else {
				return super.encode(((EventIndexLocation) value).buildCaption());
			}
		} else {
			return null;
		}
	}
}
