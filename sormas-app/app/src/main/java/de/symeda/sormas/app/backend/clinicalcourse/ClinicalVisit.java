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

package de.symeda.sormas.app.backend.clinicalcourse;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.ParentAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.symptoms.Symptoms;

@Entity(name = ClinicalVisit.TABLE_NAME)
@DatabaseTable(tableName = ClinicalVisit.TABLE_NAME)
public class ClinicalVisit extends PseudonymizableAdo {

	private static final long serialVersionUID = -8220449896773019721L;

	public static final String TABLE_NAME = "clinicalVisit";
	public static final String I18N_PREFIX = "ClinicalVisit";

	public static final String CLINICAL_COURSE = "clinicalCourse";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String SYMPTOMS = "symptoms";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private ClinicalCourse clinicalCourse;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Symptoms symptoms;
	@Enumerated(EnumType.STRING)
	private Disease disease;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date visitDateTime;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String visitRemarks;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String visitingPerson;

	@ParentAdo
	public ClinicalCourse getClinicalCourse() {
		return clinicalCourse;
	}

	public void setClinicalCourse(ClinicalCourse clinicalCourse) {
		this.clinicalCourse = clinicalCourse;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	public String getVisitingPerson() {
		return visitingPerson;
	}

	public void setVisitingPerson(String visitingPerson) {
		this.visitingPerson = visitingPerson;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
