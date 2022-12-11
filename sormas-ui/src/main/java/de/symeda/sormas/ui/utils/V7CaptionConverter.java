/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.utils.HasCaption;

public class V7CaptionConverter implements Converter<String, HasCaption> {

	@Override
	public HasCaption convertToModel(String s, Class<? extends HasCaption> aClass, Locale locale) throws ConversionException {
        throw new RuntimeException("Not implemented");
	}

	@Override
	public String convertToPresentation(HasCaption hasCaption, Class<? extends String> aClass, Locale locale) throws ConversionException {
		return hasCaption.buildCaption();
	}

	@Override
	public Class<HasCaption> getModelType() {
		return HasCaption.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
