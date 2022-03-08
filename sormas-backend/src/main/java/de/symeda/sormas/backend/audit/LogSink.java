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
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;

/**
 * Provides a configurable log sink for the SORMAS audit trail.
 */
public class LogSink {
    private static final Logger logger = LoggerFactory.getLogger(LogSink.class);
    private static LogSink instance;

    private Logger auditLogger;

    private LogSink(String fileName) throws MalformedURLException, JoranException {


        if (fileName.endsWith("xml")) {
            File file = new File(fileName);
            JoranConfigurator configurator = new JoranConfigurator();
            LoggerContext context = new LoggerContext();
            configurator.setContext(context);
            configurator.doConfigure(file);
            auditLogger = context.getLogger("Audit");
        } else {
            throw new InvalidPathException(fileName, "Please provide an XML file!");
        }

    }

    public static synchronized LogSink getInstance() {
        if (instance == null) {
            try {
                ConfigFacade configFacade = (ConfigFacade) new InitialContext().lookup("java:module/ConfigFacade");
                instance = new LogSink(configFacade.getAuditLoggerConfig());
            } catch (MalformedURLException | JoranException | NamingException e) {
                logger.error("Could not create auditLogger: %s", e);

            }

        }
        return instance;
    }


    public Logger getAuditLogger() {
        return auditLogger;
    }
}

