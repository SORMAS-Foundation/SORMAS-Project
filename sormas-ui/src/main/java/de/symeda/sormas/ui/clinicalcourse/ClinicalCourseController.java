package de.symeda.sormas.ui.clinicalcourse;

import java.util.Collection;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class ClinicalCourseController {

	public ClinicalCourseController() {

	}

	public void openClinicalVisitCreateForm(ClinicalCourseDto clinicalCourse, String caseUuid, Runnable callback) {
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		ClinicalVisitDto clinicalVisit = ClinicalVisitDto.buildClinicalVisit(clinicalCourse, new SymptomsDto(), caze.getDisease(), caze.getPerson());
		ClinicalVisitForm form = new ClinicalVisitForm(true, clinicalVisit.getDisease(),
				FacadeProvider.getPersonFacade().getPersonByUuid(clinicalVisit.getPerson().getUuid()),
				UserRight.CLINICAL_VISIT_CREATE);
		form.setValue(clinicalVisit);

		final CommitDiscardWrapperComponent<ClinicalVisitForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		view.setWidth(100, Unit.PERCENTAGE);
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					ClinicalVisitDto dto = form.getValue();
					dto = FacadeProvider.getClinicalCourseFacade().saveClinicalVisit(dto, caseUuid);
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
		ClinicalVisitDto clinicalVisit = FacadeProvider.getClinicalCourseFacade().getClinicalVisitByUuid(clinicalVisitIndex.getUuid());
		ClinicalVisitForm form = new ClinicalVisitForm(false, clinicalVisit.getDisease(), 
				FacadeProvider.getPersonFacade().getPersonByUuid(clinicalVisit.getPerson().getUuid()), 
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
					FacadeProvider.getClinicalCourseFacade().saveClinicalVisit(dto, caseUuid);
					popupWindow.close();
					Notification.show(I18nProperties.getString(Strings.messageClinicalVisitSaved), Type.TRAY_NOTIFICATION);
					if (callback != null) {
						callback.run();
					}
				}
			}
		});

		view.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.CLINICAL_VISIT_DELETE)) {
			view.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getClinicalCourseFacade().deleteClinicalVisit(clinicalVisit.getUuid(), UserProvider.getCurrent().getUserReference().getUuid());
					popupWindow.close();
					if (callback != null) {
						callback.run();
					}
				}
			}, I18nProperties.getString(Strings.entityClinicalVisit));
		}
	}
	
	public CommitDiscardWrapperComponent<ClinicalCourseForm> getClinicalCourseView(String caseUuid, ViewMode viewMode) {
		ClinicalCourseForm form = new ClinicalCourseForm(UserRight.CLINICAL_COURSE_EDIT);
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		form.setValue(caze.getClinicalCourse());
		
		final CommitDiscardWrapperComponent<ClinicalCourseForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					ClinicalCourseDto dto = form.getValue();
					FacadeProvider.getClinicalCourseFacade().saveClinicalCourse(dto);
					Notification.show(I18nProperties.getString(Strings.messageClinicalCourseSaved), Type.TRAY_NOTIFICATION);
					ControllerProvider.getCaseController().navigateToView(ClinicalCourseView.VIEW_NAME, caseUuid, viewMode);
				}
			}
		});
		
		return view;
	}
	
	public void deleteAllSelectedClinicalVisits(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoClinicalVisitsSelected),
					I18nProperties.getString(Strings.messageNoClinicalVisitsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteClinicalVisits), selectedRows.size()), new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getClinicalCourseFacade().deleteClinicalVisit(((ClinicalVisitIndexDto) selectedRow).getUuid(), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingClinicalVisitsDeleted),
							I18nProperties.getString(Strings.messageClinicalVisitsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
