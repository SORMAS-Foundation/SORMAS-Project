package de.symeda.sormas.backend.logging;

import java.util.Arrays;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.logging.FrontendLoggerFacade;
import de.symeda.sormas.backend.json.ObjectMapperProvider;

@AuditIgnore
@Stateless(name = "FrontendLoggerFacade")
public class FrontendLoggerEjb implements FrontendLoggerFacade {

	private static final Logger logger = LoggerFactory.getLogger(FrontendLoggerEjb.class);
	private static final String LOG_PREFIX = "FRONTEND-LOG:  ";

	@Override
	public void trace(String message) {
		logger.trace(LOG_PREFIX + message);
	}

	@Override
	public void trace(String message, Throwable throwable) {
		logger.trace(LOG_PREFIX + message, throwable);
	}

	@Override
	public void debug(String message) {
		logger.debug(LOG_PREFIX + message);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.debug(LOG_PREFIX + message, throwable);
	}

	@Override
	public void info(String message) {
		logger.info(LOG_PREFIX + message);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.info(LOG_PREFIX + message, throwable);
	}

	@Override
	public void warn(String message) {
		logger.warn(LOG_PREFIX + message);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.warn(LOG_PREFIX + message, throwable);
	}

	@Override
	public void error(String message) {
		logger.error(LOG_PREFIX + message);
	}

	@Override
	public void error(String format, Object... arguments) {

	}

	@Override
	public void errorToJson(String message, Object... objects) {
		if (logger.isErrorEnabled()) {
			logger.error(LOG_PREFIX + message, Arrays.stream(objects).map(ObjectMapperProvider::writeValueAsStringFailSafe).toArray());
		}
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.error(LOG_PREFIX + message, throwable);
	}
}
