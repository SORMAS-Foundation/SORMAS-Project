/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.edit;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.databinding.FragmentEnvironmentSampleEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public class EnvironmentSampleValidator {

	static void initializeEnvironmentSampleValidation(
		final FragmentEnvironmentSampleEditLayoutBinding contentBinding,
		Callable<Location> locationCallback) {

		initializeSampleAndDispatchDateValidation(contentBinding);
		initializeMeasurementValidations(contentBinding);
		initializeLocationValidations(contentBinding, locationCallback);
	}

	private static void initializeSampleAndDispatchDateValidation(final FragmentEnvironmentSampleEditLayoutBinding contentBinding) {

		ResultCallback<Boolean> sampleDateCallback = () -> {
			// Must not be after date of shipment
			if (DateHelper
				.isDateAfter(contentBinding.environmentSampleSampleDateTime.getValue(), contentBinding.environmentSampleDispatchDate.getValue())) {
				contentBinding.environmentSampleSampleDateTime.enableErrorState(
					I18nProperties.getValidationError(
						Validations.beforeDate,
						contentBinding.environmentSampleSampleDateTime.getCaption(),
						contentBinding.environmentSampleDispatchDate.getCaption()));
				return true;
			}

			return false;
		};

		ResultCallback<Boolean> dispatchDateCallback = () -> {
			// Must not be before sample date
			if (DateHelper
				.isDateBefore(contentBinding.environmentSampleDispatchDate.getValue(), contentBinding.environmentSampleSampleDateTime.getValue())) {
				contentBinding.environmentSampleDispatchDate.enableErrorState(
					I18nProperties.getValidationError(
						Validations.afterDate,
						contentBinding.environmentSampleDispatchDate.getCaption(),
						contentBinding.environmentSampleSampleDateTime.getCaption()));
				return true;
			}

			return false;
		};

		contentBinding.environmentSampleSampleDateTime.setValidationCallback(sampleDateCallback);
		contentBinding.environmentSampleDispatchDate.setValidationCallback(dispatchDateCallback);
	}

	private static void initializeMeasurementValidations(final FragmentEnvironmentSampleEditLayoutBinding contentBinding) {

		ResultCallback<Boolean> sampleVolumeCallback = () -> {
			if (StringUtils.isNotBlank(contentBinding.environmentSampleSampleVolume.getValue())
				&& Float.parseFloat(contentBinding.environmentSampleSampleVolume.getValue()) < 0) {
				contentBinding.environmentSampleSampleVolume.enableErrorState(I18nProperties.getValidationError(Validations.numberTooSmall, 0));
				return true;
			}
			return false;
		};

		ResultCallback<Boolean> turbidityCallback = () -> {
			if (StringUtils.isNotBlank(contentBinding.environmentSampleTurbidity.getValue())
				&& Float.parseFloat(contentBinding.environmentSampleTurbidity.getValue()) < 0) {
				contentBinding.environmentSampleTurbidity.enableErrorState(I18nProperties.getValidationError(Validations.numberTooSmall, 0));
				return true;
			}
			return false;
		};

		ResultCallback<Boolean> chlorineResidualsCallback = () -> {
			if (StringUtils.isNotBlank(contentBinding.environmentSampleChlorineResiduals.getValue())
				&& Float.parseFloat(contentBinding.environmentSampleChlorineResiduals.getValue()) < 0) {
				contentBinding.environmentSampleChlorineResiduals.enableErrorState(I18nProperties.getValidationError(Validations.numberTooSmall, 0));
				return true;
			}
			return false;
		};

		ResultCallback<Boolean> phValueCallback = () -> {
			if (StringUtils.isNotBlank(contentBinding.environmentSamplePhValue.getValue())
				&& (Float.parseFloat(contentBinding.environmentSamplePhValue.getValue()) < 0
					|| Float.parseFloat(contentBinding.environmentSamplePhValue.getValue()) > 14)) {
				contentBinding.environmentSamplePhValue.enableErrorState(I18nProperties.getValidationError(Validations.numberNotInRange, 0, 14));
				return true;
			}
			return false;
		};

		contentBinding.environmentSampleSampleVolume.setValidationCallback(sampleVolumeCallback);
		contentBinding.environmentSampleTurbidity.setValidationCallback(turbidityCallback);
		contentBinding.environmentSampleChlorineResiduals.setValidationCallback(chlorineResidualsCallback);
		contentBinding.environmentSamplePhValue.setValidationCallback(phValueCallback);
	}

	private static void initializeLocationValidations(
		final FragmentEnvironmentSampleEditLayoutBinding contentBinding,
		Callable<Location> locationCallback) {

		contentBinding.environmentSampleLocation.setValidationCallback(() -> {
			try {
				Location location = locationCallback.call();
				if (location.getRegion() == null) {
					contentBinding.environmentSampleLocation.enableErrorState(I18nProperties.getValidationError(Validations.validRegion));
					return true;
				}
				if (location.getDistrict() == null) {
					contentBinding.environmentSampleLocation.enableErrorState(I18nProperties.getValidationError(Validations.validDistrict));
					return true;
				}
				if (location.getLatitude() == null || location.getLongitude() == null) {
					contentBinding.environmentSampleLocation.enableErrorState(I18nProperties.getValidationError(Validations.gpsCoordinatesRequired));
					return true;
				}
				return false;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
