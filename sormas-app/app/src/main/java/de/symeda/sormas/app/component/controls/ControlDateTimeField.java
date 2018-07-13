package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;

public class ControlDateTimeField extends ControlPropertyEditField<Date> {

    // Views

    private EditText dateInput;
    private EditText timeInput;

    // Attributes

    private String dateHint;
    private String timeHint;

    // Listeners

    private InverseBindingListener inverseBindingListener;

    // Other fields

    private FragmentManager fragmentManager;

    // Constructors

    public ControlDateTimeField(Context context) {
        super(context);
    }

    public ControlDateTimeField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlDateTimeField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    // Instance methods

    private void showDateFragment() {
        if (fragmentManager == null) {
            Log.e(getClass().getName(), "Tried to show date fragment before setting fragment manager");
            return;
        }

        ControlDatePickerFragment fragment = new ControlDatePickerFragment();
        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                dateInput.setText(DateHelper.formatDate(DateHelper.getDateZero(yy, mm, dd)));
            }
        });
        fragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateInput.setText(null);
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(ControlDatePickerFragment.KEY_DATE, this.getValue());
        fragment.setArguments(dateBundle);
        fragment.show(fragmentManager, getResources().getText(R.string.hint_select_a_date).toString());
    }

    private void showTimeFragment() {
        if (fragmentManager == null) {
            Log.e(getClass().getName(), "Tried to show time fragment before setting fragment manager");
            return;
        }

        ControlTimePickerFragment fragment = new ControlTimePickerFragment();
        fragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeInput.setText(DateHelper.formatTime(DateHelper.getTime(hourOfDay, minute)));
            }
        });
        fragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timeInput.setText(null);
            }
        });

        Bundle timeBundle = new Bundle();
        timeBundle.putSerializable(ControlTimePickerFragment.KEY_TIME, this.getValue());
        fragment.setArguments(timeBundle);
        fragment.show(fragmentManager, getResources().getText(R.string.hint_select_a_time).toString());
    }

    private void setUpOnFocusChangeListener(final EditText input) {
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(hasFocus);

                if (hasFocus) {
                    changeVisualState(VisualState.FOCUSED);
                    if (input == dateInput) {
                        showDateFragment();
                    } else {
                        showTimeFragment();
                    }
                } else {
                    if (hasError) {
                        changeVisualState(VisualState.ERROR);
                    } else {
                        changeVisualState(VisualState.NORMAL);
                    }
                }
            }
        });
    }

    private void setUpOnClickListener(final EditText input) {
        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

                showOrHideNotifications(v.hasFocus());

                if (v.hasFocus()) {
                    if (input == dateInput) {
                        showDateFragment();
                    } else {
                        showTimeFragment();
                    }
                }
            }
        });
    }

    public void initializeDateTimeField(final FragmentManager fm) {
        this.fragmentManager = fm;
    }

    // Overrides

    @Override
    public Date getValue() {
        if (StringUtils.isEmpty(dateInput.getText().toString())) {
            return null;
        }

        Date date = DateHelper.parseDate(dateInput.getText().toString());
        Date time = !StringUtils.isEmpty(timeInput.getText().toString())
                ? DateHelper.parseDate(timeInput.getText().toString())
                : null;

        if (time != null) {
            LocalDate localDate = new LocalDate(date);
            DateTime dateTime = localDate.toDateTime(new LocalTime(time));
            return dateTime.toDate();
        } else {
            return date;
        }
    }

    @Override
    public void setValue(Date value) {
        if (value == null) {
            dateInput.setText(null);
            timeInput.setText(null);
        } else {
            dateInput.setText(DateHelper.formatDate(value));
            timeInput.setText(DateHelper.formatTime(value));
        }
        setInternalValue(value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateInput.setEnabled(enabled);
        timeInput.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    @Override
    protected void setHint(String value) {
        dateInput.setHint(dateHint);
        timeInput.setHint(timeHint);
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlDateTimeField,
                    0, 0);

            try {
                dateHint = a.getString(R.styleable.ControlDateTimeField_dateHint);
                timeHint = a.getString(R.styleable.ControlDateTimeField_timeHint);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            if (isSlim()) {
                inflater.inflate(R.layout.control_datetime_picker_slim_layout, this);
            } else {
                inflater.inflate(R.layout.control_datetime_picker_layout, this);
            }
        }
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((ControlDateTimeField) nextView).dateInput.requestFocus();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        dateInput = (EditText) this.findViewById(R.id.date_input);
        timeInput = (EditText) this.findViewById(R.id.time_input);
        dateInput.setInputType(InputType.TYPE_NULL);
        timeInput.setInputType(InputType.TYPE_NULL);
        dateInput.setTextAlignment(getTextAlignment());
        timeInput.setTextAlignment(getTextAlignment());
        if (getTextAlignment() == View.TEXT_ALIGNMENT_GRAVITY) {
            dateInput.setGravity(getGravity());
            timeInput.setGravity(getGravity());
        }

        dateInput.addTextChangedListener(new TextWatcher() {
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

        timeInput.addTextChangedListener(new TextWatcher() {
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

        setUpOnFocusChangeListener(dateInput);
        setUpOnFocusChangeListener(timeInput);
        setUpOnClickListener(dateInput);
        setUpOnClickListener(timeInput);
    }

    @Override
    public void changeVisualState(VisualState state) {
        if (state != VisualState.DISABLED && getUserEditRight() != null
                && !ConfigProvider.getUser().hasUserRight(getUserEditRight())) {
            return;
        }

        visualState = state;

        int labelColor = getResources().getColor(state.getLabelColor());
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControlType.TEXT_FIELD));
        int textColor = getResources().getColor(state.getTextColor());
        int hintColor = getResources().getColor(state.getHintColor());

        if (drawable != null) {
            drawable = drawable.mutate();
        }

        label.setTextColor(labelColor);
        setBackground(drawable);

        if (state != VisualState.ERROR) {
            if (textColor > 0) {
                dateInput.setTextColor(textColor);
                timeInput.setTextColor(textColor);
            }
            if (hintColor > 0) {
                dateInput.setHintTextColor(hintColor);
                timeInput.setHintTextColor(hintColor);
            }
        }

        if (state == VisualState.DISABLED) {
            setEnabled(false);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        setBackgroundResourceFor(dateInput, resId);
        setBackgroundResourceFor(timeInput, resId);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundFor(dateInput, background);
        setBackgroundFor(timeInput, background);
    }

    // Data binding, getters & setters

    @BindingAdapter("value")
    public static void setValue(ControlDateTimeField view, Date date) {
        view.setValue(date);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(ControlDateTimeField view) {
        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlDateTimeField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    public String getDateHint() {
        return dateInput.getHint().toString();
    }

    public String getTimeHint() {
        return timeInput.getHint().toString();
    }

}
