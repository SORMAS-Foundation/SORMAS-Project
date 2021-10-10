package de.symeda.sormas.backend.infrastructure.central;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.systemevents.SystemEventDto;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@LocalBean
@Stateless
public class CentralInfraSyncFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	ContinentFacadeEjb.ContinentFacadeEjbLocal continentFacadeEjb;
	@EJB
	SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal subcontinentFacadeEjb;
	@EJB
	CountryFacadeEjb.CountryFacadeEjbLocal countryFacadeEjb;
	@EJB
	RegionFacadeEjb.RegionFacadeEjbLocal regionFacadeEjb;
	@EJB
	DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacadeEjb;
	@EJB
	CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacadeEjb;
	@EJB
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;
	@Inject
	private EtcdCentralClient centralClient;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	private <DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria> void loadAndStore(
		String type,
		Class<DTO> clazz,
		InfrastructureBaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> facade,
		Date lastUpdate) {
		List<DTO> dtos;

		try {
			dtos = centralClient.getWithPrefix(String.format("/central/location/%s/", type), clazz);
		} catch (IOException e) {
			logger.error("Could not load all entities of type {} from central: %s", type, e);
			return;
		}
		logger.info("Loaded {} entities of type {}", dtos.size(), type);

		List<DTO> newDtos = dtos.stream().filter(d -> d.getChangeDate().after(lastUpdate)).collect(Collectors.toList());
		logger.info("Importing {} entities of type {}", newDtos.size(), type);

		newDtos.stream().parallel().forEach(facade::save);

	}

	public void syncAll() {
		if (!configFacade.isCentralLocationSync()) {
			logger.info("Skipping synchronization with central as feature is disabled.");
			return;
		}

		SystemEventDto currentSync = syncFacadeEjb.startSyncFor(SystemEventType.SYNC_CENTRAL);
		Date lastUpdate = syncFacadeEjb.findLastSyncDateFor(SystemEventType.SYNC_CENTRAL);
		Date syncedAt = new Date();

		loadAndStoreContinents(lastUpdate);
		loadAndStoreSubcontinents(lastUpdate);
		loadAndStoreCountries(lastUpdate);
		loadAndStoreRegions(lastUpdate);
		loadAndStoreDistricts(lastUpdate);
		loadAndStoreCommunities(lastUpdate);

		syncFacadeEjb.reportSuccessfulSyncWithTimestamp(currentSync, syncedAt);
	}

	private void loadAndStoreContinents(Date lastUpdate) {
		loadAndStore("continent", ContinentDto.class, continentFacadeEjb, lastUpdate);
	}

	private void loadAndStoreSubcontinents(Date lastUpdate) {
		loadAndStore("subcontinent", SubcontinentDto.class, subcontinentFacadeEjb, lastUpdate);
	}

	private void loadAndStoreCountries(Date lastUpdate) {
		loadAndStore("country", CountryDto.class, countryFacadeEjb, lastUpdate);
	}

	private void loadAndStoreRegions(Date lastUpdate) {
		loadAndStore("region", RegionDto.class, regionFacadeEjb, lastUpdate);
	}

	private void loadAndStoreDistricts(Date lastUpdate) {
		loadAndStore("district", DistrictDto.class, districtFacadeEjb, lastUpdate);
	}

	private void loadAndStoreCommunities(Date lastUpdate) {
		loadAndStore("community", CommunityDto.class, communityFacadeEjb, lastUpdate);
	}

}
