package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboDateTimePicker extends EditTeboPropertyField<Date> implements ITeboDatePicker, IControlValueRequireable {

    private EditText dateInput;
    private EditText timeInput;
    private InverseBindingListener inverseBindingListener;
    private FragmentManager fragmentManager;
    private String dateHint;
    private String timeHint;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboDateTimePicker(Context context) {
        super(context);
    }

    public TeboDateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboDateTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @Override
    public void setValue(Date value) throws InvalidValueException {
        if (value == null)
            return;

        try {
            dateInput.setText(DateHelper.formatDate(value));
        } catch (Exception e) {
            throw new InvalidValueException(value, "Invalid date");
            //enableErrorState("Invalid date");
        }

        try {
            timeInput.setText(DateHelper.formatTime(value));
        } catch (Exception e) {
            throw new InvalidValueException(value, "Invalid time");
            //enableErrorState("Invalid time");
        }
    }

    @Override
    public Date getValue() {
        if ((dateInput.getText() == null || dateInput.getText().toString() == "") &&
                (timeInput.getText() == null || timeInput.getText().toString() == "")) {
            return null;
        }

        Date date = null, time = null;

        try {
            date = DateHelper.parseDate(dateInput.getText().toString());
        } catch (Exception e) { }

        try {
            time = DateHelper.parseTime(timeInput.getText().toString());
        } catch (Exception e) { }


        if (date == null && time == null)
            return null;

        if (time == null)
            return date;

        if (date == null)
            return time;

        LocalDate localDate = new LocalDate(date);
        DateTime dateTime = localDate.toDateTime(new LocalTime(time));
        return dateTime.toDate();
    }

    @BindingAdapter("value")
    public static void setValue(TeboDateTimePicker view, Date date) {
        if (date == view.getValue())
            return;

        try {
            view.setValue(date);
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(TeboDateTimePicker view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboDateTimePicker view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public void setInputType(int type) {
        dateInput.setInputType(type);
        timeInput.setInputType(type);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateInput.setEnabled(enabled);
        timeInput.setEnabled(enabled);
        lblControlLabel.setEnabled(enabled);

    }

    public String getDateHint() {
        return dateInput.getHint().toString();
    }

    public void setDateHint(String dateHint) {
        dateInput.setHint(dateHint);
    }

    public String getTimeHint() {
        return timeInput.getHint().toString();
    }

    public void setTimeHint(String timeHint) {
        timeInput.setHint(timeHint);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">


    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboDateTimePicker,
                    0, 0);

            try {
                dateHint = a.getString(R.styleable.TeboDateTimePicker_dateHint);
                timeHint = a.getString(R.styleable.TeboDateTimePicker_timeHint);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_datetime_picker_layout, this);
    }


    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboDateTimePicker) nextView).dateInput.requestFocus();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        dateInput = (EditText) this.findViewById(R.id.dateInput);
        timeInput = (EditText) this.findViewById(R.id.timeInput);

        dateInput.setNextFocusLeftId(R.id.timeInput);
        dateInput.setNextFocusRightId(R.id.timeInput);
        dateInput.setNextFocusUpId(R.id.timeInput);
        dateInput.setNextFocusDownId(R.id.timeInput);
        dateInput.setNextFocusForwardId(R.id.timeInput);

        timeInput.setNextFocusLeftId(getNextFocusLeft());
        timeInput.setNextFocusRightId(getNextFocusRight());
        timeInput.setNextFocusUpId(getNextFocusUp());
        timeInput.setNextFocusDownId(getNextFocusDown());
        timeInput.setNextFocusForwardId(getNextFocusForward());

        //dateInput.setImeOptions(getImeOptions());

        dateInput.setTextAlignment(getCaptionAlignment());
        timeInput.setTextAlignment(getCaptionAlignment());

        if (getCaptionAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            dateInput.setGravity(getCaptionGravity());
            timeInput.setGravity(getCaptionGravity());
        }


        dateInput.addTextChangedListener(new TextWatcher() {
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
        timeInput.addTextChangedListener(new TextWatcher() {
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
        dateInput.setHint(dateHint);
        timeInput.setHint(timeHint);


        TeboDateTimePicker.OnDateClickHandler dateItemTextClickHandler = new TeboDateTimePicker.OnDateClickHandler(getContext(), this);
        dateInput.setOnFocusChangeListener(new TeboDateTimePicker.OnDateFocusChangeListenerHandler(this));
        dateInput.setOnClickListener(dateItemTextClickHandler);


        TeboDateTimePicker.OnTimeClickHandler timeItemTextClickHandler = new TeboDateTimePicker.OnTimeClickHandler(getContext(), this);
        timeInput.setOnFocusChangeListener(new TeboDateTimePicker.OnTimeFocusChangeListenerHandler(this));
        timeInput.setOnClickListener(timeItemTextClickHandler);
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
            int paddingLeftDate = dateInput.getPaddingLeft();
            int paddingRightDate = dateInput.getPaddingRight();

            dateInput.setPadding(paddingLeftDate, paddingTop, paddingRightDate, paddingBottom);
            dateInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);


            int paddingLeftTime = timeInput.getPaddingLeft();
            int paddingRightTime = timeInput.getPaddingRight();
            timeInput.setPadding(paddingLeftTime, paddingTop, paddingRightTime, paddingBottom);
            timeInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        } else {
            int paddingTop = 0;
            int paddingBottom = 0;
            int paddingLeft = dateInput.getPaddingLeft();
            int paddingRight = dateInput.getPaddingRight();

            dateInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);


            paddingLeft = timeInput.getPaddingLeft();
            paddingRight = timeInput.getPaddingRight();

            timeInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }

        updateControlHeight();
    }

    private void updateControlHeight() {
        if (isSlim()) {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.slimControlHeight);

            dateInput.setHeight(heightInPixel);
            dateInput.setMinHeight(heightInPixel);
            dateInput.setMaxHeight(heightInPixel);

            timeInput.setHeight(heightInPixel);
            timeInput.setMinHeight(heightInPixel);
            timeInput.setMaxHeight(heightInPixel);
        } else {
            int heightInPixel = getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight);
            dateInput.setHeight(heightInPixel);
            dateInput.setMinHeight(heightInPixel);
            dateInput.setMaxHeight(heightInPixel);

            timeInput.setHeight(heightInPixel);
            timeInput.setMinHeight(heightInPixel);
            timeInput.setMaxHeight(heightInPixel);
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
                dateInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                dateInput.setHintTextColor(getResources().getColor(hintColor));

            if (textColor > 0)
                timeInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                timeInput.setHintTextColor(getResources().getColor(hintColor));

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
                dateInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                dateInput.setHintTextColor(getResources().getColor(hintColor));

            if (textColor > 0)
                timeInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                timeInput.setHintTextColor(getResources().getColor(hintColor));

            lblControlLabel.setTextColor(textColor);
            return;
        }

        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);

            if (textColor > 0)
                dateInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                dateInput.setHintTextColor(getResources().getColor(hintColor));

            if (textColor > 0)
                timeInput.setTextColor(getResources().getColor(textColor));

            if (hintColor > 0)
                timeInput.setHintTextColor(getResources().getColor(hintColor));

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
        int pl = dateInput.getPaddingLeft();
        int pt = dateInput.getPaddingTop();
        int pr = dateInput.getPaddingRight();
        int pb = dateInput.getPaddingBottom();

        dateInput.setBackgroundResource(resid);

        dateInput.setPadding(pl, pt, pr, pb);
        updateControlHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = dateInput.getPaddingLeft();
        int pt = dateInput.getPaddingTop();
        int pr = dateInput.getPaddingRight();
        int pb = dateInput.getPaddingBottom();

        dateInput.setBackground(background);

        dateInput.setPadding(pl, pt, pr, pb);
        updateControlHeight();
    }


    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialize Methods">

    public void initialize(final FragmentManager fm) {
        this.fragmentManager = fm;
        dateInput.setInputType(InputType.TYPE_NULL);
        timeInput.setInputType(InputType.TYPE_NULL);
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
                dateInput.setText(DateHelper.formatDate(DateHelper.getDateZero(yy, mm, dd)));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateInput.setText(null);
                TeboDateTimePicker.this.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(TeboDatePickerFragment.KEY_TEBO_DATE_PICKER, this.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(this.fragmentManager, getResources().getText(R.string.hint_select_a_date).toString());
    }

    private void showTimeFragment() {
        if (this.fragmentManager == null)
            return;

        TeboTimePickerFragment newFragment = new TeboTimePickerFragment();

        newFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeInput.setText(DateHelper.formatTime(DateHelper.getTime(hourOfDay, minute)));
            }
        });

        newFragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timeInput.setText(null);
                TeboDateTimePicker.this.clearFocus();
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(TeboTimePickerFragment.KEY_TEBO_TIME_PICKER, this.getValue());
        newFragment.setArguments(dateBundle);
        newFragment.show(this.fragmentManager, getResources().getText(R.string.hint_select_a_time).toString());
    }

    private void ensureSingleLine() {
        dateInput.setSingleLine(true);
        dateInput.setMaxLines(1);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">

    private class OnDateFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboDateTimePicker datePicker;

        public OnDateFocusChangeListenerHandler(TeboDateTimePicker datePicker) {
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

    private class OnDateClickHandler implements OnClickListener {

        private Context context;
        private TeboDateTimePicker datePicker;

        public OnDateClickHandler(Context context, TeboDateTimePicker datePicker) {
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

    private class OnTimeFocusChangeListenerHandler implements OnFocusChangeListener {

        private TeboDateTimePicker datePicker;

        public OnTimeFocusChangeListenerHandler(TeboDateTimePicker datePicker) {
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

    private class OnTimeClickHandler implements OnClickListener {

        private Context context;
        private TeboDateTimePicker datePicker;

        public OnTimeClickHandler(Context context, TeboDateTimePicker datePicker) {
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
