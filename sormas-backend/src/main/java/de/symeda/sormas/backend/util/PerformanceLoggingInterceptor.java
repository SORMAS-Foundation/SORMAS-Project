package de.symeda.sormas.backend.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.utils.DateHelper;

/**
 * <p>
 * Purpose: Log duration of method invocation, usually to be to detect performance leaks in the EJB call stack.<br />
 * You can activate logging with DEBUG level to get all durations over {@value #SIGNIFANT_DURATION} ms.
 * To see all EJB method starts with parameters and all durations, set log level to TRACE.
 * </p>
 * <p>
 * <b>Warning:</b> Activating this logging has some performance impact itself, so only active it temporarly.
 * </p>
 * 
 * @see https://www.adam-bien.com/roller/abien/entry/simplest_possible_ejb_3_12
 */
public class PerformanceLoggingInterceptor {

	/**
	 * Avoids fast but noisy and mostly internal EJB calls to be logged on DEBUG level.
	 */
	private static final int SIGNIFANT_DURATION = 5;

	private final Logger logger = LoggerFactory.getLogger(PerformanceLoggingInterceptor.class);

	@AroundInvoke
	public Object logInvokeDuration(InvocationContext context) throws Exception {

		if (!logger.isDebugEnabled()) {
			// Directly proceed if logging is not activated
			return context.proceed();
		}

		boolean traceEnabled = logger.isTraceEnabled();
		if (traceEnabled) {
			logger.trace("Started: {} with parameters '{}'", getInvokedMethod(context), context.getParameters());
		}

		long startTime = DateHelper.startTime();
		try {
			return context.proceed();
		} finally {
			long ms = DateHelper.durationMillies(startTime);
			if (traceEnabled || ms > SIGNIFANT_DURATION) {
				logger.debug("Finished in {} ms: {}", ms, getInvokedMethod(context));
			}
		}
	}

	/**
	 * @return Better readable method name (with EJB class) that was invoked.
	 */
	static String getInvokedMethod(InvocationContext context) {

		return getInvokedMethod(context.getTarget().toString(), context.getMethod().getName());
	}

	/**
	 * @return Better readable method name (with EJB class) that was invoked.
	 */
	static String getInvokedMethod(String targetName, String methodName) {

		int classBegin = targetName.lastIndexOf(".") + 1;
		int innerClassBegin = targetName.indexOf("$");
		int instanceBegin = targetName.indexOf("@");

		String ejbName = targetName.substring(classBegin, innerClassBegin > 0 ? innerClassBegin : instanceBegin);
		return String.format("%s.%s", ejbName, methodName);
	}
}
