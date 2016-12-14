package de.symeda.sormas.ui.caze;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class CaseCreateForm extends AbstractEditForm<CaseDataDto> {
	
	private static final String PERSON_CREATE = "PersonCreate";

    private static final String HTML_LAYOUT = 
			LayoutUtil.divCss(CssStyles.VSPACE2,
					LayoutUtil.fluidRowLocs(CaseDataDto.DISEASE, ""),
					LayoutUtil.fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT),
					LayoutUtil.fluidRowLocs(CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY),
					LayoutUtil.fluidRow(
							LayoutUtil.fluidColumnLoc(10, 0, CaseDataDto.PERSON),
							LayoutUtil.fluidColumnLoc(2, 0, PERSON_CREATE))
					);

    private ComboBox persons;
    
    public CaseCreateForm() {
        super(CaseDataDto.class, CaseDataDto.I18N_PREFIX);
    }

    @Override
	protected void addFields() {

    	addField(CaseDataDto.DISEASE, NativeSelect.class);
    	
    	persons = addField(CaseDataDto.PERSON, ComboBox.class);    	
		updatePersonsSelect();

    	Button personCreateButton = new Button(null, FontAwesome.PLUS_SQUARE);
    	personCreateButton.setDescription("Create new person");
    	personCreateButton.addStyleName(ValoTheme.BUTTON_LINK);
    	personCreateButton.addStyleName(CssStyles.FORCE_CAPTION);
    	personCreateButton.addClickListener(e -> createPersonClicked());
    	getContent().addComponent(personCreateButton, PERSON_CREATE);    
    	
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
    			facility.addItems(FacadeProvider.getFacilityFacade().getAllByCommunity(communityDto.getUuid()));
    		}
    	});
		region.addItems(FacadeProvider.getRegionFacade().getAllAsReference());

    	setRequired(true, CaseDataDto.DISEASE, CaseDataDto.PERSON, 
    			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY);
    }
    
    private void createPersonClicked() {
    	
    	// TODO replace with firstname and lastname of person as done in contact creation
    	
    	ControllerProvider.getPersonController().create(
    			person ->  {
    				if (person != null) {
    					updatePersonsSelect();
    					// try to select new person
    					for (Object itemId : persons.getItemIds()) {
    						PersonReferenceDto dto = (PersonReferenceDto)itemId;
    						if (dto.getUuid().equals(person.getUuid())) {
    							persons.setValue(dto);
    							break;
    						}
						}
    				}
    			});
    }
    
    private void updatePersonsSelect() {
    	Object value = persons.getValue();
    	persons.removeAllItems();
    	persons.addItems(FacadeProvider.getPersonFacade().getAllPersons());
    	persons.setValue(value);
    }
    
	@Override
	protected String createHtmlLayout() {
		 return HTML_LAYOUT;
	}
}
