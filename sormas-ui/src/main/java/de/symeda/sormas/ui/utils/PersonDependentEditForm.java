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

package de.symeda.sormas.ui.utils;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.person.PersonSearchField;

public abstract class PersonDependentEditForm<DTO> extends AbstractEditForm<DTO> {

	protected static final String PERSON_SEARCH_LOC = "personSearchLoc";

	private PersonDto searchedPerson;

	protected PersonDependentEditForm(Class<DTO> type, String propertyI18nPrefix) {
		super(type, propertyI18nPrefix);
	}

	protected PersonDependentEditForm(Class<DTO> type, String propertyI18nPrefix, FieldVisibilityCheckers fieldVisibilityCheckers) {
		super(type, propertyI18nPrefix, fieldVisibilityCheckers);
	}

	protected PersonDependentEditForm(
		Class<DTO> type,
		String propertyI18nPrefix,
		boolean addFields,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(type, propertyI18nPrefix, addFields, fieldVisibilityCheckers, fieldAccessCheckers);
	}

	protected Button createPersonSearchButton(String personSearchLoc) {
		return ButtonHelper.createIconButtonWithCaption(personSearchLoc, StringUtils.EMPTY, VaadinIcons.SEARCH, clickEvent -> {
			VaadinIcons icon = (VaadinIcons) clickEvent.getButton().getIcon();
			if (icon == VaadinIcons.SEARCH) {
				PersonSearchField personSearchField = new PersonSearchField(null, I18nProperties.getString(Strings.infoSearchPerson));
				personSearchField.setWidth(1280, Unit.PIXELS);

				final CommitDiscardWrapperComponent<PersonSearchField> component = new CommitDiscardWrapperComponent<>(personSearchField);
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
				component.getCommitButton().setEnabled(false);
				component.addCommitListener(() -> {
					SimilarPersonDto pickedPerson = personSearchField.getValue();
					if (pickedPerson != null) {
						// add consumer
						searchedPerson = FacadeProvider.getPersonFacade().getPersonByUuid(pickedPerson.getUuid());
						setPerson(searchedPerson);
						enablePersonFields(false);
						clickEvent.getButton().setIcon(VaadinIcons.CLOSE);
					}
				});

				personSearchField.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingSelectPerson));
			} else {
				searchedPerson = null;
				setPerson(searchedPerson);
				enablePersonFields(true);
				clickEvent.getButton().setIcon(VaadinIcons.SEARCH);
			}
		}, CssStyles.FORCE_CAPTION);
	}

	public abstract void setPerson(PersonDto person);

	protected abstract void enablePersonFields(Boolean enable);

	public PersonDto getSearchedPerson() {
		return searchedPerson;
	}

	public void setSearchedPerson(PersonDto searchedPerson) {
		this.searchedPerson = searchedPerson;
	}
}
