package de.symeda.sormas.ui.caze;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.fields.PreviousHospitalizationsField;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseDataForm extends AbstractEditForm<CaseDataDto> {
	
	private static final String STATUS_CHANGE = "statusChange";

    private static final String HTML_LAYOUT = 
    		LayoutUtil.h3(CssStyles.VSPACE3, "Case data")+
			
			LayoutUtil.divCss(CssStyles.VSPACE2, 
					LayoutUtil.fluidRowLocs(CaseDataDto.CASE_CLASSIFICATION) +
					LayoutUtil.fluidRowLocs(CaseDataDto.INVESTIGATION_STATUS) +
		    		LayoutUtil.fluidRowCss(CssStyles.VSPACE4,
		    				LayoutUtil.fluidColumn(8, 0, 
		    						LayoutUtil.fluidRowLocs(CaseDataDto.UUID, CaseDataDto.DISEASE) +
		    						LayoutUtil.fluidRowLocs(CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE) +
		    						LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT) +
				    				LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY)),
		    				LayoutUtil.fluidColumnLoc(4, 0,  STATUS_CHANGE)
		    		)
		    )+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Responsible users")+
    		LayoutUtil.divCss(CssStyles.VSPACE2, 
    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.CONTACT_OFFICER, "")
			)+
    		LayoutUtil.fluidRowLocs(CaseDataDto.PREVIOUS_HOSPITALIZATIONS);
    	

    private final VerticalLayout statusChangeLayout;

    public CaseDataForm() {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
        statusChangeLayout = new VerticalLayout();
        statusChangeLayout.setSpacing(false);
        statusChangeLayout.setMargin(false);
        getContent().addComponent(statusChangeLayout, STATUS_CHANGE);
    }

    @Override
	protected void addFields() {
    	addField(CaseDataDto.UUID, TextField.class);
    	addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
    	addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
    	addField(CaseDataDto.REPORTING_USER, ComboBox.class);
    	addField(CaseDataDto.REPORT_DATE, DateField.class);
    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
    	addField(CaseDataDto.PREVIOUS_HOSPITALIZATIONS, PreviousHospitalizationsField.class);

    	
    	ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
    	ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
    	ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		RegionReferenceDto regionDto = (RegionReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});
    	district.addValueChangeListener(e -> {
    		community.removeAllItems();
    		DistrictReferenceDto districtDto = (DistrictReferenceDto)e.getProperty().getValue();
    		if (districtDto != null) {
    			community.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()));
    		}
    	});
    	community.addValueChangeListener(e -> {
    		facility.removeAllItems();
    		CommunityReferenceDto communityDto = (CommunityReferenceDto)e.getProperty().getValue();
    		if (communityDto != null) {
    			facility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(communityDto));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
    	ComboBox surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.SURVEILLANCE_OFFICER));
		surveillanceOfficerField.setNullSelectionAllowed(true);
		ComboBox contactOfficerField = addField(CaseDataDto.CONTACT_OFFICER, ComboBox.class);
		contactOfficerField.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.CONTACT_OFFICER));
		contactOfficerField.setNullSelectionAllowed(true);
    	
    	setRequired(true, CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

    	setReadOnly(true, CaseDataDto.UUID, CaseDataDto.DISEASE, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE);
	}
    
	@Override 
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
