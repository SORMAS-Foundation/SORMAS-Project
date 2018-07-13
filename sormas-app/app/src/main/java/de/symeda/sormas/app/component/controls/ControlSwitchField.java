package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.StateDrawableBuilder;
import de.symeda.sormas.app.util.DataUtils;

public class ControlSwitchField extends ControlPropertyEditField<Object> {

    // Constants

    private static final String FONT_FAMILY = "sans-serif-medium";

    // Views

    private RadioGroup input;

    // Attributes

    private boolean useAbbreviations;
    private Drawable background;
    private ColorStateList textColor;

    // Listeners

    private InverseBindingListener inverseBindingListener;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    // Other fields

    private List<Object> radioGroupElements = new ArrayList<>();
    private boolean enumClassSet = false;

    // Constructors

    public ControlSwitchField(Context context) {
        super(context);
    }

    public ControlSwitchField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlSwitchField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Instance methods

    private void setChildViewsEnabledState() {
        for (int i = 0; i < input.getChildCount(); i++) {
            RadioButton button = (RadioButton) input.getChildAt(i);
            setChildViewEnabledState(button);
        }
    }

    private void setChildViewEnabledState(RadioButton button) {
        button.setEnabled(input.isEnabled());
        button.setClickable(input.isEnabled());
    }

    @SuppressWarnings("unchecked")
    public void setEnumClass(Class c) {
        if (!enumClassSet) {
            List<Item> items = DataUtils.getEnumItems(c, false);

            int itemTotal = items.size();
            for (int i = 0; i < items.size(); i++) {
                addItem(i, itemTotal - 1, items.get(i));
            }

            enumClassSet = true;
        }
    }

    private void addItem(int index, int lastIndex, Item item) {
        final RadioButton button = createRadioButton(index, lastIndex, item);
        input.addView(button);
        radioGroupElements.add(item.getValue());
        setChildViewEnabledState(button);
    }

    private RadioButton createRadioButton(int index, int lastIndex, Item item) {
        RadioButton button = new RadioButton(getContext());
        button.setId(index);

        LayoutParams params = new LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );

        int borderSize = getResources().getDimensionPixelSize(R.dimen.defaultControlStrokeWidth);
        if (index == 0) {
            params.setMargins(borderSize, borderSize, 0, borderSize);
        } else if (index == lastIndex) {
            params.setMargins(0, borderSize, borderSize, borderSize);
        } else {
            params.setMargins(0, borderSize, 0, borderSize);
        }

        int paddingTop, paddingBottom;
        int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.defaultControlHorizontalPadding);
        float textSize;
        int heightInPixel;

        if (isSlim()) {
            textSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
        } else {
            textSize = getContext().getResources().getDimension(R.dimen.switchControlTextSize);
            paddingTop = 0;
            paddingBottom = 0;
            heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxSwitchButtonHeight);
        }

        button.setHeight(heightInPixel);
        button.setMinHeight(heightInPixel);
        button.setMaxHeight(heightInPixel);
        button.setPadding(paddingHorizontal, paddingTop, paddingHorizontal, paddingBottom);
        button.setBackground(getButtonDrawable(index == lastIndex, hasError));
        button.setButtonDrawable(null);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(Typeface.create(FONT_FAMILY, Typeface.NORMAL));
        button.setTextColor(textColor);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        button.setIncludeFontPadding(false);
        button.setLayoutParams(params);

        Object btnValue = item.getValue();
        String btnKey = item.getKey();

        if (useAbbreviations) {
            if (btnValue instanceof YesNoUnknown && btnKey.equals(YesNoUnknown.UNKNOWN.toString())) {
                btnKey = btnKey.substring(0, 3);
            }
            if (btnValue instanceof SymptomState && btnKey.equals(SymptomState.UNKNOWN.toString())) {
                btnKey = btnKey.substring(0, 3);
            }
        }

        if (btnKey != null) {
            button.setText(btnKey);
        }

        setUpOnClickListener(button);

        return button;
    }

    private Drawable getButtonDrawable(boolean lastButton, boolean hasError) {
        return new StateDrawableBuilder()
                .setCheckedAndDisabledDrawable(ControlSwitchState.DISABLED.getDrawable(lastButton, hasError, getResources()))
                .setPressedDrawable(ControlSwitchState.PRESSED.getDrawable(lastButton, hasError, getResources()))
                .setCheckedDrawable(ControlSwitchState.CHECKED.getDrawable(lastButton, hasError, getResources()))
                .setNormalDrawable(ControlSwitchState.NORMAL.getDrawable(lastButton, hasError, getResources()))
                .build();
    }

    private void setUpOnClickListener(RadioButton button) {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(v.hasFocus());

                input.requestFocus();
                input.requestFocusFromTouch();
            }
        });
    }

    // Overrides

    @Override
    public Object getValue() {
        int selectedValueId = input.getCheckedRadioButtonId();

        if(selectedValueId >= 0) {
            View selectedValue = input.findViewById(selectedValueId);
            if (selectedValue != null) {
                int selectedValueIndex = input.indexOfChild(selectedValue);
                return radioGroupElements.get(selectedValueIndex);
            }

            return null;
        }

        return null;
    }

    @Override
    public void setValue(Object value) {
        if (value == null && input.getCheckedRadioButtonId() != -1) {
            input.clearCheck();
        } else {
            int selectedValueIndex = radioGroupElements.indexOf(value);
            if (selectedValueIndex >= 0) {
                RadioButton button = (RadioButton) input.getChildAt(selectedValueIndex);
                input.check(button.getId());
            }
        }

        setInternalValue(value);
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlSwitchField,
                    0, 0);

            try {
                useAbbreviations = a.getBoolean(R.styleable.ControlSwitchField_useAbbreviations, false);
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
            inflater.inflate(R.layout.control_switch_layout, this);
        } else {
            throw new RuntimeException("Unable to inflate layout in " + getClass().getName());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        input = (RadioGroup) this.findViewById(R.id.input);
        input.setOrientation(HORIZONTAL);
        background = getResources().getDrawable(R.drawable.control_switch_background_border);
        textColor = getResources().getColorStateList(R.color.control_switch_color_selector);
        input.setBackground(background.mutate());

        input.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();

                if (onCheckedChangeListener != null) {
                    onCheckedChangeListener.onCheckedChanged(radioGroup, i);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
        label.setEnabled(enabled);

        setChildViewsEnabledState();
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlSwitchField) nextView).input.requestFocus();
    }

    @Override
    protected void setHint(String hint) {
        // Switch does not have a hint
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
            Drawable disabledStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_disabled);
            disabledStateDrawable = disabledStateDrawable.mutate();
            input.setBackground(disabledStateDrawable);
            setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            Drawable errorStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_error);
            errorStateDrawable = errorStateDrawable.mutate();
            input.setBackground(errorStateDrawable);
            return;
        }

        if (state == VisualState.FOCUSED || state == VisualState.NORMAL) {
            background = background.mutate();
            input.setBackground(background);
        }
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlSwitchField view, Object value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Object getValue(ControlSwitchField view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(final ControlSwitchField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter(value = {"value", "enumClass", "defaultValue"}, requireAll = false)
    public static void setValue(ControlSwitchField view, Object value, Class enumClass, Object defaultValue) {
        view.setEnumClass(enumClass);

        if (value == null) {
            value = defaultValue;
        }

        view.setValue(value);
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setUseAbbreviations(boolean useAbbreviations) {
        this.useAbbreviations = useAbbreviations;
    }

}
