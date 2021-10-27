package de.symeda.sormas.ui.utils.components.linelisting.person;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.birthdate.BirthDateField;

public class PersonField extends CustomField<PersonFieldDto> {

	private final Binder<PersonFieldDto> binder = new Binder<>(PersonFieldDto.class);

	private final TextField firstname;
	private final TextField lastname;
	private final BirthDateField birthDate;
	private final TextField approximateAge;
	private final ComboBox<ApproximateAgeType> approximateAgeType;
	private final ComboBox<Sex> sex;

	public PersonField() {
		firstname = new TextField();
		lastname = new TextField();
		birthDate = new BirthDateField();
		approximateAge = new TextField();
		approximateAgeType = new ComboBox<>();
		sex = new ComboBox<>();
	}

	@Override
	protected Component initContent() {
		if (getValue() == null) {
			setValue(new PersonFieldDto());
		}

		HorizontalLayout layout = new HorizontalLayout();

		firstname.setId("firstName");
		binder.forField(firstname).asRequired().bind(PersonFieldDto.FIRST_NAME);

		lastname.setId("lastName");
		binder.forField(lastname).asRequired().bind(PersonFieldDto.LAST_NAME);

//		birthDate.setId("birthDate");
//		binder.forField(birthDate).bind(PersonFieldDto.BIRTH_DATE);
		approximateAge.setId("approximateAge");
		
		binder.forField(approximateAge).withConverter(new StringToIntegerConverter("Please insert a number")).withNullRepresentation(0)
		.bind(PersonFieldDto.APPROXIMATE_AGE);
//		addFieldListeners(PersonDto.APPROXIMATE_AGE, e -> {
//			@SuppressWarnings("unchecked")
//			Field<ApproximateAgeType> ageTypeField = (Field<ApproximateAgeType>) getField(PersonDto.APPROXIMATE_AGE_TYPE);
//			if (!ageTypeField.isReadOnly()) {
//				if (e.getProperty().getValue() == null) {
//					ageTypeField.clear();
//				} else {
//					if (ageTypeField.isEmpty()) {
//						ageTypeField.setValue(ApproximateAgeType.YEARS);
//					}
//				}
//			}
//		});
		
		approximateAgeType.setId("approximateAgeType");
//		approximateAgeType.setValue(ApproximateAgeType.YEARS);
		approximateAgeType.setItems(ApproximateAgeType.values());
		approximateAgeType.setWidth(100, Unit.PIXELS);
		binder.forField(approximateAgeType).bind(PersonFieldDto.APPROXIMATE_AGE_TYPE);
		sex.setId("sex");
		sex.setItems(Sex.values());
		sex.setWidth(100, Unit.PIXELS);
		binder.forField(sex).asRequired().bind(PersonFieldDto.SEX);

		layout.addComponents(firstname, lastname, approximateAge, approximateAgeType, sex);

		return layout;
	}

	@Override
	protected void doSetValue(PersonFieldDto personFieldDto) {
		binder.setBean(personFieldDto);
	}

	@Override
	public PersonFieldDto getValue() {
		return binder.getBean();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		firstname.setEnabled(enabled);
		lastname.setEnabled(enabled);
		approximateAge.setEnabled(enabled);
		approximateAgeType.setEnabled(enabled);
//		birthDate.setEnabled(enabled);
		sex.setEnabled(enabled);
	}

	public BinderValidationStatus<PersonFieldDto> validate() {
		return binder.validate();
	}

	public void showCaptions() {
		firstname.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.FIRST_NAME));
		firstname.removeStyleName(CssStyles.CAPTION_HIDDEN);
		lastname.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.LAST_NAME));
		lastname.removeStyleName(CssStyles.CAPTION_HIDDEN);
		
//		birthDate.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.BIRTH_DATE));
		approximateAge.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.APPROXIMATE_AGE));
		approximateAgeType.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.APPROXIMATE_AGE_TYPE));
		sex.setCaption(I18nProperties.getPrefixCaption(PersonFieldDto.I18N_PREFIX, PersonFieldDto.SEX));
		sex.removeStyleName(CssStyles.CAPTION_HIDDEN);
	}

	public void hideCaptions() {
		CssStyles.style(firstname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
		CssStyles.style(lastname, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
		CssStyles.style(sex, CssStyles.SOFT_REQUIRED, CssStyles.CAPTION_HIDDEN);
	}
}
