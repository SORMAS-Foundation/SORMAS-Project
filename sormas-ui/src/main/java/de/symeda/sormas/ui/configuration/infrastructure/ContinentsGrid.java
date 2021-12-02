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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.symeda.sormas.api.feature.FeatureType;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class ContinentsGrid extends FilteredGrid<ContinentIndexDto, ContinentCriteria> {

	List<ContinentIndexDto> allContinents;

	public ContinentsGrid(ContinentCriteria criteria) {
		super(ContinentIndexDto.class);

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(ContinentsView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		super.setCriteria(criteria, true);
		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		setColumns(ContinentIndexDto.DISPLAY_NAME, ContinentIndexDto.EXTERNAL_ID, ContinentIndexDto.DEFAULT_NAME);
		getColumn(ContinentIndexDto.DEFAULT_NAME).setHidden(true);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)
			&& UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EDIT)) {
			addEditColumn(e -> ControllerProvider.getInfrastructureController().editContinent(e.getUuid()));
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(ContinentIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}

		reload(true);
	}

	public void reload(boolean forceFetch) {
		if (forceFetch || allContinents == null) {
			allContinents = FacadeProvider.getContinentFacade().getIndexList(null, null, null, null);
		}
		reload();
	}

	public void reload() {
		this.setItems(createFilteredStream());
		setSelectionMode(isInEagerMode() ? SelectionMode.MULTI : SelectionMode.NONE);
	}

	private Stream<ContinentIndexDto> createFilteredStream() {

		// get all filter properties
		String nameLike = getCriteria().getNameLike() != null ? getCriteria().getNameLike().toLowerCase() : null;
		EntityRelevanceStatus relevanceStatus = getCriteria().getRelevanceStatus();

		Predicate<ContinentIndexDto> filters = x -> true; // "empty" basefilter

		// name filter
		if (!StringUtils.isEmpty(nameLike)) {
			filters = filters.and(
				continent -> (continent.getDefaultName().toLowerCase().contains(nameLike)
					|| continent.getDisplayName().toLowerCase().contains(nameLike)));
		}
		// relevancestatus filter (active/archived/all)
		if (relevanceStatus != null) {
			switch (relevanceStatus) {
			case ACTIVE:
				filters = filters.and(continent -> (!continent.isArchived()));
				break;
			case ARCHIVED:
				filters = filters.and(continent -> (continent.isArchived()));
				break;
			}
		}

		// apply filters
		return allContinents.stream().filter(filters);

	}
}
