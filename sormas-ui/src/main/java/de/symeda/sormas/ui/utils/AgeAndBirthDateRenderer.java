package de.symeda.sormas.ui.utils;


import com.vaadin.ui.renderers.AbstractRenderer;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.person.PersonHelper;
import elemental.json.JsonValue;

public class AgeAndBirthDateRenderer extends AbstractRenderer<Object, AgeAndBirthDateDto> {
	public AgeAndBirthDateRenderer() {
		super(AgeAndBirthDateDto.class, "");
	}

	@Override
	public JsonValue encode(AgeAndBirthDateDto value) {
		return encode(PersonHelper.getAgeAndBirthdateString(value.getAge(), value.getAgeType(), value.getBirthdateDD(), value.getBirthdateMM(), value.getBirthdateYYYY(), FacadeProvider.getUserFacade().getCurrentUser().getLanguage()), String.class);
	}
}
