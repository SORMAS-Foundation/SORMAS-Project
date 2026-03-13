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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Handle returned by FieldHelper listener-registration methods.
 * Call {@link #remove()} to deregister all listeners that were added during that call.
 */
@FunctionalInterface
public interface Registration extends Serializable {

	void remove();

	static Registration combine(Registration... registrations) {
		List<Registration> list = Arrays.asList(registrations);
		return () -> list.forEach(Registration::remove);
	}
}
