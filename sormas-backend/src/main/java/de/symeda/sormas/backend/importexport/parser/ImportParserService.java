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

package de.symeda.sormas.backend.importexport.parser;

import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.format.ImportExportFormat;
import de.symeda.sormas.api.importexport.format.ImportFormat;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.infrastructure.area.AreaFacadeEjb.AreaFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb.ContinentFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless
public class ImportParserService {

	@EJB
	private EnumService enumService;
	@EJB
	private ContinentFacadeEjbLocal continentFacade;
	@EJB
	private SubcontinentFacadeEjbLocal subcontinentFacade;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private AreaFacadeEjbLocal areaFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private UserFacadeEjbLocal userFacade;

	private final PropertyTypeBasedParsers classBasedParsers;
	private final FormatterBasedParsers formatBasedParsers;

	public ImportParserService() {
		classBasedParsers = PropertyTypeBasedParsers.Builder.of(PropertyTypeBasedParsers.class)
			.withParser(Enum.class, this::parseEnum)
			.withParser(CustomizableEnum.class, this::parseCustomizableEnum)
			.withParser(Date.class, this::parseDate)
			.withParser(Integer.class, (v, clazz, path) -> Integer.parseInt(v))
			.withParser(Double.class, (v, clazz, path) -> Double.parseDouble(v))
			.withParser(Float.class, (v, clazz, path) -> Float.parseFloat(v))
			.withParser(Boolean.class, (v, clazz, path) -> DataHelper.parseBoolean(v))
			.withParser(boolean.class, (v, clazz, path) -> DataHelper.parseBoolean(v))
			.withParser(ContinentReferenceDto.class, this::parseContinent)
			.withParser(SubcontinentReferenceDto.class, this::parseSubContinent)
			.withParser(CountryReferenceDto.class, this::parseCountry)
			.withParser(AreaReferenceDto.class, this::parseArea)
			.withParser(RegionReferenceDto.class, this::parseRegion)
			.withParser(UserReferenceDto.class, this::parseUser)
			.withParser(String.class, (v, clazz, path) -> v)
			.build();

		formatBasedParsers =
			FormatterBasedParsers.Builder.of(FormatterBasedParsers.class).withParser(ImportExportFormat.DATE_TIME, this::parseDateTime).build();
	}

	public boolean hasParser(PropertyDescriptor pd) {
		ImportFormat parserAnnotation = pd.getWriteMethod().getAnnotation(ImportFormat.class);
		if (parserAnnotation != null) {
			// force calling parse method and fail if parsers are not not properly set
			return true;
		} else {
			return classBasedParsers.hasParser(pd.getPropertyType());
		}
	}

	public Object parseValue(PropertyDescriptor pd, String entry, String[] entryHeaderPath) throws ImportErrorException {
		Class<?> propertyType = pd.getPropertyType();
		String propertyPath = buildEntityProperty(entryHeaderPath);

		final Optional<? extends ImportExportParsers.Parser<?>> parser;

		ImportFormat formatAnnotation = pd.getWriteMethod().getAnnotation(ImportFormat.class);
		if (formatAnnotation != null) {
			parser = formatBasedParsers.getParser(formatAnnotation.value());
		} else {
			parser = classBasedParsers.getParser(propertyType);
		}

		if (parser.isPresent()) {
			return parser.get().parse(entry, (Class) propertyType, propertyPath);
		}

		throw new RuntimeException("parser not found for type [" + propertyPath + "] of type [" + propertyType.getSimpleName() + "]");
	}

	public String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}

	private Enum<?> parseEnum(String v, @SuppressWarnings("rawtypes") Class<Enum> clazz, String path) throws ImportErrorException {
		Enum<?> enumValue = null;

		try {
			//noinspection unchecked
			enumValue = Enum.valueOf(clazz, v.toUpperCase());
		} catch (IllegalArgumentException e) {
			// ignore
		}

		if (enumValue == null) {
			try {
				enumValue = enumService.getEnumByCaption(clazz, v);
			} catch (EnumService.InvalidEnumCaptionException e) {
				throw new ImportErrorException(v, path);
			}
		}

		return enumValue;
	}

	private CustomizableEnum parseCustomizableEnum(String v, Class<CustomizableEnum> clazz, String path) throws ImportErrorException {
		try {
			CustomizableEnum customizableEnum = clazz.newInstance();
			customizableEnum.setValue(v);
			return customizableEnum;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorCustomizableEnumValue, v, path));
		}
	}

	private Date parseDate(String v, Class<Date> clazz, String path) throws ImportErrorException {
		// If the string is smaller than the length of the expected date format, throw an exception
		if (v.length() < 8) {
			throw new ImportErrorException(
				I18nProperties.getValidationError(
					Validations.importInvalidDate,
					path,
					DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
		} else {
			try {
				return DateHelper.parseDateWithException(v, I18nProperties.getUserLanguage());
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						path,
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
			}
		}
	}

	private Date parseDateTime(String v, Class<Date> clazz, String path) throws ImportErrorException {
		// If the string is smaller than the length of the expected date format, throw an exception
		if (v.length() < 8) {
			throw new ImportErrorException(
				I18nProperties.getValidationError(
					Validations.importInvalidDate,
					path,
					DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
		} else {
			try {
				return DateHelper.parseDateTimeWithException(v, I18nProperties.getUserLanguage());
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						path,
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
			}
		}
	}

	private ContinentReferenceDto parseContinent(String entry, Class<ContinentReferenceDto> clazz, String path) throws ImportErrorException {
		List<ContinentReferenceDto> continents = continentFacade.getByDefaultName(entry, false);
		if (continents.isEmpty()) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, path));
		} else if (continents.size() > 1) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importSubcontinentNotUnique, entry, path));
		} else {
			return continents.get(0);
		}
	}

	private SubcontinentReferenceDto parseSubContinent(String entry, Class<SubcontinentReferenceDto> clazz, String path) throws ImportErrorException {
		List<SubcontinentReferenceDto> subcontinents = subcontinentFacade.getByDefaultName(entry, false);
		if (subcontinents.isEmpty()) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, path));
		} else if (subcontinents.size() > 1) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importSubcontinentNotUnique, entry, path));
		} else {
			return subcontinents.get(0);
		}
	}

	private CountryReferenceDto parseCountry(String v, Class<CountryReferenceDto> clazz, String path) throws ImportErrorException {
		List<CountryReferenceDto> countries = countryFacade.getReferencesByName(v, false);
		if (countries.isEmpty()) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, v, path));
		} else if (countries.size() > 1) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCountryNotUnique, v, path));
		} else {
			return countries.get(0);
		}
	}

	private AreaReferenceDto parseArea(String v, Class<AreaReferenceDto> clazz, String path) throws ImportErrorException {
		List<AreaReferenceDto> areas = areaFacade.getByName(v, false);
		if (areas.isEmpty()) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, v, path));
		} else if (areas.size() > 1) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importAreaNotUnique, v, path));
		} else {
			return areas.get(0);
		}
	}

	private RegionReferenceDto parseRegion(String v, Class<RegionReferenceDto> clazz, String path) throws ImportErrorException {
		List<Region> regions = regionService.getByName(v, false);
		if (regions.isEmpty()) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, v, path));
		} else if (regions.size() > 1) {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importRegionNotUnique, v, path));
		} else {
			return RegionFacadeEjb.toReferenceDto(regions.get(0));
		}
	}

	private UserReferenceDto parseUser(String v, Class<UserReferenceDto> clazz, String path) throws ImportErrorException {
		UserDto user = userFacade.getByUserName(v);
		if (user != null) {
			return user.toReference();
		} else {
			throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, v, path));
		}
	}
}
