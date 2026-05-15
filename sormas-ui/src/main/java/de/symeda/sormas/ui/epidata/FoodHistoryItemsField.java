/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package de.symeda.sormas.ui.epidata;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.epidata.FoodConsumptionItem;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Custom v7 field that renders one row per {@link FoodConsumptionItem}: a checkbox indicating
 * whether the item was consumed, and an inline details text field. The bound value is a
 * {@link Map} keyed by FoodConsumptionItem; presence of a key means the item was consumed,
 * the value (possibly empty) carries any free-text detail. Used inside {@link FoodHistoryForm}.
 */
public class FoodHistoryItemsField extends CustomField<Map<FoodConsumptionItem, String>> {

	private static final long serialVersionUID = 1L;

	private final Map<FoodConsumptionItem, CheckBox> checkBoxes = new LinkedHashMap<>();
	private final Map<FoodConsumptionItem, TextField> detailFields = new LinkedHashMap<>();

	private boolean syncing = false;

	@Override
	protected Component initContent() {

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(new MarginInfo(true, false, true, false));
		layout.setSpacing(true);
		layout.setWidth(100, Unit.PERCENTAGE);

		for (FoodConsumptionItem item : FoodConsumptionItem.values()) {
			HorizontalLayout row = new HorizontalLayout();
			row.setMargin(false);
			row.setSpacing(true);
			row.setWidth(100, Unit.PERCENTAGE);
			row.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

			CheckBox checkBox = new CheckBox(item.toString());
			checkBox.setWidth(180, Unit.PIXELS);
			checkBox.setHeight(37, Unit.PIXELS);
			CssStyles.style(checkBox, CssStyles.LABEL_BOLD);
			checkBox.addValueChangeListener(e -> {
				if (syncing) {
					return;
				}
				updateValueFromUi();
			});
			checkBoxes.put(item, checkBox);

			TextField detailField = new TextField();
			detailField.setWidth(100, Unit.PERCENTAGE);
			detailField.setNullRepresentation("");
			detailField.setInputPrompt(I18nProperties.getString(Strings.promptFoodHistoryItemDetails));
			detailField.setVisible(false);
			detailField.addValueChangeListener(e -> {
				if (syncing) {
					return;
				}
				updateValueFromUi();
			});
			detailFields.put(item, detailField);

			// Toggle details field visibility when the checkbox flips
			checkBox.addValueChangeListener(e -> {
				Boolean checked = (Boolean) e.getProperty().getValue();
				detailField.setVisible(Boolean.TRUE.equals(checked));
				if (Boolean.FALSE.equals(checked)) {
					detailField.setValue(null);
				}
			});

			row.addComponent(checkBox);
			row.addComponent(detailField);
			row.setExpandRatio(checkBox, 0);
			row.setExpandRatio(detailField, 1);
			row.setComponentAlignment(checkBox, Alignment.MIDDLE_LEFT);
			row.setComponentAlignment(detailField, Alignment.MIDDLE_LEFT);
			layout.addComponent(row);
		}

		applyValueToUi(getValue());
		return layout;
	}

	@Override
	public void setValue(Map<FoodConsumptionItem, String> newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		applyValueToUi(newFieldValue);
	}

	private void applyValueToUi(Map<FoodConsumptionItem, String> value) {
		if (checkBoxes.isEmpty()) {
			// Layout not initialized yet
			return;
		}
		syncing = true;
		try {
			for (FoodConsumptionItem item : FoodConsumptionItem.values()) {
				boolean consumed = value != null && value.containsKey(item);
				CheckBox checkBox = checkBoxes.get(item);
				TextField detailField = detailFields.get(item);
				checkBox.setValue(consumed);
				detailField.setVisible(consumed);
				detailField.setValue(consumed ? value.get(item) : null);
			}
		} finally {
			syncing = false;
		}
	}

	private void updateValueFromUi() {
		Map<FoodConsumptionItem, String> newValue = new EnumMap<>(FoodConsumptionItem.class);
		for (FoodConsumptionItem item : FoodConsumptionItem.values()) {
			if (Boolean.TRUE.equals(checkBoxes.get(item).getValue())) {
				String detail = detailFields.get(item).getValue();
				newValue.put(item, detail != null ? detail : "");
			}
		}

		super.setValue(newValue);
	}

	@Override
	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	public Class<? extends Map<FoodConsumptionItem, String>> getType() {
		return (Class) Map.class;
	}
}
