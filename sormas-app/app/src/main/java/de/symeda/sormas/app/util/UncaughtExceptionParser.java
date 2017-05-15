package de.symeda.sormas.app.util;

import android.content.Context;

import com.google.android.gms.analytics.ExceptionParser;
import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Mate Strysewske on 15.05.2017.
 */

public class UncaughtExceptionParser implements ExceptionParser {

    @Override
    public String getDescription(String s, Throwable throwable) {
        return ErrorReportingHelper.buildDescription(throwable.getClass().getSimpleName(), throwable.getStackTrace(), null);
    }

}
