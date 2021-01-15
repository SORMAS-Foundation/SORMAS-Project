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
package de.symeda.sormas.ui.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class TextFieldWithMaxLengthWrapper<T extends AbstractTextField> implements FieldWrapper<T> {

	// XXX: make this configurable
	private static final int MAX_ROWS = 30;
	private static final int MIN_ROWS = 4;

	@Override
	public ComponentContainer wrap(T textField) {

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		layout.addStyleName(CssStyles.FIELD_WRAPPER);

		textField.setWidth(100, Sizeable.Unit.PERCENTAGE);
		textField.addStyleName(CssStyles.RESIZABLE);
			textField.getValidators()
				.stream()
				.filter(v -> v instanceof MaxLengthValidator)
				.findFirst()
				.map(v -> ((MaxLengthValidator) v).getMaxLength())
			.ifPresent(textField::setMaxLength);
		textField.setNullRepresentation("");
		textField.setTextChangeTimeout(200);

		Label labelField = new Label();
		labelField.setId(textField.getId() + "_label");
		labelField.setWidth(100, Sizeable.Unit.PERCENTAGE);
		labelField.addStyleNames(CssStyles.ALIGN_RIGHT, CssStyles.FIELD_EXTRA_INFO, CssStyles.LABEL_ITALIC);

		textField.addTextChangeListener(e -> {
			// XXX: notify user if text is not valid (e.g. too long)
			labelField.setValue(buildLabelMessage(e.getText(), textField));
			adjustRows(textField, e.getText());
		});
		textField.addValueChangeListener(e -> {
			// XXX: notify user if text is not valid (e.g. too long)
			labelField.setValue(buildLabelMessage(textField.getValue(), textField));
			adjustRows(textField, textField.getValue());
		});

		layout.addComponents(textField, labelField);

		return layout;
	}

	private String buildLabelMessage(String text, T textField) {
		return String.format(I18nProperties.getCaption(Captions.numberOfCharacters), Strings.nullToEmpty(text).length(), textField.getMaxLength());
	}

	/**
	 * Set number of rows in textareas to the number of lines of text + 1.
	 * Min: {@link #MIN_ROWS}, Max: {@link #MAX_ROWS}
	 */
	private void adjustRows(T textField, String text) {
		if (textField instanceof TextArea) {
			((TextArea) textField).setRows(Math.min(MAX_ROWS, Math.max(CharMatcher.is('\n').countIn(Strings.nullToEmpty(text)) + 1, MIN_ROWS)));
		}
	}
}
