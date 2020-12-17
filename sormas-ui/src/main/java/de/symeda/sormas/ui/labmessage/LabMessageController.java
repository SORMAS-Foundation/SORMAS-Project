package de.symeda.sormas.ui.labmessage;

import java.util.List;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.labmessage.EventParticipantSimilarityCriteria;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.SimilarEntitiesDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.ui.ControllerProvider;
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
						personDto.getAddress().setStreet(labMessageDto.getPersonStreet());
						personDto.getAddress().setHouseNumber(labMessageDto.getPersonHouseNumber());
						personDto.getAddress().setPostalCode(labMessageDto.getPersonPostalCode());
						personDto.getAddress().setCity(labMessageDto.getPersonCity());
						FacadeProvider.getPersonFacade().savePerson(personDto);
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

					pickOrCreateEntity(labMessageDto, similarCases, similarContacts, similarEventParticipants);
				}
			});
	}

	private void pickOrCreateEntity(
		LabMessageDto labMessageDto,
		List<CaseIndexDto> cases,
		List<SimilarContactDto> contacts,
		List<SimilarEventParticipantDto> eventParticipants) {
		EntitySelectionField selectField = new EntitySelectionField(labMessageDto, cases, contacts, eventParticipants);

		final CommitDiscardWrapperComponent<EntitySelectionField> component = new CommitDiscardWrapperComponent<>(selectField);
		component.addCommitListener(() -> {
			SimilarEntitiesDto similarEntitiesDto = selectField.getValue();
//			if (resultConsumer != null) {
//				resultConsumer.accept(similarEntitiesDto.toReference());
//			}
		});

		selectField.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEntity));
	}
}
