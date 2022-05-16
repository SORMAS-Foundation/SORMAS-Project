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

package de.symeda.sormas.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceLoggingStopWatch {

	private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingStopWatch.class);

	final String methodName;
	final long startTime;

	public PerformanceLoggingStopWatch(String methodName) {
		this.methodName = methodName;
		startTime = DateHelper.startTime();
		if (logger.isTraceEnabled()) {
			logStart(methodName, null);
		}
	}

	public void stop() {
		if (logger.isTraceEnabled()) {
			logFinish(methodName, DateHelper.durationMillies(startTime));
		}
	}

	private static void logStart(String invokedMethod, Object[] parameters) {
		logger.trace("Started: {} with parameters '{}'", invokedMethod, parameters);
	}

	private static void logFinish(String invokedMethod, long milliseconds) {
		logger.debug("Finished in {} ms: {}", milliseconds, invokedMethod);
	}
}
