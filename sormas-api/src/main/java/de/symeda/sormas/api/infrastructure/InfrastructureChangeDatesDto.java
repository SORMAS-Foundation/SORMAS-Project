package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;
import java.util.Date;

public class InfrastructureChangeDatesDto implements Serializable {

	private static final long serialVersionUID = 6269655187128160377L;

	private Date continentChangeDate;
	private Date subContinentChangeDate;
	private Date countryChangeDate;
	private Date regionChangeDate;
	private Date districtChangeDate;
	private Date communityChangeDate;
	private Date facilityChangeDate;
	private Date pointOfEntryChangeDate;
	private Date userChangeDate;
	private Date diseaseClassificationChangeDate;
	private Date diseaseConfigurationChangeDate;
	private Date userRoleConfigurationChangeDate;
	private Date featureConfigurationChangeDate;
	private Date campaignChangeDate;
	private Date campaignFormMetaChangeDate;

	public Date getContinentChangeDate() {
		return continentChangeDate;
	}

	public void setContinentChangeDate(Date continentChangeDate) {
		this.continentChangeDate = continentChangeDate;
	}

	public Date getSubContinentChangeDate() {
		return subContinentChangeDate;
	}

	public void setSubContinentChangeDate(Date subContinentChangeDate) {
		this.subContinentChangeDate = subContinentChangeDate;
	}

	public Date getCountryChangeDate() {
		return countryChangeDate;
	}

	public void setCountryChangeDate(Date countryChangeDate) {
		this.countryChangeDate = countryChangeDate;
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

	public Date getUserRoleConfigurationChangeDate() {
		return userRoleConfigurationChangeDate;
	}

	public void setUserRoleConfigurationChangeDate(Date userRoleConfigurationChangeDate) {
		this.userRoleConfigurationChangeDate = userRoleConfigurationChangeDate;
	}

	public Date getFeatureConfigurationChangeDate() {
		return featureConfigurationChangeDate;
	}

	public void setFeatureConfigurationChangeDate(Date featureConfigurationChangeDate) {
		this.featureConfigurationChangeDate = featureConfigurationChangeDate;
	}

	public Date getCampaignChangeDate() {
		return campaignChangeDate;
	}

	public void setCampaignChangeDate(Date campaignChangeDate) {
		this.campaignChangeDate = campaignChangeDate;
	}

	public Date getCampaignFormMetaChangeDate() {
		return campaignFormMetaChangeDate;
	}

	public void setCampaignFormMetaChangeDate(Date campaignFormMetaChangeDate) {
		this.campaignFormMetaChangeDate = campaignFormMetaChangeDate;
	}
}
