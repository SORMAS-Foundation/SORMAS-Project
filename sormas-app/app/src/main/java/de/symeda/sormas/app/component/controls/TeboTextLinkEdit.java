package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

/**
 * Created by Orson on 08/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboTextLinkEdit extends ControlPropertyEditField<String> {


    protected TextView txtControlInput;
    protected InverseBindingListener inverseBindingListener;

    private boolean singleLine;
    private int maxLines;
    private int inputType;
    private Drawable drawableStart;
    private Drawable drawableEnd;
    private Drawable drawableLeft;
    private Drawable drawableRight;

    private ColorStateList textColor;
    private ColorStateList textColorHint;
    private ColorStateList drawableTint;
    private Drawable background;
    private int drawablePadding;

    private Location mCachedLocation;

    private OnClickListener onLinkClickListener;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTextLinkEdit(Context context) {
        super(context);
    }

    public TeboTextLinkEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTextLinkEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">


    @Override
    public void setValue(String value) {
        if (txtControlInput.getText().toString() != value) {
            txtControlInput.setText(value);
        }
    }

    @Override
    public String getValue() {
        if (txtControlInput.getText() == null)
            return "";

        return txtControlInput.getText().toString();
    }

    public void setValue(Location value) {
        if (value == null)
            return;

        try {
            if (value.toString() != getValue()) {
                mCachedLocation = value;
                setValue(value.toString());
            }
        } catch(Exception e) {
            return;
        }
    }

    public Location getLocation() {
        return mCachedLocation;
    }

    @BindingAdapter("value")
    public static void setValue(TeboTextLinkEdit view, String text) {
        if (text != view.getValue()) {
            view.setValue(text);
        }
    }

    @BindingAdapter("locationValue")
    public static void setLocationValue(TeboTextLinkEdit textField, Location location) {
        textField.setValue(location);
    }

    @InverseBindingAdapter(attribute = "locationValue", event = "valueAttrChanged")
    public static Location getLocationValue(TeboTextLinkEdit view) {
        return view.getLocation();
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static String getValue(TeboTextLinkEdit view) {
        return view.getValue();
    }

    @BindingAdapter("integer")
    public static void setInteger(TeboTextLinkEdit textField, Integer integer) {
        if (integer == null)
            return;

        if (integer.toString() != textField.getValue()) {
            textField.setValue(integer.toString());
        }
    }

    @InverseBindingAdapter(attribute = "integer", event = "valueAttrChanged")
    public static Integer getInteger(TeboTextLinkEdit view) {
        try {
            return Integer.valueOf(view.getValue());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboTextLinkEdit view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter(value={"inputType", "singleLine"}, requireAll=true)
    public static void setValue(TeboTextLinkEdit view, int inputType, boolean singleLine) {
        view.setInputType(inputType);
        view.setSingleLine(singleLine);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtControlInput.setEnabled(enabled);
        label.setEnabled(enabled);

    }

    @Override
    protected void setHint(String value) {
        txtControlInput.setHint(value);
    }

    public String getHint() {
        return txtControlInput.getHint().toString();
    }

    public boolean isSingleLine() {
        return this.singleLine;
    }

    private void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
        txtControlInput.setSingleLine(this.singleLine);

        if (this.singleLine) {
            txtControlInput.setMaxLines(1);
            //input.setLines(1);
            txtControlInput.setInputType(inputType);
            txtControlInput.setVerticalScrollBarEnabled(false);
        } else {
            txtControlInput.setMaxLines(maxLines);
            //input.setLines(maxLines);
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

    public void setOnLinkClick(View.OnClickListener onLinkClickListener) {
        if (txtControlInput == null)
            return;

        //input.setOnClickListener(onLinkClickListener);
        this.onLinkClickListener = onLinkClickListener;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    
    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTextLinkEdit,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.TeboTextLinkEdit_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboTextLinkEdit_maxLines, 1);
                inputType = a.getInt(R.styleable.TeboTextLinkEdit_inputType, InputType.TYPE_CLASS_TEXT);
                drawableStart = a.getDrawable(R.styleable.TeboTextLinkEdit_iconStart);
                drawableEnd = a.getDrawable(R.styleable.TeboTextLinkEdit_iconEnd);
                drawableLeft = a.getDrawable(R.styleable.TeboTextLinkEdit_iconLeft);
                drawableRight = a.getDrawable(R.styleable.TeboTextLinkEdit_iconRight);
                textColor = a.getColorStateList(R.styleable.TeboTextLinkEdit_textColor);
                textColorHint = a.getColorStateList(R.styleable.TeboTextLinkEdit_textColorHint);
                background = a.getDrawable(R.styleable.TeboTextLinkEdit_background);
                drawablePadding = a.getDimensionPixelSize(R.styleable.TeboTextLinkEdit_iconPadding, 0);
                drawableTint = a.getColorStateList(R.styleable.TeboTextLinkEdit_iconTint);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_popup_textview_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (TextView) this.findViewById(R.id.input);

    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        txtControlInput.setImeOptions(getImeOptions());

        txtControlInput.setTextAlignment(getControlTextAlignment());
        if(getControlTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getControlGravity());
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

        setSingleLine(singleLine);

        txtControlInput.setOnFocusChangeListener(new NotificationVisibilityOnFocusChangeHandler(this));
        txtControlInput.setOnClickListener(new NotificationVisibilityOnClickHandler());

        if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            int paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            int paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            int paddingLeft = txtControlInput.getPaddingLeft();
            int paddingRight = txtControlInput.getPaddingRight();

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        }

        txtControlInput.setTextColor(textColor);
        txtControlInput.setHintTextColor(textColorHint);
        txtControlInput.setBackground(background);

        if (drawableStart != null) {
            drawableStart.setTintList(drawableTint);
        }

        if (drawableEnd != null) {
            drawableEnd.setTintList(drawableTint);
        }

        if (drawableLeft != null) {
            drawableLeft.setTintList(drawableTint);
        }

        if (drawableRight != null) {
            drawableRight.setTintList(drawableTint);
        }

        txtControlInput.setCompoundDrawablesWithIntrinsicBounds( drawableLeft, null, drawableRight, null);
        //input.setCompoundDrawableTintList(drawableTint);
        txtControlInput.setCompoundDrawablePadding(drawablePadding);


        txtControlInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLinkClickListener != null) {
                    onLinkClickListener.onClick(v);
                }
            }
        });

        //android:background="@drawable/control_link_edittextview_background_selector"
        //android:drawablePadding="@dimen/contentHorizontalSpacing"
        //android:drawableLeft="@drawable/ic_edit_location_black_24dp"
        //android:drawableTint="@color/control_link_edittextview_color_selector"
        //android:textColor="@color/control_link_edittextview_color_selector"
        //android:textColorHint="@color/control_link_edittextview_hint_color_selector"

    }

    @Override
    public void changeVisualState(final VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (drawable != null) drawable = drawable.mutate();

        if (state == VisualState.DISABLED) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            txtControlInput.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            label.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.NORMAL) {
            User user = ConfigProvider.getUser();
            label.setTextColor(labelColor);
            setBackground(drawable);
//            txtControlInput.setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
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
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackground(background);

        txtControlInput.setPadding(pl, pt, pr, pb);
    }


    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlTextEditField)nextView).input.requestFocus();
        ((ControlTextEditField)nextView).setCursorToRight();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Handlers">

    private class NotificationVisibilityOnFocusChangeHandler implements OnFocusChangeListener {

        private TeboTextLinkEdit inputEditText;

        public NotificationVisibilityOnFocusChangeHandler(TeboTextLinkEdit inputEditText) {
            this.inputEditText = inputEditText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!v.isEnabled())
                return;

            if (hasError) {
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
            }

            if (hasFocus) {
                this.inputEditText.changeVisualState(VisualState.FOCUSED);
            } else {
                this.inputEditText.changeVisualState(VisualState.NORMAL);
            }

        }
    }

    private class NotificationVisibilityOnClickHandler implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (!v.isEnabled())
                return;

            if (hasError) {
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
            }
        }
    }

    // </editor-fold>
}
