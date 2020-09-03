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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.person;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.ui.UserProvider;

@SuppressWarnings("serial")
public class PersonSelectionGrid extends Grid {

	public PersonSelectionGrid(PersonSimilarityCriteria criteria) {
		buildGrid();
		loadData(criteria);
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<SimilarPersonDto> container = new BeanItemContainer<>(SimilarPersonDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			SimilarPersonDto.FIRST_NAME,
			SimilarPersonDto.LAST_NAME,
			SimilarPersonDto.NICKNAME,
			SimilarPersonDto.APPROXIMATE_AGE,
			SimilarPersonDto.SEX,
			SimilarPersonDto.PRESENT_CONDITION,
			SimilarPersonDto.DISTRICT_NAME,
			SimilarPersonDto.COMMUNITY_NAME,
			SimilarPersonDto.CITY,
			SimilarPersonDto.NATIONAL_HEALTH_ID,
			SimilarPersonDto.PASSPORT_NUMBER);

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.getPrefixCaption(PersonIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		getColumn(PersonIndexDto.FIRST_NAME).setMinimumWidth(150);
		getColumn(PersonIndexDto.LAST_NAME).setMinimumWidth(150);
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<SimilarPersonDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SimilarPersonDto>) container.getWrappedContainer();
	}

	private void loadData(PersonSimilarityCriteria criteria) {
		List<String> similarPersonUuids = FacadeProvider.getPersonFacade()
			.getMatchingNameDtos(UserProvider.getCurrent().getUserReference(), criteria)
			.stream()
			.filter(
				dto -> PersonHelper.areNamesSimilar(
					criteria.getFirstName(),
					criteria.getLastName(),
					dto.getFirstName(),
					dto.getLastName(),
					FacadeProvider.getConfigFacade().getNameSimilarityThreshold()))
			.map(PersonNameDto::getUuid)
			.collect(Collectors.toList());
		List<SimilarPersonDto> similarPersons = FacadeProvider.getPersonFacade().getSimilarPersonsByUuids(similarPersonUuids);

		getContainer().removeAllItems();
		getContainer().addAll(similarPersons);
		setHeightByRows(similarPersons.size() > 0 ? (Math.min(similarPersons.size(), 10)) : 1);
	}
}
