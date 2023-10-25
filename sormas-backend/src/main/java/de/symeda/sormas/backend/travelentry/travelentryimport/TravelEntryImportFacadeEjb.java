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

package de.symeda.sormas.backend.travelentry.travelentryimport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportCellData;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.travelentry.DeaContentEntry;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.travelentryimport.TravelEntryImportEntities;
import de.symeda.sormas.api.travelentry.travelentryimport.TravelEntryImportFacade;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless(name = "TravelEntryImportFacade")
public class TravelEntryImportFacadeEjb implements TravelEntryImportFacade {

	private final Logger LOGGER = LoggerFactory.getLogger(TravelEntryImportFacadeEjb.class);

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);

	private static final String PHONE_PRIVATE = "phonePrivate";
	private static final String PHONE_ADDITIONAL = "phoneAdditional";
	private static final String EMAIL = "email";
	private static final String DATE_OF_ARRIVAL = "einreisedatum";

	@EJB
	private TravelEntryFacadeEjbLocal travelEntryFacade;
	@EJB
	private UserService userService;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private EnumService enumService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	@Override
	public ImportLineResultDto<TravelEntryImportEntities> importData(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries) {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			return ImportLineResultDto.errorResult(I18nProperties.getValidationError(Validations.importLineTooLong));
		}

		final TravelEntryImportEntities entities = new TravelEntryImportEntities(userService.getCurrentUser().toReference());
		TravelEntryDto travelEntry = entities.getTravelEntry();
		fillTravelEntryWithDefaultValues(travelEntry);
		ImportLineResultDto<TravelEntryImportEntities> importResult =
			buildEntities(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, entities);
		if (importResult.isError()) {
			return importResult;
		}

		ImportLineResultDto<TravelEntryImportEntities> validationResult = validateEntities(entities);
		if (validationResult.isError()) {
			return validationResult;
		}

		PersonDto person = entities.getPerson();

		if (personFacade.isPersonSimilarToExisting(person)) {
			return ImportLineResultDto.duplicateResult(entities);
		}

		return saveImportedEntities(entities);
	}

	@Override
	public ImportLineResultDto<TravelEntryImportEntities> importDataWithExistingPerson(
		String personUuid,
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths) {

		TravelEntryImportEntities entities =
			new TravelEntryImportEntities(userService.getCurrentUser().toReference(), personFacade.getByUuid(personUuid));
		fillTravelEntryWithDefaultValues(entities.getTravelEntry());
		ImportLineResultDto<TravelEntryImportEntities> importResult = buildEntities(values, entityClasses, entityPropertyPaths, true, entities);

		if (importResult.isError()) {
			return importResult;
		}

		return saveImportedEntities(entities);
	}

	private void fillTravelEntryWithDefaultValues(TravelEntryDto travelEntry) {
		User currentUser = userService.getCurrentUser();
		Set<Disease> userDiseases = currentUser.getLimitedDiseases();
		Region userRegion = currentUser.getRegion();
		District userDistrict = currentUser.getDistrict();
		Community userCommunity = currentUser.getCommunity();
		PointOfEntry userPoe = currentUser.getPointOfEntry();

		if (CollectionUtils.isNotEmpty(userDiseases) && userDiseases.size() == 1) {
			travelEntry.setDisease(userDiseases.iterator().next());
		} else {
			List<Disease> allDiseases = diseaseConfigurationFacade.getAllDiseases(true, true, true);
			if (allDiseases.size() == 1) {
				travelEntry.setDisease(allDiseases.get(0));
			}
		}

		if (userRegion != null) {
			travelEntry.setResponsibleRegion(new RegionReferenceDto(userRegion.getUuid()));
			if (userDistrict != null) {
				travelEntry.setResponsibleDistrict(new DistrictReferenceDto(userDistrict.getUuid()));
				if (userCommunity != null) {
					travelEntry.setResponsibleCommunity(new CommunityReferenceDto(userCommunity.getUuid()));
				}
				if (userPoe != null) {
					travelEntry.setPointOfEntry(new PointOfEntryReferenceDto(userPoe.getUuid()));
				}
			}
		}
	}

	@Override
	public ImportLineResultDto<TravelEntryImportEntities> saveImportedEntities(@Valid TravelEntryImportEntities entities) {

		TravelEntryDto travelEntry = entities.getTravelEntry();
		PersonDto person = entities.getPerson();

		try {
			PersonDto savedPerson = personFacade.save(person);
			travelEntry.setPerson(savedPerson.toReference());
			travelEntry.setChangeDate(new Date());
			travelEntryFacade.save(travelEntry);
			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	private ImportLineResultDto<TravelEntryImportEntities> validateEntities(TravelEntryImportEntities entities) {
		ImportLineResultDto<TravelEntryImportEntities> validationResult = importFacade.validateConstraints(entities);
		if (validationResult.isError()) {
			return validationResult;
		}

		try {
			personFacade.validate(entities.getPerson());
			travelEntryFacade.validate(entities.getTravelEntry());
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}

		return ImportLineResultDto.successResult();
	}

	private ImportLineResultDto<TravelEntryImportEntities> buildEntities(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		TravelEntryImportEntities entities) {

		ImportLineResultDto<TravelEntryImportEntities> importResult =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, cellData -> {
				try {
					TravelEntryDto travelEntry = entities.getTravelEntry();
					if (StringUtils.isNotEmpty(cellData.getValue())) {
						// If the cell entry is not empty, try to insert it into the current travel entry or its person
						insertColumnEntryIntoData(travelEntry, entities.getPerson(), cellData.getValue(), cellData.getEntityPropertyPath());
					}
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			});

		if (!importResult.isError()) {
			TravelEntryDto travelEntry = entities.getTravelEntry();
			if (travelEntry.getPointOfEntry() == null && configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
				travelEntry.setPointOfEntry(pointOfEntryFacade.getByUuid(PointOfEntryDto.OTHER_POE_UUID).toReference());
				travelEntry.setPointOfEntryDetails(I18nProperties.getString(Strings.messageTravelEntryPOEFilledBySystem));
			}
		}

		return importResult;
	}

	private ImportLineResultDto<TravelEntryImportEntities> insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback) {

		String importError = null;
		List<String> invalidColumns = new ArrayList<>();

		for (int i = 0; i < values.length; i++) {
			String value = StringUtils.trimToNull(values[i]);
			if (ignoreEmptyEntries && StringUtils.isBlank(value)) {
				continue;
			}

			String[] entityPropertyPath = entityPropertyPaths[i];
			// Error description column is ignored
			if (entityPropertyPath[0].equals(ERROR_COLUMN_NAME)) {
				continue;
			}

			if (!(ignoreEmptyEntries && StringUtils.isEmpty(value))) {
				Exception exception =
					insertCallback.apply(new ImportCellData(value, entityClasses != null ? entityClasses[i] : null, entityPropertyPath));
				if (exception != null) {
					if (exception instanceof ImportErrorException) {
						importError = exception.getMessage();
						StringBuilder additionalInfo = new StringBuilder();
						for (String s : entityPropertyPath) {
							additionalInfo.append(" ").append(s);
						}
						importError += additionalInfo;
						importError += "value:" + value;
						break;
					} else if (exception instanceof InvalidColumnException) {
						invalidColumns.add(((InvalidColumnException) exception).getColumnName());
					}
				}
			}
		}

		if (invalidColumns.size() > 0) {
			LOGGER.warn("Unhandled columns [{}]", String.join(", ", invalidColumns));
		}

		return importError != null ? ImportLineResultDto.errorResult(importError) : ImportLineResultDto.successResult();
	}

	private void insertColumnEntryIntoData(TravelEntryDto travelEntry, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		String propertyCaption = String.join("", entryHeaderPath);
		// Build the SORMAS property based on the DEA caption
		String personProperty = getPersonProperty(propertyCaption);
		Object currentElement = personProperty != null ? person : travelEntry;
		if (personProperty != null) {
			// Map the entry to an expected SORMAS value if necessary
			entry = getPersonValue(personProperty, entry);
		}
		Language language = I18nProperties.getUserLanguage();

		try {
			// Some person-related fields need to be handled in a specific way for the DEA import
			if (PersonDto.BIRTH_DATE.equals(personProperty)) {
				Date birthDate = DateHelper.parseDate(entry, new SimpleDateFormat("dd.MM.yyyy"));
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(birthDate);
				person.setBirthdateDD(calendar.get(Calendar.DAY_OF_MONTH));
				// In calendar API months are indexed from 0 @see https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html#MONTH
				int birthdateMonth = calendar.get(Calendar.MONTH) + 1;
				person.setBirthdateMM(birthdateMonth);
				person.setBirthdateYYYY(calendar.get(Calendar.YEAR));
				return;
			} else if (PHONE_PRIVATE.equals(personProperty)) {
				person.setPhone(entry);
				return;
			} else if (PHONE_ADDITIONAL.equals(personProperty)) {
				person.setAdditionalPhone(entry);
				return;
			} else if (EMAIL.equals(personProperty)) {
				person.setEmailAddress(entry);
				return;
			} else if (DATE_OF_ARRIVAL.equalsIgnoreCase(propertyCaption)) {
				travelEntry.setDateOfArrival(DateHelper.parseDateWithException(entry, I18nProperties.getUserLanguage()));
				return;
			}

			String relevantProperty = personProperty != null ? personProperty : propertyCaption;
			PropertyDescriptor pd = new PropertyDescriptor(relevantProperty, currentElement.getClass());
			Class<?> propertyType = pd.getPropertyType();

			// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
			// according to the types of the case or person fields
			if (importFacade.executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath, false)) {
				// No action needed
			} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
				List<DistrictReferenceDto> district = districtFacade
					.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), null, null, travelEntry, person, currentElement), false);
				if (district.isEmpty()) {
					throw new ImportErrorException(
						I18nProperties
							.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildEntityProperty(entryHeaderPath)));
				} else if (district.size() > 1) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildEntityProperty(entryHeaderPath)));
				} else {
					pd.getWriteMethod().invoke(currentElement, district.get(0));
				}
			} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
				List<CommunityReferenceDto> community = communityFacade.getByName(entry, travelEntry.getResponsibleDistrict(), false);
				if (community.isEmpty()) {
					throw new ImportErrorException(
						I18nProperties
							.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildEntityProperty(entryHeaderPath)));
				} else if (community.size() > 1) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry, buildEntityProperty(entryHeaderPath)));
				} else {
					pd.getWriteMethod().invoke(currentElement, community.get(0));
				}
			} else if (propertyType.isAssignableFrom(PointOfEntryReferenceDto.class)) {
				PointOfEntryReferenceDto pointOfEntryReference;
				DistrictReferenceDto pointOfEntryDistrict =
					travelEntry.getPointOfEntryDistrict() != null ? travelEntry.getPointOfEntryDistrict() : travelEntry.getResponsibleDistrict();
				List<PointOfEntryReferenceDto> customPointsOfEntry = pointOfEntryFacade.getByName(entry, pointOfEntryDistrict, false);
				if (customPointsOfEntry.isEmpty()) {
					final String poeName = entry;
					List<PointOfEntryDto> defaultPointOfEntries = pointOfEntryFacade.getByUuids(PointOfEntryDto.CONSTANT_POE_UUIDS);
					Optional<PointOfEntryDto> defaultPointOfEntry = defaultPointOfEntries.stream()
						.filter(
							defaultPoe -> InfrastructureHelper.buildPointOfEntryString(defaultPoe.getUuid(), defaultPoe.getName()).equals(poeName))
						.findFirst();
					if (!defaultPointOfEntry.isPresent()) {
						throw new ImportErrorException(
							I18nProperties
								.getValidationError(Validations.importEntryDoesNotExistDbOrDistrict, entry, buildEntityProperty(entryHeaderPath)));
					}
					pointOfEntryReference = defaultPointOfEntry.get().toReference();
				} else if (customPointsOfEntry.size() > 1) {
					throw new ImportErrorException(
						I18nProperties
							.getValidationError(Validations.importPointOfEntryNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));

				} else {
					pointOfEntryReference = customPointsOfEntry.get(0);
				}

				pd.getWriteMethod().invoke(currentElement, pointOfEntryReference);
			} else {
				throw new UnsupportedOperationException(
					I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
			}
		} catch (IntrospectionException e) {
			// Add the property to the deaContent field of the travel entry
			if (travelEntry.getDeaContent() == null) {
				travelEntry.setDeaContent(new ArrayList<>());
			}
			travelEntry.getDeaContent().add(new DeaContentEntry(propertyCaption, entry));
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
		} catch (IllegalArgumentException | EnumService.InvalidEnumCaptionException e) {
			throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
		} catch (ParseException e) {
			throw new ImportErrorException(
				I18nProperties.getValidationError(
					Validations.importInvalidDate,
					buildEntityProperty(entryHeaderPath),
					DateHelper.getAllowedDateFormats(language.getDateFormat())));
		} catch (ImportErrorException | UnsupportedOperationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("Unexpected error when trying to import a travel entry: " + e.getMessage(), e);
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
		}
	}

	private String getPersonProperty(String propertyCaption) {
		switch (propertyCaption.toLowerCase().trim()) {
		case "nachname":
			return PersonDto.LAST_NAME;
		case "vorname":
			return PersonDto.FIRST_NAME;
		case "geschlecht":
			return PersonDto.SEX;
		case "geburtsdatum":
			return PersonDto.BIRTH_DATE;
		case "tel persönlich":
			return PHONE_PRIVATE;
		case "tel weitere":
			return PHONE_ADDITIONAL;
		case "e-mail adresse":
			return EMAIL;
		default:
			return null;
		}
	}

	private String getPersonValue(String personProperty, String value) {
		try {
			if (PersonDto.SEX.equals(personProperty)) {
				return enumService.getEnumByCaption((Class<Enum>) (Class<?>) Sex.class, value).name();
			}
			return value;
		} catch (IllegalArgumentException | EnumService.InvalidEnumCaptionException e) {
			return value;
		}
	}

	protected String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}

	@LocalBean
	@Stateless
	public static class TravelEntryImportFacadeEjbLocal extends TravelEntryImportFacadeEjb {

	}
}
