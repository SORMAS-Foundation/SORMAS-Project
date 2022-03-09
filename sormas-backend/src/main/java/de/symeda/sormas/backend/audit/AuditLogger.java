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
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.codesystems.AuditEntityType;
import org.hl7.fhir.r4.model.codesystems.AuditSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class AuditLogger {
    private static AuditLogger instance;
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    private AuditLogger() {
    }

    public static synchronized AuditLogger getInstance() {
        if (instance == null) {
            instance = new AuditLogger();
        }
        return instance;
    }

    private void accept(AuditEvent event) {
        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();
        String serialized = parser.encodeResourceToString(event);
        LogSink.getInstance().getAuditLogger().info(serialized);
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
        applicationStartAudit.setAgent(Collections.singletonList(agent));

        AuditEvent.AuditEventSourceComponent source = new AuditEvent.AuditEventSourceComponent();
        try {
            source.setSite(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            logger.error("Could not read the hostname of the machine: {}", e.toString());
        }

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
}
