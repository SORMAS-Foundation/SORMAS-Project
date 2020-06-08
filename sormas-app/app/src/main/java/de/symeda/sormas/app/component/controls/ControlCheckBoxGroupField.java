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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.util.DataUtils;

public class ControlCheckBoxGroupField extends ControlPropertyEditField<Object> {

	private Map<Object, CheckBox> checkBoxes = new HashMap<>();
	private InverseBindingListener inverseBindingListener;
	private Class<? extends Enum> enumClass = null;
	private LinearLayout checkBoxesFrame;

	// Constructors

	public ControlCheckBoxGroupField(Context context) {
		super(context);
	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlCheckBoxGroupField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public <T extends Enum> void setEnumClass(Class<T> c) {
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

	private void addItem(int index, int lastIndex, Item item) {
		final CheckBox checkBox = createCheckBox(index, lastIndex, item);
		checkBoxesFrame.addView(checkBox);
		checkBoxes.put(item.getValue(), checkBox);
	}

	private CheckBox createCheckBox(int index, int lastIndex, Item item) {
		CheckBox checkBox = new CheckBox(new ContextThemeWrapper(getContext(), R.style.ControlCheckboxStyle));
		checkBox.setId(index);
		checkBox.setText(item.getKey());
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		checkBox.setOnCheckedChangeListener((b, i) -> {
			if (inverseBindingListener != null) {
				inverseBindingListener.onChange();
			}
		});
		return checkBox;
	}

	private void uncheckAll() {
		for (CheckBox checkBox : checkBoxes.values()) {
			checkBox.setChecked(false);
		}
	}

	private void removeAllItems() {
		checkBoxes.clear();
		checkBoxesFrame.removeAllViews();
	}

	public void removeItem(Object itemId) {
		checkBoxesFrame.removeView(checkBoxes.get(itemId));
		checkBoxes.remove(itemId);
	}

	@Override
	protected void initialize(Context context, AttributeSet attrs, int defStyle) {
		// Nothing to initialize
	}

	@Override
	protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			inflater.inflate(R.layout.control_checkboxgroup_layout, this);
		} else {
			throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		checkBoxesFrame = this.findViewById(R.id.checkboxes_frame);
	}

	@Override
	protected Object getFieldValue() {
		Set<Object> selectedElements = new HashSet<>();

		for (Object key : checkBoxes.keySet()) {
			if (checkBoxes.get(key).isChecked()) {
				selectedElements.add(key);
			}
		}

		return selectedElements;
	}

	@Override
	protected void setFieldValue(Object value) {
		if (value == null) {
			uncheckAll();
		} else {
			for (Object element : (Set<Object>) value) {
				CheckBox checkBox = checkBoxes.get(element);

				if (checkBox == null) {
					throw new IllegalArgumentException(
						"Passed list arguments contains an element that is not part of this ControlCheckBoxGroupField");
				}

				checkBox.setChecked(true);
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		// TODO: Implement; Probably replace elements with tags
	}

	@Override
	protected void requestFocusForContentView(View nextView) {
		// Not needed
	}

	@Override
	public void setHint(String hint) {
		// Not needed
	}

	// Data binding, getters & setters

	@BindingAdapter("value")
	public static void setValue(ControlCheckBoxGroupField view, Object value) {
		view.setFieldValue(value);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static Object getValue(ControlCheckBoxGroupField view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static void setListener(final ControlCheckBoxGroupField view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@BindingAdapter(value = {
		"value",
		"enumClass" })
	public static void setValue(ControlCheckBoxGroupField view, Object value, Class enumClass) {
		if (enumClass != null) {
			view.setEnumClass((Class<? extends Enum>) enumClass);
		}

		view.setFieldValue(value);
	}

	@Override
	protected void changeVisualState(VisualState state) {
		// TODO: Implement
	}
}
