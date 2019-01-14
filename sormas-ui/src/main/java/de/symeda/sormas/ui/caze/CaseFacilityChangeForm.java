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
package de.symeda.sormas.ui.caze;

import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseFacilityChangeForm extends AbstractEditForm<CaseDataDto> {

	public static final String NONE_HEALTH_FACILITY_DETAILS = "noneHealthFacilityDetails";
	
	private static final String HTML_LAYOUT = 
			LayoutUtil.divs(
					LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
					LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
					LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.HEALTH_FACILITY_DETAILS)
			);

	public CaseFacilityChangeForm(UserRight editOrCreateUserRight) {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX, editOrCreateUserRight);
	}
	
	@Override
	protected void addFields() {
		ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
		ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
		community.setNullSelectionAllowed(true);
		ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
		ComboBox officer = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
       	});
		district.addValueChangeListener(e -> {
			if (community.getValue() == null) {
    			FieldHelper.removeItems(facility);
    		}
    		FieldHelper.removeItems(community);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(community, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
    		FieldHelper.updateItems(facility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, true) : null);
			
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER);
			FieldHelper.updateItems(officer, assignableSurveillanceOfficers);
			if (assignableSurveillanceOfficers.size() == 1) {
				officer.setValue(assignableSurveillanceOfficers.get(0));
			} else {
				officer.setValue(null);
			}
		});
		community.addValueChangeListener(e -> {
			FieldHelper.removeItems(facility);
    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(facility, communityDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true) :
    			district.getValue() != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict((DistrictReferenceDto) district.getValue(), true) :
    				null);
    	});
		facility.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;
				
				facilityDetails.setVisible(visibleAndRequired);
				facilityDetails.setRequired(visibleAndRequired);
				
				if (otherHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, NONE_HEALTH_FACILITY_DETAILS));
				}
				if (!visibleAndRequired) {
					facilityDetails.clear();
				}
			} else {
				facilityDetails.setVisible(false);
				facilityDetails.setRequired(false);
				facilityDetails.clear();
			}
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		
		FieldHelper.addSoftRequiredStyle(community, facilityDetails, officer);
		setRequired(true, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.HEALTH_FACILITY);
		officer.setNullSelectionAllowed(true);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
