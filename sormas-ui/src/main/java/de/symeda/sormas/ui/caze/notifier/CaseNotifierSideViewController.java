/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.caze.notifier;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Controller for managing the side view of case notifiers.
 * Provides methods to retrieve notifier components, surveillance reports, and treatments.
 */
public class CaseNotifierSideViewController {

    /**
     * Retrieves the notifier component for a case.
     *
     * @param caze
     *            the case data
     * @return notifier component with associated report and treatment details
     */
    public CaseNotifierSideViewContent getNotifierComponent(CaseDataDto caze, SurveillanceReportDto surveillanceReport) {

        if (caze == null) {
            throw new IllegalArgumentException("Caze is null");
        }

        if (caze.getNotifier() == null) {
            throw new IllegalArgumentException("Case Notifier is null");
        }

        final NotifierDto notifier =
            FacadeProvider.getNotifierFacade().getByUuidAndTime(caze.getNotifier().getUuid(), caze.getNotifier().getVersionDate().toInstant());

        return new CaseNotifierSideViewContent(surveillanceReport, notifier);
    }

    /**
     * Retrieves the oldest surveillance report for a case.
     * 
     * @param caze
     *            the case
     * @return oldest report or null if none found
     */
    public SurveillanceReportDto getOldestDoctorDeclarationReport(CaseDataDto caze) {
        return getOldestDoctorDeclarationReport(caze.toReference());
    }

    /**
     * Retrieves the oldest surveillance report for a case reference.
     *
     * @param caze
     *            the case reference
     * @return oldest report or null if none found
     */
    public SurveillanceReportDto getOldestDoctorDeclarationReport(CaseReferenceDto caze) {

        // Get reports with DOCTOR reporting type
        SurveillanceReportCriteria doctorCriteria = new SurveillanceReportCriteria();
        doctorCriteria.caze(caze);
        doctorCriteria.setReportingType(ReportingType.DOCTOR);
        List<SurveillanceReportDto> doctorReports = FacadeProvider.getSurveillanceReportFacade().getIndexList(doctorCriteria, null, null, null);

        // Filter to get the oldest report
        return doctorReports.stream()
            .filter(r -> r.getReportDate() != null)
            .min(Comparator.comparing(SurveillanceReportDto::getReportDate))
            .orElse(null);
    }

    public boolean hasNoNotificationReports(CaseReferenceDto caze) {
        // Get all reports criteria
        SurveillanceReportCriteria caseCriteria = new SurveillanceReportCriteria();
        caseCriteria.caze(caze);
        // If any reports exist, return false
        return FacadeProvider.getSurveillanceReportFacade().count(caseCriteria) == 0;
    }

    public boolean hasOnlyPhoneNotificationReports(CaseReferenceDto caze) {
        // Get reports with PHONE_NOTIFICATION reporting type
        final SurveillanceReportCriteria phoneCriteria = new SurveillanceReportCriteria();
        phoneCriteria.caze(caze);
        phoneCriteria.setReportingType(ReportingType.PHONE_NOTIFICATION);
        final long phoneRepCount = FacadeProvider.getSurveillanceReportFacade().count(phoneCriteria);

        final SurveillanceReportCriteria caseCriteria = new SurveillanceReportCriteria();
        caseCriteria.caze(caze);
        final long caseRepCount = FacadeProvider.getSurveillanceReportFacade().count(caseCriteria);

        return phoneRepCount > 0 && phoneRepCount == caseRepCount;
    }

    public boolean hasPhoneNotification(CaseReferenceDto caze) {
        SurveillanceReportCriteria phoneCriteria = new SurveillanceReportCriteria();
        phoneCriteria.caze(caze);
        phoneCriteria.setReportingType(ReportingType.PHONE_NOTIFICATION);
        return FacadeProvider.getSurveillanceReportFacade().count(phoneCriteria) != 0;
    }

    /**
     * Retrieves the newest PHONE_NOTIFICATION surveillance report for a case.
     *
     * @param caze
     *            the case reference
     * @return the newest PHONE_NOTIFICATION report or null if none found
     */
    public SurveillanceReportDto getNewestPhoneNotificationReport(CaseReferenceDto caze) {
        SurveillanceReportCriteria criteria = new SurveillanceReportCriteria();
        criteria.caze(caze);
        criteria.setReportingType(ReportingType.PHONE_NOTIFICATION);
        List<SurveillanceReportDto> phoneReports = FacadeProvider.getSurveillanceReportFacade().getIndexList(criteria, null, null, null);

        return phoneReports.stream()
            .filter(r -> r.getReportDate() != null)
            .max(Comparator.comparing(SurveillanceReportDto::getReportDate))
            .orElse(null);
    }

    /**
     * Retrieves the newest PHONE_NOTIFICATION surveillance report for a case.
     *
     * @param caze
     *            the case data
     * @return the newest PHONE_NOTIFICATION report or null if none found
     */
    public SurveillanceReportDto getNewestPhoneNotificationReport(CaseDataDto caze) {
        return getNewestPhoneNotificationReport(caze.toReference());
    }

    /**
     * Opens a dialog to create a new phone notification surveillance report for a case.
     * Creates a SurveillanceReport with PHONE_NOTIFICATION reporting type.
     *
     * @param caseRef
     *            case reference
     * @param callback
     *            callback to run after successful save or dialog close
     */
    public void createPhoneNotification(CaseReferenceDto caseRef, Runnable callback) {
        this.createPhoneNotification(FacadeProvider.getCaseFacade().getByUuid(caseRef.getUuid()), callback);
    }

    /**
     * Opens a dialog to create a new phone notification surveillance report.
     * Creates a SurveillanceReport with PHONE_NOTIFICATION reporting type.
     *
     * @param caze
     *            the case for which to create a notifier
     * @param callback
     *            callback to run after successful save or dialog close
     */
    public void createPhoneNotification(CaseDataDto caze, Runnable callback) {
        NotifierDto newNotifier = new NotifierDto();

        openEditWindow(caze, newNotifier, I18nProperties.getCaption(Captions.Notification_createNotification), callback, true);
    }

    /**
     * Opens a dialog to edit an existing phone notification.
     * Edits the phone notification surveillance report.
     *
     * @param caseRef
     * @param callback
     * @param isEditAllowed
     */
    public void editPhoneNotification(CaseReferenceDto caseRef, Runnable callback, boolean isEditAllowed) {
        this.editPhoneNotification(FacadeProvider.getCaseFacade().getByUuid(caseRef.getUuid()), callback, isEditAllowed);
    }

    /**
     * Opens a dialog to edit an existing phone notification.
     * Edits the phone notification surveillance report.
     *
     * @param caze
     *            the case containing the notifier to edit
     * @param callback
     *            callback to run after successful save or dialog close
     * @param isEditAllowed
     *            whether editing is allowed for this user
     */
    public void editPhoneNotification(CaseDataDto caze, Runnable callback, boolean isEditAllowed) {
        if (caze.getNotifier() == null) {
            return;
        }

        // We only edit the current version
        NotifierDto notifier = FacadeProvider.getNotifierFacade().getByUuid(caze.getNotifier().getUuid());

        openEditWindow(
            caze,
            notifier,
            isEditAllowed
                ? I18nProperties.getCaption(Captions.Notification_editNotification)
                : I18nProperties.getCaption(Captions.Notification_viewNotification),
            callback,
            isEditAllowed);
    }

    /**
     * Opens a read-only dialog to view a notifier.
     *
     * @param caze
     *            the case containing the notifier to view
     * @param callback
     *            callback to run after dialog close
     */
    public void viewPhoneNotification(CaseDataDto caze, Runnable callback) {
        editPhoneNotification(caze, callback, false);
    }

    /**
     * Opens the notifier edit/view window.
     *
     * @param caze
     *            the case data
     * @param notifier
     *            the notifier to edit/view
     * @param title
     *            the dialog title
     * @param callback
     *            callback to run after successful save or dialog close
     * @param isEditAllowed
     *            whether editing is allowed
     */
    private void openEditWindow(CaseDataDto caze, NotifierDto notifier, String title, Runnable callback, boolean isEditAllowed) {

        // Get or create PHONE_NOTIFICATION surveillance report
        SurveillanceReportCriteria criteria = new SurveillanceReportCriteria();
        criteria.caze(caze.toReference());
        criteria.setReportingType(ReportingType.PHONE_NOTIFICATION);
        List<SurveillanceReportDto> existingReports = FacadeProvider.getSurveillanceReportFacade().getIndexList(criteria, null, null, null);

        SurveillanceReportDto surveillanceReport;
        if (existingReports.isEmpty()) {
            // Create new report
            surveillanceReport = SurveillanceReportDto.build(caze.toReference(), FacadeProvider.getUserFacade().getCurrentUserAsReference());
            surveillanceReport.setReportingType(ReportingType.PHONE_NOTIFICATION);
            surveillanceReport.setReportDate(new Date());
        } else {
            // Use existing report (newest one if multiple exist)
            surveillanceReport = getNewestPhoneNotificationReport(caze);
        }

        final CaseNotifierForm notifierForm = new CaseNotifierForm(notifier, surveillanceReport);

        final CommitDiscardWrapperComponent<CaseNotifierForm> editView = new CommitDiscardWrapperComponent<>(notifierForm, true);

        final Window window = VaadinUiUtil.showModalPopupWindow(editView, title);

        if (isEditAllowed) {
            editView.setPreCommitListener(cb -> {
                if (!notifierForm.isValid()) {
                    // Form validation failed - errors are already shown on the form
                    return;
                }
                cb.run();
            });
            editView.addCommitListener(() -> {
                if (notifierForm.isValid()) {
                    NotifierDto savedNotifier = notifierForm.getValue();
                    savedNotifier = FacadeProvider.getNotifierFacade().save(savedNotifier);

                    // always set the notifier reference on the case to the current version
                    final NotifierReferenceDto notifierRef =
                        FacadeProvider.getNotifierFacade().getVersionReferenceByUuidAndDate(savedNotifier.getUuid());
                    caze.setNotifier(notifierRef);

                    // Update surveillance report with form values
                    updateSurveillanceReportFromForm(surveillanceReport, notifierForm);
                    FacadeProvider.getSurveillanceReportFacade().save(surveillanceReport);

                    FacadeProvider.getCaseFacade().save(caze);

                    window.close();
                    callback.run();
                } else {
                    // Form validation failed - errors are already shown on the form
                }
            });

            // TODO - NotifierFacade doesn't seem to have delete functionality yet
            // REVIEW - don't know if it is ever desired to be able to delete a phone notification
            // if (canDelete) {
            //     editView.addDeleteListener(() -> {
            //         // Delete functionality would go here
            //         window.close();
            //         callback.run();
            //     }, "Notifier");
            // }
        } else {
            // For read-only mode, disable all form fields
            notifierForm.setEnabled(false);

            // Hide save and discard buttons, add a close button instead
            editView.getCommitButton().setVisible(false);
            editView.getDiscardButton().setVisible(false);

            Button closeButton = ButtonHelper.createButton(Captions.actionClose, e -> window.close());
            editView.getButtonsPanel().addComponent(closeButton);
        }
    }

    /**
     * Updates the surveillance report with values from the notifier form.
     * Applies business logic for dates and treatment status.
     *
     * @param surveillanceReport
     *            the surveillance report to update
     * @param notifierForm
     *            the form containing the values
     */
    private void updateSurveillanceReportFromForm(SurveillanceReportDto surveillanceReport, CaseNotifierForm notifierForm) {
        // Update report date from notification date
        final LocalDate notificationDate = notifierForm.getNotificationDate() == null ? LocalDate.now() : notifierForm.getNotificationDate();
        surveillanceReport.setReportDate(Date.from(notificationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        // Update diagnosis date if provided
        final LocalDate diagnosticDate = notifierForm.getDiagnosticDate();
        if(diagnosticDate != null) {
            surveillanceReport.setDateOfDiagnosis(Date.from(diagnosticDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            surveillanceReport.setDateOfDiagnosis(null);
        }

        // Update treatment based on selected option
        final TreatmentOption selectedOption = notifierForm.getSelectedTreatmentOption();
        if (selectedOption != null) {
            if (selectedOption.equals(TreatmentOption.YES)) {
                surveillanceReport.setTreatmentStarted(YesNoUnknown.YES);
                surveillanceReport.setTreatmentNotApplicable(false);
                if (surveillanceReport.getTreatmentStartDate() == null) {
                    surveillanceReport.setTreatmentStartDate(new Date());
                }
            } else if (selectedOption.equals(TreatmentOption.NO)) {
                surveillanceReport.setTreatmentStarted(YesNoUnknown.NO);
                surveillanceReport.setTreatmentNotApplicable(false);
                surveillanceReport.setTreatmentStartDate(null);
            } else if (selectedOption.equals(TreatmentOption.NOT_APPLICABLE)) {
                surveillanceReport.setTreatmentNotApplicable(true);
                surveillanceReport.setTreatmentStarted(null);
                surveillanceReport.setTreatmentStartDate(null);
            } else if (selectedOption.equals(TreatmentOption.UNKNOWN)) {
                surveillanceReport.setTreatmentStarted(YesNoUnknown.UNKNOWN);
                surveillanceReport.setTreatmentNotApplicable(false);
                surveillanceReport.setTreatmentStartDate(null);
            }
        }
    }

}
