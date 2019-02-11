package de.symeda.sormas.ui.therapy;

import java.util.Collection;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import de.symeda.sormas.api.therapy.PrescriptionReferenceDto;
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
		PrescriptionForm form = new PrescriptionForm(true, UserRight.PRESCRIPTION_CREATE, false);
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

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(I18nProperties.getString(Strings.headingCreateNewPrescription)));
	}

	public void openPrescriptionEditForm(PrescriptionReferenceDto prescriptionReference, Runnable callback, boolean readOnly) {
		PrescriptionDto prescription = FacadeProvider.getTherapyFacade().getPrescriptionByUuid(prescriptionReference.getUuid());
		PrescriptionForm form = new PrescriptionForm(false, UserRight.PRESCRIPTION_EDIT, readOnly);
		form.setValue(prescription);

		final CommitDiscardWrapperComponent<PrescriptionForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(
				readOnly? Strings.entityPrescription : Strings.headingEditPrescription));

		view.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					PrescriptionDto dto = form.getValue();
					FacadeProvider.getTherapyFacade().savePrescription(dto);
					popupWindow.close();
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.PRESCRIPTION_DELETE)) {
			view.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getTherapyFacade().deletePrescription(prescription.getUuid(), UserProvider.getCurrent().getUserReference().getUuid());
					popupWindow.close();
					if (callback != null) {
						callback.run();
					}
				}
			}, I18nProperties.getString(Strings.entityPrescription));
		}
	}

	public void openPrescriptionEditForm(PrescriptionIndexDto prescriptionIndex, Runnable callback, boolean readOnly) {
		openPrescriptionEditForm(new PrescriptionReferenceDto(prescriptionIndex.getUuid()), callback, readOnly);
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

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewTreatment));
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

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewTreatment));
	}

	public void openTreatmentEditForm(TreatmentIndexDto treatmentIndex, Runnable callback) {
		TreatmentDto treatment = FacadeProvider.getTherapyFacade().getTreatmentByUuid(treatmentIndex.getUuid());
		TreatmentForm form = new TreatmentForm(false, UserRight.TREATMENT_EDIT);
		form.setValue(treatment);

		final CommitDiscardWrapperComponent<TreatmentForm> view = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());
		Window popupWindow = VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingEditTreatment));

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
			}, I18nProperties.getString(Strings.entityTreatment));
		}

		if (treatment.getPrescription() != null) {
			Button openPrescriptionButton = new Button(I18nProperties.getCaption(Captions.treatmentOpenPrescription));
			openPrescriptionButton.addClickListener(e -> {
				openPrescriptionEditForm(treatment.getPrescription(), null, true);
			});
			view.getButtonsPanel().addComponent(openPrescriptionButton, view.getButtonsPanel().getComponentIndex(view.getDiscardButton()));
			view.getButtonsPanel().setComponentAlignment(openPrescriptionButton, Alignment.MIDDLE_LEFT);
		}
	}

	public void deleteAllSelectedPrescriptions(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoPrescriptionsSelected),
					I18nProperties.getString(Strings.messageNoPrescriptionsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeletePrescriptions), selectedRows.size()), new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getTherapyFacade().deletePrescription(((PrescriptionIndexDto) selectedRow).getUuid(), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingPrescriptionsDeleted),
							I18nProperties.getString(Strings.messagePrescriptionsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}


	public void deleteAllSelectedTreatments(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoTreatmentsSelected),
					I18nProperties.getString(Strings.messageNoTreatmentsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteTreatments), selectedRows.size()), new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getTherapyFacade().deleteTreatment(((TreatmentIndexDto) selectedRow).getUuid(), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingTreatmentsDeleted),
							I18nProperties.getString(Strings.messageTreatmentsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
