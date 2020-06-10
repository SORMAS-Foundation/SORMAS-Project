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
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.sormas.api.HasUuid;

@Audited
public class AnonymizedEntity implements HasUuid {

	public static final String ANONYMIZING = "xxx";

	public static final String PWD = "pwd";

	private final String uuid;
	private final String pwd;

	public AnonymizedEntity(String uuid, String pwd) {
		this.uuid = uuid;
		this.pwd = pwd;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@AuditedAttribute(anonymous = true, anonymizingString = ANONYMIZING)
	public String getPwd() {
		return pwd;
	}
}
