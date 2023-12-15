/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.Select;

import de.symeda.sormas.ui.utils.ButtonHelper;

public class MultiSelect<T> extends CustomField<Set<T>> {

    private Select selectComponent = new Select();
    private Collection<T> items;
    private VerticalLayout labelLayout = new VerticalLayout();
    private Map<T, String> selectedItemsWithCaption = new HashMap<>();
    private Map<T, Label> selectedItemsWithLabelComponent = new HashMap<>();

    public static <T> MultiSelect<T> create(Class<T> clazz) {
        return new MultiSelect<>();
    }

    @Override
    protected Component initContent() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(new MarginInfo(false, false, true, false));

        selectComponent.setWidth("100%");

        selectComponent.addValueChangeListener((ValueChangeListener) event -> {
            T item = (T) selectComponent.getValue();
            if (item == null) {
                return;
            }

            String caption = selectComponent.getItemCaption(item);
            selectedItemsWithCaption.put(item, caption);
            setValue(new HashSet<>(selectedItemsWithCaption.keySet()));

            selectComponent.setValue(null);
            updateItemsOnSelectionChange();

            listSelectedItems();
        });

        verticalLayout.addComponent(selectComponent);
        verticalLayout.setSpacing(false);

        if (isReadOnly()) {
            selectComponent.setVisible(false);
            selectComponent.setReadOnly(true);
        }

        labelLayout.setMargin(false);
        labelLayout.setSpacing(false);
        verticalLayout.addComponent(labelLayout);

        initFromCurrentValue();

        return verticalLayout;
    }

    @Override
    public Class<? extends Set<T>> getType() {
        return (Class) Set.class;
    }

    public void setItems(Collection<T> items) {
        this.items = items;

        selectedItemsWithCaption.clear();
        selectComponent.removeAllItems();

        selectComponent.addItems(items);
        listSelectedItems();
    }

    public void addItem(T item) {
        items.add(item);
        selectComponent.addItem(item);
    }

    public void setItemCaption(T item, String caption) {
        selectComponent.setItemCaption(item, caption);

        if (selectedItemsWithCaption.containsKey(item)) {
            selectedItemsWithCaption.put(item, caption);
        }
        if (selectedItemsWithLabelComponent.get(item) != null) {
            selectedItemsWithLabelComponent.get(item).setValue(caption);
        }
    }

    public void removeAllItems() {
        selectComponent.removeAllItems();
        selectedItemsWithCaption.clear();

        listSelectedItems();
    }

    private void initFromCurrentValue() {
        if (getValue() == null) {
            return;
        }

        for (T item : getValue()) {
            String caption = selectComponent.getItemCaption(item);
            selectedItemsWithCaption.put(item, caption != null ? caption : "");
        }

        listSelectedItems();
    }

    private void listSelectedItems() {
        selectedItemsWithLabelComponent.clear();
        labelLayout.removeAllComponents();

        for (Map.Entry<T, String> itemEntry : new HashMap<>(selectedItemsWithCaption).entrySet()) {
            HorizontalLayout itemLayout = new HorizontalLayout();
            itemLayout.setMargin(false);
            itemLayout.setWidth("100%");

            Label label = new Label(itemEntry.getValue());
            label.setWidth("100%");
            itemLayout.addComponent(label);

            if (!isReadOnly()) {
                Button removeButton = ButtonHelper.createIconButtonWithCaption(
                        null,
                        null,
                        VaadinIcons.TRASH,
                        e -> removeSelectedItem(itemEntry.getKey()),
                        ValoTheme.BUTTON_ICON_ONLY,
                        ValoTheme.BUTTON_BORDERLESS,
                        ValoTheme.BUTTON_ICON_ALIGN_TOP);
                itemLayout.addComponent(removeButton);
            }

            labelLayout.addComponent(itemLayout);

            selectedItemsWithLabelComponent.put(itemEntry.getKey(), label);
        }
    }

    private void removeSelectedItem(T item) {
        selectedItemsWithCaption.remove(item);
        setValue(new HashSet<>(selectedItemsWithCaption.keySet()));
        updateItemsOnSelectionChange();

        listSelectedItems();
    }

    @Override
    public void setValue(Set<T> newFieldValue, boolean ignoreReadOnly) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newFieldValue, false, ignoreReadOnly);
    }

    private void updateItemsOnSelectionChange() {
        selectComponent.removeAllItems();
        selectComponent.addItems(items.stream().filter(item -> !selectedItemsWithCaption.containsKey(item)).collect(Collectors.toList()));
    }
}
