package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.HSPACE_RIGHT_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;

public abstract class AbstractSampleForm extends AbstractEditForm<SampleDto> {

	private static final long serialVersionUID = -2323128076462668517L;

	protected static final String REPORT_INFORMATION_LOC = "reportInformationLoc";
	protected static final String PATHOGEN_TESTING_INFO_LOC = "pathogenTestingInfoLoc";
	protected static final String ADDITIONAL_TESTING_INFO_LOC = "additionalTestingInfoLoc";
	protected static final String PATHOGEN_TESTING_READ_HEADLINE_LOC = "pathogenTestingReadHeadlineLoc";
	protected static final String ADDITIONAL_TESTING_READ_HEADLINE_LOC = "additionalTestingReadHeadlineLoc";
	protected static final String REQUESTED_PATHOGEN_TESTS_READ_LOC = "requestedPathogenTestsReadLoc";
	protected static final String REQUESTED_ADDITIONAL_TESTS_READ_LOC = "requestedAdditionalTestsReadLoc";

	//@formatter:off
   protected static final String SAMPLE_COMMON_HTML_LAYOUT =
                    fluidRowLocs(SampleDto.SAMPLE_PURPOSE) +
                    fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL) +
                    fluidRowLocs("", SampleDto.SAMPLE_MATERIAL_TEXT) +
                    fluidRowLocs(SampleDto.SAMPLE_SOURCE, "") +
                    fluidRowLocs(SampleDto.FIELD_SAMPLE_ID, "") +
                    fluidRowLocs(SampleDto.LAB, SampleDto.LAB_DETAILS) +

                    locCss(VSPACE_TOP_3, SampleDto.PATHOGEN_TESTING_REQUESTED) +
                    loc(PATHOGEN_TESTING_READ_HEADLINE_LOC) +
                    loc(PATHOGEN_TESTING_INFO_LOC) +
                    loc(SampleDto.REQUESTED_PATHOGEN_TESTS) +
                    loc(SampleDto.REQUESTED_OTHER_PATHOGEN_TESTS) +
                    loc(REQUESTED_PATHOGEN_TESTS_READ_LOC) +

                    locCss(VSPACE_TOP_3, SampleDto.ADDITIONAL_TESTING_REQUESTED) +
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
                    fluidRowLocs(SampleDto.COMMENT) +
                    fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT);
    //@formatter:on

	public AbstractSampleForm(Class<SampleDto> type, String propertyI18nPrefix) {
		super(type, propertyI18nPrefix);
	}

	protected AbstractSampleForm(Class<SampleDto> type, String propertyI18nPrefix, boolean addFields) {
		super(type, propertyI18nPrefix, addFields);
	}

	protected void addCommonFields() {

		final OptionGroup samplePurpose = addField(SampleDto.SAMPLE_PURPOSE, OptionGroup.class);
		samplePurpose.addValueChangeListener(e -> updateRequestedTestFields());
		addField(SampleDto.LAB_SAMPLE_ID, TextField.class);
		final DateTimeField sampleDateField = addField(SampleDto.SAMPLE_DATE_TIME, DateTimeField.class);
		sampleDateField.setInvalidCommitted(false);
		addField(SampleDto.SAMPLE_MATERIAL, ComboBox.class);
		addField(SampleDto.SAMPLE_MATERIAL_TEXT, TextField.class);
		addField(SampleDto.SAMPLE_SOURCE, ComboBox.class);
		addField(SampleDto.FIELD_SAMPLE_ID, TextField.class);
		addDateField(SampleDto.SHIPMENT_DATE, DateField.class, 7);
		addField(SampleDto.SHIPMENT_DETAILS, TextField.class);
		addField(SampleDto.RECEIVED_DATE, DateField.class);
		final ComboBox lab = addField(SampleDto.LAB, ComboBox.class);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		final TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		lab.addValueChangeListener(event -> updateLabDetailsVisibility(labDetails, event));

		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		addField(SampleDto.COMMENT, TextArea.class).setRows(2);
		addField(SampleDto.SHIPPED, CheckBox.class);
		addField(SampleDto.RECEIVED, CheckBox.class);

		addField(SampleDto.PATHOGEN_TEST_RESULT, ComboBox.class);
	}

	protected void defaultValueChangeListener() {

		final OptionGroup samplePurposeField = (OptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
		final Field<?> receivedField = getField(SampleDto.RECEIVED);
		final Field<?> shippedField = getField(SampleDto.SHIPPED);

		samplePurposeField.setRequired(true);

		Disease disease = null;
		final CaseReferenceDto associatedCase = getValue().getAssociatedCase();
		if (associatedCase != null) {
			disease = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid()).getDisease();
		} else {
			final ContactReferenceDto associatedContact = getValue().getAssociatedContact();
			if (associatedContact != null) {
				disease = FacadeProvider.getContactFacade().getContactByUuid(associatedContact.getUuid()).getDisease();
			}
		}

		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.SPECIMEN_CONDITION),
			Arrays.asList(true));
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION, SampleDto.NO_TEST_POSSIBLE_REASON),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);

		if (disease != Disease.NEW_INFLUENZA) {
			getField(SampleDto.SAMPLE_SOURCE).setVisible(false);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED)
			|| UserProvider.getCurrent().getUuid().equals(getValue().getReportingUser().getUuid())) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS),
				SampleDto.SHIPPED,
				Arrays.asList(true),
				true);
			FieldHelper.setEnabledWhen(
				getFieldGroup(),
				shippedField,
				Arrays.asList(true),
				Arrays.asList(SampleDto.SHIPMENT_DATE, SampleDto.SHIPMENT_DETAILS),
				true);
			FieldHelper.setRequiredWhen(getFieldGroup(), shippedField, Arrays.asList(SampleDto.SHIPMENT_DATE), Arrays.asList(true));
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				SampleDto.SAMPLE_PURPOSE,
				Arrays.asList(SampleDto.LAB),
				Arrays.asList(SamplePurpose.EXTERNAL, null));
			setRequired(true, SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL);
		} else {
			getField(SampleDto.SAMPLE_DATE_TIME).setEnabled(false);
			getField(SampleDto.SAMPLE_MATERIAL).setEnabled(false);
			getField(SampleDto.SAMPLE_MATERIAL_TEXT).setEnabled(false);
			getField(SampleDto.LAB).setEnabled(false);
			shippedField.setEnabled(false);
			getField(SampleDto.SHIPMENT_DATE).setEnabled(false);
			getField(SampleDto.SHIPMENT_DETAILS).setEnabled(false);
			getField(SampleDto.SAMPLE_SOURCE).setEnabled(false);
		}

		// Initialize referral and report information
		VerticalLayout reportInfoLayout = new VerticalLayout();
		String reportInfoText =
			I18nProperties.getString(Strings.reportedOn) + " " + DateFormatHelper.formatLocalDateTime(getValue().getReportDateTime()) + " "
				+ I18nProperties.getString(Strings.by) + " " + getValue().getReportingUser().toString();
		Label reportInfoLabel = new Label(reportInfoText);
		reportInfoLabel.setEnabled(false);
		reportInfoLayout.addComponent(reportInfoLabel);

		SampleReferenceDto referredFromRef = FacadeProvider.getSampleFacade().getReferredFrom(getValue().getUuid());
		if (referredFromRef != null) {
			SampleDto referredFrom = FacadeProvider.getSampleFacade().getSampleByUuid(referredFromRef.getUuid());
			FacilityReferenceDto referredFromLab = referredFrom.getLab();
			String referredButtonCaption = referredFromLab == null
				? I18nProperties.getCaption(Captions.sampleReferredFromInternal) + " ("
					+ DateFormatHelper.formatLocalDateTime(referredFrom.getSampleDateTime()) + ")"
				: I18nProperties.getCaption(Captions.sampleReferredFrom) + " " + referredFromLab.toString();
			Button referredButton = ButtonHelper.createButtonWithCaption(
				"referredFrom",
				referredButtonCaption,
				event -> ControllerProvider.getSampleController().navigateToData(referredFrom.getUuid()),
				ValoTheme.BUTTON_LINK,
				VSPACE_NONE);

			reportInfoLayout.addComponent(referredButton);
		}

		getContent().addComponent(reportInfoLayout, REPORT_INFORMATION_LOC);
	}

	protected void updateLabDetailsVisibility(TextField labDetails, Property.ValueChangeEvent event) {
		if (event.getProperty().getValue() != null
			&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
			labDetails.setVisible(true);
			labDetails.setRequired(true);
		} else {
			labDetails.setVisible(false);
			labDetails.setRequired(false);
			labDetails.clear();
		}
	}

	protected void addValidators() {
		// Validators
		final DateTimeField sampleDateField = (DateTimeField) getField(SampleDto.SAMPLE_DATE_TIME);
		final DateField shipmentDate = (DateField) getField(SampleDto.SHIPMENT_DATE);
		final DateField receivedDate = (DateField) getField(SampleDto.RECEIVED_DATE);

		sampleDateField.addValidator(
			new DateComparisonValidator(
				sampleDateField,
				shipmentDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), shipmentDate.getCaption())));
		sampleDateField.addValidator(
			new DateComparisonValidator(
				sampleDateField,
				receivedDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, sampleDateField.getCaption(), receivedDate.getCaption())));
		shipmentDate.addValidator(
			new DateComparisonValidator(
				shipmentDate,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, shipmentDate.getCaption(), sampleDateField.getCaption())));
		shipmentDate.addValidator(
			new DateComparisonValidator(
				shipmentDate,
				receivedDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, shipmentDate.getCaption(), receivedDate.getCaption())));
		receivedDate.addValidator(
			new DateComparisonValidator(
				receivedDate,
				sampleDateField,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), sampleDateField.getCaption())));
		receivedDate.addValidator(
			new DateComparisonValidator(
				receivedDate,
				shipmentDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, receivedDate.getCaption(), shipmentDate.getCaption())));
	}

	protected void setVisibilities() {

		FieldHelper
			.setVisibleWhen(getFieldGroup(), SampleDto.SAMPLE_MATERIAL_TEXT, SampleDto.SAMPLE_MATERIAL, Arrays.asList(SampleMaterial.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.NO_TEST_POSSIBLE_REASON,
			SampleDto.SPECIMEN_CONDITION,
			Arrays.asList(SpecimenCondition.NOT_ADEQUATE),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			SampleDto.SAMPLE_MATERIAL,
			Arrays.asList(SampleDto.SAMPLE_MATERIAL_TEXT),
			Arrays.asList(SampleMaterial.OTHER));
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			SampleDto.SPECIMEN_CONDITION,
			Arrays.asList(SampleDto.NO_TEST_POSSIBLE_REASON),
			Arrays.asList(SpecimenCondition.NOT_ADEQUATE));
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.LAB, SampleDto.SHIPPED, SampleDto.RECEIVED),
			SampleDto.SAMPLE_PURPOSE,
			Arrays.asList(SamplePurpose.EXTERNAL, null),
			true);
	}

	protected void initializeRequestedTestFields() {

		// Information texts for users that can edit the requested tests
		Label requestedPathogenInfoLabel = new Label(I18nProperties.getString(Strings.infoSamplePathogenTesting));
		getContent().addComponent(requestedPathogenInfoLabel, PATHOGEN_TESTING_INFO_LOC);
		Label requestedAdditionalInfoLabel = new Label(I18nProperties.getString(Strings.infoSampleAdditionalTesting));
		getContent().addComponent(requestedAdditionalInfoLabel, ADDITIONAL_TESTING_INFO_LOC);

		// Yes/No fields for requesting pathogen/additional tests
		CheckBox pathogenTestingRequestedField = addField(SampleDto.PATHOGEN_TESTING_REQUESTED, CheckBox.class);
		pathogenTestingRequestedField.setWidthUndefined();
		pathogenTestingRequestedField.addValueChangeListener(e -> updateRequestedTestFields());

		CheckBox additionalTestingRequestedField = addField(SampleDto.ADDITIONAL_TESTING_REQUESTED, CheckBox.class);
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

	private void updateRequestedTestFields() {

		boolean showRequestFields = getField(SampleDto.SAMPLE_PURPOSE).getValue() != SamplePurpose.INTERNAL;
		boolean canEditRequest = showRequestFields
			&& (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_EDIT_NOT_OWNED)
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
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, VSPACE_4, HSPACE_RIGHT_4);
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
				CssStyles.style(testLabel, CssStyles.LABEL_ROUNDED_CORNERS, CssStyles.LABEL_BACKGROUND_FOCUS_LIGHT, VSPACE_4, HSPACE_RIGHT_4);
				requestedAdditionalTestsLayout.addComponent(testLabel);
			}
			getContent().addComponent(requestedAdditionalTestsLayout, REQUESTED_ADDITIONAL_TESTS_READ_LOC);
		} else {
			getContent().removeComponent(REQUESTED_ADDITIONAL_TESTS_READ_LOC);
		}
	}
}
