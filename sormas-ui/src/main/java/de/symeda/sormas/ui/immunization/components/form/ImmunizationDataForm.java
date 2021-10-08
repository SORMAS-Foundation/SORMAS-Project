package de.symeda.sormas.ui.immunization.components.form;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.vaadin.v7.data.util.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SearchSpecificLayout;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.vaccination.VaccinationsField;

@SuppressWarnings("deprecation")
public class ImmunizationDataForm extends AbstractEditForm<ImmunizationDto> {

	private static final String OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS = "overwriteImmunizationManagementStatus";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String FACILITY_TYPE_GROUP_LOC = "facilityTypeGroupLoc";
	private static final String VACCINATION_HEADING_LOC = "vaccinationHeadingLoc";
	private static final String RECOVERY_HEADING_LOC = "recoveryHeadingLoc";
	private static final String LINK_IMMUNIZATION_TO_CASE_BTN_LOC = "linkImmunizationToCaseBtnLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = fluidRowLocs(ImmunizationDto.UUID, ImmunizationDto.EXTERNAL_ID)
		+ fluidRowLocs(ImmunizationDto.REPORT_DATE, ImmunizationDto.REPORTING_USER)
		+ fluidRowLocs(ImmunizationDto.DISEASE, ImmunizationDto.DISEASE_DETAILS)
		+ fluidRowLocs(ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS)
		+ fluidRowLocs(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS)
		+ fluidRowLocs(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ImmunizationDto.IMMUNIZATION_STATUS)
		+ fluidRowLocs(ImmunizationDto.PREVIOUS_INFECTION, ImmunizationDto.LAST_INFECTION_DATE)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.COUNTRY))
		+ fluidRowLocs(ImmunizationDto.ADDITIONAL_DETAILS)
		+ fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.RESPONSIBLE_REGION, ImmunizationDto.RESPONSIBLE_DISTRICT, ImmunizationDto.RESPONSIBLE_COMMUNITY)
		+ fluidRowLocs(FACILITY_TYPE_GROUP_LOC, ImmunizationDto.FACILITY_TYPE)
		+ fluidRowLocs(ImmunizationDto.HEALTH_FACILITY, ImmunizationDto.HEALTH_FACILITY_DETAILS)
		+ fluidRowLocs(ImmunizationDto.START_DATE, ImmunizationDto.END_DATE)
		+ fluidRowLocs(ImmunizationDto.VALID_FROM, ImmunizationDto.VALID_UNTIL)
		+ fluidRowLocs(VACCINATION_HEADING_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.NUMBER_OF_DOSES))
		+ fluidRowLocs(ImmunizationDto.VACCINATIONS)
		+ fluidRowLocs(RECOVERY_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, ImmunizationDto.RECOVERY_DATE, LINK_IMMUNIZATION_TO_CASE_BTN_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.COUNTRY));
	//@formatter:on

	private final CaseReferenceDto relatedCase;
	private Boolean ignoreMeansOfImmunizationChange = false;
	private MeansOfImmunization previousMeansOfImmunization;

	public ImmunizationDataForm(boolean isPseudonymized, CaseReferenceDto relatedCase) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getDefault(isPseudonymized));
		this.relatedCase = relatedCase;
		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		TextField immunizationUuuidField = addField(ImmunizationDto.UUID, TextField.class);
		immunizationUuuidField.setReadOnly(true);

		TextField externalIdField = addField(ImmunizationDto.EXTERNAL_ID, TextField.class);
		style(externalIdField, ERROR_COLOR_PRIMARY);

		addField(ImmunizationDto.REPORT_DATE, DateField.class);
		addField(ImmunizationDto.REPORTING_USER, ComboBox.class);

		addDiseaseField(ImmunizationDto.DISEASE, false);
		addField(ImmunizationDto.DISEASE_DETAILS, TextField.class);

		ComboBox meansOfImmunizationField = addField(ImmunizationDto.MEANS_OF_IMMUNIZATION, ComboBox.class);
		addField(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS, TextField.class);

		CheckBox overwriteImmunizationManagementStatus = addCustomField(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS, Boolean.class, CheckBox.class);
		overwriteImmunizationManagementStatus.addStyleName(VSPACE_3);

		ComboBox managementStatusField = addField(ImmunizationDto.IMMUNIZATION_MANAGEMENT_STATUS, ComboBox.class);
		managementStatusField.setNullSelectionAllowed(false);
		managementStatusField.setEnabled(false);

		ComboBox immunizationStatusField = addField(ImmunizationDto.IMMUNIZATION_STATUS, ComboBox.class);
		immunizationStatusField.setEnabled(false);

		addField(ImmunizationDto.PREVIOUS_INFECTION, NullableOptionGroup.class);
		addField(ImmunizationDto.LAST_INFECTION_DATE, DateField.class);

		ComboBox country = addInfrastructureField(ImmunizationDto.COUNTRY);
		country.addItems(FacadeProvider.getCountryFacade().getAllActiveAsReference());

		TextArea descriptionField = addField(ImmunizationDto.ADDITIONAL_DETAILS, TextArea.class, new ResizableTextAreaWrapper<>());
		descriptionField.setRows(2);
		descriptionField.setDescription(
			I18nProperties.getPrefixDescription(ImmunizationDto.I18N_PREFIX, ImmunizationDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingResponsibleJurisdiction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		ComboBox responsibleRegion = addInfrastructureField(ImmunizationDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		ComboBox responsibleDistrictCombo = addInfrastructureField(ImmunizationDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		ComboBox responsibleCommunityCombo = addInfrastructureField(ImmunizationDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrictCombo, responsibleCommunityCombo);

		ComboBox facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.values());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);
		ComboBox facilityType = addField(ImmunizationDto.FACILITY_TYPE);
		ComboBox facilityCombo = addInfrastructureField(ImmunizationDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		TextField facilityDetails = addField(ImmunizationDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		addField(ImmunizationDto.START_DATE, DateField.class);
		addDateField(ImmunizationDto.END_DATE, DateField.class, -1);

		addDateField(ImmunizationDto.VALID_FROM, DateField.class, -1);
		addDateField(ImmunizationDto.VALID_UNTIL, DateField.class, -1);

		MeansOfImmunization meansOfImmunizationValue = (MeansOfImmunization) meansOfImmunizationField.getValue();

		Label vaccinationHeadingLabel = new Label(I18nProperties.getString(Strings.headingVaccination));
		vaccinationHeadingLabel.addStyleName(H3);
		getContent().addComponent(vaccinationHeadingLabel, VACCINATION_HEADING_LOC);
		vaccinationHeadingLabel.setVisible(shouldShowVaccinationFields(meansOfImmunizationValue));

		TextField numberOfDosesField = addField(ImmunizationDto.NUMBER_OF_DOSES, TextField.class);
		numberOfDosesField.setConverter(new StringToIntegerConverter());
		numberOfDosesField.setVisible(shouldShowVaccinationFields(meansOfImmunizationValue));

		VaccinationsField vaccinationsField = addField(ImmunizationDto.VACCINATIONS, VaccinationsField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ImmunizationDto.VACCINATIONS,
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Arrays.asList(MeansOfImmunization.VACCINATION, MeansOfImmunization.VACCINATION_RECOVERY),
			false);

		Label recoveryHeadingLabel = new Label(I18nProperties.getString(Strings.headingRecovery));
		recoveryHeadingLabel.addStyleName(H3);
		getContent().addComponent(recoveryHeadingLabel, RECOVERY_HEADING_LOC);
		recoveryHeadingLabel.setVisible(shouldShowRecoveryFields(meansOfImmunizationValue));

		DateField positiveTestResultDate = addField(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, DateField.class);

		DateField recoveryDate = addField(ImmunizationDto.RECOVERY_DATE, DateField.class);

		Button linkImmunizationToCaseButton;
		if (relatedCase != null) {
			linkImmunizationToCaseButton = ButtonHelper.createButton(
				Captions.openLinkedCaseToImmunizationButton,
				e -> ControllerProvider.getCaseController().navigateToCase(relatedCase.getUuid()),
				ValoTheme.BUTTON_PRIMARY,
				FORCE_CAPTION);
		} else {
			linkImmunizationToCaseButton = ButtonHelper.createButton(
				Captions.linkImmunizationToCaseButton,
				e -> buildAndOpenSearchSpecificCaseWindow(),
				ValoTheme.BUTTON_PRIMARY,
				FORCE_CAPTION);
		}
		getContent().addComponent(linkImmunizationToCaseButton, LINK_IMMUNIZATION_TO_CASE_BTN_LOC);
		linkImmunizationToCaseButton.setVisible(shouldShowRecoveryFields(meansOfImmunizationValue));

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, ImmunizationDto.REPORT_DATE, ImmunizationDto.DISEASE, ImmunizationDto.MEANS_OF_IMMUNIZATION);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ImmunizationDto.DISEASE_DETAILS),
			ImmunizationDto.DISEASE,
			Arrays.asList(Disease.OTHER),
			true);
		FieldHelper
			.setRequiredWhen(getFieldGroup(), ImmunizationDto.DISEASE, Arrays.asList(ImmunizationDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS),
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Collections.singletonList(MeansOfImmunization.OTHER),
			true);

		overwriteImmunizationManagementStatus.addValueChangeListener(valueChangeEvent -> {
			boolean selectedValue = (boolean) valueChangeEvent.getProperty().getValue();
			if (!selectedValue) {
				ImmunizationManagementStatus value = getValue().getImmunizationManagementStatus();
				managementStatusField.setValue(value);
			}
			managementStatusField.setEnabled(selectedValue);
		});

		meansOfImmunizationField.addValueChangeListener(valueChangeEvent -> {
			if (!ignoreMeansOfImmunizationChange) {
				MeansOfImmunization meansOfImmunization = (MeansOfImmunization) valueChangeEvent.getProperty().getValue();
				if (MeansOfImmunization.RECOVERY.equals(meansOfImmunization) || MeansOfImmunization.OTHER.equals(meansOfImmunization)) {
					managementStatusField.setValue(ImmunizationManagementStatus.COMPLETED);
					if (CollectionUtils.isNotEmpty(vaccinationsField.getValue())) {

						VaadinUiUtil.showConfirmationPopup(
							I18nProperties.getString(Strings.headingDeleteVaccinations),
							new Label(I18nProperties.getString(Strings.messageDeleteImmunizationVaccinations)),
							questionWindow -> {
								ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

									private static final long serialVersionUID = 1L;

									@Override
									protected void onConfirm() {
										vaccinationsField.clear();
										previousMeansOfImmunization = meansOfImmunization;
										questionWindow.close();
									}

									@Override
									protected void onCancel() {
										ignoreMeansOfImmunizationChange = true;
										meansOfImmunizationField.setValue(previousMeansOfImmunization);
										ignoreMeansOfImmunizationChange = false;
										questionWindow.close();
									}
								};

								confirmationComponent.getConfirmButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
								confirmationComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));

								return confirmationComponent;
							},
							null);
					} else {
						previousMeansOfImmunization = meansOfImmunization;
					}
				} else {
					previousMeansOfImmunization = meansOfImmunization;
				}
				boolean isVaccinationVisible = shouldShowVaccinationFields(meansOfImmunization);
				vaccinationHeadingLabel.setVisible(isVaccinationVisible);
				numberOfDosesField.setVisible(isVaccinationVisible);
				if (!isVaccinationVisible) {
					numberOfDosesField.setValue(null);
				}
				boolean isRecoveryVisible = shouldShowRecoveryFields(meansOfImmunization);
				recoveryHeadingLabel.setVisible(isRecoveryVisible);
				positiveTestResultDate.setVisible(isRecoveryVisible);
				recoveryDate.setVisible(isRecoveryVisible);
			}
		});

		managementStatusField.addValueChangeListener(valueChangeEvent -> {
			ImmunizationManagementStatus managementStatusValue = (ImmunizationManagementStatus) valueChangeEvent.getProperty().getValue();
			switch (managementStatusValue) {
			case SCHEDULED:
			case ONGOING:
				immunizationStatusField.setValue(ImmunizationStatus.PENDING);
				break;
			case COMPLETED:
				immunizationStatusField.setValue(ImmunizationStatus.ACQUIRED);
				break;
			case CANCELED:
				immunizationStatusField.setValue(ImmunizationStatus.NOT_ACQUIRED);
				break;
			default:
				break;
			}
		});

		setReadOnly(true, ImmunizationDto.REPORTING_USER);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ImmunizationDto.LAST_INFECTION_DATE,
			ImmunizationDto.PREVIOUS_INFECTION,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		meansOfImmunizationField.addValueChangeListener(e -> {
			if (shouldShowRecoveryFields((MeansOfImmunization) e.getProperty().getValue())) {
				positiveTestResultDate.setVisible(true);
				recoveryDate.setVisible(true);
				linkImmunizationToCaseButton.setVisible(true);
			} else {
				positiveTestResultDate.setVisible(false);
				recoveryDate.setVisible(false);
				linkImmunizationToCaseButton.setVisible(false);
			}
		});

		responsibleDistrictCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto != null && facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
			}
		});

		responsibleCommunityCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					communityDto != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityType.getValue(), true, false)
						: responsibleDistrictCombo.getValue() != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByDistrictAndType(
									(DistrictReferenceDto) responsibleDistrictCombo.getValue(),
									(FacilityType) facilityType.getValue(),
									true,
									false)
							: null);
			}
		});

		facilityTypeGroup.addValueChangeListener(e -> {
			FieldHelper.updateEnumData(
				facilityType,
				facilityTypeGroup.getValue() != null
					? FacilityType.getTypes((FacilityTypeGroup) facilityTypeGroup.getValue())
					: Arrays.stream(FacilityType.values()).collect(Collectors.toList()));
		});
		facilityType.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (facilityType.getValue() != null && responsibleDistrictCombo.getValue() != null) {
				if (responsibleCommunityCombo.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) responsibleCommunityCombo.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) responsibleDistrictCombo.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				}
			}
		});

		facilityCombo.addValueChangeListener(e -> {
			updateFacilityFields(facilityCombo, facilityDetails);
			this.getValue().setFacilityType((FacilityType) facilityType.getValue());
		});

		addValueChangeListener(e -> {
			FacilityType facilityTypeValue = getValue().getFacilityType();
			if (facilityTypeValue != null) {
				facilityTypeGroup.setValue(facilityTypeValue.getFacilityTypeGroup());
				facilityCombo.setValue(getValue().getHealthFacility());
				facilityDetails.setValue(getValue().getHealthFacilityDetails());
			}
		});
	}

	private void buildAndOpenSearchSpecificCaseWindow() {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getCaption(Captions.caseSearchSpecificCase));
		window.setWidth(768, Unit.PIXELS);

		SearchSpecificLayout layout = buildSearchSpecificLayout(window);
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private SearchSpecificLayout buildSearchSpecificLayout(Window window) {

		String description = I18nProperties.getString(Strings.infoSpecificCaseSearch);
		String confirmCaption = I18nProperties.getCaption(Captions.caseSearchCase);

		com.vaadin.ui.TextField searchField = new com.vaadin.ui.TextField();
		Runnable confirmCallback = () -> {

			Boolean foundCase =
				FacadeProvider.getImmunizationFacade().linkRecoveryImmunizationToSearchedCase(searchField.getValue(), this.getValue());

			if (foundCase) {
				VaadinUiUtil
					.showSimplePopupWindow(I18nProperties.getString(Strings.headingCaseFound), I18nProperties.getString(Strings.messageCaseFound));

				window.close();
				SormasUI.refreshView();
			} else {
				VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getString(Strings.headingNoCaseFound),
					I18nProperties.getString(Strings.messageNoCaseFoundToLinkImmunization));
			}
		};

		return new SearchSpecificLayout(confirmCallback, window::close, searchField, description, confirmCaption);
	}

	private boolean shouldShowVaccinationFields(MeansOfImmunization meansOfImmunization) {
		return MeansOfImmunization.VACCINATION.equals(meansOfImmunization) || MeansOfImmunization.VACCINATION_RECOVERY.equals(meansOfImmunization);
	}

	private boolean shouldShowRecoveryFields(MeansOfImmunization meansOfImmunization) {
		return MeansOfImmunization.RECOVERY.equals(meansOfImmunization) || MeansOfImmunization.VACCINATION_RECOVERY.equals(meansOfImmunization);
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {

		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(otherHealthFacility);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
			}
			if (!visibleAndRequired) {
				tfFacilityDetails.clear();
			}
		} else {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}
	}

	@Override
	public void setValue(ImmunizationDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		ignoreMeansOfImmunizationChange = true;
		super.setValue(newFieldValue);
		ignoreMeansOfImmunizationChange = false;
		previousMeansOfImmunization = newFieldValue.getMeansOfImmunization();
	}
}
