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

package de.symeda.sormas.api.sample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;

public class SampleSimilarityCriteria implements Serializable {

	private static final long serialVersionUID = -2051359287159310211L;

	private CaseReferenceDto caze;
	private ContactReferenceDto contact;
	private EventParticipantReferenceDto eventParticipant;
	private String labSampleId;
	private Date sampleDateTime;
	private SampleMaterial sampleMaterial;

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public SampleSimilarityCriteria caze(CaseReferenceDto caze) {
		this.caze = caze;

		return this;
	}

	public ContactReferenceDto getContact() {
		return contact;
	}

	public SampleSimilarityCriteria contact(ContactReferenceDto contact) {
		this.contact = contact;

		return this;
	}

	public EventParticipantReferenceDto getEventParticipant() {
		return eventParticipant;
	}

	public SampleSimilarityCriteria eventParticipant(EventParticipantReferenceDto eventParticipant) {
		this.eventParticipant = eventParticipant;

		return this;
	}

	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}
}
