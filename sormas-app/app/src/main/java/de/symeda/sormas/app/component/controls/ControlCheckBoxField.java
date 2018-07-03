package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;

public class ControlCheckBoxField extends ControlPropertyEditField<Boolean> {

    // Views

    protected CheckBox input;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;

    // Constructors

    public ControlCheckBoxField(Context context) {
        super(context);
    }

    public ControlCheckBoxField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlCheckBoxField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    private void setUpOnFocusChangeListener() {
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(hasFocus);

                if (hasFocus) {
                    changeVisualState(VisualState.FOCUSED);
                    if (onClickListener != null) {
                        input.setOnClickListener(onClickListener);
                    }
                } else {
                    if (hasError) {
                        changeVisualState(VisualState.ERROR);
                    } else {
                        changeVisualState(VisualState.NORMAL);
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
            }
        };
    }

    public void setStateColor(int checkedColor, int uncheckedColor) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                uncheckedColor,
                checkedColor,
        };

        input.setBackgroundTintList(new ColorStateList(states, thumbColors));
    }

    // Overrides

    @Override
    public void setValue(Boolean value) {
        input.setChecked(value);
    }

    @Override
    public Boolean getValue() {
        return input.isChecked();
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
            inflater.inflate(R.layout.control_checkbox_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (CheckBox) this.findViewById(R.id.checkbox);
        input.setImeOptions(getImeOptions());
        input.setTextAlignment(getTextAlignment());
        if(getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }

        input.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });

        setUpOnFocusChangeListener();
        initializeOnClickListener();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlCheckBoxField) nextView).input.requestFocus();
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
    public void changeVisualState(VisualState state) {
        if (state != VisualState.DISABLED && getUserEditRight() != null
                && !ConfigProvider.getUser().hasUserRight(getUserEditRight())) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        label.setTextColor(labelColor);

        if (state == VisualState.DISABLED) {
            setStateColor(labelColor, labelColor);
            input.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            setStateColor(labelColor, labelColor);
            return;
        }

        int uncheckedStateColor = getResources().getColor(R.color.colorControlNormal);
        int checkedStateColor = getResources().getColor(R.color.colorControlActivated);

        if (state == VisualState.FOCUSED || state == VisualState.NORMAL) {
            setStateColor(checkedStateColor, uncheckedStateColor);
        }
    }

    @Override
    protected void setHint(String hint) {
        // Checkboxes don't have hints
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlCheckBoxField view, boolean value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static boolean getValue(ControlCheckBoxField view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlCheckBoxField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

}
