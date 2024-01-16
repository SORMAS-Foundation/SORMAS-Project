/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.customizableenum;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class CustomizableEnumCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 6254688396874544620L;

	private String freeTextFilter;
	private CustomizableEnumType dataType;
	private Disease disease;
	private Boolean active = true;

	public String getFreeTextFilter() {
		return freeTextFilter;
	}

	public CustomizableEnumCriteria freeTextFilter(String freeTextFilter) {
		this.freeTextFilter = freeTextFilter;
		return this;
	}

	public CustomizableEnumType getDataType() {
		return dataType;
	}

	public CustomizableEnumCriteria dataType(CustomizableEnumType dataType) {
		this.dataType = dataType;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public CustomizableEnumCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public CustomizableEnumCriteria active(Boolean active) {
		this.active = active;
		return this;
	}

}
