/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.configuration.customizablefield;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Vaadin 8 component for editing the selectable options of list-type customizable fields
 * ({@code COMBOBOX}, {@code CHECKBOX_LIST}, {@code RADIO_BUTTON_LIST}).
 * <p>
 * Renders one row per option: [option value text field] [🗑]
 * Reads/writes {@code List<String>}.
 */
@SuppressWarnings({
    "java:S110", // suppress sonar too many parents warning
    "java:S2160" // suppress missing equals
})
public class CustomizableFieldOptionsComponent extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private final VerticalLayout rowsLayout;
    private final List<OptionRow> rows = new ArrayList<>();
    private final List<Runnable> changeListeners = new ArrayList<>();
    private final Label lblNoOptions;

    public CustomizableFieldOptionsComponent() {

        setWidthFull();
        setMargin(new MarginInfo(false, false, true, false));
        setSpacing(false);
        CssStyles.style(this, CssStyles.VSPACE_TOP_4);

        lblNoOptions = new Label(I18nProperties.getString(Strings.infoNoCustomizableFieldOptions));
        addComponent(lblNoOptions);

        rowsLayout = new VerticalLayout();
        rowsLayout.setWidthFull();
        rowsLayout.setMargin(false);
        rowsLayout.setSpacing(false);
        addComponent(rowsLayout);

        Button btnAdd = ButtonHelper.createIconButtonWithCaption(null, null, VaadinIcons.PLUS, e -> addRow(null, true), CssStyles.VSPACE_TOP_5);
        btnAdd.setHeight(25, Unit.PIXELS);
        btnAdd.setWidthFull();
        addComponent(btnAdd);
    }

    public void setValue(List<String> options) {

        rows.clear();
        rowsLayout.removeAllComponents();

        if (options != null) {
            options.forEach(option -> addRow(option, false));
            rows.forEach(rowsLayout::addComponent);
        }

        updateNoOptionsLabelVisibility();
    }

    /**
     * Returns the current list of options, or {@code null} if there are none.
     * Blank entries are skipped.
     */
    @SuppressWarnings("java:S1168")
    public List<String> getValue() {

        if (rows.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (OptionRow row : rows) {
            String value = row.getValue();
            if (value != null && !value.trim().isEmpty()) {
                result.add(value.trim());
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * Registers a listener that is called whenever the options list changes
     * (row added, deleted, or option text edited).
     */
    public void addOptionsChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void fireChangeListeners() {
        changeListeners.forEach(Runnable::run);
    }

    private void addRow(String value, boolean render) {

        OptionRow row = new OptionRow(value);
        row.setDeleteCallback(() -> {
            rows.remove(row);
            rowsLayout.removeComponent(row);
            updateNoOptionsLabelVisibility();
            fireChangeListeners();
        });
        row.setChangeCallback(this::fireChangeListeners);
        rows.add(row);
        updateNoOptionsLabelVisibility();
        if (render) {
            rowsLayout.addComponent(row);
            fireChangeListeners();
        }
    }

    private void updateNoOptionsLabelVisibility() {

        if (lblNoOptions != null) {
            lblNoOptions.setVisible(rows.isEmpty());
        }
    }

    @SuppressWarnings({
        "java:S110", // suppress sonar too many parents warning
        "java:S2160" // suppress missing equals
    })
    private static final class OptionRow extends HorizontalLayout {

        private static final long serialVersionUID = 1L;

        private final TextField tfValue;
        private transient Runnable deleteCallback;
        private transient Runnable changeCallback;

        public OptionRow(String value) {

            tfValue = new TextField();
            tfValue.setWidthFull();
            tfValue.setPlaceholder(I18nProperties.getString(Strings.promptCustomizableFieldOption));
            if (value != null) {
                tfValue.setValue(value);
            }
            tfValue.addValueChangeListener(e -> {
                if (changeCallback != null) {
                    changeCallback.run();
                }
            });

            CssStyles.style(CssStyles.VSPACE_NONE, tfValue);

            Button btnDelete = ButtonHelper.createIconButtonWithCaption(null, null, VaadinIcons.TRASH, e -> deleteCallback.run());

            addComponent(tfValue);
            addComponent(btnDelete);
            setExpandRatio(tfValue, 1);
            setWidthFull();
            setMargin(false);
        }

        public String getValue() {
            return tfValue.getValue();
        }

        public void setDeleteCallback(Runnable callback) {
            this.deleteCallback = callback;
        }

        public void setChangeCallback(Runnable callback) {
            this.changeCallback = callback;
        }
    }
}
