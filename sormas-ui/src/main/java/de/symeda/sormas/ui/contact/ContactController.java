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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
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

	public void create(CaseReferenceDto caze) {
		CommitDiscardWrapperComponent<ContactCreateForm> createComponent = getContactCreateComponent(caze);
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create new contact");    	
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

	private ContactDto createNewContact(CaseReferenceDto caze) {
		ContactDto contact = new ContactDto();
		contact.setUuid(DataHelper.createUuid());

		contact.setCaze(caze);

		contact.setReportDateTime(new Date());
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		contact.setReportingUser(userReference);
		contact.setContactClassification(ContactClassification.UNCONFIRMED);
		contact.setContactStatus(ContactStatus.ACTIVE);

		return contact;
	}

	public CommitDiscardWrapperComponent<ContactCreateForm> getContactCreateComponent(CaseReferenceDto caze) {

		ContactCreateForm createForm = new ContactCreateForm(UserRight.CONTACT_CREATE);
		createForm.setValue(createNewContact(caze));
		final CommitDiscardWrapperComponent<ContactCreateForm> createComponent = new CommitDiscardWrapperComponent<ContactCreateForm>(createForm, createForm.getFieldGroup());

		createComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final ContactDto dto = createForm.getValue();

					ControllerProvider.getPersonController().selectOrCreatePerson(
							createForm.getPersonFirstName(), createForm.getPersonLastName(), 
							person -> {
								if (person != null) {
									dto.setPerson(person);

									// set the contact person's address to the one of the case when it is currently empty and
									// the relationship with the case has been set to living in the same household
									if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD) {
										PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid());
										if (personDto.getAddress().isEmptyLocation()) {
											CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
											personDto.getAddress().setRegion(caseDto.getRegion());
											personDto.getAddress().setDistrict(caseDto.getDistrict());
											personDto.getAddress().setCommunity(caseDto.getCommunity());
										}
										FacadeProvider.getPersonFacade().savePerson(personDto);
									}

									FacadeProvider.getContactFacade().saveContact(dto);
									Notification.show("New contact created", Type.WARNING_MESSAGE);
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
					if (dto.getRelationToCase() == ContactRelation.SAME_HOUSEHOLD) {
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
					Notification.show("Contact data saved", Type.WARNING_MESSAGE);
					editData(dto.getUuid());
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editComponent.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getContactFacade().deleteContact(contact.toReference(), UserProvider.getCurrent().getUserReference().getUuid());
					UI.getCurrent().getNavigator().navigateTo(ContactsView.VIEW_NAME);
				}
			}, I18nProperties.getString(Strings.contact));
		}

		return editComponent;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void showBulkContactDataEditComponent(Collection<Object> selectedRows, String caseUuid) {
		if (selectedRows.size() == 0) {
			new Notification("No contacts selected", "You have not selected any contacts.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
			return;
		}

		List<ContactIndexDto> selectedContacts = new ArrayList(selectedRows);

		// Check if cases with multiple districts have been selected
		String districtUuid = selectedContacts.get(0).getCaseDistrictUuid();
		for (ContactIndexDto selectedContact : selectedContacts) {
			if (!districtUuid.equals(selectedContact.getCaseDistrictUuid())) {
				districtUuid = null;
				break;
			}
		}

		DistrictReferenceDto district = FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(districtUuid);
			
		// Create a temporary contact in order to use the CommitDiscardWrapperComponent
		ContactDto tempContact = new ContactDto();

		BulkContactDataForm form = new BulkContactDataForm(district);
		form.setValue(tempContact);
		final CommitDiscardWrapperComponent<BulkContactDataForm> editView = new CommitDiscardWrapperComponent<BulkContactDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit contacts");

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				ContactDto updatedTempContact = form.getValue();
				for (ContactIndexDto indexDto : selectedContacts) {
					ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(indexDto.getUuid());
					if (form.getClassificationCheckBox().getValue() == true) {
						contactDto.setContactClassification(updatedTempContact.getContactClassification());
					}
					// Setting the contact officer is only allowed if all selected contacts are in the same district
					if (district != null && form.getContactOfficerCheckBox().getValue() == true) {
						contactDto.setContactOfficer(updatedTempContact.getContactOfficer());
					}

					FacadeProvider.getContactFacade().saveContact(contactDto);
				}
				popupWindow.close();
				if (caseUuid == null) {
					overview();
				} else {
					caseContactsOverview(caseUuid);
				}
				Notification.show("All contacts have been edited", Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
	}

	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No contacts selected", "You have not selected any contacts.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected contacts?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getContactFacade().deleteContact(new ContactReferenceDto(((ContactIndexDto) selectedRow).getUuid()), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification("Contacts deleted", "All selected contacts have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
	
	public void cancelFollowUpOfAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No contacts selected", "You have not selected any contacts.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to cancel the follow-up of all " + selectedRows.size() + " selected contacts?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						ContactIndexDto contact = (ContactIndexDto) selectedRow;
						if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
							ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
							contactDto.setFollowUpStatus(FollowUpStatus.CANCELED);
							contactDto.setFollowUpComment("Canceled by " + UserProvider.getCurrent().getUserName() + " using bulk action");
							FacadeProvider.getContactFacade().saveContact(contactDto);
						}
					}
					callback.run();
					new Notification("Follow-up canceled", "Follow-up of all selected contacts has been canceled.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}
	
	public void setAllSelectedItemsToLostToFollowUp(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No contacts selected", "You have not selected any contacts.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to set the follow-up of all " + selectedRows.size() + " selected contacts to lost to follow-up?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						ContactIndexDto contact = (ContactIndexDto) selectedRow;
						if (contact.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP) {
							ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
							contactDto.setFollowUpStatus(FollowUpStatus.LOST);
							contactDto.setFollowUpComment("Set to lost to follow-up by " + UserProvider.getCurrent().getUserName() + " using bulk action");
							FacadeProvider.getContactFacade().saveContact(contactDto);
						}
					}
					callback.run();
					new Notification("Follow-up status changed", "Follow-up of all selected contacts that have follow-up has been set to lost to follow-up.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
