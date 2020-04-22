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

import static de.symeda.sormas.ui.utils.CssStyles.HSPACE_RIGHT_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
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
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.*;

public class SampleEditForm extends AbstractEditForm<SampleDto> {
	
	private static final long serialVersionUID = 1L;
	
	private static final String REPORT_INFORMATION_LOC = "reportInformationLoc";
	private static final String PATHOGEN_TESTING_INFO_LOC = "pathogenTestingInfoLoc";
	private static final String ADDITIONAL_TESTING_INFO_LOC = "additionalTestingInfoLoc";
	private static final String PATHOGEN_TESTING_READ_HEADLINE_LOC = "pathogenTestingReadHeadlineLoc";
	private static final String ADDITIONAL_TESTING_READ_HEADLINE_LOC = "additionalTestingReadHeadlineLoc";
	private static final String REQUESTED_PATHOGEN_TESTS_READ_LOC = "requestedPathogenTestsReadLoc";
	private static final String REQUESTED_ADDITIONAL_TESTS_READ_LOC = "requestedAdditionalTestsReadLoc";

	private static final String HTML_LAYOUT = 
			h3(I18nProperties.getString(Strings.headingLaboratorySample)) +
			loc(REPORT_INFORMATION_LOC) +
			//XXX #1620 are the divs needed?
			divs(
					fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_PURPOSE),
					fluidRowLocs(SampleDto.SAMPLE_MATERIAL, SampleDto.SAMPLE_MATERIAL_TEXT),
					fluidRowLocs(SampleDto.SAMPLE_SOURCE, ""),
					fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS)
			) +
			loc(SampleDto.PATHOGEN_TESTING_REQUESTED) +
			loc(PATHOGEN_TESTING_READ_HEADLINE_LOC) +
			loc(PATHOGEN_TESTING_INFO_LOC) +
			loc(SampleDto.REQUESTED_PATHOGEN_TESTS) +
			loc(SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS) +
			loc(REQUESTED_PATHOGEN_TESTS_READ_LOC) +
			loc(SampleDto.ADDITIONAL_TESTING_REQUESTED) +
			loc(ADDITIONAL_TESTING_READ_HEADLINE_LOC) +
			loc(ADDITIONAL_TESTING_INFO_LOC) +
			loc(SampleDto.REQUESTED_ADDITIONAL_TESTS) +
			loc(SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS) +
			loc(REQUESTED_ADDITIONAL_TESTS_READ_LOC) +
			locCss(VSPACE_TOP_3, SampleDto.SHIPPED) +
			fluidRowLocs(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS) +
			locCss(VSPACE_TOP_3, SampleDto.RECEIVED) +
			fluidRowLocs(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID) +
			fluidRowLocs(SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON) +
			fluidRowLocs(SampleDto.COMMENT, SampleDto.PATHOGEN_TEST_RESULT);

	public SampleEditForm(UserRight editOrCreateUserRight) {
		super(SampleDto.class, SampleDto.I18N_PREFIX, editOrCreateUserRight);
	}

	@Override
	protected void addFields() {
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
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		addField(SampleDto.COMMENT, TextArea.class).setRows(2);
		ComboBox samplePurpose = addField(SampleDto.SAMPLE_PURPOSE, ComboBox.class);
		samplePurpose.setRequired(true);
		CheckBox shipped = addField(SampleDto.SHIPPED, CheckBox.class);
		CheckBox received = addField(SampleDto.RECEIVED, CheckBox.class);
		ComboBox pathogenTestResultField = addField(SampleDto.PATHOGEN_TEST_RESULT, ComboBox.class);
		

		initializeRequestedTestFields();

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
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(
				SampleDto.LAB, SampleDto.SHIPPED, SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS,
				SampleDto.RECEIVED, SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION),
				SampleDto.SAMPLE_PURPOSE, Arrays.asList(SamplePurpose.EXTERNAL, null), true);
		
		samplePurpose.addValueChangeListener(e -> updateRequestedTestFields());

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

		addValueChangeListener(e -> {
			CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getAssociatedCase().getUuid());

			FieldHelper.setRequiredWhen(getFieldGroup(), received, Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.SPECIMEN_CONDITION), Arrays.asList(true));
			FieldHelper.setEnabledWhen(getFieldGroup(), received, Arrays.asList(true), Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON), true);

			if (caze.getDisease() != Disease.NEW_INFLUENZA) {
				sampleSource.setVisible(false);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED) || UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid())) {
				FieldHelper.setEnabledWhen(getFieldGroup(), shipped, Arrays.asList(true), Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS), true);
				FieldHelper.setRequiredWhen(getFieldGroup(), shipped, Arrays.asList(SampleDto.SHIPMENT_DATE), Arrays.asList(true));
				FieldHelper.setRequiredWhen(getFieldGroup(), SampleDto.SAMPLE_PURPOSE, Arrays.asList(SampleDto.LAB), Arrays.asList(SamplePurpose.EXTERNAL, null));
				setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL);
			} else {
				getField(SampleDto.SAMPLE_DATE_TIME).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL).setEnabled(false);
				getField(SampleDto.SAMPLE_MATERIAL_TEXT).setEnabled(false);
				getField(SampleDto.LAB).setEnabled(false);
				getField(SampleDto.SHIPPED).setEnabled(false);
				getField(SampleDto.SHIPMENT_DATE).setEnabled(false);
				getField(SampleDto.SHIPMENT_DETAILS).setEnabled(false);
				getField(SampleDto.SAMPLE_SOURCE).setEnabled(false);
			}

			// Initialize referral and report information
			VerticalLayout reportInfoLayout = new VerticalLayout();
			String reportInfoText = I18nProperties.getString(Strings.reportedOn) + " "
					+ DateFormatHelper.formatLocalDateTime(getValue().getReportDateTime()) + " "
					+ I18nProperties.getString(Strings.by) + " " + getValue().getReportingUser().toString();
			Label reportInfoLabel = new Label(reportInfoText);
			reportInfoLabel.setEnabled(false);
			reportInfoLayout.addComponent(reportInfoLabel);

			SampleReferenceDto referredFromRef = FacadeProvider.getSampleFacade().getReferredFrom(getValue().getUuid());
			if (referredFromRef != null) {
				SampleDto referredFrom = FacadeProvider.getSampleFacade().getSampleByUuid(referredFromRef.getUuid());
				Button referredButton = new Button(I18nProperties.getCaption(Captions.sampleReferredFrom) + " " + referredFrom.getLab().toString());
				referredButton.addStyleName(ValoTheme.BUTTON_LINK);
				referredButton.addStyleName(VSPACE_NONE);
				referredButton.addClickListener(s -> ControllerProvider.getSampleController().navigateToData(referredFrom.getUuid()));
				reportInfoLayout.addComponent(referredButton);
			}

			getContent().addComponent(reportInfoLayout, REPORT_INFORMATION_LOC);
			
			if (FacadeProvider.getPathogenTestFacade().hasPathogenTest(getValue().toReference())) {
				pathogenTestResultField.setRequired(true);
			} else {
				pathogenTestResultField.setEnabled(false);
			}
		});
	}
	
	public void updateRequieredFields() {
		
	}
	
	public void makePathogenTestResultRequired() {
		ComboBox pathogenTestResultField = (ComboBox) getFieldGroup().getField(SampleDto.PATHOGEN_TEST_RESULT);
		pathogenTestResultField.setEnabled(true);
		pathogenTestResultField.setRequired(true);
		
		if (pathogenTestResultField.getValue() == null) {
			pathogenTestResultField.setValue(PathogenTestResultType.PENDING);
		}
	}
	
	private void updateRequestedTestFields() {
	
		boolean showRequestFields = getField(SampleDto.SAMPLE_PURPOSE).getValue() != SamplePurpose.INTERNAL;
		boolean canEditRequest = showRequestFields && 
				(UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED)
				|| getValue() != null && UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid()));
		boolean canOnlyReadRequests = !canEditRequest && showRequestFields;
		boolean canUseAdditionalTests = UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW);
		
		Field<?> pathogenTestingField = getField(SampleDto.PATHOGEN_TESTING_REQUESTED);
		pathogenTestingField.setVisible(canEditRequest);
		if (!showRequestFields) {
			pathogenTestingField.clear();
		}

		Field<?> additionalTestingField = getField(SampleDto.ADDITIONAL_TESTING_REQUESTED);
		additionalTestingField.setVisible(canEditRequest && canUseAdditionalTests);
		if (!showRequestFields) {
			additionalTestingField.clear();
		}

		boolean pathogenTestsRequested = Boolean.TRUE.equals(pathogenTestingField.getValue());
		setVisible(pathogenTestsRequested, SampleDto.REQUESTED_PATHOGEN_TESTS, SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS);
		getContent().getComponent(PATHOGEN_TESTING_INFO_LOC).setVisible(pathogenTestsRequested);
		
		boolean additionalTestsRequested = Boolean.TRUE.equals(additionalTestingField.getValue());
		setVisible(additionalTestsRequested, SampleDto.REQUESTED_ADDITIONAL_TESTS, SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS);
		getContent().getComponent(ADDITIONAL_TESTING_INFO_LOC).setVisible(additionalTestsRequested);
		
		getContent().getComponent(PATHOGEN_TESTING_READ_HEADLINE_LOC).setVisible(canOnlyReadRequests);
		getContent().getComponent(ADDITIONAL_TESTING_READ_HEADLINE_LOC).setVisible(canOnlyReadRequests && canUseAdditionalTests);
		
		if (getValue() != null && canOnlyReadRequests) {
			CssLayout requestedPathogenTestsLayout = new CssLayout();
			CssStyles.style(requestedPathogenTestsLayout, VSPACE_3);
			for (PathogenTestType testType : getValue().getRequestedPathogenTests()) {
				Label testLabel = new Label(testType.toString());
				testLabel.setWidthUndefined();
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
						VSPACE_4, HSPACE_RIGHT_4);
				requestedPathogenTestsLayout.addComponent(testLabel);
			}
			getContent().addComponent(requestedPathogenTestsLayout, REQUESTED_PATHOGEN_TESTS_READ_LOC);
		} else {
			getContent().removeComponent(REQUESTED_PATHOGEN_TESTS_READ_LOC);
		}
		
		if (getValue() != null && canOnlyReadRequests && canUseAdditionalTests) {
			CssLayout requestedAdditionalTestsLayout = new CssLayout();
			CssStyles.style(requestedAdditionalTestsLayout, VSPACE_3);
			for (AdditionalTestType testType : getValue().getRequestedAdditionalTests()) {
				Label testLabel = new Label(testType.toString());
				testLabel.setWidthUndefined();
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT,
						VSPACE_4, HSPACE_RIGHT_4);
				requestedAdditionalTestsLayout.addComponent(testLabel);
			}
			getContent().addComponent(requestedAdditionalTestsLayout, REQUESTED_ADDITIONAL_TESTS_READ_LOC);
		} else {
			getContent().removeComponent(REQUESTED_ADDITIONAL_TESTS_READ_LOC);			
		}
	}
	

	private void initializeRequestedTestFields() {

		// Information texts for users that can edit the requested tests
		Label requestedPathogenInfoLabel = new Label(I18nProperties.getString(Strings.infoSamplePathogenTesting));
		getContent().addComponent(requestedPathogenInfoLabel, PATHOGEN_TESTING_INFO_LOC);
		Label requestedAdditionalInfoLabel = new Label(I18nProperties.getString(Strings.infoSampleAdditionalTesting));
		getContent().addComponent(requestedAdditionalInfoLabel, ADDITIONAL_TESTING_INFO_LOC);

		// Yes/No fields for requesting pathogen/additional tests
		OptionGroup pathogenTestingRequestedField = addField(SampleDto.PATHOGEN_TESTING_REQUESTED, OptionGroup.class);
		CssStyles.style(pathogenTestingRequestedField, CssStyles.OPTIONGROUP_CAPTION_AREA_INLINE);
		pathogenTestingRequestedField.setWidthUndefined();
		pathogenTestingRequestedField.addValueChangeListener(e -> updateRequestedTestFields());
		
		OptionGroup additionalTestingRequestedField = addField(SampleDto.ADDITIONAL_TESTING_REQUESTED, OptionGroup.class);
		CssStyles.style(additionalTestingRequestedField, CssStyles.OPTIONGROUP_CAPTION_AREA_INLINE);
		additionalTestingRequestedField.setWidthUndefined();
		additionalTestingRequestedField.addValueChangeListener(e -> updateRequestedTestFields());

		// CheckBox groups to select the requested pathogen/additional tests
		OptionGroup requestedPathogenTestsField = addField(SampleDto.REQUESTED_PATHOGEN_TESTS, OptionGroup.class);
		CssStyles.style(requestedPathogenTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedPathogenTestsField.setMultiSelect(true);
		requestedPathogenTestsField.addItems((Object[]) PathogenTestType.values());
		requestedPathogenTestsField.removeItem(PathogenTestType.OTHER);
		requestedPathogenTestsField.setCaption(null);
		OptionGroup requestedAdditionalTestsField = addField(SampleDto.REQUESTED_ADDITIONAL_TESTS, OptionGroup.class);
		CssStyles.style(requestedAdditionalTestsField, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		requestedAdditionalTestsField.setMultiSelect(true);
		requestedAdditionalTestsField.addItems((Object[]) AdditionalTestType.values());
		requestedAdditionalTestsField.setCaption(null);
		
		// Text fields to type in other tests
		TextField requestedOtherPathogenTests = addField(SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS, TextField.class);
		TextField requestedOtherAdditionalTests = addField(SampleDto.REQUESTED_OTHER_ADDITIONAL_TESTS, TextField.class);
		
		// header for read view
		Label pathogenTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedPathogenTests));
		CssStyles.style(pathogenTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(pathogenTestsHeading, PATHOGEN_TESTING_READ_HEADLINE_LOC);

		Label additionalTestsHeading = new Label(I18nProperties.getString(Strings.headingRequestedAdditionalTests));
		CssStyles.style(additionalTestsHeading, CssStyles.LABEL_BOLD, CssStyles.LABEL_SECONDARY, VSPACE_4);
		getContent().addComponent(additionalTestsHeading, ADDITIONAL_TESTING_READ_HEADLINE_LOC);
		
		updateRequestedTestFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

}
