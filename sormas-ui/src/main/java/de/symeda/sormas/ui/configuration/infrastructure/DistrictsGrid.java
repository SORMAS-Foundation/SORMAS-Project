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

import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class DistrictsGrid extends FilteredGrid<DistrictIndexDto, DistrictCriteria> {

	private static final long serialVersionUID = -4437531618828715458L;

	public DistrictsGrid(DistrictCriteria criteria) {

		super(DistrictIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(DistrictsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		String[] columns = new String[] {
			DistrictIndexDto.NAME,
			DistrictIndexDto.REGION,
			DistrictIndexDto.EPID_CODE,
			DistrictIndexDto.EXTERNAL_ID,
			DistrictIndexDto.POPULATION,
			DistrictIndexDto.GROWTH_RATE };
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			columns = ArrayUtils.add(columns, DistrictIndexDto.DEFAULT_INFRASTRUCTURE);
		}
		setColumns(columns);

		getColumn(DistrictIndexDto.POPULATION).setSortable(false);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)
			&& UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editDistrict(e.getUuid()));
		}

		Optional.ofNullable(((Column<DistrictIndexDto, Boolean>) getColumn(DistrictIndexDto.DEFAULT_INFRASTRUCTURE)))
			.ifPresent(c -> c.setRenderer(new BooleanRenderer()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(DistrictIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	public void reload() {
		if (ViewModelProviders.of(DistrictsView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}
		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		setLazyDataProvider(FacadeProvider.getDistrictFacade()::getIndexList, FacadeProvider.getDistrictFacade()::count);
	}

	public void setEagerDataProvider() {
		setEagerDataProvider(FacadeProvider.getDistrictFacade()::getIndexList);
	}
}
