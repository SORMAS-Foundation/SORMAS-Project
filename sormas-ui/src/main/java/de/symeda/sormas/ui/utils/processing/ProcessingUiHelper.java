/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.processing;

import com.vaadin.server.Sizeable;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.dataprocessing.EntitySelection;
import de.symeda.sormas.api.utils.dataprocessing.HandlerCallback;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.externalmessage.processing.EntrySelectionComponentForExternalMessage;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public final class ProcessingUiHelper {

	public static void showPickOrCreatePersonWindow(PersonDto person, HandlerCallback<EntitySelection<PersonDto>> callback) {
		ControllerProvider.getPersonController()
			.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessage), selectedPersonRef -> {
				PersonDto selectedPersonDto = selectedPersonRef.getUuid().equals(person.getUuid())
					? person
					: FacadeProvider.getPersonFacade().getByUuid(selectedPersonRef.getUuid());

				callback.done(new EntitySelection<>(selectedPersonDto, person.getUuid().equals(selectedPersonRef.getUuid())));
			},
				callback::cancel,
				false,
				UiUtil.enabled(FeatureType.PERSON_DUPLICATE_CUSTOM_SEARCH)
					? I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessageWithoutMatches)
					: null);
	}

	public static void showPickOrCreateEntryWindow(
		EntrySelectionField.Options options,
		ExternalMessageDto labMessage,
		HandlerCallback<PickOrCreateEntryResult> callback) {

		EntrySelectionComponentForExternalMessage selectField = new EntrySelectionComponentForExternalMessage(labMessage, options);

		final CommitDiscardWrapperComponent<EntrySelectionComponentForExternalMessage> selectionField =
			new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);

		selectionField.addCommitListener(() -> callback.done(selectField.getSelectionResult()));
		selectionField.addDiscardListener(callback::cancel);

		selectField.setSelectionChangeCallback(commitAllowed -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		VaadinUiUtil.showModalPopupWindow(selectionField, I18nProperties.getString(Strings.headingPickOrCreateEntry), true);
	}
}
