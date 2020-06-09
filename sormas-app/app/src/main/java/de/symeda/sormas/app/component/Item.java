/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component;

import java.util.Objects;

public class Item<C> {

	private String key;
	private C value;

	public Item(String key, C value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public C getValue() {
		return value;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Item<?> item = (Item<?>) o;
		return Objects.equals(key, item.key) && Objects.equals(value, item.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}
}
