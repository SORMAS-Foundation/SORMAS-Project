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

package de.symeda.sormas.app.backend.hospitalization;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.backend.common.EmbeddedAdo;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.util.DateFormatHelper;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

@Entity(name = PreviousHospitalization.TABLE_NAME)
@DatabaseTable(tableName = PreviousHospitalization.TABLE_NAME)
@EmbeddedAdo(parentAccessor = PreviousHospitalization.HOSPITALIZATION)
public class PreviousHospitalization extends PseudonymizableAdo {

	private static final long serialVersionUID = 768263094433806267L;

	public static final String TABLE_NAME = "previoushospitalizations";
	public static final String I18N_PREFIX = "CasePreviousHospitalization";
	public static final String HOSPITALIZATION = "hospitalization";

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date admissionDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date dischargeDate;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Region region;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private District district;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Community community;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	private Facility healthFacility;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String healthFacilityDetails;

	@Enumerated(EnumType.STRING)
	private YesNoUnknown isolated;

	@Column(length = COLUMN_LENGTH_BIG)
	private String description;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Hospitalization hospitalization;

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public String getHealthFacilityDetails() {
		return healthFacilityDetails;
	}

	public void setHealthFacilityDetails(String healthFacilityDetails) {
		this.healthFacilityDetails = healthFacilityDetails;
	}

	public YesNoUnknown getIsolated() {
		return isolated;
	}

	public void setIsolated(YesNoUnknown isolated) {
		this.isolated = isolated;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Hospitalization getHospitalization() {
		return hospitalization;
	}

	public void setHospitalization(Hospitalization hospitalization) {
		this.hospitalization = hospitalization;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	@Override
	public String toString() {
		return super.toString() + " " + DateFormatHelper.formatLocalDate(getDischargeDate());
	}
}
