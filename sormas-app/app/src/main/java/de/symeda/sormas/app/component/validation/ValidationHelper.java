package de.symeda.sormas.app.component.validation;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import android.view.View;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.util.ResultCallback;

public class ValidationHelper {

	public static void initDateIntervalValidator(ControlPropertyEditField<Date> dateFromControl, ControlPropertyEditField<Date> dateUntilControl) {
		initDateIntervalValidator(dateFromControl, dateUntilControl, true);
	}

	public static void initDateIntervalValidator(
		ControlPropertyEditField<Date> dateFromControl,
		ControlPropertyEditField<Date> dateUntilControl,
		boolean doNotSetForHiddenFields) {
		if (doNotSetForHiddenFields && (dateFromControl.getVisibility() == View.GONE || dateUntilControl.getVisibility() == View.GONE)) {
			return;
		}

		ResultCallback<Boolean> dateFromUntilCallback = () -> {
			Date dateFromValue = (Date) dateFromControl.getValue();
			Date dateUntilValue = (Date) dateUntilControl.getValue();
			if (dateFromValue != null && dateUntilValue != null) {
				if (DateHelper.getStartOfDay(dateFromValue).after(DateHelper.getStartOfDay(dateUntilValue))) {
					dateFromControl.enableErrorState(
						I18nProperties.getValidationError(Validations.beforeDate, dateFromControl.getCaption(), dateUntilControl.getCaption()));
					dateUntilControl.enableErrorState(
						I18nProperties.getValidationError(Validations.afterDate, dateUntilControl.getCaption(), dateFromControl.getCaption()));
					return true;
				} else {
					dateFromControl.disableErrorState();
					dateUntilControl.disableErrorState();
				}
			}

			return false;
		};

		dateFromControl.setValidationCallback(dateFromUntilCallback);
		dateUntilControl.setValidationCallback(dateFromUntilCallback);
	}

	public static void initIntegerValidator(ControlTextEditField textEditField, String errorMessage, int min, int max) {
		textEditField.setValidationCallback(() -> {
			String vaccinationDosesValue = textEditField.getValue();
			if (!StringUtils.isEmpty(vaccinationDosesValue)) {
				try {
					int intValue = Integer.parseInt(vaccinationDosesValue);
					if (intValue < min || intValue > max) {
						textEditField.enableErrorState(errorMessage);

						return true;
					}
				} catch (NumberFormatException e) {
					textEditField.enableErrorState(errorMessage);

					return true;
				}
			}

			return false;
		});
	}

	public static void initEmailValidator(ControlTextEditField textEditField) {
		textEditField.setValidationCallback(() -> {
			String emailAddress = textEditField.getValue();
			if (!DataHelper.isValidEmailAddress(emailAddress)) {
				textEditField.enableErrorState(I18nProperties.getValidationError(Validations.validEmailAddress, textEditField.getCaption()));

				return true;
			}

			return false;
		});
	}

	public static void initPhoneNumberValidator(ControlTextEditField textEditField) {
		textEditField.setValidationCallback(() -> {
			String phoneNumber = textEditField.getValue();
			if (!DataHelper.isValidPhoneNumber(phoneNumber)) {
				textEditField.enableErrorState(I18nProperties.getValidationError(Validations.validPhoneNumber, textEditField.getCaption()));

				return true;
			}

			return false;
		});
	}

	public static void resetValidator(ControlTextEditField textEditField) {
		textEditField.disableErrorState();
		textEditField.setValidationCallback(null);
	}

	public static boolean validateLatitude(Double latitude, ControlPropertyEditField<?> fieldToValidate){
		if (latitude != null) {
			boolean hasError = latitude < -90 || latitude > 90;
			if (hasError) {
				fieldToValidate.enableErrorState(I18nProperties.getValidationError(Validations.latitudeBetween));
			}

			return hasError;
		}

		return false;
	}

    public static boolean validateLongitude(Double longitude, ControlPropertyEditField<?> fieldToValidate){
        if (longitude != null) {
            boolean hasError = longitude < -180 || longitude > 180;
            if (hasError) {
                fieldToValidate.enableErrorState(I18nProperties.getValidationError(Validations.longitudeBetween));
            }

            return hasError;
        }

        return false;
    }
}
