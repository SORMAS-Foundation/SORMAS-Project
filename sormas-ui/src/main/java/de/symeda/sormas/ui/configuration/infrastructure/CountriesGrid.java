/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
			CountryIndexDto.ISO2_CODE,
			CountryIndexDto.ISO_NUMERIC_CODE,
			CountryIndexDto.UNO_CODE,
			CountryIndexDto.DEFAULT_NAME,
			CountryIndexDto.NUTS_CODE,
			CountryIndexDto.EPIPULSE_LOCATION_CODE);
		// hidden in the view, but written by the export of the countries view
		getColumn(CountryIndexDto.EXTERNAL_ID).setHidden(true);
		getColumn(CountryIndexDto.ISO2_CODE).setHidden(true);
		getColumn(CountryIndexDto.ISO_NUMERIC_CODE).setHidden(true);
		getColumn(CountryIndexDto.UNO_CODE).setHidden(true);
		getColumn(CountryIndexDto.DEFAULT_NAME).setHidden(true);
		getColumn(CountryIndexDto.NUTS_CODE).setHidden(true);
		getColumn(CountryIndexDto.EPIPULSE_LOCATION_CODE).setHidden(true);

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

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getCountryFacade()::getIndexList, FacadeProvider.getCountryFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getCountryFacade()::getIndexList);
	}

}
