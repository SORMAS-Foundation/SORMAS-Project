/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.user;

import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
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
	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

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
        
    	if (UserProvider.getCurrent().hasUserRight(UserRight.USER_CREATE)) {
	        createButton = new Button(I18nProperties.getCaption(Captions.userNewUser));
	        createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
	        createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
	        createButton.addClickListener(e -> ControllerProvider.getUserController().create());
	        addHeaderComponent(createButton);
    	}
    }
	
	public HorizontalLayout createFilterBar() {
    	HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
    	filterLayout.setSpacing(true);
    	filterLayout.setSizeUndefined();
    	filterLayout.addStyleName(CssStyles.VSPACE_3);

        ComboBox activeFilter = new ComboBox();
        activeFilter.setWidth(200, Unit.PIXELS);
        activeFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.ACTIVE));
        activeFilter.addItems(ACTIVE_FILTER,INACTIVE_FILTER);
        activeFilter.addValueChangeListener(e-> {
        	String value = (String)e.getProperty().getValue();
			grid.setActiveFilter(value!=null?ACTIVE_FILTER.equals(value):null);
        });
        	
        filterLayout.addComponent(activeFilter);
        
        ComboBox roleFilter = new ComboBox();
        roleFilter.setWidth(200, Unit.PIXELS);
        roleFilter.setInputPrompt(I18nProperties.getPrefixCaption(UserDto.I18N_PREFIX, UserDto.USER_ROLES));
        roleFilter.addItems(UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles()));
        roleFilter.addValueChangeListener(e -> {
        	UserRole value = (UserRole) e.getProperty().getValue();
        	grid.setUserRoleFilter(value);
        });
        
        filterLayout.addComponent(roleFilter);

        TextField filter = new TextField();
        filter.setWidth(200, Unit.PIXELS);
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt(I18nProperties.getString(Strings.promptUserSearch));
        filter.setImmediate(true);
        filter.addTextChangeListener(e -> grid.filterByText(e.getText()));
        filterLayout.addComponent(filter);

        return filterLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	List<UserDto> users = FacadeProvider.getUserFacade().getAll(
    			UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles()).stream().toArray(UserRole[]::new));
        grid.setUsers(users);
    }
}
