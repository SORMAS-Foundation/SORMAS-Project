/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Arrays;
import java.util.Date;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleEditForm extends AbstractEditForm<SampleDto> {

	private static final String REPORT_INFORMATION_LOC = "reportInformationLoc";
	private static final String PATHOGEN_TESTING_INFO_LOC = "pathogenTestingInfoLoc";
	private static final String ADDITIONAL_TESTING_INFO_LOC = "additionalTestingInfoLoc";
	private static final String PATHOGEN_TESTING_READ_HEADLINE_LOC = "pathogenTestingReadHeadlineLoc";
	private static final String ADDITIONAL_TESTING_READ_HEADLINE_LOC = "additionalTestingReadHeadlineLoc";
	private static final String REQUESTED_PATHOGEN_TESTS_READ_LOC = "requestedPathogenTestsReadLoc";
	private static final String REQUESTED_ADDITIONAL_TESTS_READ_LOC = "requestedAdditionalTestsReadLoc";

	private static final String HTML_LAYOUT = 
			LayoutUtil.h3(I18nProperties.getString(Strings.headingLaboratorySample)) +
			LayoutUtil.locCss(CssStyles.VSPACE_2, REPORT_INFORMATION_LOC) +
			LayoutUtil.divsCss(CssStyles.VSPACE_3,
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_CODE),
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT),
					LayoutUtil.fluidRowLocs(SampleDto.SAMPLE_SOURCE, ""),
					LayoutUtil.fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS)
					) +
			LayoutUtil.loc(SampleDto.PATHOGEN_TESTING_REQUESTED) +
			LayoutUtil.loc(PATHOGEN_TESTING_READ_HEADLINE_LOC) +
			LayoutUtil.loc(PATHOGEN_TESTING_INFO_LOC) +
			LayoutUtil.loc(SampleDto.REQUESTED_PATHOGEN_TESTS) +
			LayoutUtil.loc(REQUESTED_PATHOGEN_TESTS_READ_LOC) +
			LayoutUtil.loc(SampleDto.ADDITIONAL_TESTING_REQUESTED) +
			LayoutUtil.loc(ADDITIONAL_TESTING_READ_HEADLINE_LOC) +
			LayoutUtil.loc(ADDITIONAL_TESTING_INFO_LOC) +
			LayoutUtil.loc(SampleDto.REQUESTED_ADDITIONAL_TESTS) +
			LayoutUtil.loc(REQUESTED_ADDITIONAL_TESTS_READ_LOC) +
			LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.SHIPPED) +
			LayoutUtil.fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
			LayoutUtil.locCss(CssStyles.VSPACE_TOP_3, SampleDto.RECEIVED) +
			LayoutUtil.fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
			LayoutUtil.fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON) +
			LayoutUtil.fluidRowLocs(SampleDto.COMMENT, SampleDto.PATHOGEN_TEST_RESULT);

	private boolean requestedTestsInitialized = false;
	
	private ComboBox pathogenTestResultField;

	public SampleEditForm(UserRight editOrCreateUserRight) {
		super(SampleDto.class, SampleDto.I18N_PREFIX, editOrCreateUserRight);
	}

	@Override
	protected void addFields() {
		addField(SampleDto.SAMPLE_CODE, TextField.class);
		addField(SampleDto.LAB_SAMPLE_ID, TextField.class);
		DateTimeField sampleDateField = addField(SampleDto.SAMPLE_DATE_TIME, DateTimeField.class);
		sampleDateField.setInvalidCommitted(false);
		addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		ComboBox sampleSource = addField(SampleDto.SAMPLE_SOURCE, ComboBox.class);
		DateField shipmentDate = addDateField(SampleDto.SHIPMENT_DATE, DateField.class, 7);
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);		
		DateField receivedDate = addField(SampleDto.RECEIVED_DATE, DateField.class);
		ComboBox lab = addField(SampleDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(true));
		TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		addField(SampleDto.COMMENT, TextArea.class).setRows(2);
		CheckBox shipped = addField(SampleDto.SHIPPED, CheckBox.class);
		CheckBox received = addField(SampleDto.RECEIVED, CheckBox.class);
		pathogenTestResultField = addField(SampleDto.PATHOGEN_TEST_RESULT, ComboBox.class);

		initializeRequestedTests();

		// Validators
		sampleDateField.addValidator(new DateComparisonValidator(sampleDateField, shipmentDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), shipmentDate.getCaption())));
		sampleDateField.addValidator(new DateComparisonValidator(sampleDateField, receivedDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), receivedDate.getCaption())));
		shipmentDate.addValidator(new DateComparisonValidator(shipmentDate, sampleDateField, false, false,
				I18nProperties.getValidationError(Validations.afterDate, shipmentDate.getCaption(), sampleDateField.getCaption())));
		shipmentDate.addValidator(new DateComparisonValidator(shipmentDate, receivedDate, true, false,
				I18nProperties.getValidationError(Validations.beforeDate, shipmentDate.getCaption(), receivedDate.getCaption())));
		receivedDate.addValidator(new DateComparisonValidator(receivedDate, sampleDateField, false, false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), sampleDateField.getCaption())));
		receivedDate.addValidator(new DateComparisonValidator(receivedDate, shipmentDate, false, false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), shipmentDate.getCaption())));

		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SampleDto.NO_TEST_POSSIBLE_REASON, SampleDto.SPECIMEN_CONDITION, Arrays.asList(SpecimenCondition.NOT_ADEQUATE), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT), Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SPECIMEN_CONDITION, Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON), Arrays.asList(SpecimenCondition.NOT_ADEQUATE));
		setRequired(true, SampleDto.PATHOGEN_TEST_RESULT);
		
		addValueChangeListener(e -> {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getAssociatedCase().getUuid());

			FieldHelper.setRequiredWhen(getFieldGroup(), received, Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.SPECIMEN_CONDITION), Arrays.asList(true));
			FieldHelper.setEnabledWhen(getFieldGroup(), received, Arrays.asList(true), Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON), true);

			if (caze.getDisease() != Disease.NEW_INFLUENCA) {
				sampleSource.setVisible(false);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED) || UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid())) {
				FieldHelper.setEnabledWhen(getFieldGroup(), shipped, Arrays.asList(true), Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS), true);
				FieldHelper.setRequiredWhen(getFieldGroup(), shipped, Arrays.asList(SampleDto.SHIPMENT_DATE), Arrays.asList(true));
				setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL, SampleDto.LAB);
			} else {
				getField(SampleDto.SAMPLE_DATE_TIME).setEnabled(false);
				getField(SampleDto.SAMPLE_CODE).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL_TEXT).setEnabled(false);
				getField(SampleDto.LAB).setEnabled(false);
				getField(SampleDto.SHIPPED).setEnabled(false);
				getField(SampleDto.SHIPMENT_DATE).setEnabled(false);
				getField(SampleDto.SHIPMENT_DETAILS).setEnabled(false);
				getField(SampleDto.SAMPLE_SOURCE).setEnabled(false);
				getField(SampleDto.PATHOGEN_TEST_RESULT).setEnabled(false);
			}

			shipped.addValueChangeListener(event -> {
				if ((boolean) event.getProperty().getValue() == true) {
					if (shipmentDate.getValue() == null) {
						shipmentDate.setValue(new Date());
					}
				}
			});

			received.addValueChangeListener(event -> {
				if ((boolean) event.getProperty().getValue() == true) {
					if (receivedDate.getValue() == null) {
						receivedDate.setValue(new Date());
					}
				}
			});

			// Initialize referral and report information
			VerticalLayout reportInfoLayout = new VerticalLayout();

			String reportInfoText = I18nProperties.getString(Strings.reportedOn) + " "
					+ DateHelper.formatLocalDateTime(getValue().getReportDateTime()) + " "
					+ I18nProperties.getString(Strings.by) + " " + getValue().getReportingUser().toString();
			Label reportInfoLabel = new Label(reportInfoText);
			reportInfoLabel.setEnabled(false);
			reportInfoLayout.addComponent(reportInfoLabel);

			SampleReferenceDto referredFromRef = FacadeProvider.getSampleFacade().getReferredFrom(getValue().getUuid());
			if (referredFromRef != null) {
				SampleDto referredFrom = FacadeProvider.getSampleFacade().getSampleByUuid(referredFromRef.getUuid());
				Button referredButton = new Button(I18nProperties.getCaption(Captions.sampleReferredFrom) + " " + referredFrom.getLab().toString());
				referredButton.addStyleName(ValoTheme.BUTTON_LINK);
				referredButton.addStyleName(CssStyles.VSPACE_NONE);
				referredButton.addClickListener(s -> ControllerProvider.getSampleController().navigateToData(referredFrom.getUuid()));
				reportInfoLayout.addComponent(referredButton);
			}

			getContent().addComponent(reportInfoLayout, REPORT_INFORMATION_LOC);
		});

		lab.addValueChangeListener(event -> {
			if (event.getProperty().getValue() != null && ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
				labDetails.setVisible(true);
				labDetails.setRequired(true);
			} else {
				labDetails.setVisible(false);
				labDetails.setRequired(false);
				labDetails.clear();
			}
		});
	}

	private void initializeRequestedTests() {
		// Yes/No fields for requesting pathogen/additional tests
		OptionGroup pathogenTestingRequestedField = addField(SampleDto.PATHOGEN_TESTING_REQUESTED, OptionGroup.class);
		CssStyles.style(pathogenTestingRequestedField, CssStyles.OPTIONGROUP_CAPTION_AREA_INLINE);
		pathogenTestingRequestedField.setWidthUndefined();
		pathogenTestingRequestedField.setRequired(true);
		OptionGroup additionalTestingRequestedField = addField(SampleDto.ADDITIONAL_TESTING_REQUESTED, OptionGroup.class);
		CssStyles.style(additionalTestingRequestedField, CssStyles.OPTIONGROUP_CAPTION_AREA_INLINE);
		additionalTestingRequestedField.setWidthUndefined();

		// CheckBox groups to select the requested pathogen/additional tests
		OptionGroup requestedPathogenTestsField = addField(SampleDto.REQUESTED_PATHOGEN_TESTS, OptionGroup.class);
		CssStyles.style(requestedPathogenTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedPathogenTestsField.setMultiSelect(true);
		requestedPathogenTestsField.addItems((Object[]) PathogenTestType.values());
		requestedPathogenTestsField.setCaption(null);
		OptionGroup requestedAdditionalTestsField = addField(SampleDto.REQUESTED_ADDITIONAL_TESTS, OptionGroup.class);
		CssStyles.style(requestedAdditionalTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedAdditionalTestsField.setMultiSelect(true);
		requestedAdditionalTestsField.addItems((Object[]) AdditionalTestType.values());
		requestedAdditionalTestsField.setCaption(null);
		
		// The code below relies on getValue() to return the sample of the form and therefore has to be delayed until the sample is set
		addValueChangeListener(e -> {
			if (!requestedTestsInitialized) {				
				if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED) || UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid())) {
					// Information texts for users that can edit the requested tests
					Label requestedPathogenInfoLabel = new Label(I18nProperties.getString(Strings.infoSamplePathogenTesting));
					getContent().addComponent(requestedPathogenInfoLabel, PATHOGEN_TESTING_INFO_LOC);
					Label requestedAdditionalInfoLabel = new Label(I18nProperties.getString(Strings.infoSampleAdditionalTesting));
					getContent().addComponent(requestedAdditionalInfoLabel, ADDITIONAL_TESTING_INFO_LOC);
					
					// Set initial visibility
					requestedPathogenTestsField.setVisible(Boolean.TRUE.equals(getValue().getPathogenTestingRequested()));
					requestedPathogenInfoLabel.setVisible(Boolean.TRUE.equals(getValue().getPathogenTestingRequested()));
					pathogenTestResultField.setVisible(Boolean.TRUE.equals(getValue().getPathogenTestingRequested()));
					pathogenTestResultField.setRequired(Boolean.TRUE.equals(getValue().getPathogenTestingRequested()));
					
					// CheckBoxes should be hidden when no tests are requested
					pathogenTestingRequestedField.addValueChangeListener(f -> {
						requestedPathogenInfoLabel.setVisible(f.getProperty().getValue().equals(Boolean.TRUE));
						requestedPathogenTestsField.setVisible(f.getProperty().getValue().equals(Boolean.TRUE));
						pathogenTestResultField.setVisible(Boolean.TRUE.equals(f.getProperty().getValue()));
						pathogenTestResultField.setRequired(Boolean.TRUE.equals(f.getProperty().getValue()));
						if (f.getProperty().getValue().equals(Boolean.TRUE) && pathogenTestResultField.getValue() == null) {
							pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
						}
					});

					if (!UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
						// Hide additional testing fields when user is not allowed to see them
						additionalTestingRequestedField.setVisible(false);
						requestedAdditionalTestsField.setVisible(false);
						requestedAdditionalInfoLabel.setVisible(false);
					} else {
						requestedAdditionalTestsField.setVisible(Boolean.TRUE.equals(getValue().getAdditionalTestingRequested()));
						requestedAdditionalInfoLabel.setVisible(Boolean.TRUE.equals(getValue().getAdditionalTestingRequested()));
						additionalTestingRequestedField.setRequired(true);
						
						additionalTestingRequestedField.addValueChangeListener(f -> {
							requestedAdditionalInfoLabel.setVisible(f.getProperty().getValue().equals(Boolean.TRUE));
							requestedAdditionalTestsField.setVisible(f.getProperty().getValue().equals(Boolean.TRUE));
						});
					}
				} else {
					// If the user can't edit the sample, they see a read-only list of requested tests
					setVisible(false, SampleDto.PATHOGEN_TESTING_REQUESTED, SampleDto.ADDITIONAL_TESTING_REQUESTED,
							SampleDto.REQUESTED_PATHOGEN_TESTS, SampleDto.REQUESTED_ADDITIONAL_TESTS);
					
					// Display a label for every requested pathogen test or hide the whole section if no tests have been requested
					if (Boolean.TRUE.equals(getValue().getPathogenTestingRequested()) && !getValue().getRequestedPathogenTests().isEmpty()) {
						Label pathogenTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedPathogenTests));
						CssStyles.style(pathogenTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, CssStyles.VSPACE_4);
						getContent().addComponent(pathogenTestsHeading, PATHOGEN_TESTING_READ_HEADLINE_LOC);

						CssLayout requestedPathogenTestsLayout = new CssLayout();
						CssStyles.style(requestedPathogenTestsLayout, CssStyles.VSPACE_3);
						for (PathogenTestType testType : getValue().getRequestedPathogenTests()) {
							Label testLabel = new Label(testType.toString());
							testLabel.setWidthUndefined();
							CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
									CssStyles.VSPACE_4, CssStyles.HSPACE_RIGHT_4);
							requestedPathogenTestsLayout.addComponent(testLabel);
						}
						getContent().addComponent(requestedPathogenTestsLayout, REQUESTED_PATHOGEN_TESTS_READ_LOC);
					}

					// If the user can view additional tests, display a label for every requested additional test
					// or hide the whole section if no tests have been requested
					if (UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW) && Boolean.TRUE.equals(getValue().getAdditionalTestingRequested())
							&& !getValue().getRequestedAdditionalTests().isEmpty()) {
						Label additionalTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedAdditionalTests));
						CssStyles.style(additionalTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, CssStyles.VSPACE_4);
						getContent().addComponent(additionalTestsHeading, ADDITIONAL_TESTING_READ_HEADLINE_LOC);

						CssLayout requestedAdditionalTestsLayout = new CssLayout();
						CssStyles.style(requestedAdditionalTestsLayout, CssStyles.VSPACE_3);
						for (AdditionalTestType testType : getValue().getRequestedAdditionalTests()) {
							Label testLabel = new Label(testType.toString());
							testLabel.setWidthUndefined();
							CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
									CssStyles.VSPACE_4, CssStyles.HSPACE_RIGHT_4);
							requestedAdditionalTestsLayout.addComponent(testLabel);
						}
						getContent().addComponent(requestedAdditionalTestsLayout, REQUESTED_ADDITIONAL_TESTS_READ_LOC);
					}

				}
			}

			requestedTestsInitialized = true;
		});
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
