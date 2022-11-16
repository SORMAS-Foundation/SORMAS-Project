package de.symeda.sormas.backend.audit;

import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

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

import org.apache.commons.collections4.SetUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;

public class AuditLoggerInterceptor {

	@EJB
	AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;

	private static final Reflections reflections;

	private static final String DELETE_PERMANENT = "deletePermanent";

	private static final String DELETE_PERMANENT_BY_UUIDS = "deletePermanentByUuids";

	private static final Set<Class<?>> adoServiceClasses;

	/**
	 * Cache to track all remote beans that should not be audited. True indicates ignore, false audit.
	 */
	private static final Map<Class<?>, Boolean> shouldIgnoreBeanCache;

	/**
	 * Cache to track all classes which should be completely ignored for audit.
	 */
	private static final Set<Class<?>> ignoreAuditClasses;

	/**
	 * Cache to track all methods which should be completely ignored.
	 */
	private static final Set<Method> ignoreAuditMethods;

	/**
	 * Cache to track all local methods which should be audited.
	 */
	private static final Set<Method> allowedLocalAuditMethods;

	static {

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.forPackages("de.symeda.sormas.backend", "de.symeda.sormas.api")
			.addScanners(SubTypes, TypesAnnotated, MethodsAnnotated)
			.setParallel(false); // <-- DO NOT USE PARALLEL SCANNING: Blocks deployment in some cases

		reflections = new Reflections(configurationBuilder);

		adoServiceClasses = new HashSet<>(reflections.get(SubTypes.of(BaseAdoService.class).asClass()));

		// Get all classes annotated with @AuditIgnore.
		// These classes should be either ignored completely (if they do not retain write methods) or
		// all methods EXCEPT FOR WRITING methods should be ignored.
		final HashSet<Class<?>> auditIgnoreAnnotatedClasses =
			new HashSet<>(reflections.get(SubTypes.of(TypesAnnotated.with(AuditIgnore.class)).asClass()));

		// In case we do not retain writes, we can simply ignore the whole annotated class
		ignoreAuditClasses = Collections.unmodifiableSet(auditIgnoreAnnotatedClasses.stream().filter(c -> {
			// Java is sometimes ridiculously complicated: A subclass does not know the annotations of its superclass
			Class<?> clazz = c;
			while (!clazz.isAssignableFrom(Object.class)) {

				if (clazz.isAnnotationPresent(AuditIgnore.class) && clazz.getAnnotation(AuditIgnore.class).retainWrites()) {
					// we found a class which is annotated with @AuditIgnore and retains writes, so we cannot ignore the whole class
					return false;
				}
				clazz = clazz.getSuperclass();
			}
			// we found no class which is annotated with @AuditIgnore and does not retains writes, so we can ignore the whole class
			return true;

		}).collect(Collectors.toSet()));

		// In case we retain writes, we need to extract all methods in the class for which inferBackendAction DOES NOT return update (i.e., write).
		Set<Method> ignoreAuditMethodsNotRetainedByClass = SetUtils.disjunction(auditIgnoreAnnotatedClasses, ignoreAuditClasses)
			.stream()
			.map(Class::getMethods)
			.flatMap(Arrays::stream)
			.filter(m -> AuditLoggerEjb.doInferBackendAction(m.getName()) != AuditEvent.AuditEventAction.U)
			.collect(Collectors.toSet());

		// Get all ignored, directly annotated methods
		HashSet<Method> ignoreAuditMethodsAnnotatedDirectly = new HashSet<>(reflections.getMethodsAnnotatedWith(AuditIgnore.class));

		Set<Method> ignoreAuditMethodsTmp = new HashSet<>();

		ignoreAuditMethodsTmp.addAll(ignoreAuditMethodsNotRetainedByClass);
		ignoreAuditMethodsTmp.addAll(ignoreAuditMethodsAnnotatedDirectly);

		ignoreAuditMethods = Collections.unmodifiableSet(ignoreAuditMethodsTmp);

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

		shouldIgnoreBeanCache = new HashMap<>();
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

		Method calledMethod = context.getMethod();

		if (ignoreAuditMethods.contains(calledMethod)) {
			// ignore certain methods for audit altogether. Statically populated cache.
			return context.proceed();
		}

		// with this we ignore EJB calls which definitely originate from within the backend
		// as they can never be called direct from outside (i.e., remote) of the backend.
		// The expression yields true if it IS a local bean and should be ignored
		// (exceptions to this rule, e.g., BaseService::deletePermanent, are handled below)
		Boolean ignoreBeanAudit = shouldIgnoreBeanCache.computeIfAbsent(target, k -> target.getAnnotationsByType(LocalBean.class).length > 0);

		if (Boolean.TRUE.equals(ignoreBeanAudit) && !allowedLocalAuditMethods.contains(calledMethod)) {
			// we have a local bean call which should not be audited -> ignore
			return context.proceed();
		}

		// we have a relevant method of a remote bean or a local bean that is allowed
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
