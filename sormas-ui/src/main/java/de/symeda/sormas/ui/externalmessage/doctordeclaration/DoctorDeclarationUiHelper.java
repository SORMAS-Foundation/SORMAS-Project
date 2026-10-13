package de.symeda.sormas.ui.externalmessage.doctordeclaration;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.externalmessage.ExternalMessagesView;
import de.symeda.sormas.ui.samples.AbstractSampleForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

/**
 * Collection of doctor declaration UI related functions
 */
public class DoctorDeclarationUiHelper {

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

	public static void establishCommitButtons(CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleComponent, boolean lastSample) {
		if (lastSample) {
			// add option to navigate to related entry after saving
			DoctorDeclarationUiHelper.addSaveAndOpenEntryButton(sampleComponent);
		}
		// add yet another listener just for the save button, it will not be added to the save and open entry button
		sampleComponent.getCommitButton().addClickListener(clickEvent -> SormasUI.get().getNavigator().navigateTo(ExternalMessagesView.VIEW_NAME));
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
