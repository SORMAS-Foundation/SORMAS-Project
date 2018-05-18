package de.symeda.sormas.ui.user;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
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
    	super(VIEW_NAME);
    	
        grid = new UserGrid();
        grid.addItemClickListener(e -> ControllerProvider.getUserController().edit((UserDto)e.getItemId()));

        gridLayout = new VerticalLayout();
        gridLayout.addComponent(createFilterBar());
        gridLayout.addComponent(grid);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false);
        gridLayout.setSizeFull();
        gridLayout.setExpandRatio(grid, 1);
        gridLayout.setStyleName("crud-main-layout");
        
        addComponent(gridLayout);
        
    	if (LoginHelper.hasUserRight(UserRight.USER_CREATE)) {
	        createButton = new Button("New user");
	        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	        createButton.setIcon(FontAwesome.PLUS_CIRCLE);
	        createButton.addClickListener(e -> ControllerProvider.getUserController().create());
	        addHeaderComponent(createButton);
    	}
    }
	
	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE_3);

        ComboBox activeFilter = new ComboBox();
        activeFilter.setWidth(200, Unit.PIXELS);
        activeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(UserDto.I18N_PREFIX, UserDto.ACTIVE));
        activeFilter.addItems(ACTIVE_FILTER,INACTIVE_FILTER);
        activeFilter.addValueChangeListener(e-> {
        	String value = (String)e.getProperty().getValue();
			grid.setActiveFilter(value!=null?ACTIVE_FILTER.equals(value):null);
        });
        	
        filterLayout.addComponent(activeFilter);
        
        ComboBox roleFilter = new ComboBox();
        roleFilter.setWidth(200, Unit.PIXELS);
        roleFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(UserDto.I18N_PREFIX, UserDto.USER_ROLES));
        roleFilter.addItems(UserRole.getAssignableRoles(LoginHelper.getCurrentUserRoles()));
        roleFilter.addValueChangeListener(e -> {
        	UserRole value = (UserRole) e.getProperty().getValue();
        	grid.setUserRoleFilter(value);
        });
        
        filterLayout.addComponent(roleFilter);

        TextField filter = new TextField();
        filter.setWidth(200, Unit.PIXELS);
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Search user");
        filter.setImmediate(true);
        filter.addTextChangeListener(e -> grid.filterByText(e.getText()));
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
