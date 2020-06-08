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
package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

@SuppressWarnings("serial")
public class StringToAnythingConverter<T> implements Converter<String, T> {

	private Class<T> modelType;

	public StringToAnythingConverter(Class<T> modelType) {
		this.modelType = modelType;
	}

	@Override
	public T convertToModel(String value, Class<? extends T> targetType, Locale locale) throws ConversionException {
		return null;
	}

	@Override
	public String convertToPresentation(T value, Class<? extends String> targetType, Locale locale) throws ConversionException {
		return value == null ? null : value.toString();
	}

	@Override
	public Class<T> getModelType() {
		return modelType;
	}

	public Class<String> getPresentationType() {
		return String.class;
	}
}
