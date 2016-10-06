package de.symeda.sormas.ui.utils;

import java.util.Arrays;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.ui.surveillance.location.LocationForm;

@SuppressWarnings("serial")
public abstract class AbstractEditForm <DTO extends DataTransferObject> extends CustomField<DTO> {// implements DtoEditForm<DTO> {

	private final BeanFieldGroup<DTO> fieldGroup;
	
	private final String propertyI18nPrefix;

	private Class<DTO> type;
	
	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix) {
		
		this.type = type;
		this.propertyI18nPrefix = propertyI18nPrefix;
		
		fieldGroup = new BeanFieldGroup<DTO>(type) {

			@Override
			protected void configureField(Field<?> field) {
				field.setBuffered(isBuffered());

				field.setEnabled(isEnabled());

				if (field.getPropertyDataSource().isReadOnly()) {
					field.setReadOnly(true);
				} else if (isReadOnly()) {
					field.setReadOnly(true);
				}
			}
		};
		
		fieldGroup.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
				
				if (AbstractSelect.class.isAssignableFrom(fieldType)) {
					return (T) createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
				} else if (LocationForm.class.isAssignableFrom(fieldType)) {
					return (T) new LocationForm();
				} else if (type.isEnum() && OptionGroup.class != fieldType) { // ComboBoxen für Enum-Values
					return (T) createEnumField(type);
				}

				return super.createField(type, fieldType);
			}
			
			@Override
			protected <T extends AbstractTextField> T createAbstractTextField(Class<T> fieldType) {
				T textField = super.createAbstractTextField(fieldType);
				textField.setNullRepresentation("");
				return textField;
			}
			

			@SuppressWarnings("rawtypes")
			protected Field createEnumField(Class<?> type) {
				Object[] enumConstants = type.getEnumConstants();

				if (SymptomState.class.isAssignableFrom(type)) {
					OptionGroup field = new OptionGroup(null, Arrays.asList(SymptomState.values()));
					CssStyles.style(field, CssStyles.INLINE_OPTIONGROUP);
					field.setImmediate(false);
					field.setNullSelectionAllowed(false);
					field.setMultiSelect(false);
					return field;
				} else {
					AbstractSelect cb;
					if (enumConstants.length <= 20) {
						cb = new NativeSelect();
					} else {
						ComboBox ccb = new ComboBox();
						ccb.setPageLength(enumConstants.length);
						cb = ccb;
					}
					for (Object o : enumConstants) {
						cb.addItem(o);
					}
					return cb;
				}
			}
		});
		
		addFields();
	}

	@Override
	public CustomLayout initContent() {
		
		String htmlLayout = createHtmlLayout();
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(htmlLayout);
		layout.setSizeFull();
	    setSizeFull();
		
		return layout;
	}
	
	@Override
	public Class<? extends DTO> getType() {
		return type;
	}
	
	@Override
	protected CustomLayout getContent() {
		return (CustomLayout)super.getContent();
	}
	
	protected abstract String createHtmlLayout();
	protected abstract void addFields();	
	
	@Override
	protected DTO getInternalValue() {
		BeanItem<DTO> beanItem = getFieldGroup().getItemDataSource();
		if (beanItem == null) {
			return null;
		} else {
			return beanItem.getBean();
		}
	}

	@Override
	protected void setInternalValue(DTO newValue) {
		super.setInternalValue(newValue);
		BeanFieldGroup<DTO> fieldGroup = getFieldGroup();
		fieldGroup.setItemDataSource(newValue);
	}
	
	@Override
	public boolean isModified() {
		if (getFieldGroup().isModified()) {
			return true;
		}
		return super.isModified();
	}
	
	@Override
	public void commit() throws SourceException, InvalidValueException {
		try {
			getFieldGroup().commit();
		} catch (CommitException e) {
			throw new SourceException(this, e);
		}
		super.commit();
	}
	
	@Override
	public void discard() throws SourceException {
		getFieldGroup().discard();
		super.discard();
	}

	public BeanFieldGroup<DTO> getFieldGroup() {
		return this.fieldGroup;
	}

    protected void addFields(String ...properties) {
    	for (String property: properties) {
    		addField(property);
    	}
    }

	@SuppressWarnings("unchecked")
	protected <T extends Field> T addField(String propertyId) {
		return (T) addField(propertyId, Field.class);
	}
	
	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addField(String propertyId, Class<T> fieldType) {
		T field = getFieldGroup().buildAndBind(propertyId, (Object)propertyId, fieldType);

		field.setCaption(I18nProperties.getFieldCaption(getPropertyI18nPrefix(), propertyId, field.getCaption()));
		if (field instanceof AbstractField) {
			AbstractField abstractField = (AbstractField)field;
			abstractField.setDescription(I18nProperties.getFieldDescription(
					getPropertyI18nPrefix(), propertyId, abstractField.getDescription()));
		}
		
		field.setWidth(100, Unit.PERCENTAGE);
        
		getContent().addComponent(field, propertyId);
        return field;
	}
	
	protected void setReadOnly(boolean readOnly, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setReadOnly(readOnly);
		}
	}
	
	protected void setVisible(boolean visible, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setVisible(visible);
		}
	}
	
	protected void setRequired(boolean required, String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getFieldGroup().getField(propertyId).setRequired(required);
		}
	}

	protected void addFieldListener(String propertyId, ValueChangeListener ...listeners) {
		for (ValueChangeListener listener : listeners) {
			getFieldGroup().getField(propertyId).addValueChangeListener(listener);
		}
	}
	
	protected void addValidator(String propertyId, Validator... validators) {
		for (Validator validator : validators) {
			getFieldGroup().getField(propertyId).addValidator(validator);
		}
	}

	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}
}
