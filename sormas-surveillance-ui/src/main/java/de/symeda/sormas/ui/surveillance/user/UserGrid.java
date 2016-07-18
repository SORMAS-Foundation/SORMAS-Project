package de.symeda.sormas.ui.surveillance.user;

import java.util.Collection;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import elemental.json.JsonValue;

public class UserGrid extends Grid {

	private static final long serialVersionUID = -1L;

	public UserGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<UserDto> container = new BeanItemContainer<UserDto>(UserDto.class);
        setContainerDataSource(container);
        setColumns(UserDto.ACTIVE, UserDto.USER_NAME, UserDto.NAME, UserDto.USER_EMAIL, UserDto.ADDRESS, UserDto.LGA);
        
        getColumn(UserDto.ACTIVE).setRenderer(new ActiveRenderer());
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			UserDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
	}
	
    /**
     * Filter the grid based on a search string that is searched for in the
     * product name, availability and category columns.
     *
     * @param filterString
     *            string to look for
     */
//    public void setFilter(String filterString) {
//    	getContainer().removeContainerFilters(CaseDataDto.PERSON);
//        if (filterString.length() > 0) {
//            SimpleStringFilter nameFilter = new SimpleStringFilter(CaseDataDto.PERSON, filterString, true, false);
//            getContainer().addContainerFilter(
////            new Or(nameFilter, descFilter, statusFilter));
//            new Or(nameFilter));
//        }
//
//    }
    
    public void setFilter(UserRole roleToFilter) {
    	removeAllStatusFilter();
    	if (roleToFilter != null) {
    		
//    		Vergleich in einer Liste!!!!
//    		for (iterable_type iterable_element : iterable) {
//				
//			}
    		
	    	Equal filter = new Equal(UserDto.USER_ROLES, roleToFilter);  
	        getContainer().addContainerFilter(filter);
    	}
    }
    
    public void removeAllStatusFilter() {
    	getContainer().removeContainerFilters(CaseDataDto.CASE_STATUS);
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<UserDto> getContainer() {
        return (BeanItemContainer<UserDto>) super.getContainerDataSource();
    }

    public void setUsers(Collection<UserDto> users) {
        getContainer().removeAllItems();
        getContainer().addAll(users);
    }

    public void refresh(UserDto user) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<UserDto> item = getContainer().getItem(user);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(UserDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(user);
        }
    }

    public void remove(CaseDataDto caze) {
        getContainer().removeItem(caze);
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


