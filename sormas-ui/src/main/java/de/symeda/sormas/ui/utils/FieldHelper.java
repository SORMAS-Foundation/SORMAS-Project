package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

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
	
	/**
	 * Sets the field with the targetPropertyId to required when at least one of the
	 * fields associated with the sourcePropertyIds has its value set to "Yes" or the
	 * field associated with textFieldPropertyId is not empty. This method is intended to be used
	 * with the onsetDate and onsetSymptom fields.
	 * 
	 * @param fieldGroup
	 * @param targetPropertyId
	 * @param sourcePropertyIds
	 * @param sourceValues
	 */
	public static void setRequiredWhen(FieldGroup fieldGroup, Object targetPropertyId,
			List<String> sourcePropertyIds, List<Object> sourceValues, Object textFieldPropertyId) {
		
		final Field textField = fieldGroup.getField(textFieldPropertyId);
		if(textField instanceof AbstractField<?>) {
			((AbstractField) textField).setImmediate(true);
		}
		
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}
		}
		
		// Initialize
		final Field targetField = fieldGroup.getField(targetPropertyId);
		if(doesAnyContainValue(fieldGroup, sourcePropertyIds, sourceValues) || 
				!isEmptyTextField(fieldGroup, textFieldPropertyId)) {
			targetField.setRequired(true);
		} else {
			targetField.setRequired(false);
		}
		
		// Add listeners
		registerUpdateRequiredOnValueChange(textField, targetField, fieldGroup, sourcePropertyIds, sourceValues, textFieldPropertyId);
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			registerUpdateRequiredOnValueChange(sourceField, targetField, fieldGroup, sourcePropertyIds, sourceValues, textFieldPropertyId);
		}
		
	}
	
	/**
	 * Returns true if if the value of any field associated with the sourcePropertyIds
	 * is set to one of the values contained in sourceValues.
	 * 
	 * @param fieldGroup
	 * @param sourcePropertyIds
	 * @param sourceValues
	 * @return
	 */
	private static boolean doesAnyContainValue(FieldGroup fieldGroup, List<String> sourcePropertyIds, 
			List<Object> sourceValues) {
		
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceValues.contains(sourceField.getValue())) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isEmptyTextField(FieldGroup fieldGroup, Object sourcePropertyId) {
		TextField sourceField = (TextField)fieldGroup.getField(sourcePropertyId);
		return (sourceField.getValue() == null || sourceField.getValue().isEmpty());
	}
	
	private static void registerUpdateRequiredOnValueChange(Field field, Field targetField, FieldGroup fieldGroup, 
			List<String> sourcePropertyIds, List<Object> sourceValues, Object textFieldPropertyId) {
		
		field.addValueChangeListener(event -> {
			if(doesAnyContainValue(fieldGroup, sourcePropertyIds, sourceValues) || 
					!isEmptyTextField(fieldGroup, textFieldPropertyId)) {
				targetField.setRequired(true);
			} else {
				targetField.setRequired(false);
			}
		});
		
	}

}
