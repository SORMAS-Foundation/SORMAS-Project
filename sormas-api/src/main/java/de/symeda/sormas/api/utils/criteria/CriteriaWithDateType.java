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

package de.symeda.sormas.api.utils.criteria;

import java.util.List;

public abstract class CriteriaWithDateType extends BaseCriteria {

	private static final long serialVersionUID = -1426212155238381380L;

	private final Class<? extends CriteriaDateType> dateTypeCalss;

	public CriteriaWithDateType(Class<? extends CriteriaDateType> dateTypeCalss) {
		this.dateTypeCalss = dateTypeCalss;
	}

	@Override
	protected Object parseUrlParam(Class<?> type, List<String> fieldParams) throws InstantiationException, IllegalAccessException {
		if (CriteriaDateType.class.isAssignableFrom(type)) {
			return CriteriaDateTypeHelper.valueOf(dateTypeCalss, fieldParams.get(0));
		}

		return super.parseUrlParam(type, fieldParams);
	}
}
