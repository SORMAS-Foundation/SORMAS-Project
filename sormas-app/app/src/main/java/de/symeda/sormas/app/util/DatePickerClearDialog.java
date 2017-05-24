package de.symeda.sormas.app.util;

import android.app.DatePickerDialog;
import android.content.Context;

import de.symeda.sormas.app.R;

/**
 * Created by Stefan Szczesny on 01.08.2016.
 */
public class DatePickerClearDialog extends DatePickerDialog {

    public DatePickerClearDialog(Context context, OnDateSetListener callBack,
                                 int year, int monthOfYear, int dayOfMonth) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);

        setButton(BUTTON_POSITIVE, (context.getString(R.string.action_ok)), this);
        setButton(BUTTON_NEUTRAL, (context.getString(R.string.action_clear)), this); // ADD THIS
        setButton(BUTTON_NEGATIVE, (context.getString(R.string.action_cancel)), this);
    }
}