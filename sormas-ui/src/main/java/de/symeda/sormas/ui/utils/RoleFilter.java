package de.symeda.sormas.ui.utils;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;

import de.symeda.sormas.api.user.UserRole;

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