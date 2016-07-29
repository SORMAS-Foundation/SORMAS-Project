package de.symeda.sormas.ui.surveillance.user;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToCollectionConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
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
        setColumns(UserDto.ACTIVE, UserDto.USER_ROLES, UserDto.USER_NAME, UserDto.NAME, UserDto.USER_EMAIL, UserDto.ADDRESS, UserDto.LGA);
        
        getColumn(UserDto.ACTIVE).setRenderer(new ActiveRenderer());

        getColumn(UserDto.USER_ROLES).setConverter(new StringToCollectionConverter());

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			UserDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
	}
	
    public void setFilter(UserRole roleToFilter) {
    	removeAllStatusFilter();
    	if (roleToFilter != null) {
    		
    		RoleFilter roleFilter = new RoleFilter(UserDto.USER_ROLES, roleToFilter);
	        getContainer().addContainerFilter(roleFilter);
    	}
    }
    
    public void removeAllStatusFilter() {
    	getContainer().removeContainerFilters(UserDto.USER_ROLES);
    }
    
    
    public void setFilter(Boolean active) {
    	removeAllActiveFilter();
    	if(active!=null) {
    		Equal activeFilter = new Equal(UserDto.ACTIVE,active);
    		getContainer().addContainerFilter(activeFilter);
    	}
    }

	private void removeAllActiveFilter() {
		getContainer().removeContainerFilters(UserDto.ACTIVE);
	}
	
	public void setFilter(String filterString) {
    	getContainer().removeContainerFilters(UserDto.NAME);
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(UserDto.NAME, filterString, true, false);
            getContainer().addContainerFilter(
//            new Or(nameFilter, descFilter, statusFilter));
            new Or(nameFilter));
        }
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
    
    @SuppressWarnings("serial")
	public class RoleFilter implements Filter {
    	
    	private Object propertyId;
        private UserRole roleToFilter;
        
        public RoleFilter(Object propertyId, UserRole roleToFilter) {
            this.propertyId = propertyId;
            this.roleToFilter = roleToFilter;
        }

		@SuppressWarnings("unchecked")
		@Override
		public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
			final Property<?> p = item.getItemProperty(getPropertyId());
	        if (null == p) {
	            return false;
	        }
	        Collection<UserRole> roles = (Collection<UserRole>) p.getValue();
        	for (UserRole role : roles) {
				if(compareEquals(roleToFilter, role)) {
					return true;
				}
			}
	        
	        // all cases should have been processed above
	        return false;
		}

		@Override
		public boolean appliesToProperty(Object propertyId) {
			return getPropertyId().equals(propertyId);
		}
		
		public UserRole getRoleToFilter() {
			return this.roleToFilter;
		}
		
		public Object getPropertyId() {
	        return propertyId;
	    }
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private boolean compareEquals(Object valueOne, Object valueTwo) {
	        if (valueOne == null || valueTwo == null) {
	            return (valueTwo == valueOne);
	        } else if (valueOne == valueTwo) {
	            return true;
	        } else if (valueOne instanceof Comparable
	                && valueTwo.getClass()
	                        .isAssignableFrom(valueOne.getClass())) {
	            return ((Comparable) valueOne).compareTo(valueTwo) == 0;
	        } else {
	            return valueOne.equals(valueTwo);
	        }
	    }

    	
    }
}


