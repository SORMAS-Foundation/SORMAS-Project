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

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

@SuppressWarnings("serial")
public class V7HtmlReferenceDtoConverter implements Converter<String, ReferenceDto> {

	@Override
	public ReferenceDto convertToModel(String value, Class<? extends ReferenceDto> targetType, Locale locale) throws ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(ReferenceDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {

		String html;
		if (value != null) {
			String uuid = value.getUuid();
			html = "<a title='" + uuid + "'>" + DataHelper.getShortUuid(uuid) + "</a> (" + value.getCaption() + ")";
		} else {
			html = "";
		}
		return html;
	}

	@Override
	public Class<ReferenceDto> getModelType() {
		return ReferenceDto.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
