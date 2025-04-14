/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.util.List;

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
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Represents the side view content for a case notifier in the SORMAS UI.
 * Displays details about the notifier, including personal information,
 * notification dates, and treatment status.
 */
public class CaseNotifierSideViewContent extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    /**
     * The case data associated with the notifier.
     */
    private CaseDataDto caze;

    /**
     * The notifier details.
     */
    private NotifierDto notifier;

    /**
     * The oldest surveillance report associated with the case.
     */
    private SurveillanceReportDto oldestReport;

    /**
     * The treatment details, if available.
     */
    private TreatmentDto treatment;

    /**
     * Indicates whether treatment is not applicable.
     */
    private boolean treatmentNotApplicable;

    /**
     * Constructs a new CaseNotifierSideViewContent instance.
     *
     * @param caze
     *            The case data associated with the notifier.
     * @param notifier
     *            The notifier details.
     * @param oldestReport
     *            The oldest surveillance report associated with the case.
     * @param treatment
     *            The treatment details, if available.
     * @param treatmentNotApplicable
     *            Indicates whether treatment is not applicable.
     */
    public CaseNotifierSideViewContent(
        CaseDataDto caze,
        NotifierDto notifier,
        SurveillanceReportDto oldestReport,
        TreatmentDto treatment,
        boolean treatmentNotApplicable) {

        this.caze = caze;
        this.notifier = notifier;
        this.oldestReport = oldestReport;
        this.treatment = treatment;
        this.treatmentNotApplicable = treatmentNotApplicable;

        setStyleName("case-notifier-side-view");
        setMargin(false);
        setSpacing(false);
        buildNotifierDetails();
    }

    /**
     * Builds the notifier details layout, including personal information,
     * notification dates, and treatment status.
     */
    private void buildNotifierDetails() {

        // Full name
        Label fullNameLabel = new Label(notifier.getFirstName() + " " + notifier.getLastName());
        CssStyles.style(fullNameLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);

        // Registration number
        Label registrationNumberLabel = new Label(notifier.getRegistrationNumber());
        CssStyles.style(registrationNumberLabel, CssStyles.LABEL_BOLD);

        // Spacer before phone label
        Label spacerBeforePhone = new Label();
        spacerBeforePhone.setHeight("0.05rem");

        // Phone
        Label phoneLabel = new Label(notifier.getPhone());

        // Email
        Label emailLabel = new Label(notifier.getEmail());

        // Spacer before email label
        Label spacerDates = new Label();
        spacerDates.setHeight("0.1rem");

        // Date of notification
        DateField notificationDateField = new DateField(I18nProperties.getCaption(Captions.Notification_dateOfNotification));
        notificationDateField.setValue(oldestReport.getReportDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        notificationDateField.setReadOnly(true);

        // Date of diagnostic
        DateField diagnosticDateField = new DateField(I18nProperties.getCaption(Captions.SurveillanceReport_dateOfDiagnosis));
        diagnosticDateField.setValue(LocalDate.now()); // Example date
        diagnosticDateField.setReadOnly(true);

        // Horizontal layout for date fields
        HorizontalLayout dateFieldsLayout = new HorizontalLayout(notificationDateField, diagnosticDateField);
        dateFieldsLayout.setSpacing(true);
        dateFieldsLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        // Spacer before treatment group
        Label spacerBeforeTreatment = new Label();
        spacerBeforeTreatment.setHeight("0.05rem");

        RadioButtonGroup<TreatmentOption> treatmentGroup = buildTreatmentOptions();

        // Add components to the layout
        addComponent(fullNameLabel);
        addComponent(registrationNumberLabel);
        addComponent(spacerBeforePhone);
        addComponent(phoneLabel);
        addComponent(emailLabel);
        addComponent(spacerDates);
        addComponent(dateFieldsLayout);
        addComponent(spacerBeforeTreatment);
        addComponent(treatmentGroup);

    }

    /**
     * Builds the treatment options as a radio button group.
     *
     * @return A {@link RadioButtonGroup} containing treatment options.
     */
    private RadioButtonGroup<TreatmentOption> buildTreatmentOptions() {
        TreatmentOption yes = new TreatmentOption(YesNoUnknown.YES.toString(), I18nProperties.getEnumCaption(YesNoUnknown.YES));
        TreatmentOption no = new TreatmentOption(YesNoUnknown.NO.toString(), I18nProperties.getEnumCaption(YesNoUnknown.NO));
        TreatmentOption notApplicable = new TreatmentOption("NA", I18nProperties.getString(Strings.notApplicable));
        TreatmentOption unknown = new TreatmentOption(YesNoUnknown.UNKNOWN.toString(), I18nProperties.getEnumCaption(YesNoUnknown.UNKNOWN));

        List<TreatmentOption> options = List.of(yes, no, notApplicable, unknown);

        DataProvider<TreatmentOption, ?> dataProvider = DataProvider.ofCollection(options);

        // Treatment radios
        RadioButtonGroup<TreatmentOption> treatmentGroup = new RadioButtonGroup<>(I18nProperties.getCaption(Captions.Treatment));
        treatmentGroup.setDataProvider(dataProvider);
        treatmentGroup.setItemCaptionGenerator(t -> t.getCaption());

        if (treatmentNotApplicable) {
            treatmentGroup.setValue(notApplicable);
        } else {
            if (treatment == null) {
                treatmentGroup.setValue(unknown);
            } else {
                if (treatment.getTreatmentDateTime() != null) {
                    treatmentGroup.setValue(yes);
                } else {
                    treatmentGroup.setValue(no);
                }
            }
        }
        treatmentGroup.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        treatmentGroup.setReadOnly(true);
        return treatmentGroup;
    }
}
