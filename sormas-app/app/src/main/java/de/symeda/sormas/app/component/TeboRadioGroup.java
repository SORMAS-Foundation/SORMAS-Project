package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DisplayMetricsHelper;

/**
 * Created by Orson on 26/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboRadioGroup extends EditTeboPropertyField<Object> {

    private static final int RADIO_BUTTON_WIDTH = 48;
    private static final int RADIO_BUTTON_HEIGHT = 48;
    private static final float RADIO_BUTTON_MARGIN_START = 8f;
    private static final int RADIO_BUTTON_LABEL_MARGIN_START = 4;

    private RadioGroup radioGroup;
    private InverseBindingListener inverseBindingListener;
    private List<Object> radioGroupElements = new ArrayList<>();

    private View controlFrame;
    private View captionFrame;

    //private boolean showCaption;
    private boolean horizontalFlow;
    private int uncheckedStateColor;
    private int checkedStateColor;

    private AttributeSet attrs;

    private float scaleX;
    private float scaleY;
    private boolean includeMarginStart;
    private float radioButtonMarginStart;
    private float radioButtonLabelMarginStart;
    private boolean enumClassSet = false;

    private OnTeboRadioButtonCheckedChangeListener mOnTeboRadioButtonCheckedChangeListener;


    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboRadioGroup(Context context) {
        super(context);
    }

    public TeboRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.attrs = attrs;
    }

    public TeboRadioGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.attrs = attrs;
    }

    // </editor-fold>

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        uncheckedStateColor = getResources().getColor(R.color.colorControlNormal);
        checkedStateColor = getResources().getColor(R.color.colorControlActivated);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboRadioGroup,
                    0, 0);

            try {
                //showCaption = a.getBoolean(R.styleable.TeboRadioGroup_showCaption, true);
                horizontalFlow = a.getBoolean(R.styleable.TeboRadioGroup_horizontalFlow, false);
                scaleX = a.getFloat(R.styleable.TeboRadioGroup_scaleX, 1.2f);
                scaleY = a.getFloat(R.styleable.TeboRadioGroup_scaleY, 1.2f);
                includeMarginStart = a.getBoolean(R.styleable.TeboRadioGroup_includeMarginStart, false);
                radioButtonMarginStart = a.getDimension(R.styleable.TeboRadioGroup_radioButtonMarginStart, RADIO_BUTTON_MARGIN_START);
                radioButtonLabelMarginStart = a.getDimension(R.styleable.TeboRadioGroup_radioButtonLabelMarginStart, RADIO_BUTTON_LABEL_MARGIN_START);
                /*hint = a.getString(R.styleable.TeboRadioGroup_hint);
                description = a.getString(R.styleable.TeboRadioGroup_description);
                caption = a.getString(R.styleable.TeboRadioGroup_labelCaption);
                captionColor = a.getColor(R.styleable.TeboRadioGroup_labelColor,
                        getResources().getColor(R.color.controlLabelColor));
                textAlignment = a.getInt(R.styleable.TeboRadioGroup_textAlignment, View.TEXT_ALIGNMENT_VIEW_START);
                gravity = a.getInt(R.styleable.TeboRadioGroup_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);
                required = a.getBoolean(R.styleable.TeboRadioGroup_required, false);*/
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_radiogroup_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        controlFrame = this.findViewById(R.id.controlFrame);
        captionFrame = this.findViewById(R.id.captionFrame);
        radioGroup = (RadioGroup) this.findViewById(R.id.rbgControlInput);

        radioGroup.setNextFocusLeftId(getNextFocusLeft());
        radioGroup.setNextFocusRightId(getNextFocusRight());
        radioGroup.setNextFocusUpId(getNextFocusUp());
        radioGroup.setNextFocusDownId(getNextFocusDown());
        radioGroup.setNextFocusForwardId(getNextFocusForward());

        //radioGroup.setImeOptions(getImeOptions());

        radioGroup.setTextAlignment(getCaptionAlignment());
        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            radioGroup.setGravity(getCaptionGravity());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }

            /*if (additionalListener != null) {
                additionalListener.onCheckedChanged(compoundButton, b);
            }*/
        });

        /*controlFrame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });*/

        if (isShowCaption()) {
            captionFrame.setVisibility(View.VISIBLE);
            lblControlLabel.setText(getControlCaption());
        } else {
            captionFrame.setVisibility(View.GONE);
        }

        radioGroup.setOnFocusChangeListener(new TeboRadioGroup.OnFocusChangeListenerHandler(this));
        radioGroup.setOnClickListener(new TeboRadioGroup.OnRadioGroupClickHandler(getContext(), this));

    }

    @BindingAdapter("value")
    public static void setValue(TeboRadioGroup view, RadioButton value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static Object getValue(TeboRadioGroup view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(final TeboRadioGroup view, InverseBindingListener listener) {
        view.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (view.inverseBindingListener != null) {
                    view.inverseBindingListener.onChange();
                }

                view.onValueChanged();

                if (view.mOnTeboRadioButtonCheckedChangeListener != null) {
                    Object checkedItem = checkedId < 0? null : view.radioGroupElements.get(checkedId);
                    view.mOnTeboRadioButtonCheckedChangeListener.onCheckedChanged(view,
                            checkedItem, checkedId);
                }
            }
        });

        if (listener != null) {
            view.inverseBindingListener = listener;
        }
    }

    public void setEnumClass(Class c) {
        if (!enumClassSet) {
            List<Item> items = DataUtils.getEnumItems(c);
            for (int i = 0; i < items.size(); i++) {
                this.addItem(i, items.get(i));
            }
        }

        enumClassSet = true;
    }

    public void setItems(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            this.addItem(i, items.get(i));
        }
    }

    public void addItem(int index, Item item) {
        if(item.getValue() == null)
            return;

        //Create Frame
        LinearLayout row = createRadioButtonFrame();

        //Create Radio Button
        RadioButton button = createRadioButton(index, item);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout)radioGroup.getChildAt(i);
                    RadioButton button = (RadioButton)row.getChildAt(0);
                    int buttonId = button.getId();

                    if (button == v) {
                        //radioGroup.check(buttonId);
                        button.setChecked(true);
                    } else {
                        //radioGroup.clearCheck();
                        button.setChecked(false);
                    }
                }
            }
        });

        //Create Label
        View labelRow = createRadioButtonLabel(item.getKey());

        row.addView(button);
        row.addView(labelRow);


        //wv.requestLayout();//It is necesary to refresh the screen

        radioGroup.setOrientation((horizontalFlow) ? HORIZONTAL : VERTICAL);
        radioGroup.addView(row);
        radioGroupElements.add(item.getValue());
    }

    @BindingAdapter(value={"value", "enumClass"}, requireAll=true)
    public static void setValue(TeboRadioGroup view, Object value, Class c) {
        view.setEnumClass(c);
        view.setValue(value);
    }

    @BindingAdapter(value={"value", "items"}, requireAll=true)
    public static void setValue(TeboRadioGroup view, Object value, List<Item> list) {
        view.setItems(list);
        view.setValue(value);
    }

    @Override
    public void setValue(Object value) {
        int indexOfData = radioGroupElements.indexOf(value);

        if (indexOfData < 0)
            return;

        RadioButton btnToCheck = getCheckedRadioButtonByIndex(indexOfData);

        if (btnToCheck == null)
            return;

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            LinearLayout row = (LinearLayout)radioGroup.getChildAt(i);
            RadioButton button = (RadioButton)row.getChildAt(0);

            if (btnToCheck.getId() == button.getId()) {
                //radioGroup.check(buttonId);
                button.setChecked(true);
            } else {
                //radioGroup.clearCheck();
                button.setChecked(false);
            }
        }
    }

    @Override
    public Object getValue() {
        int checkedButtonIndex = getCheckedRadioButtonIndex();

        if (checkedButtonIndex < 0)
            return null;

        return radioGroupElements.get(checkedButtonIndex);
    }

    public int getCheckedRadioButtonIndex() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            LinearLayout row = (LinearLayout)radioGroup.getChildAt(i);
            RadioButton button = (RadioButton)row.getChildAt(0);

            if (button.isChecked())
                return i;
        }

        return -1;
    }

    public int getCheckedRadioButtonId() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            LinearLayout row = (LinearLayout)radioGroup.getChildAt(i);
            RadioButton button = (RadioButton)row.getChildAt(0);

            if (button.isChecked())
                return button.getId();
        }

        return -1;
    }

    public RadioButton getCheckedRadioButton() {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            LinearLayout row = (LinearLayout)radioGroup.getChildAt(i);
            RadioButton button = (RadioButton)row.getChildAt(0);

            if (button.isChecked())
                return button;
        }

        return null;
    }

    public RadioButton getCheckedRadioButtonByIndex(int index) {
        LinearLayout row = (LinearLayout)radioGroup.getChildAt(index);
        RadioButton button = (RadioButton)row.getChildAt(0);

        return button;
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
        lblControlLabel.setEnabled(enabled);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboRadioGroup) nextView).radioGroup.requestFocus();
    }

    @NonNull
    private RadioButton createRadioButton(int index, Item item) {
        int indexOfRealRadioButton = 0;
        boolean foundFirst = false;

        if (item.getValue() != null && !foundFirst) {
            indexOfRealRadioButton = indexOfRealRadioButton + 1;
            foundFirst = true;
        }

        RadioButton button = new RadioButton(getContext());
        button.setId(index);

        LayoutParams params = new LayoutParams(
                DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_WIDTH * this.scaleX),
                DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_HEIGHT * this.scaleY)
        );
        /*LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );*/

        if (horizontalFlow) {
            if (index > indexOfRealRadioButton)
                params.setMarginStart(DisplayMetricsHelper.dpToPixels(getContext(), radioButtonMarginStart * this.scaleX));
        }

        /*button.setWidth(DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_WIDTH));
        button.setHeight(DisplayMetricsHelper.dpToPixels(button.getContext(), RADIO_BUTTON_HEIGHT));*/

        button.setIncludeFontPadding(false);
        button.setPaddingRelative(0, 0, 0, 0);

        button.setCompoundDrawablePadding(0);

        button.setLayoutParams(params);
        button.setScaleX(this.scaleX);
        button.setScaleY(this.scaleY);
        button.setButtonDrawable(null);
        button.setBackground(getSingleIndicator(button)); //getSingleIndicator(button)


        return button;
    }

    @NonNull
    private LinearLayout createRadioButtonFrame() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );

        row.setLayoutParams(params);

        return row;
    }

    private View createRadioButtonLabel(String label) {
        LinearLayout labelRow = new LinearLayout(getContext());
        labelRow.setOrientation(HORIZONTAL);
        labelRow.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );

        if (includeMarginStart) {
            params.setMarginStart((int) (radioButtonLabelMarginStart * this.scaleX));
            //params.setMarginStart(DisplayMetricsHelper.dpToPixels(getContext(), radioButtonLabelMarginStart * this.scaleX));
        }
        labelRow.setLayoutParams(params);


        TextView rbLabel = new TextView (new ContextThemeWrapper(getContext(),
                R.style.CheckboxLabelStyle), null, 0);

        ViewGroup.LayoutParams tvParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        rbLabel.setText(label);
        rbLabel.setLayoutParams(tvParams);
        labelRow.addView(rbLabel);

        return labelRow;
    }

    private Drawable getSingleIndicator(RadioButton button) {
        Drawable rbIndicator = null;
        if (attrs != null) {
            TypedArray a = button.getContext().getTheme()
                    .obtainStyledAttributes(new int[] {android.R.attr.listChoiceIndicatorSingle});

            try {
                if ((a != null) && (a.length() > 0)) {
                    rbIndicator = a.getDrawable(0);
                }
            } finally {
                a.recycle();
            }
        }

        return rbIndicator;
    }

    public void removeAllItems() {
        radioGroup.removeAllViews();
        radioGroupElements.clear();
    }

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
    public void changeVisualState(VisualState state, UserRight editOrCreateUserRight) {
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
            setStateColor(checkedStateColor, uncheckedStateColor);
            return;
        }


        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setStateColor(checkedStateColor, uncheckedStateColor);
            setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
            return;
        }
    }

    // </editor-fold>

    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboRadioGroup input;

        public OnFocusChangeListenerHandler(TeboRadioGroup input) {
            this.input = input;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!v.isEnabled())
                return;

            if (inErrorState()) {
                //changeVisualState(VisualState.ERROR);
                showNotification();

                return;
            } else {
                hideNotification();
            }

            if (hasFocus) {
                changeVisualState(VisualState.FOCUSED);
            } else {
                changeVisualState(VisualState.NORMAL);
            }

        }
    }

    private class OnRadioGroupClickHandler implements View.OnClickListener {

        private Context context;
        private TeboRadioGroup input;

        public OnRadioGroupClickHandler(Context context, TeboRadioGroup input) {
            this.context = context;
            this.input = input;
        }
        @Override
        public void onClick(View v) {
            if(!v.isEnabled())
                return;

            if (inErrorState()) {
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

    public void setOnCheckedChangeListener(OnTeboRadioButtonCheckedChangeListener listener) {
        this.mOnTeboRadioButtonCheckedChangeListener = listener;
    }

}
