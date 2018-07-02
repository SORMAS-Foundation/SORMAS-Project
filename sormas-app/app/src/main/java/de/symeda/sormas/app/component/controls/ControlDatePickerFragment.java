package de.symeda.sormas.app.component.controls;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.app.R;

public class ControlDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    // Constants

    public static final String KEY_LINKED_DATE = "LinkedDate";

    // Attributes

    private Date date = null;

    // Listeners

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DialogInterface.OnClickListener onClearListener;

    // Overrides

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putSerializable(KEY_LINKED_DATE, date);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();

        if (arguments != null && arguments.containsKey(KEY_LINKED_DATE)) {
            date = (Date) arguments.get(KEY_LINKED_DATE);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerWithClear = new DatePickerDialog(getActivity(),
                R.style.Theme_Tebo_Dialog_DatePicker, this, year, month, dayOfMonth);

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
        if (onDateSetListener != null) {
            onDateSetListener.onDateSet(view, year, month, dayOfMonth);
        }
    }

    // Getters & setters

    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

}
