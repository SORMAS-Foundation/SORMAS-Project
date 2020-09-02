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
package de.symeda.auditlog.api.value.format.override;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Temporal;

import de.symeda.auditlog.api.value.format.UtilDateFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * @author Oliver Milke
 * @since 11.04.2016
 */
public class DateFormatOverrideDetector implements OverrideDetector<Date> {

	/**
	 * Returns an adjusted {@link ValueFormatter} for an entity property as long as this is applicable according to the the method
	 * description.
	 * 
	 * @param m
	 *            The method that describes the property.
	 * @return Returns <code>null</code> if no reasonable derivation can be found. Returns a ValueFormatter that is adjusted to the value of
	 *         the
	 *         {@link Temporal} annotation, if present.
	 */
	@Override
	public ValueFormatter<Date> override(Method m) {

		if (!Date.class.isAssignableFrom(m.getReturnType())) {
			// no reasonable derivation possible
			return null;
		} else {
			// generally possible
			Temporal annotation = m.getAnnotation(Temporal.class);

			if (annotation == null) {
				return new UtilDateFormatter();
			} else {
				return new UtilDateFormatter(annotation.value());
			}
		}
	}
}
