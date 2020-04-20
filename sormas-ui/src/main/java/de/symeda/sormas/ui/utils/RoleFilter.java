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
package de.symeda.sormas.ui.utils;

import java.util.Collection;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

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