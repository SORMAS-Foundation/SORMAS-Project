/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.externalmessage.physiciansreport;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.externalmessage.processing.EntrySelectionField;
import de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PhysiciansReportProcessingFlow extends AbstractPhysiciansReportProcessingFlow {

	public PhysiciansReportProcessingFlow(ExternalMessageMapper mapper, ExternalMessageProcessingFacade processingFacade) {
		super(UserProvider.getCurrent().getUser(), mapper, processingFacade);
	}

	@Override
	protected CompletionStage<Boolean> handleMissingDisease() {
		return ExternalMessageProcessingUIHelper.showMissingDiseaseConfiguration();
	}

	@Override
	protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
		return ExternalMessageProcessingUIHelper.showRelatedForwardedMessageConfirmation();
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
		ExternalMessageProcessingUIHelper.showPickOrCreatePersonWindow(person, callback);
	}

	@Override
	protected void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		ExternalMessageDto externalMessage,
		HandlerCallback<de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult> callback) {

		if (CollectionUtils.isEmpty(similarCases)) {
			de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult result =
				new de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult();
			result.setNewCase(true);

			callback.done(result);
			return;
		}

		EntrySelectionField.Options.Builder optionsBuilder =
			new EntrySelectionField.Options.Builder().addSelectCase(similarCases).addCreateEntry(EntrySelectionField.OptionType.CREATE_CASE);

		ExternalMessageProcessingUIHelper.showPickOrCreateEntryWindow(optionsBuilder.build(), externalMessage, callback);
	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, PersonDto person, ExternalMessageDto externalMessage, HandlerCallback<CaseDataDto> callback) {
		ExternalMessageProcessingUIHelper.showCreateCaseWindow(caze, person, externalMessage, mapper, callback);
	}

	@Override
	protected void handleUpdateCase(CaseDataDto caze, ExternalMessageDto externalMessage, HandlerCallback<CaseDataDto> callback) {

		PhysiciansReportCaseEditComponent caseComponent = new PhysiciansReportCaseEditComponent(caze, externalMessage);
		caseComponent.addCommitListener(() -> callback.done(caze));
		caseComponent.addDiscardListener(callback::cancel);

		Window window = VaadinUiUtil.createPopupWindow();
		ExternalMessageProcessingUIHelper
			.showFormWithLabMessage(externalMessage, caseComponent, window, I18nProperties.getString(Strings.headingProcessPhysiciansReport), false);
	}

	@Override
	protected void handleConvertSamePersonContactsAndEventParticipants(CaseDataDto caze, HandlerCallback<Void> callback) {
		ControllerProvider.getCaseController().convertSamePersonContactsAndEventParticipants(caze, () -> {
			callback.done(null);
		});
	}
}
