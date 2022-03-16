package de.symeda.sormas.backend.audit;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.backend.auditlog.AuditContextProducer;
import de.symeda.sormas.backend.auditlog.AuditLogServiceBean;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.i18n.I18nFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AuditLoggerInterceptor {

	@Resource
	private SessionContext sessionContext;

	private final static Set<Class<?>> ignoreAuditClasses = new HashSet<>(
		Arrays.asList(ConfigFacadeEjb.class, CurrentUserService.class, AuditContextProducer.class, AuditLogServiceBean.class, I18nFacadeEjb.class));

	private static Set<Method> ignoreAuditMethods;

	static {
		try {
			ignoreAuditMethods = new HashSet<>(
				Arrays.asList(
					ContinentFacadeEjb.class.getMethod("getByDefaultName", String.class, boolean.class),
					SubcontinentFacadeEjb.class.getMethod("getByDefaultName", String.class, boolean.class)));
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@AroundInvoke
	public Object logAudit(InvocationContext context) throws Exception {
		Object target = context.getTarget();
		if (target.getClass().getAnnotationsByType(LocalBean.class).length > 0) {
			// with this we ignore EJB calls which definitely originate from within the backend
			// as they can never be called direct from outside (i.e., remote) of he backend
			return context.proceed();
		}

		if (ignoreAuditClasses.contains(target.getClass())) {
			// ignore certain classes for audit altogether
			return context.proceed();
		}

		Method calledMethod = context.getMethod();

		if (ignoreAuditMethods.contains(calledMethod)) {
			return context.proceed();
		}

		// start auditing

		// AuditContextProducer
		Date start = Calendar.getInstance(TimeZone.getDefault()).getTime();
		List<String> parameters = getParameters(context);

		User currentUser;
		try {
			CurrentUserService currentUserService =
				(CurrentUserService) new InitialContext().lookup("java:global/sormas-ear/sormas-backend/CurrentUserService");
			currentUser = currentUserService.getCurrentUser();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

		String agentUuid = currentUser == null ? null : currentUser.getUuid();

		// do the actual call
		Object result = context.proceed();

		String returnValue = printObject(result);

		AuditLogger.getInstance()
			.logBackendCall(sessionContext.getCallerPrincipal().getName(), agentUuid, calledMethod.toString(), parameters, returnValue, start);

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
				String str = uuidList.stream().map(HasUuid::getUuid).collect(Collectors.joining());
				return String.format("List(%s)", str);
			}
		}
		return o.toString();
	}
}
