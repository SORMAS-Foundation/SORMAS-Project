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

import java.time.ZoneId;
import java.util.Locale;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Side view content for case notifiers.
 * Displays notifier details, notification dates, and treatment status.
 */
public class CaseNotifierSideViewContent extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    /** The case data associated with the notifier. */
    private CaseDataDto caze;

    /** The notifier details. */
    private NotifierDto notifier;

    /** The oldest surveillance report associated with the case. */
    private SurveillanceReportDto oldestReport;

    private TherapyDto therapy;

    /**
     * Creates a new case notifier side view content.
     * 
     * @param caze
     *            the case data
     * @param notifier
     *            the notifier details
     * @param oldestReport
     *            the oldest surveillance report for the case
     */
    public CaseNotifierSideViewContent(CaseDataDto caze, NotifierDto notifier, SurveillanceReportDto oldestReport) {

        this.caze = caze;
        this.therapy = caze.getTherapy();
        this.notifier = notifier;
        this.oldestReport = oldestReport;

        setStyleName("case-notifier-side-view");
        setMargin(false);
        setSpacing(false);
        buildNotifierDetails();
    }

    /**
     * Builds the notifier details layout with personal info, dates, and treatment status.
     */
    private void buildNotifierDetails() {

        // Full name
        Label fullNameLabel = new Label(notifier.getFirstName() + " " + notifier.getLastName());
        CssStyles.style(fullNameLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
        addComponent(fullNameLabel);

        // Registration number
        Label registrationNumberLabel = new Label(notifier.getRegistrationNumber());
        CssStyles.style(registrationNumberLabel, CssStyles.LABEL_BOLD);
        addComponent(registrationNumberLabel);

        // Spacer before contact label
        if ((notifier.getPhone() != null && !notifier.getPhone().isEmpty()) || (notifier.getEmail() != null && !notifier.getEmail().isEmpty())) {
            Label spacerBeforeContact = new Label();
            spacerBeforeContact.setHeight("0.1rem");
            addComponent(spacerBeforeContact);
        }

        // Phone
        if (notifier.getPhone() != null && !notifier.getPhone().isEmpty()) {
            Label phoneLabel = new Label(notifier.getPhone());
            addComponent(phoneLabel);
        }

        // Email
        if (notifier.getEmail() != null && !notifier.getEmail().isEmpty()) {
            Label emailLabel = new Label(notifier.getEmail());
            addComponent(emailLabel);
        }

        // Spacer before email label
        Label spacerDates = new Label();
        spacerDates.setHeight("0.1rem");

        // Date of notification
        DateField notificationDateField = new DateField(I18nProperties.getCaption(Captions.Notification_dateOfNotification));
        notificationDateField.setValue(
            oldestReport == null
                ? notifier.getChangeDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : oldestReport.getReportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        notificationDateField.setReadOnly(true);

        // Date of diagnostic
        DateField diagnosticDateField = new DateField(I18nProperties.getCaption(Captions.SurveillanceReport_dateOfDiagnosis));
        diagnosticDateField.setValue(
            oldestReport == null
                ? null
                : oldestReport.getDateOfDiagnosis() == null
                    ? null
                    : oldestReport.getDateOfDiagnosis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        diagnosticDateField.setReadOnly(true);

        // Horizontal layout for date fields
        HorizontalLayout dateFieldsLayout = new HorizontalLayout(notificationDateField, diagnosticDateField);
        dateFieldsLayout.setSpacing(true);
        dateFieldsLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        addComponent(spacerDates);
        addComponent(dateFieldsLayout);

        RadioButtonGroup<TreatmentOption> treatmentGroup = buildTreatmentOptions();
        addComponent(treatmentGroup);

        if ((notifier.getAgentFirstName() != null && !notifier.getAgentFirstName().isBlank())
            || (notifier.getAgentLastName() != null && !notifier.getAgentLastName().isBlank())) {
            // Spacer before Reporting Agent label
            Label spacerAgent = new Label();
            spacerAgent.setHeight("0.1rem");
            addComponent(spacerAgent);
            Label reportingAgent = new Label(I18nProperties.getCaption(Captions.Notification_reportingAgent));
            CssStyles.style(reportingAgent, CssStyles.LABEL_BOLD, CssStyles.LABEL_BOTTOM_LINE);
            addComponent(reportingAgent);
            Label agentNameLabel = new Label(notifier.getAgentFirstName() + " " + notifier.getAgentLastName().toUpperCase(Locale.ROOT));
            CssStyles.style(agentNameLabel, CssStyles.LABEL_RELEVANT);
            addComponent(agentNameLabel);
        }

        Label spacerNotificationType = new Label();
        spacerNotificationType.setHeight("0.1rem");
        addComponent(spacerNotificationType);
        // Notification type
        Label notificationTypeLabel = new Label(
            oldestReport == null
                ? I18nProperties.getCaption(Captions.Notification_notificationTypePhone)
                : I18nProperties.getCaption(Captions.Notification_notificationTypeExternal));
        CssStyles.style(notificationTypeLabel, CssStyles.BADGE);
        addComponent(notificationTypeLabel);

    }

    /**
     * Builds treatment options as a radio button group.
     *
     * @return radio button group with treatment options
     */
    private RadioButtonGroup<TreatmentOption> buildTreatmentOptions() {
        DataProvider<TreatmentOption, ?> dataProvider = DataProvider.ofCollection(TreatmentOption.ALL_OPTIONS);

        // Treatment radios
        RadioButtonGroup<TreatmentOption> treatmentGroup = new RadioButtonGroup<>(I18nProperties.getCaption(Captions.Treatment));
        treatmentGroup.setDataProvider(dataProvider);
        treatmentGroup.setItemCaptionGenerator(t -> t.getCaption());

        treatmentGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        treatmentGroup.setReadOnly(true);
        treatmentGroup.clear();

        if (therapy == null) {
            return treatmentGroup;
        }

        if (therapy.isTreatmentNotApplicable()) {
            treatmentGroup.setValue(TreatmentOption.NOT_APPLICABLE);
            return treatmentGroup;
        }

        final YesNoUnknown treatmentStarted = therapy.getTreatmentStarted();

        if (treatmentStarted == null) {
            return treatmentGroup;
        }

        treatmentGroup.setValue(TreatmentOption.forValue(treatmentStarted));

        return treatmentGroup;
    }
}
