/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextArea;

import de.symeda.sormas.api.FormType;
import de.symeda.sormas.api.infrastructure.fields.FormFieldsDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;

public class FormFieldsEditForm extends AbstractEditForm<FormFieldsDto> {

	private static final long serialVersionUID = 1L;

	private static final String HTML_LAYOUT = fluidRowLocs(FormFieldsDto.FORM_TYPE, FormFieldsDto.FIELD_NAME)
		+ fluidRowLocs(FormFieldsDto.DESCRIPTION);

	private boolean create;

	public FormFieldsEditForm(boolean create) {
		super(FormFieldsDto.class, FormFieldsDto.I18N_PREFIX, false);
		this.create = create;

		setWidth(540, Unit.PIXELS);

		if (create) {
			hideValidationUntilNextCommit();
		}
		addFields();
	}

	@Override
	protected void addFields() {
		ComboBox formType = addField(FormFieldsDto.FORM_TYPE, ComboBox.class);
		formType.addItems(FormType.values());
		formType.setNullSelectionAllowed(false);

		addField(FormFieldsDto.FIELD_NAME, TextArea.class);
		addField(FormFieldsDto.DESCRIPTION, TextArea.class);

		setRequired(true, FormFieldsDto.FORM_TYPE, FormFieldsDto.FIELD_NAME);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}

