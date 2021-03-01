/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.dataprocessor;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CountryFacadeEjb;
import de.symeda.sormas.backend.region.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class SharedDataProcessorHelper {

	@EJB
	private UserService userService;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private CountryFacadeEjbLocal countryFacade;

	public ValidationErrors processOriginInfo(SormasToSormasOriginInfoDto originInfo, String validationGroupCaption) {
		if (originInfo == null) {
			return ValidationErrors.create(
				I18nProperties.getCaption(validationGroupCaption),
				I18nProperties.getValidationError(Validations.sormasToSormasShareInfoMissing));
		}

		ValidationErrors validationErrors = new ValidationErrors();

		if (originInfo.getOrganizationId() == null) {
			validationErrors.add(
				I18nProperties.getCaption(Captions.CaseData_sormasToSormasOriginInfo),
				I18nProperties.getValidationError(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			validationErrors.add(
				I18nProperties.getCaption(Captions.CaseData_sormasToSormasOriginInfo),
				I18nProperties.getValidationError(Validations.sormasToSormasSenderNameMissing));
		}

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		return validationErrors;
	}

	public ValidationErrors processPerson(PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		processLocation(person.getAddress(), Captions.Person, validationErrors);

		person.getAddresses().forEach(address -> {
			processLocation(address, Captions.Person, validationErrors);
		});

		person.setBirthCountry(loadLocalCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors));
		person.setCitizenship(loadLocalCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors));

		return validationErrors;
	}

	public DataHelper.Pair<InfrastructureData, List<String>> loadLocalInfrastructure(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community) {
		return loadLocalInfrastructure(region, district, community, null, null, null, null, null);
	}

	public DataHelper.Pair<InfrastructureData, List<String>> loadLocalInfrastructure(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		CommunityReferenceDto community,
		FacilityType facilityType,
		FacilityReferenceDto facility,
		String facilityDetails,
		PointOfEntryReferenceDto pointOfEntry,
		String pointOfEntryDetails) {

		InfrastructureData infrastructureData = new InfrastructureData();
		List<String> unmatchedFields = new ArrayList<>();

		infrastructureData.region = loadLocalRegion(region);
		if (region != null && infrastructureData.region == null) {
			unmatchedFields.add(I18nProperties.getCaption(Captions.region) + ": " + region.getCaption());
		}

		infrastructureData.district = loadLocalDistrict(district);
		if (district != null && infrastructureData.district == null) {
			unmatchedFields.add(I18nProperties.getCaption(Captions.district) + ": " + district.getCaption());
		}

		infrastructureData.community = loadLocalCommunity(community);

		if (facility != null) {
			WithDetails<FacilityReferenceDto> localFacility = loadLocalFacility(facility, facilityType, facilityDetails);

			if (localFacility.entity == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.facility) + ": " + facility.getCaption());
			} else {
				infrastructureData.facility = localFacility.entity;
				infrastructureData.facilityDetails = localFacility.details;
			}
		}

		if (pointOfEntry != null) {
			WithDetails<PointOfEntryReferenceDto> localPointOfEntry = loadLocalPointOfEntry(pointOfEntry, pointOfEntryDetails);

			if (localPointOfEntry.entity == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.pointOfEntry) + ": " + pointOfEntry.getCaption());
			} else {
				infrastructureData.pointOfEntry = localPointOfEntry.entity;
				infrastructureData.pointOfEntryDetails = localPointOfEntry.details;
			}
		}

		return new DataHelper.Pair(infrastructureData, unmatchedFields);
	}

	public void handleInfraStructure(
		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors,
		String groupNameTag,
		ValidationErrors validationErrors,
		Consumer<InfrastructureData> onNoErrors) {

		List<String> errors = infrastructureAndErrors.getElement1();
		if (errors.size() > 0) {
			validationErrors.add(
				I18nProperties.getCaption(groupNameTag),
				String.format(I18nProperties.getString(Strings.errorSormasToSormasInfrastructure), String.join(",", errors)));

		} else {
			onNoErrors.accept(infrastructureAndErrors.getElement0());
		}
	}

	private CountryReferenceDto loadLocalCountry(CountryReferenceDto country, String validationGroupTag, ValidationErrors validationErrors) {
		if (country == null) {
			return null;
		}

		CountryDto localCountry = countryFacade.getByIsoCode(country.getIsoCode(), true);

		if (localCountry == null) {
			validationErrors.add(
				I18nProperties.getCaption(validationGroupTag),
				String.format(I18nProperties.getString(Strings.errorSormasToSormasCountry), country.getCaption() + "(" + country.getIsoCode() + ")"));
		}

		return CountryFacadeEjb.toReferenceDto(localCountry);
	}

	public Map<String, ValidationErrors> processSamples(List<SormasToSormasSampleDto> samples) {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();

		samples.forEach(sormasToSormasSample -> {
			SampleDto sample = sormasToSormasSample.getSample();
			ValidationErrors sampleErrors = new ValidationErrors();

			sample.setReportingUser(userService.getCurrentUser().toReference());

			DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors =
				loadLocalInfrastructure(null, null, null, null, sample.getLab(), sample.getLabDetails(), null, null);

			handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, sampleErrors, (infrastructureData -> {
				sample.setLab(infrastructureData.facility);
				sample.setLabDetails(infrastructureData.facilityDetails);
			}));

			if (sampleErrors.hasError()) {
				validationErrors.put(buildSampleValidationGroupName(sample), sampleErrors);
			}

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {
				DataHelper.Pair<InfrastructureData, List<String>> ptInfrastructureAndErrors = loadLocalInfrastructure(
					null,
					null,
					null,
					FacilityType.LABORATORY,
					pathogenTest.getLab(),
					pathogenTest.getLabDetails(),
					null,
					null);

				ValidationErrors pathogenTestErrors = new ValidationErrors();
				handleInfraStructure(ptInfrastructureAndErrors, Captions.PathogenTest_lab, pathogenTestErrors, (infrastructureData -> {
					pathogenTest.setLab(infrastructureData.facility);
					pathogenTest.setLabDetails(infrastructureData.facilityDetails);
				}));

				if (pathogenTestErrors.hasError()) {
					validationErrors.put(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors);
				}
			});
		});

		return validationErrors;
	}

	public ValidationErrors processContactData(ContactDto contact, PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		processPerson(person);

		contact.setPerson(person.toReference());
		contact.setReportingUser(userService.getCurrentUser().toReference());

		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors =
			loadLocalInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.region);
			contact.setDistrict(infrastructure.district);
			contact.setCommunity(infrastructure.community);
		}));

		processEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public void processEpiData(EpiDataDto epiData, ValidationErrors validationErrors) {
		if (epiData != null) {
			epiData.getExposures().forEach(exposure -> {
				LocationDto exposureLocation = exposure.getLocation();
				if (exposureLocation != null) {
					processLocation(exposureLocation, Captions.EpiData_exposures, validationErrors);
				}
			});
		}
	}

	private void processLocation(LocationDto address, String groupNameTag, ValidationErrors validationErrors) {
		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors = loadLocalInfrastructure(
			address.getRegion(),
			address.getDistrict(),
			address.getCommunity(),
			address.getFacilityType(),
			address.getFacility(),
			address.getFacilityDetails(),
			null,
			null);

		handleInfraStructure(infrastructureAndErrors, groupNameTag, validationErrors, (infrastructure -> {
			address.setRegion(infrastructure.region);
			address.setDistrict(infrastructure.district);
			address.setCommunity(infrastructure.community);
			address.setFacility(infrastructure.facility);
			address.setFacilityDetails(infrastructure.facilityDetails);
		}));
	}

	private RegionReferenceDto loadLocalRegion(RegionReferenceDto region) {
		if (region == null) {
			return null;
		}

		Optional<RegionReferenceDto> localRegion =
			region.getExternalId() != null ? regionFacade.getByExternalId(region.getExternalId(), false).stream().findFirst() : Optional.empty();

		if (!localRegion.isPresent()) {
			localRegion = regionFacade.getByName(region.getCaption(), false).stream().findFirst();
		}

		return localRegion.orElse(null);
	}

	private DistrictReferenceDto loadLocalDistrict(DistrictReferenceDto district) {
		if (district == null) {
			return null;
		}

		Optional<DistrictReferenceDto> localDistrict = district.getExternalId() != null
			? districtFacade.getByExternalId(district.getExternalId(), false).stream().findFirst()
			: Optional.empty();

		if (!localDistrict.isPresent()) {
			localDistrict = districtFacade.getByName(district.getCaption(), null, false).stream().findFirst();
		}

		return localDistrict.orElse(null);
	}

	private CommunityReferenceDto loadLocalCommunity(CommunityReferenceDto community) {
		if (community == null) {
			return null;
		}

		Optional<CommunityReferenceDto> localCommunity = community.getExternalId() != null
			? communityFacade.getByExternalId(community.getExternalId(), false).stream().findFirst()
			: Optional.empty();

		if (!localCommunity.isPresent()) {
			localCommunity = communityFacade.getByName(community.getCaption(), null, false).stream().findFirst();
		}

		return localCommunity.orElse(null);
	}

	private WithDetails<FacilityReferenceDto> loadLocalFacility(FacilityReferenceDto facility, FacilityType facilityType, String facilityDetails) {
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

	public static class InfrastructureData {

		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private String facilityDetails;
		private PointOfEntryReferenceDto pointOfEntry;
		private String pointOfEntryDetails;

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
