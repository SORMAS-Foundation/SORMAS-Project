package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class SampleListCriteria extends BaseCriteria {

	private final CaseReferenceDto caseReferenceDto;
	private final ContactReferenceDto contactReferenceDto;
	private final EventParticipantReferenceDto eventParticipantReferenceDto;

	private final SampleAssociationType sampleAssociationType;

	public static class Builder {

		private CaseReferenceDto caseReferenceDto;
		private ContactReferenceDto contactReferenceDto;
		private EventParticipantReferenceDto eventParticipantReferenceDto;

		private SampleAssociationType sampleAssociationType;

		public Builder withCase(CaseReferenceDto caseReferenceDto) {
			this.caseReferenceDto = caseReferenceDto;
			this.sampleAssociationType = SampleAssociationType.CASE;
			return this;
		}

		public Builder withContact(ContactReferenceDto contactReferenceDto) {
			this.contactReferenceDto = contactReferenceDto;
			this.sampleAssociationType = SampleAssociationType.CONTACT;
			return this;
		}

		public Builder withEventParticipant(EventParticipantReferenceDto eventParticipantReferenceDto) {
			this.eventParticipantReferenceDto = eventParticipantReferenceDto;
			this.sampleAssociationType = SampleAssociationType.EVENT_PARTICIPANT;
			return this;
		}

		public SampleListCriteria build() {
			return new SampleListCriteria(this);
		}
	}

	private SampleListCriteria(Builder builder) {
		this.caseReferenceDto = builder.caseReferenceDto;
		this.contactReferenceDto = builder.contactReferenceDto;
		this.eventParticipantReferenceDto = builder.eventParticipantReferenceDto;

		this.sampleAssociationType = builder.sampleAssociationType;
	}

	public CaseReferenceDto getCaseReferenceDto() {
		return caseReferenceDto;
	}

	public ContactReferenceDto getContactReferenceDto() {
		return contactReferenceDto;
	}

	public EventParticipantReferenceDto getEventParticipantReferenceDto() {
		return eventParticipantReferenceDto;
	}

	public SampleAssociationType getSampleAssociationType() {
		return sampleAssociationType;
	}
}
