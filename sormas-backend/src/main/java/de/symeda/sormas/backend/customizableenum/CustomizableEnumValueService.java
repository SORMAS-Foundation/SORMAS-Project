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

package de.symeda.sormas.backend.customizableenum;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.customizableenum.CustomizableEnumCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class CustomizableEnumValueService extends AdoServiceWithUserFilterAndJurisdiction<CustomizableEnumValue> {

	public CustomizableEnumValueService() {
		super(CustomizableEnumValue.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, CustomizableEnumValue> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(CustomizableEnumCriteria criteria, CriteriaBuilder cb, Root<CustomizableEnumValue> from) {

		Predicate filter = cb.equal(from.get(CustomizableEnumValue.ACTIVE), criteria.getActive());

		if (criteria.getFreeTextFilter() != null) {
			String[] textFilters = criteria.getFreeTextFilter().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(CustomizableEnumValue.VALUE), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(CustomizableEnumValue.CAPTION), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getDataType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(CustomizableEnumValue.DATA_TYPE), criteria.getDataType()));
		}
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.like(from.get(CustomizableEnumValue.DISEASES).as(String.class), "%" + criteria.getDisease().name() + '%'));
		}

		return filter;
	}

}
