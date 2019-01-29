package de.symeda.sormas.ui.therapy;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TherapyController {
	
	public TherapyController() {
		
	}
	
	public void openPrescriptionCreateForm(TherapyDto therapy, Runnable callback) {
		PrescriptionForm form = new PrescriptionForm(true, UserRight.PRESCRIPTION_CREATE);
		form.setValue(PrescriptionDto.buildPrescription(therapy));
		final CommitDiscardWrapperComponent<PrescriptionForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					PrescriptionDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().savePrescription(dto);
					callback.run();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString("createNewPrescription"));
	}
	
	public void openPrescriptionEditForm(PrescriptionIndexDto prescriptionIndex, Runnable callback) {
		PrescriptionDto prescription = FacadeProvider.getTherapyFacade().getPrescriptionByUuid(prescriptionIndex.getUuid());
		PrescriptionForm form = new PrescriptionForm(false, UserRight.PRESCRIPTION_EDIT);
		form.setValue(prescription);
		
		final CommitDiscardWrapperComponent<PrescriptionForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString("editPrescription"));
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					PrescriptionDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().savePrescription(dto);
					popupWindow.close();
					callback.run();
				}
			}
		});
		
		view.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.PRESCRIPTION_DELETE)) {
			view.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getTherapyFacade().deletePrescription(prescription.getUuid(), UserProvider.getCurrent().getUserReference().getUuid());
					popupWindow.close();
					callback.run();
				}
			}, I18nProperties.getString("prescription"));
		}
	}
	
	public void openTreatmentCreateForm(TherapyDto therapy, Runnable callback) {
		TreatmentForm form = new TreatmentForm(true, UserRight.TREATMENT_CREATE);
		form.setValue(TreatmentDto.buildTreatment(therapy));
		final CommitDiscardWrapperComponent<TreatmentForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().saveTreatment(dto);
					callback.run();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString("createNewTreatment"));
	}
	
	public void openTreatmentCreateForm(PrescriptionDto prescription, Runnable callback) {
		TreatmentForm form = new TreatmentForm(true, UserRight.TREATMENT_CREATE);
		form.setValue(TreatmentDto.buildTreatment(prescription));
		final CommitDiscardWrapperComponent<TreatmentForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().saveTreatment(dto);
					callback.run();
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString("createNewTreatment"));
	}
	
	public void openTreatmentEditForm(TreatmentIndexDto treatmentIndex, Runnable callback) {
		TreatmentDto treatment = FacadeProvider.getTherapyFacade().getTreatmentByUuid(treatmentIndex.getUuid());
		TreatmentForm form = new TreatmentForm(false, UserRight.TREATMENT_EDIT);
		form.setValue(treatment);
		
		final CommitDiscardWrapperComponent<TreatmentForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString("editTreatment"));
		
		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().saveTreatment(dto);
					popupWindow.close();
					callback.run();
				}
			}
		});
		
		view.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.TREATMENT_DELETE)) {
			view.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getTherapyFacade().deleteTreatment(treatment.getUuid(), UserProvider.getCurrent().getUserReference().getUuid());
					popupWindow.close();
					callback.run();
				}
			}, I18nProperties.getString("treatment"));
		}
	}

}
