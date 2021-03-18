package de.symeda.sormas.app.backend.infrastructure;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.app.backend.campaign.CampaignDtoHelper;
import de.symeda.sormas.app.backend.campaign.form.CampaignFormMetaDtoHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.disease.DiseaseConfigurationDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.feature.FeatureConfigurationDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.ContinentDtoHelper;
import de.symeda.sormas.app.backend.region.CountryDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.region.SubContinentDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.user.UserRoleConfigDtoHelper;

public class InfrastructureHelper {

	public static InfrastructureChangeDatesDto getInfrastructureChangeDates() {
		InfrastructureChangeDatesDto changeDates = new InfrastructureChangeDatesDto();

		changeDates.setContinentChangeDate(DatabaseHelper.getContinentDao().getLatestChangeDate());
		changeDates.setSubContinentChangeDate(DatabaseHelper.getSubContinentDao().getLatestChangeDate());
		changeDates.setCountryChangeDate(DatabaseHelper.getCountryDao().getLatestChangeDate());
		changeDates.setRegionChangeDate(DatabaseHelper.getRegionDao().getLatestChangeDate());
		changeDates.setDistrictChangeDate(DatabaseHelper.getDistrictDao().getLatestChangeDate());
		changeDates.setCommunityChangeDate(DatabaseHelper.getCommunityDao().getLatestChangeDate());
		changeDates.setFacilityChangeDate(DatabaseHelper.getFacilityDao().getLatestChangeDate());
		changeDates.setPointOfEntryChangeDate(DatabaseHelper.getPointOfEntryDao().getLatestChangeDate());
		changeDates.setUserChangeDate(DatabaseHelper.getUserDao().getLatestChangeDate());
		changeDates.setDiseaseClassificationChangeDate(DatabaseHelper.getDiseaseClassificationCriteriaDao().getLatestChangeDate());
		changeDates.setDiseaseConfigurationChangeDate(DatabaseHelper.getDiseaseConfigurationDao().getLatestChangeDate());
		changeDates.setUserRoleConfigurationChangeDate(DatabaseHelper.getUserRoleConfigDao().getLatestChangeDate());
		changeDates.setFeatureConfigurationChangeDate(DatabaseHelper.getFeatureConfigurationDao().getLatestChangeDate());
		changeDates.setCampaignChangeDate(DatabaseHelper.getCampaignDao().getLatestChangeDate());
		changeDates.setCampaignFormMetaChangeDate(DatabaseHelper.getCampaignFormMetaDao().getLatestChangeDate());

		return changeDates;
	}

	public static void handlePulledInfrastructureData(InfrastructureSyncDto infrastructureData) throws DaoException {
		new ContinentDtoHelper().handlePulledList(DatabaseHelper.getContinentDao(), infrastructureData.getContinents());
		new SubContinentDtoHelper().handlePulledList(DatabaseHelper.getSubContinentDao(), infrastructureData.getSubContinents());
		new CountryDtoHelper().handlePulledList(DatabaseHelper.getCountryDao(), infrastructureData.getCountries());
		new RegionDtoHelper().handlePulledList(DatabaseHelper.getRegionDao(), infrastructureData.getRegions());
		new DistrictDtoHelper().handlePulledList(DatabaseHelper.getDistrictDao(), infrastructureData.getDistricts());
		new CommunityDtoHelper().handlePulledList(DatabaseHelper.getCommunityDao(), infrastructureData.getCommunities());
		new FacilityDtoHelper().handlePulledList(DatabaseHelper.getFacilityDao(), infrastructureData.getFacilities());
		new PointOfEntryDtoHelper().handlePulledList(DatabaseHelper.getPointOfEntryDao(), infrastructureData.getPointsOfEntry());
		new UserDtoHelper().handlePulledList(DatabaseHelper.getUserDao(), infrastructureData.getUsers());
		new DiseaseClassificationDtoHelper()
			.handlePulledList(DatabaseHelper.getDiseaseClassificationCriteriaDao(), infrastructureData.getDiseaseClassifications());
		new DiseaseConfigurationDtoHelper()
			.handlePulledList(DatabaseHelper.getDiseaseConfigurationDao(), infrastructureData.getDiseaseConfigurations());
		DatabaseHelper.getUserRoleConfigDao().delete(infrastructureData.getDeletedUserRoleConfigurationUuids());
		new UserRoleConfigDtoHelper().handlePulledList(DatabaseHelper.getUserRoleConfigDao(), infrastructureData.getUserRoleConfigurations());
		DatabaseHelper.getFeatureConfigurationDao().delete(infrastructureData.getDeletedFeatureConfigurationUuids());
		new FeatureConfigurationDtoHelper()
			.handlePulledList(DatabaseHelper.getFeatureConfigurationDao(), infrastructureData.getFeatureConfigurations());
		if (!DatabaseHelper.getFeatureConfigurationDao().isFeatureDisabled(FeatureType.CAMPAIGNS)) {
			new CampaignDtoHelper().handlePulledList(DatabaseHelper.getCampaignDao(), infrastructureData.getCampaigns());
			new CampaignFormMetaDtoHelper().handlePulledList(DatabaseHelper.getCampaignFormMetaDao(), infrastructureData.getCampaignFormMetas());
		}
	}
}
