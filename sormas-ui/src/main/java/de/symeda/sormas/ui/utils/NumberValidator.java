/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.math.BigDecimal;

import com.vaadin.ui.Notification;
import com.vaadin.v7.data.validator.AbstractValidator;

public class NumberValidator extends AbstractValidator<Number> {

	private BigDecimal minValue;
	private BigDecimal maxValue;

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
	}

	@Override
	protected boolean isValidValue(Number number) {
		if (number == null) {
			return true;
		}

		return validateRange(number);
	}

	@Override
	public Class<Number> getType() {
		return Number.class;
	}

	private boolean validateRange(Number number) {
		BigDecimal decimalNumber = new BigDecimal(number.toString());

		if (minValue != null && minValue.compareTo(decimalNumber) > 0) {
			return false;
		}

		if (maxValue != null && maxValue.compareTo(decimalNumber) < 0) {
			return false;
		}
		System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" +getErrorMessage());
		Notification.show(getErrorMessage());
		
		return false;
	}
}
