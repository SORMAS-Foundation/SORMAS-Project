package de.symeda.sormas.ui.clinicalcourse;

import java.util.Collection;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ClinicalCourseController {

	public ClinicalCourseController() {

	}

	public void openClinicalVisitCreateForm(ClinicalCourseReferenceDto clinicalCourse, String caseUuid, Runnable callback) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.build(clinicalCourse, caze.getDisease());
		ClinicalVisitForm form = new ClinicalVisitForm(true, clinicalVisit.getDisease(),
				FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()),
				UserRight.CLINICAL_VISIT_CREATE);
		form.setValue(clinicalVisit);

		final CommitDiscardWrapperComponent<ClinicalVisitForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		view.setWidth(100, Unit.PERCENTAGE);
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					ClinicalVisitDto dto = form.getValue();
					dto = FacadeProvider.getClinicalVisitFacade().saveClinicalVisit(dto, caseUuid);
					Notification.show(I18nProperties.getString(Strings.messageClinicalVisitCreated), Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		});
		
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewClinicalVisit));
		// Clinical visit form is too big for typical screens
		popupWindow.setWidth(form.getWidth() + 90, Unit.PIXELS); 
		popupWindow.setHeight(80, Unit.PERCENTAGE); 
	}

	public void openClinicalVisitEditForm(ClinicalVisitIndexDto clinicalVisitIndex, String caseUuid, Runnable callback) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		ClinicalVisitDto clinicalVisit = FacadeProvider.getClinicalVisitFacade().getClinicalVisitByUuid(clinicalVisitIndex.getUuid());
		ClinicalVisitForm form = new ClinicalVisitForm(false, clinicalVisit.getDisease(), 
				FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()), 
				UserRight.CLINICAL_VISIT_EDIT);
		form.setValue(clinicalVisit);

		final CommitDiscardWrapperComponent<ClinicalVisitForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		view.setWidth(100, Unit.PERCENTAGE);
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingEditClinicalVisit));
		// Clinical visit form is too big for typical screens
		popupWindow.setWidth(form.getWidth() + 90, Unit.PIXELS); 
		popupWindow.setHeight(80, Unit.PERCENTAGE); 
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					ClinicalVisitDto dto = form.getValue();
					FacadeProvider.getClinicalVisitFacade().saveClinicalVisit(dto, caseUuid);
					popupWindow.close();
					Notification.show(I18nProperties.getString(Strings.messageClinicalVisitSaved), Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		});

		view.addDiscardListener(() -> popupWindow.close());

		if (UserProvider.getCurrent().hasUserRight(UserRight.CLINICAL_VISIT_DELETE)) {
			view.addDeleteListener(() -> {
				FacadeProvider.getClinicalVisitFacade().deleteClinicalVisit(clinicalVisit.getUuid());
				popupWindow.close();
				if (callback != null) {
					callback.run();
				}
			}, I18nProperties.getString(Strings.entityClinicalVisit));
		}
	}
	
	public void deleteAllSelectedClinicalVisits(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoClinicalVisitsSelected),
					I18nProperties.getString(Strings.messageNoClinicalVisitsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), selectedRows.size()), () -> {
				for (Object selectedRow : selectedRows) {
					FacadeProvider.getClinicalVisitFacade().deleteClinicalVisit(((ClinicalVisitIndexDto) selectedRow).getUuid());
				}
				callback.run();
				new Notification(I18nProperties.getString(Strings.headingClinicalVisitsDeleted),
						I18nProperties.getString(Strings.messageClinicalVisitsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
			});
		}
	}

}
