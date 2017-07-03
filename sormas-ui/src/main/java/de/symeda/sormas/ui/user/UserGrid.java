package de.symeda.sormas.ui.user;

import java.util.Collection;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
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

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
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
        
        setColumns(EDIT_BTN_ID, UserDto.ACTIVE, UserDto.USER_ROLES, UserDto.USER_NAME, UserDto.NAME, UserDto.USER_EMAIL, UserDto.ADDRESS, UserDto.DISTRICT);

        getColumn(EDIT_BTN_ID).setRenderer(new HtmlRenderer());
        getColumn(EDIT_BTN_ID).setWidth(60);
        
        getColumn(UserDto.ACTIVE).setRenderer(new ActiveRenderer());
        getColumn(UserDto.ACTIVE).setWidth(80);

        getColumn(UserDto.USER_ROLES).setConverter(new StringToCollectionConverter());

        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
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
	
	public void setNameFilter(String filterString) {
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
    
    @SuppressWarnings("serial")
	public class RoleFilter implements Filter {
    	
    	private transient Object propertyId;
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


