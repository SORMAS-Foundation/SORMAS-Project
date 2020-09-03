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

import javax.persistence.Column;
import javax.persistence.Entity;

import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.app.backend.common.InfrastructureAdo;

@Entity(name = Region.TABLE_NAME)
@DatabaseTable(tableName = Region.TABLE_NAME)
public class Region extends InfrastructureAdo {

	private static final long serialVersionUID = -2958216667876104358L;

	public static final String TABLE_NAME = "region";
	public static final String I18N_PREFIX = "Region";

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";

	@Column
	private String name;

	@Column
	private String epidCode;

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

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
