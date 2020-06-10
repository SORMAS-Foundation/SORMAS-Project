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

package de.symeda.sormas.app.sample.edit;

import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public class SampleValidator {

	static void initializeSampleValidation(final FragmentSampleEditLayoutBinding contentBinding) {
		ResultCallback<Boolean> sampleDateCallback = () -> {
			// Must not be after date of shipment
			if (contentBinding.sampleSampleDateTime.getValue() != null && contentBinding.sampleShipmentDate.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance()
					.compare(contentBinding.sampleSampleDateTime.getValue(), contentBinding.sampleShipmentDate.getValue())
					> 0) {
					contentBinding.sampleSampleDateTime.enableErrorState(
						I18nProperties.getValidationError(
							Validations.beforeDate,
							contentBinding.sampleSampleDateTime.getCaption(),
							contentBinding.sampleShipmentDate.getCaption()));
					return true;
				}
			}

			return false;
		};

		ResultCallback<Boolean> shipmentDateCallback = () -> {
			// Must not be before sample date
			if (contentBinding.sampleShipmentDate.getValue() != null && contentBinding.sampleSampleDateTime.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance()
					.compare(contentBinding.sampleShipmentDate.getValue(), contentBinding.sampleSampleDateTime.getValue())
					< 0) {
					contentBinding.sampleShipmentDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.afterDate,
							contentBinding.sampleShipmentDate.getCaption(),
							contentBinding.sampleSampleDateTime.getCaption()));
					return true;
				}
			}

			return false;
		};

		contentBinding.sampleSampleDateTime.setValidationCallback(sampleDateCallback);
		contentBinding.sampleShipmentDate.setValidationCallback(shipmentDateCallback);
	}
}
