package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class PointOfEntryImporter extends DataImporter {

	public PointOfEntryImporter(File inputFile, UserReferenceDto currentUser) {
		super(inputFile, false, currentUser);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(String[] values, String[] entityClasses, String[] entityProperties, String[][] entityPropertyPaths) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		PointOfEntryDto newPointOfEntry = PointOfEntryDto.build();

		boolean poeHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, (importColumnInformation) -> {
			try {
				if (!StringUtils.isEmpty(importColumnInformation.getValue())) {
					insertColumnEntryIntoData(newPointOfEntry, importColumnInformation.getValue(), importColumnInformation.getEntityPropertyPath());
				}
			} catch (ImportErrorException | InvalidColumnException e) {
				return e;
			}

			return null;
		});

		if (!poeHasImportError) {
			try {
				FacadeProvider.getPointOfEntryFacade().save(newPointOfEntry);
				return ImportLineResult.SUCCESS;
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	private void insertColumnEntryIntoData(PointOfEntryDto pointOfEntry, String value, String[] entityPropertyPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = pointOfEntry;
		for (int i = 0; i < entityPropertyPath.length; i++) {
			String headerPathElementName = entityPropertyPath[i];

			try {
				if (i != entityPropertyPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, value, entityPropertyPath)) {
						continue;
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entityPropertyPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entityPropertyPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(value, buildEntityProperty(entityPropertyPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a point of entry: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
		}
	}

}
