package de.symeda.sormas.app.component.validation;

import org.apache.commons.lang3.StringUtils;

import android.view.View;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.util.ResultCallback;

public class ValidationHelper {

	public static void initDateIntervalValidator(ControlDateField dateFromControl, ControlDateField dateUntilContol) {
		if (dateFromControl.getVisibility() == View.GONE || dateUntilContol.getVisibility() == View.GONE) {
			return;
		}

		ResultCallback<Boolean> dateFromCallback = () -> {
			if (dateFromControl.getValue() != null && dateUntilContol.getValue() != null) {
				if (dateFromControl.getValue().after(dateUntilContol.getValue())) {
					dateFromControl.enableErrorState(
						I18nProperties.getValidationError(Validations.beforeDate, dateFromControl.getCaption(), dateUntilContol.getCaption()));
					return true;
				}
			}

			return false;
		};

		ResultCallback<Boolean> dateUntilCallback = () -> {
			if (dateUntilContol.getValue() != null && dateFromControl.getValue() != null) {
				if (dateUntilContol.getValue().before(dateFromControl.getValue())) {
					dateUntilContol.enableErrorState(
						I18nProperties.getValidationError(Validations.afterDate, dateUntilContol.getCaption(), dateFromControl.getCaption()));
					return true;
				}
			}

			return false;
		};

		dateFromControl.setValidationCallback(dateFromCallback);
		dateUntilContol.setValidationCallback(dateUntilCallback);
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
}
