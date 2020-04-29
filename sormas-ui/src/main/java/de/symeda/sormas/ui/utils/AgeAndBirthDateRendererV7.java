package de.symeda.sormas.ui.utils;

import com.vaadin.v7.ui.Grid;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import elemental.json.JsonValue;

public class AgeAndBirthDateRendererV7 extends Grid.AbstractRenderer<AgeAndBirthDateDto> {

	public AgeAndBirthDateRendererV7() {
		super(AgeAndBirthDateDto.class, "");
	}

	@Override
	public JsonValue encode(AgeAndBirthDateDto value) {
		if(value == null){
			return encode(getNullRepresentation(), String.class);
		}

		Language userLanguage = I18nProperties.getUserLanguage();
		String dateString = PersonHelper.getAgeAndBirthdateString(value.getAge(), value.getAgeType(), value.getBirthdateDD(), value.getBirthdateMM(), value.getBirthdateYYYY(), userLanguage);

		return encode(dateString, String.class);
	}
}
