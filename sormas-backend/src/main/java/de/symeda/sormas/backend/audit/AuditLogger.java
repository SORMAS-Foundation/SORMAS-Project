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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.symeda.sormas.api.ConfigFacade;
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

import javax.naming.InitialContext;
import javax.naming.NamingException;
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

import static de.symeda.sormas.api.audit.Constants.createPrefix;
import static de.symeda.sormas.api.audit.Constants.deletePrefix;
import static de.symeda.sormas.api.audit.Constants.executePrefix;
import static de.symeda.sormas.api.audit.Constants.readPrefix;
import static de.symeda.sormas.api.audit.Constants.updatePrefix;

public class AuditLogger {

	private static AuditLogger instance;
	private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);
	private final Logger auditLogger;
	private final String auditSourceSite;
	private final Map<String, AuditEvent.AuditEventAction> actionMap;

	private AuditLogger(String auditSourceSite) {
		auditLogger = LogSink.getInstance().getAuditLogger();
		this.auditSourceSite = auditSourceSite;
		actionMap = new HashMap<>();
	}

	public static synchronized AuditLogger getInstance() {
		if (instance == null) {
			try {
				ConfigFacade configFacade = (ConfigFacade) new InitialContext().lookup("java:module/ConfigFacade");
				String sourceSite = configFacade.getAuditSourceSite();
				if (sourceSite.equals("")) {
					logger.warn("audit.source.site is empty! Please configure it for more expedient audit trail analysis.");
					sourceSite = "NOT CONFIGURED";
				}
				instance = new AuditLogger(sourceSite);
			} catch (NamingException e) {
				logger.error("Could not fetch ConfigFacade for audit logger.");
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private void accept(AuditEvent event) {
		FhirContext ctx = FhirContext.forR4();
		IParser parser = ctx.newJsonParser();
		String serialized = parser.encodeResourceToString(event);
		auditLogger.info(serialized);
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

	public void logBackendCall(String agentName, String agentUuid, Method calledMethod, List<String> params, String returnValue, Date start) {
		AuditEvent backendCall = new AuditEvent();

		// backendCall.setType();
		// backendCall.setSubType();

		backendCall.setAction(inferAction(calledMethod.getName()));
		Period period = new Period();
		period.setStart(start);
		Date end = Calendar.getInstance(TimeZone.getDefault()).getTime();
		period.setEnd(end);
		backendCall.setPeriod(period);

		backendCall.setRecorded(end);

		// success
		//backendCall.setOutcome(AuditEvent.AuditEventOutcome._0);
		backendCall.setOutcomeDesc(returnValue);

		AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
		CodeableConcept codeableConcept = new CodeableConcept();

		if (agentName.equals("SYSTEM") || agentName.equals("ANONYMOUS")) {
			//todo
		} else {
			codeableConcept.addCoding(new Coding("https://www.hl7.org/fhir/valueset-participation-role-type.html", "humanuser", "human user"));
			agent.setType(codeableConcept);
		}

		agent.setName(agentName);
		Reference who = new Reference();
		Identifier identifier = new Identifier();
		if (!agentName.equals("SYSTEM") && !agentName.equals("ANONYMOUS")) {
			identifier.setValue(agentUuid);
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

		// System Object
		//AuditEntityType entityType = AuditEntityType._2;
		//entity.setType(new Coding(entityType.getSystem(), entityType.toCode(), entityType.getDisplay()));

		backendCall.addEntity(entity);

		accept(backendCall);
	}

	private AuditEvent.AuditEventAction inferAction(String calledMethod) {

		AuditEvent.AuditEventAction cached = actionMap.get(calledMethod);
		if (cached != null) {
			return cached;
		}

		AuditEvent.AuditEventAction infered;
		if (methodsStartsWith(calledMethod, createPrefix)) {
			infered = AuditEvent.AuditEventAction.C;
		} else if (methodsStartsWith(calledMethod, readPrefix)) {
			infered = AuditEvent.AuditEventAction.R;
		} else if (methodsStartsWith(calledMethod, updatePrefix)) {
			infered = AuditEvent.AuditEventAction.U;
		} else if (methodsStartsWith(calledMethod, deletePrefix)) {
			infered = AuditEvent.AuditEventAction.D;
		} else if (methodsStartsWith(calledMethod, executePrefix)) {
			infered = AuditEvent.AuditEventAction.E;
		} else {
			infered = AuditEvent.AuditEventAction.E;
		}
		actionMap.put(calledMethod, infered);
		return infered;
	}

	private boolean methodsStartsWith(String methodName, Set<String> prefixes) {
		return prefixes.stream().anyMatch(methodName::startsWith);
	}

}
