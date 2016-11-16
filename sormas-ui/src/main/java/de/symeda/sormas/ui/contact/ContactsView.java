package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.contact.ContactStatus;
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
    private Button newButton;

	private VerticalLayout gridLayout;

    public ContactsView() {
        setSizeFull();
        addStyleName("crud-view");

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
    	topLayout.setWidth("100%");
    	
    	Label header = new Label("Contacts");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
    	
    	Button statusAll = new Button("all", e -> grid.removeAllStatusFilter());
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
    	Button statusPossible = new Button(ContactStatus.POSSIBLE.toString(), e -> grid.setStatusFilter(ContactStatus.POSSIBLE));
    	statusPossible.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusPossible);
        
        Button statusInvestigated = new Button(ContactStatus.FOLLOW_UP.toString(), e -> grid.setStatusFilter(ContactStatus.FOLLOW_UP));
        statusInvestigated.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusInvestigated);
        
        newButton = new Button("New contact");
        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newButton.setIcon(FontAwesome.PLUS_CIRCLE);
        //newButton.addClickListener(e -> ControllerProvider.getContactController().create());
        topLayout.addComponent(newButton);

        topLayout.setExpandRatio(statusInvestigated, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

	public HorizontalLayout createFilterBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth("100%");
    	
        ComboBox diseaseFilter = new ComboBox();
        diseaseFilter.addItems((Object[])Disease.values());
        diseaseFilter.addValueChangeListener(e->grid.setDiseaseFilter(((Disease)e.getProperty().getValue())));
        topLayout.addComponent(diseaseFilter);

        ComboBox districtFilter = new ComboBox();
        UserDto user = LoginHelper.getCurrentUser();
        districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
        districtFilter.addValueChangeListener(e->grid.setDistrictFilter(((ReferenceDto)e.getProperty().getValue())));
        topLayout.addComponent(districtFilter);

        ComboBox officerFilter = new ComboBox();
        officerFilter.addItems(FacadeProvider.getUserFacade().getAssignableUsers(user, UserRole.CONTACT_OFFICER));
        officerFilter.addValueChangeListener(e->grid.setContactOfficerFilter(((UserReferenceDto)e.getProperty().getValue())));
        topLayout.addComponent(officerFilter);

        topLayout.setExpandRatio(officerFilter, 1);
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	grid.reload();
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

//    public void refresh(CaseDataDto product) {
//        grid.refresh(product);
//        grid.scrollTo(product);
//    }
//
//    public void remove(CaseDataDto caze) {
//        grid.remove(caze);
//    }

}
