package de.symeda.sormas.ui.caze;

import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
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
			LayoutUtil.h3(CssStyles.VSPACE3, "Additional medical information")+
			LayoutUtil.fluidRowCss(CssStyles.VSPACE2,
					LayoutUtil.fluidColumn(8, 0, 
						LayoutUtil.fluidRowLocs(CaseDataDto.PREGNANT, "") +
						LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION, CaseDataDto.MEASLES_DOSES) +
						LayoutUtil.fluidRowLocs(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, "")
					)
			)+
    		LayoutUtil.h3(CssStyles.VSPACE3, "Responsible users")+
    		LayoutUtil.divCss(CssStyles.VSPACE2, 
    				LayoutUtil.fluidRowLocs(CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.CONTACT_OFFICER, "")
			);
    	

    private final VerticalLayout statusChangeLayout;
    private final PersonDto person;
    private final Disease disease;

    public CaseDataForm(PersonDto person, Disease disease) {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
        this.person = person;
        this.disease = disease;
        statusChangeLayout = new VerticalLayout();
        statusChangeLayout.setSpacing(false);
        statusChangeLayout.setMargin(false);
        getContent().addComponent(statusChangeLayout, STATUS_CHANGE);
        addFields();
    }

    @Override
	protected void addFields() {
    	if (person == null || disease == null) {
    		return;
    	}
    	
    	addField(CaseDataDto.UUID, TextField.class);
    	addField(CaseDataDto.CASE_CLASSIFICATION, OptionGroup.class);
    	addField(CaseDataDto.INVESTIGATION_STATUS, OptionGroup.class);
    	addField(CaseDataDto.REPORTING_USER, ComboBox.class);
    	addField(CaseDataDto.REPORT_DATE, DateTimeField.class);
    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
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

		ComboBox surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class);
		surveillanceOfficerField.setNullSelectionAllowed(true);
		ComboBox contactOfficerField = addField(CaseDataDto.CONTACT_OFFICER, ComboBox.class);
		contactOfficerField.setNullSelectionAllowed(true);
		
		district.addValueChangeListener(e -> {
			List<UserReferenceDto> assignableSurveillanceOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict((DistrictReferenceDto) district.getValue(), false, UserRole.SURVEILLANCE_OFFICER);
			List<UserReferenceDto> assignableContactOfficers = FacadeProvider.getUserFacade().getAssignableUsersByDistrict((DistrictReferenceDto) district.getValue(), false, UserRole.CONTACT_OFFICER);
			
			surveillanceOfficerField.removeAllItems();
			surveillanceOfficerField.select(0);
			surveillanceOfficerField.addItems(assignableSurveillanceOfficers);
			contactOfficerField.removeAllItems();
			surveillanceOfficerField.select(0);
			contactOfficerField.addItems(assignableContactOfficers);
		});
    	
		addField(CaseDataDto.PREGNANT, OptionGroup.class);
		addField(CaseDataDto.MEASLES_VACCINATION, ComboBox.class);
		addField(CaseDataDto.MEASLES_DOSES, TextField.class);
		addField(CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE, ComboBox.class);
    	
    	setRequired(true, CaseDataDto.CASE_CLASSIFICATION, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

    	setReadOnly(true, CaseDataDto.UUID, CaseDataDto.DISEASE, CaseDataDto.INVESTIGATION_STATUS,
    			CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE);
    	
    	Sex personSex = person.getSex();
    	if (personSex != Sex.FEMALE) {
    		setReadOnly(true, CaseDataDto.PREGNANT);
    	}
    	
    	boolean visible = DiseasesConfiguration.isDefinedOrMissing(CaseDataDto.class, CaseDataDto.MEASLES_VACCINATION, disease);
		setVisible(visible, CaseDataDto.MEASLES_VACCINATION);
		
		FieldHelper.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.MEASLES_DOSES, CaseDataDto.MEASLES_VACCINATION_INFO_SOURCE), 
				CaseDataDto.MEASLES_VACCINATION, Arrays.asList(Vaccination.VACCINATED), true);
	}
    
	@Override 
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
