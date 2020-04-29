/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.controls;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.VisualStateControlType;
import de.symeda.sormas.app.util.DateFormatHelper;

public class ControlDateTimeField extends ControlPropertyEditField<Date> {

    // Views

    private EditText dateInput;
    private EditText timeInput;

    // Attributes

    private String dateHint;
    private String timeHint;
    private int allowedDaysInFuture;

    // Listeners

    private InverseBindingListener inverseBindingListener;

    // Other fields

    private FragmentManager fragmentManager;
    private SimpleDateFormat dateFormat;

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

    /**
     * @return true if an error is set, false if not
     */
    public boolean setErrorIfOutOfDateRange() {
        if (getFieldValue() == null || getFieldValue().before(new Date())) {
            return false;
        }

        if (allowedDaysInFuture > 0) {
            if (DateHelper.getFullDaysBetween(new Date(), getFieldValue()) > allowedDaysInFuture) {
                enableErrorState(I18nProperties.getValidationError(Validations.futureDate, getCaption(), allowedDaysInFuture));
                return true;
            }
        } else if (allowedDaysInFuture == 0) {
            if (!DateHelper.isSameDay(new Date(), getFieldValue())) {
                enableErrorState(I18nProperties.getValidationError(Validations.futureDateStrict, getCaption()));
                return true;
            }
        }

        return false;
    }

    private void showDateFragment() {
        if (fragmentManager == null) {
            Log.e(getClass().getName(), "Tried to show date fragment before setting fragment manager");
            return;
        }

        ControlDatePickerFragment fragment = new ControlDatePickerFragment();
        fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                dateInput.setText(DateHelper.formatLocalDate(DateHelper.getDateZero(yy, mm, dd), dateFormat));
            }
        });
        fragment.setOnClearListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateInput.setText(null);
            }
        });

        Bundle dateBundle = new Bundle();
        dateBundle.putSerializable(ControlDatePickerFragment.KEY_DATE, this.getFieldValue());
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
        timeBundle.putSerializable(ControlTimePickerFragment.KEY_TIME, this.getFieldValue());
        fragment.setArguments(timeBundle);
        fragment.show(fragmentManager, getResources().getText(R.string.hint_select_a_time).toString());
    }

//    private void setUpOnFocusChangeListener(final EditText input) {
//        input.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!v.isEnabled()) {
//                    return;
//                }
//
//                showOrHideNotifications(hasFocus);
//
//                if (hasFocus) {
//                    changeVisualState(VisualState.FOCUSED);
//                    if (input == dateInput) {
//                        showDateFragment();
//                    } else {
//                        showTimeFragment();
//                    }
//                } else {
//                    if (hasError) {
//                        changeVisualState(VisualState.ERROR);
//                    } else {
//                        changeVisualState(VisualState.NORMAL);
//                    }
//                }
//            }
//        });
//    }

    private void setUpOnClickListener(final EditText input) {
        input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isEnabled()) {
                    return;
                }

//                showOrHideNotifications(v.hasFocus());
//
//                if (v.hasFocus()) {
                    if (input == dateInput) {
                        showDateFragment();
                    } else {
                        showTimeFragment();
                    }
//                }
            }
        });
    }

    public void initializeDateTimeField(final FragmentManager fm) {
        this.fragmentManager = fm;
    }

    // Overrides

    @Override
    protected Date getFieldValue() {
        if (StringUtils.isEmpty(dateInput.getText().toString())) {
            return null;
        }

        Date date = DateHelper.parseDate(dateInput.getText().toString(), dateFormat);
        Date time = !StringUtils.isEmpty(timeInput.getText().toString())
                ? DateHelper.parseTime(timeInput.getText().toString())
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
    protected void setFieldValue(Date value) {
        if (value == null) {
            dateInput.setText(null);
            timeInput.setText(null);
        } else {
            dateInput.setText(DateHelper.formatLocalDate(value, dateFormat));
            timeInput.setText(DateHelper.formatTime(value));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateInput.setEnabled(enabled);
        timeInput.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    @Override
    public void setHint(String value) {
        dateInput.setHint(dateHint);
        timeInput.setHint(timeHint);
    }

    @Override
    protected void initialize(Context context, AttributeSet attrs, int defStyle) {
        dateFormat = DateFormatHelper.getLocalDateFormat();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlDateTimeField,
                    0, 0);

            try {
                dateHint = a.getString(R.styleable.ControlDateTimeField_dateHint);
                timeHint = a.getString(R.styleable.ControlDateTimeField_timeHint);
                allowedDaysInFuture = a.getInt(R.styleable.ControlDateTimeField_allowedDaysInFuture, 0);
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

        addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (!isLiveValidationDisabled()) {
                    ((ControlDateTimeField) field).setErrorIfOutOfDateRange();
                }
            }
        });

//        setUpOnFocusChangeListener(dateInput);
//        setUpOnFocusChangeListener(timeInput);
        setUpOnClickListener(dateInput);
        setUpOnClickListener(timeInput);
    }

    @Override
    protected void changeVisualState(VisualState state) {
        if (getUserEditRight() != null && !ConfigProvider.hasUserRight(getUserEditRight())) {
            state = VisualState.DISABLED;
        }

        if (this.visualState == state) {
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
            dateInput.setTextColor(textColor);
            timeInput.setTextColor(textColor);
            dateInput.setHintTextColor(hintColor);
            timeInput.setHintTextColor(hintColor);
        }

        setEnabled(state != VisualState.DISABLED);
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
        view.setFieldValue(date);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Date getValue(ControlDateTimeField view) {
        return view.getFieldValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(ControlDateTimeField view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @BindingAdapter("dateFormat")
    public static void setDateFormat(ControlDateTimeField field, SimpleDateFormat dateFormat) {
        field.dateFormat = dateFormat;
    }

    public String getDateHint() {
        return dateInput.getHint().toString();
    }

    public String getTimeHint() {
        return timeInput.getHint().toString();
    }

}
