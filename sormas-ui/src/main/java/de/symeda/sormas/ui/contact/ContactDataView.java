/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.events.eventLink.EventListComponent;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.SidePanelLayout;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

public class ContactDataView extends AbstractContactView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String CASE_BUTTONS_LOC = "caseButtons";
	public static final String EVENTS_LOC = "events";
	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";
	public static final String IMMUNIZATION_LOC = "immunizations";
	public static final String VACCINATIONS_LOC = "vaccinations";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";
	public static final String DOCUMENTS_LOC = "documents";

	private CommitDiscardWrapperComponent<ContactDataForm> editComponent;

	public ContactDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		ContactDto contactDto = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());

		editComponent = ControllerProvider.getContactController()
			.getContactDataEditComponent(getContactRef().getUuid(), ViewMode.NORMAL, contactDto.isPseudonymized());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		SidePanelLayout layout = new SidePanelLayout(
			editComponent,
			CASE_LOC,
			CASE_BUTTONS_LOC,
			EVENTS_LOC,
			TASKS_LOC,
			SAMPLES_LOC,
			IMMUNIZATION_LOC,
			VACCINATIONS_LOC,
			SORMAS_TO_SORMAS_LOC,
			DOCUMENTS_LOC,
			QuarantineOrderDocumentsComponent.QUARANTINE_LOC);

		container.addComponent(layout);

		CaseDataDto caseDto = null;
		if (contactDto.getCaze() != null) {
			caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(contactDto.getCaze().getUuid());
			layout.addSidePanelComponent(createCaseInfoLayout(caseDto), CASE_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_REASSIGN_CASE)) {
			HorizontalLayout buttonsLayout = new HorizontalLayout();
			buttonsLayout.setSpacing(true);

			Button chooseCaseButton = ButtonHelper.createButton(
				contactDto.getCaze() == null ? Captions.contactChooseSourceCase : Captions.contactChangeCase,
				null,
				ValoTheme.BUTTON_PRIMARY,
				CssStyles.VSPACE_2);
			buttonsLayout.addComponent(chooseCaseButton);
			Button removeCaseButton = ButtonHelper.createButton(Captions.contactRemoveCase, null, ValoTheme.BUTTON_LINK);

			if (contactDto.getCaze() != null) {
				buttonsLayout.addComponent(removeCaseButton);
			}

			chooseCaseButton.addClickListener(e -> {
				VaadinUiUtil.showConfirmationPopup(
					I18nProperties.getString(Strings.headingDiscardUnsavedChanges),
					new Label(I18nProperties.getString(Strings.confirmationContactSourceCaseDiscardUnsavedChanges)),
					I18nProperties.getString(Strings.yes),
					I18nProperties.getString(Strings.no),
					480,
					confirmed -> {
						if (confirmed) {
							editComponent.discard();
							Disease selectedDisease = ((ContactDataForm) editComponent.getWrappedComponent()).getSelectedDisease();
							ControllerProvider.getContactController().openSelectCaseForContactWindow(selectedDisease, selectedCase -> {
								if (selectedCase != null) {
									((ContactDataForm) editComponent.getWrappedComponent()).setSourceCase(selectedCase.toReference());
									ContactDto contactToChange = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
									contactToChange.setCaze(selectedCase.toReference());
									FacadeProvider.getContactFacade().save(contactToChange);
									layout.addComponent(createCaseInfoLayout(selectedCase.getUuid()), CASE_LOC);
									removeCaseButton.setVisible(true);
									chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));
									ControllerProvider.getContactController().navigateToData(contactDto.getUuid());
									new Notification(null, I18nProperties.getString(Strings.messageContactCaseChanged), Type.TRAY_NOTIFICATION, false)
										.show(Page.getCurrent());
								}
							});
						}
					});
			});
			removeCaseButton.addClickListener(e -> {
				if (contactDto.getRegion() == null || contactDto.getDistrict() == null) {
					// Ask user to fill in a region and district before removing the source case
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingContactDataNotComplete),
						I18nProperties.getString(Strings.messageSetContactRegionAndDistrict));
				} else {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingRemoveCaseFromContact),
						new Label(I18nProperties.getString(Strings.confirmationContactSourceCaseDiscardUnsavedChanges)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						480,
						confirmed -> {
							if (confirmed) {
								editComponent.discard();
								layout.removeComponent(CASE_LOC);
								((ContactDataForm) editComponent.getWrappedComponent()).setSourceCase(null);
								ContactDto contactToChange = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
								contactToChange.setCaze(null);
								FacadeProvider.getContactFacade().save(contactToChange);
								removeCaseButton.setVisible(false);
								chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChooseSourceCase));
								ControllerProvider.getContactController().navigateToData(contactDto.getUuid());
								new Notification(null, I18nProperties.getString(Strings.messageContactCaseRemoved), Type.TRAY_NOTIFICATION, false)
									.show(Page.getCurrent());
							}
						});
				}
			});

			layout.addSidePanelComponent(buttonsLayout, CASE_BUTTONS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)) {
			TaskListComponent taskList = new TaskListComponent(TaskContext.CONTACT, getContactRef(), contactDto.getDisease());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			SampleListComponent sampleList = new SampleListComponent(
				new SampleCriteria().contact(getContactRef()).sampleAssociationType(SampleAssociationType.CONTACT),
				e -> showNavigationConfirmPopupIfDirty(
					() -> ControllerProvider.getSampleController().create(getContactRef(), contactDto.getDisease(), () -> {
						final ContactDto contactByUuid = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
						FacadeProvider.getContactFacade().save(contactByUuid);
						SormasUI.refreshView();
					})));

			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesContact));
			layout.addSidePanelComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			&& UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW)) {
			VerticalLayout eventsLayout = new VerticalLayout();
			eventsLayout.setMargin(false);
			eventsLayout.setSpacing(false);

			EventListComponent eventList = new EventListComponent(getContactRef());
			eventList.addStyleName(CssStyles.SIDE_COMPONENT);
			eventsLayout.addComponent(eventList);

			layout.addSidePanelComponent(eventsLayout, EVENTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				final ImmunizationListCriteria immunizationListCriteria =
					new ImmunizationListCriteria.Builder(contactDto.getPerson()).wihDisease(contactDto.getDisease()).build();
				layout.addSidePanelComponent(new SideComponentLayout(new ImmunizationListComponent(immunizationListCriteria)), IMMUNIZATION_LOC);
			} else {
				VaccinationListCriteria criteria =
					new VaccinationListCriteria.Builder(contactDto.getPerson()).withDisease(contactDto.getDisease()).build();
				layout.addSidePanelComponent(
					new SideComponentLayout(
						new VaccinationListComponent(
							getContactRef(),
							criteria,
							contactDto.getRegion() != null ? contactDto.getRegion() : caseDto.getResponsibleRegion(),
							contactDto.getDistrict() != null ? contactDto.getDistrict() : caseDto.getResponsibleDistrict(),
							this)),
					VACCINATIONS_LOC);
			}
		}

		boolean sormasToSormasfeatureEnabled = FacadeProvider.getSormasToSormasFacade().isSharingCasesContactsAndSamplesEnabledForUser();
		if (sormasToSormasfeatureEnabled || contactDto.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(contactDto, sormasToSormasfeatureEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		DocumentListComponent documentList = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)) {
			documentList =
				new DocumentListComponent(DocumentRelatedEntityType.CONTACT, getContactRef(), UserRight.CONTACT_EDIT, contactDto.isPseudonymized());
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		QuarantineOrderDocumentsComponent.addComponentToLayout(layout.getSidePanelComponent(), contactDto, documentList);

		if (isContactEditAllowed(false)) {
			if (FacadeProvider.getContactFacade().isArchived(contactDto.getUuid())
				&& FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.EDIT_ARCHIVED_ENTITIES)) {
				layout.disable(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
			}
		} else {
			layout.disable();
		}

	}

	private CaseInfoLayout createCaseInfoLayout(String caseUuid) {

		return createCaseInfoLayout(FacadeProvider.getCaseFacade().getByUuid(caseUuid));
	}

	private CaseInfoLayout createCaseInfoLayout(CaseDataDto caseDto) {

		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

		return caseInfoLayout;
	}
}
