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

import java.util.function.Supplier;

import com.vaadin.v7.data.validator.AbstractValidator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.person.ApproximateAgeType;

@SuppressWarnings("serial")
public class ApproximateAgeValidator extends AbstractValidator<Integer> {

	private TextField ageField;
	private Supplier<ApproximateAgeType> ageTypeSupplier;

	public ApproximateAgeValidator(TextField ageField, Supplier<ApproximateAgeType> ageTypeSupplier, String errorMessage) {
		super(errorMessage);
		this.ageField = ageField;
		this.ageTypeSupplier = ageTypeSupplier;
	}

	public ApproximateAgeValidator(TextField ageField, ComboBox ageTypeField, String errorMessage) {
		this(ageField, () -> (ApproximateAgeType) ageTypeField.getValue(), errorMessage);
	}

	@Override
	protected boolean isValidValue(Integer age) {
		ApproximateAgeType ageType = ageTypeSupplier.get();

		if (!ApproximateAgeType.YEARS.equals(ageType) || age == null) {
			return true;
		} else {
			return age <= 150;
		}
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
}
