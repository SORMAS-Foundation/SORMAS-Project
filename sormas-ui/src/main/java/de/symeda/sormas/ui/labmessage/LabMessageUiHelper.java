/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;

import com.vaadin.ui.HorizontalLayout;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.samples.AbstractSampleForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class LabMessageUiHelper {

	public static Button addCancelAndUpdateLabels(CommitDiscardWrapperComponent<?> component, String discardCaptionTag) {
		component.getButtonsPanel().setExpandRatio(component.getDiscardButton(), 0);
		Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, null);
		component.getButtonsPanel().addComponent(cancelButton, 0);
		component.getButtonsPanel().setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
		component.getButtonsPanel().setExpandRatio(cancelButton, 1);

		component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionSaveAndContinue));
		component.getDiscardButton().setCaption(I18nProperties.getCaption(discardCaptionTag));

		return cancelButton;
	}

	public static void establishFinalCommitButtons(CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent) {
		// add option to navigate to related entry after saving
		LabMessageUiHelper.addSaveAndOpenEntryButton(sampleComponent);
		// add yet another listener just for the save button, it will not be added to the save and open entry button
		sampleComponent.getCommitButton()
			.addClickListener((Button.ClickListener) clickEvent -> SormasUI.get().getNavigator().navigateTo(LabMessagesView.VIEW_NAME));
	}

	private static Button addSaveAndOpenEntryButton(CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent) {
		Button saveAndOpenEntryButton = ButtonHelper.createButton("");

		// Copy every existing listener from the old commit button to the newly added one
		for (Object listener : sampleComponent.getCommitButton().getListeners(Button.ClickEvent.class)) {
			saveAndOpenEntryButton.addClickListener((Button.ClickListener) listener);
		}

		SampleDto sample = sampleComponent.getWrappedComponent().getValue();

		if (sample.getAssociatedCase() != null) {
			saveAndOpenEntryButton
				.addClickListener(clickEvent -> ControllerProvider.getCaseController().navigateToCase(sample.getAssociatedCase().getUuid()));
			saveAndOpenEntryButton.setCaption(I18nProperties.getCaption(Captions.actionSaveAndOpenCase));
		} else if (sample.getAssociatedContact() != null) {
			saveAndOpenEntryButton
				.addClickListener(clickEvent -> ControllerProvider.getContactController().navigateToData(sample.getAssociatedContact().getUuid()));
			saveAndOpenEntryButton.setCaption(I18nProperties.getCaption(Captions.actionSaveAndOpenContact));
		} else if (sample.getAssociatedEventParticipant() != null) {
			saveAndOpenEntryButton.addClickListener(
				clickEvent -> ControllerProvider.getEventParticipantController().navigateToData(sample.getAssociatedEventParticipant().getUuid()));
			saveAndOpenEntryButton.setCaption(I18nProperties.getCaption(Captions.actionSaveAndOpenEventParticipant));
		} else {
			throw new UnsupportedOperationException("Could not create saveAndOpenEntryButton: associated entity is not supported.");
		}

		saveAndOpenEntryButton.setStyleName(sampleComponent.getCommitButton().getStyleName());

		HorizontalLayout buttonsPanel = sampleComponent.getButtonsPanel();
		buttonsPanel.addComponent(saveAndOpenEntryButton, buttonsPanel.getComponentCount() - 1);

		saveAndOpenEntryButton.setId("saveAndOpenEntryButton");

		return saveAndOpenEntryButton;
	}

}
