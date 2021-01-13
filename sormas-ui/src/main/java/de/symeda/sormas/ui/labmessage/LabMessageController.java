package de.symeda.sormas.ui.labmessage;

import java.util.List;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.EventParticipantSimilarityCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.SimilarEntriesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseCreateForm;
import de.symeda.sormas.ui.samples.PathogenTestForm;
import de.symeda.sormas.ui.samples.SampleCreateForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class LabMessageController {

	public LabMessageController() {

	}

	public void show(String uuid) {

		LabMessageDto newDto = FacadeProvider.getLabMessageFacade().getByUuid(uuid);

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(newDto);
		VerticalLayout layout = new VerticalLayout(form);
		layout.setMargin(true);
		VaadinUiUtil.showPopupWindow(layout, I18nProperties.getString(Strings.headingShowLabMessage));
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
					if (selectedPerson.getUuid().equals(personDto.getUuid())) {
						PersonDto savedPerson = FacadeProvider.getPersonFacade().getPersonByUuid(personDto.getUuid());
						savedPerson.getAddress().setStreet(labMessageDto.getPersonStreet());
						savedPerson.getAddress().setHouseNumber(labMessageDto.getPersonHouseNumber());
						savedPerson.getAddress().setPostalCode(labMessageDto.getPersonPostalCode());
						savedPerson.getAddress().setCity(labMessageDto.getPersonCity());
						FacadeProvider.getPersonFacade().savePerson(savedPerson);
					}

					CaseCriteria caseCriteria = new CaseCriteria();
					caseCriteria.person(selectedPerson);
					caseCriteria.disease(labMessageDto.getTestedDisease());
					CaseSimilarityCriteria caseSimilarityCriteria = new CaseSimilarityCriteria();
					caseSimilarityCriteria.caseCriteria(caseCriteria);
					caseSimilarityCriteria.personUuid(selectedPerson.getUuid());
					List<CaseIndexDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(caseSimilarityCriteria);

					ContactSimilarityCriteria contactSimilarityCriteria = new ContactSimilarityCriteria();
					contactSimilarityCriteria.setPerson(selectedPerson);
					contactSimilarityCriteria.setDisease(labMessageDto.getTestedDisease());
					List<SimilarContactDto> similarContacts = FacadeProvider.getContactFacade().getMatchingContacts(contactSimilarityCriteria);

					EventParticipantSimilarityCriteria eventParticipantSimilarityCriteria = new EventParticipantSimilarityCriteria();
					eventParticipantSimilarityCriteria.setPerson(selectedPerson);
					eventParticipantSimilarityCriteria.setDisease(labMessageDto.getTestedDisease());
					List<SimilarEventParticipantDto> similarEventParticipants =
						FacadeProvider.getEventParticipantFacade().getSimilarEventParticipants(eventParticipantSimilarityCriteria);

					pickOrCreateEntry(labMessageDto, similarCases, similarContacts, similarEventParticipants);
				}
			});
	}

	private void pickOrCreateEntry(
		LabMessageDto labMessageDto,
		List<CaseIndexDto> cases,
		List<SimilarContactDto> contacts,
		List<SimilarEventParticipantDto> eventParticipants) {
		EntrySelectionField selectField = new EntrySelectionField(labMessageDto, cases, contacts, eventParticipants);

		final CommitDiscardWrapperComponent<EntrySelectionField> component = new CommitDiscardWrapperComponent<>(selectField);
		component.addCommitListener(() -> {
			SimilarEntriesDto similarEntriesDto = selectField.getValue();
			if (similarEntriesDto.isNewCase()) {
				createCase(labMessageDto);
			}
		});

		selectField.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});
		component.getCommitButton().setEnabled(false);

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEntry));
	}

	private void createCase(LabMessageDto labMessageDto) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			ControllerProvider.getCaseController().getCaseCreateComponent(null, null, null);

		Window window = VaadinUiUtil.createPopupWindow();
		caseCreateComponent.addCommitListener(() -> {
			createSample(caseCreateComponent.getWrappedComponent().getValue(), labMessageDto);
			window.close();
		});

		PersonDto personDto = PersonDto.build();
		personDto.setFirstName(labMessageDto.getPersonFirstName());
		personDto.setLastName(labMessageDto.getPersonLastName());
		personDto.setSex(labMessageDto.getPersonSex());
		personDto.setBirthdateDD(labMessageDto.getPersonBirthDateDD());
		personDto.setBirthdateMM(labMessageDto.getPersonBirthDateMM());
		personDto.setBirthdateYYYY(labMessageDto.getPersonBirthDateYYYY());
		personDto.getAddress().setPostalCode(labMessageDto.getPersonPostalCode());
		personDto.getAddress().setCity(labMessageDto.getPersonCity());
		personDto.getAddress().setStreet(labMessageDto.getPersonStreet());
		personDto.getAddress().setHouseNumber(labMessageDto.getPersonHouseNumber());
		CaseDataDto caseDto = CaseDataDto.build(personDto.toReference(), labMessageDto.getTestedDisease());
		caseDto.setReportingUser(UserProvider.getCurrent().getUserReference());
		caseCreateComponent.getWrappedComponent().setValue(caseDto);
		caseCreateComponent.getWrappedComponent().setPerson(personDto);

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(labMessageDto);
		HorizontalLayout layout = new HorizontalLayout(form, caseCreateComponent);
		layout.setMargin(true);
		window.setContent(layout);
		window.setCaption(I18nProperties.getString(Strings.headingCreateNewCase));
		UI.getCurrent().addWindow(window);
	}

	private void createSample(CaseDataDto caseDataDto, LabMessageDto labMessageDto) {
		SampleDto sampleDto = SampleDto.build(UserProvider.getCurrent().getUserReference(), caseDataDto.toReference());
		sampleDto.setSampleDateTime(labMessageDto.getSampleDateTime());
		if (labMessageDto.getSampleReceivedDate() != null) {
			sampleDto.setReceived(true);
			sampleDto.setReceivedDate(labMessageDto.getSampleReceivedDate());
			sampleDto.setLabSampleID(labMessageDto.getLabSampleId());
		}
		sampleDto.setSampleMaterial(labMessageDto.getSampleMaterial());
		sampleDto.setSpecimenCondition(labMessageDto.getSpecimenCondition());

		Window window = VaadinUiUtil.createPopupWindow();
		CommitDiscardWrapperComponent<SampleCreateForm> sampleCreateComponent =
			ControllerProvider.getSampleController().getSampleCreateComponent(sampleDto, () -> {
				window.close();
			});

		sampleCreateComponent.addCommitListener(() -> {
			createPathogenTest(sampleCreateComponent.getWrappedComponent().getValue(), labMessageDto);
		});

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(labMessageDto);
		HorizontalLayout layout = new HorizontalLayout(form, sampleCreateComponent);
		layout.setMargin(true);
		window.setContent(layout);
		window.setCaption(I18nProperties.getString(Strings.headingCreateNewSample));
		UI.getCurrent().addWindow(window);
	}

	private void createPathogenTest(SampleDto sampleDto, LabMessageDto labMessageDto) {
		PathogenTestDto pathogenTestDto = PathogenTestDto.build(sampleDto, UserProvider.getCurrent().getUser());
		pathogenTestDto.setTestType(labMessageDto.getTestType());
		pathogenTestDto.setTestedDisease(labMessageDto.getTestedDisease());
		pathogenTestDto.setTestDateTime(labMessageDto.getTestDateTime());

		Window window = VaadinUiUtil.createPopupWindow();

		CommitDiscardWrapperComponent<PathogenTestForm> pathogenTestCreateComponent =
			ControllerProvider.getPathogenTestController().getPathogenTestCreateComponent(sampleDto.toReference(), 0, () -> {
				labMessageDto.setProcessed(true);
				FacadeProvider.getLabMessageFacade().save(labMessageDto);
				window.close();
			}, null);

		pathogenTestCreateComponent.getWrappedComponent().setValue(pathogenTestDto);

		LabMessageEditForm form = new LabMessageEditForm(true);
		form.setValue(labMessageDto);
		HorizontalLayout layout = new HorizontalLayout(form, pathogenTestCreateComponent);
		layout.setMargin(true);
		window.setContent(layout);
		window.setCaption(I18nProperties.getString(Strings.headingCreatePathogenTestResult));
		UI.getCurrent().addWindow(window);
	}
}
