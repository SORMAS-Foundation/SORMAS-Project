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

import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import com.vaadin.data.Binder;
import com.vaadin.v7.ui.PasswordField;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

/**
 * Form for editing system configuration values.
 */
public class SystemConfigurationValueEditForm extends AbstractEditForm<SystemConfigurationValueDto> {

	private static final String HTML_LAYOUT =
		locs(SystemConfigurationValueDtoWrapper.WRAPPED_OBJECT_PROPERTY_NAME, SystemConfigurationValueDto.VALIDATION_MESSAGE_PROPERTY_NAME);

	private final SystemConfigurationValueDynamicInput dynamicInput;

	/**
	 * Constructor for creating a new SystemConfigurationValueEditForm.
	 *
	 * @param value
	 *            the system configuration value to be edited
	 */
	public SystemConfigurationValueEditForm(final SystemConfigurationValueDto value) {

		super(
			SystemConfigurationValueDto.class,
			SystemConfigurationValueDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getNoop());

		setWidth(640, Unit.PIXELS);
		setValue(value);

		final Binder<SystemConfigurationValueDtoWrapper> binder = new Binder<>(SystemConfigurationValueDtoWrapper.class);
		binder.setBean(new SystemConfigurationValueDtoWrapper(value));

		dynamicInput = new SystemConfigurationValueDynamicInput();
		binder.forField(dynamicInput).bind(SystemConfigurationValueDtoWrapper.WRAPPED_OBJECT_PROPERTY_NAME);
		getContent().addComponent(dynamicInput, SystemConfigurationValueDtoWrapper.WRAPPED_OBJECT_PROPERTY_NAME);

	}

	/**
	 * Adds the fields to the form based on the system configuration value.
	 */
	@Override
	protected void addFields() {

		if (getValue().getEncrypt().booleanValue()) {
			addField(SystemConfigurationValueDto.VALUE_PROPERTY_NAME, PasswordField.class);
		} else {
			addFields(SystemConfigurationValueDto.VALUE_PROPERTY_NAME);
		}

		setEnabled(true, SystemConfigurationValueDto.VALUE_PROPERTY_NAME);
	}

	/**
	 * Creates the HTML layout for the form.
	 *
	 * @return the HTML layout as a string
	 */
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
