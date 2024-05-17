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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class FacilitiesGrid extends FilteredGrid<FacilityIndexDto, FacilityCriteria> {

	private static final long serialVersionUID = 4488941182432777837L;

	public FacilitiesGrid(FacilityCriteria criteria) {

		super(FacilityIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(FacilitiesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		setColumns(
			FacilityIndexDto.NAME,
			FacilityIndexDto.TYPE,
			FacilityIndexDto.REGION,
			FacilityIndexDto.DISTRICT,
			FacilityIndexDto.COMMUNITY,
			FacilityIndexDto.POSTAL_CODE,
			FacilityIndexDto.CITY,
			FacilityIndexDto.STREET,
			FacilityIndexDto.HOUSE_NUMBER,
			FacilityIndexDto.ADDITIONAL_INFORMATION,
			FacilityIndexDto.LATITUDE,
			FacilityIndexDto.LONGITUDE,
			FacilityIndexDto.EXTERNAL_ID);

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editFacility(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(FacilityIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		if (ViewModelProviders.of(FacilitiesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getFacilityFacade()::getIndexList, FacadeProvider.getFacilityFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getFacilityFacade()::getIndexList);
	}
}
