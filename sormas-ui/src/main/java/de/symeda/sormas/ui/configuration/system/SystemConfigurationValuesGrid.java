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

import com.vaadin.ui.Grid.Column;

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

	/**
	 * The number of mask characters to generate for encrypted values.
	 */
	private static final int ENCRYPTED_VALUE_LENGTH = 15;

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

		configureColumns();
	}

	private void configureColumns() {

		removeColumn(SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME);
		addColumn(value -> value.isEncrypted() ? "*".repeat(ENCRYPTED_VALUE_LENGTH) : value.getValue())
			.setId(SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME)
			.setDescriptionGenerator(v -> this.getValueDescription(v));

		removeColumn(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME);
		addColumn(this::getCategoryCaption).setId(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME)
			.setDescriptionGenerator(v -> this.getValueDescription(v));

		removeColumn(SystemConfigurationValueIndexDto.DESCRIPTION_PROPERTY_NAME);
		addColumn(this::getValueDescription).setId(SystemConfigurationValueIndexDto.DESCRIPTION_PROPERTY_NAME)
			.setDescriptionGenerator(v -> this.getValueDescription(v));

		setColumns(
			SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME,
			SystemConfigurationValueIndexDto.KEY_PROPERTY_NAME,
			SystemConfigurationValueIndexDto.DESCRIPTION_PROPERTY_NAME,
			SystemConfigurationValueIndexDto.VALUE_PROPERTY_NAME);

		addEditColumn(e -> ControllerProvider.getSystemConfigurationController().editSystemConfigurationValue(e.getUuid()));

		for (final Column<?, ?> column : getColumns()) {
			if (!column.getId().equals(SystemConfigurationValueIndexDto.CATEGORY_NAME_PROPERTY_NAME)) {
				column.setCaption(I18nProperties.getPrefixCaption(SystemConfigurationValueIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
			}
		}
	}

	private String getCategoryCaption(SystemConfigurationValueIndexDto value) {

		final StringBuilder caption = new StringBuilder();
		SystemConfigurationI18nHelper.processI18nString(
			value.getCategoryCaption(),
			(defaultName, key) -> caption.append(I18nProperties.getPrefixCaption(SystemConfigurationValueIndexDto.I18N_PREFIX, key, defaultName)));
		if (caption.length() == 0) {
			caption.append(value.getCategoryCaption());
		}
		return caption.toString();
	}

	private String getValueDescription(SystemConfigurationValueIndexDto value) {

		if (value.getDescription() == null || value.getDescription().isEmpty()) {
			return "";
		}

		final StringBuilder description = new StringBuilder();
		SystemConfigurationI18nHelper.processI18nString(value.getDescription(), (key) -> description.append(I18nProperties.getString(key)));
		if (description.length() == 0) {
			description.append(value.getDescription());
		}
		return description.toString();
	}

	/**
	 * Reloads the grid data.
	 */
	public void reload() {
		getDataProvider().refreshAll();
	}

}
