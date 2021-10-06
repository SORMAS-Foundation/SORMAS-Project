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
import de.symeda.sormas.api.systemevents.SystemEventStatus;
import de.symeda.sormas.api.systemevents.SystemEventType;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb;
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
	private SystemEventFacadeEjb.SystemEventFacadeEjbLocal systemEventFacade;
	@Inject
	private EtcdCentralClient centralClient;

	private <DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria> void loadAndStore(
		String type,
		Class<DTO> clazz,
		InfrastructureBaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> facade) {
		List<DTO> dtos;

		Date lastUpdate = findLastUpdateDate();
		SystemEventDto event = initializeFetchEvent();

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
		systemEventFacade.reportSuccess(event, new Date());
	}

	public void loadAndStoreAll() {
		loadAndStoreContinents();
		loadAndStoreSubcontinents();
		loadAndStoreCountries();
		loadAndStoreRegions();
		loadAndStoreDistricts();
		loadAndStoreCommunities();
	}

	public void loadAndStoreContinents() {
		loadAndStore("continent", ContinentDto.class, continentFacadeEjb);
	}

	public void loadAndStoreSubcontinents() {
		loadAndStore("subcontinent", SubcontinentDto.class, subcontinentFacadeEjb);
	}

	public void loadAndStoreCountries() {
		loadAndStore("country", CountryDto.class, countryFacadeEjb);
	}

	public void loadAndStoreRegions() {
		loadAndStore("region", RegionDto.class, regionFacadeEjb);
	}

	public void loadAndStoreDistricts() {
		loadAndStore("district", DistrictDto.class, districtFacadeEjb);
	}

	public void loadAndStoreCommunities() {
		loadAndStore("community", CommunityDto.class, communityFacadeEjb);
	}

	protected SystemEventDto initializeFetchEvent() {
		Date startDate = new Date();
		SystemEventDto systemEvent = SystemEventDto.build();
		systemEvent.setType(SystemEventType.SYNC_CENTRAL);
		systemEvent.setStatus(SystemEventStatus.STARTED);
		systemEvent.setStartDate(startDate);
		systemEventFacade.saveSystemEvent(systemEvent);
		return systemEvent;
	}

	protected Date findLastUpdateDate() {
		SystemEventDto latestSuccess = systemEventFacade.getLatestSuccessByType(SystemEventType.SYNC_CENTRAL);
		Long millis;
		if (latestSuccess != null) {
			millis = determineLatestSuccessMillis(latestSuccess);
		} else {
			logger.info(
				"No previous successful attempt to fetch external lab message could be found. The synchronization date is set to 0 (UNIX milliseconds)");
			millis = 0L;
		}
		return new Date(millis);
	}

	private long determineLatestSuccessMillis(SystemEventDto latestSuccess) {
		String info = latestSuccess.getAdditionalInfo();
		if (info != null) {
			try {
				//parse last synchronization date
				return Long.parseLong(info.replace("Last synchronization date: ", ""));
			} catch (NumberFormatException e) {
				logger.error("Synchronization date could not be parsed for the last successful lab message retrieval. Falling back to start date.");
				return latestSuccess.getStartDate().getTime();
			}
		} else {
			logger.warn("Synchronization date could not be found for the last successful lab message retrieval. Falling back to start date.");
			return latestSuccess.getStartDate().getTime();
		}
	}
}
