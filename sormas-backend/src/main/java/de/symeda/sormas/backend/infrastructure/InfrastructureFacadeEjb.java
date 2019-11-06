package de.symeda.sormas.backend.infrastructure;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
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
	
	@Override
	public InfrastructureSyncDto getNewInfrastructureData(Date since) {
		InfrastructureSyncDto sync = new InfrastructureSyncDto();
		
		if (facilityService.getNumberOfChangedFacilities(since) > configFacade.getInfrastructureSyncThreshold()
				|| communityService.getNumberOfChangedCommunities(since) > configFacade.getInfrastructureSyncThreshold()) {
			sync.setInitialSyncRequired(true);
			return sync;
		}
		
		sync.setRegions(regionFacade.getAllAfter(since));
		sync.setDistricts(districtFacade.getAllAfter(since));
		sync.setCommunities(communityFacade.getAllAfter(since));
		sync.setFacilities(facilityFacade.getAllByRegionAfter(null, since));
		sync.setPointsOfEntry(pointOfEntryFacade.getAllAfter(since));
		sync.setUsers(userFacade.getAllAfter(since));
		sync.setDiseaseClassifications(caseClassificationFacade.getAllSince(since));
		sync.setDiseaseConfigurations(diseaseConfigurationFacade.getAllAfter(since));
		sync.setUserRoleConfigurations(userRoleConfigurationFacade.getAllAfter(since));
		
		return sync;
	}
	
}
