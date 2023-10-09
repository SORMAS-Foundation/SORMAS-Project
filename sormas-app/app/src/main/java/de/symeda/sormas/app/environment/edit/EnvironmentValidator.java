package de.symeda.sormas.app.environment.edit;

import java.util.concurrent.Callable;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.controls.ControlTextPopupField;

public class EnvironmentValidator {

	static void initializeLocationValidations(final ControlTextPopupField locationField, Callable<Location> locationCallback) {

		locationField.setValidationCallback(() -> {
			try {
				Location location = locationCallback.call();
				if (location.getRegion() == null) {
					locationField.enableErrorState(I18nProperties.getValidationError(Validations.validRegion));
					return true;
				}
				if (location.getDistrict() == null) {
					locationField.enableErrorState(I18nProperties.getValidationError(Validations.validDistrict));
					return true;
				}
				if (location.getLatitude() == null || location.getLongitude() == null) {
					locationField.enableErrorState(I18nProperties.getValidationError(Validations.gpsCoordinatesRequired));
					return true;
				}

				return false;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
