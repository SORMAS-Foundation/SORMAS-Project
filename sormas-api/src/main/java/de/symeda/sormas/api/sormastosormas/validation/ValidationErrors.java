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

package de.symeda.sormas.api.sormastosormas.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Collect all ValidationErrors of a single S2S entity
 */
public class ValidationErrors implements Serializable {

	private static final long serialVersionUID = 1635651082132555214L;

	private ValidationErrorGroup group;

	private List<ValidationErrorGroup> subGroups = new ArrayList<>();

	public ValidationErrors() {
	}

	public ValidationErrors(ValidationErrorGroup group) {
		this.group = group;
	}

	public ValidationErrors(ValidationErrorGroup group, List<ValidationErrorGroup> subGroups) {
		this.group = group;
		this.subGroups = subGroups;
	}

	public ValidationErrors(ValidationErrorGroup group, ValidationErrors errors) {
		this.group = group;
		addAll(errors);
	}

	public void add(ValidationErrorGroup subGroup, ValidationErrorMessage message) {

		ValidationErrorGroup subGroupToAdd;

		if (subGroups.contains(subGroup)) {
			subGroupToAdd = subGroups.get(subGroups.indexOf(subGroup));
		} else {
			subGroupToAdd = new ValidationErrorGroup(subGroup.getI18nTag(), subGroup.getUuid());
			subGroups.add(subGroupToAdd);
		}

		subGroupToAdd.getMessages().add(message);
	}

	public void addAll(ValidationErrors errors) {
		for (ValidationErrorGroup subGroup : errors.getSubGroups()) {
			for (ValidationErrorMessage message : subGroup.getMessages()) {
				add(subGroup, message);
			}
		}
	}

	public ValidationErrorGroup getGroup() {
		return group;
	}

	public void setGroup(ValidationErrorGroup group) {
		this.group = group;
	}

	public List<ValidationErrorGroup> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(List<ValidationErrorGroup> subGroups) {
		this.subGroups = subGroups;
	}

	public boolean hasError() {
		return !subGroups.isEmpty();
	}

	public static ValidationErrors create(ValidationErrorGroup group, ValidationErrorMessage message) {
		ValidationErrors errors = new ValidationErrors();

		errors.add(group, message);

		return errors;
	}
}
