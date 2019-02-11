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

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.StringToCollectionConverter;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.utils.RoleFilter;
import de.symeda.sormas.ui.utils.UuidRenderer;
import elemental.json.JsonValue;

public class UserGrid extends Grid {

	private static final long serialVersionUID = -1L;
	
	private static final String EDIT_BTN_ID = "edit";

	@SuppressWarnings("serial")
	public UserGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<UserDto> container = new BeanItemContainer<UserDto>(UserDto.class);
        GeneratedPropertyContainer editContainer = new GeneratedPropertyContainer(container);
        // edit button
        editContainer.addGeneratedProperty(EDIT_BTN_ID, new PropertyValueGenerator<String>() {
			@Override
			public String getValue(Item item, Object itemId, Object propertyId) {
				return FontAwesome.PENCIL_SQUARE.getHtml();
			}
			@Override
			public Class<String> getType() {
				return String.class;
			}
		});
        
        setContainerDataSource(editContainer);
        
        setColumns(EDIT_BTN_ID, UserDto.UUID, UserDto.ACTIVE, UserDto.USER_ROLES, UserDto.USER_NAME, UserDto.NAME, UserDto.USER_EMAIL, UserDto.ADDRESS, UserDto.DISTRICT);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        getColumn(EDIT_BTN_ID).setHeaderCaption("");
        
        getColumn(UserDto.UUID).setRenderer(new UuidRenderer());

        getColumn(UserDto.ACTIVE).setRenderer(new ActiveRenderer());
        getColumn(UserDto.ACTIVE).setWidth(80);

        getColumn(UserDto.USER_ROLES).setConverter(new StringToCollectionConverter());

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixCaption(
        			UserDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
	}
	
    public void setUserRoleFilter(UserRole roleToFilter) {
    	getContainer().removeContainerFilters(UserDto.USER_ROLES);
    	if (roleToFilter != null) {
    		RoleFilter roleFilter = new RoleFilter(UserDto.USER_ROLES, roleToFilter);
	        getContainer().addContainerFilter(roleFilter);
    	}
    }
    
    public void setActiveFilter(Boolean active) {
    	getContainer().removeContainerFilters(UserDto.ACTIVE);
    	if(active!=null) {
    		Equal activeFilter = new Equal(UserDto.ACTIVE,active);
    		getContainer().addContainerFilter(activeFilter);
    	}
    }
	
	public void filterByText(String filterString) {
    	getContainer().removeContainerFilters(UserDto.NAME);
    	getContainer().removeContainerFilters(UserDto.USER_NAME);
    	getContainer().removeContainerFilters(UserDto.USER_EMAIL);
    	getContainer().removeContainerFilters(UserDto.PHONE);
    	getContainer().removeContainerFilters(UserDto.UUID);
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(UserDto.NAME, filterString, true, false);
            SimpleStringFilter userNameFilter = new SimpleStringFilter(UserDto.USER_NAME, filterString, true, false);
            SimpleStringFilter emailFilter = new SimpleStringFilter(UserDto.USER_EMAIL, filterString, true, false);
            SimpleStringFilter phoneFilter = new SimpleStringFilter(UserDto.PHONE, filterString, true, false);
            SimpleStringFilter uuidFilter = new SimpleStringFilter(UserDto.UUID, filterString, true, false);
            getContainer().addContainerFilter(
            		new Or(nameFilter, userNameFilter, emailFilter, phoneFilter, uuidFilter));
        }
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<UserDto> getContainer() {
    	GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
        return (BeanItemContainer<UserDto>) container.getWrappedContainer();
    }

    public void setUsers(Collection<UserDto> users) {
        getContainer().removeAllItems();
        getContainer().addAll(users);
    }
    
    @SuppressWarnings("serial")
	public static class ActiveRenderer extends HtmlRenderer {
   	 
        @Override
        public JsonValue encode(String value) {
        	String iconValue = FontAwesome.CHECK_SQUARE_O.getHtml();
        	if(!Boolean.parseBoolean(value)) {
        		iconValue = FontAwesome.SQUARE_O.getHtml();
        	}
            return super.encode(iconValue);
        }
    }
}


