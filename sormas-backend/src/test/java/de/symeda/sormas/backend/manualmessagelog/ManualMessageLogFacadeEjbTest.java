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

package de.symeda.sormas.backend.manualmessagelog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogIndexDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class ManualMessageLogFacadeEjbTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf;
	private UserDto user;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF();
		user = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
	}

	@Test
	public void testGetIndexList() {
		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);
		ManualMessageLog caseEmail = createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(caze.getPerson()));
			m.setCaze(getCaseService().getByReferenceDto(caze.toReference()));
		});
		ContactDto contact = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		ManualMessageLog contactEmail = createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(contact.getPerson()));
			m.setContact(getContactService().getByReferenceDto(contact.toReference()));
		});

		createManualMessageLog(MessageType.SMS, null, m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(caze.getPerson()));
			m.setCaze(getCaseService().getByReferenceDto(caze.toReference()));
		});

		List<ManualMessageLogIndexDto> caseEmails = getManualMessageLogFacade()
			.getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).caze(caze.toReference()));
		assertThat(caseEmails, hasSize(1));
		assertThat(caseEmails.get(0).getUuid(), is(caseEmail.getUuid()));
		assertThat(caseEmails.get(0).getEmailAddress(), is("test@email.com"));
		assertThat(caseEmails.get(0).getSendingUser(), is(user.toReference()));
		assertThat(caseEmails.get(0).getMessageType(), is(MessageType.EMAIL));
		assertThat(caseEmails.get(0).getUsedTemplate(), is("TestTemplate.txt"));
		assertThat(caseEmails.get(0).getAttachedDocuments(), containsInAnyOrder("TestDocument.pdf", "TestDoc2.docx"));

		List<ManualMessageLogIndexDto> contactEmails = getManualMessageLogFacade()
			.getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).contact(contact.toReference()));
		assertThat(contactEmails, hasSize(1));
		assertThat(contactEmails.get(0).getUuid(), is(contactEmail.getUuid()));
	}

	@Test
	public void testGetIndexListForEventParticipant() {
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), creator.createPerson(), user.toReference());

		ManualMessageLog eventParticipantEmail = createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(eventParticipant.getPerson().toReference()));
			m.setEventParticipant(getEventParticipantService().getByReferenceDto(eventParticipant.toReference()));
		});

		List<ManualMessageLogIndexDto> epEmails = getManualMessageLogFacade().getIndexList(
			new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).eventParticipant(eventParticipant.toReference()));
		assertThat(epEmails, hasSize(1));
		assertThat(epEmails.get(0).getUuid(), is(eventParticipantEmail.getUuid()));
	}

	@Test
	public void testGetIndexListForTravelEntry() {
		TravelEntryDto travelEntry = creator.createTravelEntry(creator.createPerson().toReference(), user.toReference(), rdcf, null);
		ManualMessageLog travelEntryEmail = createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(travelEntry.getPerson()));
			m.setTravelEntry(getTravelEntryService().getByReferenceDto(travelEntry.toReference()));
		});

		List<ManualMessageLogIndexDto> epEmails = getManualMessageLogFacade()
			.getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).travelEntry(travelEntry.toReference()));
		assertThat(epEmails, hasSize(1));
		assertThat(epEmails.get(0).getUuid(), is(travelEntryEmail.getUuid()));
	}

	@Test
	public void testGetIndexListPseudonymization() {
		TravelEntryDto travelEntry = creator.createTravelEntry(creator.createPerson().toReference(), user.toReference(), rdcf, null);
		ManualMessageLog travelEntryEmail = createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(travelEntry.getPerson()));
			m.setTravelEntry(getTravelEntryService().getByReferenceDto(travelEntry.toReference()));
		});

		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		UserDto user2 = creator.createUser(rdcf2, "Officer2", DefaultUserRole.SURVEILLANCE_OFFICER);

		loginWith(user2);

		List<ManualMessageLogIndexDto> travelEntryEmails = getManualMessageLogFacade()
			.getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).travelEntry(travelEntry.toReference()));
		assertThat(travelEntryEmails, hasSize(1));
		assertThat(travelEntryEmails.get(0).getUuid(), is(travelEntryEmail.getUuid()));
		assertThat(travelEntryEmails.get(0).getEmailAddress(), is(I18nProperties.getCaption(Captions.inaccessibleValue)));
		assertThat(travelEntryEmails.get(0).getSendingUser(), is(nullValue()));
	}

	@Test
	public void testGetForCaseWithSpecialAcces() {
		CaseDataDto caze = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf, null);
		createEmailMessageLog(m -> {
			m.setRecipientPerson(getPersonService().getByReferenceDto(caze.getPerson()));
			m.setCaze(getCaseService().getByReferenceDto(caze.toReference()));
		});

		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		UserDto user2 = creator.createUser(rdcf2, "Officer2", DefaultUserRole.SURVEILLANCE_OFFICER);

		creator.createSpecialCaseAccess(caze.toReference(), user.toReference(), user2.toReference(), DateHelper.addDays(new Date(), 1));

		loginWith(user2);

		List<ManualMessageLogIndexDto> epEmails = getManualMessageLogFacade()
			.getIndexList(new ManualMessageLogCriteria().messageType(MessageType.EMAIL).withTemplate(true).caze(caze.toReference()));
		assertThat(epEmails, hasSize(1));
		assertThat(epEmails.get(0).isPseudonymized(), is(false));
	}

	private ManualMessageLog createEmailMessageLog(Consumer<ManualMessageLog> setupRelatedEntity) {
		return createManualMessageLog(MessageType.EMAIL, "TestTemplate.txt", setupRelatedEntity);
	}

	private ManualMessageLog createManualMessageLog(MessageType messageType, String templateName, Consumer<ManualMessageLog> setupRelatedEntity) {
		ManualMessageLog log = new ManualMessageLog();
		log.setMessageType(messageType);
		log.setSendingUser(getUserService().getByUuid(user.getUuid()));
		log.setSentDate(new Date());
		log.setEmailAddress("test@email.com");
		log.setUsedTemplate(templateName);
		log.setAttachedDocuments(Arrays.asList("TestDocument.pdf", "TestDoc2.docx"));

		setupRelatedEntity.accept(log);

		getManualMessageLogService().ensurePersisted(log);

		return log;
	}
}
