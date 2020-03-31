/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.util.DataUtils;

public class ControlSearchableSpinnerField extends ControlPropertyEditField {

    // Views

    protected Button input;
    protected ControlSearchableSpinnerDialogFragment dialog;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    protected ControlSearchableSpinnerFieldListeners spinnerFieldListeners;

    // Other fields

    protected Object valueOnBind;
    protected int indexOnOpen = -1;
    protected boolean excludeEmptyItem;
    protected FragmentManager fragmentManager;

    // Constructors

    public ControlSearchableSpinnerField(Context context) {
        super(context);
    }

    public ControlSearchableSpinnerField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlSearchableSpinnerField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void initializeSpinner(List<Item> dataSource, final FragmentManager fm) {
        this.fragmentManager = fm;
        setSpinnerData(dataSource);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue, final FragmentManager fm) {
        this.fragmentManager = fm;
        setSpinnerData(dataSource, initialValue);
    }

    public void initializeSpinner(List<Item> dataSource, ValueChangeListener valueChangeListener, final FragmentManager fm) {
        this.fragmentManager = fm;
        setSpinnerData(dataSource);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue, ValueChangeListener valueChangeListener, final FragmentManager fm) {
        this.fragmentManager = fm;
        setSpinnerData(dataSource, initialValue);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
    }

    public void initializeSpinner(List<Item> dataSource, Object initialValue, ValueChangeListener valueChangeListener, VisualState initialState, final FragmentManager fm) {
        this.fragmentManager = fm;
        setSpinnerData(dataSource, initialValue);
        spinnerFieldListeners.registerListener(valueChangeListener, this);
        visualState = initialState;
        changeVisualState(visualState == null ? VisualState.NORMAL : visualState);
    }

    public void setSpinnerData(List<Item> items, Object selectedValue) {
        dialog.setAdapter(new ControlSearchableSpinnerAdapter(
                getContext(),
                this,
                items != null ? items : DataUtils.addEmptyItem(new ArrayList<>()),
                R.layout.control_spinner_item_layout,
                R.layout.control_spinner_dropdown_item_layout,
                R.id.text,
                excludeEmptyItem));
        setFieldValue(selectedValue);
    }

    public ArrayAdapter getAdapter() {
        return (ControlSearchableSpinnerAdapter) dialog.getAdapter();
    }

    public void setSpinnerData(List<Item> items) {
        setSpinnerData(items, valueOnBind);
    }

    protected void removeSelection() {
        ControlSearchableSpinnerAdapter adapter = dialog.getAdapter();
        dialog.setAdapter(null);
        dialog.setSelection(Spinner.INVALID_POSITION);
        dialog.setAdapter(adapter);
    }

    public int getPositionOf(Item item) {
        if (item == null) {
            return -1;
        }

        for (int i = 0; i < dialog.getAdapter().getCount(); i++) {
            Item itemAtIndex = (Item) dialog.getAdapter().getItem(i);
            if (item.getKey().equals(itemAtIndex.getKey())) {
                return i;
            }
        }

        return -1;
    }

    public Item getSelectedItem() {
        return (Item) dialog.getSelectedItem();
    }

    public void setSelection(int position) {
        dialog.setSelection(position);
    }

    // Overrides

    @Override
    protected Object getFieldValue() {
        if (dialog.getSelectedItem() != null) {
            return ((Item) dialog.getSelectedItem()).getValue();
        } else if (dialog.getAdapter() == null && valueOnBind != null) {
            return valueOnBind;
        } else {
            return null;
        }
    }

    @Override
    protected void setFieldValue(Object value) {
        if (dialog == null) {
            return;
        }

        if (value == null) {
            removeSelection();
            return;
        }

        SpinnerAdapter adapter = dialog.getAdapter();

        valueOnBind = value;
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Object itemValue = ((Item) adapter.getItem(i)).getValue();
                if (value.equals(itemValue)) {
                    dialog.setSelection(i);
                    input.setText(value.toString());
                    break;
                }
            }
        } else {
            removeSelection();
        }
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        spinnerFieldListeners = new ControlSearchableSpinnerFieldListeners();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlSpinnerField,
                    0, 0);

            try {
                excludeEmptyItem = a.getBoolean(R.styleable.ControlSpinnerField_excludeEmptyItem, false);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            inflater.inflate(R.layout.control_searchable_spinner_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (Button) this.findViewById(R.id.spinner_input);


        dialog = ControlSearchableSpinnerDialogFragment.newInstance();

        spinnerFieldListeners.registerListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
                input.setText(dialog.getSelectedItem().toString());
            }
        }, this);
        dialog.setOnItemSelectedListener(spinnerFieldListeners);

        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(fragmentManager, "dialog");
            }
        });

    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        if (((ControlSearchableSpinnerField) nextView).dialog != null)
            ((ControlSearchableSpinnerField) nextView).dialog.show(fragmentManager, "dialog");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); // this has to be called first
        input.setEnabled(enabled);
        label.setEnabled(enabled);
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
    public void setHint(String hint) {
        this.hint = hint;
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
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.SPINNER));

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);

        input.requestLayout();

        setEnabled(state != VisualState.DISABLED);
    }


    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlSearchableSpinnerField view, Object value) {
        view.setFieldValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Object getValue(ControlSearchableSpinnerField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlSearchableSpinnerField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

}
