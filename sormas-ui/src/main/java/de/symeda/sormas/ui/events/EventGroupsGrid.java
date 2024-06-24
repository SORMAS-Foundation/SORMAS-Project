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
package de.symeda.sormas.ui.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.navigator.View;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class EventGroupsGrid extends FilteredGrid<EventGroupIndexDto, EventGroupCriteria> {

	@SuppressWarnings("unchecked")
	public <V extends View> EventGroupsGrid(EventGroupCriteria criteria, Class<V> viewClass) {

		super(EventGroupIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		List<String> columnIds = new ArrayList<>(Arrays.asList(EventGroupIndexDto.UUID, EventGroupIndexDto.NAME, EventGroupIndexDto.EVENT_COUNT));

		setColumns(columnIds.toArray(new String[0]));

		getColumn(EventGroupIndexDto.EVENT_COUNT).setSortable(false);

		((Column<EventGroupIndexDto, String>) getColumn(EventGroupIndexDto.UUID)).setRenderer(new UuidRenderer());

		for (Column<EventGroupIndexDto, ?> column : getColumns()) {
			String columnId = column.getId();
			column.setCaption(I18nProperties.getPrefixCaption(EventGroupIndexDto.I18N_PREFIX, columnId, column.getCaption()));
		}

		addItemClickListener(
			new ShowDetailsListener<>(EventGroupIndexDto.UUID, e -> ControllerProvider.getEventGroupController().navigateToData(e.getUuid())));
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(EventsView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getEventGroupFacade()::getIndexList, FacadeProvider.getEventGroupFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(FacadeProvider.getEventGroupFacade()::getIndexList);
	}
}
