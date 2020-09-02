/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Diseases;

public final class FieldHelper {

	private FieldHelper() {
		// Hide Utility Class Constructor
	}

	public static void setReadOnlyWhen(
		FieldGroup fieldGroup,
		Object targetPropertyId,
		Object sourcePropertyId,
		List<Object> sourceValues,
		boolean clearOnReadOnly,
		boolean readOnlyWhenNull) {

		setReadOnlyWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnReadOnly, readOnlyWhenNull);
	}

	@SuppressWarnings("rawtypes")
	public static void setReadOnlyWhen(
		final FieldGroup fieldGroup,
		List<Object> targetPropertyIds,
		Object sourcePropertyId,
		final List<Object> sourceValues,
		final boolean clearOnReadOnly,
		boolean readOnlyWhenNull) {

		Field sourceField = fieldGroup.getField(sourcePropertyId);
		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean readOnly;
			if (sourceField.getValue() == null) {
				readOnly = readOnlyWhenNull;
			} else {
				readOnly = sourceValues.contains(sourceField.getValue());
			}
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if (readOnly && clearOnReadOnly && targetField.getValue() != null) {
					targetField.setReadOnly(false);
					targetField.clear();
				}
				targetField.setReadOnly(readOnly);
				if (readOnly) { // workaround to make sure the caption also knows the field is read-only
					targetField.addStyleName("v-readonly");
				} else {
					targetField.removeStyleName("v-readonly");
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean readOnly;
			if (sourceField.getValue() == null) {
				readOnly = readOnlyWhenNull;
			} else {
				readOnly = sourceValues.contains(event.getProperty().getValue());
			}
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if (readOnly && clearOnReadOnly && targetField.getValue() != null) {
					targetField.setReadOnly(false);
					targetField.clear();
				}
				targetField.setReadOnly(readOnly);
				if (readOnly) { // workaround to make sure the caption also knows the field is read-only
					targetField.addStyleName("v-readonly");
				} else {
					targetField.removeStyleName("v-readonly");
				}
			}
		});
	}

	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		String targetPropertyId,
		Object sourcePropertyId,
		List<Object> sourceValues,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Object sourcePropertyId,
		final List<Object> sourceValues,
		final boolean clearOnHidden) {

		Field sourceField = fieldGroup.getField(sourcePropertyId);

		setVisibleWhen(fieldGroup, targetPropertyIds, sourceField, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		String targetPropertyId,
		Field sourceField,
		List<Object> sourceValues,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourceField, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Field sourceField,
		final List<Object> sourceValues,
		final boolean clearOnHidden) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());

		setVisibleWhen(sourceField, targetFields, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(Field sourceField, List<? extends Field<?>> targetFields, List<Object> sourceValues, boolean clearOnHidden) {
		if (sourceField != null) {
			if (sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}

			// initialize
			{
				boolean visible = sourceValues.contains(sourceField.getValue());

				targetFields.forEach(targetField -> {
					targetField.setVisible(visible);
					if (!visible && clearOnHidden && targetField.getValue() != null) {
						targetField.clear();
					}
				});
			}

			sourceField.addValueChangeListener(event -> {
				boolean visible = sourceValues.contains(event.getProperty().getValue());
				targetFields.forEach(targetField -> {
					targetField.setVisible(visible);
					if (!visible && clearOnHidden && targetField.getValue() != null) {
						targetField.clear();
					}
				});
			});
		}
	}

	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Map<Object, List<Object>> sourcePropertyIdsAndValues,
		final boolean clearOnHidden) {

		onValueChangedSetVisible(fieldGroup, targetPropertyIds, sourcePropertyIdsAndValues, clearOnHidden);

		sourcePropertyIdsAndValues.forEach((sourcePropertyId, sourceValues) -> {
			fieldGroup.getField(sourcePropertyId)
				.addValueChangeListener(event -> onValueChangedSetVisible(fieldGroup, targetPropertyIds, sourcePropertyIdsAndValues, clearOnHidden));
		});
	}

	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		String targetPropertyId,
		Map<Object, List<Object>> sourcePropertyIdsAndValues,
		final boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyIdsAndValues, clearOnHidden);
	}

	private static void onValueChangedSetVisible(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Map<Object, List<Object>> sourcePropertyIdsAndValues,
		final boolean clearOnHidden) {

		//a workaround variable to be modified in the forEach lambda
		boolean[] visibleArray = {
			true };

		sourcePropertyIdsAndValues.forEach((sourcePropertyId, sourceValues) -> {
			if (!sourceValues.contains(fieldGroup.getField(sourcePropertyId).getValue()))
				visibleArray[0] = false;
		});

		boolean visible = visibleArray[0];

		for (Object targetPropertyId : targetPropertyIds) {
			@SuppressWarnings("rawtypes")
			Field targetField = fieldGroup.getField(targetPropertyId);
			targetField.setVisible(visible);
			if (!visible && clearOnHidden && targetField.getValue() != null) {
				targetField.clear();
			}
		}
	}

	public static void setRequiredWhen(
		FieldGroup fieldGroup,
		Object sourcePropertyId,
		List<String> targetPropertyIds,
		final List<Object> sourceValues) {

		setRequiredWhen(fieldGroup, fieldGroup.getField(sourcePropertyId), targetPropertyIds, sourceValues);
	}

	public static void setRequiredWhenNotNull(FieldGroup fieldGroup, Object sourcePropertyId, String targetPropertyId) {
		setRequiredWhen(fieldGroup, fieldGroup.getField(sourcePropertyId), Arrays.asList(targetPropertyId), Arrays.asList((Object) null), true, null);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(FieldGroup fieldGroup, Field sourceField, List<String> targetPropertyIds, final List<Object> sourceValues) {
		setRequiredWhen(fieldGroup, sourceField, targetPropertyIds, sourceValues, false, null);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(
		FieldGroup fieldGroup,
		Field sourceField,
		List<String> targetPropertyIds,
		final List<Object> sourceValues,
		Disease disease) {
		setRequiredWhen(fieldGroup, sourceField, targetPropertyIds, sourceValues, false, disease);
	}

	/**
	 * Sets the target fields to required when the sourceField has a value that's
	 * contained in the sourceValues list; the disease is needed to make sure that
	 * no fields are set to required that are not visible and therefore cannot be
	 * edited by the user.
	 */
	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(
		FieldGroup fieldGroup,
		Field sourceField,
		List<String> targetPropertyIds,
		final List<Object> sourceValues,
		boolean requiredWhenNot,
		Disease disease) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());

		setRequiredWhen(sourceField, targetFields, sourceValues, requiredWhenNot, disease);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(
		Field sourceField,
		List<? extends Field<?>> targetFields,
		List<Object> sourceValues,
		boolean requiredWhenNot,
		Disease disease) {

		if (sourceField != null) {
			if (sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}

			// initialize
			{
				boolean required = sourceValues.contains(sourceField.getValue());

				for (Field targetField : targetFields) {
					if (!targetField.isVisible()) {
						targetField.setRequired(false);
						continue;
					}

					if (disease == null || Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, targetField.getId(), disease)) {
						targetField.setRequired(required);
					}
				}
			}

			sourceField.addValueChangeListener(event -> {
				boolean required = sourceValues.contains(event.getProperty().getValue());
				required = required != requiredWhenNot;
				for (Field targetField : targetFields) {
					if (!targetField.isVisible()) {
						targetField.setRequired(false);
						continue;
					}

					if (disease == null || Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, targetField.getId(), disease)) {
						targetField.setRequired(required);
					}
				}
			});
		}
	}

	/**
	 * Sets the target fields to enabled when the source field has a value that's
	 * contained in the sourceValues list.
	 */
	@SuppressWarnings("rawtypes")
	public static void setEnabledWhen(
		FieldGroup fieldGroup,
		Field sourceField,
		final List<Object> sourceValues,
		List<Object> targetPropertyIds,
		boolean clearOnDisabled) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean enabled = sourceValues.contains(sourceField.getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setEnabled(enabled);
				if (!enabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean enabled = sourceValues.contains(event.getProperty().getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setEnabled(enabled);
				if (!enabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		});
	}

	public static void setFirstVisibleClearOthers(Field<?> first, Field<?>... others) {
		if (first != null) {
			first.setVisible(true);
		}
		for (Field<?> other : others) {
			other.setVisible(false);
			other.setValue(null);
		}
	}

	public static void setFirstRequired(Field<?> first, Field<?>... others) {
		if (first != null) {
			first.setRequired(true);
		}
		for (Field<?> other : others) {
			other.setRequired(false);
		}
	}

	public static void updateItems(AbstractSelect select, List<?> items) {
		Object value = select.getValue();
		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		select.removeAllItems();
		if (items != null) {
			select.addItems(items);
		}
		select.setValue(value);
		select.setReadOnly(readOnly);
	}

	public static <T> void updateItems(ComboBox<T> select, Collection<T> items) {
		T value = select.getValue();
		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		if (items != null) {
			select.setItems(items);
			if (items.contains(value)) {
				select.setValue(value);
			} else {
				select.clear();
			}
		} else {
			removeItems(select);
		}
		select.setReadOnly(readOnly);
	}

	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public static void updateEnumData(AbstractSelect select, Iterable<? extends Enum> enumData) {

		select.removeAllItems();
		for (Object r : enumData) {
			Item newItem = select.addItem(r);
			newItem.getItemProperty(DefaultFieldGroupFieldFactory.CAPTION_PROPERTY_ID).setValue(r.toString());
		}
	}

	public static void removeItems(AbstractSelect select) {
		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		select.removeAllItems();
		select.setReadOnly(readOnly);
	}

	public static void removeItems(ComboBox<?> select) {

		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		select.setItems(new ArrayList<>());
		select.setReadOnly(readOnly);
		select.clear();
	}

	public static void addSoftRequiredStyle(Field<?>... fields) {

		for (Field<?> field : fields) {
			if (!field.getStyleName().contains(CssStyles.SOFT_REQUIRED)) {
				CssStyles.style(field, CssStyles.SOFT_REQUIRED);
			}
		}
	}

	public static void removeSoftRequiredStyle(Field<?>... fields) {

		for (Field<?> field : fields) {
			CssStyles.removeStyles(field, CssStyles.SOFT_REQUIRED);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void addSoftRequiredStyleWhen(Field<?> sourceField, List<Field<?>> targetFields, final List<Object> sourceValues) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean softRequired = sourceValues.contains(sourceField.getValue());
			for (Field<?> targetField : targetFields) {
				if (softRequired) {
					addSoftRequiredStyle(targetField);
				} else {
					removeSoftRequiredStyle(targetField);
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean softRequired = sourceValues.contains(event.getProperty().getValue());
			for (Field<?> targetField : targetFields) {
				if (softRequired) {
					addSoftRequiredStyle(targetField);
				} else {
					removeSoftRequiredStyle(targetField);
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	public static void addSoftRequiredStyleWhen(
		FieldGroup fieldGroup,
		Field sourceField,
		List<String> targetPropertyIds,
		final List<Object> sourceValues,
		Disease disease) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean required = sourceValues.contains(sourceField.getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if (disease == null || Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, (String) targetPropertyId, disease)) {
					if (required) {
						addSoftRequiredStyle(targetField);
					} else {
						removeSoftRequiredStyle(targetField);
					}
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean required = sourceValues.contains(event.getProperty().getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if (disease == null || Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, (String) targetPropertyId, disease)) {
					if (required) {
						addSoftRequiredStyle(targetField);
					} else {
						removeSoftRequiredStyle(targetField);
					}
				}
			}
		});
	}

	public static Stream<Component> stream(Component parent) {

		if (parent instanceof HasComponents) {
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(((HasComponents) parent).iterator(), Spliterator.ORDERED), false)
				.map(child -> stream(child))
				.reduce(Stream.of(parent), (s1, s2) -> Stream.concat(s1, s2));
		} else {
			return Stream.of(parent);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Stream<Field> streamFields(Component parent) {
		return FieldHelper.stream(parent).filter(c -> c instanceof Field).map(c -> (Field) c);
	}
}
