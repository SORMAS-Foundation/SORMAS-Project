package de.symeda.sormas.ui.labmessage;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactDto;
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
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityFacade;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
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
import de.symeda.sormas.ui.samples.PathogenTestSelectionField;
import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.samples.SampleSelectionField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import javax.naming.CannotProceedException;

public class LabMessageController {

	public LabMessageController() {

	}

	public void showLabMessage(String labMessageUuid, Runnable onShare) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);

		Window window = VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));
		LabMessageEditForm form = new LabMessageEditForm(true, newDto.isProcessed(), () -> {
			window.close();
			onShare.run();
		});
		form.setWidth(550, Sizeable.Unit.PIXELS);
		layout.addComponent(form);

		form.setValue(newDto);
	}

	public void processLabMessage(String labMessageUuid) {
		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(labMessageUuid);
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
					List<CaseIndexDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);

					ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
					contactSimilarityCriteria.setPerson(selectedPerson);
					contactSimilarityCriteria.setDisease(labMessageDto.getTestedDisease());
					List<SimilarContactDto> similarContacts = FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);

					EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria();
					eventParticipantCriteria.person(selectedPerson);
					List<SimilarEventParticipantDto> similarEventParticipants =
						FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);

					pickOrCreateEntry(labMessageDto, similarCases, similarContacts, similarEventParticipants, selectedPersonDto);
				}
			}, false);
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
		List<CaseIndexDto> cases,
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
				SampleCriteria criteria = new SampleCriteria();
				criteria.caze(caseDto.toReference());
				criteria.setDisease(caseDto.getDisease());
				List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(caseDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), caseDto.toReference()), labMessageDto, false);
				} else {
					pickOrCreateSample(caseDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(similarEntriesDto.getContact().getUuid());
				SampleCriteria criteria = new SampleCriteria();
				criteria.contact(contactDto.toReference());
				criteria.setDisease(contactDto.getDisease());
				List<SampleDto> samples = FacadeProvider.getSampleFacade().getByContactUuids(Collections.singletonList(contactDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), contactDto.toReference()), labMessageDto, false);
				} else {
					pickOrCreateSample(contactDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getEventParticipant() != null) {
				EventParticipantDto eventParticipantDto =
					FacadeProvider.getEventParticipantFacade().getByUuid(similarEntriesDto.getEventParticipant().getUuid());
				SampleCriteria criteria = new SampleCriteria();
				criteria.eventParticipant(eventParticipantDto.toReference());
				criteria.setDisease(labMessageDto.getTestedDisease());
				List<SampleDto> samples =
					FacadeProvider.getSampleFacade().getByEventParticipantUuids(Collections.singletonList(eventParticipantDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(
						SampleDto.build(UserProvider.getCurrent().getUserReference(), eventParticipantDto.toReference()),
						labMessageDto,
						false);
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
					createEventParticipant(FacadeProvider.getEventFacade().getEventByUuid(eventReferenceDto.getUuid()), labMessageDto, person);
				} else {
					CommitDiscardWrapperComponent<VerticalLayout> commitDiscardWrapperComponent = new CommitDiscardWrapperComponent<>(
						new VerticalLayout(new Label(I18nProperties.getString(Strings.infoEventParticipantAlreadyExisting))));
					commitDiscardWrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionContinue));
					commitDiscardWrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionBack));

					commitDiscardWrapperComponent.addCommitListener(() -> {
						EventParticipantReferenceDto participant =
							FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(selectedEvent.getUuid(), person.getUuid());
						List<SampleDto> samples =
							FacadeProvider.getSampleFacade().getByEventParticipantUuids(Collections.singletonList(participant.getUuid()));
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
		EventParticipantEditForm createForm = new EventParticipantEditForm(eventDto, false);
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
				List<PathogenTestDto> tests = FacadeProvider.getPathogenTestFacade().getAllBySample(sampleDto.toReference());
				if (tests.isEmpty()) {
					createPathogenTest(sampleDto, labMessageDto);
				} else {
					pickOrCreateTest(sampleDto, labMessageDto, tests, samples.size());
				}
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

	private void pickOrCreateTest(SampleDto sampleDto, LabMessageDto labMessageDto, List<PathogenTestDto> tests, int caseSampleCount) {
		PathogenTestSelectionField selectField =
			new PathogenTestSelectionField(tests, I18nProperties.getString(Strings.infoPickOrCreatePathogenTest));

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<PathogenTestSelectionField> selectionField = new CommitDiscardWrapperComponent<>(selectField);
		selectionField.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		selectionField.setWidth(1280, Sizeable.Unit.PIXELS);
		selectionField.addCommitListener(() -> {
			PathogenTestDto testDto = selectField.getValue();
			if (testDto != null) {
				editPatogenTest(sampleDto, testDto, caseSampleCount, labMessageDto.getTestedDisease(), labMessageDto);
			} else {
				createPathogenTest(sampleDto, labMessageDto);
			}
			window.close();
		});
		selectField.setSelectionChangeCallback((commitAllowed) -> selectionField.getCommitButton().setEnabled(commitAllowed));
		selectionField.getCommitButton().setEnabled(false);
		selectionField.addDiscardListener(window::close);

		showFormWithLabMessage(labMessageDto, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreatePathogenTest), false);
	}

	private void editPatogenTest(SampleDto sampleDto, PathogenTestDto testDto, int caseSampleCount, Disease disease, LabMessageDto labMessageDto) {
		BiConsumer<PathogenTestDto, Runnable> onSavedPathogenTest = (pathogenTestDto, callback) -> {
			if (pathogenTestDto != null
				&& pathogenTestDto.getTestResult() != null
				&& Boolean.TRUE.equals(pathogenTestDto.getTestResultVerified())
				&& pathogenTestDto.getTestedDisease() == disease) {
				if (pathogenTestDto.getTestResult() != sampleDto.getPathogenTestResult()) {
					final SampleCreateForm createForm = new SampleCreateForm();
					ControllerProvider.getSampleController()
						.showChangePathogenTestResultWindow(
							new CommitDiscardWrapperComponent<>(createForm),
							sampleDto.getUuid(),
							pathogenTestDto.getTestResult(),
							callback);
				} else {
					callback.run();
				}
			} else {
				callback.run();
			}
		};

		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestEditComponent =
			ControllerProvider.getPathogenTestController().getPathogenTestEditComponent(testDto, caseSampleCount, () -> {
				finishProcessingLabMessage(labMessageDto);
				window.close();
			}, onSavedPathogenTest);

		pathogenTestEditComponent.addDiscardListener(window::close);
		pathogenTestEditComponent.getWrappedComponent().setValue(testDto);

		showFormWithLabMessage(
			labMessageDto,
			pathogenTestEditComponent,
			window,
			I18nProperties.getString(Strings.headingEditPathogenTestResult),
			false);
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
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, true);
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

		caseCreateComponent.getWrappedComponent().getField(PersonDto.FIRST_NAME).setEnabled(false);
		caseCreateComponent.getWrappedComponent().getField(PersonDto.LAST_NAME).setEnabled(false);
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
		ContactDto contactDto = ContactDto.build(null, labMessageDto.getTestedDisease(), null);
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

		contactCreateComponent.getWrappedComponent().getField(PersonDto.FIRST_NAME).setEnabled(false);
		contactCreateComponent.getWrappedComponent().getField(PersonDto.LAST_NAME).setEnabled(false);
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
		sampleDto.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		sampleDto.setLab(getLabReference(labMessageDto));
		sampleDto.setLabDetails(labMessageDto.getTestLabName());
	}

	private FacilityReferenceDto getLabReference(LabMessageDto labMessageDto) {
		FacilityFacade facilityFacade = FacadeProvider.getFacilityFacade();
		List<FacilityReferenceDto> labs = facilityFacade.getByExternalIdAndType(labMessageDto.getTestLabExternalId(), FacilityType.LABORATORY, false);
		if (labs != null && labs.size() == 1) {
			return labs.get(0);
		} else {
			return facilityFacade.getFacilityReferenceByUuid(FacilityDto.OTHER_FACILITY_UUID);
		}
	}

	private CommitDiscardWrapperComponent<SampleCreateForm> getSampleCreateComponent(
		SampleDto sampleDto,
		LabMessageDto labMessageDto,
		Window window) {
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			ControllerProvider.getSampleController().getSampleCreateComponent(sampleDto, () -> {
			});

		CheckBox includeTestCheckbox = sampleCreateComponent.getWrappedComponent().getField(Captions.sampleIncludeTestOnCreation);
		includeTestCheckbox.setValue(Boolean.TRUE);
		includeTestCheckbox.setEnabled(false);

		CheckBox viaLimsCheckbox = sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.VIA_LIMS);
		if (viaLimsCheckbox != null) {
			viaLimsCheckbox.setValue(Boolean.TRUE);
			viaLimsCheckbox.setEnabled(false);
		}

		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_RESULT)).setValue(labMessageDto.getTestResult());
		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_TYPE)).setValue(labMessageDto.getTestType());
		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TESTED_DISEASE)).setValue(labMessageDto.getTestedDisease());
		((NullableOptionGroup) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_RESULT_VERIFIED))
			.setValue(labMessageDto.isTestResultVerified());
		((DateTimeField) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_DATE_TIME))
			.setValue(labMessageDto.getTestDateTime());
		((DateField) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.REPORT_DATE)).setValue(labMessageDto.getMessageDateTime());

		sampleCreateComponent.addCommitListener(() -> {
			window.close();
			finishProcessingLabMessage(labMessageDto);
		});
		sampleCreateComponent.addDiscardListener(window::close);
		return sampleCreateComponent;
	}

	private void createPathogenTest(SampleDto sampleDto, LabMessageDto labMessageDto) {
		PathogenTestDto pathogenTestDto = buildPathogenTest(sampleDto, labMessageDto);
		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestCreateComponent =
			getPathogenTestCreateComponent(sampleDto, labMessageDto, pathogenTestDto, window);
		showFormWithLabMessage(
			labMessageDto,
			pathogenTestCreateComponent,
			window,
			I18nProperties.getString(Strings.headingCreatePathogenTestResult),
			false);
	}

	private PathogenTestDto buildPathogenTest(SampleDto sampleDto, LabMessageDto labMessageDto) {
		PathogenTestDto pathogenTestDto = PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser());
		pathogenTestDto.setTestType(labMessageDto.getTestType());
		pathogenTestDto.setTestedDisease(labMessageDto.getTestedDisease());
		pathogenTestDto.setTestDateTime(labMessageDto.getTestDateTime());
		pathogenTestDto.setTestResultText(labMessageDto.getTestResultText());
		return pathogenTestDto;
	}

	private CommitDiscardWrapperComponent<PathogenTestForm> getPathogenTestCreateComponent(
		SampleDto sampleDto,
		LabMessageDto labMessageDto,
		PathogenTestDto pathogenTestDto,
		Window window) {
		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestCreateComponent =
			ControllerProvider.getPathogenTestController().getPathogenTestCreateComponent(sampleDto.toReference(), 0, () -> {
				window.close();
				finishProcessingLabMessage(labMessageDto);
			}, null);
		pathogenTestCreateComponent.addDiscardListener(window::close);
		pathogenTestCreateComponent.getWrappedComponent().setValue(pathogenTestDto);
		return pathogenTestCreateComponent;
	}

	private void showFormWithLabMessage(
		LabMessageDto labMessageDto,
		CommitDiscardWrapperComponent createComponent,
		Window window,
		String heading,
		boolean entityCreated) {

		addProcessedInMeantimeCheck(createComponent, labMessageDto, entityCreated);
		LabMessageEditForm form = new LabMessageEditForm(true, labMessageDto.isProcessed(), null);
		form.setValue(labMessageDto);
		form.setWidth(550, Sizeable.Unit.PIXELS);
		HorizontalLayout layout = new HorizontalLayout(form, createComponent);
		layout.setMargin(true);
		window.setContent(layout);
		window.setCaption(heading);
		UI.getCurrent().addWindow(window);
	}

	private void finishProcessingLabMessage(LabMessageDto labMessageDto) {
		labMessageDto.setProcessed(true);
		FacadeProvider.getLabMessageFacade().save(labMessageDto);
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
				return ButtonHelper.createButton(
					Captions.labMessage_deleteNewlyCreatedCase,
					e -> FacadeProvider.getCaseFacade().deleteCase(sample.getAssociatedCase().getUuid()),
					ValoTheme.BUTTON_PRIMARY);
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

	private void addProcessedInMeantimeCheck(CommitDiscardWrapperComponent createComponent, LabMessageDto labMessageDto, boolean entityCreated) {
		createComponent.setPrimaryCommitListener(() -> {
			if (FacadeProvider.getLabMessageFacade().isProcessed(labMessageDto.getUuid())) {
				createComponent.getCommitButton().setEnabled(false);
				showAlreadyProcessedPopup(createComponent.getWrappedComponent(), entityCreated);
				throw new CannotProceedException("The lab message was processed in the meantime");
			}
		});
	}
}
