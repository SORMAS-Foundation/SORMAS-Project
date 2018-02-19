package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.ui.epidata.EpiDataBurialsField;
import de.symeda.sormas.ui.epidata.EpiDataGatheringsField;
import de.symeda.sormas.ui.epidata.EpiDataTravelsField;
import de.symeda.sormas.ui.hospitalization.PreviousHospitalizationsField;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public abstract class AbstractEditForm <DTO extends EntityDto> extends CustomField<DTO> implements CommitHandler {// implements DtoEditForm<DTO> {

	private final BeanFieldGroup<DTO> fieldGroup;

	private final String propertyI18nPrefix;

	private Class<DTO> type;
	private boolean hideValidationUntilNextCommit = false;
	private List<Field<?>> customFields = new ArrayList<>();
	private List<Field<?>> visibleAllowedFields = new ArrayList<>();

	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix, UserRight editOrCreateUserRight) {

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

		fieldGroup.addCommitHandler(this);

		fieldGroup.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {

				if (type.isEnum()) {
					if (SymptomState.class.isAssignableFrom(type)) {
						OptionGroup field = super.createField(type, OptionGroup.class);
						CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
						return (T) field;
					} else {
						if (!AbstractSelect.class.isAssignableFrom(fieldType)) {
							fieldType = (Class<T>) ComboBox.class;
						}
						T field = super.createField(type, fieldType);
						if (field instanceof OptionGroup) {
							CssStyles.style(field, ValoTheme.OPTIONGROUP_HORIZONTAL);
						} else if (field instanceof ComboBox) {
							((ComboBox)field).setFilteringMode(FilteringMode.CONTAINS);
						}
						return field;
					}
				}
				else if (AbstractSelect.class.isAssignableFrom(fieldType)) {
					return (T) createCompatibleSelect((Class<? extends AbstractSelect>) fieldType);
				} 
				else if (LocationEditForm.class.isAssignableFrom(fieldType)) {
					return (T) new LocationEditForm(editOrCreateUserRight);
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
				else if (fieldType.equals(Field.class)) {
					// no specific field type defined -> fallbacks

					if (Date.class.isAssignableFrom(type)) {
						DateField field = super.createField(type, DateField.class);
						field.setDateFormat(DateHelper.getDateFormat().toPattern());
						return (T) field;
					}
					else if (ReferenceDto.class.isAssignableFrom(type)) {
						return (T) new ComboBox();
					}
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

		if (editOrCreateUserRight != null && !LoginHelper.hasUserRight(editOrCreateUserRight)) {
			getFieldGroup().setReadOnly(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static CommitDiscardWrapperComponent<? extends AbstractEditForm> buildCommitDiscardWrapper(AbstractEditForm wrappedForm) {
		return new CommitDiscardWrapperComponent<>(wrappedForm, wrappedForm.getFieldGroup());
	}

	@SuppressWarnings("rawtypes")
	public static CommitDiscardWrapperComponent<VerticalLayout> buildCommitDiscardWrapper(AbstractEditForm ...wrappedForms) {
		VerticalLayout formsLayout = new VerticalLayout();
		FieldGroup[] fieldGroups = new FieldGroup[wrappedForms.length];
		for (int i=0; i<wrappedForms.length; i++) {
			formsLayout.addComponent(wrappedForms[i]);
			fieldGroups[i] = wrappedForms[i].getFieldGroup();
		}
		return new CommitDiscardWrapperComponent<>(formsLayout, fieldGroups);
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
	public void setValue(DTO newFieldValue) throws com.vaadin.data.Property.ReadOnlyException, ConversionException {
		super.setValue(newFieldValue);
	}

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
	public void preCommit(CommitEvent commitEvent) throws CommitException {
		if (hideValidationUntilNextCommit) {
			hideValidationUntilNextCommit = false;
			for (Field<?> field : getFieldGroup().getFields()) {
				if (field instanceof AbstractField) {
					AbstractField<?> abstractField = (AbstractField<?>)field;
					abstractField.setValidationVisible(true);
				}
			}

			for (Field<?> field : customFields) {
				if (field instanceof AbstractField) {
					AbstractField<?> abstractField = (AbstractField<?>)field;
					abstractField.setValidationVisible(true);
				}
			}
		}

		for (Field<?> field : customFields) {
			field.validate();
		}
	}

	/**
	 * Attention (!!!!)
	 * This method is not called when used with CommitDiscardWrapperComponent (uses FieldGroup instead)
	 */
	@Override
	public void commit() throws SourceException, InvalidValueException {

		try {
			getFieldGroup().commit();
		} catch (CommitException e) {
			if (e.getInvalidFields().size() > 0) {
				throw new InvalidValueException(
						e.getInvalidFields().keySet().stream().map(f -> f.getCaption()).collect(Collectors.joining(", ")), 
						e.getInvalidFields().values().stream().toArray(InvalidValueException[]::new));
			} else {
				throw new SourceException(this, e);
			}
		}
		super.commit();
	}

	@Override
	public void postCommit(CommitEvent commitEvent) throws CommitException {

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
		customFields.add(field);
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

		String caption = I18nProperties.getPrefixFieldCaption(getPropertyI18nPrefix(), propertyId, field.getCaption());
		field.setCaption(caption);

		if (field instanceof AbstractField) {
			AbstractField abstractField = (AbstractField)field;
			abstractField.setDescription(I18nProperties.getPrefixFieldDescription(
					getPropertyI18nPrefix(), propertyId, abstractField.getDescription()));

			if (hideValidationUntilNextCommit) {
				abstractField.setValidationVisible(false);
			}
		}

		String validationError = I18nProperties.getPrefixValidationError(getPropertyI18nPrefix(), propertyId);
		field.setRequiredError(String.format(validationError, caption));

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

	protected void styleAsOptionGroupHorizontal(List<String> fields) {
		for(String field : fields) {
			CssStyles.style((OptionGroup)getFieldGroup().getField(field), ValoTheme.OPTIONGROUP_HORIZONTAL);
		}
	}

	protected void setReadOnly(boolean readOnly, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			getField(propertyId).setReadOnly(readOnly);
		}
	}

	protected void setVisible(boolean visible, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			if (visible == false || isVisibleAllowed(propertyId)) {
				getField(propertyId).setVisible(visible);
			}
		}
	}

	protected void discard(String ...propertyIds) {
		for (String propertyId : propertyIds) {
			getField(propertyId).discard();
		}
	}

	protected void setRequired(boolean required, String ...fieldOrPropertyIds) {
		for (String propertyId : fieldOrPropertyIds) {
			Field<?> field = getField(propertyId);
			field.setRequired(required);
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

	public void hideValidationUntilNextCommit() {

		this.hideValidationUntilNextCommit  = true;

		for (Field<?> field : getFieldGroup().getFields()) {
			if (field instanceof AbstractField) {
				AbstractField<?> abstractField = (AbstractField<?>)field;
				abstractField.setValidationVisible(false);
			}
		}

		for (Field<?> field : customFields) {
			if (field instanceof AbstractField) {
				AbstractField<?> abstractField = (AbstractField<?>)field;
				abstractField.setValidationVisible(false);
			}
		}
	}

	/**
	 * Sets the initial visibilities based on annotations and builds a list of all fields in a form that are allowed to be visible - 
	 * this is either because the @Diseases and @Outbreaks annotations are not relevant or at least one of these annotations are present on the respective field.
	 * 
	 * @param disease	Not null if the @Diseases annotation should be taken into account
	 * @param viewMode	Not null if the @Outbreaks annotation should be taken into account
	 */
	protected void initializeVisibilitiesAndAllowedVisibilities(Disease disease, ViewMode viewMode) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			Field<?> field = getFieldGroup().getField(propertyId);
			boolean diseaseVisibility = true;
			boolean outbreakVisibility = true;
			
			if (disease != null) {
				if (!Diseases.DiseasesConfiguration.isDefinedOrMissing(getType(), (String) propertyId, disease)) {
					diseaseVisibility = false;
				}
			}
			
			if (viewMode != null && viewMode == ViewMode.OUTBREAK) {
				if (!Outbreaks.OutbreaksConfiguration.isDefined(getType(), (String) propertyId)) {
					outbreakVisibility = false;
				}
			}
			
			if (diseaseVisibility && outbreakVisibility) {
				visibleAllowedFields.add(field);
			} else {
				field.setVisible(false);
			}
		}
	}
	
	/**
	 * Returns true if the visibleAllowedFields list is either empty (because all fields are allowed to be visible) or contains
	 * the given field. This needs to be called before EVERY setVisible or setVisibleWhen call.
	 */
	protected boolean isVisibleAllowed(Field<?> field) {
		return visibleAllowedFields.isEmpty() || visibleAllowedFields.contains(field);
	}
	
	protected boolean isVisibleAllowed(String propertyId) {
		return isVisibleAllowed(getFieldGroup().getField(propertyId));
	}
	
}
