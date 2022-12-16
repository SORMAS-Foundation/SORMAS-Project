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

package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.audit.AuditedClass;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;

@AuditedClass
public class InfrastructureChangeDatesDto implements Serializable {

	private static final long serialVersionUID = 6269655187128160377L;

	@Schema(description = "Cut-off date for when the collection of continents was last modified.")
	private Date continentChangeDate;
	@Schema(description = "Cut-off date for when the collection of subcontinents was last modified.")
	private Date subcontinentChangeDate;
	@Schema(description = "Cut-off date for when the collection of countries was last modified.")
	private Date countryChangeDate;
	@Schema(description = "Cut-off date for when the collection of areas was last modified.")
	private Date areaChangeDate;
	@Schema(description = "Cut-off date for when the collection of regions was last modified.")
	private Date regionChangeDate;
	@Schema(description = "Cut-off date for when the collection of disctricts was last modified.")
	private Date districtChangeDate;
	@Schema(description = "Cut-off date for when the collection of communities was last modified.")
	private Date communityChangeDate;
	@Schema(description = "Cut-off date for when the collection of facilities was last modified.")
	private Date facilityChangeDate;
	@Schema(description = "Cut-off date for when the collection of points-of-entry was last modified.")
	private Date pointOfEntryChangeDate;
	@Schema(description = "Cut-off date for when the user database was last modified.")
	private Date userChangeDate;
	@Schema(description = "Cut-off date for when the disease classification database was last modified.")
	private Date diseaseClassificationChangeDate;
	@Schema(description = "Cut-off date for when the disease configuration database was last modified.")
	private Date diseaseConfigurationChangeDate;
	@Schema(description = "Cut-off date for  when the database of user roles was last modified.")
	private Date userRoleChangeDate;
	@Schema(description = "Cut-off date for when the feature configuration database was last modified.")
	private Date featureConfigurationChangeDate;

	public Date getContinentChangeDate() {
		return continentChangeDate;
	}

	public void setContinentChangeDate(Date continentChangeDate) {
		this.continentChangeDate = continentChangeDate;
	}

	public Date getSubcontinentChangeDate() {
		return subcontinentChangeDate;
	}

	public void setSubcontinentChangeDate(Date subcontinentChangeDate) {
		this.subcontinentChangeDate = subcontinentChangeDate;
	}

	public Date getCountryChangeDate() {
		return countryChangeDate;
	}

	public void setCountryChangeDate(Date countryChangeDate) {
		this.countryChangeDate = countryChangeDate;
	}

	public Date getAreaChangeDate() {
		return areaChangeDate;
	}

	public void setAreaChangeDate(Date areaChangeDate) {
		this.areaChangeDate = areaChangeDate;
	}

	public Date getRegionChangeDate() {
		return regionChangeDate;
	}

	public void setRegionChangeDate(Date regionChangeDate) {
		this.regionChangeDate = regionChangeDate;
	}

	public Date getDistrictChangeDate() {
		return districtChangeDate;
	}

	public void setDistrictChangeDate(Date districtChangeDate) {
		this.districtChangeDate = districtChangeDate;
	}

	public Date getCommunityChangeDate() {
		return communityChangeDate;
	}

	public void setCommunityChangeDate(Date communityChangeDate) {
		this.communityChangeDate = communityChangeDate;
	}

	public Date getFacilityChangeDate() {
		return facilityChangeDate;
	}

	public void setFacilityChangeDate(Date facilityChangeDate) {
		this.facilityChangeDate = facilityChangeDate;
	}

	public Date getPointOfEntryChangeDate() {
		return pointOfEntryChangeDate;
	}

	public void setPointOfEntryChangeDate(Date pointOfEntryChangeDate) {
		this.pointOfEntryChangeDate = pointOfEntryChangeDate;
	}

	public Date getUserChangeDate() {
		return userChangeDate;
	}

	public void setUserChangeDate(Date userChangeDate) {
		this.userChangeDate = userChangeDate;
	}

	public Date getDiseaseClassificationChangeDate() {
		return diseaseClassificationChangeDate;
	}

	public void setDiseaseClassificationChangeDate(Date diseaseClassificationChangeDate) {
		this.diseaseClassificationChangeDate = diseaseClassificationChangeDate;
	}

	public Date getDiseaseConfigurationChangeDate() {
		return diseaseConfigurationChangeDate;
	}

	public void setDiseaseConfigurationChangeDate(Date diseaseConfigurationChangeDate) {
		this.diseaseConfigurationChangeDate = diseaseConfigurationChangeDate;
	}

	public Date getUserRoleChangeDate() {
		return userRoleChangeDate;
	}

	public void setUserRoleChangeDate(Date userRoleChangeDate) {
		this.userRoleChangeDate = userRoleChangeDate;
	}

	public Date getFeatureConfigurationChangeDate() {
		return featureConfigurationChangeDate;
	}

	public void setFeatureConfigurationChangeDate(Date featureConfigurationChangeDate) {
		this.featureConfigurationChangeDate = featureConfigurationChangeDate;
	}
}
