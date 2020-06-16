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
package de.symeda.sormas.ui.caze;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.exporter.CaseExportConfigurationEditLayout;
import de.symeda.sormas.ui.caze.exporter.CaseExportConfigurationsGrid;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryForm;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryView;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoForm;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoView;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseForm;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseView;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.epidata.EpiDataView;
import de.symeda.sormas.ui.hospitalization.HospitalizationForm;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.therapy.TherapyView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DiscardListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;

public class CaseController {

	public CaseController() {

	}

	public void registerViews(Navigator navigator) {

		UserProvider userProvider = UserProvider.getCurrent();
		navigator.addView(CasesView.VIEW_NAME, CasesView.class);
		if (userProvider.hasUserRight(UserRight.CASE_MERGE)) {
			navigator.addView(MergeCasesView.VIEW_NAME, MergeCasesView.class);
		}
		navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
		navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
		navigator.addView(MaternalHistoryView.VIEW_NAME, MaternalHistoryView.class);
		if (userProvider.hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			navigator.addView(PortHealthInfoView.VIEW_NAME, PortHealthInfoView.class);
		}
		navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
		if (userProvider.hasUserRight(UserRight.CONTACT_VIEW)) {
			navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
		}
		navigator.addView(HospitalizationView.VIEW_NAME, HospitalizationView.class);
		navigator.addView(EpiDataView.VIEW_NAME, EpiDataView.class);
		if (userProvider.hasUserRight(UserRight.THERAPY_VIEW)) {
			navigator.addView(TherapyView.VIEW_NAME, TherapyView.class);
		}
		if (userProvider.hasUserRight(UserRight.CLINICAL_COURSE_VIEW)) {
			navigator.addView(ClinicalCourseView.VIEW_NAME, ClinicalCourseView.class);
		}
	}

	public void create() {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void createFromEventParticipant(EventParticipantDto eventParticipant) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, eventParticipant);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void createFromContact(ContactDto contact) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(contact, null);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void navigateToIndex() {
		String navigationState = CasesView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToMergeCasesView() {
		String navigationState = MergeCasesView.VIEW_NAME;
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
		if (viewMode == ViewMode.NORMAL) {
			// pass full view mode as param so it's also used for other views when switching
			navigationState += "?" + AbstractCaseView.VIEW_MODE_URL_PREFIX + "=" + viewMode.toString();
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
		page.setUriFragment("!" + CasesView.VIEW_NAME + "/" + fragmentParameter, false);
	}

	private CaseDataDto findCase(String uuid) {
		return FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
	}

	private void saveCase(CaseDataDto cazeDto) {

		// Compare old and new case
		CaseDataDto existingDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(cazeDto.getUuid());
		onCaseChanged(existingDto, cazeDto);

		CaseDataDto resultDto = FacadeProvider.getCaseFacade().saveCase(cazeDto);

		if (resultDto.getPlagueType() != cazeDto.getPlagueType()) {
			// TODO would be much better to have a notification for this triggered in the backend
			Window window = VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingSaveNotification),
				String.format(
					I18nProperties.getString(Strings.messagePlagueTypeChange),
					resultDto.getPlagueType().toString(),
					resultDto.getPlagueType().toString()));
			window.addCloseListener(new CloseListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {
					if (existingDto.getCaseClassification() != resultDto.getCaseClassification() && resultDto.getClassificationUser() == null) {
						Notification notification = new Notification(
							String.format(
								I18nProperties.getString(Strings.messageCaseSavedClassificationChanged),
								resultDto.getCaseClassification().toString()),
							Type.WARNING_MESSAGE);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
					} else {
						Notification.show(I18nProperties.getString(Strings.messageCaseSaved), Type.WARNING_MESSAGE);
					}
					SormasUI.refreshView();
				}
			});
		} else {
			// Notify user about an automatic case classification change
			if (existingDto != null
				&& existingDto.getCaseClassification() != resultDto.getCaseClassification()
				&& resultDto.getClassificationUser() == null) {
				Notification notification = new Notification(
					String.format(
						I18nProperties.getString(Strings.messageCaseSavedClassificationChanged),
						resultDto.getCaseClassification().toString()),
					Type.WARNING_MESSAGE);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			} else {
				Notification.show(I18nProperties.getString(Strings.messageCaseSaved), Type.WARNING_MESSAGE);
			}
			SormasUI.refreshView();
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

	public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(
		ContactDto convertedContact,
		EventParticipantDto convertedEventParticipant) {

		assert (convertedContact == null || convertedEventParticipant == null);

		CaseCreateForm createForm = new CaseCreateForm();

		CaseDataDto caze;
		PersonDto person;
		SymptomsDto symptoms;
		if (convertedContact != null) {
			VisitDto lastVisit = FacadeProvider.getVisitFacade().getLastVisitByContact(convertedContact.toReference());
			if (lastVisit != null) {
				symptoms = lastVisit.getSymptoms();
			} else {
				symptoms = null;
			}
			person = FacadeProvider.getPersonFacade().getPersonByUuid(convertedContact.getPerson().getUuid());
			caze = CaseDataDto.buildFromContact(convertedContact, lastVisit);
		} else if (convertedEventParticipant != null) {
			EventDto event = FacadeProvider.getEventFacade().getEventByUuid(convertedEventParticipant.getEvent().getUuid());
			symptoms = null;
			person = convertedEventParticipant.getPerson();
			caze = CaseDataDto.buildFromEventParticipant(convertedEventParticipant, event.getDisease());
		} else {
			symptoms = null;
			person = null;
			caze = CaseDataDto.build(null, null);
		}

		UserDto user = UserProvider.getCurrent().getUser();
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		caze.setReportingUser(userReference);

		if (UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			caze.setRegion(user.getRegion());
			caze.setDistrict(user.getDistrict());
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setDisease(Disease.UNDEFINED);
		} else if (user.getHealthFacility() != null) {
			FacilityDto healthFacility = FacadeProvider.getFacilityFacade().getByUuid(user.getHealthFacility().getUuid());
			caze.setRegion(healthFacility.getRegion());
			caze.setDistrict(healthFacility.getDistrict());
			caze.setCommunity(healthFacility.getCommunity());
			caze.setHealthFacility(healthFacility.toReference());
		} else {
			caze.setRegion(user.getRegion());
			caze.setDistrict(user.getDistrict());
			caze.setCommunity(user.getCommunity());
		}

		createForm.setValue(caze);
		createForm.setPerson(person);
		createForm.setSymptoms(symptoms);

		if (convertedContact != null || convertedEventParticipant != null) {
			createForm.setPersonalDetailsReadOnly(true);
			createForm.setDiseaseReadOnly(true);
		}

		final CommitDiscardWrapperComponent<CaseCreateForm> editView = new CommitDiscardWrapperComponent<CaseCreateForm>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final CaseDataDto dto = createForm.getValue();

					if (convertedContact != null || convertedEventParticipant != null) {
						saveConvertedCase(dto, convertedContact, convertedEventParticipant);
						Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
						navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
					} else {
						// look for potential duplicate
						selectOrCreate(dto, createForm.getPersonFirstName(), createForm.getPersonLastName(), uuid -> {
							if (uuid == null) {
								PersonDto person = PersonDto.build();
								person.setFirstName(createForm.getPersonFirstName());
								person.setLastName(createForm.getPersonLastName());
								person.setBirthdateDD(createForm.getBirthdateDD());
								person.setBirthdateMM(createForm.getBirthdateMM());
								person.setBirthdateYYYY(createForm.getBirthdateYYYY());
								person.setSex(createForm.getSex());
								person.setPresentCondition(createForm.getPresentCondition());
								person = FacadeProvider.getPersonFacade().savePerson(person);
								dto.setPerson(person.toReference());
								SymptomsDto symptoms = SymptomsDto.build();
								symptoms.setOnsetDate(createForm.getOnsetDate());
								dto.setSymptoms(symptoms);
								saveCase(dto);
								Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
								navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
							} else {
								navigateToView(CaseDataView.VIEW_NAME, uuid, null);
							}

						});

					}
				}
			}
		});

		return editView;
	}

	private void saveConvertedCase(CaseDataDto resultCase, ContactDto convertedContact, EventParticipantDto convertedEventParticipant) {

		saveCase(resultCase);

		if (convertedEventParticipant != null) {
			// retrieve the event participant just in case it has been changed during case
			// saving
			EventParticipantDto updatedEventParticipant =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(convertedEventParticipant.getUuid());
			// set resulting case on event participant and save it
			updatedEventParticipant.setResultingCase(resultCase.toReference());
			FacadeProvider.getEventParticipantFacade().saveEventParticipant(updatedEventParticipant);
		}
		if (convertedContact != null) {
			// retrieve the contact just in case it has been changed during case saving
			ContactDto updatedContact = FacadeProvider.getContactFacade().getContactByUuid(convertedContact.getUuid());
			// automatically change the contact status to "converted"
			updatedContact.setContactStatus(ContactStatus.CONVERTED);
			// set resulting case on contact and save it
			updatedContact.setResultingCase(resultCase.toReference());
			FacadeProvider.getContactFacade().saveContact(updatedContact);
			FacadeProvider.getCaseFacade().setSampleAssociations(updatedContact.toReference(), resultCase.toReference());
		}
	}

	public void selectOrCreate(CaseDataDto caseDto, String personFirstName, String personLastName, Consumer<String> selectedCaseUuidConsumer) {

		CaseCriteria caseCriteria = new CaseCriteria().disease(caseDto.getDisease()).region(caseDto.getRegion());
		CaseSimilarityCriteria criteria = new CaseSimilarityCriteria().firstName(personFirstName)
			.lastName(personLastName)
			.caseCriteria(caseCriteria)
			.reportDate(caseDto.getReportDate());

		List<CaseIndexDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(criteria);

		if (similarCases.size() > 0) {
			CasePickOrCreateField pickOrCreateField = new CasePickOrCreateField(similarCases);
			pickOrCreateField.setWidth(1280, Unit.PIXELS);

			final CommitDiscardWrapperComponent<CasePickOrCreateField> component = new CommitDiscardWrapperComponent<>(pickOrCreateField);
			component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			component.getCommitButton().setEnabled(false);
			component.addCommitListener(() -> {
				CaseIndexDto pickedCase = pickOrCreateField.getValue();
				if (pickedCase != null) {
					selectedCaseUuidConsumer.accept(pickedCase.getUuid());
				} else {
					selectedCaseUuidConsumer.accept(null);
				}
			});

			pickOrCreateField.setSelectionChangeCallback((commitAllowed) -> {
				component.getCommitButton().setEnabled(commitAllowed);
			});

			VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateCase));
		} else {
			selectedCaseUuidConsumer.accept(null);
		}
	}

//	public CommitDiscardWrapperComponent<? extends Component> getCaseCombinedEditComponent(final String caseUuid,
//			final ViewMode viewMode) {
//
//		CaseDataDto caze = findCase(caseUuid);
//		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
//
//		CaseDataForm caseEditForm = new CaseDataForm(person, caze.getDisease(), viewMode);
//		caseEditForm.setValue(caze);
//
//		HospitalizationForm hospitalizationForm = new HospitalizationForm(caze, viewMode);
//		hospitalizationForm.setValue(caze.getHospitalization());
//
//		SymptomsForm symptomsForm = new SymptomsForm(caze, caze.getDisease(), person, SymptomsContext.CASE, viewMode);
//		symptomsForm.setValue(caze.getSymptoms());
//
//		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease(), viewMode);
//		epiDataForm.setValue(caze.getEpiData());
//
//		CommitDiscardWrapperComponent<? extends Component> editView = AbstractEditForm
//				.buildCommitDiscardWrapper(caseEditForm, hospitalizationForm, symptomsForm, epiDataForm);
//
//		editView.addCommitListener(new CommitListener() {
//			@Override
//			public void onCommit() {
//				CaseDataDto cazeDto = caseEditForm.getValue();
//				cazeDto.setHospitalization(hospitalizationForm.getValue());
//				cazeDto.setSymptoms(symptomsForm.getValue());
//				cazeDto.setEpiData(epiDataForm.getValue());
//
//				saveCase(cazeDto);
//			}
//		});
//
//		appendSpecialCommands(caze, editView);
//
//		return editView;
//	}

	public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(
		final String caseUuid,
		final ViewMode viewMode,
		boolean isInJurisdiction) {
		CaseDataDto caze = findCase(caseUuid);
		CaseDataForm caseEditForm = new CaseDataForm(
			FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid()),
			caze.getDisease(),
			viewMode,
			isInJurisdiction);
		caseEditForm.setValue(caze);

		CommitDiscardWrapperComponent<CaseDataForm> editView = new CommitDiscardWrapperComponent<CaseDataForm>(
			caseEditForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT),
			caseEditForm.getFieldGroup());

		editView.addCommitListener(() -> {
			CaseDataDto oldCase = findCase(caseUuid);
			CaseDataDto cazeDto = caseEditForm.getValue();
			if (cazeDto.getHealthFacility() != null && !cazeDto.getHealthFacility().getUuid().equals(oldCase.getHealthFacility().getUuid())) {
				saveCaseWithHealthFacilityChangedPrompt(cazeDto, oldCase);
			} else {
				saveCase(cazeDto);
			}
		});

		appendSpecialCommands(caze, editView);

		return editView;
	}

	public void showBulkCaseDataEditComponent(Collection<? extends CaseIndexDto> selectedCases) {

		if (selectedCases.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if cases with multiple regions and districts have been selected
		String regionUuid = null, districtUuid = null;
		boolean first = true;
		for (CaseIndexDto selectedCase : selectedCases) {
			if (first) {
				regionUuid = selectedCase.getRegionUuid();
				districtUuid = selectedCase.getDistrictUuid();
				first = false;
			} else {
				if (!DataHelper.equal(regionUuid, selectedCase.getRegionUuid())) {
					regionUuid = null;
				}
				if (!DataHelper.equal(districtUuid, selectedCase.getDistrictUuid())) {
					districtUuid = null;
				}
			}
			if (regionUuid == null && districtUuid == null)
				break;
		}

		RegionReferenceDto region = regionUuid != null ? FacadeProvider.getRegionFacade().getRegionReferenceByUuid(regionUuid) : null;
		DistrictReferenceDto district = districtUuid != null ? FacadeProvider.getDistrictFacade().getDistrictReferenceByUuid(districtUuid) : null;

		// Create a temporary case in order to use the CommitDiscardWrapperComponent
		CaseBulkEditData bulkEditData = new CaseBulkEditData();
		bulkEditData.setRegion(region);
		bulkEditData.setDistrict(district);

		BulkCaseDataForm form = new BulkCaseDataForm(district);
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkCaseDataForm> editView =
			new CommitDiscardWrapperComponent<BulkCaseDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditCases));

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseBulkEditData updatedBulkEditData = form.getValue();

				boolean diseaseChange = form.getDiseaseCheckBox().getValue();
				boolean classificationChange = form.getClassificationCheckBox().getValue();
				boolean investigationStatusChange = form.getInvestigationStatusCheckBox().getValue();
				boolean outcomeChange = form.getOutcomeCheckBox().getValue();
				boolean surveillanceOfficerChange = district != null && form.getSurveillanceOfficerCheckBox().getValue();
				boolean healthFacilityChange = form.getHealthFacilityCheckbox().getValue();

				if (healthFacilityChange) {
					VaadinUiUtil.showChooseOptionPopup(
						I18nProperties.getCaption(Captions.caseInfrastructureDataChanged),
						new Label(I18nProperties.getString(Strings.messageHealthFacilityMulitChanged)),
						I18nProperties.getCaption(Captions.caseTransferCases),
						I18nProperties.getCaption(Captions.caseEditData),
						500,
						e -> {
							bulkEditWithHealthFacilities(
								selectedCases,
								updatedBulkEditData,
								diseaseChange,
								classificationChange,
								investigationStatusChange,
								outcomeChange,
								surveillanceOfficerChange,
								e.booleanValue());

							popupWindow.close();
							navigateToIndex();
							Notification.show(I18nProperties.getString(Strings.messageCasesEdited), Type.HUMANIZED_MESSAGE);
						});

				} else {
					CaseFacade caseFacade = FacadeProvider.getCaseFacade();
					bulkEdit(
						selectedCases,
						updatedBulkEditData,
						diseaseChange,
						classificationChange,
						investigationStatusChange,
						outcomeChange,
						surveillanceOfficerChange,
						caseFacade);

					popupWindow.close();
					navigateToIndex();
					Notification.show(I18nProperties.getString(Strings.messageCasesEdited), Type.HUMANIZED_MESSAGE);
				}
			}
		});

		editView.addDiscardListener(new DiscardListener() {

			@Override
			public void onDiscard() {
				popupWindow.close();
			}
		});
	}

	private void bulkEdit(
		Collection<? extends CaseIndexDto> selectedCases,
		CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange,
		CaseFacade caseFacade) {

		for (CaseIndexDto indexDto : selectedCases) {
			CaseDataDto caseDto = changeCaseDto(
				updatedCaseBulkEditData,
				caseFacade.getCaseDataByUuid(indexDto.getUuid()),
				diseaseChange,
				classificationChange,
				investigationStatusChange,
				outcomeChange,
				surveillanceOfficerChange);

			caseFacade.saveCase(caseDto);
		}
	}

	private void bulkEditWithHealthFacilities(
		Collection<? extends CaseIndexDto> selectedCases,
		CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange,
		Boolean doTransfer) {

		CaseFacade caseFacade = FacadeProvider.getCaseFacade();
		for (CaseIndexDto indexDto : selectedCases) {
			CaseDataDto updatedCase = changeCaseDto(
				updatedCaseBulkEditData,
				caseFacade.getCaseDataByUuid(indexDto.getUuid()),
				diseaseChange,
				classificationChange,
				investigationStatusChange,
				outcomeChange,
				surveillanceOfficerChange);
			updatedCase.setRegion(updatedCaseBulkEditData.getRegion());
			updatedCase.setDistrict(updatedCaseBulkEditData.getDistrict());
			updatedCase.setCommunity(updatedCaseBulkEditData.getCommunity());
			updatedCase.setHealthFacility(updatedCaseBulkEditData.getHealthFacility());
			if (doTransfer) {
				CaseDataDto oldCase = caseFacade.getCaseDataByUuid(indexDto.getUuid());
				CaseLogic.createPreviousHospitalizationAndUpdateHospitalization(updatedCase, oldCase);
			}
			caseFacade.saveCase(updatedCase);
		}
	}

	private CaseDataDto changeCaseDto(
		CaseBulkEditData updatedCaseBulkEditData,
		CaseDataDto caseDto,
		boolean diseaseChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange) {

		if (diseaseChange) {
			caseDto.setDisease(updatedCaseBulkEditData.getDisease());
			caseDto.setDiseaseDetails(updatedCaseBulkEditData.getDiseaseDetails());
			caseDto.setPlagueType(updatedCaseBulkEditData.getPlagueType());
			caseDto.setDengueFeverType(updatedCaseBulkEditData.getDengueFeverType());
			caseDto.setRabiesType(updatedCaseBulkEditData.getRabiesType());
		}
		if (classificationChange) {
			caseDto.setCaseClassification(updatedCaseBulkEditData.getCaseClassification());
		}
		if (investigationStatusChange) {
			caseDto.setInvestigationStatus(updatedCaseBulkEditData.getInvestigationStatus());
		}
		if (outcomeChange) {
			caseDto.setOutcome(updatedCaseBulkEditData.getOutcome());
		}
		// Setting the surveillance officer is only allowed if all selected cases are in
		// the same district
		if (surveillanceOfficerChange) {
			caseDto.setSurveillanceOfficer(updatedCaseBulkEditData.getSurveillanceOfficer());
		}

		return caseDto;
	}

	private void appendSpecialCommands(CaseDataDto caze, CommitDiscardWrapperComponent<? extends Component> editView) {

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getCaseFacade().deleteCase(caze.getUuid());
				UI.getCurrent().getNavigator().navigateTo(CasesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityCase));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_ARCHIVE)) {
			boolean archived = FacadeProvider.getCaseFacade().isArchived(caze.getUuid());
			Button archiveCaseButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editView.commit();
				archiveOrDearchiveCase(caze.getUuid(), !archived);
			}, ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveCaseButton);
			editView.getButtonsPanel().setComponentAlignment(archiveCaseButton, Alignment.BOTTOM_LEFT);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_REFER_FROM_POE) && caze.isUnreferredPortHealthCase()) {
			Button btnReferToFacility = ButtonHelper.createButton(Captions.caseReferToFacility, e -> {
				editView.commit();
				CaseDataDto caseDto = findCase(caze.getUuid());
				referFromPointOfEntry(caseDto);
			});

			editView.getButtonsPanel().addComponentAsFirst(btnReferToFacility);
			editView.getButtonsPanel().setComponentAlignment(btnReferToFacility, Alignment.BOTTOM_LEFT);
		}
	}

	public CommitDiscardWrapperComponent<HospitalizationForm> getHospitalizationComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caze = findCase(caseUuid);
		HospitalizationForm hospitalizationForm = new HospitalizationForm(caze, viewMode);
		hospitalizationForm.setValue(caze.getHospitalization());

		final CommitDiscardWrapperComponent<HospitalizationForm> editView = new CommitDiscardWrapperComponent<HospitalizationForm>(
			hospitalizationForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT),
			hospitalizationForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setHospitalization(hospitalizationForm.getValue());
				saveCase(cazeDto);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<MaternalHistoryForm> getMaternalHistoryComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caze = findCase(caseUuid);
		MaternalHistoryForm form = new MaternalHistoryForm(viewMode);
		form.setValue(caze.getMaternalHistory());

		final CommitDiscardWrapperComponent<MaternalHistoryForm> component = new CommitDiscardWrapperComponent<MaternalHistoryForm>(
			form,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT),
			form.getFieldGroup());
		component.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				caze.setMaternalHistory(form.getValue());
				saveCase(caze);
			}
		});

		return component;
	}

	public CommitDiscardWrapperComponent<PortHealthInfoForm> getPortHealthInfoComponent(final String caseUuid) {

		CaseDataDto caze = findCase(caseUuid);
		PointOfEntryDto pointOfEntry = FacadeProvider.getPointOfEntryFacade().getByUuid(caze.getPointOfEntry().getUuid());
		PortHealthInfoForm form = new PortHealthInfoForm(pointOfEntry, caze.getPointOfEntryDetails());
		form.setValue(caze.getPortHealthInfo());

		final CommitDiscardWrapperComponent<PortHealthInfoForm> component = new CommitDiscardWrapperComponent<PortHealthInfoForm>(
			form,
			UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_EDIT),
			form.getFieldGroup());
		component.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				caze.setPortHealthInfo(form.getValue());
				saveCase(caze);
			}
		});

		return component;
	}

	public CommitDiscardWrapperComponent<SymptomsForm> getSymptomsEditComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caseDataDto = findCase(caseUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());

		SymptomsForm symptomsForm = new SymptomsForm(caseDataDto, caseDataDto.getDisease(), person, SymptomsContext.CASE, viewMode);
		symptomsForm.setValue(caseDataDto.getSymptoms());

		CommitDiscardWrapperComponent<SymptomsForm> editView = new CommitDiscardWrapperComponent<SymptomsForm>(
			symptomsForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT),
			symptomsForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setSymptoms(symptomsForm.getValue());
				saveCase(cazeDto);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caze = findCase(caseUuid);

		EpiDataForm epiDataForm = new EpiDataForm(caze.getDisease(), viewMode);
		EpiDataDto dto = caze.getEpiData();

		//default values
		setDefaultValues(dto);

		epiDataForm.setValue(dto);
		final CommitDiscardWrapperComponent<EpiDataForm> editView = new CommitDiscardWrapperComponent<EpiDataForm>(
			epiDataForm,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT),
			epiDataForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setEpiData(epiDataForm.getValue());
				saveCase(cazeDto);
			}
		});

		return editView;
	}

	public void setDefaultValues(EpiDataDto epiDataDto) {
		if (epiDataDto == null) {
			return;
		}

		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(EpiDataDto.class, EntityDto.class).getPropertyDescriptors()) {
				if (pd.getWriteMethod() != null && (pd.getReadMethod().getReturnType().equals(YesNoUnknown.class))) {
					try {
						if (pd.getReadMethod().invoke(epiDataDto) == null)
							pd.getWriteMethod().invoke(epiDataDto, YesNoUnknown.NO);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public CommitDiscardWrapperComponent<ClinicalCourseForm> getClinicalCourseComponent(String caseUuid, ViewMode viewMode) {

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		ClinicalCourseForm form = new ClinicalCourseForm();
		form.setValue(caze.getClinicalCourse());

		final CommitDiscardWrapperComponent<ClinicalCourseForm> view =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasUserRight(UserRight.CLINICAL_COURSE_EDIT), form.getFieldGroup());
		view.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!form.getFieldGroup().isModified()) {
					CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
					cazeDto.setClinicalCourse(form.getValue());
					saveCase(cazeDto);
				}
			}
		});

		return view;
	}

	public void saveCaseWithHealthFacilityChangedPrompt(CaseDataDto caze, CaseDataDto oldCase) {

		VaadinUiUtil.showChooseOptionPopup(
			I18nProperties.getCaption(Captions.caseInfrastructureDataChanged),
			new Label(I18nProperties.getString(Strings.messageHealthFacilityChanged)),
			I18nProperties.getCaption(Captions.caseTransferCase),
			I18nProperties.getCaption(Captions.caseEditData),
			500,
			e -> {
				if (e.booleanValue() == true) {
					CaseLogic.createPreviousHospitalizationAndUpdateHospitalization(caze, oldCase);
					saveCase(caze);
				} else {
					saveCase(caze);
				}
			});
	}

	public void referFromPointOfEntry(CaseDataDto caze) {

		CaseFacilityChangeForm form = new CaseFacilityChangeForm();
		form.setValue(caze);
		CommitDiscardWrapperComponent<CaseFacilityChangeForm> view = new CommitDiscardWrapperComponent<CaseFacilityChangeForm>(
			form,
			UserProvider.getCurrent().hasUserRight(UserRight.CASE_REFER_FROM_POE),
			form.getFieldGroup());
		view.getCommitButton().setCaption(I18nProperties.getCaption(Captions.caseReferToFacility));

		Window window = VaadinUiUtil.showPopupWindow(view);
		window.setCaption(I18nProperties.getString(Strings.headingReferCaseFromPointOfEntry));

		view.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				CaseDataDto dto = form.getValue();
				dto.getHospitalization().setAdmissionDate(new Date());
				FacadeProvider.getCaseFacade().saveCase(dto);
				window.close();
				Notification.show(I18nProperties.getString(Strings.messageCaseReferredFromPoe), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.refreshView();
			}
		});

		Button btnCancel = ButtonHelper.createButton(Captions.actionCancel, e -> {
			window.close();
		});

		view.getButtonsPanel().replaceComponent(view.getDiscardButton(), btnCancel);
	}

	private void archiveOrDearchiveCase(String caseUuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveCase),
					I18nProperties.getString(Strings.entityCase).toLowerCase(),
					I18nProperties.getString(Strings.entityCase).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveCase),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e.booleanValue() == true) {
						FacadeProvider.getCaseFacade().archiveOrDearchiveCase(caseUuid, true);
						Notification.show(
							String.format(I18nProperties.getString(Strings.messageCaseArchived), I18nProperties.getString(Strings.entityCase)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToView(CaseDataView.VIEW_NAME, caseUuid, null);
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveCase),
					I18nProperties.getString(Strings.entityCase).toLowerCase(),
					I18nProperties.getString(Strings.entityCase).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveCase),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e.booleanValue()) {
						FacadeProvider.getCaseFacade().archiveOrDearchiveCase(caseUuid, false);
						Notification.show(
							String.format(I18nProperties.getString(Strings.messageCaseDearchived), I18nProperties.getString(Strings.entityCase)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToView(CaseDataView.VIEW_NAME, caseUuid, null);
					}
				});
		}
	}

	public void openClassificationRulesPopup(DiseaseClassificationCriteriaDto diseaseCriteria) {

		VerticalLayout classificationRulesLayout = new VerticalLayout();
		classificationRulesLayout.setMargin(true);

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

			Label notACaseContent = new Label();
			notACaseContent.setContentMode(ContentMode.HTML);
			notACaseContent.setWidth(100, Unit.PERCENTAGE);
			notACaseContent.setValue(ClassificationHtmlRenderer.createNotACaseHtmlString(diseaseCriteria));
			classificationRulesLayout.addComponent(notACaseContent);
		}

		Window popupWindow = VaadinUiUtil.showPopupWindow(classificationRulesLayout);
		popupWindow.addCloseListener(e -> {
			popupWindow.close();
		});
		popupWindow.setWidth(860, Unit.PIXELS);
		popupWindow.setHeight(80, Unit.PERCENTAGE);
		popupWindow.setCaption(I18nProperties.getString(Strings.classificationRulesFor) + " " + diseaseCriteria.getDisease().toString());
	}

	public void deleteAllSelectedItems(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteCases), selectedRows.size()), () -> {
					for (CaseIndexDto selectedRow : selectedRows) {
						FacadeProvider.getCaseFacade().deleteCase(selectedRow.getUuid());
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingCasesDeleted),
						I18nProperties.getString(Strings.messageCasesDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}

	public void archiveAllSelectedItems(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveCases), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						for (CaseIndexDto selectedRow : selectedRows) {
							FacadeProvider.getCaseFacade().archiveOrDearchiveCase(selectedRow.getUuid(), true);
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingCasesArchived),
							I18nProperties.getString(Strings.messageCasesArchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void dearchiveAllSelectedItems(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmDearchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveCases), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						for (CaseIndexDto selectedRow : selectedRows) {
							FacadeProvider.getCaseFacade().archiveOrDearchiveCase(selectedRow.getUuid(), false);
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingCasesDearchived),
							I18nProperties.getString(Strings.messageCasesDearchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void openEditExportConfigurationWindow(CaseExportConfigurationsGrid grid, ExportConfigurationDto config) {

		Window newExportWindow = VaadinUiUtil.createPopupWindow();
		CaseExportConfigurationEditLayout editLayout = new CaseExportConfigurationEditLayout(config, (exportConfiguration) -> {
			FacadeProvider.getExportFacade().saveExportConfiguration(exportConfiguration);
			newExportWindow.close();
			new Notification(null, I18nProperties.getString(Strings.messageExportConfigurationSaved), Type.WARNING_MESSAGE, false)
				.show(Page.getCurrent());
			grid.reload();
		}, () -> {
			newExportWindow.close();
			grid.reload();
		});
		newExportWindow.setWidth(1024, Unit.PIXELS);
		newExportWindow.setCaption(I18nProperties.getCaption(Captions.exportNewExportConfiguration));
		newExportWindow.setContent(editLayout);
		UI.getCurrent().addWindow(newExportWindow);
	}

	public void lineListing() {

		Window window = new Window(I18nProperties.getString(Strings.headingLineListing));

		LineListingLayout lineListingForm = new LineListingLayout(window);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_CHANGE_EPID_NUMBER)) {
			lineListingForm.setWidth(LineListingLayout.DEFAULT_WIDTH, Unit.PIXELS);
		} else {
			lineListingForm.setWidth(LineListingLayout.WITDH_WITHOUT_EPID_NUMBER, Unit.PIXELS);
		}
		window.setContent(lineListingForm);
		window.setModal(true);
		window.setPositionX((int) Math.max(0, (Page.getCurrent().getBrowserWindowWidth() - lineListingForm.getWidth())) / 2);
		window.setPositionY(70);
		window.setResizable(false);

		UI.getCurrent().addWindow(window);
	}
}
