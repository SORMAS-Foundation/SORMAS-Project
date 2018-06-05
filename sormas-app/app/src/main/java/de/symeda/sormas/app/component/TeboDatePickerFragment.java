package de.symeda.sormas.app.component;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.symeda.sormas.app.R;

/**
 * Created by Orson on 31/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    public static final String KEY_TEBO_DATE_PICKER = "TeboDatePicker";
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DialogInterface.OnClickListener onClearListener;

    private Date dateValue = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState == null)
            return;

        outState.putSerializable(KEY_TEBO_DATE_PICKER, dateValue);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        if (arguments == null || arguments.isEmpty())
            return;

        if (!arguments.containsKey(KEY_TEBO_DATE_PICKER))
            return;

        dateValue = (Date) arguments.get(KEY_TEBO_DATE_PICKER);
    }


    //1. onCreate(Bundle)
    //2. onCreateDialog(Bundle savedInstanceState)
    //3. onCreateView(LayoutInflater, ViewGroup, Bundle)

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Date date = (Date) getArguments().get(KEY_TEBO_TIME_PICKER);

        final Calendar calendar = Calendar.getInstance();
        if(dateValue != null) {
            GregorianCalendar date = new GregorianCalendar();
            date.setGregorianChange(dateValue);
            calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerWithClear = new DatePickerDialog(getActivity(),
                R.style.Theme_Tebo_Dialog_DatePicker, this, year, month, dayOfMonth);

        //datePickerWithClear.getDatePicker().setCalendarViewShown(true);
        //datePickerWithClear.getDatePicker().setSpinnersShown(false);
        datePickerWithClear.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                getResources().getText(R.string.action_clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePickerWithClear.cancel();
                        onClearListener.onClick(dialog, which);
                    }
                });

        return datePickerWithClear;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (onDateSetListener != null)
            onDateSetListener.onDateSet(view, year, month, dayOfMonth);
    }

    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }
}
