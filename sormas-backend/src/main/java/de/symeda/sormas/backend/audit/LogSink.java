package de.symeda.sormas.backend.audit;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.InvalidPathException;

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
                instance = new LogSink("/tmp/audit.xml");
            } catch (MalformedURLException | JoranException e) {
                logger.error("Could not create auditLogger: %s", e);
            }
        }
        return instance;
    }


    public Logger getAuditLogger() {
        return auditLogger;
    }
}

