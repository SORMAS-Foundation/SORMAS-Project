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

package de.symeda.sormas.app.backend.facility;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.databinding.Bindable;

import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.app.backend.common.InfrastructureAdo;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name = Facility.TABLE_NAME)
@DatabaseTable(tableName = Facility.TABLE_NAME)
public class Facility extends InfrastructureAdo {

	private static final long serialVersionUID = 8572137127616417072L;

	public static final String TABLE_NAME = "facility";
	public static final String I18N_PREFIX = "Facility";

	public static final String REGION = "region_id";
	public static final String DISTRICT = "district_id";
	public static final String COMMUNITY = "community_id";
	public static final String NAME = "name";
	public static final String TYPE = "type";

	@Column
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;
	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String city;

	@DatabaseField
	private Double latitude;
	@DatabaseField
	private Double longitude;

	@Enumerated(EnumType.STRING)
	private FacilityType type;
	@Column
	private boolean publicOwnership;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Bindable
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public FacilityType getType() {
		return type;
	}

	public void setType(FacilityType type) {
		this.type = type;
	}

	public boolean isPublicOwnership() {
		return publicOwnership;
	}

	public void setPublicOwnership(boolean publicOwnership) {
		this.publicOwnership = publicOwnership;
	}

	@Override
	public String toString() {
		return FacilityHelper.buildFacilityString(getUuid(), name);
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
