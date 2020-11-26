package de.symeda.sormas.app.component.validation;

import org.joda.time.DateTimeComparator;

import android.view.View;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.util.ResultCallback;

public class ValidationHelper {

	public static void initDateIntervalValidator(ControlDateField dateFromControl, ControlDateField dateUntilContol) {
		if (dateFromControl.getVisibility() == View.GONE || dateUntilContol.getVisibility() == View.GONE) {
			return;
		}

		ResultCallback<Boolean> dateFromCallback = () -> {
			if (dateFromControl.getValue() != null && dateUntilContol.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance().compare(dateFromControl.getValue(), dateUntilContol.getValue()) > 0) {
					dateFromControl.enableErrorState(
						I18nProperties.getValidationError(Validations.beforeDate, dateFromControl.getCaption(), dateUntilContol.getCaption()));
					return true;
				}
			}

			return false;
		};

		ResultCallback<Boolean> dateUntilCallback = () -> {
			if (dateUntilContol.getValue() != null && dateFromControl.getValue() != null) {
				if (DateTimeComparator.getDateOnlyInstance().compare(dateUntilContol.getValue(), dateFromControl.getValue()) < 0) {
					dateUntilContol.enableErrorState(
						I18nProperties.getValidationError(Validations.beforeDate, dateUntilContol.getCaption(), dateFromControl.getCaption()));
					return true;
				}
			}

			return false;
		};

		dateFromControl.setValidationCallback(dateFromCallback);
		dateUntilContol.setValidationCallback(dateUntilCallback);
	}
}
