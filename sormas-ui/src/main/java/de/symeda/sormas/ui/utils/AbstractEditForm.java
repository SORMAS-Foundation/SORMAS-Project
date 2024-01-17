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

package de.symeda.sormas.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.components.NotBlankTextValidator;

public abstract class AbstractEditForm<DTO> extends AbstractForm<DTO> implements FieldGroup.CommitHandler {// implements DtoEditForm<DTO> {

	private static final long serialVersionUID = 1L;

	protected final FieldVisibilityCheckers fieldVisibilityCheckers;
	protected final UiFieldAccessCheckers fieldAccessCheckers;

	private boolean hideValidationUntilNextCommit = false;
	private List<Field<?>> visibleAllowedFields = new ArrayList<>();
	private boolean visibilitiesInitialized;
	private List<Field<?>> editableAllowedFields = new ArrayList<>();
	private boolean fieldAccessesInitialized;

	private ComboBox diseaseField;
	private boolean setServerDiseaseAsDefault;

	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix) {
		this(type, propertyI18nPrefix, true, null, null);
	}

	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix, boolean addFields) {
		this(type, propertyI18nPrefix, addFields, null, null);
	}

	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix, FieldVisibilityCheckers fieldVisibilityCheckers) {
		this(type, propertyI18nPrefix, true, fieldVisibilityCheckers, null);
	}

	protected AbstractEditForm(Class<DTO> type, String propertyI18nPrefix, boolean addFields, FieldVisibilityCheckers fieldVisibilityCheckers) {
		this(type, propertyI18nPrefix, addFields, fieldVisibilityCheckers, null);
	}

	protected AbstractEditForm(
		Class<DTO> type,
		String propertyI18nPrefix,
		boolean addFields,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers) {

		super(type, propertyI18nPrefix, new SormasFieldGroupFieldFactory(fieldVisibilityCheckers, fieldAccessCheckers), false);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
		this.fieldAccessCheckers = fieldAccessCheckers;

		getFieldGroup().addCommitHandler(this);
		setWidth(900, Unit.PIXELS);

		if (addFields) {
			addFields();
		}
	}

	protected AbstractEditForm(
		Class<DTO> type,
		String propertyI18nPrefix,
		boolean addFields,
		FieldVisibilityCheckers fieldVisibilityCheckers,
		UiFieldAccessCheckers fieldAccessCheckers,
		boolean isEditAllowed) {

		super(type, propertyI18nPrefix, new SormasFieldGroupFieldFactory(fieldVisibilityCheckers, fieldAccessCheckers, isEditAllowed), false);
		this.fieldVisibilityCheckers = fieldVisibilityCheckers;
		this.fieldAccessCheckers = fieldAccessCheckers;

		getFieldGroup().addCommitHandler(this);
		setWidth(900, Unit.PIXELS);

		if (addFields) {
			addFields();
		}
	}

	@Override
	public void setValue(DTO newFieldValue) throws com.vaadin.v7.data.Property.ReadOnlyException, ConversionException {
		super.setValue(newFieldValue);
		// this method should only be called once upon initializing the form, thus allowing us to set the default disease here
		if (diseaseField != null && diseaseField.getValue() == null && setServerDiseaseAsDefault) {
			setDefaultDiseaseValue();
		}
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

		List<Field<?>> customFields = getCustomFields();

		if (hideValidationUntilNextCommit) {
			hideValidationUntilNextCommit = false;
			for (Field<?> field : getFieldGroup().getFields()) {
				if (field instanceof AbstractField) {
					AbstractField<?> abstractField = (AbstractField<?>) field;
					abstractField.setValidationVisible(true);
				}
			}

			for (Field<?> field : customFields) {
				if (field instanceof AbstractField) {
					AbstractField<?> abstractField = (AbstractField<?>) field;
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
			} else if (e.getCause() instanceof InvalidValueException) {
				throw (InvalidValueException) e.getCause();
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

	protected ComboBox addDiseaseField(String fieldId, boolean showNonPrimaryDiseases) {
		return addDiseaseField(fieldId, showNonPrimaryDiseases, false);
	}

	/**
	 * Adds the field to the form by using addField(fieldId, fieldType), but additionally sets up a ValueChangeListener
	 * that makes sure the value that is about to be selected is added to the list of allowed values. This is intended
	 * to be used for Disease fields that might contain a disease that is no longer active in the system and thus will
	 * not be returned by DiseaseHelper.isActivePrimaryDisease(disease).
	 * 
	 * @param showNonPrimaryDiseases
	 *            Whether or not diseases that have been configured as non-primary should be included
	 * @param setServerDiseaseAsDefault
	 *            If only a single diseases is active on the server, set it as the default value
	 */
	@SuppressWarnings("unchecked")
	protected ComboBox addDiseaseField(String fieldId, boolean showNonPrimaryDiseases, boolean setServerDiseaseAsDefault) {

		diseaseField = addField(fieldId, ComboBox.class);
		this.setServerDiseaseAsDefault = setServerDiseaseAsDefault;
		if (showNonPrimaryDiseases) {
			addNonPrimaryDiseasesTo(diseaseField);
		}

		if (setServerDiseaseAsDefault) {
			setDefaultDiseaseValue();
		}

		// Make sure that the ComboBox still contains a pre-selected inactive disease
		diseaseField.addValueChangeListener(e -> {
			Object value = e.getProperty().getValue();
			if (value != null && !diseaseField.containsId(value)) {
				Item newItem = diseaseField.addItem(value);
				newItem.getItemProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID).setValue(value.toString());
			}
		});
		return diseaseField;
	}

	/**
	 * If the server is only configured for one disease, automatically set this as the default value of the disease field.
	 * Disease.OTHER and Disease.UNDEFINED are not counted as disease
	 */
	private void setDefaultDiseaseValue() {
		Disease defaultDisease = FacadeProvider.getDiseaseConfigurationFacade().getDefaultDisease();
		if (defaultDisease != null) {
			diseaseField.setValue(defaultDisease);
		} else if (diseaseField.getItemIds().size() == 1) {
			diseaseField.setValue(diseaseField.getItemIds().stream().findFirst().get());
		}
	}

	protected ComboBox addInfrastructureField(String fieldId) {
		return addInfrastructureField(fieldId, true);
	}

	protected ComboBox addInfrastructureField(String fieldId, boolean showInactiveTag) {
		ComboBox field = addField(fieldId, ComboBox.class);
		// Make sure that the ComboBox still contains a pre-selected inactive infrastructure entity
		field.addValueChangeListener(e -> {
			InfrastructureDataReferenceDto value = (InfrastructureDataReferenceDto) e.getProperty().getValue();
			if (value != null && !field.containsId(value)) {
				InfrastructureDataReferenceDto inactiveValue = value.clone();
				if (showInactiveTag) {
					inactiveValue.setCaption(value.getCaption() + " (" + I18nProperties.getString(Strings.inactive) + ")");
				} else {
					inactiveValue.setCaption(value.getCaption());
				}
				field.addItem(inactiveValue);
			}
		});
		return field;
	}

	protected ComboBox addCustomizableEnumField(String fieldId) {
		ComboBox field = addField(fieldId, ComboBox.class);
		// Make sure that the ComboBox still contains a pre-selected inactive customizable enum
		field.addValueChangeListener(e -> {
			CustomizableEnum value = (CustomizableEnum) e.getProperty().getValue();
			if (value != null && !field.containsId(value)) {
				CustomizableEnum inactiveValue = value.clone();
				inactiveValue.setCaption(value.getCaption() + " (" + I18nProperties.getString(Strings.inactive) + ")");
				field.addItem(inactiveValue);
			}
		});
		return field;
	}

	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	@Override
	protected <T extends Field> T addField(String propertyId) {
		return (T) addField(propertyId, Field.class);
	}

	/**
	 * @param allowedDaysInFuture
	 *            How many days in the future the value of this field can be or
	 *            -1 for no restriction at all
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected <T extends Field> T addDateField(String propertyId, Class<T> fieldType, int allowedDaysInFuture) {
		T field = createField(propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		getContent().addComponent(field, propertyId);
		addFutureDateValidator(field, allowedDaysInFuture);
		return field;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected <T extends Field> void formatField(T field, String propertyId) {
		formatField(field, propertyId, I18nProperties.getPrefixCaption(propertyI18nPrefix, propertyId, field.getCaption()));
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected <T extends Field> void formatField(T field, String propertyId, String customCaption) {

		super.formatField(field, propertyId);

		field.setCaption(customCaption);

		if (field instanceof AbstractField) {
			AbstractField<?> abstractField = (AbstractField) field;
			abstractField.setDescription(I18nProperties.getPrefixDescription(propertyI18nPrefix, propertyId, abstractField.getDescription()));

			if (hideValidationUntilNextCommit && !abstractField.isInvalidCommitted()) {
				abstractField.setValidationVisible(false);
			}
		}

		String validationError = I18nProperties.getPrefixValidationError(propertyI18nPrefix, propertyId, customCaption);
		field.setRequiredError(validationError);

		field.setWidth(100, Unit.PERCENTAGE);
	}

	protected void styleAsOptionGroupHorizontal(List<String> fields) {
		for (String field : fields) {
			CssStyles.style((NullableOptionGroup) getFieldGroup().getField(field), ValoTheme.OPTIONGROUP_HORIZONTAL);
		}
	}

	protected void setReadOnly(boolean readOnly, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			if (readOnly || isEditableAllowed(propertyId)) {
				getField(propertyId).setReadOnly(readOnly);
			}
		}
	}

	protected void setEnabled(boolean enabled, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			if (enabled || isEditableAllowed(propertyId)) {
				getField(propertyId).setEnabled(enabled);
			}
		}
	}

	@Override
	public void clear() {
		// clear the fields instead of the form itself
		for (Field<?> field : getFieldGroup().getFields()) {
			field.clear();
		}
	}

	protected void setVisible(boolean visible, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			if (!visible || isVisibleAllowed(propertyId)) {
				getField(propertyId).setVisible(visible);
			}
		}
	}

	protected void setVisible(boolean visible, Field<?>... fields) {

		for (Field<?> field : fields) {
			if (!visible || isVisibleAllowed(field)) {
				field.setVisible(visible);
			}
		}
	}

	protected void setVisibleClear(boolean visible, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			if (!visible || isVisibleAllowed(propertyId)) {
				Field<?> field = getField(propertyId);
				if (!visible) {
					field.clear();
				}
				field.setVisible(visible);
			}
		}
	}

	protected void discard(String... propertyIds) {

		for (String propertyId : propertyIds) {
			getField(propertyId).discard();
		}
	}

	protected void setRequired(boolean required, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			if (!required || isEditableAllowed(propertyId)) {
				Field<?> field = getField(propertyId);
				if (!field.isReadOnly()) {
					field.setRequired(required);
					if (TextField.class.isAssignableFrom(field.getClass())) {
						if (required) {
							field.addValidator(new NotBlankTextValidator(I18nProperties.getRequiredError(field.getCaption())));
						} else {
							final Collection<Validator> validators = field.getValidators();
							final Optional<Validator> first = validators.stream()
								.filter(validator -> validator.getClass().isAssignableFrom(NotBlankTextValidator.class))
								.findFirst();
							first.ifPresent(field::removeValidator);
						}
					}
				}
			}
		}
	}

	protected void setSoftRequired(boolean required, String... fieldOrPropertyIds) {

		for (String propertyId : fieldOrPropertyIds) {
			Field<?> field = getField(propertyId);
			if (required) {
				FieldHelper.addSoftRequiredStyle(field);
			} else {
				FieldHelper.removeSoftRequiredStyle(field);
			}
		}
	}

	protected void addFieldListeners(String fieldOrPropertyId, ValueChangeListener... listeners) {

		for (ValueChangeListener listener : listeners) {
			getField(fieldOrPropertyId).addValueChangeListener(listener);
		}
	}

	protected void addValidators(String fieldOrPropertyId, Validator... validators) {

		for (Validator validator : validators) {
			getField(fieldOrPropertyId).addValidator(validator);
		}
	}

	protected boolean areFieldsValid(String... propertyIds) {
		return Stream.of(propertyIds).allMatch(p -> getField(p).isValid());
	}

	@Override
	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}

	public void hideValidationUntilNextCommit() {

		this.hideValidationUntilNextCommit = true;

		for (Field<?> field : getFieldGroup().getFields()) {
			if (field instanceof AbstractField) {
				AbstractField<?> abstractField = (AbstractField<?>) field;
				if (!abstractField.isInvalidCommitted()) {
					abstractField.setValidationVisible(false);
				}
			}
		}

		for (Field<?> field : getCustomFields()) {
			if (field instanceof AbstractField) {
				AbstractField<?> abstractField = (AbstractField<?>) field;
				if (!abstractField.isInvalidCommitted()) {
					abstractField.setValidationVisible(false);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void addNonPrimaryDiseasesTo(ComboBox diseaseField) {

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, false, true);
		for (Disease disease : diseases) {
			if (diseaseField.getItem(disease) != null) {
				continue;
			}

			Item newItem = diseaseField.addItem(disease);
			newItem.getItemProperty(SormasFieldGroupFieldFactory.CAPTION_PROPERTY_ID).setValue(disease.toString());
		}
	}

	/**
	 * Sets the initial visibilities based on annotations and builds a list of all fields in a form that are allowed to be visible -
	 * this is either because the @Diseases and @Outbreaks annotations are not relevant or at least one of these annotations are present on
	 * the respective field.
	 */
	protected void initializeVisibilitiesAndAllowedVisibilities() {

		if (fieldVisibilityCheckers == null) {
			throw new RuntimeException("Visibility checker is not set!");
		}

		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			Field<?> field = getFieldGroup().getField(propertyId);

			if (fieldVisibilityCheckers.isVisible(getType(), propertyId.toString())) {
				visibleAllowedFields.add(field);
			} else {
				field.setVisible(false);
			}
		}

		visibilitiesInitialized = true;
	}

	/**
	 * Returns true if the visibleAllowedFields list is either empty (because all fields are allowed to be visible) or contains
	 * the given field. This needs to be called before EVERY setVisible or setVisibleWhen call.
	 */
	protected boolean isVisibleAllowed(Field<?> field) {
		return !visibilitiesInitialized || visibleAllowedFields.contains(field);
	}

	protected boolean isVisibleAllowed(String propertyId) {
		return isVisibleAllowed(getFieldGroup().getField(propertyId));
	}

	/**
	 * Sets the initial enabled states based on annotations and builds a list of all fields in a form
	 * that are allowed to be enabled based on access rights
	 */
	protected void initializeAccessAndAllowedAccesses() {

		if (fieldAccessCheckers == null) {
			throw new RuntimeException("Access checker is not set!");
		}
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			Field<?> field = getFieldGroup().getField(propertyId);

			if (fieldAccessCheckers.isAccessible(getType(), propertyId.toString())) {
				editableAllowedFields.add(field);
			} else {
				field.setEnabled(false);
				field.setRequired(false);
				field.addStyleName(CssStyles.INACCESSIBLE_FIELD);

				if (field instanceof AbstractTextField) {
					((AbstractTextField) field).setInputPrompt(I18nProperties.getCaption(Captions.inaccessibleValue));
				}

				if (field instanceof ComboBoxWithPlaceholder) {
					FieldHelper.setComboInaccessible((ComboBoxWithPlaceholder) field);
				}
			}
		}

		fieldAccessesInitialized = true;
	}

	/**
	 * Returns true if the enabledAllowedFields list is either empty (because all fields are allowed to be enabled) or contains
	 * the given field. This needs to be called before EVERY setEnabled or setEnabledWhen call.
	 */
	protected boolean isEditableAllowed(Field<?> field) {
		return !fieldAccessesInitialized || editableAllowedFields.contains(field);
	}

	protected boolean isEditableAllowed(String propertyId) {
		return isEditableAllowed(getFieldGroup().getField(propertyId));
	}

	public void setHeading(String heading) {
		throw new RuntimeException("setHeading should be implemented in " + getClass().getSimpleName());
	}
}
