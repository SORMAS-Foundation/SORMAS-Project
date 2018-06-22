package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.ITeboDatePicker;
import de.symeda.sormas.app.component.InvalidValueException;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;

/**
 * Created by Orson on 31/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboDatePicker extends ControlPropertyEditField<Date> implements ITeboDatePicker {

    private EditText txtControlInput;
    private InverseBindingListener inverseBindingListener;
    private FragmentManager fragmentManager;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboDatePicker(Context context) {
        super(context);
    }

    public TeboDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @Override
    public void setValue(Date value) throws InvalidValueException {
        if (value == null)
            return;

        try {
            txtControlInput.setText(DateHelper.formatDate(value));
        } catch (Exception e) {
            throw new InvalidValueException(value, "Invalid date");
            //enableErrorState("Invalid date");
        }
    }

    @Override
    public Date getValue() {
        if (txtControlInput.getText() == null || txtControlInput.getText().toString() == "")
            return null;

        return DateHelper.parseDate(txtControlInput.getText().toString());
    }

    @BindingAdapter("value")
    public static void setValue(TeboDatePicker view, Date date) {
        if (date == view.getValue())
            return;

        try {
            view.setValue(date);
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(TeboDatePicker view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboDatePicker view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void setInputType(int type) {
        txtControlInput.setInputType(type);
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

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">


    @Override
    protected void initializeView(Context context, AttributeSet attrs, int defStyle) {
        /*if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboDatePicker,
                    0, 0);

            try {

            } finally {
                a.recycle();
            }
        }*/
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_date_picker_layout, this);
    }


    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboDatePicker) nextView).txtControlInput.requestFocus();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        txtControlInput = (EditText) this.findViewById(R.id.input);

        //input.setImeOptions(getImeOptions());

        txtControlInput.setTextAlignment(getControlTextAlignment());

        if (getControlTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            txtControlInput.setGravity(getControlGravity());
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


        CompositeOnFocusChangeListener txtControlInputListeners = new CompositeOnFocusChangeListener();
        txtControlInputListeners.registerListener(new OnFocusChangeListenerHandler(this));

        OnEditTextClickHandler editTextClickHandler = new OnEditTextClickHandler(getContext(), this);


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
    public void changeVisualState(VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        int textColor = getResources().getColor(state.getTextColor());
        int hintColor = getResources().getColor(state.getHintColor());

        //Drawable drawable = getResources().getDrawable(R.drawable.selector_text_control_edit_error);

        if (state == VisualState.DISABLED) {
            label.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(textColor);

            if (hintColor > 0)
                txtControlInput.setHintTextColor(hintColor);

            setEnabled(false);
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

            if (textColor > 0)
                txtControlInput.setTextColor(textColor);

            if (hintColor > 0)
                txtControlInput.setHintTextColor(hintColor);

            label.setTextColor(textColor);
            return;
        }

        if (state == VisualState.NORMAL) {
            User user = ConfigProvider.getUser();
            label.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                txtControlInput.setTextColor(textColor);

            if (hintColor > 0)
                txtControlInput.setHintTextColor(hintColor);

//            setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
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

    private void showDateFragment() {
        if (this.fragmentManager == null)
            return;

        TeboDatePickerFragment newFragment = new TeboDatePickerFragment();

        newFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                txtControlInput.setText(DateHelper.formatDate(DateHelper.getDateZero(yy, mm, dd)));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtControlInput.setText(null);
                TeboDatePicker.this.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(TeboDatePickerFragment.KEY_TEBO_DATE_PICKER, this.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(this.fragmentManager, getResources().getText(R.string.hint_select_a_date).toString());
    }

    private void ensureSingleLine() {
        txtControlInput.setSingleLine(true);
        txtControlInput.setMaxLines(1);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">

    private class OnFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboDatePicker datePicker;

        public OnFocusChangeListenerHandler(TeboDatePicker datePicker) {
            this.datePicker = datePicker;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!v.isEnabled())
                return;

            if (hasError) {
                //changeVisualState(VisualState.ERROR);
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
            }

            //int colorOnFocus = v.getResources().getColor(VisualState.FOCUSED.getLabelColor());
            //int colorDefault = v.getResources().getColor(VisualState.NORMAL.getLabelColor());
            if (hasFocus) {
                changeVisualState(VisualState.FOCUSED);
                //label.setTextColor(colorOnFocus);
                showDateFragment();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else {
                changeVisualState(VisualState.NORMAL);
                //label.setTextColor(colorDefault);
            }

        }
    }

    private class OnEditTextClickHandler implements OnClickListener {

        private Context context;
        private TeboDatePicker datePicker;

        public OnEditTextClickHandler(Context context, TeboDatePicker datePicker) {
            this.context = context;
            this.datePicker = datePicker;
        }

        @Override
        public void onClick(View v) {
            if (!v.isEnabled())
                return;

            if (hasError) {
                showErrorNotification();

                return;
            } else {
                hideErrorNotification();
                showDateFragment();
            }
        }
    }

    // </editor-fold>
}
