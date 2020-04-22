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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import java.util.Collection;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.caze.CaseSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ContactController {

	public ContactController() {

	}

	public void registerViews(Navigator navigator) {
		navigator.addView(ContactsView.VIEW_NAME, ContactsView.class);
		navigator.addView(ContactDataView.VIEW_NAME, ContactDataView.class);
		navigator.addView(ContactPersonView.VIEW_NAME, ContactPersonView.class);
		navigator.addView(ContactVisitsView.VIEW_NAME, ContactVisitsView.class);
	}

	public void create() {
		create(null);
	}

	public void create(CaseReferenceDto caseRef) {
		CaseDataDto caze = null;
		if (caseRef != null) {
			caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		}
		CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent(caze);
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

	public void editData(String contactUuid) {
		String navigationState = ContactDataView.VIEW_NAME + "/" + contactUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);	
	}

	public void editPerson(String contactUuid) {
		String navigationState = ContactPersonView.VIEW_NAME + "/" + contactUuid;
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
		page.setUriFragment("!" + ContactsView.VIEW_NAME + "/"
				+ fragmentParameter, false);
	}

	private ContactDto createNewContact(CaseDataDto caze) {
		ContactDto contact = caze != null ? ContactDto.build(caze) : ContactDto.build();

		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		contact.setReportingUser(userReference);

		return contact;
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(CaseDataDto caze) {
		ContactCreateForm createForm = new ContactCreateForm(UserRight.CONTACT_CREATE, caze != null ? caze.getDisease() : null, caze != null);
		createForm.setValue(createNewContact(caze));
		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<ContactCreateForm>(createForm, createForm.getFieldGroup());

		createComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final ContactDto dto = createForm.getValue();
					final PersonDto person = PersonDto.build();
					person.setFirstName(createForm.getPersonFirstName());
					person.setLastName(createForm.getPersonLastName());
					person.setBirthdateYYYY(createForm.getBirthdateYYYY());
					person.setBirthdateMM(createForm.getBirthdateMM());
					person.setBirthdateDD(createForm.getBirthdateDD());
					person.setSex(createForm.getSex());

					ControllerProvider.getPersonController().selectOrCreatePerson(
							person,
							I18nProperties.getString(Strings.infoSelectOrCreatePersonForContact),
							selectedPerson -> {
								if (selectedPerson != null) {
									dto.setPerson(selectedPerson);

									// set the contact person's address to the one of the case when it is currently empty and
									// the relationship with the case has been set to living in the same household
									if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && dto.getCaze() != null) {
										PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid());
										if (personDto.getAddress().isEmptyLocation()) {
											CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(dto.getCaze().getUuid());
											personDto.getAddress().setRegion(caseDto.getRegion());
											personDto.getAddress().setDistrict(caseDto.getDistrict());
											personDto.getAddress().setCommunity(caseDto.getCommunity());
										}
										FacadeProvider.getPersonFacade().savePerson(personDto);
									}

									FacadeProvider.getContactFacade().saveContact(dto);
									Notification.show(I18nProperties.getString(Strings.messageContactCreated), Type.WARNING_MESSAGE);
									editData(dto.getUuid());
								}
							});
				}
			}
		});

		return createComponent;
	}

	public CommitDiscardWrapperComponent<ContactDataForm> getContactDataEditComponent(String contactUuid) {
		ContactDataForm editForm = new ContactDataForm(UserRight.CONTACT_EDIT);
		//editForm.setWidth(editForm.getWidth() * 8/12, Unit.PIXELS);
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(contactUuid);
		editForm.setValue(contact);
		final CommitDiscardWrapperComponent<ContactDataForm> editComponent = new CommitDiscardWrapperComponent<ContactDataForm>(editForm, editForm.getFieldGroup());

		editComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					ContactDto dto = editForm.getValue();

					// set the contact person's address to the one of the case when it is currently empty and
					// the relationship with the case has been set to living in the same household
					if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD && dto.getCaze() != null) {
						PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(dto.getPerson().getUuid());
						if (person.getAddress().isEmptyLocation()) {
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

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editComponent.addDeleteListener(() -> {
				FacadeProvider.getContactFacade().deleteContact(contact.getUuid());
				UI.getCurrent().getNavigator().navigateTo(ContactsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityContact));
		}

		return editComponent;
	}

	public void showBulkContactDataEditComponent(Collection<ContactIndexDto> selectedContacts, String caseUuid) {
		if (selectedContacts.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoContactsSelected), 
					I18nProperties.getString(Strings.messageNoContactsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
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
		final CommitDiscardWrapperComponent<BulkContactDataForm> editView = new CommitDiscardWrapperComponent<BulkContactDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditContacts));

		editView.addCommitListener(new CommitListener() {
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

	public void deleteAllSelectedItems(Collection<ContactIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoContactsSelected), 
					I18nProperties.getString(Strings.messageNoContactsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteContacts), selectedRows.size()), new Runnable() {
				public void run() {
					for (ContactIndexDto selectedRow : selectedRows) {
						FacadeProvider.getContactFacade().deleteContact(selectedRow.getUuid());
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingContactsDeleted), 
							I18nProperties.getString(Strings.messageContactsDeleted), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void cancelFollowUpOfAllSelectedItems(Collection<ContactIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoContactsSelected), 
					I18nProperties.getString(Strings.messageNoContactsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationCancelFollowUp), selectedRows.size()), new Runnable() {
				public void run() {
					for (ContactIndexDto contact : selectedRows) {
						if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
							ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
							contactDto.setFollowUpStatus(FollowUpStatus.CANCELED);
							contactDto.setFollowUpComment(String.format(I18nProperties.getString(Strings.infoCanceledBy), UserProvider.getCurrent().getUserName()));
							FacadeProvider.getContactFacade().saveContact(contactDto);
						}
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingFollowUpCanceled), 
							I18nProperties.getString(Strings.messageFollowUpCanceled), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void setAllSelectedItemsToLostToFollowUp(Collection<ContactIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(I18nProperties.getString(Strings.headingNoContactsSelected), 
					I18nProperties.getString(Strings.messageNoContactsSelected), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationLostToFollowUp), selectedRows.size()), new Runnable() {
				public void run() {
					for (ContactIndexDto contact : selectedRows) {
						if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
							ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
							contactDto.setFollowUpStatus(FollowUpStatus.LOST);
							contactDto.setFollowUpComment(String.format(I18nProperties.getString(Strings.infoLostToFollowUpBy), UserProvider.getCurrent().getUserName()));
							FacadeProvider.getContactFacade().saveContact(contactDto);
						}
					}
					callback.run();
					new Notification(I18nProperties.getString(Strings.headingFollowUpStatusChanged), 
							I18nProperties.getString(Strings.messageFollowUpStatusChanged), Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
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

}
