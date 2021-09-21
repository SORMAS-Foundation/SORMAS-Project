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

package de.symeda.sormas.backend.sormastosormas.data.received;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SormasToSormasEntityDto;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb.ContinentFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class ReceivedDataProcessorHelper {

	@EJB
	private UserService userService;
	@EJB
	private ContinentFacadeEjbLocal continentFacade;
	@EJB
	private SubcontinentFacadeEjbLocal subcontinentFacade;
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
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private InfrastructureValidator infraValidator;

	public ValidationErrors processOriginInfo(SormasToSormasOriginInfoDto originInfo, String validationGroupCaption) {
		if (originInfo == null) {
			return ValidationErrors
				.create(new ValidationErrorGroup(validationGroupCaption), new ValidationErrorMessage(Validations.sormasToSormasShareInfoMissing));
		}

		ValidationErrors validationErrors = new ValidationErrors();

		if (originInfo.getOrganizationId() == null) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasSenderNameMissing));
		}

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		return validationErrors;
	}

	public ValidationErrors processPerson(PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		validationErrors.addAll(infraValidator.processLocation(person.getAddress(), Captions.Person));

		person.getAddresses().forEach(address -> {
			validationErrors.addAll(infraValidator.processLocation(address, Captions.Person));
		});

		validationErrors.addAll(
			infraValidator
				.processInfrastructure(InfrastructureValidator.CentralInfra.COUNTRY, person.getBirthCountry(), Captions.Person_birthCountry));

		validationErrors.addAll(
			infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.COUNTRY, person.getCitizenship(), Captions.Person_citizenship));

		return validationErrors;
	}

	public ValidationErrors processPersonPreview(SormasToSormasPersonPreview person) {
		ValidationErrors validationErrors = new ValidationErrors();
		validationErrors.addAll(infraValidator.processLocation(person.getAddress(), Captions.Person));
		return validationErrors;
	}

	public List<ValidationErrors> processSamples(List<SormasToSormasSampleDto> samples) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		Map<String, SampleDto> existingSamplesMap =
			sampleFacade.getByUuids(samples.stream().map(s -> s.getSample().getUuid()).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(SampleDto::getUuid, Function.identity()));

		samples.forEach(sormasToSormasSample -> {
			SampleDto sample = sormasToSormasSample.getSample();
			ValidationErrors sampleErrors = new ValidationErrors();

			updateReportingUser(sample, existingSamplesMap.get(sample.getUuid()));

			sampleErrors
				.addAll(infraValidator.processFacility(sample.getLab(), FacilityType.LABORATORY, sample.getLabDetails(), Captions.Sample_lab));

			if (sampleErrors.hasError()) {
				validationErrors.add(new ValidationErrors(buildSampleValidationGroupName(sample), sampleErrors));
			}

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {

				ValidationErrors pathogenTestErrors = new ValidationErrors();

				pathogenTestErrors.addAll(
					infraValidator
						.processFacility(pathogenTest.getLab(), FacilityType.LABORATORY, pathogenTest.getLabDetails(), Captions.Sample_lab));

				if (pathogenTestErrors.hasError()) {
					validationErrors.add(new ValidationErrors(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors));
				}
			});
		});

		return validationErrors;
	}

	public ValidationErrors processContactData(ContactDto contact, PersonDto person, ContactDto existingContact) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = processPerson(person);
		validationErrors.addAll(personValidationErrors);

		contact.setPerson(person.toReference());
		updateReportingUser(contact, existingContact);
		validationErrors
			.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.REGION, contact.getRegion(), Captions.Contact));
		validationErrors
			.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.DISTRICT, contact.getDistrict(), Captions.Contact));
		validationErrors
			.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.COMMUNITY, contact.getCommunity(), Captions.Contact));

		processEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public ValidationErrors processContactPreview(SormasToSormasContactPreview contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		validationErrors
				.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.REGION, contact.getRegion(), Captions.Contact));
		validationErrors
				.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.DISTRICT, contact.getDistrict(), Captions.Contact));
		validationErrors
				.addAll(infraValidator.processInfrastructure(InfrastructureValidator.CentralInfra.COMMUNITY, contact.getCommunity(), Captions.Contact));

		return validationErrors;
	}

	public void processEpiData(EpiDataDto epiData, ValidationErrors validationErrors) {
		// todo this should return validationErrors
		if (epiData != null) {
			epiData.getExposures().forEach(exposure -> {
				LocationDto exposureLocation = exposure.getLocation();
				if (exposureLocation != null) {
					validationErrors.addAll(infraValidator.processLocation(exposureLocation, Captions.EpiData_exposures));
				}
			});
			epiData.getActivitiesAsCase().forEach(activity -> {
				LocationDto activityLocation = activity.getLocation();
				if (activityLocation != null) {
					validationErrors.addAll(infraValidator.processLocation(activityLocation, Captions.EpiData_activitiesAsCase));
				}
			});
		}
	}

	public void updateReportingUser(SormasToSormasEntityDto entity, SormasToSormasEntityDto originalEntiy) {
		UserReferenceDto reportingUser = originalEntiy == null ? userService.getCurrentUser().toReference() : originalEntiy.getReportingUser();
		entity.setReportingUser(reportingUser);
	}

}
