package de.symeda.sormas.ui.user;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

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
    private Button createButton;

	private VerticalLayout gridLayout;

    public UsersView() {
        setSizeFull();
        addStyleName("crud-view");

        grid = new UserGrid();
        grid.addItemClickListener(e -> ControllerProvider.getUserController().edit((UserDto)e.getItemId()));

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
    	topLayout.setWidth(100, Unit.PERCENTAGE);
    	topLayout.addStyleName(CssStyles.VSPACE3);
    	
    	Label header = new Label("Officers");
    	header.setSizeUndefined();
    	CssStyles.style(header, CssStyles.H2, CssStyles.NO_MARGIN);
    	topLayout.addComponent(header);
    	
    	Button statusAll = new Button("all", e -> grid.setUserRoleFilter(null));
        statusAll.setStyleName(ValoTheme.BUTTON_LINK);
        topLayout.addComponent(statusAll);

        for (UserRole role : UserRole.getAssignableRoles(LoginHelper.getCurrentUserRoles())) {
	    	Button userRoleButton = new Button(role.toString(), e -> grid.setUserRoleFilter(role));
	    	userRoleButton.setStyleName(ValoTheme.BUTTON_LINK);
	        topLayout.addComponent(userRoleButton);
        }

        createButton = new Button("New user");
        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
        createButton.addClickListener(e -> ControllerProvider.getUserController().create());
        topLayout.addComponent(createButton);
        topLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(createButton, 1);
        
        return topLayout;
    }
	
	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE3);

        ComboBox activeFilter = new ComboBox();
        activeFilter.setWidth(200, Unit.PIXELS);
        activeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(UserDto.I18N_PREFIX, UserDto.ACTIVE));
        activeFilter.addItems(ACTIVE_FILTER,INACTIVE_FILTER);
        activeFilter.addValueChangeListener(e-> {
        	String value = (String)e.getProperty().getValue();
			grid.setActiveFilter(value!=null?ACTIVE_FILTER.equals(value):null);
        });
        	
        filterLayout.addComponent(activeFilter);

        TextField filter = new TextField();
        filter.setWidth(200, Unit.PIXELS);
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Search user");
        filter.setImmediate(true);
        filter.addTextChangeListener(e -> grid.setNameFilter(e.getText()));
        filterLayout.addComponent(filter);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	// TODO what if a user has no assignable roles?
    	List<UserDto> users = FacadeProvider.getUserFacade().getAll(
    			UserRole.getAssignableRoles(LoginHelper.getCurrentUserRoles()).stream().toArray(UserRole[]::new));
        grid.setUsers(users);
    }
}
