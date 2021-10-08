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

import java.util.Date;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Validator;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.externaljournal.ExternalJournalSyncResponseDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class PersonController {

	private PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public PersonController() {
	}

	public void registerViews(Navigator navigator) {
		UserProvider userProvider = UserProvider.getCurrent();
		navigator.addView(PersonsView.VIEW_NAME, PersonsView.class);
		navigator.addView(PersonDataView.VIEW_NAME, PersonDataView.class);
		navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
	}

	public VerticalLayout getPersonViewTitleLayout(PersonDto personDto) {
		final VerticalLayout titleLayout = new VerticalLayout();
		titleLayout.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		titleLayout.setSpacing(false);

		final String shortUuid = DataHelper.getShortUuid(personDto.getUuid());
		final String personFullName = personDto.toReference().getCaption();
		final StringBuilder personLabelSb = new StringBuilder();
		if (StringUtils.isNotBlank(personFullName)) {
			personLabelSb.append(personFullName);

			if (personDto.getBirthdateDD() != null && personDto.getBirthdateMM() != null && personDto.getBirthdateYYYY() != null) {
				personLabelSb.append(" (* ")
					.append(
						PersonHelper.formatBirthdate(
							personDto.getBirthdateDD(),
							personDto.getBirthdateMM(),
							personDto.getBirthdateYYYY(),
							I18nProperties.getUserLanguage()))
					.append(")");
			}
		}
		personLabelSb.append(personLabelSb.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		final Label personLabel = new Label(personLabelSb.toString());
		personLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		titleLayout.addComponent(personLabel);

		return titleLayout;
	}

	public void selectOrCreatePerson(final PersonDto person, String infoText, Consumer<PersonReferenceDto> resultConsumer, boolean saveNewPerson) {
		// This builds a selection field for all potential similar persons if any.
		// The user can choose to merge or create a new person in case there is a similar person in the system.
		PersonSelectionField personSelect = new PersonSelectionField(person, infoText);
		personSelect.setWidth(1024, Unit.PIXELS);

		// check if we have duplicate persons for the given PersonDto
		if (personSelect.hasMatches()) {
			// if yes give the user the chance to pick or create a new one.
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
						savedPerson = personFacade.savePerson(person);
					} else {
						savedPerson = person;
					}
					resultConsumer.accept(savedPerson.toReference());
				}
			});

			personSelect.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));
			personSelect.selectBestMatch();
		} else if (saveNewPerson) {
			// no duplicate persons found so save a new person
			PersonDto savedPerson = personFacade.savePerson(person);
			resultConsumer.accept(savedPerson.toReference());
		} else {
			resultConsumer.accept(person.toReference());
		}
	}

	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(String personUuid, UserRight editUserRight) {
		PersonDto personDto = personFacade.getPersonByUuid(personUuid);

		PersonEditForm editForm = new PersonEditForm(personDto.isPseudonymized());
		editForm.setValue(personDto);

		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<PersonEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(editUserRight),
			editForm.getFieldGroup());

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
		String personUuid,
		Disease disease,
		String diseaseDetails,
		UserRight editUserRight,
		final ViewMode viewMode) {
		PersonDto personDto = personFacade.getPersonByUuid(personUuid);

		PersonEditForm editForm = new PersonEditForm(personContext, disease, diseaseDetails, viewMode, personDto.isPseudonymized());
		editForm.setValue(personDto);

		final CommitDiscardWrapperComponent<PersonEditForm> editView = new CommitDiscardWrapperComponent<PersonEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(editUserRight),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					PersonDto dto = editForm.getValue();
					savePerson(dto);
				}
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
