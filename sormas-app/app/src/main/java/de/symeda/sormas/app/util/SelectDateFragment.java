package de.symeda.sormas.app.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.app.R;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 *
 * you have to override the methods
 * onDateSet(DatePicker view, int yy, int mm, int dd)
 * and
 * onClear()
 */
public abstract class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String DATE = "DATE";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().get(DATE);
        final Calendar calendar = Calendar.getInstance();
        if(date!=null) {
            calendar.setTime(date);
        }
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerClear = new DatePickerDialog(getActivity(), this, yy, mm, dd);
        datePickerClear.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                getResources().getText(R.string.action_clear),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClear();
                    }
                });
        return datePickerClear;
    }

    @Override
    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
    }

    protected void onClear() {
    }
}