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

package de.symeda.sormas.ui.email;

import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.manualmessagelog.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ExternalEmailSideComponent extends SideComponent {

	public static ExternalEmailSideComponent forCase(CaseDataDto caze, Consumer<Runnable> actionCallback) {
		return new ExternalEmailSideComponent(
				DocumentWorkflow.CASE_EMAIL,
				RootEntityType.ROOT_CASE,
				caze.toReference(),
				caze.getPerson(),
				Strings.messageCasePersonHasNoEmail,
				Strings.messageNoExternalEmailToCaseSent,
				new ManualMessageLogCriteria().caze(caze.toReference()),
				actionCallback);
	}

	public static ExternalEmailSideComponent forContact(ContactDto contact, Consumer<Runnable> actionCallback) {
		return new ExternalEmailSideComponent(
				DocumentWorkflow.CONTACT_EMAIL,
				RootEntityType.ROOT_CONTACT,
				contact.toReference(),
				contact.getPerson(),
				Strings.messageContactPersonHasNoEmail,
				Strings.messageNoExternalEmailToContactSent,
				new ManualMessageLogCriteria().contact(contact.toReference()),
				actionCallback);
	}

	public static ExternalEmailSideComponent forEventParticipant(EventParticipantDto eventParticipant, Consumer<Runnable> actionCallback) {
		return new ExternalEmailSideComponent(
				DocumentWorkflow.EVENT_PARTICIPANT_EMAIL,
				RootEntityType.ROOT_EVENT_PARTICIPANT,
				eventParticipant.toReference(),
				eventParticipant.getPerson().toReference(),
				Strings.messageEventParticipantPersonHasNoEmail,
				Strings.messageNoExternalEmailToEventParticipantSent,
				new ManualMessageLogCriteria().eventParticipant(eventParticipant.toReference()),
				actionCallback);
	}

	public static ExternalEmailSideComponent forTravelEntry(TravelEntryDto travelEntry, Consumer<Runnable> actionCallback) {
		return new ExternalEmailSideComponent(
				DocumentWorkflow.TRAVEL_ENTRY_EMAIL,
				RootEntityType.ROOT_TRAVEL_ENTRY,
				travelEntry.toReference(),
				travelEntry.getPerson(),
				Strings.messageTravelEntryPersonHasNoEmail,
				Strings.messageNoExternalEmailToTravelEntrySent,
				new ManualMessageLogCriteria().travelEntry(travelEntry.toReference()),
				actionCallback);
	}

	private ExternalEmailSideComponent(
		DocumentWorkflow documentWorkflow,
		RootEntityType rootEntityType,
		ReferenceDto rootEntityReference,
		PersonReferenceDto personRef,
		String noRecipientStringKey,
		String noEmailsStringKey,
		ManualMessageLogCriteria emailListCriteria,
		Consumer<Runnable> actionCallback) {
		super(I18nProperties.getCaption(Captions.messagesEmails), actionCallback);

		addCreateButton(
			I18nProperties.getCaption(Captions.messagesSendEmail),
			() -> ControllerProvider.getExternalEmailController().sendEmail(documentWorkflow, rootEntityType, rootEntityReference, personRef),
			UserRight.EXTERNAL_EMAIL_SEND);

		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personRef.getUuid());
		if (CollectionUtils.isEmpty(person.getAllEmailAddresses())) {
			createButton.setEnabled(false);

			Label noRecipientLabel = new Label(I18nProperties.getString(noRecipientStringKey));
			noRecipientLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);

			addComponent(noRecipientLabel);
		}

		ExternalEmailList emailList = new ExternalEmailList(emailListCriteria.messageType(MessageType.EMAIL), noEmailsStringKey);
		addComponent(emailList);
	}
}
