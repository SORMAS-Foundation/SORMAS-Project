/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.groups;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.shared.ui.grid.HeightMode;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridHeightResizer;

@SuppressWarnings("serial")
public class EventGroupSelectionGrid extends FilteredGrid<EventGroupIndexDto, EventGroupCriteria> {

	public EventGroupSelectionGrid(EventGroupCriteria criteria) {

		super(EventGroupIndexDto.class);

		setLazyDataProvider(FacadeProvider.getEventGroupFacade()::getIndexList, FacadeProvider.getEventGroupFacade()::count);
		addDataSizeChangeListener(new GridHeightResizer());

		setCriteria(criteria);
		buildGrid();
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		setColumns(EventGroupIndexDto.UUID, EventGroupIndexDto.NAME, EventGroupIndexDto.EVENT_COUNT);

		for (Column<EventGroupIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(EventGroupIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
		getColumn(EventGroupIndexDto.EVENT_COUNT).setSortable(false);

		getColumn(EventGroupIndexDto.NAME).setMaximumWidth(300);
	}

	@Override
	public void setCriteria(EventGroupCriteria criteria) {
		getFilteredDataProvider().setFilter(criteria);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ConfigurableFilterDataProvider<EventGroupIndexDto, Void, EventGroupCriteria> getFilteredDataProvider() {
		return (ConfigurableFilterDataProvider<EventGroupIndexDto, Void, EventGroupCriteria>) super.getDataProvider();
	}
}
