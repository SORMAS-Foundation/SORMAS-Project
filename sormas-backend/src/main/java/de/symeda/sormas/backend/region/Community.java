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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.common.InfrastructureAdo;

@Entity
public class Community extends InfrastructureAdo {

	private static final long serialVersionUID = 1971053920357795693L;

	public static final String TABLE_NAME = "community";

	public static final String NAME = "name";
	public static final String DISTRICT = "district";
	public static final String EXTERNAL_ID = "externalID";

	private String name;
	private District district;
	private String externalID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	@Override
	public String toString() {
		return getName();
	}
}
