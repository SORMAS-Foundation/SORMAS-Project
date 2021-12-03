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

package de.symeda.sormas.ui.labmessage;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FutureDateValidator;

public class LabMessageGridFilterForm extends AbstractFilterForm<LabMessageCriteria> {

	private static final long serialVersionUID = -7375416530959728367L;

	protected LabMessageGridFilterForm() {
		super(LabMessageCriteria.class, LabMessageIndexDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			LabMessageCriteria.SEARCH_FIELD_LIKE,
			LabMessageCriteria.MESSAGE_DATE_FROM,
			LabMessageCriteria.MESSAGE_DATE_TO,
			LabMessageCriteria.BIRTH_DATE_FROM,
			LabMessageCriteria.BIRTH_DATE_TO };
	}

	@Override
	protected void addFields() {
		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LabMessageCriteria.SEARCH_FIELD_LIKE, I18nProperties.getString(Strings.promptLabMessagesSearchField), 200));
		searchField.setNullRepresentation("");

		DateField messageDateFrom = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LabMessageCriteria.MESSAGE_DATE_FROM, I18nProperties.getString(Strings.promptLabMessagesDateFrom), 200));

		DateField messageDateTo = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LabMessageCriteria.MESSAGE_DATE_TO, I18nProperties.getString(Strings.promptLabMessagesDateTo), 200));
		messageDateFrom.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.MESSAGE_DATE_FROM));
		messageDateTo.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.MESSAGE_DATE_TO));
		DateComparisonValidator.addStartEndValidators(messageDateFrom, messageDateTo);

		DateField personBirthDateFrom = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				LabMessageCriteria.BIRTH_DATE_FROM,
				I18nProperties.getString(Strings.promptLabMessagesPersonBirthDateFrom),
				200));

		DateField personBirthDateTo = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				LabMessageCriteria.BIRTH_DATE_TO,
				I18nProperties.getString(Strings.promptLabMessagesPersonBirthDateTo),
				200));
		personBirthDateFrom.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.BIRTH_DATE_FROM));
		personBirthDateTo.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.BIRTH_DATE_TO));
		DateComparisonValidator.addStartEndValidators(personBirthDateFrom, personBirthDateTo);

		initDateFields(messageDateFrom, messageDateTo, personBirthDateFrom, personBirthDateTo);
	}

	private void initDateFields(DateField... dateFields) {
		for (DateField dateField : dateFields) {
			dateField.addStyleName("caption-hidden");
			for (Validator validator : dateField.getValidators()) {
				if (validator instanceof FutureDateValidator) {
					((FutureDateValidator) validator).resetWithCaption();
				}
			}
		}
	}
}
