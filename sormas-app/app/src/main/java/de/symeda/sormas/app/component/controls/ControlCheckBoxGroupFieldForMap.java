package de.symeda.sormas.app.component.controls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.ColorStateList;
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

public class ControlCheckBoxGroupFieldForMap<T extends Enum<?>> extends ControlPropertyEditField<Map<T, Boolean>> {

	private final Map<T, CheckBox> checkBoxes = new HashMap<>();
	private InverseBindingListener inverseBindingListener;
	private Class<? extends Enum<?>> enumClass = null;
	private LinearLayout checkBoxesFrame;

	// Constructors
	public ControlCheckBoxGroupFieldForMap(Context context) {
		super(context);
	}

	public ControlCheckBoxGroupFieldForMap(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlCheckBoxGroupFieldForMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setEnumClass(Class<T> c) {
		if (!DataHelper.equal(c, enumClass)) {
			suppressListeners = true;
			removeAllItems();

			List<Item<T>> items = (List<Item<T>>) DataUtils.buildEnumItems(c, false, null);

			int itemTotal = items.size();
			for (int i = 0; i < items.size(); i++) {
				addItem(i, itemTotal - 1, items.get(i));
			}

			enumClass = c;
			suppressListeners = false;
		}
	}

	private void addItem(int index, int lastIndex, Item<T> item) {
		final CheckBox checkBox = createCheckBox(index, lastIndex, item);
		checkBoxesFrame.addView(checkBox);
		checkBoxes.put(item.getValue(), checkBox);
	}

	private CheckBox createCheckBox(int index, int lastIndex, Item<T> item) {
		CheckBox checkBox = new CheckBox(new ContextThemeWrapper(getContext(), R.style.ControlCheckboxStyle));
		checkBox.setId(index);
		checkBox.setText(item.getKey());
		checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		checkBox.setOnCheckedChangeListener((b, i) -> {
			onValueChanged();
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
	protected Map<T, Boolean> getFieldValue() {
		Map<T, Boolean> selectedElements = new HashMap<>();

		for (T key : checkBoxes.keySet()) {
			if (checkBoxes.get(key).isChecked()) {
				selectedElements.put(key, true);
			}
		}

		return selectedElements;
	}

	@Override
	protected void setFieldValue(Map<T, Boolean> value) {
		if (value == null) {
			uncheckAll();
		} else {
			for (Map.Entry<T, Boolean> element : value.entrySet()) {
				CheckBox checkBox = checkBoxes.get(element.getKey());

				if (checkBox == null) {
					throw new IllegalArgumentException(
						"Passed list arguments contains an element that is not part of this ControlCheckBoxGroupField");
				}

				checkBox.setChecked(element.getValue());
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		int textColor = getResources().getColor(VisualState.NORMAL.getTextColor());
		checkBoxes.forEach((t, checkBox) -> {
			checkBox.setEnabled(enabled);
			checkBox.setTextColor(textColor);
			checkBox.setButtonTintList(ColorStateList.valueOf(textColor));
		});
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
	public static <E extends Enum<?>> void setValue(ControlCheckBoxGroupFieldForMap<E> view, Map<E, Boolean> value) {
		view.setFieldValue(value);
	}

	@InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
	public static <E extends Enum<?>> Map<E, Boolean> getValue(ControlCheckBoxGroupFieldForMap<E> view) {
		return view.getFieldValue();
	}

	@BindingAdapter("valueAttrChanged")
	public static <E extends Enum<?>> void setListener(final ControlCheckBoxGroupFieldForMap<E> view, InverseBindingListener listener) {
		view.inverseBindingListener = listener;
	}

	@BindingAdapter(value = {
		"value",
		"enumClass" })
	public static <E extends Enum<?>> void setValue(ControlCheckBoxGroupFieldForMap<E> view, Map<E, Boolean> value, Class<E> enumClass) {
		if (enumClass != null) {
			view.setEnumClass(enumClass);
		}
		view.setFieldValue(value);
	}

	@Override
	protected void changeVisualState(VisualState state) {
	}
}
