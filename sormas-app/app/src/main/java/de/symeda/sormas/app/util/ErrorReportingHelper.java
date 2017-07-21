package de.symeda.sormas.app.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 27.04.2017.
 */
public class ErrorReportingHelper {

    public static String getStackTrace(Throwable throwable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Sends an exception report to Google Analytics
     * @param tracker
     * @param e
     * @param entity The entity object (e.g. a case or contact) if this error is associated with one
     * @param fatal
     * @param additionalInformation Additional information about the exception, e.g. the case and user UUID; please
     *                              make sure that there is no sensitive data transferred!
     */
    public static void sendCaughtException(Tracker tracker, Exception e, AbstractDomainObject entity, boolean fatal, String... additionalInformation) {

        tracker.send(new HitBuilders.ExceptionBuilder()
                        .setDescription(buildErrorReportDescription(e, entity, additionalInformation))
                        .setFatal(fatal)
                        .build());
    }

    /**
     * Builds a string to send to Google Analytics; doesn't use the detailMessage from the Throwable because it could contain personal information
     *
     * @param t
     * @param entity
     * @param additionalInformation
     * @return
     */
    public static String buildErrorReportDescription(Throwable t, AbstractDomainObject entity, String... additionalInformation) {
        StackTraceElement[] stackTrace = t.getStackTrace();
        Throwable rootCause = getRootCause(t);
        StackTraceElement[] rootCauseStackTrace = rootCause.getStackTrace();

        StringBuilder description = new StringBuilder();
        description.append(t.getClass().getSimpleName() + " - Root cause: ");
        description.append(rootCause + " - Stack trace: ");
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if (i == stackTrace.length - 1) {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber());
            } else {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber() + " -> ");
            }
        }
        description.append(" - Root cause stack trace: ");
        for (int i = 0; i < rootCauseStackTrace.length; i++) {
            StackTraceElement element = rootCauseStackTrace[i];
            if (i == rootCauseStackTrace.length - 1) {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber());
            } else {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber() + " -> ");
            }
        }
        if (entity != null) {
            description.append(" - Entity: " + entity.getClass().getSimpleName() + ": " + entity.getUuid());
        }
        if (ConfigProvider.getUser() != null && ConfigProvider.getUser().getUuid() != null) {
            description.append(" - User: " + ConfigProvider.getUser().getUuid());
        }
        for (String s : additionalInformation) {
            description.append(s);
        }

        return description.toString();
    }

}
