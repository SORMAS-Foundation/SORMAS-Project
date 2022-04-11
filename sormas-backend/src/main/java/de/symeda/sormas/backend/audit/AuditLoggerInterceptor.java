package de.symeda.sormas.backend.audit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.backend.auditlog.AuditContextProducer;
import de.symeda.sormas.backend.auditlog.AuditLogServiceBean;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.i18n.I18nFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;

public class AuditLoggerInterceptor {

	@EJB
	AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;
	@EJB
	CurrentUserService currentUserService;

	// todo we need the session context in addition to the UserService as SYSTEM/ANONYMOUS do return null in the currentUserService
	@Resource
	private SessionContext sessionContext;

	/**
	 * Cache to track all classes that should be ignored and those who must be audited. False indicates audit, True ignore
	 */
	private final static Map<Class<?>, Boolean> shouldIgnoreClassCache = new HashMap<>();

	/**
	 * Cache to track all classes which should be completely ignored for audit.
	 */
	private final static Set<Class<?>> ignoreAuditClasses = Collections.unmodifiableSet(
		new HashSet<>(
			Arrays.asList(
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
						UserFacadeEjb.class.getMethod("getCurrentUser"))));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
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

		// start auditing

		// AuditContextProducer
		Date start = Calendar.getInstance(TimeZone.getDefault()).getTime();
		List<String> parameters = getParameters(context);

		User currentUser = currentUserService.getCurrentUser();
		String agentUuid = currentUser == null ? null : currentUser.getUuid();

		// do the actual call
		Object result = context.proceed();

		String returnValue = printObject(result);

		auditLogger.logBackendCall(sessionContext.getCallerPrincipal().getName(), agentUuid, calledMethod, parameters, returnValue, start);

		return result;
	}

	private List<String> getParameters(InvocationContext context) {
		Object[] contextParameters = context.getParameters();

		if (contextParameters != null) {
			return Arrays.stream(contextParameters).filter(Objects::nonNull).map(this::printObject).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	private String printObject(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof HasUuid) {
			return ((HasUuid) o).getUuid();
		}
		if (o instanceof List) {
			List list = (List) o;
			if (!list.isEmpty() && list.get(0) instanceof HasUuid) {
				List<HasUuid> uuidList = list;
				String str = uuidList.stream().map(HasUuid::getUuid).collect(Collectors.joining(","));
				return String.format("List(%s)", str);
			}
		}
		return o.toString();
	}
}
