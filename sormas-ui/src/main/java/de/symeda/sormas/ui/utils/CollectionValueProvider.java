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

import java.util.Collection;

import com.vaadin.data.ValueProvider;

/**
 * A ValueProvider that allows displaying a collection as a comma separated list of
 * strings.
 */
@SuppressWarnings({
	"serial",
	"rawtypes" })
public class CollectionValueProvider<T extends Collection> implements ValueProvider<T, String> {

	@Override
	public String apply(T source) {

		if (source == null) {
			return "";
		}

		StringBuilder b = new StringBuilder();
		for (Object o : source) {
			b.append(o.toString());
			b.append(", ");
		}
		if (b.length() >= 2) {
			return b.substring(0, b.length() - 2);
		}
		return "";
	}
}
