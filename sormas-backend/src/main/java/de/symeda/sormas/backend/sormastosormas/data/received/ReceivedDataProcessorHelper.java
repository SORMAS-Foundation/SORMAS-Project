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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
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
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class ReceivedDataProcessorHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReceivedDataProcessorHelper.class);

	@EJB
	private UserService userService;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;

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

	public ValidationErrors processPerson(PersonDto person, PersonDto existingPerson) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateLocation(person.getAddress(), Captions.Person, validationErrors);

		person.getAddresses().forEach(address -> infraValidator.validateLocation(address, Captions.Person, validationErrors));

		CountryReferenceDto birthCountry = processCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors);
		person.setBirthCountry(birthCountry);

		CountryReferenceDto citizenship = processCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors);
		person.setCitizenship(citizenship);

		handleIgnoredProperties(person, existingPerson);

		return validationErrors;
	}

	public ValidationErrors processPersonPreview(SormasToSormasPersonPreview person) {
		ValidationErrors validationErrors = new ValidationErrors();

		infraValidator.validateLocation(person.getAddress(), Captions.Person, validationErrors);

		return validationErrors;
	}

	private CountryReferenceDto processCountry(CountryReferenceDto country, String errorCaption, ValidationErrors validationErrors) {
		CountryReferenceDto localCountry = infraValidator.lookupLocalCountry(country);
		if (country != null && localCountry == null) {
			validationErrors
				.add(new ValidationErrorGroup(errorCaption), new ValidationErrorMessage(Validations.sormasToSormasCountry, country.getCaption()));
		}
		return localCountry;
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

			DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
				infraValidator.validateInfrastructure(
					null,
					null,
					null,
					null,
					null,
					null,
					// todo shouldn't this be FacilityType.LABORATORY?
					null,
					sample.getLab(),
					sample.getLabDetails(),
					null,
					null);

			infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, sampleErrors, (infrastructureData -> {
				sample.setLab(infrastructureData.getFacility());
				sample.setLabDetails(infrastructureData.getFacilityDetails());
			}));

			if (sampleErrors.hasError()) {
				validationErrors.add(new ValidationErrors(buildSampleValidationGroupName(sample), sampleErrors));
			}

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {
				DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> ptInfrastructureAndErrors =
					infraValidator.validateInfrastructure(
						null,
						null,
						null,
						null,
						null,
						null,
						FacilityType.LABORATORY,
						pathogenTest.getLab(),
						pathogenTest.getLabDetails(),
						null,
						null);

				ValidationErrors pathogenTestErrors = new ValidationErrors();
				infraValidator.handleInfraStructure(ptInfrastructureAndErrors, Captions.PathogenTest_lab, pathogenTestErrors, (infrastructureData -> {
					pathogenTest.setLab(infrastructureData.getFacility());
					pathogenTest.setLabDetails(infrastructureData.getFacilityDetails());
				}));

				if (pathogenTestErrors.hasError()) {
					validationErrors.add(new ValidationErrors(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors));
				}
			});
		});

		return validationErrors;
	}

	public ValidationErrors processContactData(ContactDto contact, PersonDto person, ContactDto existingContact) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = processPerson(person, getExistingPerson(existingContact));
		validationErrors.addAll(personValidationErrors);

		contact.setPerson(person.toReference());
		updateReportingUser(contact, existingContact);
		handleIgnoredProperties(contact, existingContact);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		processEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public ValidationErrors processContactPreview(SormasToSormasContactPreview contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		return validationErrors;
	}

	public void processEpiData(EpiDataDto epiData, ValidationErrors validationErrors) {
		if (epiData != null) {
			epiData.getExposures().forEach(exposure -> {
				LocationDto exposureLocation = exposure.getLocation();
				if (exposureLocation != null) {
					infraValidator.validateLocation(exposureLocation, Captions.EpiData_exposures, validationErrors);
				}
			});
			epiData.getActivitiesAsCase().forEach(activity -> {
				LocationDto activityLocation = activity.getLocation();
				if (activityLocation != null) {
					infraValidator.validateLocation(activityLocation, Captions.EpiData_activitiesAsCase, validationErrors);
				}
			});
		}
	}

	public void updateReportingUser(SormasToSormasEntityDto entity, SormasToSormasEntityDto originalEntity) {
		UserReferenceDto reportingUser = originalEntity == null ? userService.getCurrentUser().toReference() : originalEntity.getReportingUser();

		entity.setReportingUser(reportingUser);
	}

    public <T> void handleIgnoredProperties(T receivedEntity, T originalEntity) {
        Class<?> dtoType = receivedEntity.getClass();
        SormasToSormasConfig s2SConfig = configFacade.getS2SConfig();
        for (Field field : dtoType.getDeclaredFields()) {
            if (field.isAnnotationPresent(S2SIgnoreProperty.class)) {
                String s2sConfigProperty = field.getAnnotation(S2SIgnoreProperty.class).configProperty();
                if (s2SConfig.getIgnoreProperties().get(s2sConfigProperty)) {
                    field.setAccessible(true);
                    try {
                    	Object originalValue = originalEntity != null ? field.get(originalEntity) : null;
                        field.set(receivedEntity, originalValue);
                    } catch (IllegalAccessException e) {
                        LOGGER.error("Could not set field {} for {}", field.getName(), dtoType.getSimpleName());
                    }
                    field.setAccessible(false);
                }
            }
        }
    }

	public PersonDto getExitingPerson(@Nullable CaseDataDto existingCaseDto) {
		if (existingCaseDto == null) {
			return null;
		}
		return personFacade.getPersonByUuid(existingCaseDto.getPerson().getUuid());
	}

	public PersonDto getExistingPerson(@Nullable ContactDto existingContactDto) {
		if (existingContactDto == null) {
			return null;
		}
		return personFacade.getPersonByUuid(existingContactDto.getPerson().getUuid());
	}

	public PersonDto getExistingPerson(@Nullable EventParticipantDto eventParticipantDto) {
		if (eventParticipantDto == null) {
			return null;
		}
		return personFacade.getPersonByUuid(eventParticipantDto.getPerson().getUuid());
	}
}
