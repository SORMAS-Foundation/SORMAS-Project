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

package de.symeda.sormas.ui.externalmessage.labmessage;

import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showCreateCaseWindow;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showFormWithLabMessage;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showMissingDiseaseConfiguration;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showMultipleSamplesPopup;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showPickOrCreateEntryWindow;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showPickOrCreatePersonWindow;
import static de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper.showRelatedForwardedMessageConfirmation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.Registration;
import com.vaadin.ui.HorizontalLayout;
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
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.contact.ContactCreateForm;
import de.symeda.sormas.ui.events.EventDataForm;
import de.symeda.sormas.ui.events.EventParticipantCreateForm;
import de.symeda.sormas.ui.events.eventLink.EventSelectionField;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.AbstractLabMessageProcessingFlow;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.LabMessageProcessingHelper;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.PickOrCreateEventResult;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.PickOrCreateSampleResult;
import de.symeda.sormas.ui.externalmessage.labmessage.processing.SampleAndPathogenTests;
import de.symeda.sormas.ui.externalmessage.processing.EntrySelectionField;
import de.symeda.sormas.ui.externalmessage.processing.ExternalMessageProcessingUIHelper;
import de.symeda.sormas.ui.externalmessage.processing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.samples.humansample.SampleController;
import de.symeda.sormas.ui.samples.humansample.SampleCreateForm;
import de.symeda.sormas.ui.samples.humansample.SampleEditPathogenTestListHandler;
import de.symeda.sormas.ui.samples.humansample.SampleSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Lab message processing flow implemented with vaadin dialogs/components for handling confirmation and object edit/save steps
 */
public class LabMessageProcessingFlow extends AbstractLabMessageProcessingFlow {

	public LabMessageProcessingFlow() {
		super(UserProvider.getCurrent().getUser(), FacadeProvider.getCountryFacade().getServerCountry());
	}

	@Override
	protected CompletionStage<Boolean> handleMissingDisease() {
		return showMissingDiseaseConfiguration();
	}

	@Override
	protected CompletionStage<Boolean> handleRelatedForwardedMessages() {
		return showRelatedForwardedMessageConfirmation();
	}

	@Override
	protected void handlePickOrCreatePerson(PersonDto person, HandlerCallback<PersonDto> callback) {
		showPickOrCreatePersonWindow(person, callback);
	}

	@Override
	protected void handlePickOrCreateEntry(
		List<CaseSelectionDto> similarCases,
		List<SimilarContactDto> similarContacts,
		List<SimilarEventParticipantDto> similarEventParticipants,
		ExternalMessageDto labMessage,
		HandlerCallback<PickOrCreateEntryResult> callback) {

		EntrySelectionField.Options.Builder optionsBuilder = new EntrySelectionField.Options.Builder().addSelectCase(similarCases)
			.addSelectContact(similarContacts)
			.addSelectEventParticipant(similarEventParticipants)
			.addCreateEntry(EntrySelectionField.OptionType.CREATE_CASE)
			.addCreateEntry(EntrySelectionField.OptionType.CREATE_CONTACT)
			.addCreateEntry(EntrySelectionField.OptionType.CREATE_EVENT_PARTICIPANT);

		showPickOrCreateEntryWindow(optionsBuilder.build(), labMessage, callback);
	}

	@Override
	protected void handleCreateCase(CaseDataDto caze, PersonDto person, ExternalMessageDto labMessage, HandlerCallback<CaseDataDto> callback) {
		showCreateCaseWindow(caze, person, labMessage, callback);
	}

	@Override
	public CompletionStage<Boolean> handleMultipleSampleConfirmation() {
		return showMultipleSamplesPopup();
	}

	@Override
	protected void handleCreateSampleAndPathogenTests(
		SampleDto sample,
		List<PathogenTestDto> pathogenTests,
		Disease disease,
		ExternalMessageDto labMessage,
		boolean entityCreated,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback) {

		SampleEditPathogenTestListHandler pathogenTestHandler = new SampleEditPathogenTestListHandler();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			getSampleCreateComponent(sample, lastSample, pathogenTests, labMessage, disease, pathogenTestHandler::addPathogenTest);

		sampleCreateComponent.setPostCommitListener(() -> {
			pathogenTestHandler.saveAll(sample.toReference());

			callback.done(new SampleAndPathogenTests(sampleCreateComponent.getWrappedComponent().getValue(), pathogenTestHandler.getPathogenTests()));
		});
		sampleCreateComponent.addDiscardListener(callback::cancel);

		Window window = VaadinUiUtil.createPopupWindow();
		showFormWithLabMessage(labMessage, sampleCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewSample), entityCreated);
	}

	@Override
	protected void handleCreateContact(ContactDto contact, PersonDto person, ExternalMessageDto labMessage, HandlerCallback<ContactDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			ControllerProvider.getContactController().getContactCreateComponent(null, false, null, true);

		contactCreateComponent.addCommitListener(() -> {
			LabMessageProcessingHelper.updateAddressAndSavePerson(
				FacadeProvider.getPersonFacade().getByUuid(contactCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessage);

			callback.done(contactCreateComponent.getWrappedComponent().getValue());
		});
		contactCreateComponent.addDiscardListener(callback::cancel);

		contactCreateComponent.getWrappedComponent().setValue(contact);
		contactCreateComponent.getWrappedComponent().setPerson(person);

		showFormWithLabMessage(labMessage, contactCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewContact), false);
	}

	@Override
	protected void handlePickOrCreateEvent(ExternalMessageDto labMessage, HandlerCallback<PickOrCreateEventResult> callback) {
		EventSelectionField eventSelect =
			new EventSelectionField(labMessage.getDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForLabMessage), null);
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

		eventSelect.setSelectionChangeCallback(commitAllowed -> component.getCommitButton().setEnabled(commitAllowed));

		window.setContent(component);
		window.setCaption(I18nProperties.getString(Strings.headingPickOrCreateEvent));
		UI.getCurrent().addWindow(window);
	}

	@Override
	protected void handleCreateEvent(EventDto event, HandlerCallback<EventDto> callback) {
		EventDataForm eventCreateForm = new EventDataForm(true, false, true);
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
		ExternalMessageDto labMessage,
		HandlerCallback<EventParticipantDto> callback) {
		Window window = VaadinUiUtil.createPopupWindow();

		EventParticipantCreateForm createForm = new EventParticipantCreateForm(
			event.getEventLocation().getRegion() == null && event.getEventLocation().getDistrict() == null,
			eventParticipant.getPerson().getCreationDate() == null);
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				FacadeProvider.getPersonFacade().save(dto.getPerson());
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
		List<SampleDto> similarSamples,
		List<SampleDto> otherSamples,
		ExternalMessageDto labMessage,
		int sampleReportIndex,
		HandlerCallback<PickOrCreateSampleResult> callback) {

		HorizontalLayout sampleDetailsLayout = new HorizontalLayout();
		sampleDetailsLayout.setSpacing(true);

		SampleReportDto sampleReport = labMessage.getSampleReportsNullSafe().get(sampleReportIndex);

		Date sampleDateTime = sampleReport.getSampleDateTime();
		addLabelIfAvailable(
			sampleDetailsLayout,
			sampleDateTime == null ? null : sampleDateTime.toString(),
			ExternalMessageDto.I18N_PREFIX,
			SampleReportDto.SAMPLE_DATE_TIME);
		addLabelIfAvailable(sampleDetailsLayout, sampleReport.getLabSampleId(), ExternalMessageDto.I18N_PREFIX, SampleReportDto.LAB_SAMPLE_ID);

		SampleMaterial sampleMaterial = sampleReport.getSampleMaterial();
		addLabelIfAvailable(
			sampleDetailsLayout,
			sampleMaterial == null ? null : sampleMaterial.toString(),
			ExternalMessageDto.I18N_PREFIX,
			SampleReportDto.SAMPLE_MATERIAL);

		SampleSelectionField selectField =
			new SampleSelectionField(similarSamples, otherSamples, I18nProperties.getString(Strings.infoPickOrCreateSample), sampleDetailsLayout);

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

		selectField.setSelectionChangeCallback(commitAllowed -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		showFormWithLabMessage(labMessage, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreateSample), false);
	}

	private void addLabelIfAvailable(HorizontalLayout layout, String text, String i18nPrefix, String captionKey) {
		if (StringUtils.isBlank(text)) {
			return;
		}
		Label label = new Label(text);
		label.setCaption(I18nProperties.getPrefixCaption(i18nPrefix, captionKey));
		label.setWidthUndefined();
		layout.addComponent(label);
	}

	@Override
	protected void handleEditSample(
		SampleDto sample,
		List<PathogenTestDto> newPathogenTests,
		ExternalMessageDto labMessage,
		boolean lastSample,
		HandlerCallback<SampleAndPathogenTests> callback) {

		ExternalMessageProcessingUIHelper.showEditSampleWindow(sample, lastSample, newPathogenTests, labMessage, callback::done, callback::cancel);
	}

	private CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
		SampleDto sample,
		boolean lastSample,
		List<PathogenTestDto> pathogenTests,
		ExternalMessageDto externalMessageDto,
		Disease disease,
		Consumer<PathogenTestDto> pathogenTestSaveHandler) {
		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent = sampleController.getSampleCreateComponent(sample, disease, null);

		// add pathogen test create components
		List<PathogenTestDto> pathogenTestsToAdd = new ArrayList<>(pathogenTests);
		// always build at least one PathogenTestDto
		if (pathogenTestsToAdd.isEmpty()) {
			pathogenTestsToAdd.add(LabMessageProcessingHelper.buildPathogenTest(null, externalMessageDto, sample, user));
		}

		ExternalMessageProcessingUIHelper.addNewPathogenTests(pathogenTestsToAdd, sampleCreateComponent, true, pathogenTestSaveHandler, null);

		// add option to create additional pathogen tests
		sampleController.addPathogenTestButton(sampleCreateComponent, true, null, null, pathogenTestSaveHandler);

		LabMessageUiHelper.establishCommitButtons(sampleCreateComponent, lastSample);

		return sampleCreateComponent;
	}

	private void addPathogenTests(
		List<PathogenTestDto> pathogenTests,
		ExternalMessageDto labMessage,
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent,
		Consumer<PathogenTestDto> saveHandler) {

	}
}
