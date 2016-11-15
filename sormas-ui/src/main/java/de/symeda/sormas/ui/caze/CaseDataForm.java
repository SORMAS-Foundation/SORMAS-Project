package de.symeda.sormas.ui.caze;

import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
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
					LayoutUtil.fluidRowLocs(CaseDataDto.CASE_STATUS) +
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
			);

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
    	addField(CaseDataDto.CASE_STATUS, OptionGroup.class);
    	addField(CaseDataDto.REPORTING_USER, ComboBox.class);
    	addField(CaseDataDto.REPORT_DATE, DateField.class);
    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
    	ComboBox region = addField(CaseDataDto.REGION, ComboBox.class);
    	ComboBox district = addField(CaseDataDto.DISTRICT, ComboBox.class);
    	ComboBox community = addField(CaseDataDto.COMMUNITY, ComboBox.class);
    	ComboBox facility = addField(CaseDataDto.HEALTH_FACILITY, ComboBox.class);
    	
    	region.addValueChangeListener(e -> {
    		district.removeAllItems();
    		ReferenceDto regionDto = (ReferenceDto)e.getProperty().getValue();
    		if (regionDto != null) {
    			district.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(regionDto.getUuid()));
    		}
    	});
    	district.addValueChangeListener(e -> {
    		community.removeAllItems();
    		ReferenceDto districtDto = (ReferenceDto)e.getProperty().getValue();
    		if (districtDto != null) {
    			community.addItems(FacadeProvider.getCommunityFacade().getAllByDistrict(districtDto.getUuid()));
    		}
    	});
    	community.addValueChangeListener(e -> {
    		facility.removeAllItems();
    		ReferenceDto communityDto = (ReferenceDto)e.getProperty().getValue();
    		if (communityDto != null) {
    			facility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(communityDto.getUuid()));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

		UserReferenceDto currentUser = LoginHelper.getCurrentUserAsReference();
    	addField(CaseDataDto.SURVEILLANCE_OFFICER, ComboBox.class)
    		.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.SURVEILLANCE_OFFICER));
    	addField(CaseDataDto.CONTACT_OFFICER, ComboBox.class)
			.addItems(FacadeProvider.getUserFacade().getAssignableUsers(currentUser, UserRole.CONTACT_OFFICER));
    	
    	setRequired(true, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);

    	setReadOnly(true, CaseDataDto.UUID, 
    			CaseDataDto.CASE_STATUS, CaseDataDto.DISEASE, 
    			CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE);
	}
    
    public void setStatusChangeButtons(CaseStatus currentStatus, Iterable<CaseStatus> statuses, Consumer<CaseStatus> statusChangeConsumer) {
    	
    	statusChangeLayout.removeAllComponents();
    	
    	for (final CaseStatus status : statuses) {
        	Button button = new Button();
        	button.setCaption(status.getChangeString());
        	if (CaseHelper.isPrimary(currentStatus, status)) {
        		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        	}
        	button.addStyleName(CssStyles.FORCE_CAPTION);
        	button.setWidth(100, Unit.PERCENTAGE);
        	button.addClickListener(e -> statusChangeConsumer.accept(status));
        	statusChangeLayout.addComponent(button);
    	}
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
