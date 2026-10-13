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

package de.symeda.sormas.ui.immunization.components.panel;

import java.util.Date;
import java.util.Objects;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.InformationReliability;
import de.symeda.sormas.api.immunization.VaccinationStatusData;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings({
    "java:S2160", // suppress sonar missing equals
    "java:S110" // suppress sonar too many parents warning
})
public class VaccinationStatusPanel extends VerticalLayout {

    private static final long serialVersionUID = -4863945869156850850L;

    private transient VaccinationStatusData data;

    private final String captionVaccinationStatus;
    private final String captionVaccinationStatusDetails;
    private final String captionNumberOfDoses;
    private final String captionDateOfLastDose;
    private final String captionInformationReliability;
    private final String captionReferenceDate;
    private final String captionReferencePeriod;
    private final String captionVaccinationStatusLastUpdated;

    private HorizontalLayout vaccinationStatusRow;
    private HorizontalLayout vaccinationStatusDetailsRow;
    private HorizontalLayout numberOfDosesRow;
    private HorizontalLayout dateOfLastDoseRow;
    private HorizontalLayout informationReliabilityRow;
    private HorizontalLayout referencePeriodRow;
    private HorizontalLayout vaccinationStatusLastUpdatedRow;

    private Label vaccinationStatusValueLabel;
    private Label vaccinationStatusDetailsValueLabel;
    private Label numberOfDosesValueLabel;
    private Label dateOfLastDoseValueLabel;
    private Label informationReliabilityValueLabel;
    private Label referencePeriodCaptionLabel;
    private Label referencePeriodValueLabel;
    private Label vaccinationStatusLastUpdatedValueLabel;

    public VaccinationStatusPanel() {

        captionVaccinationStatus = I18nProperties.getCaption("ImmunizationSidePanel.vaccinationStatus");
        captionVaccinationStatusDetails = I18nProperties.getCaption("ImmunizationSidePanel.vaccinationStatusDetails");
        captionNumberOfDoses = I18nProperties.getCaption("ImmunizationSidePanel.numberOfDoses");
        captionDateOfLastDose = I18nProperties.getCaption("ImmunizationSidePanel.dateOfLastDose");
        captionInformationReliability = I18nProperties.getCaption("ImmunizationSidePanel.informationReliability");
        captionReferenceDate = I18nProperties.getCaption("ImmunizationSidePanel.referenceDate", "Reference date");
        captionReferencePeriod = I18nProperties.getCaption("ImmunizationSidePanel.referencePeriod");
        captionVaccinationStatusLastUpdated = I18nProperties.getCaption("ImmunizationSidePanel.vaccinationStatusLastUpdated");

        setWidth(100, Sizeable.Unit.PERCENTAGE);
        setMargin(false);
        setSpacing(false);

        initializeComponents();
        buildLayout();
    }

    public static VaccinationStatusPanel forCase(CaseDataDto caze) {
        VaccinationStatusData caseData = VaccinationStatusData.Builder.createBlank()
            .vaccinationStatus(caze.getVaccinationStatus())
            .vaccinationStatusDetails(caze.getVaccinationStatusDetails())
            .numberOfDoses(caze.getNumberOfDoses())
            .informationReliability(caze.getInformationReliability())
            .referencePeriodFrom(caze.getReportDate())
            .referencePeriodTo(caze.getReportDate())
            .vaccinationStatusLastUpdated(caze.getVaccinationStatusLastUpdated())
            .build();

        return createPanel(caseData);
    }

    public static VaccinationStatusPanel forContact(ContactDto contact) {
        Date referencePeriodFrom = contact.getFirstContactDate();
        if (referencePeriodFrom == null) {
            referencePeriodFrom = contact.getLastContactDate() != null ? contact.getLastContactDate() : contact.getReportDateTime();
        }
        Date referencePeriodTo = contact.getLastContactDate() != null ? contact.getLastContactDate() : referencePeriodFrom;

        VaccinationStatusData contactData = VaccinationStatusData.Builder.createBlank()
            .vaccinationStatus(contact.getVaccinationStatus())
            .vaccinationStatusDetails(contact.getVaccinationStatusDetails())
            .numberOfDoses(contact.getNumberOfDoses())
            .informationReliability(contact.getInformationReliability())
            .referencePeriodFrom(referencePeriodFrom)
            .referencePeriodTo(referencePeriodTo)
            .vaccinationStatusLastUpdated(contact.getVaccinationStatusLastUpdated())
            .build();

        return createPanel(contactData);
    }

    public static VaccinationStatusPanel forEventParticipant(EventParticipantDto eventParticipant, EventDto event) {
        Date referencePeriodFrom = event.getStartDate() != null ? event.getStartDate() : event.getReportDateTime();
        Date referencePeriodTo = event.getEndDate() != null ? event.getEndDate() : referencePeriodFrom;

        VaccinationStatusData eventParticipantData = VaccinationStatusData.Builder.createBlank()
            .vaccinationStatus(eventParticipant.getVaccinationStatus())
            .vaccinationStatusDetails(eventParticipant.getVaccinationStatusDetails())
            .numberOfDoses(eventParticipant.getNumberOfDoses())
            .informationReliability(eventParticipant.getInformationReliability())
            .referencePeriodFrom(referencePeriodFrom)
            .referencePeriodTo(referencePeriodTo)
            .vaccinationStatusLastUpdated(eventParticipant.getVaccinationStatusLastUpdated())
            .build();

        return createPanel(eventParticipantData);
    }

    private static VaccinationStatusPanel createPanel(VaccinationStatusData entityData) {
        VaccinationStatusPanel panel = new VaccinationStatusPanel();
        panel.setValue(entityData);
        return panel;
    }

    protected void initializeComponents() {
        vaccinationStatusValueLabel = new Label();
        vaccinationStatusRow = createLabelRow(captionVaccinationStatus, vaccinationStatusValueLabel);

        vaccinationStatusDetailsValueLabel = new Label();
        vaccinationStatusDetailsRow = createLabelRow(captionVaccinationStatusDetails, vaccinationStatusDetailsValueLabel);

        numberOfDosesValueLabel = new Label();
        numberOfDosesRow = createLabelRow(captionNumberOfDoses, numberOfDosesValueLabel);

        dateOfLastDoseValueLabel = new Label();
        dateOfLastDoseRow = createLabelRow(captionDateOfLastDose, dateOfLastDoseValueLabel);

        informationReliabilityValueLabel = new Label();
        informationReliabilityRow = createLabelRow(captionInformationReliability, informationReliabilityValueLabel);

        referencePeriodCaptionLabel = new Label(captionReferencePeriod);
        referencePeriodValueLabel = new Label();
        referencePeriodRow = createLabelRow(referencePeriodCaptionLabel, referencePeriodValueLabel);

        vaccinationStatusLastUpdatedValueLabel = new Label();
        vaccinationStatusLastUpdatedRow = createLabelRow(captionVaccinationStatusLastUpdated, vaccinationStatusLastUpdatedValueLabel);
    }

    protected HorizontalLayout createLabelRow(String caption, Label valueLabel) {
        return createLabelRow(new Label(caption), valueLabel);
    }

    protected HorizontalLayout createLabelRow(Label captionLabel, Label valueLabel) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidth(100, Sizeable.Unit.PERCENTAGE);
        row.setMargin(false);
        row.setSpacing(false);

        String caption = captionLabel.getValue();
        captionLabel.setDescription(caption);
        valueLabel.setDescription(caption);
        captionLabel.setWidthUndefined();

        row.addComponents(captionLabel, valueLabel);

        row.setExpandRatio(captionLabel, 0.5f);
        row.setExpandRatio(valueLabel, 1.0f);
        return row;
    }

    protected void buildLayout() {
        addComponent(vaccinationStatusRow);
        addComponent(vaccinationStatusDetailsRow);
        addComponent(numberOfDosesRow);
        addComponent(dateOfLastDoseRow);
        addComponent(informationReliabilityRow);
        addComponent(referencePeriodRow);
        addComponent(vaccinationStatusLastUpdatedRow);
    }

    protected void updateComponents() {
        if (data == null) {
            clear();
            return;
        }

        VaccinationStatus vaccinationStatus = data.getVaccinationStatus();
        if (vaccinationStatus != null) {
            vaccinationStatusRow.setVisible(true);
            vaccinationStatusValueLabel.setValue(vaccinationStatus.toString());
        } else {
            vaccinationStatusRow.setVisible(false);
            vaccinationStatusValueLabel.setValue("");
        }
        vaccinationStatusValueLabel.removeStyleNames(CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL);
        if (vaccinationStatus == VaccinationStatus.VACCINATED) {
            vaccinationStatusValueLabel.addStyleName(CssStyles.LABEL_POSITIVE);
        } else if (vaccinationStatus == VaccinationStatus.UNVACCINATED) {
            vaccinationStatusValueLabel.addStyleName(CssStyles.LABEL_CRITICAL);
        }

        String details = data.getVaccinationStatusDetails();
        boolean showDetails = data.getVaccinationStatus() == VaccinationStatus.OTHER && details != null && !details.trim().isEmpty();
        vaccinationStatusDetailsRow.setVisible(showDetails);
        vaccinationStatusDetailsValueLabel.setValue(showDetails ? details : "");

        boolean showNumberOfDoses = data.getNumberOfDoses() != null;
        numberOfDosesRow.setVisible(showNumberOfDoses);
        numberOfDosesValueLabel.setValue(showNumberOfDoses ? data.getNumberOfDoses().toString() : "");

        boolean showDateOfLastDose = data.getDateOfLastDose() != null;
        dateOfLastDoseRow.setVisible(showDateOfLastDose);
        dateOfLastDoseValueLabel.setValue(showDateOfLastDose ? DateFormatHelper.formatLocalDate(data.getDateOfLastDose()) : "");

        InformationReliability reliability = data.getInformationReliability();
        if (reliability != null) {
            informationReliabilityRow.setVisible(true);
            informationReliabilityValueLabel.setValue(reliability.toString());
        } else {
            informationReliabilityRow.setVisible(false);
            informationReliabilityValueLabel.setValue("");
        }

        Date referencePeriodFromDate = data.getReferencePeriodFrom();
        Date referencePeriodToDate = data.getReferencePeriodTo();
        String periodFrom = referencePeriodFromDate != null ? DateFormatHelper.formatLocalDate(referencePeriodFromDate) : null;
        String periodTo = referencePeriodToDate != null ? DateFormatHelper.formatLocalDate(referencePeriodToDate) : null;
        boolean showReferencePeriod = periodFrom != null || periodTo != null;
        referencePeriodRow.setVisible(showReferencePeriod);
        if (showReferencePeriod) {
            boolean isSingleReferenceDate =
                referencePeriodFromDate == null || referencePeriodToDate == null || Objects.equals(referencePeriodFromDate, referencePeriodToDate);
            referencePeriodCaptionLabel.setValue(isSingleReferenceDate ? captionReferenceDate : captionReferencePeriod);
            String referenceCaption = referencePeriodCaptionLabel.getValue();
            referencePeriodCaptionLabel.setDescription(referenceCaption);
            referencePeriodValueLabel.setDescription(referenceCaption);
            String referencePeriod;
            if (periodFrom != null && periodTo != null && !Objects.equals(referencePeriodFromDate, referencePeriodToDate)) {
                referencePeriod = periodFrom + " - " + periodTo;
            } else if (periodFrom != null) {
                referencePeriod = periodFrom;
            } else {
                referencePeriod = periodTo;
            }
            referencePeriodValueLabel.setValue(referencePeriod);
        } else {
            referencePeriodCaptionLabel.setValue(captionReferencePeriod);
            referencePeriodCaptionLabel.setDescription(captionReferencePeriod);
            referencePeriodValueLabel.setDescription(captionReferencePeriod);
            referencePeriodValueLabel.setValue("");
        }

        boolean showVaccinationStatusLastUpdated = data.getVaccinationStatusLastUpdated() != null;
        vaccinationStatusLastUpdatedRow.setVisible(showVaccinationStatusLastUpdated);
        vaccinationStatusLastUpdatedValueLabel
            .setValue(showVaccinationStatusLastUpdated ? DateFormatHelper.formatDate(data.getVaccinationStatusLastUpdated()) : "");
    }

    public void setValue(VaccinationStatusData data) {
        this.data = data;
        updateComponents();
    }

    public void clear() {
        vaccinationStatusValueLabel.setValue("");
        vaccinationStatusRow.setVisible(false);
        vaccinationStatusValueLabel.removeStyleNames(CssStyles.LABEL_POSITIVE, CssStyles.LABEL_CRITICAL);
        vaccinationStatusDetailsValueLabel.setValue("");
        vaccinationStatusDetailsRow.setVisible(false);
        numberOfDosesValueLabel.setValue("");
        numberOfDosesRow.setVisible(false);
        dateOfLastDoseValueLabel.setValue("");
        dateOfLastDoseRow.setVisible(false);
        informationReliabilityValueLabel.setValue("");
        informationReliabilityRow.setVisible(false);
        referencePeriodValueLabel.setValue("");
        referencePeriodRow.setVisible(false);
        vaccinationStatusLastUpdatedValueLabel.setValue("");
        vaccinationStatusLastUpdatedRow.setVisible(false);
    }
}
