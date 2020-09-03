/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component;

import de.symeda.sormas.app.R;

public enum VisualState {

	NORMAL(R.drawable.selector_text_control_edit,
		R.drawable.selector_text_control_edit,
		R.drawable.selector_spinner,
		R.drawable.selector_spinner_filter,
		R.drawable.selector_text_filter,
		R.drawable.control_switch_background_border,
		R.color.controlLabelColor,
		R.color.controlTextColor,
		R.color.controlTextViewHint),
	FOCUSED(R.drawable.selector_text_control_edit,
		R.drawable.selector_text_control_edit,
		R.drawable.selector_spinner,
		R.drawable.selector_spinner_filter,
		R.drawable.selector_text_filter,
		R.drawable.selector_text_control_edit,
		R.color.colorControlActivated,
		R.color.controlTextColor,
		R.color.controlTextViewHint),
	DISABLED(R.drawable.selector_text_control_edit,
		R.drawable.selector_text_control_edit,
		R.drawable.selector_spinner,
		R.drawable.selector_spinner_filter,
		R.drawable.selector_text_filter,
		R.drawable.selector_text_control_edit,
		R.color.colorControlDisabled,
		R.color.colorControlDisabled,
		R.color.colorControlDisabledHint),
	ERROR(R.drawable.selector_text_control_edit_error,
		R.drawable.selector_text_control_edit_error,
		R.drawable.selector_spinner_error,
		R.drawable.selector_spinner_filter,
		R.drawable.selector_text_filter,
		R.drawable.control_switch_background_border_error,
		R.color.colorControlError,
		R.color.controlTextColor,
		R.color.controlTextViewHint);

	private int backgroundTextField;
	private int backgroundCheckbox;
	private int backgroundSpinner;
	private int backgroundSpinnerFilter;
	private int backgroundTextFilter;
	private int backgroundSwitch;
	private int labelColor;
	private int textColor;
	private int hintColor;

	VisualState(
		int backgroundTextField,
		int backgroundCheckbox,
		int backgroundSpinner,
		int backgroundSpinnerFilter,
		int backgroundTextFilter,
		int backgroundSwitch,
		int labelColor,
		int textColor,
		int hintColor) {
		this.backgroundTextField = backgroundTextField;
		this.backgroundCheckbox = backgroundCheckbox;
		this.backgroundSpinner = backgroundSpinner;
		this.backgroundSpinnerFilter = backgroundSpinnerFilter;
		this.backgroundTextFilter = backgroundTextFilter;
		this.backgroundSwitch = backgroundSwitch;
		this.labelColor = labelColor;
		this.textColor = textColor;
		this.hintColor = hintColor;
	}

	public int getBackground(VisualStateControlType controlType) {
		switch (controlType) {
		case TEXT_FIELD:
			return backgroundTextField;
		case CHECKBOX:
			return backgroundCheckbox;
		case SPINNER:
			return backgroundSpinner;
		case SPINNER_FILTER:
			return backgroundSpinnerFilter;
		case TEXT_FILTER:
			return backgroundTextFilter;
		case SWITCH:
			return backgroundSwitch;
		default:
			throw new IllegalArgumentException(controlType.toString());
		}
	}

	public int getLabelColor() {
		return labelColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public int getHintColor() {
		return hintColor;
	}
}
