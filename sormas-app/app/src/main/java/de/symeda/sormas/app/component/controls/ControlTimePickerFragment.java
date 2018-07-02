package de.symeda.sormas.app.component.controls;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.app.R;

public class ControlTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    // Constants

    public static final String KEY_LINKED_TIME = "LinkedTime";

    // Attributes

    private Date time = null;

    // Listeners

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private DialogInterface.OnClickListener onClearListener;

    // Overrides

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putSerializable(KEY_LINKED_TIME, time);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

        if (arguments != null && arguments.containsKey(KEY_LINKED_TIME)) {
            time = (Date) arguments.get(KEY_LINKED_TIME);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (time != null) {
            calendar.setTime(time);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        final TimePickerDialog timePickerWithClear = new TimePickerDialog(getActivity(),
                R.style.Theme_Tebo_Dialog_DatePicker, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        timePickerWithClear.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                getResources().getText(R.string.action_clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timePickerWithClear.cancel();
                        onClearListener.onClick(dialog, which);
                    }
                });

        return timePickerWithClear;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (onTimeSetListener != null)
            onTimeSetListener.onTimeSet(view, hourOfDay, minute);
    }

    // Getters & setters

    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }

}
