package de.symeda.sormas.ui.contact;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.visit.VisitGrid;

public class ContactVisitsView extends AbstractContactView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = "contacts/visits";

	private VisitGrid grid;    
    private Button newButton;
	private VerticalLayout gridLayout;

    public ContactVisitsView() {
    	super(VIEW_NAME);

        setSizeFull();

        grid = new VisitGrid();

        gridLayout = new VerticalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);

        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(grid);
        gridLayout.setExpandRatio(grid, 1);
        
        setSubComponent(gridLayout);
    }

	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	
    	Button contactButton = new Button("contact related", e -> {
    		grid.reload(getContactRef());
    	});
    	contactButton.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(contactButton);

    	Button personButton = new Button("all visits of contact person", e -> {
    		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
    		grid.reload(contact.getPerson());
    	});
    	personButton.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(personButton);
        
        newButton = new Button("New visit");
        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newButton.setIcon(FontAwesome.PLUS_CIRCLE);
        newButton.addClickListener(e -> {
        	ControllerProvider.getVisitController().createVisit(this.getContactRef(), 
        			r -> grid.reload(getContactRef()));
        });
        topLayout.addComponent(newButton);
        topLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(newButton, 1);
        
        topLayout.addStyleName(CssStyles.VSPACE_3);
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	grid.reload(getContactRef());
    }
}
