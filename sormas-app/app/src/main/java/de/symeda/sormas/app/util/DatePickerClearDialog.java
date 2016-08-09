package de.symeda.sormas.app.util;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public class DatePickerClearDialog extends DatePickerDialog {

    public DatePickerClearDialog(Context context, OnDateSetListener callBack,
                                 int year, int monthOfYear, int dayOfMonth) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);

        setButton(BUTTON_POSITIVE, ("Ok"), this);
        setButton(BUTTON_NEUTRAL, ("Clear"), this); // ADD THIS
        setButton(BUTTON_NEGATIVE, ("Cancel"), this);
    }
}