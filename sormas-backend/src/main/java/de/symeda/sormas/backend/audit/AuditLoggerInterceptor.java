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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.backend.auditlog.AuditContextProducer;
import de.symeda.sormas.backend.auditlog.AuditLogServiceBean;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.i18n.I18nFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.UserFacadeEjb;

public class AuditLoggerInterceptor {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@EJB
	AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;

	/**
	 * Cache to track all classes that should be ignored and those who must be audited. False indicates audit, True ignore
	 */
	private static final Map<Class<?>, Boolean> shouldIgnoreClassCache = new HashMap<>();

	/**
	 * Cache to track all classes which should be completely ignored for audit.
	 */
	private static final Set<Class<?>> ignoreAuditClasses = Collections.unmodifiableSet(
		new HashSet<>(
			Arrays.asList(
				FeatureConfigurationFacadeEjb.class,
				ConfigFacadeEjb.class,
				CurrentUserService.class,
				AuditContextProducer.class,
				AuditLogServiceBean.class,
				AuditLoggerEjb.class,
				I18nFacadeEjb.class)));

	/**
	 * Cache to track all methods which should be completely ignored.
	 */
	private static final Set<Method> ignoreAuditMethods;

	static {
		try {
			ignoreAuditMethods = Collections.unmodifiableSet(
				new HashSet<>(
					Arrays.asList(
						ContinentFacadeEjb.class.getMethod("getByDefaultName", String.class, boolean.class),
						SubcontinentFacadeEjb.class.getMethod("getByDefaultName", String.class, boolean.class),
						UserFacadeEjb.class.getMethod("getCurrentUser"),
						UserFacadeEjb.class.getMethod("getValidLoginRights", String.class, String.class))));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@AroundTimeout
	public Object logTimeout(InvocationContext context) throws Exception {
		if (AuditLoggerEjb.isLoggingDisabled()) {
			return context.proceed();
		}
		return backendAuditing(context, context.getMethod());
	}

	@AroundInvoke
	public Object logAudit(InvocationContext context) throws Exception {

		if (AuditLoggerEjb.isLoggingDisabled()) {
			return context.proceed();
		}

		Class<?> target = context.getTarget().getClass();

		if (ignoreAuditClasses.contains(target)) {
			// ignore certain classes for audit altogether. Statically populated cache.
			return context.proceed();
		}

		// with this we ignore EJB calls which definitely originate from within the backend
		// as they can never be called direct from outside (i.e., remote) of the backend
		// expression yields true if it IS a local bean => should not be audited and ignored
		Boolean shouldIgnoreAudit = shouldIgnoreClassCache.computeIfAbsent(target, k -> target.getAnnotationsByType(LocalBean.class).length > 0);

		if (shouldIgnoreAudit) {
			// ignore local beans
			return context.proceed();
		}

		Method calledMethod = context.getMethod();

		if (ignoreAuditMethods.contains(calledMethod)) {
			// ignore certain methods for audit altogether. Statically populated cache.
			return context.proceed();
		}

		return backendAuditing(context, calledMethod);
	}

	private Object backendAuditing(InvocationContext context, Method calledMethod) throws Exception {

		Object[] parameters = context.getParameters();
		Date start = Calendar.getInstance(TimeZone.getDefault()).getTime();

		Object result = context.proceed();

		Date end = Calendar.getInstance(TimeZone.getDefault()).getTime();

		auditLogger.logBackendCall(calledMethod, parameters, result, start, end);
		return result;
	}

}
