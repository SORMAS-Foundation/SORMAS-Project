package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class ContactsView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;
	
	public static final String VIEW_NAME = "contacts";

	public static final String SEARCH_FIELD = "searchField";

	private ContactGrid grid;    
	private VerticalLayout gridLayout;

    public ContactsView() {
    	super(VIEW_NAME);
    	
        grid = new ContactGrid();

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        
        addComponent(gridLayout);
    }


	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setSizeUndefined();
    	topLayout.addStyleName(CssStyles.VSPACE_3);
    	
    	Button statusAll = new Button("all", e -> grid.setClassificationFilter(null));
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
        for (ContactClassification status : ContactClassification.values()) {
	    	Button statusButton = new Button(status.toString(), e -> grid.setClassificationFilter(status));
	    	statusButton.setStyleName(ValoTheme.BUTTON_LINK);
	    	topLayout.addComponent(statusButton);
        }
        
        return topLayout;
    }

	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE_3);
    	
        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.setWidth(140, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CAZE_DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        filterLayout.addComponent(diseaseFilter);

        UserDto user = LoginHelper.getCurrentUser();

        ComboBox regionFilter = new ComboBox();
        if (user.getRegion() == null) {
            regionFilter.setWidth(140, Unit.PIXELS);
            regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
            regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	grid.setRegionFilter(region);
            });
            filterLayout.addComponent(regionFilter);
        }

        ComboBox districtFilter = new ComboBox();
        districtFilter.setWidth(140, Unit.PIXELS);
        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
        districtFilter.setDescription("Select a district in the state");
        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));

        if (user.getRegion() != null) {
            districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
            districtFilter.setEnabled(true);
        } else {
            regionFilter.addValueChangeListener(e -> {
            	RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
            	districtFilter.removeAllItems();
            	if (region != null) {
            		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()));
                	districtFilter.setEnabled(true);
            	} else {
                	districtFilter.setEnabled(false);
            	}
            });
            districtFilter.setEnabled(false);
        }
        filterLayout.addComponent(districtFilter);

        ComboBox facilityFilter = new ComboBox();
        facilityFilter.setWidth(140, Unit.PIXELS);
        facilityFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
        facilityFilter.setDescription("Select a facility in the LGA");
        facilityFilter.addValueChangeListener(e->grid.setHealthFacilityFilter(((FacilityReferenceDto)e.getProperty().getValue())));
        facilityFilter.setEnabled(false);
        filterLayout.addComponent(facilityFilter);

        districtFilter.addValueChangeListener(e-> {
        	facilityFilter.removeAllItems();
        	DistrictReferenceDto district = (DistrictReferenceDto)e.getProperty().getValue();
        	if (district != null) {
        		facilityFilter.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(district, true));
        		facilityFilter.setEnabled(true);
        	} else {
        		facilityFilter.setEnabled(false);
        	}
        });

        ComboBox officerFilter = new ComboBox();
        officerFilter.setWidth(140, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER));
        if (user.getRegion() != null) {
        	officerFilter.addItems(FacadeProvider.getUserFacade().getAssignableUsersByRegion(user.getRegion(), UserRole.CONTACT_OFFICER));
        }
        officerFilter.addValueChangeListener(e->grid.setContactOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(officerFilter);
        
        ComboBox followUpStatusFilter = new ComboBox();
        followUpStatusFilter.setWidth(140, Unit.PIXELS);
        followUpStatusFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.FOLLOW_UP_STATUS));
        followUpStatusFilter.addItems((Object[])FollowUpStatus.values());
        followUpStatusFilter.addValueChangeListener(e->grid.setFollowUpStatusFilter(((FollowUpStatus)e.getProperty().getValue())));
        filterLayout.addComponent(followUpStatusFilter);
        
        TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, SEARCH_FIELD));
		searchField.addTextChangeListener(e->grid.filterByText(e.getText()));
		filterLayout.addComponent(searchField);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }
}
