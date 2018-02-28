package de.symeda.sormas.app.component;

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

/**
 * Created by Orson on 25/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboCheckBox extends EditTeboPropertyField<Boolean> {

    protected CheckBox checkBox;
    protected InverseBindingListener inverseBindingListener;
    private CompoundButton.OnCheckedChangeListener additionalListener;

    private View controlFrame;
    private int uncheckedStateColor;
    private int checkedStateColor;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboCheckBox(Context context) {
        super(context);
    }

    public TeboCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        uncheckedStateColor = getResources().getColor(R.color.colorControlNormal);
        checkedStateColor = getResources().getColor(R.color.colorControlActivated);

        /*if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboCheckBox,
                    0, 0);

            try {
                hint = a.getString(R.styleable.TeboCheckBox_hint);
                description = a.getString(R.styleable.TeboCheckBox_description);
                caption = a.getString(R.styleable.TeboCheckBox_labelCaption);
                captionColor = a.getColor(R.styleable.TeboCheckBox_labelColor,
                        getResources().getColor(R.color.controlLabelColor));
                textAlignment = a.getInt(R.styleable.TeboCheckBox_textAlignment, View.TEXT_ALIGNMENT_VIEW_START);
                gravity = a.getInt(R.styleable.TeboCheckBox_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);
                required = a.getBoolean(R.styleable.TeboCheckBox_required, false);
            } finally {
                a.recycle();
            }
        }*/
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_checkbox_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        controlFrame = this.findViewById(R.id.controlFrame);
        checkBox = (CheckBox) this.findViewById(R.id.chkControlInput);

        checkBox.setNextFocusLeftId(getNextFocusLeft());
        checkBox.setNextFocusRightId(getNextFocusRight());
        checkBox.setNextFocusUpId(getNextFocusUp());
        checkBox.setNextFocusDownId(getNextFocusDown());
        checkBox.setNextFocusForwardId(getNextFocusForward());

        checkBox.setImeOptions(getImeOptions());

        checkBox.setTextAlignment(getCaptionAlignment());
        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            checkBox.setGravity(getCaptionGravity());
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();

                if (additionalListener != null) {
                    additionalListener.onCheckedChanged(compoundButton, b);
                }
            }
        });

        controlFrame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });

        //checkBox.setText(getControlCaption());

        //Set Hint
        //checkBox.setSubHeading(hint);


        checkBox.setOnFocusChangeListener(new OnFocusChangeListenerHandler(this));
        checkBox.setOnClickListener(new OnCheckboxClickHandler(getContext(), this));
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setChecked(value);
    }

    @Override
    public Boolean getValue() {
        return checkBox.isChecked();
    }

    @BindingAdapter("value")
    public static void setValue(TeboCheckBox view, boolean value) {
        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static boolean getValue(TeboCheckBox view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboCheckBox view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboCheckBox) nextView).checkBox.requestFocus();
    }

    public void setAdditionalListener(CompoundButton.OnCheckedChangeListener additionalListener) {
        this.additionalListener = additionalListener;
    }

    @Override
    public void setBackgroundResource(int resid) {
        int pl = checkBox.getPaddingLeft();
        int pt = checkBox.getPaddingTop();
        int pr = checkBox.getPaddingRight();
        int pb = checkBox.getPaddingBottom();

        checkBox.setBackgroundResource(resid);

        checkBox.setPadding(pl, pt, pr, pb);
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = checkBox.getPaddingLeft();
        int pt = checkBox.getPaddingTop();
        int pr = checkBox.getPaddingRight();
        int pb = checkBox.getPaddingBottom();

        checkBox.setBackground(background);

        checkBox.setPadding(pl, pt, pr, pb);
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


        checkBox.setBackgroundTintList(new ColorStateList(states, thumbColors));
    }

    @Override
    public void changeVisualState(VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.CHECKBOX));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.CHECKBOX));

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(labelColor, labelColor);
            //setBackground(drawable);
            checkBox.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(labelColor, labelColor);



            //checkBox.setForegroundTintList();

            //setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(checkedStateColor, uncheckedStateColor);
            //setBackground(drawable);
            return;
        }


        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            lblControlLabel.setTextColor(labelColor);
            setStateColor(checkedStateColor, uncheckedStateColor);
            //setBackground(drawable);
            checkBox.setEnabled(true);
            return;
        }
    }

    // </editor-fold>



    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboCheckBox input;

        public OnFocusChangeListenerHandler(TeboCheckBox input) {
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

    private class OnCheckboxClickHandler implements View.OnClickListener {

        private Context context;
        private TeboCheckBox input;

        public OnCheckboxClickHandler(Context context, TeboCheckBox input) {
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
        }
    }
}
