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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.infrastructure.fields.FormFieldReferenceDto;
import de.symeda.sormas.api.infrastructure.forms.FormBuilderDto;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Component that dynamically renders form fields based on FormBuilder configuration.
 * Supports field visibility dependencies, data binding, and validation.
 */
public class FormBuilderComponent extends GridLayout {

	private static final long serialVersionUID = 1L;

	private final FormBuilderDto formBuilder;
	private final FormBuilderFieldFactory fieldFactory;
	private final Map<String, Field<?>> fields = new HashMap<>();
	private final Map<String, Object> fieldValues = new HashMap<>();
	private int currentCol = -1;

	public FormBuilderComponent(FormBuilderDto formBuilder) {
		this.formBuilder = formBuilder;
		this.fieldFactory = new FormBuilderFieldFactory();

		setColumns(12);
		setWidth(100, Unit.PERCENTAGE);
		setHeightUndefined();
		setMargin(new MarginInfo(true, true));

		buildForm();
	}

	/**
	 * Builds the form by iterating through form fields and creating components.
	 */
	private void buildForm() {
		if (formBuilder == null || formBuilder.getFormFields() == null) {
			return;
		}

		// Sort fields by display order
		List<FormFieldReferenceDto> sortedFields = FormBuilderUtils.sortFieldsByDisplayOrder(formBuilder.getFormFields());

		for (FormFieldReferenceDto fieldRef : sortedFields) {
			Component component = fieldFactory.createField(fieldRef);

			if (component instanceof Label) {
				// Handle labels and section dividers
				addLabelComponent((Label) component, fieldRef);
			} else if (component instanceof Field) {
				// Handle input fields
				addFieldComponent((Field<?>) component, fieldRef);
			}
		}
	}

	/**
	 * Adds a label component to the layout.
	 */
	private void addLabelComponent(Label label, FormFieldReferenceDto fieldRef) {
		// Labels take full width
		if (currentCol >= 0) {
			insertRow(getRows());
			currentCol = -1;
		}

		addComponent(label, 0, getRows() - 1, 11, getRows() - 1);
		insertRow(getRows());
		currentCol = -1;
	}

	/**
	 * Adds a field component to the layout.
	 */
	private void addFieldComponent(Field<?> field, FormFieldReferenceDto fieldRef) {
		// Default to 4 columns (col-4) if not specified
		int columnSpan = 4;

		// Check if field should take full row
		if (currentCol + columnSpan > 11) {
			insertRow(getRows());
			currentCol = -1;
		}

		field.setSizeFull();
		field.setId(fieldRef.getUuid());

		// Add field to layout
		addComponent(field, currentCol + 1, getRows() - 1, currentCol + columnSpan, getRows() - 1);

		// Store field reference
		fields.put(fieldRef.getUuid(), field);
		if (fieldRef.getFieldName() != null) {
			fields.put(fieldRef.getFieldName(), field);
		}

		// Move to next column position
		currentCol += columnSpan;

		// Handle visibility dependencies if needed
		// Note: This is a simplified version - full implementation would need
		// dependingOn field information from FormField entity
	}

	/**
	 * Gets all field values as a map keyed by field UUID.
	 * 
	 * @return Map of field UUIDs to their values
	 */
	public Map<String, Object> getFieldValues() {
		Map<String, Object> values = new HashMap<>();
		for (Map.Entry<String, Field<?>> entry : fields.entrySet()) {
			Field<?> field = entry.getValue();
			if (field != null && field.getValue() != null) {
				values.put(entry.getKey(), field.getValue());
			}
		}
		return values;
	}

	/**
	 * Sets field values from a map.
	 * 
	 * @param values
	 *            Map of field UUIDs/names to their values
	 */
	@SuppressWarnings("unchecked")
	public void setFieldValues(Map<String, Object> values) {
		if (values == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : values.entrySet()) {
			Field<?> field = fields.get(entry.getKey());
			if (field != null) {
				((Field<Object>) field).setValue(entry.getValue());
			}
		}
	}

	/**
	 * Gets a specific field by its UUID or field name.
	 * 
	 * @param fieldId
	 *            Field UUID or field name
	 * @return Field component or null if not found
	 */
	public Field<?> getField(String fieldId) {
		return fields.get(fieldId);
	}

	/**
	 * Gets all fields as a map.
	 * 
	 * @return Map of field identifiers to Field components
	 */
	public Map<String, Field<?>> getFields() {
		return fields;
	}

	/**
	 * Sets the visibility of a field based on another field's value.
	 * 
	 * @param fieldId
	 *            ID of the field to show/hide
	 * @param dependingOnFieldId
	 *            ID of the field to depend on
	 * @param visibleValues
	 *            Values that make the field visible
	 */
	public void setVisibilityDependency(String fieldId, String dependingOnFieldId, Object... visibleValues) {
		Component component = fields.get(fieldId);
		Field<?> dependingOnField = fields.get(dependingOnFieldId);

		if (component == null || dependingOnField == null) {
			return;
		}

		// Set initial visibility
		updateFieldVisibility(component, dependingOnField, visibleValues);

		// Add value change listener
		dependingOnField.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateFieldVisibility(component, dependingOnField, visibleValues);
			}
		});
	}

	/**
	 * Updates field visibility based on depending field value.
	 */
	private void updateFieldVisibility(Component component, Field<?> dependingOnField, Object... visibleValues) {
		Object fieldValue = dependingOnField.getValue();
		boolean visible = false;

		if (fieldValue != null) {
			for (Object visibleValue : visibleValues) {
				if (fieldValue.equals(visibleValue)) {
					visible = true;
					break;
				}
			}
		}

		component.setVisible(visible);

		// Clear field value when hidden
		if (!visible && component instanceof Field) {
			((Field<?>) component).setValue(null);
		}
	}

	/**
	 * Sets all fields to read-only mode.
	 * 
	 * @param readOnly
	 *            true to make fields read-only, false to make them editable
	 */
	public void setReadOnly(boolean readOnly) {
		for (Field<?> field : fields.values()) {
			field.setReadOnly(readOnly);
		}
	}
}

