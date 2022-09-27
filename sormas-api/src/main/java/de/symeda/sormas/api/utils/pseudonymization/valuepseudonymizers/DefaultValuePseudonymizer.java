/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.pseudonymization.ValuePseudonymizer;

public class DefaultValuePseudonymizer<T> extends ValuePseudonymizer<T> {

	private String stringValuePlaceholder = "";

	public DefaultValuePseudonymizer(String stringValuePlaceholder) {
		this.stringValuePlaceholder = stringValuePlaceholder;
	}

	@Override
	public T pseudonymizeValue(T value) {
		return getPseudonymizedValue(value);
	}

	@Override
	public boolean isValuePseudonymized(T value) {
		return DataHelper.equal(value, getPseudonymizedValue(value));
	}

	private T getPseudonymizedValue(T originalValue) {
		return originalValue instanceof String ? (T) stringValuePlaceholder : null;
	}
}
