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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteria;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CommunitiesGrid extends FilteredGrid<CommunityDto, CommunityCriteria> {

	private static final long serialVersionUID = 3355810665696318673L;

	public CommunitiesGrid(CommunityCriteria criteria) {

		super(CommunityDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CommunitiesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(CommunityDto.NAME, CommunityDto.REGION, CommunityDto.DISTRICT, CommunityDto.EXTERNAL_ID);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)
			&& UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editCommunity(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(CommunityDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		if (ViewModelProviders.of(CommunitiesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getCommunityFacade()::getIndexList, FacadeProvider.getCommunityFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getCommunityFacade()::getIndexList);
	}
}
