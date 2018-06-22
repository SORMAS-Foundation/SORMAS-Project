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

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

@BindingMethods({@BindingMethod(type = ControlTextEditField.class, attribute = "valueFormat", method = "setValueFormat")})
public class ControlTextEditField extends ControlPropertyEditField<String> {

    // Views

    protected EditText input;

    // Attributes

    private boolean singleLine;
    private int maxLines;
    private boolean textArea;
    private int inputType;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;

    // Constructors

    public ControlTextEditField(Context context) {
        super(context);
    }

    public ControlTextEditField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlTextEditField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void setCursorToRight() {
        input.setSelection(input.getText().length());
    }

    /**
     * Handles clicks on the buttons to switch to the next view.
     */
    private void setOnEditorActionListener() {
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int definedActionId = v.getImeActionId();
                if (definedActionId == EditorInfo.IME_ACTION_NONE) {
                    return false;
                }

                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    int id = getNextFocusForwardId();
                    if (id != NO_ID) {
                        View nextView = v.getRootView().findViewById(id);
                        if (nextView != null && nextView.getVisibility() == VISIBLE) {
                            if (nextView instanceof ControlTextEditField) {
                                requestFocusForContentView(nextView);
                            } else if (nextView instanceof ControlPropertyField) {
                                ((ControlPropertyField) nextView).requestFocusForContentView(nextView);
                            } else {
                                nextView.requestFocus();
                            }
                        }
                    }

                    return true;
                }

                return false;
            }
        });
    }

    private void setOnFocusChangeListener() {
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideErrorNotifications(hasFocus);

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    if (hasFocus) {
                        changeVisualState(VisualState.FOCUSED);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                        if (onClickListener != null) {
                            input.setOnClickListener(onClickListener);
                        }
                    } else {
                        changeVisualState(VisualState.NORMAL);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        input.setOnClickListener(null);
                    }
                }
            }
        });
    }

    private void setupOnClickListener() {
        if (onClickListener != null) {
            return;
        }

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideErrorNotifications(v.hasFocus());

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    if (v.hasFocus()) {
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        };
    }

    private void showOrHideErrorNotifications(boolean hasFocus) {
        if (hasError) {
            if (hasFocus)
                showErrorNotification();
        } else {
            hideErrorNotification();

            if (hasMinorError) {
                if (hasFocus)
                    showMinorErrorNotification();
            } else {
                hideMinorErrorNotification();
            }
        }
    }

    // Overrides

    @Override
    public void setValue(String value) {
        input.setText(value);
    }

    @Override
    public String getValue() {
        if (input.getText() == null) {
            return null;
        }

        return input.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    @Override
    protected void setHint(String value) {
        input.setHint(value);
    }

    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlTextEditField,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.ControlTextEditField_singleLine, true);
                maxLines = a.getInt(R.styleable.ControlTextEditField_maxLines, 1);
                textArea = a.getBoolean(R.styleable.ControlTextEditField_textArea, true);
                inputType = a.getInt(R.styleable.ControlTextEditField_inputType, InputType.TYPE_CLASS_TEXT);
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
            inflater.inflate(R.layout.control_textfield_edit_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (EditText) this.findViewById(R.id.input);
        input.setImeOptions(getImeOptions());
        input.setImeActionLabel(null, getImeOptions());
        input.setTextAlignment(getControlTextAlignment());
        if(getControlTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getControlGravity());
        }
        setInputType(inputType);
        setSingleLine(singleLine);

        input.addTextChangedListener(new TextWatcher() {
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
        setOnFocusChangeListener();
        setupOnClickListener();
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlTextEditField)nextView).input.requestFocus();
        ((ControlTextEditField)nextView).setCursorToRight();
    }

    @Override
    public void changeVisualState(final VisualState state) {
        if (state != VisualState.DISABLED && getUserRight() != null
                && !ConfigProvider.getUser().hasUserRight(getUserRight())) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        int textColor = getResources().getColor(state.getTextColor());
        int hintColor = getResources().getColor(state.getHintColor());

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);

        if (state != VisualState.ERROR) {
            if (textColor > 0) {
                input.setTextColor(textColor);
            }
            if (hintColor > 0) {
                input.setHintTextColor(hintColor);
            }
        }

        if (state == VisualState.DISABLED) {
            setEnabled(false);
            input.setEnabled(false);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        input.setBackgroundResource(resId);
    }

    @Override
    public void setBackground(Drawable background) {
        input.setBackground(background);
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlTextEditField view, String text) {
        view.setValue(text);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static String getValue(ControlTextEditField view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlTextEditField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public String getHint() {
        return input.getHint().toString();
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    private void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;

        if (this.singleLine) {
            input.setMaxLines(1);
            input.setVerticalScrollBarEnabled(false);
        } else {
            input.setMaxLines(maxLines);
            input.setVerticalScrollBarEnabled(true);
            if (textArea) {
                input.setLines(maxLines);
            }
        }
    }

    public int getInputType() {
        return inputType;
    }

    private void setInputType(int inputType) {
        this.inputType = inputType;
        input.setInputType(inputType);
    }

}
