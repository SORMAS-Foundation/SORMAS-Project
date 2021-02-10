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
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseDataView;
import de.symeda.sormas.ui.caze.CasesView;
import de.symeda.sormas.ui.caze.MergeCasesView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import org.apache.commons.lang3.StringUtils;

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
		PersonSelectionField personSelect = new PersonSelectionField(person, infoText);
		personSelect.setWidth(1024, Unit.PIXELS);

		if (personSelect.hasMatches()) {
			// TODO add user right parameter
			final CommitDiscardWrapperComponent<PersonSelectionField> component =
				new CommitDiscardWrapperComponent<PersonSelectionField>(personSelect);
			if (!saveNewPerson) {
				component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			}
			component.addCommitListener(new CommitListener() {

				@Override
				public void onCommit() {
					SimilarPersonDto selectedPerson = personSelect.getValue();
					if (selectedPerson != null) {
						if (resultConsumer != null) {
							resultConsumer.accept(selectedPerson.toReference());
						}
					} else {
						PersonDto savedPerson = personFacade.savePerson(person);
						resultConsumer.accept(savedPerson.toReference());
					}
				}
			});

			personSelect.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));
			personSelect.selectBestMatch();
		} else if (saveNewPerson) {
			PersonDto savedPerson = personFacade.savePerson(person);
			resultConsumer.accept(savedPerson.toReference());
		} else {
			resultConsumer.accept(person.toReference());
		}
	}

	public CommitDiscardWrapperComponent<PersonEditForm> getPersonEditComponent(
		String personUuid,
		UserRight editUserRight) {
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
		PersonDto existingPerson = FacadeProvider.getPersonFacade().getPersonByUuid(personDto.getUuid());
		List<CaseDataDto> personCases = FacadeProvider.getCaseFacade().getAllCasesOfPerson(personDto.getUuid());

		onPersonChanged(existingPerson, personDto);

		personFacade.savePerson(personDto);

		// Check whether the classification of any of this person's cases has changed
		CaseClassification newClassification = null;
		for (CaseDataDto personCase : personCases) {
			CaseDataDto updatedPersonCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(personCase.getUuid());
			if (personCase.getCaseClassification() != updatedPersonCase.getCaseClassification()
				&& updatedPersonCase.getClassificationUser() == null) {
				newClassification = updatedPersonCase.getCaseClassification();
				break;
			}
		}

		if (newClassification != null) {
			Notification notification = new Notification(
				String.format(I18nProperties.getString(Strings.messagePersonSavedClassificationChanged), newClassification.toString()),
				Type.WARNING_MESSAGE);
			notification.setDelayMsec(-1);
			notification.show(Page.getCurrent());
		} else {
			Notification.show(I18nProperties.getString(Strings.messagePersonSaved), Type.WARNING_MESSAGE);
		}

		SormasUI.refreshView();
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
}
