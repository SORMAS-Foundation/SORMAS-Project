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

@Entity(name = District.TABLE_NAME)
@DatabaseTable(tableName = District.TABLE_NAME)
public class District extends InfrastructureAdo {

	private static final long serialVersionUID = -6057113756091470463L;

	public static final String TABLE_NAME = "district";
	public static final String I18N_PREFIX = "District";

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String REGION = "region";

	@Column
	private String name;

	@Column
	private String epidCode;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false, maxForeignAutoRefreshLevel = 3)
	@ManyToOne(cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(nullable = false)
	private Region region;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEpidCode() {
		return epidCode;
	}

	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
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
