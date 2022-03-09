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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
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
        AuditEvent applicationStartAudit = new AuditEvent();
        applicationStartAudit.setType(new Coding("https://hl7.org/fhir/R4/valueset-audit-event-type.html", "110100", "Application Activity"));

        Coding subtype = new Coding("https://hl7.org/fhir/R4/valueset-audit-event-sub-type.html", "110120", "Application Start");
        applicationStartAudit.setSubtype(Collections.singletonList(subtype));

        applicationStartAudit.setAction(AuditEvent.AuditEventAction.E);
        applicationStartAudit.setRecorded(Calendar.getInstance(TimeZone.getDefault()).getTime());
        // success
        applicationStartAudit.setOutcome(AuditEvent.AuditEventOutcome.fromCode("0"));
        applicationStartAudit.setOutcomeDesc("Application starting");

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

        source.addType(new Coding("https://www.hl7.org/fhir/valueset-audit-source-type.html", "4", "Application Server"));
        applicationStartAudit.setSource(source);

        applicationStartAudit.addEntity();

        accept(applicationStartAudit);
    }
}
