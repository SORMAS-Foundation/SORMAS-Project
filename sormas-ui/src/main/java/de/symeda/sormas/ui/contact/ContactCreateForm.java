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

import java.util.Arrays;

import org.joda.time.LocalDate;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.DateRangeValidator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class ContactCreateForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String FIRST_NAME = PersonDto.FIRST_NAME;
	private static final String LAST_NAME = PersonDto.LAST_NAME; 
	private static final String CASE_INFO_LOC = "caseInfoLoc";
	private static final String CHOOSE_CASE_LOC = "chooseCaseLoc";
	private static final String REMOVE_CASE_LOC = "removeCaseLoc";

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(6, CASE_INFO_LOC, 3, CHOOSE_CASE_LOC, 3, REMOVE_CASE_LOC) +
			LayoutUtil.fluidRowLocs(ContactDto.REPORT_DATE_TIME, ContactDto.DISEASE) +
			LayoutUtil.fluidRowLocs(ContactDto.DISEASE_DETAILS) +
			LayoutUtil.fluidRowLocs(FIRST_NAME, LAST_NAME) +
			LayoutUtil.fluidRowLocs(ContactDto.REGION, ContactDto.DISTRICT) +
			LayoutUtil.fluidRowLocs(ContactDto.LAST_CONTACT_DATE, ContactDto.CASE_ID_EXTERNAL_SYSTEM) +
			LayoutUtil.fluidRowLocs(ContactDto.CASE_OR_EVENT_INFORMATION) +
			LayoutUtil.fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
			LayoutUtil.fluidRowLocs(ContactDto.RELATION_TO_CASE) +
			LayoutUtil.fluidRowLocs(ContactDto.RELATION_DESCRIPTION) +
			LayoutUtil.fluidRowLocs(ContactDto.DESCRIPTION);

	private OptionGroup contactProximity;
	private Disease disease;
	private Boolean hasCaseRelation;
	private CaseReferenceDto selectedCase;

	public ContactCreateForm(UserRight editOrCreateUserRight, Disease disease, boolean hasCaseRelation) {
		super(ContactDto.class, ContactDto.I18N_PREFIX, editOrCreateUserRight);

		this.disease = disease;
		this.hasCaseRelation = new Boolean(hasCaseRelation);

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
		TextField firstName = addCustomField(FIRST_NAME, String.class, TextField.class);
		TextField lastName = addCustomField(LAST_NAME, String.class, TextField.class);
		ComboBox region = addInfrastructureField(ContactDto.REGION);
		ComboBox district = addInfrastructureField(ContactDto.DISTRICT);

		DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
		contactProximity = addField(ContactDto.CONTACT_PROXIMITY, OptionGroup.class);
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		addField(ContactDto.DESCRIPTION, TextArea.class).setRows(2);
		ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
		addField(ContactDto.RELATION_DESCRIPTION, TextField.class);
		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(2);

		CssStyles.style(CssStyles.SOFT_REQUIRED, firstName, lastName, lastContactDate, contactProximity, relationToCase);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		setRequired(true, FIRST_NAME, LAST_NAME);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.RELATION_DESCRIPTION, ContactDto.RELATION_TO_CASE, Arrays.asList(ContactRelation.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.DISEASE_DETAILS, ContactDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.DISEASE, Arrays.asList(ContactDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));

		cbDisease.addValueChangeListener(e -> {
			disease = (Disease) e.getProperty().getValue();
			setVisible(disease != null, ContactDto.CONTACT_PROXIMITY);
			updateContactProximity();
		});

		if (!hasCaseRelation) {
			Label caseInfoLabel = new Label(I18nProperties.getString(Strings.infoNoSourceCaseSelected), ContentMode.HTML);
			Button chooseCaseButton = new Button(I18nProperties.getCaption(Captions.contactChooseCase));
			Button removeCaseButton = new Button(I18nProperties.getCaption(Captions.contactRemoveCase));

			CssStyles.style(caseInfoLabel, CssStyles.VSPACE_TOP_4);
			getContent().addComponent(caseInfoLabel, CASE_INFO_LOC);

			CssStyles.style(chooseCaseButton, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_2);
			chooseCaseButton.addClickListener(e -> {
				ControllerProvider.getContactController().openSelectCaseForContactWindow((Disease) cbDisease.getValue(), selectedCase -> {
					if (selectedCase != null) {
						this.selectedCase = selectedCase.toReference();
						caseInfoLabel.setValue(String.format(I18nProperties.getString(Strings.infoContactCreationSourceCase), 
								selectedCase.getPersonFirstName() + " " + selectedCase.getPersonLastName() + " " + "(" +
										DataHelper.getShortUuid(selectedCase.getUuid()) + ")"));
						caseInfoLabel.removeStyleName(CssStyles.VSPACE_TOP_4);
						removeCaseButton.setVisible(true);
						chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));

						cbDisease.setValue(selectedCase.getDisease());
						updateFieldVisibilitiesByCase(true);
					}
				});
			});
			getContent().addComponent(chooseCaseButton, CHOOSE_CASE_LOC);

			CssStyles.style(removeCaseButton, ValoTheme.BUTTON_LINK);
			removeCaseButton.addClickListener(e -> {
				this.selectedCase = null;
				caseInfoLabel.setValue(I18nProperties.getString(Strings.infoNoSourceCaseSelected));
				caseInfoLabel.addStyleName(CssStyles.VSPACE_TOP_4);
				removeCaseButton.setVisible(false);
				chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChooseCase));
				
				updateFieldVisibilitiesByCase(false);
			});
			getContent().addComponent(removeCaseButton, REMOVE_CASE_LOC);
			removeCaseButton.setVisible(false);
		}

		addValueChangeListener(e -> {
			updateFieldVisibilitiesByCase(hasCaseRelation);
			if (!hasCaseRelation && disease == null) {
				setVisible(false, ContactDto.CONTACT_PROXIMITY);
			}

			updateContactProximity();
		});
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
			dateField.addValidator(new DateRangeValidator(I18nProperties.getValidationError(Validations.beforeDate, 
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE), 
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)),
					null, new LocalDate(getValue().getReportDateTime()).toDate(), Resolution.SECOND));
		}
	}

	private void updateContactProximity() {
		ContactProximity value = (ContactProximity) contactProximity.getValue();
		FieldHelper.updateEnumData(contactProximity, Arrays.asList(ContactProximity.getValues(disease, FacadeProvider.getConfigFacade().getCountryLocale())));
		contactProximity.setValue(value);
	}

	public String getPersonFirstName() {
		return (String) getField(FIRST_NAME).getValue();
	}

	public String getPersonLastName() {
		return (String) getField(LAST_NAME).getValue();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
