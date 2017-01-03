package de.symeda.sormas.app.util;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.app.R;

/**
 * Created by Mate Strysewske on 03.01.2017.
 */
public class SelectTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String DATE = "DATE";
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private DialogInterface.OnClickListener onClearListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().get(DATE);
        final Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        final TimePickerDialog timePickerClear = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePickerClear.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                getResources().getText(R.string.action_clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timePickerClear.cancel();
                        onClearListener.onClick(dialog, which);
                    }
                });
        return timePickerClear;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        onTimeSetListener.onTimeSet(view,hourOfDay,minute);
    }

    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}
