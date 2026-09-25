package de.symeda.sormas.api.logging;

import javax.ejb.Remote;

/**
 * Writes frontend messages to the server log using the configured SLF4J logging levels.
 * Exceptions passed to this remote facade must be serializable and available on the server classpath.
 */
@Remote
public interface FrontendLoggerFacade {

	void trace(String message);

	void trace(String message, Throwable throwable);

	void debug(String message);

	void debug(String message, Throwable throwable);

	void info(String message);

	void info(String message, Throwable throwable);

	void warn(String message);

	void warn(String message, Throwable throwable);

	void error(String message);

	void error(String format, Object... arguments);

	void errorToJson(String message, Object... objects);

	void error(String message, Throwable throwable);
}
