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

	public static final String CONTINENT = "continent";
	public static final String SUBCONTINENT = "subcontinent";
	public static final String COUNTRY = "country";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
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

		newDtos.forEach(facade::save);

	}

	public void syncAll() {
		if (!configFacade.isCentralLocationSync()) {
			logger.info("Skipping synchronization with central as feature is disabled.");
			return;
		}
		logger.info("Syncing all infra data.");
		try {
			SystemEventDto currentSync = syncFacadeEjb.startSyncFor(SystemEventType.CENTRAL_SYNC_INFRA);
			Date lastUpdate = syncFacadeEjb.findLastSyncDateFor(SystemEventType.CENTRAL_SYNC_INFRA);
			Date syncedAt = new Date();

			loadAndStoreContinents(lastUpdate);
			loadAndStoreSubcontinents(lastUpdate);
			loadAndStoreCountries(lastUpdate);
			loadAndStoreRegions(lastUpdate);
			loadAndStoreDistricts(lastUpdate);
			loadAndStoreCommunities(lastUpdate);

			syncFacadeEjb.reportSuccessfulSyncWithTimestamp(currentSync, syncedAt);
		} catch (Exception e) {
			// broad clause is necessary here: Cron schedule stability was influenced by uncatched exceptions 
			logger.error("Could not sync with central: %s", e);
		}

	}

	private void loadAndStoreContinents(Date lastUpdate) {
		loadAndStore(CONTINENT, ContinentDto.class, continentFacadeEjb, lastUpdate);
	}

	private void loadAndStoreSubcontinents(Date lastUpdate) {
		loadAndStore(SUBCONTINENT, SubcontinentDto.class, subcontinentFacadeEjb, lastUpdate);
	}

	private void loadAndStoreCountries(Date lastUpdate) {
		loadAndStore(COUNTRY, CountryDto.class, countryFacadeEjb, lastUpdate);
	}

	private void loadAndStoreRegions(Date lastUpdate) {
		loadAndStore(REGION, RegionDto.class, regionFacadeEjb, lastUpdate);
	}

	private void loadAndStoreDistricts(Date lastUpdate) {
		loadAndStore(DISTRICT, DistrictDto.class, districtFacadeEjb, lastUpdate);
	}

	private void loadAndStoreCommunities(Date lastUpdate) {
		loadAndStore(COMMUNITY, CommunityDto.class, communityFacadeEjb, lastUpdate);
	}

}
