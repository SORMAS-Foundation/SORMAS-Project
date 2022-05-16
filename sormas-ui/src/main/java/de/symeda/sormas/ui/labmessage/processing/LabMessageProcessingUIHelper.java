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

package de.symeda.sormas.ui.labmessage.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.naming.CannotProceedException;

import org.apache.commons.lang3.mutable.MutableObject;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeleteDetails;
import de.symeda.sormas.api.common.DeleteReason;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.labmessage.LabMessageForm;
import de.symeda.sormas.ui.labmessage.LabMessageUiHelper;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.SampleController;
import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.samples.SampleEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Collection of common UI related functions used by processing related code placed in multiple classes
 */
public class LabMessageProcessingUIHelper {

	private LabMessageProcessingUIHelper() {
	}

	public static void showEditSampleWindow(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		LabMessageDto labMessage,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler) {
		Window window = VaadinUiUtil.createPopupWindow();
		MutableObject<CommitDiscardWrapperComponent<SampleEditForm>> editComponentWrapper = new MutableObject<>();
		// discard on close without commit or discard button clicked
		Registration closeListener = window.addCloseListener((e) -> {
			editComponentWrapper.getValue().discard();
		});

		CommitDiscardWrapperComponent<SampleEditForm> sampleEditComponent =
			LabMessageProcessingUIHelper.getSampleEditComponent(sample, newPathogenTests, labMessage, commitHandler, cancelHandler, () -> {
				// do not discard
				closeListener.remove();
				window.close();
			});

		showFormWithLabMessage(labMessage, sampleEditComponent, window, I18nProperties.getString(Strings.headingEditSample), false, false);
		sampleEditComponent.addDoneListener(
			() -> {
				// prevent discard on close
				closeListener.remove();
				// close after commit/discard
				window.close();
			});
	}

	private static CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		LabMessageDto labMessage,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler,
		Runnable closeOnNavigateToRefer) {
		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleEditForm> sampleEditComponent =
			sampleController.getSampleEditComponent(sample.getUuid(), sample.isPseudonymized(), sampleController.getDiseaseOf(sample), false);

		// add existing tests to edit component
		int caseSampleCount = sampleController.caseSampleCountOf(sample);

		List<PathogenTestDto> existingTests = FacadeProvider.getPathogenTestFacade().getAllBySample(sample.toReference());
		for (PathogenTestDto existingTest : existingTests) {
			PathogenTestForm pathogenTestForm = sampleController.addPathogenTestComponent(sampleEditComponent, existingTest, caseSampleCount);
			// when the user removes the pathogen test from the sampleEditComponent, mark the pathogen test as to be removed on commit
			pathogenTestForm.addDetachListener(
				(ClientConnector.DetachEvent detachEvent) -> sampleEditComponent.getWrappedComponent()
					.getTestsToBeRemovedOnCommit()
					.add(pathogenTestForm.getValue().toReference()));
		}
		if (!existingTests.isEmpty()) {
			// delete all pathogen test marked as removed on commit
			sampleEditComponent.addCommitListener(() -> {
				for (PathogenTestReferenceDto pathogenTest : sampleEditComponent.getWrappedComponent().getTestsToBeRemovedOnCommit()) {
					FacadeProvider.getPathogenTestFacade()
						.deletePathogenTest(
							pathogenTest.getUuid(),
							new DeleteDetails(
								DeleteReason.OTHER_REASON,
								I18nProperties.getString(Strings.pathogenTestDeletedDuringLabMessageConversion)));
				}
			});
		}
		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleEditComponent, true);

		// add newly submitted tests to sample edit component
		List<String> existingTestExternalIds =
			existingTests.stream().filter(Objects::nonNull).map(PathogenTestDto::getExternalId).filter(Objects::nonNull).collect(Collectors.toList());

		List<PathogenTestDto> newTestsToAdd =
			newPathogenTests.stream().filter(p -> !existingTestExternalIds.contains(p.getExternalId())).collect(Collectors.toList());

		for (PathogenTestDto test : newTestsToAdd) {
			PathogenTestForm form = sampleController.addPathogenTestComponent(sampleEditComponent, test, caseSampleCount);
			sampleController.setViaLimsFieldChecked(form);
		}

		// always add at least one PathogenTest
		if (existingTests.isEmpty() && newTestsToAdd.isEmpty()) {
			sampleController.addPathogenTestComponent(
				sampleEditComponent,
				LabMessageProcessingHelper.buildPathogenTest(null, labMessage, sample, UserProvider.getCurrent().getUser()),
				caseSampleCount);
		}

		// button configuration
		Consumer<Disease> createReferral = (disease) -> {
			// discard current changes and create sample referral
			SampleDto existingSample =
				FacadeProvider.getSampleFacade().getSampleByUuid(sampleEditComponent.getWrappedComponent().getValue().getUuid());
			createSampleReferral(existingSample, disease, labMessage, commitHandler, cancelHandler);

			closeOnNavigateToRefer.run();
		};
		Consumer<SampleDto> editSample = referredTo -> {
			showEditSampleWindow(referredTo, newPathogenTests, labMessage, commitHandler, cancelHandler);
			closeOnNavigateToRefer.run();
		};

		sampleController.addReferOrLinkToOtherLabButton(sampleEditComponent, sampleController.getDiseaseOf(sample), createReferral, editSample);

		sampleController.addReferredFromButton(sampleEditComponent, editSample);

		// add commit and discard listeners
		sampleEditComponent.addCommitListener(() -> {
			List<PathogenTestDto> createdPathogenTests = new ArrayList<>();
			for (int i = 0; i < sampleEditComponent.getComponentCount(); i++) {
				Component component = sampleEditComponent.getComponent(i);
				if (PathogenTestForm.class.isAssignableFrom(component.getClass())) {
					createdPathogenTests.add(((PathogenTestForm) component).getValue());
				}
			}

			commitHandler.accept(new SampleAndPathogenTests(sampleEditComponent.getWrappedComponent().getValue(), createdPathogenTests));
		});
		sampleEditComponent.addDiscardListener(cancelHandler::run);

		LabMessageUiHelper.establishFinalCommitButtons(sampleEditComponent);

		return sampleEditComponent;
	}

	public static void showFormWithLabMessage(
		LabMessageDto labMessage,
		CommitDiscardWrapperComponent<? extends Component> editComponent,
		Window window,
		String heading,
		boolean entityCreated) {
		showFormWithLabMessage(labMessage, editComponent, window, heading, entityCreated, true);
	}

	public static void showFormWithLabMessage(
		LabMessageDto labMessage,
		CommitDiscardWrapperComponent<? extends Component> editComponent,
		Window window,
		String heading,
		boolean entityCreated,
		boolean discardOnClose) {

		addProcessedInMeantimeCheck(editComponent, labMessage, entityCreated);
		LabMessageForm form = new LabMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setFirstComponent(form);
		horizontalSplitPanel.setSecondComponent(editComponent);
		horizontalSplitPanel.setSplitPosition(569, Sizeable.Unit.PIXELS); // This is just the position it needs to avoid vertical scroll bars.
		horizontalSplitPanel.addStyleName("lab-message-processing");

		Panel panel = new Panel();
		panel.setHeightFull();
		panel.setContent(horizontalSplitPanel);

		HorizontalLayout layout = new HorizontalLayout(panel);
		layout.setHeightFull();
		layout.setMargin(true);

		window.setHeightFull();
		window.setContent(layout);
		window.setCaption(heading);
		UI.getCurrent().addWindow(window);

		form.setValue(labMessage);

		// discard on close without clicking discard/commit button
		Registration closeListener = window.addCloseListener((e) -> {
			if (discardOnClose) {
				editComponent.discard();
			}
		});

		// close after clicking commit or discard button
		editComponent.addDoneListener(() -> {
			// prevent discard on close
			closeListener.remove();
			window.close();
		});
	}

	private static void createSampleReferral(
		SampleDto existingSample,
		Disease disease,
		LabMessageDto labMessage,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler) {
		Window window = VaadinUiUtil.createPopupWindow();

		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			sampleController.getSampleReferralCreateComponent(existingSample, disease);
		addAllTestReportsOf(labMessage, sampleCreateComponent);
		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleCreateComponent, true);

		sampleCreateComponent.addCommitListener(() -> {
			List<PathogenTestDto> createdPathogenTests = new ArrayList<>();
			for (int i = 0; i < sampleCreateComponent.getComponentCount(); i++) {
				Component component = sampleCreateComponent.getComponent(i);
				if (PathogenTestForm.class.isAssignableFrom(component.getClass())) {
					createdPathogenTests.add(((PathogenTestForm) component).getValue());
				}
			}

			commitHandler.accept(new SampleAndPathogenTests(sampleCreateComponent.getWrappedComponent().getValue(), createdPathogenTests));
		});
		sampleCreateComponent.addDiscardListener(cancelHandler::run);

		LabMessageUiHelper.establishFinalCommitButtons(sampleCreateComponent);

		showFormWithLabMessage(labMessage, sampleCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewSample), false);
	}

	private static void addAllTestReportsOf(LabMessageDto labMessageDto, CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent) {

		SampleController sampleController = ControllerProvider.getSampleController();
		SampleDto sample = sampleCreateComponent.getWrappedComponent().getValue();
		List<PathogenTestDto> pathogenTests =
			LabMessageProcessingHelper.buildPathogenTests(sample, labMessageDto, UserProvider.getCurrent().getUser());
		int caseSampleCount = sampleController.caseSampleCountOf(sample);

		for (PathogenTestDto pathogenTest : pathogenTests) {
			PathogenTestForm pathogenTestCreateComponent =
				sampleController.addPathogenTestComponent(sampleCreateComponent, pathogenTest, caseSampleCount);
			sampleController.setViaLimsFieldChecked(pathogenTestCreateComponent);
		}
	}

	private static void addProcessedInMeantimeCheck(
		CommitDiscardWrapperComponent<? extends Component> createComponent,
		LabMessageDto labMessageDto,
		boolean entityCreated) {
		createComponent.setPrimaryCommitListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getLabMessageFacade().isProcessed(labMessageDto.getUuid()))) {
				createComponent.getCommitButton().setEnabled(false);
				showAlreadyProcessedPopup(createComponent.getWrappedComponent(), entityCreated);
				throw new CannotProceedException("The lab message was processed in the meantime");
			}
		});
	}

	/**
	 * @param component
	 *            that holds a reference to the current state of processing a labMessage
	 * @param entityCreated
	 *            should be true if a Case, Contact or EventParticipant has already been created. This will result in an option to delete
	 *            that entity again.
	 */
	public static void showAlreadyProcessedPopup(Component component, boolean entityCreated) {
		VerticalLayout warningLayout = VaadinUiUtil.createWarningLayout();
		Window popupWindow = VaadinUiUtil.showPopupWindow(warningLayout);
		Label infoLabel = new Label(I18nProperties.getValidationError(Validations.labMessageAlreadyProcessedError));
		CssStyles.style(infoLabel, CssStyles.LABEL_LARGE, CssStyles.LABEL_WHITE_SPACE_NORMAL);
		warningLayout.addComponent(infoLabel);
		popupWindow.addCloseListener(e -> popupWindow.close());
		popupWindow.setWidth(400, Sizeable.Unit.PIXELS);

		// If a case, contact or event participant was saved by the user while processing...
		if (entityCreated) {
			Button button = createDeleteEntityButton(component);
			button.addClickListener(e -> popupWindow.close());
			warningLayout.addComponent(button);
		}
	}

	/**
	 * @param component
	 *            component is expected to not be null, as it should never be null in a correct call of this method. Calling this method
	 *            with a null component will result in a NPE.
	 * @return Button to delete the formerly created Case, Contact or EventParticipant entity
	 */
	private static Button createDeleteEntityButton(Component component) {
		if (SampleCreateForm.class.equals(component.getClass())) {
			SampleDto sample = ((SampleCreateForm) component).getValue();
			if (sample.getAssociatedCase() != null) {
				return ButtonHelper.createButton(Captions.labMessage_deleteNewlyCreatedCase, e -> {
					try {
						FacadeProvider.getCaseFacade()
							.delete(sample.getAssociatedCase().getUuid(), new DeleteDetails(DeleteReason.DUPLICATE_ENTRIES, null));
					} catch (ExternalSurveillanceToolException survToolException) {
						// should not happen because the new case was not shared
						throw new RuntimeException(survToolException);
					}
				}, ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedContact() != null) {
				return ButtonHelper.createButton(
					Captions.labMessage_deleteNewlyCreatedContact,
					e -> FacadeProvider.getContactFacade()
						.delete(sample.getAssociatedContact().getUuid(), new DeleteDetails(DeleteReason.DUPLICATE_ENTRIES, null)),
					ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedEventParticipant() != null) {
				return ButtonHelper.createButton(
					Captions.labMessage_deleteNewlyCreatedEventParticipant,
					e -> FacadeProvider.getEventParticipantFacade()
						.delete(sample.getAssociatedEventParticipant().getUuid(), new DeleteDetails(DeleteReason.DUPLICATE_ENTRIES, null)),
					ValoTheme.BUTTON_PRIMARY);
			}
		}
		throw new UnsupportedOperationException("The created entity to be deleted could net be determined.");
	}

}
