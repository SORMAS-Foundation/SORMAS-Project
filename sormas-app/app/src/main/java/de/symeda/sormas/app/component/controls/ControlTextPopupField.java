package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class ControlTextPopupField extends ControlPropertyEditField<String> {

    // Views

    protected TextView input;

    // Attributes

    private Object internalValue;
    private Drawable iconStart;
    private Drawable iconEnd;

    // Listeners

    protected InverseBindingListener inverseBindingListener;
    private OnClickListener onClickListener;

    // Constructors

    public ControlTextPopupField(Context context) {
        super(context);
    }

    public ControlTextPopupField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlTextPopupField(Context context, AttributeSet attrs, int defStyle) {
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

    // Overrides


    @Override
    public void setValue(Object value) {
        internalValue = value;
        setFieldValue(DataHelper.toStringNullable(value));
    }

    @Override
    public Object getValue() {
        return internalValue;
    }

    @Override
    protected void setFieldValue(String value) {
        input.setText(value);
    }

    @Override
    protected String getFieldValue() {
        if (input.getText() == null)
            return null;
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
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlTextPopupField,
                    0, 0);

            try {
                iconStart = a.getDrawable(R.styleable.ControlTextPopupField_iconStart);
                iconEnd = a.getDrawable(R.styleable.ControlTextPopupField_iconEnd);
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
            if (isSlim()) {
                inflater.inflate(R.layout.control_textfield_popup_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_textfield_popup_layout, this);
            }
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (TextView) this.findViewById(R.id.input);
        input.setImeOptions(getImeOptions());
        input.setTextAlignment(getTextAlignment());
        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            input.setGravity(getGravity());
        }

        input.setCompoundDrawablesWithIntrinsicBounds(iconStart, null, iconEnd, null);
        if (iconStart != null) {
            iconStart.setTint(getResources().getColor(R.color.control_link_edittextview_color_selector));
        }
        if (iconEnd != null) {
            iconEnd.setTint(getResources().getColor(R.color.control_link_edittextview_color_selector));
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getHint() == null) {
            setHint(I18nProperties.getFieldCaption(getFieldCaptionPropertyId()));
        }
    }


    @Override
    protected void changeVisualState(final VisualState state) {
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

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlTextEditField) nextView).input.requestFocus();
        ((ControlTextEditField) nextView).setCursorToRight();
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlTextPopupField view, String text) {
        view.setValue(text);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static String getValue(ControlTextPopupField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlTextPopupField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter("locationValue")
    public static void setLocationValue(ControlTextPopupField textPopupField, Location location) {
        textPopupField.setValue(location);
    }

    @InverseBindingAdapter(attribute = "locationValue", event = "valueAttrChanged")
    public static Location getLocationValue(ControlTextPopupField textPopupField) {
        return (Location) textPopupField.getValue();
    }
}
