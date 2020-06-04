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
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "InfrastructureFacade")
public class InfrastructureFacadeEjb implements InfrastructureFacade {

	@EJB
	protected RegionFacadeEjbLocal regionFacade;
	@EJB
	protected DistrictFacadeEjbLocal districtFacade;
	@EJB
	protected CommunityFacadeEjbLocal communityFacade;
	@EJB
	protected FacilityFacadeEjbLocal facilityFacade;
	@EJB
	protected PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	protected UserFacadeEjbLocal userFacade;
	@EJB
	protected CaseClassificationFacadeEjbLocal caseClassificationFacade;
	@EJB
	protected DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	protected UserRoleConfigFacadeEjbLocal userRoleConfigurationFacade;
	@EJB
	protected FacilityService facilityService;
	@EJB
	protected CommunityService communityService;
	@EJB
	protected ConfigFacadeEjbLocal configFacade;
	@EJB
	protected FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	protected UserService userService;
	
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
