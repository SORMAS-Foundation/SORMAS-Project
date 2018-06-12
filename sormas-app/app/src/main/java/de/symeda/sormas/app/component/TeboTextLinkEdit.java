package de.symeda.sormas.app.component;

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

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Orson on 08/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboTextLinkEdit extends EditTeboPropertyField<String> implements IControlValueRequireable {


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
        lblControlLabel.setEnabled(enabled);

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
            //txtControlInput.setLines(1);
            txtControlInput.setInputType(inputType);
            txtControlInput.setVerticalScrollBarEnabled(false);
        } else {
            txtControlInput.setMaxLines(maxLines);
            //txtControlInput.setLines(maxLines);
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

        //txtControlInput.setOnClickListener(onLinkClickListener);
        this.onLinkClickListener = onLinkClickListener;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    
    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTextLinkEdit,
                    0, 0);

            try {
                singleLine = a.getBoolean(R.styleable.TeboTextLinkEdit_singleLine, true);
                maxLines = a.getInt(R.styleable.TeboTextLinkEdit_maxLines, 1);
                inputType = a.getInt(R.styleable.TeboTextLinkEdit_inputType, InputType.TYPE_CLASS_TEXT);
                drawableStart = a.getDrawable(R.styleable.TeboTextLinkEdit_drawableStart);
                drawableEnd = a.getDrawable(R.styleable.TeboTextLinkEdit_drawableEnd);
                drawableLeft = a.getDrawable(R.styleable.TeboTextLinkEdit_drawableLeft);
                drawableRight = a.getDrawable(R.styleable.TeboTextLinkEdit_drawableRight);
                textColor = a.getColorStateList(R.styleable.TeboTextLinkEdit_textColor);
                textColorHint = a.getColorStateList(R.styleable.TeboTextLinkEdit_textColorHint);
                background = a.getDrawable(R.styleable.TeboTextLinkEdit_background);
                drawablePadding = a.getDimensionPixelSize(R.styleable.TeboTextLinkEdit_drawablePadding, 0);
                drawableTint = a.getColorStateList(R.styleable.TeboTextLinkEdit_drawableTint);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_link_textview_edit_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (TextView) this.findViewById(R.id.txtControlInput);

    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        txtControlInput.setNextFocusLeftId(getNextFocusLeft());
        txtControlInput.setNextFocusRightId(getNextFocusRight());
        txtControlInput.setNextFocusUpId(getNextFocusUp());
        txtControlInput.setNextFocusDownId(getNextFocusDown());
        txtControlInput.setNextFocusForwardId(getNextFocusForward());

        txtControlInput.setImeOptions(getImeOptions());

        txtControlInput.setTextAlignment(getCaptionAlignment());
        if(getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getCaptionGravity());
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
        //txtControlInput.setCompoundDrawableTintList(drawableTint);
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
    public void changeVisualState(final VisualState state, UserRight editOrCreateUserRight) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.EDIT_TEXT));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.EDIT_TEXT));
        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (drawable != null) drawable = drawable.mutate();

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            txtControlInput.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            txtControlInput.setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
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
    public boolean isRequiredStatusValid() {
        if (!isRequired())
            return true;

        return getValue() != "";
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboTextInputEditText)nextView).txtControlInput.requestFocus();
        ((TeboTextInputEditText)nextView).setCursorToRight();
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

            if (inErrorState()) {
                showNotification();

                return;
            } else {
                hideNotification();
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

            if (inErrorState()) {
                showNotification();

                return;
            } else {
                hideNotification();
            }
        }
    }

    // </editor-fold>
}
