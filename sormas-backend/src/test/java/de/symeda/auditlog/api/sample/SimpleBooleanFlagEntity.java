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
public class SimpleBooleanFlagEntity implements HasUuid {

	public static final String FLAG = "flag";

	private final String uuid;
	private final Boolean flag;

	public SimpleBooleanFlagEntity(String uuid, Boolean flag) {

		this.uuid = uuid;

		this.flag = flag;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@AuditedAttribute(DemoBooleanFormatter.class)
	public Boolean getFlag() {
		return flag;
	}
}
