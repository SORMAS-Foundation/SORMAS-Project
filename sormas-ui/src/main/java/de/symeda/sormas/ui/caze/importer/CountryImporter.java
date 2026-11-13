/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.caze.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import javax.ejb.EJBException;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.EmptyValueException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.CountryImportProgressLayout;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportProgressLayout;
import de.symeda.sormas.ui.importer.InfrastructureImporter;

public class CountryImporter extends InfrastructureImporter {

	/**
	 * Property of {@link CountryDto} that an import never changes on an existing country, so "Allow overwrite" does not
	 * restore a country an admin archived (the default countries file has {@code archived=FALSE} for all of them).
	 */
	private static final String ARCHIVED_PROPERTY = "archived";

	public CountryImporter(File inputFile, UserDto currentUser, ValueSeparator csvSeparator) throws IOException {
		this(inputFile, currentUser, false, csvSeparator);
	}

	public CountryImporter(File inputFile, UserDto currentUser, boolean allowOverwrite, ValueSeparator csvSeparator) throws IOException {
		super(inputFile, currentUser, InfrastructureType.COUNTRY, allowOverwrite, csvSeparator);
	}

	/**
	 * Imports one line as a new country, or, when the country already exists, as an update of it.
	 *
	 * <p>
	 * An existing country is found by the line's {@code uuid} first, then by its ISO code (see
	 * {@link #findExistingCountry(CountryDto)}). Without "Allow overwrite" it is reported as a duplicate and not changed.
	 * With "Allow overwrite", every non-empty value of the line (e.g. {@code iso2Code}, {@code isoNumericCode},
	 * {@code nutsCode}, {@code epipulseLocationCode}, {@code externalId}, {@code defaultName}) is written onto the existing
	 * country, which keeps its uuid, its change date and its archived state; an already set subcontinent is kept as well.
	 * Empty cells never clear a value.
	 *
	 * <p>
	 * Example: "Import default countries" with "Allow overwrite" on a server whose Greece ({@code uuid ee353123-…}) has no
	 * codes yet → Greece gets {@code iso2Code = "GR"}, {@code isoNumericCode = "300"}, {@code nutsCode = "EL"} and
	 * {@code epipulseLocationCode = "EL"}; the same without "Allow overwrite" → Greece is listed as a duplicate and keeps its
	 * empty codes; a line for a new country → the country is created; a line whose NUTS code another country already has →
	 * listed as a duplicate with the message of {@code countryNutsCodeAlreadyExists}, only this line is not imported.
	 */
	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException {

		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		CountryDto newEntityDto = CountryDto.build();
		boolean hasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, cellData -> {
			try {
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newEntityDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}
			return null;
		});

		if (!hasImportError) {
			ImportLineResultDto<CountryDto> constraintErrors = validateConstraints(newEntityDto);
			if (constraintErrors.isError()) {
				writeImportError(values, constraintErrors.getMessage());
				hasImportError = true;
			}
		}

		if (!hasImportError) {
			CountryDto countryToSave = newEntityDto;

			CountryDto existingCountry = findExistingCountry(newEntityDto);
			if (existingCountry != null) {
				if (!allowOverwrite) {
					writeImportError(values, I18nProperties.getValidationError(Validations.importCountryAlreadyExists));
					return ImportLineResult.DUPLICATE;
				}

				// the existing country keeps its uuid and change date, so the save is an update and passes the change date check
				countryToSave = DtoCopyHelper.copyDtoValues(existingCountry, newEntityDto, true, ARCHIVED_PROPERTY);
			}

			try {
				FacadeProvider.getCountryFacade().save(countryToSave, allowOverwrite);
				return ImportLineResult.SUCCESS;
			} catch (EmptyValueException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.DUPLICATE;
			} catch (EJBException e) {
				// any other error on saving, e.g. the ISO code of an archived country that the line was not matched to
				// (country.isocode is unique): only this line fails, instead of the whole import
				logger.warn("Could not import country {}: {}", countryToSave.getIsoCode(), e.getMessage());
				writeImportError(values, I18nProperties.getValidationError(Validations.importUnexpectedError));
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	/**
	 * The country that a line describes, if it already exists: the country with the line's uuid (archived or not), else the
	 * active country with the line's ISO code, else {@code null}.
	 *
	 * <p>
	 * Example: a line of the default countries with {@code uuid ee353123-…} → the Greece imported from that file before; a
	 * line with a new uuid and ISO code {@code LUX} → the Luxembourg created by hand; a new country → {@code null}.
	 */
	private CountryDto findExistingCountry(CountryDto country) {

		if (country == null) {
			return null;
		}

		if (StringUtils.isNotBlank(country.getUuid())) {
			CountryDto byUuid = FacadeProvider.getCountryFacade().getByUuid(country.getUuid());
			if (byUuid != null) {
				return byUuid;
			}
		}

		if (StringUtils.isNotBlank(country.getIsoCode())) {
			CountryReferenceDto byIsoCode = FacadeProvider.getCountryFacade().getCountryByIsoCode(country.getIsoCode().trim());
			if (byIsoCode != null && StringUtils.isNotBlank(byIsoCode.getUuid())) {
				return FacadeProvider.getCountryFacade().getByUuid(byIsoCode.getUuid());
			}
		}

		return null;
	}

	/**
	 * Inserts the entry of a single cell into the infrastructure object.
	 */
	private void insertColumnEntryIntoData(CountryDto newEntityDto, String value, String[] entityPropertyPath)
		throws InvalidColumnException, ImportErrorException {

		Object currentElement = newEntityDto;
		for (int i = 0; i < entityPropertyPath.length; i++) {
			String headerPathElementName = entityPropertyPath[i];

			try {
				if (i != entityPropertyPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();
					if (!executeDefaultInvoke(pd, currentElement, value, entityPropertyPath)) {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entityPropertyPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(value, buildEntityProperty(entityPropertyPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import infrastructure data: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
		}
	}

	public void startImport(Consumer<StreamResource> errorReportConsumer, UI currentUI) throws IOException, CsvValidationException {
		startImport(errorReportConsumer, currentUI, true);
	}

	@Override
	protected ImportProgressLayout getImportProgressLayout(UI currentUI, boolean duplicatesPossible) throws IOException, CsvValidationException {
		return new CountryImportProgressLayout(readImportFileLength(inputFile), currentUI, this::cancelImport);
	}
}
