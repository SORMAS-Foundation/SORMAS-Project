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

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.UserError;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;

public class ValidationUtils {

	private ValidationUtils() {
	}

	public static void initComponentErrorValidator(
		TextField field,
		String initialFieldValue,
		String validationMessageTag,
		Label warningLabel,
		Function<String, Boolean> exists) {
		if (field == null) {
			return;
		}

		Function<String, Void> validateExternalToken = (String value) -> {
			if (field.isVisible() && !StringUtils.isEmpty(value) && exists.apply(value)) {
				field.setComponentError(new UserError(I18nProperties.getValidationError(validationMessageTag)));
				warningLabel.setVisible(true);
			} else {
				field.setComponentError(null);
				warningLabel.setVisible(false);
			}

			return null;
		};

		validateExternalToken.apply(initialFieldValue);
		field.addValueChangeListener(f -> validateExternalToken.apply((String) f.getProperty().getValue()));
	}
}
