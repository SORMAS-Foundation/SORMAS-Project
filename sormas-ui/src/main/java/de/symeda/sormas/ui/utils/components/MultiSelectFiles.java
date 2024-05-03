/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.utils.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.email.ExternalBulkEmailOptionsForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class MultiSelectFiles<T> extends CustomField<Set<T>> {

	private VerticalLayout labelLayout = new VerticalLayout();
	private Map<String, T> selectedItemsWithCaption = new HashMap<>();

	public static <T> MultiSelectFiles<T> create(Class<T> clazz) {
		return new MultiSelectFiles<>();
	}

	@Override
	protected Component initContent() {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin(new MarginInfo(false, false, true, false));

		verticalLayout.setSpacing(false);

		labelLayout.setMargin(false);
		labelLayout.setSpacing(false);
		verticalLayout.addComponent(labelLayout);

		return verticalLayout;
	}

	@Override
	public Class<? extends Set<T>> getType() {
		return (Class) Set.class;
	}

	private void listSelectedItems() {
		labelLayout.removeAllComponents();

		for (Map.Entry<String, T> itemEntry : new HashMap<>(selectedItemsWithCaption).entrySet()) {
			HorizontalLayout itemLayout = new HorizontalLayout();
			itemLayout.setMargin(false);
			itemLayout.setWidth("100%");

			Label label = new Label(itemEntry.getKey());
			label.setWidth("100%");
			itemLayout.addComponent(label);

			if (!isReadOnly()) {
				Button removeButton = ButtonHelper.createIconButtonWithCaption(
					null,
					null,
					VaadinIcons.TRASH,
					e -> removeSelectedItem(itemEntry.getValue()),
					ValoTheme.BUTTON_ICON_ONLY,
					ValoTheme.BUTTON_BORDERLESS,
					ValoTheme.BUTTON_ICON_ALIGN_TOP);
				itemLayout.addComponent(removeButton);
			}

			labelLayout.addComponent(itemLayout);
		}
	}

	private void removeSelectedItem(T item) {
		Optional<Map.Entry<String, T>> itemKey =
			selectedItemsWithCaption.entrySet().stream().filter(selection -> selection.getValue().equals(item)).findFirst();

		itemKey.ifPresent(stringTEntry -> selectedItemsWithCaption.remove(stringTEntry.getKey()));

		setValue(new HashSet<>(selectedItemsWithCaption.values()));
		listSelectedItems();
	}

	@Override
	public void setValue(Set<T> newFieldValue, boolean ignoreReadOnly) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue, false, ignoreReadOnly);
	}

	public void addSelectedItemWithCaption(T item, String caption) {

		if (selectedItemsWithCaption.size() <= 5) {
			Map<String, T> selection = new HashMap<>(selectedItemsWithCaption);
			selection.put(caption, item);
			selectedItemsWithCaption.putAll(selection);
			setValue(new HashSet<>(selectedItemsWithCaption.values()));
		} else {
			VaadinUiUtil.createWarningLayout()
				.addComponent(
					new Label(
						String.format(
							I18nProperties.getString(Strings.messageBulkEmailMaxAttachedFiles),
							ExternalBulkEmailOptionsForm.MAX_ATTACHMENT_NUMBER)));
		}
		listSelectedItems();

	}

	public Map<String, T> getSelectedItemsWithCaption() {
		return selectedItemsWithCaption;
	}

}
