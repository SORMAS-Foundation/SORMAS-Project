package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;

import com.vaadin.ui.Button;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class PointOfEntryImporter extends DataImporter {

	public PointOfEntryImporter(File inputFile, Button downloadErrorReportButton, UserReferenceDto currentUser, UI currentUI) throws IOException {
		super(inputFile, downloadErrorReportButton, currentUser, currentUI);
	}

	@Override
	protected void importDataFromCsvLine(String[] nextLine, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (nextLine.length > headersLine.length) {
			hasImportError = true;
			writeImportError(nextLine, I18nProperties.getValidationError(Validations.importLineTooLong));
			readNextLineFromCsv(headersLine, headers);
		}

		PointOfEntryDto newPointOfEntry = PointOfEntryDto.build();

		boolean poeHasImportError = insertRowIntoData(nextLine, headers, false, new BiFunction<String, String[], Exception>() {
			@Override
			public Exception apply(String entry, String[] entryHeaderPath) {
				try {
					insertColumnEntryIntoData(newPointOfEntry, entry, entryHeaderPath);
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
				readNextLineFromCsv(headersLine, headers);
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(nextLine, e.getMessage());
				importedCallback.accept(ImportResult.ERROR);
				readNextLineFromCsv(headersLine, headers);
			}
		} else {
			hasImportError = true;
			importedCallback.accept(ImportResult.ERROR);
			readNextLineFromCsv(headersLine, headers);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

					if (propertyType.isEnum()) {
						pd.getWriteMethod().invoke(currentElement, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
					} else if (propertyType.isAssignableFrom(Double.class)) {
						pd.getWriteMethod().invoke(currentElement, Double.parseDouble(entry));
					} else if (propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(boolean.class)) {
						pd.getWriteMethod().invoke(currentElement, Boolean.parseBoolean(entry));
					} else if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
						List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (region.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade().getByName(entry, pointOfEntry.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExistDbOrRegion, entry, buildHeaderPathString(entryHeaderPath)));
						} else if (district.size() > 1) {
							throw new ImportErrorException(I18nProperties.getValidationError(Validations.importDistrictNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(String.class)) {
						pd.getWriteMethod().invoke(currentElement, entry);
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
