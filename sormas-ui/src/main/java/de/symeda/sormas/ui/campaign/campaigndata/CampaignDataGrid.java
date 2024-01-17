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

package de.symeda.sormas.ui.campaign.campaigndata;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class CampaignDataGrid extends FilteredGrid<CampaignFormDataIndexDto, CampaignFormDataCriteria> {

	private static final long serialVersionUID = 8045806100043073638L;

	public CampaignDataGrid(CampaignFormDataCriteria criteria) {

		super(CampaignFormDataIndexDto.class);
		setSizeFull();

		setLazyDataProvider(FacadeProvider.getCampaignFormDataFacade()::getIndexList, FacadeProvider.getCampaignFormDataFacade()::count);
		setCriteria(criteria);
		addDefaultColumns();
	}

	protected void addDefaultColumns() {
		addEditColumn(e -> {
			ControllerProvider.getCampaignController().navigateToFormDataView(e.getUuid());
		});

		setColumns(
			ACTION_BTN_ID,
			CampaignFormDataIndexDto.CAMPAIGN,
			CampaignFormDataIndexDto.FORM,
			CampaignFormDataIndexDto.REGION,
			CampaignFormDataIndexDto.DISTRICT,
			CampaignFormDataIndexDto.COMMUNITY,
			CampaignFormDataIndexDto.FORM_DATE);
		getColumn(ACTION_BTN_ID).setWidth(40).setStyleGenerator(item -> CssStyles.GRID_CELL_LINK);

		((Column<CampaignFormDataIndexDto, Date>) getColumn(CampaignFormDataIndexDto.FORM_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(I18nProperties.getUserLanguage())));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CampaignFormDataIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(CampaignFormDataIndexDto.REGION).setHidden(true);
			getColumn(CampaignFormDataIndexDto.DISTRICT).setHidden(true);
			getColumn(CampaignFormDataIndexDto.COMMUNITY).setHidden(true);
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	public void addCustomColumn(String property, String caption) {
		Column<CampaignFormDataIndexDto, Object> newColumn =
			addColumn(e -> e.getFormValues().stream().filter(v -> v.getId().equals(property)).findFirst().orElse(null));
		newColumn.setSortable(false);
		newColumn.setCaption(caption);
		newColumn.setId(property);
	}

}
