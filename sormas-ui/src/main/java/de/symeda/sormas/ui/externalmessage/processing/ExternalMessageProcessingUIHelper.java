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

package de.symeda.sormas.ui.externalmessage.processing;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.naming.CannotProceedException;

import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.mutable.MutableObject;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
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
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.processing.AbstractProcessingFlow;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageMapper;
import de.symeda.sormas.api.externalmessage.processing.ExternalMessageProcessingResult;
import de.symeda.sormas.api.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.api.externalmessage.processing.labmessage.LabMessageProcessingHelper;
import de.symeda.sormas.api.externalmessage.processing.labmessage.SampleAndPathogenTests;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseCreateForm;
import de.symeda.sormas.ui.externalmessage.ExternalMessageForm;
import de.symeda.sormas.ui.externalmessage.labmessage.LabMessageUiHelper;
import de.symeda.sormas.ui.samples.AbstractSampleForm;
import de.symeda.sormas.ui.samples.CollapsiblePathogenTestForm;
import de.symeda.sormas.ui.samples.humansample.SampleController;
import de.symeda.sormas.ui.samples.humansample.SampleCreateForm;
import de.symeda.sormas.ui.samples.humansample.SampleEditForm;
import de.symeda.sormas.ui.samples.humansample.SampleEditPathogenTestListHandler;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Collection of common UI related functions used by processing related code placed in multiple classes
 */
public class ExternalMessageProcessingUIHelper {

	private ExternalMessageProcessingUIHelper() {
	}

	public static CompletionStage<Boolean> showMissingDiseaseConfiguration() {
		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.externalMessageNoDisease),
			new Label(I18nProperties.getString(Strings.messageDiseaseNotSpecifiedInLabMessage)),
			I18nProperties.getCaption(Captions.actionContinue),
			I18nProperties.getCaption(Captions.actionCancel));
	}

	public static CompletionStage<Boolean> showRelatedForwardedMessageConfirmation() {
		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.externalMessageForwardedMessageFound),
			new Label(I18nProperties.getString(Strings.messageForwardedExternalMessageFound)),
			I18nProperties.getCaption(Captions.actionYes),
			I18nProperties.getCaption(Captions.actionCancel));
	}

	public static CompletionStage<Boolean> showMultipleSamplesPopup() {
		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.externalMessageMultipleSampleReports),
			new Label(I18nProperties.getString(Strings.messageMultipleSampleReports), ContentMode.HTML),
			I18nProperties.getCaption(Captions.actionContinue),
			I18nProperties.getCaption(Captions.actionCancel));
	}

	public static void showPickOrCreatePersonWindow(
		PersonDto person,
		AbstractProcessingFlow.HandlerCallback<ExternalMessageProcessingResult.EntitySelection<PersonDto>> callback) {
		ControllerProvider.getPersonController()
			.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessage), selectedPersonRef -> {
				PersonDto selectedPersonDto = selectedPersonRef.getUuid().equals(person.getUuid())
					? person
					: FacadeProvider.getPersonFacade().getByUuid(selectedPersonRef.getUuid());

				callback.done(
					new ExternalMessageProcessingResult.EntitySelection<>(selectedPersonDto, person.getUuid().equals(selectedPersonRef.getUuid())));
			},
				callback::cancel,
				false,
				FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.PERSON_DUPLICATE_CUSTOM_SEARCH)
					? I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessageWithoutMatches)
					: null);
	}

	public static void showPickOrCreateEntryWindow(
		EntrySelectionField.Options options,
		ExternalMessageDto labMessage,
		AbstractProcessingFlow.HandlerCallback<PickOrCreateEntryResult> callback) {

		EntrySelectionField selectField = new EntrySelectionField(labMessage, options);

		final CommitDiscardWrapperComponent<EntrySelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);

		selectionField.addCommitListener(() -> callback.done(selectField.getValue()));
		selectionField.addDiscardListener(callback::cancel);

		selectField.setSelectionChangeCallback(commitAllowed -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		VaadinUiUtil.showModalPopupWindow(selectionField, I18nProperties.getString(Strings.headingPickOrCreateEntry), true);
	}

	public static void showCreateCaseWindow(
		CaseDataDto caze,
		PersonDto person,
		ExternalMessageDto labMessage,
		ExternalMessageMapper mapper,
		AbstractProcessingFlow.HandlerCallback<CaseDataDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, null, null, true);
		caseCreateComponent.addCommitListener(() -> {
			updateAddressAndSavePerson(
				FacadeProvider.getPersonFacade().getByUuid(caseCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				mapper);

			callback.done(caseCreateComponent.getWrappedComponent().getValue());

		});
		caseCreateComponent.addDiscardListener(callback::cancel);

		caseCreateComponent.getWrappedComponent().setValue(caze);
		if (Boolean.TRUE.equals(FacadeProvider.getPersonFacade().isValidPersonUuid(person.getUuid()))) {
			caseCreateComponent.getWrappedComponent().setSearchedPerson(person);
		}
		caseCreateComponent.getWrappedComponent().setPerson(person);

		showFormWithLabMessage(labMessage, caseCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewCase), false);
	}

	public static void updateAddressAndSavePerson(PersonDto personDto, ExternalMessageMapper mapper) {
		if (personDto.getAddress().getCity() == null
			&& personDto.getAddress().getHouseNumber() == null
			&& personDto.getAddress().getPostalCode() == null
			&& personDto.getAddress().getStreet() == null) {
			mapper.mapToLocation(personDto.getAddress());
		}
		FacadeProvider.getPersonFacade().save(personDto);
	}

	public static void showEditSampleWindow(
		SampleDto sample,
		boolean lastSample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto externalMessageDto,
		ExternalMessageMapper mapper,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler) {
		Window window = VaadinUiUtil.createPopupWindow();
		MutableObject<CommitDiscardWrapperComponent<SampleEditForm>> editComponentWrapper = new MutableObject<>();
		// discard on close without commit or discard button clicked
		Registration closeListener = window.addCloseListener(
			nonNull(editComponentWrapper.getValue()) ? e -> editComponentWrapper.getValue().discard() : e -> editComponentWrapper.getValue());

		CommitDiscardWrapperComponent<SampleEditForm> sampleEditComponent = ExternalMessageProcessingUIHelper
			.getSampleEditComponent(sample, lastSample, newPathogenTests, externalMessageDto, mapper, commitHandler, cancelHandler, () -> {
				// do not discard
				closeListener.remove();
				window.close();
			});

		showFormWithLabMessage(
			externalMessageDto,
			sampleEditComponent,
			window,
			I18nProperties.getString(Strings.headingExternalMessageProcessSample),
			false,
			false);
		sampleEditComponent.addDoneListener(() -> {
			// prevent discard on close
			closeListener.remove();
			// close after commit/discard
			window.close();
		});
	}

	private static CommitDiscardWrapperComponent<SampleEditForm> getSampleEditComponent(
		SampleDto sample,
		boolean lastSample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto externalMessageDto,
		ExternalMessageMapper mapper,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler,
		Runnable closeOnNavigateToRefer) {
		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleEditForm> sampleEditComponent = sampleController.getSampleEditComponent(
			sample.getUuid(),
			sample.isPseudonymized(),
			sample.isInJurisdiction(),
			sampleController.getDiseaseOf(sample),
			false,
			null);
		sampleEditComponent.getWrappedComponent().setHeading(I18nProperties.getString(Strings.headingExternalMessageSampleInformation));

		// add existing tests to edit component
		int caseSampleCount = sampleController.caseSampleCountOf(sample);
		SampleEditPathogenTestListHandler pathogenTestHandler = new SampleEditPathogenTestListHandler();

		List<PathogenTestDto> existingTests = FacadeProvider.getPathogenTestFacade().getAllBySample(sample.toReference());

		Label existingTestsSeparator = new Label("<br/><hr/><br/>", ContentMode.HTML);
		existingTestsSeparator.setWidth(100f, Sizeable.Unit.PERCENTAGE);
		existingTestsSeparator.setVisible(!existingTests.isEmpty());
		sampleEditComponent.addComponent(existingTestsSeparator, sampleEditComponent.getComponentCount() - 1);

		Label existingTestsLabel = createPathogenTestGroupLabel(Strings.headingExternalMessageExistingPathogenTests, existingTests);
		sampleEditComponent.addComponent(existingTestsLabel, sampleEditComponent.getComponentCount() - 1);

		for (int i = 0; i < existingTests.size(); i++) {
			PathogenTestDto existingTest = existingTests.get(i);
			CollapsiblePathogenTestForm pathogenTestForm = sampleController.addPathogenTestComponent(
				sampleEditComponent,
				existingTest,
				pathogenTestHandler::addPathogenTest,
				caseSampleCount,
				false,
				false,
				i > 0);
			// when the user removes the pathogen test from the sampleEditComponent, mark the pathogen test as to be removed on commit
			pathogenTestForm.addDetachListener((ClientConnector.DetachEvent detachEvent) -> {
				List<PathogenTestReferenceDto> pathogenTestsToRemove = sampleEditComponent.getWrappedComponent().getTestsToBeRemovedOnCommit();

				pathogenTestsToRemove.add(pathogenTestForm.getValue().toReference());
				if (pathogenTestsToRemove.size() == existingTests.size()) {
					existingTestsSeparator.setVisible(false);
					existingTestsLabel.setVisible(false);
				}
			});
		}
		if (!existingTests.isEmpty()) {
			// delete all pathogen test marked as removed on commit
			sampleEditComponent.addCommitListener(() -> {
				for (PathogenTestReferenceDto pathogenTest : sampleEditComponent.getWrappedComponent().getTestsToBeRemovedOnCommit()) {
					FacadeProvider.getPathogenTestFacade()
						.deletePathogenTest(
							pathogenTest.getUuid(),
							new DeletionDetails(
								DeletionReason.OTHER_REASON,
								I18nProperties.getString(Strings.pathogenTestDeletedDuringLabMessageConversion)));
				}
			});
		}

		// add newly submitted tests to sample edit component
		List<String> existingTestExternalIds =
			existingTests.stream().filter(Objects::nonNull).map(PathogenTestDto::getExternalId).filter(Objects::nonNull).collect(Collectors.toList());

		List<PathogenTestDto> newTestsToAdd =
			newPathogenTests.stream().filter(p -> !existingTestExternalIds.contains(p.getExternalId())).collect(Collectors.toList());

		// always add at least one PathogenTest
		if (existingTests.isEmpty() && newTestsToAdd.isEmpty()) {
			newTestsToAdd.add(LabMessageProcessingHelper.buildPathogenTest(null, mapper, sample, UserProvider.getCurrent().getUser()));
		}

		Label newTestSeparator = new Label("<br/><hr/><br/>", ContentMode.HTML);
		newTestSeparator.setWidth(100f, Sizeable.Unit.PERCENTAGE);
		newTestSeparator.setVisible(!newTestsToAdd.isEmpty());
		sampleEditComponent.addComponent(newTestSeparator, sampleEditComponent.getComponentCount() - 1);

		Label newTestsLabel = createPathogenTestGroupLabel(Strings.headingExternalMessageNewPathogenTests, newTestsToAdd);
		sampleEditComponent.addComponent(newTestsLabel, sampleEditComponent.getComponentCount() - 1);

		MutableLong newPathogenTestCount = new MutableLong(newTestsToAdd.size());
		ExternalMessageProcessingUIHelper.addNewPathogenTests(newTestsToAdd, sampleEditComponent, false, pathogenTestHandler::addPathogenTest, () -> {
			long newTestCount = newPathogenTestCount.decrementAndGet();
			if (newTestCount == 0) {
				newTestsLabel.setVisible(false);
			}
		});

		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleEditComponent, true, () -> {
			newPathogenTestCount.increment();
			newTestsLabel.setVisible(true);
		}, () -> {
			long newTestCount = newPathogenTestCount.decrementAndGet();
			if (newTestCount == 0) {
				newTestsLabel.setVisible(false);
			}
		}, pathogenTestHandler::addPathogenTest);

		// button configuration
		Consumer<Disease> createReferral = disease -> {
			// discard current changes and create sample referral
			SampleDto existingSample =
				FacadeProvider.getSampleFacade().getSampleByUuid(sampleEditComponent.getWrappedComponent().getValue().getUuid());
			createSampleReferral(existingSample, lastSample, disease, newPathogenTests, externalMessageDto, commitHandler, cancelHandler);

			closeOnNavigateToRefer.run();
		};
		Consumer<SampleDto> editSample = referredTo -> {
			showEditSampleWindow(referredTo, lastSample, newPathogenTests, externalMessageDto, mapper, commitHandler, cancelHandler);
			closeOnNavigateToRefer.run();
		};

		sampleController.addReferOrLinkToOtherLabButton(sampleEditComponent, sampleController.getDiseaseOf(sample), createReferral, editSample);

		sampleController.addReferredFromButton(sampleEditComponent, editSample);

		// add done and discard listeners
		sampleEditComponent.setPostCommitListener(() -> {
			pathogenTestHandler.saveAll(sample.toReference());
			commitHandler
				.accept(new SampleAndPathogenTests(sampleEditComponent.getWrappedComponent().getValue(), pathogenTestHandler.getPathogenTests()));
		});
		sampleEditComponent.addDiscardListener(cancelHandler::run);

		LabMessageUiHelper.establishCommitButtons(sampleEditComponent, lastSample);

		return sampleEditComponent;
	}

	private static Label createPathogenTestGroupLabel(String captionStringProperty, List<PathogenTestDto> testsInGroup) {
		Label existingTestsLabel = new Label(I18nProperties.getString(captionStringProperty));
		existingTestsLabel.addStyleName(H3);
		existingTestsLabel.setVisible(!testsInGroup.isEmpty());
		return existingTestsLabel;
	}

	public static void showFormWithLabMessage(
		ExternalMessageDto externalMessageDto,
		CommitDiscardWrapperComponent<? extends Component> editComponent,
		Window window,
		String heading,
		boolean entityCreated) {
		showFormWithLabMessage(externalMessageDto, editComponent, window, heading, entityCreated, true);
	}

	public static void showFormWithLabMessage(
		ExternalMessageDto externalMessageDto,
		CommitDiscardWrapperComponent<? extends Component> editComponent,
		Window window,
		String heading,
		boolean entityCreated,
		boolean discardOnClose) {

		addProcessedInMeantimeCheck(editComponent, externalMessageDto, entityCreated);
		ExternalMessageForm form = new ExternalMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);

		form.addStyleName(CssStyles.VSPACE_TOP_3);
		editComponent.addStyleName(CssStyles.VSPACE_TOP_3);

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
		layout.setMargin(new MarginInfo(false, true, true, true));

		window.setHeightFull();
		window.setContent(layout);
		window.setCaption(heading);
		UI.getCurrent().addWindow(window);

		form.setValue(externalMessageDto);

		// discard on close without clicking discard/commit button
		Registration closeListener = window.addCloseListener(e -> {
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
		boolean lastSample,
		Disease disease,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto externalMessageDto,
		Consumer<SampleAndPathogenTests> commitHandler,
		Runnable cancelHandler) {
		Window window = VaadinUiUtil.createPopupWindow();

		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			sampleController.getSampleReferralCreateComponent(existingSample, disease);

		SampleEditPathogenTestListHandler pathogenTestHandler = new SampleEditPathogenTestListHandler();

		newPathogenTests.forEach(t -> t.setSample(existingSample.toReference()));
		addNewPathogenTests(newPathogenTests, sampleCreateComponent, true, pathogenTestHandler::addPathogenTest, null);

		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleCreateComponent, true, null, null, pathogenTestHandler::addPathogenTest);

		sampleCreateComponent.setPostCommitListener(() -> {
			SampleDto referredSample = sampleCreateComponent.getWrappedComponent().getValue();

			pathogenTestHandler.saveAll(referredSample.toReference());
			commitHandler.accept(new SampleAndPathogenTests(referredSample, pathogenTestHandler.getPathogenTests()));
		});
		sampleCreateComponent.addDiscardListener(cancelHandler::run);

		LabMessageUiHelper.establishCommitButtons(sampleCreateComponent, lastSample);

		showFormWithLabMessage(externalMessageDto, sampleCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewSample), false);
	}

	public static void addNewPathogenTests(
		List<PathogenTestDto> pathogenTests,
		CommitDiscardWrapperComponent<? extends AbstractSampleForm> sampleForm,
		boolean forceSeparator,
		Consumer<PathogenTestDto> saveHandler,
		Runnable deleteHandler) {

		SampleController sampleController = ControllerProvider.getSampleController();
		SampleDto sample = sampleForm.getWrappedComponent().getValue();
		int caseSampleCount = sampleController.caseSampleCountOf(sample);

		for (int i = 0; i < pathogenTests.size(); i++) {
			PathogenTestDto pathogenTest = pathogenTests.get(i);
			CollapsiblePathogenTestForm pathogenTestComponent = sampleController
				.addPathogenTestComponent(sampleForm, pathogenTest, saveHandler, caseSampleCount, true, true, forceSeparator || i > 0);
			pathogenTestComponent.addDetachListener((e) -> {
				if (deleteHandler != null) {
					deleteHandler.run();
				}
			});
		}
	}

	public static void addProcessedInMeantimeCheck(
		CommitDiscardWrapperComponent<? extends Component> createComponent,
		ExternalMessageDto externalMessageDto,
		boolean entityCreated) {
		createComponent.setPrimaryCommitListener(() -> {
			if (Boolean.TRUE.equals(FacadeProvider.getExternalMessageFacade().isProcessed(externalMessageDto.getUuid()))) {
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
		Label infoLabel = new Label(I18nProperties.getValidationError(Validations.externalMessageAlreadyProcessedError));
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
				return ButtonHelper.createButton(Captions.externalMessage_deleteNewlyCreatedCase, e -> {
					FacadeProvider.getCaseFacade()
						.delete(sample.getAssociatedCase().getUuid(), new DeletionDetails(DeletionReason.DUPLICATE_ENTRIES, null));
				}, ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedContact() != null) {
				return ButtonHelper.createButton(
					Captions.externalMessage_deleteNewlyCreatedContact,
					e -> FacadeProvider.getContactFacade()
						.delete(sample.getAssociatedContact().getUuid(), new DeletionDetails(DeletionReason.DUPLICATE_ENTRIES, null)),
					ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedEventParticipant() != null) {
				return ButtonHelper.createButton(
					Captions.externalMessage_deleteNewlyCreatedEventParticipant,
					e -> FacadeProvider.getEventParticipantFacade()
						.delete(sample.getAssociatedEventParticipant().getUuid(), new DeletionDetails(DeletionReason.DUPLICATE_ENTRIES, null)),
					ValoTheme.BUTTON_PRIMARY);
			}
		}
		throw new UnsupportedOperationException("The created entity to be deleted could net be determined.");
	}

}
