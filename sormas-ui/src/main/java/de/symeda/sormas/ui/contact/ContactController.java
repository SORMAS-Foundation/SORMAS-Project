/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.contact;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.contact.ContactBulkEditData;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.deletionconfiguration.AutomaticDeletionInfoDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionField;
import de.symeda.sormas.ui.contact.components.linelisting.layout.LineListingLayout;
import de.symeda.sormas.ui.epidata.ContactEpiDataView;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CoreEntityArchiveMessages;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.NotificationHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.automaticdeletion.AutomaticDeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.RowLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class ContactController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public ContactController() {

	}

	public void registerViews(Navigator navigator) {
		UserProvider userProvider = UserProvider.getCurrent();

		navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
		if (userProvider.hasUserRight(UserRight.CASE_MERGE)) {
			navigator.addView(MergeContactsView.VIEW_NAME, MergeContactsView.class);
		}
		navigator.addView(ContactDataView.VIEW_NAME, ContactDataView.class);
		navigator.addView(ContactPersonView.VIEW_NAME, ContactPersonView.class);
		navigator.addView(ContactVisitsView.VIEW_NAME, ContactVisitsView.class);
		navigator.addView(ContactEpiDataView.VIEW_NAME, ContactEpiDataView.class);
	}

	public void openLineListingWindow() {
		openLineListingWindow(null);
	}

	public void openLineListingWindow(CaseDataDto caseDataDto) {
		Window window = new Window(I18nProperties.getString(Strings.headingLineListing));

		LineListingLayout lineListingForm = new LineListingLayout(window, caseDataDto);
		lineListingForm.setSaveCallback(contacts -> saveContactsFromLineListing(lineListingForm, contacts));

		lineListingForm.setWidth(LineListingLayout.DEFAULT_WIDTH, Unit.PIXELS);

		window.setContent(lineListingForm);
		window.setModal(true);
		window.setPositionX((int) Math.max(0, (Page.getCurrent().getBrowserWindowWidth() - lineListingForm.getWidth())) / 2);
		window.setPositionY(70);
		window.setResizable(false);

		UI.getCurrent().addWindow(window);
	}

	private void saveContactsFromLineListing(LineListingLayout lineListingForm, List<LineListingLayout.ContactLineDto> contacts) {
		try {
			lineListingForm.validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		for (LineListingLayout.ContactLineDto contactLineDto : contacts) {
			ContactDto newContact = contactLineDto.getContact();
			if (UserProvider.getCurrent() != null) {
				newContact.setReportingUser(UserProvider.getCurrent().getUserReference());
			}

			PersonDto newPerson = contactLineDto.getPerson();

			ControllerProvider.getPersonController()
				.selectOrCreatePerson(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
					if (selectedPerson != null) {
						newContact.setPerson(selectedPerson);

						selectOrCreateContact(newContact, FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid()), uuid -> {
							if (uuid == null) {
								FacadeProvider.getContactFacade().save(newContact);
								Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.ASSISTIVE_NOTIFICATION);
							}
						});
					}
				}, true);
		}

		lineListingForm.closeWindow();
		ControllerProvider.getContactController().navigateToIndex();
	}

	public void openLineListingWindow(EventDto eventDto, Set<EventParticipantIndexDto> eventParticipantIndexDtos) {
		if (eventParticipantIndexDtos == null || eventParticipantIndexDtos.isEmpty()) {
			return;
		}

		Window window = new Window(I18nProperties.getString(Strings.headingLineListing));

		List<String> uuids = eventParticipantIndexDtos.stream().map(EventParticipantIndexDto::getUuid).collect(Collectors.toList());
		List<EventParticipantDto> eventParticipantDtos = FacadeProvider.getEventParticipantFacade().getByUuids(uuids);

		LineListingLayout lineListingForm = new LineListingLayout(window, eventDto, eventParticipantDtos);
		lineListingForm.setSaveCallback(contacts -> saveContactsFromEventParticipantsLineListing(lineListingForm, contacts));

		lineListingForm.setWidth(LineListingLayout.DEFAULT_WIDTH, Unit.PIXELS);

		window.setContent(lineListingForm);
		window.setModal(true);
		window.setPositionX((int) Math.max(0, (Page.getCurrent().getBrowserWindowWidth() - lineListingForm.getWidth())) / 2);
		window.setPositionY(70);
		window.setResizable(false);

		UI.getCurrent().addWindow(window);
	}

	private void saveContactsFromEventParticipantsLineListing(LineListingLayout lineListingForm, List<LineListingLayout.ContactLineDto> contacts) {
		try {
			lineListingForm.validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		for (LineListingLayout.ContactLineDto contactLineDto : contacts) {
			ContactDto newContact = contactLineDto.getContact();
			if (UserProvider.getCurrent() != null) {
				newContact.setReportingUser(UserProvider.getCurrent().getUserReference());
			}

			newContact.setPerson(contactLineDto.getPerson().toReference());

			selectOrCreateContact(newContact, contactLineDto.getPerson(), uuid -> {
				if (uuid == null) {
					FacadeProvider.getContactFacade().save(newContact);
					Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.ASSISTIVE_NOTIFICATION);
				}
			});
		}

		lineListingForm.closeWindow();
		ControllerProvider.getContactController().navigateToIndex();
	}

	public void create() {
		create(null, false, null);
	}

	public void create(CaseReferenceDto caseRef) {
		create(caseRef, false, null);
	}

	/**
	 * @param asResultingCase
	 *            Determines whether the case should be set as resulting case instead of source case
	 * @param alternativeCallback
	 *            Callback that is executed instead of opening the created contact
	 */
	public void create(CaseReferenceDto caseRef, boolean asResultingCase, Runnable alternativeCallback) {

		CaseDataDto caze = null;
		if (caseRef != null) {
			caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		}
		CommitDiscardWrapperComponent<ContactCreateForm> createComponent =
			getContactCreateComponent(caze, asResultingCase, alternativeCallback, false);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewContact));
	}

	public void create(EventParticipantReferenceDto eventParticipantRef) {
		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantRef.getUuid());
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		if (event.getDisease() == null) {
			new Notification(
				I18nProperties.getString(Strings.headingCreateNewContactIssue),
				I18nProperties.getString(Strings.messageEventParticipantToContactWithoutEventDisease),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent(eventParticipant);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewContact));
	}

	public void navigateToData(String contactUuid) {
		navigateToData(contactUuid, false);
	}

	public void navigateToData(String contactUuid, boolean openTab) {
		navigateToView(ContactDataView.VIEW_NAME, contactUuid, openTab);
	}

	public void navigateToView(String viewName, String contactUuid, boolean openTab) {
		String navigationState = viewName + "/" + contactUuid;
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

	public void navigateTo(ContactCriteria contactCriteria) {
		ViewModelProviders.of(ContactsView.class).remove(ContactCriteria.class);
		String navigationState = AbstractView.buildNavigationState(ContactsView.VIEW_NAME, contactCriteria);
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToIndex() {
		String navigationState = ContactsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToMergeContactsView() {
		String navigationState = MergeContactsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void editData(String contactUuid) {
		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void overview() {
		String navigationState = ContactsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void caseContactsOverview(String caseUuid) {
		String navigationState = CaseContactsView.VIEW_NAME + "/" + caseUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	/**
	 * Update the fragment without causing navigator to change view
	 */
	public void setUriFragmentParameter(String contactUuid) {
		String fragmentParameter;
		if (contactUuid == null || contactUuid.isEmpty()) {
			fragmentParameter = "";
		} else {
			fragmentParameter = contactUuid;
		}

		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + ContactsView.VIEW_NAME + "/" + fragmentParameter, false);
	}

	private ContactDto createNewContact(CaseDataDto caze, boolean asSourceContact) {
		ContactDto contact = caze != null ? ContactDto.build(caze) : ContactDto.build();

		if (caze != null && asSourceContact) {
			contact.setCaze(null);
			contact.setResultingCase(caze.toReference());
			contact.setPerson(caze.getPerson());
		}

		setDefaults(contact);

		return contact;
	}

	private ContactDto createNewContact(EventParticipantDto eventParticipant) {
		ContactDto contact = ContactDto.build(eventParticipant);

		setDefaults(contact);

		return contact;
	}

	private void setDefaults(ContactDto contact) {
		UserDto user = UserProvider.getCurrent().getUser();
		contact.setReportingUser(user.toReference());
		contact.setReportingDistrict(user.getDistrict());
	}

	private ContactDto createNewContact(EventParticipantDto eventParticipant, Disease disease) {
		ContactDto contact = createNewContact(eventParticipant);
		contact.setDisease(disease);

		return contact;
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(CaseDataDto caze) {
		return getContactCreateComponent(caze, false, null, false);
	}

	/**
	 * @param asSourceContact
	 *            Determines whether the case should be set as resulting case instead of source case
	 * @param alternativeCallback
	 *            Callback that is executed instead of opening the created contact
	 */
	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(
		final CaseDataDto caze,
		boolean asSourceContact,
		Runnable alternativeCallback,
		boolean createdFromLabMesssage) {

		final PersonDto casePerson = caze != null ? FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()) : null;
		ContactCreateForm createForm = new ContactCreateForm(
			caze != null ? caze.getDisease() : null,
			caze != null && !asSourceContact,
			asSourceContact,
			!createdFromLabMesssage);

		createForm.setValue(createNewContact(caze, asSourceContact));
		if (casePerson != null && asSourceContact) {
			createForm.setPerson(casePerson);
		}
		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<ContactCreateForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final ContactDto dto = createForm.getValue();
				if (asSourceContact && caze != null) {
					CaseDataDto caseDto = FacadeProvider.getCaseFacade().getByUuid(caze.getUuid());
					caseDto.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
					FacadeProvider.getCaseFacade().save(caseDto);
				}
				if (asSourceContact && alternativeCallback != null && casePerson != null) {
					selectOrCreateContact(dto, casePerson, selectedContactUuid -> {
						if (selectedContactUuid != null) {
							ContactDto selectedContact = FacadeProvider.getContactFacade().getByUuid(selectedContactUuid);
							selectedContact.setResultingCase(caze.toReference());
							selectedContact.setResultingCaseUser(UserProvider.getCurrent().getUserReference());
							selectedContact.setContactStatus(ContactStatus.CONVERTED);
							selectedContact.setContactClassification(ContactClassification.CONFIRMED);
							FacadeProvider.getContactFacade().save(selectedContact);

							// Avoid asking the user to discard unsaved changes
							createComponent.discard();
							alternativeCallback.run();
						}
					});
				} else if (createdFromLabMesssage) {
					PersonDto dbPerson = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());
					if (dbPerson == null) {
						PersonDto personDto = PersonDto.build();
						transferDataToPerson(createForm, personDto);
						FacadeProvider.getPersonFacade().savePerson(personDto);
						dto.setPerson(personDto.toReference());
						createNewContact(dto, e -> {
						});
					} else {
						transferDataToPerson(createForm, dbPerson);
						FacadeProvider.getPersonFacade().savePerson(dbPerson);
						createNewContact(dto, e -> {
						});
					}
				} else {

					PersonDto searchedPerson = createForm.getSearchedPerson();
					if (searchedPerson != null) {
						dto.setPerson(searchedPerson.toReference());
						selectOrCreateContact(dto, searchedPerson, selectedContactUuid -> {
							if (selectedContactUuid != null) {
								editData(selectedContactUuid);
							}
						});
					} else {

						final PersonDto person = PersonDto.build();
						transferDataToPerson(createForm, person);

						ControllerProvider.getPersonController()
							.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForContact), selectedPerson -> {
								if (selectedPerson != null) {
									dto.setPerson(selectedPerson);

									fillPersonAddressIfEmpty(dto, () -> FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid()));

									selectOrCreateContact(dto, person, selectedContactUuid -> {
										if (selectedContactUuid != null) {
											editData(selectedContactUuid);
										}
									});
								}
							}, true);
					}
				}
			}
		});

		return createComponent;

	}

	private void transferDataToPerson(ContactCreateForm createForm, PersonDto person) {
		createForm.getPersonCreateForm().transferDataToPerson(person);
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(EventParticipantDto eventParticipant) {
		final ContactCreateForm createForm;
		final Disease disease;
		if (eventParticipant.getResultingCase() != null) {
			disease = FacadeProvider.getCaseFacade().getCaseDataByUuid(eventParticipant.getResultingCase().getUuid()).getDisease();
		} else {
			disease = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false).getDisease();
		}
		createForm = new ContactCreateForm(disease, false, false, true);

		createForm.setValue(createNewContact(eventParticipant, disease));
		createForm.setPerson(eventParticipant.getPerson());
		createForm.setPersonDetailsReadOnly();
		createForm.setDiseaseReadOnly();

		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE),
			createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final ContactDto dto = createForm.getValue();
				PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());

				fillPersonAddressIfEmpty(dto, () -> personDto);

				selectOrCreateContact(dto, personDto, selectedContactUuid -> {
					if (selectedContactUuid != null) {
						editData(selectedContactUuid);
					}
				});

			}
		});

		return createComponent;
	}

	public void selectOrCreateContact(final ContactDto contact, final PersonDto personDto, Consumer<String> resultConsumer) {
		ContactSelectionField contactSelect = new ContactSelectionField(
			contact,
			I18nProperties.getString(Strings.infoSelectOrCreateContact),
			personDto.getFirstName(),
			personDto.getLastName());
		contactSelect.setWidth(1024, Unit.PIXELS);

		if (contactSelect.hasMatches()) {
			// TODO add user right parameter
			final CommitDiscardWrapperComponent<ContactSelectionField> component = new CommitDiscardWrapperComponent<>(contactSelect);
			component.addCommitListener(() -> {
				final SimilarContactDto selectedContact = contactSelect.getValue();
				if (selectedContact != null) {
					if (resultConsumer != null) {
						resultConsumer.accept(selectedContact.getUuid());
					}
				} else {
					createNewContact(contact, resultConsumer);
				}
			});

			contactSelect.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateContact));
			contactSelect.selectBestMatch();
		} else {
			createNewContact(contact, resultConsumer);
		}
	}

	private void createNewContact(ContactDto contact, Consumer<String> resultConsumer) {
		final ContactDto savedContact = FacadeProvider.getContactFacade().save(contact);
		Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.WARNING_MESSAGE);
		resultConsumer.accept(savedContact.getUuid());
	}

	public CommitDiscardWrapperComponent<ContactDataForm> getContactDataEditComponent(
		String contactUuid,
		final ViewMode viewMode,
		boolean isPsuedonymized) {

		//editForm.setWidth(editForm.getWidth() * 8/12, Unit.PIXELS);
		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactUuid);
		AutomaticDeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getContactFacade().getAutomaticDeletionInfo(contactUuid);

		ContactDataForm editForm = new ContactDataForm(contact.getDisease(), viewMode, isPsuedonymized);
		editForm.setValue(contact);
		final CommitDiscardWrapperComponent<ContactDataForm> editComponent = new CommitDiscardWrapperComponent<ContactDataForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT),
			editForm.getFieldGroup());

		if (automaticDeletionInfoDto != null) {
			editComponent.getButtonsPanel().addComponentAsFirst(new AutomaticDeletionLabel(automaticDeletionInfoDto));
		}

		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				ContactDto dto = editForm.getValue();

				fillPersonAddressIfEmpty(dto, () -> FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid()));

				FacadeProvider.getContactFacade().save(dto);

				Notification.show(I18nProperties.getString(Strings.messageContactSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getContactFacade().delete(contact.getUuid());
				UI.getCurrent().getNavigator().navigateTo(ContactsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityContact));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(
					contact,
					FacadeProvider.getContactFacade(),
					CoreEntityArchiveMessages.CONTACT,
					editComponent,
					() -> navigateToView(ContactDataView.VIEW_NAME, contact.getUuid(), false));
		}

		return editComponent;
	}

	public void showBulkContactDataEditComponent(Collection<? extends ContactIndexDto> selectedContacts, String caseUuid) {
		if (selectedContacts.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoContactsSelected),
				I18nProperties.getString(Strings.messageNoContactsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if cases with multiple districts have been selected
		String districtUuid = null;
		for (ContactIndexDto selectedContact : selectedContacts) {
			String selectedDistrictUuid = selectedContact.getDistrictUuid();
			if (districtUuid == null) {
				districtUuid = selectedDistrictUuid;
			} else if (!districtUuid.equals(selectedDistrictUuid)) {
				districtUuid = null;
				break;
			}
		}

		DistrictReferenceDto district = districtUuid != null ? FacadeProvider.getDistrictFacade().getReferenceByUuid(districtUuid) : null;

		// Create a temporary contact in order to use the CommitDiscardWrapperComponent
		ContactBulkEditData bulkEditData = new ContactBulkEditData();
		BulkContactDataForm form = new BulkContactDataForm(district, selectedContacts);
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkContactDataForm> editView =
			new CommitDiscardWrapperComponent<BulkContactDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditContacts));

		editView.addCommitListener(() -> {
			ContactBulkEditData updatedBulkEditData = form.getValue();
			ContactFacade contactFacade = FacadeProvider.getContactFacade();

			boolean classificationChange = form.getClassificationCheckBox().getValue();
			boolean contactOfficerChange = district != null ? form.getContactOfficerCheckBox().getValue() : false;

			int changedContacts = bulkEdit(selectedContacts, updatedBulkEditData, contactFacade, classificationChange, contactOfficerChange);

			popupWindow.close();
			if (caseUuid == null) {
				overview();
			} else {
				caseContactsOverview(caseUuid);
			}

			if (changedContacts == selectedContacts.size()) {
				Notification.show(I18nProperties.getString(Strings.messageContactsEdited), Type.HUMANIZED_MESSAGE);
			} else {
				NotificationHelper.showNotification(
					String.format(I18nProperties.getString(Strings.messageContactsEditedExceptArchived), changedContacts),
					Type.HUMANIZED_MESSAGE,
					-1);
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());
	}

	private int bulkEdit(
		Collection<? extends ContactIndexDto> selectedContacts,
		ContactBulkEditData updatedContactBulkEditData,
		ContactFacade contactFacade,
		boolean classificationChange,
		boolean contactOfficerChange) {

		return contactFacade.saveBulkContacts(
			selectedContacts.stream().map(ContactIndexDto::getUuid).collect(Collectors.toList()),
			updatedContactBulkEditData,
			classificationChange,
			contactOfficerChange);
	}

	public void deleteAllSelectedItems(Collection<? extends ContactIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoContactsSelected),
				I18nProperties.getString(Strings.messageNoContactsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteContacts), selectedRows.size()),
				new Runnable() {

					public void run() {
						for (ContactIndexDto selectedRow : selectedRows) {
							FacadeProvider.getContactFacade().delete(selectedRow.getUuid());
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingContactsDeleted),
							I18nProperties.getString(Strings.messageContactsDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void cancelFollowUpOfAllSelectedItems(Collection<? extends ContactIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoContactsSelected),
				I18nProperties.getString(Strings.messageNoContactsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				String.format(I18nProperties.getString(Strings.headingContactsCancelFollowUp)),
				new Label(String.format(I18nProperties.getString(Strings.confirmationCancelFollowUp), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						for (ContactIndexDto contact : selectedRows) {
							if (!FollowUpStatus.NO_FOLLOW_UP.equals(contact.getFollowUpStatus())
								&& !FollowUpStatus.CANCELED.equals(contact.getFollowUpStatus())) {
								ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(contact.getUuid());
								contactDto.setFollowUpStatus(FollowUpStatus.CANCELED);
								contactDto.addToFollowUpComment(
									String.format(I18nProperties.getString(Strings.infoCanceledBy), UserProvider.getCurrent().getUserName()));
								FacadeProvider.getContactFacade().save(contactDto);
							}
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingFollowUpCanceled),
							I18nProperties.getString(Strings.messageFollowUpCanceled),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void setAllSelectedItemsToLostToFollowUp(Collection<? extends ContactIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoContactsSelected),
				I18nProperties.getString(Strings.messageNoContactsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				String.format(I18nProperties.getString(Strings.headingContactsLostToFollowUp)),
				new Label(String.format(I18nProperties.getString(Strings.confirmationLostToFollowUp), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						for (ContactIndexDto contact : selectedRows) {
							if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
								ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(contact.getUuid());
								contactDto.setFollowUpStatus(FollowUpStatus.LOST);
								contactDto.addToFollowUpComment(
									String.format(I18nProperties.getString(Strings.infoLostToFollowUpBy), UserProvider.getCurrent().getUserName()));
								FacadeProvider.getContactFacade().save(contactDto);
							}
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingFollowUpStatusChanged),
							I18nProperties.getString(Strings.messageFollowUpStatusChanged),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void openSelectCaseForContactWindow(Disease disease, Consumer<CaseSelectionDto> selectedCaseCallback) {

		CaseSelectionField selectionField = new CaseSelectionField(disease);
		selectionField.setWidth(1280, Unit.PIXELS);

		final CommitDiscardWrapperComponent<CaseSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		component.getCommitButton().setEnabled(false);
		component.addCommitListener(() -> {
			selectedCaseCallback.accept(selectionField.getValue());
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingSelectSourceCase));
	}

	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String contactUuid) {

		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactUuid);
		EpiDataForm epiDataForm = new EpiDataForm(contact.getDisease(), ContactDto.class, contact.getEpiData().isPseudonymized(), null);
		epiDataForm.setValue(contact.getEpiData());

		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(
			epiDataForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT),
			epiDataForm.getFieldGroup());

		editView.addCommitListener(() -> {
			ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(contactUuid);
			contactDto.setEpiData(epiDataForm.getValue());
			FacadeProvider.getContactFacade().save(contactDto);
			Notification.show(I18nProperties.getString(Strings.messageContactSaved), Type.WARNING_MESSAGE);
			SormasUI.refreshView();
		});

		return editView;
	}

	public void deleteContact(ContactIndexDto contact, Runnable callback) {
		VaadinUiUtil.showDeleteConfirmationWindow(
			String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getString(Strings.entityContact)),
			() -> {
				FacadeProvider.getContactFacade().delete(contact.getUuid());
				callback.run();
			});
	}

	public TitleLayout getContactViewTitleLayout(ContactDto contact) {
		TitleLayout titleLayout = new TitleLayout();

		RowLayout diseaseRow = new RowLayout();
		diseaseRow.addToLayout(DiseaseHelper.toString(contact.getDisease(), contact.getDiseaseDetails()));
		diseaseRow.addToLayout(DiseaseHelper.variantInBrackets(contact.getDiseaseVariant()), CssStyles.LABEL_PRIMARY);

		titleLayout.addRow(diseaseRow);

		titleLayout.addRow(contact.getContactClassification().toString());

		String shortUuid = DataHelper.getShortUuid(contact.getUuid());
		String contactPersonFullName = contact.getPerson().getCaption();
		StringBuilder mainRowText = new StringBuilder();
		if (StringUtils.isNotBlank(contactPersonFullName)) {
			mainRowText.append(contactPersonFullName);

			PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
			String dateOfBirth =
				DateFormatHelper.formatDate(contactPerson.getBirthdateDD(), contactPerson.getBirthdateMM(), contactPerson.getBirthdateYYYY());
			if (StringUtils.isNotBlank(dateOfBirth)) {
				mainRowText.append(" (* ").append(dateOfBirth).append(")");
			}

			if (contact.getCaze() != null && (contact.getCaze().getFirstName() != null || contact.getCaze().getLastName() != null)) {
				mainRowText.append(" ")
					.append(I18nProperties.getString(Strings.toCase))
					.append(" ")
					.append(PersonDto.buildCaption(contact.getCaze().getFirstName(), contact.getCaze().getLastName()));
			}
		}
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private void fillPersonAddressIfEmpty(ContactDto contact, Supplier<PersonDto> personSupplier) {
		// set the contact person's address to the one of the case when it is currently empty and
		// the relationship with the case has been set to living in the same household
		if (contact.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && contact.getCaze() != null) {
			PersonDto person = personSupplier.get();
			if (person.getAddress().checkIsEmptyLocation()) {
				CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(contact.getCaze().getUuid());
				person.getAddress().setRegion(CaseLogic.getRegionWithFallback(caze));
				person.getAddress().setDistrict(CaseLogic.getDistrictWithFallback(caze));
				person.getAddress().setCommunity(CaseLogic.getCommunityWithFallback(caze));
			}
			FacadeProvider.getPersonFacade().savePerson(person);
		}
	}
}
