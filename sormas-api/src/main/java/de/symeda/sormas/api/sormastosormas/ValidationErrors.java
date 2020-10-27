/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationErrors implements Serializable {

	private static final long serialVersionUID = 1635651082132555214L;

	private final Map<String, List<String>> errors;

	public ValidationErrors() {
		errors = new HashMap<>();
	}

	public void add(String group, String message) {
		List<String> groupErrors;

		if (errors.containsKey(group)) {
			groupErrors = errors.get(group);
		} else {
			groupErrors = new ArrayList<>();
			errors.put(group, groupErrors);
		}

		groupErrors.add(message);
	}

	public void addAll(ValidationErrors errors) {
		for (Map.Entry<String, List<String>> error : errors.errors.entrySet()) {
			for (String message : error.getValue()) {
				add(error.getKey(), message);
			}
		}
	}

	public Map<String, List<String>> getErrors() {
		return errors;
	}

	public boolean hasError() {
		return errors.size() > 0;
	}

	public static ValidationErrors create(String group, String message) {
		ValidationErrors errors = new ValidationErrors();

		errors.add(group, message);

		return errors;
	}
}
