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
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class PersonSelectionFilterForm extends AbstractFilterForm<PersonSimilarityCriteria> {

	private static final long serialVersionUID = 3261148255867475008L;

	protected PersonSelectionFilterForm() {
		super(PersonSimilarityCriteria.class, CaseDataDto.I18N_PREFIX, null, Captions.actionSearch, Captions.actionReset);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			PersonSimilarityCriteria.FIRST_NAME,
			PersonSimilarityCriteria.LAST_NAME,
			PersonSimilarityCriteria.UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE };
	}

	@Override
	protected void addFields() {
		addField(
			FieldConfiguration.withCaptionAndPixelSized(PersonSimilarityCriteria.FIRST_NAME, I18nProperties.getCaption(Captions.firstName), 100));
		addField(FieldConfiguration.withCaptionAndPixelSized(PersonSimilarityCriteria.LAST_NAME, I18nProperties.getCaption(Captions.lastName), 100));

		addField(
			FieldConfiguration.withCaptionAndPixelSized(
				PersonSimilarityCriteria.UUID_EXTERNAL_ID_EXTERNAL_TOKEN_LIKE,
				I18nProperties.getString(Strings.promptPersonDuplicateSearchIdExternalId),
				150)).addStyleName(CssStyles.HSPACE_RIGHT_3);
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
}
