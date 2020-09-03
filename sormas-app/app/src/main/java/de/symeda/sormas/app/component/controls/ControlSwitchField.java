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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.StateDrawableBuilder;
import de.symeda.sormas.app.util.DataUtils;

public class ControlSwitchField extends ControlPropertyEditField<Object> {

	// Constants

	private static final String RADIO_BUTTON_FONT_FAMILY = "sans-serif-medium";

	// Views

	private RadioGroup input;

	// Attributes

	private boolean useAbbreviations;
	private boolean useBoolean;
	private Drawable background;
	private ColorStateList textColor;

	// Listeners

	private InverseBindingListener inverseBindingListener;
	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

	// Other fields

	private Class<? extends Enum> enumClass = null;
	private List<Object> radioGroupElements = new ArrayList<>();

	// Constructors

	public ControlSwitchField(Context context) {
		super(context);
	}

	public ControlSwitchField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlSwitchField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Instance methods

	private void setChildViewsEnabledState() {
		for (int i = 0; i < input.getChildCount(); i++) {
			RadioButton button = (RadioButton) input.getChildAt(i);
			setChildViewEnabledState(button);
		}
	}

	private void setChildViewEnabledState(RadioButton button) {
		button.setEnabled(input.isEnabled());
		button.setClickable(input.isEnabled());
	}

	public void setEnumClass(Class<? extends Enum> c) {
		if (!DataHelper.equal(c, enumClass)) {
			suppressListeners = true;
			removeAllItems();

			List<Item> items = DataUtils.getEnumItems(c, false);

			int itemTotal = items.size();
			for (int i = 0; i < items.size(); i++) {
				addItem(i, itemTotal - 1, items.get(i));
			}

			enumClass = c;
			suppressListeners = false;
		}
	}

	public void setBooleanContent() {
		suppressListeners = true;
		removeAllItems();

		List<Item> items = DataUtils.getBooleanItems();

		int itemTotal = items.size();
		for (int i = 0; i < items.size(); i++) {
			addItem(i, itemTotal - 1, items.get(i));
		}

		suppressListeners = false;
	}

	private void addItem(int index, int lastIndex, Item item) {
		final RadioButton button = createRadioButton(index, lastIndex, item);
		input.addView(button);
		radioGroupElements.add(item.getValue());
		setChildViewEnabledState(button);
	}

	private void removeAllItems() {
		input.removeAllViews();
		radioGroupElements.clear();
	}

	private RadioButton createRadioButton(int index, int lastIndex, Item item) {
		RadioButton button = new RadioButton(getContext());
		button.setId(index);

		LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

		int borderSize = getResources().getDimensionPixelSize(R.dimen.defaultControlStrokeWidth);
		if (index == 0) {
			params.setMargins(borderSize, borderSize, 0, borderSize);
		} else if (index == lastIndex) {
			params.setMargins(0, borderSize, borderSize, borderSize);
		} else {
			params.setMargins(0, borderSize, 0, borderSize);
		}

		float textSize;
		int heightInPixel;
		if (isSlim()) {
			textSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
			heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
		} else {
			textSize = getContext().getResources().getDimension(R.dimen.switchControlTextSize);
			heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxSwitchButtonHeight);
		}

		button.setHeight(heightInPixel);
		button.setMinHeight(heightInPixel);
		button.setMaxHeight(heightInPixel);
		button.setPadding(0, 0, 0, 0);
		button.setBackground(getButtonDrawable(index == lastIndex, hasError));
		button.setButtonDrawable(null);
		button.setGravity(Gravity.CENTER);
		button.setTypeface(Typeface.create(RADIO_BUTTON_FONT_FAMILY, Typeface.NORMAL));
		button.setTextColor(textColor);
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		button.setIncludeFontPadding(false);
		button.setLayoutParams(params);

		Object btnValue = item.getValue();
		String btnKey = item.getKey();

		if (useAbbreviations) {
			if (btnValue instanceof YesNoUnknown && btnKey.equals(YesNoUnknown.UNKNOWN.toString())) {
				btnKey = btnKey.substring(0, 3);
			}
			if (btnValue instanceof SymptomState && btnKey.equals(SymptomState.UNKNOWN.toString())) {
				btnKey = btnKey.substring(0, 3);
			}
		}

		if (btnKey != null) {
			button.setText(btnKey);
		}

		setUpOnClickListener(button);

		return button;
	}

	private Drawable getButtonDrawable(boolean lastButton, boolean hasError) {
		return new StateDrawableBuilder().setCheckedAndDisabledDrawable(ControlSwitchState.DISABLED.getDrawable(lastButton, hasError, getResources()))
			.setPressedDrawable(ControlSwitchState.PRESSED.getDrawable(lastButton, hasError, getResources()))
			.setCheckedDrawable(ControlSwitchState.CHECKED.getDrawable(lastButton, hasError, getResources()))
			.setNormalDrawable(ControlSwitchState.NORMAL.getDrawable(lastButton, hasError, getResources()))
			.build();
	}

	private void setUpOnClickListener(RadioButton button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(v.hasFocus());

				input.requestFocus();
				input.requestFocusFromTouch();
			}
		});
	}

	// Overrides

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlSwitchField, 0, 0);

			try {
				useAbbreviations = a.getBoolean(R.styleable.ControlSwitchField_useAbbreviations, isSlim());
				useBoolean = a.getBoolean(R.styleable.ControlSwitchField_useBoolean, false);
			} finally {
				a.recycle();
			}
		}
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			if (isSlim()) {
				inflater.inflate(R.layout.control_switch_slim_layout, this);
			} else {
				inflater.inflate(R.layout.control_switch_layout, this);
			}
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		input = (RadioGroup) this.findViewById(R.id.switch_input);
		input.setOrientation(HORIZONTAL);
		background = getResources().getDrawable(R.drawable.control_switch_background_border);
		textColor = getResources().getColorStateList(R.color.control_switch_color_selector);
		input.setBackground(background.mutate());

		if (useBoolean) {
			setBooleanContent();
		} else if (enumClass == null) {
			setEnumClass(YesNoUnknown.class);
		}

		input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				if (inverseBindingListener != null) {
					inverseBindingListener.onChange();
				}

				// on checked changed is also called when other button is deselected before new button is selected
				RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
				if (radioButton == null || radioButton.isChecked()) {
					onValueChanged();
				}

				if (onCheckedChangeListener != null && !suppressListeners) {
					onCheckedChangeListener.onCheckedChanged(radioGroup, i);
				}
			}
		});
	}

	@Override
	protected Object getFieldValue() {
		int selectedValueId = input.getCheckedRadioButtonId();

		if (selectedValueId >= 0) {
			View selectedValue = input.findViewById(selectedValueId);
			if (selectedValue != null) {
				int selectedValueIndex = input.indexOfChild(selectedValue);
				return radioGroupElements.get(selectedValueIndex);
			}

			return null;
		}

		return null;
	}

	@Override
	protected void setFieldValue(Object value) {
		if (value == null) {
			input.clearCheck();
		} else {
			int selectedValueIndex = radioGroupElements.indexOf(value);
			if (selectedValueIndex >= 0) {
				RadioButton button = (RadioButton) input.getChildAt(selectedValueIndex);
				if (!button.isChecked()) {
					input.check(button.getId());
				}
			}
		}
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
		((ControlSwitchField) nextView).input.requestFocus();
	}

	@Override
	public void setHint(String hint) {
		// Switch does not have a hint
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
			Drawable disabledStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_disabled);
			disabledStateDrawable = disabledStateDrawable.mutate();
			input.setBackground(disabledStateDrawable);
			setEnabled(false);
			return;
		}

		setEnabled(true);

		for (int i = 0; i < input.getChildCount(); i++) {
			RadioButton button = (RadioButton) input.getChildAt(i);
			button.setBackground(getButtonDrawable(i == input.getChildCount() - 1, hasError));
		}

		if (state == VisualState.ERROR) {
			Drawable errorStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_error);
			errorStateDrawable = errorStateDrawable.mutate();
			input.setBackground(errorStateDrawable);
			return;
		}

		if (state == VisualState.FOCUSED || state == VisualState.NORMAL) {
			background = background.mutate();
			input.setBackground(background);
		}
	}

	// Data binding, getters & setters

	@BindingAdapter("value")
	public static void setValue(ControlSwitchField view, Object value) {
		view.setFieldValue(value);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Object getValue(ControlSwitchField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(final ControlSwitchField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@BindingAdapter(value = {
		"value",
		"useBoolean",
		"enumClass",
		"defaultValue" }, requireAll = false)
	public static void setValue(ControlSwitchField view, Object value, Boolean useBoolean, Class enumClass, Object defaultValue) {
		if (enumClass != null) {
			view.setEnumClass((Class<? extends Enum>) enumClass);
		}

		if (value == null) {
			value = defaultValue;
		}

		view.setFieldValue(value);
	}

	public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}

	public void setUseAbbreviations(boolean useAbbreviations) {
		this.useAbbreviations = useAbbreviations;
	}
}
