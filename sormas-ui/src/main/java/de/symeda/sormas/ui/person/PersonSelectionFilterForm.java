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

package de.symeda.sormas.ui.person;

import com.vaadin.server.UserError;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class PersonSelectionFilterForm extends AbstractFilterForm<PersonSimilarityCriteria> {

	private static final long serialVersionUID = 3261148255867475008L;

	protected PersonSelectionFilterForm() {
		super(PersonSimilarityCriteria.class, CaseDataDto.I18N_PREFIX, null, Captions.actionSearch, Captions.actionReset, null, true);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			PersonSimilarityCriteria.NAME_UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE,
			PersonSimilarityCriteria.BIRTHDATE_YYYY,
			PersonSimilarityCriteria.BIRTHDATE_MM,
			PersonSimilarityCriteria.BIRTHDATE_DD };
	}

	@Override
	protected void addFields() {

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				PersonSimilarityCriteria.NAME_UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE,
				I18nProperties.getString(Strings.promptPersonDuplicateSearchIdExternalId),
				150)).addStyleName(CssStyles.HSPACE_RIGHT_3);

		addBirthDateFields(getContent(), PersonSimilarityCriteria.BIRTHDATE_YYYY, PersonSimilarityCriteria.BIRTHDATE_MM, PersonSimilarityCriteria.BIRTHDATE_DD);
	}

	public boolean validateFields() {
		String validationError = I18nProperties.getValidationError(Validations.nameOrAnyOtherFieldShouldBeFilled);

		clearValidation();

		if (getFieldGroup().getFields().stream().allMatch(Field::isEmpty)) {
			getFieldGroup().getFields().forEach(f -> {
				if (AbstractField.class.isAssignableFrom(f.getClass())) {
					((AbstractField<?>) f).setComponentError(new UserError(validationError));
				}
			});

			return false;
		}

		return true;
	}

	public void clearValidation() {
		getFieldGroup().getFields().forEach(f -> {
			if (AbstractField.class.isAssignableFrom(f.getClass())) {
				((AbstractField<?>) f).setComponentError(null);
			}
		});
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		switch (propertyId) {
		case PersonSimilarityCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(PersonSimilarityCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(PersonSimilarityCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(PersonSimilarityCriteria.BIRTHDATE_YYYY).getValue()));
			break;
		}
		}
	}

}
