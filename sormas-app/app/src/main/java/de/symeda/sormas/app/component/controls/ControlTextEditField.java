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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class ControlTextEditField extends ControlPropertyEditField<String> {

	// Views

	protected EditText input;

	// Attributes

	private boolean singleLine;
	private int maxLines;
	private int maxLength;
	private boolean textArea;
	private int inputType;

	// Listeners

	protected InverseBindingListener inverseBindingListener;
	private OnClickListener onClickListener;

	// Constructors

	public ControlTextEditField(Context context) {
		super(context);
	}

	public ControlTextEditField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlTextEditField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// Instance methods

	public void setCursorToRight() {
		input.setSelection(input.getText().length());
	}

	/**
	 * Handles clicks on the buttons to switch to the next view.
	 */
	private void setUpOnEditorActionListener() {
		input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int definedActionId = v.getImeActionId();
				if (definedActionId == EditorInfo.IME_ACTION_NONE) {
					return false;
				}

				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					int id = getNextFocusForwardId();
					if (id != NO_ID) {
						View nextView = v.getRootView().findViewById(id);
						if (nextView != null && nextView.getVisibility() == VISIBLE) {
							if (nextView instanceof ControlTextEditField) {
								requestFocusForContentView(nextView);
							} else if (nextView instanceof ControlPropertyField) {
								((ControlPropertyField) nextView).requestFocusForContentView(nextView);
							} else {
								nextView.requestFocus();
							}
						}
					}

					return true;
				}

				return false;
			}
		});
	}

	private void setUpOnFocusChangeListener() {
		input.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(hasFocus);

				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				if (imm != null) {
					if (hasFocus) {
						changeVisualState(VisualState.FOCUSED);
						imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
						// Prevent the content from being automatically selected
						input.setSelection(input.getText().length(), input.getText().length());
						if (onClickListener != null) {
							input.setOnClickListener(onClickListener);
						}
					} else {
						if (hasError) {
							changeVisualState(VisualState.ERROR);
						} else {
							changeVisualState(VisualState.NORMAL);
						}
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						input.setOnClickListener(null);
					}
				}
			}
		});
	}

	private void initializeOnClickListener() {
		if (onClickListener != null) {
			return;
		}

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!v.isEnabled()) {
					return;
				}

				showOrHideNotifications(v.hasFocus());

				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

				if (imm != null) {
					if (v.hasFocus()) {
						imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
						//// Prevent the content from being automatically selected
						//input.setSelection(input.getText().length(), input.getText().length());
					} else {
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					}
				}
			}
		};
	}

	// Overrides

	@Override
	public String getValue() {
		return (String) super.getValue();
	}

	@Override
	protected String getFieldValue() {
		if (input.getText() == null) {
			return null;
		}
		return input.getText().toString();
	}

	@Override
	protected void setFieldValue(String value) {
		input.setText(value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled); // this has to be called first
		input.setEnabled(enabled);
		label.setEnabled(enabled);
	}

	@Override
	public void setHint(String hint) {
		this.hint = hint;
		input.setHint(hint);
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlTextEditField, 0, 0);

			try {
				singleLine = a.getBoolean(R.styleable.ControlTextEditField_singleLine, true);
				maxLines = a.getInt(R.styleable.ControlTextEditField_maxLines, 1);
				textArea = a.getBoolean(R.styleable.ControlTextEditField_textArea, false);
				maxLength =
					a.getInt(R.styleable.ControlTextEditField_maxLength, textArea ? EntityDto.COLUMN_LENGTH_BIG : EntityDto.COLUMN_LENGTH_DEFAULT);
				inputType = a.getInt(R.styleable.ControlTextEditField_inputType, InputType.TYPE_CLASS_TEXT);
			} finally {
				a.recycle();
			}
		}
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			if (textArea) {
				inflater.inflate(R.layout.control_textfield_edit_multi_row_layout, this);
			} else if (isSlim()) {
				inflater.inflate(R.layout.control_textfield_edit_slim_layout, this);
			} else {
				inflater.inflate(R.layout.control_textfield_edit_layout, this);
			}
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		input = (EditText) this.findViewById(R.id.text_input);
		if (getImeOptions() == EditorInfo.IME_NULL) {
			setImeOptions(EditorInfo.IME_ACTION_DONE);
		}
		input.setImeOptions(getImeOptions());
		input.setImeActionLabel(null, getImeOptions());
		input.setTextAlignment(getTextAlignment());
		if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
			input.setGravity(getGravity());
		}
		input.setInputType(inputType);
		setSingleLine(singleLine);
		if (maxLength >= 0) {
			input.setFilters(
				new InputFilter[] {
					new InputFilter.LengthFilter(maxLength) });
		}

		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (inverseBindingListener != null) {
					inverseBindingListener.onChange();
				}
				onValueChanged();
			}
		});

		setUpOnEditorActionListener();
		setUpOnFocusChangeListener();
		initializeOnClickListener();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (getHint() == null) {
			setHint(I18nProperties.getPrefixCaption(getPropertyIdPrefix(), getSubPropertyId()));
		}
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		if (nextView != null) {
			((ControlTextEditField) nextView).input.requestFocus();
			((ControlTextEditField) nextView).setCursorToRight();
		}
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
		Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
		int textColor = getResources().getColor(state.getTextColor());
		int hintColor = getResources().getColor(state.getHintColor());

		if (drawable != null) {
			drawable = drawable.mutate();
		}

		label.setTextColor(labelColor);
		setBackground(drawable);

		if (state != VisualState.ERROR) {
			input.setTextColor(textColor);
			input.setHintTextColor(hintColor);
		}

		setEnabled(state != VisualState.DISABLED);
	}

	@Override
	public void setBackgroundResource(int resId) {
		setBackgroundResourceFor(input, resId);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundFor(input, background);
	}

	// Data binding, getters & setters

	@BindingAdapter("value")
	public static void setValue(ControlTextEditField view, String text) {
		view.setFieldValue(text);
	}

	@BindingAdapter("value")
	public static void setValue(ControlTextEditField view, Integer integerValue) {
		if (integerValue != null) {
			view.setFieldValue(String.valueOf(integerValue));
		} else {
			view.setFieldValue(null);
		}
	}

	@BindingAdapter("value")
	public static void setValue(ControlTextEditField view, Float floatValue) {
		if (floatValue != null) {
			view.setFieldValue(String.valueOf(floatValue));
		} else {
			view.setFieldValue(null);
		}
	}

	@BindingAdapter("value")
	public static void setValue(ControlTextEditField view, Double doubleValue) {
		if (doubleValue != null) {
			view.setFieldValue(String.valueOf(doubleValue));
		} else {
			view.setFieldValue(null);
		}
	}

	public void setDoubleValue(Double doubleValue) {
		setValue(this, doubleValue);
	}

	public void setFloatValue(Float floatValue) {
		setValue(this, floatValue);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static String getValue(ControlTextEditField view) {
		return view.getFieldValue();
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Integer getIntegerValue(ControlTextEditField view) {
		if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
			return Integer.valueOf(view.getFieldValue());
		} else {
			return null;
		}
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Float getFloatValue(ControlTextEditField view) {
		if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
			return Float.valueOf(view.getFieldValue());
		} else {
			return null;
		}
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Double getDoubleValue(ControlTextEditField view) {
		if (view.getFieldValue() != null && !view.getFieldValue().isEmpty()) {
			return Double.valueOf(view.getFieldValue());
		} else {
			return null;
		}
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(ControlTextEditField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	public String getHint() {
		if (input.getHint() == null) {
			return null;
		}

		return input.getHint().toString();
	}

	public boolean isSingleLine() {
		return singleLine;
	}

	public void setSingleLine(boolean singleLine) {
		this.singleLine = singleLine;

		if (this.singleLine) {
			input.setMaxLines(1);
			input.setVerticalScrollBarEnabled(false);
		} else {
			input.setMaxLines(maxLines);
			input.setVerticalScrollBarEnabled(true);
			if (textArea) {
				input.setLines(maxLines);
			}
		}
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
		if (input != null)
			input.setInputType(inputType);
	}

}
