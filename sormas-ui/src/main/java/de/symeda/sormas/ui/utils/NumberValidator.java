package de.symeda.sormas.ui.utils;

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

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.validator.AbstractValidator;

public class NumberValidator extends AbstractValidator<String> {

	private BigDecimal minValue;
	private BigDecimal maxValue;
	private boolean decimalAllowed;

	public NumberValidator(String errorMessage) {
		this(errorMessage, null, null, true);
	}

	public NumberValidator(String errorMessage, Number minValue, Number maxValue) {
		this(errorMessage, minValue, maxValue, true);
	}

	public NumberValidator(String errorMessage, Number minValue, Number maxValue, boolean decimalAllowed) {
		super(errorMessage);

		if (minValue != null) {
			this.minValue = new BigDecimal(minValue.toString());
		}

		if (maxValue != null) {
			this.maxValue = new BigDecimal(maxValue.toString());
		}

		this.decimalAllowed = decimalAllowed;
	}

	@Override
	protected boolean isValidValue(String number) {
		if (StringUtils.isBlank(number)) {
			return true;
		}

		Number parsedNumber;
		try {
			parsedNumber = Integer.valueOf(number);
		} catch (NumberFormatException ie) {
			try {
				parsedNumber = Long.valueOf(number);
			} catch (NumberFormatException le) {
				if (!decimalAllowed) {
					return false;
				}
				try {
					parsedNumber = Float.valueOf(number);
				} catch (NumberFormatException fe) {
					try {
						parsedNumber = Double.valueOf(number);
					} catch (NumberFormatException de) {
						return false;
					}
				}
			}
		}

		return validateRange(parsedNumber);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

	private boolean validateRange(Number number) {
		BigDecimal decimalNumber = new BigDecimal(number.toString());

		if (minValue != null && minValue.compareTo(decimalNumber) > 0) {
			return false;
		}

		if (maxValue != null && maxValue.compareTo(decimalNumber) < 0) {
			return false;
		}

		return true;
	}
}
