package de.symeda.sormas.app.component.controls;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 14/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ControlTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String KEY_LINKED_TIME = "LinkedTime";
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private DialogInterface.OnClickListener onClearListener;

    private boolean is24HourView;

    private Date timeValue = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState == null)
            return;

        outState.putSerializable(KEY_LINKED_TIME, timeValue);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.is24HourView = true;

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        if (arguments == null || arguments.isEmpty())
            return;

        if (!arguments.containsKey(KEY_LINKED_TIME))
            return;

        timeValue = (Date) arguments.get(KEY_LINKED_TIME);
    }


    //1. onCreate(Bundle)
    //2. onCreateDialog(Bundle savedInstanceState)
    //3. onCreateView(LayoutInflater, ViewGroup, Bundle)

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Date date = (Date) getArguments().get(KEY_LINKED_TIME);

        final Calendar calendar = Calendar.getInstance();
        if(timeValue != null) {
            calendar.setTime(timeValue);
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        final TimePickerDialog timePickerWithClear = new TimePickerDialog(getActivity(),
                R.style.Theme_Tebo_Dialog_DatePicker, this, hour, minute, is24HourView);

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


        /*public TimePickerDialog(Context context, int themeResId, OnTimeSetListener listener,
            int hourOfDay, int minute, boolean is24HourView) {*/

        return timePickerWithClear;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (onTimeSetListener != null)
            onTimeSetListener.onTimeSet(view, hourOfDay, minute);
    }

    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }
}
