/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class SormasGrid extends Grid {

	public void setSormasColumns(Class<?> clazz, List<String> propertyIds) {

		FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale());
		super.setColumns(
			propertyIds.stream().filter(propertyId -> fieldVisibilityCheckers.isVisible(clazz, propertyId)).collect(Collectors.toList()).toArray());
	}

	public void setSormasColumns(List<SormasColumn> columns) {

		FieldVisibilityCheckers fieldVisibilityCheckers = FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale());
		super.setColumns(
			columns.stream()
				.filter(column -> fieldVisibilityCheckers.isVisible(column.getClazz(), column.getPropertyId()))
				.collect(Collectors.toList())
				.toArray());
	}

	public static class SormasColumn {

		private Class<?> clazz;
		private String propertyId;

		public SormasColumn(Class<?> clazz, String propertyId) {
			this.clazz = clazz;
			this.propertyId = propertyId;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public String getPropertyId() {
			return propertyId;
		}
	}
}
