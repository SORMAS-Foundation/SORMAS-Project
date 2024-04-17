/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externalmessage.processing;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;

public class ExternalMessageProcessingResult {

	private EntitySelection<PersonDto> selectedPerson;
	private EntitySelection<CaseDataDto> selectedCase;
	private EntitySelection<ContactDto> selectedContact;
	private EntitySelection<EventDto> selectedEvent;
	private EntitySelection<EventParticipantDto> selectedEventParticipant;
	private List<SampleSelection> samples = new ArrayList<>();

	public ExternalMessageProcessingResult() {
	}

	private ExternalMessageProcessingResult(ExternalMessageProcessingResult otherResult) {
		this.selectedPerson = otherResult.selectedPerson;
		this.selectedCase = otherResult.selectedCase;
		this.selectedContact = otherResult.selectedContact;
		this.selectedEvent = otherResult.selectedEvent;
		this.selectedEventParticipant = otherResult.selectedEventParticipant;
		this.samples = otherResult.samples;
	}

	public ExternalMessageProcessingResult withPerson(PersonDto person, boolean isNew) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedPerson = new EntitySelection<>(person, isNew);
		return newResult;
	}

	public ExternalMessageProcessingResult withCreatedCase(CaseDataDto caze) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedCase = new EntitySelection<>(caze, true);
		return newResult;
	}

	public ExternalMessageProcessingResult withSelectedCase(CaseDataDto caze) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedCase = new EntitySelection<>(caze, false);
		return newResult;
	}

	public ExternalMessageProcessingResult withCreatedContact(ContactDto contact) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedContact = new EntitySelection<>(contact, true);
		return newResult;
	}

	public ExternalMessageProcessingResult withSelectedContact(ContactDto contact) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedContact = new EntitySelection<>(contact, false);
		return newResult;
	}

	public ExternalMessageProcessingResult withCreatedEvent(EventDto event) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedEvent = new EntitySelection<>(event, true);

		return newResult;
	}

	public ExternalMessageProcessingResult withSelectedEvent(EventDto event) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedEvent = new EntitySelection<>(event, false);

		return newResult;
	}

	public ExternalMessageProcessingResult withCreatedEventParticipant(EventParticipantDto eventParticipant) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedEventParticipant = new EntitySelection<>(eventParticipant, true);

		return newResult;
	}

	public ExternalMessageProcessingResult withSelectedEventParticipant(EventParticipantDto eventParticipant) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);
		newResult.selectedEventParticipant = new EntitySelection<>(eventParticipant, false);

		return newResult;
	}

	public ExternalMessageProcessingResult andWithSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		SampleReportDto sampleReport,
		Boolean sampleCreated) {
		ExternalMessageProcessingResult newResult = new ExternalMessageProcessingResult(this);

		ArrayList<SampleSelection> newPathogenTests = new ArrayList<>(samples);
		newPathogenTests.add(new SampleSelection(sample, sampleReport, pathogenTests, sampleCreated));

		newResult.samples = newPathogenTests;

		return newResult;
	}

	public EntitySelection<PersonDto> getSelectedPerson() {
		return selectedPerson;
	}

	public PersonDto getPerson() {
		return selectedPerson.getEntity();
	}

	public EntitySelection<CaseDataDto> getSelectedCase() {
		return selectedCase;
	}

	public CaseDataDto getCase() {
		return selectedCase != null ? selectedCase.getEntity() : null;
	}

	public EntitySelection<ContactDto> getSelectedContact() {
		return selectedContact;
	}

	public ContactDto getContact() {
		return selectedContact != null ? selectedContact.getEntity() : null;
	}

	public EntitySelection<EventDto> getSelectedEvent() {
		return selectedEvent;
	}

	public EventDto getEvent() {
		return selectedEvent != null ? selectedEvent.getEntity() : null;
	}

	public EntitySelection<EventParticipantDto> getSelectedEventParticipant() {
		return selectedEventParticipant;
	}

	public EventParticipantDto getEventParticipant() {
		return selectedEventParticipant != null ? selectedEventParticipant.getEntity() : null;
	}

	public List<SampleSelection> getSamples() {
		return samples;
	}

	@Override
	public String toString() {
		return "ExternalMessageProcessingResult{" + "selectedPerson=" + selectedPerson + ", selectedCase=" + selectedCase + ", selectedContact="
			+ selectedContact + ", selectedEvent=" + selectedEvent + ", selectedEventParticipant=" + selectedEventParticipant + ", samples=" + samples
			+ '}';
	}

	public static class EntitySelection<T> {

		private final T entity;
		private final boolean isNew;

		public EntitySelection(T entity, boolean isNew) {
			this.entity = entity;
			this.isNew = isNew;
		}

		public T getEntity() {
			return entity;
		}

		public boolean isNew() {
			return isNew;
		}

		@Override
		public String toString() {
			return "EntitySelection{" + "entity=" + entity + ", isNew=" + isNew + '}';
		}
	}

	public static class SampleSelection extends EntitySelection<SampleDto> {

		private final SampleReportDto sampleReport;

		private final List<PathogenTestDto> pathogenTests;

		public SampleSelection(SampleDto entity, SampleReportDto sampleReport, List<PathogenTestDto> pathogenTests, boolean isNew) {
			super(entity, isNew);
			this.sampleReport = sampleReport;
			this.pathogenTests = pathogenTests;
		}

		public SampleReportDto getSampleReport() {
			return sampleReport;
		}

		public List<PathogenTestDto> getPathogenTests() {
			return pathogenTests;
		}

		@Override
		public String toString() {
			return "SampleSelection{" + "sampleReport=" + sampleReport + ", pathogenTests=" + pathogenTests + ", entity=" + getEntity() + ", isNew="
				+ isNew() + '}';
		}
	}
}
