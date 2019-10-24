package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class PointOfEntryImporter extends DataImporter {

	public PointOfEntryImporter(File inputFile, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this(inputFile, null, currentUser, currentUI);
	}
	
	public PointOfEntryImporter(File inputFile, OutputStreamWriter errorReportWriter, UserReferenceDto currentUser, UI currentUI) throws IOException {
		super(inputFile, false, errorReportWriter, currentUser, currentUI);
	}

	@Override
	protected void importDataFromCsvLine(String[] values, String[] entityClasses, String[] entityProperties, String[][] entityPropertyPaths) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			hasImportError = true;
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			importedCallback.accept(ImportResult.ERROR);
			return;
		}

		PointOfEntryDto newPointOfEntry = PointOfEntryDto.build();

		boolean poeHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, false, new Function<ImportCellData, Exception>() {
			@Override
			public Exception apply(ImportCellData importColumnInformation) {
				try {
					insertColumnEntryIntoData(newPointOfEntry, importColumnInformation.getValue(), importColumnInformation.getEntityPropertyPath());
				} catch (ImportErrorException | InvalidColumnException e) {
					return e;
				}

				return null;
			}
		});

		if (!poeHasImportError) {
			try {
				FacadeProvider.getPointOfEntryFacade().save(newPointOfEntry);
				importedCallback.accept(ImportResult.SUCCESS);
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(values, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
			}
		} else {
			hasImportError = true;
			importedCallback.accept(ImportResult.ERROR);
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
