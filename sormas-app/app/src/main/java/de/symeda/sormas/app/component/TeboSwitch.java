package de.symeda.sormas.app.component;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.R;
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
public class TeboSwitch extends EditTeboPropertyField<Object> {


    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;

    private static final int BUTTON_1 = 0;
    private static final int BUTTON_2 = 1;
    private static final int BUTTON_3 = 2;
    private static final int BUTTON_4 = 3;

    private static final float DEFAULT_SCALE = 1.0f;

    private static final int CHECKED_STATE_COLOR = SormasColor.CTRL_FOCUS;
    private static final int UNCHECKED_STATE_COLOR = SormasColor.CTRL_NORMAL;

    private static final String CAPTION_FONT_FAMILY_NAME = "sans-serif-medium";
    private static final float DEFAULT_TEXT_SIZE = 23f;


    private float scaleX;
    private float scaleY;
    private int checkedButton;
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
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        unknownItem = new Item(getResources().getString(R.string.unknown), null);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboSwitch,
                    0, 0);

            try {
                checkedButton = a.getInt(R.styleable.TeboSwitch_checkedButton, BUTTON_1);
                orientation = a.getInt(R.styleable.TeboSwitch_orientation, HORIZONTAL);
                background = a.getDrawable(R.styleable.TeboSwitch_background);
                textColor = a.getColorStateList(R.styleable.TeboSwitch_textColor);
                scaleX = a.getFloat(R.styleable.TeboSwitch_scale, DEFAULT_SCALE);
                scaleY = scaleX;

                borderSize = a.getDimensionPixelSize(R.styleable.TeboSwitch_borderSize, 0);
                paddingTop = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingTop, 0);
                paddingBottom = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingBottom, 0);
                paddingLeft = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingLeft, 0);
                paddingRight = a.getDimensionPixelSize(R.styleable.TeboSwitch_paddingRight, 0);

                textSize = a.getDimension(R.styleable.TeboSwitch_textSize, DEFAULT_TEXT_SIZE);
                includeUnknown = a.getBoolean(R.styleable.TeboSwitch_includeUnknown, false);

                /*marginTop = a.getDimensionPixelSize(R.styleable.TeboSwitch_marginTop, 0);
                marginBottom = a.getDimensionPixelSize(R.styleable.TeboSwitch_marginBottom, 0);
                marginLeft = a.getDimensionPixelSize(R.styleable.TeboSwitch_marginLeft, 0);
                marginRight = a.getDimensionPixelSize(R.styleable.TeboSwitch_marginRight, 0);
*/

            } finally {
                a.recycle();
            }
        }

        normalDrawable = TeboSwitchState.NORMAL.getDefaultDrawable();
        pressedDrawable = TeboSwitchState.PRESSED.getDefaultDrawable();
        checkedDrawable = TeboSwitchState.CHECKED.getDefaultDrawable();

        normalDrawableLast = TeboSwitchState.NORMAL.getDrawableForLastPosition();
        pressedDrawableLast = TeboSwitchState.PRESSED.getDrawableForLastPosition();
        checkedDrawableLast = TeboSwitchState.CHECKED.getDrawableForLastPosition();
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_tebo_switch_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        radioGroup = (RadioGroup) this.findViewById(R.id.rbTeboSwitch);





        radioGroup.setOrientation(orientation);

        /*radioGroup.setNextFocusLeftId(getNextFocusLeft());
        radioGroup.setNextFocusRightId(getNextFocusRight());
        radioGroup.setNextFocusUpId(getNextFocusUp());
        radioGroup.setNextFocusDownId(getNextFocusDown());
        radioGroup.setNextFocusForwardId(getNextFocusForward());*/

        /*radioGroup.setTextAlignment(getCaptionAlignment());
        if (getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            radioGroup.setGravity(getCaptionGravity());
        }*/

        if (background != null) {
            radioGroup.setBackground(background.mutate());
        }

        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }

                onValueChanged();

                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onCheckedChanged(TeboSwitch.this, radioGroupElements.get(i), i);
            }




            *//*if (additionalListener != null) {
                additionalListener.onCheckedChanged(compoundButton, b);
            }*//*
        });*/



        /*button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private TeboSwitch teboSwitch;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckedChangeListener != null)
                    onCheckedChangeListener.onCheckedChanged(teboSwitch, buttonView, isChecked);
            }

            private CompoundButton.OnCheckedChangeListener init(TeboSwitch teboSwitch){
                this.teboSwitch = teboSwitch;
                return this;
            }
        }.init(this));*/

        //setValue(radioGroupElements.get(checkedButton));

        //radioGroup.setOnFocusChangeListener(new TeboRadioGroup.OnFocusChangeListenerHandler(this));
        //radioGroup.setOnClickListener(new TeboRadioGroup.OnRadioGroupClickHandler(getContext(), this));
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

        checkedButton = valueIndex;

        View child = radioGroup.getChildAt(valueIndex);

        if (child == null)
            return;

        if(child != null) {
            radioGroup.check(child.getId());
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
        lblControlLabel.setEnabled(enabled);
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

    public void setStateColor(int checkedColor, int uncheckedColor) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                uncheckedColor,
                checkedColor,
        };


        radioGroup.setBackgroundTintList(new ColorStateList(states, thumbColors));
    }

    @Override
    public void changeVisualState(VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.CHECKBOX));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.CHECKBOX));

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(labelColor, labelColor);
            //setBackground(drawable);
            setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(labelColor, labelColor);
            return;
        }

        if (state == VisualState.FOCUSED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(CHECKED_STATE_COLOR, UNCHECKED_STATE_COLOR);
            return;
        }


        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(CHECKED_STATE_COLOR, UNCHECKED_STATE_COLOR);
            setEnabled(true);
            return;
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

        if (value != view.getValue() || !view.isInitialized()) {
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

    private void addItem(int index, int lastIndex, Item item) {
        //TODO: Orson Watchout
        /*if(item.getValue() == null)
            return;*/

        //Create Radio Button
        final RadioButton button = createRadioButton(index, lastIndex, item);
        radioGroup.addView(button);
        radioGroupElements.add(item.getValue());
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

            button.setHeight(getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight));
        }

        button.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        //Set button background
        if (index != lastIndex)
            button.setBackground(getDefaultButtonDrawable());

        if (index == lastIndex)
            button.setBackground(getLastButtonDrawable());

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
            if (btnValue instanceof YesNoUnknown && btnKey == YesNoUnknown.UNKNOWN.toString()) {
                btnKey = btnKey.substring(0, 3);
            }
        }

        if (btnKey != null)
            button.setText(btnKey);//btnValue.toString());

        return button;
    }

    private Drawable getDefaultButtonDrawable() {
        return new StateDrawableBuilder()
                .setNormalDrawable(normalDrawable)
                .setCheckedDrawable(checkedDrawable)
                .setPressedDrawable(pressedDrawable)
                .build();
    }

    private Drawable getLastButtonDrawable() {
        return new StateDrawableBuilder()
                .setNormalDrawable(normalDrawableLast)
                .setCheckedDrawable(checkedDrawableLast)
                .setPressedDrawable(pressedDrawableLast)
                .build();
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
