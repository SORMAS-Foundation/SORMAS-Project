/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils.fieldvisibility;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldVisibilityCheckers {
	private List<Checker> checkers = new ArrayList<>();
	private List<FieldBasedChecker> fieldBasedCheckers = new ArrayList<>();

	public FieldVisibilityCheckers() {
	}

	public boolean isVisible(Class<?> parentType, String propertyId) {
		for (Checker checker : checkers) {
			if(!checker.isVisible(parentType, propertyId)){
				return false;
			}
		}

		Field declaredField = getDeclaredField(parentType, propertyId);

		if(declaredField == null){
			return true;
		}

		for (FieldBasedChecker checker : fieldBasedCheckers) {
			if(!checker.isVisible(declaredField)){
				return false;
			}
		}

		return true;
	}

	public FieldVisibilityCheckers add(Checker checker){
		this.checkers.add(checker);

		return this;
	}

	public FieldVisibilityCheckers add(FieldBasedChecker checker){
		this.fieldBasedCheckers.add(checker);

		return this;
	}

	private Field getDeclaredField(Class<?> parentType, String propertyId) {
		try {
			return parentType.getDeclaredField(propertyId);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public interface Checker {
		boolean isVisible(Class<?> parentType, String propertyId);
	}

	public interface FieldBasedChecker {
		boolean isVisible(Field field);
	}
}
