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

package de.symeda.sormas.app.backend.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.j256.ormlite.table.DatabaseTable;

@Entity(name = Config.TABLE_NAME)
@DatabaseTable(tableName = Config.TABLE_NAME)
public class Config {

	public static final String TABLE_NAME = "config";

	public static final String KEY = "key";
	public static final String VALUE = "value";

	@Id
	private String key;

	@Column(nullable = false)
	private String value;

	public Config() {
	}

	public Config(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
