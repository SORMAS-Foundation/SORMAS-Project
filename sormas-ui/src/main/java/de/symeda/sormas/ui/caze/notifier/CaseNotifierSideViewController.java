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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.person.notifier.NotifierReferenceDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
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
    public CaseNotifierSideViewContent getNotifierComponent(CaseDataDto caze) {

        NotifierDto notifier =
            FacadeProvider.getNotifierFacade().getByUuidAndTime(caze.getNotifier().getUuid(), caze.getNotifier().getVersionDate().toInstant());

        return new CaseNotifierSideViewContent(caze, notifier, getOldestReport(caze));
    }

    /**
     * Retrieves the oldest surveillance report for a case.
     * 
     * @param caze
     *            the case
     * @return oldest report or null if none found
     */
    public SurveillanceReportDto getOldestReport(CaseDataDto caze) {

        CaseReferenceDto cazeRef = new CaseReferenceDto();
        cazeRef.setUuid(caze.getUuid());
        return getOldestReport(cazeRef);
    }

    /**
     * Retrieves the oldest surveillance report for a case reference.
     *
     * @param caze
     *            the case reference
     * @return oldest report or null if none found
     */
    public SurveillanceReportDto getOldestReport(CaseReferenceDto caze) {

        SurveillanceReportCriteria criteria = new SurveillanceReportCriteria();
        criteria.caze(caze);
        criteria.setReportingType(ReportingType.DOCTOR);

        List<SurveillanceReportDto> reports = FacadeProvider.getSurveillanceReportFacade().getIndexList(criteria, null, null, null);

        // Filter to get the oldest report
        return reports.stream()
            .min(Comparator.comparing(SurveillanceReportDto::getReportDate)) // Assuming getDate() returns the report date
            .orElse(null);
    }

    /**
     * Opens a dialog to create a new notifier.
     * Only allowed when no surveillance report exists for the case.
     *
     * @param caze
     *            the case for which to create a notifier
     * @param callback
     *            callback to run after successful save or dialog close
     */
    public void createNotifier(CaseDataDto caze, Runnable callback) {
        // Check if surveillance report exists - if so, notifier creation is not allowed
        SurveillanceReportDto oldestReport = getOldestReport(caze);
        if (oldestReport != null) {
            VaadinUiUtil.showSimplePopupWindow(
                I18nProperties.getString(Strings.notificationCannotCreate),
                I18nProperties.getString(Strings.notificationCreationNotAllowedWithoutSurveillanceReport));
            return;
        }

        NotifierDto newNotifier = new NotifierDto();
        TherapyDto therapy = caze.getTherapy();

        openEditWindow(caze, newNotifier, therapy, I18nProperties.getCaption(Captions.Notification_createNotification), false, callback, true);
    }

    /**
     * Opens a dialog to edit an existing notifier.
     * Only allowed when no surveillance report exists for the case.
     *
     * @param caze
     *            the case containing the notifier to edit
     * @param callback
     *            callback to run after successful save or dialog close
     * @param isEditAllowed
     *            whether editing is allowed for this user
     */
    public void editNotifier(CaseDataDto caze, Runnable callback, boolean isEditAllowed) {
        if (caze.getNotifier() == null) {
            return;
        }

        // Check if surveillance report exists - if so, notifier editing is not allowed
        SurveillanceReportDto oldestReport = getOldestReport(caze);
        if (oldestReport != null && isEditAllowed) {
            VaadinUiUtil.showSimplePopupWindow(
                I18nProperties.getString(Strings.notificationCannotCreate),
                I18nProperties.getString(Strings.notificationCreationNotAllowedWithoutSurveillanceReport));
            return;
        }

        // We only edit the current version
        NotifierDto notifier = FacadeProvider.getNotifierFacade().getByUuid(caze.getNotifier().getUuid());
        TherapyDto therapy = caze.getTherapy();

        openEditWindow(
            caze,
            notifier,
            therapy,
            isEditAllowed
                ? I18nProperties.getCaption(Captions.Notification_editNotification)
                : I18nProperties.getCaption(Captions.Notification_viewNotification),
            true,
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
    public void viewNotifier(CaseDataDto caze, Runnable callback) {
        editNotifier(caze, callback, false);
    }

    /**
     * Opens the notifier edit/view window.
     *
     * @param caze
     *            the case data
     * @param notifier
     *            the notifier to edit/view
     * @param therapy
     *            the therapy data
     * @param title
     *            the dialog title
     * @param canDelete
     *            whether delete functionality should be available
     * @param callback
     *            callback to run after successful save or dialog close
     * @param isEditAllowed
     *            whether editing is allowed
     */
    private void openEditWindow(
        CaseDataDto caze,
        NotifierDto notifier,
        TherapyDto therapy,
        String title,
        boolean canDelete,
        Runnable callback,
        boolean isEditAllowed) {

        final CaseNotifierForm notifierForm = new CaseNotifierForm(notifier, therapy);

        final CommitDiscardWrapperComponent<CaseNotifierForm> editView = new CommitDiscardWrapperComponent<>(notifierForm, true);

        final Window window = VaadinUiUtil.showModalPopupWindow(editView, title);

        if (isEditAllowed) {
            editView.setPreCommitListener((cb) -> {
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

                    // Handle treatment option changes

                    final TreatmentOption selectedOption = notifierForm.getSelectedTreatmentOption();
                    updateTherapyBasedOnSelection(caze, selectedOption);

                    // Handle diagnostic date updates if changed
                    final LocalDate diagnosticDate = notifierForm.getDiagnosticDate();
                    if (diagnosticDate != null) {
                        final Date diagnosisDate = Date.from(diagnosticDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                        handleDiagnosticDateUpdate(caze, diagnosisDate);
                    }

                    FacadeProvider.getCaseFacade().save(caze);

                    window.close();
                    callback.run();
                } else {
                    // Form validation failed - errors are already shown on the form
                }
            });

            // TODO - NotifierFacade doesn't seem to have delete functionality yet
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
        }
    }

    /**
     * Updates therapy data based on selected treatment option.
     *
     * @param caze
     *            the case data containing the therapy
     * @param selectedOption
     *            the selected treatment option from the form
     */
    private void updateTherapyBasedOnSelection(CaseDataDto caze, TreatmentOption selectedOption) {

        if (selectedOption == null) {
            return;
        }

        TherapyDto therapy = caze.getTherapy();

        if (therapy == null) {
            therapy = TherapyDto.build();
        }

        if (selectedOption.equals(TreatmentOption.YES)) {
            therapy.setTreatmentStarted(YesNoUnknown.YES);
            therapy.setTreatmentNotApplicable(false);
            if (therapy.getTreatmentStartDate() == null) {
                therapy.setTreatmentStartDate(new java.util.Date());
            }
            return;
        }

        if (selectedOption.equals(TreatmentOption.NO)) {
            therapy.setTreatmentStarted(YesNoUnknown.NO);
            therapy.setTreatmentNotApplicable(false);
            therapy.setTreatmentStartDate(null);
            return;
        }

        if (selectedOption.equals(TreatmentOption.NOT_APPLICABLE)) {
            therapy.setTreatmentNotApplicable(true);
            therapy.setTreatmentStarted(null);
            therapy.setTreatmentStartDate(null);
            return;
        }

        if (selectedOption.equals(TreatmentOption.UNKNOWN)) {
            therapy.setTreatmentStarted(YesNoUnknown.UNKNOWN);
            therapy.setTreatmentNotApplicable(false);
            therapy.setTreatmentStartDate(null);
            return;
        }

        caze.setTherapy(therapy);
    }

    /**
     * Handles diagnostic date updates from the notifier form.
     * Placeholder for future diagnostic date handling.
     *
     * @param caze
     *            the case data
     * @param diagnosisDate
     *            the diagnosis date from the form
     */
    private void handleDiagnosticDateUpdate(CaseDataDto caze, java.util.Date diagnosisDate) {
        // TODO - Implement proper diagnostic date handling
    }

}
