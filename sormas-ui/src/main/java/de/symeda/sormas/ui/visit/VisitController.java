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
package de.symeda.sormas.ui.visit;

import java.util.Collection;
import java.util.function.Consumer;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import javax.validation.constraints.NotNull;

public class VisitController {

	public VisitController() {

	}

	public void editVisit(@NotNull final SormasUI ui,
						  String visitUuid,
						  ContactReferenceDto contactRef,
						  CaseReferenceDto caseRef,
						  Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = FacadeProvider.getVisitFacade().getVisitByUuid(visitUuid);
		VisitEditForm editForm;
		if (contactRef != null) {
			ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());
			PersonDto visitPerson = FacadeProvider.getPersonFacade().getPersonByUuid(visit.getPerson().getUuid());
			editForm = new VisitEditForm(visit.getDisease(), contact, visitPerson, false, !contact.isPseudonymized());
		} else if (caseRef != null) {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
			PersonDto visitPerson = FacadeProvider.getPersonFacade().getPersonByUuid(visit.getPerson().getUuid());
			editForm = new VisitEditForm(visit.getDisease(), caze, visitPerson, false, !caze.isPseudonymized());
		} else {
			throw new IllegalArgumentException("Cannot edit a visit without contact nor case");
		}
		editForm.setValue(visit);
		boolean canEdit = VisitOrigin.USER.equals(visit.getOrigin());
		editVisit(ui, editForm, visit.toReference(), doneConsumer, canEdit);
	}

	private void editVisit(@NotNull final SormasUI ui, VisitEditForm editForm, VisitReferenceDto visitRef, Consumer<VisitReferenceDto> doneConsumer, boolean canEdit) {

		final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<>(
			editForm,
			ui.getUserProvider().hasUserRight(UserRight.VISIT_EDIT),
			editForm.getFieldGroup());
		editView.setWidth(100, Unit.PERCENTAGE);

		if (!canEdit) {
			editView.setEnabled(false);
		}

		Window window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditVisit));
		// visit form is too big for typical screens
		window.setWidth(editForm.getWidth() + 90, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE);

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				FacadeProvider.getVisitFacade().saveVisit(editForm.getValue());
				if (doneConsumer != null) {
					doneConsumer.accept(visitRef);
				}
			}
		});

		if (ui.getUserProvider().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getVisitFacade().deleteVisit(visitRef.getUuid());
				UI.getCurrent().removeWindow(window);
				if (doneConsumer != null) {
					doneConsumer.accept(visitRef);
				}
			}, I18nProperties.getCaption(VisitDto.I18N_PREFIX));
		}
	}

	private void createVisit(@NotNull final SormasUI ui, VisitEditForm createForm, Consumer<VisitReferenceDto> doneConsumer) {
		final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(
				createForm,
				ui.getUserProvider().hasUserRight(UserRight.VISIT_CREATE),
				createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				VisitDto dto = createForm.getValue();
				dto = FacadeProvider.getVisitFacade().saveVisit(dto);
				if (doneConsumer != null) {
					doneConsumer.accept(dto.toReference());
				}
			}
		});

		Window window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewVisit));
		// visit form is too big for typical screens
		window.setWidth(createForm.getWidth() + 64 + 24, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE);
	}
	
	public void createVisit(@NotNull final SormasUI ui, ContactReferenceDto contactRef, Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = createNewVisit(ui, contactRef);
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());
		PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
		VisitEditForm createForm = new VisitEditForm(visit.getDisease(), contact, contactPerson, true, true);
		createForm.setValue(visit);

		createVisit(ui, createForm, doneConsumer);
	}

	public void createVisit(@NotNull final SormasUI ui, CaseReferenceDto caseRef, Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = createNewVisit(ui, caseRef);
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
		VisitEditForm createForm = new VisitEditForm(visit.getDisease(), caze, person, true, true);
		createForm.setValue(visit);

		createVisit(ui, createForm, doneConsumer);
	}

	private VisitDto createNewVisit(@NotNull final SormasUI ui, PersonReferenceDto personRef, Disease disease) {
		VisitDto visit = VisitDto.build(personRef, disease, VisitOrigin.USER);
		UserReferenceDto userReference = ui.getUserProvider().getUserReference();
		visit.setVisitUser(userReference);
		return visit;
	}

	private VisitDto createNewVisit(@NotNull final SormasUI ui, ContactReferenceDto contactRef) {
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactRef.getUuid());
		return createNewVisit(ui, contact.getPerson(), contact.getDisease());
	}

	private VisitDto createNewVisit(@NotNull final SormasUI ui, CaseReferenceDto caseRef) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		return createNewVisit(ui, caze.getPerson(), caze.getDisease());
	}

	public void deleteAllSelectedItems(Collection<VisitIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoVisitsSelected),
				I18nProperties.getString(Strings.messageNoVisitsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteVisits), selectedRows.size()), () -> {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getVisitFacade().deleteVisit(((VisitIndexDto) selectedRow).getUuid());
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingVisitsDeleted),
						I18nProperties.getString(Strings.messageVisitsDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}
}
