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

package de.symeda.sormas.app.backend.outbreak;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Martin Wahnschaffe on 17.01.2018.
 */

@Entity(name = Outbreak.TABLE_NAME)
@DatabaseTable(tableName = Outbreak.TABLE_NAME)
public class Outbreak extends AbstractDomainObject {

	private static final long serialVersionUID = 6517638433928902578L;

	public static final String TABLE_NAME = "outbreak";
	public static final String I18N_PREFIX = "Outbreak";

	public static final String DISTRICT = "district_id";
	public static final String DISEASE = "disease";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String REPORTING_USER = "reportingUser_id";
	public static final String REPORT_DATE = "reportDate";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@Enumerated(EnumType.STRING)
	private Disease disease;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date startDate;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date endDate;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDate;

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
}
