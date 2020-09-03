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

package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;

public class ControlCheckBoxField extends ControlPropertyEditField<Boolean> {

	// Views

	protected CheckBox input;

	// Listeners

	protected InverseBindingListener inverseBindingListener;
	private OnClickListener onClickListener;

	// Constructors

	public ControlCheckBoxField(Context context) {
		super(context);
	}

	public ControlCheckBoxField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlCheckBoxField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Instance methods

	private void setUpOnFocusChangeListener() {
		input.setOnFocusChangeListener((v, hasFocus) -> {
			if (!v.isEnabled()) {
				return;
			}

			showOrHideNotifications(hasFocus);

			if (hasFocus) {
				changeVisualState(VisualState.FOCUSED);
				if (onClickListener != null) {
					input.setOnClickListener(onClickListener);
				}
			} else {
				if (hasError) {
					changeVisualState(VisualState.ERROR);
				} else {
					changeVisualState(VisualState.NORMAL);
				}
			}
		});
	}

	private void initializeOnClickListener() {
		if (onClickListener != null) {
			return;
		}

		onClickListener = v -> {
			if (!v.isEnabled()) {
				return;
			}

			showOrHideNotifications(v.hasFocus());
		};
	}

	public void setStateColor(int checkedColor, int uncheckedColor) {
		int[][] states = new int[][] {
			new int[] {
				-android.R.attr.state_checked },
			new int[] {
				android.R.attr.state_checked }, };

		int[] thumbColors = new int[] {
			uncheckedColor,
			checkedColor, };

		input.setBackgroundTintList(new ColorStateList(states, thumbColors));
	}

	// Overrides

	@Override
	protected void setFieldValue(Boolean value) {
		input.setChecked(value != null ? value : false);
	}

	@Override
	protected Boolean getFieldValue() {
		return input.isChecked();
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		// Nothing to initialize
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.control_checkbox_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@SuppressLint("WrongConstant")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		input = this.findViewById(R.id.checkbox);
		input.setImeOptions(getImeOptions());
		input.setTextAlignment(getTextAlignment());
		if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
			input.setGravity(getGravity());
		}

		input.setOnCheckedChangeListener((compoundButton, b) -> {
			if (inverseBindingListener != null) {
				inverseBindingListener.onChange();
			}
			onValueChanged();
		});

		setUpOnFocusChangeListener();
		initializeOnClickListener();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		input.setEnabled(enabled);
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		((ControlCheckBoxField) nextView).input.requestFocus();
	}

	@Override
	public void setBackgroundResource(int resId) {
		setBackgroundResourceFor(input, resId);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundFor(input, background);
	}

	@Override
	protected void changeVisualState(VisualState state) {
		if (getUserEditRight() != null && !ConfigProvider.hasUserRight(getUserEditRight())) {
			state = VisualState.DISABLED;
		}

		if (this.visualState == state) {
			return;
		}

		visualState = state;

		int labelColor = getResources().getColor(state.getLabelColor());
		label.setTextColor(labelColor);

		if (state == VisualState.DISABLED) {
			setStateColor(labelColor, labelColor);
			setEnabled(false);
			return;
		}

		setEnabled(true);

		if (state == VisualState.ERROR) {
			setStateColor(labelColor, labelColor);
		}
	}

	@Override
	public void setHint(String hint) {
		// Checkboxes don't have hints
	}

	// Data binding, getters & setters

	@BindingAdapter("value")
	public static void setValue(ControlCheckBoxField view, Boolean value) {
		view.setFieldValue(value);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Boolean getValue(ControlCheckBoxField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(ControlCheckBoxField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}
}
