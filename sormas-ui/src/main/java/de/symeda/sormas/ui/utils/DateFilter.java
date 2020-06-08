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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.utils;

import java.util.Date;

import org.joda.time.LocalDate;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class DateFilter implements Filter {

	final Object propertyId;
	final String filterString;

	public DateFilter(Object propertyId, String filterString) {
		this.propertyId = propertyId;
		this.filterString = filterString;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {

		if (DataHelper.isNullOrEmpty(filterString)) {
			return true;
		}

		Date date = (Date) item.getItemProperty(propertyId).getValue();

		if (date == null) {
			return false;
		}

		Date[] dateBounds = DateHelper.findDateBounds(filterString);

		if (dateBounds != null) {
			if (!date.before(dateBounds[0]) && !date.after(dateBounds[1])) {
				// not outside bounds
				return true;
			}
		}

		Integer[] dayAndMonth = DateHelper.findDatePrefix(filterString);
		if (dayAndMonth != null) {

			LocalDate localDate = new LocalDate(date);
			if (dayAndMonth[0] != null) {
				if (localDate.getDayOfMonth() != dayAndMonth[0]) {
					// day does not match
					return false;
				}
			}
			if (dayAndMonth[1] != null) {
				if (localDate.getMonthOfYear() != dayAndMonth[1]) {
					// month does not match
					return false;
				}
			}

			// available data matches
			return true;
		}

		return false;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return this.propertyId.equals(propertyId);
	}
}
