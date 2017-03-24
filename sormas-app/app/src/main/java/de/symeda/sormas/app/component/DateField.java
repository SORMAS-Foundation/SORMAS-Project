package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SelectDateFragment;

/**
 * Created by Mate Strysewske on 29.11.2016.
 */

public class DateField extends PropertyField<Date> implements DateFieldInterface {

    private EditText dateContent;
    private Fragment fragment;

    private InverseBindingListener inverseBindingListener;

    public DateField(Context context) {
        super(context);
        initializeViews(context);
    }

    public DateField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public DateField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(Date value) {
        if(value != null) {
            dateContent.setText(DateHelper.formatDate(value));
        }
    }

    @Override
    public Date getValue() {
        return DateHelper.parseDate(dateContent.getText().toString());
    }

    @BindingAdapter("android:value")
    public static void setValue(DateField view, Date date) {
        view.setValue(date);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static Date getValue(DateField view) {
        return view.getValue();
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(DateField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void setInputType(int type) {
        dateContent.setInputType(type);
    }

    public void showDateFragment() {
        SelectDateFragment newFragment = new SelectDateFragment();

        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet (DatePicker view, int yy, int mm, int dd){
                dateContent.setText(DateHelper.formatDate(DateHelper.getDateZero(yy, mm, dd)));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateContent.setText(null);
                DateField.this.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(SelectDateFragment.DATE, this.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(fragment.getFragmentManager(), getResources().getText(R.string.headline_date_picker).toString());
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
        inflater.inflate(R.layout.field_date_field, this);
    }

    /**
     * Fill the model-map and fill the ui. Appends an DatePickerDialog for open on button click and the nested binding.
     */
    public void initialize(final Fragment fragment) {
        this.fragment = fragment;
        this.setInputType(InputType.TYPE_NULL);
        dateContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDateFragment();
            }
        });
        dateContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDateFragment();
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        this.clearFocus();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dateContent = (EditText) this.findViewById(R.id.date_content);
        dateContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });
        caption = (TextView) this.findViewById(R.id.date_caption);
        caption.setText(getCaption());
        addCaptionHintIfDescription();
        addCaptionOnClickListener();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateContent.setEnabled(enabled);
        caption.setEnabled(enabled);
    }

}
