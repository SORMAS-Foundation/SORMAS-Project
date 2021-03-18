package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.ContinentDto;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.SubContinentDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRoleConfigDto;

public class InfrastructureSyncDto implements Serializable {

	private static final long serialVersionUID = -3874808120492307171L;

	private boolean initialSyncRequired;
	private List<ContinentDto> continents;
	private List<SubContinentDto> subContinents;
	private List<CountryDto> countries;
	private List<RegionDto> regions;
	private List<DistrictDto> districts;
	private List<CommunityDto> communities;
	private List<FacilityDto> facilities;
	private List<PointOfEntryDto> pointsOfEntry;
	private List<UserDto> users;
	private List<DiseaseClassificationCriteriaDto> diseaseClassifications;
	private List<DiseaseConfigurationDto> diseaseConfigurations;
	private List<UserRoleConfigDto> userRoleConfigurations;
	private List<String> deletedUserRoleConfigurationUuids;
	private List<FeatureConfigurationDto> featureConfigurations;
	private List<String> deletedFeatureConfigurationUuids;
	private List<CampaignDto> campaigns;
	private List<CampaignFormMetaDto> campaignFormMetas;

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

	public List<SubContinentDto> getSubContinents() {
		return subContinents;
	}

	public void setSubContinents(List<SubContinentDto> subContinents) {
		this.subContinents = subContinents;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
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

	public List<UserRoleConfigDto> getUserRoleConfigurations() {
		return userRoleConfigurations;
	}

	public void setUserRoleConfigurations(List<UserRoleConfigDto> userRoleConfigurations) {
		this.userRoleConfigurations = userRoleConfigurations;
	}

	public List<String> getDeletedUserRoleConfigurationUuids() {
		return deletedUserRoleConfigurationUuids;
	}

	public void setDeletedUserRoleConfigurationUuids(List<String> deletedUserRoleConfigurationUuids) {
		this.deletedUserRoleConfigurationUuids = deletedUserRoleConfigurationUuids;
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

	public List<CampaignDto> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<CampaignDto> campaigns) {
		this.campaigns = campaigns;
	}

	public List<CampaignFormMetaDto> getCampaignFormMetas() {
		return campaignFormMetas;
	}

	public void setCampaignFormMetas(List<CampaignFormMetaDto> campaignFormMetas) {
		this.campaignFormMetas = campaignFormMetas;
	}
}
