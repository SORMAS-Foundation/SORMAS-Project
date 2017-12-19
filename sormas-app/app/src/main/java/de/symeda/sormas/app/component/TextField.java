package de.symeda.sormas.app.component;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.UserRightHelper;

/**
 * Created by Mate Strysewske on 28.11.2016.
 */
@InverseBindingMethods({
        @InverseBindingMethod(type = TextField.class, attribute = "integer")
})
public class TextField extends PropertyField<String> implements TextFieldInterface {

    protected EditText textContent;

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
        caption.setText(newCaption);
    }

    public void setInputType(int inputType) {
        textContent.setInputType(inputType);
    }

    public void setCursorToRight() {
        textContent.setSelection(textContent.getText().length());
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
        setOnEditorActionListener();
        caption = (TextView) this.findViewById(R.id.text_caption);
        caption.setText(getCaption());
        addCaptionOnClickListener();
    }

    @BindingAdapter("integer")
    public static void setInteger(TextField textField, Integer integer) {
        if (integer != null) {
            textField.setValue(integer.toString());
        }
    }

    /**
     * Handles hiding of the soft keyboard when custom fields are selected and management of
     * the next button
     */
    private void setOnEditorActionListener() {
        textContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    int id = getNextFocusForwardId();
                    if(id != View.NO_ID) {
                        View nextView = v.getRootView().findViewById(id);
                        if (nextView != null && nextView.getVisibility() == VISIBLE) {
                            if (!(nextView instanceof TextField)) {
                                if (nextView instanceof PropertyField) {
                                    ((PropertyField) nextView).requestFocusForContentView(nextView);
                                } else {
                                    nextView.requestFocus();
                                }
                                hideKeyboard(v);
                            } else {
                                requestFocusForContentView(nextView);
                            }
                        } else {
                            hideKeyboard(v);
                        }
                    } else {
                        hideKeyboard(v);
                    }
                    return true;
                } else {
                    hideKeyboard(v);
                    return false;
                }
            }
        });
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TextField) nextView).textContent.requestFocus();
        ((TextField) nextView).setCursorToRight();
    }
}
