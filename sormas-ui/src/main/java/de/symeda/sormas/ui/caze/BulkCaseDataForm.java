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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseBulkEditData;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class BulkCaseDataForm extends AbstractEditForm<CaseBulkEditData> {

	private static final long serialVersionUID = 1L;

	private static final String DISEASE_CHECKBOX = "diseaseCheckbox";
	private static final String CLASSIFICATION_CHECKBOX = "classificationCheckbox";
	private static final String INVESTIGATION_STATUS_CHECKBOX = "investigationStatusCheckbox";
	private static final String OUTCOME_CHECKBOX = "outcomeCheckbox";
	private static final String SURVEILLANCE_OFFICER_CHECKBOX = "surveillanceOfficerCheckbox";
	private static final String HEALTH_FACILITY_CHECKBOX = "healthFacilityCheckbox";
	private static final String TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String WARNING_LAYOUT = "warningLayout";

	//@formatter:off
    private static final String HTML_LAYOUT =
            fluidRowLocsCss(VSPACE_4, DISEASE_CHECKBOX) +
                    fluidRow(
                            fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
                            fluidColumn(6, 0, locs(
                                    CaseDataDto.DISEASE_DETAILS,
                                    CaseDataDto.PLAGUE_TYPE,
                                    CaseDataDto.DENGUE_FEVER_TYPE,
                                    CaseDataDto.RABIES_TYPE))) +
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
                    fluidRowLocs(CaseDataDto.HEALTH_FACILITY, CaseBulkEditData.HEALTH_FACILITY_DETAILS);
    //@formatter:on

	private final DistrictReferenceDto singleSelectedDistrict;

	private boolean initialized = false;

	private CheckBox diseaseCheckBox;
	private CheckBox classificationCheckBox;
	private CheckBox investigationStatusCheckBox;
	private CheckBox outcomeCheckBox;
	private CheckBox surveillanceOfficerCheckBox;
	private CheckBox healthFacilityCheckbox;
	private ComboBox facilityTypeGroup;
	private ComboBox facilityType;
	private TextField healthFacilityDetails;
	private Collection<? extends CaseIndexDto> selectedCases;
	private OptionGroup facilityOrHome;
	private HorizontalLayout warningLayout;

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

		diseaseCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkDisease));
		getContent().addComponent(diseaseCheckBox, DISEASE_CHECKBOX);
		ComboBox disease = addDiseaseField(CaseDataDto.DISEASE, false);
		disease.setEnabled(false);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		addField(CaseDataDto.PLAGUE_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.RABIES_TYPE, NullableOptionGroup.class);

		if (isVisibleAllowed(CaseDataDto.DISEASE_DETAILS)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
			FieldHelper
				.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		}
		if (isVisibleAllowed(CaseDataDto.PLAGUE_TYPE)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);
		}
		if (isVisibleAllowed(CaseDataDto.DENGUE_FEVER_TYPE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE),
				CaseDataDto.DISEASE,
				Arrays.asList(Disease.DENGUE),
				true);
		}
		if (isVisibleAllowed(CaseDataDto.RABIES_TYPE)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.RABIES_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.RABIES), true);
		}

		classificationCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseClassification));
		getContent().addComponent(classificationCheckBox, CLASSIFICATION_CHECKBOX);
		investigationStatusCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkInvestigationStatus));
		getContent().addComponent(investigationStatusCheckBox, INVESTIGATION_STATUS_CHECKBOX);
		outcomeCheckBox = new CheckBox(I18nProperties.getCaption(Captions.bulkCaseOutcome));
		getContent().addComponent(outcomeCheckBox, OUTCOME_CHECKBOX);
		NullableOptionGroup caseClassification = addField(CaseBulkEditData.CASE_CLASSIFICATION, NullableOptionGroup.class);
		caseClassification.setEnabled(false);
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
			List<UserReferenceDto> assignableSurveillanceOfficers =
				FacadeProvider.getUserFacade().getUserRefsByDistrict(singleSelectedDistrict, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(surveillanceOfficer, assignableSurveillanceOfficers);

			surveillanceOfficerCheckBox.addValueChangeListener(e -> {
				surveillanceOfficer.setEnabled((boolean) e.getProperty().getValue());
			});
		}

		healthFacilityCheckbox = new CheckBox(I18nProperties.getCaption(Captions.bulkFacility));
		getContent().addComponent(healthFacilityCheckbox, HEALTH_FACILITY_CHECKBOX);

		ComboBox region = addInfrastructureField(CaseBulkEditData.REGION);
		region.setEnabled(false);
		ComboBox district = addInfrastructureField(CaseBulkEditData.DISTRICT);
		district.setEnabled(false);
		ComboBox community = addInfrastructureField(CaseBulkEditData.COMMUNITY);
		community.setNullSelectionAllowed(true);
		community.setEnabled(false);
		facilityOrHome = new OptionGroup(I18nProperties.getCaption(Captions.casePlaceOfStay), TypeOfPlace.getTypesOfPlaceForCases());
		addCustomField(facilityOrHome, FACILITY_OR_HOME_LOC, I18nProperties.getCaption(Captions.casePlaceOfStay));
		facilityOrHome.setId("facilityOrHome");
		facilityOrHome.setEnabled(false);
		CssStyles.style(facilityOrHome, ValoTheme.OPTIONGROUP_HORIZONTAL);

		healthFacilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		healthFacilityDetails.setVisible(false);

		facilityTypeGroup = new ComboBox();
		facilityTypeGroup.setId("typeGroup");
		addCustomField(facilityTypeGroup, TYPE_GROUP_LOC, I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		facilityTypeGroup.setEnabled(false);
		facilityType = addField(CaseDataDto.FACILITY_TYPE, ComboBox.class);
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
			FieldHelper.removeItems(facility);
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			if (districtDto != null && facilityType.getValue() != null) {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) facilityType.getValue(), true, false));
			}
		});
		community.addValueChangeListener(e -> {
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

		FieldHelper.setRequiredWhen(getFieldGroup(), diseaseCheckBox, Arrays.asList(CaseBulkEditData.DISEASE), Arrays.asList(true));
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
			disease.setEnabled((boolean) e.getProperty().getValue());
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
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public CheckBox getDiseaseCheckBox() {
		return diseaseCheckBox;
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
				tfFacilityDetails.setRequired(visibleAndRequired);
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
}
