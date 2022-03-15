package de.symeda.sormas.backend.audit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class AuditLoggerInterceptor {

	@Resource
	private SessionContext sessionContext;

	@AroundInvoke
	public Object logInvokeDuration(InvocationContext context) throws Exception {
		if (!context.getTarget().toString().startsWith("de.symeda.sormas.backend.common.ConfigFacadeEjb")) {
			List<String> parameters=Collections.emptyList();
			Object[] contextParameters = context.getParameters();
			if (contextParameters != null){
				parameters = Arrays.stream(contextParameters).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
			}

			String invokedMethod = getInvokedMethod(context);
			AuditLogger.getInstance().logBackendCall(sessionContext.getCallerPrincipal().getName(),"SUBJECT-UUID", invokedMethod, parameters, "ReturnValue");
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
