package de.symeda.sormas.backend.infrastructure;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.ClientInfraSyncFacade;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import de.symeda.sormas.backend.campaign.CampaignFacadeEjb;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaFacadeEjb;
import de.symeda.sormas.backend.caze.classification.CaseClassificationFacadeEjb.CaseClassificationFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;

@Stateless(name = "ClientInfraSyncFacade")
public class ClientInfraSyncFacadeEjb implements ClientInfraSyncFacade {

	@EJB
	private ContinentFacadeEjb.ContinentFacadeEjbLocal continentFacade;
	@EJB
	private SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal subcontinentFacade;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private AreaFacadeEjb.AreaFacadeEjbLocal areaFacade;
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
	@EJB
	private CampaignFacadeEjb.CampaignFacadeEjbLocal campaignFacade;
	@EJB
	private CampaignFormMetaFacadeEjb.CampaignFormMetaFacadeEjbLocal campaignFormMetaFacade;

	@Override
	public InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates) {

		InfrastructureSyncDto sync = new InfrastructureSyncDto();

		if (facilityService.countAfter(changeDates.getFacilityChangeDate()) > configFacade.getInfrastructureSyncThreshold()
			|| communityService.countAfter(changeDates.getCommunityChangeDate()) > configFacade.getInfrastructureSyncThreshold()) {
			sync.setInitialSyncRequired(true);
			return sync;
		}

		sync.setContinents(continentFacade.getAllAfter(changeDates.getContinentChangeDate()));
		sync.setSubcontinents(subcontinentFacade.getAllAfter(changeDates.getSubcontinentChangeDate()));
		sync.setCountries(countryFacade.getAllAfter(changeDates.getCountryChangeDate()));
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

		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			sync.setAreas(areaFacade.getAllAfter(changeDates.getAreaChangeDate()));
		}

		return sync;
	}
}
