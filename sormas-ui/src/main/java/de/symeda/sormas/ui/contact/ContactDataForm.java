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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.Date;

import org.joda.time.LocalDate;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.validator.DateRangeValidator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ContactDataForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String TO_CASE_BTN_LOC = "toCaseBtnLoc";
	private static final String CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC = "cancelOrResumeFollowUpBtnLoc";
	private static final String LOST_FOLLOW_UP_BTN_LOC = "lostFollowUpBtnLoc";

	private static final String HTML_LAYOUT = 
			h3(I18nProperties.getString(Strings.headingContactData)) +
			fluidRowLocs(ContactDto.CONTACT_CLASSIFICATION, ContactDto.CONTACT_STATUS) +
			locCss(VSPACE_3, TO_CASE_BTN_LOC) +
			fluidRowLocs(ContactDto.LAST_CONTACT_DATE, ContactDto.DISEASE) +
			fluidRowLocs(ContactDto.DISEASE_DETAILS) +
			fluidRowLocs(ContactDto.UUID, ContactDto.EXTERNAL_ID) +
			fluidRowLocs(ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME) +
			fluidRowLocs(ContactDto.REGION, ContactDto.DISTRICT) +
			fluidRowLocs(ContactDto.CASE_ID_EXTERNAL_SYSTEM, "") +
			loc(ContactDto.CASE_OR_EVENT_INFORMATION) +
			fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
			fluidRowLocs(ContactDto.CONTACT_PROXIMITY_DETAILS) +
			fluidRowLocs(ContactDto.CONTACT_CATEGORY) +
			fluidRowLocs(ContactDto.RELATION_TO_CASE) +
			fluidRowLocs(ContactDto.RELATION_DESCRIPTION) +
			fluidRowLocs(ContactDto.DESCRIPTION) +
			fluidRowLocs(6, ContactDto.QUARANTINE, 3, ContactDto.QUARANTINE_FROM,
					3, ContactDto.QUARANTINE_TO)
			+ fluidRowLocs(4, ContactDto.QUARANTINE_ORDER_MEANS,
					8,
					ContactDto.QUARANTINE_HELP_NEEDED)
			+
			locCss(VSPACE_3, ContactDto.HIGH_PRIORITY) +
			fluidRowLocs(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS) +
			loc(ContactDto.CARE_FOR_PEOPLE_OVER_60) +
			h3(I18nProperties.getString(Strings.headingFollowUpStatus)) +
			fluidRowLocs(ContactDto.FOLLOW_UP_STATUS, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC, LOST_FOLLOW_UP_BTN_LOC) +
			fluidRowLocs(4, ContactDto.FOLLOW_UP_UNTIL, 8, ContactDto.OVERWRITE_FOLLOW_UP_UTIL) +
			fluidRowLocs(ContactDto.FOLLOW_UP_COMMENT) +
			fluidRowLocs(ContactDto.CONTACT_OFFICER, "");

	private OptionGroup contactProximity;
	private Field<?> quarantine;
	private DateField quarantineFrom;
	private DateField quarantineTo;
	private ComboBox cbDisease;
	private OptionGroup contactCategory;
	private ComboBox quarantineOrderMeans;
	private TextField quarantineHelpNeeded;

	public ContactDataForm(UserRight editOrCreateUserRight) {
		super(ContactDto.class, ContactDto.I18N_PREFIX, editOrCreateUserRight);
	}

	@Override
	protected void addFields() {
		addField(ContactDto.CONTACT_CLASSIFICATION, OptionGroup.class);
		addField(ContactDto.CONTACT_STATUS, OptionGroup.class);
		addField(ContactDto.UUID, TextField.class);
		addField(ContactDto.EXTERNAL_ID, TextField.class);
		addField(ContactDto.REPORTING_USER, ComboBox.class);
		DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
		DateField reportDate = addField(ContactDto.REPORT_DATE_TIME, DateField.class);
		contactProximity = addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		if (isGermanServer()) {
			contactProximity.addValueChangeListener(
					e -> updateContactCategory((ContactProximity) contactProximity.getValue()));
			addField(ContactDto.CONTACT_PROXIMITY_DETAILS, TextField.class);
			contactCategory = addField(ContactDto.CONTACT_CATEGORY, OptionGroup.class);
		}
		ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
		addField(ContactDto.RELATION_DESCRIPTION, TextField.class);
		cbDisease = addDiseaseField(ContactDto.DISEASE, false);
		cbDisease.setNullSelectionAllowed(false);
		addField(ContactDto.DISEASE_DETAILS, TextField.class);

		quarantine = addField(ContactDto.QUARANTINE);
		quarantine.addValueChangeListener(e -> updateQuarantineFields());
		quarantineFrom = addField(ContactDto.QUARANTINE_FROM, DateField.class);
		quarantineFrom.setVisible(false);
		quarantineTo = addDateField(ContactDto.QUARANTINE_TO, DateField.class, -1);
		quarantineTo.setVisible(false);

		if (isGermanServer()) {
			quarantineOrderMeans = addField(ContactDto.QUARANTINE_ORDER_MEANS, ComboBox.class);
			quarantineOrderMeans.setVisible(false);
		}
		quarantineHelpNeeded = addField(ContactDto.QUARANTINE_HELP_NEEDED, TextField.class);
		quarantineHelpNeeded.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
		quarantineHelpNeeded.setVisible(false);

		addField(ContactDto.DESCRIPTION, TextArea.class).setRows(3);

		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(2);

		addField(ContactDto.FOLLOW_UP_STATUS, ComboBox.class);
		addField(ContactDto.FOLLOW_UP_COMMENT, TextArea.class).setRows(1);
		DateField dfFollowUpUntil = addDateField(ContactDto.FOLLOW_UP_UNTIL, DateField.class, -1);

		ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
		contactOfficerField.setNullSelectionAllowed(true);

		ComboBox region = addInfrastructureField(ContactDto.REGION);
		region.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.REGION));
		ComboBox district = addInfrastructureField(ContactDto.DISTRICT);
		district.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.DISTRICT));
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district,
					regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto == null && getValue().getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
				districtDto = caseDto.getDistrict();
			}

			FieldHelper.updateItems(contactOfficerField, districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.CONTACT_OFFICER) : null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());


		CheckBox cbHighPriority = addField(ContactDto.HIGH_PRIORITY, CheckBox.class);
		addField(ContactDto.OVERWRITE_FOLLOW_UP_UTIL, CheckBox.class);
		OptionGroup ogImmunosuppressiveTherapyBasicDisease = addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, OptionGroup.class);
		addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS, TextField.class);
		OptionGroup ogCareForPeopleOver60 = addField(ContactDto.CARE_FOR_PEOPLE_OVER_60, OptionGroup.class);

		cbDisease.addValueChangeListener(e -> {
			updateDiseaseConfiguration((Disease) e.getProperty().getValue());
		});

		setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.CONTACT_STATUS, ContactDto.FOLLOW_UP_STATUS);

		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.FOLLOW_UP_STATUS, Arrays.asList(ContactDto.FOLLOW_UP_COMMENT), Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.LOST));
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.RELATION_DESCRIPTION, ContactDto.RELATION_TO_CASE, Arrays.asList(ContactRelation.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, Arrays.asList(YesNoUnknown.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.DISEASE_DETAILS, ContactDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.DISEASE, Arrays.asList(ContactDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setReadOnlyWhen(getFieldGroup(), Arrays.asList(ContactDto.FOLLOW_UP_UNTIL), ContactDto.OVERWRITE_FOLLOW_UP_UTIL, Arrays.asList(Boolean.FALSE), false, true);
		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.OVERWRITE_FOLLOW_UP_UTIL, Arrays.asList(ContactDto.FOLLOW_UP_UNTIL), Arrays.asList(Boolean.TRUE));

		addValueChangeListener(e -> {
			if (getValue() != null) {
				CaseDataDto caseDto = null;

				if (getValue().getCaze() != null) {
					setVisible(false, ContactDto.DISEASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION);
					caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
				} else {
					setRequired(true, ContactDto.DISEASE, ContactDto.REGION, ContactDto.DISTRICT);
				}

				updateLastContactDateValidator();
				updateDiseaseConfiguration(getValue().getDisease());
				updateFollowUpStatusComponents();

				DistrictReferenceDto referenceDistrict = getValue().getDistrict() != null ? getValue().getDistrict() : caseDto != null ? caseDto.getDistrict() : null;
				if (referenceDistrict != null) {
					contactOfficerField.addItems(FacadeProvider.getUserFacade().getUserRefsByDistrict(referenceDistrict, false, UserRole.CONTACT_OFFICER));
				}

				getContent().removeComponent(TO_CASE_BTN_LOC);
				if (getValue().getResultingCase() != null) {
					// link to case
					Link linkToData = ControllerProvider.getCaseController().createLinkToData(getValue().getResultingCase().getUuid(), 
							I18nProperties.getCaption(Captions.contactOpenContactCase));
					getContent().addComponent(linkToData, TO_CASE_BTN_LOC);
				} else if (!ContactClassification.NO_CONTACT.equals(getValue().getContactClassification())) {
					if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CONVERT)) {
						Button toCaseButton = new Button(I18nProperties.getCaption(Captions.contactCreateContactCase));
						toCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
						final FieldGroup fieldGroup = getFieldGroup();

						toCaseButton.addClickListener(new ClickListener() {
							@Override
							public void buttonClick(ClickEvent event) {
								if (!ContactClassification.CONFIRMED.equals(getValue().getContactClassification())) {
									VaadinUiUtil.showSimplePopupWindow(
											I18nProperties.getString(Strings.headingContactConfirmationRequired),
											I18nProperties.getString(Strings.messageContactToCaseConfirmationRequired));
								} else {
									ControllerProvider.getCaseController().createFromContact(getValue());
								}
							}
						});

						getContent().addComponent(toCaseButton, TO_CASE_BTN_LOC);
					}
				}

				if (!isGermanServer()) {
					setVisible(false, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS,
							ContactDto.CARE_FOR_PEOPLE_OVER_60, ContactDto.EXTERNAL_ID);
				} else {
					ogImmunosuppressiveTherapyBasicDisease.addValueChangeListener(getHighPriorityValueChangeListener(cbHighPriority));
					ogCareForPeopleOver60.addValueChangeListener(getHighPriorityValueChangeListener(cbHighPriority));
				}

				// Add follow-up until validator
				Date minimumFollowUpUntilDate = DateHelper.addDays(ContactLogic.getStartDate(lastContactDate.getValue(), reportDate.getValue()),
						FacadeProvider.getDiseaseConfigurationFacade().getFollowUpDuration((Disease) cbDisease.getValue()));
				dfFollowUpUntil.addValidator(new DateRangeValidator(I18nProperties.getValidationError(Validations.contactFollowUpUntilDate), 
						minimumFollowUpUntilDate, null, Resolution.DAY));
			}
		});

		setRequired(true, ContactDto.CONTACT_CLASSIFICATION, ContactDto.CONTACT_STATUS, ContactDto.REPORT_DATE_TIME);
		FieldHelper.addSoftRequiredStyle(lastContactDate, contactProximity, relationToCase);
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
			case MEDICAL_UNSAVE:
				contactCategory.setValue(ContactCategory.HIGH_RISK);
				break;
			case SAME_ROOM:
			case FACE_TO_FACE_SHORT:
			case MEDICAL_SAME_ROOM:
				contactCategory.setValue(ContactCategory.LOW_RISK);
				break;
			case MEDICAL_DISTANT:
			case MEDICAL_SAVE:
				contactCategory.setValue(ContactCategory.NO_RISK);
				break;
			default:
			}
		}
	}

	private void updateQuarantineFields() {
		QuarantineType quarantineType = (QuarantineType) quarantine.getValue();
		boolean visible;
		if (QuarantineType.HOME.equals(quarantineType) || QuarantineType.INSTITUTIONELL.equals(quarantineType)) {
			visible = true;
		} else {
			visible = false;
			quarantineFrom.clear();
			quarantineTo.clear();
		}

		quarantineFrom.setVisible(visible);
		quarantineTo.setVisible(visible);
		if (quarantineOrderMeans != null) {
			quarantineOrderMeans.setVisible(visible);
		}
		quarantineHelpNeeded.setVisible(visible);
	}

	private ValueChangeListener getHighPriorityValueChangeListener(CheckBox cbHighPriority) {
		return e -> {
			if (YesNoUnknown.YES.equals(e.getProperty().getValue())) {
				cbHighPriority.setValue(true);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void updateFollowUpStatusComponents() {

		getContent().removeComponent(CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
		getContent().removeComponent(LOST_FOLLOW_UP_BTN_LOC);

		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
		boolean followUpVisible = getValue() != null && statusField.isVisible();
		if (followUpVisible && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)) {
			FollowUpStatus followUpStatus = statusField.getValue();
			if (followUpStatus == FollowUpStatus.FOLLOW_UP) {

				Button cancelButton = new Button(I18nProperties.getCaption(Captions.contactCancelFollowUp));
				cancelButton.setWidth(100, Unit.PERCENTAGE);
				cancelButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.CANCELED);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(cancelButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);

				Button lostButton = new Button(I18nProperties.getCaption(Captions.contactLostToFollowUp));
				lostButton.setWidth(100, Unit.PERCENTAGE);
				lostButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.LOST);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(lostButton, LOST_FOLLOW_UP_BTN_LOC);

			} else if (followUpStatus == FollowUpStatus.CANCELED
					|| followUpStatus == FollowUpStatus.LOST) {

				Button resumeButton = new Button(I18nProperties.getCaption(Captions.contactResumeFollowUp));
				resumeButton.addStyleName(CssStyles.FORCE_CAPTION);
				resumeButton.setWidth(100, Unit.PERCENTAGE);
				resumeButton.addClickListener(new ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
						statusField.setReadOnly(false);
						statusField.setValue(FollowUpStatus.FOLLOW_UP);
						statusField.setReadOnly(true);
						updateFollowUpStatusComponents();
					}
				});
				getContent().addComponent(resumeButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
			}
		}		
	}

	protected void updateLastContactDateValidator() {
		Field<?> dateField = getField(ContactDto.LAST_CONTACT_DATE);
		for (Validator validator : dateField.getValidators()) {
			if (validator instanceof DateRangeValidator) {
				dateField.removeValidator(validator);
			}
		}
		if (getValue() != null) {
			dateField.addValidator(new DateRangeValidator(I18nProperties.getValidationError(Validations.beforeDate, dateField.getCaption(), getField(ContactDto.REPORT_DATE_TIME).getCaption()),
					null, new LocalDate(getValue().getReportDateTime()).plusDays(1).toDate(), Resolution.SECOND));
		}
	}

	private void updateDiseaseConfiguration(Disease disease) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(ContactDto.class, (String)propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible && getFieldGroup().getField(propertyId).isVisible());
		}

		ContactProximity value = (ContactProximity) contactProximity.getValue();
		FieldHelper.updateEnumData(contactProximity, Arrays.asList(ContactProximity.getValues(disease, FacadeProvider.getConfigFacade().getCountryLocale())));
		contactProximity.setValue(value);
	}

	public Disease getSelectedDisease() {
		if (getValue().getCaze() != null) {
			return getValue().getDisease();
		} else {
			return (Disease) cbDisease.getValue();
		}
	}

	public void setSourceCase(CaseIndexDto caze) {
		if (caze != null) {
			getValue().setCaze(caze.toReference());
			updateFieldVisibilitiesByCase(true);
		} else {
			getValue().setCaze(null);
			updateFieldVisibilitiesByCase(false);
		}
	}

	private void updateFieldVisibilitiesByCase(boolean caseSelected) {
		setVisible(!caseSelected, ContactDto.DISEASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION);
		setRequired(!caseSelected, ContactDto.DISEASE, ContactDto.REGION, ContactDto.DISTRICT);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
