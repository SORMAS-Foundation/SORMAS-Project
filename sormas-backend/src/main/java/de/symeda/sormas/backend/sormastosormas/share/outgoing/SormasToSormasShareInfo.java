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

package de.symeda.sormas.backend.sormastosormas.share.outgoing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;

@Entity(name = "sormastosormasshareinfo")
public class SormasToSormasShareInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -8368155805122562791L;

	public static final String TABLE_NAME = "sormastosormasshareinfo";

	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String SAMPLE = "sample";
	public static final String EVENT = "event";
	public static final String EVENT_PARTICIPANT = "eventParticipant";
	public static final String IMMUNIZATION = "immunization";
	public static final String SURVEILLANCE_REPORT = "surveillanceReport";
	public static final String ORGANIZATION_ID = "organizationId";
	public static final String REQUESTS = "requests";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";

	private Case caze;

	private Contact contact;

	private Sample sample;

	private Event event;

	private EventParticipant eventParticipant;

	private Immunization immunization;

	private SurveillanceReport surveillanceReport;

	private String organizationId;

	private List<ShareRequestInfo> requests;

	private boolean ownershipHandedOver;

	public SormasToSormasShareInfo() {
		requests = new ArrayList<>();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public EventParticipant getEventParticipant() {
		return eventParticipant;
	}

	public void setEventParticipant(EventParticipant eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Immunization getImmunization() {
		return immunization;
	}

	public void setImmunization(Immunization immunization) {
		this.immunization = immunization;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public SurveillanceReport getSurveillanceReport() {
		return surveillanceReport;
	}

	public void setSurveillanceReport(SurveillanceReport surveillanceReport) {
		this.surveillanceReport = surveillanceReport;
	}

	@Column(length = FieldConstraints.CHARACTER_LIMIT_DEFAULT, nullable = false)
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = ShareRequestInfo.SHARE_REQUEST_INFO_SHARE_INFO_TABLE,
		joinColumns = @JoinColumn(name = ShareRequestInfo.SHARE_REQUEST_INFO_SHARE_INFO_INVERS_JOIN_COLUMN),
		inverseJoinColumns = @JoinColumn(name = ShareRequestInfo.SHARE_REQUEST_INFO_SHARE_INFO_JOIN_COLUMN))
	public List<ShareRequestInfo> getRequests() {
		return requests;
	}

	public void setRequests(List<ShareRequestInfo> requests) {
		this.requests = requests;
	}

	@Column
	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	@Transient
	public SormasToSormasShareable getSharedEntity() {
		return Stream.of(caze, contact, sample, immunization, surveillanceReport, event, eventParticipant)
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("ShareInfo[" + getUuid() + "] does not contain an entity"));
	}
}
