package de.symeda.sormas.app.util;

import com.google.android.gms.analytics.ExceptionParser;

/**
 * Created by Orson on 08/11/2017.
 */

public class UncaughtExceptionParser implements ExceptionParser {

    @Override
    public String getDescription(String s, Throwable throwable) {
        return ErrorReportingHelper.buildErrorReportDescription(throwable, null);
    }

}

