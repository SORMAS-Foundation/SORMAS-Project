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

package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.CssStyles.LAYOUT_COL_HIDE_INVSIBLE;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Sets;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonCreateForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class ContactCreateForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String PERSON_NAME_LOC = "personNameLoc";
	private static final String CASE_INFO_LOC = "caseInfoLoc";
	private static final String CHOOSE_CASE_LOC = "chooseCaseLoc";
	private static final String REMOVE_CASE_LOC = "removeCaseLoc";
	private static final String ADOPT_ADDRESS_LOC = "adoptAddressLoc";

	//@formatter:off
	private static final String HTML_LAYOUT =
			LayoutUtil.loc(PERSON_NAME_LOC) +
					LayoutUtil.fluidRowLocs(ContactDto.PERSON) + 
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
					fluidRowLocs(ContactDto.CONTACT_PROXIMITY_DETAILS) +
					fluidRowLocs(ContactDto.CONTACT_CATEGORY) +
					LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
					LayoutUtil.fluidRowLocs(ADOPT_ADDRESS_LOC) +
					LayoutUtil.fluidRowLocs(ContactDto.RELATION_DESCRIPTION) +
					LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION);
	//@formatter:on

	private NullableOptionGroup contactProximity;
	private Disease disease;
	private final Boolean hasCaseRelation;
	private final boolean asSourceContact;
	private NullableOptionGroup contactCategory;
	private TextField contactProximityDetails;

	private PersonCreateForm personCreateForm;

	DateField reportDate;
	CheckBox multiDayContact;
	DateField firstContactDate;
	DateField lastContactDate;
	ComboBox relationToCase;
	AdoptAddressLayout adoptAddressLayout;

	ComboBox region;
	ComboBox district;
	ComboBox community;

	private final boolean showPersonSearchButton;

	/**
	 * TODO use disease and case relation information given in ContactDto
	 */
	public ContactCreateForm(Disease disease, boolean hasCaseRelation, boolean asSourceContact, boolean showPersonSearchButton) {
		super(
			ContactDto.class,
			ContactDto.I18N_PREFIX,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()));

		this.disease = disease;
		this.hasCaseRelation = hasCaseRelation;
		this.asSourceContact = asSourceContact;
		this.showPersonSearchButton = showPersonSearchButton;

		addFields();

		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {

		if (hasCaseRelation == null) {
			return;
		}

		reportDate = addField(ContactDto.REPORT_DATE_TIME, DateField.class);
		ComboBox cbDisease = addDiseaseField(ContactDto.DISEASE, false, true);
		addField(ContactDto.DISEASE_DETAILS, TextField.class);

		personCreateForm = new PersonCreateForm(false, false, false, showPersonSearchButton);
		personCreateForm.setWidth(100, Unit.PERCENTAGE);
		personCreateForm.setValue(new PersonDto());
		getContent().addComponent(personCreateForm, ContactDto.PERSON);

		addField(ContactDto.RETURNING_TRAVELER, NullableOptionGroup.class);
		region = addInfrastructureField(ContactDto.REGION);
		district = addInfrastructureField(ContactDto.DISTRICT);
		community = addInfrastructureField(ContactDto.COMMUNITY);

		multiDayContact = addField(ContactDto.MULTI_DAY_CONTACT, CheckBox.class);
		firstContactDate = addField(ContactDto.FIRST_CONTACT_DATE, DateField.class);
		lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
		firstContactDate.addValueChangeListener(event -> lastContactDate.setRequired(event.getProperty().getValue() != null));
		multiDayContact.addValueChangeListener(event -> updateDateComparison());

		List<AbstractField<Date>> validatedFields = Arrays.asList(firstContactDate, lastContactDate, reportDate);
		validatedFields.forEach(field -> field.addValueChangeListener(r -> {
			validatedFields.forEach(otherField -> {
				otherField.setValidationVisible(!otherField.isValid());
			});
		}));

		FieldHelper
			.setVisibleWhen(getFieldGroup(), ContactDto.FIRST_CONTACT_DATE, ContactDto.MULTI_DAY_CONTACT, Collections.singletonList(true), true);
		updateDateComparison();

		contactProximity = addField(ContactDto.CONTACT_PROXIMITY, NullableOptionGroup.class);
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contactProximity.addValueChangeListener(e -> updateContactCategory((ContactProximity) contactProximity.getNullableValue()));
			contactProximityDetails = addField(ContactDto.CONTACT_PROXIMITY_DETAILS, TextField.class);
			contactCategory = addField(ContactDto.CONTACT_CATEGORY, NullableOptionGroup.class);
		}
		addField(ContactDto.DESCRIPTION, TextArea.class).setRows(4);
		relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
		addField(ContactDto.RELATION_DESCRIPTION, TextField.class);

		adoptAddressLayout = new AdoptAddressLayout();
		adoptAddressLayout.setVisible(false);
		getContent().addComponent(adoptAddressLayout, ADOPT_ADDRESS_LOC);

		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(4);

		CssStyles.style(CssStyles.SOFT_REQUIRED, firstContactDate, lastContactDate, contactProximity, relationToCase);

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

		setRequired(true, ContactDto.REPORT_DATE_TIME);
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
			if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
				setVisible(disease == Disease.CORONAVIRUS, ContactDto.CONTACT_CATEGORY, ContactDto.CONTACT_PROXIMITY_DETAILS);
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
						caseInfoLabel.setValue(
							String.format(
								I18nProperties.getString(Strings.infoContactCreationSourceCase),
								selectedCase.getPersonFirstName() + " " + selectedCase.getPersonLastName() + " " + "("
									+ DataHelper.getShortUuid(selectedCase.getUuid()) + ")"));
						caseInfoLabel.removeStyleName(CssStyles.VSPACE_TOP_4);
						removeCaseButton.setVisible(true);
						chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));

						getValue().setCaze(selectedCase.toReference());
						cbDisease.setValue(selectedCase.getDisease());
						updateFieldVisibilitiesByCase(true);
					}
				});
			});
			getContent().addComponent(chooseCaseButton, CHOOSE_CASE_LOC);

			removeCaseButton.addClickListener(e -> {
				getValue().setCaze(null);
				cbDisease.setValue(null);
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
				personCreateForm.setVisible(false);
				personCreateForm.enablePersonFields(false);

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
		ValueChangeListener valueChangeListener = e -> {
			boolean sameHousehold = ContactRelation.SAME_HOUSEHOLD.equals(relationToCase.getValue());
			adoptAddressLayout.setVisible(sameHousehold);
			adoptAddressLayout.setAdoptAddress(sameHousehold);
		};
		if (caseSelected) {
			relationToCase.addValueChangeListener(valueChangeListener);
			if (ContactRelation.SAME_HOUSEHOLD.equals(relationToCase.getValue())) {
				boolean sameHousehold = ContactRelation.SAME_HOUSEHOLD.equals(relationToCase.getValue());
				adoptAddressLayout.setVisible(sameHousehold);
				adoptAddressLayout.setAdoptAddress(sameHousehold);
			}
		} else {
			relationToCase.removeValueChangeListener(valueChangeListener);
			adoptAddressLayout.setVisible(false);
			adoptAddressLayout.setAdoptAddress(false);
		}
	}

	private void updateContactProximity() {

		ContactProximity value = (ContactProximity) contactProximity.getNullableValue();
		FieldHelper.updateEnumData(
			contactProximity,
			Arrays.asList(ContactProximity.getValues(disease, FacadeProvider.getConfigFacade().getCountryLocale())));
		contactProximity.setValue(value);
	}

	private void hideAndFillJurisdictionFields() {

		region.setVisible(false);
		region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		district.setVisible(false);
		district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		community.setVisible(false);
		community.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public PersonCreateForm getPersonCreateForm() {
		return personCreateForm;
	}

	public PersonDto getSearchedPerson() {
		return personCreateForm.getSearchedPerson();
	}

	public void setPerson(PersonDto person) {
		personCreateForm.setPerson(person);
	}

	public void setPersonDetailsReadOnly() {
		personCreateForm.setPersonDetailsReadOnly();
	}

	public void setDiseaseReadOnly() {
		getField(CaseDataDto.DISEASE).setEnabled(false);
	}

	@Override
	public void setValue(ContactDto newFieldValue) {
		super.setValue(newFieldValue);
		updateDateComparison();
		adoptAddressLayout.setContact(newFieldValue);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}

	private void updateDateComparison() {
		DateComparisonValidator.removeDateComparisonValidators(firstContactDate);
		DateComparisonValidator.removeDateComparisonValidators(lastContactDate);
		DateComparisonValidator.removeDateComparisonValidators(reportDate);

		DateComparisonValidator.addStartEndValidators(lastContactDate, reportDate);

		if (firstContactDate.isVisible() || multiDayContact.getValue() == Boolean.TRUE) {
			DateComparisonValidator.addStartEndValidators(firstContactDate, lastContactDate);
			DateComparisonValidator.addStartEndValidators(firstContactDate, reportDate);
		}
	}

}
