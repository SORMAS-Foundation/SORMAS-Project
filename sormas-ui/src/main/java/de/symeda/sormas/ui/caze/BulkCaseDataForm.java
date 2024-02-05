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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_4;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseBulkEditData;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class BulkCaseDataForm extends AbstractEditForm<CaseBulkEditData> {

	private static final long serialVersionUID = 1L;

	private static final String DISEASE_CHECKBOX = "diseaseCheckbox";

	private static final String DISEASE_VARIANT_CHECKBOX = "diseaseVariantCheckbox";
	private static final String CLASSIFICATION_CHECKBOX = "classificationCheckbox";
	private static final String INVESTIGATION_STATUS_CHECKBOX = "investigationStatusCheckbox";
	private static final String OUTCOME_CHECKBOX = "outcomeCheckbox";
	private static final String SURVEILLANCE_OFFICER_CHECKBOX = "surveillanceOfficerCheckbox";
	private static final String HEALTH_FACILITY_CHECKBOX = "healthFacilityCheckbox";
	private static final String TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String WARNING_LAYOUT = "warningLayout";
	private static final String SHARE_CHECKBOX = "shareCheckbox";
	private static final String DONT_SHARE_WARNING_LOC = "dontShareWarning";

	//@formatter:off
    private static final String HTML_LAYOUT =
            		fluidRowLocsCss(VSPACE_4, DISEASE_CHECKBOX) +
                    fluidRow(
                            fluidColumnLoc(6, 0, CaseBulkEditData.DISEASE),
                            fluidColumn(6, 0, locs(
									CaseBulkEditData.DISEASE_DETAILS,
									CaseBulkEditData.PLAGUE_TYPE,
									CaseBulkEditData.DENGUE_FEVER_TYPE,
									CaseBulkEditData.RABIES_TYPE))) +
                    fluidRowLocsCss(VSPACE_4, DISEASE_VARIANT_CHECKBOX) +
					fluidRowLocs(CaseBulkEditData.DISEASE_VARIANT, CaseBulkEditData.DISEASE_VARIANT_DETAILS) +
                    fluidRowLocsCss(VSPACE_4, CLASSIFICATION_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.CASE_CLASSIFICATION) +
                    fluidRowLocsCss(VSPACE_4, INVESTIGATION_STATUS_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.INVESTIGATION_STATUS) +
                    fluidRowLocsCss(VSPACE_4, OUTCOME_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.OUTCOME) +
                    fluidRowLocsCss(VSPACE_4, SURVEILLANCE_OFFICER_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.SURVEILLANCE_OFFICER, "") +
                    fluidRowLocsCss(VSPACE_4, HEALTH_FACILITY_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.REGION,
                            CaseBulkEditData.DISTRICT,
                            CaseBulkEditData.COMMUNITY) +
                    fluidRowLocs(FACILITY_OR_HOME_LOC, TYPE_GROUP_LOC, CaseBulkEditData.FACILITY_TYPE) +
                    fluidRowLocs(WARNING_LAYOUT) +
                    fluidRowLocs(CaseBulkEditData.HEALTH_FACILITY, CaseBulkEditData.HEALTH_FACILITY_DETAILS) +
                    fluidRowLocs(SHARE_CHECKBOX) +
                    fluidRowLocs(CaseBulkEditData.DONT_SHARE_WITH_REPORTING_TOOL) +
                    fluidRowLocs(DONT_SHARE_WARNING_LOC);
    //@formatter:on

	private final DistrictReferenceDto singleSelectedDistrict;

	private boolean initialized = false;

	private CheckBox diseaseCheckBox;

	private CheckBox diseaseVariantCheckBox;
	private CheckBox classificationCheckBox;
	private CheckBox investigationStatusCheckBox;
	private CheckBox outcomeCheckBox;
	private CheckBox surveillanceOfficerCheckBox;
	private CheckBox healthFacilityCheckbox;
	private CheckBox shareWithReportingToolCheckbox;
	private ComboBox facilityTypeGroup;
	private ComboBox facilityType;
	private TextField healthFacilityDetails;
	private Collection<? extends CaseIndexDto> selectedCases;
	private OptionGroup facilityOrHome;
	private HorizontalLayout warningLayout;

	private ComboBox region;
	private ComboBox district;
	private ComboBox community;

	public BulkCaseDataForm(DistrictReferenceDto singleSelectedDistrict, Collection<? extends CaseIndexDto> selectedCases) {
		super(CaseBulkEditData.class, CaseDataDto.I18N_PREFIX);
		this.singleSelectedDistrict = singleSelectedDistrict;
		setWidth(680, Unit.PIXELS);
		hideValidationUntilNextCommit();
		initialized = true;
		this.selectedCases = selectedCases;
		addFields();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {
		if (!initialized) {
			return;
		}

		// Disease
		diseaseCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkDisease));
		getContent().addComponent(diseaseCheckBox, DISEASE_CHECKBOX);
		ComboBox diseaseField = addDiseaseField(CaseBulkEditData.DISEASE, false);
		diseaseField.setEnabled(false);
		addField(CaseBulkEditData.DISEASE_DETAILS, TextField.class);
		addField(CaseBulkEditData.PLAGUE_TYPE, NullableOptionGroup.class);
		addField(CaseBulkEditData.DENGUE_FEVER_TYPE, NullableOptionGroup.class);
		addField(CaseBulkEditData.RABIES_TYPE, NullableOptionGroup.class);

		if (isVisibleAllowed(CaseBulkEditData.DISEASE_DETAILS)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseBulkEditData.DISEASE_DETAILS),
				CaseBulkEditData.DISEASE,
				Arrays.asList(Disease.OTHER),
				true);
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				CaseBulkEditData.DISEASE,
				Arrays.asList(CaseBulkEditData.DISEASE_DETAILS),
				Arrays.asList(Disease.OTHER));
		}
		if (isVisibleAllowed(CaseBulkEditData.PLAGUE_TYPE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseBulkEditData.PLAGUE_TYPE),
				CaseBulkEditData.DISEASE,
				Arrays.asList(Disease.PLAGUE),
				true);
		}
		if (isVisibleAllowed(CaseBulkEditData.DENGUE_FEVER_TYPE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseBulkEditData.DENGUE_FEVER_TYPE),
				CaseBulkEditData.DISEASE,
				Arrays.asList(Disease.DENGUE),
				true);
		}
		if (isVisibleAllowed(CaseBulkEditData.RABIES_TYPE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseBulkEditData.RABIES_TYPE),
				CaseBulkEditData.DISEASE,
				Arrays.asList(Disease.RABIES),
				true);
		}

		// Disease variant
		diseaseVariantCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkDiseaseVariant));
		diseaseVariantCheckBox.setVisible(false);
		getContent().addComponent(diseaseVariantCheckBox, DISEASE_VARIANT_CHECKBOX);

		ComboBoxWithPlaceholder diseaseVariantField = addField(CaseBulkEditData.DISEASE_VARIANT, ComboBoxWithPlaceholder.class);
		diseaseVariantField.setPlaceholder(I18nProperties.getCaption(Captions.caseNoDiseaseVariant));
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setEnabled(false);
		diseaseVariantField.setVisible(false);

		TextField diseaseVariantDetailsField = addField(CaseBulkEditData.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});
		diseaseField.addValueChangeListener(
			(ValueChangeListener) valueChangeEvent -> updateDiseaseVariantField(
				diseaseVariantField,
				(Disease) valueChangeEvent.getProperty().getValue()));

		if (diseaseField.getValue() != null) {
			Disease disease = (Disease) diseaseField.getValue();
			updateDiseaseVariantField(diseaseVariantField, disease);
		}

		// Classification
		classificationCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseClassification));
		getContent().addComponent(classificationCheckBox, CLASSIFICATION_CHECKBOX);
		investigationStatusCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkInvestigationStatus));
		getContent().addComponent(investigationStatusCheckBox, INVESTIGATION_STATUS_CHECKBOX);
		outcomeCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseOutcome));
		getContent().addComponent(outcomeCheckBox, OUTCOME_CHECKBOX);
		NullableOptionGroup caseClassification = addField(CaseBulkEditData.CASE_CLASSIFICATION, NullableOptionGroup.class);
		caseClassification.setEnabled(false);
		if (!isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			caseClassification.removeItem(CaseClassification.CONFIRMED_NO_SYMPTOMS);
			caseClassification.removeItem(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);
		}
		NullableOptionGroup investigationStatus = addField(CaseBulkEditData.INVESTIGATION_STATUS, NullableOptionGroup.class);
		investigationStatus.setEnabled(false);
		NullableOptionGroup outcome = addField(CaseBulkEditData.OUTCOME, NullableOptionGroup.class);
		outcome.setEnabled(false);

		if (singleSelectedDistrict != null) {
			surveillanceOfficerCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkSurveillanceOfficer));
			getContent().addComponent(surveillanceOfficerCheckBox, SURVEILLANCE_OFFICER_CHECKBOX);
			ComboBox surveillanceOfficer = addField(CaseBulkEditData.SURVEILLANCE_OFFICER, ComboBox.class);
			surveillanceOfficer.setEnabled(false);
			FieldHelper.addSoftRequiredStyleWhen(
				getFieldGroup(),
				surveillanceOfficerCheckBox,
				Arrays.asList(CaseBulkEditData.SURVEILLANCE_OFFICER),
				Arrays.asList(true),
				null);
			Set<Disease> selectedDiseases = this.selectedCases.stream().map(c -> c.getDisease()).collect(Collectors.toSet());
			List<UserReferenceDto> assignableCaseResponsibles = null;

			if (selectedDiseases.size() == 1) {
				Disease selectedDisease = selectedDiseases.iterator().next();
				assignableCaseResponsibles =
					FacadeProvider.getUserFacade().getUserRefsByDistrict(singleSelectedDistrict, selectedDisease, UserRight.CASE_RESPONSIBLE);

			} else {
				assignableCaseResponsibles =
					FacadeProvider.getUserFacade().getUserRefsByDistrict(singleSelectedDistrict, true, UserRight.CASE_RESPONSIBLE);
			}
			FieldHelper.updateItems(surveillanceOfficer, assignableCaseResponsibles);

			surveillanceOfficerCheckBox.addValueChangeListener(e -> {
				surveillanceOfficer.setEnabled((boolean) e.getProperty().getValue());
			});
		}

		healthFacilityCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkFacility));
		getContent().addComponent(healthFacilityCheckbox, HEALTH_FACILITY_CHECKBOX);

		region = addInfrastructureField(CaseBulkEditData.REGION);
		region.setEnabled(false);
		district = addInfrastructureField(CaseBulkEditData.DISTRICT);
		district.setEnabled(false);
		community = addInfrastructureField(CaseBulkEditData.COMMUNITY);
		community.setNullSelectionAllowed(true);
		community.setEnabled(false);
		facilityOrHome = new OptionGroup(I18nProperties.getCaption(Captions.casePlaceOfStay), TypeOfPlace.FOR_CASES);
		addCustomField(facilityOrHome, FACILITY_OR_HOME_LOC, I18nProperties.getCaption(Captions.casePlaceOfStay));
		facilityOrHome.setId("facilityOrHome");
		facilityOrHome.setEnabled(false);
		CssStyles.style(facilityOrHome, ValoTheme.OPTIONGROUP_HORIZONTAL);

		healthFacilityDetails = addField(CaseBulkEditData.HEALTH_FACILITY_DETAILS, TextField.class);
		healthFacilityDetails.setVisible(false);

		facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		addCustomField(facilityTypeGroup, TYPE_GROUP_LOC, I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		facilityTypeGroup.setEnabled(false);
		facilityType = addField(CaseBulkEditData.FACILITY_TYPE, ComboBox.class);
		facilityType.setEnabled(false);
		ComboBox facility = addInfrastructureField(CaseBulkEditData.HEALTH_FACILITY);
		facility.setImmediate(true);
		facility.setEnabled(false);
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		healthFacilityDetails.addValueChangeListener(e -> {
			updateFacilityFields(facility, healthFacilityDetails);
		});
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.removeItems(community);
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);

			if (!TypeOfPlace.HOME.equals(facilityOrHome.getValue())) {
				FieldHelper.removeItems(facility);
				if (districtDto != null && facilityType.getValue() != null) {
					FieldHelper.updateItems(
						facility,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
				}
			}
		});
		community.addValueChangeListener(e -> {
			if (!TypeOfPlace.HOME.equals(facilityOrHome.getValue())) {
				FieldHelper.removeItems(facility);

				CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
				if (facilityType.getValue() != null) {
					FieldHelper.updateItems(
						facility,
						communityDto != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) facilityType.getValue(), true, false)
							: district.getValue() != null
								? FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType(
										(DistrictReferenceDto) district.getValue(),
										(FacilityType) facilityType.getValue(),
										true,
										false)
								: null);
				}
			}
		});
		facilityTypeGroup.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			FieldHelper.updateEnumData(facilityType, FacilityType.getAccommodationTypes((FacilityTypeGroup) facilityTypeGroup.getValue()));
		});
		facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY); // default value

		facilityType.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			if (facilityType.getValue() != null && district.getValue() != null) {
				if (community.getValue() != null) {
					FieldHelper.updateItems(
						facility,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) community.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facility,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) district.getValue(),
								(FacilityType) facilityType.getValue(),
								true,
								false));
				}
			}
		});
		warningLayout = VaadinUiUtil.createWarningComponent(I18nProperties.getString(Strings.pseudonymizedCasesSelectedWarning));
		facilityOrHome.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
				if (facilityTypeGroup.getValue() == null) {
					facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
				}
				if (facilityType.getValue() == null && FacilityTypeGroup.MEDICAL_FACILITY.equals(facilityTypeGroup.getValue())) {
					facilityType.setValue(FacilityType.HOSPITAL);
				}

				if (facilityType.getValue() != null) {
					updateFacility((DistrictReferenceDto) district.getValue(), (CommunityReferenceDto) community.getValue(), facility);
				}
				this.getContent().removeComponent(warningLayout);
				healthFacilityDetails.setVisible(false);
			} else {
				long pseudonymizedCount = selectedCases.stream().filter(caze -> caze.isPseudonymized()).count();
				if (pseudonymizedCount > 0) {
					this.getContent().addComponent(warningLayout, WARNING_LAYOUT);

					healthFacilityDetails.setVisible(true);
				}
				FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
				facility.addItem(noFacilityRef);
				facility.setValue(noFacilityRef);
			}
		});

		facility.addValueChangeListener(e -> {
			updateFacilityFields(facility, healthFacilityDetails);
		});
		facilityType.setValue(FacilityType.HOSPITAL); // default

		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		if (FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			shareWithReportingToolCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseShareWithReportingTool));
			shareWithReportingToolCheckbox.addStyleName(VSPACE_3);
			getContent().addComponent(shareWithReportingToolCheckbox, SHARE_CHECKBOX);

			CheckBox dontShareCheckbox = addField(CaseBulkEditData.DONT_SHARE_WITH_REPORTING_TOOL, CheckBox.class);
			CaseFormHelper.addDontShareWithReportingTool(
				getContent(),
				() -> dontShareCheckbox,
				DONT_SHARE_WARNING_LOC,
				Strings.messageBulkDontShareWithReportingToolWarning);

			dontShareCheckbox.setEnabled(false);
			FieldHelper.setEnabledWhen(
				shareWithReportingToolCheckbox,
				Collections.singletonList(Boolean.TRUE),
				Collections.singletonList(dontShareCheckbox),
				true);

		}

		FieldHelper.setRequiredWhen(getFieldGroup(), diseaseCheckBox, Arrays.asList(CaseBulkEditData.DISEASE), Arrays.asList(true));
		FieldHelper.addSoftRequiredStyleWhen(
			getFieldGroup(),
			diseaseVariantCheckBox,
			Arrays.asList(CaseBulkEditData.DISEASE_VARIANT),
			Arrays.asList(true),
			null);
		FieldHelper
			.setRequiredWhen(getFieldGroup(), classificationCheckBox, Arrays.asList(CaseBulkEditData.CASE_CLASSIFICATION), Arrays.asList(true));
		FieldHelper
			.setRequiredWhen(getFieldGroup(), investigationStatusCheckBox, Arrays.asList(CaseBulkEditData.INVESTIGATION_STATUS), Arrays.asList(true));
		FieldHelper.setRequiredWhen(getFieldGroup(), outcomeCheckBox, Arrays.asList(CaseBulkEditData.OUTCOME), Arrays.asList(true));
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			healthFacilityCheckbox,
			Arrays.asList(CaseBulkEditData.REGION, CaseBulkEditData.DISTRICT, CaseBulkEditData.HEALTH_FACILITY),
			Arrays.asList(true));
		FieldHelper.setRequiredWhen(
			healthFacilityCheckbox,
			Arrays.asList(facilityTypeGroup, facilityType, facilityOrHome),
			Arrays.asList(true),
			false,
			null);
		FieldHelper.setVisibleWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityType, facility),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false);
		FieldHelper.setRequiredWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityType, facility),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false,
			null);
		diseaseCheckBox.addValueChangeListener(e -> {
			boolean checked = (boolean) e.getProperty().getValue();
			diseaseField.setEnabled(checked);
			if (!checked) {
				diseaseField.setValue(null);
			}

			updateDiseaseVariantField(diseaseVariantField, null);
		});
		diseaseVariantCheckBox.addValueChangeListener(e -> {
			boolean checked = (boolean) e.getProperty().getValue();
			diseaseVariantField.setEnabled(checked);
			if (!checked) {
				diseaseVariantField.setValue(null);
				diseaseVariantDetailsField.setValue(null);
			}
		});
		classificationCheckBox.addValueChangeListener(e -> {
			caseClassification.setEnabled((boolean) e.getProperty().getValue());
		});
		investigationStatusCheckBox.addValueChangeListener(e -> {
			investigationStatus.setEnabled((boolean) e.getProperty().getValue());
		});
		outcomeCheckBox.addValueChangeListener(e -> {
			outcome.setEnabled((boolean) e.getProperty().getValue());
		});
		healthFacilityCheckbox.addValueChangeListener(e -> {
			region.setEnabled((boolean) e.getProperty().getValue());
			district.setEnabled((boolean) e.getProperty().getValue());
			community.setEnabled((boolean) e.getProperty().getValue());
			facilityTypeGroup.setEnabled((boolean) e.getProperty().getValue());
			facilityOrHome.setEnabled((boolean) e.getProperty().getValue());
			facilityOrHome.setRequired((boolean) e.getProperty().getValue());
			healthFacilityDetails.setEnabled((boolean) e.getProperty().getValue());
			facilityType.setEnabled((boolean) e.getProperty().getValue());
			facility.setEnabled((boolean) e.getProperty().getValue());
			if ((boolean) e.getProperty().getValue()) {
				FieldHelper.addSoftRequiredStyle(community);
			} else {
				FieldHelper.removeSoftRequiredStyle(community);
			}
		});

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public CheckBox getDiseaseCheckBox() {
		return diseaseCheckBox;
	}

	public CheckBox getDiseaseVariantCheckBox() {
		return diseaseVariantCheckBox;
	}

	public CheckBox getClassificationCheckBox() {
		return classificationCheckBox;
	}

	public CheckBox getInvestigationStatusCheckBox() {
		return investigationStatusCheckBox;
	}

	public CheckBox getOutcomeCheckBox() {
		return outcomeCheckBox;
	}

	public CheckBox getSurveillanceOfficerCheckBox() {
		return surveillanceOfficerCheckBox;
	}

	public CheckBox getHealthFacilityCheckbox() {
		return healthFacilityCheckbox;
	}

	private void updateFacility(DistrictReferenceDto district, CommunityReferenceDto community, ComboBox facility) {
		FieldHelper.removeItems(facility);
		if (facilityType.getValue() != null && district != null) {
			if (community != null) {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByCommunityAndType(community, (FacilityType) facilityType.getValue(), true, false));
			} else {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(district, (FacilityType) facilityType.getValue(), true, false));
			}
		}
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {

		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
			}
			if (!visibleAndRequired) {
				tfFacilityDetails.clear();
			}
		} else if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}
	}

	private void updateDiseaseVariantField(ComboBox diseaseVariantField, Disease disease) {
		List<DiseaseVariant> diseaseVariants =
			FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
		FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
		boolean visible = disease != null && CollectionUtils.isNotEmpty(diseaseVariants);

		diseaseVariantField.setVisible(visible);

		diseaseVariantCheckBox.setVisible(visible);
		if (!visible) {
			diseaseVariantCheckBox.setValue(false);
		}
	}

	private void hideAndFillJurisdictionFields() {
		region.setVisible(false);
		region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		district.setVisible(false);
		district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		community.setVisible(false);
		community.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}
}
