package de.symeda.sormas.backend.audit;

import static org.reflections.scanners.Scanners.SubTypes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import de.symeda.sormas.backend.auditlog.AuditContextProducer;
import de.symeda.sormas.backend.auditlog.AuditLogServiceBean;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.i18n.I18nFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.UserFacadeEjb;

public class AuditLoggerInterceptor {

	@EJB
	AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;

	private static final Reflections reflections;
	static {

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.forPackages("de.symeda.sormas.backend").addScanners(SubTypes).setParallel(false);
		reflections = new Reflections(configurationBuilder);
	}

	private static final Set<Class<?>> adoServiceClasses = new HashSet<>(reflections.get(SubTypes.of(BaseAdoService.class).asClass()));

	/**
	 * Cache to track all classes that should be ignored and those who must be audited. False indicates audit, True ignore
	 */
	private static final Map<Class<?>, Boolean> shouldIgnoreClassCache;
	static {
		Map<Class<?>, Boolean> map = new HashMap<>();
		// explicitly add all services doing deletion here to bypass the local bean check
		adoServiceClasses.forEach(clazz -> map.put(clazz, false));
		shouldIgnoreClassCache = map;
	}

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

	/**
	 * Cache to track all local methods which should be audited.
	 */
	private static final Set<Method> allowedLocalAuditMethods;

	private static final String DELETE_PERMANENT = "deletePermanent";

	private static final String DELETE_PERMANENT_BY_UUIDS = "deletePermanentByUuids";

	static {
		// explicitly add all local methods which should be explicitly audited. Please note that this is a set of
		// methods, therefore, its cardinality may be smaller than the size of the deletableAdoServiceClasses list as 
		// some service classes just use the super class implementation of the deletePermanent method.
		Set<Method> deletePermanentMethods = adoServiceClasses.stream().map(clazz -> {
			try {
				return clazz.getMethod(DELETE_PERMANENT, AbstractDomainObject.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());

		Set<Method> deletePermanentByUuids = adoServiceClasses.stream().map(clazz -> {
			try {
				return clazz.getMethod(DELETE_PERMANENT_BY_UUIDS, List.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());

		deletePermanentMethods.addAll(deletePermanentByUuids);
		allowedLocalAuditMethods = Collections.unmodifiableSet(deletePermanentMethods);
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

		if (Boolean.TRUE.equals(shouldIgnoreAudit)) {
			// ignore local beans
			return context.proceed();
		}

		Method calledMethod = context.getMethod();

		if (ignoreAuditMethods.contains(calledMethod) || !allowedLocalAuditMethods.contains(calledMethod)) {
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
