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
package de.symeda.sormas.ui.events.sevenoneseven;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;

/**
 * Display of the free texts of a 7-1-7 assessment that the server has hidden, e.g. because the event is outside the user's
 * jurisdiction.
 */
public final class Event717FieldAccess {

	private Event717FieldAccess() {
		// Hide Utility Class Constructor
	}

	/**
	 * @return Access checkers that disable the sensitive fields of the forms if the assessment is pseudonymized.
	 */
	@SuppressWarnings("rawtypes")
	public static UiFieldAccessCheckers createFieldAccessCheckers(boolean isPseudonymized) {
		return UiFieldAccessCheckers.forSensitiveData(isPseudonymized, FacadeProvider.getConfigFacade().getCountryLocale());
	}

	/**
	 * @return The value, or the caption for an inaccessible value if the assessment is pseudonymized and the value has been hidden.
	 */
	public static String displayValue(String value, boolean isPseudonymized) {
		return isPseudonymized && StringUtils.isEmpty(value) ? I18nProperties.getCaption(Captions.inaccessibleValue) : value;
	}
}
