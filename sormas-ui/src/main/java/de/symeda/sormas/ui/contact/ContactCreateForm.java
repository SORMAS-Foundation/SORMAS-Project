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
package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.LAYOUT_COL_HIDE_INVSIBLE;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.DateRangeValidator;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;

public class ContactCreateForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String PERSON_NAME_LOC = "personNameLoc";
	private static final String CASE_INFO_LOC = "caseInfoLoc";
	private static final String CHOOSE_CASE_LOC = "chooseCaseLoc";
	private static final String REMOVE_CASE_LOC = "removeCaseLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			LayoutUtil.loc(PERSON_NAME_LOC) +
			LayoutUtil.fluidRowLocs(PersonDto.FIRST_NAME, PersonDto.LAST_NAME) +
					LayoutUtil.fluidRow(fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD),
							fluidRowLocs(PersonDto.SEX)) +
					LayoutUtil.fluidRowLocs(PersonDto.NATIONAL_HEALTH_ID, PersonDto.PASSPORT_NUMBER) +
					LayoutUtil.fluidRowLocs(PersonDto.PHONE, PersonDto.EMAIL_ADDRESS) +
					LayoutUtil.fluidRowLocs(ContactDto.RETURNING_TRAVELER) +
					LayoutUtil.fluidRowLocs(ContactDto.REPORT_DATE_TIME, ContactDto.DISEASE) +
					LayoutUtil.fluidRowLocs(ContactDto.DISEASE_DETAILS) +
					LayoutUtil.fluidRowLocs(6, CASE_INFO_LOC, 3, CHOOSE_CASE_LOC, 3, REMOVE_CASE_LOC) +
					LayoutUtil.fluidRowLocs(ContactDto.CASE_ID_EXTERNAL_SYSTEM) +
					LayoutUtil.fluidRowLocs(ContactDto.MULTI_DAY_CONTACT) +
					LayoutUtil.fluidRow(
						LayoutUtil.fluidColumnLocCss(LAYOUT_COL_HIDE_INVSIBLE,6,0, ContactDto.FIRST_CONTACT_DATE),
						LayoutUtil.fluidColumnLoc(6, 0, ContactDto.LAST_CONTACT_DATE)) +
					LayoutUtil.fluidRowLocs(ContactDto.CASE_OR_EVENT_INFORMATION) +
					LayoutUtil.fluidRowLocs(ContactDto.REGION, ContactDto.DISTRICT) +
					LayoutUtil.fluidRowLocs(ContactDto.COMMUNITY) +
					LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
					fluidRowLocs(ContactDto.CONTACT_PROXIMITY_DETAILS) + fluidRowLocs(ContactDto.CONTACT_CATEGORY)
					+
					LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
					LayoutUtil.fluidRowLocs(ContactDto.RELATION_DESCRIPTION) +
					LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION);
	//@formatter:on

	private NullableOptionGroup contactProximity;
	private Disease disease;
	private final Boolean hasCaseRelation;
	private final boolean asSourceContact;
	private CaseReferenceDto selectedCase;
	private NullableOptionGroup contactCategory;
	private TextField contactProximityDetails;
	private ComboBox birthDateDay;

	/**
	 * TODO use disease and case relation information given in ContactDto
	 */
	public ContactCreateForm(Disease disease, boolean hasCaseRelation, boolean asSourceContact) {
		super(ContactDto.class, ContactDto.I18N_PREFIX);

		this.disease = disease;
		this.hasCaseRelation = hasCaseRelation;
		this.asSourceContact = asSourceContact;

		addFields();

		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {

		if (hasCaseRelation == null) {
			return;
		}

		addField(ContactDto.REPORT_DATE_TIME, DateField.class);
		ComboBox cbDisease = addDiseaseField(ContactDto.DISEASE, false);
		addField(ContactDto.DISEASE_DETAILS, TextField.class);
		TextField firstName = addCustomField(PersonDto.FIRST_NAME, String.class, TextField.class);
		TextField lastName = addCustomField(PersonDto.LAST_NAME, String.class, TextField.class);
		addCustomField(PersonDto.NATIONAL_HEALTH_ID, String.class, TextField.class);
		addCustomField(PersonDto.PASSPORT_NUMBER, String.class, TextField.class);
		TextField phone = addCustomField(PersonDto.PHONE, String.class, TextField.class);
		phone.setCaption(I18nProperties.getCaption(Captions.Person_phone));
		TextField email = addCustomField(PersonDto.EMAIL_ADDRESS, String.class, TextField.class);
		email.setCaption(I18nProperties.getCaption(Captions.Person_emailAddress));

		phone.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phone.getCaption())));
		email.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, email.getCaption())));

		addField(ContactDto.RETURNING_TRAVELER, NullableOptionGroup.class);
		ComboBox region = addInfrastructureField(ContactDto.REGION);
		ComboBox district = addInfrastructureField(ContactDto.DISTRICT);
		ComboBox community = addInfrastructureField(ContactDto.COMMUNITY);

		CheckBox multiDayContact = addField(ContactDto.MULTI_DAY_CONTACT, CheckBox.class);
		DateField firstContactDate = addField(ContactDto.FIRST_CONTACT_DATE, DateField.class);
		DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);

		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.FIRST_CONTACT_DATE, ContactDto.MULTI_DAY_CONTACT, Collections.singletonList(true), true);
		initContactDateValidation(firstContactDate, lastContactDate, multiDayContact);

		contactProximity = addField(ContactDto.CONTACT_PROXIMITY, NullableOptionGroup.class);
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contactProximity.addValueChangeListener(e -> updateContactCategory((ContactProximity) contactProximity.getNullableValue()));
			contactProximityDetails = addField(ContactDto.CONTACT_PROXIMITY_DETAILS, TextField.class);
			contactCategory = addField(ContactDto.CONTACT_CATEGORY, NullableOptionGroup.class);
		}
		addField(ContactDto.DESCRIPTION, TextArea.class).setRows(4);
		ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
		addField(ContactDto.RELATION_DESCRIPTION, TextField.class);
		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(4);

		birthDateDay = addCustomField(PersonDto.BIRTH_DATE_DD, Integer.class, ComboBox.class);
		birthDateDay.addStyleName(FORCE_CAPTION);
		birthDateDay.setInputPrompt(I18nProperties.getString(Strings.day));
		ComboBox birthDateMonth = addCustomField(PersonDto.BIRTH_DATE_MM, Integer.class, ComboBox.class);
		birthDateMonth.addItems(DateHelper.getMonthsInYear());
		birthDateMonth.setPageLength(12);
		birthDateMonth.addStyleName(FORCE_CAPTION);
		birthDateMonth.setInputPrompt(I18nProperties.getString(Strings.month));
		setItemCaptionsForMonths(birthDateMonth);
		ComboBox birthDateYear = addCustomField(PersonDto.BIRTH_DATE_YYYY, Integer.class, ComboBox.class);
		birthDateYear.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE));
		birthDateYear.addItems(DateHelper.getYearsToNow());
		birthDateYear.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		birthDateYear.setInputPrompt(I18nProperties.getString(Strings.year));
		// Update the list of days according to the selected month and year
		birthDateYear.addValueChangeListener(e -> {
			updateListOfDays((Integer) e.getProperty().getValue(), (Integer) birthDateMonth.getValue());
		});
		birthDateMonth.addValueChangeListener(e -> {
			updateListOfDays((Integer) birthDateYear.getValue(), (Integer) e.getProperty().getValue());
		});

		ComboBox sex = addCustomField(PersonDto.SEX, Sex.class, ComboBox.class);
		sex.setCaption(I18nProperties.getCaption(Captions.Person_sex));

		CssStyles.style(CssStyles.SOFT_REQUIRED, firstName, lastName, firstContactDate, lastContactDate, contactProximity, relationToCase);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
		});

		setRequired(true, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, ContactDto.REPORT_DATE_TIME, PersonDto.SEX);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.RELATION_DESCRIPTION,
			ContactDto.RELATION_TO_CASE,
			Arrays.asList(ContactRelation.OTHER),
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.DISEASE_DETAILS, ContactDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.DISEASE, Arrays.asList(ContactDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));

		cbDisease.addValueChangeListener(e -> {
			disease = (Disease) e.getProperty().getValue();
			setVisible(disease != null, ContactDto.CONTACT_PROXIMITY);
			if (isConfiguredServer("de")) {
				contactCategory.setVisible(disease != null);
				contactProximityDetails.setVisible(disease != null);
			}
			updateContactProximity();
		});

		if (!hasCaseRelation) {
			Label caseInfoLabel = new Label(I18nProperties.getString(Strings.infoNoSourceCaseSelected), ContentMode.HTML);
			Button chooseCaseButton = ButtonHelper.createButton(Captions.contactChooseCase, null, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_2);
			Button removeCaseButton = ButtonHelper.createButton(Captions.contactRemoveCase, null, ValoTheme.BUTTON_LINK);

			CssStyles.style(caseInfoLabel, CssStyles.VSPACE_TOP_4);
			getContent().addComponent(caseInfoLabel, CASE_INFO_LOC);

			chooseCaseButton.addClickListener(e -> {
				ControllerProvider.getContactController().openSelectCaseForContactWindow((Disease) cbDisease.getValue(), selectedCase -> {
					if (selectedCase != null) {
						this.selectedCase = selectedCase.toReference();
						caseInfoLabel.setValue(
							String.format(
								I18nProperties.getString(Strings.infoContactCreationSourceCase),
								selectedCase.getPersonFirstName() + " " + selectedCase.getPersonLastName() + " " + "("
									+ DataHelper.getShortUuid(selectedCase.getUuid()) + ")"));
						caseInfoLabel.removeStyleName(CssStyles.VSPACE_TOP_4);
						removeCaseButton.setVisible(true);
						chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));

						cbDisease.setValue(selectedCase.getDisease());
						getValue().setCaze(this.selectedCase);
						updateFieldVisibilitiesByCase(true);
					}
				});
			});
			getContent().addComponent(chooseCaseButton, CHOOSE_CASE_LOC);

			removeCaseButton.addClickListener(e -> {
				this.selectedCase = null;
				getValue().setCaze(null);
				caseInfoLabel.setValue(I18nProperties.getString(Strings.infoNoSourceCaseSelected));
				caseInfoLabel.addStyleName(CssStyles.VSPACE_TOP_4);
				removeCaseButton.setVisible(false);
				chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChooseCase));

				updateFieldVisibilitiesByCase(false);
			});
			getContent().addComponent(removeCaseButton, REMOVE_CASE_LOC);
			removeCaseButton.setVisible(false);
		}
		if (asSourceContact) {
			setEnabled(false, ContactDto.DISEASE, ContactDto.DISEASE_DETAILS);
		}

		addValueChangeListener(e -> {
			updateFieldVisibilitiesByCase(hasCaseRelation);
			if (!hasCaseRelation && disease == null) {
				setVisible(false, ContactDto.CONTACT_PROXIMITY);
				if (isConfiguredServer("de")) {
					contactCategory.setVisible(false);
					contactProximityDetails.setVisible(false);
				}
			}

			updateContactProximity();

			if (asSourceContact) {
				setVisible(
					false,
					PersonDto.FIRST_NAME,
					PersonDto.LAST_NAME,
					PersonDto.BIRTH_DATE_DD,
					PersonDto.BIRTH_DATE_MM,
					PersonDto.BIRTH_DATE_YYYY,
					PersonDto.SEX,
					PersonDto.NATIONAL_HEALTH_ID,
					PersonDto.PASSPORT_NUMBER,
					PersonDto.PHONE,
					PersonDto.EMAIL_ADDRESS);

				setRequired(false, PersonDto.SEX);

				TextField personNameField = addCustomField(PERSON_NAME_LOC, String.class, TextField.class);
				personNameField.setCaption(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.PERSON));
				personNameField.setValue(getValue().getPerson().getCaption());
				personNameField.setReadOnly(true);
			}
		});
	}

	/*
	 * Only used for Systems in Germany. Follows specific rules for german systems.
	 */
	private void updateContactCategory(ContactProximity proximity) {

		if (proximity != null) {
			switch (proximity) {
			case FACE_TO_FACE_LONG:
			case TOUCHED_FLUID:
			case AEROSOL:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.HIGH_RISK));
				break;
			case MEDICAL_UNSAFE:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.HIGH_RISK_MED));
				break;
			case MEDICAL_LIMITED:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.MEDIUM_RISK_MED));
				break;
			case SAME_ROOM:
			case FACE_TO_FACE_SHORT:
			case MEDICAL_SAME_ROOM:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.LOW_RISK));
				break;
			case MEDICAL_DISTANT:
			case MEDICAL_SAFE:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.NO_RISK));
				break;
			default:
			}
		}
	}

	private void updateFieldVisibilitiesByCase(boolean caseSelected) {
		setVisible(!caseSelected, ContactDto.DISEASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION);
		setRequired(!caseSelected, ContactDto.DISEASE, ContactDto.REGION, ContactDto.DISTRICT);
	}

	private void updateRelationDescriptionField(ComboBox relationToCase, TextField relationDescription) {
		boolean otherContactRelation = relationToCase.getValue().equals(ContactRelation.OTHER);
		relationDescription.setVisible(otherContactRelation);
	}

	protected void updateLastContactDateValidator() {
		Field<?> dateField = getField(ContactDto.LAST_CONTACT_DATE);
		for (Validator validator : dateField.getValidators()) {
			if (validator instanceof DateRangeValidator) {
				dateField.removeValidator(validator);
			}
		}
		if (getValue() != null) {
			dateField.addValidator(
				new DateRangeValidator(
					I18nProperties.getValidationError(
						Validations.beforeDate,
						I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE),
						I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)),
					null,
					new LocalDate(getValue().getReportDateTime()).toDate(),
					Resolution.SECOND));
		}
	}

	private void updateContactProximity() {

		ContactProximity value = (ContactProximity) contactProximity.getNullableValue();
		FieldHelper.updateEnumData(
			contactProximity,
			Arrays.asList(ContactProximity.getValues(disease, FacadeProvider.getConfigFacade().getCountryLocale())));
		contactProximity.setValue(value);
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

	public String getPersonFirstName() {
		return (String) getField(PersonDto.FIRST_NAME).getValue();
	}

	public String getPersonLastName() {
		return (String) getField(PersonDto.LAST_NAME).getValue();
	}

	public String getNationalHealthId() {
		return (String) getField(PersonDto.NATIONAL_HEALTH_ID).getValue();
	}

	public String getPassportNumber() {
		return (String) getField(PersonDto.PASSPORT_NUMBER).getValue();
	}

	public Integer getBirthdateDD() {
		return (Integer) getField(PersonDto.BIRTH_DATE_DD).getValue();
	}

	public Integer getBirthdateMM() {
		return (Integer) getField(PersonDto.BIRTH_DATE_MM).getValue();
	}

	public Integer getBirthdateYYYY() {
		return (Integer) getField(PersonDto.BIRTH_DATE_YYYY).getValue();
	}

	public Sex getSex() {
		return (Sex) getField(PersonDto.SEX).getValue();
	}

	public String getPhone() {
		return (String) getField(PersonDto.PHONE).getValue();
	}

	public String getEmailAddress() {
		return (String) getField(PersonDto.EMAIL_ADDRESS).getValue();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public void setPerson(PersonDto person) {
		if (person != null) {
			((TextField) getField(PersonDto.FIRST_NAME)).setValue(person.getFirstName());
			((TextField) getField(PersonDto.LAST_NAME)).setValue(person.getLastName());
			((ComboBox) getField(PersonDto.BIRTH_DATE_YYYY)).setValue(person.getBirthdateYYYY());
			((ComboBox) getField(PersonDto.BIRTH_DATE_MM)).setValue(person.getBirthdateMM());
			((ComboBox) getField(PersonDto.BIRTH_DATE_DD)).setValue(person.getBirthdateDD());
			((ComboBox) getField(PersonDto.SEX)).setValue(person.getSex());
			((TextField) getField(PersonDto.NATIONAL_HEALTH_ID)).setValue(person.getNationalHealthId());
			((TextField) getField(PersonDto.PASSPORT_NUMBER)).setValue(person.getPassportNumber());
			((TextField) getField(PersonDto.PHONE)).setValue(person.getPhone());
			((TextField) getField(PersonDto.EMAIL_ADDRESS)).setValue(person.getEmailAddress());
		} else {
			getField(PersonDto.FIRST_NAME).clear();
			getField(PersonDto.LAST_NAME).clear();
			getField(PersonDto.BIRTH_DATE_DD).clear();
			getField(PersonDto.BIRTH_DATE_MM).clear();
			getField(PersonDto.BIRTH_DATE_YYYY).clear();
			getField(PersonDto.SEX).clear();
			getField(PersonDto.NATIONAL_HEALTH_ID).clear();
			getField(PersonDto.PASSPORT_NUMBER).clear();
			getField(PersonDto.PHONE).clear();
			getField(PersonDto.EMAIL_ADDRESS).clear();
		}
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

		setRequired(false, PersonDto.FIRST_NAME, PersonDto.LAST_NAME, PersonDto.SEX);
	}

	public void setDiseaseReadOnly() {
		getField(CaseDataDto.DISEASE).setEnabled(false);
	}

	private void initContactDateValidation(DateField startDate, DateField endDate, CheckBox multiDayCheckbox) {
		DateComparisonValidator startDateValidator = new DateComparisonValidator(
			startDate,
			endDate,
			true,
			true,
			I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption()));

		DateComparisonValidator endDateValidator = new DateComparisonValidator(
			endDate,
			startDate,
			false,
			true,
			I18nProperties.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption()));

		startDate.addValueChangeListener(event -> endDate.setRequired(event.getProperty().getValue() != null));

		multiDayCheckbox.addValueChangeListener(e -> {
			if ((Boolean) e.getProperty().getValue()) {
				startDate.addValidator(startDateValidator);
				endDate.addValidator(endDateValidator);
			} else {
				startDate.removeValidator(startDateValidator);
				endDate.removeValidator(endDateValidator);
			}
		});

	}
}
