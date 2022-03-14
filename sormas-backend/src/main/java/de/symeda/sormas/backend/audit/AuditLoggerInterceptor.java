package de.symeda.sormas.backend.audit;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.Collections;
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 1)
public class AuditLoggerInterceptor {

	private final AuditLogger auditLogger = AuditLogger.getInstance();

	@AroundInvoke
	public Object logInvokeDuration(InvocationContext context) throws Exception {
		auditLogger.logBackendCall("Subject", "Object", getInvokedMethod(context), Collections.emptyList(), "ReturnValue");
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
