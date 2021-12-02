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

package de.symeda.sormas.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;

import de.symeda.sormas.api.i18n.I18nProperties;

public class ConstrainValidationHelper {

	public static Map<List<String>, String> getPropertyErrors(Set<? extends ConstraintViolation> constraintViolations) {
		Map<List<String>, String> errors = new HashMap<>();

		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			List<String> path = new ArrayList<>();

			for (Path.Node propertyNode : constraintViolation.getPropertyPath()) {
				if (propertyNode.getKind() == ElementKind.PROPERTY) {
					if (propertyNode.getIndex() != null && path.size() > 0) {
						int pathSize = path.size();
						path.set(pathSize - 1, path.get(pathSize - 1) + "[" + propertyNode.getIndex() + "]");
					}

					path.add(propertyNode.getName());
				}
			}

			errors.put(
				path,
				I18nProperties.getValidationError(constraintViolation.getMessage(), constraintViolation.getConstraintDescriptor().getAttributes()));
		}

		return errors;
	}
}
