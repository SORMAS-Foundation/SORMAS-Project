/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.audit;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import de.symeda.sormas.api.ConfigFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;

/**
 * Provides a configurable log sink for the SORMAS audit trail.
 */
public class LogSink {
    private static final Logger logger = LoggerFactory.getLogger(LogSink.class);
    private static LogSink instance;

    private Logger auditLogger;

    private LogSink(String fileName) {
        if (fileName == null || fileName.equals("")) {
            setupNopLogger();
            return;
        }

        File file = new File(fileName);
        JoranConfigurator configurator = new JoranConfigurator();
        LoggerContext context = new LoggerContext();
        configurator.setContext(context);

        try {
            configurator.doConfigure(file);
        } catch (JoranException e) {
            logger.error("Could not setup audit logger: ", e);
            setupNopLogger();
            return;
        }
        auditLogger = context.getLogger("Audit");
    }

    /**
     * Issue a warning and make audit logging a NOP
     */
    private void setupNopLogger() {
        logger.warn("Audit logger is disabled! Using NOP Logger instead!");
        auditLogger = NOPLogger.NOP_LOGGER;
    }

    public static synchronized LogSink getInstance() {
        String configPath = null;

        if (instance == null) {
            // initialize
            try {
                ConfigFacade configFacade = (ConfigFacade) new InitialContext().lookup("java:module/ConfigFacade");
                // happy path
                configPath = configFacade.getAuditLoggerConfig();
            } catch (NamingException e) {
                // constructor in finally block will set up NOPLogger
                logger.error("Could not lookup ConfigFacade");
            } finally {
                instance = new LogSink(configPath);
            }
        }

        return instance;
    }

    public Logger getAuditLogger() {
        return auditLogger;
    }
}

