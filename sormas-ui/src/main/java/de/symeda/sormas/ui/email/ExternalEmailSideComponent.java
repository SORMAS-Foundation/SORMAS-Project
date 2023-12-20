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
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogCriteria;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ExternalEmailSideComponent extends SideComponent {

	private static final long serialVersionUID = 3988960053146779975L;

	public static ExternalEmailSideComponent forCase(
		CaseDataDto caze,
		boolean isEditAllowed,
		Runnable callback,
		Consumer<Runnable> sendEmailWrapper) {
		return new ExternalEmailSideComponent(
			DocumentWorkflow.CASE_EMAIL,
			RootEntityType.ROOT_CASE,
			DocumentRelatedEntityType.CASE,
			caze.toReference(),
			caze.getPerson(),
			Strings.messageCasePersonHasNoEmail,
			Strings.messageNoExternalEmailToCaseSent,
			new ManualMessageLogCriteria().caze(caze.toReference()),
			isEditAllowed,
			caze.isInJurisdiction(),
			callback,
			sendEmailWrapper);
	}

	public static ExternalEmailSideComponent forContact(ContactDto contact, boolean isEditAllowed, Consumer<Runnable> sendEmailWrapper) {
		return new ExternalEmailSideComponent(
			DocumentWorkflow.CONTACT_EMAIL,
			RootEntityType.ROOT_CONTACT,
			DocumentRelatedEntityType.CONTACT,
			contact.toReference(),
			contact.getPerson(),
			Strings.messageContactPersonHasNoEmail,
			Strings.messageNoExternalEmailToContactSent,
			new ManualMessageLogCriteria().contact(contact.toReference()),
			isEditAllowed,
			contact.isInJurisdiction(),
			null,
			sendEmailWrapper);
	}

	public static ExternalEmailSideComponent forEventParticipant(
		EventParticipantDto eventParticipant,
		boolean isEditAllowed,
		Consumer<Runnable> sendEmailWrapper) {
		return new ExternalEmailSideComponent(
			DocumentWorkflow.EVENT_PARTICIPANT_EMAIL,
			RootEntityType.ROOT_EVENT_PARTICIPANT,
			null,
			eventParticipant.toReference(),
			eventParticipant.getPerson().toReference(),
			Strings.messageEventParticipantPersonHasNoEmail,
			Strings.messageNoExternalEmailToEventParticipantSent,
			new ManualMessageLogCriteria().eventParticipant(eventParticipant.toReference()),
			isEditAllowed,
			eventParticipant.isInJurisdiction(),
			null,
			sendEmailWrapper);
	}

	public static ExternalEmailSideComponent forTravelEntry(TravelEntryDto travelEntry, boolean isEditAllowed, Consumer<Runnable> sendEmailWrapper) {
		return new ExternalEmailSideComponent(
			DocumentWorkflow.TRAVEL_ENTRY_EMAIL,
			RootEntityType.ROOT_TRAVEL_ENTRY,
			DocumentRelatedEntityType.TRAVEL_ENTRY,
			travelEntry.toReference(),
			travelEntry.getPerson(),
			Strings.messageTravelEntryPersonHasNoEmail,
			Strings.messageNoExternalEmailToTravelEntrySent,
			new ManualMessageLogCriteria().travelEntry(travelEntry.toReference()),
			isEditAllowed,
			travelEntry.isInJurisdiction(),
			null,
			sendEmailWrapper);
	}

	private ExternalEmailList emailList = null;

	private ExternalEmailSideComponent(
		DocumentWorkflow documentWorkflow,
		RootEntityType rootEntityType,
		DocumentRelatedEntityType documentRelatedEntityType,
		ReferenceDto rootEntityReference,
		PersonReferenceDto personRef,
		String noRecipientStringKey,
		String noEmailsStringKey,
		ManualMessageLogCriteria baseCriteria,
		boolean isEditAllowed,
		boolean isInJurisdiction,
		Runnable sendEmailCallback,
		Consumer<Runnable> sendEmailWrapper) {
		super(I18nProperties.getCaption(Captions.messagesEmails), sendEmailWrapper);

		if (isEditAllowed
			&& (isInJurisdiction && UserProvider.getCurrent().hasUserRight(UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION)
				|| !isInJurisdiction && UserProvider.getCurrent().hasUserRight(UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION))) {
			addCreateButton(
				I18nProperties.getCaption(Captions.messagesSendEmail),
				() -> ControllerProvider.getExternalEmailController()
					.sendEmail(documentWorkflow, rootEntityType, documentRelatedEntityType, rootEntityReference, personRef, () -> {
						if (sendEmailCallback != null) {
							sendEmailCallback.run();
						} else {
							emailList.reload();
						}
					}),
				UserRight.EXTERNAL_EMAIL_SEND);
		}

		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personRef.getUuid());
		if (isEditAllowed && CollectionUtils.isEmpty(person.getAllEmailAddresses())) {
			if (createButton != null) {
				createButton.setEnabled(false);
			}

			Label noRecipientLabel = new Label(I18nProperties.getString(noRecipientStringKey));
			noRecipientLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);

			addComponent(noRecipientLabel);
		}

		emailList = new ExternalEmailList(baseCriteria.withTemplate(true).messageType(MessageType.EMAIL), noEmailsStringKey);

		addComponent(emailList);
		emailList.reload();
	}
}
