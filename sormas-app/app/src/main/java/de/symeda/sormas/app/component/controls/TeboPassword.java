package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.ShowKeyboardOnEditTextFocus;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;

/**
 * Created by Orson on 01/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

@BindingMethods({
        @BindingMethod(type = TeboPassword.class, attribute = "valueFormat", method = "setValueFormat")
})
public class TeboPassword extends ControlPropertyEditField<String> {

    protected TextInputLayout txtControlInputLayout;
    protected EditText txtControlInput;
    protected InverseBindingListener inverseBindingListener;

    private boolean singleLine;
    private int maxLines;
    private int inputType;
    private boolean passwordToggleEnabled;


    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboPassword(Context context) {
        super(context);
    }

    public TeboPassword(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboPassword(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">


    @Override
    public void setValue(String value) {
        txtControlInput.setText(value);
    }

    public void setValue(Float value) {
        if (value == null)
            return;

        if (value.toString() != getValue()) {
            setValue(value.toString());
        }
    }

    public void setValue(Integer value) {
        if (value == null)
            return;

        if (value.toString() != getValue()) {
            setValue(value.toString());
        }
    }

    @Override
    public String getValue() {
        if (txtControlInput.getText() == null)
            return null;

        return txtControlInput.getText().toString();
    }

    @BindingAdapter("value")
    public static void setValue(TeboPassword view, String text) {
        if (text != view.getValue()) {
            view.setValue(text);
        }
    }

    @BindingAdapter("value")
    public static void setValue(TeboPassword view, Float floatValue) {
        if (floatValue.toString() != view.getValue()) {
            view.setValue(floatValue);
        }
    }

    @BindingAdapter("value")
    public static void setValue(TeboPassword view, Integer intValue) {
        if (intValue.toString() != view.getValue()) {
            view.setValue(intValue);
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static String getValue(TeboPassword view) {
        return view.getValue();
    }

    @BindingAdapter("integerValue")
    public static void setIntegerValue(TeboPassword textField, Integer integer) {
        if (integer == null)
            return;

        if (integer.toString() != textField.getValue()) {
            textField.setValue(integer.toString());
        }
    }

    @InverseBindingAdapter(attribute = "integerValue", event = "valueAttrChanged")
    public static Integer getIntegerValue(TeboPassword view) {
        try {
            return Integer.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("floatValue")
    public static void setFloatValue(TeboPassword textField, Float floatValue) {
        if (floatValue == null)
            return;

        if (floatValue.toString() != textField.getValue()) {
            textField.setValue(floatValue.toString());
        }
    }

    @InverseBindingAdapter(attribute = "floatValue", event = "valueAttrChanged")
    public static Float getFloatValue(TeboPassword view) {
        try {
            return Float.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboPassword view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter(value={"inputType", "singleLine"}, requireAll=true)
    public static void setValue(TeboPassword view, int inputType, boolean singleLine) {
        view.setInputType(inputType);
        view.setSingleLine(singleLine);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtControlInput.setEnabled(enabled);
        label.setEnabled(enabled);

    }

    @Override
    protected void setHint(String value) {
        txtControlInput.setHint(value);
    }

    public String getHint() {
        return txtControlInput.getHint().toString();
    }

    public void setCursorToRight() {
        txtControlInput.setSelection(txtControlInput.getText().length());
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    private void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        txtControlInput.setSingleLine(this.singleLine);

        if (this.singleLine) {
            txtControlInput.setMaxLines(1);
            //input.setLines(1);
            txtControlInput.setInputType(inputType);
            txtControlInput.setVerticalScrollBarEnabled(false);
        } else {
            txtControlInput.setMaxLines(maxLines);
            //input.setLines(maxLines);
            txtControlInput.setInputType(inputType);
            txtControlInput.setVerticalScrollBarEnabled(true);
        }
    }

    public int getInputType() {
        return inputType;
    }

    private void setInputType(int inputType) {
        this.inputType = inputType;
        txtControlInput.setInputType(inputType);
    }

    // </editor-fold>

    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboPassword,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.TeboPassword_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboPassword_maxLines, 1);
                inputType = a.getInt(R.styleable.TeboPassword_inputType, InputType.TYPE_CLASS_TEXT);
                passwordToggleEnabled = a.getBoolean(R.styleable.TeboPassword_passwordToggleEnabled, false);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_password_layout, this);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInputLayout = (TextInputLayout) this.findViewById(R.id.text_input_layout);
        txtControlInput = (EditText) this.findViewById(R.id.input);

        txtControlInput.setImeOptions(getImeOptions());

        txtControlInput.setTextAlignment(getControlTextAlignment());
        if(getControlTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getControlGravity());
        }

        txtControlInput.addTextChangedListener(new TextWatcher() {
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

        ShowKeyboardOnEditTextFocus showKeyboardOnEditTextFocus = new ShowKeyboardOnEditTextFocus(getContext());

        CompositeOnFocusChangeListener txtControlInputListeners = new CompositeOnFocusChangeListener();
        txtControlInputListeners.registerListener(showKeyboardOnEditTextFocus);
        //txtControlInputListeners.registerListener(new ChangeLabelColorOnEditTextFocus(getContext(), label));
        txtControlInputListeners.registerListener(new OnFocusChangeListenerHandler(this));


        OnEditTextClickHandler editTextClickHandler = new OnEditTextClickHandler(getContext(), this);

        txtControlInput.setOnFocusChangeListener(txtControlInputListeners);
        txtControlInput.setOnClickListener(editTextClickHandler);

        setInputType(inputType);
        setSingleLine(singleLine);

        txtControlInputLayout.setPasswordVisibilityToggleEnabled(passwordToggleEnabled);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            int paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            int paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            int paddingLeft = txtControlInput.getPaddingLeft();
            int paddingRight = txtControlInput.getPaddingRight();

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Overriden from Base">

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboPassword)nextView).txtControlInput.requestFocus();
        ((TeboPassword)nextView).setCursorToRight();
    }

    // </editor-fold>

    /**
     * Handles hiding of the soft keyboard when custom fields are selected and management of
     * the next button
     */
    private void setOnEditorActionListener() {
        txtControlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    int id = getNextFocusForwardId();
                    if(id != View.NO_ID) {
                        View nextView = v.getRootView().findViewById(id);
                        if (nextView != null && nextView.getVisibility() == VISIBLE) {
                            if (!(nextView instanceof TeboPassword)) {
                                if (nextView instanceof ControlPropertyField) {
                                    ((ControlPropertyField) nextView).requestFocusForContentView(nextView);
                                } else {
                                    nextView.requestFocus();
                                }
//                                hideKeyboard(v);
                            } else {
                                requestFocusForContentView(nextView);
                            }
                        } else {
//                            hideKeyboard(v);
                        }
                    } else {
//                        hideKeyboard(v);
                    }
                    return true;
                } else {
//                    hideKeyboard(v);
                    return false;
                }
            }
        });
    }


    @Override
    public void changeVisualState(final VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (state == VisualState.DISABLED) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            txtControlInput.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.NORMAL) {
            User user = ConfigProvider.getUser();
            label.setTextColor(labelColor);
            setBackground(drawable);
//            txtControlInput.setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
            return;
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackgroundResource(resid);

        txtControlInput.setPadding(pl, pt, pr, pb);
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackground(background);

        txtControlInput.setPadding(pl, pt, pr, pb);
    }

    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboPassword inputEditText;

        public OnFocusChangeListenerHandler(TeboPassword inputEditText) {
            this.inputEditText = inputEditText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!v.isEnabled())
                return;

            if (hasError) {
                //changeVisualState(VisualState.ERROR);
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
            }

            //int colorOnFocus = v.getResources().getColor(VisualState.FOCUSED.getLabelColor());
            //int colorDefault = v.getResources().getColor(VisualState.NORMAL.getLabelColor());

            if (hasFocus) {
                changeVisualState(VisualState.FOCUSED);
                //label.setTextColor(colorOnFocus);
            } else {
                changeVisualState(VisualState.NORMAL);
                //label.setTextColor(colorDefault);
            }

        }
    }

    private class OnEditTextClickHandler implements View.OnClickListener {

        private Context context;
        private TeboPassword inputEditText;

        public OnEditTextClickHandler(Context context, TeboPassword inputEditText) {
            this.context = context;
            this.inputEditText = inputEditText;
        }
        @Override
        public void onClick(View v) {
            if(!v.isEnabled())
                return;

            if (hasError) {
                //changeVisualState(VisualState.ERROR);
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
            }

            InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (v.hasFocus()) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            } else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

}