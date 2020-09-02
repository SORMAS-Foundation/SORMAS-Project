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
package de.symeda.auditlog.api.sample;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedIgnore;
import de.symeda.sormas.api.HasUuid;

@Audited
public class Entity implements HasUuid {

	public static final String STRING = "string";
	public static final String INTEGER = "integer";
	public static final String FLAG = "flag";

	private final String uuid;

	private int integer;
	private Boolean flag;
	private String string;

	public Entity(String uuid, Boolean flag, String string, int integer) {

		this.uuid = uuid;

		this.setFlag(flag);
		this.setString(string);
		this.setInteger(integer);
	}

	@Override
	@AuditedIgnore
	public String getUuid() {
		return uuid;

	}

	public Boolean getFlag() {
		return flag;
	}

	public String getString() {
		return string;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public void setString(String string) {
		this.string = string;
	}
}
