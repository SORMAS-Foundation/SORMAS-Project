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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
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
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public final class FieldHelper {

	private FieldHelper() {
		// Hide Utility Class Constructor
	}

	public static void setReadOnlyWhen(
		FieldGroup fieldGroup,
		Object targetPropertyId,
		Object sourcePropertyId,
		List<?> sourceValues,
		boolean clearOnReadOnly,
		boolean readOnlyWhenNull) {

		setReadOnlyWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnReadOnly, readOnlyWhenNull);
	}

	@SuppressWarnings("rawtypes")
	public static void setReadOnlyWhen(
		final FieldGroup fieldGroup,
		List<?> targetPropertyIds,
		Object sourcePropertyId,
		final List<?> sourceValues,
		final boolean clearOnReadOnly,
		boolean readOnlyWhenNull) {

		Field sourceField = fieldGroup.getField(sourcePropertyId);
		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean readOnly;
			if (getNullableSourceFieldValue(sourceField) == null) {
				readOnly = readOnlyWhenNull;
			} else {
				readOnly = sourceValues.contains(getNullableSourceFieldValue(sourceField));
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
			if (getNullableSourceFieldValue(sourceField) == null) {
				readOnly = readOnlyWhenNull;
			} else {
				readOnly = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
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

	public static void setVisibleWithCheckersWhen(
		FieldGroup fieldGroup,
		Class<?> targetClass,
		String targetPropertyId,
		Object sourcePropertyId,
		Object sourceValue,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		boolean clearOnHidden) {
		if (fieldVisibilityCheckers.isVisible(targetClass, targetPropertyId)) {
			FieldHelper.setVisibleWhen(fieldGroup, targetPropertyId, sourcePropertyId, sourceValue, clearOnHidden);
		} else {
			fieldGroup.getField(targetPropertyId).setVisible(false);
		}
	}

	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		String targetPropertyId,
		Object sourcePropertyId,
		Object sourceValue,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, targetPropertyId, sourcePropertyId, Arrays.asList(sourceValue), clearOnHidden);
	}

	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		String targetPropertyId,
		Object sourcePropertyId,
		List<?> sourceValues,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnHidden);
	}

	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Object sourcePropertyId,
		Object sourceValue,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, targetPropertyIds, sourcePropertyId, Arrays.asList(sourceValue), clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Object sourcePropertyId,
		final List<?> sourceValues,
		final boolean clearOnHidden) {

		Field sourceField = fieldGroup.getField(sourcePropertyId);

		setVisibleWhen(fieldGroup, targetPropertyIds, sourceField, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		FieldGroup fieldGroup,
		String targetPropertyId,
		Field sourceField,
		List<?> sourceValues,
		boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourceField, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Field sourceField,
		final List<?> sourceValues,
		final boolean clearOnHidden) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());

		setVisibleWhen(sourceField, targetFields, sourceValues, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(Field sourceField, List<? extends Field<?>> targetFields, List<?> sourceValues, boolean clearOnHidden) {
		setVisibleWhen(sourceField, targetFields, field -> sourceValues.contains(getNullableSourceFieldValue(field)), clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhenSourceNotNull(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		String sourcePropertyId,
		boolean clearOnHidden) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());
		Field sourceField = fieldGroup.getField(sourcePropertyId);
		setVisibleWhenSourceNotNull(sourceField, targetFields, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhenSourceNotNull(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Field sourceField,
		boolean clearOnHidden) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());
		setVisibleWhenSourceNotNull(sourceField, targetFields, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhenSourceNotNull(Field sourceField, List<? extends Field<?>> targetFields, boolean clearOnHidden) {
		setVisibleWhen(sourceField, targetFields, field -> getNullableSourceFieldValue(field) != null, clearOnHidden);
	}

	@SuppressWarnings("rawtypes")
	public static void setVisibleWhen(
		Field sourceField,
		List<? extends Field<?>> targetFields,
		Function<Field, Boolean> isVisibleFunction,
		boolean clearOnHidden) {
		if (sourceField != null) {
			if (sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}

			// initialize
			{
				boolean visible = isVisibleFunction.apply(sourceField);

				targetFields.forEach(targetField -> {
					targetField.setVisible(visible);
					if (!visible && clearOnHidden && targetField.getValue() != null) {
						targetField.clear();
					}
				});
			}

			sourceField.addValueChangeListener(event -> {
				boolean visible = isVisibleFunction.apply((Field) event.getProperty());
				targetFields.forEach(targetField -> {
					targetField.setVisible(visible);
					if (!visible && clearOnHidden && targetField.getValue() != null) {
						targetField.clear();
					}
				});
			});
		}
	}

	public static void setCaptionWhen(Field<?> sourceField, Field<?> targetField, Object sourceValue, String matchCaption, String noMatchCaption) {
		if (sourceField != null) {
			// initialize
			{
				boolean matches = sourceValue.equals(getNullableSourceFieldValue(sourceField));

				targetField.setCaption(matches ? matchCaption : noMatchCaption);
			}

			sourceField.addValueChangeListener(event -> {
				boolean matches = sourceValue.equals(getNullableSourceFieldValue(sourceField));
				targetField.setCaption(matches ? matchCaption : noMatchCaption);
			});
		}
	}

	public static void setVisibleWhen(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Map<?, ? extends List<?>> sourcePropertyIdsAndValues,
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
		Map<?, ? extends List<?>> sourcePropertyIdsAndValues,
		final boolean clearOnHidden) {

		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyIdsAndValues, clearOnHidden);
	}

	public static void setVisibleWhen(final Field targetField, Map<Field, ? extends List<?>> sourceFieldsAndValues, final boolean clearOnHidden) {

		onValueChangedSetVisible(targetField, sourceFieldsAndValues, clearOnHidden);
		sourceFieldsAndValues.forEach(
			(sourcePropertyId, sourceValues) -> targetField
				.addValueChangeListener(event -> onValueChangedSetVisible(targetField, sourceFieldsAndValues, clearOnHidden)));
	}

	private static void onValueChangedSetVisible(
		final FieldGroup fieldGroup,
		List<String> targetPropertyIds,
		Map<?, ? extends List<?>> sourcePropertyIdsAndValues,
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

	private static void onValueChangedSetVisible(
		Field targetField,
		Map<Field, ? extends List<?>> sourceFieldsAndValues,
		final boolean clearOnHidden) {

		//a workaround variable to be modified in the forEach lambda
		boolean[] visibleArray = {
			true };

		sourceFieldsAndValues.forEach((sourceField, sourceValues) -> {
			if (!sourceValues.contains(sourceField.getValue()))
				visibleArray[0] = false;
		});
		boolean visible = visibleArray[0];

		targetField.setVisible(visible);
		if (!visible && clearOnHidden && targetField.getValue() != null) {
			targetField.clear();
		}
	}

	public static void setRequiredWhen(FieldGroup fieldGroup, Object sourcePropertyId, List<String> targetPropertyIds, final List<?> sourceValues) {

		setRequiredWhen(fieldGroup, fieldGroup.getField(sourcePropertyId), targetPropertyIds, sourceValues);
	}

	public static void setRequiredWhenNotNull(FieldGroup fieldGroup, Object sourcePropertyId, String targetPropertyId) {
		setRequiredWhen(fieldGroup, fieldGroup.getField(sourcePropertyId), Arrays.asList(targetPropertyId), Arrays.asList((Object) null), true, null);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(FieldGroup fieldGroup, Field sourceField, List<String> targetPropertyIds, final List<?> sourceValues) {
		setRequiredWhen(fieldGroup, sourceField, targetPropertyIds, sourceValues, false, null);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(
		FieldGroup fieldGroup,
		Field sourceField,
		List<String> targetPropertyIds,
		final List<?> sourceValues,
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
		final List<?> sourceValues,
		boolean requiredWhenNot,
		Disease disease) {

		final List<? extends Field<?>> targetFields = targetPropertyIds.stream().map(id -> fieldGroup.getField(id)).collect(Collectors.toList());

		setRequiredWhen(sourceField, targetFields, sourceValues, requiredWhenNot, disease);
	}

	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(
		Field sourceField,
		List<? extends Field<?>> targetFields,
		List<?> sourceValues,
		boolean requiredWhenNot,
		Disease disease) {

		if (sourceField != null) {
			if (sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}

			// initialize
			{
				boolean required = sourceValues.contains(getNullableSourceFieldValue(sourceField));

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
				boolean required = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
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

	public static <T> void setValueWhen(FieldGroup fieldGroup, String sourceFieldId, Object sourceValue, String targetPropertyId, T targetValue) {
		final Field<?> sourceField = fieldGroup.getField(sourceFieldId);
		final List<Object> sourceValues = Collections.singletonList(sourceValue);
		final Field<T> targetFields = (Field<T>) fieldGroup.getField(targetPropertyId);

		setValueWhen(sourceField, sourceValues, targetFields, targetValue);
	}

	public static <T> void setValueWhen(Field<?> sourceField, final List<?> sourceValues, Field<T> targetField, T targetValue) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField<?>) sourceField).setImmediate(true);
		}

		// initialize
		{
			if (sourceValues.contains(getNullableSourceFieldValue(sourceField))) {
				targetField.setValue(targetValue);
			}
		}

		sourceField.addValueChangeListener(event -> {
			if (sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())))) {
				targetField.setValue(targetValue);
			}
		});
	}

	public static void setEnabledWhen(
		FieldGroup fieldGroup,
		String sourceFieldId,
		Object sourceValue,
		String targetPropertyId,
		boolean clearOnDisabled) {

		final Field<?> sourceField = fieldGroup.getField(sourceFieldId);
		final List<Object> sourceValues = Collections.singletonList(sourceValue);
		final List<Field<?>> targetFields = Collections.singletonList(fieldGroup.getField(targetPropertyId));

		setEnabledWhen(sourceField, sourceValues, targetFields, clearOnDisabled);
	}

	public static void setDisabledWhen(
		FieldGroup fieldGroup,
		String sourceFieldId,
		Object sourceValue,
		String targetPropertyId,
		boolean clearOnDisabled) {

		final Field<?> sourceField = fieldGroup.getField(sourceFieldId);
		final List<Object> sourceValues = Collections.singletonList(sourceValue);
		final List<Field<?>> targetFields = Collections.singletonList(fieldGroup.getField(targetPropertyId));

		setDisabledWhen(sourceField, sourceValues, targetFields, clearOnDisabled);
	}

	/**
	 * Sets the target fields to enabled when the source field has a value that's
	 * contained in the sourceValues list.
	 */
	public static void setEnabledWhen(
		FieldGroup fieldGroup,
		Field<?> sourceField,
		final List<?> sourceValues,
		List<?> targetPropertyIds,
		boolean clearOnDisabled) {
		final List<Field<?>> targetFields = targetPropertyIds.stream().map(fieldGroup::getField).collect(Collectors.toList());

		setEnabledWhen(sourceField, sourceValues, targetFields, clearOnDisabled);
	}

	public static void setEnabledWhen(Field<?> sourceField, final List<?> sourceValues, List<Field<?>> targetFields, boolean clearOnDisabled) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField<?>) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean enabled = sourceValues.contains(getNullableSourceFieldValue(sourceField));
			for (Field<?> targetField : targetFields) {
				targetField.setEnabled(enabled);
				if (!enabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean enabled = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
			for (Field<?> targetField : targetFields) {
				targetField.setEnabled(enabled);
				if (!enabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		});
	}

	public static void setDisabledWhen(Field<?> sourceField, final List<?> sourceValues, List<Field<?>> targetFields, boolean clearOnDisabled) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField<?>) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean disabled = sourceValues.contains(getNullableSourceFieldValue(sourceField));
			for (Field<?> targetField : targetFields) {
				targetField.setEnabled(!disabled);
				if (disabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean disabled = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
			for (Field<?> targetField : targetFields) {
				targetField.setEnabled(!disabled);
				if (disabled && clearOnDisabled) {
					targetField.clear();
				}
			}
		});
	}

	public static void setEnabled(boolean enabled, Field... fields) {
		Arrays.asList(fields).forEach(field -> field.setEnabled(enabled));
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

	public static void updateItems(UserField select, List<UserReferenceDto> items) {
		UserReferenceDto value = select.getValue();
		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		select.removeAllItems();
		if (items != null) {
			select.addItems(items);
		}
		select.setValue(value);
		select.setReadOnly(readOnly);
	}

	public static void updateItems(
		AbstractSelect select,
		List<?> items,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		Class<? extends Enum> enumClass) {

		Object value = select.getValue();
		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		select.removeAllItems();
		if (items != null) {
			if (fieldVisibilityCheckers != null) {
				select.addItems(
					items.stream().filter(i -> fieldVisibilityCheckers.isVisible(enumClass, ((Enum<?>) i).name())).collect(Collectors.toList()));
			} else {
				select.addItems(items);
			}
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

		boolean readOnly = select.isReadOnly();
		select.setReadOnly(false);
		Object value = select.getValue();
		select.removeAllItems();
		select.addContainerProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID, String.class, "");
		select.setItemCaptionPropertyId((SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID));
		if (enumData != null) {
			for (Object r : enumData) {
				Item newItem = select.addItem(r);
				newItem.getItemProperty(DefaultFieldGroupFieldFactory.CAPTION_PROPERTY_ID).setValue(r.toString());
			}
		}
		select.setValue(value);
		select.setReadOnly(readOnly);
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
	public static void addSoftRequiredStyleWhen(Field<?> sourceField, List<Field<?>> targetFields, final List<?> sourceValues) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean softRequired = sourceValues.contains(getNullableSourceFieldValue(sourceField));
			for (Field<?> targetField : targetFields) {
				if (softRequired) {
					addSoftRequiredStyle(targetField);
				} else {
					removeSoftRequiredStyle(targetField);
				}
			}
		}

		sourceField.addValueChangeListener(event -> {
			boolean softRequired = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
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
		final List<?> sourceValues,
		Disease disease) {

		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean required = sourceValues.contains(getNullableSourceFieldValue(sourceField));
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
			boolean required = sourceValues.contains(getNullableSourceFieldValue(((Field) event.getProperty())));
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

	public static Collection<Enum<?>> getVisibleEnumItems(Class<Enum<?>> enumClass, FieldVisibilityCheckers checkers) {
		return Arrays.stream(enumClass.getEnumConstants())
			.filter(constant -> checkers.isVisible(enumClass, constant.name()))
			.collect(Collectors.toList());
	}

	public static Object getNullableSourceFieldValue(Field sourceField) {
		if (sourceField instanceof NullableOptionGroup && ((NullableOptionGroup) sourceField).isMultiSelect()) {
			return ((NullableOptionGroup) sourceField).getNullableValue();
		} else {
			return sourceField.getValue();
		}
	}

	public static void updateOfficersField(UserField officerField, CaseDataDto caze, UserRight right) {
		List<DistrictReferenceDto> officerDistricts =
			Stream.of(caze.getResponsibleDistrict(), caze.getDistrict()).filter(Objects::nonNull).collect(Collectors.toList());
		FieldHelper.updateItems(
			officerField,
			officerDistricts.size() > 0 ? FacadeProvider.getUserFacade().getUserRefsByDistricts(officerDistricts, caze.getDisease(), right) : null);

	}

	public static void setComboInaccessible(ComboBoxWithPlaceholder combo) {
		combo.setEnabled(false);
		combo.setPlaceholder(I18nProperties.getCaption(Captions.inaccessibleValue));

	}
}
