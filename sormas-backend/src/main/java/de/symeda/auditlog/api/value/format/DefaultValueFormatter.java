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
package de.symeda.auditlog.api.value.format;

import java.util.Objects;

import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.auditlog.api.value.DefaultValueContainer;
import de.symeda.auditlog.api.value.SimpleValueContainer;
import de.symeda.auditlog.api.value.ValueContainer;
import de.symeda.sormas.api.HasUuid;

/**
 * Default formatter for {@link AuditedAttribute} that supports the following types:
 * <ol>
 * <li>{@link Enum} -> {@code value.name()}</li>
 * <li>{@link HasUuid} -> {@code value.getUuid()}</li>
 * <li>{@code null} -> {@link ValueContainer#DEFAULT_NULL_STRING}</li>
 * <li>Others -> {@code value.toString()}</li>
 * </ol>
 * <p/>
 * Implementation is compatible to the implementations in {@link DefaultValueContainer}.
 * 
 * @author Oliver Milke
 * @since 08.04.2016
 */
public class DefaultValueFormatter implements ValueFormatter<Object> {

	private final EnumFormatter enumFormatter = new EnumFormatter();

	@Override
	public String format(Object value) {

		if (value instanceof Enum) {
			return enumFormatter.format((Enum<?>) value);
		} else if (value instanceof HasUuid) {
			HasUuid uuidObject = (HasUuid) value;

			// never null because of instanceof
			return uuidObject.getUuid();
		} else {
			return Objects.toString(value, SimpleValueContainer.DEFAULT_NULL_STRING);
		}
	}
}
