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
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.ui.utils.AbstractFilterForm;

public class PersonSelectionFilterForm extends AbstractFilterForm<PersonSimilarityCriteria> {

	private static final long serialVersionUID = 3261148255867475008L;

	private TextField firstName;
	private TextField lastName;

	protected PersonSelectionFilterForm() {
		super(PersonSimilarityCriteria.class, CaseDataDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			PersonSimilarityCriteria.FIRST_NAME,
			PersonSimilarityCriteria.LAST_NAME };
	}

	@Override
	protected void addFields() {
		firstName = addField(PersonSimilarityCriteria.FIRST_NAME);
		firstName.setInputPrompt(I18nProperties.getCaption(Captions.firstName));

		lastName = addField(PersonSimilarityCriteria.LAST_NAME);
		lastName.setInputPrompt(I18nProperties.getCaption(Captions.lastName));
	}

	public boolean validateNameFields() {
		boolean valid = true;
		if (firstName.isEmpty()) {
			firstName.setComponentError(
				new UserError(I18nProperties.getValidationError(Validations.required, I18nProperties.getCaption(Captions.firstName))));
			valid = false;
		}

		if (lastName.isEmpty()) {
			lastName.setComponentError(
				new UserError(I18nProperties.getValidationError(Validations.required, I18nProperties.getCaption(Captions.lastName))));
			valid = false;
		}

		return valid;
	}
}
