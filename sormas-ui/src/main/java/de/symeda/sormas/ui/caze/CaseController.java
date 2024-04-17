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

package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseBulkEditData;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.caze.classification.ClassificationHtmlRenderer;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.components.linelisting.LineListingLayout;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryForm;
import de.symeda.sormas.ui.caze.maternalhistory.MaternalHistoryView;
import de.symeda.sormas.ui.caze.messaging.SmsComponent;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoForm;
import de.symeda.sormas.ui.caze.porthealthinfo.PortHealthInfoView;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseForm;
import de.symeda.sormas.ui.clinicalcourse.ClinicalCourseView;
import de.symeda.sormas.ui.epidata.CaseEpiDataView;
import de.symeda.sormas.ui.epidata.EpiDataForm;
import de.symeda.sormas.ui.externalsurveillanceservice.ExternalSurveillanceServiceGateway;
import de.symeda.sormas.ui.hospitalization.HospitalizationForm;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.symptoms.SymptomsForm;
import de.symeda.sormas.ui.therapy.TherapyView;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.linelisting.model.LineDto;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class CaseController {

	public CaseController() {

	}

	public void registerViews(Navigator navigator) {

		navigator.addView(CasesView.VIEW_NAME, CasesView.class);
		if (UiUtil.permitted(UserRight.CASE_MERGE)) {
			navigator.addView(MergeCasesView.VIEW_NAME, MergeCasesView.class);
		}
		navigator.addView(CaseDataView.VIEW_NAME, CaseDataView.class);
		navigator.addView(CasePersonView.VIEW_NAME, CasePersonView.class);
		navigator.addView(MaternalHistoryView.VIEW_NAME, MaternalHistoryView.class);
		if (UiUtil.permitted(UserRight.PORT_HEALTH_INFO_VIEW)) {
			navigator.addView(PortHealthInfoView.VIEW_NAME, PortHealthInfoView.class);
		}
		navigator.addView(CaseSymptomsView.VIEW_NAME, CaseSymptomsView.class);
		if (UiUtil.permitted(UserRight.CONTACT_VIEW)) {
			navigator.addView(CaseContactsView.VIEW_NAME, CaseContactsView.class);
		}
		navigator.addView(HospitalizationView.VIEW_NAME, HospitalizationView.class);
		navigator.addView(CaseEpiDataView.VIEW_NAME, CaseEpiDataView.class);
		if (UiUtil.permitted(UserRight.THERAPY_VIEW)) {
			navigator.addView(TherapyView.VIEW_NAME, TherapyView.class);
		}
		if (UiUtil.permitted(FeatureType.VIEW_TAB_CASES_CLINICAL_COURSE, UserRight.CLINICAL_COURSE_VIEW)) {
			navigator.addView(ClinicalCourseView.VIEW_NAME, ClinicalCourseView.class);
		}
		if (UiUtil.enabled(FeatureType.CASE_FOLLOWUP)) {
			navigator.addView(CaseVisitsView.VIEW_NAME, CaseVisitsView.class);
		}

		navigator.addView(CaseExternalDataView.VIEW_NAME, CaseExternalDataView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null, null, null, null, false);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void createFromEventParticipant(EventParticipantDto eventParticipant) {
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);

		if (event.getDisease() == null) {
			new Notification(
				I18nProperties.getString(Strings.headingCreateNewCaseIssue),
				I18nProperties.getString(Strings.messageEventParticipantToCaseWithoutEventDisease),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		CaseDataDto dto = CaseDataDto.build(PersonDto.build().toReference(), event.getDisease());
		dto.setRegion(eventParticipant.getRegion() != null ? eventParticipant.getRegion() : event.getEventLocation().getRegion());
		dto.setDistrict(eventParticipant.getDistrict() != null ? eventParticipant.getDistrict() : event.getEventLocation().getDistrict());
		dto.setCommunity(event.getEventLocation().getCommunity());
		dto.setReportingUser(UiUtil.getUserReference());

		selectOrCreateCase(dto, FacadeProvider.getPersonFacade().getByUuid(eventParticipant.getPerson().getUuid()), uuid -> {
			if (uuid == null) {
				CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
					getCaseCreateComponent(null, eventParticipant, null, null, null, false);
				caseCreateComponent.addCommitListener(() -> {
					EventParticipantDto updatedEventparticipant = FacadeProvider.getEventParticipantFacade().getByUuid(eventParticipant.getUuid());
					if (updatedEventparticipant.getResultingCase() != null) {
						String caseUuid = updatedEventparticipant.getResultingCase().getUuid();
						CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
						convertSamePersonContactsAndEventParticipants(caze, null);
					}
				});
				VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
			} else {
				CaseDataDto selectedCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
				EventParticipantDto updatedEventParticipant = FacadeProvider.getEventParticipantFacade().getByUuid(eventParticipant.getUuid());
				updatedEventParticipant.setResultingCase(selectedCase.toReference());
				FacadeProvider.getEventParticipantFacade().save(updatedEventParticipant);

				FacadeProvider.getCaseFacade().setSampleAssociations(updatedEventParticipant.toReference(), selectedCase.toReference());

				convertSamePersonContactsAndEventParticipants(selectedCase, null);

				navigateToView(CaseDataView.VIEW_NAME, selectedCase.getUuid(), null);
			}
		});
	}

	public void createFromEventParticipantDifferentDisease(EventParticipantDto eventParticipant, Disease disease) {
		if (disease == null) {
			new Notification(
				I18nProperties.getString(Strings.headingCreateNewCaseIssue),
				I18nProperties.getString(Strings.messageEventParticipantToCaseWithoutEventDisease),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
			getCaseCreateComponent(null, eventParticipant, null, null, disease, false);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void createFromContact(ContactDto contact) {
		PersonDto selectedPerson = FacadeProvider.getPersonFacade().getByUuid(contact.getPerson().getUuid());
		CaseDataDto dto = CaseDataDto.build(PersonDto.build().toReference(), contact.getDisease());

		dto.setDiseaseDetails(contact.getDiseaseDetails());
		dto.setRegion(contact.getRegion());
		dto.setDistrict(contact.getDistrict());
		dto.setReportDate(contact.getReportDateTime());
		dto.setCommunity(contact.getCommunity());
		dto.setReportingUser(UiUtil.getUserReference());

		selectOrCreateCase(dto, FacadeProvider.getPersonFacade().getByUuid(selectedPerson.getUuid()), uuid -> {
			if (uuid == null) {
				CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(contact, null, null, null, null, false);
				caseCreateComponent.addCommitListener(() -> {
					ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(contact.getUuid());
					if (contactDto.getResultingCase() != null) {
						String caseUuid = contactDto.getResultingCase().getUuid();
						CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
						convertSamePersonContactsAndEventParticipants(caze, null);
					}
				});
				caseCreateComponent.addDiscardListener(() -> SormasUI.refreshView());
				VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
			} else {
				CaseDataDto selectedCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
				selectedCase.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
				FacadeProvider.getCaseFacade().save(selectedCase);

				ContactDto updatedContact = FacadeProvider.getContactFacade().getByUuid(contact.getUuid());
				updatedContact.setContactStatus(ContactStatus.CONVERTED);
				updatedContact.setResultingCase(selectedCase.toReference());
				updatedContact.setResultingCaseUser(UiUtil.getUserReference());
				FacadeProvider.getContactFacade().save(updatedContact);

				FacadeProvider.getCaseFacade().setSampleAssociations(updatedContact.toReference(), selectedCase.toReference());

				convertSamePersonContactsAndEventParticipants(selectedCase, null);

				navigateToView(CaseDataView.VIEW_NAME, selectedCase.getUuid(), null);
			}
		});
	}

	public void createFromUnrelatedContact(ContactDto contact, Disease disease) {
		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(contact, null, null, null, disease, false);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void createFromTravelEntry(TravelEntryDto travelEntryDto) {
		PersonDto selectedPerson = FacadeProvider.getPersonFacade().getByUuid(travelEntryDto.getPerson().getUuid());
		CaseDataDto dto = CaseDataDto.buildFromTravelEntry(travelEntryDto, selectedPerson);

		dto.setReportingUser(UiUtil.getUserReference());

		selectOrCreateCase(dto, FacadeProvider.getPersonFacade().getByUuid(selectedPerson.getUuid()), uuid -> {
			if (uuid == null) {
				CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent =
					getCaseCreateComponent(null, null, travelEntryDto, null, null, false);
				VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
			} else {
				TravelEntryDto updatedTravelEntry = FacadeProvider.getTravelEntryFacade().getByUuid(travelEntryDto.getUuid());
				updatedTravelEntry.setResultingCase(FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid).toReference());
				FacadeProvider.getTravelEntryFacade().save(updatedTravelEntry);
				navigateToView(CaseDataView.VIEW_NAME, uuid, null);
			}
		});
	}

	public void createFromPersonReference(PersonReferenceDto personReference) {
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(personReference.getUuid());
		CaseDataDto dto = CaseDataDto.build(personReference, null);

		dto.setReportingUser(UiUtil.getUserReference());

		CommitDiscardWrapperComponent<CaseCreateForm> caseCreateComponent = getCaseCreateComponent(null, null, null, person, null, false);
		caseCreateComponent.getWrappedComponent().setSearchedPerson(person);
		VaadinUiUtil.showModalPopupWindow(caseCreateComponent, I18nProperties.getString(Strings.headingCreateNewCase));
	}

	public void convertSamePersonContactsAndEventParticipants(CaseDataDto caze, Runnable callback) {

		List<SimilarContactDto> matchingContacts;
		if (UiUtil.permitted(UserRight.CONTACT_EDIT)) {
			ContactSimilarityCriteria contactCriteria = new ContactSimilarityCriteria().withPerson(caze.getPerson())
				.withDisease(caze.getDisease())
				.withContactClassification(ContactClassification.CONFIRMED)
				.withExcludePseudonymized(true)
				.withNoResultingCase(true);
			matchingContacts = FacadeProvider.getContactFacade().getMatchingContacts(contactCriteria);
		} else {
			matchingContacts = Collections.emptyList();
		}

		List<SimilarEventParticipantDto> matchingEventParticipants;
		if (UiUtil.permitted(UserRight.EVENTPARTICIPANT_EDIT)) {
			EventParticipantCriteria eventParticipantCriteria = new EventParticipantCriteria().withPerson(caze.getPerson())
				.withDisease(caze.getDisease())
				.withExcludePseudonymized(true)
				.withNoResultingCase(true);

			matchingEventParticipants = FacadeProvider.getEventParticipantFacade().getMatchingEventParticipants(eventParticipantCriteria);
		} else {
			matchingEventParticipants = Collections.emptyList();
		}

		if (matchingContacts.size() > 0 || matchingEventParticipants.size() > 0) {
			String infoText = matchingEventParticipants.isEmpty()
				? String.format(I18nProperties.getString(Strings.infoConvertToCaseContacts), matchingContacts.size(), caze.getDisease())
				: (matchingContacts.isEmpty()
					? String.format(
						I18nProperties.getString(Strings.infoConvertToCaseEventParticipants),
						matchingEventParticipants.size(),
						caze.getDisease())
					: String.format(
						I18nProperties.getString(Strings.infoConvertToCaseContactsAndEventParticipants),
						matchingContacts.size(),
						caze.getDisease(),
						matchingEventParticipants.size(),
						caze.getDisease()));

			HorizontalLayout infoComponent = VaadinUiUtil.createInfoComponent(infoText);
			infoComponent.setWidth(600, Sizeable.Unit.PIXELS);
			CommitDiscardWrapperComponent<HorizontalLayout> convertToCaseConfirmComponent = new CommitDiscardWrapperComponent<>(infoComponent);
			convertToCaseConfirmComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionYesForAll));
			convertToCaseConfirmComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionNo));

			convertToCaseConfirmComponent.addCommitListener(() -> {
				CaseDataDto refreshedCaze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
				refreshedCaze.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
				saveCase(refreshedCaze);
				setResultingCase(refreshedCaze, matchingContacts, matchingEventParticipants);
				SormasUI.refreshView();

				if (callback != null) {
					callback.run();
				}
			});
			convertToCaseConfirmComponent.addDiscardListener(() -> {
				if (callback != null) {
					callback.run();
				}
			});

			Button convertSomeButton =
				ButtonHelper.createButton("convertSome", I18nProperties.getCaption(Captions.actionYesForSome), (Button.ClickListener) event -> {
					convertToCaseConfirmComponent.discard();
					showConvertToCaseSelection(caze, matchingContacts, matchingEventParticipants, callback);
				}, ValoTheme.BUTTON_PRIMARY);

			HorizontalLayout buttonsPanel = convertToCaseConfirmComponent.getButtonsPanel();
			buttonsPanel.addComponent(convertSomeButton, convertToCaseConfirmComponent.getComponentCount() - 1);
			buttonsPanel.setComponentAlignment(convertSomeButton, Alignment.BOTTOM_RIGHT);
			buttonsPanel.setExpandRatio(convertSomeButton, 0);

			VaadinUiUtil.showModalPopupWindow(convertToCaseConfirmComponent, I18nProperties.getString(Strings.headingCaseConversion), true);
		} else {
			if (callback != null) {
				callback.run();
			}
		}
	}

	private void showConvertToCaseSelection(
		CaseDataDto caze,
		List<SimilarContactDto> matchingContacts,
		List<SimilarEventParticipantDto> matchingEventParticipants,
		Runnable callback) {

		ConvertToCaseSelectionField convertToCaseSelectionField = new ConvertToCaseSelectionField(caze, matchingContacts, matchingEventParticipants);
		convertToCaseSelectionField.setWidth(1280, Sizeable.Unit.PIXELS);

		CommitDiscardWrapperComponent<ConvertToCaseSelectionField> convertToCaseSelectComponent =
			new CommitDiscardWrapperComponent<>(convertToCaseSelectionField);
		convertToCaseSelectComponent.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
		convertToCaseSelectComponent.getDiscardButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));

		convertToCaseSelectComponent.addCommitListener(() -> {
			List<SimilarContactDto> selectedContacts = convertToCaseSelectionField.getSelectedContacts();
			if (!selectedContacts.isEmpty()) {
				CaseDataDto refreshedCaze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caze.getUuid());
				refreshedCaze.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
				saveCase(refreshedCaze);
			}

			setResultingCase(caze, selectedContacts, convertToCaseSelectionField.getSelectedEventParticipants());
			SormasUI.refreshView();
		});

		convertToCaseSelectComponent.addDoneListener(() -> {
			if (callback != null) {
				callback.run();
			}
		});

		VaadinUiUtil.showModalPopupWindow(convertToCaseSelectComponent, I18nProperties.getString(Strings.headingCaseConversion), true);
	}

	private void setResultingCase(
		CaseDataDto caze,
		List<SimilarContactDto> matchingContacts,
		List<SimilarEventParticipantDto> matchingEventParticipants) {

		if (matchingContacts != null && !matchingContacts.isEmpty()) {
			List<String> contactUuids = matchingContacts.stream().map(SimilarContactDto::getUuid).collect(Collectors.toList());
			List<ContactDto> contacts = FacadeProvider.getContactFacade().getByUuids(contactUuids);
			for (ContactDto contact : contacts) {
				contact.setContactStatus(ContactStatus.CONVERTED);
				contact.setResultingCase(caze.toReference());
				contact.setResultingCaseUser(UiUtil.getUserReference());
				FacadeProvider.getContactFacade().save(contact);
			}
		}

		if (matchingEventParticipants != null && !matchingEventParticipants.isEmpty()) {
			List<String> eventParticipantUuids =
				matchingEventParticipants.stream().map(SimilarEventParticipantDto::getUuid).collect(Collectors.toList());
			List<EventParticipantDto> eventParticipants = FacadeProvider.getEventParticipantFacade().getByUuids(eventParticipantUuids);
			for (EventParticipantDto eventParticipant : eventParticipants) {
				eventParticipant.setResultingCase(caze.toReference());
				FacadeProvider.getEventParticipantFacade().save(eventParticipant);
			}
		}

	}

	public void navigateToIndex() {
		String navigationState = CasesView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateTo(CaseCriteria caseCriteria) {
		ViewModelProviders.of(CasesView.class).remove(CaseCriteria.class);
		String navigationState = AbstractView.buildNavigationState(CasesView.VIEW_NAME, caseCriteria);
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToMergeCasesView() {
		String navigationState = MergeCasesView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToMergeCasesView(CaseCriteria criteria) {
		ViewModelProvider viewModelProvider = ViewModelProviders.of(MergeCasesView.class);

		// update the current criteria
		viewModelProvider.remove(CaseCriteria.class);
		viewModelProvider.get(CaseCriteria.class, criteria);

		// force the grid to load as it is filtered, so it should not take too long to load
		viewModelProvider.remove(MergeCasesViewConfiguration.class);
		viewModelProvider.get(MergeCasesViewConfiguration.class, new MergeCasesViewConfiguration(true));

		String navigationState = AbstractView.buildNavigationState(MergeCasesView.VIEW_NAME, criteria);
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
			navigationState += "?" + AbstractCaseView.VIEW_MODE_URL_PREFIX + "=" + viewMode;
		}

		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

	public Link createLinkToData(String caseUuid, String caption) {
		return new Link(caption, new ExternalResource("#!" + CaseDataView.VIEW_NAME + "/" + caseUuid));
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

	protected CaseDataDto saveCase(CaseDataDto cazeDto) {

		if (cazeDto.getReInfection() == YesNoUnknown.NO || cazeDto.getReInfection() == YesNoUnknown.UNKNOWN) {
			cazeDto.setPreviousInfectionDate(null);
			cazeDto.setReinfectionDetails(Collections.emptyMap());
			cazeDto.setReinfectionStatus(null);
		}

		// Compare old and new case
		CaseDataDto existingDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(cazeDto.getUuid());
		CaseDataDto resultDto = FacadeProvider.getCaseFacade().save(cazeDto);

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
		return resultDto;
	}

	public CommitDiscardWrapperComponent<CaseCreateForm> getCaseCreateComponent(
		ContactDto convertedContact,
		EventParticipantDto convertedEventParticipant,
		TravelEntryDto convertedTravelEntry,
		PersonDto convertedPerson,
		Disease unrelatedDisease,
		boolean createdFromLabMessage) {

		assert ((convertedContact == null && convertedEventParticipant == null)
			|| (convertedContact == null && convertedTravelEntry == null)
			|| (convertedEventParticipant == null && convertedTravelEntry == null));
		assert (unrelatedDisease == null || (convertedEventParticipant == null && convertedTravelEntry == null));

		CaseCreateForm createForm;
		if (createdFromLabMessage) {
			createForm = new CaseCreateForm(true, false, null);
		} else {
			createForm = convertedContact == null && convertedEventParticipant == null && convertedTravelEntry == null && unrelatedDisease == null
				? new CaseCreateForm()
				: new CaseCreateForm(convertedTravelEntry);
		}

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
			person = FacadeProvider.getPersonFacade().getByUuid(convertedContact.getPerson().getUuid());
			if (unrelatedDisease == null) {
				caze = CaseDataDto.buildFromContact(convertedContact);
				caze.getEpiData().setContactWithSourceCaseKnown(YesNoUnknown.YES);
			} else {
				caze = CaseDataDto.buildFromUnrelatedContact(convertedContact, unrelatedDisease);
			}
		} else if (convertedEventParticipant != null) {
			EventDto event = FacadeProvider.getEventFacade().getEventByUuid(convertedEventParticipant.getEvent().getUuid(), false);
			symptoms = null;
			person = FacadeProvider.getPersonFacade().getByUuid(convertedEventParticipant.getPerson().getUuid());
			if (unrelatedDisease == null) {
				caze = CaseDataDto.buildFromEventParticipant(convertedEventParticipant, person, event.getDisease());
			} else {
				caze = CaseDataDto.buildFromEventParticipant(convertedEventParticipant, person, unrelatedDisease);
			}
		} else if (convertedTravelEntry != null) {
			symptoms = null;
			person = FacadeProvider.getPersonFacade().getByUuid(convertedTravelEntry.getPerson().getUuid());
			caze = CaseDataDto.buildFromTravelEntry(convertedTravelEntry, person);
		} else {
			symptoms = null;
			person = convertedPerson;
			PersonReferenceDto personreference = person == null ? null : person.toReference();
			caze = CaseDataDto.build(personreference, null);
		}

		UserDto user = UiUtil.getUser();
		UserReferenceDto userReference = UiUtil.getUserReference();
		caze.setReportingUser(userReference);

		if (UiUtil.isPortHealthUser()) {
			caze.setResponsibleRegion(user.getRegion());
			caze.setResponsibleDistrict(user.getDistrict());
			caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
			caze.setDisease(Disease.UNDEFINED);
		} else if (user.getHealthFacility() != null) {
			FacilityDto healthFacility = FacadeProvider.getFacilityFacade().getByUuid(user.getHealthFacility().getUuid());
			caze.setResponsibleRegion(healthFacility.getRegion());
			caze.setResponsibleDistrict(healthFacility.getDistrict());
			caze.setResponsibleCommunity(healthFacility.getCommunity());
			caze.setHealthFacility(healthFacility.toReference());
		} else if (convertedTravelEntry == null) {
			caze.setResponsibleRegion(user.getRegion());
			caze.setResponsibleDistrict(user.getDistrict());
			caze.setResponsibleCommunity(user.getCommunity());
		}

		createForm.setValue(caze);
		createForm.setSymptoms(symptoms);

		if (convertedContact != null || convertedEventParticipant != null || convertedTravelEntry != null) {
			createForm.setPersonalDetailsReadOnlyIfNotEmpty(true);
			createForm.setDiseaseReadOnly(true);
		}

		if (convertedPerson != null) {
			createForm.setPersonalDetailsReadOnlyIfNotEmpty(true);
		}

		final CommitDiscardWrapperComponent<CaseCreateForm> editView =
			new CommitDiscardWrapperComponent<CaseCreateForm>(createForm, UiUtil.permitted(UserRight.CASE_CREATE), createForm.getFieldGroup());
		if (createForm.getHomeAddressForm() != null) {
			editView.addFieldGroups(createForm.getHomeAddressForm().getFieldGroup());
		}

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				final CaseDataDto dto = createForm.getValue();

				if (dto.getHealthFacility() == null || FacilityDto.NONE_FACILITY_UUID.equals(dto.getHealthFacility().getUuid())) {
					dto.setFacilityType(null);
				}

				if (convertedContact != null) {
					int incubationPeriod = FacadeProvider.getDiseaseConfigurationFacade().getCaseFollowUpDuration(dto.getDisease());
					List<VisitDto> visits = FacadeProvider.getVisitFacade()
						.getVisitsByContactAndPeriod(
							convertedContact.toReference(),
							DateHelper.subtractDays(dto.getReportDate(), incubationPeriod),
							DateHelper.addDays(dto.getReportDate(), incubationPeriod));
					for (VisitDto visit : visits) {
						SymptomsHelper.updateSymptoms(visit.getSymptoms(), dto.getSymptoms());
					}

					dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
					dto.setWasInQuarantineBeforeIsolation(YesNoUnknown.YES);

					transferDataToPerson(createForm, person);
					FacadeProvider.getPersonFacade().save(person);

					saveCase(dto);

					if (convertedContact.getDisease().equals(dto.getDisease())) {
						// retrieve the contact just in case it has been changed during case saving
						ContactDto updatedContact = FacadeProvider.getContactFacade().getByUuid(convertedContact.getUuid());
						// automatically change the contact status to "converted"
						updatedContact.setContactStatus(ContactStatus.CONVERTED);
						// automatically change the contact classification to "confirmed"
						updatedContact.setContactClassification(ContactClassification.CONFIRMED);
						// set resulting case on contact and save it
						if (updatedContact.getResultingCase() == null && updatedContact.getDisease() == dto.getDisease()) {
							updatedContact.setResultingCase(dto.toReference());
						}
						FacadeProvider.getContactFacade().save(updatedContact);
						FacadeProvider.getCaseFacade().updateFollowUpComment(dto);
					}
					FacadeProvider.getCaseFacade().setSampleAssociations(convertedContact.toReference(), dto.toReference());
					Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
					if (!createdFromLabMessage) {
						navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
					}
				} else if (convertedEventParticipant != null) {
					transferDataToPerson(createForm, person);
					FacadeProvider.getPersonFacade().save(person);
					selectOrCreateCase(dto, person, uuid -> {
						if (uuid == null) {
							dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
							saveCase(dto);
							// retrieve the event participant just in case it has been changed during case saving
							EventParticipantDto updatedEventParticipant =
								FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(convertedEventParticipant.getUuid());
							if (unrelatedDisease == null) {
								// set resulting case on event participant and save it
								FacadeProvider.getCaseFacade().setResultingCase(updatedEventParticipant.toReference(), dto.toReference());
								FacadeProvider.getCaseFacade().setSampleAssociations(updatedEventParticipant.toReference(), dto.toReference());
							} else {
								FacadeProvider.getCaseFacade()
									.setSampleAssociationsUnrelatedDisease(updatedEventParticipant.toReference(), dto.toReference());
							}
							if (!createdFromLabMessage) {
								navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
							}
						} else {
							if (unrelatedDisease == null && convertedEventParticipant.getResultingCase() == null) {
								convertedEventParticipant.setResultingCase(FacadeProvider.getCaseFacade().getReferenceByUuid(uuid));
							}
							FacadeProvider.getEventParticipantFacade().save(convertedEventParticipant);
							if (!createdFromLabMessage) {
								navigateToView(CaseDataView.VIEW_NAME, uuid, null);
							}
						}
					});
				} else if (convertedTravelEntry != null) {
					transferDataToPerson(createForm, person);
					FacadeProvider.getPersonFacade().save(person);

					dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
					saveCase(dto);

					// retrieve the travel entry just in case it has been changed during case saving
					TravelEntryDto updatedTravelEntry = FacadeProvider.getTravelEntryFacade().getByUuid(convertedTravelEntry.getUuid());
					// set resulting case on travel entry and save it
					updatedTravelEntry.setResultingCase(dto.toReference());
					FacadeProvider.getTravelEntryFacade().save(updatedTravelEntry);
					Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
					if (!createdFromLabMessage) {
						navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
					}
				} else if (createdFromLabMessage) {
					PersonDto dbPerson = FacadeProvider.getPersonFacade().getByUuid(dto.getPerson().getUuid());
					if (dbPerson == null) {
						PersonDto personDto = PersonDto.build();
						transferDataToPerson(createForm, personDto);
						FacadeProvider.getPersonFacade().save(personDto);
						dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
						dto.setPerson(personDto.toReference());
						saveCase(dto);
					} else {
						transferDataToPerson(createForm, dbPerson);
						FacadeProvider.getPersonFacade().save(dbPerson);
						dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
						saveCase(dto);
					}
				} else {
					PersonDto searchedPerson = createForm.getSearchedPerson();
					if (searchedPerson != null) {
						updateHomeAddress(createForm, searchedPerson);
						dto.setPerson(searchedPerson.toReference());
						selectOrCreateCase(createForm, dto, searchedPerson.toReference());
					} else {
						// look for potential duplicate
						final PersonDto duplicatePerson = PersonDto.build();
						transferDataToPerson(createForm, duplicatePerson);
						ControllerProvider.getPersonController()
							.selectOrCreatePerson(
								duplicatePerson,
								I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase),
								selectedPerson -> {
									if (selectedPerson != null) {
										dto.setPerson(selectedPerson);
										selectOrCreateCase(createForm, dto, selectedPerson);
									}
								},
								true);
					}
				}
			}
		});

		return editView;

	}

	private void selectOrCreateCase(CaseCreateForm createForm, CaseDataDto dto, PersonReferenceDto selectedPerson) {
		selectOrCreateCase(dto, FacadeProvider.getPersonFacade().getByUuid(selectedPerson.getUuid()), uuid -> {
			if (uuid == null) {
				dto.getSymptoms().setOnsetDate(createForm.getOnsetDate());
				saveCase(dto);
				navigateToView(CaseDataView.VIEW_NAME, dto.getUuid(), null);
			} else {
				navigateToView(CaseDataView.VIEW_NAME, uuid, null);
			}
		});
	}

	private void transferDataToPerson(CaseCreateForm createForm, PersonDto person) {
		createForm.getPersonCreateForm().transferDataToPerson(person);
	}

	private void updateHomeAddress(CaseCreateForm createForm, PersonDto person) {
		createForm.getPersonCreateForm().updateHomeAddress(person);
		FacadeProvider.getPersonFacade().save(person);
	}

	public void selectOrCreateCase(CaseDataDto caseDto, PersonDto person, Consumer<String> selectedCaseUuidConsumer) {
		CaseSimilarityCriteria criteria = CaseSimilarityCriteria.forCase(caseDto, person.getUuid());

		// Check for similar cases for the **given person**.
		// This is a case similarity check for a fixed person and will not return cases where persons are similar.
		List<CaseSelectionDto> similarCases = FacadeProvider.getCaseFacade().getSimilarCases(criteria);

		if (similarCases.size() > 0) {
			CasePickOrCreateField pickOrCreateField = new CasePickOrCreateField(caseDto, person, similarCases);
			pickOrCreateField.setWidth(1280, Unit.PIXELS);

			final CommitDiscardWrapperComponent<CasePickOrCreateField> component = new CommitDiscardWrapperComponent<>(pickOrCreateField);
			component.getCommitButton().setCaption(I18nProperties.getCaption(Captions.actionConfirm));
			component.getCommitButton().setEnabled(false);
			component.addCommitListener(() -> {
				CaseSelectionDto pickedCase = pickOrCreateField.getValue();
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

	public CommitDiscardWrapperComponent<CaseDataForm> getCaseDataEditComponent(final String caseUuid, final ViewMode viewMode) {
		CaseDataDto caze = findCase(caseUuid);
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getCaseFacade().getAutomaticDeletionInfo(caseUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getCaseFacade().getManuallyDeletionInfo(caseUuid);

		CaseDataForm caseEditForm = new CaseDataForm(
			caseUuid,
			FacadeProvider.getPersonFacade().getByUuid(caze.getPerson().getUuid()),
			caze.getDisease(),
			caze.getSymptoms(),
			viewMode,
			caze.isPseudonymized(),
			caze.isInJurisdiction());
		caseEditForm.setValue(caze);

		CommitDiscardWrapperComponent<CaseDataForm> editView =
			new CommitDiscardWrapperComponent<CaseDataForm>(caseEditForm, true, caseEditForm.getFieldGroup());

		editView.getButtonsPanel()
			.addComponentAsFirst(new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, caze.isDeleted(), CaseDataDto.I18N_PREFIX));

		if (caze.isDeleted()) {
			editView.getWrappedComponent().getField(CaseDataDto.DELETION_REASON).setVisible(true);
			if (editView.getWrappedComponent().getField(CaseDataDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editView.getWrappedComponent().getField(CaseDataDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		editView.addCommitListener(() -> {
			CaseDataDto oldCase = findCase(caseUuid);
			CaseDataDto cazeDto = caseEditForm.getValue();
			saveCaseWithFacilityChangedPrompt(cazeDto, oldCase);
		});

		editView.addDiscardListener(() -> caseEditForm.onDiscard());

		if (UiUtil.permitted(UserRight.CASE_REFER_FROM_POE) && caze.checkIsUnreferredPortHealthCase()) {

			Button.ClickListener clickListener = clickEvent -> {
				editView.commit();
				CaseDataDto caseDto = findCase(caze.getUuid());
				referFromPointOfEntry(caseDto);
			};

			editView.getWrappedComponent().addButtonListener(CaseDataForm.CASE_REFER_POINT_OF_ENTRY_BTN_LOC, clickListener);
		}

		if (UiUtil.getUserRoles().stream().anyMatch(userRoleDto -> !userRoleDto.isRestrictAccessToAssignedEntities())
			|| DataHelper.isSame(caze.getSurveillanceOfficer(), UiUtil.getUserReference())) {
			appendSpecialCommands(caze, editView);
		}

		editView.restrictEditableComponentsOnEditView(
			UserRight.CASE_EDIT,
			null,
			UserRight.CASE_DELETE,
			UserRight.CASE_ARCHIVE,
			FacadeProvider.getCaseFacade().getEditPermissionType(caze.getUuid()),
			caze.isInJurisdiction());

		return editView;
	}

	public <T extends CaseIndexDto> void showBulkCaseDataEditComponent(Collection<T> selectedCases, AbstractCaseGrid<?> caseGrid) {

		if (selectedCases.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if cases with multiple regions and districts have been selected
		String regionUuid = null;
		String districtUuid = null;
		boolean first = true;
		for (CaseIndexDto selectedCase : selectedCases) {
			String currentRegionUuid =
				selectedCase.getResponsibleRegionUuid() == null ? selectedCase.getRegionUuid() : selectedCase.getResponsibleRegionUuid();
			String currentDistrictUuid =
				selectedCase.getResponsibleDistrictUuid() == null ? selectedCase.getDistrictUuid() : selectedCase.getResponsibleDistrictUuid();

			if (first) {
				regionUuid = currentRegionUuid;
				districtUuid = currentDistrictUuid;
				first = false;
			} else {
				if (!DataHelper.equal(regionUuid, currentRegionUuid)) {
					regionUuid = null;
				}
				if (!DataHelper.equal(districtUuid, currentDistrictUuid)) {
					districtUuid = null;
				}
			}
			if (regionUuid == null && districtUuid == null)
				break;
		}

		RegionReferenceDto region = regionUuid != null ? FacadeProvider.getRegionFacade().getReferenceByUuid(regionUuid) : null;
		DistrictReferenceDto district = districtUuid != null ? FacadeProvider.getDistrictFacade().getReferenceByUuid(districtUuid) : null;

		// Create a temporary case in order to use the CommitDiscardWrapperComponent
		CaseBulkEditData bulkEditData = new CaseBulkEditData();
		bulkEditData.setRegion(region);
		bulkEditData.setDistrict(district);

		BulkCaseDataForm form = new BulkCaseDataForm(district, selectedCases);
		form.setValue(bulkEditData);
		final CommitDiscardWrapperComponent<BulkCaseDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditCases));

		editView.addCommitListener(() -> {
			CaseBulkEditData updatedBulkEditData = form.getValue();

			boolean diseaseChange = form.getDiseaseCheckBox().getValue();
			boolean diseaseVariantChange = form.getDiseaseVariantCheckBox().getValue();
			boolean classificationChange = form.getClassificationCheckBox().getValue();
			boolean investigationStatusChange = form.getInvestigationStatusCheckBox().getValue();
			boolean outcomeChange = form.getOutcomeCheckBox().getValue();
			boolean surveillanceOfficerChange = district != null && form.getSurveillanceOfficerCheckBox().getValue();
			boolean facilityChange = form.getHealthFacilityCheckbox().getValue();

			CaseFacade caseFacade = FacadeProvider.getCaseFacade();
			List<T> selectedCasesCpy = new ArrayList<>(selectedCases);

			if (facilityChange) {
				VaadinUiUtil.showChooseOptionPopup(
					I18nProperties.getCaption(Captions.caseInfrastructureDataChanged),
					new Label(I18nProperties.getString(Strings.messageFacilityMulitChanged)),
					I18nProperties.getCaption(Captions.caseTransferCases),
					I18nProperties.getCaption(Captions.caseEditData),
					500,
					e -> BulkOperationHandler.<T> forBulkEdit()
						.doBulkOperation(
							selectedEntries -> caseFacade.saveBulkEditWithFacilities(
								selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
								updatedBulkEditData,
								diseaseChange,
								diseaseVariantChange,
								classificationChange,
								investigationStatusChange,
								outcomeChange,
								surveillanceOfficerChange,
								e),
							selectedCasesCpy,
							bulkOperationCallback(caseGrid, popupWindow)));
			} else {
				BulkOperationHandler.<T> forBulkEdit()
					.doBulkOperation(
						selectedEntries -> caseFacade.saveBulkCase(
							selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
							updatedBulkEditData,
							diseaseChange,
							diseaseVariantChange,
							classificationChange,
							investigationStatusChange,
							outcomeChange,
							surveillanceOfficerChange),
						selectedCasesCpy,
						bulkOperationCallback(caseGrid, popupWindow));
			}
		});

		editView.addDiscardListener(popupWindow::close);
	}

	public <T extends CaseIndexDto> Consumer<List<T>> bulkOperationCallback(AbstractCaseGrid<?> caseGrid, Window popupWindow) {
		return remainingCases -> {
			if (popupWindow != null) {
				popupWindow.close();
			}
			caseGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingCases)) {
				if (caseGrid instanceof CaseGrid) {
					((CaseGrid) caseGrid).asMultiSelect().selectItems(remainingCases.toArray(new CaseIndexDto[0]));
				} else if (caseGrid instanceof CaseGridDetailed) {
					((CaseGridDetailed) caseGrid).asMultiSelect().selectItems(remainingCases.toArray(new CaseIndexDetailedDto[0]));
				}
			} else {
				navigateToIndex();
			}
		};
	}

	private void appendSpecialCommands(CaseDataDto caze, CommitDiscardWrapperComponent<? extends Component> editView) {

		if (UiUtil.permitted(UserRight.CASE_DELETE)) {
			editView.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
				if (UiUtil.permitted(UserRight.CONTACT_VIEW)) {
					long contactCount = FacadeProvider.getContactFacade().getContactCount(caze.toReference());
					if (contactCount > 0) {
						VaadinUiUtil.showThreeOptionsPopup(
							I18nProperties.getString(Strings.headingDeleteContacts),
							new Label(I18nProperties.getString(Strings.confirmationDeleteCaseContacts)),
							I18nProperties.getCaption(Captions.actionYes),
							I18nProperties.getCaption(Captions.actionNo),
							I18nProperties.getCaption(Captions.caseCancelDeletion),
							null,
							option -> {
								if (option == VaadinUiUtil.PopupOption.OPTION1) {
									deleteCase(caze, true, deleteDetails);
								} else if (option == VaadinUiUtil.PopupOption.OPTION2) {
									deleteCase(caze, false, deleteDetails);
								}
								// Option 3 does not need to be handled because it would just return
							});
					} else {
						deleteCase(caze, false, deleteDetails);
					}
				} else {
					deleteCase(caze, false, deleteDetails);
				}
			}, getDeleteConfirmationDetails(Collections.singletonList(caze.getUuid())), (deleteDetails) -> {
				FacadeProvider.getCaseFacade().restore(caze.getUuid());
				UI.getCurrent().getNavigator().navigateTo(CasesView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityCase), caze.getUuid(), FacadeProvider.getCaseFacade());
		}

		// Initialize 'Archive' button
		if (UiUtil.permitted(UserRight.CASE_ARCHIVE)) {
			ControllerProvider.getArchiveController()
				.addArchivingButton(caze, ArchiveHandlers.forCase(), editView, () -> navigateToView(CaseDataView.VIEW_NAME, caze.getUuid(), null));
		}
	}

	private String getDeleteConfirmationDetails(List<String> caseUuids) {
		boolean hasPendingRequest = FacadeProvider.getSormasToSormasCaseFacade().hasPendingRequest(caseUuids);

		return hasPendingRequest ? "<br/>" + I18nProperties.getString(Strings.messageDeleteWithPendingShareRequest) + "<br/>" : "";
	}

	private void deleteCase(CaseDataDto caze, boolean withContacts, DeletionDetails deletionDetails) {
		try {
			if (withContacts) {
				FacadeProvider.getCaseFacade().deleteWithContacts(caze.getUuid(), deletionDetails);
			} else {
				FacadeProvider.getCaseFacade().delete(caze.getUuid(), deletionDetails);
			}
			UI.getCurrent().getNavigator().navigateTo(CasesView.VIEW_NAME);
		} catch (ExternalSurveillanceToolRuntimeException e) {
			Notification.show(
				String.format(
					I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntryNotDeleted),
					DataHelper.getShortUuid(caze.getUuid())),
				"",
				Type.ERROR_MESSAGE);
		}
	}

	public void archiveAllSelectedItems(Collection<CaseIndexDto> selectedRows, AbstractCaseGrid<?> caseGrid) {
		ControllerProvider.getArchiveController()
			.archiveSelectedItems(selectedRows, ArchiveHandlers.forCase(), bulkOperationCallback(caseGrid, null));
	}

	public void dearchiveAllSelectedItems(Collection<CaseIndexDto> selectedRows, AbstractCaseGrid<?> caseGrid) {
		ControllerProvider.getArchiveController()
			.dearchiveSelectedItems(selectedRows, ArchiveHandlers.forCase(), bulkOperationCallback(caseGrid, null));
	}

	public CommitDiscardWrapperComponent<HospitalizationForm> getHospitalizationComponent(
		final String caseUuid,
		ViewMode viewMode,
		boolean isEditAllowed) {

		CaseDataDto caze = findCase(caseUuid);
		HospitalizationForm hospitalizationForm =
			new HospitalizationForm(caze, viewMode, caze.isPseudonymized(), caze.isInJurisdiction(), isEditAllowed);
		hospitalizationForm.setValue(caze.getHospitalization());

		final CommitDiscardWrapperComponent<HospitalizationForm> editView =
			new CommitDiscardWrapperComponent<HospitalizationForm>(hospitalizationForm, hospitalizationForm.getFieldGroup());

		final JurisdictionValues jurisdictionValues = new JurisdictionValues();

		editView.setPreCommitListener(successCallback -> {
			final CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			final YesNoUnknown initialAdmittedToHealthFacility = cazeDto.getHospitalization().getAdmittedToHealthFacility();
			final YesNoUnknown admittedToHealthFacility =
				(YesNoUnknown) ((NullableOptionGroup) hospitalizationForm.getField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY))
					.getNullableValue();

			if (YesNoUnknown.YES == admittedToHealthFacility
				&& initialAdmittedToHealthFacility != admittedToHealthFacility
				&& cazeDto.getFacilityType() != FacilityType.HOSPITAL) {

				PlaceOfStayEditForm placeOfStayEditForm = new PlaceOfStayEditForm(cazeDto);
				placeOfStayEditForm.setValue(cazeDto);
				final CommitDiscardWrapperComponent<PlaceOfStayEditForm> wrapperComponent = new CommitDiscardWrapperComponent<>(
					placeOfStayEditForm,
					UiUtil.permitted(UserRight.CASE_EDIT),
					placeOfStayEditForm.getFieldGroup());
				wrapperComponent.addCommitListener(() -> {
					final CaseDataDto dto = placeOfStayEditForm.getValue();
					jurisdictionValues.region = dto.getRegion();
					jurisdictionValues.district = dto.getDistrict();
					jurisdictionValues.community = dto.getCommunity();
					jurisdictionValues.facilityType = FacilityType.HOSPITAL;
					jurisdictionValues.facility = dto.getHealthFacility();
					jurisdictionValues.facilityDetails = dto.getHealthFacilityDetails();
					jurisdictionValues.valuesUpdated = true;
				});

				VaadinUiUtil
					.showModalPopupWindow(wrapperComponent, I18nProperties.getString(Strings.headingPlaceOfStayInHospital), preCommitSuccessful -> {
						if (preCommitSuccessful) {
							successCallback.run();
						}
					});
			} else {
				successCallback.run();
			}
		});

		editView.addCommitListener(() -> {
			final CaseDataDto cazeDto = findCase(caseUuid);

			if (jurisdictionValues.valuesUpdated) {
				cazeDto.setRegion(jurisdictionValues.region);
				cazeDto.setDistrict(jurisdictionValues.district);
				cazeDto.setCommunity(jurisdictionValues.community);
				cazeDto.setFacilityType(jurisdictionValues.facilityType);
				cazeDto.setHealthFacility(jurisdictionValues.facility);
				cazeDto.setHealthFacilityDetails(jurisdictionValues.facilityDetails);
			}
			cazeDto.setHospitalization(hospitalizationForm.getValue());
			saveCase(cazeDto);
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<MaternalHistoryForm> getMaternalHistoryComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caze = findCase(caseUuid);
		MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		MaternalHistoryForm form = new MaternalHistoryForm(viewMode, maternalHistory.isPseudonymized(), maternalHistory.isInJurisdiction());
		form.setValue(maternalHistory);

		final CommitDiscardWrapperComponent<MaternalHistoryForm> component =
			new CommitDiscardWrapperComponent<MaternalHistoryForm>(form, UiUtil.permitted(UserRight.CASE_EDIT), form.getFieldGroup());
		component.addCommitListener(() -> {
			CaseDataDto caze1 = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			caze1.setMaternalHistory(form.getValue());
			saveCase(caze1);
		});

		return component;
	}

	public CommitDiscardWrapperComponent<PortHealthInfoForm> getPortHealthInfoComponent(final String caseUuid) {

		CaseDataDto caze = findCase(caseUuid);

		if (!hasPointOfEntry(caze)) {
			return null;
		}

		PointOfEntryDto pointOfEntry = FacadeProvider.getPointOfEntryFacade().getByCaseUuid(caseUuid);
		PortHealthInfoForm form =
			new PortHealthInfoForm(pointOfEntry, caze.getPointOfEntryDetails(), caze.isPseudonymized(), caze.isInJurisdiction());
		form.setValue(getPortHealthInfo(caze));

		final CommitDiscardWrapperComponent<PortHealthInfoForm> component =
			new CommitDiscardWrapperComponent<PortHealthInfoForm>(form, UiUtil.permitted(UserRight.PORT_HEALTH_INFO_EDIT), form.getFieldGroup());
		component.addCommitListener(() -> {
			CaseDataDto caze1 = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			caze1.setPortHealthInfo(form.getValue());
			saveCase(caze1);
		});

		return component;
	}

	public CommitDiscardWrapperComponent<SymptomsForm> getSymptomsEditComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caseDataDto = findCase(caseUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(caseDataDto.getPerson().getUuid());

		SymptomsForm symptomsForm = new SymptomsForm(
			caseDataDto,
			caseDataDto.getDisease(),
			person,
			SymptomsContext.CASE,
			viewMode,
			UiFieldAccessCheckers
				.forDataAccessLevel(UiUtil.getPseudonymizableDataAccessLevel(caseDataDto.isInJurisdiction()), caseDataDto.isPseudonymized()));
		symptomsForm.setValue(caseDataDto.getSymptoms());

		CommitDiscardWrapperComponent<SymptomsForm> editView =
			new CommitDiscardWrapperComponent<SymptomsForm>(symptomsForm, UiUtil.permitted(UserRight.CASE_EDIT), symptomsForm.getFieldGroup());

		editView.addCommitListener(() -> {
			CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			cazeDto.setSymptoms(symptomsForm.getValue());
			saveCase(cazeDto);
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(final String caseUuid, Consumer<Boolean> sourceContactsToggleCallback) {
		boolean hasCaseEditRight = UiUtil.permitted(UserRight.CASE_EDIT);
		CommitDiscardWrapperComponent<EpiDataForm> epiDataComponent = getEpiDataComponent(caseUuid, sourceContactsToggleCallback, hasCaseEditRight);
		epiDataComponent.setEnabled(hasCaseEditRight);
		return epiDataComponent;
	}

	public CommitDiscardWrapperComponent<EpiDataForm> getEpiDataComponent(
		final String caseUuid,
		Consumer<Boolean> sourceContactsToggleCallback,
		boolean isEditAllowed) {

		CaseDataDto caze = findCase(caseUuid);
		EpiDataForm epiDataForm = new EpiDataForm(
			caze.getDisease(),
			CaseDataDto.class,
			caze.isPseudonymized(),
			caze.isInJurisdiction(),
			sourceContactsToggleCallback,
			isEditAllowed);
		epiDataForm.setValue(caze.getEpiData());

		final CommitDiscardWrapperComponent<EpiDataForm> editView =
			new CommitDiscardWrapperComponent<EpiDataForm>(epiDataForm, epiDataForm.getFieldGroup());

		editView.addCommitListener(() -> {
			CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
			cazeDto.setEpiData(epiDataForm.getValue());
			saveCase(cazeDto);
		});

		if (UiUtil.permitted(UserRight.CONTACT_VIEW)) {
			long sourceContactCount = FacadeProvider.getContactFacade().count(new ContactCriteria().resultingCase(caze.toReference()));
			if (sourceContactCount > 0) {
				epiDataForm.disableContactWithSourceCaseKnownField();
			}
		}

		return editView;
	}

	public CommitDiscardWrapperComponent<ClinicalCourseForm> getClinicalCourseComponent(String caseUuid) {

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		ClinicalCourseForm form = new ClinicalCourseForm(caze.isPseudonymized(), caze.isInJurisdiction());
		form.setValue(caze.getClinicalCourse());

		final CommitDiscardWrapperComponent<ClinicalCourseForm> view =
			new CommitDiscardWrapperComponent<>(form, UiUtil.permitted(UserRight.CLINICAL_COURSE_EDIT), form.getFieldGroup());
		view.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				CaseDataDto cazeDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
				cazeDto.setClinicalCourse(form.getValue());
				saveCase(cazeDto);
			}
		});

		view.getCommitButton().setVisible(false);
		view.getDiscardButton().setVisible(false);

		return view;
	}

	public DetailSubComponentWrapper getExternalDataComponent(final String caseUuid, ViewMode viewMode) {

		CaseDataDto caseDataDto = findCase(caseUuid);

		CaseExternalDataForm caseExternalDataForm = new CaseExternalDataForm(
			UiFieldAccessCheckers
				.forDataAccessLevel(UiUtil.getPseudonymizableDataAccessLevel(caseDataDto.isInJurisdiction()), caseDataDto.isPseudonymized()));
		caseExternalDataForm.setValue(caseDataDto);

		DetailSubComponentWrapper wrapper = new DetailSubComponentWrapper(() -> null);
		wrapper.addComponent(caseExternalDataForm);

		return wrapper;
	}

	public void saveCaseWithFacilityChangedPrompt(CaseDataDto caze, CaseDataDto oldCase) {

		if (FacilityType.HOSPITAL == caze.getFacilityType() && oldCase.getFacilityType() != FacilityType.HOSPITAL) {
			CurrentHospitalizationForm currentHospitalizationForm = new CurrentHospitalizationForm();
			currentHospitalizationForm.setValue(caze.getHospitalization());
			VaadinUiUtil.showThreeOptionsPopup(
				I18nProperties.getString(Strings.headingCurrentHospitalization),
				currentHospitalizationForm,
				I18nProperties.getCaption(Captions.actionSaveAndOpenHospitalization),
				I18nProperties.getCaption(Captions.actionSave),
				I18nProperties.getCaption(Captions.actionDiscard),
				700,
				option -> {
					final NullableOptionGroup admittedToHealthFacilityField =
						currentHospitalizationForm.getField(HospitalizationDto.ADMITTED_TO_HEALTH_FACILITY);
					switch (option) {
					case OPTION1: {
						caze.getHospitalization().setAdmittedToHealthFacility((YesNoUnknown) admittedToHealthFacilityField.getNullableValue());
						saveCase(caze);
						ControllerProvider.getCaseController().navigateToView(HospitalizationView.VIEW_NAME, caze.getUuid(), null);
					}
						break;
					case OPTION2: {
						caze.getHospitalization().setAdmittedToHealthFacility((YesNoUnknown) admittedToHealthFacilityField.getNullableValue());
						saveCase(caze);
						ControllerProvider.getCaseController().navigateToView(CaseDataView.VIEW_NAME, caze.getUuid(), null);
					}
						break;
					case OPTION3:
						SormasUI.refreshView();
						break;
					}
				});
		} else if (oldCase.getFacilityType() == FacilityType.HOSPITAL
			&& caze.getHealthFacility() != null
			&& !caze.getHealthFacility().getUuid().equals(oldCase.getHealthFacility().getUuid())) {
			VaadinUiUtil.showChooseOptionPopup(
				I18nProperties.getCaption(Captions.caseInfrastructureDataChanged),
				new Label(I18nProperties.getString(Strings.messageFacilityChanged)),
				I18nProperties.getCaption(Captions.caseTransferCase),
				I18nProperties.getCaption(Captions.caseEditData),
				500,
				e -> {
					CaseLogic.handleHospitalization(caze, oldCase, e.booleanValue());
					saveCase(caze);
					SormasUI.refreshView();
				});
		} else {
			saveCase(caze);
		}
	}

	public void referFromPointOfEntry(CaseDataDto caze) {

		CaseFacilityChangeForm form = new CaseFacilityChangeForm();
		form.setValue(caze);
		CommitDiscardWrapperComponent<CaseFacilityChangeForm> view =
			new CommitDiscardWrapperComponent<CaseFacilityChangeForm>(form, UiUtil.permitted(UserRight.CASE_REFER_FROM_POE), form.getFieldGroup());
		view.getCommitButton().setCaption(I18nProperties.getCaption(Captions.caseReferFromPointOfEntry));

		Window window = VaadinUiUtil.showPopupWindow(view);
		window.setCaption(I18nProperties.getString(Strings.headingReferCaseFromPointOfEntry));

		view.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				CaseDataDto dto = form.getValue();
				dto.getHospitalization().setAdmissionDate(new Date());
				FacadeProvider.getCaseFacade().save(dto);
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

	public void openClassificationRulesPopup(DiseaseClassificationCriteriaDto diseaseCriteria) {

		VerticalLayout classificationRulesLayout = new VerticalLayout();
		classificationRulesLayout.setMargin(true);

		if (diseaseCriteria != null) {
			if (diseaseCriteria.getSuspectCriteria() != null) {
				Label suspectContent = new Label();
				suspectContent.setContentMode(ContentMode.HTML);
				suspectContent.setWidth(100, Unit.PERCENTAGE);
				suspectContent.setValue(ClassificationHtmlRenderer.createSuspectHtmlString(diseaseCriteria));
				classificationRulesLayout.addComponent(suspectContent);
			}

			if (diseaseCriteria.getProbableCriteria() != null) {
				Label probableContent = new Label();
				probableContent.setContentMode(ContentMode.HTML);
				probableContent.setWidth(100, Unit.PERCENTAGE);
				probableContent.setValue(ClassificationHtmlRenderer.createProbableHtmlString(diseaseCriteria));
				classificationRulesLayout.addComponent(probableContent);
			}

			if (diseaseCriteria.getConfirmedCriteria() != null) {
				Label confirmedContent = new Label();
				confirmedContent.setContentMode(ContentMode.HTML);
				confirmedContent.setWidth(100, Unit.PERCENTAGE);
				confirmedContent.setValue(ClassificationHtmlRenderer.createConfirmedHtmlString(diseaseCriteria));
				classificationRulesLayout.addComponent(confirmedContent);
			}

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
				if (diseaseCriteria.getConfirmedNoSymptomsCriteria() != null) {
					Label confirmedNoSymptomsContent = new Label();
					confirmedNoSymptomsContent.setContentMode(ContentMode.HTML);
					confirmedNoSymptomsContent.setWidth(100, Unit.PERCENTAGE);
					confirmedNoSymptomsContent.setValue(ClassificationHtmlRenderer.createConfirmedNoSymptomsHtmlString(diseaseCriteria));
					classificationRulesLayout.addComponent(confirmedNoSymptomsContent);
				}

				if (diseaseCriteria.getConfirmedUnknownSymptomsCriteria() != null) {
					Label confirmedUnknownSymptomsContent = new Label();
					confirmedUnknownSymptomsContent.setContentMode(ContentMode.HTML);
					confirmedUnknownSymptomsContent.setWidth(100, Unit.PERCENTAGE);
					confirmedUnknownSymptomsContent.setValue(ClassificationHtmlRenderer.createConfirmedUnknownSymptomsHtmlString(diseaseCriteria));
					classificationRulesLayout.addComponent(confirmedUnknownSymptomsContent);
				}
			}

			if (diseaseCriteria.getNotACaseCriteria() != null) {
				Label notACaseContent = new Label();
				notACaseContent.setContentMode(ContentMode.HTML);
				notACaseContent.setWidth(100, Unit.PERCENTAGE);
				notACaseContent.setValue(ClassificationHtmlRenderer.createNotACaseHtmlString(diseaseCriteria));
				classificationRulesLayout.addComponent(notACaseContent);
			}
		}

		Window popupWindow = VaadinUiUtil.showPopupWindow(classificationRulesLayout);
		popupWindow.addCloseListener(e -> {
			popupWindow.close();
		});
		popupWindow.setWidth(860, Unit.PIXELS);
		popupWindow.setHeight(80, Unit.PERCENTAGE);
		popupWindow.setCaption(I18nProperties.getString(Strings.classificationRulesFor) + " " + diseaseCriteria.getDisease().toString());
	}

	public void deleteAllSelectedItems(Collection<? extends CaseIndexDto> selectedRows, AbstractCaseGrid<?> caseGrid) {

		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(selectedRows, DeleteRestoreHandlers.forCase(), bulkOperationCallback(caseGrid, null));
	}

	public void restoreSelectedCases(Collection<? extends CaseIndexDto> selectedRows, AbstractCaseGrid<?> caseGrid) {
		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(selectedRows, DeleteRestoreHandlers.forCase(), bulkOperationCallback(caseGrid, null));
	}

	public void sendSmsToAllSelectedItems(Collection<? extends CaseIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			final List<String> caseUuids = selectedRows.stream().map(caseIndexDto -> caseIndexDto.getUuid()).collect(Collectors.toList());
			final SmsComponent smsComponent =
				new SmsComponent(FacadeProvider.getCaseFacade().countCasesWithMissingContactInformation(caseUuids, MessageType.SMS));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getCaption(Captions.messagesSendingSms),
				smsComponent,
				I18nProperties.getCaption(Captions.actionSend),
				I18nProperties.getCaption(Captions.actionCancel),
				640,
				confirmationEvent -> {
					if (confirmationEvent.booleanValue()) {
						FacadeProvider.getCaseFacade().sendMessage(caseUuids, "", smsComponent.getValue(), MessageType.SMS);
						Notification.show(null, I18nProperties.getString(Strings.notificationSmsSent), Type.TRAY_NOTIFICATION);
					}
				});
		}
	}

	public void openLineListingWindow() {

		Window window = new Window(I18nProperties.getString(Strings.headingLineListing));

		LineListingLayout lineListingForm = new LineListingLayout(window);
		lineListingForm.setSaveCallback(cases -> saveCasesFromLineListing(lineListingForm, cases));

		if (UiUtil.permitted(UserRight.CASE_CHANGE_EPID_NUMBER)) {
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

	private void saveCasesFromLineListing(LineListingLayout lineListingForm, LinkedList<LineDto<CaseDataDto>> cases) {
		try {
			lineListingForm.validate();
		} catch (ValidationRuntimeException e) {
			Notification.show(I18nProperties.getString(Strings.errorFieldValidationFailed), "", Type.ERROR_MESSAGE);
			return;
		}

		while (!cases.isEmpty()) {
			LineDto<CaseDataDto> caseLineDto = cases.pop();
			CaseDataDto newCase = caseLineDto.getEntity();
			PersonDto newPerson = caseLineDto.getPerson();

			ControllerProvider.getPersonController()
				.selectOrCreatePerson(newPerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
					if (selectedPerson != null) {
						newCase.setPerson(selectedPerson);

						selectOrCreateCase(newCase, FacadeProvider.getPersonFacade().getByUuid(selectedPerson.getUuid()), uuid -> {
							if (uuid == null) {
								FacadeProvider.getCaseFacade().save(newCase);
								Notification.show(I18nProperties.getString(Strings.messageCaseCreated), Type.ASSISTIVE_NOTIFICATION);
							}
						});

						if (cases.isEmpty()) {
							lineListingForm.closeWindow();
							ControllerProvider.getCaseController().navigateToIndex();
						}
					}
				}, true);
		}
	}

	public TitleLayout getCaseViewTitleLayout(CaseDataDto caseData) {
		TitleLayout titleLayout = new TitleLayout();

		titleLayout.addRow(DiseaseHelper.toString(caseData.getDisease(), caseData.getDiseaseDetails()));
		titleLayout.addRow(caseData.getCaseClassification().toString());

		String shortUuid = DataHelper.getShortUuid(caseData.getUuid());
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(caseData.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	public <T extends CaseIndexDto> void sendCasesToExternalSurveillanceTool(Collection<T> selectedCases, AbstractCaseGrid<?> caseGrid) {

		// Show an error when at least one selected case is not a CORONAVIRUS case
		Optional<T> nonCoronavirusCase = selectedCases.stream().filter(c -> c.getDisease() != Disease.CORONAVIRUS).findFirst();
		if (nonCoronavirusCase.isPresent()) {
			Notification.show(
				String.format(
					I18nProperties.getString(Strings.errorExternalSurveillanceToolNonCoronavirusCase),
					DataHelper.getShortUuid(nonCoronavirusCase.get().getUuid()),
					I18nProperties.getEnumCaption(Disease.CORONAVIRUS)),
				"",
				Type.ERROR_MESSAGE);
			return;
		}

		// Show an error when at least one selected case is not owned by this server because ownership has been handed over
		List<String> selectedUuids = selectedCases.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList());
		List<String> notSharableUuids = FacadeProvider.getCaseFacade().getUuidsNotShareableWithExternalReportingTools(selectedUuids);
		if (CollectionUtils.isNotEmpty(notSharableUuids)) {

			List<T> withoutNotShareable = selectedCases.stream().filter(c -> !notSharableUuids.contains(c.getUuid())).collect(Collectors.toList());

			TextArea notShareableListComponent = new TextArea("", new ArrayList<>(notSharableUuids).toString());
			notShareableListComponent.setWidthFull();
			notShareableListComponent.setEnabled(false);
			Label notSharableLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.errorExternalSurveillanceToolCasesNotSharable),
					notSharableUuids.size(),
					selectedCases.size()),
				ContentMode.HTML);
			notSharableLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);

			if (existShareableCases(selectedCases.size(), notSharableUuids.size())) {
				VaadinUiUtil.showPopupWindow(
					new VerticalLayout(notSharableLabel, notShareableListComponent),
					I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_send));
			} else {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_send),
					new VerticalLayout(notSharableLabel, notShareableListComponent),
					String.format(
						I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_excludeAndSend),
						withoutNotShareable.size(),
						selectedCases.size()),
					I18nProperties.getCaption(Captions.actionCancel),
					800,
					(confirmed) -> {
						if (confirmed) {
							ExternalSurveillanceServiceGateway
								.sendCasesToExternalSurveillanceTool(withoutNotShareable, false, bulkOperationCallback(caseGrid, null));
						}
					});
			}
		} else {
			ExternalSurveillanceServiceGateway.sendCasesToExternalSurveillanceTool(selectedCases, true, bulkOperationCallback(caseGrid, null));
		}
	}

	public void linkSelectedCasesToEvent(Collection<? extends CaseIndexDto> selectedRows, AbstractCaseGrid<?> caseGrid) {
		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoCasesSelected),
				I18nProperties.getString(Strings.messageNoCasesSelected),
				Notification.Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		if (!selectedRows.stream().allMatch(caze -> caze.getDisease().equals(selectedRows.stream().findAny().get().getDisease()))) {
			new Notification(I18nProperties.getString(Strings.messageBulkCasesWithDifferentDiseasesSelected), Notification.Type.WARNING_MESSAGE)
				.show(Page.getCurrent());
			return;
		}

		ControllerProvider.getEventController()
			.selectOrCreateEventForCaseList(
				selectedRows.stream().map(CaseIndexDto::toReference).collect(Collectors.toList()),
				remaining -> bulkOperationCallback(caseGrid, null).accept(
					selectedRows.stream()
						.filter(s -> remaining.stream().anyMatch(r -> r.getUuid().equals(s.getUuid())))
						.collect(Collectors.toList())));
	}

	public boolean existShareableCases(int selectedCasesSize, int notShareableCasesSize) {
		return selectedCasesSize == notShareableCasesSize;
	}

	private static class JurisdictionValues {

		protected boolean valuesUpdated;
		protected RegionReferenceDto region;
		protected DistrictReferenceDto district;
		protected CommunityReferenceDto community;
		protected FacilityType facilityType;
		protected FacilityReferenceDto facility;
		protected String facilityDetails;
	}

	public boolean hasPointOfEntry(CaseDataDto caze) {
		if (caze.getPointOfEntry() == null) {
			return FacadeProvider.getPointOfEntryFacade().existsForCase(caze.getUuid());
		}
		return true;
	}

	public PortHealthInfoDto getPortHealthInfo(CaseDataDto caze) {
		PortHealthInfoDto portHealthInfoDto = caze.getPortHealthInfo();
		if (portHealthInfoDto == null) {
			portHealthInfoDto = FacadeProvider.getPortHealthInfoFacade().getByCaseUuid(caze.getUuid());
		}
		return portHealthInfoDto;
	}
}
