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

import static de.symeda.sormas.ui.utils.LayoutUtil.divs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.List;

import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

public class CaseFacilityChangeForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String TYPE_LOC = "typeLoc";

	//@formatter:off
	private static final String HTML_LAYOUT = 
			//XXX #1620 are the divs needed?
			divs(
					fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY) +
					fluidRowLocs(FACILITY_OR_HOME_LOC, TYPE_GROUP_LOC, TYPE_LOC) +
					fluidRowLocs(CaseDataDto.HEALTH_FACILITY, CaseDataDto.HEALTH_FACILITY_DETAILS) + 
					fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, "")
			);
	//@formatter:on

	private NullableOptionGroup facilityOrHome;
	private ComboBox typeGroup;
	private ComboBox type;

	public CaseFacilityChangeForm() {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
	}

	@Override
	protected void addFields() {

		ComboBox region = addInfrastructureField(CaseDataDto.REGION);
		ComboBox district = addInfrastructureField(CaseDataDto.DISTRICT);
		ComboBox community = addInfrastructureField(CaseDataDto.COMMUNITY);
		community.setNullSelectionAllowed(true);
		facilityOrHome = new NullableOptionGroup(I18nProperties.getCaption(Captions.casePlaceOfStay), TypeOfPlace.getTypesOfPlaceForCases());
		facilityOrHome.setId("facilityOrHome");
		facilityOrHome.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(facilityOrHome, ValoTheme.OPTIONGROUP_HORIZONTAL);
		getContent().addComponent(facilityOrHome, FACILITY_OR_HOME_LOC);
		typeGroup = new ComboBox();
		typeGroup.setId("typeGroup");
		typeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.setWidth(100, Unit.PERCENTAGE);
		typeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		typeGroup.setVisible(false);
		getContent().addComponent(typeGroup, TYPE_GROUP_LOC);
		type = new ComboBox();
		type.setId("type");
		type.setCaption(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		type.setWidth(100, Unit.PERCENTAGE);
		type.setVisible(false);
		getContent().addComponent(type, TYPE_LOC);
		ComboBox facility = addInfrastructureField(CaseDataDto.HEALTH_FACILITY);
		facility.setVisible(false);
		ComboBox officer = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			FieldHelper.removeItems(community);
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			if (districtDto != null && type.getValue() != null) {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade()
						.getActiveFacilitiesByDistrictAndType(districtDto, (FacilityType) type.getValue(), true, false));
			}

			List<UserReferenceDto> assignableSurveillanceOfficers =
				FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(officer, assignableSurveillanceOfficers);
			if (assignableSurveillanceOfficers.size() == 1) {
				officer.setValue(assignableSurveillanceOfficers.get(0));
			} else {
				officer.setValue(null);
			}
		});
		community.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (type.getValue() != null) {
				FieldHelper.updateItems(
					facility,
					communityDto != null
						? FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(communityDto, (FacilityType) type.getValue(), true, false)
						: district.getValue() != null
							? FacadeProvider.getFacilityFacade()
								.getActiveFacilitiesByDistrictAndType(
									(DistrictReferenceDto) district.getValue(),
									(FacilityType) type.getValue(),
									true,
									false)
							: null);
			}
		});
		facilityOrHome.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			if (TypeOfPlace.HOME.equals(facilityOrHome.getValue())) {
				FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
				facility.addItem(noFacilityRef);
				facility.setValue(noFacilityRef);
			}
			if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
				typeGroup.setVisible(true);
				type.setVisible(true);
				facility.setVisible(true);
				facility.setRequired(true);
				if (type.getValue() != null)
					updateFacility((DistrictReferenceDto) district.getValue(), (CommunityReferenceDto) community.getValue(), facility);
			} else {
				typeGroup.setVisible(false);
				type.setVisible(false);
				facility.setVisible(false);
				facility.setRequired(false);
			}
			updateFacilityFields(facility, facilityDetails);
		});
		typeGroup.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			FieldHelper.updateEnumData(type, FacilityType.getAccommodationTypes((FacilityTypeGroup) typeGroup.getValue()));
		});
		type.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
			if (type.getValue() != null && district.getValue() != null) {
				if (community.getValue() != null) {
					FieldHelper.updateItems(
						facility,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByCommunityAndType(
								(CommunityReferenceDto) community.getValue(),
								(FacilityType) type.getValue(),
								true,
								false));
				} else {
					FieldHelper.updateItems(
						facility,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								(DistrictReferenceDto) district.getValue(),
								(FacilityType) type.getValue(),
								true,
								false));
				}
			}
		});
		facility.addValueChangeListener(e -> {
			updateFacilityFields(facility, facilityDetails);
			if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
				this.getValue().setFacilityType((FacilityType) type.getValue());
			}
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		FieldHelper.addSoftRequiredStyle(community, facilityDetails, officer);
		setRequired(true, CaseDataDto.REGION, CaseDataDto.DISTRICT, FACILITY_OR_HOME_LOC, TYPE_GROUP_LOC, TYPE_LOC);
		officer.setNullSelectionAllowed(true);
	}

	private void updateFacilityFields(ComboBox cbFacility, TextField tfFacilityDetails) {
		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visibleAndRequired);
			tfFacilityDetails.setRequired(visibleAndRequired);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
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

	private void updateFacility(DistrictReferenceDto district, CommunityReferenceDto community, ComboBox facility) {
		FieldHelper.removeItems(facility);
		if (type.getValue() != null && district != null) {
			if (community != null) {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, (FacilityType) type.getValue(), true, false));
			} else {
				FieldHelper.updateItems(
					facility,
					FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, (FacilityType) type.getValue(), true, false));
			}
		}
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
