package de.symeda.sormas.ui.therapy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
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
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TherapyController {

	public TherapyController() {

	}

	public void openPrescriptionCreateForm(TherapyReferenceDto therapy, Runnable callback) {
		PrescriptionForm form = new PrescriptionForm(true, false, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		form.setValue(PrescriptionDto.buildPrescription(therapy));
		final CommitDiscardWrapperComponent<PrescriptionForm> view =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.PRESCRIPTION_CREATE), form.getFieldGroup());

		view.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					PrescriptionDto dto = form.getValue();
					FacadeProvider.getPrescriptionFacade().savePrescription(dto);
					Notification.show(I18nProperties.getString(Strings.messagePrescriptionCreated), Type.TRAY_NOTIFICATION);
					callback.run();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewPrescription));
	}

	public void openPrescriptionEditForm(
		PrescriptionReferenceDto prescriptionReference,
		Runnable callback,
		boolean isEditAllowed,
		boolean isDeleteAllowed) {
		PrescriptionDto prescription = FacadeProvider.getPrescriptionFacade().getPrescriptionByUuid(prescriptionReference.getUuid());
		PrescriptionForm form = new PrescriptionForm(false, !isEditAllowed, prescription.isPseudonymized(), prescription.isInJurisdiction());
		form.setValue(prescription);

		boolean isEditOrDeleteAllowed = isEditAllowed || isDeleteAllowed;
		final CommitDiscardWrapperComponent<PrescriptionForm> view =
			new CommitDiscardWrapperComponent<>(form, isEditOrDeleteAllowed, form.getFieldGroup());
		Window popupWindow = VaadinUiUtil
			.showModalPopupWindow(view, I18nProperties.getString(!isEditAllowed ? Strings.headingViewPrescription : Strings.headingEditPrescription));

		if (isEditOrDeleteAllowed) {
			view.addCommitListener(new CommitListener() {

				@Override
				public void onCommit() {
					if (!form.getFieldGroup().isModified()) {
						PrescriptionDto dto = form.getValue();
						FacadeProvider.getPrescriptionFacade().savePrescription(dto);
						popupWindow.close();
						Notification.show(I18nProperties.getString(Strings.messagePrescriptionSaved), Type.TRAY_NOTIFICATION);
						if (callback != null) {
							callback.run();
						}
					}
				}
			});

			view.addDiscardListener(() -> popupWindow.close());

			if (isDeleteAllowed) {
				view.addDeleteListener(new DeleteListener() {

					@Override
					public void onDelete() {
						List<String> prescriptionUuids = new ArrayList<>();
						prescriptionUuids.add(prescription.getUuid());
						List<TreatmentIndexDto> treatmentDtos = FacadeProvider.getTreatmentFacade().getTreatmentForPrescription(prescriptionUuids);
						if (treatmentDtos.size() > 0) {
							handleDeletePrescriptionWithTreatments(treatmentDtos, prescriptionUuids);
						} else {
							FacadeProvider.getPrescriptionFacade().deletePrescription(prescription.getUuid());
						}
						popupWindow.close();
						if (callback != null) {
							callback.run();
						}
					}
				}, I18nProperties.getString(Strings.entityPrescription));
			}
			view.restrictEditableComponentsOnEditView(
				UserRight.CASE_EDIT,
				UserRight.PRESCRIPTION_EDIT,
				UserRight.PRESCRIPTION_DELETE,
				null,
				prescription.isInJurisdiction());
		}
		view.getButtonsPanel().setVisible(isEditOrDeleteAllowed);
	}

	private void handleDeletePrescriptionWithTreatments(List<TreatmentIndexDto> treatmentIndexDtos, List<String> prescriptionUuids) {
		Consumer<Boolean> resultConsumer = new Consumer<Boolean>() {

			@Override
			public void accept(Boolean option) {
				List<String> treatmentUuids = treatmentIndexDtos.stream().map(t -> t.getUuid()).collect(Collectors.toList());
				if (Boolean.TRUE.equals(option)) {
					//delete just prescription and leave the treatments standalone
					FacadeProvider.getTreatmentFacade().unlinkPrescriptionFromTreatments(treatmentUuids);

				} else {
					//delete the prescription and all the treatments assign with
					FacadeProvider.getTreatmentFacade().deleteTreatments(treatmentUuids);
				}
				for (String prescriptionUuid : prescriptionUuids) {
					FacadeProvider.getPrescriptionFacade().deletePrescription(prescriptionUuid);
				}
				SormasUI.refreshView();
			}
		};
		VaadinUiUtil.showChooseOptionPopup(
			I18nProperties.getCaption(Captions.prescriptionWithTreatmentTitleDelete),
			new Label(I18nProperties.getString(Strings.confirmationDeletePrescriptionWithTreatment)),
			I18nProperties.getCaption(Captions.prescriptionAlone),
			I18nProperties.getCaption(Captions.prescriptionWithTreatment),
			650,
			resultConsumer,
			true);
	}

	public void openPrescriptionEditForm(PrescriptionIndexDto prescriptionIndex, Runnable callback, boolean isEditAllowed, boolean isDeleteAllowed) {
		openPrescriptionEditForm(new PrescriptionReferenceDto(prescriptionIndex.getUuid()), callback, isEditAllowed, isDeleteAllowed);
	}

	public void openTreatmentCreateForm(TherapyReferenceDto therapy, Runnable callback) {
		TreatmentForm form = new TreatmentForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		form.setValue(TreatmentDto.build(therapy));
		final CommitDiscardWrapperComponent<TreatmentForm> view =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.TREATMENT_CREATE), form.getFieldGroup());

		view.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTreatmentFacade().saveTreatment(dto);
					Notification.show(I18nProperties.getString(Strings.messageTreatmentCreated), Type.TRAY_NOTIFICATION);
					callback.run();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewTreatment));
	}

	public void openTreatmentCreateForm(PrescriptionDto prescription, Runnable callback) {
		TreatmentForm form = new TreatmentForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		form.setValue(TreatmentDto.build(prescription));
		final CommitDiscardWrapperComponent<TreatmentForm> view =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.TREATMENT_CREATE), form.getFieldGroup());

		view.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTreatmentFacade().saveTreatment(dto);
					Notification.show(I18nProperties.getString(Strings.messageTreatmentCreated), Type.TRAY_NOTIFICATION);
					callback.run();
				}
			}
		});

		VaadinUiUtil.showModalPopupWindow(view, I18nProperties.getString(Strings.headingCreateNewTreatment));
	}

	public void openTreatmentEditForm(
		TreatmentIndexDto treatmentIndex,
		Runnable callback,
		boolean isEditAllowed,
		boolean isDeleteAllowed,
		boolean isPrescriptionEditAllowed,
		boolean isPrescriptionDeleteAllowed) {
		TreatmentDto treatment = FacadeProvider.getTreatmentFacade().getTreatmentByUuid(treatmentIndex.getUuid());

		boolean isEditOrDeleteAllowed = isEditAllowed || isDeleteAllowed;
		TreatmentForm form = new TreatmentForm(false, treatment.isPseudonymized(), treatment.isInJurisdiction());
		form.setValue(treatment);

		final CommitDiscardWrapperComponent<TreatmentForm> view =
			new CommitDiscardWrapperComponent<>(form, isEditOrDeleteAllowed, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil
			.showModalPopupWindow(view, I18nProperties.getString(!isEditAllowed ? Strings.headingViewTreatment : Strings.headingEditTreatment));

		if (isEditOrDeleteAllowed) {
			view.addCommitListener(() -> {
				if (!form.getFieldGroup().isModified()) {
					TreatmentDto dto = form.getValue();
					FacadeProvider.getTreatmentFacade().saveTreatment(dto);
					popupWindow.close();
					Notification.show(I18nProperties.getString(Strings.messageTreatmentSaved), Type.TRAY_NOTIFICATION);
					callback.run();
				}
			});

			view.addDiscardListener(popupWindow::close);

			if (isDeleteAllowed) {
				view.addDeleteListener(() -> {
					FacadeProvider.getTreatmentFacade().deleteTreatment(treatment.getUuid());
					popupWindow.close();
					callback.run();
				}, I18nProperties.getString(Strings.entityTreatment));
			}

			view.restrictEditableComponentsOnEditView(
				UserRight.CASE_EDIT,
				UserRight.TREATMENT_EDIT,
				UserRight.TREATMENT_DELETE,
				null,
				treatment.isInJurisdiction());
		}
		view.getButtonsPanel().setVisible(isEditOrDeleteAllowed);

		if (treatment.getPrescription() != null) {
			Button openPrescriptionButton = ButtonHelper.createButton(Captions.treatmentOpenPrescription, e -> {
				openPrescriptionEditForm(treatment.getPrescription(), null, isPrescriptionEditAllowed, isPrescriptionDeleteAllowed);
				popupWindow.close();
				callback.run();
			});

			view.getButtonsPanel().addComponent(openPrescriptionButton, view.getButtonsPanel().getComponentIndex(view.getDiscardButton()));
			view.getButtonsPanel().setComponentAlignment(openPrescriptionButton, Alignment.MIDDLE_LEFT);
		}
	}

	public void deleteAllSelectedPrescriptions(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoPrescriptionsSelected),
				I18nProperties.getString(Strings.messageNoPrescriptionsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeletePrescriptions), selectedRows.size()),
				new Runnable() {

					public void run() {
						List<String> prescriptionUuids = new ArrayList<>();
						for (Object selectedRow : selectedRows) {
							prescriptionUuids.add(((PrescriptionIndexDto) selectedRow).getUuid());

						}

						List<TreatmentIndexDto> treatmentDtos = FacadeProvider.getTreatmentFacade().getTreatmentForPrescription(prescriptionUuids);
						if (treatmentDtos.size() > 0) {
							handleDeletePrescriptionWithTreatments(treatmentDtos, prescriptionUuids);
						} else {
							for (String prescriptionUuid : prescriptionUuids) {
								FacadeProvider.getPrescriptionFacade().deletePrescription(prescriptionUuid);
							}
						}

						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingPrescriptionsDeleted),
							I18nProperties.getString(Strings.messagePrescriptionsDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void deleteAllSelectedTreatments(Collection<Object> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoTreatmentsSelected),
				I18nProperties.getString(Strings.messageNoTreatmentsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteTreatments), selectedRows.size()),
				new Runnable() {

					public void run() {
						for (Object selectedRow : selectedRows) {
							FacadeProvider.getTreatmentFacade().deleteTreatment(((TreatmentIndexDto) selectedRow).getUuid());
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingTreatmentsDeleted),
							I18nProperties.getString(Strings.messageTreatmentsDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}
}
