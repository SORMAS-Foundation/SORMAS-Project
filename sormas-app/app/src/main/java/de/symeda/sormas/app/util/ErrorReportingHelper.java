package de.symeda.sormas.app.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.symeda.sormas.app.SormasApplication;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;

/**
 * Created by Mate Strysewske on 27.04.2017.
 */
public class ErrorReportingHelper {

    /**
     * Sends an exception report to Google Analytics
     * @param tracker
     * @param className The name of the class where the exception was caught
     * @param e
     * @param entity The entity object (e.g. a case or contact) if this error is associated with one
     * @param fatal
     * @param additionalInformation Additional information about the exception, e.g. the case and user UUID; please
     *                              make sure that there is no sensitive data transferred!
     */
    public static void sendCaughtException(Tracker tracker, String className, Exception e, AbstractDomainObject entity, boolean fatal, String... additionalInformation) {
        // TODO add AbstractDomainObject and User parameters
        StringBuilder description = new StringBuilder();
        description.append(e.getClass().getSimpleName() + " in " + className + " - "
                + e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getMethodName() + ":"
                + e.getStackTrace()[0].getLineNumber() + " - Stack trace: ");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            if (i == e.getStackTrace().length - 1) {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber());
            } else {
                description.append(element.getClassName() + ":" + element.getMethodName() + ":" +
                        element.getLineNumber() + " -> ");
            }
        }
        if (entity != null) {
            description.append(" - " + entity.getClass().getSimpleName() + ": " + entity.getUuid());
        }
        description.append(" - User: " + ConfigProvider.getUser().getUuid());
        for (String s : additionalInformation) {
            description.append(s);
        }

        tracker.send(new HitBuilders.ExceptionBuilder()
                        .setDescription(description.toString())
                        .setFatal(fatal)
                        .build());
    }

}
