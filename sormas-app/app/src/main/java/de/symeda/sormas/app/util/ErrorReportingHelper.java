/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.config.ConfigProvider;

public class ErrorReportingHelper {

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Sends an exception report to Google Analytics
     * @param entity The entity object (e.g. a case or contact) if this error is associated with one
     * @param additionalInformation Additional information about the exception, e.g. the case and user UUID; please
     *                              make sure that there is no sensitive data transferred!
     */
    public static void sendCaughtException(Tracker tracker, Exception e, AbstractDomainObject entity, boolean fatal, String... additionalInformation) {
        sendCaughtException(tracker, e, entity != null ? entity.getClass() : null, entity != null ? entity.getUuid() : null, fatal, additionalInformation);
    }

    /**
     * Sends an exception report to Google Analytics
     * @param additionalInformation Additional information about the exception, e.g. the case and user UUID; please
     *                              make sure that there is no sensitive data transferred!
     */
    public static void sendCaughtException(Tracker tracker, Exception e, Class entityClass, String entityUuid, boolean fatal, String... additionalInformation) {

        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(buildErrorReportDescription(e, entityClass, entityUuid, additionalInformation))
                .setFatal(fatal)
                .build());
    }

    /**
     * Builds a string to send to Google Analytics; doesn't use the detailMessage from the Throwable because it could contain personal information
     */
    public static String buildErrorReportDescription(Throwable t, AbstractDomainObject entity, String... additionalInformation) {
        return buildErrorReportDescription(t, entity != null ? entity.getClass() : null, entity != null ? entity.getUuid() : null, additionalInformation);
    }
        /**
         * Builds a string to send to Google Analytics; doesn't use the detailMessage from the Throwable because it could contain personal information
         */
    public static String buildErrorReportDescription(Throwable t, Class entityClass, String entityUuid, String... additionalInformation) {
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
        if (entityClass != null) {
            description.append(" - Entity: " + entityClass.getSimpleName() + ": " + entityUuid);
        }
        if (ConfigProvider.getUser() != null && ConfigProvider.getUser().getUuid() != null) {
            description.append(" - User: " + ConfigProvider.getUser().getUuid());
        }
        if (ConfigProvider.getServerRestUrl() != null) {
            description.append(" - System: " + ConfigProvider.getServerRestUrl());
        }
        for (String s : additionalInformation) {
            description.append(s);
        }

        return description.toString();
    }

}
