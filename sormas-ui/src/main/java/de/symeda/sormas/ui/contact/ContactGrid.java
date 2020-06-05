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
package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.navigator.View;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.utils.SortProperty;

@SuppressWarnings("serial")
public class ContactGrid extends AbstractContactGrid<ContactIndexDto> {

	public <V extends View> ContactGrid(ContactCriteria criteria, Class<V> viewClass) {
		super(ContactIndexDto.class, criteria, viewClass);
	}

	protected List<ContactIndexDto> getGridData(ContactCriteria contactCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		return FacadeProvider.getContactFacade().getIndexList(contactCriteria, first, max, sortProperties);
	}
}
