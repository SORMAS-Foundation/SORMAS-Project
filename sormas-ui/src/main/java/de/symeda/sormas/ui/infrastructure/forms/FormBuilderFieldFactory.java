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

package de.symeda.sormas.ui.infrastructure.forms;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.NumberValidator;
import de.symeda.sormas.ui.utils.SormasFieldGroupFieldFactory;

/**
 * Factory for creating Vaadin field components from FormField definitions.
 */
public final class FormBuilderFieldFactory {

	private final SormasFieldGroupFieldFactory fieldFactory;

	public FormBuilderFieldFactory() {
		this.fieldFactory = new SormasFieldGroupFieldFactory(null, null);
	}

	/**
	 * Creates a text field component for the given form field.
	 * 
	 * @param field
	 *            The form field definition
	 * @return TextField component
	 */
	public TextField createTextField(FormFieldReferenceDto field) {
		TextField textField = fieldFactory.createField(String.class, TextField.class);
		textField.setCaption(field.getCaption());
		textField.setId(field.getUuid());
		textField.setSizeFull();
		return textField;
	}

	/**
	 * Creates a number field component for the given form field.
	 * 
	 * @param field
	 *            The form field definition
	 * @return TextField component with number validation
	 */
	public TextField createNumberField(FormFieldReferenceDto field) {
		TextField numberField = createTextField(field);
		numberField.addValidator(new NumberValidator(
			de.symeda.sormas.api.i18n.I18nProperties.getValidationError(de.symeda.sormas.api.i18n.Validations.onlyNumbersAllowed, field.getCaption())));
		return numberField;
	}

	/**
	 * Creates a boolean (Yes/No) field component for the given form field.
	 * 
	 * @param field
	 *            The form field definition
	 * @return NullableOptionGroup component
	 */
	public NullableOptionGroup createBooleanField(FormFieldReferenceDto field) {
		NullableOptionGroup booleanField = fieldFactory.createField(Boolean.class, NullableOptionGroup.class);
		booleanField.setCaption(field.getCaption());
		booleanField.setId(field.getUuid());
		booleanField.setSizeFull();
		return booleanField;
	}

	/**
	 * Creates a label component for display-only text.
	 * 
	 * @param field
	 *            The form field definition
	 * @return Label component
	 */
	public Label createLabel(FormFieldReferenceDto field) {
		Label label = new Label();
		label.setValue(field.getCaption() != null ? field.getCaption() : "");
		label.setContentMode(ContentMode.HTML);
		label.setId(field.getUuid());
		label.addStyleName(CssStyles.LABEL_BOLD);
		return label;
	}

	/**
	 * Creates a section divider component.
	 * 
	 * @param field
	 *            The form field definition
	 * @return Label component styled as a divider
	 */
	public Label createSectionDivider(FormFieldReferenceDto field) {
		Label divider = new Label();
		divider.setValue(field.getCaption() != null ? field.getCaption() : "");
		divider.setContentMode(ContentMode.HTML);
		divider.setId(field.getUuid());
		divider.addStyleName(CssStyles.H3);
		divider.addStyleName(CssStyles.VSPACE_TOP_3);
		divider.addStyleName(CssStyles.VSPACE_3);
		return divider;
	}

	/**
	 * Creates an appropriate field component based on the field type.
	 * Uses heuristics from FormBuilderUtils to determine field type.
	 * 
	 * @param field
	 *            The form field definition
	 * @return Field component
	 */
	public Component createField(FormFieldReferenceDto field) {
		if (FormBuilderUtils.isLabelField(field)) {
			if (field.getFieldName() != null && field.getFieldName().toLowerCase().contains("section")) {
				return createSectionDivider(field);
			}
			return createLabel(field);
		} else if (FormBuilderUtils.isBooleanField(field)) {
			return createBooleanField(field);
		} else if (FormBuilderUtils.isNumberField(field)) {
			return createNumberField(field);
		} else {
			return createTextField(field);
		}
	}
}

