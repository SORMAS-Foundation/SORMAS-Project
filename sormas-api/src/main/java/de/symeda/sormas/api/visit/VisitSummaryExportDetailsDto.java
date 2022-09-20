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
import java.util.Date;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class VisitSummaryExportDetailsDto implements Serializable {

	private static final long serialVersionUID = -4677902897777543789L;

	private Date visitDateTime;
	private VisitStatus visitStatus;
	private String symptoms;

	public VisitSummaryExportDetailsDto(Date visitDateTime, VisitStatus visitStatus, String symptoms) {

		this.visitDateTime = visitDateTime;
		this.visitStatus = visitStatus;
		this.symptoms = symptoms;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}
}
