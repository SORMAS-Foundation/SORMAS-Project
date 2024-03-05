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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactBulkEditData;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionField;
import de.symeda.sormas.ui.contact.components.linelisting.layout.LineListingLayout;
import de.symeda.sormas.ui.epidata.ContactEpiDataView;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DeletableUtils;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.linelisting.model.LineDto;
import de.symeda.sormas.ui.utils.components.page.title.RowLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class ContactController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public ContactController() {

	}

	public void registerViews(Navigator navigator) {
		navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
		if (UiUtil.permitted(UserRight.CASE_MERGE)) {
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

	private void saveContactsFromLineListing(LineListingLayout lineListingForm, LinkedList<LineDto<ContactDto>> contacts) {
		try {
			lineListingForm.validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		AdoptAddressLayout adoptAddressLayout = lineListingForm.getSharedInfoField().getCaseSelector().getAdoptAddressLayout();
		boolean adoptHomeAddress = adoptAddressLayout != null ? adoptAddressLayout.isAdoptAddress() : false;

		while (!contacts.isEmpty()) {
			LineDto<ContactDto> contactLineDto = contacts.pop();
			ContactDto newContact = contactLineDto.getEntity();
			PersonDto newPerson = contactLineDto.getPerson();

			ControllerProvider.getPersonController()
				.selectOrCreatePerson(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
					if (selectedPerson != null) {
						newContact.setPerson(selectedPerson);

						selectOrCreateContact(newContact, FacadeProvider.getPersonFacade().getByUuid(selectedPerson.getUuid()), uuid -> {
							if (uuid == null) {
								FacadeProvider.getContactFacade().save(newContact);
								Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.ASSISTIVE_NOTIFICATION);
							}
						});

						if (contacts.isEmpty()) {
							lineListingForm.closeWindow();
							ControllerProvider.getContactController().navigateToIndex();
						}
					}
					if (adoptHomeAddress && ContactRelation.SAME_HOUSEHOLD.equals(newContact.getRelationToCase())) {
						FacadeProvider.getPersonFacade()
							.copyHomeAddress(
								FacadeProvider.getCaseFacade().getByUuid(newContact.getCaze().getUuid()).getPerson(),
								newContact.getPerson());
					}
				}, true);
		}
	}

	public void openLineListingWindow(EventDto eventDto, Set<EventParticipantIndexDto> eventParticipantIndexDtos) {
		if (eventParticipantIndexDtos.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventParticipantsSelected),
				I18nProperties.getString(Strings.messageNoEventParticipantsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {

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
	}

	private void saveContactsFromEventParticipantsLineListing(LineListingLayout lineListingForm, LinkedList<LineDto<ContactDto>> contacts) {
		try {
			lineListingForm.validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		while (!contacts.isEmpty()) {
			LineDto<ContactDto> contactLineDto = contacts.pop();
			ContactDto newContact = contactLineDto.getEntity();
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

			if (contacts.isEmpty()) {
				lineListingForm.closeWindow();
				ControllerProvider.getContactController().navigateToIndex();
			}
		}
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

	public void create(PersonReferenceDto personRef) {
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personRef.getUuid());

		CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent(person);
		createComponent.getWrappedComponent().getPersonCreateForm().setSearchedPerson(person);
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
		ViewModelProviders.of(ContactsView.class).get(ContactCriteria.class, contactCriteria);

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

	public void navigateToMergeContactsView(ContactCriteria criteria) {
		ViewModelProvider viewModelProvider = ViewModelProviders.of(MergeContactsView.class);

		// update the current criteria
		viewModelProvider.remove(ContactCriteria.class);
		viewModelProvider.get(ContactCriteria.class, criteria);

		// force the grid to load as it is filtered, so it should not take too long to load
		viewModelProvider.remove(MergeContactsViewConfiguration.class);
		viewModelProvider.get(MergeContactsViewConfiguration.class, new MergeContactsViewConfiguration(true));

		String navigationState = AbstractView.buildNavigationState(MergeContactsView.VIEW_NAME, criteria);
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

	private ContactDto createNewContact(PersonDto person) {
		ContactDto contact = ContactDto.build(person);

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

		final PersonDto casePerson = caze != null ? FacadeProvider.getPersonFacade().getByUuid(caze.getPerson().getUuid()) : null;
		ContactCreateForm createForm = new ContactCreateForm(
			caze != null ? caze.getDisease() : null,
			caze != null && !asSourceContact,
			asSourceContact,
			!createdFromLabMesssage);

		createForm.setValue(createNewContact(caze, asSourceContact));
		if (casePerson != null && asSourceContact) {
			createForm.setPerson(casePerson);
		}
		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent =
			new CommitDiscardWrapperComponent<ContactCreateForm>(createForm, UiUtil.permitted(UserRight.CONTACT_CREATE), createForm.getFieldGroup());

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
					PersonDto dbPerson = FacadeProvider.getPersonFacade().getByUuid(dto.getPerson().getUuid());
					if (dbPerson == null) {
						PersonDto personDto = PersonDto.build();
						transferDataToPerson(createForm, personDto);
						FacadeProvider.getPersonFacade().save(personDto);
						dto.setPerson(personDto.toReference());
						createNewContact(dto, e -> {
						});
					} else {
						transferDataToPerson(createForm, dbPerson);
						FacadeProvider.getPersonFacade().save(dbPerson);
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

									selectOrCreateContact(dto, selectedPerson, selectedContactUuid -> {
										if (selectedContactUuid != null) {
											editData(selectedContactUuid);
										}
									});
								}
								if (createForm.adoptAddressLayout.isAdoptAddress()) {
									FacadeProvider.getPersonFacade()
										.copyHomeAddress(
											FacadeProvider.getCaseFacade().getByUuid(dto.getCaze().getUuid()).getPerson(),
											dto.getPerson());
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

		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent =
			new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.CONTACT_CREATE), createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final ContactDto dto = createForm.getValue();
				PersonFacade personFacade = FacadeProvider.getPersonFacade();
				PersonDto personDto = personFacade.getByUuid(dto.getPerson().getUuid());
				transferDataToPerson(createForm, personDto);
				personFacade.save(personDto);

				selectOrCreateContact(dto, personDto, selectedContactUuid -> {
					if (selectedContactUuid != null) {
						editData(selectedContactUuid);
					}
				});

			}
		});

		return createComponent;
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(PersonDto person) {
		final ContactCreateForm createForm;

		createForm = new ContactCreateForm(null, false, false, true);
		createForm.setValue(createNewContact(person));
		createForm.setPerson(person);
		createForm.setPersonDetailsReadOnly();

		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent =
			new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.CONTACT_CREATE), createForm.getFieldGroup());

		createComponent.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final ContactDto dto = createForm.getValue();
				PersonFacade personFacade = FacadeProvider.getPersonFacade();
				PersonDto personDto = personFacade.getByUuid(dto.getPerson().getUuid());
				transferDataToPerson(createForm, personDto);
				personFacade.save(personDto);

				selectOrCreateContact(dto, personDto, selectedContactUuid -> {
					if (selectedContactUuid != null) {
						editData(selectedContactUuid);
					}
				});

			}
		});

		return createComponent;
	}

	private void selectOrCreateContact(final ContactDto contact, final PersonDto personDto, Consumer<String> resultConsumer) {
		selectOrCreateContact(contact, personDto.toReference(), resultConsumer);
	}

	public void selectOrCreateContact(final ContactDto contact, final PersonReferenceDto personReferenceDto, Consumer<String> resultConsumer) {
		ContactSelectionField contactSelect =
			new ContactSelectionField(contact, personReferenceDto, I18nProperties.getString(Strings.infoSelectOrCreateContact));
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
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getContactFacade().getAutomaticDeletionInfo(contactUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getContactFacade().getManuallyDeletionInfo(contactUuid);

		ContactDataForm editForm = new ContactDataForm(contact.getDisease(), viewMode, isPsuedonymized, contact.isInJurisdiction());
		editForm.setValue(contact);
		final CommitDiscardWrapperComponent<ContactDataForm> editComponent =
			new CommitDiscardWrapperComponent<ContactDataForm>(editForm, true, editForm.getFieldGroup());

		editComponent.getButtonsPanel()
			.addComponentAsFirst(new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, contact.isDeleted(), ContactDto.I18N_PREFIX));

		if (contact.isDeleted()) {
			editComponent.getWrappedComponent().getField(ContactDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(ContactDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(ContactDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				ContactDto dto = editForm.getValue();

				FacadeProvider.getContactFacade().save(dto);

				Notification.show(I18nProperties.getString(Strings.messageContactSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		if (UserProvider.getCurrent().getUserRoles().stream().anyMatch(userRoleDto -> !userRoleDto.isRestrictAccessToAssignedEntities())
			|| DataHelper.equal(contact.getContactOfficer(), UserProvider.getCurrent().getUserReference())) {
			if (UiUtil.permitted(UserRight.CONTACT_DELETE)) {
				editComponent.addDeleteWithReasonOrRestoreListener(
					ContactsView.VIEW_NAME,
					getDeleteConfirmationDetails(Collections.singletonList(contact.getUuid())),
					I18nProperties.getString(Strings.entityContact),
					contactUuid,
					FacadeProvider.getContactFacade());
			}

			// Initialize 'Archive' button
			if (UiUtil.permitted(UserRight.CONTACT_ARCHIVE)) {
				ControllerProvider.getArchiveController()
					.addArchivingButton(
						contact,
						ArchiveHandlers.forContact(),
						editComponent,
						() -> navigateToView(ContactDataView.VIEW_NAME, contact.getUuid(), false));
			}
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.CONTACT_EDIT,
			null,
			UserRight.CONTACT_DELETE,
			UserRight.CONTACT_ARCHIVE,
			FacadeProvider.getContactFacade().getEditPermissionType(contactUuid),
			contact.isInJurisdiction());

		return editComponent;
	}

	private String getDeleteConfirmationDetails(List<String> contactUuids) {
		boolean hasPendingRequest = FacadeProvider.getSormasToSormasContactFacade().hasPendingRequest(contactUuids);

		return hasPendingRequest ? "<br/>" + I18nProperties.getString(Strings.messageDeleteWithPendingShareRequest) + "<br/>" : "";
	}

	public <T extends ContactIndexDto> void showBulkContactDataEditComponent(
		Collection<T> selectedContacts,
		String caseUuid,
		AbstractContactGrid<?> contactGrid) {
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
		final CommitDiscardWrapperComponent<BulkContactDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditContacts));

		editView.addCommitListener(() -> {
			ContactBulkEditData updatedBulkEditData = form.getValue();
			ContactFacade contactFacade = FacadeProvider.getContactFacade();

			boolean classificationChange = form.getClassificationCheckBox().getValue();
			boolean contactOfficerChange = district != null ? form.getContactOfficerCheckBox().getValue() : false;

			List<ContactIndexDto> selectedContactsCpy = new ArrayList<>(selectedContacts);

			BulkOperationHandler.<ContactIndexDto> forBulkEdit()
				.doBulkOperation(
					selectedEntries -> contactFacade.saveBulkContacts(
						selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
						updatedBulkEditData,
						classificationChange,
						contactOfficerChange),
					selectedContactsCpy,
					bulkOperationCallback(caseUuid, contactGrid, popupWindow));
		});

		editView.addDiscardListener(popupWindow::close);
	}

	private <T extends ContactIndexDto> Consumer<List<T>> bulkOperationCallback(
		String caseUuid,
		AbstractContactGrid<?> contactGrid,
		Window popupWindow) {
		return remainingContacts -> {
			if (popupWindow != null) {
				popupWindow.close();
			}
			contactGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingContacts)) {
				if (contactGrid instanceof ContactGrid) {
					((ContactGrid) contactGrid).asMultiSelect().selectItems(remainingContacts.toArray(new ContactIndexDto[0]));
				} else if (contactGrid instanceof ContactGridDetailed) {
					((ContactGridDetailed) contactGrid).asMultiSelect().selectItems(remainingContacts.toArray(new ContactIndexDetailedDto[0]));
				}
			} else {
				if (caseUuid == null) {
					overview();
				} else {
					caseContactsOverview(caseUuid);
				}
			}
		};
	}

	public void deleteAllSelectedItems(Collection<? extends ContactIndexDto> selectedRows, AbstractContactGrid<?> contactGrid) {

		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(selectedRows, DeleteRestoreHandlers.forContact(), bulkOperationCallback(null, contactGrid, null));

	}

	public void restoreSelectedContacts(Collection<? extends ContactIndexDto> selectedRows, AbstractContactGrid<?> contactGrid) {
		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(selectedRows, DeleteRestoreHandlers.forContact(), bulkOperationCallback(null, contactGrid, null));
	}

	public <T extends ContactIndexDto> void cancelFollowUpOfAllSelectedItems(
		Collection<T> selectedRows,
		String caseUuid,
		AbstractContactGrid<?> contactGrid) {

		if (selectedRows.isEmpty()) {
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
					if (Boolean.TRUE.equals(confirmed)) {
						String userName = UserProvider.getCurrent().getUserName();

						new BulkOperationHandler<ContactIndexDto>(
							Strings.messageFollowUpCanceled,
							Strings.messageVisitsWithWrongStatusNotCancelled,
							Strings.headingSomeVisitsNotCancelled,
							Strings.headingVisitsNotCancelled,
							Strings.messageCountVisitsNotCancelled,
							null,
							null,
							Strings.messageCountVisitsNotCancelledAccessDeniedReason,
							Strings.messageNoEligibleVisitForCancellation,
							Strings.infoBulkProcessFinishedWithSkipsOutsideJurisdictionOrNotEligible,
							Strings.infoBulkProcessFinishedWithoutSuccess).doBulkOperation(batch -> {
								List<ProcessedEntity> processedContacts = new ArrayList<>();

								for (ContactIndexDto contact : batch) {
									if (!FollowUpStatus.NO_FOLLOW_UP.equals(contact.getFollowUpStatus())
										&& !FollowUpStatus.CANCELED.equals(contact.getFollowUpStatus())) {
										processedContacts.add(setFollowUpStatus(contact, FollowUpStatus.CANCELED, Strings.infoCanceledBy, userName));
									} else {
										processedContacts.add(new ProcessedEntity(contact.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
									}
								}
								return processedContacts;
							}, new ArrayList<>(selectedRows), bulkOperationCallback(caseUuid, contactGrid, null));
					}
				});
		}
	}

	public <T extends ContactIndexDto> void setAllSelectedItemsToLostToFollowUp(
		Collection<T> selectedRows,
		String caseUuid,
		AbstractContactGrid<?> contactGrid) {
		if (selectedRows.isEmpty()) {
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
					if (Boolean.TRUE.equals(confirmed)) {
						String userName = UserProvider.getCurrent().getUserName();

						new BulkOperationHandler<ContactIndexDto>(
							Strings.messageFollowUpStatusChanged,
							Strings.messageVisitsWithWrongStatusNotSetToLost,
							Strings.headingSomeVisitsNotSetToLost,
							Strings.headingVisitsNotSetToLost,
							Strings.messageCountVisitsNotSetToLost,
							null,
							null,
							Strings.messageCountVisitsNotSetToLostAccessDeniedReason,
							Strings.messageNoEligibleVisitForSettingToLost,
							Strings.infoBulkProcessFinishedWithSkipsOutsideJurisdictionOrNotEligible,
							Strings.infoBulkProcessFinishedWithoutSuccess).doBulkOperation(batch -> {
								List<ProcessedEntity> processedContacts = new ArrayList<>();

								for (ContactIndexDto contact : batch) {
									if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
										processedContacts
											.add(setFollowUpStatus(contact, FollowUpStatus.LOST, Strings.infoLostToFollowUpBy, userName));
									} else {
										processedContacts.add(new ProcessedEntity(contact.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
									}
								}
								return processedContacts;
							}, new ArrayList<>(selectedRows), bulkOperationCallback(caseUuid, contactGrid, null));
					}
				});
		}
	}

	public ProcessedEntity setFollowUpStatus(ContactIndexDto contact, FollowUpStatus followUpStatus, String followUpComment, String userName) {

		ProcessedEntity processedContact;
		ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(contact.getUuid());
		contactDto.setFollowUpStatus(followUpStatus);
		contactDto.addToFollowUpComment(String.format(I18nProperties.getString(followUpComment), userName));
		try {
			FacadeProvider.getContactFacade().save(contactDto);
			processedContact = new ProcessedEntity(contact.getUuid(), ProcessedEntityStatus.SUCCESS);
		} catch (AccessDeniedException e) {
			processedContact = new ProcessedEntity(contact.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
			logger.error("The follow up status of contact with uuid {} could not be set due to an AccessDeniedException", contact.getUuid(), e);
		} catch (Exception e) {
			processedContact = new ProcessedEntity(contact.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE);
			logger.error("The follow up status of contact with uuid {} could not be set due to an Exception", contact.getUuid(), e);
		}
		return processedContact;
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

	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String contactUuid, boolean isEditAllowed) {

		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(contactUuid);
		EpiDataDto epiData = contact.getEpiData();
		EpiDataForm epiDataForm =
			new EpiDataForm(contact.getDisease(), ContactDto.class, epiData.isPseudonymized(), epiData.isInJurisdiction(), null, isEditAllowed);
		epiDataForm.setValue(epiData);

		final CommitDiscardWrapperComponent<EpiDataForm> editView =
			new CommitDiscardWrapperComponent<EpiDataForm>(epiDataForm, epiDataForm.getFieldGroup());

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
		DeletableUtils.showDeleteWithReasonPopup(
			String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getString(Strings.entityContact)),
			(deleteDetails) -> {
				FacadeProvider.getContactFacade().delete(contact.getUuid(), deleteDetails);
				callback.run();
			});
	}

	public void archiveAllSelectedItems(Collection<ContactIndexDto> selectedRows, AbstractContactGrid<?> contactGrid) {
		ControllerProvider.getArchiveController()
			.archiveSelectedItems(selectedRows, ArchiveHandlers.forContact(), bulkOperationCallback(null, contactGrid, null));
	}

	public void dearchiveAllSelectedItems(Collection<ContactIndexDto> selectedRows, AbstractContactGrid<?> contactGrid) {
		ControllerProvider.getArchiveController()
			.dearchiveSelectedItems(selectedRows, ArchiveHandlers.forContact(), bulkOperationCallback(null, contactGrid, null));
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

			PersonDto contactPerson = FacadeProvider.getPersonFacade().getByUuid(contact.getPerson().getUuid());
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

	public void linkSelectedContactsToEvent(Collection<? extends ContactIndexDto> selectedRows, AbstractContactGrid<?> contactGrid) {
		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoContactsSelected),
				I18nProperties.getString(Strings.messageNoContactsSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		if (!selectedRows.stream().allMatch(contact -> contact.getDisease().equals(selectedRows.stream().findAny().get().getDisease()))) {
			new Notification(I18nProperties.getString(Strings.messageBulkContactsWithDifferentDiseasesSelected), Notification.Type.WARNING_MESSAGE)
				.show(Page.getCurrent());
			return;
		}

		ControllerProvider.getEventController()
			.selectOrCreateEventForContactList(selectedRows.stream().map(ContactIndexDto::toReference).collect(Collectors.toList()), remaining -> {
				bulkOperationCallback(null, contactGrid, null).accept(
					selectedRows.stream()
						.filter(s -> remaining.stream().anyMatch(r -> r.getUuid().equals(s.getUuid())))
						.collect(Collectors.toList()));
			});
	}
}
