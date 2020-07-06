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

import com.vaadin.v7.data.validator.AbstractValidator;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class NumberValidator extends AbstractValidator<String> {

	public NumberValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(String number) {
		if (StringUtils.isBlank(number)) {
			return true;
		}

		try {
			Integer.valueOf(number);
		} catch (NumberFormatException ie) {
			try {
				Long.valueOf(number);
			} catch (NumberFormatException le) {
				try {
					Float.valueOf(number);
				} catch (NumberFormatException fe) {
					try {
						Double.valueOf(number);
					} catch (NumberFormatException de) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}
}
