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
import java.util.Date;
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
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitIndexDto;
import de.symeda.sormas.api.visit.VisitLogic;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class VisitController {

	public VisitController() {

	}

	public void editVisit(
		String visitUuid,
		ContactReferenceDto contactRef,
		CaseReferenceDto caseRef,
		Consumer<VisitReferenceDto> doneConsumer,
		boolean isEditAllowed) {
		VisitDto visit = FacadeProvider.getVisitFacade().getVisitByUuid(visitUuid);
		VisitEditForm editForm;
		Date startDate = null;
		Date endDate = null;
		if (contactRef != null) {
			ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactRef.getUuid());
			PersonDto visitPerson = FacadeProvider.getPersonFacade().getByUuid(visit.getPerson().getUuid());
			editForm = new VisitEditForm(visit.getDisease(), contact, visitPerson, false);
			startDate = ContactLogic.getStartDate(contact);
			endDate = ContactLogic.getEndDate(contact);
		} else if (caseRef != null) {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
			PersonDto visitPerson = FacadeProvider.getPersonFacade().getByUuid(visit.getPerson().getUuid());
			editForm = new VisitEditForm(visit.getDisease(), caze, visitPerson, false);
			startDate = CaseLogic.getStartDate(caze);
			endDate = CaseLogic.getEndDate(caze);
		} else {
			throw new IllegalArgumentException("Cannot edit a visit without contact nor case");
		}
		editForm.setValue(visit);
		boolean canEdit = VisitOrigin.USER.equals(visit.getOrigin()) && isEditAllowed;
		editVisit(
			editForm,
			visit.toReference(),
			doneConsumer,
			canEdit,
			VisitLogic.getAllowedStartDate(startDate),
			VisitLogic.getAllowedEndDate(endDate));
	}

	private void editVisit(
		VisitEditForm editForm,
		VisitReferenceDto visitRef,
		Consumer<VisitReferenceDto> doneConsumer,
		boolean canEdit,
		Date allowedStartDate,
		Date allowedEndDate) {

		final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<>(
			editForm,
			UserProvider.getCurrent().hasUserRight(UserRight.VISIT_EDIT) && canEdit,
			editForm.getFieldGroup());
		editView.setWidth(100, Unit.PERCENTAGE);

		Window window;
		if (!canEdit) {
			editView.getButtonsPanel().setVisible(false);
			window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingViewVisit));
		} else {
			window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditVisit));
		}

		// visit form is too big for typical screens
		window.setWidth(editForm.getWidth() + 90, Unit.PIXELS);
		window.setHeight(80, Unit.PERCENTAGE);

		editView.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				FacadeProvider.getVisitFacade().saveVisit(editForm.getValue(), allowedStartDate, allowedEndDate);
				if (doneConsumer != null) {
					doneConsumer.accept(visitRef);
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.VISIT_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getVisitFacade().deleteVisit(visitRef.getUuid());
				UI.getCurrent().removeWindow(window);
				if (doneConsumer != null) {
					doneConsumer.accept(visitRef);
				}
			}, I18nProperties.getCaption(VisitDto.I18N_PREFIX));
		}
	}

	private void createVisit(VisitEditForm createForm, Consumer<VisitReferenceDto> doneConsumer, Date allowedStartDate, Date allowedEndDate) {
		final CommitDiscardWrapperComponent<VisitEditForm> editView = new CommitDiscardWrapperComponent<VisitEditForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.VISIT_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				VisitDto dto = createForm.getValue();
				dto = FacadeProvider.getVisitFacade().saveVisit(dto, allowedStartDate, allowedEndDate);
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

	public void createVisit(ContactReferenceDto contactRef, Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = createNewVisit(contactRef);
		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactRef.getUuid());
		PersonDto contactPerson = FacadeProvider.getPersonFacade().getByUuid(contact.getPerson().getUuid());
		VisitEditForm createForm = new VisitEditForm(visit.getDisease(), contact, contactPerson, true);
		createForm.setValue(visit);

		createVisit(
			createForm,
			doneConsumer,
			VisitLogic.getAllowedStartDate(ContactLogic.getStartDate(contact)),
			VisitLogic.getAllowedEndDate(ContactLogic.getEndDate(contact)));
	}

	public void createVisit(CaseReferenceDto caseRef, Consumer<VisitReferenceDto> doneConsumer) {
		VisitDto visit = createNewVisit(caseRef);
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(caze.getPerson().getUuid());
		VisitEditForm createForm = new VisitEditForm(visit.getDisease(), caze, person, true);
		createForm.setValue(visit);

		createVisit(
			createForm,
			doneConsumer,
			VisitLogic.getAllowedStartDate(CaseLogic.getStartDate(caze)),
			VisitLogic.getAllowedEndDate(CaseLogic.getEndDate(caze)));
	}

	private VisitDto createNewVisit(PersonReferenceDto personRef, Disease disease) {
		VisitDto visit = VisitDto.build(personRef, disease, VisitOrigin.USER);
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		visit.setVisitUser(userReference);
		return visit;
	}

	private VisitDto createNewVisit(ContactReferenceDto contactRef) {
		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactRef.getUuid());
		return createNewVisit(contact.getPerson(), contact.getDisease());
	}

	private VisitDto createNewVisit(CaseReferenceDto caseRef) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		return createNewVisit(caze.getPerson(), caze.getDisease());
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
