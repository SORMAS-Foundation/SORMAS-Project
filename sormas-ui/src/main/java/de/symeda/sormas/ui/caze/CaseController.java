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
package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationForm;
import de.symeda.sormas.ui.hospitalization.CaseHospitalizationView;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class CaseController {

	public CaseController() {

	}

	public void registerViews(Navigator navigator) {
		navigator.addView(CasesView.VIEW_NAME, CasesView.class);
		navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
		navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
		navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
		}
		navigator.addView(CaseHospitalizationView.VIEW_NAME, CaseHospitalizationView.class);
		navigator.addView(EpiDataView.VIEW_NAME, EpiDataView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null, null, null);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");    	
	}

	public void create(PersonReferenceDto person, Disease disease, EventParticipantDto eventParticipant) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, null, eventParticipant);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case"); 
	}

	public void create(PersonReferenceDto person, Disease disease, ContactDto contact) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(person, disease, contact, null);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, "Create new case");
	}

	public void navigateToIndex() {
		String navigationState = CasesView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToCase(String caseUuid) {
		navigateToView(CaseDataView.VIEW_NAME, caseUuid, null, false);
	}

	public void navigateToCase(String caseUuid, boolean openTab) {
		navigateToView(CaseDataView.VIEW_NAME, caseUuid, null, openTab);
	}

	public void navigateToView(String viewName, String caseUuid, ViewMode viewMode) {
		navigateToView(viewName, caseUuid, viewMode, false);
	}
	
	public void navigateToView(String viewName, String caseUuid, ViewMode viewMode, boolean openTab) {

		String navigationState = viewName + "/" + caseUuid;
		if (viewMode == ViewMode.FULL) {
			// pass full view mode as param so it's also used for other views when switching
			navigationState	+= "/" + AbstractCaseView.VIEW_MODE_URL_PREFIX + "=" + viewMode.toString();
		}
		
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}		
	}

	public Link createLinkToData(String caseUuid, String caption) {
		Link link = new Link(caption, new ExternalResource("#!" + CaseDataView.VIEW_NAME + "/" + caseUuid));
		return link;
	}

	/**
	 * Update the fragment without causing navigator to change view
	 */
	public void setUriFragmentParameter(String caseUuid) {
		String fragmentParameter;
		if (caseUuid == null || caseUuid.isEmpty()) {
			fragmentParameter = "";
		} else {
			fragmentParameter = caseUuid;
		}

		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + CasesView.VIEW_NAME + "/"
				+ fragmentParameter, false);
	}

	private CaseDataDto findCase(String uuid) {
		return FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
	}

	private CaseDataDto createNewCase(PersonReferenceDto person, Disease disease) {
		CaseDataDto caze = CaseDataDto.build(person, disease);

		UserDto user = UserProvider.getCurrent().getUser();
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		caze.setReportingUser(userReference);
		caze.setRegion(user.getRegion());
		caze.setDistrict(user.getDistrict());

		return caze;
	}

	public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(PersonReferenceDto person, Disease disease, ContactDto contact, EventParticipantDto eventParticipant) {

		CaseCreateForm createForm = new CaseCreateForm(UserRight.CASE_CREATE);
		CaseDataDto caze = createNewCase(person, disease);
		createForm.setValue(caze);

		if (person != null) {
			createForm.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid()));
			createForm.setNameReadOnly(true);
		}
		if (contact != null) {
			createForm.setDiseaseReadOnly(true);
		}
		final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(createForm, createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final CaseDataDto dto = createForm.getValue();
					// Generate EPID number prefix
					Calendar calendar = Calendar.getInstance();
					String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2);
					RegionDto region = FacadeProvider.getRegionFacade().getRegionByUuid(dto.getRegion().getUuid());
					DistrictDto district = FacadeProvider.getDistrictFacade().getDistrictByUuid(dto.getDistrict().getUuid());
					dto.setEpidNumber(region.getEpidCode() != null ? region.getEpidCode() : "" 
							+ "-" + district.getEpidCode() != null ? district.getEpidCode() : "" 
									+ "-" + year + "-");

					if (contact != null) {
						// automatically change the contact status to "converted"
						contact.setContactStatus(ContactStatus.CONVERTED);
						FacadeProvider.getContactFacade().saveContact(contact);
					}

					if (contact != null || eventParticipant != null) {
						// use the person of the contact or event participant the case is created for
						dto.setPerson(person);
						CaseDataDto savedCase = FacadeProvider.getCaseFacade().saveCase(dto);
						if (eventParticipant != null) {
							// retrieve the event participant just in case it has been changed during case saving
							EventParticipantDto updatedEventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid());
							// set resulting case on event participant and save it
							updatedEventParticipant.setResultingCase(savedCase.toReference());
							FacadeProvider.getEventParticipantFacade().saveEventParticipant(updatedEventParticipant);
						}
						if (contact != null) {
							// retrieve the contact just in case it has been changed during case saving
							ContactDto updatedContact = FacadeProvider.getContactFacade().getContactByUuid(contact.getUuid());
							// set resulting case on contact and save it
							updatedContact.setResultingCase(savedCase.toReference());
							FacadeProvider.getContactFacade().saveContact(updatedContact);
						}
						Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
						navigateToView(CasePersonView.VIEW_NAME, dto.getUuid(), null);
					} else {
						ControllerProvider.getPersonController().selectOrCreatePerson(
								createForm.getPersonFirstName(), createForm.getPersonLastName(), 
								person -> {
									if (person != null) {
										dto.setPerson(person);
										FacadeProvider.getCaseFacade().saveCase(dto);
										Notification.show("New case created", Type.ASSISTIVE_NOTIFICATION);
										navigateToView(CasePersonView.VIEW_NAME, dto.getUuid(), null);
									}
								});
					}
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<? extends Component> getCaseCombinedEditComponent(final String caseUuid, final ViewMode viewMode) {

		CaseDataDto caze = findCase(caseUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());

		CaseDataForm caseEditForm = new CaseDataForm(person, caze.getDisease(), UserRight.CASE_EDIT, viewMode);
		caseEditForm.setValue(caze);

		CaseHospitalizationForm hospitalizationForm = new CaseHospitalizationForm(caze, UserRight.CASE_EDIT, viewMode);
		hospitalizationForm.setValue(caze.getHospitalization());

		SymptomsForm symptomsForm = new SymptomsForm(caze, caze.getDisease(), person, SymptomsContext.CASE, UserRight.CASE_EDIT, viewMode);
		symptomsForm.setValue(caze.getSymptoms());

		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease(), UserRight.CASE_EDIT, viewMode);
		epiDataForm.setValue(caze.getEpiData());

		CommitDiscardWrapperComponent<? extends Component> editView = AbstractEditForm.buildCommitDiscardWrapper(
				caseEditForm, hospitalizationForm, symptomsForm, epiDataForm);

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				CaseDataDto cazeDto = caseEditForm.getValue();   
				cazeDto.setHospitalization(hospitalizationForm.getValue());
				cazeDto.setSymptoms(symptomsForm.getValue());
				cazeDto.setEpiData(epiDataForm.getValue());

				saveCase(cazeDto, CaseDataView.VIEW_NAME, viewMode);
			}
		});

		appendSpecialCommands(new CaseReferenceDto(caseUuid), editView);

		return editView;
	}

	public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(final String caseUuid, final ViewMode viewMode) {
		CaseDataDto caze = findCase(caseUuid);
		CaseDataForm caseEditForm = new CaseDataForm(FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()), caze.getDisease(), UserRight.CASE_EDIT, viewMode);
		caseEditForm.setValue(caze);
		CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, caseEditForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				CaseDataDto cazeDto = caseEditForm.getValue();   
				saveCase(cazeDto, CaseDataView.VIEW_NAME, viewMode);
			}
		});

		appendSpecialCommands(caze.toReference(), editView);

		return editView;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void showBulkCaseDataEditComponent(Collection<Object> selectedRows) {
		if (selectedRows.size() == 0) {
			new Notification("No cases selected", "You have not selected any cases.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
			return;
		} 

		List<CaseIndexDto> selectedCases = new ArrayList(selectedRows);

		// Check if cases with multiple districts have been selected
		String districtUuid = selectedCases.get(0).getDistrictUuid();
		for (CaseIndexDto selectedCase : selectedCases) {
			if (!districtUuid.equals(selectedCase.getDistrictUuid())) {
				districtUuid = null;
				break;
			}
		}

		DistrictReferenceDto district = FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(districtUuid);

		// Create a temporary case in order to use the CommitDiscardWrapperComponent
		CaseDataDto tempCase = new CaseDataDto();

		BulkCaseDataForm form = new BulkCaseDataForm(district);
		form.setValue(tempCase);
		final CommitDiscardWrapperComponent<BulkCaseDataForm> editView = new CommitDiscardWrapperComponent<BulkCaseDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, "Edit cases");

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				CaseDataDto updatedTempCase = form.getValue();
				for (CaseIndexDto indexDto : selectedCases) {
					CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(indexDto.getUuid());
					if (form.getClassificationCheckBox().getValue() == true) {
						caseDto.setCaseClassification(updatedTempCase.getCaseClassification());
					}
					if (form.getInvestigationStatusCheckBox().getValue() == true) {
						caseDto.setInvestigationStatus(updatedTempCase.getInvestigationStatus());
					}
					if (form.getOutcomeCheckBox().getValue() == true) {
						caseDto.setOutcome(updatedTempCase.getOutcome());
					}
					// Setting the surveillance officer is only allowed if all selected cases are in the same district
					if (district!= null && form.getSurveillanceOfficerCheckBox().getValue() == true) {
						caseDto.setSurveillanceOfficer(updatedTempCase.getSurveillanceOfficer());
					}

					FacadeProvider.getCaseFacade().saveCase(caseDto);
				}
				popupWindow.close();
				navigateToIndex();
				Notification.show("All cases have been edited", Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(new DiscardListener() {
			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
	}

	private void appendSpecialCommands(CaseReferenceDto cazeRef, CommitDiscardWrapperComponent<? extends Component> editView) {
		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getCaseFacade().deleteCase(cazeRef, UserProvider.getCurrent().getUserReference().getUuid());
					UI.getCurrent().getNavigator().navigateTo(CasesView.VIEW_NAME);
				}
			}, I18nProperties.getString(Strings.caze));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_ARCHIVE)) {
			boolean archived = FacadeProvider.getCaseFacade().isArchived(cazeRef.getUuid());
			Button archiveCaseButton = new Button();
			archiveCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
			if (archived) {
				archiveCaseButton.setCaption(I18nProperties.getCaption("dearchive"));
			} else {
				archiveCaseButton.setCaption(I18nProperties.getCaption("archive"));
			}
			archiveCaseButton.addClickListener(e -> {
				editView.commit();
				archiveOrDearchiveCase(cazeRef.getUuid(), !archived);
			});

			editView.getButtonsPanel().addComponentAsFirst(archiveCaseButton);
			editView.getButtonsPanel().setComponentAlignment(archiveCaseButton, Alignment.BOTTOM_LEFT);
		}

		// Initialize 'Transfer case' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_TRANSFER)) {
			Button transferCaseButton = new Button();
			transferCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
			transferCaseButton.setCaption("Transfer case");
			transferCaseButton.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					editView.commit();
					CaseDataDto cazeDto = findCase(cazeRef.getUuid());
					transferCase(cazeDto);
				}
			});

			editView.getButtonsPanel().addComponentAsFirst(transferCaseButton);
			editView.getButtonsPanel().setComponentAlignment(transferCaseButton, Alignment.BOTTOM_LEFT);
		}
	}

	public CommitDiscardWrapperComponent<CaseHospitalizationForm> getCaseHospitalizationComponent(final String caseUuid, ViewMode viewMode) {
		CaseDataDto caze = findCase(caseUuid);
		CaseHospitalizationForm hospitalizationForm = new CaseHospitalizationForm(caze, UserRight.CASE_EDIT, viewMode);
		hospitalizationForm.setValue(caze.getHospitalization());

		final CommitDiscardWrapperComponent<CaseHospitalizationForm> editView = new CommitDiscardWrapperComponent<CaseHospitalizationForm>(hospitalizationForm, hospitalizationForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setHospitalization(hospitalizationForm.getValue());
				saveCase(cazeDto, CaseHospitalizationView.VIEW_NAME, viewMode);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<SymptomsForm> getCaseSymptomsEditComponent(final String caseUuid, ViewMode viewMode) {
		CaseDataDto caseDataDto = findCase(caseUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());

		SymptomsForm symptomsForm = new SymptomsForm(caseDataDto, caseDataDto.getDisease(), person, SymptomsContext.CASE, UserRight.CASE_EDIT, viewMode);
		symptomsForm.setValue(caseDataDto.getSymptoms());
		CommitDiscardWrapperComponent<SymptomsForm> editView = new CommitDiscardWrapperComponent<SymptomsForm>(symptomsForm, symptomsForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setSymptoms(symptomsForm.getValue());
				saveCase(cazeDto, CaseSymptomsView.VIEW_NAME, viewMode);
			}
		});

		return editView;
	}    

	private void saveCase(CaseDataDto cazeDto, String viewName, final ViewMode viewMode) {
		// Compare old and new case
		CaseDataDto existingDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(cazeDto.getUuid());
		onCaseChanged(existingDto, cazeDto);

		CaseDataDto resultDto = FacadeProvider.getCaseFacade().saveCase(cazeDto);

		if (resultDto.getPlagueType() != cazeDto.getPlagueType()) {
			// TODO would be much better to have a notification for this triggered in the backend
			Window window = VaadinUiUtil.showSimplePopupWindow("Save notification", 
					"The symptoms selected match the clinical criteria for " + resultDto.getPlagueType().toString() + ". "
							+ "The plague type is set to " + resultDto.getPlagueType().toString() + " for this case.");
			window.addCloseListener(new CloseListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void windowClose(CloseEvent e) {
					if (existingDto.getCaseClassification() != resultDto.getCaseClassification() &&
							resultDto.getClassificationUser() == null) {
						Notification notification = new Notification("Case saved. The classification was automatically changed to " + resultDto.getCaseClassification().toString() + ".", Type.WARNING_MESSAGE);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
						navigateToView(viewName, cazeDto.getUuid(), viewMode);
					} else {
						Notification.show("Case saved", Type.WARNING_MESSAGE);
						navigateToView(viewName, cazeDto.getUuid(), viewMode);
					}
				}
			});
		} else {
			// Notify user about an automatic case classification change
			if (existingDto.getCaseClassification() != resultDto.getCaseClassification() &&
					resultDto.getClassificationUser() == null) {
				Notification notification = new Notification("Case saved. The classification was automatically changed to " + resultDto.getCaseClassification().toString() + ".", Type.WARNING_MESSAGE);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
				navigateToView(viewName, cazeDto.getUuid(), viewMode);
			} else {
				Notification.show("Case saved", Type.WARNING_MESSAGE);
				navigateToView(viewName, cazeDto.getUuid(), viewMode);
			}
		}
	}

	private void onCaseChanged(CaseDataDto existingCase, CaseDataDto changedCase) {
		if (existingCase == null) {
			return;
		}

		// classification
		if (changedCase.getCaseClassification() != existingCase.getCaseClassification()) {
			changedCase.setClassificationDate(new Date());
			changedCase.setClassificationUser(UserProvider.getCurrent().getUserReference());
		}
	}

	public CommitDiscardWrapperComponent<EpiDataForm> getCaseEpiDataComponent(final String caseUuid, ViewMode viewMode) {
		CaseDataDto caze = findCase(caseUuid);
		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease(), UserRight.CASE_EDIT, viewMode);
		epiDataForm.setValue(caze.getEpiData());

		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(epiDataForm, epiDataForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setEpiData(epiDataForm.getValue());
				saveCase(cazeDto, EpiDataView.VIEW_NAME, viewMode);
			}
		});

		return editView;
	}

	public void transferCase(CaseDataDto caze) {
		CaseFacilityChangeForm facilityChangeForm = new CaseFacilityChangeForm(UserRight.CASE_TRANSFER);
		facilityChangeForm.setValue(caze);
		CommitDiscardWrapperComponent<CaseFacilityChangeForm> facilityChangeView = new CommitDiscardWrapperComponent<CaseFacilityChangeForm>(facilityChangeForm, facilityChangeForm.getFieldGroup());
		facilityChangeView.getCommitButton().setCaption("Transfer case");
		facilityChangeView.setMargin(true);

		Window popupWindow = VaadinUiUtil.showPopupWindow(facilityChangeView);
		popupWindow.setCaption("Transfer case");

		facilityChangeView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!facilityChangeForm.getFieldGroup().isModified()) {
					CaseDataDto dto = facilityChangeForm.getValue();
					FacadeProvider.getCaseFacade().saveAndTransferCase(dto);
					popupWindow.close();
					Notification.show("Case has been transfered to another health facility.", Type.WARNING_MESSAGE);
					navigateToView(CaseDataView.VIEW_NAME, caze.getUuid(), null);
				}
			}
		});

		Button cancelButton = new Button("cancel");
		cancelButton.setStyleName(ValoTheme.BUTTON_LINK);
		cancelButton.addClickListener(e -> {
			popupWindow.close();
		});
		facilityChangeView.getButtonsPanel().replaceComponent(facilityChangeView.getDiscardButton(), cancelButton);
	}

	private void archiveOrDearchiveCase(String caseUuid, boolean archive) {
		if (archive) {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.archivePrompt), I18nProperties.getString(Strings.caze).toLowerCase(), I18nProperties.getString(Strings.caze).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.archiveCase), contentLabel, I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
				if (e.booleanValue() == true) {
					FacadeProvider.getCaseFacade().archiveOrDearchiveCase(caseUuid, true);
					Notification.show(String.format(I18nProperties.getString(Strings.archiveNotification), I18nProperties.getString(Strings.caze)), Type.ASSISTIVE_NOTIFICATION);
					navigateToView(CaseDataView.VIEW_NAME, caseUuid, null);
				}
			});
		} else {
			Label contentLabel = new Label(String.format(I18nProperties.getString(Strings.dearchivePrompt), I18nProperties.getString(Strings.caze).toLowerCase(), I18nProperties.getString(Strings.caze).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.dearchiveCase), contentLabel, I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, e -> {
				if (e.booleanValue()) {
					FacadeProvider.getCaseFacade().archiveOrDearchiveCase(caseUuid, false);
					Notification.show(String.format(I18nProperties.getString(Strings.dearchiveNotification), I18nProperties.getString(Strings.caze)), Type.ASSISTIVE_NOTIFICATION);
					navigateToView(CaseDataView.VIEW_NAME, caseUuid, null);
				}
			});
		}
	}

	public void openClassificationRulesPopup(CaseDataDto caze) {
		VerticalLayout classificationRulesLayout = new VerticalLayout();
		classificationRulesLayout.setMargin(true);

		DiseaseClassificationCriteriaDto diseaseCriteria = FacadeProvider.getCaseClassificationFacade().getByDisease(caze.getDisease());
		if (diseaseCriteria != null) {
			Label suspectContent = new Label();
			suspectContent.setContentMode(ContentMode.HTML);
			suspectContent.setWidth(100, Unit.PERCENTAGE);
			suspectContent.setValue(ClassificationHtmlRenderer.createSuspectHtmlString(diseaseCriteria));
			classificationRulesLayout.addComponent(suspectContent);
	
			Label probableContent = new Label();
			probableContent.setContentMode(ContentMode.HTML);
			probableContent.setWidth(100, Unit.PERCENTAGE);
			probableContent.setValue(ClassificationHtmlRenderer.createProbableHtmlString(diseaseCriteria));
			classificationRulesLayout.addComponent(probableContent);
	
			Label confirmedContent = new Label();
			confirmedContent.setContentMode(ContentMode.HTML);
			confirmedContent.setWidth(100, Unit.PERCENTAGE);
			confirmedContent.setValue(ClassificationHtmlRenderer.createConfirmedHtmlString(diseaseCriteria));
			classificationRulesLayout.addComponent(confirmedContent);
		}

		Window popupWindow = VaadinUiUtil.showPopupWindow(classificationRulesLayout);
		popupWindow.addCloseListener(e -> {
			popupWindow.close();
		});
		popupWindow.setWidth(860, Unit.PIXELS);
		popupWindow.setHeight(80, Unit.PERCENTAGE);
		popupWindow.setCaption(I18nProperties.getString(Strings.classificationRulesFor) + " " + caze.getDisease().toString());
	}

	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No cases selected", "You have not selected any cases.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected cases?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getCaseFacade().deleteCase(new CaseReferenceDto(((CaseIndexDto) selectedRow).getUuid()), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification("Cases deleted", "All selected cases have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void archiveAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No cases selected", "You have not selected any cases.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup("Confirm archiving", new Label("Are you sure you want to archive all " + selectedRows.size() + " selected cases?"), "Yes", "No", null, e -> {
				if (e.booleanValue() == true) {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getCaseFacade().archiveOrDearchiveCase(((CaseIndexDto) selectedRow).getUuid(), true);
					}
					callback.run();
					new Notification("Cases archived", "All selected cases have been archived.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

	public void dearchiveAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No cases selected", "You have not selected any cases.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup("Confirm de-archiving", new Label("Are you sure you want to de-archive all " + selectedRows.size() + " selected cases?"), "Yes", "No", null, e -> {
				if (e.booleanValue() == true) {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getCaseFacade().archiveOrDearchiveCase(((CaseIndexDto) selectedRow).getUuid(), false);
					}
					callback.run();
					new Notification("Cases de-archived", "All selected cases have been de-archived.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
