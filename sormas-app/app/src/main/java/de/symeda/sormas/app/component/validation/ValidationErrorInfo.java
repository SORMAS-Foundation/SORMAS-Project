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

package de.symeda.sormas.app.component.validation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;

public class ValidationErrorInfo {

	private Context context;
	private List<ControlPropertyEditField> fieldsWithError;

	public ValidationErrorInfo(Context context) {
		this.context = context;
		fieldsWithError = new ArrayList<>();
	}

	public void addFieldWithError(ControlPropertyEditField field) {
		fieldsWithError.add(field);
	}

	public boolean hasError() {
		return !fieldsWithError.isEmpty();
	}

	@Override
	public String toString() {
		if (fieldsWithError.isEmpty()) {
			return null;
		}

		StringBuilder errorStringBuilder = new StringBuilder();
		Resources resources = context.getResources();

		errorStringBuilder.append(resources.getString(R.string.validation_error_info_pre_text)).append(" ");

		for (ControlPropertyEditField field : fieldsWithError) {
			errorStringBuilder.append(field.getCaption());
			if (fieldsWithError.indexOf(field) == fieldsWithError.size() - 2) {
				errorStringBuilder.append(" ").append(resources.getString(R.string.and)).append(" ");
			} else if (fieldsWithError.indexOf(field) != fieldsWithError.size() - 1) {
				errorStringBuilder.append(", ");
			} else {
				errorStringBuilder.append(".");
			}
		}

		errorStringBuilder.append("\n\n").append(resources.getString(R.string.validation_error_info_post_text));

		return errorStringBuilder.toString();
	}
}
