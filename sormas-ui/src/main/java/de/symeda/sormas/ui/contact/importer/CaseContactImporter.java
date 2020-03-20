package de.symeda.sormas.ui.contact.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.Property.ValueChangeListener;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.ContactImportSimilarityResult;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportCellData;
import de.symeda.sormas.ui.importer.ImportErrorException;
import de.symeda.sormas.ui.importer.ImportLineResult;
import de.symeda.sormas.ui.importer.ImportSimilarityResultOption;
import de.symeda.sormas.ui.person.PersonSelectField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseContactImporter extends DataImporter {

	CaseReferenceDto caseReference;
	Disease caseDisease;
	UI currentUI;

	public CaseContactImporter(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser,
			CaseReferenceDto caseReference, Disease caseDisease) {
		super(inputFile, hasEntityClassRow, currentUser);
		this.caseReference = caseReference;
		this.caseDisease = caseDisease;
	}

	@Override
	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback, UI currentUI,
			boolean duplicatesPossible) throws IOException {
		this.currentUI = currentUI;
		super.startImport(addErrorReportToLayoutCallback, currentUI, duplicatesPossible);
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(String[] values, String[] entityClasses, String[] entityProperties,
			String[][] entityPropertyPaths, boolean firstLine)
			throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		final PersonDto newPersonTemp = PersonDto.build();
		final ContactDto newContact = ContactDto.build(caseReference);
		newContact.setReportingUser(currentUser);
		newContact.setCaseDisease(caseDisease);

		boolean contactHasImportError = insertRowIntoData(values, entityClasses, entityPropertyPaths, true,
				new Function<ImportCellData, Exception>() {
					@Override
					public Exception apply(ImportCellData importColumnInformation) {
						if (!StringUtils.isEmpty(importColumnInformation.getValue())) {
							try {
								insertColumnEntryIntoData(newContact, newPersonTemp, importColumnInformation.getValue(),
										importColumnInformation.getEntityPropertyPath());
							} catch (ImportErrorException | InvalidColumnException e) {
								return e;
							}
						}
						return null;
					}
				});

		if (!contactHasImportError) {
			try {
				FacadeProvider.getPersonFacade().validate(newPersonTemp);
				FacadeProvider.getContactFacade().validate(newContact);
			} catch (ValidationRuntimeException e) {
				contactHasImportError = true;
				writeImportError(values, e.getMessage());
			}
		}

		PersonDto newPerson = newPersonTemp;

		if (!contactHasImportError) {
			try {
				ContactImportConsumer consumer = new ContactImportConsumer();
				ImportSimilarityResultOption resultOption = null;

				ContactImportLock LOCK = new ContactImportLock();
				synchronized (LOCK) {

					handleSimilarity(newPerson, new Consumer<ContactImportSimilarityResult>() {
						@Override
						public void accept(ContactImportSimilarityResult result) {
							consumer.onImportResult(result, LOCK);
						}
					});

					try {
						if (!LOCK.wasNotified) {
							LOCK.wait();
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException when trying to perform LOCK.wait() in contact import: "
								+ e.getMessage());
						throw e;
					}

					if (consumer.result != null) {
						resultOption = consumer.result.getResultOption();
					}
					
					if (ImportSimilarityResultOption.PICK.equals(resultOption)) {
						newPerson = FacadeProvider.getPersonFacade()
								.getPersonByUuid(consumer.result.getMatchingPerson().getUuid());
					}
				}

				if (contactHasImportError) {
					// In case insertRowIntoCase when matching person/case has thrown an unexpected
					// error
					return ImportLineResult.ERROR;
				} else if (ImportSimilarityResultOption.SKIP.equals(resultOption)) {
					return ImportLineResult.SKIPPED;
				} else {
					PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(newPerson);
					newContact.setPerson(savedPerson.toReference());
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

	protected void handleSimilarity(PersonDto newPerson,
			Consumer<ContactImportSimilarityResult> resultConsumer) {
		currentUI.accessSynchronously(new Runnable() {
			@Override
			public void run() {

				PersonSelectField personSelect = new PersonSelectField(true);
				personSelect.setFirstName(newPerson.getFirstName());
				personSelect.setLastName(newPerson.getLastName());
				personSelect.setNickname(newPerson.getNickname());
				personSelect.setApproximateAge(newPerson.getApproximateAge());
				personSelect.setSex(newPerson.getSex());
				personSelect.setPresentCondition(newPerson.getPresentCondition());
				personSelect.setDistrict(newPerson.getAddress().getDistrict());
				personSelect.setCommunity(newPerson.getAddress().getCommunity());
				personSelect.setCity(newPerson.getAddress().getCity());
				personSelect.setWidth(1024, Unit.PIXELS);

				if (personSelect.hasMatches()) {
					personSelect.selectBestMatch();
					final CommitDiscardWrapperComponent<PersonSelectField> selectOrCreateComponent = new CommitDiscardWrapperComponent<>(
							personSelect);

					ValueChangeListener nameChangeListener = e -> {
						selectOrCreateComponent.getCommitButton()
								.setEnabled(!(personSelect.getFirstName() == null
										|| personSelect.getFirstName().isEmpty() || personSelect.getLastName() == null
										|| personSelect.getLastName().isEmpty()));

					};
					personSelect.getFirstNameField().addValueChangeListener(nameChangeListener);
					personSelect.getLastNameField().addValueChangeListener(nameChangeListener);

					selectOrCreateComponent.addCommitListener(new CommitListener() {
						@Override
						public void onCommit() {
							PersonIndexDto person = personSelect.getValue();
							if (person == null) {
								resultConsumer.accept(
										new ContactImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
							} else {
								resultConsumer.accept(
										new ContactImportSimilarityResult(person, ImportSimilarityResultOption.PICK));
							}
						}
					});

					selectOrCreateComponent.addDiscardListener(new DiscardListener() {
						@Override
						public void onDiscard() {
							resultConsumer.accept(
									new ContactImportSimilarityResult(null, ImportSimilarityResultOption.SKIP));
						}
					});

					personSelect.setSelectionChangeCallback((commitAllowed) -> {
						selectOrCreateComponent.getCommitButton().setEnabled(commitAllowed);
					});

					VaadinUiUtil.showModalPopupWindow(selectOrCreateComponent,
							I18nProperties.getString(Strings.headingPickOrCreatePerson));
				} else {
					resultConsumer.accept(new ContactImportSimilarityResult(null, ImportSimilarityResultOption.CREATE));
				}
			}
		});
	}

	private void insertColumnEntryIntoData(ContactDto contact, PersonDto person, String entry, String[] entryHeaderPath)
			throws InvalidColumnException, ImportErrorException {
		Object currentElement = contact;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass())
							.getReadMethod().invoke(currentElement);
					// Replace PersonReferenceDto with the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (executeDefaultInvokings(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else {
						throw new UnsupportedOperationException(I18nProperties.getValidationError(
								Validations.importPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importErrorInColumn,
						buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importInvalidDate,
						buildEntityProperty(entryHeaderPath)));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a contact: " + e.getMessage());
				throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importUnexpectedError));
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
