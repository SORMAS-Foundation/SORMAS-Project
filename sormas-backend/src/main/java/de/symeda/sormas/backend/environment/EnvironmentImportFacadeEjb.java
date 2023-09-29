/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment;

import static de.symeda.sormas.backend.environment.EnvironmentFacadeEjb.EnvironmentFacadeEjbLocal;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentImportFacade;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportCellData;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.importexport.ImportHelper;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "EnvironmentImportFacade")
@RightsAllowed(UserRight._ENVIRONMENT_IMPORT)
public class EnvironmentImportFacadeEjb implements EnvironmentImportFacade {

	private static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private EnvironmentFacadeEjbLocal environmentFacade;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjb.CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;

	@Override
	public ImportLineResultDto<EnvironmentDto> importEnvironmentData(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries) {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			return ImportLineResultDto.errorResult(I18nProperties.getValidationError(Validations.importLineTooLong));
		}

		final EnvironmentDto environment = EnvironmentDto.build(userFacade.getCurrentUser());
		ImportLineResultDto<EnvironmentDto> importResult =
			buildEnvironment(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, environment);

		if (importResult.isError()) {
			return importResult;
		}

		ImportLineResultDto<EnvironmentDto> validationResult = validateEnvironment(environment);
		if (validationResult.isError()) {
			return validationResult;
		} else {
			LocationDto environmentLocation = environment.getLocation();
			EnvironmentCriteria criteria = new EnvironmentCriteria().country(environmentLocation.getCountry())
				.region(environmentLocation.getRegion())
				.district(environmentLocation.getDistrict())
				.gpsLat(environmentLocation.getLatitude())
				.gpsLon(environmentLocation.getLongitude())
				.environmentMedia(environment.getEnvironmentMedia())
				.externalId(environment.getExternalId());
			String similarEnvironmentUuid = environmentService.getSimilarEnvironmentUuid(criteria);
			if (similarEnvironmentUuid != null) {
				return ImportLineResultDto.duplicateResult(
					environment,
					String.format(I18nProperties.getString(Strings.messageDuplicateEnvironmentFound), similarEnvironmentUuid));
			}
		}

		return saveEnvironment(environment);
	}

	private ImportLineResultDto<EnvironmentDto> buildEnvironment(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		EnvironmentDto environment) {

		return insertRowIntoData(values, entityClasses, entityPropertyPaths, ignoreEmptyEntries, (cellData) -> {
			try {
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(environment, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});
	}

	protected ImportLineResultDto<EnvironmentDto> insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback) {

		String importError = null;
		List<String> invalidColumns = new ArrayList<>();

		for (int i = 0; i < values.length; i++) {
			String value = StringUtils.trimToNull(values[i]);
			if (ignoreEmptyEntries && (value == null || value.isEmpty())) {
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
						break;
					} else if (exception instanceof InvalidColumnException) {
						invalidColumns.add(((InvalidColumnException) exception).getColumnName());
					}
				}
			}
		}

		if (!invalidColumns.isEmpty()) {
			LOGGER.warn("Unhandled columns [{}]", String.join(", ", invalidColumns));
		}

		return importError != null ? ImportLineResultDto.errorResult(importError) : ImportLineResultDto.successResult();
	}

	private void insertColumnEntryIntoData(EnvironmentDto environment, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = environment;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					if (importFacade.executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath, true)) {
						continue;
					}

					if (EnvironmentDto.WATER_USE.equals(entryHeaderPath[0])) {
						try {
							ObjectMapper mapper = new ObjectMapper();
							Map<WaterUse, Boolean> waterUse = mapper.readValue(entry, new TypeReference<>() {
							});

							pd.getWriteMethod().invoke(currentElement, waterUse);
						} catch (Exception e) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importInvalidWaterUseValue,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath),
									Stream.of(WaterUse.values()).map(WaterUse::name).collect(Collectors.joining(", "))));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = districtFacade
							.getByName(entry, ImportHelper.getRegionBasedOnDistrict(pd.getName(), environment.getLocation(), currentElement), false);
						if (district.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrRegion,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importDistrictNotUnique,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = communityFacade.getByName(
							entry,
							ImportHelper.getDistrictBasedOnCommunity(pd.getName(), environment.getLocation(), currentElement),
							false);
						if (community.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrDistrict,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importCommunityNotUnique,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						DataHelper.Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData =
							ImportHelper.getDistrictAndCommunityBasedOnFacility(pd.getName(), environment.getLocation(), currentElement);
						List<FacilityReferenceDto> facilities = facilityFacade.getByNameAndType(
							entry,
							infrastructureData.getElement0(),
							infrastructureData.getElement1(),
							environment.getLocation().getFacilityType(),
							false);

						if (facilities.isEmpty()) {
							if (infrastructureData.getElement1() != null) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrCommunity,
										entry,
										importFacade.buildEntityProperty(entryHeaderPath)));
							} else {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										importFacade.buildEntityProperty(entryHeaderPath)));
							}
						} else if (facilities.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importFacilityNotUniqueInDistrict,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else if (facilities.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importFacilityNotUniqueInCommunity,
									entry,
									importFacade.buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facilities.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importEnvironmentPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(importFacade.buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, importFacade.buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, importFacade.buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						importFacade.buildEntityProperty(entryHeaderPath),
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when trying to import an environment: " + e.getMessage(), e);
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEnvironmentUnexpectedError));
			}
		}
	}

	private ImportLineResultDto<EnvironmentDto> validateEnvironment(EnvironmentDto environment) {
		ImportLineResultDto<EnvironmentDto> validationResult = importFacade.validateConstraints(environment);
		if (validationResult.isError()) {
			return validationResult;
		}

		try {
			environmentFacade.validate(environment);
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}

		return ImportLineResultDto.successResult();
	}

	private ImportLineResultDto<EnvironmentDto> saveEnvironment(EnvironmentDto environment) {
		try {
			environmentFacade.save(environment);

			return ImportLineResultDto.successResult();
		} catch (ValidationRuntimeException e) {
			return ImportLineResultDto.errorResult(e.getMessage());
		}
	}

	@Stateless
	@LocalBean
	public static class EnvironmentImportFacadeEjbLocal extends EnvironmentImportFacadeEjb {
	}
}
