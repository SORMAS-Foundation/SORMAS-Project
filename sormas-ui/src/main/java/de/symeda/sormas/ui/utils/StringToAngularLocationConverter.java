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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import com.vaadin.v7.data.util.converter.StringToDoubleConverter;

public final class StringToAngularLocationConverter extends StringToDoubleConverter {
	
	private static final long serialVersionUID = -8697124581004777191L;

	protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
		
		DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getNumberInstance(locale);
		numberFormat.setGroupingUsed(false);
		numberFormat.setMaximumFractionDigits(5);

		return numberFormat;
	}
	
	@Override
	protected Number convertToNumber(String value, Class<? extends Number> targetType, Locale locale)
			throws ConversionException {
		
		return Optional.ofNullable(value)
		.map(v -> v.replace(',', '.'))
		.map(v -> super.convertToNumber(v, targetType, Locale.ENGLISH))
		.orElse(null);
	}
	
}