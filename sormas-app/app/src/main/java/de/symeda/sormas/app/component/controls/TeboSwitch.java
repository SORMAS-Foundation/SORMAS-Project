package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnTeboSwitchAttachedToWindowListener;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.StateDrawableBuilder;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.SormasColor;

/**
 * Created by Orson on 06/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

//FIXME: Fix bug when layout_width is set to wrap_content
public class TeboSwitch extends ControlPropertyEditField<Object> {


    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private static final int BUTTON_1 = 0;
    private static final int BUTTON_2 = 1;
    private static final int BUTTON_3 = 2;
    private static final int BUTTON_4 = 3;

    private static final float DEFAULT_SCALE = 1.0f;

    private static final String CAPTION_FONT_FAMILY_NAME = "sans-serif-medium";
    private static final float DEFAULT_TEXT_SIZE = 23f;


    private float scaleX;
    private float scaleY;
    private int checkedButtonIndex;
    private int orientation;
    private Drawable background;
    private ColorStateList textColor;
    private RadioGroup radioGroup;
    private InverseBindingListener inverseBindingListener;
    private List<Object> radioGroupElements = new ArrayList<>();

    private int borderSize;
    private int paddingTop;
    private int paddingBottom;
    private int paddingLeft;
    private int paddingRight;

    private float textSize;
    private boolean includeUnknown;

    private Drawable normalDrawable;
    private Drawable pressedDrawable;
    private Drawable checkedDrawable;
    private Drawable normalDrawableLast;
    private Drawable pressedDrawableLast;
    private Drawable checkedDrawableLast;

    private boolean enumClassSet = false;

    private boolean initialized = false;
    private Object defaultValue = null;

    private boolean abbrevUnknown = false;

    private Item unknownItem;

    private int mLastCheckId;

    private OnTeboSwitchCheckedChangeListener onCheckedChangeListener;
    private OnTeboSwitchAttachedToWindowListener mOnTeboSwitchAttachedToWindowListener;

    public enum ButtonPosition {
        NOT_LAST,
        LAST;
    }

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboSwitch(Context context) {
        super(context);
    }

    public TeboSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        unknownItem = new Item(getResources().getString(R.string.unknown), null);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboSwitch,
                    0, 0);

            try {
                checkedButtonIndex = a.getInt(R.styleable.TeboSwitch_checkedButton, BUTTON_1);
                orientation = a.getInt(R.styleable.TeboSwitch_orientation, HORIZONTAL);
                background = a.getDrawable(R.styleable.TeboSwitch_background);
                textColor = a.getColorStateList(R.styleable.TeboSwitch_textColor);
                scaleX = a.getFloat(R.styleable.TeboSwitch_scale, DEFAULT_SCALE);
                scaleY = scaleX;

                borderSize = a.getDimensionPixelSize(R.styleable.TeboSwitch_borderSize,
                        getResources().getDimensionPixelSize(R.dimen.defaultControlStrokeWidth));
                paddingTop = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingTop,
                        isSlim() ? 0 : getResources().getDimensionPixelSize(R.dimen.defaultControlVerticalPadding));
                paddingBottom = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingBottom,
                        isSlim() ? 0 : getResources().getDimensionPixelSize(R.dimen.defaultControlVerticalPadding));
                paddingLeft = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingLeft,
                        isSlim() ? 0 : getResources().getDimensionPixelSize(R.dimen.defaultControlHorizontalPadding));
                paddingRight = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingRight,
                        isSlim() ? 0 : getResources().getDimensionPixelSize(R.dimen.defaultControlHorizontalPadding));

                textSize = a.getDimension(R.styleable.TeboSwitch_textSize,
                        getResources().getDimensionPixelSize(R.dimen.defaultControlTextSize));
                includeUnknown = a.getBoolean(R.styleable.TeboSwitch_includeUnknown, false);

            } finally {
                a.recycle();
            }
        }

        normalDrawable = TeboSwitchState.NORMAL.getDefaultDrawable(false);
        pressedDrawable = TeboSwitchState.PRESSED.getDefaultDrawable(false);
        checkedDrawable = TeboSwitchState.CHECKED.getDefaultDrawable(false);

        normalDrawableLast = TeboSwitchState.NORMAL.getDrawableForLastPosition(false);
        pressedDrawableLast = TeboSwitchState.PRESSED.getDrawableForLastPosition(false);
        checkedDrawableLast = TeboSwitchState.CHECKED.getDrawableForLastPosition(false);
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_switch_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        radioGroup = (RadioGroup) this.findViewById(R.id.input);
        radioGroup.setOrientation(orientation);

        if (background != null) {
            radioGroup.setBackground(background.mutate());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (this.mOnTeboSwitchAttachedToWindowListener != null)
            this.mOnTeboSwitchAttachedToWindowListener.onAttachedToWindow(this);
    }

    @Override
    public void setValue(Object value) {
        //TODO: Orson Watchout
        /*if (value == null)
            return;*/

        int valueIndex = radioGroupElements.indexOf(value);

        if (valueIndex < 0)
            return;

        checkedButtonIndex = valueIndex;

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton c = (RadioButton)radioGroup.getChildAt(i);

            if (c == null)
                continue;

            if(i == valueIndex) {
                c.setChecked(true);
            } else {
                c.setChecked(false);
            }
        }
    }

    @Override
    public Object getValue() {
        if (radioGroup == null)
            return null;

        int checkedButtonId = radioGroup.getCheckedRadioButtonId();

        if(checkedButtonId < 0)
            return null;

        View checkedButton = radioGroup.findViewById(checkedButtonId);

        if (checkedButton == null)
            return null;

        int childIndex = radioGroup.indexOfChild(checkedButton);

        if(childIndex < 0)
            return null;

        return radioGroupElements.get(childIndex);
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        radioGroup.setEnabled(enabled);
        radioGroup.setFocusable(enabled);
        radioGroup.setClickable(enabled);

        label.setEnabled(enabled);
        label.setFocusable(enabled);
        label.setClickable(enabled);

        syncChildViewsEnableState();
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboSwitch) nextView).radioGroup.requestFocus();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setAbbrevUnknown(boolean abbrevUnknown) {
        this.abbrevUnknown = abbrevUnknown;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Error & Visual State">

    public void setStateColorBackground(int checkedColor, int uncheckedColor) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                uncheckedColor,
                checkedColor,
        };

        ColorStateList colorStateListBackground = new ColorStateList(states, thumbColors);
        radioGroup.setBackgroundTintList(colorStateListBackground);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setBackgroundTintList(colorStateListBackground);
        }
    }

    public void setStateColorText(ColorStateList colorStateListText) {
        /*int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                uncheckedColor,
                checkedColor,
        };

        ColorStateList colorStateListText = new ColorStateList(states, thumbColors);*/
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            ((RadioButton)radioGroup.getChildAt(i)).setTextColor(colorStateListText);
        }
    }

    @Override
    public void changeVisualState(VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.SWITCH));

        if (state == VisualState.DISABLED) {
            changeRadioButtonState(false);
            Drawable disabledStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_disabled);

            if (disabledStateDrawable != null)
                disabledStateDrawable = disabledStateDrawable.mutate();

            label.setTextColor(labelColor);
            radioGroup.setBackground(disabledStateDrawable);
            setStateColorText(textColor);
            setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            changeRadioButtonState(true);
            Drawable errorStateDrawable = getResources().getDrawable(R.drawable.control_switch_background_border_error);

            if (errorStateDrawable != null)
                errorStateDrawable = errorStateDrawable.mutate();

            label.setTextColor(labelColor);
            radioGroup.setBackground(errorStateDrawable);
            ColorStateList textColorError = getResources().getColorStateList(R.color.control_switch_color_selector_error);
            setStateColorText(textColorError);


            return;
        }

        if (state == VisualState.FOCUSED) {
            changeRadioButtonState(false);
            label.setTextColor(labelColor);

            if (background != null)
                background = background.mutate();

            radioGroup.setBackground(background);
            setStateColorText(textColor);
            return;
        }


        if (state == VisualState.NORMAL) {
            changeRadioButtonState(false);
            label.setTextColor(labelColor);

            if (background != null)
                background = background.mutate();

            radioGroup.setBackground(background);
            setStateColorText(textColor);
            setEnabled(true);
            return;
        }
    }

    @Override
    protected void setHint(String hint) {

    }

    private void changeRadioButtonState(boolean errorState) {
        int lastIndex = radioGroup.getChildCount() - 1;
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton)radioGroup.getChildAt(i);

            if (i != lastIndex)
                button.setBackground(errorState ? getDefaultButtonDrawableError() : getDefaultButtonDrawable());

            if (i == lastIndex)
                button.setBackground(errorState ?  getLastButtonDrawableError() : getLastButtonDrawable());
        }
    }

    // </editor-fold>


    @BindingAdapter("value")
    public static void setValue(TeboSwitch view, Object value) {
        if (value != view.getValue()) {
            view.setValue(value);
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static Object getValue(TeboSwitch view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(final TeboSwitch view, InverseBindingListener listener) {
        view.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (view.inverseBindingListener != null) {
                    view.inverseBindingListener.onChange();
                }

                view.onValueChanged();

                if (view.onCheckedChangeListener != null) {
                    Object checkedItem = checkedId < 0? null : view.radioGroupElements.get(checkedId);
                    view.onCheckedChangeListener.onCheckedChanged(view,
                            checkedItem, checkedId);
                }
            }
        });

        if (listener != null) {
            view.inverseBindingListener = listener;
        }
    }

    @BindingAdapter(value={"value", "includeUnknown", "enumClass", "defaultValue", "abbrevUnknown", "onCheckedChangeListener"}, requireAll=true)
    public static void setValue(TeboSwitch view, Object value, boolean includeUnknown, Class c, Object defaultValue, boolean abbrevUnknown, OnTeboSwitchCheckedChangeListener listener) {
        view.setAbbrevUnknown(abbrevUnknown);
        view.setIncludeUnknown(includeUnknown);
        view.setOnCheckedChangeListener(listener);
        view.setEnumClass(c);
        view.setDefaultValue(defaultValue);

        if (value == null)
            value = defaultValue;

        Object kkk = view.getValue();

        if (value != kkk || !view.isInitialized()) {
            view.setValue(value);
        }

        view.setInitialized(true);
    }

    @BindingAdapter(value={"value", "includeUnknown", "enumClass", "defaultValue", "abbrevUnknown"}, requireAll=true)
    public static void setValue(TeboSwitch view, Object value, boolean includeUnknown, Class c, Object defaultValue, boolean abbrevUnknown) {
        view.setAbbrevUnknown(abbrevUnknown);
        view.setIncludeUnknown(includeUnknown);
        view.setOnCheckedChangeListener(null);
        view.setEnumClass(c);
        view.setDefaultValue(defaultValue);

        if (value == null)
            value = defaultValue;

        if (value != view.getValue() || !view.isInitialized()) {
            view.setValue(value);
        }

        view.setInitialized(true);
    }

    @BindingAdapter(value={"value", "enumClass", "defaultValue", "onCheckedChangeListener"}, requireAll=true)
    public static void setValue(TeboSwitch view, Object value, Class c, Object defaultValue, OnTeboSwitchCheckedChangeListener listener) {
        setValue(view, value, false, c, defaultValue, false, listener);
    }


    public void setIncludeUnknown(boolean includeUnknown) {
        this.includeUnknown = includeUnknown;
    }

    public void setEnumClass(Class c) {
        if (!enumClassSet) {
            List<Item> items = DataUtils.getEnumItems(c, false);

            if (includeUnknown) {
                String unknown = getResources().getString(R.string.unknown);
                items.add(0, new Item(unknown, null));
            }

            int itemTotal = items.size();
            for (int i = 0; i < items.size(); i++) {
                this.addItem(i, itemTotal - 1, items.get(i));
            }
        }

        enumClassSet = true;
    }

    public void setEnumClass(Class c, boolean reload) {
        if (reload)
            removeAllItems();

        if (!enumClassSet || reload) {
            setEnumClass(c);
        }
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    private void syncChildViewsEnableState() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(radioGroup.isEnabled());
            radioGroup.getChildAt(i).setFocusable(radioGroup.isEnabled());
            radioGroup.getChildAt(i).setClickable(radioGroup.isEnabled());
        }
    }

    private void addItem(int index, int lastIndex, Item item) {
        //TODO: Orson Watchout
        /*if(item.getValue() == null)
            return;*/

        //Create Radio Button
        final RadioButton button = createRadioButton(index, lastIndex, item);

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        radioGroup.addView(button);
        radioGroupElements.add(item.getValue());

        //Enable or Disable children
        syncChildViewsEnableState();
    }

    @NonNull
    private RadioButton createRadioButton(int index, int lastIndex, Item item) {
        RadioButton button = new RadioButton(getContext());
        button.setId(index);

        LayoutParams params = new LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );

        if (index == 0) {
            params.setMargins(borderSize, borderSize, 0, borderSize);
        } else if (index == lastIndex) {
            params.setMargins(0, borderSize, borderSize, borderSize);
        } else {
            params.setMargins(0, borderSize, 0, borderSize);
        }

        if (isSlim()) {
            textSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);

            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            button.setHeight(heightInPixel);
            button.setMinHeight(heightInPixel);
            button.setMaxHeight(heightInPixel);
        } else {
            textSize = getContext().getResources().getDimension(R.dimen.switchControlTextSize);
            paddingTop = 0;
            paddingBottom = 0;

            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxSwitchButtonHeight);
            button.setHeight(heightInPixel);
            button.setMinHeight(heightInPixel);
            button.setMaxHeight(heightInPixel);
        }

        button.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        //Set button background
        if (index != lastIndex)
            button.setBackground(hasError ? getDefaultButtonDrawableError() : getDefaultButtonDrawable());

        if (index == lastIndex)
            button.setBackground(hasError ?  getLastButtonDrawableError() : getLastButtonDrawable());


        button.setButtonDrawable(null);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(Typeface.create(CAPTION_FONT_FAMILY_NAME, Typeface.NORMAL));
        button.setTextColor(textColor != null ? textColor : ColorStateList.valueOf(SormasColor.BLUE));
        //button.setTextSize(textSize * scaleX);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * scaleX);

        button.setIncludeFontPadding(false);
        button.setScaleX(scaleX);
        button.setScaleY(scaleY);
        button.setLayoutParams(params);

        Object btnValue = item.getValue();
        String btnKey = item.getKey();

        //TODO: This is a hack
        if (abbrevUnknown) {
            if (btnValue instanceof YesNoUnknown && btnKey.equals(YesNoUnknown.UNKNOWN.toString())) {
                btnKey = btnKey.substring(0, 3);
            }

            if (btnValue instanceof SymptomState && btnKey.equals(SymptomState.UNKNOWN.toString())) {
                btnKey = btnKey.substring(0, 3);
            }
        }

        if (btnKey != null)
            button.setText(btnKey);//btnValue.toString());

        return button;
    }

    private Drawable getDefaultButtonDrawable() {
        return new StateDrawableBuilder()
                .setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDefaultDrawable(false))
                .setPressedDrawable(TeboSwitchState.PRESSED.getDefaultDrawable(false))
                .setCheckedDrawable(TeboSwitchState.CHECKED.getDefaultDrawable(false))
                .setNormalDrawable(TeboSwitchState.NORMAL.getDefaultDrawable(false))
                .build();
                //.setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDefaultDrawable(false))
    }

    private Drawable getLastButtonDrawable() {
        return new StateDrawableBuilder()
                .setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDrawableForLastPosition(false))
                .setPressedDrawable(TeboSwitchState.PRESSED.getDrawableForLastPosition(false))
                .setCheckedDrawable(TeboSwitchState.CHECKED.getDrawableForLastPosition(false))
                .setNormalDrawable(TeboSwitchState.NORMAL.getDrawableForLastPosition(false))
                .build();
                //.setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDrawableForLastPosition(false))
    }

    private Drawable getDefaultButtonDrawableError() {
        return new StateDrawableBuilder()
                .setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDefaultDrawable(true))
                .setPressedDrawable(TeboSwitchState.PRESSED.getDefaultDrawable(true))
                .setCheckedDrawable(TeboSwitchState.CHECKED.getDefaultDrawable(true))
                .setNormalDrawable(TeboSwitchState.NORMAL.getDefaultDrawable(true))
                .build();
                //.setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDefaultDrawable(true))
    }

    private Drawable getLastButtonDrawableError() {
        return new StateDrawableBuilder()
                .setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDrawableForLastPosition(true))
                .setPressedDrawable(TeboSwitchState.PRESSED.getDrawableForLastPosition(true))
                .setCheckedDrawable(TeboSwitchState.CHECKED.getDrawableForLastPosition(true))
                .setNormalDrawable(TeboSwitchState.NORMAL.getDrawableForLastPosition(true))
                .build();
                //.setCheckedAndDisabledDrawable(TeboSwitchState.DISABLED.getDrawableForLastPosition(true))
    }

    public void removeAllItems() {
        radioGroup.removeAllViews();
        radioGroupElements.clear();
    }

    public void setOnCheckedChangeListener(OnTeboSwitchCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnAttachedToWindow(OnTeboSwitchAttachedToWindowListener listener) {
        this.mOnTeboSwitchAttachedToWindowListener = listener;
    }

}
