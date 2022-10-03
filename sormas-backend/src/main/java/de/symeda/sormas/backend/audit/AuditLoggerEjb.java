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

import static de.symeda.sormas.api.audit.Constants.createPrefix;
import static de.symeda.sormas.api.audit.Constants.deletePrefix;
import static de.symeda.sormas.api.audit.Constants.executePrefix;
import static de.symeda.sormas.api.audit.Constants.readPrefix;
import static de.symeda.sormas.api.audit.Constants.updatePrefix;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;

import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.codesystems.AuditEntityType;
import org.hl7.fhir.r4.model.codesystems.AuditSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.symeda.sormas.api.audit.AuditLoggerFacade;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;

@Singleton(name = "AuditLoggerFacade")
public class AuditLoggerEjb implements AuditLoggerFacade {

	private static final Logger logger = LoggerFactory.getLogger(AuditLoggerEjb.class);
	public static final String VALUESET_AUDIT_EVENT_TYPE_HTML = "https://hl7.org/fhir/R4/valueset-audit-event-type.html";
	public static final String VALUESET_PARTICIPATION_ROLE_TYPE_HTML = "https://www.hl7.org/fhir/valueset-participation-role-type.html";
	public static final Coding USER_AUTHENTICATION_CODING = new Coding(VALUESET_AUDIT_EVENT_TYPE_HTML, "110114", "User Authentication");
	public static final Coding IMPORT_CODING = new Coding(VALUESET_AUDIT_EVENT_TYPE_HTML, "110107", "Import");
	public static final Coding EXPORT_CODING = new Coding(VALUESET_AUDIT_EVENT_TYPE_HTML, "110106", "Export");

	public static final Coding LOGIN_CODING = new Coding("https://hl7.org/fhir/R4/valueset-audit-event-sub-type.html", "110122", "Login");
	public static final Reference LAB_MESSAGE_CONVERT_TO_PDF = new Reference("convertToPDF");
	public static final Reference LAB_MESSAGE_CONVERT_TO_HTML = new Reference("convertToHTML");
	public static final Reference GET_EXTERNAL_LAB_MESSAGES = new Reference("getExternalLabMessages");

	private FhirContext fhirContext;
	private IParser fhirJsonParser;
	private String auditSourceSite;
//	private Reflections reflections;
	private Map<String, AuditEvent.AuditEventAction> actionBackendMap;
	private static final Map<String, AuditEvent.AuditEventAction> actionRestMap = new HashMap<String, AuditEvent.AuditEventAction>() {

		{
			put("PUT", AuditEvent.AuditEventAction.C);
			put("GET", AuditEvent.AuditEventAction.R);
			put("POST", AuditEvent.AuditEventAction.U);
			put("DELETE", AuditEvent.AuditEventAction.D);
		}
	};

	private Map<Class<?>, String> cachedClassName = new HashMap<>();

	private Map<Class<?>, List<Field>> cachedAnnotatedFields = new HashMap<>();
	private Map<Class<?>, List<Method>> cachedAnnotatedMethods = new HashMap<>();

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private LogSink auditLogger;
	@EJB
	private CurrentUserService currentUserService;

	// todo we need the session context in addition to the UserService as SYSTEM/ANONYMOUS do return null in the currentUserService
	@Resource
	private SessionContext sessionContext;

	private static boolean loggingDisabled = false;

	public static boolean isLoggingDisabled() {
		// using ConfigFacade in the interceptor is difficult,
		// therefore, we compute the value for the skip fast path here and forward it
		return loggingDisabled;
	}

	@PostConstruct
	private void setup() {
		String sourceSite = configFacade.getAuditSourceSite();
		if (sourceSite.equals("")) {
			logger.warn("audit.source.site is empty! Please configure it for more expedient audit trail analysis.");
			sourceSite = "NOT CONFIGURED";
		}
		this.auditSourceSite = sourceSite;

		if (configFacade.getAuditLoggerConfig().equals("")) {
			loggingDisabled = true;
		}

		actionBackendMap = new HashMap<>();

		fhirContext = FhirContext.forR4();
		fhirJsonParser = fhirContext.newJsonParser();
		// FIXME #10377: Checking if reflections causes blocking deployment
//		reflections = new Reflections("de.symeda.sormas.api", SubTypes, TypesAnnotated, FieldsAnnotated, MethodsAnnotated);
	}

	private void accept(AuditEvent event) {
		String serialized = fhirJsonParser.encodeResourceToString(event);
		auditLogger.getAuditLogger().info(serialized);
	}

	public void logApplicationStart() {
		Coding subtype = new Coding("https://hl7.org/fhir/R4/valueset-audit-event-sub-type.html", "110120", "Application Start");
		String outcomeDesc = "Application starting";
		logApplicationLifecycle(subtype, outcomeDesc);
	}

	public void logApplicationStop() {
		Coding subtype = new Coding("https://hl7.org/fhir/R4/valueset-audit-event-sub-type.html", "110121", "Application Stop");
		String outcomeDesc = "Application stopping";
		logApplicationLifecycle(subtype, outcomeDesc);
	}

	private void logApplicationLifecycle(Coding subtype, String outcomeDesc) {
		AuditEvent applicationStartAudit = new AuditEvent();
		applicationStartAudit.setType(new Coding(VALUESET_AUDIT_EVENT_TYPE_HTML, "110100", "Application Activity"));

		applicationStartAudit.setSubtype(Collections.singletonList(subtype));

		applicationStartAudit.setAction(AuditEvent.AuditEventAction.E);
		applicationStartAudit.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		// success
		applicationStartAudit.setOutcome(AuditEvent.AuditEventOutcome._0);
		applicationStartAudit.setOutcomeDesc(outcomeDesc);

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.addCoding(new Coding("https://www.hl7.org/fhir/valueset-participation-role-type.html", "110151", "Application Launcher"));
		agent.setType(codeableConcept);
		agent.setName("SYSTEM");
		applicationStartAudit.addAgent(agent);

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(auditSourceSite);
		// Application Server
		AuditSourceType auditSourceType = AuditSourceType._4;
		source.addType(new Coding(auditSourceType.getSystem(), auditSourceType.toCode(), auditSourceType.getDisplay()));
		applicationStartAudit.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference("StartupShutdownService"));

		// System Object
		AuditEntityType entityType = AuditEntityType._2;
		entity.setType(new Coding(entityType.getSystem(), entityType.toCode(), entityType.getDisplay()));
		applicationStartAudit.addEntity(entity);

		accept(applicationStartAudit);
	}

	public void logBackendCall(Method calledMethod, Object[] params, Object returnValue, Date start, Date end) {
		AuditEvent backendCall = new AuditEvent();

		backendCall.setAction(inferBackendAction(calledMethod.getName()));
		makePeriod(start, end, backendCall);

		backendCall.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());
		backendCall.setOutcomeDesc(printObject(returnValue));

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
		CodeableConcept codeableConcept = new CodeableConcept();

		AgentDetails agentDetails = new AgentDetails(currentUserService, sessionContext);

		if (agentDetails.name.equals("SYSTEM") || agentDetails.name.equals("ANONYMOUS")) {
			codeableConcept.addCoding(new Coding(VALUESET_PARTICIPATION_ROLE_TYPE_HTML, "110150", "Application"));
			agent.setType(codeableConcept);
		} else {
			codeableConcept.addCoding(new Coding(VALUESET_PARTICIPATION_ROLE_TYPE_HTML, "humanuser", "human user"));
			agent.setType(codeableConcept);
		}

		agent.setName(agentDetails.name);
		Reference who = new Reference();
		Identifier identifier = new Identifier();
		if (!agentDetails.name.equals("SYSTEM") && !agentDetails.name.equals("ANONYMOUS")) {
			identifier.setValue(agentDetails.uuid);
		}

		who.setIdentifier(identifier);
		agent.setWho(who);
		backendCall.addAgent(agent);

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(auditSourceSite);
		//Application Server process
		AuditSourceType auditSourceType = AuditSourceType._4;
		source.addType(new Coding(auditSourceType.getSystem(), auditSourceType.toCode(), auditSourceType.getDisplay()));
		backendCall.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference(calledMethod.toString()));

		List<AuditEvent.AuditEventEntityDetailComponent> details = new ArrayList<>();

		if (params != null) {
			Arrays.stream(params).map(this::printObject).forEach(p -> {
				AuditEvent.AuditEventEntityDetailComponent detail =
					new AuditEvent.AuditEventEntityDetailComponent(new StringType("param"), new StringType(p));
				details.add(detail);
			});
		}

		entity.setDetail(details);
		backendCall.addEntity(entity);

		accept(backendCall);
	}

	private void makePeriod(Date start, Date end, AuditEvent event) {
		Period period = new Period();
		period.setStart(start);
		period.setEnd(end);
		event.setPeriod(period);
	}

	private AuditEvent.AuditEventAction inferBackendAction(String calledMethod) {

		AuditEvent.AuditEventAction cached = actionBackendMap.get(calledMethod);
		if (cached != null) {
			return cached;
		}

		AuditEvent.AuditEventAction inferred;
		if (methodsStartsWith(calledMethod, createPrefix)) {
			inferred = AuditEvent.AuditEventAction.C;
		} else if (methodsStartsWith(calledMethod, readPrefix)) {
			inferred = AuditEvent.AuditEventAction.R;
		} else if (methodsStartsWith(calledMethod, updatePrefix)) {
			inferred = AuditEvent.AuditEventAction.U;
		} else if (methodsStartsWith(calledMethod, deletePrefix)) {
			inferred = AuditEvent.AuditEventAction.D;
		} else if (methodsStartsWith(calledMethod, executePrefix)) {
			inferred = AuditEvent.AuditEventAction.E;
		} else {
			inferred = AuditEvent.AuditEventAction.E;
		}
		actionBackendMap.put(calledMethod, inferred);
		return inferred;
	}

	private boolean methodsStartsWith(String methodName, Set<String> prefixes) {
		return prefixes.stream().anyMatch(methodName::startsWith);
	}

	@Override
	public void logRestCall(String path, String method) {
		AuditEvent restCall = new AuditEvent();

		restCall.setType(new Coding(VALUESET_AUDIT_EVENT_TYPE_HTML, "rest", "RESTful Operation"));
		restCall.setAction(inferRestAction(method));

		restCall.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		// agent
		AuditEvent.AuditEventAgentComponent agent = getAuditEventAgentComponent();
		restCall.addAgent(agent);

		// source
		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - SORMAS REST API", auditSourceSite));

		// Web Server
		AuditSourceType auditSourceType = AuditSourceType._3;
		source.addType(new Coding(auditSourceType.getSystem(), auditSourceType.toCode(), auditSourceType.getDisplay()));
		restCall.setSource(source);

		// entity
		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference(path));
		restCall.addEntity(entity);

		accept(restCall);
	}

	private AuditEvent.AuditEventAction inferRestAction(String actionMethod) {
		AuditEvent.AuditEventAction action = actionRestMap.get(actionMethod);
		if (action != null) {
			return action;
		} else {
			return AuditEvent.AuditEventAction.E;
		}
	}

	@Override
	public void logFailedRestLogin(String caller, String method, String pathInfo) {
		AuditEvent restLoginFail = new AuditEvent();

		restLoginFail.setType(USER_AUTHENTICATION_CODING);
		restLoginFail.addSubtype(LOGIN_CODING);
		restLoginFail.setAction(AuditEvent.AuditEventAction.E);

		restLoginFail.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		restLoginFail.setOutcome(AuditEvent.AuditEventOutcome._4);
		restLoginFail.setOutcomeDesc("Authentication failed");

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();

		agent.setName(caller);

		restLoginFail.addAgent(agent);

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - REST MultiAuthenticationMechanism", auditSourceSite));
		restLoginFail.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference(String.format("%s %s", method, pathInfo)));
		restLoginFail.addEntity(entity);

		accept(restLoginFail);
	}

	@Override
	public void logFailedUiLogin(String caller, String method, String pathInfo) {
		AuditEvent uiLoginFail = new AuditEvent();

		uiLoginFail.setType(USER_AUTHENTICATION_CODING);
		uiLoginFail.addSubtype(LOGIN_CODING);
		uiLoginFail.setAction(AuditEvent.AuditEventAction.E);

		uiLoginFail.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		uiLoginFail.setOutcome(AuditEvent.AuditEventOutcome._4);
		uiLoginFail.setOutcomeDesc("Authentication failed");

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
		agent.setName(caller);
		uiLoginFail.addAgent(agent);

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - UI MultiAuthenticationMechanism", auditSourceSite));
		uiLoginFail.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference(String.format("%s %s", method, pathInfo)));
		uiLoginFail.addEntity(entity);

		accept(uiLoginFail);
	}

	@Override
	public void logGetExternalMessagesSuccess(Date since, List<String> externalLabMessages, Date start, Date end, String authAlias) {
		String outcome = String.format("%d external lab messages since %s fetched", externalLabMessages.size(), since);

		List<AuditEvent.AuditEventEntityDetailComponent> details = new ArrayList<>();
		externalLabMessages.forEach(m -> {
			AuditEvent.AuditEventEntityDetailComponent detail =
				new AuditEvent.AuditEventEntityDetailComponent(new StringType("externalLabMessage"), new StringType(m));
			details.add(detail);
		});

		logLabMessageSuccess(IMPORT_CODING, GET_EXTERNAL_LAB_MESSAGES, outcome, details, start, end, authAlias);
	}

	@Override
	public void logExternalMessagesHtmlSuccess(String uuid, int length, Date start, Date end, String authAlias) {
		String outcome = "Successfully exported HTML report";
		List<AuditEvent.AuditEventEntityDetailComponent> details = new ArrayList<>();

		details.add(new AuditEvent.AuditEventEntityDetailComponent(new StringType("uuid"), new StringType(uuid)));
		details.add(new AuditEvent.AuditEventEntityDetailComponent(new StringType("length"), new StringType(String.valueOf(length))));

		logLabMessageSuccess(EXPORT_CODING, LAB_MESSAGE_CONVERT_TO_HTML, outcome, details, start, end, authAlias);
	}

	@Override
	public void logExternalMessagesPdfSuccess(String uuid, int length, Date start, Date end, String authAlias) {
		String outcome = "Successfully exported PDF report";
		List<AuditEvent.AuditEventEntityDetailComponent> details = new ArrayList<>();

		details.add(new AuditEvent.AuditEventEntityDetailComponent(new StringType("uuid"), new StringType(uuid)));
		details.add(new AuditEvent.AuditEventEntityDetailComponent(new StringType("length"), new StringType(String.valueOf(length))));

		logLabMessageSuccess(EXPORT_CODING, LAB_MESSAGE_CONVERT_TO_PDF, outcome, details, start, end, authAlias);
	}

	private void logLabMessageSuccess(
		Coding type,
		Reference what,
		String outcome,
		List<AuditEvent.AuditEventEntityDetailComponent> details,
		Date start,
		Date end,
		String authAlias) {
		AuditEvent logLabMessage = new AuditEvent();

		logLabMessage.setType(type);
		logLabMessage.setAction(AuditEvent.AuditEventAction.E);

		makePeriod(start, end, logLabMessage);
		logLabMessage.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		logLabMessage.setOutcome(AuditEvent.AuditEventOutcome._0);
		logLabMessage.setOutcomeDesc(outcome);

		logLabMessage.addAgent(getAuditEventAgentComponentWithAuthAlias(authAlias));

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - DEMIS", auditSourceSite));
		logLabMessage.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(what);

		entity.setDetail(details);
		logLabMessage.addEntity(entity);

		accept(logLabMessage);
	}

	@Override
	public void logGetExternalMessagesError(String outcome, String error, Date start, Date end, String authAlias) {
		logLabMessageError("", IMPORT_CODING, GET_EXTERNAL_LAB_MESSAGES, outcome, error, start, end, authAlias);
	}

	@Override
	public void logExternalMessagesHtmlError(String messageUuid, String outcome, String error, Date start, Date end, String authAlias) {
		logLabMessageError(messageUuid, EXPORT_CODING, LAB_MESSAGE_CONVERT_TO_HTML, outcome, error, start, end, authAlias);
	}

	@Override
	public void logExternalMessagesPdfError(String messageUuid, String outcome, String error, Date start, Date end, String authAlias) {
		logLabMessageError(messageUuid, EXPORT_CODING, LAB_MESSAGE_CONVERT_TO_PDF, outcome, error, start, end, authAlias);
	}

	private void logLabMessageError(
		String messageUuid,
		Coding type,
		Reference what,
		String outcome,
		String error,
		Date start,
		Date end,
		String authAlias) {
		AuditEvent logLabMessage = new AuditEvent();

		logLabMessage.setType(type);
		logLabMessage.setAction(AuditEvent.AuditEventAction.E);
		makePeriod(start, end, logLabMessage);
		logLabMessage.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		logLabMessage.setOutcome(AuditEvent.AuditEventOutcome._8);
		logLabMessage.setOutcomeDesc(outcome);

		logLabMessage.addAgent(getAuditEventAgentComponentWithAuthAlias(authAlias));

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - DEMIS", auditSourceSite));
		logLabMessage.setSource(source);

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(what);
		AuditEvent.AuditEventEntityDetailComponent uuid =
			new AuditEvent.AuditEventEntityDetailComponent(new StringType("messageUuid"), new StringType(messageUuid));
		entity.addDetail(uuid);
		AuditEvent.AuditEventEntityDetailComponent errorMessage =
			new AuditEvent.AuditEventEntityDetailComponent(new StringType("errorMessage"), new StringType(error));
		entity.addDetail(errorMessage);
		logLabMessage.addEntity(entity);

		accept(logLabMessage);

	}

	private AuditEvent.AuditEventAgentComponent getAuditEventAgentComponent() {
		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();

		AgentDetails agentDetails = new AgentDetails(currentUserService, sessionContext);

		agent.setName(agentDetails.name);
		Reference who = new Reference();
		Identifier identifier = new Identifier();
		identifier.setValue(agentDetails.uuid);
		who.setIdentifier(identifier);
		agent.setWho(who);
		return agent;
	}

	private AuditEvent.AuditEventAgentComponent getAuditEventAgentComponentWithAuthAlias(String authAlias) {
		AuditEvent.AuditEventAgentComponent agent = getAuditEventAgentComponent();
		agent.setAltId(authAlias);
		return agent;
	}

	private String printObject(Object object) {
		if (object == null) {
			return "null";
		}

		if (object instanceof Collection) {
			Collection<Object> collection = (Collection<Object>) object;

			if (!collection.isEmpty()) {
				String str = collection.stream().map(this::tryPrintFromAnnotation).collect(Collectors.joining(","));
				return String.format("[%s]", str);
			} else {
				return "[]";
			}
		}

		if (object instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) object;

			if (!map.isEmpty()) {
				String str = map.entrySet()
					.stream()
					.map(e -> String.format("%s=%s", tryPrintFromAnnotation(e.getKey()), tryPrintFromAnnotation(e.getValue())))
					.collect(Collectors.joining(","));
				return String.format("{%s}", str);
			} else {
				return "{}";
			}
		}

		return tryPrintFromAnnotation(object);

	}

	private String tryPrintFromAnnotation(Object object) {
		StringBuilder finalValue = new StringBuilder();

		String auditedClassName = getClassNameAnnotatedWithAuditedClass(object.getClass());
		if (auditedClassName != null) {
			finalValue.append(auditedClassName);
		}

		String fieldValue = null;

		List<Field> fields = getFieldsAnnotatedWithAuditInclude(object.getClass());
		if (!fields.isEmpty()) {
			fieldValue = printFromFieldAnnotations(object, fields);
		}

		String methodValue = null;

		List<Method> methods = getMethodsAnnotatedWithAuditInclude(object.getClass());
		if (!methods.isEmpty()) {
			methodValue = printFromMethodAnnotations(object, methods);
		}

		if (finalValue.length() == 0) {
			logger.debug("Audit logging for object of type {} is not implemented. Please file an issue.", object.getClass());
			return object.toString();
		}

		final boolean innerValue = fieldValue != null || methodValue != null;
		if (innerValue) {
			finalValue.append("(");
		}

		if (fieldValue != null) {
			finalValue.append(fieldValue);
		}

		if (fieldValue != null && methodValue != null) {
			finalValue.append(",");
		}

		if (methodValue != null) {
			finalValue.append(methodValue);
		}

		if (innerValue) {
			finalValue.append(")");
		}

		return finalValue.toString();
	}

	private String printFromFieldAnnotations(Object object, List<Field> fields) {

		if (fields.isEmpty()) {
			return null;
		}

		return fields.stream().map(f -> {
			try {
				String fieldName = f.getName();
				f.setAccessible(true);
				final Object fieldValue = f.get(object);
				f.setAccessible(false);
				String value = printObject(fieldValue);
				return String.format("%s=%s", fieldName, value);
			} catch (Exception e) {
				return "field access failed";
			}
		}).collect(Collectors.joining(","));
	}

	private String printFromMethodAnnotations(Object object, List<Method> methods) {

		if (methods.isEmpty()) {
			return null;
		}

		return methods.stream().map(m -> {
			try {
				String methodName = m.getName();
				final Object returnObject = m.invoke(object);
				String value = printObject(returnObject);
				return String.format("%s=%s", methodName, value);
			} catch (Exception e) {
				return "getter invocation failed";
			}
		}).collect(Collectors.joining(","));

	}

	private String getClassNameAnnotatedWithAuditedClass(final Class<?> type) {

		// FIXME #10377: Checking if reflections causes blocking deployment
		return null;
//		return cachedClassName.computeIfAbsent(type, k -> {
//			Optional<Class<?>> foundName =
//				reflections.get(SubTypes.of(TypesAnnotated.with(AuditedClass.class)).asClass()).stream().filter(c -> c.equals(type)).findFirst();
//			return foundName.map(Class::getSimpleName).orElse(null);
//		});
	}

	private List<Field> getFieldsAnnotatedWithAuditInclude(Class<?> type) {

		// FIXME #10377: Checking if reflections causes blocking deployment
		return new ArrayList<>();
//		return cachedAnnotatedFields.computeIfAbsent(type, k -> {
//			Set<Field> fieldsFound = reflections.get(Fields.of(type).filter(withAnnotation(AuditInclude.class)));
//			final ArrayList<Field> fields = new ArrayList<>(fieldsFound);
//			return fields.stream().sorted(Comparator.comparing(Field::getName)).collect(Collectors.toList());
//		});
	}

	private List<Method> getMethodsAnnotatedWithAuditInclude(final Class<?> type) {

		// FIXME #10377: Checking if reflections causes blocking deployment
		return new ArrayList<>();
//		return cachedAnnotatedMethods.computeIfAbsent(type, k -> {
//			Set<Method> methodsFound = reflections.get(Methods.of(type).filter(withAnnotation(AuditInclude.class)));
//			final ArrayList<Method> methods = new ArrayList<>(methodsFound);
//			return methods.stream().sorted(Comparator.comparing(Method::getName)).collect(Collectors.toList());
//		});
	}

	private class AgentDetails {

		final String uuid;
		final String name;

		public AgentDetails(CurrentUserService currentUserService, SessionContext sessionContext) {
			User currentUser = currentUserService.getCurrentUser();
			uuid = currentUser == null ? null : currentUser.getUuid();
			String tmpName = currentUser == null ? null : currentUser.getUserName();

			if (tmpName == null) {
				// in case the user is SYSTEM or ANONYMOUS, current user service will not help
				tmpName = sessionContext.getCallerPrincipal().getName();
			}
			name = tmpName;
		}
	}

	@LocalBean
	@Singleton
	public static class AuditLoggerEjbLocal extends AuditLoggerEjb {

	}
}
