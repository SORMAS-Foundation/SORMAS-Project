package de.symeda.sormas.backend.infrastructure.central;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@LocalBean
@Stateless
public class CentralInfraSyncFacade {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

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

	@Inject
	private EtcdCentralClient centralClient;

	private <DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, CRITERIA extends BaseCriteria> void loadAndStore(
		String type,
		Class<DTO> clazz,
		InfrastructureBaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> facade) {
		List<DTO> dtos;
		try {
			dtos = centralClient.getWithPrefix(String.format("/central/location/%s/", type), clazz);
		} catch (IOException e) {
			LOGGER.error("Could not load all entities of type {} from central: %s", type, e);
			return;
		}
		LOGGER.info("Loaded {} entities of type {}", dtos.size(), type);
		for (DTO dto : dtos) {
			facade.save(dto);
		}
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
}
