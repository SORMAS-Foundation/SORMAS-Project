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

package de.symeda.sormas.ui.sormastosormas;

import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestIndexDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class ShareRequestGrid extends FilteredGrid<SormasToSormasShareRequestIndexDto, ShareRequestCriteria> {

	private static final long serialVersionUID = -7556621082342162960L;

	private static final String SHOW_MESSAGE = "showRequest";
	private static final String COLUMN_ACTIONS = "actions";

	public ShareRequestGrid(boolean isInEagerMode, ShareRequestCriteria criteria) {
		super(SormasToSormasShareRequestIndexDto.class);

		initGridColumns();

		if (isInEagerMode) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}
	}

	private void initGridColumns() {
		addShowColumn((request) -> {
			ControllerProvider.getSormasToSormasController().showRequestDetails(request);
		});
		addComponentColumn(indexDto -> createActionButtons(indexDto)).setId(COLUMN_ACTIONS);

		setColumns(
			SHOW_MESSAGE,
			SormasToSormasShareRequestIndexDto.UUID,
			SormasToSormasShareRequestIndexDto.CREATION_DATE,
			SormasToSormasShareRequestIndexDto.DATA_TYPE,
			SormasToSormasShareRequestIndexDto.ORGANIZATION_NAME,
			SormasToSormasShareRequestIndexDto.SENDER_NAME,
			SormasToSormasShareRequestIndexDto.OWNERSHIP_HANDED_OVER,
			SormasToSormasShareRequestIndexDto.STATUS,
			SormasToSormasShareRequestIndexDto.COMMENT,
			COLUMN_ACTIONS);

		getColumn(COLUMN_ACTIONS).setMinimumWidth(260);
		((Column<SormasToSormasShareRequestIndexDto, String>) getColumn(LabMessageIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<SormasToSormasShareRequestIndexDto, Date>) getColumn(SormasToSormasShareRequestIndexDto.CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		getColumn(SormasToSormasShareRequestIndexDto.ORGANIZATION_NAME).setSortable(false);
		getColumn(SormasToSormasShareRequestIndexDto.OWNERSHIP_HANDED_OVER).setRenderer(new BooleanRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(SormasToSormasShareRequestIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		setSortOrder(
			Collections.singletonList(new GridSortOrder<>(getColumn(SormasToSormasShareRequestIndexDto.CREATION_DATE), SortDirection.DESCENDING)));
	}

	private Component createActionButtons(SormasToSormasShareRequestIndexDto indexDto) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setMargin(false);
		layout.setSpacing(true);

		if (indexDto.getStatus() == ShareRequestStatus.PENDING) {
			layout.addComponent(ButtonHelper.createButton(Captions.actionAccept, (e) -> {
				ControllerProvider.getSormasToSormasController().acceptShareRequest(indexDto, this::reload);
			}, ValoTheme.BUTTON_SMALL));
			layout.addComponent(ButtonHelper.createButton(Captions.actionReject, (e) -> {
				ControllerProvider.getSormasToSormasController().rejectShareRequest(indexDto, this::reload);
			}, ValoTheme.BUTTON_SMALL));
		}

		return layout;
	}

	protected void addShowColumn(Consumer<SormasToSormasShareRequestIndexDto> handler) {

		Column<SormasToSormasShareRequestIndexDto, String> showColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		showColumn.setId(SHOW_MESSAGE);
		showColumn.setSortable(false);
		showColumn.setWidth(20);

		addItemClickListener(new ShowDetailsListener<>(SHOW_MESSAGE, handler::accept));
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		DataProvider<SormasToSormasShareRequestIndexDto, ShareRequestCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getSormasToSormasShareRequestFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getSormasToSormasShareRequestFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<SormasToSormasShareRequestIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getSormasToSormasShareRequestFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
