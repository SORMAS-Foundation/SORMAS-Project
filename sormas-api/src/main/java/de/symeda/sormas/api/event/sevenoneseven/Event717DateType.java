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
package de.symeda.sormas.api.event.sevenoneseven;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

/**
 * 7-1-7 milestone dates the event directory and the 7-1-7 summary can be filtered by. They are only evaluated for 7-1-7
 * assessments; the event list ignores them.
 */
public enum Event717DateType
	implements
	CriteriaDateType {

	DATE_OF_EMERGENCE,
	DATE_OF_DETECTION,
	DATE_OF_NOTIFICATION;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
