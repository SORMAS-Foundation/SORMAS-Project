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

package de.symeda.sormas.ui.utils;

import java.util.function.Function;

import com.vaadin.ui.StyleGenerator;

import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;

public class FieldAccessColumnStyleGenerator<T> implements StyleGenerator<T> {

	private static final long serialVersionUID = -8348150203879621498L;

	public static <T extends Pseudonymizable> FieldAccessColumnStyleGenerator<T> getDefault(Class<T> beanType, String columnId) {

		return forFieldAccessCheckers(beanType, columnId, UiFieldAccessCheckers.getDefault(true));
	}

	public static <T extends Pseudonymizable> FieldAccessColumnStyleGenerator<T> forSensitiveData(Class<T> beanType, String columnId) {

		return forFieldAccessCheckers(beanType, columnId, UiFieldAccessCheckers.forSensitiveData(true));
	}

	private static <T extends Pseudonymizable> FieldAccessColumnStyleGenerator<T> forFieldAccessCheckers(
		Class<T> beanType,
		String columnId,
		UiFieldAccessCheckers<T> psuedonymizedDataFieldChecker) {

		return new FieldAccessColumnStyleGenerator<>(t -> {
			if (t.isPseudonymized()) {
				return psuedonymizedDataFieldChecker.isEmbedded(beanType, columnId)
					? psuedonymizedDataFieldChecker.hasRight()
					: psuedonymizedDataFieldChecker.isAccessible(beanType, columnId);
			}

			return true;
		});
	}

	private final Function<T, Boolean> accessCheck;

	public FieldAccessColumnStyleGenerator(Function<T, Boolean> accessCheck) {
		this.accessCheck = accessCheck;
	}

	@Override
	public String apply(T dto) {
		if (Boolean.FALSE.equals(accessCheck.apply(dto))) {
			return CssStyles.INACCESSIBLE_COLUMN;
		}

		return "";
	}
}
