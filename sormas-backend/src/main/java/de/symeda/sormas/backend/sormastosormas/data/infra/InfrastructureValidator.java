package de.symeda.sormas.backend.sormastosormas.data.infra;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.user.UserService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

	private WithDetails<PointOfEntryReferenceDto> loadLocalPointOfEntry(PointOfEntryReferenceDto pointOfEntry, String pointOfEntryDetails) {
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

	private InfrastructureValidator.WithDetails<FacilityReferenceDto> loadLocalFacility(
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

	public DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> loadLocalInfrastructure(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community) {
		return loadLocalInfrastructure(region, district, community, null, null, null, null, null);
	}

	public DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> loadLocalInfrastructure(CaseDataDto caze) {
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors = loadLocalInfrastructure(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getCommunity(),
			caze.getFacilityType(),
			caze.getHealthFacility(),
			caze.getHealthFacilityDetails(),
			caze.getPointOfEntry(),
			caze.getPointOfEntryDetails());

		InfrastructureValidator.InfrastructureData infrastructureData = infrastructureAndErrors.getElement0();
		List<ValidationErrorMessage> unmatchedFields = infrastructureAndErrors.getElement1();

		RegionReferenceDto responsibleRegion = caze.getResponsibleRegion();
		infrastructureData.responsibleRegion = regionFacade.loadLocal(responsibleRegion);
		if (responsibleRegion != null && infrastructureData.responsibleRegion == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasResponsibleRegion, responsibleRegion.getCaption()));
		}

		DistrictReferenceDto responsibleDistrict = caze.getResponsibleDistrict();
		infrastructureData.responsibleDistrict = districtFacade.loadLocal(responsibleDistrict);
		if (responsibleDistrict != null && infrastructureData.responsibleDistrict == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasResponsibleDistrict, responsibleDistrict.getCaption()));
		}

		CommunityReferenceDto responsibleCommunity = caze.getResponsibleCommunity();
		infrastructureData.responsibleCommunity = communityFacade.loadLocal(responsibleCommunity);
		if (responsibleCommunity != null && infrastructureData.responsibleCommunity == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasResponsibleCommunity, responsibleCommunity.getCaption()));
		}

		return infrastructureAndErrors;
	}

	public DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> loadLocalInfrastructure(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType facilityType,
		FacilityReferenceDto facility,
		String facilityDetails,
		PointOfEntryReferenceDto pointOfEntry,
		String pointOfEntryDetails) {
		return loadLocalInfrastructure(
			null,
			null,
			null,
			region,
			district,
			community,
			facilityType,
			facility,
			facilityDetails,
			pointOfEntry,
			pointOfEntryDetails);
	}

	public DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> loadLocalInfrastructure(
		ContinentReferenceDto continent,
		SubcontinentReferenceDto subcontinent,
		CountryReferenceDto country,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType facilityType,
		FacilityReferenceDto facility,
		String facilityDetails,
		PointOfEntryReferenceDto pointOfEntry,
		String pointOfEntryDetails) {

		InfrastructureValidator.InfrastructureData infrastructureData = new InfrastructureValidator.InfrastructureData();
		List<ValidationErrorMessage> unmatchedFields = new ArrayList<>();

		infrastructureData.continent = continentFacade.loadLocal(continent);
		if (continent != null && infrastructureData.continent == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasContinent, Captions.continent, continent.getCaption()));
		}

		infrastructureData.subcontinent = subcontinentFacade.loadLocal(subcontinent);
		if (subcontinent != null && infrastructureData.subcontinent == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasSubcontinent, subcontinent.getCaption()));
		}

		infrastructureData.country = countryFacade.loadLocal(country);
		if (country != null && infrastructureData.country == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasCountry, country.getCaption()));
		}

		infrastructureData.region = regionFacade.loadLocal(region);
		if (region != null && infrastructureData.region == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasRegion, region.getCaption()));
		}

		infrastructureData.district = districtFacade.loadLocal(district);
		if (district != null && infrastructureData.district == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasDistrict, district.getCaption()));
		}

		infrastructureData.community = communityFacade.loadLocal(community);
		if (community != null && infrastructureData.community == null) {
			unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasCommunity, community.getCaption()));
		}

		if (facility != null) {
			InfrastructureValidator.WithDetails<FacilityReferenceDto> localFacility = loadLocalFacility(facility, facilityType, facilityDetails);

			if (localFacility.entity == null) {
				unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasFacility, facility.getCaption()));
			} else {
				infrastructureData.facility = localFacility.entity;
				infrastructureData.facilityDetails = localFacility.details;
			}
		}

		if (pointOfEntry != null) {
			InfrastructureValidator.WithDetails<PointOfEntryReferenceDto> localPointOfEntry =
				loadLocalPointOfEntry(pointOfEntry, pointOfEntryDetails);

			if (localPointOfEntry.entity == null) {
				unmatchedFields.add(new ValidationErrorMessage(Validations.sormasToSormasPointOfEntry, pointOfEntry.getCaption()));
			} else {
				infrastructureData.pointOfEntry = localPointOfEntry.entity;
				infrastructureData.pointOfEntryDetails = localPointOfEntry.details;
			}
		}

		return new DataHelper.Pair<>(infrastructureData, unmatchedFields);
	}

	public void handleInfraStructure(
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<InfrastructureValidator.InfrastructureData> onNoErrors) {

		List<ValidationErrorMessage> errors = infrastructureAndErrors.getElement1();
		if (errors.size() > 0) {
			for (ValidationErrorMessage error : errors) {
				validationErrors.add(new ValidationErrorGroup(groupNameTag), error);
			}
		} else {
			onNoErrors.accept(infrastructureAndErrors.getElement0());
		}

	}

	public void processLocation(LocationDto address, String groupNameTag, ValidationErrors validationErrors) {
		DataHelper.Pair<InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors = loadLocalInfrastructure(
			address.getContinent(),
			address.getSubcontinent(),
			address.getCountry(),
			address.getRegion(),
			address.getDistrict(),
			address.getCommunity(),
			address.getFacilityType(),
			address.getFacility(),
			address.getFacilityDetails(),
			null,
			null);

		handleInfraStructure(infrastructureAndErrors, groupNameTag, validationErrors, (infrastructure -> {
			address.setContinent(infrastructure.getContinent());
			address.setSubcontinent(infrastructure.getSubcontinent());
			address.setCountry(infrastructure.getCountry());
			address.setRegion(infrastructure.region);
			address.setDistrict(infrastructure.district);
			address.setCommunity(infrastructure.community);
			address.setFacility(infrastructure.facility);
			address.setFacilityDetails(infrastructure.facilityDetails);
		}));
	}

	public static class InfrastructureData {

		private ContinentReferenceDto continent;
		private SubcontinentReferenceDto subcontinent;
		private CountryReferenceDto country;
		private RegionReferenceDto responsibleRegion;
		private DistrictReferenceDto responsibleDistrict;
		private CommunityReferenceDto responsibleCommunity;
		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private String facilityDetails;
		private PointOfEntryReferenceDto pointOfEntry;
		private String pointOfEntryDetails;

		public ContinentReferenceDto getContinent() {
			return continent;
		}

		public SubcontinentReferenceDto getSubcontinent() {
			return subcontinent;
		}

		public CountryReferenceDto getCountry() {
			return country;
		}

		public RegionReferenceDto getResponsibleRegion() {
			return responsibleRegion;
		}

		public DistrictReferenceDto getResponsibleDistrict() {
			return responsibleDistrict;
		}

		public CommunityReferenceDto getResponsibleCommunity() {
			return responsibleCommunity;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public CommunityReferenceDto getCommunity() {
			return community;
		}

		public FacilityReferenceDto getFacility() {
			return facility;
		}

		public String getFacilityDetails() {
			return facilityDetails;
		}

		public PointOfEntryReferenceDto getPointOfEntry() {
			return pointOfEntry;
		}

		public String getPointOfEntryDetails() {
			return pointOfEntryDetails;
		}
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
