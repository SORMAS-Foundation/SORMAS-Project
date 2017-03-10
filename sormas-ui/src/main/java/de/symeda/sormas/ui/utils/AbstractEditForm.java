package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.ui.caze.PreviousHospitalizationsField;
import de.symeda.sormas.ui.epidata.EpiDataBurialsField;
import de.symeda.sormas.ui.epidata.EpiDataGatheringsField;
import de.symeda.sormas.ui.epidata.EpiDataTravelsField;
import de.symeda.sormas.ui.location.LocationForm;

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
				
				if (type.isEnum()) {
					if (SymptomState.class.isAssignableFrom(type)) {
						OptionGroup field = super.createField(type, OptionGroup.class);
						CssStyles.style(field, CssStyles.ROW_OPTIONGROUP);
						return (T) field;
					} else {
						if (!AbstractSelect.class.isAssignableFrom(fieldType)) {
							fieldType = (Class<T>) NativeSelect.class;
						}
						T field = super.createField(type, fieldType);
						if (OptionGroup.class.isAssignableFrom(fieldType)) {
							CssStyles.style(field, CssStyles.INLINE_OPTIONGROUP);
						}
						return field;
					}
				}
				else if (AbstractSelect.class.isAssignableFrom(fieldType)) {
					return (T) createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
				} 
				else if (LocationForm.class.isAssignableFrom(fieldType)) {
					return (T) new LocationForm();
				} 
				else if (DateTimeField.class.isAssignableFrom(fieldType)) {
					return (T) new DateTimeField();
				} 
				else if (PreviousHospitalizationsField.class.isAssignableFrom(fieldType)) {
					return (T) new PreviousHospitalizationsField();
				} 
				else if (EpiDataBurialsField.class.isAssignableFrom(fieldType)) {
					return (T) new EpiDataBurialsField();
				}
				else if (EpiDataGatheringsField.class.isAssignableFrom(fieldType)) {
					return (T) new EpiDataGatheringsField();
				}
				else if (EpiDataTravelsField.class.isAssignableFrom(fieldType)) {
					return (T) new EpiDataTravelsField();
				}
				
				return super.createField(type, fieldType);
			}
			
			@Override
			protected <T extends AbstractTextField> T createAbstractTextField(Class<T> fieldType) {
				T textField = super.createAbstractTextField(fieldType);
				textField.setNullRepresentation("");
				return textField;
			}
		});
		
		setWidth(900, Unit.PIXELS);
		setHeightUndefined();
		
		addFields();
	}

	@Override
	public CustomLayout initContent() {
		
		String htmlLayout = createHtmlLayout();
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();

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

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addCustomField(String fieldId, Class<?> dataType, Class<T> fieldType) {
    	T field = getFieldGroup().getFieldFactory().createField(dataType, fieldType);
		formatField(field, fieldId);
		getContent().addComponent(field, fieldId);
		return field;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T extends Field> T addField(String propertyId) {
		return (T) addField(propertyId, Field.class);
	}
	
	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addField(String propertyId, Class<T> fieldType) {
		T field = getFieldGroup().buildAndBind(propertyId, (Object)propertyId, fieldType);
		formatField(field, propertyId);
		getContent().addComponent(field, propertyId);
        return field;
	}
	
	@SuppressWarnings("rawtypes")
	protected <T extends Field> T formatField(T field, String propertyId) {
		field.setCaption(I18nProperties.getPrefixFieldCaption(getPropertyI18nPrefix(), propertyId, field.getCaption()));
		if (field instanceof AbstractField) {
			AbstractField abstractField = (AbstractField)field;
			abstractField.setDescription(I18nProperties.getPrefixFieldDescription(
					getPropertyI18nPrefix(), propertyId, abstractField.getDescription()));
		}
		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}
	
	protected Field<?> getField(String fieldOrPropertyId) {
		Field<?> field = getFieldGroup().getField(fieldOrPropertyId);
		if (field == null) {
			// try to get the field from the layout
			Component component = getContent().getComponent(fieldOrPropertyId);
			if (component instanceof Field<?>) {
				field = (Field<?>)component;
			}
		}
		return field;
	}
	
	protected void styleAsRow(List<String> fields) {
		for(String field : fields) {
			CssStyles.style(getFieldGroup().getField(field), CssStyles.ROW_OPTIONGROUP);
		}
	}
	
	protected void setReadOnly(boolean readOnly, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			getField(propertyId).setReadOnly(readOnly);
		}
	}
	
	protected void setVisible(boolean visible, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			getField(propertyId).setVisible(visible);
		}
	}
	
	protected void discard(String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getField(propertyId).discard();
		}
	}
	
	protected void setRequired(boolean required, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			getField(propertyId).setRequired(required);
		}
	}

	protected void addFieldListeners(String fieldOrPropertyId, ValueChangeListener ...listeners) {
		for (ValueChangeListener listener : listeners) {
			getField(fieldOrPropertyId).addValueChangeListener(listener);
		}
	}
	
	protected void addValidators(String fieldOrPropertyId, Validator... validators) {
		for (Validator validator : validators) {
			getField(fieldOrPropertyId).addValidator(validator);
		}
	}

	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}
}
