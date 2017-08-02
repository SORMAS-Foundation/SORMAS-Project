package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.Item;

/**
 * Created by Mate Strysewske on 30.11.2016.
 */
public class SpinnerField extends PropertyField<Object> implements SpinnerFieldInterface {

    private Spinner spinnerElement;
    private InverseBindingListener inverseBindingListener;
    private SpinnerFieldListener spinnerFieldListener = new SpinnerFieldListener();

    private int indexOnOpen;

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
        return (spinnerElement.getSelectedItem() != null) ?
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

    public void registerListener(OnItemSelectedListener listener) {
        spinnerFieldListener.registerListener(listener);
    }

    public Spinner getSpinnerElement() {
        return spinnerElement;
    }

//    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
//        spinnerElement.setOnItemSelectedListener(listener);
//    }
//
//    public OnItemSelectedListener getOnItemSelectedListener() {
//        return spinnerElement.getOnItemSelectedListener();
//    }

    public void setSpinnerAdapter(List<Item> items) {
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getContext(),
                android.R.layout.simple_spinner_item,
                items) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()).getKey());
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(Color.LTGRAY);
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new Item(this.getContext().getString(R.string.hint_select_entry), null));
        spinnerElement.setAdapter(adapter);
        spinnerElement.setSelection(adapter.getCount());
    }

    public void setOnsetSymptomSpinnerAdapter(List<Item> items) {
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getContext(),
                android.R.layout.simple_spinner_item,
                items) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()).getKey());
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(Color.LTGRAY);
                }

                return v;
            }
             @Override
             public int getCount() {
                        return super.getCount() - 1;
                    }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new Item(this.getContext().getString(R.string.hint_select_entry), null));
        spinnerElement.setAdapter(adapter);
        spinnerElement.setSelection(adapter.getCount());
    }

    public void setMonthSpinnerAdapter(List<Item> items) {
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getContext(),
                android.R.layout.simple_spinner_item,
                items) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()).getKey());
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(Color.LTGRAY);
                } else if (position > 0) {
                    TextView tv = ((TextView) v);
                    tv.setText(I18nProperties.getEnumCaption(Month.values()[position - 1]));
                }

                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position > 0 && position < getCount()) {
                    TextView tv = ((TextView) v);
                    tv.setText(I18nProperties.getEnumCaption(Month.values()[position - 1]));
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add(new Item(this.getContext().getString(R.string.hint_select_entry), null));
        spinnerElement.setAdapter(adapter);
        spinnerElement.setSelection(adapter.getCount());
    }

    public void initialize(List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        this.setSpinnerAdapter(items);
        for (AdapterView.OnItemSelectedListener listener : moreListeners) {
            this.registerListener(listener);
        }
    }

    public void initializeForOnsetSymptom(List<Item> items) {
        this.setOnsetSymptomSpinnerAdapter(items);
    }

    public void initializeForMonth(List<Item> items, final AdapterView.OnItemSelectedListener[] moreListeners) {
        this.setMonthSpinnerAdapter(items);
        for (AdapterView.OnItemSelectedListener listener : moreListeners) {
            this.registerListener(listener);
        }
    }

    /**
     * Update the spinner list and set selected value.
     * @param selectedItem
     * @param items
     */
    public void setAdapterAndValue(Object selectedItem, List<Item> items) {
        this.setSpinnerAdapter(items);
        this.setValue(selectedItem);
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
        spinnerFieldListener.registerListener(new Spinner.OnItemSelectedListener() {
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
        spinnerElement.setOnItemSelectedListener(spinnerFieldListener);
        spinnerElement.setFocusable(true);
        spinnerElement.setFocusableInTouchMode(true);
        spinnerElement.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if (spinnerElement.getSelectedItemPosition() == spinnerElement.getAdapter().getCount()) {
                        spinnerElement.setSelection(indexOnOpen);
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    spinnerElement.performClick();
                } else {
                    if (spinnerElement.getSelectedItemPosition() == 0) {
                        spinnerElement.setSelection(spinnerElement.getAdapter().getCount());
                    }
                }
            }
        });
        caption = (TextView) this.findViewById(R.id.spinner_caption);
        caption.setText(getCaption());
        addCaptionHintIfDescription();
        addCaptionOnClickListener();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        spinnerElement.setEnabled(enabled);
        caption.setEnabled(enabled);
    }

    public void setSelectionOnOpen(Object object) {
        for(int i = 0; i < spinnerElement.getAdapter().getCount(); i++) {
            if(object.equals(((Item)spinnerElement.getAdapter().getItem(i)).getValue())) {
                indexOnOpen = i;
                break;
            }
        }
    }

    public int getPositionOf(Item item) {
        for (int i = 0; i < spinnerElement.getAdapter().getCount(); i++) {
            if (item.getKey().equals(((Item) spinnerElement.getAdapter().getItem(i)).getKey())) {
                return i;
            }
        }

        return -1;
    }

    public void selectLastEntry() {
        spinnerElement.setSelection(spinnerElement.getCount());
    }

    public Item getSelectedItem() {
        return (Item) spinnerElement.getSelectedItem();
    }

    private void setSelectedItem(Object selectedItem) {
        if (selectedItem == null) {
            spinnerElement.setSelection(spinnerElement.getAdapter().getCount());
        } else {
            for (int i = 0; i < spinnerElement.getAdapter().getCount(); i++) {
                if (selectedItem.equals(((Item) spinnerElement.getAdapter().getItem(i)).getValue())) {
                    spinnerElement.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((SpinnerField) nextView).spinnerElement.requestFocus();
    }

}
