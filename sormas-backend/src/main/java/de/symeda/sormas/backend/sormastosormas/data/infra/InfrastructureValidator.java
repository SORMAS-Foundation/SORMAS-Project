package de.symeda.sormas.backend.sormastosormas.data.infra;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.central.EtcdCentralClient;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

@Stateless
@LocalBean
public class InfrastructureValidator {

	@EJB
	private UserService userService;
	@EJB
	private ContinentFacadeEjb.ContinentFacadeEjbLocal continentFacade;
	@EJB
	private SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal subcontinentFacade;
	@EJB
	private RegionFacadeEjb.RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private CountryFacadeEjb.CountryFacadeEjbLocal countryFacade;
	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@Inject
	private EtcdCentralClient centralClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasDiscoveryService.class);

	public enum CentralInfra {
		CONTINENT,
		SUB_CONTINENT,
		COUNTRY,
		REGION,
		DISTRICT,
		COMMUNITY
	}

	private <T> T loadFromEtcd(String uuid, Class<T> clazz) {

		String key = String.format("/central/location/%s/%s", clazz.getSimpleName().toLowerCase(Locale.ROOT), uuid);
		EtcdCentralClient.KeyValue result = null;
		try {
			result = centralClient.get(key);
			if (result == null) {
				LOGGER.error("Not value for key {} found", key);
				return null;
			}
		} catch (IOException e) {
			LOGGER.error("Could not load data for UUID {} of type {} due to IO: %s", uuid, clazz.getSimpleName(), e);
			return null;
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		try {
			return mapper.readValue(result.getValue(), clazz);
		} catch (JsonProcessingException e) {
			LOGGER.error("Could not serialize location object: %s", e);
			return null;
		}

	}

	public ValidationErrors processInfrastructure(CentralInfra type, InfrastructureDataReferenceDto refToCheck, String errorCaption) {
		ValidationErrors validationErrors = new ValidationErrors();
		if (refToCheck == null) {
			return validationErrors;
		}

		String errorMessage = null;
		InfrastructureAdo centralInfra;
		final String checkUuid = refToCheck.getUuid();

		switch (type) {

		case CONTINENT:
			centralInfra = loadFromEtcd(checkUuid, Continent.class);
			errorMessage = Validations.sormasToSormasContinent;
			break;
		case SUB_CONTINENT:
			centralInfra = loadFromEtcd(checkUuid, Subcontinent.class);
			errorMessage = Validations.sormasToSormasSubcontinent;
			break;
		case COUNTRY:
			centralInfra = loadFromEtcd(checkUuid, Country.class);
			break;
		case REGION:
			centralInfra = loadFromEtcd(checkUuid, Region.class);
			errorMessage = Validations.sormasToSormasRegion;
			break;
		case DISTRICT:
			centralInfra = loadFromEtcd(checkUuid, District.class);
			errorMessage = Validations.sormasToSormasDistrict;
			break;
		case COMMUNITY:
			centralInfra = loadFromEtcd(checkUuid, Community.class);
			errorMessage = Validations.sormasToSormasCommunity;
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + type);
		}

		// todo equality check missing
		if (centralInfra == null) {
			validationErrors.add(new ValidationErrorGroup(errorCaption), new ValidationErrorMessage(errorMessage, refToCheck.getCaption()));
		}
		return validationErrors;
	}

	private InfrastructureValidator.WithDetails<FacilityReferenceDto> loadFacility(
		FacilityReferenceDto facility,
		FacilityType facilityType,
		String facilityDetails) {

		String facilityUuid = facility.getUuid();

		if (FacilityDto.CONSTANT_FACILITY_UUIDS.contains(facilityUuid)) {

			FacilityReferenceDto localFacility = facilityDetails != null
				? facilityFacade.getByNameAndType(facilityDetails.trim(), null, null, facilityType, false).stream().findFirst().orElse(null)
				: null;
			if (localFacility == null) {
				localFacility = facilityFacade.getByUuid(facilityUuid).toReference();
			} else {
				facilityDetails = null;
			}

			return WithDetails.of(localFacility, facilityDetails);
		} else {
			Optional<FacilityReferenceDto> localFacility = facility.getExternalId() != null
				? facilityFacade.getByExternalIdAndType(facility.getExternalId(), facilityType, false).stream().findFirst()
				: Optional.empty();

			if (!localFacility.isPresent()) {
				localFacility = facilityFacade.getByNameAndType(facility.getCaption(), null, null, facilityType, false).stream().findFirst();
			}

			final String details;
			if (!localFacility.isPresent()) {
				details = facility.getCaption();
				localFacility = Optional.of(facilityFacade.getByUuid(FacilityDto.OTHER_FACILITY_UUID).toReference());
			} else {
				details = facilityDetails;
			}

			return localFacility.map((f) -> WithDetails.of(f, details)).orElse(null);
		}
	}

	public ValidationErrors processFacility(FacilityReferenceDto facility, FacilityType facilityType, String facilityDetails, String errorCaption) {
		ValidationErrors validationErrors = new ValidationErrors();
		if (facility == null) {
			return validationErrors;
		}

		// todo set details correctly
		// call all setters correctly

		WithDetails<FacilityReferenceDto> tmp = loadFacility(facility, facilityType, facilityDetails);
		FacilityReferenceDto localFacility = tmp.entity;
		if (facility != null && localFacility == null) {
			validationErrors
				.add(new ValidationErrorGroup(errorCaption), new ValidationErrorMessage(Validations.sormasToSormasFacility, facility.getCaption()));
		}
		return validationErrors;
	}

	private WithDetails<PointOfEntryReferenceDto> loadPointOfEntry(PointOfEntryReferenceDto pointOfEntry, String pointOfEntryDetails) {
		// todo set details correctly
		// call all setters correctly
		String pointOfEntryUuid = pointOfEntry.getUuid();

		if (PointOfEntryDto.CONSTANT_POE_UUIDS.contains(pointOfEntryUuid)) {
			PointOfEntryReferenceDto localPointOfEntry = pointOfEntryDetails != null
				? pointOfEntryFacade.getByName(pointOfEntryDetails.trim(), null, false).stream().findFirst().orElse(null)
				: null;
			if (localPointOfEntry == null) {
				localPointOfEntry = pointOfEntryFacade.getByUuid(pointOfEntryUuid).toReference();
			} else {
				pointOfEntryDetails = null;
			}

			return WithDetails.of(localPointOfEntry, pointOfEntryDetails);
		} else {

			Optional<PointOfEntryReferenceDto> localPointOfEntry = pointOfEntry.getExternalId() != null
				? pointOfEntryFacade.getByExternalId(pointOfEntry.getExternalId(), false).stream().findFirst()
				: Optional.empty();

			if (!localPointOfEntry.isPresent()) {
				localPointOfEntry = pointOfEntryFacade.getByName(pointOfEntry.getCaption(), null, false).stream().findFirst();
			}

			final String details;
			if (!localPointOfEntry.isPresent()) {
				details = pointOfEntry.getCaption();
				localPointOfEntry = Optional
					.of(pointOfEntryFacade.getByUuid(PointOfEntryDto.getOtherPointOfEntryUuid(pointOfEntry.getPointOfEntryType())).toReference());
			} else {
				details = pointOfEntryDetails;
			}

			return localPointOfEntry.map(p -> WithDetails.of(p, details)).orElse(null);
		}
	}

	public ValidationErrors processPointOfEntry(PointOfEntryReferenceDto pointOfEntry, String pointOfEntryDetails, String errorCaption) {
		ValidationErrors validationErrors = new ValidationErrors();
		if (pointOfEntry == null) {
			return validationErrors;
		}

		WithDetails<PointOfEntryReferenceDto> tmp = loadPointOfEntry(pointOfEntry, pointOfEntryDetails);
		PointOfEntryReferenceDto localPointOfEntry = tmp.entity;
		if (pointOfEntry != null && localPointOfEntry == null) {
			validationErrors.add(
				new ValidationErrorGroup(errorCaption),
				new ValidationErrorMessage(Validations.sormasToSormasPointOfEntry, pointOfEntry.getCaption()));
		}
		return validationErrors;
	}

	public ValidationErrors processLocation(LocationDto location, String groupNameTag) {
		ValidationErrors validationErrors = new ValidationErrors();
		validationErrors.addAll(processInfrastructure(CentralInfra.CONTINENT, location.getContinent(), groupNameTag));
		validationErrors.addAll(processInfrastructure(CentralInfra.SUB_CONTINENT, location.getSubcontinent(), groupNameTag));
		validationErrors.addAll(processInfrastructure(CentralInfra.COUNTRY, location.getCountry(), groupNameTag));
		validationErrors.addAll(processInfrastructure(CentralInfra.REGION, location.getRegion(), groupNameTag));
		validationErrors.addAll(processInfrastructure(CentralInfra.DISTRICT, location.getDistrict(), groupNameTag));
		validationErrors.addAll(processInfrastructure(CentralInfra.COMMUNITY, location.getCommunity(), groupNameTag));
		validationErrors.addAll(processFacility(location.getFacility(), location.getFacilityType(), location.getFacilityDetails(), groupNameTag));

		return validationErrors;
	}

	private static final class WithDetails<T> {

		private T entity;
		private String details;

		public static <T> WithDetails<T> of(T facility, String facilityDetails) {
			WithDetails<T> localFacility = new WithDetails<>();

			localFacility.entity = facility;
			localFacility.details = facilityDetails;

			return localFacility;
		}
	}

}
