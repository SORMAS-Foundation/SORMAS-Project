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

package de.symeda.sormas.backend.sormastosormas;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;

public class ShareData<T extends SormasToSormasDto> {

	private final T dto;

	private List<AssociatedEntityWrapper<?>> associatedEntities;

	public ShareData(T dto) {
		this.dto = dto;
		this.associatedEntities = new ArrayList<>();
	}

	public T getDto() {
		return dto;
	}

	public void addAssociatedEntities(List<AssociatedEntityWrapper<?>> entities) {
		this.associatedEntities.addAll(entities);
	}

	public List<AssociatedEntityWrapper<?>> getAssociatedEntities() {
		return associatedEntities;
	}
}
