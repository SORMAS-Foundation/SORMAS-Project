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

package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;

public class SormasToSormasDto implements Serializable {

	private static final long serialVersionUID = 3226296154450214227L;

	@Valid
	private SormasToSormasOriginInfoDto originInfo;

	@Valid
	private List<SormasToSormasCaseDto> cases;
	@Valid
	private List<SormasToSormasContactDto> contacts;
	@Valid
	private List<SormasToSormasSampleDto> samples;
	@Valid
	private List<SormasToSormasEventDto> events;
	@Valid
	private List<SormasToSormasEventParticipantDto> eventParticipants;
	@Valid
	private List<SormasToSormasImmunizationDto> immunizations;

	public SormasToSormasOriginInfoDto getOriginInfo() {
		return originInfo;
	}

	public void setOriginInfo(SormasToSormasOriginInfoDto originInfo) {
		this.originInfo = originInfo;
	}

	public List<SormasToSormasCaseDto> getCases() {
		return cases;
	}

	public void setCases(List<SormasToSormasCaseDto> cases) {
		this.cases = cases;
	}

	public List<SormasToSormasContactDto> getContacts() {
		return contacts;
	}

	public void setContacts(List<SormasToSormasContactDto> contacts) {
		this.contacts = contacts;
	}

	public List<SormasToSormasSampleDto> getSamples() {
		return samples;
	}

	public void setSamples(List<SormasToSormasSampleDto> samples) {
		this.samples = samples;
	}

	public List<SormasToSormasEventDto> getEvents() {
		return events;
	}

	public void setEvents(List<SormasToSormasEventDto> events) {
		this.events = events;
	}

	public List<SormasToSormasEventParticipantDto> getEventParticipants() {
		return eventParticipants;
	}

	public void setEventParticipants(List<SormasToSormasEventParticipantDto> eventParticipants) {
		this.eventParticipants = eventParticipants;
	}

	public List<SormasToSormasImmunizationDto> getImmunizations() {
		return immunizations;
	}

	public void setImmunizations(List<SormasToSormasImmunizationDto> immunizations) {
		this.immunizations = immunizations;
	}
}
