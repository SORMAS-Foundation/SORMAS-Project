/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.StringJoiner;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.CronExpressionValidator;

public class CronExpressionField extends CustomField<String> {

	private static final long serialVersionUID = 1L;

	private static final String ENABLED_DEFAULT_EXPRESSION = "0 0 0 * * *";

	private static final String[] FIELD_CAPTIONS = {
		Captions.cronFieldSecond,
		Captions.cronFieldMinute,
		Captions.cronFieldHour,
		Captions.cronFieldDayOfMonth,
		Captions.cronFieldMonth,
		Captions.cronFieldDayOfWeek };

	private final TextField[] fields = new TextField[CronExpressionValidator.FIELD_COUNT];
	private final CheckBox enabled = new CheckBox();
	private final Label summary = new Label();

	public CronExpressionField() {

		for (int index = 0; index < fields.length; index++) {
			TextField field = new TextField();
			field.setCaption(I18nProperties.getCaption(FIELD_CAPTIONS[index]));
			field.setDescription(rangeHint(index));
			field.setWidth(70, Unit.PIXELS);
			final int fieldIndex = index;
			field.addValueChangeListener(event -> onFieldChanged(fieldIndex));
			fields[index] = field;
		}

		enabled.setCaption(I18nProperties.getCaption(Captions.cronJobEnabled));
		enabled.addValueChangeListener(event -> setJobEnabled(event.getValue()));
	}

	@Override
	protected Component initContent() {

		HorizontalLayout fieldRow = new HorizontalLayout();
		for (TextField field : fields) {
			fieldRow.addComponent(field);
		}

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		layout.addComponent(fieldRow);
		layout.addComponent(summary);
		layout.addComponent(enabled);
		return layout;
	}

	@Override
	protected void doSetValue(String value) {

		String[] parts = CronExpressionValidator.isDisabled(value) ? new String[0] : value.trim().split(" ");
		for (int index = 0; index < fields.length; index++) {
			fields[index].setValue(index < parts.length ? parts[index] : "");
		}
		enabled.setValue(!CronExpressionValidator.isDisabled(value));
		refresh();
	}

	@Override
	public String getValue() {

		if (!enabled.getValue()) {
			return "";
		}
		StringJoiner joined = new StringJoiner(" ");
		for (TextField field : fields) {
			joined.add(field.getValue());
		}
		return joined.toString();
	}

	public void setJobEnabled(boolean jobEnabled) {

		if (enabled.getValue() != jobEnabled) {
			enabled.setValue(jobEnabled);
		}
		if (jobEnabled && CronExpressionValidator.isDisabled(joinFields())) {
			doSetFields(ENABLED_DEFAULT_EXPRESSION);
		}
		for (TextField field : fields) {
			field.setEnabled(jobEnabled);
		}
		refresh();
	}

	public boolean isExpressionValid() {
		return CronExpressionValidator.isValid(getValue());
	}

	private void onFieldChanged(int fieldIndex) {

		TextField field = fields[fieldIndex];
		boolean valid = CronExpressionValidator.isFieldValid(fieldIndex, field.getValue());
		field.setComponentError(valid ? null : new com.vaadin.server.UserError(rangeHint(fieldIndex)));
		refresh();
	}

	private void refresh() {
		summary.setValue(CronExpressionSummary.describe(getValue()));
	}

	private String joinFields() {

		StringJoiner joined = new StringJoiner(" ");
		for (TextField field : fields) {
			joined.add(field.getValue());
		}
		return joined.toString().trim().isEmpty() ? "" : joined.toString();
	}

	private void doSetFields(String expression) {

		String[] parts = expression.split(" ");
		for (int index = 0; index < fields.length; index++) {
			fields[index].setValue(parts[index]);
		}
	}

	private String rangeHint(int fieldIndex) {

		String range = CronExpressionValidator.LOWER_BOUNDS[fieldIndex] + "-" + CronExpressionValidator.UPPER_BOUNDS[fieldIndex];
		return CronExpressionValidator.allowsIncrement(fieldIndex) ? range + ", * or */n" : range + " or *";
	}
}
