package de.symeda.sormas.ui.hospitalization;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class PreviousHospitalizationEditForm extends AbstractEditForm<PreviousHospitalizationDto> {

	private static final String HTML_LAYOUT = 
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.ADMISSION_DATE, PreviousHospitalizationDto.DISCHARGE_DATE)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.REGION, PreviousHospitalizationDto.DISTRICT)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.COMMUNITY, PreviousHospitalizationDto.HEALTH_FACILITY)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.ISOLATED)+
			LayoutUtil.fluidRowLocs(PreviousHospitalizationDto.DESCRIPTION)
			;

	public PreviousHospitalizationEditForm(boolean create, UserRight editOrCreateUserRight) {
		super(PreviousHospitalizationDto.class, PreviousHospitalizationDto.I18N_PREFIX, editOrCreateUserRight);

		setWidth(540, Unit.PIXELS);
		
		if (create) {
			hideValidationUntilNextCommit();
		}
	}

	@Override
	protected void addFields() {

		DateField admissionDate = addField(PreviousHospitalizationDto.ADMISSION_DATE, DateField.class);
		DateField dischargeDate = addField(PreviousHospitalizationDto.DISCHARGE_DATE, DateField.class);
		addField(PreviousHospitalizationDto.ISOLATED, OptionGroup.class);
		addField(PreviousHospitalizationDto.DESCRIPTION, TextArea.class).setRows(2);

		ComboBox facilityRegion = addField(PreviousHospitalizationDto.REGION, ComboBox.class);
		ComboBox facilityDistrict = addField(PreviousHospitalizationDto.DISTRICT, ComboBox.class);
		ComboBox facilityCommunity = addField(PreviousHospitalizationDto.COMMUNITY, ComboBox.class);
		facilityCommunity.setNullSelectionAllowed(true);
		ComboBox healthFacility = addField(PreviousHospitalizationDto.HEALTH_FACILITY, ComboBox.class);
		healthFacility.setImmediate(true);

		facilityRegion.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(facilityDistrict, regionDto != null ? FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()) : null);
       	});
		facilityDistrict.addValueChangeListener(e -> {
			if (facilityCommunity.getValue() == null) {
    			FieldHelper.removeItems(healthFacility);
    		}
    		FieldHelper.removeItems(facilityCommunity);
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(facilityCommunity, districtDto != null ? FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()) : null);
    		FieldHelper.updateItems(healthFacility, districtDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(districtDto, true) : null);
    	});
		facilityCommunity.addValueChangeListener(e -> {
			FieldHelper.removeItems(healthFacility);
    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
    		FieldHelper.updateItems(healthFacility, communityDto != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByCommunity(communityDto, true) :
    			facilityDistrict.getValue() != null ? FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict((DistrictReferenceDto) facilityDistrict.getValue(), true) :
    				null);
    	});

		facilityRegion.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		FieldHelper.addSoftRequiredStyle(admissionDate, dischargeDate, facilityCommunity);
		setRequired(true,
				PreviousHospitalizationDto.REGION,
				PreviousHospitalizationDto.DISTRICT,
				PreviousHospitalizationDto.HEALTH_FACILITY);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}
}
