/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.system;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.util.ReflectTools;

import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDataProvider;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;

/**
 * Custom field for dynamic input of system configuration values.
 * The component will render a text field, drop down, password field, or checkbox grid depending on the
 * {@link SystemConfigurationValueDataProvider} if any is available.
 * The default is to render a text field.
 */
public class SystemConfigurationValueDynamicInput extends CustomField<SystemConfigurationValueDto> {

    /**
     * Enum representing the types of input values.
     */
    public enum ValueTypes {
        TEXT,
        PASSWORD,
        DROPDOWN,
        CHECKBOX_GRID
    }
    /**
     * Interface for listening to value change events.
     */
    public interface ValueChangeListener {

        Method VALUE_CHANGE_METHOD = ReflectTools.findMethod(ValueChangeListener.class, "valueChange", ValueChangeEvent.class);

        /**
         * Called when the value changes.
         *
         * @param event
         *            the value change event
         */
        void valueChange(ValueChangeEvent event);
    }
    /**
     * Event representing a change in value.
     */
    public static class ValueChangeEvent extends Component.Event {

        /**
         * Constructs a new ValueChangeEvent.
         *
         * @param source
         *            the source component
         */
        public ValueChangeEvent(final Component source) {
            super(source);
        }
    }

    private static final String STYLE_NAME_DYNAMIC_INPUT = "system-configuration-dynamic-input";

    private static final String STYLE_NAME_TOGGLE_PASSWORD = "toggle-password";

    private static final String STYLE_NAME_WRAPPING_CHECKBOX_GROUP = "wrapping-checkbox-group";

    private static final long serialVersionUID = 1L;

    private SystemConfigurationValueDto value;

    @Override
    public SystemConfigurationValueDto getValue() {
        return value;
    }

    @Override
    public void doSetValue(final SystemConfigurationValueDto newFieldValue) {

        Objects.requireNonNull(newFieldValue);
        this.value = newFieldValue;
    }

    /**
     * Adds a value change listener.
     *
     * @param listener
     *            the listener to add
     */
    public void addValueChangeListener(final ValueChangeListener listener) {
        addListener(ValueChangeEvent.class, listener, ValueChangeListener.VALUE_CHANGE_METHOD);
    }

    /**
     * Determines the type of value input.
     *
     * @return the value type
     */
    public ValueTypes getValueType() {

        final var dataProvider = getValue().getDataProvider();

        if (null != dataProvider) {

            final var options = dataProvider.getOptions().values();
            final var keys = dataProvider.getKeys();

            if (keys.size() > 1) {
                return ValueTypes.CHECKBOX_GRID;
            }

            if (keys.size() == 1 && options.size() > 1) {
                return ValueTypes.DROPDOWN;
            }
        }

        if (Boolean.TRUE.equals(getValue().getEncrypt())) {
            return ValueTypes.PASSWORD;
        }

        return ValueTypes.TEXT;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Component initContent() {

        addStyleName(STYLE_NAME_DYNAMIC_INPUT);

        final var mainLayout = new VerticalLayout();

        mainLayout.setSpacing(true);
        mainLayout.setMargin(false);
        mainLayout.setSizeUndefined();
        mainLayout.setWidthFull();

        switch (getValueType()) {
        case DROPDOWN:
            mainLayout.addComponent(createDropdown());
            break;
        case CHECKBOX_GRID:
            mainLayout.addComponent(createCheckboxGrid());
            break;
        case PASSWORD:
            mainLayout.addComponent(createPasswordField());
            break;
        default:
            mainLayout.addComponent(createTextField());
        }

        return mainLayout;
    }

    /**
     * Creates a text field for input.
     *
     * @return the text field
     */
    private TextField createTextField() {

        final var field = new TextField();
        field.setValue(getValue().getValue());
        field.addValueChangeListener(event -> fireValueChange(field));
        field.setWidthFull();
        return field;
    }

    /**
     * Creates a password field for input.
     *
     * @return the password field
     */
    private Component createPasswordField() {

        final var layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.addStyleName(STYLE_NAME_TOGGLE_PASSWORD);
        final var passwordField = new PasswordField();
        passwordField.setValue(getValue().getValue());
        passwordField.setWidthFull();

        final var textField = new TextField();
        textField.setValue(getValue().getValue());
        textField.setWidthFull();
        textField.setVisible(false);
        passwordField.setValue(getValue().getValue());

        final var toggleButton = new Button();
        toggleButton.setIcon(VaadinIcons.EYE_SLASH);

        passwordField.addValueChangeListener(event -> {
            textField.setValue(passwordField.getValue());
            fireValueChange(passwordField);
        });
        textField.addValueChangeListener(event -> {
            passwordField.setValue(event.getValue());
            fireValueChange(passwordField);
        });

        toggleButton.addClickListener(event -> {
            if (passwordField.isVisible()) {
                passwordField.setVisible(false);
                textField.setVisible(true);
                textField.setValue(passwordField.getValue());
                toggleButton.setIcon(VaadinIcons.EYE);
            } else {
                passwordField.setVisible(true);
                textField.setVisible(false);
                passwordField.setValue(textField.getValue());
                toggleButton.setIcon(VaadinIcons.EYE_SLASH);
            }
        });

        layout.addComponents(passwordField, textField, toggleButton);
        return layout;
    }

    /**
     * Creates a dropdown for input.
     *
     * @return the dropdown
     */
    private ComboBox<String> createDropdown() {

        final var combo = new ComboBox<String>();
        combo.setEmptySelectionAllowed(false);
        combo.setItems(getValue().getDataProvider().getOptions().values());
        combo.setSelectedItem(getValue().getValue());
        combo.addValueChangeListener(event -> fireValueChange(combo));
        combo.setWidthFull();
        return combo;
    }

    /**
     * Creates a checkbox grid for input.
     *
     * @return the checkbox grid
     */
    private CheckBoxGroup<String> createCheckboxGrid() {

        final var cbg = new CheckBoxGroup<String>();
        cbg.setItems(getValue().getDataProvider().getOptions().values());
        cbg.select(getValue().getDataProvider().getMappedValues(getValue()).values().toArray(new String[] {}));
        cbg.addValueChangeListener(event -> fireValueChange(cbg));
        cbg.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        cbg.addStyleName(STYLE_NAME_WRAPPING_CHECKBOX_GROUP);
        cbg.setWidthFull();
        return cbg;
    }

    /**
     * Fires a value change event.
     *
     * @param source
     *            the source component
     */
    @SuppressWarnings("unchecked")
    private void fireValueChange(final Component source) {

        if (source instanceof TextField) {
            getValue().setValue(((TextField) source).getValue());
        }

        if (source instanceof PasswordField) {
            getValue().setValue(((PasswordField) source).getValue());
        }

        if (source instanceof ComboBox) {
            getValue().getDataProvider().getKeys().stream().findFirst().ifPresent(k -> {
                final HashMap<String, String> values = new HashMap<>(1);
                values.put(k, ((ComboBox<String>) source).getValue());
                getValue().getDataProvider().applyValues(values, getValue());
            });
        }

        if (source instanceof CheckBoxGroup) {
            final HashMap<String, String> selectedValues = new HashMap<>(getValue().getDataProvider().getKeys().size());
            ((CheckBoxGroup<String>) source).getSelectedItems()
                .forEach(
                    v -> getValue().getDataProvider()
                        .getOptions()
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().equals(v))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .ifPresent(k -> selectedValues.put(k, v)));
            getValue().getDataProvider().applyValues(selectedValues, getValue());
        }

        fireEvent(new ValueChangeEvent(this));
    }
}
