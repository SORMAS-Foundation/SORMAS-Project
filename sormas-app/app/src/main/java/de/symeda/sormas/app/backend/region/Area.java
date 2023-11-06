/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;

@Entity(name = Area.TABLE_NAME)
@DatabaseTable(tableName = Area.TABLE_NAME)
public class Area extends InfrastructureAdo {

	public static final String TABLE_NAME = "area";
	public static final String I18N_PREFIX = "Area";

	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	@Column
	private String name;
	@Column
	private String externalId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public String buildCaption() {
		return getName();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
