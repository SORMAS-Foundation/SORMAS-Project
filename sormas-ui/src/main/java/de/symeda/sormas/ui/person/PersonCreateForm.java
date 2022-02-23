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

package de.symeda.sormas.ui.person;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.time.Month;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PersonCreateForm extends AbstractEditForm<PersonDto> {

	private static final long serialVersionUID = 639431574534995815L;

	private static final String PERSON_SEARCH_LOC = "personSearchLoc";
	private static final String ENTER_HOME_ADDRESS_NOW = "enterHomeAddressNow";
	private static final String HOME_ADDRESS_HEADER = "addressHeader";
	private static final String HOME_ADDRESS_LOC = "homeAddressLoc";

	private ComboBox birthDateDay;
	private LocationEditForm homeAddressForm;

	private final boolean showHomeAddressForm;
	private final boolean showPresentCondition;
	private final boolean showSymptomsOnsetDate;

	private PersonDto searchedPerson;
	private Button searchPersonButton;

	private static final String HTML_LAYOUT = fluidRowLocs(6, PersonDto.FIRST_NAME, 5, PersonDto.LAST_NAME, 1, PERSON_SEARCH_LOC)
		+ fluidRow(fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD), fluidRowLocs(PersonDto.SEX))
		+ fluidRowLocs(PersonDto.NATIONAL_HEALTH_ID, PersonDto.PASSPORT_NUMBER)
		+ fluidRowLocs(PersonDto.PRESENT_CONDITION, SymptomsDto.ONSET_DATE)
		+ fluidRowLocs(PersonDto.PHONE, PersonDto.EMAIL_ADDRESS)
		+ fluidRowLocs(ENTER_HOME_ADDRESS_NOW)
		+ loc(HOME_ADDRESS_HEADER)
		+ divsCss(VSPACE_3, fluidRowLocs(HOME_ADDRESS_LOC));

	public PersonCreateForm(boolean showHomeAddressForm, boolean showPresentCondition, boolean showSymptomsOnsetDate) {
		super(
			PersonDto.class,
			PersonDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getDefault(false));
		this.showHomeAddressForm = showHomeAddressForm;
		this.showPresentCondition = showPresentCondition;
		this.showSymptomsOnsetDate = showSymptomsOnsetDate;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(PersonDto.FIRST_NAME, TextField.class);
		addField(PersonDto.LAST_NAME, TextField.class);

		searchPersonButton = createPersonSearchButton(PERSON_SEARCH_LOC);
		getContent().addComponent(searchPersonButton, PERSON_SEARCH_LOC);

		birthDateDay = addField(PersonDto.BIRTH_DATE_DD, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateDay.setNullSelectionAllowed(true);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		birthDateDay.setCaption("");
		ComboBox birthDateMonth = addField(PersonDto.BIRTH_DATE_MM, ComboBox.class);
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateMonth.setNullSelectionAllowed(true);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		birthDateMonth.setCaption("");
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addField(PersonDto.BIRTH_DATE_YYYY, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		// @TODO: Done for nullselection Bug, fixed in Vaadin 7.7.3
		birthDateYear.setNullSelectionAllowed(true);
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		birthDateDay.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) birthDateMonth.getValue(), (Integer) e));
		birthDateMonth.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) birthDateYear.getValue(), (Integer) e, (Integer) birthDateDay.getValue()));
		birthDateYear.addValidator(
			e -> ControllerProvider.getPersonController()
				.validateBirthDate((Integer) e, (Integer) birthDateMonth.getValue(), (Integer) birthDateDay.getValue()));

		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
			birthDateMonth.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
			birthDateYear.markAsDirty();
			birthDateDay.markAsDirty();
		});
		birthDateDay.addValueChangeListener(e -> {
			birthDateYear.markAsDirty();
			birthDateMonth.markAsDirty();
		});

		ComboBox sex = addField(PersonDto.SEX, ComboBox.class);

		addField(PersonDto.PASSPORT_NUMBER, TextField.class);
		addField(PersonDto.NATIONAL_HEALTH_ID, TextField.class);

		ComboBox presentCondition = addField(PersonDto.PRESENT_CONDITION, ComboBox.class);
		presentCondition.setVisible(showPresentCondition);
		FieldHelper.addSoftRequiredStyle(presentCondition, sex);

		if (showSymptomsOnsetDate) {
			addCustomField(
				SymptomsDto.ONSET_DATE,
				Date.class,
				DateField.class,
				I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE));
		}

		TextField phone = addCustomField(PersonDto.PHONE, String.class, TextField.class);
		phone.setCaption(I18nProperties.getCaption(Captions.Person_phone));
		TextField email = addCustomField(PersonDto.EMAIL_ADDRESS, String.class, TextField.class);
		email.setCaption(I18nProperties.getCaption(Captions.Person_emailAddress));

		phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption())));
		email.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, email.getCaption())));

		if (showHomeAddressForm) {
			addHomeAddressForm();
		}

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();
		hideValidationUntilNextCommit();
		setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	private void setItemCaptionsForMonths(AbstractSelect months) {

		months.setItemCaption(1, I18nProperties.getEnumCaption(Month.JANUARY));
		months.setItemCaption(2, I18nProperties.getEnumCaption(Month.FEBRUARY));
		months.setItemCaption(3, I18nProperties.getEnumCaption(Month.MARCH));
		months.setItemCaption(4, I18nProperties.getEnumCaption(Month.APRIL));
		months.setItemCaption(5, I18nProperties.getEnumCaption(Month.MAY));
		months.setItemCaption(6, I18nProperties.getEnumCaption(Month.JUNE));
		months.setItemCaption(7, I18nProperties.getEnumCaption(Month.JULY));
		months.setItemCaption(8, I18nProperties.getEnumCaption(Month.AUGUST));
		months.setItemCaption(9, I18nProperties.getEnumCaption(Month.SEPTEMBER));
		months.setItemCaption(10, I18nProperties.getEnumCaption(Month.OCTOBER));
		months.setItemCaption(11, I18nProperties.getEnumCaption(Month.NOVEMBER));
		months.setItemCaption(12, I18nProperties.getEnumCaption(Month.DECEMBER));
	}

	private void updateListOfDays(Integer selectedYear, Integer selectedMonth) {

		Integer currentlySelected = (Integer) birthDateDay.getValue();
		birthDateDay.removeAllItems();
		birthDateDay.addItems(DateHelper.getDaysInMonth(selectedMonth, selectedYear));
		if (birthDateDay.containsId(currentlySelected)) {
			birthDateDay.setValue(currentlySelected);
		}
	}

	private void addHomeAddressForm() {
		CheckBox enterHomeAddressNow = new CheckBox(I18nProperties.getCaption(Captions.caseDataEnterHomeAddressNow));
		enterHomeAddressNow.addStyleName(VSPACE_3);
		getContent().addComponent(enterHomeAddressNow, ENTER_HOME_ADDRESS_NOW);

		Label addressHeader = new Label(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.ADDRESS));
		addressHeader.addStyleName(H3);
		getContent().addComponent(addressHeader, HOME_ADDRESS_HEADER);
		addressHeader.setVisible(false);

		homeAddressForm = new LocationEditForm(
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getNoop());
		homeAddressForm.setValue(new LocationDto());
		homeAddressForm.setCaption(null);
		homeAddressForm.setWidthFull();

		getContent().addComponent(homeAddressForm, HOME_ADDRESS_LOC);
		homeAddressForm.setVisible(false);

		enterHomeAddressNow.addValueChangeListener(e -> {
			boolean isChecked = (boolean) e.getProperty().getValue();
			addressHeader.setVisible(isChecked);
			homeAddressForm.setVisible(isChecked);
			homeAddressForm.clear();
			homeAddressForm.setFacilityFieldsVisible(isChecked, true);
		});
	}

	protected Button createPersonSearchButton(String personSearchLoc) {
		return ButtonHelper.createIconButtonWithCaption(personSearchLoc, StringUtils.EMPTY, VaadinIcons.SEARCH, clickEvent -> {
			VaadinIcons icon = (VaadinIcons) clickEvent.getButton().getIcon();
			if (icon == VaadinIcons.SEARCH) {
				PersonSearchField personSearchField = new PersonSearchField(null, I18nProperties.getString(Strings.infoSearchPerson));
				personSearchField.setWidth(1280, Unit.PIXELS);

				final CommitDiscardWrapperComponent<PersonSearchField> component = new CommitDiscardWrapperComponent<>(personSearchField);
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
				component.getCommitButton().setEnabled(false);
				component.addCommitListener(() -> {
					SimilarPersonDto pickedPerson = personSearchField.getValue();
					if (pickedPerson != null) {
						// add consumer
						PersonDto personByUuid = FacadeProvider.getPersonFacade().getPersonByUuid(pickedPerson.getUuid());
						setPerson(personByUuid);
						enablePersonFields(false);
						clickEvent.getButton().setIcon(VaadinIcons.CLOSE);
						searchedPerson = personByUuid;
					}
				});

				personSearchField.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingSelectPerson));
			} else {
				setPerson(null);
				enablePersonFields(true);
				clickEvent.getButton().setIcon(VaadinIcons.SEARCH);
				searchedPerson = null;
			}
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.FORCE_CAPTION);
	}

	public void setPerson(PersonDto person) {

		if (person != null) {
			setValue(person);
			((TextField) getField(PersonDto.PHONE)).setValue(person.getPhone());
			((TextField) getField(PersonDto.EMAIL_ADDRESS)).setValue(person.getEmailAddress());
			if (homeAddressForm != null) {
				homeAddressForm.setValue(person.getAddress());
			}
		} else {
			setValue(new PersonDto());
			getField(PersonDto.PHONE).clear();
			getField(PersonDto.EMAIL_ADDRESS).clear();
			if (homeAddressForm != null) {
				homeAddressForm.clear();
			}
		}
	}

	public void transferDataToPerson(PersonDto person) {
		commit();
		PersonDto personCreated = getValue();

		person.setFirstName(personCreated.getFirstName());
		person.setLastName(personCreated.getLastName());
		person.setBirthdateDD(personCreated.getBirthdateDD());
		person.setBirthdateMM(personCreated.getBirthdateMM());
		person.setBirthdateYYYY(personCreated.getBirthdateYYYY());
		person.setSex(personCreated.getSex());
		person.setPresentCondition(personCreated.getPresentCondition());
		person.setNationalHealthId(personCreated.getNationalHealthId());
		person.setPassportNumber(personCreated.getPassportNumber());

		if (StringUtils.isNotEmpty(getPhone())) {
			person.setPhone(getPhone());
		}
		if (StringUtils.isNotEmpty(getEmailAddress())) {
			person.setEmailAddress(getEmailAddress());
		}
		if (getHomeAddressForm() != null && getHomeAddressForm().getValue() != null) {
			person.setAddress(getHomeAddressForm().getValue());
		}
	}

	public void enablePersonFields(Boolean enable) {
		getField(PersonDto.FIRST_NAME).setEnabled(enable);
		getField(PersonDto.LAST_NAME).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_DD).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_MM).setEnabled(enable);
		getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(enable);
		getField(PersonDto.SEX).setEnabled(enable);
		getField(PersonDto.PRESENT_CONDITION).setEnabled(enable);
		getField(PersonDto.PHONE).setEnabled(enable);
		getField(PersonDto.EMAIL_ADDRESS).setEnabled(enable);
		getField(PersonDto.PASSPORT_NUMBER).setEnabled(enable);
		getField(PersonDto.NATIONAL_HEALTH_ID).setEnabled(enable);
		if (homeAddressForm != null) {
			homeAddressForm.setEnabled(enable);
		}
		setRequired(enable, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void setPersonalDetailsReadOnlyIfNotEmpty(boolean readOnly) {

		getField(PersonDto.FIRST_NAME).setEnabled(!readOnly);
		getField(PersonDto.LAST_NAME).setEnabled(!readOnly);
		searchPersonButton.setEnabled(!readOnly);
		if (getField(PersonDto.SEX).getValue() != null) {
			getField(PersonDto.SEX).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_YYYY).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_YYYY).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_MM).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_MM).setEnabled(!readOnly);
		}
		if (getField(PersonDto.BIRTH_DATE_DD).getValue() != null) {
			getField(PersonDto.BIRTH_DATE_DD).setEnabled(!readOnly);
		}
		setRequired(!readOnly, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void setPersonDetailsReadOnly() {

		setEnabled(
			false,
			PersonDto.FIRST_NAME,
			PersonDto.LAST_NAME,
			PersonDto.SEX,
			PersonDto.BIRTH_DATE_YYYY,
			PersonDto.BIRTH_DATE_MM,
			PersonDto.BIRTH_DATE_DD,
			PersonDto.NATIONAL_HEALTH_ID,
			PersonDto.PASSPORT_NUMBER,
			PersonDto.PHONE,
			PersonDto.EMAIL_ADDRESS);

		searchPersonButton.setEnabled(false);

		setRequired(false, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public LocationEditForm getHomeAddressForm() {
		return homeAddressForm;
	}

	public void setSymptoms(SymptomsDto symptoms) {

		if (symptoms != null) {
			((DateField) getField(SymptomsDto.ONSET_DATE)).setValue(symptoms.getOnsetDate());
		} else {
			getField(SymptomsDto.ONSET_DATE).clear();
		}
	}

	public String getPhone() {
		return (String) getField(PersonDto.PHONE).getValue();
	}

	public String getEmailAddress() {
		return (String) getField(PersonDto.EMAIL_ADDRESS).getValue();
	}

	public Date getOnsetDate() {
		return (Date) getField(SymptomsDto.ONSET_DATE).getValue();
	}

	public PersonDto getSearchedPerson() {
		return searchedPerson;
	}
}
