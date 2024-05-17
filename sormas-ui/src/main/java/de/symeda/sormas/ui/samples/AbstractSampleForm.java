package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.CssStyles.HSPACE_RIGHT_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sample.AdditionalTestType;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SamplingReason;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;

public abstract class AbstractSampleForm extends AbstractEditForm<SampleDto> {

	private static final long serialVersionUID = -2323128076462668517L;

	protected static final String PATHOGEN_TESTING_INFO_LOC = "pathogenTestingInfoLoc";
	protected static final String ADDITIONAL_TESTING_INFO_LOC = "additionalTestingInfoLoc";
	protected static final String PATHOGEN_TESTING_READ_HEADLINE_LOC = "pathogenTestingReadHeadlineLoc";
	protected static final String ADDITIONAL_TESTING_READ_HEADLINE_LOC = "additionalTestingReadHeadlineLoc";
	protected static final String REQUESTED_PATHOGEN_TESTS_READ_LOC = "requestedPathogenTestsReadLoc";
	protected static final String REQUESTED_ADDITIONAL_TESTS_READ_LOC = "requestedAdditionalTestsReadLoc";
	protected static final String REPORT_INFO_LABEL_LOC = "reportInfoLabelLoc";
	protected static final String REFERRED_FROM_BUTTON_LOC = "referredFromButtonLoc";

	//@formatter:off
    protected static final String SAMPLE_COMMON_HTML_LAYOUT =
            fluidRowLocs(4, SampleDto.UUID, 4, REPORT_INFO_LABEL_LOC, 3,SampleDto.REPORTING_USER, 1, "") +
                    fluidRowLocs(SampleDto.SAMPLE_PURPOSE) +
                    fluidRowLocs(SampleDto.SAMPLE_DATE_TIME, SampleDto.SAMPLE_MATERIAL) +
                    fluidRowLocs("", SampleDto.SAMPLE_MATERIAL_TEXT) +
                    fluidRowLocs(SampleDto.SAMPLING_REASON, SampleDto.SAMPLING_REASON_DETAILS) +
                    fluidRowLocs(SampleDto.SAMPLE_SOURCE, "") +
                    fluidRowLocs(SampleDto.FIELD_SAMPLE_ID, REFERRED_FROM_BUTTON_LOC) +
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
                    fluidRowLocs(SampleDto.PATHOGEN_TEST_RESULT) +
					fluidRowLocs(CaseDataDto.DELETION_REASON) +
					fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);
    //@formatter:on

	protected AbstractSampleForm(Class<SampleDto> type, String propertyI18nPrefix, Disease disease, UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			type,
			propertyI18nPrefix,
			true,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			fieldAccessCheckers);
	}

	protected void addCommonFields() {

		final NullableOptionGroup samplePurpose = addField(SampleDto.SAMPLE_PURPOSE, NullableOptionGroup.class);
		addField(SampleDto.UUID).setReadOnly(true);
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
		final ComboBox lab = addInfrastructureField(SampleDto.LAB);
		lab.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		final TextField labDetails = addField(SampleDto.LAB_DETAILS, TextField.class);
		labDetails.setVisible(false);
		lab.addValueChangeListener(event -> updateLabDetailsVisibility(labDetails, event));

		addField(SampleDto.SPECIMEN_CONDITION, ComboBox.class);
		addField(SampleDto.NO_TEST_POSSIBLE_REASON, TextField.class);
		TextArea comment = addField(SampleDto.COMMENT, TextArea.class);
		comment.setRows(4);
		comment.setDescription(
			I18nProperties.getPrefixDescription(SampleDto.I18N_PREFIX, SampleDto.COMMENT, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		addField(SampleDto.SHIPPED, CheckBox.class);
		addField(SampleDto.RECEIVED, CheckBox.class);

		ComboBox testResultField = addField(SampleDto.PATHOGEN_TEST_RESULT, ComboBox.class);
		testResultField.removeItem(PathogenTestResultType.NOT_DONE);

		addFields(SampleDto.SAMPLING_REASON, SampleDto.SAMPLING_REASON_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			SampleDto.SAMPLING_REASON_DETAILS,
			SampleDto.SAMPLING_REASON,
			Collections.singletonList(SamplingReason.OTHER_REASON),
			true);

		addField(SampleDto.DELETION_REASON);
		addField(SampleDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, SampleDto.DELETION_REASON, SampleDto.OTHER_DELETION_REASON);

	}

	protected void defaultValueChangeListener() {

		final NullableOptionGroup samplePurposeField = (NullableOptionGroup) getField(SampleDto.SAMPLE_PURPOSE);
		final Field<?> receivedField = getField(SampleDto.RECEIVED);
		final Field<?> shippedField = getField(SampleDto.SHIPPED);

		samplePurposeField.setRequired(true);

		Disease disease = null;
		final CaseReferenceDto associatedCase = getValue().getAssociatedCase();
		if (associatedCase != null && UiUtil.permitted(UserRight.CASE_VIEW)) {
			disease = FacadeProvider.getCaseFacade().getCaseDataByUuid(associatedCase.getUuid()).getDisease();
		} else {
			final ContactReferenceDto associatedContact = getValue().getAssociatedContact();
			if (associatedContact != null && UiUtil.permitted(UserRight.CONTACT_VIEW)) {
				disease = FacadeProvider.getContactFacade().getByUuid(associatedContact.getUuid()).getDisease();
			}
		}

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION),
			SampleDto.RECEIVED,
			Arrays.asList(true),
			true);
		FieldHelper.setEnabledWhen(
			getFieldGroup(),
			receivedField,
			Arrays.asList(true),
			Arrays.asList(SampleDto.RECEIVED_DATE, SampleDto.LAB_SAMPLE_ID, SampleDto.SPECIMEN_CONDITION),
			true);

		if (disease != Disease.NEW_INFLUENZA) {
			getField(SampleDto.SAMPLE_SOURCE).setVisible(false);
		}

		UserReferenceDto reportingUser = getValue().getReportingUser();
		if (UiUtil.permitted(UserRight.SAMPLE_EDIT_NOT_OWNED) || (reportingUser != null && UiUtil.getUserUuid().equals(reportingUser.getUuid()))) {
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

		StringBuilder reportInfoText = new StringBuilder().append(I18nProperties.getString(Strings.reportedOn))
			.append(" ")
			.append(DateFormatHelper.formatLocalDateTime(getValue().getReportDateTime()));
		if (reportingUser != null) {
			reportInfoText.append(" ").append(I18nProperties.getString(Strings.by)).append(" ");
		}
		Label reportInfoLabel = new Label(reportInfoText.toString());
		reportInfoLabel.setEnabled(false);
		getContent().addComponent(reportInfoLabel, REPORT_INFO_LABEL_LOC);
		UserField reportingUserField = addField(SampleDto.REPORTING_USER, UserField.class);
		reportingUserField.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		reportingUserField.setReadOnly(true);
	}

	protected void updateLabDetailsVisibility(TextField labDetails, Property.ValueChangeEvent event) {
		if (event.getProperty().getValue() != null
			&& ((FacilityReferenceDto) event.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
			labDetails.setVisible(true);
			labDetails.setRequired(isEditableAllowed(labDetails));
		} else {
			labDetails.setVisible(false);
			labDetails.setRequired(false);
			labDetails.clear();
		}
	}

	protected void addValidators() {
		// Validators
		final DateTimeField sampleDateField = getField(SampleDto.SAMPLE_DATE_TIME);
		final DateField shipmentDate = getField(SampleDto.SHIPMENT_DATE);
		final DateField receivedDate = getField(SampleDto.RECEIVED_DATE);

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

		List<AbstractField<Date>> validatedFields = Arrays.asList(sampleDateField, shipmentDate, receivedDate);
		validatedFields.forEach(field -> field.addValueChangeListener(r -> {
			validatedFields.forEach(otherField -> {
				otherField.setValidationVisible(!otherField.isValid());
			});
		}));
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
		requestedPathogenTestsField.addItems(
			Arrays.stream(PathogenTestType.values())
				.filter(c -> fieldVisibilityCheckers.isVisible(PathogenTestType.class, c.name()))
				.collect(Collectors.toList()));
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
		UserReferenceDto reportingUser = getValue() != null ? getValue().getReportingUser() : null;
		boolean canEditRequest = showRequestFields
			&& (UiUtil.permitted(UserRight.SAMPLE_EDIT_NOT_OWNED) || reportingUser != null && UiUtil.getUserUuid().equals(reportingUser.getUuid()));
		boolean canOnlyReadRequests = !canEditRequest && showRequestFields;
		boolean canUseAdditionalTests = UiUtil.permitted(FeatureType.ADDITIONAL_TESTS, UserRight.ADDITIONAL_TEST_VIEW);

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
