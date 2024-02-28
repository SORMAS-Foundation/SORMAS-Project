/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.FutureDateValidator;

public class ExternalMessageGridFilterForm extends AbstractFilterForm<ExternalMessageCriteria> {

	private static final long serialVersionUID = -7375416530959728367L;

	protected ExternalMessageGridFilterForm() {
		super(ExternalMessageCriteria.class, ExternalMessageIndexDto.I18N_PREFIX, null);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			ExternalMessageCriteria.SEARCH_FIELD_LIKE,
			ExternalMessageCriteria.MESSAGE_CONTENT_LIKE,
			ExternalMessageCriteria.ASSIGNEE,
			ExternalMessageCriteria.TYPE,
			ExternalMessageCriteria.DISEASE,
			ExternalMessageCriteria.DISEASE_VARIANT,
			ExternalMessageCriteria.MESSAGE_DATE_FROM,
			ExternalMessageCriteria.MESSAGE_DATE_TO,
			ExternalMessageCriteria.BIRTH_DATE_FROM,
			ExternalMessageCriteria.BIRTH_DATE_TO };
	}

	@Override
	protected void addFields() {
		UserDto user = currentUserDto();

		TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ExternalMessageCriteria.SEARCH_FIELD_LIKE,
				I18nProperties.getString(Strings.promptExternalMessagesSearchField),
				200));
		searchField.setNullRepresentation("");

		TextField contentSearchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ExternalMessageCriteria.MESSAGE_CONTENT_LIKE,
				I18nProperties.getString(Strings.promptExternalMessagesContentSearchField),
				200));
		contentSearchField.setNullRepresentation("");

		ComboBoxWithPlaceholder assignee =
			addField(FieldConfiguration.pixelSized(ExternalMessageCriteria.ASSIGNEE, 200), ComboBoxWithPlaceholder.class);
		assignee.addItem(new UserReferenceDto(ReferenceDto.NO_REFERENCE_UUID, "", "", I18nProperties.getCaption(Captions.unassigned)));
		assignee.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRights(user.getRegion(), null, UserRight.EXTERNAL_MESSAGE_PROCESS));
		assignee.setNullSelectionAllowed(true);

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			addField(FieldConfiguration.pixelSized(ExternalMessageDto.TYPE, 140), ComboBox.class);
		}

		addField(FieldConfiguration.pixelSized(ExternalMessageCriteria.DISEASE, 140));
		addField(FieldConfiguration.pixelSized(CaseDataDto.DISEASE_VARIANT, 140), ComboBox.class);

		DateTimeField messageDateFrom = addField(ExternalMessageCriteria.MESSAGE_DATE_FROM, DateTimeField.class);
		messageDateFrom.setCaption(I18nProperties.getPrefixCaption(ExternalMessageCriteria.I18N_PREFIX, ExternalMessageCriteria.MESSAGE_DATE_FROM));
		messageDateFrom.setInputPrompt(I18nProperties.getString(Strings.promptExternalMessagesDateFrom));
		messageDateFrom.setDescription(I18nProperties.getString(Strings.promptExternalMessagesDateFrom));

		DateTimeField messageDateTo = addField(ExternalMessageCriteria.MESSAGE_DATE_TO, DateTimeField.class);
		messageDateTo.setCaption(I18nProperties.getPrefixCaption(ExternalMessageCriteria.I18N_PREFIX, ExternalMessageCriteria.MESSAGE_DATE_TO));
		messageDateTo.setInputPrompt(I18nProperties.getString(Strings.promptExternalMessagesDateTo));
		messageDateTo.setDescription(I18nProperties.getString(Strings.promptExternalMessagesDateTo));
		DateComparisonValidator.addStartEndValidators(messageDateFrom, messageDateTo, false);

		DateField personBirthDateFrom = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ExternalMessageCriteria.BIRTH_DATE_FROM,
				I18nProperties.getString(Strings.promptExternalMessagesPersonBirthDateFrom),
				200));

		DateField personBirthDateTo = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ExternalMessageCriteria.BIRTH_DATE_TO,
				I18nProperties.getString(Strings.promptExternalMessagesPersonBirthDateTo),
				200));
		personBirthDateFrom.setCaption(I18nProperties.getPrefixCaption(ExternalMessageCriteria.I18N_PREFIX, ExternalMessageCriteria.BIRTH_DATE_FROM));
		personBirthDateTo.setCaption(I18nProperties.getPrefixCaption(ExternalMessageCriteria.I18N_PREFIX, ExternalMessageCriteria.BIRTH_DATE_TO));
		DateComparisonValidator.addStartEndValidators(personBirthDateFrom, personBirthDateTo);

		initDateFields(messageDateFrom, messageDateTo, personBirthDateFrom, personBirthDateTo);
	}

	private void initDateFields(Field<Date>... dateFields) {
		for (Field<Date> dateField : dateFields) {
			dateField.addStyleName("caption-hidden");
			for (Validator validator : dateField.getValidators()) {
				if (validator instanceof FutureDateValidator) {
					((FutureDateValidator) validator).resetWithCaption();
				}
			}
		}
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		if (ExternalMessageDto.DISEASE.equals(propertyId)) {
			ComboBox diseaseVariantField = getField(CaseDataDto.DISEASE_VARIANT);
			Disease disease = (Disease) event.getProperty().getValue();
			if (disease == null) {
				FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
				FieldHelper.setEnabled(false, diseaseVariantField);
			} else {
				List<DiseaseVariant> diseaseVariants =
					FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
				FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
				FieldHelper.setEnabled(CollectionUtils.isNotEmpty(diseaseVariants), diseaseVariantField);
			}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(ExternalMessageCriteria criteria) {

		ComboBox diseaseField = getField(ExternalMessageDto.DISEASE);
		ComboBox diseaseVariantField = getField(ExternalMessageDto.DISEASE_VARIANT);
		Disease disease = (Disease) diseaseField.getValue();
		if (disease == null) {
			FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
			FieldHelper.setEnabled(false, diseaseVariantField);
		} else {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			FieldHelper.setEnabled(CollectionUtils.isNotEmpty(diseaseVariants), diseaseVariantField);
		}
	}
}
