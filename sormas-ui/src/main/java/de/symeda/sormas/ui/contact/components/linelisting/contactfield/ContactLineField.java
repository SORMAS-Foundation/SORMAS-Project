package de.symeda.sormas.ui.contact.components.linelisting.contactfield;

import java.time.LocalDate;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.contact.components.linelisting.MultiDayContactField;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonField;
import de.symeda.sormas.ui.utils.components.linelisting.person.PersonFieldDto;
import de.symeda.sormas.ui.utils.components.multidayselector.MultiDaySelectorDto;

public class ContactLineField extends CustomField<ContactLineFieldDto> {

	private final Binder<ContactLineFieldDto> binder = new Binder<>(ContactLineFieldDto.class);

	private final DateField dateOfReport;
	private final MultiDayContactField multiDay;
	private final ComboBox<ContactProximity> typeOfContact;
	private final ComboBox<ContactRelation> relationToCase;
	private final PersonField person;

	public ContactLineField() {
		dateOfReport = new DateField();
		multiDay = new MultiDayContactField();
		typeOfContact = new ComboBox<>();
		relationToCase = new ComboBox<>();
		person = new PersonField();
	}

	@Override
	protected Component initContent() {
		setValue(new ContactLineFieldDto());

		HorizontalLayout layout = new HorizontalLayout();

		layout.setMargin(false);
		layout.setSpacing(false);

		dateOfReport.setId("dateOfReport");
		dateOfReport.setWidth(150, Unit.PIXELS);
		binder.forField(dateOfReport).asRequired().bind(ContactLineFieldDto.DATE_OF_REPORT);
		dateOfReport.setRangeEnd(LocalDate.now());

		multiDay.setId("multiDay");
		binder.forField(multiDay).bind(ContactLineFieldDto.MULTI_DAY_SELECTOR);

		typeOfContact.setId("typeOfContact");
		typeOfContact.setItems(ContactProximity.values());
		typeOfContact.setWidth(150, Unit.PIXELS);
		typeOfContact.addStyleName(CssStyles.CAPTION_OVERFLOW);
		binder.forField(typeOfContact).bind(ContactLineFieldDto.TYPE_OF_CONTACT);

		relationToCase.setId("relationToCase");
		relationToCase.setItems(ContactRelation.values());
		relationToCase.setWidth(150, Unit.PIXELS);
		relationToCase.addStyleName(CssStyles.CAPTION_OVERFLOW);
		binder.forField(relationToCase).bind(ContactLineFieldDto.RELATION_TO_CASE);

		person.setId("person");
		binder.forField(person).bind(ContactLineFieldDto.PERSON);

		layout.addComponents(dateOfReport, multiDay, typeOfContact, relationToCase, person);

		layout.setComponentAlignment(dateOfReport, Alignment.BOTTOM_LEFT);
		layout.setComponentAlignment(typeOfContact, Alignment.BOTTOM_LEFT);
		layout.setComponentAlignment(relationToCase, Alignment.BOTTOM_LEFT);
		layout.setComponentAlignment(person, Alignment.BOTTOM_LEFT);

		return layout;
	}

	@Override
	protected void doSetValue(ContactLineFieldDto contactFieldDto) {
		binder.setBean(contactFieldDto);
	}

	@Override
	public ContactLineFieldDto getValue() {
		return binder.getBean();
	}

	public boolean hasErrors() {
		BinderValidationStatus<MultiDaySelectorDto> contactDatesStatus = multiDay.validate();
		BinderValidationStatus<PersonFieldDto> personValidationStatus = person.validate();
		BinderValidationStatus<ContactLineFieldDto> lineValidationStatus = binder.validate();
		return contactDatesStatus.hasErrors() || personValidationStatus.hasErrors() || lineValidationStatus.hasErrors();
	}

	public void showCaptions() {
		dateOfReport.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
		dateOfReport.removeStyleName(CssStyles.CAPTION_HIDDEN);
		multiDay.showCaptions();
		typeOfContact.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.CONTACT_PROXIMITY));
		typeOfContact.removeStyleName(CssStyles.CAPTION_HIDDEN);
		relationToCase.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.RELATION_TO_CASE));
		relationToCase.removeStyleName(CssStyles.CAPTION_HIDDEN);
		person.showCaptions();
	}
}
