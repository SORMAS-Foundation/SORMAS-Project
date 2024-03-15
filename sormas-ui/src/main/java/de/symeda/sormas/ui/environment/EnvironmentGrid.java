/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class EnvironmentGrid extends FilteredGrid<EnvironmentIndexDto, EnvironmentCriteria> {

	private static final long serialVersionUID = -4035837468160064406L;

	public EnvironmentGrid(EnvironmentCriteria criteria, ViewConfiguration viewConfiguration) {
		super(EnvironmentIndexDto.class);
		setSizeFull();

		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider(FacadeProvider.getEnvironmentFacade()::getIndexList);
		} else {
			setLazyDataProvider(FacadeProvider.getEnvironmentFacade()::getIndexList, FacadeProvider.getEnvironmentFacade()::count);
			setCriteria(criteria);
		}

		initColumns();

		addItemClickListener(
			new ShowDetailsListener<>(
				EnvironmentIndexDto.UUID,
				e -> ControllerProvider.getEnvironmentController().navigateToEnvironment(e.getUuid())));
	}

	protected void initColumns() {
		Column<EnvironmentIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));
		deleteColumn.setHidden(true);

		setColumns(getGridColumns().toArray(String[]::new));

		((Column<EnvironmentIndexDto, String>) getColumn(EnvironmentIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<EnvironmentIndexDto, Date>) getColumn(EnvironmentIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		if (!UiUtil.permitted(UserRight.ENVIRONMENT_DELETE)) {
			removeColumn(DELETE_REASON_COLUMN);
		}

		for (Column<EnvironmentIndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties
					.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), EnvironmentIndexDto.I18N_PREFIX, LocationDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(EnvironmentIndexDto.class, column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(EnvironmentIndexDto.REGION).setHidden(true);
			getColumn(EnvironmentIndexDto.DISTRICT).setHidden(true);
			getColumn(EnvironmentIndexDto.COMMUNITY).setHidden(true);
		}
	}

	protected Stream<String> getGridColumns() {
		return Stream
			.of(
				Stream.of(
					EnvironmentIndexDto.UUID,
					EnvironmentIndexDto.EXTERNAL_ID,
					EnvironmentIndexDto.ENVIRONMENT_NAME,
					EnvironmentIndexDto.ENVIRONMENT_MEDIA,
					EnvironmentIndexDto.REGION,
					EnvironmentIndexDto.DISTRICT,
					EnvironmentIndexDto.COMMUNITY,
					EnvironmentIndexDto.LATITUDE,
					EnvironmentIndexDto.LONGITUDE,
					EnvironmentIndexDto.POSTAL_CODE,
					EnvironmentIndexDto.CITY,
					EnvironmentIndexDto.REPORT_DATE,
					EnvironmentIndexDto.INVESTIGATION_STATUS),
				Stream.of(DELETE_REASON_COLUMN))
			.flatMap(Function.identity());
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
