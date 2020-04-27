package de.symeda.sormas.ui.utils;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.*;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;

public abstract class AbstractFilterForm<T> extends AbstractForm<T> {

	protected AbstractFilterForm(Class<T> type, String propertyI18nPrefix, UserRight editOrCreateUserRight, boolean addFields) {
		super(type, propertyI18nPrefix, editOrCreateUserRight, addFields);
	}

	@Override
	protected <T1 extends Field> void formatField(T1 field, String propertyId) {
		super.formatField(field, propertyId);

		field.addStyleName("filter-item");

		String caption = I18nProperties.getPrefixCaption(propertyI18nPrefix, propertyId, field.getCaption());
		setFieldCaption(field, caption);

		if (TextField.class.isAssignableFrom(field.getClass())) {
			((TextField) field).addTextChangeListener(e -> field.setValue(e.getText()));
		}

		field.addValueChangeListener(e -> {
			onFieldValueChange(propertyId, e);
		});
	}

	private <T1 extends Field> void setFieldCaption(T1 field, String caption) {
		field.setCaption(null);
		if (field instanceof ComboBox) {
			((ComboBox) field).setInputPrompt(caption);
		} else if (field instanceof AbstractTextField) {
			((AbstractTextField) field).setInputPrompt(caption);
		} else if (field instanceof PopupDateField) {
			((PopupDateField) field).setInputPrompt(caption);
		} else {
			field.setCaption(caption);
		}
	}

	@Override
	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		super.applyFieldConfiguration(configuration, field);

		if (configuration.getCaption() != null) {
			setFieldCaption(field, configuration.getCaption());
		}
	}

	private boolean skipChangeEvents;

	@Override
	public void setValue(T newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		doWithoutChangeHandler(() -> super.setValue(newFieldValue));
	}

	private void onFieldValueChange(String propertyId, Property.ValueChangeEvent event) {
		if (!skipChangeEvents) {
			try {
				doWithoutChangeHandler(() -> applyDependenciesOnFieldChange(propertyId, event));

				this.getFieldGroup().commit();
				this.fireValueChange(true);
			} catch (FieldGroup.CommitException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {

	}

	private void doWithoutChangeHandler(Callable callback) {
		this.skipChangeEvents = true;
		try {
			callback.call();
		} finally {
			this.skipChangeEvents = false;
		}
	}

	interface Callable {
		void call();
	}
}
