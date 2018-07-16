package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
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

public class ControlPasswordField extends ControlPropertyEditField<String> {

    // Views

    protected TextInputLayout inputLayout;
    protected EditText input;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;

    // Constructors

    public ControlPasswordField(Context context) {
        super(context);
    }

    public ControlPasswordField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlPasswordField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    public void setCursorToRight() {
        input.setSelection(input.getText().length());
    }

    /**
     * Handles clicks on the buttons to switch to the next view.
     */
    private void setUpOnEditorActionListener() {
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

    private void setUpOnFocusChangeListener() {
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(hasFocus);

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    if (hasFocus) {
                        changeVisualState(VisualState.FOCUSED);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                        // Prevent the content from being automatically selected
                        input.setSelection(input.getText().length(), input.getText().length());
                        if (onClickListener != null) {
                            input.setOnClickListener(onClickListener);
                        }
                    } else {
                        if (hasError) {
                            changeVisualState(VisualState.ERROR);
                        } else {
                            changeVisualState(VisualState.NORMAL);
                        }
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        input.setOnClickListener(null);
                    }
                }
            }
        });
    }

    private void initializeOnClickListener() {
        if (onClickListener != null) {
            return;
        }

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(v.hasFocus());

                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    if (v.hasFocus()) {
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                        // Prevent the content from being automatically selected
                        input.setSelection(input.getText().length(), input.getText().length());
                    } else {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        };
    }

    // Overrides


    @Override
    public String getValue() {
        return (String)super.getValue();
    }

    @Override
    protected String getFieldValue() {
        if (input.getText() == null) {
            return null;
        }

        return input.getText().toString();
    }

    @Override
    protected void setFieldValue(String value) {
        input.setText(value);
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
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        // Nothing to initializeSpinner
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (isSlim()) {
                inflater.inflate(R.layout.control_password_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_password_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        inputLayout = (TextInputLayout) this.findViewById(R.id.text_input_layout);
        input = (EditText) this.findViewById(R.id.input);
        input.setImeOptions(getImeOptions());
        input.setTextAlignment(getTextAlignment());
        if(getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }
        inputLayout.setPasswordVisibilityToggleEnabled(true);

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

        setUpOnEditorActionListener();
        setUpOnFocusChangeListener();
        initializeOnClickListener();
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlPasswordField) nextView).input.requestFocus();
        ((ControlPasswordField) nextView).setCursorToRight();
    }

    @Override
    protected void changeVisualState(final VisualState state) {
        if (this.visualState == state) {
            return;
        }

        if (state != VisualState.DISABLED && getUserEditRight() != null
                && !ConfigProvider.getUser().hasUserRight(getUserEditRight())) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);
    }

    @Override
    public void setBackgroundResource(int resId) {
        setBackgroundResourceFor(input, resId);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundFor(input, background);
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlPasswordField view, String text) {
        view.setFieldValue(text);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static String getValue(ControlPasswordField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlPasswordField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

}