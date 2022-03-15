package de.symeda.sormas.backend.audit;

import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class AuditLoggerInterceptor {

	@Resource
	private SessionContext sessionContext;

	@AroundInvoke
	public Object logInvokeDuration(InvocationContext context) throws Exception {
		String target = context.getTarget().toString();
		if (!target.startsWith("de.symeda.sormas.backend.common.ConfigFacadeEjb")
			&& !target.startsWith("de.symeda.sormas.backend.user.CurrentUserService")) {
			Date start = Calendar.getInstance(TimeZone.getDefault()).getTime();
			List<String> parameters = Collections.emptyList();
			Object[] contextParameters = context.getParameters();
			if (contextParameters != null) {
				parameters = Arrays.stream(contextParameters).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
			}

			User currentUser;
			try {
				CurrentUserService currentUserService =

					(CurrentUserService) new InitialContext().lookup("java:global/sormas-ear/sormas-backend/CurrentUserService");
				currentUser = currentUserService.getCurrentUser();
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}

			String invokedMethod = getInvokedMethod(context);
			String agentUuid = currentUser == null ? "" : currentUser.getUuid();
			Object result = context.proceed();
			String returnValue = result == null ? "" : result.toString();

			AuditLogger.getInstance()
				.logBackendCall(sessionContext.getCallerPrincipal().getName(), agentUuid, invokedMethod, parameters, returnValue, start);
			return result;
		}
		return context.proceed();
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
