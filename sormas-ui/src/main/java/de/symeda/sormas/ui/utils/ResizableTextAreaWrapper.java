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

import java.util.stream.Stream;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;

public class ResizableTextAreaWrapper<T extends AbstractTextField> implements FieldWrapper<T> {

	// XXX: make this configurable
	private static final int MAX_ROWS = 30;
	private static final int MIN_ROWS = 4;

	private final boolean withMaxLength;
	private T textField;
	private String caption;
	private Label labelField;

	public ResizableTextAreaWrapper() {
		this(true);
	}

	public ResizableTextAreaWrapper(boolean withMaxLength) {
		this.withMaxLength = withMaxLength;
	}

	@Override
	public ComponentContainer wrap(T textField, String caption) {
		return wrap(textField, caption, true);
	}

	@Override
	public ComponentContainer wrap(T textField, String caption, boolean withMargin) {

		this.textField = textField;
		this.caption = caption;

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setWidth(100, Sizeable.Unit.PERCENTAGE);
		if (withMargin) {
			layout.addStyleName(CssStyles.FIELD_WRAPPER);
		}

		textField.setWidth(100, Sizeable.Unit.PERCENTAGE);
		textField.addStyleName(CssStyles.RESIZABLE);
		textField.setNullRepresentation("");
		textField.setTextChangeTimeout(200);

		Stream<Validator> maxLengthValidatorStream = textField.getValidators().stream().filter(v -> v instanceof MaxLengthValidator);

		if (withMaxLength) {
			maxLengthValidatorStream.findFirst().map(v -> ((MaxLengthValidator) v).getMaxLength()).ifPresent(textField::setMaxLength);

			labelField = new Label(buildLabelMessage(textField.getValue(), textField, caption));
			labelField.setId(textField.getId() + "_label");
			labelField.setWidth(100, Sizeable.Unit.PERCENTAGE);
			labelField.addStyleNames(CssStyles.ALIGN_RIGHT, CssStyles.FIELD_EXTRA_INFO, CssStyles.LABEL_ITALIC);
			layout.addComponents(labelField);
		} else {
			maxLengthValidatorStream.iterator().forEachRemaining(v -> textField.removeValidator(v));
		}

		textField.addTextChangeListener(e -> {
			updateTextfieldAppearance();
		});
		textField.addValueChangeListener(e -> {
			updateTextfieldAppearance();
		});

		layout.addComponents(textField);
		if (withMaxLength) {
			layout.addComponents(labelField);
		}
		return layout;
	}

	private void updateTextfieldAppearance() {
		if (withMaxLength) {
			// XXX: notify user if text is not valid (e.g. too long)
			labelField.setValue(buildLabelMessage(textField.getValue(), textField, caption));
		}
		adjustRows(textField);
	}

	private String buildLabelMessage(String text, T textField, String caption) {
		return String.format(I18nProperties.getCaption(caption), Strings.nullToEmpty(text).length(), textField.getMaxLength());
	}

	/**
	 * Set number of rows in textareas to the number of lines of text + 1.
	 * Min: {@link #MIN_ROWS}, Max: {@link #MAX_ROWS}
	 */
	private void adjustRows(T textField) {
		if (textField instanceof TextArea) {
			((TextArea) textField)
				.setRows(Math.min(MAX_ROWS, Math.max(CharMatcher.is('\n').countIn(Strings.nullToEmpty(textField.getValue())) + 1, MIN_ROWS)));
		}
	}
}
