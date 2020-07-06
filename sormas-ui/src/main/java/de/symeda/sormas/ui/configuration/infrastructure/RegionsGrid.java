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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.region.RegionIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class RegionsGrid extends FilteredGrid<RegionIndexDto, RegionCriteria> {

	private static final long serialVersionUID = 6289713952342575369L;

	public RegionsGrid(RegionCriteria criteria) {

		super(RegionIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(RegionsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			RegionIndexDto.NAME,
			RegionIndexDto.AREA,
			RegionIndexDto.EPID_CODE,
			RegionIndexDto.EXTERNAL_ID,
			RegionIndexDto.POPULATION,
			RegionIndexDto.GROWTH_RATE);

		if (!FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			removeColumn(RegionIndexDto.AREA);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editRegion(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(RegionIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<RegionIndexDto, RegionCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getRegionFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> {
				return (int) FacadeProvider.getRegionFacade().count(query.getFilter().orElse(null));
			});
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<RegionIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getRegionFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
