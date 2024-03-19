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

package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CountriesGrid extends FilteredGrid<CountryIndexDto, CountryCriteria> {

	private static final long serialVersionUID = -8192499609737564649L;

	List<CountryIndexDto> allCountries;

	public CountriesGrid(CountryCriteria criteria) {
		super(CountryIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CountriesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		super.setCriteria(criteria, true);
		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			CountryIndexDto.ISO_CODE,
			CountryIndexDto.DISPLAY_NAME,
			CountryIndexDto.SUBCONTINENT,
			CountryIndexDto.EXTERNAL_ID,
			CountryIndexDto.UNO_CODE,
			CountryIndexDto.DEFAULT_NAME);
		getColumn(CountryIndexDto.DEFAULT_NAME).setHidden(true);

		if (UiUtil.permitted(FeatureType.EDIT_INFRASTRUCTURE_DATA, UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editCountry(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CountryDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		reload();
	}

	public void reload() {
		setSelectionMode(isInEagerMode() ? SelectionMode.MULTI : SelectionMode.NONE);
		if (ViewModelProviders.of(CountriesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	private Stream<CountryIndexDto> createFilteredStream() {

		// get all filter properties
		String nameCodeLike = getCriteria().getNameCodeLike() != null ? getCriteria().getNameCodeLike().toLowerCase() : null;
		String subcontinentUuid = getCriteria().getSubcontinent() != null ? getCriteria().getSubcontinent().getUuid() : null;
		EntityRelevanceStatus relevanceStatus = getCriteria().getRelevanceStatus();

		Predicate<CountryIndexDto> filters = x -> true; // "empty" basefilter

		// nameCodeLike Filter
		if (!StringUtils.isEmpty(nameCodeLike)) {
			filters = filters.and(
				country -> (country.getDefaultName().toLowerCase().contains(nameCodeLike)
					|| country.getDisplayName().toLowerCase().contains(nameCodeLike)
					|| country.getIsoCode().toLowerCase().contains(nameCodeLike)
					|| (country.getUnoCode() != null && country.getUnoCode().toLowerCase().contains(nameCodeLike))));
		}
		// subcontinent filter
		if (subcontinentUuid != null) {
			filters = filters.and(country -> (country.getSubcontinent() != null && country.getSubcontinent().getUuid().equals(subcontinentUuid)));
		}
		// relevancestatus filter (active/archived/all)
		if (relevanceStatus != null) {
			switch (relevanceStatus) {
			case ACTIVE:
				filters = filters.and(country -> (!country.isArchived()));
				break;
			case ARCHIVED:
				filters = filters.and(country -> (country.isArchived()));
				break;
			}
		}

		// apply filters
		return allCountries.stream().filter(filters);

	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getCountryFacade()::getIndexList, FacadeProvider.getCountryFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getCountryFacade()::getIndexList);
	}

}
