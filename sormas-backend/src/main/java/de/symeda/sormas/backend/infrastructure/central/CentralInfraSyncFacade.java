package de.symeda.sormas.backend.infrastructure.central;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
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
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.systemevent.sync.SyncFacadeEjb;

@LocalBean
@Stateless
public class CentralInfraSyncFacade {

	public static final String CONTINENT = "continent";
	public static final String SUBCONTINENT = "subcontinent";
	public static final String COUNTRY = "country";
	public static final String AREA = "area";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CENTRAL_LOCATION_TEMPLATE = "/central/location/%s/";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ContinentFacadeEjb.ContinentFacadeEjbLocal continentFacadeEjb;
	@EJB
	private SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal subcontinentFacadeEjb;
	@EJB
	private CountryFacadeEjb.CountryFacadeEjbLocal countryFacadeEjb;
	@EJB
	private AreaFacadeEjb.AreaFacadeEjbLocal areaFacadeEjb;
	@EJB
	private RegionFacadeEjb.RegionFacadeEjbLocal regionFacadeEjb;
	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacadeEjb;
	@EJB
	private CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacadeEjb;
	@EJB
	private SyncFacadeEjb.SyncFacadeEjbLocal syncFacadeEjb;
	@Inject
	private EtcdCentralClient centralClient;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacadeEjb;

	private <DTO extends InfrastructureDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria> Date loadAndStore(
		String type,
		Class<DTO> clazz,
		InfrastructureFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> facade,
		Date lastSync) {
		List<DTO> dtos;

		try {
			dtos = centralClient.getWithPrefix(String.format(CENTRAL_LOCATION_TEMPLATE, type), clazz);
		} catch (IOException e) {
			logger.error("Could not load all entities of type {} from central: %s", type, e);
			return lastSync;
		}
		logger.info("Loaded {} entities of type {}", dtos.size(), type);

		List<DTO> newDtos = dtos.stream().filter(d -> d.getChangeDate().after(lastSync)).collect(Collectors.toList());
		newDtos.forEach(d -> d.setCentrallyManaged(true));

		logger.info("Importing {} entities of type {}", newDtos.size(), type);
		if (newDtos.isEmpty()) {
			return lastSync;
		}

		// find the newest change date in the received data
		Date newestChangeDate = newDtos.stream().map(DTO::getChangeDate).max(Date::compareTo).orElse(lastSync);
		logger.info("The newest change date is {}", newestChangeDate);

		newDtos.forEach(d -> {
			logger.info("Processing: {} - {}", d, d.getUuid());
			facade.saveFromCentral(d);
		});
		logger.info("Successfully imported all entities from central");
		return newestChangeDate;
	}

	public void syncAll() {
		if (!configFacade.isCentralLocationSync()) {
			logger.info("Skipping synchronization with central as feature is disabled.");
			return;
		}
		logger.info("Syncing all infra data.");
		try {
			SystemEventDto currentSync = syncFacadeEjb.startSyncFor(SystemEventType.CENTRAL_SYNC_INFRA);
			Date lastSync = syncFacadeEjb.findLastSyncDateFor(SystemEventType.CENTRAL_SYNC_INFRA);
			Date[] latestChangeDates = {
				loadAndStoreContinents(lastSync),
				loadAndStoreSubcontinents(lastSync),
				loadAndStoreCountries(lastSync),
				loadAndStoreArea(lastSync),
				loadAndStoreRegions(lastSync),
				loadAndStoreDistricts(lastSync),
				loadAndStoreCommunities(lastSync) };

			// we filter for the highest change data received from central. If no newer date is found, we keep the last sync.
			Date latestSyncDate = Arrays.stream(latestChangeDates).max(Date::compareTo).orElse(lastSync);
			syncFacadeEjb.reportSuccessfulSyncWithTimestamp(currentSync, latestSyncDate);

		} catch (Exception e) {
			// broad clause is necessary here: Cron schedule stability has been influenced by uncatched exceptions
			logger.error("Could not sync with central: %s", e);
		}
	}

	private Date loadAndStoreContinents(Date lastUpdate) {
		return loadAndStore(CONTINENT, ContinentDto.class, continentFacadeEjb, lastUpdate);
	}

	private Date loadAndStoreSubcontinents(Date lastUpdate) {
		return loadAndStore(SUBCONTINENT, SubcontinentDto.class, subcontinentFacadeEjb, lastUpdate);
	}

	private Date loadAndStoreCountries(Date lastUpdate) {
		return loadAndStore(COUNTRY, CountryDto.class, countryFacadeEjb, lastUpdate);
	}

	private Date loadAndStoreArea(Date lastUpdate) {
		if (featureConfigurationFacadeEjb.isFeatureEnabled(FeatureType.INFRASTRUCTURE_TYPE_AREA)) {
			return loadAndStore(AREA, AreaDto.class, areaFacadeEjb, lastUpdate);
		} else {
			logger.info("Skipping sync for Area as feature is disabled.");
			return lastUpdate;
		}
	}

	private Date loadAndStoreRegions(Date lastUpdate) {
		return loadAndStore(REGION, RegionDto.class, regionFacadeEjb, lastUpdate);
	}

	private Date loadAndStoreDistricts(Date lastUpdate) {
		return loadAndStore(DISTRICT, DistrictDto.class, districtFacadeEjb, lastUpdate);
	}

	private Date loadAndStoreCommunities(Date lastUpdate) {
		return loadAndStore(COMMUNITY, CommunityDto.class, communityFacadeEjb, lastUpdate);
	}

}
