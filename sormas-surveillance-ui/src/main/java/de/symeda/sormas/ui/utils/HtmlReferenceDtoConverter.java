package de.symeda.sormas.ui.utils;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

@SuppressWarnings("serial")
public class HtmlReferenceDtoConverter implements Converter<String,ReferenceDto> {

	@Override
	public ReferenceDto convertToModel(String value, Class<? extends ReferenceDto> targetType, Locale locale) throws ConversionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String convertToPresentation(ReferenceDto value, Class<? extends String> targetType, Locale locale) throws ConversionException {
		String html;
		if (value != null) {
			String uuid = value.getUuid();
			html = "<a title='" + uuid + "'>" + DataHelper.getShortUuid(uuid) + " (" + value.getCaption() + ")</a>";
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
