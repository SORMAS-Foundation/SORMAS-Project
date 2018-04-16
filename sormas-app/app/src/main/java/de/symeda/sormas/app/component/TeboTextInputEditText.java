package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;

/**
 * Created by Orson on 23/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

@BindingMethods({
        @BindingMethod(type = TeboTextInputEditText.class, attribute = "valueFormat", method = "setValueFormat")
})
public class TeboTextInputEditText extends EditTeboPropertyField<String> implements ITextControlInterface, IControlValueRequireable {

    protected EditText txtControlInput;
    protected InverseBindingListener inverseBindingListener;

    private String hint;
    private boolean singleLine;
    private int maxLines;
    private boolean textArea;
    private int inputType;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextInputEditText(Context context) {
        super(context);
    }

    public TeboTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextInputEditText(Context context, AttributeSet attrs, int defStyle) {
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
    public static void setValue(TeboTextInputEditText view, String text) {
        if (text != view.getValue()) {
            view.setValue(text);
        }
    }

    @BindingAdapter("value")
    public static void setValue(TeboTextInputEditText view, Float floatValue) {
        if (floatValue.toString() != view.getValue()) {
            view.setValue(floatValue);
        }
    }

    @BindingAdapter("value")
    public static void setValue(TeboTextInputEditText view, Integer intValue) {
        if (intValue.toString() != view.getValue()) {
            view.setValue(intValue);
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static String getValue(TeboTextInputEditText view) {
        return view.getValue();
    }

    @BindingAdapter("integerValue")
    public static void setIntegerValue(TeboTextInputEditText textField, Integer integer) {
        if (integer == null)
            return;

        if (integer.toString() != textField.getValue()) {
            textField.setValue(integer.toString());
        }
    }

    @InverseBindingAdapter(attribute = "integerValue", event = "valueAttrChanged")
    public static Integer getIntegerValue(TeboTextInputEditText view) {
        try {
            return Integer.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("floatValue")
    public static void setFloatValue(TeboTextInputEditText textField, Float floatValue) {
        if (floatValue == null)
            return;

        if (floatValue.toString() != textField.getValue()) {
            textField.setValue(floatValue.toString());
        }
    }

    @InverseBindingAdapter(attribute = "floatValue", event = "valueAttrChanged")
    public static Float getFloatValue(TeboTextInputEditText view) {
        try {
            return Float.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboTextInputEditText view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter(value={"inputType", "singleLine"}, requireAll=true)
    public static void setValue(TeboTextInputEditText view, int inputType, boolean singleLine) {
        view.setInputType(inputType);
        view.setSingleLine(singleLine);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtControlInput.setEnabled(enabled);
        lblControlLabel.setEnabled(enabled);

    }

    public void setHint(String value) {
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
            //txtControlInput.setLines(1);
            txtControlInput.setInputType(inputType);
            txtControlInput.setVerticalScrollBarEnabled(false);
        } else {
            txtControlInput.setMaxLines(maxLines);

            if (textArea)
                txtControlInput.setLines(maxLines);

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
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTextInputEditText,
                    0, 0);

            try {
                hint = a.getString(R.styleable.TeboTextInputEditText_hint);
                singleLine = a.getBoolean(R.styleable.TeboTextInputEditText_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboTextInputEditText_maxLines, 1);
                textArea = a.getBoolean(R.styleable.TeboTextInputEditText_textArea, true);
                inputType = a.getInt(R.styleable.TeboTextInputEditText_inputType, InputType.TYPE_CLASS_TEXT);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_textfield_edit_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (EditText) this.findViewById(R.id.txtControlInput);

        txtControlInput.setNextFocusLeftId(getNextFocusLeft());
        txtControlInput.setNextFocusRightId(getNextFocusRight());
        txtControlInput.setNextFocusUpId(getNextFocusUp());
        txtControlInput.setNextFocusDownId(getNextFocusDown());
        txtControlInput.setNextFocusForwardId(getNextFocusForward());

        setImeOptions();

        txtControlInput.setTextAlignment(getCaptionAlignment());
        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getCaptionGravity());
            //if (textArea) txtControlInput.setGravity(Gravity.TOP | Gravity.START);
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

        //Set Hint
        txtControlInput.setHint(hint);

        ShowKeyboardOnEditTextFocus showKeyboardOnEditTextFocus = new ShowKeyboardOnEditTextFocus(getContext());

        CompositeOnFocusChangeListener txtControlInputListeners = new CompositeOnFocusChangeListener();
        txtControlInputListeners.registerListener(showKeyboardOnEditTextFocus);
        //txtControlInputListeners.registerListener(new ChangeLabelColorOnEditTextFocus(getContext(), lblControlLabel));
        txtControlInputListeners.registerListener(new OnFocusChangeListenerHandler(this));


        OnEditTextClickHandler editTextClickHandler = new OnEditTextClickHandler(getContext(), this);

        txtControlInput.setOnFocusChangeListener(txtControlInputListeners);
        txtControlInput.setOnClickListener(editTextClickHandler);

        setInputType(inputType);
        setSingleLine(singleLine);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        updateControlDimensions();

        /*if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            int paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            int paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            int paddingLeft = txtControlInput.getPaddingLeft();
            int paddingRight = txtControlInput.getPaddingRight();

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
            txtControlInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        } else {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
        }*/
    }

    private void updateControlDimensions() {
        int paddingTop = 0, paddingBottom = 0, paddingLeft = 0, paddingRight = 0;
        if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            txtControlInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        } else if (singleLine) {
            paddingTop = 0; paddingBottom = 0;
        } else {
            paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.textViewTopPadding);
            paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.textViewBottomPadding);
        }

        paddingLeft = txtControlInput.getPaddingLeft();
        paddingRight = txtControlInput.getPaddingRight();
        txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        updateControlHeight();
    }

    private void updateControlHeight() {
        if (isSlim()) {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
        } else if (singleLine) {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
        } else {
            ViewGroup.LayoutParams params = txtControlInput.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Overriden from Base">

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboTextInputEditText)nextView).txtControlInput.requestFocus();
        ((TeboTextInputEditText)nextView).setCursorToRight();
    }

    @Override
    public void updateCaption(String newCaption) {
        setCaption(newCaption);
    }

    // </editor-fold>



    @Override
    public void changeVisualState(final VisualState state, UserRight editOrCreateUserRight) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.EDIT_TEXT));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.EDIT_TEXT));
        int textColor = state.getTextColor(VisualStateControl.EDIT_TEXT);
        int hintColor = state.getHintColor(VisualStateControl.EDIT_TEXT);

        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            lblControlLabel.setTextColor(textColor);
            return;
        }

        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
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
        updateControlHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackground(background);

        txtControlInput.setPadding(pl, pt, pr, pb);
        updateControlHeight();
    }

    @Override
    public boolean isRequiredStatusValid() {
        if (!isRequired())
            return true;

        return getValue() != "";
    }

    //<editor-fold desc="Private Methods">
    private void setImeOptions() {
        txtControlInput.setImeOptions(getImeOptions());
        txtControlInput.setImeActionLabel(null, getImeOptions());
    }

    /**
     * Handles hiding of the soft keyboard when custom fields are selected and management of
     * the next button
     */
    private void setOnEditorActionListener() {
        txtControlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int _actionId = v.getImeActionId();

                if (_actionId == EditorInfo.IME_ACTION_NONE)
                    return false;

                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    int id = getNextFocusForwardId();
                    if(id != View.NO_ID) {
                        View nextView = v.getRootView().findViewById(id);
                        if (nextView != null && nextView.getVisibility() == VISIBLE) {
                            if (!(nextView instanceof TeboTextInputEditText)) {
                                if (nextView instanceof TeboPropertyField) {
                                    ((TeboPropertyField) nextView).requestFocusForContentView(nextView);
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

    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboTextInputEditText inputEditText;

        public OnFocusChangeListenerHandler(TeboTextInputEditText inputEditText) {
            this.inputEditText = inputEditText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!v.isEnabled())
                return;

            if (inErrorState()) {
                //changeVisualState(VisualState.ERROR);
                if (hasFocus)
                    showNotification(hasFocus);

                return;
            } else {
                hideNotification();
            }

            //int colorOnFocus = v.getResources().getColor(VisualState.FOCUSED.getLabelColor());
            //int colorDefault = v.getResources().getColor(VisualState.NORMAL.getLabelColor());

            if (hasFocus) {
                changeVisualState(VisualState.FOCUSED);
                //lblControlLabel.setTextColor(colorOnFocus);
            } else {
                changeVisualState(VisualState.NORMAL);
                //lblControlLabel.setTextColor(colorDefault);
            }

        }
    }

    private class OnEditTextClickHandler implements View.OnClickListener {

        private Context context;
        private TeboTextInputEditText inputEditText;

        public OnEditTextClickHandler(Context context, TeboTextInputEditText inputEditText) {
            this.context = context;
            this.inputEditText = inputEditText;
        }
        @Override
        public void onClick(View v) {
            if(!v.isEnabled())
                return;

            if (inErrorState()) {
                //changeVisualState(VisualState.ERROR);
                if(v.hasFocus())
                    showNotification();

                return;
            } else {
                hideNotification();
            }

            InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (v.hasFocus()) {
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            } else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
    //</editor-fold>

}
