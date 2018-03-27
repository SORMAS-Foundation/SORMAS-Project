package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Date;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboTimePicker extends EditTeboPropertyField<Date> implements ITeboTimePicker, IControlValueRequireable {

    private EditText txtControlInput;
    private InverseBindingListener inverseBindingListener;
    private FragmentManager fragmentManager;
    private String hint;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboTimePicker(Context context) {
        super(context);
    }

    public TeboTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @Override
    public void setValue(Date value) throws InvalidValueException {
        if (value == null)
            return;

        try {
            txtControlInput.setText(DateHelper.formatTime(value));
        } catch (Exception e) {
            throw new InvalidValueException(value, "Invalid time");
            //enableErrorState("Invalid time");
        }
    }

    @Override
    public Date getValue() {
        if (txtControlInput.getText() == null || txtControlInput.getText().toString() == "")
            return null;

        return DateHelper.parseDate(txtControlInput.getText().toString());
    }

    @BindingAdapter("value")
    public static void setValue(TeboTimePicker view, Date date) {
        if (date == view.getValue())
            return;

        try {
            view.setValue(date);
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(TeboTimePicker view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboTimePicker view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void setInputType(int type) {
        txtControlInput.setInputType(type);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtControlInput.setEnabled(enabled);
        lblControlLabel.setEnabled(enabled);

    }

    public void setHint(String value) {
        txtControlInput.setHint(value);
    }

    public String getHint() {
        return txtControlInput.getHint().toString();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboTimePicker,
                    0, 0);

            try {
                hint = a.getString(R.styleable.TeboTimePicker_hint);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_time_picker_layout, this);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboTimePicker) nextView).txtControlInput.requestFocus();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (EditText) this.findViewById(R.id.txtControlInput);

        txtControlInput.setNextFocusLeftId(getNextFocusLeft());
        txtControlInput.setNextFocusRightId(getNextFocusRight());
        txtControlInput.setNextFocusUpId(getNextFocusUp());
        txtControlInput.setNextFocusDownId(getNextFocusDown());
        txtControlInput.setNextFocusForwardId(getNextFocusForward());

        //txtControlInput.setImeOptions(getImeOptions());

        txtControlInput.setTextAlignment(getCaptionAlignment());

        if (getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getCaptionGravity());
        }


        txtControlInput.addTextChangedListener(new TextWatcher() {
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

        //Set Hint
        txtControlInput.setHint(hint);


        CompositeOnFocusChangeListener txtControlInputListeners = new CompositeOnFocusChangeListener();
        txtControlInputListeners.registerListener(new TeboTimePicker.OnFocusChangeListenerHandler(this));

        TeboTimePicker.OnEditTextClickHandler editTextClickHandler = new TeboTimePicker.OnEditTextClickHandler(getContext(), this);


        txtControlInput.setOnFocusChangeListener(txtControlInputListeners);
        txtControlInput.setOnClickListener(editTextClickHandler);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        updateControlDimensions();
    }

    private void updateControlDimensions() {
        if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            int paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            int paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            int paddingLeft = txtControlInput.getPaddingLeft();
            int paddingRight = txtControlInput.getPaddingRight();

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            txtControlInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        } else {
            int paddingTop = 0;
            int paddingBottom = 0;
            int paddingLeft = txtControlInput.getPaddingLeft();
            int paddingRight = txtControlInput.getPaddingRight();

            txtControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        updateControlHeight();
    }

    private void updateControlHeight() {
        if (isSlim()) {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
        } else {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight);
            txtControlInput.setHeight(heightInPixel);
            txtControlInput.setMinHeight(heightInPixel);
            txtControlInput.setMaxHeight(heightInPixel);
        }
    }

    @Override
    public void changeVisualState(VisualState state, UserRight editOrCreateUserRight) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.EDIT_TEXT));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.EDIT_TEXT));
        int textColor = state.getTextColor(VisualStateControl.EDIT_TEXT);
        int hintColor = state.getHintColor(VisualStateControl.EDIT_TEXT);

        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            setEnabled(false);
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

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            lblControlLabel.setTextColor(textColor);
            return;
        }

        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                txtControlInput.setHintTextColor(getResources().getColor(hintColor));

            setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
            return;
        }
    }

    @Override
    public boolean isRequiredStatusValid() {
        if (!isRequired())
            return true;

        return getValue() != null;
    }

    @Override
    public void setBackgroundResource(int resid) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackgroundResource(resid);

        txtControlInput.setPadding(pl, pt, pr, pb);
        updateControlHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = txtControlInput.getPaddingLeft();
        int pt = txtControlInput.getPaddingTop();
        int pr = txtControlInput.getPaddingRight();
        int pb = txtControlInput.getPaddingBottom();

        txtControlInput.setBackground(background);

        txtControlInput.setPadding(pl, pt, pr, pb);
        updateControlHeight();
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialize Methods">

    public void initialize(final FragmentManager fm) {
        this.fragmentManager = fm;
        txtControlInput.setInputType(InputType.TYPE_NULL);
        this.clearFocus();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private void showTimeFragment() {
        if (this.fragmentManager == null)
            return;

        TeboTimePickerFragment newFragment = new TeboTimePickerFragment();

        newFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                txtControlInput.setText(DateHelper.formatTime(DateHelper.getTime(hourOfDay, minute)));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtControlInput.setText(null);
                TeboTimePicker.this.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(TeboTimePickerFragment.KEY_TEBO_TIME_PICKER, this.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(this.fragmentManager, getResources().getText(R.string.hint_select_a_time).toString());
    }

    private void ensureSingleLine() {
        txtControlInput.setSingleLine(true);
        txtControlInput.setMaxLines(1);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">

    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboTimePicker datePicker;

        public OnFocusChangeListenerHandler(TeboTimePicker datePicker) {
            this.datePicker = datePicker;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!v.isEnabled())
                return;

            if (inErrorState()) {
                //changeVisualState(VisualState.ERROR);
                showNotification();

                return;
            } else {
                hideNotification();
            }

            //int colorOnFocus = v.getResources().getColor(VisualState.FOCUSED.getLabelColor());
            //int colorDefault = v.getResources().getColor(VisualState.NORMAL.getLabelColor());
            if (hasFocus) {
                changeVisualState(VisualState.FOCUSED);
                //lblControlLabel.setTextColor(colorOnFocus);
                showTimeFragment();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else {
                changeVisualState(VisualState.NORMAL);
                //lblControlLabel.setTextColor(colorDefault);
            }

        }
    }

    private class OnEditTextClickHandler implements OnClickListener {

        private Context context;
        private TeboTimePicker datePicker;

        public OnEditTextClickHandler(Context context, TeboTimePicker datePicker) {
            this.context = context;
            this.datePicker = datePicker;
        }

        @Override
        public void onClick(View v) {
            if (!v.isEnabled())
                return;

            if (inErrorState()) {
                showNotification();

                return;
            } else {
                hideNotification();
                showTimeFragment();
            }
        }
    }

    // </editor-fold>
}