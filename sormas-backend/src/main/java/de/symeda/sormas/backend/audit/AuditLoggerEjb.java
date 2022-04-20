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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

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
	private String auditSourceSite;
	private Map<String, AuditEvent.AuditEventAction> actionBackendMap;
	private static final Map<String, AuditEvent.AuditEventAction> actionRestMap = new HashMap<String, AuditEvent.AuditEventAction>() {

		{
			put("PUT", AuditEvent.AuditEventAction.C);
			put("GET", AuditEvent.AuditEventAction.R);
			put("POST", AuditEvent.AuditEventAction.U);
			put("DELETE", AuditEvent.AuditEventAction.D);
		}
	};

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
		// therefore we compute the value for the skip fast path here and forward it
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
	}

	private void accept(AuditEvent event) {
		FhirContext ctx = FhirContext.forR4();
		IParser parser = ctx.newJsonParser();
		String serialized = parser.encodeResourceToString(event);
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
		applicationStartAudit.setType(new Coding("https://hl7.org/fhir/R4/valueset-audit-event-type.html", "110100", "Application Activity"));

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

	public void logBackendCall(Method calledMethod, List<String> params, String returnValue, Date start) {
		AuditEvent backendCall = new AuditEvent();

		backendCall.setAction(inferBackendAction(calledMethod.getName()));
		Period period = new Period();
		period.setStart(start);
		Date end = Calendar.getInstance(TimeZone.getDefault()).getTime();
		period.setEnd(end);
		backendCall.setPeriod(period);

		backendCall.setRecorded(end);

		backendCall.setOutcomeDesc(returnValue);

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
		CodeableConcept codeableConcept = new CodeableConcept();

		AgentDetails agentDetails = new AgentDetails(currentUserService, sessionContext);

		if (agentDetails.name.equals("SYSTEM") || agentDetails.name.equals("ANONYMOUS")) {
			codeableConcept.addCoding(new Coding("https://www.hl7.org/fhir/valueset-participation-role-type.html", "110150", "Application"));
			agent.setType(codeableConcept);
		} else {
			codeableConcept.addCoding(new Coding("https://www.hl7.org/fhir/valueset-participation-role-type.html", "humanuser", "human user"));
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
		params.forEach(p -> {
			AuditEvent.AuditEventEntityDetailComponent detail =
				new AuditEvent.AuditEventEntityDetailComponent(new StringType("param"), new StringType(p));
			details.add(detail);
		});

		entity.setDetail(details);
		backendCall.addEntity(entity);

		accept(backendCall);
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

		restCall.setType(new Coding("https://hl7.org/fhir/R4/valueset-audit-event-type.html", "rest", "RESTful Operation"));
		restCall.setAction(inferRestAction(method));

		restCall.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		// agent
		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();

		AgentDetails agentDetails = new AgentDetails(currentUserService, sessionContext);

		agent.setName(agentDetails.name);
		Reference who = new Reference();
		Identifier identifier = new Identifier();
		identifier.setValue(agentDetails.uuid);
		who.setIdentifier(identifier);
		agent.setWho(who);
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

		restLoginFail.setType(new Coding("https://hl7.org/fhir/R4/valueset-audit-event-type.html", "110114", "User Authentication"));
		restLoginFail
			.setSubtype(Collections.singletonList(new Coding("https://hl7.org/fhir/R4/valueset-audit-event-sub-type.html", "110122", "Login")));
		restLoginFail.setAction(AuditEvent.AuditEventAction.E);

		restLoginFail.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());

		restLoginFail.setOutcome(AuditEvent.AuditEventOutcome._4);
		restLoginFail.setOutcomeDesc("Authentication failed");

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();

		agent.setName(caller);

		restLoginFail.addAgent(agent);

		AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
		source.setSite(String.format("%s - REST MultiAuthenticationMechanism", auditSourceSite));

		AuditEvent.AuditEventEntityComponent entity = new AuditEvent.AuditEventEntityComponent();
		entity.setWhat(new Reference(String.format("%s %s", method, pathInfo)));
		restLoginFail.addEntity(entity);

		accept(restLoginFail);
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
	@Stateless
	public static class AuditLoggerEjbLocal extends AuditLoggerEjb {

	}
}
