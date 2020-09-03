/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.region;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.InfrastructureAdo;

@Entity(name = Community.TABLE_NAME)
@DatabaseTable(tableName = Community.TABLE_NAME)
public class Community extends InfrastructureAdo {

	private static final long serialVersionUID = 1971053920357795693L;

	public static final String TABLE_NAME = "community";
	public static final String I18N_PREFIX = "Community";

	public static final String NAME = "name";
	public static final String DISTRICT = "district";

	@Column
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	private District district;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
