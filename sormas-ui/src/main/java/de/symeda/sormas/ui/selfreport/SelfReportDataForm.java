/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.selfreport;

import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.luxembourg.LuxembourgNationalHealthIdValidator;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;
import de.symeda.sormas.ui.utils.ValidationUtils;
import de.symeda.sormas.ui.utils.components.BirthdateFields;

public class SelfReportDataForm extends AbstractEditForm<SelfReportDto> {

	private static final long serialVersionUID = 4231818066366722425L;

	private static final String NATIONAL_HEALTH_ID_WARNING_LABEL = "nationalHealthIdWarningLoc";
	private static final String ADDRESS_HEADER_LOC = "addressHeaderLoc";

	private static final String HTML_LAYOUT = fluidRowLocs(SelfReportDto.UUID, SelfReportDto.TYPE)
		+ fluidRowLocs(SelfReportDto.INVESTIGATION_STATUS, SelfReportDto.PROCESSING_STATUS)
		+ fluidRowLocs(SelfReportDto.REPORT_DATE, SelfReportDto.CASE_REFERENCE)
		+ fluidRowLocs(SelfReportDto.DISEASE, SelfReportDto.DISEASE_VARIANT)
		+ fluidRowLocs(SelfReportDto.FIRST_NAME, SelfReportDto.LAST_NAME)
		+ fluidRow(fluidRowLocs(PersonDto.BIRTH_DATE_YYYY, PersonDto.BIRTH_DATE_MM, PersonDto.BIRTH_DATE_DD), fluidRowLocs(SelfReportDto.SEX))
		+ fluidRowLocs(SelfReportDto.NATIONAL_HEALTH_ID, "")
		+ fluidRowLocs(NATIONAL_HEALTH_ID_WARNING_LABEL, "")
		+ loc(ADDRESS_HEADER_LOC)
		+ divsCss(VSPACE_3, fluidRowLocs(SelfReportDto.ADDRESS))
		+ fluidRowLocs(SelfReportDto.EMAIL, SelfReportDto.PHONE_NUMBER)
		+ fluidRowLocs(SelfReportDto.DATE_OF_TEST, SelfReportDto.DATE_OF_SYMPTOMS)
		+ fluidRowLocs(SelfReportDto.WORKPLACE, SelfReportDto.DATE_WORKPLACE)
		+ fluidRowLocs(SelfReportDto.ISOLATION_DATE, SelfReportDto.CONTACT_DATE)
		+ fluidRowLocs(SelfReportDto.COMMENT)
		+ fluidRowLocs(SelfReportDto.RESPONSIBLE_USER, "")
		+ fluidRowLocs(SelfReportDto.DELETION_REASON)
		+ fluidRowLocs(SelfReportDto.OTHER_DELETION_REASON);
	private LocationEditForm addressForm;

	public SelfReportDataForm(Disease disease, boolean inJurisdiction, boolean isPseudonymized) {
		super(
			SelfReportDto.class,
			SelfReportDto.I18N_PREFIX,
			true,
			FieldVisibilityCheckers.withDisease(disease).add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
			UiFieldAccessCheckers.forDataAccessLevel(UiUtil.getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized));
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		addField(SelfReportDto.UUID).setReadOnly(true);
		addField(SelfReportDto.TYPE).setReadOnly(true);

		addField(SelfReportDto.INVESTIGATION_STATUS, OptionGroup.class);
		addField(SelfReportDto.PROCESSING_STATUS, OptionGroup.class).setReadOnly(true);

		addField(SelfReportDto.REPORT_DATE).setRequired(true);
		addField(SelfReportDto.CASE_REFERENCE);

		//disease related fieldss
		ComboBox diseaseField = addDiseaseField(SelfReportDto.DISEASE, false);
		addField(SelfReportDto.DISEASE_DETAILS, TextField.class);
		diseaseField.setRequired(true);
		ComboBox diseaseVariantField = addCustomizableEnumField(SelfReportDto.DISEASE_VARIANT);
		TextField diseaseVariantDetailsField = addField(SelfReportDto.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);
		if (isVisibleAllowed(SelfReportDto.DISEASE_DETAILS)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(SelfReportDto.DISEASE_DETAILS),
				SelfReportDto.DISEASE,
				Arrays.asList(Disease.OTHER),
				true);
			FieldHelper
				.setRequiredWhen(getFieldGroup(), SelfReportDto.DISEASE, Arrays.asList(SelfReportDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		}
		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField
				.setVisible(disease != null && isVisibleAllowed(SelfReportDto.DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		// person fields
		addField(SelfReportDto.FIRST_NAME).setRequired(true);
		addField(SelfReportDto.LAST_NAME).setRequired(true);

		BirthdateFields birthdateFields = new BirthdateFields(SelfReportDto.BIRTHDATE_DD, SelfReportDto.BIRTHDATE_MM, SelfReportDto.BIRTHDATE_YYYY);
		birthdateFields.initFields(propertyId -> addField(propertyId, ComboBox.class), () -> {
		});

		addField(SelfReportDto.SEX, ComboBox.class).setRequired(true);

		TextField nationalHealthIdField = addField(PersonDto.NATIONAL_HEALTH_ID);
		Label nationalHealthIdWarningLabel = new Label(I18nProperties.getString(Strings.messagePersonNationalHealthIdInvalid));
		nationalHealthIdWarningLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		nationalHealthIdWarningLabel.setVisible(false);
		getContent().addComponent(nationalHealthIdWarningLabel, NATIONAL_HEALTH_ID_WARNING_LABEL);

		//address
		Label addressHeader = new Label(I18nProperties.getPrefixCaption(SelfReportDto.I18N_PREFIX, SelfReportDto.ADDRESS));
		CssStyles.style(CssStyles.H3, addressHeader);
		getContent().addComponent(addressHeader, ADDRESS_HEADER_LOC);

		addressForm = addField(SelfReportDto.ADDRESS, LocationEditForm.class);
		addressForm.setCaption(null);

		// contact info fields
		TextField emailField = addField(SelfReportDto.EMAIL, TextField.class);
		emailField.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, emailField.getCaption())));
		TextField phoneNumberField = addField(SelfReportDto.PHONE_NUMBER, TextField.class);
		phoneNumberField
			.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, phoneNumberField.getCaption())));
		setSoftRequired(true, SelfReportDto.EMAIL, SelfReportDto.PHONE_NUMBER);

		addFields(
			SelfReportDto.DATE_OF_TEST,
			SelfReportDto.DATE_OF_SYMPTOMS,
			SelfReportDto.WORKPLACE,
			SelfReportDto.DATE_WORKPLACE,
			SelfReportDto.ISOLATION_DATE,
			SelfReportDto.CONTACT_DATE);
		setSoftRequired(true, SelfReportDto.DATE_OF_TEST, SelfReportDto.DATE_OF_SYMPTOMS);
		FieldHelper.setVisibleWhen(getFieldGroup(), SelfReportDto.CONTACT_DATE, SelfReportDto.TYPE, SelfReportType.CONTACT, true);

		addField(SelfReportDto.COMMENT, TextArea.class).setRows(6);

		ComboBox responsibleUserField = addField(SelfReportDto.RESPONSIBLE_USER, ComboBox.class);
		List<UserReferenceDto> responsibleUsers = FacadeProvider.getUserFacade()
			.getUserRefsByInfrastructure(null, JurisdictionLevel.NATION, JurisdictionLevel.NATION, null, UserRight.SELF_REPORT_EDIT);
		FieldHelper.updateItems(responsibleUserField, responsibleUsers);

		// soft delete related fields
		addField(SelfReportDto.DELETION_REASON);
		addField(SelfReportDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, SelfReportDto.DELETION_REASON, SelfReportDto.OTHER_DELETION_REASON);

		addValueChangeListener(e -> {
			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				ValidationUtils.initComponentErrorValidator(
					nationalHealthIdField,
					nationalHealthIdField.getValue(),
					Validations.invalidNationalHealthId,
					nationalHealthIdWarningLabel,
					nationalHealthId -> !LuxembourgNationalHealthIdValidator.isValid(
						nationalHealthId,
						(Integer) birthdateFields.getBirthDateYear().getValue(),
						(Integer) birthdateFields.getBirthDateMonth().getValue(),
						(Integer) birthdateFields.getBirthDateDay().getValue()),
					ErrorLevel.WARNING);
			}
		});

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();
	}

	@Override
	public void setValue(SelfReportDto newFieldValue) {
		super.setValue(newFieldValue);

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		// @see PersonEditForm::setValue
		addressForm.discard();
	}
}
