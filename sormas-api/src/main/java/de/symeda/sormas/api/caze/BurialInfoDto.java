/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.person.BurialConductor;
import de.symeda.sormas.api.utils.SensitiveData;

@AuditedClass
public class BurialInfoDto implements Serializable {

	private static final long serialVersionUID = -8353779195208414541L;
	@AuditInclude
	private Date burialDate;
	private BurialConductor burialConductor;
	@SensitiveData
	private String burialPlaceDescription;

	public BurialInfoDto(Date burialDate, BurialConductor burialConductor, String burialPlaceDescription) {

		this.burialDate = burialDate;
		this.burialConductor = burialConductor;
		this.burialPlaceDescription = burialPlaceDescription;
	}

	public Date getBurialDate() {
		return burialDate;
	}

	public BurialConductor getBurialConductor() {
		return burialConductor;
	}

	public String getBurialPlaceDescription() {
		return burialPlaceDescription;
	}
}
