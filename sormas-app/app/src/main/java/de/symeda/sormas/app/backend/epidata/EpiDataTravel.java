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

package de.symeda.sormas.app.backend.epidata;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.epidata.TravelType;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.util.DateFormatHelper;

/**
 * Created by Mate Strysewske on 08.03.2017.
 */

@Entity(name = EpiDataTravel.TABLE_NAME)
@DatabaseTable(tableName = EpiDataTravel.TABLE_NAME)
@EmbeddedAdo(parentAccessor = EpiDataTravel.EPI_DATA)
public class EpiDataTravel extends PseudonymizableAdo {

	private static final long serialVersionUID = -4280455878066233175L;

	public static final String TABLE_NAME = "epidatatravel";
	public static final String I18N_PREFIX = "EpiDataTravel";
	public static final String EPI_DATA = "epiData";

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private EpiData epiData;

	@Enumerated(EnumType.STRING)
	private TravelType travelType;

	@Column(length = 512)
	private String travelDestination;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date travelDateFrom;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date travelDateTo;

	public EpiData getEpiData() {
		return epiData;
	}

	public void setEpiData(EpiData epiData) {
		this.epiData = epiData;
	}

	public TravelType getTravelType() {
		return travelType;
	}

	public void setTravelType(TravelType travelType) {
		this.travelType = travelType;
	}

	public String getTravelDestination() {
		return travelDestination;
	}

	public void setTravelDestination(String travelDestination) {
		this.travelDestination = travelDestination;
	}

	public Date getTravelDateFrom() {
		return travelDateFrom;
	}

	public void setTravelDateFrom(Date travelDateFrom) {
		this.travelDateFrom = travelDateFrom;
	}

	public Date getTravelDateTo() {
		return travelDateTo;
	}

	public void setTravelDateTo(Date travelDateTo) {
		this.travelDateTo = travelDateTo;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + " " + DateFormatHelper.formatLocalDate(getTravelDateTo());
	}
}
