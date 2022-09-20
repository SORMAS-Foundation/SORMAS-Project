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

package de.symeda.sormas.api.deletionconfiguration;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public class DeletionInfoDto implements Serializable {

	private Date deletionDate;
	private Date referenceDate;
	private int deletionPeriod;
	private String deletionReferenceField;

	public DeletionInfoDto(Date deletionDate, Date referenceDate, int deletionPeriod, String deletionReferenceField) {
		this.deletionDate = deletionDate;
		this.referenceDate = referenceDate;
		this.deletionPeriod = deletionPeriod;
		this.deletionReferenceField = deletionReferenceField;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public int getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(int deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}

	public String getDeletionReferenceField() {
		return deletionReferenceField;
	}

	public void setDeletionReferenceField(String deletionReferenceField) {
		this.deletionReferenceField = deletionReferenceField;
	}
}
