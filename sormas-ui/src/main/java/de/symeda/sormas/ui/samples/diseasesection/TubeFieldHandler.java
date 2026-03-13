/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Collection;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

/**
 * Standalone helper that manages the 4 IGRA tube field pairs for TB sections.
 * Each pair consists of a numeric {@link TextField} and a GT10 {@link NullableOptionGroup} checkbox.
 * The two fields are kept in sync: the numeric value auto-checks GT10 when > 10,
 * and the checkbox clears the numeric value if they contradict.
 */
public class TubeFieldHandler {

	private final FieldGroup fieldGroup;
	private final CustomLayout panel;

	public TubeFieldHandler(FieldGroup fieldGroup, CustomLayout panel) {
		this.fieldGroup = fieldGroup;
		this.panel = panel;
	}

	/** Builds and binds all 4 tube pairs and wires their mutual listeners. */
	public void attachTubeFields() {
		addTubePair(PathogenTestDto.TUBE_NIL, PathogenTestDto.TUBE_NIL_GT10);
		addTubePair(PathogenTestDto.TUBE_AG_TB1, PathogenTestDto.TUBE_AG_TB1_GT10);
		addTubePair(PathogenTestDto.TUBE_AG_TB2, PathogenTestDto.TUBE_AG_TB2_GT10);
		addTubePair(PathogenTestDto.TUBE_MITOGENE, PathogenTestDto.TUBE_MITOGENE_GT10);
	}

	/** Removes all 4 tube pairs from the panel and unbinds them from the field group. */
	public void detachTubeFields() {
		detachTubePair(PathogenTestDto.TUBE_NIL, PathogenTestDto.TUBE_NIL_GT10);
		detachTubePair(PathogenTestDto.TUBE_AG_TB1, PathogenTestDto.TUBE_AG_TB1_GT10);
		detachTubePair(PathogenTestDto.TUBE_AG_TB2, PathogenTestDto.TUBE_AG_TB2_GT10);
		detachTubePair(PathogenTestDto.TUBE_MITOGENE, PathogenTestDto.TUBE_MITOGENE_GT10);
	}

	private void addTubePair(String numericId, String gt10Id) {
		TextField numericField = fieldGroup.buildAndBind(numericId, (Object) numericId, TextField.class);
		numericField.setId(numericId);
		numericField.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, numericId, numericField.getCaption()));
		numericField.setConversionError(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, numericField.getCaption()));
		CssStyles.style(numericField, CssStyles.CAPTION_ON_TOP);
		numericField.setWidth("100%");
		numericField.setVisible(false);
		panel.addComponent(numericField, numericId);

		NullableOptionGroup gt10Field = fieldGroup.buildAndBind(gt10Id, (Object) gt10Id, NullableOptionGroup.class);
		gt10Field.setId(gt10Id);
		gt10Field.setCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, gt10Id, gt10Field.getCaption()));
		CssStyles.style(gt10Field, CssStyles.CAPTION_ON_TOP);
		gt10Field.setWidth("100%");
		gt10Field.setVisible(false);
		panel.addComponent(gt10Field, gt10Id);

		// numeric → auto-check GT10 when value > 10
		numericField.addValueChangeListener(e -> handleNumericChange(numericId, gt10Id));

		// GT10 checkbox → clear numeric if value contradicts the checkbox
		gt10Field.addValueChangeListener(e -> handleGt10Change(e, numericId));
	}

	private void detachTubePair(String numericId, String gt10Id) {
		Field<?> numericField = fieldGroup.getField(numericId);
		if (numericField != null) {
			fieldGroup.unbind(numericField);
			panel.removeComponent(numericField);
		}
		Field<?> gt10Field = fieldGroup.getField(gt10Id);
		if (gt10Field != null) {
			fieldGroup.unbind(gt10Field);
			panel.removeComponent(gt10Field);
		}
	}

	private void handleNumericChange(String numericId, String gt10Id) {
		NullableOptionGroup gt10 = (NullableOptionGroup) fieldGroup.getField(gt10Id);
		if (gt10 == null) {
			return;
		}
		TextField numericField = (TextField) fieldGroup.getField(numericId);
		if (numericField == null) {
			return;
		}
		Float converted = getConvertedFloat(numericField);
		if (converted == null) {
			gt10.select(false);
			return;
		}
		gt10.select(converted > 10);
	}

	private void handleGt10Change(Property.ValueChangeEvent e, String numericId) {
		Object raw = e.getProperty().getValue() instanceof Collection
			? ((Collection<?>) e.getProperty().getValue()).stream().findFirst().orElse(null)
			: e.getProperty().getValue();

		TextField numericField = (TextField) fieldGroup.getField(numericId);
		if (numericField == null) {
			return;
		}
		Float converted = getConvertedFloat(numericField);
		if (raw == null || converted == null) {
			return;
		}
		boolean checked = Boolean.TRUE.equals(raw);
		if (checked && converted <= 10) {
			numericField.clear();
		} else if (!checked && converted > 10) {
			numericField.clear();
		}
	}

	/** Locale-aware conversion; returns null for empty or unparseable input (e.g. trailing comma). */
	private static Float getConvertedFloat(TextField field) {
		try {
			return (Float) field.getConvertedValue();
		} catch (Converter.ConversionException e) {
			return null;
		}
	}
}
