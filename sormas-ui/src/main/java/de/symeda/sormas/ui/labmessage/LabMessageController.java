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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.CannotProceedException;
import javax.naming.NamingException;

import de.symeda.sormas.ui.samples.SampleController;
import de.symeda.sormas.ui.samples.SampleEditForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.labmessage.ExternalMessageResult;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageIndexDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleSimilarityCriteria;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseCreateForm;
import de.symeda.sormas.ui.contact.ContactCreateForm;
import de.symeda.sormas.ui.events.EventDataForm;
import de.symeda.sormas.ui.events.EventParticipantEditForm;
import de.symeda.sormas.ui.events.eventLink.EventSelectionField;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.samples.SampleSelectionField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public LabMessageController() {

	}

	public void showLabMessage(String labMessageUuid, Runnable onFormActionPerformed) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid, false);
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		Window window = VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));

		LabMessageForm form = new LabMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);
		layout.addComponent(form);

		if (newDto.getStatus().isProcessable()) {
			layout.addStyleName("lab-message-processable");
			layout.addComponent(getLabMessageButtonsPanel(newDto, () -> {
				window.close();
				onFormActionPerformed.run();
			}));
		} else {
			layout.addStyleName("lab-message-not-processable");
		}

		form.setValue(newDto);
	}

	public void showLabMessagesSlider(List<LabMessageDto> labMessages) {
		new LabMessageSlider(labMessages);
	}

	public void processLabMessage(String labMessageUuid) {
		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid, false);
		final PersonDto personDto = buildPerson(labMessageDto);

		if (FacadeProvider.getLabMessageFacade().isProcessed(labMessageUuid)) {
			showAlreadyProcessedPopup(null, false);
			return;
		}
		ControllerProvider.getPersonController()
			.selectOrCreatePerson(personDto, I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessage), selectedPerson -> {
				if (FacadeProvider.getLabMessageFacade().isProcessed(labMessageUuid)) {
					showAlreadyProcessedPopup(null, false); // it currently is not possible to delete persons, so no reversion is provided here.
					return;
				}
				if (selectedPerson != null) {
					PersonDto selectedPersonDto;
					if (selectedPerson.getUuid().equals(personDto.getUuid())) {
						selectedPersonDto = personDto;
					} else {
						selectedPersonDto = FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid());
					}

					CaseCriteria caseCriteria = new CaseCriteria();
					caseCriteria.person(selectedPersonDto.toReference());
					caseCriteria.disease(labMessageDto.getTestedDisease());
					CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
					caseSimilarityCriteria.caseCriteria(caseCriteria);
					caseSimilarityCriteria.personUuid(selectedPerson.getUuid());
					List<CaseSelectionDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);

					ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
					contactSimilarityCriteria.setPerson(selectedPerson);
					contactSimilarityCriteria.setDisease(labMessageDto.getTestedDisease());
					List<SimilarContactDto> similarContacts = FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);

					EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
					eventParticipantCriteria.setPerson(selectedPerson);
					eventParticipantCriteria.setDisease(labMessageDto.getTestedDisease());
					List<SimilarEventParticipantDto> similarEventParticipants =
						FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);

					pickOrCreateEntry(labMessageDto, similarCases, similarContacts, similarEventParticipants, selectedPersonDto);
				}
			}, false);
	}

	public void deleteAllSelectedItems(Collection<LabMessageIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoLabMessagesSelected),
				I18nProperties.getString(Strings.messageNoLabMessagesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else if (selectedRows.stream().anyMatch(m -> m.getStatus() == LabMessageStatus.PROCESSED)) {
			new Notification(
				I18nProperties.getString(Strings.headingLabMessagesDeleteProcessed),
				I18nProperties.getString(Strings.messageLabMessagesDeleteProcessed),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteLabMessages), selectedRows.size()),
				() -> {
					FacadeProvider.getLabMessageFacade()
						.deleteLabMessages(selectedRows.stream().map(LabMessageIndexDto::getUuid).collect(Collectors.toList()));
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingLabMessagesDeleted),
						I18nProperties.getString(Strings.messageLabMessagesDeleted),
						Notification.Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	private PersonDto buildPerson(LabMessageDto labMessageDto) {
		final PersonDto personDto = PersonDto.build();
		personDto.setFirstName(labMessageDto.getPersonFirstName());
		personDto.setLastName(labMessageDto.getPersonLastName());
		personDto.setBirthdateDD(labMessageDto.getPersonBirthDateDD());
		personDto.setBirthdateMM(labMessageDto.getPersonBirthDateMM());
		personDto.setBirthdateYYYY(labMessageDto.getPersonBirthDateYYYY());
		personDto.setSex(labMessageDto.getPersonSex());
		personDto.setPhone(labMessageDto.getPersonPhone());
		personDto.setEmailAddress(labMessageDto.getPersonEmail());
		return personDto;
	}

	private void pickOrCreateEntry(
		LabMessageDto labMessageDto,
		List<CaseSelectionDto> cases,
		List<SimilarContactDto> contacts,
		List<SimilarEventParticipantDto> eventParticipants,
		PersonDto person) {
		EntrySelectionField selectField = new EntrySelectionField(labMessageDto, cases, contacts, eventParticipants);

		final CommitDiscardWrapperComponent<EntrySelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);

		selectionField.addCommitListener(() -> {
			if (FacadeProvider.getLabMessageFacade().isProcessed(labMessageDto.getUuid())) {
				showAlreadyProcessedPopup(null, false);
				return;
			}
			SimilarEntriesDto similarEntriesDto = selectField.getValue();
			if (similarEntriesDto.isNewCase()) {
				createCase(labMessageDto, person);
			} else if (similarEntriesDto.isNewContact()) {
				createContact(labMessageDto, person);
			} else if (similarEntriesDto.isNewEventParticipant()) {
				pickOrCreateEvent(labMessageDto, person);
			} else if (similarEntriesDto.getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(similarEntriesDto.getCaze().getUuid());
				CaseReferenceDto cazeRef = caseDto.toReference();

				List<SampleDto> samples = FacadeProvider.getSampleFacade().getSimilarSamples(createSampleCriteria(labMessageDto).caze(cazeRef));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), cazeRef), labMessageDto, false);
				} else {
					pickOrCreateSample(caseDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(similarEntriesDto.getContact().getUuid());
				ContactReferenceDto contactRef = contactDto.toReference();

				List<SampleDto> samples = FacadeProvider.getSampleFacade().getSimilarSamples(createSampleCriteria(labMessageDto).contact(contactRef));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), contactRef), labMessageDto, false);
				} else {
					pickOrCreateSample(contactDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getEventParticipant() != null) {
				EventParticipantDto eventParticipantDto =
					FacadeProvider.getEventParticipantFacade().getByUuid(similarEntriesDto.getEventParticipant().getUuid());
				EventParticipantReferenceDto eventParticipantRef = eventParticipantDto.toReference();

				List<SampleDto> samples =
					FacadeProvider.getSampleFacade().getSimilarSamples(createSampleCriteria(labMessageDto).eventParticipant(eventParticipantRef));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), eventParticipantRef), labMessageDto, false);
				} else {
					pickOrCreateSample(eventParticipantDto, labMessageDto, samples);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		});

		selectField.setSelectionChangeCallback((commitAllowed) -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);

		VaadinUiUtil.showModalPopupWindow(selectionField, I18nProperties.getString(Strings.headingPickOrCreateEntry));
	}

	private SampleSimilarityCriteria createSampleCriteria(LabMessageDto labMessageDto) {
		SampleSimilarityCriteria sampleCriteria = new SampleSimilarityCriteria();
		sampleCriteria.setLabSampleId(labMessageDto.getLabSampleId());
		sampleCriteria.setSampleDateTime(labMessageDto.getSampleDateTime());
		sampleCriteria.setSampleMaterial(labMessageDto.getSampleMaterial());
		return sampleCriteria;
	}

	private void pickOrCreateEvent(LabMessageDto labMessageDto, PersonDto person) {
		EventSelectionField eventSelect =
			new EventSelectionField(labMessageDto.getTestedDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForLabMessage));
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {

				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.setPerson(person.toReference());
				eventCriteria.setUserFilterIncluded(false);
				List<EventIndexDto> eventIndexDtos = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				if (!eventIndexDtos.contains(selectedEvent)) {
					createEventParticipant(FacadeProvider.getEventFacade().getEventByUuid(eventReferenceDto.getUuid(), false), labMessageDto, person);
				} else {
					CommitDiscardWrapperComponent<VerticalLayout> commitDiscardWrapperComponent = new CommitDiscardWrapperComponent<>(
						new VerticalLayout(new Label(I18nProperties.getString(Strings.infoEventParticipantAlreadyExisting))));
					commitDiscardWrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionContinue));
					commitDiscardWrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionBack));

					commitDiscardWrapperComponent.addCommitListener(() -> {
						EventParticipantReferenceDto participant =
							FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(selectedEvent.getUuid(), person.getUuid());
						List<SampleDto> samples =
							FacadeProvider.getSampleFacade().getSimilarSamples(createSampleCriteria(labMessageDto).eventParticipant(participant));
						if (samples.isEmpty()) {
							createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), participant), labMessageDto, false);
						} else {
							pickOrCreateSample(FacadeProvider.getEventParticipantFacade().getByUuid(participant.getUuid()), labMessageDto, samples);
						}
					});
					commitDiscardWrapperComponent.addDiscardListener(() -> pickOrCreateEvent(labMessageDto, person));
					VaadinUiUtil.showModalPopupWindow(commitDiscardWrapperComponent, I18nProperties.getCaption(Captions.info));
				}
			} else {
				createEvent(labMessageDto, person);
			}
			window.close();
		});

		component.addDiscardListener(window::close);

		eventSelect.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));

		window.setContent(component);
		window.setCaption(I18nProperties.getString(Strings.headingPickOrCreateEvent));
		UI.getCurrent().addWindow(window);
	}

	private void createEvent(LabMessageDto labMessageDto, PersonDto person) {

		EventDataForm eventCreateForm = new EventDataForm(true, false);
		eventCreateForm.setValue(ControllerProvider.getEventController().createNewEvent(labMessageDto.getTestedDisease()));
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		Window window = VaadinUiUtil.createPopupWindow();
		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().saveEvent(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Notification.Type.WARNING_MESSAGE);

				createEventParticipant(dto, labMessageDto, person);
				window.close();
			}
		});
		editView.addDiscardListener(window::close);

		window.setContent(editView);
		window.setCaption(I18nProperties.getString(Strings.headingCreateNewEvent));
		UI.getCurrent().addWindow(window);
	}

	private void createEventParticipant(EventDto eventDto, LabMessageDto labMessageDto, PersonDto person) {
		EventParticipantDto eventParticipant = buildEventParticipant(eventDto, person);
		Window window = VaadinUiUtil.createPopupWindow();
		final CommitDiscardWrapperComponent<EventParticipantEditForm> createComponent =
			getEventParticipantEditForm(eventDto, labMessageDto, eventParticipant, window);
		showFormWithLabMessage(labMessageDto, createComponent, window, I18nProperties.getString(Strings.headingCreateNewEventParticipant), false);
	}

	private CommitDiscardWrapperComponent<EventParticipantEditForm> getEventParticipantEditForm(
		EventDto eventDto,
		LabMessageDto labMessageDto,
		EventParticipantDto eventParticipant,
		Window window) {
		EventParticipantEditForm createForm = new EventParticipantEditForm(eventDto, false, eventParticipant.getPerson().isPseudonymized());
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				FacadeProvider.getPersonFacade().savePerson(dto.getPerson());
				EventParticipantDto savedDto = FacadeProvider.getEventParticipantFacade().saveEventParticipant(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Notification.Type.ASSISTIVE_NOTIFICATION);
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), savedDto.toReference()), labMessageDto, true);
				window.close();
			}
		});
		createComponent.addDiscardListener(window::close);
		return createComponent;
	}

	private EventParticipantDto buildEventParticipant(EventDto eventDto, PersonDto person) {
		EventParticipantDto eventParticipant = EventParticipantDto.build(eventDto.toReference(), UserProvider.getCurrent().getUserReference());
		eventParticipant.setPerson(person);
		return eventParticipant;
	}

	private void savePerson(PersonDto personDto, LabMessageDto labMessageDto) {
		if (personDto.getAddress().getCity() == null
			&& personDto.getAddress().getHouseNumber() == null
			&& personDto.getAddress().getPostalCode() == null
			&& personDto.getAddress().getStreet() == null) {
			personDto.getAddress().setStreet(labMessageDto.getPersonStreet());
			personDto.getAddress().setHouseNumber(labMessageDto.getPersonHouseNumber());
			personDto.getAddress().setPostalCode(labMessageDto.getPersonPostalCode());
			personDto.getAddress().setCity(labMessageDto.getPersonCity());
		}
		FacadeProvider.getPersonFacade().savePerson(personDto);
	}

	private void pickOrCreateSample(PseudonymizableDto dto, LabMessageDto labMessageDto, List<SampleDto> samples) {
		SampleSelectionField selectField = new SampleSelectionField(samples, I18nProperties.getString(Strings.infoPickOrCreateSample));

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<SampleSelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);
		selectionField.addCommitListener(() -> {
			SampleDto sampleDto = selectField.getValue();
			if (sampleDto != null) {
				editSample(sampleDto, labMessageDto);
			} else if (CaseDataDto.class.equals(dto.getClass())) {
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), ((CaseDataDto) dto).toReference()), labMessageDto, false);
			} else if (ContactDto.class.equals(dto.getClass())) {
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), ((ContactDto) dto).toReference()), labMessageDto, false);
			} else if (EventParticipantDto.class.equals(dto.getClass())) {
				createSample(
					SampleDto.build(UserProvider.getCurrent().getUserReference(), ((EventParticipantDto) dto).toReference()),
					labMessageDto,
					false);
			}
			window.close();
		});
		selectField.setSelectionChangeCallback((commitAllowed) -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);
		selectionField.addDiscardListener(window::close);

		showFormWithLabMessage(labMessageDto, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreateSample), false);
	}

	private void editSample(SampleDto sample, LabMessageDto labMessage) {

		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleEditForm> sampleEditComponent =
			sampleController.getSampleEditComponent(sample.getUuid(), sample.isPseudonymized());

		// add existing tests to edit component
		int caseSampleCount = FacadeProvider.getSampleFacade().caseSampleCountOf(sample);

		List<PathogenTestDto> existingTests = FacadeProvider.getPathogenTestFacade().getAllBySample(sample.toReference());
		for (PathogenTestDto existingTest : existingTests) {
			sampleController.addPathogenTestComponent(sampleEditComponent, existingTest, caseSampleCount, true);
		}

		// add newly submitted tests to sample edit component
		List<String> existingTestExternalIds = existingTests.stream().map(t -> t.getExternalId()).collect(Collectors.toList());
		while (existingTestExternalIds.remove(null)); // otherwise tests without external id will seen as match

		List<PathogenTestDto> newTests = buildPathogenTests(sample, labMessage);

		for (PathogenTestDto test : newTests) {
			if (!existingTestExternalIds.contains(test.getExternalId())) {
				PathogenTestForm form = sampleController.addPathogenTestComponent(sampleEditComponent, test, caseSampleCount, true);
				setViaLimsFieldCheckedAndDisabled(form);
			}
		}

		Window window = VaadinUiUtil.createPopupWindow();
		sampleEditComponent.addCommitListener(window::close);
		sampleEditComponent.addDiscardListener(window::close);

		showFormWithLabMessage(labMessage, sampleEditComponent, window, I18nProperties.getString(Strings.headingEditSample), false);

	}

	private void createCase(LabMessageDto labMessageDto, PersonDto person) {
		Window window = VaadinUiUtil.createPopupWindow();
		CaseDataDto caseDto = buildCase(labMessageDto, person);
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(labMessageDto, person, window, caseDto);
		showFormWithLabMessage(labMessageDto, caseCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewCase), false);
	}

	private CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(
		LabMessageDto labMessageDto,
		PersonDto person,
		Window window,
		CaseDataDto caseDto) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, null, true);
		caseCreateComponent.addCommitListener(() -> {
			savePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(caseCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessageDto);
			createSample(
				SampleDto.build(UserProvider.getCurrent().getUserReference(), caseCreateComponent.getWrappedComponent().getValue().toReference()),
				labMessageDto,
				true);
			window.close();
		});
		caseCreateComponent.addDiscardListener(window::close);
		caseCreateComponent.getWrappedComponent().setValue(caseDto);
		caseCreateComponent.getWrappedComponent().setPerson(person);

		return caseCreateComponent;
	}

	private CaseDataDto buildCase(LabMessageDto labMessageDto, PersonDto person) {
		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), labMessageDto.getTestedDisease());
		caseDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		return caseDto;
	}

	private void createContact(LabMessageDto labMessageDto, PersonDto person) {
		Window window = VaadinUiUtil.createPopupWindow();
		ContactDto contactDto = buildContact(labMessageDto, person);
		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			getContactCreateComponent(labMessageDto, person, window, contactDto);
		showFormWithLabMessage(labMessageDto, contactCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewContact), false);
	}

	private ContactDto buildContact(LabMessageDto labMessageDto, PersonDto person) {
		ContactDto contactDto = ContactDto.build(null, labMessageDto.getTestedDisease(), null, null);
		contactDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		contactDto.setPerson(person.toReference());
		return contactDto;
	}

	private CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(
		LabMessageDto labMessageDto,
		PersonDto person,
		Window window,
		ContactDto contactDto) {
		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			ControllerProvider.getContactController().getContactCreateComponent(null, false, null, true);

		contactCreateComponent.addCommitListener(() -> {
			savePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(contactCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessageDto);
			createSample(
				SampleDto.build(UserProvider.getCurrent().getUserReference(), contactCreateComponent.getWrappedComponent().getValue().toReference()),
				labMessageDto,
				true);
			window.close();
		});
		contactCreateComponent.addDiscardListener(window::close);
		contactCreateComponent.getWrappedComponent().setValue(contactDto);
		contactCreateComponent.getWrappedComponent().setPerson(person);

		return contactCreateComponent;
	}

	private void createSample(SampleDto sampleDto, LabMessageDto labMessageDto, boolean newEntityCreated) {
		fillSample(sampleDto, labMessageDto);
		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent = getSampleCreateComponent(sampleDto, labMessageDto, window);
		showFormWithLabMessage(
			labMessageDto,
			sampleCreateComponent,
			window,
			I18nProperties.getString(Strings.headingCreateNewSample),
			newEntityCreated);
	}

	private void fillSample(SampleDto sampleDto, LabMessageDto labMessageDto) {
		sampleDto.setSampleDateTime(labMessageDto.getSampleDateTime());
		if (labMessageDto.getSampleReceivedDate() != null) {
			sampleDto.setReceived(true);
			sampleDto.setReceivedDate(labMessageDto.getSampleReceivedDate());
			sampleDto.setLabSampleID(labMessageDto.getLabSampleId());
		}
		sampleDto.setSampleMaterial(labMessageDto.getSampleMaterial());
		sampleDto.setSampleMaterialText(labMessageDto.getSampleMaterialText());
		sampleDto.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		sampleDto.setLab(getLabReference(labMessageDto));
		sampleDto.setLabDetails(labMessageDto.getLabName());
		if (FacadeProvider.getLabMessageFacade().homogenousTestResultTypesIn(labMessageDto)) {
			sampleDto.setPathogenTestResult(labMessageDto.getTestReports().get(0).getTestResult());
		}
	}

	private FacilityReferenceDto getLabReference(LabMessageDto labMessageDto) {
		FacilityFacade facilityFacade = FacadeProvider.getFacilityFacade();
		List<FacilityReferenceDto> labs = labMessageDto.getLabExternalId() != null
			? facilityFacade.getByExternalIdAndType(labMessageDto.getLabExternalId(), FacilityType.LABORATORY, false)
			: null;
		if (labs != null && labs.size() == 1) {
			return labs.get(0);
		} else {
			return facilityFacade.getFacilityReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);
		}
	}

	private CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(SampleDto sample, LabMessageDto labMessageDto, Window window) {
		SampleController sampleController = ControllerProvider.getSampleController();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent = sampleController.getSampleCreateComponent(sample, () -> {
		});

		sampleCreateComponent.addDoneListener(() -> finishProcessingLabMessage(labMessageDto, sample));

		// add pathogen test create components
		//********************
		List<PathogenTestDto> pathogenTests = buildPathogenTests(sample, labMessageDto);
		int caseSampleCount = FacadeProvider.getSampleFacade().caseSampleCountOf(sample);
		// the first pathogenTestCreateComponent must not be removable. It is expected to exist because the buildPathogenTests shall always return at least one.
		PathogenTestForm pathogenTestCreateComponent =
			sampleController.addPathogenTestComponent(sampleCreateComponent, pathogenTests.get(0), caseSampleCount, false);
		setViaLimsFieldCheckedAndDisabled(pathogenTestCreateComponent);
		// all other pathogen test create components may be removed.
		for (PathogenTestDto pathogenTest : pathogenTests.stream().skip(1).collect(Collectors.toList())) {
			pathogenTestCreateComponent = sampleController.addPathogenTestComponent(sampleCreateComponent, pathogenTest, caseSampleCount, true);
			setViaLimsFieldCheckedAndDisabled(pathogenTestCreateComponent);
		}

		sampleCreateComponent.addCommitListener(window::close);
		sampleCreateComponent.addDiscardListener(window::close);
		return sampleCreateComponent;
	}

	private void setViaLimsFieldCheckedAndDisabled(PathogenTestForm pathogenTestForm) {
		CheckBox viaLimsCheckbox = pathogenTestForm.getField(PathogenTestDto.VIA_LIMS);
		viaLimsCheckbox.setValue(Boolean.TRUE);
		viaLimsCheckbox.setEnabled(false);
	}

	private List<PathogenTestDto> buildPathogenTests(SampleDto sample, LabMessageDto labMessage) {
		ArrayList pathogenTests = new ArrayList();
		Disease disease = labMessage.getTestedDisease();
		Date reportDate = null;

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			reportDate = labMessage.getMessageDateTime();
		}
		for (TestReportDto testReport : labMessage.getTestReports()) {
			pathogenTests.add(buildPathogenTest(sample, testReport, disease, reportDate));
		}
		// always build at least one PathogenTestDto
		if (pathogenTests.isEmpty()) {
			pathogenTests.add(buildPathogenTest(sample, null, disease, reportDate));
		}
		return pathogenTests;
	}

	private PathogenTestDto buildPathogenTest(SampleDto sample, TestReportDto testReport, Disease testedDisease, Date reportDate) {
		PathogenTestDto pathogenTest = PathogenTestDto.build(sample, UserProvider.getCurrent().getUser());
		if (testReport != null) {
			migrateAttributes(testReport, pathogenTest);
		}
		pathogenTest.setTestedDisease(testedDisease);

		pathogenTest.setReportDate(reportDate);
		return pathogenTest;
	}

	private void migrateAttributes(TestReportDto source, PathogenTestDto target) {
		target.setTestResult(source.getTestResult());
		target.setTestType(source.getTestType());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResultText(source.getTestResultText());
		target.setTypingId(source.getTypingId());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
	}

	private void showFormWithLabMessage(
		LabMessageDto labMessageDto,
		CommitDiscardWrapperComponent<? extends Component> createComponent,
		Window window,
		String heading,
		boolean entityCreated) {

		addProcessedInMeantimeCheck(createComponent, labMessageDto, entityCreated);
		LabMessageForm form = new LabMessageForm();
		form.setWidth(550, Sizeable.Unit.PIXELS);

		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
		horizontalSplitPanel.setFirstComponent(form);
		horizontalSplitPanel.setSecondComponent(createComponent);
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

		form.setValue(labMessageDto);
	}

	private void finishProcessingLabMessage(LabMessageDto labMessage, SampleDto sample) {
		labMessage.setSample(sample.toReference());
		labMessage.setStatus(LabMessageStatus.PROCESSED);
		FacadeProvider.getLabMessageFacade().save(labMessage);
		SormasUI.get().getNavigator().navigateTo(LabMessagesView.VIEW_NAME);
	}

	/**
	 *
	 * @param component
	 *            that holds a reference to the current state of processing a labMessage
	 * @param entityCreated
	 *            should be true if a Case, Contact or EventParticipant has already been created. This will result in an option to delete
	 *            that entity again.
	 */
	private void showAlreadyProcessedPopup(Component component, boolean entityCreated) {
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
	 *
	 * @param component
	 *            component is expected to not be null, as it should never be null in a correct call of this method. Calling this method
	 *            with a null component will result in a NPE.
	 * @return Button to delete the formerly created Case, Contact or EventParticipant entity
	 */
	private Button createDeleteEntityButton(Component component) {
		if (SampleCreateForm.class.equals(component.getClass())) {
			SampleDto sample = ((SampleCreateForm) component).getValue();
			if (sample.getAssociatedCase() != null) {
				return ButtonHelper.createButton(Captions.labMessage_deleteNewlyCreatedCase, e -> {
					try {
						FacadeProvider.getCaseFacade().deleteCase(sample.getAssociatedCase().getUuid());
					} catch (ExternalSurveillanceToolException survToolException) {
						// should not happen because the new case was not shared
						throw new RuntimeException(survToolException);
					}
				}, ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedContact() != null) {
				return ButtonHelper.createButton(
					Captions.labMessage_deleteNewlyCreatedContact,
					e -> FacadeProvider.getContactFacade().deleteContact(sample.getAssociatedContact().getUuid()),
					ValoTheme.BUTTON_PRIMARY);
			} else if (sample.getAssociatedEventParticipant() != null) {
				return ButtonHelper.createButton(
					Captions.labMessage_deleteNewlyCreatedEventParticipant,
					e -> FacadeProvider.getEventParticipantFacade().deleteEventParticipant(sample.getAssociatedEventParticipant()),
					ValoTheme.BUTTON_PRIMARY);
			}
		}
		throw new UnsupportedOperationException("The created entity to be deleted could net be determined.");
	}

	private void addProcessedInMeantimeCheck(
		CommitDiscardWrapperComponent<? extends Component> createComponent,
		LabMessageDto labMessageDto,
		boolean entityCreated) {
		createComponent.setPrimaryCommitListener(() -> {
			if (FacadeProvider.getLabMessageFacade().isProcessed(labMessageDto.getUuid())) {
				createComponent.getCommitButton().setEnabled(false);
				showAlreadyProcessedPopup(createComponent.getWrappedComponent(), entityCreated);
				throw new CannotProceedException("The lab message was processed in the meantime");
			}
		});
	}

	private HorizontalLayout getLabMessageButtonsPanel(LabMessageDto labMessage, Runnable callback) {
		HorizontalLayout buttonsPanel = new HorizontalLayout();
		buttonsPanel.setMargin(false);
		buttonsPanel.setSpacing(true);

		Button deleteButton = ButtonHelper.createButton(Captions.actionDelete, I18nProperties.getCaption(Captions.actionDelete), (e) -> {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getCaption(Captions.LabMessage)),
				() -> {
					if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
						showAlreadyProcessedPopup(null, false);
					} else {
						FacadeProvider.getLabMessageFacade().deleteLabMessage(labMessage.getUuid());
						callback.run();
					}
				});
		}, ValoTheme.BUTTON_DANGER, CssStyles.BUTTON_BORDER_NEUTRAL);

		buttonsPanel.addComponent(deleteButton);

		Button unclearButton =
			ButtonHelper.createButton(Captions.actionUnclearLabMessage, I18nProperties.getCaption(Captions.actionUnclearLabMessage), (e) -> {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingConfirmUnclearLabMessage),
					new Label(I18nProperties.getString(Strings.confirmationUnclearLabMessage)),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					null,
					(confirmed) -> {
						if (confirmed) {
							if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
								showAlreadyProcessedPopup(null, false);
							} else {
								labMessage.setStatus(LabMessageStatus.UNCLEAR);
								FacadeProvider.getLabMessageFacade().save(labMessage);
								callback.run();
							}
						}
					});
			});

		buttonsPanel.addComponent(unclearButton);

		Button forwardButton = ButtonHelper
			.createButton(Captions.actionManualForwardLabMessage, I18nProperties.getCaption(Captions.actionManualForwardLabMessage), (e) -> {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingConfirmManuallyForwardedLabMessage),
					new Label(I18nProperties.getString(Strings.confirmationManuallyForwardedLabMessage)),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					null,
					(confirmed) -> {
						if (confirmed) {
							if (FacadeProvider.getLabMessageFacade().isProcessed(labMessage.getUuid())) {
								showAlreadyProcessedPopup(null, false);
							} else {
								labMessage.setStatus(LabMessageStatus.FORWARDED);
								FacadeProvider.getLabMessageFacade().save(labMessage);
								callback.run();
							}
						}
					});
			});

		buttonsPanel.addComponent(forwardButton);

		if (FacadeProvider.getSormasToSormasFacade().isSharingLabMessagesEnabledForUser()) {
			Button shareButton = ButtonHelper.createIconButton(Captions.sormasToSormasSendLabMessage, VaadinIcons.SHARE, (e) -> {
				ControllerProvider.getSormasToSormasController().shareLabMessage(labMessage, callback);
			});

			buttonsPanel.addComponent(shareButton);
		}

		return buttonsPanel;
	}

	public Optional<byte[]> convertToPDF(String labMessageUuid) {

		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid, false);

		try {
			ExternalMessageResult<byte[]> result = FacadeProvider.getExternalLabResultsFacade().convertToPDF(labMessageDto);

			if (result.isSuccess()) {
				return Optional.of(result.getValue());
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingLabMessageDownload),
					result.getError(),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}

		} catch (NamingException e) {
			new Notification(
				I18nProperties.getString(Strings.headingLabMessageDownload),
				I18nProperties.getString(Strings.messageExternalLabResultsAdapterNotFound),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			logger.error(e.getMessage());
		}
		return Optional.empty();
	}
}
