package de.symeda.sormas.ui.caze;

import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseFacilityChangeForm extends AbstractEditForm<CaseDataDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.div(
					LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
					LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY) +
					LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.HEALTH_FACILITY_DETAILS)
			);

	public CaseFacilityChangeForm() {
		super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
	}
	
	@Override
	protected void addFields() {
		ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
		ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
		ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
		ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
		ComboBox officer = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		TextField facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		
		region.addValueChangeListener(e -> {
			district.removeAllItems();
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			if (regionDto != null) {
				district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
			}
		});
		district.addValueChangeListener(e -> {
			community.removeAllItems();
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			if (districtDto != null) {
				community.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()));
			}
			
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict(districtDto, false, UserRole.SURVEILLANCE_OFFICER);
			officer.removeAllItems();
			officer.addItems(assignableSurveillanceOfficers);
			officer.setValue(null);
		});
		community.addValueChangeListener(e -> {
			facility.removeAllItems();
			CommunityReferenceDto communityDto = (CommunityReferenceDto) e.getProperty().getValue();
			if (communityDto != null) {
				facility.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true));
			}
		});
		facility.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				boolean otherHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
				boolean noneHealthFacility = ((FacilityReferenceDto) e.getProperty().getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
				boolean visibleAndRequired = otherHealthFacility || noneHealthFacility;
				
				facilityDetails.setVisible(visibleAndRequired);
				facilityDetails.setRequired(visibleAndRequired);
				
				if (otherHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
				}
				if (noneHealthFacility) {
					facilityDetails.setCaption(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS));
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
		
		region.setRequired(true);
		district.setRequired(true);
		community.setRequired(true);
		facility.setRequired(true);
		officer.setNullSelectionAllowed(true);
	}
	
	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
	
}
