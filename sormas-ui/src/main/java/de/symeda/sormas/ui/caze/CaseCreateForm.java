/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Sets;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
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
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.person.PersonCreateForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String FACILITY_TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String DONT_SHARE_WARNING_LOC = "dontShareWithReportingToolWarnLoc";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_PLACE_OF_STAY_JURISDICTION = "differentPlaceOfStayJurisdiction";
	private static final String PLACE_OF_STAY_HEADING_LOC = "placeOfStayHeadingLoc";
	private static final String DIFFERENT_POINT_OF_ENTRY_JURISDICTION = "differentPointOfEntryJurisdiction";
	private static final String POINT_OF_ENTRY_REGION = "pointOfEntryRegion";
	private static final String POINT_OF_ENTRY_DISTRICT = "pointOfEntryDistrict";

	private ComboBox diseaseVariantField;
	private TextField diseaseVariantDetailsField;
	private NullableOptionGroup facilityOrHome;
	private ComboBox facilityTypeGroup;
	private ComboBox facilityType;
	private ComboBox responsibleRegionCombo;
	private ComboBox responsibleDistrictCombo;
	private ComboBox responsibleCommunityCombo;
	private CheckBox differentPlaceOfStayJurisdiction;
	private CheckBox differentPointOfEntryJurisdiction;
	private ComboBox regionCombo;
	private ComboBox districtCombo;
	private ComboBox communityCombo;
	private ComboBox facilityCombo;
	private ComboBox pointOfEntryDistrictCombo;
	private NullableOptionGroup ogCaseOrigin;

	private PersonCreateForm personCreateForm;

	private final boolean showHomeAddressForm;
	private final boolean showPersonSearchButton;

	// If a case is created form a TravelEntry, the variable convertedTravelEntry provides the
	// necessary extra data. This variable is expected to be replaced in the implementation of
	// issue #5910.
	private final TravelEntryDto convertedTravelEntry;

	//@formatter:off
    private static final String HTML_LAYOUT = fluidRowLocs(CaseDataDto.CASE_ORIGIN, "")
        + fluidRowLocs(CaseDataDto.REPORT_DATE, CaseDataDto.EPID_NUMBER, CaseDataDto.EXTERNAL_ID)
        + fluidRow(
        fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
        fluidColumn(6, 0,
            locs(CaseDataDto.DISEASE_DETAILS, CaseDataDto.PLAGUE_TYPE, CaseDataDto.DENGUE_FEVER_TYPE,
                CaseDataDto.RABIES_TYPE)))
        + fluidRowLocs(CaseDataDto.DISEASE_VARIANT, CaseDataDto.DISEASE_VARIANT_DETAILS)
		+ fluidRowLocs(CaseDataDto.RE_INFECTION)
        + fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC)
        + fluidRowLocs(CaseDataDto.RESPONSIBLE_REGION, CaseDataDto.RESPONSIBLE_DISTRICT, CaseDataDto.RESPONSIBLE_COMMUNITY)
        + fluidRowLocs(CaseDataDto.DONT_SHARE_WITH_REPORTING_TOOL)
        + fluidRowLocs(DONT_SHARE_WARNING_LOC)
        + fluidRowLocs(DIFFERENT_PLACE_OF_STAY_JURISDICTION)
        + fluidRowLocs(PLACE_OF_STAY_HEADING_LOC)
        + fluidRowLocs(FACILITY_OR_HOME_LOC)
        + fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY)
        + fluidRowLocs(FACILITY_TYPE_GROUP_LOC, CaseDataDto.FACILITY_TYPE)
        + fluidRowLocs(CaseDataDto.HEALTH_FACILITY, CaseDataDto.HEALTH_FACILITY_DETAILS)
        + fluidRowLocs(DIFFERENT_POINT_OF_ENTRY_JURISDICTION)
        + fluidRowLocs(POINT_OF_ENTRY_REGION, POINT_OF_ENTRY_DISTRICT)
        + fluidRowLocs(CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS)
		+ fluidRowLocs(CaseDataDto.PERSON);
    //@formatter:on

	public CaseCreateForm() {
		this(true, true, null);
	}

	public CaseCreateForm(TravelEntryDto convertedTravelEntry) {
		this(false, true, convertedTravelEntry);
	}

	public CaseCreateForm(Boolean showHomeAddressForm, Boolean showPersonSearchButton, TravelEntryDto convertedTravelEntry) {
		super(
			CaseDataDto.class,
			CaseDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.getNoop());
		this.convertedTravelEntry = convertedTravelEntry;
		this.showHomeAddressForm = showHomeAddressForm;
		this.showPersonSearchButton = showPersonSearchButton;
		addFields();
		setWidth(720, Unit.PIXELS);
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {

		ogCaseOrigin = addField(CaseDataDto.CASE_ORIGIN, NullableOptionGroup.class);
		ogCaseOrigin.setRequired(true);

		TextField epidField = addField(CaseDataDto.EPID_NUMBER, TextField.class);
		epidField.setInvalidCommitted(true);
		style(epidField, ERROR_COLOR_PRIMARY);

		if (!FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			TextField externalIdField = addField(CaseDataDto.EXTERNAL_ID, TextField.class);
			style(externalIdField, ERROR_COLOR_PRIMARY);
		} else {
			CheckBox dontShareCheckbox = addField(CaseDataDto.DONT_SHARE_WITH_REPORTING_TOOL, CheckBox.class);
			CaseFormHelper.addDontShareWithReportingTool(getContent(), () -> dontShareCheckbox, DONT_SHARE_WARNING_LOC);
		}

		addField(CaseDataDto.REPORT_DATE, DateField.class);
		ComboBox diseaseField = addDiseaseField(CaseDataDto.DISEASE, false, true);
		diseaseVariantField = addField(CaseDataDto.DISEASE_VARIANT, ComboBox.class);
		diseaseVariantDetailsField = addField(CaseDataDto.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);
		diseaseVariantField.setNullSelectionAllowed(true);
		diseaseVariantField.setVisible(false);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		NullableOptionGroup plagueType = addField(CaseDataDto.PLAGUE_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.RABIES_TYPE, NullableOptionGroup.class);

		addField(CaseDataDto.RE_INFECTION, NullableOptionGroup.class);

		personCreateForm = new PersonCreateForm(showHomeAddressForm, true, true, showPersonSearchButton);
		personCreateForm.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(personCreateForm, CaseDataDto.PERSON);

		// Jurisdiction fields
		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingCaseResponsibleJurisidction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		responsibleRegionCombo = addInfrastructureField(CaseDataDto.RESPONSIBLE_REGION);
		responsibleRegionCombo.setRequired(true);
		responsibleDistrictCombo = addInfrastructureField(CaseDataDto.RESPONSIBLE_DISTRICT);
		responsibleDistrictCombo.setRequired(true);
		responsibleCommunityCombo = addInfrastructureField(CaseDataDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunityCombo.setNullSelectionAllowed(true);
		responsibleCommunityCombo.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegionCombo, responsibleDistrictCombo, responsibleCommunityCombo);

		differentPlaceOfStayJurisdiction = addCustomField(DIFFERENT_PLACE_OF_STAY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPlaceOfStayJurisdiction.addStyleName(VSPACE_3);

		Label placeOfStayHeadingLabel = new Label(I18nProperties.getCaption(Captions.casePlaceOfStay));
		placeOfStayHeadingLabel.addStyleName(H3);
		getContent().addComponent(placeOfStayHeadingLabel, PLACE_OF_STAY_HEADING_LOC);

		regionCombo = addInfrastructureField(CaseDataDto.REGION);
		districtCombo = addInfrastructureField(CaseDataDto.DISTRICT);
		communityCombo = addInfrastructureField(CaseDataDto.COMMUNITY);
		communityCombo.setNullSelectionAllowed(true);

		differentPointOfEntryJurisdiction = addCustomField(DIFFERENT_POINT_OF_ENTRY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPointOfEntryJurisdiction.addStyleName(VSPACE_3);

		ComboBox pointOfEntryRegionCombo = addCustomField(POINT_OF_ENTRY_REGION, RegionReferenceDto.class, ComboBox.class);
		pointOfEntryDistrictCombo = addCustomField(POINT_OF_ENTRY_DISTRICT, DistrictReferenceDto.class, ComboBox.class);
		InfrastructureFieldsHelper.initInfrastructureFields(pointOfEntryRegionCombo, pointOfEntryDistrictCombo, null);

		pointOfEntryDistrictCombo.addValueChangeListener(e -> updatePOEs());

		FieldHelper.setVisibleWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo, communityCombo),
			Collections.singletonList(Boolean.TRUE),
			true);

		FieldHelper.setVisibleWhen(
			differentPointOfEntryJurisdiction,
			Arrays.asList(pointOfEntryRegionCombo, pointOfEntryDistrictCombo),
			Collections.singletonList(Boolean.TRUE),
			true);

		FieldHelper.setRequiredWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			false,
			null);

		ogCaseOrigin.addValueChangeListener(e -> {
			boolean pointOfEntryRegionDistrictVisible =
				CaseOrigin.POINT_OF_ENTRY.equals(ogCaseOrigin.getValue()) && Boolean.TRUE.equals(differentPointOfEntryJurisdiction.getValue());
			pointOfEntryRegionCombo.setVisible(pointOfEntryRegionDistrictVisible);
			pointOfEntryDistrictCombo.setVisible(pointOfEntryRegionDistrictVisible);
		});

		facilityOrHome =
			addCustomField(FACILITY_OR_HOME_LOC, TypeOfPlace.class, NullableOptionGroup.class, I18nProperties.getCaption(Captions.casePlaceOfStay));
		facilityOrHome.removeAllItems();
		for (TypeOfPlace place : TypeOfPlace.FOR_CASES) {
			facilityOrHome.addItem(place);
			facilityOrHome.setItemCaption(place, I18nProperties.getEnumCaption(place));
		}
		facilityOrHome.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		facilityOrHome.setId("facilityOrHome");
		facilityOrHome.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(facilityOrHome, ValoTheme.OPTIONGROUP_HORIZONTAL);
		facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		getContent().addComponent(facilityTypeGroup, FACILITY_TYPE_GROUP_LOC);
		facilityType = addField(CaseDataDto.FACILITY_TYPE, ComboBox.class);
		facilityType.setWidth(100, Unit.PERCENTAGE);
		facilityCombo = addInfrastructureField(CaseDataDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);
		ComboBox cbPointOfEntry = addInfrastructureField(CaseDataDto.POINT_OF_ENTRY);
		cbPointOfEntry.setImmediate(true);
		TextField tfPointOfEntryDetails = addField(CaseDataDto.POINT_OF_ENTRY_DETAILS, TextField.class);
		tfPointOfEntryDetails.setVisible(false);

		if (convertedTravelEntry != null) {
			differentPointOfEntryJurisdiction.setValue(true);
			RegionReferenceDto regionReferenceDto = convertedTravelEntry.getPointOfEntryRegion() != null
				? convertedTravelEntry.getPointOfEntryRegion()
				: convertedTravelEntry.getResponsibleRegion();
			pointOfEntryRegionCombo.setValue(regionReferenceDto);
			DistrictReferenceDto districtReferenceDto = convertedTravelEntry.getPointOfEntryDistrict() != null
				? convertedTravelEntry.getPointOfEntryDistrict()
				: convertedTravelEntry.getResponsibleDistrict();
			pointOfEntryDistrictCombo.setValue(districtReferenceDto);

			differentPointOfEntryJurisdiction.setReadOnly(true);
			pointOfEntryRegionCombo.setReadOnly(true);
			pointOfEntryDistrictCombo.setReadOnly(true);
			updatePOEs();
			cbPointOfEntry.setReadOnly(true);
			tfPointOfEntryDetails.setReadOnly(true);
			ogCaseOrigin.setReadOnly(true);
		}

		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		districtCombo.addValueChangeListener(e -> {
			FieldHelper.removeItems(communityCombo);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				communityCombo,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);

			updateFacility();
			if (!Boolean.TRUE.equals(differentPointOfEntryJurisdiction.getValue())) {
				updatePOEs();
			}
		});
		communityCombo.addValueChangeListener(e -> {
			updateFacility();
		});
		facilityOrHome.addValueChangeListener(e -> {
			FacilityReferenceDto healthFacility = UserProvider.getCurrent().getUser().getHealthFacility();
			boolean hasOptionalHealthFacility = UserProvider.getCurrent().hasOptionalHealthFacility();
			if (hasOptionalHealthFacility && healthFacility != null) {
				String facilityId = healthFacility.getUuid();
				FacilityDto facilityDto = FacadeProvider.getFacilityFacade().getByUuid(facilityId);
				FacilityType facilityUserType = facilityDto.getType();
				FacilityTypeGroup facilityUserTypeGroup = facilityDto.getType().getFacilityTypeGroup();
				facilityTypeGroup.addItems(facilityUserTypeGroup);
				facilityTypeGroup.setValue(facilityUserTypeGroup);
				facilityType.addItems(facilityUserType);
				facilityType.setValue(facilityUserType);
				String facilityName = facilityDto.getName();
				facilityCombo.setValue(facilityName);
			} else {
				FieldHelper.removeItems(facilityCombo);
				if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())
					|| ((facilityOrHome.getValue() instanceof java.util.Set) && TypeOfPlace.FACILITY.equals(facilityOrHome.getNullableValue()))) {
					if (facilityTypeGroup.getValue() == null) {
						facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
					}
					if (facilityType.getValue() == null && FacilityTypeGroup.MEDICAL_FACILITY.equals(facilityTypeGroup.getValue())) {
						facilityType.setValue(FacilityType.HOSPITAL);
					}

					if (facilityType.getValue() != null) {
						updateFacility();
					}

					if (CaseOrigin.IN_COUNTRY.equals(ogCaseOrigin.getValue())) {
						facilityCombo.setRequired(true);
					}
					updateFacilityFields(facilityCombo, facilityDetails);
				} else if (TypeOfPlace.HOME.equals(facilityOrHome.getValue())
					|| ((facilityOrHome.getValue() instanceof java.util.Set) && TypeOfPlace.HOME.equals(facilityOrHome.getNullableValue()))) {
					setNoneFacility();
				} else {
					facilityCombo.removeAllItems();
					facilityCombo.setValue(null);
					updateFacilityFields(facilityCombo, facilityDetails);
				}
			}
		});
		facilityTypeGroup.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			FieldHelper.updateEnumData(facilityType, FacilityType.getAccommodationTypes((FacilityTypeGroup) facilityTypeGroup.getValue()));
		});
		facilityType.addValueChangeListener(e -> updateFacility());
		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		JurisdictionLevel userJurisdictionLevel = UserProvider.getCurrent().getJurisdictionLevel();
		if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			regionCombo.setReadOnly(true);
			responsibleRegionCombo.setReadOnly(true);
			districtCombo.setReadOnly(true);
			responsibleDistrictCombo.setReadOnly(true);
			communityCombo.setReadOnly(true);
			responsibleCommunityCombo.setReadOnly(true);
			differentPlaceOfStayJurisdiction.setVisible(false);
			differentPlaceOfStayJurisdiction.setEnabled(false);

			facilityOrHome.setImmediate(true);
			facilityOrHome.setValue(Sets.newHashSet(TypeOfPlace.FACILITY)); // [FACILITY]
			facilityOrHome.setReadOnly(true);
			facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
			facilityTypeGroup.setReadOnly(true);
			facilityType.setValue(FacilityType.HOSPITAL);
			facilityType.setReadOnly(true);
			facilityCombo.setValue(UserProvider.getCurrent().getUser().getHealthFacility());
			facilityCombo.setReadOnly(true);
		}

		if (!UserProvider.getCurrent().isPortHealthUser()) {
			ogCaseOrigin.addValueChangeListener(ev -> {
				if (ev.getProperty().getValue() == CaseOrigin.IN_COUNTRY) {
					setVisible(false, CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS);
					differentPointOfEntryJurisdiction.setVisible(false);
					setRequired(true, FACILITY_OR_HOME_LOC, FACILITY_TYPE_GROUP_LOC, CaseDataDto.FACILITY_TYPE, CaseDataDto.HEALTH_FACILITY);
					setRequired(false, CaseDataDto.POINT_OF_ENTRY);
					updateFacilityFields(facilityCombo, facilityDetails);
				} else {
					setVisible(true, CaseDataDto.POINT_OF_ENTRY);
					differentPointOfEntryJurisdiction.setVisible(true);
					setRequired(true, CaseDataDto.POINT_OF_ENTRY);
					if (userJurisdictionLevel != JurisdictionLevel.HEALTH_FACILITY) {
						facilityOrHome.clear();
						setRequired(false, FACILITY_OR_HOME_LOC, FACILITY_TYPE_GROUP_LOC, CaseDataDto.FACILITY_TYPE, CaseDataDto.HEALTH_FACILITY);
					}
					updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails);
				}
			});

			setRequired(true, FACILITY_OR_HOME_LOC);
		}

		// jurisdiction field valuechangelisteners
		responsibleDistrictCombo.addValueChangeListener(e -> {
			Boolean differentPlaceOfStay = differentPlaceOfStayJurisdiction.getValue();
			if (!Boolean.TRUE.equals(differentPlaceOfStay)) {
				updateFacility();
				if (!Boolean.TRUE.equals(differentPointOfEntryJurisdiction.getValue())) {
					updatePOEs();
				}
			}
		});
		responsibleCommunityCombo.addValueChangeListener((e) -> {
			Boolean differentPlaceOfStay = differentPlaceOfStayJurisdiction.getValue();
			if (differentPlaceOfStay == null || Boolean.FALSE.equals(differentPlaceOfStay)) {
				updateFacility();
			}
		});

		differentPlaceOfStayJurisdiction.addValueChangeListener(e -> {
			updateFacility();
			if (!Boolean.TRUE.equals(differentPointOfEntryJurisdiction.getValue())) {
				updatePOEs();
			}
		});

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();

		setRequired(true, CaseDataDto.REPORT_DATE, CaseDataDto.DISEASE, FACILITY_TYPE_GROUP_LOC, CaseDataDto.FACILITY_TYPE);
		FieldHelper.addSoftRequiredStyle(plagueType, communityCombo, facilityDetails);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			CaseDataDto.CASE_ORIGIN,
			Arrays.asList(CaseDataDto.HEALTH_FACILITY),
			Arrays.asList(CaseOrigin.IN_COUNTRY));
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			CaseDataDto.CASE_ORIGIN,
			Arrays.asList(CaseDataDto.POINT_OF_ENTRY),
			Arrays.asList(CaseOrigin.POINT_OF_ENTRY));
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.DENGUE), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.RABIES_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.RABIES), true);
		FieldHelper.setVisibleWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityType, facilityCombo),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false);
		FieldHelper.setRequiredWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityType, facilityCombo),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false,
			null);

		facilityCombo.addValueChangeListener(e -> {
			updateFacilityFields(facilityCombo, facilityDetails);
		});

		cbPointOfEntry.addValueChangeListener(e -> {
			updatePointOfEntryFields(cbPointOfEntry, tfPointOfEntryDetails);
		});

		addValueChangeListener(e -> {
			if (UserProvider.getCurrent().isPortHealthUser()) {
				setVisible(false, CaseDataDto.CASE_ORIGIN, CaseDataDto.DISEASE, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);
				setVisible(true, CaseDataDto.POINT_OF_ENTRY);
			}
		});
		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			updateDiseaseVariant((Disease) valueChangeEvent.getProperty().getValue());
			personCreateForm.updatePresentConditionEnum((Disease) valueChangeEvent.getProperty().getValue());
		});

		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		if (diseaseField.getValue() != null) {
			Disease disease = (Disease) diseaseField.getValue();
			updateDiseaseVariant(disease);
			personCreateForm.updatePresentConditionEnum(disease);
		}
	}

	private void hideAndFillJurisdictionFields() {

		ogCaseOrigin.setVisible(false);
		getContent().getComponent(RESPONSIBLE_JURISDICTION_HEADING_LOC).setVisible(false);
		getContent().getComponent(PLACE_OF_STAY_HEADING_LOC).setVisible(false);
		differentPlaceOfStayJurisdiction.setVisible(false);
		responsibleRegionCombo.setVisible(false);
		responsibleRegionCombo.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		responsibleDistrictCombo.setVisible(false);
		responsibleDistrictCombo.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		responsibleCommunityCombo.setVisible(false);
		responsibleCommunityCombo.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	private void updateDiseaseVariant(Disease disease) {
		List<DiseaseVariant> diseaseVariants =
			FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
		FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
		diseaseVariantField
			.setVisible(disease != null && isVisibleAllowed(CaseDataDto.DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
	}

	private void setNoneFacility() {
		FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
		facilityCombo.addItem(noFacilityRef);
		facilityCombo.setValue(noFacilityRef);
	}

	private void updateFacility() {

		if (UserProvider.getCurrent().getJurisdictionLevel() == JurisdictionLevel.HEALTH_FACILITY) {
			return;
		}

		Object facilityOrHomeValue = facilityOrHome.isRequired() ? facilityOrHome.getValue() : facilityOrHome.getNullableValue();
		if (TypeOfPlace.HOME.equals(facilityOrHomeValue)) {
			setNoneFacility();
			return;
		}

		FieldHelper.removeItems(facilityCombo);

		final DistrictReferenceDto district;
		final CommunityReferenceDto community;

		if (Boolean.TRUE.equals(differentPlaceOfStayJurisdiction.getValue())) {
			district = (DistrictReferenceDto) districtCombo.getValue();
			community = (CommunityReferenceDto) communityCombo.getValue();
		} else {
			district = (DistrictReferenceDto) responsibleDistrictCombo.getValue();
			community = (CommunityReferenceDto) responsibleCommunityCombo.getValue();
		}

		if (facilityType.getValue() != null && district != null) {
			if (community != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByCommunityAndType(community, (FacilityType) facilityType.getValue(), true, false));
			} else {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(district, (FacilityType) facilityType.getValue(), true, false));
			}
		}
	}

	private void updatePOEs() {

		ComboBox comboBoxPOE = getField(CaseDataDto.POINT_OF_ENTRY);
		if (!comboBoxPOE.isReadOnly()) {
			DistrictReferenceDto districtDto;

			if (Boolean.TRUE.equals(differentPointOfEntryJurisdiction.getValue())) {
				districtDto = (DistrictReferenceDto) pointOfEntryDistrictCombo.getValue();
			} else if (Boolean.TRUE.equals(differentPlaceOfStayJurisdiction.getValue())) {
				districtDto = (DistrictReferenceDto) districtCombo.getValue();
			} else {
				districtDto = (DistrictReferenceDto) responsibleDistrictCombo.getValue();
			}

			List<PointOfEntryReferenceDto> POEs = districtDto == null
				? Collections.emptyList()
				: FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(districtDto.getUuid(), true);
			FieldHelper.updateItems(comboBoxPOE, POEs);
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
		} else if (((facilityOrHome.getValue() instanceof java.util.Set)
			&& (facilityOrHome.getNullableValue() == null || TypeOfPlace.FACILITY.equals(facilityOrHome.getNullableValue())))
			|| TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
			tfFacilityDetails.setVisible(false);
			tfFacilityDetails.setRequired(false);
			tfFacilityDetails.clear();
		}
	}

	private void updatePointOfEntryFields(ComboBox cbPointOfEntry, TextField tfPointOfEntryDetails) {

		if (cbPointOfEntry.getValue() != null) {
			boolean isOtherPointOfEntry = ((PointOfEntryReferenceDto) cbPointOfEntry.getValue()).isOtherPointOfEntry();
			setVisible(isOtherPointOfEntry, CaseDataDto.POINT_OF_ENTRY_DETAILS);
			setRequired(isOtherPointOfEntry, CaseDataDto.POINT_OF_ENTRY_DETAILS);
			if (!isOtherPointOfEntry) {
				tfPointOfEntryDetails.clear();
			}
		} else {
			tfPointOfEntryDetails.setVisible(false);
			tfPointOfEntryDetails.setRequired(false);
			tfPointOfEntryDetails.clear();
		}
	}

	public Date getOnsetDate() {
		return personCreateForm.getOnsetDate();
	}

	public void setSymptoms(SymptomsDto symptoms) {
		personCreateForm.setSymptoms(symptoms);
	}

	public void setPersonalDetailsReadOnlyIfNotEmpty(boolean readOnly) {
		personCreateForm.setPersonalDetailsReadOnlyIfNotEmpty(readOnly);
	}

	public void setDiseaseReadOnly(boolean readOnly) {
		getField(CaseDataDto.DISEASE).setEnabled(!readOnly);
	}

	public PersonCreateForm getPersonCreateForm() {
		return personCreateForm;
	}

	public LocationEditForm getHomeAddressForm() {
		return personCreateForm.getHomeAddressForm();
	}

	public PersonDto getSearchedPerson() {
		return personCreateForm.getSearchedPerson();
	}

	public void setPerson(PersonDto person) {
		personCreateForm.setPerson(person);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	public void setValue(CaseDataDto caseDataDto) throws com.vaadin.v7.data.Property.ReadOnlyException, Converter.ConversionException {
		super.setValue(caseDataDto);

		FacilityReferenceDto healthFacility = caseDataDto.getHealthFacility();
		if (healthFacility != null) {
			if (FacilityDto.NONE_FACILITY_UUID.equals(healthFacility.getUuid())) {
				facilityOrHome.setValue(TypeOfPlace.HOME);
			} else {
				facilityOrHome.setValue(TypeOfPlace.FACILITY);
				facilityTypeGroup.setValue(caseDataDto.getFacilityType().getFacilityTypeGroup());
				facilityType.setValue(caseDataDto.getFacilityType());
				facilityCombo.setValue(healthFacility);
			}
		}

		if (convertedTravelEntry != null) {
			diseaseVariantDetailsField.setValue(convertedTravelEntry.getDiseaseVariantDetails());
		}

		PersonReferenceDto casePersonReference = caseDataDto.getPerson();
		String personUuid = casePersonReference == null ? null : casePersonReference.getUuid();
		PersonDto personByUuid = personUuid == null ? null : FacadeProvider.getPersonFacade().getByUuid(personUuid);
		personCreateForm.setPerson(personByUuid);

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}
	}

	public void setSearchedPerson(PersonDto searchedPerson) {
		personCreateForm.setSearchedPerson(searchedPerson);
	}
}
