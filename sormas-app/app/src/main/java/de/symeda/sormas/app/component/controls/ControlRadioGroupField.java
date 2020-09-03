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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DisplayMetricsHelper;

public class ControlRadioGroupField extends ControlPropertyEditField<Object> {

	// Constants

	private static final int RADIO_BUTTON_WIDTH = 48;
	private static final int RADIO_BUTTON_HEIGHT = 48;
	private static final float RADIO_BUTTON_MARGIN_START = 2;
	private static final int RADIO_BUTTON_MARGIN_END = 8;

	// Views

	private RadioGroup input;

	// Listeners

	private InverseBindingListener inverseBindingListener;

	// Other fields

	private List<Object> radioGroupElements = new ArrayList<>();
	private boolean enumClassSet = false;
	private AttributeSet attrs;

	// Constructors

	public ControlRadioGroupField(Context context) {
		super(context);
	}

	public ControlRadioGroupField(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.attrs = attrs;
	}

	public ControlRadioGroupField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.attrs = attrs;
	}

	// Instance methods

	public void setItems(List<Item> items) {
		for (int i = 0; i < items.size(); i++) {
			this.addItem(i, items.get(i));
		}
	}

	public void addItem(int index, Item item) {
		if (item.getValue() == null) {
			return;
		}

		LinearLayout buttonFrame = createRadioButtonFrame();
		RadioButton button = createRadioButton(index);
		View buttonLabel = createRadioButtonLabel(item.getKey());
		buttonFrame.addView(button);
		buttonFrame.addView(buttonLabel);

		input.addView(buttonFrame);
		radioGroupElements.add(item.getValue());
		setChildViewEnabledState(button);
	}

	public void clear() {
		input.removeAllViews();
		radioGroupElements.clear();
	}

	private RadioButton createRadioButton(int index) {
		RadioButton button = new RadioButton(getContext());
		button.setId(index);

		LayoutParams params = new LayoutParams(
			DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_WIDTH),
			DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_HEIGHT));
		params.setMarginStart(DisplayMetricsHelper.dpToPixels(getContext(), RADIO_BUTTON_MARGIN_START));
		params.setMarginEnd(RADIO_BUTTON_MARGIN_END);

		button.setIncludeFontPadding(false);
		button.setPaddingRelative(0, 0, 0, 0);
		button.setCompoundDrawablePadding(0);
		button.setLayoutParams(params);
		button.setButtonDrawable(null);
		button.setBackground(getBackgroundIndicator(button));

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				input.clearCheck();
				input.check(v.getId());

				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(v.hasFocus());

				input.requestFocus();
				input.requestFocusFromTouch();
			}
		});

		return button;
	}

	private LinearLayout createRadioButtonFrame() {
		LinearLayout frame = new LinearLayout(getContext());
		frame.setOrientation(HORIZONTAL);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

		frame.setLayoutParams(params);

		return frame;
	}

	private View createRadioButtonLabel(String caption) {
		TextView buttonLabel = new TextView(new ContextThemeWrapper(getContext(), R.style.CheckBoxLabelStyle), null, 0);

		ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		buttonLabel.setText(caption);
		buttonLabel.setTextColor(getResources().getColor(R.color.controlTextColor));
		buttonLabel.setLayoutParams(params);

		return buttonLabel;
	}

	private Drawable getBackgroundIndicator(RadioButton button) {
		Drawable indicator = null;
		if (attrs != null) {
			TypedArray a = button.getContext()
				.getTheme()
				.obtainStyledAttributes(
					new int[] {
						android.R.attr.listChoiceIndicatorSingle });

			if ((a != null) && (a.length() > 0)) {
				try {
					indicator = a.getDrawable(0);
				} finally {
					a.recycle();
				}
			}
		}

		return indicator;
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

	private int getCheckedRadioButtonIndex() {
		for (int i = 0; i < input.getChildCount(); i++) {
			LinearLayout frame = (LinearLayout) input.getChildAt(i);
			RadioButton button = (RadioButton) frame.getChildAt(0);

			if (button.isChecked()) {
				return i;
			}
		}

		return -1;
	}

	private void setChildViewsEnabledState() {
		for (int i = 0; i < input.getChildCount(); i++) {
			LinearLayout frame = (LinearLayout) input.getChildAt(i);
			RadioButton button = (RadioButton) frame.getChildAt(0);
			setChildViewEnabledState(button);
		}
	}

	private void setChildViewEnabledState(RadioButton button) {
		button.setEnabled(input.isEnabled());
		button.setClickable(input.isEnabled());
	}

	// Overrides

	@Override
	protected Object getFieldValue() {
		int checkedButtonIndex = getCheckedRadioButtonIndex();

		if (checkedButtonIndex >= 0) {
			return radioGroupElements.get(checkedButtonIndex);
		} else {
			return null;
		}
	}

	@Override
	protected void setFieldValue(Object value) {
		if (value == null) {
			input.clearCheck();
		} else {
			int checkedButtonIndex = radioGroupElements.indexOf(value);
			if (input.getChildAt(checkedButtonIndex) != null) {
				LinearLayout frame = (LinearLayout) input.getChildAt(checkedButtonIndex);
				RadioButton button = (RadioButton) frame.getChildAt(0);
				input.check(button.getId());
			}
		}
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		// Nothing to initializeSpinner
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.control_radiogroup_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		input = (RadioGroup) this.findViewById(R.id.radiogroup_input);
		input.setTextAlignment(getTextAlignment());
		if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
			input.setGravity(getGravity());
		}

		input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				if (inverseBindingListener != null) {
					inverseBindingListener.onChange();
				}
				onValueChanged();
			}
		});
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		input.setEnabled(enabled);
		label.setEnabled(enabled);

		setChildViewsEnabledState();
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		((ControlRadioGroupField) nextView).input.requestFocus();
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
			return;
		}

		int uncheckedStateColor = getResources().getColor(R.color.colorControlNormal);
		int checkedStateColor = getResources().getColor(R.color.colorControlActivated);

		if (state == VisualState.FOCUSED || state == VisualState.NORMAL) {
			setStateColor(checkedStateColor, uncheckedStateColor);
		}
	}

	@Override
	public void setHint(String hint) {
		// Radio groups don't have hints
	}

	// Data binding, getters & setters

	@BindingAdapter("value")
	public static void setValue(ControlRadioGroupField view, RadioButton value) {
		view.setFieldValue(value);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Object getValue(ControlRadioGroupField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(final ControlRadioGroupField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@SuppressWarnings("unchecked")
	public void setEnumClass(Class c) {
		if (!enumClassSet) {
			setItems(DataUtils.getEnumItems(c));
			enumClassSet = true;
		}
	}

	@BindingAdapter(value = {
		"value",
		"enumClass" })
	public static void setValue(ControlRadioGroupField view, Object value, Class c) {
		view.setEnumClass(c);
		view.setFieldValue(value);
	}
}
