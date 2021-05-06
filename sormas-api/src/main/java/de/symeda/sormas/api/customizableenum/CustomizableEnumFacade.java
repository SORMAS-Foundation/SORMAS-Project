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

package de.symeda.sormas.api.customizableenum;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;

@Remote
public interface CustomizableEnumFacade {

	List<CustomizableEnumValueDto> getAllAfter(Date date);

	List<CustomizableEnumValueDto> getByUuids(List<String> uuids);

	List<String> getAllUuids();

	<T extends CustomizableEnum> T getEnumValue(CustomizableEnumType type, String value);

	<T extends CustomizableEnum> List<T> getEnumValues(CustomizableEnumType type, Disease disease);

	void loadData();

}
