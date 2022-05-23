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

import java.util.Date;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
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
			LabMessageCriteria.ASSIGNEE,
			LabMessageCriteria.TYPE,
			LabMessageCriteria.MESSAGE_DATE_FROM,
			LabMessageCriteria.MESSAGE_DATE_TO,
			LabMessageCriteria.BIRTH_DATE_FROM,
			LabMessageCriteria.BIRTH_DATE_TO };
	}

	@Override
	protected void addFields() {
		UserDto user = currentUserDto();

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LabMessageCriteria.SEARCH_FIELD_LIKE, I18nProperties.getString(Strings.promptLabMessagesSearchField), 200));
		searchField.setNullRepresentation("");

		ComboBoxWithPlaceholder assignee = addField(LabMessageCriteria.ASSIGNEE, ComboBoxWithPlaceholder.class);
		assignee.addItem(new UserReferenceDto(ReferenceDto.NO_REFERENCE_UUID, I18nProperties.getCaption(Captions.unassigned), ""));
		assignee.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRights(user.getRegion(), null, UserRight.LAB_MESSAGES));
		assignee.setNullSelectionAllowed(true);

		addField(LabMessageDto.TYPE, ComboBox.class);

		DateTimeField messageDateFrom = addField(LabMessageCriteria.MESSAGE_DATE_FROM, DateTimeField.class);
		messageDateFrom.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.MESSAGE_DATE_FROM));
		messageDateFrom.setInputPrompt(I18nProperties.getString(Strings.promptLabMessagesDateFrom));

		DateTimeField messageDateTo = addField(LabMessageCriteria.MESSAGE_DATE_TO, DateTimeField.class);
		messageDateTo.setCaption(I18nProperties.getPrefixCaption(LabMessageCriteria.I18N_PREFIX, LabMessageCriteria.MESSAGE_DATE_TO));
		messageDateTo.setInputPrompt(I18nProperties.getString(Strings.promptLabMessagesDateTo));
		DateComparisonValidator.addStartEndValidators(messageDateFrom, messageDateTo, false);

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
}
