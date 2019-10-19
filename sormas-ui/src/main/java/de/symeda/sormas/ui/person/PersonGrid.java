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
package de.symeda.sormas.ui.person;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.MethodProperty;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.ui.UserProvider;

@SuppressWarnings("serial")
public class PersonGrid extends Grid {

	private final List<PersonNameDto> persons;

	private UserReferenceDto currentUser;

	/**
	 * Initializes the person grid with variable first and last names, dynamically retrieving
	 * the list of person names.
	 */
	public PersonGrid(String firstName, String lastName) {
		persons = FacadeProvider.getPersonFacade().getNameDtos(UserProvider.getCurrent().getUserReference());
		buildGrid();
		reload(firstName, lastName);
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<PersonIndexDto> container = new BeanItemContainer<PersonIndexDto>(PersonIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(PersonIndexDto.FIRST_NAME, PersonIndexDto.LAST_NAME, PersonIndexDto.NICKNAME, 
				PersonIndexDto.APPROXIMATE_AGE, PersonIndexDto.SEX, PersonIndexDto.PRESENT_CONDITION,
				PersonIndexDto.DISTRICT_NAME, PersonIndexDto.COMMUNITY_NAME, PersonIndexDto.CITY);

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					PersonIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		getColumn(PersonIndexDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(PersonIndexDto.LAST_NAME).setMinimumWidth(150);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<PersonIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<PersonIndexDto>) container.getWrappedContainer();
	}

	public void reload(String firstName, String lastName) {
		List<PersonIndexDto> entries = new ArrayList<>();
		for (PersonNameDto person : persons) {
			if (PersonHelper.areNamesSimilar(firstName + " " + lastName, person.getFirstName() + " " + person.getLastName())) {
				PersonIndexDto indexDto = FacadeProvider.getPersonFacade().getIndexDto(person.getUuid());
				entries.add(indexDto);
			}
		}

		getContainer().removeAllItems();
		getContainer().addAll(entries);    
		setHeightByRows(entries.size() > 0 ? (entries.size() <= 10 ? entries.size() : 10) : 1);
	}

	public void refresh(PersonIndexDto entry) {
		// We avoid updating the whole table through the backend here so we can
		// get a partial update for the grid
		BeanItem<PersonIndexDto> item = getContainer().getItem(entry);
		if (item != null) {
			// Updated product
			@SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(PersonIndexDto.UUID);
			p.fireValueChange();
		} else {
			// New product
			getContainer().addBean(entry);
		}
	}

	public void remove(PersonIndexDto entry) {
		getContainer().removeItem(entry);
	}

}


