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

package de.symeda.sormas.ui.labmessage;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.labmessage.processing.AbstractPhysicianReportProcessingFlow;
import de.symeda.sormas.ui.labmessage.processing.LabMessageProcessingUIHelper;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PhysicianReportProcessingFlow extends AbstractPhysicianReportProcessingFlow {

	public PhysicianReportProcessingFlow() {
		super(UserProvider.getCurrent().getUser());
	}

	@Override
	protected CompletionStage<Boolean> handleMissingDisease() {
		return LabMessageProcessingUIHelper.showMissingDiseaseConfiguration();
	}

	@Override
	protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
		return LabMessageProcessingUIHelper.showRelatedForwardedMessageConfirmation();
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
		LabMessageProcessingUIHelper.showPickOrCreatePersonWindow(person, callback);
	}

	@Override
	protected void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		LabMessageDto labMessage,
		HandlerCallback<PickOrCreateEntryResult> callback) {

		if (CollectionUtils.isEmpty(similarCases)) {
			PickOrCreateEntryResult result = new PickOrCreateEntryResult();
			result.setNewCase(true);

			callback.done(result);
			return;
		}

		EntrySelectionField.Options.Builder optionsBuilder =
			new EntrySelectionField.Options.Builder().addSelectCase(similarCases).addCreateEntry(EntrySelectionField.OptionType.CREATE_CASE);

		LabMessageProcessingUIHelper.showPickOrCreateEntryWindow(optionsBuilder.build(), labMessage, callback);
	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
		LabMessageProcessingUIHelper.showCreateCaseWindow(caze, person, labMessage, callback);
	}

	@Override
	protected void handleUpdateCase(CaseDataDto caze, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {

		PhysicianReportCaseEditComponent caseComponent = new PhysicianReportCaseEditComponent(caze, labMessage);

		Window window = VaadinUiUtil.createPopupWindow();
		LabMessageProcessingUIHelper
			.showFormWithLabMessage(labMessage, caseComponent, window, I18nProperties.getString(Strings.headingCreateNewCase), false);
	}
}
