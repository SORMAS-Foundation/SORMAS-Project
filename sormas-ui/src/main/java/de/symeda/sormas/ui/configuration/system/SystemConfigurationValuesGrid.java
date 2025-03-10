/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.system;

import java.util.Random;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueCriteria;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueIndexDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;

/**
 * Grid component for displaying and managing system configuration values.
 */
public class SystemConfigurationValuesGrid extends FilteredGrid<SystemConfigurationValueIndexDto, SystemConfigurationValueCriteria> {

	private static final long serialVersionUID = 1L;

	private static final Random RANDOM = new Random();

	/**
	 * Constructs a new SystemConfigurationValuesGrid with the specified criteria.
	 *
	 * @param criteria
	 *            the criteria for filtering system configuration values
	 */
	public SystemConfigurationValuesGrid(final SystemConfigurationValueCriteria criteria) {

		super(SystemConfigurationValueIndexDto.class);

		initGrid(criteria);
	}

	protected void initGrid(final SystemConfigurationValueCriteria criteria) {
		setSizeFull();

		setLazyDataProvider(
			FacadeProvider.getSystemConfigurationValueFacade()::getIndexList,
			FacadeProvider.getSystemConfigurationValueFacade()::count);
		setCriteria(criteria);

		removeColumn(SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME);

		addColumn(value -> value.isEncrypted() ? "*".repeat(RANDOM.nextInt(50)) : value.getValue())
			.setId(SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME);

		removeColumn(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME);
		addColumn(value -> {
			final StringBuilder caption = new StringBuilder();
			SystemConfigurationI18nHelper.processI18nString(
				value.getCategoryCaption(),
				(defaultName, key) -> caption
					.append(I18nProperties.getPrefixCaption(SystemConfigurationValueIndexDto.I18N_PREFIX, key, defaultName)));
			if (caption.length() == 0) {
				caption.append(value.getCategoryCaption());
			}
			return caption.toString();
		}).setId(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME);

		setColumns(
			SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME,
			SystemConfigurationValueIndexDto.KEY_PROPERTY_NAME,
			SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME);

		addEditColumn(e -> ControllerProvider.getSystemConfigurationController().editSystemConfigurationValue(e.getUuid()));

		for (final Column<?, ?> column : getColumns()) {
			if (column.getId().equals(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME)) {
				continue;
			}
			column.setCaption(I18nProperties.getPrefixCaption(SystemConfigurationValueIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
	}

	/**
	 * Reloads the grid data.
	 */
	public void reload() {
		getDataProvider().refreshAll();
	}

}
