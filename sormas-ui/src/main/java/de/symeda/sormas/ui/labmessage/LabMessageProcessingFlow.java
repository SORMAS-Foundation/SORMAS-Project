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

import static de.symeda.sormas.ui.labmessage.processing.LabMessageProcessingUIHelper.showFormWithLabMessage;

import de.symeda.sormas.ui.labmessage.processing.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.ui.labmessage.processing.LabMessageProcessingHelper;
import de.symeda.sormas.ui.labmessage.processing.LabMessageProcessingUIHelper;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.ui.labmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.ui.labmessage.processing.SampleAndPathogenTests;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseCreateForm;
import de.symeda.sormas.ui.contact.ContactCreateForm;
import de.symeda.sormas.ui.events.EventDataForm;
import de.symeda.sormas.ui.events.EventParticipantEditForm;
import de.symeda.sormas.ui.events.eventLink.EventSelectionField;
import de.symeda.sormas.ui.labmessage.EntrySelectionField;
import de.symeda.sormas.ui.labmessage.LabMessageMapper;
import de.symeda.sormas.ui.labmessage.LabMessageUiHelper;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.SampleController;
import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.samples.SampleSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageProcessingFlow extends AbstractLabMessageProcessingFlow {

	public LabMessageProcessingFlow() {
		super(UserProvider.getCurrent().getUser(), FacadeProvider.getCountryFacade().getServerCountry());
	}

	@Override
	protected CompletionStage<Boolean> handleMissingDisease() {
		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.labMessageNoDisease),
			new Label(I18nProperties.getString(Strings.messageDiseaseNotSpecifiedInLabMessage)),
			I18nProperties.getCaption(Captions.actionContinue),
			I18nProperties.getCaption(Captions.actionCancel),
			null);
	}

	@Override
	protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
		return VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getCaption(Captions.labMessageForwardedMessageFound),
			new Label(I18nProperties.getString(Strings.messageForwardedLabMessageFound)),
			I18nProperties.getCaption(Captions.actionYes),
			I18nProperties.getCaption(Captions.actionCancel),
			null);
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
		ControllerProvider.getPersonController()
			.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessage), selectedPersonRef -> {
				PersonDto selectedPersonDto = null;
				if (selectedPersonRef != null) {
					if (selectedPersonRef.getUuid().equals(person.getUuid())) {
						selectedPersonDto = person;
					} else {
						selectedPersonDto = FacadeProvider.getPersonFacade().getPersonByUuid(selectedPersonRef.getUuid());
					}
				}

				callback.done(selectedPersonDto);
			},
				callback::cancel,
				false,
				FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.PERSON_DUPLICATE_CUSTOM_SEARCH)
					? I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessageWithoutMatches)
					: null);
	}

	@Override
	protected void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants,
		LabMessageDto labMessage,
		HandlerCallback<PickOrCreateEntryResult> callback) {
		EntrySelectionField selectField = new EntrySelectionField(labMessage, similarCases, similarContacts, similarEventParticipants);

		final CommitDiscardWrapperComponent<EntrySelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);

		selectionField.addCommitListener(() -> callback.done(selectField.getValue()));
		selectionField.addDiscardListener(callback::cancel);

		selectField.setSelectionChangeCallback((commitAllowed) -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		VaadinUiUtil.showModalPopupWindow(selectionField, I18nProperties.getString(Strings.headingPickOrCreateEntry), true);
	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, PersonDto person, LabMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, null, true);
		caseCreateComponent.addCommitListener(() -> {
			updateAddressAndSavePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(caseCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessage);

			callback.done(caseCreateComponent.getWrappedComponent().getValue());

		});
		caseCreateComponent.addDiscardListener(callback::cancel);

		caseCreateComponent.getWrappedComponent().setValue(caze);
		if (FacadeProvider.getPersonFacade().isValidPersonUuid(person.getUuid())) {
			caseCreateComponent.getWrappedComponent().setSearchedPerson(person);
		}
		caseCreateComponent.getWrappedComponent().setPerson(person);

		showFormWithLabMessage(labMessage, caseCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewCase), false);
	}

	@Override
	protected void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		LabMessageDto labMessage,
		boolean entityCreated,
		HandlerCallback<SampleAndPathogenTests> callback) {

		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			getSampleCreateComponent(sample, pathogenTests, labMessage, disease, window);

		sampleCreateComponent.addCommitListener(() -> {
			List<PathogenTestDto> createdPathogenTests = new ArrayList<>();
			for (int i = 0; i < sampleCreateComponent.getComponentCount(); i++) {
				Component component = sampleCreateComponent.getComponent(i);
				if (PathogenTestForm.class.isAssignableFrom(component.getClass())) {
					createdPathogenTests.add(((PathogenTestForm) component).getValue());
				}
			}

			callback.done(new SampleAndPathogenTests(sampleCreateComponent.getWrappedComponent().getValue(), createdPathogenTests));
		});
		sampleCreateComponent.addDiscardListener(callback::cancel);

		showFormWithLabMessage(labMessage, sampleCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewSample), entityCreated);
	}

	@Override
	protected void handleCreateContact(ContactDto contact, PersonDto person, LabMessageDto labMessage, HandlerCallback<ContactDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			ControllerProvider.getContactController().getContactCreateComponent(null, false, null, true);

		contactCreateComponent.addCommitListener(() -> {
			updateAddressAndSavePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(contactCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessage);

			callback.done(contactCreateComponent.getWrappedComponent().getValue());
		});
		contactCreateComponent.addDiscardListener(callback::cancel);

		contactCreateComponent.getWrappedComponent().setValue(contact);
		contactCreateComponent.getWrappedComponent().setPerson(person);

		showFormWithLabMessage(labMessage, contactCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewContact), false);
	}

	@Override
	protected void handlePickOrCreateEvent(LabMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {
		EventSelectionField eventSelect =
			new EventSelectionField(labMessage.getTestedDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForLabMessage), null);
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			PickOrCreateEventResult result = new PickOrCreateEventResult();
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {
				result.setEvent(selectedEvent);
			} else {
				result.setNewEvent(true);
			}

			callback.done(result);
		});
		component.addDiscardListener(callback::cancel);
		Registration closeListener = window.addCloseListener((e -> component.discard()));
		component.addDoneListener(() -> {
			closeListener.remove();
			window.close();
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));

		window.setContent(component);
		window.setCaption(I18nProperties.getString(Strings.headingPickOrCreateEvent));
		UI.getCurrent().addWindow(window);
	}

	@Override
	protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
		EventDataForm eventCreateForm = new EventDataForm(true, false);
		eventCreateForm.setValue(event);
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		Window window = VaadinUiUtil.createPopupWindow();
		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();

				EventDto savedEvent = FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Notification.Type.WARNING_MESSAGE);

				callback.done(savedEvent);
			}
		});
		editView.addDiscardListener(callback::cancel);
		Registration closeListener = window.addCloseListener((e -> editView.discard()));
		editView.addDoneListener(() -> {
			closeListener.remove();
			window.close();
		});

		window.setContent(editView);
		window.setCaption(I18nProperties.getString(Strings.headingCreateNewEvent));
		UI.getCurrent().addWindow(window);
	}

	@Override
	protected void handleCreateEventParticipant(
		EventParticipantDto eventParticipant,
		EventDto event,
		LabMessageDto labMessage,
		HandlerCallback<EventParticipantDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		EventParticipantEditForm createForm = new EventParticipantEditForm(event, false, eventParticipant.getPerson().isPseudonymized(), false);
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				FacadeProvider.getPersonFacade().savePerson(dto.getPerson());
				EventParticipantDto savedDto = FacadeProvider.getEventParticipantFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Notification.Type.ASSISTIVE_NOTIFICATION);

				callback.done(savedDto);
			}
		});
		createComponent.addDiscardListener(callback::cancel);

		showFormWithLabMessage(labMessage, createComponent, window, I18nProperties.getString(Strings.headingCreateNewEventParticipant), false);
	}

	@Override
	protected CompletionStage<Boolean> confirmPickExistingEventParticipant() {
		CompletableFuture<Boolean> ret = new CompletableFuture<>();

		CommitDiscardWrapperComponent<VerticalLayout> commitDiscardWrapperComponent =
			new CommitDiscardWrapperComponent<>(new VerticalLayout(new Label(I18nProperties.getString(Strings.infoEventParticipantAlreadyExisting))));
		commitDiscardWrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionContinue));
		commitDiscardWrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionBack));

		commitDiscardWrapperComponent.addCommitListener(() -> ret.complete(true));
		commitDiscardWrapperComponent.addDiscardListener(() -> ret.complete(false));

		VaadinUiUtil.showModalPopupWindow(commitDiscardWrapperComponent, I18nProperties.getCaption(Captions.info), true);

		return ret;
	}

	@Override
	protected void handlePickOrCreateSample(
		List<SampleDto> samples,
		LabMessageDto labMessage,
		HandlerCallback<PickOrCreateSampleResult> callback) {
		SampleSelectionField selectField = new SampleSelectionField(samples, I18nProperties.getString(Strings.infoPickOrCreateSample));

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<SampleSelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);
		selectionField.addCommitListener(() -> {
			PickOrCreateSampleResult result = new PickOrCreateSampleResult();

			SampleDto sampleDto = selectField.getValue();
			if (sampleDto != null) {
				result.setSample(sampleDto);
			} else {
				result.setNewSample(true);
			}

			callback.done(result);
		});
		selectionField.addDiscardListener(callback::cancel);

		selectField.setSelectionChangeCallback((commitAllowed) -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		showFormWithLabMessage(labMessage, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreateSample), false);
	}

	@Override
	protected void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		LabMessageDto labMessage,
		HandlerCallback<SampleAndPathogenTests> callback) {

		LabMessageProcessingUIHelper.showEditSampleWindow(sample, newPathogenTests, labMessage, callback::done, callback::cancel);
	}

	private CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
			SampleDto sample,
			List<PathogenTestDto> pathogenTests,
			LabMessageDto labMessageDto,
			Disease disease,
			Window window) {
		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent = sampleController.getSampleCreateComponent(sample, disease, () -> {
		});

		// add pathogen test create components
		addPathogenTests(pathogenTests, labMessageDto, sampleCreateComponent);
		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleCreateComponent, true);

		sampleCreateComponent.addCommitListener(window::close);
		sampleCreateComponent.addDiscardListener(window::close);

		LabMessageUiHelper.establishFinalCommitButtons(sampleCreateComponent);

		return sampleCreateComponent;
	}

	public void addPathogenTests(
			List<PathogenTestDto> pathogenTests,
			LabMessageDto labMessage,
			CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent) {

		SampleController sampleController = ControllerProvider.getSampleController();
		SampleDto sample = sampleCreateComponent.getWrappedComponent().getValue();
		int caseSampleCount = sampleController.caseSampleCountOf(sample);

		List<PathogenTestDto> pathogenTestsToAdd = new ArrayList<>(pathogenTests);
		// always build at least one PathogenTestDto
		if (pathogenTestsToAdd.isEmpty()) {
			pathogenTestsToAdd.add(LabMessageProcessingHelper.buildPathogenTest(null, labMessage, sample, user));
		}

		for (PathogenTestDto pathogenTest : pathogenTestsToAdd) {
			PathogenTestForm pathogenTestCreateComponent =
					sampleController.addPathogenTestComponent(sampleCreateComponent, pathogenTest, caseSampleCount);
			sampleController.setViaLimsFieldChecked(pathogenTestCreateComponent);
		}
	}

	private void updateAddressAndSavePerson(PersonDto personDto, LabMessageDto labMessageDto) {
		if (personDto.getAddress().getCity() == null
				&& personDto.getAddress().getHouseNumber() == null
				&& personDto.getAddress().getPostalCode() == null
				&& personDto.getAddress().getStreet() == null) {
			LabMessageMapper.forLabMessage(labMessageDto).mapToLocation(personDto.getAddress());
		}
		FacadeProvider.getPersonFacade().savePerson(personDto);
	}
}
