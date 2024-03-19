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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.event.EventParticipantSelectionDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.externaljournal.ExternalJournalSyncResponseDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.events.eventParticipantMerge.PickLeadEventParticipant;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class PersonController {

	private PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public PersonController() {
	}

	public void registerViews(Navigator navigator) {
		navigator.addView(PersonsView.VIEW_NAME, PersonsView.class);
		navigator.addView(PersonDataView.VIEW_NAME, PersonDataView.class);
		navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
	}

	public TitleLayout getPersonViewTitleLayout(PersonDto personDto) {
		final TitleLayout titleLayout = new TitleLayout();

		final String shortUuid = DataHelper.getShortUuid(personDto.getUuid());
		final StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(personDto);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	public void mergePersons(PersonIndexDto person1, PersonIndexDto person2) {
		final PersonGrid personGrid = new PersonGrid();
		personGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		final List<PersonIndexDto> persons = Arrays.asList(person1, person2);
		personGrid.setFixDataProvider(persons);
		personGrid.setVisible(true);
		personGrid.setWidth(100, Unit.PERCENTAGE);

		final Window popupWindow = VaadinUiUtil.createPopupWindow();
		popupWindow.setWidth(1024, Unit.PIXELS);
		popupWindow.setCaption(I18nProperties.getString(Strings.headingPickOrMergePerson));

		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(Strings.infoPersonMergeDescription)));
		layout.addComponent(personGrid);

		final ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConfirm() {
				PersonController.this.mergePersons(personGrid, persons, popupWindow, true);
			}

			@Override
			protected void onCancel() {
				PersonController.this.mergePersons(personGrid, persons, popupWindow, false);
			}
		};

		final Button mergeButton = confirmationComponent.getConfirmButton();
		mergeButton.setCaption(I18nProperties.getCaption(Captions.actionMerge));
		mergeButton.setEnabled(false);
		final Button pickButton = confirmationComponent.getCancelButton();
		pickButton.setCaption(I18nProperties.getCaption(Captions.actionPick));
		pickButton.removeStyleName(ValoTheme.BUTTON_LINK);
		pickButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		pickButton.setEnabled(false);

		personGrid.addItemClickListener(itemClick -> {
			if (!itemClick.getMouseEventDetails().isDoubleClick()) {
				boolean enabled = personGrid.getSelectedItems().isEmpty()
					|| !DataHelper.equal(itemClick.getItem(), personGrid.getSelectedItems().stream().findFirst().get());
				mergeButton.setEnabled(enabled);
				pickButton.setEnabled(enabled);
			}
		});

		confirmationComponent.addExtraButton(
			ButtonHelper
				.createButton(Strings.unsavedChanges_cancel, I18nProperties.getCaption(Captions.actionDiscard), null, ValoTheme.BUTTON_PRIMARY),
			buttonEvent -> popupWindow.close());

		layout.addComponent(confirmationComponent);
		layout.setComponentAlignment(confirmationComponent, Alignment.BOTTOM_RIGHT);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(true);
		popupWindow.setContent(layout);
		popupWindow.setClosable(false);

		UI.getCurrent().addWindow(popupWindow);
	}

	private void mergePersons(PersonGrid personGrid, List<PersonIndexDto> personIndexDtos, Window popupWindow, boolean mergeProperties) {

		final Set<PersonIndexDto> selectedItems = personGrid.getSelectedItems();
		final PersonIndexDto leadPerson = selectedItems.iterator().next();
		final PersonIndexDto otherPerson = personIndexDtos.stream().filter(p -> p.getUuid() != leadPerson.getUuid()).findFirst().get();

		final String confirmationMessage;

		if (FacadeProvider.getPersonFacade().isSharedOrReceived(otherPerson.getUuid())) {
			if (FacadeProvider.getPersonFacade().isSharedOrReceived(leadPerson.getUuid())) {
				confirmationMessage = I18nProperties.getString(Strings.infoPersonMergeConfirmationBothShared);
			} else {
				new Notification(
					I18nProperties.getString(Strings.headingMergePersonError),
					I18nProperties.getString(Strings.infoPersonMergeSharedMustLead),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return;
			}
		} else {
			confirmationMessage = I18nProperties.getString(Strings.infoPersonMergeConfirmation);
		}

		String firstPersonUuid = personIndexDtos.get(0).getUuid();
		String secondPersonUuid = personIndexDtos.get(1).getUuid();

		List<EventParticipantSelectionDto> eventParticipantsWithSameEvent =
			FacadeProvider.getEventParticipantFacade().getEventParticipantsWithSameEvent(firstPersonUuid, secondPersonUuid);

		Set<String> eventsWithMoreDuplicateThanAllowed = eventParticipantsWithSameEvent.stream()
			.collect(Collectors.groupingBy(EventParticipantSelectionDto::getEvent, Collectors.counting()))
			.entrySet()
			.stream()
			.filter(entry -> entry.getValue() > 2)
			.map(Map.Entry::getKey)
			.map(EventReferenceDto::getUuid)
			.collect(Collectors.toSet());

		if (!eventsWithMoreDuplicateThanAllowed.isEmpty()) {
			VerticalLayout popUpContent = new VerticalLayout();
			popUpContent.addComponent(
				VaadinUiUtil
					.createInfoComponent(I18nProperties.getString(Strings.infoPickorMergeEventParticipantDuplicateEventParticipantByPersonByEvent)));
			eventsWithMoreDuplicateThanAllowed.forEach(eventUuid -> {
				Label eventUuidLabel = new Label(eventUuid);
				popUpContent.addComponent(eventUuidLabel);
			});

			Window popupMoreDuplicatesThanAllowedWindow = VaadinUiUtil
				.showPopupWindow(popUpContent, I18nProperties.getString(Strings.headingMergeDuplicateEventParticipantSamePersonSameEvent));

			Button.ClickListener cancelListener = clickEvent -> {
				popupMoreDuplicatesThanAllowedWindow.close();
			};

			popUpContent.setSpacing(false);

			Button cancelButton = ButtonHelper.createButton(Captions.actionCancel, cancelListener);
			popUpContent.addComponent(cancelButton);
			popUpContent.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);

			return;
		}

		if (!eventParticipantsWithSameEvent.isEmpty()) {
			if (UiUtil.permitted(UserRight.EVENTPARTICIPANT_VIEW)) {
				selectLeadEventParticipantByEvent(
					eventParticipantsWithSameEvent,
					(selectedEventParticipants, mergeEventParticipantProperties) -> pickOrMergeConfirmationPopUp(
						popupWindow,
						mergeProperties,
						leadPerson,
						otherPerson,
						confirmationMessage,
						selectedEventParticipants,
						mergeEventParticipantProperties));
				return;
			} else {
				VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getString(Strings.headingMergePersonError),
					I18nProperties.getString(Strings.messagePersonMergeNoEventParticipantRights));
				return;
			}
		}

		pickOrMergeConfirmationPopUp(popupWindow, mergeProperties, leadPerson, otherPerson, confirmationMessage, new ArrayList<>(), false);
	}

	private Window pickOrMergeConfirmationPopUp(
		Window popupWindow,
		boolean mergePersonProperties,
		PersonIndexDto leadPerson,
		PersonIndexDto otherPerson,
		String confirmationMessage,
		List<String> selectedEventParticipantUuids,
		boolean mergeEventParticipantProperties) {
		Window mergeWindow = VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingPickOrMergePersonConfirmation),
			new Label(confirmationMessage, ContentMode.HTML),
			I18nProperties.getCaption(Captions.actionConfirm),
			I18nProperties.getCaption(Captions.actionDiscard),
			800,
			confirm -> {
				if (Boolean.TRUE.equals(confirm)) {
					FacadeProvider.getPersonFacade()
						.mergePerson(
							leadPerson.getUuid(),
							otherPerson.getUuid(),
							mergePersonProperties,
							selectedEventParticipantUuids,
							mergeEventParticipantProperties);
					popupWindow.close();
					SormasUI.refreshView();
				}
			});

		return mergeWindow;
	}

	private void selectLeadEventParticipantByEvent(
		List<EventParticipantSelectionDto> eventParticipantSelectionDtos,
		BiConsumer<List<String>, Boolean> callback) {

		PickLeadEventParticipant pickLeadEventParticipant = new PickLeadEventParticipant(eventParticipantSelectionDtos);

		final CommitDiscardWrapperComponent<PickLeadEventParticipant> component = new CommitDiscardWrapperComponent<>(pickLeadEventParticipant);

		component.getCommitButton().setCaption(I18nProperties.getCaption((Captions.actionPick)));

		final Button mergeButton = ButtonHelper.createButton(Captions.actionMerge);
		component.getButtonsPanel().addComponent(mergeButton);
		mergeButton.removeStyleName(ValoTheme.BUTTON_LINK);
		mergeButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

		mergeButton.addClickListener(clickEvent -> {
			pickLeadEventParticipant.setPickOrMerge(PickLeadEventParticipant.PickOrMerge.MERGE);
			component.commit();
		});

		component.setPreCommitListener(preCommitCallback -> {
			List<String> pickedEventParticipants = component.getWrappedComponent().getValue();

			if (eventParticipantSelectionDtos.size() / 2 > pickedEventParticipants.size()) {
				VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getString(Strings.headingPickEventParticipantsIncompleteSelection),
					I18nProperties.getString(Strings.messagePickEventParticipantsIncompleteSelection));
			} else {
				preCommitCallback.run();
			}
		});

		component.addCommitListener(() -> {
			List<String> pickedEventParticipants = component.getWrappedComponent().getValue();
			callback.accept(pickedEventParticipants, pickLeadEventParticipant.getPickOrMerge() == PickLeadEventParticipant.PickOrMerge.MERGE);
		});

		component.setWidth(1500, Unit.PIXELS);

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickEventParticipants));
	}

	public void selectOrCreatePerson(final PersonDto person, String infoText, Consumer<PersonReferenceDto> resultConsumer, boolean saveNewPerson) {
		selectOrCreatePerson(person, infoText, resultConsumer, null, saveNewPerson, null);
	}

	/**
	 * Provides a PersonSelectionField to be able to decide to pick an existing Person from the system
	 * or to create a new one with the given information.
	 * 
	 * @param person
	 *            Dto wich contains the information about the person who should be created.
	 * @param infoText
	 *            Information that is shown to the user.
	 * @param resultConsumer
	 *            Is the operation that is executed after this mehtod.
	 * @param saveNewPerson
	 *            Indicates if the new person should be saved.
	 * @param infoTextWithoutMatches
	 *            Information that should be shown to the user if the window is still shown
	 *            if there are no matches found by the system.
	 */
	public void selectOrCreatePerson(
		final PersonDto person,
		String infoText,
		Consumer<PersonReferenceDto> resultConsumer,
		Runnable discardCallback,
		boolean saveNewPerson,
		String infoTextWithoutMatches) {

		PersonSelectionField personSelect = new PersonSelectionField(person, infoText, infoTextWithoutMatches);
		personSelect.setWidth(1024, Unit.PIXELS);

		if (StringUtils.isNotBlank(infoTextWithoutMatches) || personSelect.hasMatches()) {
			// TODO add user right parameter
			final CommitDiscardWrapperComponent<PersonSelectionField> component =
				new CommitDiscardWrapperComponent<PersonSelectionField>(personSelect);
			if (!saveNewPerson) {
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			}
			component.addCommitListener(() -> {
				SimilarPersonDto selectedPerson = personSelect.getValue();
				if (selectedPerson != null) {
					if (resultConsumer != null) {
						resultConsumer.accept(selectedPerson.toReference());
					}
				} else {
					PersonDto savedPerson;
					if (saveNewPerson) {
						savedPerson = personFacade.save(person);
					} else {
						savedPerson = person;
					}
					resultConsumer.accept(savedPerson.toReference());
				}
			});

			component.addDiscardListener(() -> {
				if (discardCallback != null) {
					discardCallback.run();
				}
			});

			personSelect.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson), true);
			personSelect.selectBestMatch();
		} else if (saveNewPerson) {
			// no duplicate persons found so save a new person
			PersonDto savedPerson = personFacade.save(person);
			resultConsumer.accept(savedPerson.toReference());
		} else {
			resultConsumer.accept(person.toReference());
		}
	}

	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(String personUuid, boolean isEditAllowed) {
		PersonDto personDto = personFacade.getByUuid(personUuid);

		PersonEditForm editForm =
			new PersonEditForm(UiUtil.permitted(isEditAllowed, UserRight.PERSON_EDIT), personDto.isPseudonymized(), personDto.isInJurisdiction());
		editForm.setValue(personDto);

		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				PersonDto dto = editForm.getValue();
				savePerson(dto);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(
		PersonContext personContext,
		PersonDto person,
		Disease disease,
		String diseaseDetails,
		UserRight editUserRight,
		final ViewMode viewMode) {

		PersonEditForm editForm =
			new PersonEditForm(personContext, disease, diseaseDetails, viewMode, person.isPseudonymized(), person.isInJurisdiction());
		editForm.setValue(person);

		final CommitDiscardWrapperComponent<PersonEditForm> editView =
			new CommitDiscardWrapperComponent<>(editForm, UiUtil.permitted(editUserRight), editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				PersonDto dto = editForm.getValue();
				savePerson(dto);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(
		PersonContext personContext,
		PersonDto person,
		Disease disease,
		String diseaseDetails,
		UserRight editUserRight,
		final ViewMode viewMode,
		boolean isEditAllowed) {

		PersonEditForm editForm = new PersonEditForm(
			personContext,
			disease,
			diseaseDetails,
			viewMode,
			person.isPseudonymized(),
			person.isInJurisdiction(),
			UiUtil.permitted(isEditAllowed, editUserRight));
		editForm.setValue(person);

		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				PersonDto dto = editForm.getValue();
				savePerson(dto);
			}
		});

		return editView;
	}

	private void savePerson(PersonDto personDto) {
		DataHelper.Pair<CaseClassification, PersonDto> saveResult = personFacade.savePersonWithoutNotifyingExternalJournal(personDto);

		ExternalJournalSyncResponseDto responseDto = FacadeProvider.getExternalJournalFacade().notifyExternalJournal(saveResult.getElement1());
		String synchronizationMessage = getSynchronizationMessage(responseDto);

		CaseClassification newClassification = saveResult.getElement0();
		if (newClassification != null) {
			String personSavedMessage =
				String.format(I18nProperties.getString(Strings.messagePersonSavedClassificationChanged), newClassification.toString());
			String notificationMessage = String.format("%s.%s", personSavedMessage, synchronizationMessage);
			if (responseDto == null || (responseDto.isSuccess() && responseDto.getErrors().isEmpty())) {
				Notification notification = new Notification(notificationMessage, Type.WARNING_MESSAGE);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			} else {
				VaadinUiUtil.showWarningPopup(notificationMessage);
			}
		} else {
			String personSavedMessage = I18nProperties.getString(Strings.messagePersonSaved);
			String notificationMessage = String.format("%s.%s", personSavedMessage, synchronizationMessage);
			if (responseDto == null || (responseDto.isSuccess() && responseDto.getErrors().isEmpty())) {
				Notification.show(notificationMessage, Type.WARNING_MESSAGE);
			} else {
				VaadinUiUtil.showWarningPopup(notificationMessage);
			}
		}

		SormasUI.refreshView();
	}

	private String getSynchronizationMessage(ExternalJournalSyncResponseDto responseDto) {
		if (responseDto == null) {
			return "";
		}

		if (!responseDto.isSuccess()) {
			return I18nProperties.getValidationError(Validations.externalJournalPersonSynchronizationFailure, responseDto.getMessage());
		} else if (!responseDto.getErrors().isEmpty()) {
			StringBuffer sb = new StringBuffer();
			responseDto.getErrors().forEach((errorKey, errorValue) -> {
				sb.append(errorValue);
				sb.append(";");
			});
			return I18nProperties.getValidationError(Validations.externalJournalPersonSynchronizationPartial, sb.toString());
		} else {
			return I18nProperties.getValidationError(Validations.externalJournalPersonSynchronizationSuccess);
		}
	}

	private void onPersonChanged(PersonDto existingPerson, PersonDto changedPerson) {
		// approximate age reference date
		if (existingPerson == null
			|| !DataHelper.equal(changedPerson.getApproximateAge(), existingPerson.getApproximateAge())
			|| !DataHelper.equal(changedPerson.getApproximateAgeType(), existingPerson.getApproximateAgeType())) {
			if (changedPerson.getApproximateAge() == null) {
				changedPerson.setApproximateAgeReferenceDate(null);
			} else {
				changedPerson.setApproximateAgeReferenceDate(changedPerson.getDeathDate() != null ? changedPerson.getDeathDate() : new Date());
			}
		}
	}

	public void navigateToPersons() {
		SormasUI.get().getNavigator().navigateTo(PersonsView.VIEW_NAME);
	}

	public void navigateToPersons(PersonCriteria criteria) {
		ViewModelProviders.of(PersonsView.class).remove(PersonCriteria.class);
		ViewModelProviders.of(PersonsView.class).get(PersonCriteria.class, criteria);

		String navigationState = AbstractView.buildNavigationState(PersonsView.VIEW_NAME, criteria);
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToPerson(String uuid) {
		final String navigationState = PersonDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void validateBirthDate(Integer year, Integer month, Integer day) throws Validator.InvalidValueException {
		try {
			PersonHelper.validateBirthDate(year, month, day);
		} catch (ValidationRuntimeException ex) {
			throw new Validator.InvalidValueException(ex.getMessage());
		}
	}
}
