package de.symeda.sormas.ui.user;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.samples.ResetButtonForTextField;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.AbstractView;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link UserController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class UsersView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;
	
	public static final String VIEW_NAME = "users";
	public static final String ACTIVE_FILTER = "Active";
	public static final String INACTIVE_FILTER = "Inactive";

	private UserGrid grid;    
    private Button newCase;

	private VerticalLayout gridLayout;

    public UsersView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new UserGrid();
        grid.addItemClickListener(e -> ControllerProvider.getUserController().edit((UserDto)e.getItemId()));

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createTopBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        
        addComponent(gridLayout);
    }


	public HorizontalLayout createTopBar() {
    	HorizontalLayout topLayout = new HorizontalLayout();
    	topLayout.setSpacing(true);
    	topLayout.setWidth("100%");
    	
    	Button statusAll = new Button("all", e -> grid.removeAllStatusFilter());
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);
        
    	Button surveillanceOfficerFilter = new Button("Surveillance Officer", e -> grid.setFilter(UserRole.SURVEILLANCE_OFFICER));
    	surveillanceOfficerFilter.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(surveillanceOfficerFilter);
        
        Button informantFilter = new Button("Informant", e -> grid.setFilter(UserRole.INFORMANT));
    	informantFilter.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(informantFilter);

        ComboBox activeFilter = new ComboBox();
        activeFilter.addItems(ACTIVE_FILTER,INACTIVE_FILTER);
        activeFilter.addValueChangeListener(e-> {
        	String value = (String)e.getProperty().getValue();
			grid.setFilter(value!=null?ACTIVE_FILTER.equals(value):null);
        });
        	
        topLayout.addComponent(activeFilter);

        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Search user");
        ResetButtonForTextField.extend(filter);
        filter.setImmediate(true);
        filter.addTextChangeListener(e -> grid.setFilter(e.getText()));
        topLayout.addComponent(filter);

        newCase = new Button("New user");
        newCase.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newCase.setIcon(FontAwesome.PLUS_CIRCLE);
        newCase.addClickListener(e -> ControllerProvider.getUserController().create());
        topLayout.addComponent(newCase);

        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	List<UserDto> users = FacadeProvider.getUserFacade().getAll(UserRole.INFORMANT, UserRole.SURVEILLANCE_OFFICER);
        grid.setUsers(users);
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void refresh(UserDto product) {
        grid.refresh(product);
        grid.scrollTo(product);
    }

    public void remove(CaseDataDto caze) {
        grid.remove(caze);
    }

}
