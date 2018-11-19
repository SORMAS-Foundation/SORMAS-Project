/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.api.person;

import de.symeda.sormas.api.ReferenceDto;

public class PersonReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public PersonReferenceDto() {
		
	}
	
	public PersonReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public PersonReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
	public PersonReferenceDto(String uuid, String firstname, String lastName) {
		setUuid(uuid);
		setCaption(PersonDto.buildCaption(firstname, lastName));
	}
	
}
