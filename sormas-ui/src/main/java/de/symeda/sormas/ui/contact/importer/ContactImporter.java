package de.symeda.sormas.ui.contact.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import de.symeda.sormas.api.person.SimilarPersonDto;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.contact.ContactSelectionField;
import de.symeda.sormas.ui.importer.ContactImportSimilarityResult;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportCellData;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.importer.ImporterPersonHelper;
import de.symeda.sormas.ui.person.PersonSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Data importer that is used to import contacts for a specific case, or just
 * contacts with or without a related case.
 * 
 * This importer adds the following logic:
 * 
 * - Check the database for persons that are similar to the imported contact
 * person and, if at least one is found, let the user resolve the conflict. -
 * Based on the results of the conflict resolve, an existing person might be
 * picked for the contact. - Save the person and contact to the database (unless
 * the import was skipped or otherwise canceled).
 */
public class ContactImporter extends DataImporter {

	private CaseDataDto caze;
	private UI currentUI;

	public ContactImporter(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser, CaseDataDto caze) {

		super(inputFile, hasEntityClassRow, currentUser);
		this.caze = caze;
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI, boolean duplicatesPossible) throws IOException {

		this.currentUI = currentUI;
		super.startImport(addErrorReportToLayoutCallback, currentUI, duplicatesPossible);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		final PersonDto newPersonTemp = PersonDto.build();
		final ContactDto newContactTemp = caze != null ? ContactDto.build(caze) : ContactDto.build();
		newContactTemp.setReportingUser(currentUser);

		boolean contactHasImportError =
			insertRowIntoData(values, entityClasses, entityPropertyPaths, true, new Function<ImportCellData, Exception>() {

				@Override
				public Exception apply(ImportCellData importColumnInformation) {
					// If the cell entry is not empty, try to insert it into the current contact or person object
					if (!StringUtils.isEmpty(importColumnInformation.getValue())) {
						try {
							insertColumnEntryIntoData(
								newContactTemp,
								newPersonTemp,
								importColumnInformation.getValue(),
								importColumnInformation.getEntityPropertyPath());
						} catch (ImportErrorException | InvalidColumnException e) {
							return e;
						}
					}
					return null;
				}
			});

		// try to assign the contact to an existing case
		if (caze == null && newContactTemp.getCaseIdExternalSystem() != null) {
			CaseDataDto existingCase =
				FacadeProvider.getCaseFacade().getCaseDataByUuid(newContactTemp.getCaseIdExternalSystem().trim().toUpperCase());
			if (existingCase != null) {
				newContactTemp.assignCase(existingCase);
				newContactTemp.setCaseIdExternalSystem(null);
			}
		}

		// If the row does not have any import errors, call the backend validation of all associated entities
		if (!contactHasImportError) {
			try {
				FacadeProvider.getPersonFacade().validate(newPersonTemp);
				FacadeProvider.getContactFacade().validate(newContactTemp);
			} catch (ValidationRuntimeException e) {
				contactHasImportError = true;
				writeImportError(values, e.getMessage());
			}
		}

		PersonDto newPerson = newPersonTemp;

		// If the contact still does not have any import errors, search for persons similar to the contact person in the database and,
		// if there are any, display a window to resolve the conflict to the user
		if (!contactHasImportError) {
			try {
				ContactImportConsumer consumer = new ContactImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				ContactImportLock personSelectLock = new ContactImportLock();
				// We need to pause the current thread to prevent the import from continuing until the user has acted
				synchronized (personSelectLock) {
					// Call the logic that allows the user to handle the similarity; once this has been done, the LOCK should be notified
					// to allow the importer to resume
					handleSimilarity(newPerson, result -> {
						consumer.onImportResult(result, personSelectLock);
					});

					try {
						if (!personSelectLock.wasNotified) {
							personSelectLock.wait();
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException when trying to perform LOCK.wait() in contact import: " + e.getMessage());
						throw e;
					}

					if (consumer.result != null) {
						resultOption = consumer.result.getResultOption();
					}

					// If the user picked an existing person, override the contact person with it
					if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
						newPerson = FacadeProvider.getPersonFacade().getPersonByUuid(consumer.result.getMatchingPerson().getUuid());
					}
				}

				// Determine the import result and, if there was no duplicate, the user did not skip over the contact 
				// or an existing person was picked, save the contact and person to the database
				if (contactHasImportError) {
					return ImportLineResult.ERROR;
				} else if (ImportSimilarityResultOption.SKIP.equals(resultOption)) {
					return ImportLineResult.SKIPPED;
				} else {
					final PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(newPerson);
					newContactTemp.setPerson(savedPerson.toReference());

					ContactDto newContact = newContactTemp;

					final ContactImportLock contactSelectLock = new ContactImportLock();
					synchronized (contactSelectLock) {

						handleContactSimilarity(newContactTemp, savedPerson, result -> consumer.onImportResult(result, contactSelectLock));

						try {
							if (!contactSelectLock.wasNotified) {
								contactSelectLock.wait();
							}
						} catch (InterruptedException e) {
							logger.error("InterruptedException when trying to perform LOCK.wait() in contact import: " + e.getMessage());
							throw e;
						}

						if (consumer.result != null) {
							resultOption = consumer.result.getResultOption();
						}

						if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
							newContact = FacadeProvider.getContactFacade().getContactByUuid(consumer.result.getMatchingContact().getUuid());
						}
					}

					FacadeProvider.getContactFacade().saveContact(newContact);

					consumer.result = null;
					return ImportLineResult.SUCCESS;
				}
			} catch (ValidationRuntimeException e) {
				writeImportError(values, e.getMessage());
				return ImportLineResult.ERROR;
			}
		} else {
			return ImportLineResult.ERROR;
		}
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate persons.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected void handleSimilarity(PersonDto newPerson, Consumer<ContactImportSimilarityResult> resultConsumer) {

		currentUI.accessSynchronously(() -> {
			PersonSelectionField personSelect =
				new PersonSelectionField(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForContactImport));
			personSelect.setWidth(1024, Unit.PIXELS);

			if (personSelect.hasMatches()) {
				final CommitDiscardWrapperComponent<PersonSelectionField> component = new CommitDiscardWrapperComponent<>(personSelect);
				component.addCommitListener(() -> {
					SimilarPersonDto person = personSelect.getValue();
					if (person == null) {
						resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
					} else {
						resultConsumer.accept(new ContactImportSimilarityResult(person, null, ImportSimilarityResultOption.PICK));
					}
				});

				component.addDiscardListener(
					() -> resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP)));

				personSelect.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));

				personSelect.selectBestMatch();
			} else {
				resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}
		});
	}

	protected void handleContactSimilarity(ContactDto newContact, PersonDto person, Consumer<ContactImportSimilarityResult> resultConsumer) {

		currentUI.accessSynchronously(() -> {
			ContactSelectionField contactSelection = new ContactSelectionField(
				newContact,
				I18nProperties.getString(Strings.infoSelectOrCreateContactImport),
				person.getFirstName(),
				person.getLastName());
			contactSelection.setWidth(1024, Unit.PIXELS);

			if (contactSelection.hasMatches()) {
				final CommitDiscardWrapperComponent<ContactSelectionField> component = new CommitDiscardWrapperComponent<>(contactSelection);
				component.addCommitListener(() -> {
					SimilarContactDto similarContactDto = contactSelection.getValue();
					if (similarContactDto == null) {
						resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
					} else {
						resultConsumer.accept(new ContactImportSimilarityResult(null, similarContactDto, ImportSimilarityResultOption.PICK));
					}
				});

				component.addDiscardListener(
					() -> resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.SKIP)));

				contactSelection.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateContact));

				contactSelection.selectBestMatch();
			} else {
				resultConsumer.accept(new ContactImportSimilarityResult(null, null, ImportSimilarityResultOption.CREATE));
			}
		});
	}

	/**
	 * Inserts the entry of a single cell into the contact or its person.
	 */
	private void insertColumnEntryIntoData(ContactDto contact, PersonDto person, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, ImportErrorException {
		Object currentElement = contact;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Set the current element to the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the contact or person fields
					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade()
							.getByName(
								entry,
								ImporterPersonHelper.getRegionBasedOnDistrict(pd.getName(), null, contact, person, currentElement),
								false);
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
						List<CommunityReferenceDto> community =
							FacadeProvider.getCommunityFacade().getByName(entry, ImporterPersonHelper.getPersonDistrict(pd.getName(), person), false);
						if (community.isEmpty()) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(
									Validations.importEntryDoesNotExistDbOrDistrict,
									entry,
									buildEntityProperty(entryHeaderPath)));
						} else if (community.size() > 1) {
							throw new ImportErrorException(
								I18nProperties.getValidationError(Validations.importCommunityNotUnique, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						Pair<DistrictReferenceDto, CommunityReferenceDto> infrastructureData =
							ImporterPersonHelper.getPersonDistrictAndCommunity(pd.getName(), person);
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade()
							.getByName(entry, infrastructureData.getElement0(), infrastructureData.getElement1(), false);
						if (facility.isEmpty()) {
							if (infrastructureData.getElement1() != null) {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrCommunity,
										entry,
										buildEntityProperty(entryHeaderPath)));
							} else {
								throw new ImportErrorException(
									I18nProperties.getValidationError(
										Validations.importEntryDoesNotExistDbOrDistrict,
										entry,
										buildEntityProperty(entryHeaderPath)));
							}
						} else if (facility.size() > 1 && infrastructureData.getElement1() == null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInDistrict, entry, buildEntityProperty(entryHeaderPath)));
						} else if (facility.size() > 1 && infrastructureData.getElement1() != null) {
							throw new ImportErrorException(
								I18nProperties
									.getValidationError(Validations.importFacilityNotUniqueInCommunity, entry, buildEntityProperty(entryHeaderPath)));
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
						}
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a contact: " + e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importUnexpectedError));
			}
		}
	}

	private class ContactImportConsumer {

		protected ContactImportSimilarityResult result;

		private void onImportResult(ContactImportSimilarityResult result, ContactImportLock LOCK) {
			this.result = result;
			synchronized (LOCK) {
				LOCK.notify();
				LOCK.wasNotified = true;
			}
		}
	}

	private static class ContactImportLock {

		protected boolean wasNotified = false;
	}
}
