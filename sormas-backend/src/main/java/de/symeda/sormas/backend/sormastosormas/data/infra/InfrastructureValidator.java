/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.sormastosormas.data.infra;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
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
import java.io.Serializable;
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

	private WithDetails<FacilityReferenceDto> lookupLocalFacility(FacilityReferenceDto facility, FacilityType facilityType, String facilityDetails) {
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

			return new WithDetails<>(localFacility, facilityDetails);
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

			return localFacility.map(f -> new WithDetails<>(f, details)).orElse(null);
		}
	}

	private WithDetails<PointOfEntryReferenceDto> lookupPointOfEntry(PointOfEntryReferenceDto pointOfEntry, String pointOfEntryDetails) {
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

			return new WithDetails<>(localPointOfEntry, pointOfEntryDetails);
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

			return localPointOfEntry.map(p -> new WithDetails<>(p, details)).orElse(null);
		}
	}

	private <DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria> void validateInfra(
		InfrastructureDataReferenceDto dto,
		String groupNameTag,
		ValidationErrors validationErrors,
		InfrastructureBaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> facade,
		String i18property,
		Consumer<REF_DTO> onNoErrors) {
		if (dto != null) {
			REF_DTO match = facade.getReferenceByUuid(dto.getUuid());
			if (match != null) {
				onNoErrors.accept(match);
			} else {
				validationErrors.add(new ValidationErrorGroup(groupNameTag), new ValidationErrorMessage(i18property, dto.getCaption()));
			}
		}
	}

	public void validateContinent(
		ContinentReferenceDto continent,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<ContinentReferenceDto> onNoErrors) {
		validateInfra(continent, groupNameTag, validationErrors, continentFacade, Validations.sormasToSormasContinent, onNoErrors);
	}

	public void validateSubcontinent(
		SubcontinentReferenceDto subcontinent,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<SubcontinentReferenceDto> onNoErrors) {

		validateInfra(subcontinent, groupNameTag, validationErrors, subcontinentFacade, Validations.sormasToSormasSubcontinent, onNoErrors);
	}

	public void validateCountry(
		CountryReferenceDto country,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<CountryReferenceDto> onNoErrors) {
		validateInfra(country, groupNameTag, validationErrors, countryFacade, Validations.sormasToSormasCountry, onNoErrors);
	}

	public void validateRegion(
		RegionReferenceDto region,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<RegionReferenceDto> onNoErrors) {
		validateInfra(region, groupNameTag, validationErrors, regionFacade, Validations.sormasToSormasRegion, onNoErrors);
	}

	public void validateDistrit(
		DistrictReferenceDto district,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<DistrictReferenceDto> onNoErrors) {
		validateInfra(district, groupNameTag, validationErrors, districtFacade, Validations.sormasToSormasDistrict, onNoErrors);
	}

	public void validateCommunity(
		CommunityReferenceDto community,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<CommunityReferenceDto> onNoErrors) {
		validateInfra(
			community,
			groupNameTag,
			validationErrors,
			communityFacade,
			Validations.sormasToSormasCommunity,

			onNoErrors);
	}

	public void validateResponsibleRegion(
		RegionReferenceDto region,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<RegionReferenceDto> onNoErrors) {
		validateInfra(region, groupNameTag, validationErrors, regionFacade, Validations.sormasToSormasResponsibleRegion, onNoErrors);
	}

	public void validateResponsibleDistrict(
		DistrictReferenceDto district,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<DistrictReferenceDto> onNoErrors) {
		validateInfra(district, groupNameTag, validationErrors, districtFacade, Validations.sormasToSormasResponsibleDistrict, onNoErrors);
	}

	public void validateResponsibleCommunity(
		CommunityReferenceDto community,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<CommunityReferenceDto> onNoErrors) {
		validateInfra(community, groupNameTag, validationErrors, communityFacade, Validations.sormasToSormasResponsibleCommunity, onNoErrors);
	}

	public void validateFacility(
		FacilityReferenceDto facility,
		FacilityType facilityType,
		String facilityDetails,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<InfrastructureValidator.WithDetails<FacilityReferenceDto>> onNoErrors) {

		if (facility != null) {
			InfrastructureValidator.WithDetails<FacilityReferenceDto> localFacility = lookupLocalFacility(facility, facilityType, facilityDetails);

			if (localFacility == null || localFacility.entity == null) {
				validationErrors.add(
					new ValidationErrorGroup(groupNameTag),
					new ValidationErrorMessage(Validations.sormasToSormasFacility, facility.getCaption()));
			} else {
				onNoErrors.accept(localFacility);
			}
		}
	}

	public void validatePointOfEntry(
		PointOfEntryReferenceDto pointOfEntry,
		String pointOfEntryDetails,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<InfrastructureValidator.WithDetails<PointOfEntryReferenceDto>> onNoErrors) {

		if (pointOfEntry != null) {
			InfrastructureValidator.WithDetails<PointOfEntryReferenceDto> localPointOfEntry = lookupPointOfEntry(pointOfEntry, pointOfEntryDetails);

			if (localPointOfEntry == null || localPointOfEntry.entity == null) {
				validationErrors.add(
					new ValidationErrorGroup(groupNameTag),
					new ValidationErrorMessage(Validations.sormasToSormasPointOfEntry, pointOfEntry.getCaption()));
			} else {
				onNoErrors.accept(localPointOfEntry);
			}
		}
	}

	public static class WithDetails<T> {

		public WithDetails(T entity, String details) {
			this.entity = entity;
			this.details = details;
		}

		private final T entity;
		private String details;

		public T getEntity() {
			return entity;
		}

		public String getDetails() {
			return details;
		}
	}
}
