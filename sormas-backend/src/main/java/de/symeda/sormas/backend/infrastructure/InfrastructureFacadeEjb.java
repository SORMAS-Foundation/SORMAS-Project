package de.symeda.sormas.backend.infrastructure;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;

@Stateless(name = "InfrastructureFacade")
public class InfrastructureFacadeEjb implements InfrastructureFacade {

	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private CaseClassificationFacadeEjbLocal caseClassificationFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigurationFacade;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CommunityService communityService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@Override
	public InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates) {

		InfrastructureSyncDto sync = new InfrastructureSyncDto();

		if (facilityService.countAfter(changeDates.getFacilityChangeDate()) > configFacade.getInfrastructureSyncThreshold()
			|| communityService.countAfter(changeDates.getCommunityChangeDate()) > configFacade.getInfrastructureSyncThreshold()) {
			sync.setInitialSyncRequired(true);
			return sync;
		}

		sync.setRegions(regionFacade.getAllAfter(changeDates.getRegionChangeDate()));
		sync.setDistricts(districtFacade.getAllAfter(changeDates.getDistrictChangeDate()));
		sync.setCommunities(communityFacade.getAllAfter(changeDates.getCommunityChangeDate()));
		sync.setFacilities(facilityFacade.getAllByRegionAfter(null, changeDates.getFacilityChangeDate()));
		sync.setPointsOfEntry(pointOfEntryFacade.getAllAfter(changeDates.getPointOfEntryChangeDate()));
		sync.setUsers(userFacade.getAllAfter(changeDates.getUserChangeDate()));
		sync.setDiseaseClassifications(caseClassificationFacade.getAllSince(changeDates.getDiseaseClassificationChangeDate()));
		sync.setDiseaseConfigurations(diseaseConfigurationFacade.getAllAfter(changeDates.getDiseaseConfigurationChangeDate()));
		sync.setUserRoleConfigurations(userRoleConfigurationFacade.getAllAfter(changeDates.getUserRoleConfigurationChangeDate()));
		sync.setDeletedUserRoleConfigurationUuids(userRoleConfigurationFacade.getDeletedUuids(changeDates.getUserRoleConfigurationChangeDate()));
		sync.setFeatureConfigurations(featureConfigurationFacade.getAllAfter(changeDates.getFeatureConfigurationChangeDate()));
		sync.setDeletedFeatureConfigurationUuids(featureConfigurationFacade.getDeletedUuids(changeDates.getFeatureConfigurationChangeDate()));

		return sync;
	}
}
