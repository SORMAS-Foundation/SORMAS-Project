package de.symeda.sormas.app.backend.infrastructure;

import java.util.Optional;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.disease.DiseaseConfigurationDtoHelper;
import de.symeda.sormas.app.backend.facility.FacilityDtoHelper;
import de.symeda.sormas.app.backend.feature.FeatureConfigurationDtoHelper;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntryDtoHelper;
import de.symeda.sormas.app.backend.region.AreaDtoHelper;
import de.symeda.sormas.app.backend.region.CommunityDtoHelper;
import de.symeda.sormas.app.backend.region.ContinentDtoHelper;
import de.symeda.sormas.app.backend.region.CountryDtoHelper;
import de.symeda.sormas.app.backend.region.DistrictDtoHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;
import de.symeda.sormas.app.backend.region.SubcontinentDtoHelper;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.backend.user.UserRoleDtoHelper;
import de.symeda.sormas.app.component.dialog.SynchronizationDialog;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;

public class InfrastructureHelper {

	private static final Logger logger = LoggerFactory.getLogger(InfrastructureHelper.class);

	public static InfrastructureChangeDatesDto getInfrastructureChangeDates() {

		InfrastructureChangeDatesDto changeDates = new InfrastructureChangeDatesDto();

		changeDates.setContinentChangeDate(DatabaseHelper.getContinentDao().getLatestChangeDate());
		changeDates.setSubcontinentChangeDate(DatabaseHelper.getSubcontinentDao().getLatestChangeDate());
		changeDates.setCountryChangeDate(DatabaseHelper.getCountryDao().getLatestChangeDate());
		changeDates.setRegionChangeDate(DatabaseHelper.getRegionDao().getLatestChangeDate());
		changeDates.setDistrictChangeDate(DatabaseHelper.getDistrictDao().getLatestChangeDate());
		changeDates.setCommunityChangeDate(DatabaseHelper.getCommunityDao().getLatestChangeDate());
		changeDates.setFacilityChangeDate(DatabaseHelper.getFacilityDao().getLatestChangeDate());
		changeDates.setPointOfEntryChangeDate(DatabaseHelper.getPointOfEntryDao().getLatestChangeDate());
		changeDates.setUserChangeDate(DatabaseHelper.getUserDao().getLatestChangeDate());
		changeDates.setDiseaseClassificationChangeDate(DatabaseHelper.getDiseaseClassificationCriteriaDao().getLatestChangeDate());
		changeDates.setDiseaseConfigurationChangeDate(DatabaseHelper.getDiseaseConfigurationDao().getLatestChangeDate());
		changeDates.setUserRoleChangeDate(DatabaseHelper.getUserRoleDao().getLatestChangeDate());
		changeDates.setFeatureConfigurationChangeDate(DatabaseHelper.getFeatureConfigurationDao().getLatestChangeDate());
		changeDates.setAreaChangeDate(DatabaseHelper.getAreaDao().getLatestChangeDate());

		return changeDates;
	}

	public static void handlePulledInfrastructureData(
		InfrastructureSyncDto infrastructureData,
		Optional<SynchronizationDialog.SynchronizationCallbacks> callbacks)
		throws DaoException, NoConnectionException, ServerConnectionException, ServerCommunicationException {

		new ContinentDtoHelper().handlePulledList(DatabaseHelper.getContinentDao(), infrastructureData.getContinents(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new SubcontinentDtoHelper().handlePulledList(DatabaseHelper.getSubcontinentDao(), infrastructureData.getSubcontinents(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new CountryDtoHelper().handlePulledList(DatabaseHelper.getCountryDao(), infrastructureData.getCountries(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new AreaDtoHelper().handlePulledList(DatabaseHelper.getAreaDao(), infrastructureData.getAreas(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new RegionDtoHelper().handlePulledList(DatabaseHelper.getRegionDao(), infrastructureData.getRegions(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new DistrictDtoHelper().handlePulledList(DatabaseHelper.getDistrictDao(), infrastructureData.getDistricts(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new CommunityDtoHelper().handlePulledList(DatabaseHelper.getCommunityDao(), infrastructureData.getCommunities(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new FacilityDtoHelper().handlePulledList(DatabaseHelper.getFacilityDao(), infrastructureData.getFacilities(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new PointOfEntryDtoHelper().handlePulledList(DatabaseHelper.getPointOfEntryDao(), infrastructureData.getPointsOfEntry(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new UserRoleDtoHelper().handlePulledList(DatabaseHelper.getUserRoleDao(), infrastructureData.getUserRoles(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new UserDtoHelper().handlePulledList(DatabaseHelper.getUserDao(), infrastructureData.getUsers(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new DiseaseClassificationDtoHelper()
			.handlePulledList(DatabaseHelper.getDiseaseClassificationCriteriaDao(), infrastructureData.getDiseaseClassifications(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		new DiseaseConfigurationDtoHelper()
			.handlePulledList(DatabaseHelper.getDiseaseConfigurationDao(), infrastructureData.getDiseaseConfigurations(), callbacks);
		callbacks.ifPresent(c -> c.getLoadNextCallback().run());
		DatabaseHelper.getUserRoleDao().delete(infrastructureData.getDeletedUserRoleUuids());
		DatabaseHelper.getFeatureConfigurationDao().delete(infrastructureData.getDeletedFeatureConfigurationUuids());
		new FeatureConfigurationDtoHelper()
			.handlePulledList(DatabaseHelper.getFeatureConfigurationDao(), infrastructureData.getFeatureConfigurations(), callbacks);
	}
}
