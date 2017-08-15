package de.symeda.sormas.ui.utils;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.Diseases;

public final class FieldHelper {

	public static void setReadOnlyWhen(FieldGroup fieldGroup, Object targetPropertyId, 
			Object sourcePropertyId, List<Object> sourceValues, boolean clearOnReadOnly) {
		setReadOnlyWhen(fieldGroup, Arrays.asList(targetPropertyId), sourcePropertyId, sourceValues, clearOnReadOnly);
	}

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
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
	
	public static void setRequiredWhen(FieldGroup fieldGroup, Object sourcePropertyId,
			List<Object> targetPropertyIds, final List<Object> sourceValues) {
		
		setRequiredWhen(fieldGroup, fieldGroup.getField(sourcePropertyId), targetPropertyIds, sourceValues);
	}
	
	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(FieldGroup fieldGroup, Field sourceField,
			List<Object> targetPropertyIds, final List<Object> sourceValues) {
		
		if(sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}
		
		// initialize
		{
			boolean required = sourceValues.contains(sourceField.getValue());
			for(Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setRequired(required);
			}
		}
		
		sourceField.addValueChangeListener(event -> {
			boolean required = sourceValues.contains(event.getProperty().getValue());
			for(Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				targetField.setRequired(required);
			}
		});
	}
	
	/**
	 * Sets the target fields to required when the sourceField has a value that's contained
	 * in the sourceValues list; the disease is needed to make sure that no fields are set
	 * to required that are not visible and therefore cannot be edited by the user.
	 */
	@SuppressWarnings("rawtypes")
	public static void setRequiredWhen(FieldGroup fieldGroup, Field sourceField, 
			List<Object> targetPropertyIds, final List<Object> sourceValues, Disease disease) {
		
		if(sourceField instanceof AbstractField<?>) {
			((AbstractField) sourceField).setImmediate(true);
		}
		
		// initialize
		{
			boolean required = sourceValues.contains(sourceField.getValue());
			for(Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if(Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, (String) targetPropertyId, disease)) {
					targetField.setRequired(required);
				}
			}
		}
		
		sourceField.addValueChangeListener(event -> {
			boolean required = sourceValues.contains(event.getProperty().getValue());
			for(Object targetPropertyId : targetPropertyIds) {
				Field targetField = fieldGroup.getField(targetPropertyId);
				if(Diseases.DiseasesConfiguration.isDefined(SymptomsDto.class, (String) targetPropertyId, disease)) {
					targetField.setRequired(required);
				}
			}
		});
	}
	
	/**
	 * Sets the target fields to enabled when the source field has a value that's contained
	 * in the sourceValues list.
	 */
	@SuppressWarnings("rawtypes")
	public static void setEnabledWhen(FieldGroup fieldGroup, Field sourceField, final List<Object> sourceValues,
			List<Object> targetPropertyIds, boolean clearOnDisabled) {
		
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
	

}
