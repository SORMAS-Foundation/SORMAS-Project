/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import java.util.Collection;
import java.util.function.Consumer;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.caze.CaseSelectionField;
import de.symeda.sormas.ui.epidata.ContactEpiDataView;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class ContactController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public ContactController() {

	}

	public void registerViews(Navigator navigator) {
		navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
		navigator.addView(ContactDataView.VIEW_NAME, ContactDataView.class);
		navigator.addView(ContactPersonView.VIEW_NAME, ContactPersonView.class);
		navigator.addView(ContactVisitsView.VIEW_NAME, ContactVisitsView.class);
		navigator.addView(ContactEpiDataView.VIEW_NAME, ContactEpiDataView.class);
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
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid());

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
		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
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
		ContactCreateForm createForm =
			new ContactCreateForm(caze != null ? caze.getDisease() : null, caze != null && !asSourceContact, asSourceContact);
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
				if (asSourceContact && alternativeCallback != null && casePerson != null) {
					selectOrCreateContact(dto, casePerson, I18nProperties.getString(Strings.infoSelectOrCreateContact), selectedContactUuid -> {
						if (selectedContactUuid != null) {
							ContactDto selectedContact = FacadeProvider.getContactFacade().getContactByUuid(selectedContactUuid);
							selectedContact.setResultingCase(caze.toReference());
							selectedContact.setResultingCaseUser(UserProvider.getCurrent().getUserReference());
							selectedContact.setContactStatus(ContactStatus.CONVERTED);
							selectedContact.setContactClassification(ContactClassification.CONFIRMED);
							FacadeProvider.getContactFacade().saveContact(selectedContact);

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
					final PersonDto person = PersonDto.build();
					transferDataToPerson(createForm, person);

					ControllerProvider.getPersonController()
						.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForContact), selectedPerson -> {
							if (selectedPerson != null) {
								dto.setPerson(selectedPerson);

								// set the contact person's address to the one of the case when it is currently empty and
								// the relationship with the case has been set to living in the same household
								if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && dto.getCaze() != null) {
									PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid());
									if (personDto.getAddress().checkIsEmptyLocation()) {
										CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
										personDto.getAddress().setRegion(caseDto.getRegion());
										personDto.getAddress().setDistrict(caseDto.getDistrict());
										personDto.getAddress().setCommunity(caseDto.getCommunity());
									}
									FacadeProvider.getPersonFacade().savePerson(personDto);
								}

								selectOrCreateContact(
									dto,
									person,
									I18nProperties.getString(Strings.infoSelectOrCreateContact),
									selectedContactUuid -> {
										if (selectedContactUuid != null) {
											editData(selectedContactUuid);
										}
									});
							}
						}, true);
				}
			}
		});

		return createComponent;

	}

	private void transferDataToPerson(ContactCreateForm createForm, PersonDto person) {
		person.setFirstName(createForm.getPersonFirstName());
		person.setLastName(createForm.getPersonLastName());
		person.setNationalHealthId(createForm.getNationalHealthId());
		person.setPassportNumber(createForm.getPassportNumber());
		person.setBirthdateYYYY(createForm.getBirthdateYYYY());
		person.setBirthdateMM(createForm.getBirthdateMM());
		person.setBirthdateDD(createForm.getBirthdateDD());
		person.setSex(createForm.getSex());
		person.setPhone(createForm.getPhone());
		person.setEmailAddress(createForm.getEmailAddress());
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(EventParticipantDto eventParticipant) {
		final ContactCreateForm createForm;
		final Disease disease;
		if (eventParticipant.getResultingCase() != null) {
			disease = FacadeProvider.getCaseFacade().getCaseDataByUuid(eventParticipant.getResultingCase().getUuid()).getDisease();
		} else {
			disease = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid()).getDisease();
		}
		createForm = new ContactCreateForm(disease, false, false);

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

				// set the contact person's address to the one of the case when it is currently empty and
				// the relationship with the case has been set to living in the same household
				if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && dto.getCaze() != null) {
					if (personDto.getAddress().checkIsEmptyLocation()) {
						CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
						personDto.getAddress().setRegion(caseDto.getRegion());
						personDto.getAddress().setDistrict(caseDto.getDistrict());
						personDto.getAddress().setCommunity(caseDto.getCommunity());
					}
					FacadeProvider.getPersonFacade().savePerson(personDto);
				}

				selectOrCreateContact(dto, personDto, I18nProperties.getString(Strings.infoSelectOrCreateContact), selectedContactUuid -> {
					if (selectedContactUuid != null) {
						editData(selectedContactUuid);
					}
				});

			}
		});

		return createComponent;
	}

	public void selectOrCreateContact(final ContactDto contact, final PersonDto personDto, String infoText, Consumer<String> resultConsumer) {
		ContactSelectionField contactSelect = new ContactSelectionField(contact, infoText, personDto.getFirstName(), personDto.getLastName());
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
		final ContactDto savedContact = FacadeProvider.getContactFacade().saveContact(contact);
		Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.WARNING_MESSAGE);
		resultConsumer.accept(savedContact.getUuid());
	}

	public CommitDiscardWrapperComponent<ContactDataForm> getContactDataEditComponent(
		String contactUuid,
		final ViewMode viewMode,
		boolean isPsuedonymized) {

		//editForm.setWidth(editForm.getWidth() * 8/12, Unit.PIXELS);
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactUuid);
		ContactDataForm editForm = new ContactDataForm(contact.getDisease(), viewMode, isPsuedonymized);
		editForm.setValue(contact);
		final CommitDiscardWrapperComponent<ContactDataForm> editComponent = new CommitDiscardWrapperComponent<ContactDataForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT),
			editForm.getFieldGroup());

		editComponent.addCommitListener(new CommitDiscardWrapperComponent.CommitListener() {

			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					ContactDto dto = editForm.getValue();

					// set the contact person's address to the one of the case when it is currently empty and
					// the relationship with the case has been set to living in the same household
					if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && dto.getCaze() != null) {
						PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());
						if (person.getAddress().checkIsEmptyLocation()) {
							CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
							person.getAddress().setRegion(caze.getRegion());
							person.getAddress().setDistrict(caze.getDistrict());
							person.getAddress().setCommunity(caze.getCommunity());
						}
						FacadeProvider.getPersonFacade().savePerson(person);
					}

					dto = FacadeProvider.getContactFacade().saveContact(dto);
					Notification.show(I18nProperties.getString(Strings.messageContactSaved), Type.WARNING_MESSAGE);
					SormasUI.refreshView();
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_DELETE)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getContactFacade().deleteContact(contact.getUuid());
				UI.getCurrent().getNavigator().navigateTo(ContactsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityContact));
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
			if (districtUuid == null) {
				districtUuid = selectedContact.getDistrictUuid();
			} else if (!districtUuid.equals(selectedContact.getDistrictUuid())) {
				districtUuid = null;
				break;
			}
		}

		DistrictReferenceDto district = districtUuid != null ? FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(districtUuid) : null;

		// Create a temporary contact in order to use the CommitDiscardWrapperComponent
		ContactBulkEditData bulkEditData = new ContactBulkEditData();
		BulkContactDataForm form = new BulkContactDataForm(district);
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkContactDataForm> editView =
			new CommitDiscardWrapperComponent<BulkContactDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditContacts));

		editView.addCommitListener(new CommitDiscardWrapperComponent.CommitListener() {

			@Override
			public void onCommit() {
				ContactBulkEditData updatedBulkEditData = form.getValue();
				for (ContactIndexDto indexDto : selectedContacts) {
					ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(indexDto.getUuid());
					if (form.getClassificationCheckBox().getValue() == true) {
						contactDto.setContactClassification(updatedBulkEditData.getContactClassification());
					}
					// Setting the contact officer is only allowed if all selected contacts are in the same district
					if (district != null && form.getContactOfficerCheckBox().getValue() == true) {
						contactDto.setContactOfficer(updatedBulkEditData.getContactOfficer());
					}

					FacadeProvider.getContactFacade().saveContact(contactDto);
				}
				popupWindow.close();
				if (caseUuid == null) {
					overview();
				} else {
					caseContactsOverview(caseUuid);
				}
				Notification.show(I18nProperties.getString(Strings.messageContactsEdited), Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());
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
							FacadeProvider.getContactFacade().deleteContact(selectedRow.getUuid());
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
							if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
								ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
								contactDto.setFollowUpStatus(FollowUpStatus.CANCELED);
								contactDto.setFollowUpComment(
									String.format(I18nProperties.getString(Strings.infoCanceledBy), UserProvider.getCurrent().getUserName()));
								FacadeProvider.getContactFacade().saveContact(contactDto);
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
								ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
								contactDto.setFollowUpStatus(FollowUpStatus.LOST);
								contactDto.setFollowUpComment(
									String.format(I18nProperties.getString(Strings.infoLostToFollowUpBy), UserProvider.getCurrent().getUserName()));
								FacadeProvider.getContactFacade().saveContact(contactDto);
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

	public void openSelectCaseForContactWindow(Disease disease, Consumer<CaseIndexDto> selectedCaseCallback) {

		CaseCriteria criteria = new CaseCriteria().disease(disease);
		CaseSelectionField selectionField = new CaseSelectionField(criteria);
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

		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactUuid);
		EpiDataForm epiDataForm = new EpiDataForm(contact.getDisease(), ContactDto.class, contact.getEpiData().isPseudonymized(), null);
		epiDataForm.setValue(contact.getEpiData());

		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(
			epiDataForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT),
			epiDataForm.getFieldGroup());

		editView.addCommitListener(() -> {
			ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contactUuid);
			contactDto.setEpiData(epiDataForm.getValue());
			FacadeProvider.getContactFacade().saveContact(contactDto);
			Notification.show(I18nProperties.getString(Strings.messageContactSaved), Type.WARNING_MESSAGE);
			SormasUI.refreshView();
		});

		return editView;
	}

	public void deleteContact(ContactIndexDto contact, Runnable callback) {
		VaadinUiUtil.showDeleteConfirmationWindow(
			String.format(I18nProperties.getString(Strings.confirmationDeleteEntity), I18nProperties.getString(Strings.entityContact)),
			() -> {
				FacadeProvider.getContactFacade().deleteContact(contact.getUuid());
				callback.run();
			});
	}

	public VerticalLayout getContactViewTitleLayout(ContactDto contact) {
		VerticalLayout titleLayout = new VerticalLayout();
		titleLayout.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		titleLayout.setSpacing(false);

		Label diseaseLabel = new Label(DiseaseHelper.toString(contact.getDisease(), contact.getDiseaseDetails()));
		CssStyles.style(diseaseLabel, CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponents(diseaseLabel);

		Label classificationLabel = new Label(contact.getContactClassification().toString());
		classificationLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponent(classificationLabel);

		String shortUuid = DataHelper.getShortUuid(contact.getUuid());
		String contactPersonFullName = contact.getPerson().getCaption();
		StringBuilder contactLabelSb = new StringBuilder();
		if (StringUtils.isNotBlank(contactPersonFullName)) {
			contactLabelSb.append(contactPersonFullName);

			PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
			if (contactPerson.getBirthdateDD() != null && contactPerson.getBirthdateMM() != null && contactPerson.getBirthdateYYYY() != null) {
				contactLabelSb.append(" (* ")
					.append(
						PersonHelper.formatBirthdate(
							contactPerson.getBirthdateDD(),
							contactPerson.getBirthdateMM(),
							contactPerson.getBirthdateYYYY(),
							I18nProperties.getUserLanguage()))
					.append(")");
			}

			if (contact.getCaze() != null && (contact.getCaze().getFirstName() != null || contact.getCaze().getLastName() != null)) {
				contactLabelSb.append(" ")
					.append(I18nProperties.getString(Strings.toCase))
					.append(" ")
					.append(PersonDto.buildCaption(contact.getCaze().getFirstName(), contact.getCaze().getLastName()));
			}
		}
		contactLabelSb.append(contactLabelSb.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		Label contactLabel = new Label(contactLabelSb.toString());
		contactLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		titleLayout.addComponent(contactLabel);

		return titleLayout;
	}
}
