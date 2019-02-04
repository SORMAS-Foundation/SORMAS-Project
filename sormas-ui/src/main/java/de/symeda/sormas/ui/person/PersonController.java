/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.person;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.events.EventParticipantsView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonController {

	private PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public PersonController() {

	}

	public void create(Consumer<PersonReferenceDto> doneConsumer) {
		create("", "", doneConsumer);
	}

	public void create(String firstName, String lastName, Consumer<PersonReferenceDto> doneConsumer) {
		PersonDto person = createNewPerson();
		person.setFirstName(firstName);
		person.setLastName(lastName);

		person = personFacade.savePerson(person);
		doneConsumer.accept(person.toReference()); 
	}

	public void selectOrCreatePerson(String firstName, String lastName, Consumer<PersonReferenceDto> resultConsumer) {
		PersonSelectField personSelect = new PersonSelectField();
		personSelect.setFirstName(firstName);
		personSelect.setLastName(lastName);
		personSelect.setWidth(1024, Unit.PIXELS);

		if (personSelect.hasMatches()) {
			personSelect.selectBestMatch();
			// TODO add user right parameter
			final CommitDiscardWrapperComponent<PersonSelectField> selectOrCreateComponent = 
					new CommitDiscardWrapperComponent<PersonSelectField>(personSelect);

			ValueChangeListener nameChangeListener = e -> {
				selectOrCreateComponent.getCommitButton().setEnabled(!(personSelect.getFirstName() == null || personSelect.getFirstName().isEmpty()
						|| personSelect.getLastName() == null || personSelect.getLastName().isEmpty()));

			};
			personSelect.getFirstNameField().addValueChangeListener(nameChangeListener);
			personSelect.getLastNameField().addValueChangeListener(nameChangeListener);

			selectOrCreateComponent.addCommitListener(new CommitListener() {
				@Override
				public void onCommit() {
					PersonIndexDto person = personSelect.getValue();
					if (person != null) {
						if (resultConsumer != null) {
							resultConsumer.accept(person.toReference());
						}
					} else {	
						create(personSelect.getFirstName(), personSelect.getLastName(), resultConsumer);
					}
				}
			});

			personSelect.setSelectionChangeCallback((commitAllowed) -> {
				selectOrCreateComponent.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(selectOrCreateComponent, "Pick or create person");
		} else {
			create(personSelect.getFirstName(), personSelect.getLastName(), resultConsumer);
		}
	}

	private PersonDto createNewPerson() {
		PersonDto person = new PersonDto();
		person.setUuid(DataHelper.createUuid());
		return person;
	}

	public CommitDiscardWrapperComponent<PersonCreateForm> getPersonCreateComponent(PersonDto person, UserRight editOrCreateUserRight) {

		PersonCreateForm createForm = new PersonCreateForm(editOrCreateUserRight);
		createForm.setValue(person);
		final CommitDiscardWrapperComponent<PersonCreateForm> editComponent = new CommitDiscardWrapperComponent<PersonCreateForm>(createForm, createForm.getFieldGroup());

		editComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					PersonDto dto = createForm.getValue();
					personFacade.savePerson(dto);
				}
			}
		});

		return editComponent;
	}  


	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(String personUuid, Disease disease, String diseaseDetails, UserRight editOrCreateUserRight, final ViewMode viewMode) {
		PersonEditForm personEditForm = new PersonEditForm(disease, diseaseDetails, editOrCreateUserRight, viewMode);

		PersonDto personDto = personFacade.getPersonByUuid(personUuid);
		personEditForm.setValue(personDto);

		return getPersonEditView(personEditForm, editOrCreateUserRight);
	}

	private CommitDiscardWrapperComponent<PersonEditForm> getPersonEditView(PersonEditForm editForm, UserRight editOrCreateUserRight) {
		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<PersonEditForm>(editForm, editForm.getFieldGroup());
		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		
		List<CaseDataDto> personCases = caseFacade.getAllCasesOfPerson(editForm.getValue().getUuid(), UserProvider.getCurrent().getUserReference().getUuid());
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					PersonDto dto = editForm.getValue();
					personFacade.savePerson(dto);
					
					// Check whether the classification of any of this person's cases has changed
					CaseClassification newClassification = null;
					for (CaseDataDto personCase : personCases) {
						CaseDataDto updatedPersonCase = caseFacade.getCaseDataByUuid(personCase.getUuid());
						if (personCase.getCaseClassification() != updatedPersonCase.getCaseClassification() && updatedPersonCase.getClassificationUser() == null) {
							newClassification = updatedPersonCase.getCaseClassification();
							break;
						}
					}
					
					if (newClassification != null) {
						Notification notification = new Notification("Person data save. The classification of at least one case associated with this person was automatically changed to " + newClassification.toString() + ".", Type.WARNING_MESSAGE);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
					} else {
						Notification.show("Person data saved", Type.WARNING_MESSAGE);
					}
					
					refreshView();
				}
			}
		});

		return editView;
	}

	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
		if (currentView instanceof EventParticipantsView) {
			// force refresh, because view didn't change
			((EventParticipantsView)currentView).enter(null);
		}
	}

}
