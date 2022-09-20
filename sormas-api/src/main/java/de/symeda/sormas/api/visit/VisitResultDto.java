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

package de.symeda.sormas.api.visit;

import java.io.Serializable;

import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class VisitResultDto implements Serializable {

	public VisitResultDto() {
	}

	public VisitResultDto(VisitOrigin origin, VisitResult result) {
		this.origin = origin;
		this.result = result;
	}

	@AuditInclude
	private VisitOrigin origin;
	private VisitResult result;

	public VisitOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(VisitOrigin origin) {
		this.origin = origin;
	}

	public VisitResult getResult() {
		return result;
	}

	public void setResult(VisitResult result) {
		this.result = result;
	}

	public String toString() {
		return result.toString();
	}
}
