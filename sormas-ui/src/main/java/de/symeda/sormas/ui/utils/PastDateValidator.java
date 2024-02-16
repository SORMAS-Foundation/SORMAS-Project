/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.v7.data.validator.AbstractValidator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

public class PastDateValidator extends AbstractValidator<Date> {

	public PastDateValidator(String caption) {
		super(I18nProperties.getValidationError(Validations.pastDate, caption));
	}

	@Override
	protected boolean isValidValue(Date value) {
		return value == null || value.after(new Date());
	}

	@Override
	public Class<Date> getType() {
		return Date.class;
	}
}
