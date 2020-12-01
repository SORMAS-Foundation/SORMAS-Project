/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.campaign.campaigns;

import java.util.Date;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignCriteria;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CampaignGrid extends FilteredGrid<CampaignIndexDto, CampaignCriteria> {

	private static final long serialVersionUID = -7922340233873282326L;

	private static final String VIEW_FORMS_BTN_ID = "viewForms";

	@SuppressWarnings("unchecked")
	public CampaignGrid(CampaignCriteria criteria) {

		super(CampaignIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CampaignsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		addEditColumn(e -> ControllerProvider.getCampaignController().navigateToCampaign(e.getUuid()));

		Column<CampaignIndexDto, String> viewFormsColumn = addColumn(entry -> VaadinIcons.EYE.getHtml(), new HtmlRenderer());
		viewFormsColumn.setId(VIEW_FORMS_BTN_ID);
		viewFormsColumn.setSortable(false);
		viewFormsColumn.setWidth(25);

		setColumns(
			EDIT_BTN_ID,
			VIEW_FORMS_BTN_ID,
			CampaignIndexDto.NAME,
			CampaignIndexDto.START_DATE,
			CampaignIndexDto.END_DATE);
		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CampaignIndexDto, Date>) getColumn(CampaignIndexDto.START_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		((Column<CampaignIndexDto, Date>) getColumn(CampaignIndexDto.END_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CampaignIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
		getColumn(EDIT_BTN_ID).setWidth(40).setStyleGenerator(item -> CssStyles.GRID_CELL_LINK);
		getColumn(VIEW_FORMS_BTN_ID).setWidth(40).setStyleGenerator(item -> CssStyles.GRID_CELL_LINK);

		addItemClickListener(
			new ShowDetailsListener<>(VIEW_FORMS_BTN_ID, e -> ControllerProvider.getCampaignController().navigateToCampaignData(e.getUuid())));
	}

	public void setLazyDataProvider() {

		DataProvider<CampaignIndexDto, CampaignCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getCampaignFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getCampaignFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<CampaignIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getCampaignFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
