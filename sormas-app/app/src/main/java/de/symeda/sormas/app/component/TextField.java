package de.symeda.sormas.app.component;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;

/**
 * Created by Mate Strysewske on 28.11.2016.
 */
@InverseBindingMethods({
        @InverseBindingMethod(type = TextField.class, attribute = "integer")
})
public class TextField extends PropertyField<String> implements TextFieldInterface {

    protected EditText textContent;
    protected TextView textCaption;

    protected InverseBindingListener inverseBindingListener;

    public TextField(Context context) {
        super(context);
        initializeViews(context);
    }

    public TextField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public TextField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    public void setValue(String value) {
        textContent.setText(value);
    }

    @Override
    public String getValue() {
        return textContent.getText().toString();
    }

    @BindingAdapter("android:value")
    public static void setValue(TextField view, String text) {
        view.setValue(text);
    }

    @InverseBindingAdapter(attribute = "android:value", event = "android:valueAttrChanged" /*default - can also be removed*/)
    public static String getValue(TextField view) {
        return view.getValue();
    }

    @InverseBindingAdapter(attribute = "integer", event = "android:valueAttrChanged")
    public static Integer getInteger(TextField view) {
        try {
            return Integer.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("android:valueAttrChanged")
    public static void setListener(TextField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void updateCaption(String newCaption) {
        textCaption.setText(newCaption);
    }

    public void setInputType(int inputType) {
        textContent.setInputType(inputType);
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
        inflater.inflate(R.layout.field_text_field, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textContent = (EditText) this.findViewById(R.id.text_input);
        textContent.addTextChangedListener(new TextWatcher() {
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
        textCaption = (TextView) this.findViewById(R.id.text_caption);
        textCaption.setText(getCaption());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textContent.setEnabled(enabled);
        textCaption.setEnabled(enabled);
    }

    @BindingAdapter("app:integer")
    public static void setInteger(TextField textField, Integer integer) {
        if (integer != null) {
            textField.setValue(integer.toString());
        }
    }

    @BindingAdapter("app:location")
    public static void setLocationForTextField(TextField textField, Location location) {
        if(location != null) {
            textField.setValue(location.toString());
        }
    }

}
