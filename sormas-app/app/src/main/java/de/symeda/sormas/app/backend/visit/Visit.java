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

package de.symeda.sormas.app.backend.visit;

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
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DateFormatHelper;

@Entity(name = Visit.TABLE_NAME)
@DatabaseTable(tableName = Visit.TABLE_NAME)
public class Visit extends PseudonymizableAdo {

	public static final String TABLE_NAME = "visits";
	public static final String I18N_PREFIX = "Visit";

	public static final String PERSON = "person";
	public static final String DISEASE = "disease";
	public static final String VISIT_DATE_TIME = "visitDateTime";
	public static final String VISIT_USER = "visitUser";
	public static final String VISIT_STATUS = "visitStatus";
	public static final String VISIT_REMARKS = "visitRemarks";
	public static final String SYMPTOMS = "symptoms";

	@DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
	private Person person;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false)
	private Date visitDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private User visitUser;

	@Enumerated(EnumType.STRING)
	private VisitStatus visitStatus;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String visitRemarks;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Symptoms symptoms;

	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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

	public User getVisitUser() {
		return visitUser;
	}

	public void setVisitUser(User visitUser) {
		this.visitUser = visitUser;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public String getVisitRemarks() {
		return visitRemarks;
	}

	public void setVisitRemarks(String visitRemarks) {
		this.visitRemarks = visitRemarks;
	}

	/**
	 * return the symptoms, if null build new in service layer
	 * 
	 * @return
	 */
	public Symptoms getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + " " + DateFormatHelper.formatLocalDate(getVisitDateTime());
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}
}
