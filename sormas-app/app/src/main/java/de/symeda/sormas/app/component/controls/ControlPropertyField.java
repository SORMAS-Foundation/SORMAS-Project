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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.ControlLabelOnTouchListener;

public abstract class ControlPropertyField<T> extends LinearLayout {

	// Views

	private View labelFrame;
	protected TextView label;

	// Attributes

	private String description;
	private String caption;
	private boolean showCaption;
	private int textAlignment;
	private int gravity;
	private int imeOptions;
	private boolean slim;
	private Boolean captionCapitalized;
	private Boolean captionItalic;

	private Map<ControlPropertyField, List<Object>> visibilityDependencies;
	private boolean dependencyParentVisibility = true;

	private View visibilityChild;

	// Other fields

	protected boolean suppressListeners = false;
	private ArrayList<ValueChangeListener> valueChangedListeners;

	// Constructors

	public ControlPropertyField(Context context) {
		super(context);
		initializePropertyField(context, null);
		initialize(context, null, 0);
		inflateView(context, null, 0);
	}

	public ControlPropertyField(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializePropertyField(context, attrs);
		initialize(context, attrs, 0);
		inflateView(context, null, 0);
	}

	public ControlPropertyField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializePropertyField(context, attrs);
		initialize(context, attrs, defStyle);
		inflateView(context, null, 0);
	}

	// Abstract methods

	protected abstract T getFieldValue();

	protected abstract void setFieldValue(T value);

	protected abstract void initialize(Context context, AttributeSet attrs, int defStyle);

	protected abstract void inflateView(Context context, AttributeSet attrs, int defStyle);

	protected abstract void requestFocusForContentView(View nextView);

	// Instance methods

	private void initializePropertyField(Context context, AttributeSet attrs) {
		description = I18nProperties.getPrefixDescription(getPropertyIdPrefix(), getSubPropertyId());

		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlPropertyField, 0, 0);

			try {
				caption = a.getString(R.styleable.ControlPropertyField_caption);
				showCaption = a.getBoolean(R.styleable.ControlPropertyField_showCaption, true);
				textAlignment = a.getInt(R.styleable.ControlPropertyField_textAlignment, View.TEXT_ALIGNMENT_VIEW_START);
				gravity = a.getInt(R.styleable.ControlPropertyField_gravity, Gravity.START | Gravity.CENTER_VERTICAL);
				imeOptions = a.getInt(R.styleable.ControlPropertyField_imeOptions, EditorInfo.IME_NULL);
				slim = a.getBoolean(R.styleable.ControlPropertyField_slim, false);
				if (a.hasValue(R.styleable.ControlPropertyField_captionCapitalized)) {
					captionCapitalized = a.getBoolean(R.styleable.ControlPropertyField_captionCapitalized, true);
				} else {
					captionCapitalized = null;
				}
				if (a.hasValue(R.styleable.ControlPropertyField_captionItalic)) {
					captionItalic = a.getBoolean(R.styleable.ControlPropertyField_captionItalic, false);
				} else {
					captionItalic = null;
				}
			} finally {
				a.recycle();
			}
		}

		if (StringUtils.isEmpty(caption)) {
			caption = I18nProperties.getPrefixCaption(getPropertyIdPrefix(), getSubPropertyId());
		}
	}

	public void addValueChangedListener(ValueChangeListener listener) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new ArrayList<>();
		}

		valueChangedListeners.add(listener);
	}

	protected void onValueChanged() {
		if (valueChangedListeners != null && !suppressListeners) {
			for (ValueChangeListener valueChangedListener : valueChangedListeners) {
				valueChangedListener.onChange(this);
			}
		}
	}

	protected String getFieldIdString() {
		return getResources().getResourceName(getId());
	}

	public static String getPropertyIdPrefix(String fieldId) {
		int separatorIndex = fieldId.lastIndexOf("/");
		int endSeparatorIndex = fieldId.lastIndexOf("_");
		if (endSeparatorIndex == -1)
			endSeparatorIndex = fieldId.length();
		return fieldId.substring(separatorIndex + 1, separatorIndex + 2).toUpperCase()
			+ fieldId.substring(separatorIndex + 2, endSeparatorIndex).replaceAll("_", ".");
	}

	public static String getSubPropertyId(String fieldId) {
		int separatorIndex = fieldId.lastIndexOf("_");
		return fieldId.substring(separatorIndex + 1);
	}

	public String getPropertyIdPrefix() {
		String fieldId = getFieldIdString();
		return getPropertyIdPrefix(fieldId);
	}

	public String getSubPropertyId() {
		String fieldId = getFieldIdString();
		return getSubPropertyId(fieldId);
	}

	protected void setBackgroundResourceFor(View input, int resId) {
		int paddingLeft = input.getPaddingLeft();
		int paddingTop = input.getPaddingTop();
		int paddingRight = input.getPaddingRight();
		int paddingBottom = input.getPaddingBottom();

		input.setBackgroundResource(resId);

		input.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	protected void setBackgroundFor(View input, Drawable background) {
		int paddingLeft = input.getPaddingLeft();
		int paddingTop = input.getPaddingTop();
		int paddingRight = input.getPaddingRight();
		int paddingBottom = input.getPaddingBottom();

		input.setBackground(background);

		input.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
	}

	/**
	 * Handles automatic visibility setting based on the dependencyParentField and
	 * dependencyParentValue set in the layout.
	 *
	 * @param clearOnHide
	 *            When fields are hidden, their value should generally be set to
	 *            null. However, this may only be done when this method is called
	 *            from the InternalValueChangedListener to prevent data loss in case
	 *            this method is called before the parent field's internal value
	 *            has been set.
	 */
	private void setVisibilityBasedOnParentField(boolean clearOnHide) {
		if (visibilityDependencies == null) {
			return;
		}

		for (Map.Entry<ControlPropertyField, List<Object>> dependency : visibilityDependencies.entrySet()) {
			if (dependency.getKey().getVisibility() != VISIBLE) {
				hideField(clearOnHide);
				return;
			}

			if (dependency.getValue().contains(dependency.getKey().getValue())) {
				if (dependencyParentVisibility) {
					setVisibility(VISIBLE);
				} else {
					hideField(clearOnHide);
					return;
				}
			} else {
				if (dependencyParentVisibility) {
					hideField(clearOnHide);
					return;
				} else {
					setVisibility(VISIBLE);
				}
			}
		}
	}

	public void hideField(boolean eraseValue) {
		setVisibility(GONE);
		if (eraseValue) {
			setFieldValue(null);
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibilityChild != null) {
			visibilityChild.setVisibility(visibility);
		}
	}

	// Overrides

	@SuppressLint("WrongConstant")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		labelFrame = this.findViewById(R.id.label_frame);
		label = (TextView) this.findViewById(R.id.label);

		if (label == null) {
			throw new NullPointerException("No label found for property field " + getFieldIdString());
		}

		label.setText(caption);
		label.setTextAlignment(textAlignment);

		if (getTextAlignment() == TEXT_ALIGNMENT_GRAVITY) {
			label.setGravity(getGravity());
		}

		// TODO: Refactor this after the tooltips component has been replaced
		label.setOnClickListener(new ControlLabelOnTouchListener(this));

		int typeFace = (captionItalic != null && captionItalic) ? Typeface.ITALIC : Typeface.NORMAL;

		if (captionCapitalized != null) {
			if (!captionCapitalized) {
				label.setTypeface(Typeface.create("sans-serif", typeFace));
				label.setAllCaps(false);
			} else {
				label.setTypeface(Typeface.create("sans-serif-medium", typeFace));
				label.setAllCaps(true);
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (labelFrame == null && label == null) {
			return;
		}

		int visible = isShowCaption() ? View.VISIBLE : View.GONE;

		if (labelFrame != null) {
			labelFrame.setVisibility(visible);
			return;
		}

		if (label != null) {
			label.setVisibility(visible);
		}
	}

	// Data binding, getters & setters

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;

		if (label == null) {
			throw new NullPointerException("No label found for property field " + getFieldIdString());
		}

		label.setText(caption);
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int getTextAlignment() {
		return textAlignment;
	}

	@Override
	public int getGravity() {
		return gravity;
	}

	public int getImeOptions() {
		return imeOptions;
	}

	public void setImeOptions(int imeOptions) {
		this.imeOptions = imeOptions;
	}

	public boolean isShowCaption() {
		return showCaption;
	}

	public void setShowCaption(boolean showCaption) {
		this.showCaption = showCaption;
	}

	public boolean isSlim() {
		return slim;
	}

	public void setValue(Object value) {
		setFieldValue((T) value);
		onValueChanged();
	}

	public Object getValue() {
		return getFieldValue();
	}

	@BindingAdapter(value = {
		"dependencyParentField",
		"dependencyParentValue",
		"dependencyParentValue2",
		"dependencyParentVisibility",
		"dependencyParentClearOnHide" }, requireAll = false)
	public static void setDependencyParentField(
		ControlPropertyField field,
		ControlPropertyField dependencyParentField,
		Object dependencyParentValue,
		Object dependencyParentValue2,
		Boolean dependencyParentVisibility,
		Boolean dependencyParentClearOnHide) {

		Map<ControlPropertyField, List<Object>> visibilityDependencies = null;

		if (dependencyParentField != null) {
			List<Object> dependencyValues = new ArrayList();
			dependencyValues.add(dependencyParentValue);
			if (dependencyParentValue2 != null) {
				dependencyValues.add(dependencyParentValue2);
			}

			visibilityDependencies = new HashMap<ControlPropertyField, List<Object>>() {

				{
					put(dependencyParentField, dependencyValues);
				}
			};
		}

		setVisibilityDependencies(field, visibilityDependencies, dependencyParentVisibility, dependencyParentClearOnHide);
	}

	@BindingAdapter(value = {
		"dependencyParentField",
		"dependencyParentValue",
		"dependencyParent2Field",
		"dependencyParent2Value",
		"dependencyParentVisibility",
		"dependencyParentClearOnHide" }, requireAll = false)
	public static void setDependencyParentField(
		ControlPropertyField field,
		ControlPropertyField dependencyParentField,
		Object dependencyParentValue,
		ControlPropertyField dependencyParent2Field,
		Object dependencyParent2Value,
		Boolean dependencyParentVisibility,
		Boolean dependencyParentClearOnHide) {

		Map<ControlPropertyField, List<Object>> visibilityDependencies = new HashMap<>();

		if (dependencyParentField != null) {
			visibilityDependencies.put(dependencyParentField, new ArrayList<Object>() {

				{
					add(dependencyParentValue);
				}
			});
		}

		if (dependencyParent2Field != null) {
			visibilityDependencies.put(dependencyParent2Field, new ArrayList<Object>() {

				{
					add(dependencyParent2Value);
				}
			});
		}

		if (visibilityDependencies.size() == 0)
			visibilityDependencies = null;

		setVisibilityDependencies(field, visibilityDependencies, dependencyParentVisibility, dependencyParentClearOnHide);
	}

	@BindingAdapter(value = {
		"dependencyParentField",
		"dependencyParentMinValue",
		"dependencyParentMaxValue",
		"dependencyParentVisibility",
		"dependencyParentClearOnHide" }, requireAll = false)
	public static void setDependencyParentField(
		ControlPropertyField field,
		ControlPropertyField dependencyParentField,
		Integer dependencyParentMinValue,
		Integer dependencyParentMaxValue,
		Boolean dependencyParentVisibility,
		Boolean dependencyParentClearOnHide) {

		Map<ControlPropertyField, List<Object>> visibilityDependencies = null;

		if (dependencyParentField != null) {
			List<Object> dependencyValues = new ArrayList();
			while (dependencyParentMinValue <= dependencyParentMaxValue) {
				dependencyValues.add(dependencyParentMinValue);
				dependencyParentMinValue += 1;
			}

			visibilityDependencies = new HashMap<ControlPropertyField, List<Object>>() {

				{
					put(dependencyParentField, dependencyValues);
				}
			};
		}

		setVisibilityDependencies(field, visibilityDependencies, dependencyParentVisibility, dependencyParentClearOnHide);
	}

	@BindingAdapter(value = {
		"visibilityDependencies",
		"dependencyParentVisibility",
		"dependencyParentClearOnHide" }, requireAll = false)
	public static void setVisibilityDependencies(
		ControlPropertyField field,
		Map<ControlPropertyField, List<Object>> visibilityDependencies,
		Boolean dependencyParentVisibility,
		Boolean dependencyParentClearOnHide) {
		field.visibilityDependencies = visibilityDependencies;

		if (dependencyParentVisibility != null) {
			field.dependencyParentVisibility = dependencyParentVisibility;
		}

		if (visibilityDependencies != null) {
			field.setVisibilityBasedOnParentField(false);

			for (Map.Entry<ControlPropertyField, List<Object>> dependency : visibilityDependencies.entrySet()) {
				dependency.getKey()
					.addValueChangedListener(
						parentField -> field
							.setVisibilityBasedOnParentField(dependencyParentClearOnHide != null ? dependencyParentClearOnHide : true));
			}
		}
	}

	@BindingAdapter(value = {
		"visibilityChild" })
	public static void setVisibilityChild(ControlPropertyField field, View visibilityChild) {
		field.visibilityChild = visibilityChild;
		if (visibilityChild != null) {
			visibilityChild.setVisibility(field.getVisibility());
		}
	}

	@BindingAdapter(value = {
		"goneIfValue",
		"goneIfVariable" })
	public static void setGoneIf(ControlPropertyField field, Enum goneIfValue, Enum goneIfVariable) {
		if (goneIfVariable == goneIfValue) {
			field.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfNotValue",
		"goneIfVariable" })
	public static void setGoneIfNot(ControlPropertyField field, Enum goneIfNotValue, Enum goneIfVariable) {
		if (goneIfVariable != goneIfNotValue) {
			field.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfNotValue",
		"goneIfNotValue2",
		"goneIfVariable" })
	public static void setGoneIfNot(ControlPropertyField field, Enum goneIfNotValue, Enum goneIfNotValue2, Enum goneIfVariable) {
		if (goneIfVariable != goneIfNotValue && goneIfVariable != goneIfNotValue2) {
			field.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfLessThanValue",
		"goneIfVariable" })
	public static void setGoneIfLessThan(ControlPropertyField field, Integer goneIfLessThanValue, Integer goneIfVariable) {
		if (goneIfVariable < goneIfLessThanValue) {
			field.setVisibility(GONE);
		}
	}

	@BindingAdapter(value = {
		"goneIfLessThanValue",
		"goneIfField" })
	public static void setGoneIfLessThan(ControlPropertyField field, Integer goneIfLessThanValue, ControlPropertyField goneIfField) {
		if (goneIfField.getValue() == null || (Integer) goneIfField.getValue() < goneIfLessThanValue) {
			field.setVisibility(GONE);
		}
	}
}
