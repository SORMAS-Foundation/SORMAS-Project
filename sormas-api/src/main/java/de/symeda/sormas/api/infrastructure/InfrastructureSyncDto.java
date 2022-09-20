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

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleDto;

@AuditedClass
public class InfrastructureSyncDto implements Serializable {

	private static final long serialVersionUID = -3874808120492307171L;

	private boolean initialSyncRequired;
	private List<ContinentDto> continents;
	private List<SubcontinentDto> subcontinents;
	private List<CountryDto> countries;
	private List<AreaDto> areas;
	private List<RegionDto> regions;
	private List<DistrictDto> districts;
	private List<CommunityDto> communities;
	private List<FacilityDto> facilities;
	private List<PointOfEntryDto> pointsOfEntry;
	private List<UserDto> users;
	private List<DiseaseClassificationCriteriaDto> diseaseClassifications;
	private List<DiseaseConfigurationDto> diseaseConfigurations;
	private List<UserRoleDto> userRoles;
	private List<String> deletedUserRoleUuids;
	private List<FeatureConfigurationDto> featureConfigurations;
	private List<String> deletedFeatureConfigurationUuids;

	public boolean isInitialSyncRequired() {
		return initialSyncRequired;
	}

	public void setInitialSyncRequired(boolean initialSyncRequired) {
		this.initialSyncRequired = initialSyncRequired;
	}

	public List<ContinentDto> getContinents() {
		return continents;
	}

	public void setContinents(List<ContinentDto> continents) {
		this.continents = continents;
	}

	public List<SubcontinentDto> getSubcontinents() {
		return subcontinents;
	}

	public void setSubcontinents(List<SubcontinentDto> subcontinents) {
		this.subcontinents = subcontinents;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public List<AreaDto> getAreas() {
		return areas;
	}

	public void setAreas(List<AreaDto> areas) {
		this.areas = areas;
	}

	public List<RegionDto> getRegions() {
		return regions;
	}

	public void setRegions(List<RegionDto> regions) {
		this.regions = regions;
	}

	public List<DistrictDto> getDistricts() {
		return districts;
	}

	public void setDistricts(List<DistrictDto> districts) {
		this.districts = districts;
	}

	public List<CommunityDto> getCommunities() {
		return communities;
	}

	public void setCommunities(List<CommunityDto> communities) {
		this.communities = communities;
	}

	public List<FacilityDto> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<FacilityDto> facilities) {
		this.facilities = facilities;
	}

	public List<PointOfEntryDto> getPointsOfEntry() {
		return pointsOfEntry;
	}

	public void setPointsOfEntry(List<PointOfEntryDto> pointsOfEntry) {
		this.pointsOfEntry = pointsOfEntry;
	}

	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}

	public List<DiseaseClassificationCriteriaDto> getDiseaseClassifications() {
		return diseaseClassifications;
	}

	public void setDiseaseClassifications(List<DiseaseClassificationCriteriaDto> diseaseClassifications) {
		this.diseaseClassifications = diseaseClassifications;
	}

	public List<DiseaseConfigurationDto> getDiseaseConfigurations() {
		return diseaseConfigurations;
	}

	public void setDiseaseConfigurations(List<DiseaseConfigurationDto> diseaseConfigurations) {
		this.diseaseConfigurations = diseaseConfigurations;
	}

	public List<UserRoleDto> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UserRoleDto> userRoles) {
		this.userRoles = userRoles;
	}

	public List<String> getDeletedUserRoleUuids() {
		return deletedUserRoleUuids;
	}

	public void setDeletedUserRoleUuids(List<String> deletedUserRoleUuids) {
		this.deletedUserRoleUuids = deletedUserRoleUuids;
	}

	public List<FeatureConfigurationDto> getFeatureConfigurations() {
		return featureConfigurations;
	}

	public void setFeatureConfigurations(List<FeatureConfigurationDto> featureConfigurations) {
		this.featureConfigurations = featureConfigurations;
	}

	public List<String> getDeletedFeatureConfigurationUuids() {
		return deletedFeatureConfigurationUuids;
	}

	public void setDeletedFeatureConfigurationUuids(List<String> deletedFeatureConfigurationUuids) {
		this.deletedFeatureConfigurationUuids = deletedFeatureConfigurationUuids;
	}
}
