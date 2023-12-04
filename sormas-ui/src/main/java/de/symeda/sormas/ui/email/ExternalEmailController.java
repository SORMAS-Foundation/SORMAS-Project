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

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.RootEntityType;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.externalemail.ExternalEmailException;
import de.symeda.sormas.api.externalemail.ExternalEmailOptionsDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ExternalEmailController {

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
				Notification.show(I18nProperties.getString(Strings.errorOccurred), e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
		});

		optionsCommitDiscard.addDiscardListener(optionsPopup::close);

	}
}
