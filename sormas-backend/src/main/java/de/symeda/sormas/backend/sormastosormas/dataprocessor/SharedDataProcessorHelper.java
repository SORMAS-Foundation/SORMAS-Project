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
		CommunityReferenceDto community,
		FacilityType facilityType,
		FacilityReferenceDto facility,
		PointOfEntryReferenceDto pointOfEntry) {

		InfrastructureData infrastructureData = new InfrastructureData();
		List<String> unmatchedFields = new ArrayList<>();

		RegionReferenceDto localRegion = null;
		if (region != null) {
			String regionName = region.getCaption();
			localRegion = regionFacade.getByName(regionName, false).stream().findFirst().orElse(null);

			if (localRegion == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.region) + ": " + regionName);
			} else {
				infrastructureData.region = localRegion;
			}
		}

		DistrictReferenceDto localDistrict = null;
		if (district != null) {
			String districtName = district.getCaption();
			localDistrict = districtFacade.getByName(districtName, localRegion, false).stream().findFirst().orElse(null);
			if (localDistrict == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.district) + ": " + districtName);
			} else {
				infrastructureData.district = localDistrict;
			}
		}

		CommunityReferenceDto localCommunity = null;
		if (community != null) {
			String communityName = community.getCaption();
			localCommunity = communityFacade.getByName(communityName, localDistrict, false).stream().findFirst().orElse(null);

			if (localCommunity == null) {
				unmatchedFields.add(I18nProperties.getCaption(Captions.community) + ": " + communityName);
			} else {
				infrastructureData.community = localCommunity;
			}
		}

		if (facility != null) {
			String facilityUuid = facility.getUuid();
			if (FacilityDto.CONSTANT_FACILITY_UUIDS.contains(facilityUuid)) {
				infrastructureData.facility = facilityFacade.getByUuid(facilityUuid).toReference();
			} else {
				String facilityName = facility.getCaption();
				FacilityReferenceDto localFacility = facilityFacade.getByNameAndType(facilityName, localDistrict, localCommunity, facilityType, false)
					.stream()
					.findFirst()
					.orElse(null);

				if (localFacility == null) {
					unmatchedFields.add(I18nProperties.getCaption(Captions.facility) + ": " + facilityName);
				} else {
					infrastructureData.facility = localFacility;
				}
			}
		}

		if (pointOfEntry != null) {
			String pointOfEntryUuid = pointOfEntry.getUuid();

			if (PointOfEntryDto.CONSTANT_POE_UUIDS.contains(pointOfEntryUuid)) {
				infrastructureData.pointOfEntry = pointOfEntryFacade.getByUuid(pointOfEntryUuid).toReference();
			} else {
				String pointOfEntryName = pointOfEntry.getCaption();
				PointOfEntryReferenceDto localPointOfEntry =
					pointOfEntryFacade.getByName(pointOfEntryName, localDistrict, false).stream().findFirst().orElse(null);

				if (localPointOfEntry == null) {
					unmatchedFields.add(I18nProperties.getCaption(Captions.pointOfEntry) + ": " + pointOfEntryName);
				} else {
					infrastructureData.pointOfEntry = localPointOfEntry;
				}
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
				loadLocalInfrastructure(null, null, null, null, sample.getLab(), null);

			handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, sampleErrors, (infrastructureData -> {
				sample.setLab(infrastructureData.facility);
			}));

			if (sampleErrors.hasError()) {
				validationErrors.put(buildSampleValidationGroupName(sample), sampleErrors);
			}

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {
				DataHelper.Pair<InfrastructureData, List<String>> ptInfrastructureAndErrors =
					loadLocalInfrastructure(null, null, null, FacilityType.LABORATORY, pathogenTest.getLab(), null);

				ValidationErrors pathogenTestErrors = new ValidationErrors();
				handleInfraStructure(ptInfrastructureAndErrors, Captions.PathogenTest_lab, pathogenTestErrors, (infrastructureData -> {
					pathogenTest.setLab(infrastructureData.facility);
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
			loadLocalInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity(), null, null, null);

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
			null);

		handleInfraStructure(infrastructureAndErrors, groupNameTag, validationErrors, (infrastructure -> {
			address.setRegion(infrastructure.region);
			address.setDistrict(infrastructure.district);
			address.setCommunity(infrastructure.community);
			address.setFacility(infrastructure.facility);
		}));
	}

	public static class InfrastructureData {

		private RegionReferenceDto region;
		private DistrictReferenceDto district;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private PointOfEntryReferenceDto pointOfEntry;

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

		public PointOfEntryReferenceDto getPointOfEntry() {
			return pointOfEntry;
		}
	}
}
