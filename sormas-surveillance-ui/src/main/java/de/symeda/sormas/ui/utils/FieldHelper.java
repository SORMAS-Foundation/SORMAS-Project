package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

public final class FieldHelper {

	public static void setReadOnlyWhen(FieldGroup fieldGroup, Object targetPropertyId, 
			Object sourcePropertyId, List<Object> sourceValues, boolean clearOnReadOnly) {
		setReadOnlyWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnReadOnly);
	}

	public static void setReadOnlyWhen(final FieldGroup fieldGroup, List<Object> targetPropertyIds, 
			Object sourcePropertyId, final List<Object> sourceValues, final boolean clearOnReadOnly) {

		Field sourceField = fieldGroup.getField(sourcePropertyId); 
		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}

		// initialize
		{
			boolean readOnly = sourceValues.contains(sourceField.getValue());
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
			boolean readOnly = sourceValues.contains(event.getProperty().getValue());
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

	public static void setVisibleWhen(FieldGroup fieldGroup, Object targetPropertyId, 
			Object sourcePropertyId, List<Object> sourceValues, boolean clearOnHidden) {
		setVisibleWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnHidden);
	}

	public static void setVisibleWhen(final FieldGroup fieldGroup, List<Object> targetPropertyIds, 
			Object sourcePropertyId, final List<Object> sourceValues, final boolean clearOnHidden) {

		Field sourceField = fieldGroup.getField(sourcePropertyId);
		if (sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}
		
		// initialize
		{
			boolean visible = sourceValues.contains(sourceField.getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setVisible(visible);
				if (!visible && clearOnHidden && targetField.getValue() != null) {
					targetField.clear();
				}
			}
		}
		
		sourceField.addValueChangeListener(event -> {
			boolean visible = sourceValues.contains(event.getProperty().getValue());
			for (Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setVisible(visible);
				if (!visible && clearOnHidden && targetField.getValue() != null) {
					targetField.clear();
				}
			}
		});
	}

	public static void setFirstVisibleClearOthers(Field<?> first, Field<?> ...others) {
		if (first != null) {
			first.setVisible(true);
		}
		for (Field<?> other : others) {
			other.setVisible(false);
			other.setValue(null);
		}
	}
	
	public static void setFirstRequired(Field<?> first, Field<?> ...others) {
		if (first != null) {
			first.setRequired(true);
		}
		for (Field<?> other : others) {
			other.setRequired(false);
		}
	}

}
