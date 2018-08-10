package de.symeda.sormas.app.core.adapter;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

public class DateBindingAdapters {

    @BindingAdapter("android:text")
    public static void setText(TextView textView, Date dataValue) {
        if (dataValue != null) {
            textView.setText(DateHelper.formatLocalShortDate(dataValue));
        }
    }

}
