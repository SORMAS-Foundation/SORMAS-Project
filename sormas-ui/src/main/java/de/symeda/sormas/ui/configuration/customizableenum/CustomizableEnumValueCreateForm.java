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

package de.symeda.sormas.ui.configuration.customizableenum;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.customizableenum.CustomizableEnumHelper;
import de.symeda.sormas.api.customizableenum.CustomizableEnumValueDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.AbstractEditForm;

public class CustomizableEnumValueCreateForm extends AbstractEditForm<CustomizableEnumValueDto> {

	private static final String HTML_LAYOUT = fluidRowLocs(CustomizableEnumValueDto.DATA_TYPE)
		+ fluidRowLocs(CustomizableEnumValueDto.VALUE, CustomizableEnumValueDto.CAPTION)
		+ fluidRowLocs(CustomizableEnumValueDto.DESCRIPTION);

	public CustomizableEnumValueCreateForm() {

		super(
			CustomizableEnumValueDto.class,
			CustomizableEnumValueDto.I18N_PREFIX,
			true,
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getNoop());

		setWidth(540, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {

		addField(CustomizableEnumValueDto.DATA_TYPE, ComboBox.class);
		TextField tfValue = addField(CustomizableEnumValueDto.VALUE, TextField.class);
		addField(CustomizableEnumValueDto.CAPTION, TextField.class);
		addField(CustomizableEnumValueDto.DESCRIPTION, TextField.class);

		setRequired(true, CustomizableEnumValueDto.DATA_TYPE, CustomizableEnumValueDto.CAPTION, CustomizableEnumValueDto.VALUE);

		tfValue.addValidator(value -> {
			if (!CustomizableEnumHelper.isValidEnumValue((String) value)) {
				throw new Validator.InvalidValueException(I18nProperties.getValidationError(Validations.customizableEnumValueAllowedCharacters));
			}
		});
	}

	@Override
	public void setValue(CustomizableEnumValueDto newFieldValue) {
		super.setValue(newFieldValue);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
