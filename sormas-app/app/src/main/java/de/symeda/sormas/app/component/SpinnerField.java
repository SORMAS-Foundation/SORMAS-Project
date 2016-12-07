package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.Item;

/**
 * Created by Mate Strysewske on 30.11.2016.
 */
public class SpinnerField extends PropertyField<Object> implements SpinnerFieldInterface {

    private TextView spinnerCaption;
    private Spinner spinnerElement;

    private InverseBindingListener inverseBindingListener;

    public SpinnerField(Context context) {
        super(context);
        initializeViews(context);
    }

    public SpinnerField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SpinnerField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @BindingAdapter("android:value")
    public static void setValue(SpinnerField view, Object value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static Object getValue(SpinnerField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(SpinnerField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @Override
    public void setValue(Object value) {
        setSelectedItem(value);
    }

    @Override
    public Object getValue() {
        return spinnerElement.getSelectedItem() != null ?
                ((Item)spinnerElement.getSelectedItem()).getValue() : null;
    }

    public int getCount() {
        return spinnerElement.getCount();
    }

    public Object getItemAtPosition(int i) {
        return spinnerElement.getItemAtPosition(i);
    }

    public SpinnerAdapter getAdapter() {
        return spinnerElement.getAdapter();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        spinnerElement.setOnItemSelectedListener(listener);
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return spinnerElement.getOnItemSelectedListener();
    }

    public void setSpinnerAdapter(List<Item> items) {
        ArrayAdapter<Item> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerElement.setAdapter(adapter);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.field_spinner_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        spinnerElement = (Spinner) this.findViewById(R.id.spinner_content);
        spinnerElement.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });
        spinnerCaption = (TextView) this.findViewById(R.id.spinner_caption);
        spinnerCaption.setText(getCaption());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        spinnerElement.setEnabled(enabled);
        spinnerCaption.setEnabled(enabled);
    }

    private void setSelectedItem(Object selectedItem) {
        if(spinnerElement.getAdapter() != null) {
            if(selectedItem != null) {
                for(int i = 0; i < spinnerElement.getAdapter().getCount(); i++) {
                    if(selectedItem.equals(((Item)spinnerElement.getAdapter().getItem(i)).getValue())) {
                        spinnerElement.setSelection(i);
                    }
                }
            } else {
                spinnerElement.setSelection(0);
            }
        }
    }

}
