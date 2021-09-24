/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.share.shareinfo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.sample.Sample;

@Entity(name = "sormastosormasshareinfo")
public class SormasToSormasShareInfo extends AbstractDomainObject {

	private static final long serialVersionUID = -8368155805122562791L;

	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String SAMPLE = "sample";
	public static final String EVENT = "event";
	public static final String EVENT_PARTICIPANT = "eventParticipant";
	public static final String ORGANIZATION_ID = "organizationId";
	public static final String REQUESTS = "requests";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";

	private Case caze;

	private Contact contact;

	private Sample sample;

	private Event event;

	private EventParticipant eventParticipant;

	private String organizationId;

	private List<ShareRequestInfo> requests;

	private boolean ownershipHandedOver;

	public SormasToSormasShareInfo() {
		requests = new ArrayList<>();
	}

	@ManyToOne
	public Case getCaze() {
		return caze;
	}

	public void setCaze(Case caze) {
		this.caze = caze;
	}

	@ManyToOne
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@ManyToOne
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@ManyToOne
	public EventParticipant getEventParticipant() {
		return eventParticipant;
	}

	public void setEventParticipant(EventParticipant eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	@Column(columnDefinition = "text")
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
}
