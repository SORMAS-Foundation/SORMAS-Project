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

package de.symeda.sormas.app.person.edit;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.app.databinding.FragmentPersonEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

final class PersonValidator {

	static void initializePersonValidation(final FragmentPersonEditLayoutBinding contentBinding) {
		ResultCallback<Boolean> deathDateCallback = () -> {
			Date birthDate = PersonEditFragment.calculateBirthDateValue(contentBinding);
			if (contentBinding.personDeathDate.getValue() != null && birthDate != null) {
				if (DateTimeComparator.getDateOnlyInstance().compare(contentBinding.personDeathDate.getValue(), birthDate) < 0) {
					contentBinding.personDeathDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.afterDate,
							contentBinding.personDeathDate.getCaption(),
							contentBinding.personBirthdateLabel.getText()));
					return true;
				}
			}

			return false;
		};

		ResultCallback<Boolean> burialDateCallback = () -> {
			if (contentBinding.personBurialDate.getValue() != null && contentBinding.personDeathDate.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance()
					.compare(contentBinding.personBurialDate.getValue(), contentBinding.personDeathDate.getValue())
					< 0) {
					contentBinding.personBurialDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.afterDate,
							contentBinding.personBurialDate.getCaption(),
							contentBinding.personDeathDate.getCaption()));
					return true;
				}
			}

			return false;
		};

		ResultCallback<Boolean> approximateAgeCallback = () -> {
			if (ApproximateAgeType.YEARS.equals(contentBinding.personApproximateAgeType.getValue())
				&& !StringUtils.isEmpty(contentBinding.personApproximateAge.getValue())
				&& Integer.valueOf(contentBinding.personApproximateAge.getValue()) >= 150) {
				contentBinding.personApproximateAge.enableErrorState(I18nProperties.getValidationError(Validations.softApproximateAgeTooHigh));
				return true;
			}

			return false;
		};

		contentBinding.personDeathDate.setValidationCallback(deathDateCallback);
		contentBinding.personBurialDate.setValidationCallback(burialDateCallback);
		contentBinding.personApproximateAge.setValidationCallback(approximateAgeCallback);
	}
}
