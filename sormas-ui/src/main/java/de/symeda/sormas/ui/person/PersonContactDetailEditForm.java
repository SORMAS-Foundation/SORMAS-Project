package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.LayoutUtil.divs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonContactDetailDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.person.PhoneNumberType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class PersonContactDetailEditForm extends AbstractEditForm<PersonContactDetailDto> {

	private static final String HTML_LAYOUT = divs(
		fluidRowLocs(PersonContactDetailDto.THIRD_PARTY),
		fluidRowLocs(PersonContactDetailDto.THIRD_PARTY_ROLE, PersonContactDetailDto.THIRD_PARTY_NAME),
		fluidRowLocs(PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE, PersonContactDetailDto.PHONE_NUMBER_TYPE, PersonContactDetailDto.DETAILS),
		fluidRowLocs(PersonContactDetailDto.CONTACT_INFORMATION, PersonContactDetailDto.ADDITIONAL_INFORMATION),
		fluidRowLocs(PersonContactDetailDto.PRIMARY));

	public PersonContactDetailEditForm(FieldVisibilityCheckers fieldVisibilityCheckers, UiFieldAccessCheckers fieldAccessCheckers) {
		super(PersonContactDetailDto.class, PersonContactDetailDto.I18N_PREFIX, true, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		addField(PersonContactDetailDto.THIRD_PARTY, CheckBox.class);
		addField(PersonContactDetailDto.THIRD_PARTY_ROLE, TextField.class);
		addField(PersonContactDetailDto.THIRD_PARTY_NAME, TextField.class);
		addField(PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE);
		addField(PersonContactDetailDto.PHONE_NUMBER_TYPE);
		addField(PersonContactDetailDto.DETAILS, TextField.class);
		addField(PersonContactDetailDto.CONTACT_INFORMATION, TextField.class);
		addField(PersonContactDetailDto.ADDITIONAL_INFORMATION, TextField.class);
		addField(PersonContactDetailDto.PRIMARY, CheckBox.class);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PersonContactDetailDto.THIRD_PARTY_ROLE, PersonContactDetailDto.THIRD_PARTY_NAME),
			PersonContactDetailDto.THIRD_PARTY,
			true,
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PersonContactDetailDto.DETAILS),
			PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE,
			PersonContactDetailType.OTHER,
			false);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PersonContactDetailDto.DETAILS),
			PersonContactDetailDto.PHONE_NUMBER_TYPE,
			PhoneNumberType.OTHER,
			false);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(PersonContactDetailDto.PHONE_NUMBER_TYPE),
			PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE,
			PersonContactDetailType.PHONE,
			false);

		addFieldListeners(PersonContactDetailDto.PERSON_CONTACT_DETAILS_TYPE, e -> {
			final Field<?> contactInformationField = getFieldGroup().getField(PersonContactDetailDto.CONTACT_INFORMATION);
			final PersonContactDetailType value = (PersonContactDetailType) e.getProperty().getValue();
			for (Validator validator : contactInformationField.getValidators()) {
				contactInformationField.removeValidator(validator);
			}
			if (value == PersonContactDetailType.PHONE) {
				contactInformationField.addValidator(
					new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, contactInformationField.getCaption())));
			} else if (value == PersonContactDetailType.EMAIL) {
				contactInformationField.addValidator(
					new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, contactInformationField.getCaption())));
			}
		});
	}
}
