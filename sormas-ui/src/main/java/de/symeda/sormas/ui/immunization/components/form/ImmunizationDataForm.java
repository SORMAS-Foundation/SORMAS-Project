/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.NumberValidator;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;
import de.symeda.sormas.ui.utils.UserField;
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
		+ fluidRowLocs(ImmunizationDto.NUMBER_OF_DOSES, ImmunizationDto.NUMBER_OF_DOSES_DETAILS)
		+ fluidRowLocs(ImmunizationDto.VACCINATIONS)
		+ fluidRowLocs(RECOVERY_HEADING_LOC)
		+ fluidRowLocs(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, ImmunizationDto.RECOVERY_DATE, LINK_IMMUNIZATION_TO_CASE_BTN_LOC)
		+ fluidRow(fluidColumnLoc(6, 0, ImmunizationDto.COUNTRY))
		+ fluidRowLocs(CaseDataDto.DELETION_REASON)
		+ fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);
	//@formatter:on

	private final CaseReferenceDto relatedCase;
	private boolean ignoreMeansOfImmunizationChange = false;
	private MeansOfImmunization previousMeansOfImmunization;
	private CheckBox overwriteImmunizationManagementStatus;
	private ComboBoxWithPlaceholder facilityTypeGroup;
	private final Consumer<Runnable> actionCallback;
	private ComboBox responsibleRegion;
	private ComboBox responsibleDistrict;
	private ComboBox responsibleCommunity;

	public ImmunizationDataForm(boolean isPseudonymized, boolean inJurisdiction, CaseReferenceDto relatedCase, Consumer<Runnable> actionCallback) {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized));
		this.relatedCase = relatedCase;
		this.actionCallback = actionCallback;
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
		addField(ImmunizationDto.REPORTING_USER, UserField.class);

		ComboBox cbDisease = addDiseaseField(ImmunizationDto.DISEASE, false);
		addField(ImmunizationDto.DISEASE_DETAILS, TextField.class);

		ComboBox meansOfImmunizationField = addField(ImmunizationDto.MEANS_OF_IMMUNIZATION, ComboBox.class);
		addField(ImmunizationDto.MEANS_OF_IMMUNIZATION_DETAILS, TextField.class);

		overwriteImmunizationManagementStatus = addCustomField(OVERWRITE_IMMUNIZATION_MANAGEMENT_STATUS, Boolean.class, CheckBox.class);
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

		responsibleRegion = addInfrastructureField(ImmunizationDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		responsibleDistrict = addInfrastructureField(ImmunizationDto.RESPONSIBLE_DISTRICT);
		responsibleDistrict.setRequired(true);
		responsibleCommunity = addInfrastructureField(ImmunizationDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunity.setNullSelectionAllowed(true);
		responsibleCommunity.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);

		facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.values());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);
		ComboBox facilityType = addField(ImmunizationDto.FACILITY_TYPE, ComboBoxWithPlaceholder.class);
		ComboBox facilityCombo = addInfrastructureField(ImmunizationDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		TextField facilityDetails = addField(ImmunizationDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		DateField startDate = addField(ImmunizationDto.START_DATE, DateField.class);
		DateField endDate = addDateField(ImmunizationDto.END_DATE, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(startDate, endDate);
		DateComparisonValidator.dateFieldDependencyValidationVisibility(startDate, endDate);

		DateField validFrom = addDateField(ImmunizationDto.VALID_FROM, DateField.class, -1);
		DateField validUntil = addDateField(ImmunizationDto.VALID_UNTIL, DateField.class, -1);
		DateComparisonValidator.addStartEndValidators(validFrom, validUntil);
		DateComparisonValidator.dateFieldDependencyValidationVisibility(validFrom, validUntil);

		MeansOfImmunization meansOfImmunizationValue = (MeansOfImmunization) meansOfImmunizationField.getValue();

		boolean isVaccinationVisibleInitial = shouldShowVaccinationFields(meansOfImmunizationValue);

		Label vaccinationHeadingLabel = new Label(I18nProperties.getString(Strings.headingVaccination));
		vaccinationHeadingLabel.addStyleName(H3);
		getContent().addComponent(vaccinationHeadingLabel, VACCINATION_HEADING_LOC);
		vaccinationHeadingLabel.setVisible(isVaccinationVisibleInitial);

		Field numberOfDosesField = addField(ImmunizationDto.NUMBER_OF_DOSES);
		numberOfDosesField.addValidator(new NumberValidator(I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10, false));
		numberOfDosesField.setVisible(isVaccinationVisibleInitial);

		Field numberOfDosesDetailsField = addField(ImmunizationDto.NUMBER_OF_DOSES_DETAILS);
		numberOfDosesDetailsField.setReadOnly(true);
		numberOfDosesDetailsField.setVisible(isVaccinationVisibleInitial && getValue().getNumberOfDosesDetails() != null);

		VaccinationsField vaccinationsField = addField(ImmunizationDto.VACCINATIONS, VaccinationsField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ImmunizationDto.VACCINATIONS,
			ImmunizationDto.MEANS_OF_IMMUNIZATION,
			Arrays.asList(MeansOfImmunization.VACCINATION, MeansOfImmunization.VACCINATION_RECOVERY),
			false);
		cbDisease.addValueChangeListener(e -> vaccinationsField.setDisease((Disease) cbDisease.getValue()));

		Label recoveryHeadingLabel = new Label(I18nProperties.getString(Strings.headingRecovery));
		recoveryHeadingLabel.addStyleName(H3);
		getContent().addComponent(recoveryHeadingLabel, RECOVERY_HEADING_LOC);
		recoveryHeadingLabel.setVisible(shouldShowRecoveryFields(meansOfImmunizationValue));

		DateField positiveTestResultDate = addField(ImmunizationDto.POSITIVE_TEST_RESULT_DATE, DateField.class);

		DateField recoveryDate = addField(ImmunizationDto.RECOVERY_DATE, DateField.class);

		addField(ImmunizationDto.DELETION_REASON);
		addField(ImmunizationDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, ImmunizationDto.DELETION_REASON, ImmunizationDto.OTHER_DELETION_REASON);

		Button linkImmunizationToCaseButton;
		if (relatedCase != null) {
			linkImmunizationToCaseButton = ButtonHelper.createButton(
				Captions.openLinkedCaseToImmunizationButton,
				e -> ControllerProvider.getCaseController().navigateToCase(relatedCase.getUuid()),
				ValoTheme.BUTTON_PRIMARY,
				FORCE_CAPTION);
		} else {
			linkImmunizationToCaseButton = ButtonHelper.createButton(Captions.linkImmunizationToCaseButton, e -> {
				if (this.isModified()) {
					actionCallback.accept(() -> {
						ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getByUuid(getValue().getUuid());
						buildAndOpenSearchSpecificCaseWindow(immunizationDto);
					});
				} else {
					buildAndOpenSearchSpecificCaseWindow(this.getValue());
				}

			}, ValoTheme.BUTTON_PRIMARY, FORCE_CAPTION);
		}
		getContent().addComponent(linkImmunizationToCaseButton, LINK_IMMUNIZATION_TO_CASE_BTN_LOC);
		linkImmunizationToCaseButton.setVisible(shouldShowRecoveryFields(meansOfImmunizationValue));

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (!isEditableAllowed(ImmunizationDto.HEALTH_FACILITY)) {
			setEnabled(false, ImmunizationDto.FACILITY_TYPE, ImmunizationDto.HEALTH_FACILITY_DETAILS);
			FieldHelper.setComboInaccessible(facilityTypeGroup);
		}

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
			ignoreMeansOfImmunizationChange = selectedValue;
		});

		meansOfImmunizationField.addValueChangeListener(valueChangeEvent -> {
			MeansOfImmunization meansOfImmunization = (MeansOfImmunization) valueChangeEvent.getProperty().getValue();

			boolean isVaccinationVisible = shouldShowVaccinationFields(meansOfImmunization);
			boolean isRecoveryVisible = shouldShowRecoveryFields(meansOfImmunization);

			if (!ignoreMeansOfImmunizationChange) {
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
										if (!isVaccinationVisible) {
											numberOfDosesField.setValue(null);
										}
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
			}

			vaccinationHeadingLabel.setVisible(isVaccinationVisible);
			numberOfDosesField.setVisible(isVaccinationVisible);
			numberOfDosesDetailsField.setVisible(isVaccinationVisible && getValue().getNumberOfDosesDetails() != null);

			recoveryHeadingLabel.setVisible(isRecoveryVisible);
			positiveTestResultDate.setVisible(isRecoveryVisible);
			recoveryDate.setVisible(isRecoveryVisible);

			if (!isVaccinationVisible) {
				numberOfDosesField.clear();
			}

			if (!isRecoveryVisible) {
				positiveTestResultDate.clear();
				recoveryDate.clear();
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

		responsibleDistrict.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto != null && facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
			}
		});

		responsibleCommunity.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facilityCombo,
					communityDto != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityType.getValue(), true, false)
						: responsibleDistrict.getValue() != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByDistrictAndType(
									(DistrictReferenceDto) responsibleDistrict.getValue(),
									(FacilityType) facilityType.getValue(),
									true,
									false)
							: null);
			}
		});

		facilityTypeGroup.addValueChangeListener(e -> {
			if (facilityTypeGroup.getValue() == null) {
				facilityType.clear();
			}
			FieldHelper.updateEnumData(
				facilityType,
				facilityTypeGroup.getValue() != null
					? FacilityType.getTypes((FacilityTypeGroup) facilityTypeGroup.getValue())
					: Arrays.stream(FacilityType.values()).collect(Collectors.toList()));
		});
		facilityType.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (facilityType.getValue() != null && responsibleDistrict.getValue() != null) {
				if (responsibleCommunity.getValue() != null) {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) responsibleCommunity.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facilityCombo,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) responsibleDistrict.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				}
			}
		});

		facilityCombo.addValueChangeListener(e -> {
			updateFacilityFields(facilityCombo, facilityDetails);
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

	private void buildAndOpenSearchSpecificCaseWindow(ImmunizationDto immunizationDto) {

		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getCaption(Captions.caseSearchSpecificCase));
		window.setWidth(768, Unit.PIXELS);

		SearchSpecificLayout layout = buildSearchSpecificLayout(window, immunizationDto);
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private SearchSpecificLayout buildSearchSpecificLayout(Window window, ImmunizationDto immunizationDto) {

		String description = I18nProperties.getString(Strings.infoSpecificCaseSearch);
		String confirmCaption = I18nProperties.getCaption(Captions.caseSearchCase);

		com.vaadin.ui.TextField searchField = new com.vaadin.ui.TextField();
		Runnable confirmCallback = () -> {

			boolean foundCase =
				FacadeProvider.getImmunizationFacade().linkRecoveryImmunizationToSearchedCase(searchField.getValue(), immunizationDto);

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

	private void hideAndFillJurisdictionFields() {

		getContent().getComponent(RESPONSIBLE_JURISDICTION_HEADING_LOC).setVisible(false);
		responsibleRegion.setVisible(false);
		responsibleRegion.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		responsibleDistrict.setVisible(false);
		responsibleDistrict.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		responsibleCommunity.setVisible(false);
		responsibleCommunity.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	@Override
	public void setValue(ImmunizationDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		ignoreMeansOfImmunizationChange = true;
		super.setValue(newFieldValue);
		ignoreMeansOfImmunizationChange = false;
		previousMeansOfImmunization = newFieldValue.getMeansOfImmunization();

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		discard();
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
		FacilityType facilityTypeValue = getValue().getFacilityType();
		if (facilityTypeValue == null) {
			facilityTypeGroup.clear();
		} else {
			facilityTypeGroup.setValue(facilityTypeValue.getFacilityTypeGroup());
		}
		overwriteImmunizationManagementStatus.setValue(false);
	}
}
