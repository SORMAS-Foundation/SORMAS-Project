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

import com.vaadin.data.provider.DataProvider;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Form for creating and editing notifier information.
 * Provides editable fields for personal info, contact details, and treatment status.
 * 
 * Note: Only valid when no surveillance report exists.
 */
public class CaseNotifierForm extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    // Form fields
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField registrationNumberField;
    private TextField phoneField;
    private TextField emailField;
    private TextArea addressField;
    private DateField notificationDateField;
    private DateField diagnosticDateField;
    private RadioButtonGroup<TreatmentOption> treatmentGroup;
    private TextField agentFirstNameField;
    private TextField agentLastNameField;

    // Data objects
    private NotifierDto notifier;
    private TherapyDto therapy;

    /**
     * Creates a new notifier form.
     */
    public CaseNotifierForm() {
        setStyleName("notifier-edit-form");
        setMargin(true);
        setSpacing(true);
        setWidth(800, Unit.PIXELS);
        buildForm();
    }

    /**
     * Creates a new notifier form with initial data.
     */
    public CaseNotifierForm(NotifierDto notifier, TherapyDto therapy) {
        this();
        this.notifier = notifier;
        this.therapy = therapy;
        populateFields();
    }

    /**
     * Builds the form layout with all necessary fields.
     */
    private void buildForm() {
        // Personal Information Section
        Label personalInfoLabel = new Label(I18nProperties.getString(Strings.headingPersonInformation));
        CssStyles.style(personalInfoLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_BOTTOM_LINE);
        addComponent(personalInfoLabel);

        // Name fields
        HorizontalLayout nameLayout = new HorizontalLayout();
        nameLayout.setSpacing(true);
        nameLayout.setWidth(100, Unit.PERCENTAGE);

        firstNameField = new TextField(I18nProperties.getPrefixCaption("Person", NotifierDto.FIRST_NAME));
        firstNameField.setRequiredIndicatorVisible(true);
        firstNameField.setWidth(100, Unit.PERCENTAGE);

        lastNameField = new TextField(I18nProperties.getPrefixCaption("Person", NotifierDto.LAST_NAME));
        lastNameField.setRequiredIndicatorVisible(true);
        lastNameField.setWidth(100, Unit.PERCENTAGE);

        nameLayout.addComponents(firstNameField, lastNameField);
        nameLayout.setExpandRatio(firstNameField, 1);
        nameLayout.setExpandRatio(lastNameField, 1);
        addComponent(nameLayout);

        // Registration number
        registrationNumberField = new TextField(I18nProperties.getCaption(Captions.Notification_registrationNumber));
        registrationNumberField.setRequiredIndicatorVisible(true);
        registrationNumberField.setWidth(100, Unit.PERCENTAGE);
        addComponent(registrationNumberField);

        // Contact Information Section
        Label contactInfoLabel = new Label(I18nProperties.getString(Strings.headingContactInformation));
        CssStyles.style(contactInfoLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_BOTTOM_LINE);
        addComponent(contactInfoLabel);

        // Contact fields
        HorizontalLayout contactLayout = new HorizontalLayout();
        contactLayout.setSpacing(true);
        contactLayout.setWidth(100, Unit.PERCENTAGE);

        phoneField = new TextField(I18nProperties.getPrefixCaption("Person", NotifierDto.PHONE));
        phoneField.setWidth(100, Unit.PERCENTAGE);

        emailField = new TextField(I18nProperties.getPrefixCaption("Person", NotifierDto.EMAIL));
        emailField.setWidth(100, Unit.PERCENTAGE);

        contactLayout.addComponents(phoneField, emailField);
        contactLayout.setExpandRatio(phoneField, 1);
        contactLayout.setExpandRatio(emailField, 1);
        addComponent(contactLayout);

        // Address
        addressField = new TextArea(I18nProperties.getPrefixCaption("Location", NotifierDto.ADDRESS));
        addressField.setRows(3);
        addressField.setWidth(100, Unit.PERCENTAGE);
        addComponent(addressField);

        // Notification Dates Section
        Label datesLabel = new Label("Notifier Information");
        CssStyles.style(datesLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_BOTTOM_LINE);
        addComponent(datesLabel);

        // Date fields
        HorizontalLayout dateLayout = new HorizontalLayout();
        dateLayout.setSpacing(true);
        dateLayout.setWidth(100, Unit.PERCENTAGE);

        notificationDateField = new DateField(I18nProperties.getCaption(Captions.Notification_dateOfNotification));
        notificationDateField.setWidth(100, Unit.PERCENTAGE);
        notificationDateField.setEnabled(false); // Read-only as it's automatically set
        notificationDateField.setDescription("This date is automatically set based on when the notifier is created or modified");

        diagnosticDateField = new DateField(I18nProperties.getCaption(Captions.SurveillanceReport_dateOfDiagnosis));
        diagnosticDateField.setWidth(100, Unit.PERCENTAGE);
        diagnosticDateField.setEnabled(false); // Disabled as it's only available in surveillance report context
        diagnosticDateField.setDescription("Diagnostic date is only available when editing surveillance reports, not when editing notifiers");

        dateLayout.addComponents(notificationDateField, diagnosticDateField);
        dateLayout.setExpandRatio(notificationDateField, 1);
        dateLayout.setExpandRatio(diagnosticDateField, 1);
        addComponent(dateLayout);

        // Treatment Section
        treatmentGroup = buildTreatmentOptions();
        addComponent(treatmentGroup);

        // Reporting Agent Section
        Label agentLabel = new Label(I18nProperties.getCaption(Captions.Notification_reportingAgent));
        CssStyles.style(agentLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_BOTTOM_LINE);
        addComponent(agentLabel);

        // Agent fields
        HorizontalLayout agentLayout = new HorizontalLayout();
        agentLayout.setSpacing(true);
        agentLayout.setWidth(100, Unit.PERCENTAGE);

        agentFirstNameField = new TextField(I18nProperties.getCaption(Captions.firstName));
        agentFirstNameField.setWidth(100, Unit.PERCENTAGE);

        agentLastNameField = new TextField(I18nProperties.getCaption(Captions.lastName));
        agentLastNameField.setWidth(100, Unit.PERCENTAGE);

        agentLayout.addComponents(agentFirstNameField, agentLastNameField);
        agentLayout.setExpandRatio(agentFirstNameField, 1);
        agentLayout.setExpandRatio(agentLastNameField, 1);
        addComponent(agentLayout);
    }

    /**
     * Builds treatment options as a radio button group.
     */
    private RadioButtonGroup<TreatmentOption> buildTreatmentOptions() {
        RadioButtonGroup<TreatmentOption> group = new RadioButtonGroup<>(I18nProperties.getCaption(Captions.Treatment));
        group.setDataProvider(DataProvider.ofCollection(TreatmentOption.ALL_OPTIONS));
        group.setItemCaptionGenerator(TreatmentOption::getCaption);
        group.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        group.setValue(TreatmentOption.UNKNOWN); // Default value

        return group;
    }

    /**
     * Populates form fields with notifier and related data.
     */
    private void populateFields() {
        if (notifier != null) {
            firstNameField.setValue(notifier.getFirstName() != null ? notifier.getFirstName() : "");
            lastNameField.setValue(notifier.getLastName() != null ? notifier.getLastName() : "");
            registrationNumberField.setValue(notifier.getRegistrationNumber() != null ? notifier.getRegistrationNumber() : "");
            phoneField.setValue(notifier.getPhone() != null ? notifier.getPhone() : "");
            emailField.setValue(notifier.getEmail() != null ? notifier.getEmail() : "");
            addressField.setValue(notifier.getAddress() != null ? notifier.getAddress() : "");
            agentFirstNameField.setValue(notifier.getAgentFirstName() != null ? notifier.getAgentFirstName() : "");
            agentLastNameField.setValue(notifier.getAgentLastName() != null ? notifier.getAgentLastName() : "");

            // Set notification date to notifier's creation/modification date
            if (notifier.getCreationDate() != null) {
                notificationDateField.setValue(notifier.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else if (notifier.getChangeDate() != null) {
                notificationDateField.setValue(notifier.getChangeDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        } else {
            // For new notifier, set notification date to current date
            notificationDateField.setValue(java.time.LocalDate.now());
        }

        treatmentGroup.clear();

        if (therapy != null) {
            if (therapy.isTreatmentNotApplicable()) {
                treatmentGroup.setValue(TreatmentOption.NOT_APPLICABLE);
            } else {
                final YesNoUnknown treatmentStarted = therapy.getTreatmentStarted();
                if (treatmentStarted != null) {
                    treatmentGroup.setValue(TreatmentOption.forValue(treatmentStarted));
                }
            }
        }
    }

    /**
     * Gets current notifier data from form fields.
     */
    public NotifierDto getValue() {
        if (notifier == null) {
            notifier = new NotifierDto();
        }

        notifier.setFirstName(firstNameField.getValue());
        notifier.setLastName(lastNameField.getValue());
        notifier.setRegistrationNumber(registrationNumberField.getValue());
        notifier.setPhone(phoneField.getValue());
        notifier.setEmail(emailField.getValue());
        notifier.setAddress(addressField.getValue());
        notifier.setAgentFirstName(agentFirstNameField.getValue());
        notifier.setAgentLastName(agentLastNameField.getValue());

        // Note: notification date is handled automatically by the system
        // based on creation/modification times, not user input

        return notifier;
    }

    /**
     * Gets diagnostic date from form (always null in notifier context).
     */
    public LocalDate getDiagnosticDate() {
        // Always returns null as the field is disabled in notifier context
        return null; // diagnosticDateField.getValue();
    }

    /**
     * Gets selected treatment option from form.
     */
    public TreatmentOption getSelectedTreatmentOption() {
        return treatmentGroup.getValue();
    }

    /**
     * Sets notifier data and populates form fields.
     */
    public void setValue(NotifierDto notifier) {
        this.notifier = notifier;
        populateFields();
    }

    /**
     * Validates form and returns true if all required fields are filled.
     */
    public boolean isValid() {
        boolean valid = true;

        if (firstNameField.getValue() == null || firstNameField.getValue().trim().isEmpty()) {
            firstNameField.setComponentError(new com.vaadin.server.UserError("Required field"));
            valid = false;
        } else {
            firstNameField.setComponentError(null);
        }

        if (lastNameField.getValue() == null || lastNameField.getValue().trim().isEmpty()) {
            lastNameField.setComponentError(new com.vaadin.server.UserError("Required field"));
            valid = false;
        } else {
            lastNameField.setComponentError(null);
        }

        if (registrationNumberField.getValue() == null || registrationNumberField.getValue().trim().isEmpty()) {
            registrationNumberField.setComponentError(new com.vaadin.server.UserError("Required field"));
            valid = false;
        } else {
            registrationNumberField.setComponentError(null);
        }

        return valid;
    }
}
