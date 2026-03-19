/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.utils.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.HasValue;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldVisibilityRestrictions;
import de.symeda.sormas.ui.utils.components.customizablefield.CustomizableFieldInput;
import de.symeda.sormas.ui.utils.components.customizablefield.CustomizableFieldInputFactory;

/**
 * Container component for managing multiple customizable field value fields belonging to a specific UI group.
 * 
 * Each group is bound to one {@code uiGroup} value (from {@link CustomizableFieldMetadataDto#UI_GROUP}).
 * When rendering, only fields whose metadata {@code uiGroup} matches this group's value are displayed.
 * 
 * Usage:
 * 
 * <pre>
 * CustomizableFieldsGroup group = new CustomizableFieldsGroup("myGroup");
 * group.setFieldsMetadata(fieldsList);   // full list – group filters by its own uiGroup
 * group.setFieldsValues(valuesMap);
 * group.updateFieldsDisplay();
 * form.addComponent(group, "myGroup");   // use the uiGroup as the layout location ID
 * </pre>
 */
public class CustomizableFieldsGroup extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private static final String STYLE_NAME_CUSTOMIZABLE_FIELDS_GROUP = "customizable-fields-group";

	private final String uiGroup;
	private final Map<String, CustomizableFieldInput<?>> fieldComponents;
	private final List<HasValue.ValueChangeListener<Object>> valueChangeListeners = new ArrayList<>();
	private List<CustomizableFieldMetadataDto> fieldsMetadata;
	private Map<String, CustomizableFieldValueDto> fieldsValues;
	private CustomizableFieldVisibilityContext visibilityContext;

	/**
	 * Creates a group scoped to the given UI group.
	 * Only fields whose metadata {@code uiGroup} equals this value will be rendered.
	 *
	 * @param uiGroup
	 *            the UI group identifier, must not be null
	 */
	public CustomizableFieldsGroup(String uiGroup) {
		if (uiGroup == null) {
			throw new IllegalArgumentException("uiGroup must not be null");
		}
		this.uiGroup = uiGroup;
		this.fieldComponents = new HashMap<>();
		this.setSpacing(true);
		this.setMargin(false);
		this.setWidth(100, Unit.PERCENTAGE);
		this.addStyleName(STYLE_NAME_CUSTOMIZABLE_FIELDS_GROUP);
		this.addStyleName(uiGroup);

		this.setId(uiGroup);
	}

	/**
	 * Returns the UI group this group is scoped to.
	 *
	 * @return the UI group identifier
	 */
	public String getUiGroup() {
		return uiGroup;
	}

	/**
	 * Registers a listener that is called whenever any field in this group changes its value.
	 * The listener is also applied retroactively to any fields already rendered.
	 *
	 * @param listener
	 *            the listener to register
	 */
	@SuppressWarnings("unchecked")
	public void addValueChangeListener(HasValue.ValueChangeListener<?> listener) {
		HasValue.ValueChangeListener<Object> typed = (HasValue.ValueChangeListener<Object>) listener;
		valueChangeListeners.add(typed);
		for (CustomizableFieldInput<?> field : fieldComponents.values()) {
			((CustomizableFieldInput<Object>) field).addValueChangeListener(typed);
		}
	}

	/**
	 * Sets the runtime visibility context used to evaluate
	 * {@link CustomizableFieldVisibilityRestrictions} during {@link #updateFieldsDisplay()}.
	 * Fields whose restrictions do not match the context are omitted from the rendered output.
	 * When {@code null}, all restriction checks are skipped (fields are shown regardless).
	 *
	 * @param visibilityContext
	 *            the current runtime values (e.g. the active disease)
	 */
	public void setVisibilityContext(CustomizableFieldVisibilityContext visibilityContext) {
		this.visibilityContext = visibilityContext;
	}

	/**
	 * Sets the metadata for customizable fields that can be displayed in this panel.
	 * After setting metadata, you can call {@link #updateFieldsDisplay()} to render the fields.
	 *
	 * @param metadata
	 *            the list of field metadata DTOs
	 */
	public void setFieldsMetadata(List<CustomizableFieldMetadataDto> metadata) {
		this.fieldsMetadata = metadata;
	}

	/**
	 * Sets the values for customizable fields to be displayed.
	 * Key should be the field metadata UUID.
	 *
	 * @param values
	 *            map of field values keyed by metadata UUID
	 */
	public void setFieldsValues(Map<String, CustomizableFieldValueDto> values) {
		this.fieldsValues = values;
	}

	/**
	 * Updates the display of all fields based on current metadata and values.
	 * <p>
	 * Active fields whose {@code uiGroup} matches this group are:
	 * <ol>
	 * <li>sorted by {@link CustomizableFieldMetadataDto#getUiLinePosition()} ascending
	 * (fields with {@code null} position are placed last, each on its own row);</li>
	 * <li>grouped by {@code uiLinePosition} – fields sharing the same (non-null) position
	 * are placed side-by-side in a {@link HorizontalLayout};</li>
	 * <li>sized within their row proportionally to
	 * {@link CustomizableFieldMetadataDto#getUiLineWeight()} (Vaadin expand ratio;
	 * defaults to {@code 1.0} when the weight is {@code null}).</li>
	 * </ol>
	 */
	public void updateFieldsDisplay() {
		removeAllComponents();
		fieldComponents.clear();

		if (fieldsMetadata == null || fieldsMetadata.isEmpty()) {
			return;
		}

		// Collect active fields for this group that match the current visibility context
		List<CustomizableFieldMetadataDto> groupFields = new ArrayList<>();
		for (CustomizableFieldMetadataDto metadata : fieldsMetadata) {
			boolean visibilityContextMatch = visibilityContext == null
				|| metadata.getVisibilityRestrictions() == null
				|| metadata.getVisibilityRestrictions().matches(visibilityContext);
			if (metadata.isActive() && uiGroup.equals(metadata.getUiGroup()) && visibilityContextMatch) {
				groupFields.add(metadata);
			}
		}

		if (groupFields.isEmpty()) {
			return;
		}

		// Sort: non-null positions first (ascending), null positions last (stable)
		groupFields.sort(Comparator.comparing(CustomizableFieldMetadataDto::getUiLinePosition, Comparator.nullsLast(Comparator.naturalOrder())));

		// Group by uiLinePosition; null-positioned fields each get their own synthetic key
		// using a LinkedHashMap to preserve insertion (sorted) order.
		LinkedHashMap<Object, List<CustomizableFieldMetadataDto>> lines = new LinkedHashMap<>();
		int nullKeyCounter = 0;
		for (CustomizableFieldMetadataDto metadata : groupFields) {
			Object lineKey = metadata.getUiLinePosition() != null ? metadata.getUiLinePosition() : "__null_" + nullKeyCounter++;
			lines.computeIfAbsent(lineKey, k -> new ArrayList<>()).add(metadata);
		}

		// Render each line as a HorizontalLayout row
		for (List<CustomizableFieldMetadataDto> lineFields : lines.values()) {
			addLineRow(lineFields);
		}
	}

	/**
	 * Renders a single horizontal row for the given list of fields.
	 * Each field's width within the row is controlled by its {@code uiLineWeight}
	 * (used as a Vaadin expand ratio; defaults to {@code 1.0} when absent).
	 *
	 * @param lineFields
	 *            fields to place side-by-side on this row
	 */
	private void addLineRow(List<CustomizableFieldMetadataDto> lineFields) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		row.setMargin(false);

		for (CustomizableFieldMetadataDto metadata : lineFields) {
			CustomizableFieldInput<?> field = CustomizableFieldInputFactory.create(metadata);

			CustomizableFieldValueDto dto = (fieldsValues != null && fieldsValues.containsKey(metadata.getUuid()))
				? fieldsValues.get(metadata.getUuid())
				: new CustomizableFieldValueDto();
			field.setFieldValue(dto);

			field.setWidth(100, Unit.PERCENTAGE);
			row.addComponent(field);

			float expandRatio = metadata.getUiLineWeight() != null ? metadata.getUiLineWeight() : 1.0f;
			row.setExpandRatio(field, expandRatio);

			for (@SuppressWarnings("unchecked")
			HasValue.ValueChangeListener<Object> listener : valueChangeListeners) {
				((CustomizableFieldInput<Object>) field).addValueChangeListener(listener);
			}

			fieldComponents.put(metadata.getUuid(), field);
		}

		addComponent(row);
	}

	/**
	 * Gets a specific field component by metadata UUID.
	 *
	 * @param metadataUuid
	 *            the UUID of the field metadata
	 * @return the field component, or null if not found
	 */
	public CustomizableFieldInput<?> getFieldByMetadataUuid(String metadataUuid) {
		return fieldComponents.get(metadataUuid);
	}

	/**
	 * Gets all field value components.
	 *
	 * @return map of field components keyed by metadata UUID
	 */
	public Map<String, CustomizableFieldInput<?>> getAllFields() {
		return new HashMap<>(fieldComponents);
	}

	/**
	 * Gets all current field values as a map.
	 * Key is the metadata UUID, value is the CustomizableFieldValueDto.
	 *
	 * @return map of all current field values
	 */
	public Map<String, CustomizableFieldValueDto> getFieldsValues() {
		Map<String, CustomizableFieldValueDto> result = new HashMap<>();
		for (Map.Entry<String, CustomizableFieldInput<?>> entry : fieldComponents.entrySet()) {
			CustomizableFieldValueDto value = entry.getValue().getFieldValue();
			if (value != null && value.getValue() != null) {
				result.put(entry.getKey(), value);
			}
		}
		return result;
	}

	/**
	 * Clears all field values.
	 */
	public void clearAllValues() {
		for (CustomizableFieldInput<?> field : fieldComponents.values()) {
			field.clear();
		}
	}

	/**
	 * Checks if any of the customizable fields have values.
	 *
	 * @return true if at least one field has a non-empty value
	 */
	public boolean hasAnyValues() {
		return fieldComponents.values().stream().anyMatch(field -> !field.isEmpty());
	}
}
