package de.symeda.sormas.ui.labmessage;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;

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
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
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
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageController {

	public LabMessageController() {

	}

	public void show(String uuid) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(uuid);

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setWidth(550, Sizeable.Unit.PIXELS);
		VerticalLayout layout = new VerticalLayout(form);
		layout.setMargin(true);
		VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));
		form.setValue(newDto);
	}

	public void process(String uuid) {
		LabMessageDto labMessageDto = FacadeProvider.getLabMessageFacade().getByUuid(uuid);
		final PersonDto personDto = PersonDto.build();
		personDto.setFirstName(labMessageDto.getPersonFirstName());
		personDto.setLastName(labMessageDto.getPersonLastName());
		personDto.setBirthdateDD(labMessageDto.getPersonBirthDateDD());
		personDto.setBirthdateMM(labMessageDto.getPersonBirthDateMM());
		personDto.setBirthdateYYYY(labMessageDto.getPersonBirthDateYYYY());
		personDto.setSex(labMessageDto.getPersonSex());

		ControllerProvider.getPersonController()
			.selectOrCreatePerson(personDto, I18nProperties.getString(Strings.infoSelectOrCreatePersonForLabMessage), selectedPerson -> {
				if (selectedPerson != null) {
					PersonDto selectedPersonDto = null;
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
				List<SampleDto> samples = FacadeProvider.getSampleFacade().getByCaseUuids(Arrays.asList(caseDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), caseDto.toReference()), labMessageDto);
				} else {
					pickOrCreateSample(caseDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getContact() != null) {
				ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(similarEntriesDto.getContact().getUuid());
				SampleCriteria criteria = new SampleCriteria();
				criteria.contact(contactDto.toReference());
				criteria.setDisease(contactDto.getDisease());
				List<SampleDto> samples = FacadeProvider.getSampleFacade().getByContactUuids(Arrays.asList(contactDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), contactDto.toReference()), labMessageDto);
				} else {
					pickOrCreateSample(contactDto, labMessageDto, samples);
				}
			} else if (similarEntriesDto.getEventParticipant() != null) {
				EventParticipantDto eventParticipantDto =
					FacadeProvider.getEventParticipantFacade().getByUuid(similarEntriesDto.getEventParticipant().getUuid());
				SampleCriteria criteria = new SampleCriteria();
				criteria.eventParticipant(eventParticipantDto.toReference());
				criteria.setDisease(labMessageDto.getTestedDisease());
				List<SampleDto> samples = FacadeProvider.getSampleFacade().getByEventParticipantUuids(Arrays.asList(eventParticipantDto.getUuid()));
				if (samples.isEmpty()) {
					createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), eventParticipantDto.toReference()), labMessageDto);
				} else {
					pickOrCreateSample(eventParticipantDto, labMessageDto, samples);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		});

		selectField.setSelectionChangeCallback((commitAllowed) -> {
			selectionField.getCommitButton().setEnabled(commitAllowed);
		});
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
					CommitDiscardWrapperComponent commitDiscardWrapperComponent = new CommitDiscardWrapperComponent(
						new VerticalLayout(new Label(I18nProperties.getString(Strings.infoEventParticipantAlreadyExisting))));
					commitDiscardWrapperComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionContinue));
					commitDiscardWrapperComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionBack));

					commitDiscardWrapperComponent.addCommitListener(() -> {
						EventParticipantReferenceDto participant =
							FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(selectedEvent.getUuid(), person.getUuid());
						List<SampleDto> samples = FacadeProvider.getSampleFacade().getByEventParticipantUuids(Arrays.asList(participant.getUuid()));
						if (samples.isEmpty()) {
							createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), participant), labMessageDto);
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

		component.addDiscardListener(() -> window.close());

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

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
		editView.addDiscardListener(() -> window.close());

		window.setContent(editView);
		window.setCaption(I18nProperties.getString(Strings.headingCreateNewEvent));
		UI.getCurrent().addWindow(window);
	}

	private void createEventParticipant(EventDto eventDto, LabMessageDto labMessageDto, PersonDto person) {
		EventParticipantDto eventParticipant = EventParticipantDto.build(eventDto.toReference(), UserProvider.getCurrent().getUserReference());
		eventParticipant.setPerson(person);

		EventParticipantEditForm createForm = new EventParticipantEditForm(eventDto, false);
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
			createForm.getFieldGroup());

		Window window = VaadinUiUtil.createPopupWindow();

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final EventParticipantDto dto = createForm.getValue();

				FacadeProvider.getPersonFacade().savePerson(dto.getPerson());
				EventParticipantDto savedDto = FacadeProvider.getEventParticipantFacade().saveEventParticipant(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Notification.Type.ASSISTIVE_NOTIFICATION);
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), savedDto.toReference()), labMessageDto);
				window.close();
			}
		});
		createComponent.addDiscardListener(() -> window.close());

		showFormWithLabMessage(labMessageDto, createComponent, window, I18nProperties.getString(Strings.headingCreateNewEventParticipant));
	}

	private PersonDto savePerson(PersonDto personDto, LabMessageDto labMessageDto) {
		if (personDto.getAddress().getCity() == null
			&& personDto.getAddress().getHouseNumber() == null
			&& personDto.getAddress().getPostalCode() == null
			&& personDto.getAddress().getStreet() == null) {
			personDto.getAddress().setStreet(labMessageDto.getPersonStreet());
			personDto.getAddress().setHouseNumber(labMessageDto.getPersonHouseNumber());
			personDto.getAddress().setPostalCode(labMessageDto.getPersonPostalCode());
			personDto.getAddress().setCity(labMessageDto.getPersonCity());
		}
		return FacadeProvider.getPersonFacade().savePerson(personDto);
	}

	private void pickOrCreateSample(PseudonymizableDto dto, LabMessageDto labMessageDto, List<SampleDto> samples) {
		SampleSelectionField selectField = new SampleSelectionField(samples, I18nProperties.getString(Strings.infoPickOrCreateSample));

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<EntrySelectionField> selectionField = new CommitDiscardWrapperComponent(selectField);
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
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), ((CaseDataDto) dto).toReference()), labMessageDto);
			} else if (ContactDto.class.equals(dto.getClass())) {
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), ((ContactDto) dto).toReference()), labMessageDto);
			} else if (EventParticipantDto.class.equals(dto.getClass())) {
				createSample(SampleDto.build(UserProvider.getCurrent().getUserReference(), ((EventParticipantDto) dto).toReference()), labMessageDto);
			}
			window.close();
		});
		selectField.setSelectionChangeCallback((commitAllowed) -> {
			selectionField.getCommitButton().setEnabled(commitAllowed);
		});
		selectionField.getCommitButton().setEnabled(false);
		selectionField.addDiscardListener(() -> window.close());

		showFormWithLabMessage(labMessageDto, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreateSample));
	}

	private void pickOrCreateTest(SampleDto sampleDto, LabMessageDto labMessageDto, List<PathogenTestDto> tests, int caseSampleCount) {
		PathogenTestSelectionField selectField =
			new PathogenTestSelectionField(tests, I18nProperties.getString(Strings.infoPickOrCreatePathogenTest));

		Window window = VaadinUiUtil.createPopupWindow();

		final CommitDiscardWrapperComponent<PathogenTestSelectionField> selectionField = new CommitDiscardWrapperComponent(selectField);
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
		selectField.setSelectionChangeCallback((commitAllowed) -> {
			selectionField.getCommitButton().setEnabled(commitAllowed);
		});
		selectionField.getCommitButton().setEnabled(false);
		selectionField.addDiscardListener(() -> window.close());

		showFormWithLabMessage(labMessageDto, selectionField, window, I18nProperties.getString(Strings.headingPickOrCreatePathogenTest));
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
							new CommitDiscardWrapperComponent(createForm),
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
				finishProcess(labMessageDto);
				window.close();
			}, onSavedPathogenTest);

		pathogenTestEditComponent.addDiscardListener(() -> window.close());
		pathogenTestEditComponent.getWrappedComponent().setValue(testDto);

		showFormWithLabMessage(labMessageDto, pathogenTestEditComponent, window, I18nProperties.getString(Strings.headingEditPathogenTestResult));
	}

	private void createCase(LabMessageDto labMessageDto, PersonDto person) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null, true);

		CaseDataDto caseDto = CaseDataDto.build(person.toReference(), labMessageDto.getTestedDisease());
		caseDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		Window window = VaadinUiUtil.createPopupWindow();
		caseCreateComponent.addCommitListener(() -> {
			savePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(caseCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessageDto);
			createSample(
				SampleDto.build(UserProvider.getCurrent().getUserReference(), caseCreateComponent.getWrappedComponent().getValue().toReference()),
				labMessageDto);
			window.close();
		});
		caseCreateComponent.addDiscardListener(() -> window.close());
		caseCreateComponent.getWrappedComponent().setValue(caseDto);
		caseCreateComponent.getWrappedComponent().setPerson(person);

		caseCreateComponent.getWrappedComponent().getField(PersonDto.FIRST_NAME).setEnabled(false);
		caseCreateComponent.getWrappedComponent().getField(PersonDto.LAST_NAME).setEnabled(false);

		showFormWithLabMessage(labMessageDto, caseCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	private void createContact(LabMessageDto labMessageDto, PersonDto person) {
		CommitDiscardWrapperComponent<ContactCreateForm> contactCreateComponent =
			ControllerProvider.getContactController().getContactCreateComponent(null, false, null, true);

		ContactDto contactDto = ContactDto.build(null, labMessageDto.getTestedDisease(), null);
		contactDto.setPerson(person.toReference());
		contactDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		Window window = VaadinUiUtil.createPopupWindow();
		contactCreateComponent.addCommitListener(() -> {
			savePerson(
				FacadeProvider.getPersonFacade().getPersonByUuid(contactCreateComponent.getWrappedComponent().getValue().getPerson().getUuid()),
				labMessageDto);
			createSample(
				SampleDto.build(UserProvider.getCurrent().getUserReference(), contactCreateComponent.getWrappedComponent().getValue().toReference()),
				labMessageDto);
			window.close();
		});
		contactCreateComponent.addDiscardListener(() -> window.close());
		contactCreateComponent.getWrappedComponent().setValue(contactDto);
		contactCreateComponent.getWrappedComponent().setPerson(person);

		contactCreateComponent.getWrappedComponent().getField(PersonDto.FIRST_NAME).setEnabled(false);
		contactCreateComponent.getWrappedComponent().getField(PersonDto.LAST_NAME).setEnabled(false);

		showFormWithLabMessage(labMessageDto, contactCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewContact));
	}

	private void showFormWithLabMessage(LabMessageDto labMessageDto, CommitDiscardWrapperComponent createComponent, Window window, String heading) {
		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(labMessageDto);
		form.setWidth(550, Sizeable.Unit.PIXELS);
		HorizontalLayout layout = new HorizontalLayout(form, createComponent);
		layout.setMargin(true);
		window.setContent(layout);
		window.setCaption(heading);
		UI.getCurrent().addWindow(window);
	}

	private void createSample(SampleDto sampleDto, LabMessageDto labMessageDto) {
		sampleDto.setSampleDateTime(labMessageDto.getSampleDateTime());
		if (labMessageDto.getSampleReceivedDate() != null) {
			sampleDto.setReceived(true);
			sampleDto.setReceivedDate(labMessageDto.getSampleReceivedDate());
			sampleDto.setLabSampleID(labMessageDto.getLabSampleId());
		}
		sampleDto.setSampleMaterial(labMessageDto.getSampleMaterial());
		sampleDto.setSpecimenCondition(labMessageDto.getSpecimenCondition());

		List<FacilityReferenceDto> labs =
			FacadeProvider.getFacilityFacade().getByExternalIdAndType(labMessageDto.getTestLabExternalId(), FacilityType.LABORATORY, false);
		if (labs != null && labs.size() == 1) {
			sampleDto.setLab(labs.get(0));
		}

		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			ControllerProvider.getSampleController().getSampleCreateComponent(sampleDto, () -> {
			});

		CheckBox checkBox = sampleCreateComponent.getWrappedComponent().getField(Captions.sampleIncludeTestOnCreation);
		checkBox.setValue(Boolean.TRUE);
		checkBox.setEnabled(false);
		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_RESULT)).setValue(labMessageDto.getTestResult());
		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_TYPE)).setValue(labMessageDto.getTestType());
		((ComboBox) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TESTED_DISEASE)).setValue(labMessageDto.getTestedDisease());
		((DateTimeField) sampleCreateComponent.getWrappedComponent().getField(PathogenTestDto.TEST_DATE_TIME))
			.setValue(labMessageDto.getTestDateTime());

		sampleCreateComponent.addCommitListener(() -> {
			window.close();
			finishProcess(labMessageDto);
		});
		sampleCreateComponent.addDiscardListener(() -> window.close());

		showFormWithLabMessage(labMessageDto, sampleCreateComponent, window, I18nProperties.getString(Strings.headingCreateNewSample));
	}

	private void createPathogenTest(SampleDto sampleDto, LabMessageDto labMessageDto) {
		PathogenTestDto pathogenTestDto = PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser());
		pathogenTestDto.setTestType(labMessageDto.getTestType());
		pathogenTestDto.setTestedDisease(labMessageDto.getTestedDisease());
		pathogenTestDto.setTestDateTime(labMessageDto.getTestDateTime());
		pathogenTestDto.setTestResultText(labMessageDto.getTestResultText());

		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestCreateComponent =
			ControllerProvider.getPathogenTestController().getPathogenTestCreateComponent(sampleDto.toReference(), 0, () -> {
				window.close();
				finishProcess(labMessageDto);
			}, null);

		pathogenTestCreateComponent.addDiscardListener(() -> window.close());
		pathogenTestCreateComponent.getWrappedComponent().setValue(pathogenTestDto);

		showFormWithLabMessage(labMessageDto, pathogenTestCreateComponent, window, I18nProperties.getString(Strings.headingCreatePathogenTestResult));
	}

	private void finishProcess(LabMessageDto labMessageDto) {
		labMessageDto.setProcessed(true);
		FacadeProvider.getLabMessageFacade().save(labMessageDto);
		SormasUI.get().getNavigator().navigateTo(LabMessagesView.VIEW_NAME);
	}
}
