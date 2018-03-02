package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.CompositeOnFocusChangeListener;

/**
 * Created by Orson on 31/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboDatePicker extends EditTeboPropertyField<Date> implements ITeboDatePicker, IControlValueRequireable {

    private EditText txtControlInput;
    private InverseBindingListener inverseBindingListener;
    private FragmentManager fragmentManager;
    private String hint;

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
                    R.styleable.TeboDatePicker,
                    0, 0);

            try {
                hint = a.getString(R.styleable.TeboDatePicker_hint);
            } finally {
                a.recycle();
            }
        }
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
        txtControlInputListeners.registerListener(new OnFocusChangeListenerHandler(this));

        OnEditTextClickHandler editTextClickHandler = new OnEditTextClickHandler(getContext(), this);


        txtControlInput.setOnFocusChangeListener(txtControlInputListeners);
        txtControlInput.setOnClickListener(editTextClickHandler);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

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
    }

    @Override
    public void changeVisualState(VisualState state) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.EDIT_TEXT));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.EDIT_TEXT));

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
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            txtControlInput.setEnabled(true);
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
                showDateFragment();
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
        private TeboDatePicker datePicker;

        public OnEditTextClickHandler(Context context, TeboDatePicker datePicker) {
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
                showDateFragment();
            }
        }
    }

    // </editor-fold>
}
