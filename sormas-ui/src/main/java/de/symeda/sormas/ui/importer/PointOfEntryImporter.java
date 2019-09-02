package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
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
		super(inputFile, errorReportWriter, currentUser, currentUI);
	}

	@Override
	protected void importDataFromCsvLine(String[] nextLine, List<String> entityHeaders, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (nextLine.length > headersLine.length) {
			hasImportError = true;
			writeImportError(nextLine, I18nProperties.getValidationError(Validations.importLineTooLong));
			readNextLineFromCsv(entityHeaders, headersLine, headers);
		}

		PointOfEntryDto newPointOfEntry = PointOfEntryDto.build();

		boolean poeHasImportError = insertRowIntoData(nextLine, entityHeaders, headers, false, new Function<ImportColumnInformation, Exception>() {
			@Override
			public Exception apply(ImportColumnInformation importColumnInformation) {
				try {
					insertColumnEntryIntoData(newPointOfEntry, importColumnInformation.getEntry(), importColumnInformation.getEntryHeaderPath());
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
				readNextLineFromCsv(entityHeaders, headersLine, headers);
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(nextLine, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
				readNextLineFromCsv(entityHeaders, headersLine, headers);
			}
		} else {
			hasImportError = true;
			importedCallback.accept(ImportResult.ERROR);
			readNextLineFromCsv(entityHeaders, headersLine, headers);
		}
	}

	private void insertColumnEntryIntoData(PointOfEntryDto pointOfEntry, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = pointOfEntry;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else {
						throw new UnsupportedOperationException (I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn, buildHeaderPathString(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildHeaderPathString(entryHeaderPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a point of entry: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
		}
	}

}
