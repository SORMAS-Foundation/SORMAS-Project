package de.symeda.sormas.ui.caze.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.function.Consumer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.EmptyValueException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.CountryImportProgressLayout;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportProgressLayout;
import de.symeda.sormas.ui.importer.InfrastructureImporter;

public class CountryImporter extends InfrastructureImporter {

	public CountryImporter(File inputFile, UserDto currentUser) {
		super(inputFile, currentUser, InfrastructureType.COUNTRY);
	}

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
		boolean iHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (cellData) -> {
			try {
				if (!StringUtils.isEmpty(cellData.getValue())) {
					insertColumnEntryIntoData(newEntityDto, cellData.getValue(), cellData.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}
			return null;
		});

		if (!iHasImportError) {
			try {
				FacadeProvider.getCountryFacade().saveCountry(newEntityDto);
				return ImportLineResult.SUCCESS;
			} catch (EmptyValueException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.DUPLICATE;
			}
		} else {
			return ImportLineResult.ERROR;
		}
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
					validateFieldLength(headerPathElementName, value);
					if (!executeDefaultInvokings(pd, currentElement, value, entityPropertyPath)) {
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

	private void validateFieldLength(String field, String value) throws ImportErrorException, InvalidColumnException {
		try {
			Size size = CountryDto.class.getDeclaredField(field).getAnnotation(Size.class);
			boolean shouldNotBeBlank = CountryDto.class.isAnnotationPresent(NotBlank.class);
			if (shouldNotBeBlank && StringUtils.isBlank(value)) {
				String message = "The value {0} is blank.";
				throw new ImportErrorException(MessageFormat.format(message, value));
			}
			if (size != null) {
				if (value.length() < size.min()) {
					String message = "The value {0} has length {1} but the minimum length is {2}";
					throw new ImportErrorException(MessageFormat.format(message, value, value.length(), size.min()));
				}
				if (value.length() > size.max()) {
					String message = "The value {0} has length {1} but the maximum length is {2}";
					throw new ImportErrorException(MessageFormat.format(message, value, value.length(), size.max()));
				}
			}
		} catch (NoSuchFieldException e) {
			throw new InvalidColumnException(field);
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
