package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.util.DataUtils;

public class ControlSpinnerField extends ControlPropertyEditField<Object> {

    // Views

    protected Spinner input;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private ControlSpinnerFieldListeners spinnerFieldListeners;

    // Other fields

    private Object valueOnBind;
    private int indexOnOpen;

    // Constructors

    public ControlSpinnerField(Context context) {
        super(context);
    }

    public ControlSpinnerField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlSpinnerField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void initializeSpinner(List<Item> dataSource) {
        setSpinnerData(dataSource);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue) {
        setSpinnerData(dataSource, initialValue);
    }

    public void initializeSpinner(List<Item> dataSource, ValueChangeListener valueChangeListener) {
        setSpinnerData(dataSource);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue, ValueChangeListener valueChangeListener) {
        setSpinnerData(dataSource, initialValue);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue, ValueChangeListener valueChangeListener, VisualState initialState) {
        setSpinnerData(dataSource, initialValue);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
        visualState = initialState;
        changeVisualState(visualState == null ? VisualState.NORMAL : visualState);
    }

    public void setSpinnerData(List<Item> items, Object selectedValue) {
        input.setAdapter(new ControlSpinnerAdapter(
                getContext(),
                this,
                items != null ? items : DataUtils.addEmptyItem(new ArrayList<Item>()),
                R.layout.control_spinner_item_layout,
                R.layout.control_spinner_dropdown_item_layout,
                R.id.text));
        setFieldValue(selectedValue);
    }

    public ArrayAdapter getAdapter() {
        return (ControlSpinnerAdapter)input.getAdapter();
    }

    public void setSpinnerData(List<Item> items) {
        setSpinnerData(items, valueOnBind);
    }

    private void removeSelection() {
        SpinnerAdapter adapter = input.getAdapter();
        input.setAdapter(null);
        input.setSelection(Spinner.INVALID_POSITION);
        input.setAdapter(adapter);
    }

    public int getPositionOf(Item item) {
        if (item == null) {
            return -1;
        }

        for (int i = 0; i < input.getAdapter().getCount(); i++) {
            Item itemAtIndex = (Item) input.getAdapter().getItem(i);
            if (item.getKey().equals(itemAtIndex.getKey())) {
                return i;
            }
        }

        return -1;
    }

    public Item getSelectedItem() {
        return (Item) input.getSelectedItem();
    }

    // Overrides

    @Override
    protected Object getFieldValue() {
        if (input.getSelectedItem() != null) {
            return ((Item) input.getSelectedItem()).getValue();
        } else if (input.getAdapter() == null && valueOnBind != null) {
            return valueOnBind;
        } else {
            return null;
        }
    }

    @Override
    protected void setFieldValue(Object value) {
        if (input == null) {
            return;
        }

        if (value == null) {
            removeSelection();
            return;
        }

        SpinnerAdapter adapter = input.getAdapter();

        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Object itemValue = ((Item) adapter.getItem(i)).getValue();
                if (value.equals(itemValue)) {
                    input.setSelection(i);
                    break;
                }
            }
        } else {
            valueOnBind = value;
            removeSelection();
        }
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        spinnerFieldListeners = new ControlSpinnerFieldListeners();
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (isSlim()) {
                inflater.inflate(R.layout.control_spinner_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_spinner_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (Spinner) this.findViewById(R.id.input);

        spinnerFieldListeners.registerListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        }, this);
        input.setOnItemSelectedListener(spinnerFieldListeners);

        input.setFocusable(true);
        input.setFocusableInTouchMode(true);
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (getValue() == null) {
                        input.setSelection(indexOnOpen);
                    }
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    if (input.isShown()) {
                        input.performClick();
                    }
                }
            }
        });
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlSpinnerField) nextView).input.requestFocus();
    }

    @Override
    public void setEnabled(boolean enabled) {
        input.setEnabled(enabled);
        label.setEnabled(enabled);
        super.setEnabled(enabled);
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
    protected void setHint(String hint) {
        // Hint is handled in adapter
    }

    @Override
    protected void changeVisualState(VisualState state) {
        if (this.visualState == state) {
            return;
        }

        if (state != VisualState.DISABLED && getUserEditRight() != null
                && !ConfigProvider.getUser().hasUserRight(getUserEditRight())) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.SPINNER));

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);

        input.requestLayout();
    }

    public void setSelectionOnOpen(Object object) {
        for (int i = 0; i < getAdapter().getCount(); i++) {
            if (object.equals(((Item) getAdapter().getItem(i)).getValue())) {
                indexOnOpen = i;
                break;
            }
        }
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlSpinnerField view, Object value) {
        view.setFieldValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Object getValue(ControlSpinnerField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlSpinnerField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

}
