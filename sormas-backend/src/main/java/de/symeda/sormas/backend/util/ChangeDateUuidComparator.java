/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.util;

import java.util.Comparator;

import de.symeda.sormas.backend.common.AbstractDomainObject;

public class ChangeDateUuidComparator<T extends AbstractDomainObject> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		if (o2 == null) {
			return o1 == null ? 0 : 1;
		}
		int dateComparison;
		if (o1.getChangeDate() == null) {
			dateComparison = o2.getChangeDate() == null ? 0 : 1;
		} else {
			// compare timestamps with precision millisecond
			dateComparison = Long.compare(o1.getChangeDate().getTime(), o2.getChangeDate().getTime());
		}
		if (dateComparison != 0) {
			return dateComparison;
		}
		if (o1.getUuid() == null) {
			return o2.getUuid() == null ? 0 : 1;
		}
		return o1.getUuid().compareTo(o2.getUuid());
	}
}
