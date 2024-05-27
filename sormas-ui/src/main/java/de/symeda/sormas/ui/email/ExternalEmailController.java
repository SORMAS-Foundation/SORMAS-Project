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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.AttachmentException;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsWithAttachmentsDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalEmailController {

	private static final Logger logger = LoggerFactory.getLogger(ExternalEmailController.class);

	public void sendEmail(
		DocumentWorkflow documentWorkflow,
		RootEntityType rootEntityType,
		DocumentRelatedEntityType documentRelatedEntityType,
		ReferenceDto rootEntityReference,
		PersonReferenceDto personReference,
		Runnable callback) {
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personReference.getUuid());
		ExternalEmailOptionsForm optionsForm = new ExternalEmailOptionsForm(
			documentWorkflow,
			documentRelatedEntityType,
			person,
			FacadeProvider.getExternalEmailFacade().isAttachmentAvailable(personReference));

		ExternalEmailOptionsDto defaultValue = new ExternalEmailOptionsDto(documentWorkflow, rootEntityType, rootEntityReference);
		String presonPrimaryEmail = person.getEmailAddress(true);
		if (StringUtils.isNotBlank(presonPrimaryEmail)) {
			defaultValue.setRecipientEmail(presonPrimaryEmail);
		}

		optionsForm.setValue(defaultValue);

		CommitDiscardWrapperComponent<ExternalEmailOptionsForm> optionsCommitDiscard =
			new CommitDiscardWrapperComponent<>(optionsForm, optionsForm.getFieldGroup());
		optionsCommitDiscard.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionSend));
		Window optionsPopup = VaadinUiUtil.showPopupWindow(optionsCommitDiscard, I18nProperties.getString(Strings.headingExternalEmailSend));
		optionsForm.setWidth(600, Sizeable.Unit.PIXELS);

		optionsCommitDiscard.addCommitListener(() -> {
			ExternalEmailOptionsDto options = optionsForm.getValue();

			try {
				FacadeProvider.getExternalEmailFacade().sendEmail(options);

				optionsPopup.close();
				Notification.show(null, I18nProperties.getString(Strings.notificationExternalEmailSent), Notification.Type.TRAY_NOTIFICATION);
				callback.run();
			} catch (DocumentTemplateException | ExternalEmailException e) {
				logger.error("Email could not be sent", e);
				Notification.show(I18nProperties.getString(Strings.errorOccurred), e.getMessage(), Notification.Type.ERROR_MESSAGE);
			} catch (AttachmentException | ValidationException e) {
				logger.warn("Email could not be sent", e);
				Notification.show(I18nProperties.getString(Strings.errorOccurred), e.getMessage(), Notification.Type.WARNING_MESSAGE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		optionsCommitDiscard.addDiscardListener(optionsPopup::close);

	}

	public <T extends HasUuid> void sendBulkEmail(
		DocumentWorkflow documentWorkflow,
		RootEntityType rootEntityType,
		DocumentRelatedEntityType documentRelatedEntityType,
		Collection<T> selectionReference,
		Consumer<List<T>> bulkOperationDoneCallback,
		Function<T, ReferenceDto> mapToReference,
		DocumentWorkflow templatesWorkflow) {

		ExternalBulkEmailOptionsForm optionsForm =
			new ExternalBulkEmailOptionsForm(documentWorkflow, documentRelatedEntityType, rootEntityType, templatesWorkflow);
		ExternalEmailOptionsWithAttachmentsDto defaultValue = new ExternalEmailOptionsWithAttachmentsDto(documentWorkflow, rootEntityType);
		optionsForm.setValue(defaultValue);

		CommitDiscardWrapperComponent<ExternalBulkEmailOptionsForm> optionsCommitDiscard =
			new CommitDiscardWrapperComponent<>(optionsForm, optionsForm.getFieldGroup());
		optionsCommitDiscard.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionSend));
		Window optionsPopup = VaadinUiUtil.showPopupWindow(optionsCommitDiscard, I18nProperties.getString(Strings.headingExternalEmailSend));
		optionsForm.setWidth(600, Sizeable.Unit.PIXELS);

		optionsCommitDiscard.addCommitListener(() -> {
			ExternalEmailOptionsWithAttachmentsDto options = optionsForm.getValue();

			List<T> selectedEntitiesCpy = new ArrayList<>(selectionReference);

			new BulkOperationHandler<T>(
				Strings.messageBulkEmailsSentToAllSelectedEntities,
				Strings.messageBulkEmailsNotSentToToEntites,
				Strings.headingBulkEmailsSomeNotSent,
				Strings.headingBulkEmailsNoProcessedEntities,
				Strings.messageBulkEmailsCountNotProcessed,
				Strings.messageBulkEmailsCountNotProcessedExternalReason,
				null,
				null,
				Strings.messageBulkEmailsNoEligible,
				Strings.messageBulkEmailsFinishedWithSkips,
				Strings.messageBulkEmailsFinishedWithoutSuccess).doBulkOperation(selectedEntries -> {
					try {
						return FacadeProvider.getExternalEmailFacade()
							.sendBulkEmail(options, selectedEntries.stream().map(mapToReference).collect(Collectors.toList()));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}, selectedEntitiesCpy, bulkOperationDoneCallback);

			optionsPopup.close();
			Notification.show(null, I18nProperties.getString(Strings.notificationExternalEmailSent), Notification.Type.TRAY_NOTIFICATION);
		});

		optionsCommitDiscard.addDiscardListener(optionsPopup::close);
	}
}
