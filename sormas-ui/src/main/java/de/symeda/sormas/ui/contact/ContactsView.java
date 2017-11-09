package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
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
        diseaseFilter.setWidth(200, Unit.PIXELS);
        diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CAZE_DISEASE));
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        filterLayout.addComponent(diseaseFilter);

        UserDto user = LoginHelper.getCurrentUser();
        if(user.getRegion() != null) {
	        ComboBox districtFilter = new ComboBox();
	        districtFilter.setWidth(200, Unit.PIXELS);
	        districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CAZE_DISTRICT));
	        districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
	        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue())));
	        filterLayout.addComponent(districtFilter);
        }

        ComboBox officerFilter = new ComboBox();
        officerFilter.setWidth(240, Unit.PIXELS);
        officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER));
        if (user.getRegion() != null) {
        	officerFilter.addItems(FacadeProvider.getUserFacade().getAssignableUsersByRegion(user.getRegion(), UserRole.CONTACT_OFFICER));
        }
        officerFilter.addValueChangeListener(e->grid.setContactOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        filterLayout.addComponent(officerFilter);
        
        ComboBox followUpStatusFilter = new ComboBox();
        followUpStatusFilter.setWidth(240, Unit.PIXELS);
        followUpStatusFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.FOLLOW_UP_STATUS));
        followUpStatusFilter.addItems((Object[])FollowUpStatus.values());
        followUpStatusFilter.addValueChangeListener(e->grid.setFollowUpStatusFilter(((FollowUpStatus)e.getProperty().getValue())));
        filterLayout.addComponent(followUpStatusFilter);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }
}
