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

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Defines the types of customizable fields that can be created.
 */
public enum CustomizableFieldType {

	TEXT,
	TEXTAREA,
	NUMBER,
	DECIMAL,
	DATE,
	DATE_TIME,
	COMBOBOX,
	CHECKBOX,
	YES_NO_UNKNOWN,
	CHECKBOX_LIST,
	RADIO_BUTTON_LIST;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
