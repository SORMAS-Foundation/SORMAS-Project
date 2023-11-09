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
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
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
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.vaccination.VaccinationAssociationType;
import de.symeda.sormas.api.vaccination.VaccinationCriteria;
import de.symeda.sormas.ui.ControllerProvider;
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
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
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

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
			addCreateFromCaseButtonLogic();
		}

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(
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

		final String uuid = contactDto.getUuid();
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_REASSIGN_CASE) && isEditAllowed()) {
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

			chooseCaseButton.addClickListener(e -> showUnsavedChangesPopup(() -> {
				Disease selectedDisease = editComponent.getWrappedComponent().getSelectedDisease();
				ControllerProvider.getContactController().openSelectCaseForContactWindow(selectedDisease, selectedCase -> {
					if (selectedCase != null) {
						editComponent.getWrappedComponent().setSourceCase(selectedCase.toReference());
						ContactDto contactToChange = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
						contactToChange.setCaze(selectedCase.toReference());
						FacadeProvider.getContactFacade().save(contactToChange);
						layout.addComponent(createCaseInfoLayout(selectedCase.getUuid()), CASE_LOC);
						removeCaseButton.setVisible(true);
						chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));
						ControllerProvider.getContactController().navigateToData(uuid);
						new Notification(null, I18nProperties.getString(Strings.messageContactCaseChanged), Type.TRAY_NOTIFICATION, false)
							.show(Page.getCurrent());
					}
				});
			}));
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
								editComponent.getWrappedComponent().setSourceCase(null);
								ContactDto contactToChange = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
								contactToChange.setCaze(null);
								FacadeProvider.getContactFacade().save(contactToChange);
								removeCaseButton.setVisible(false);
								chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChooseSourceCase));
								ControllerProvider.getContactController().navigateToData(uuid);
								new Notification(null, I18nProperties.getString(Strings.messageContactCaseRemoved), Type.TRAY_NOTIFICATION, false)
									.show(Page.getCurrent());
							}
						});
				}
			});

			layout.addSidePanelComponent(buttonsLayout, CASE_BUTTONS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW)) {
			TaskListComponent taskList =
				new TaskListComponent(TaskContext.CONTACT, getContactRef(), contactDto.getDisease(), this::showUnsavedChangesPopup, isEditAllowed());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			SampleListComponent sampleList = new SampleListComponent(
				new SampleCriteria().contact(getContactRef()).disease(contactDto.getDisease()).sampleAssociationType(SampleAssociationType.CONTACT),
				this::showUnsavedChangesPopup,
				isEditAllowed());
			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesContact));
			layout.addSidePanelComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)
			&& UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW)) {
			VerticalLayout eventsLayout = new VerticalLayout();
			eventsLayout.setMargin(false);
			eventsLayout.setSpacing(false);

			EventListComponent eventList = new EventListComponent(getContactRef(), this::showUnsavedChangesPopup, isEditAllowed());
			eventList.addStyleName(CssStyles.SIDE_COMPONENT);
			eventsLayout.addComponent(eventList);

			layout.addSidePanelComponent(eventsLayout, EVENTS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				layout.addSidePanelComponent(new SideComponentLayout(new ImmunizationListComponent(() -> {
					ContactDto refreshedContact = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
					return new ImmunizationListCriteria.Builder(refreshedContact.getPerson()).withDisease(refreshedContact.getDisease()).build();
				}, null, this::showUnsavedChangesPopup, isEditAllowed())), IMMUNIZATION_LOC);
			} else {
				layout.addSidePanelComponent(new SideComponentLayout(new VaccinationListComponent(() -> {
					ContactDto refreshedContact = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
					CaseDataDto refreshedCase = null;
					if (refreshedContact.getCaze() != null) {
						refreshedCase = FacadeProvider.getCaseFacade().getCaseDataByUuid(refreshedContact.getCaze().getUuid());
					}
					return new VaccinationCriteria.Builder(refreshedContact.getPerson()).withDisease(refreshedContact.getDisease())
						.build()
						.vaccinationAssociationType(VaccinationAssociationType.CONTACT)
						.contactReference(getContactRef())
						.region(refreshedContact.getRegion() != null ? refreshedContact.getRegion() : refreshedCase.getResponsibleRegion())
						.district(refreshedContact.getDistrict() != null ? refreshedContact.getDistrict() : refreshedCase.getResponsibleDistrict());
				}, null, this::showUnsavedChangesPopup, isEditAllowed())), VACCINATIONS_LOC);
			}
		}

		boolean sormasToSormasfeatureEnabled =
			FacadeProvider.getSormasToSormasFacade().isAnyFeatureConfigured(FeatureType.SORMAS_TO_SORMAS_SHARE_CONTACTS);
		if (sormasToSormasfeatureEnabled || contactDto.getSormasToSormasOriginInfo() != null || contactDto.isOwnershipHandedOver()) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(contactDto, isEditAllowed());
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		final EditPermissionType contactEditAllowed = FacadeProvider.getContactFacade().getEditPermissionType(uuid);
		DocumentListComponent documentList = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)
			&& UserProvider.getCurrent().hasUserRight(UserRight.DOCUMENT_VIEW)) {
			boolean isDocumentDeleteAllowed =
				EditPermissionType.ALLOWED.equals(contactEditAllowed) || EditPermissionType.WITHOUT_OWNERSHIP.equals(contactEditAllowed);
			documentList = new DocumentListComponent(
				DocumentRelatedEntityType.CONTACT,
				getContactRef(),
				UserRight.CONTACT_EDIT,
				contactDto.isPseudonymized(),
				isEditAllowed(),
				isDocumentDeleteAllowed);
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		QuarantineOrderDocumentsComponent.addComponentToLayout(layout, contactDto, documentList);

		final boolean deleted = FacadeProvider.getContactFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, contactEditAllowed);
	}

	private void addCreateFromCaseButtonLogic() {
		ContactDataForm contactDataForm = editComponent.getWrappedComponent();

		if (contactDataForm.getValue().getResultingCase() == null) {
			if (!ContactClassification.NO_CONTACT.equals(contactDataForm.getValue().getContactClassification())) {
				if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CONVERT)) {
					contactDataForm.getToCaseButton().addClickListener(event -> {
						if (!ContactClassification.CONFIRMED.equals(contactDataForm.getValue().getContactClassification())) {
							VaadinUiUtil.showSimplePopupWindow(
								I18nProperties.getString(Strings.headingContactConfirmationRequired),
								I18nProperties.getString(Strings.messageContactToCaseConfirmationRequired));
						} else {
							if (contactDataForm.getValue().getFollowUpComment() != null) {
								int finalFollowUpCommentLenght =
									ContactLogic
										.extendFollowUpStatusComment(
											contactDataForm.getValue().getFollowUpComment(),
											I18nProperties.getString(Strings.messageSystemFollowUpCanceled))
										.length();
								if (finalFollowUpCommentLenght > FieldConstraints.CHARACTER_LIMIT_BIG) {
									VerticalLayout verticalLayout = new VerticalLayout();
									Label contentLabel = new Label(
										String.format(
											I18nProperties.getString(Strings.messageContactConversionFollowUpCommentLarge),
											I18nProperties.getString(Strings.messageSystemFollowUpCanceled)),
										ContentMode.HTML);
									contentLabel.setWidth(100, Unit.PERCENTAGE);
									verticalLayout.addComponent(contentLabel);
									verticalLayout.setMargin(false);

									VaadinUiUtil.showConfirmationPopup(
										I18nProperties.getString(Strings.headingContactConversionFollowUpCommentLarge),
										verticalLayout,
										I18nProperties.getString(Strings.messageContactConversionFollowUpCommentLargeOmitMessage),
										I18nProperties.getString(Strings.messageContactConversionFollowUpCommentLargeAdjustComment),
										confirm -> {
											if (Boolean.TRUE.equals(confirm)) {
												createFromContactWithCheckChanges(contactDataForm);
											}
										});
								} else {
									createFromContactWithCheckChanges(contactDataForm);
								}
							} else {
								createFromContactWithCheckChanges(contactDataForm);
							}
						}
					});
				}
			}
		}
	}

	private void createFromContactWithCheckChanges(ContactDataForm contactDataForm) {
		if (editComponent.isModified()) {
			showUnsavedChangesPopup(() -> ControllerProvider.getCaseController().createFromContact(contactDataForm.getValue()));
		} else {
			ControllerProvider.getCaseController().createFromContact(contactDataForm.getValue());
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
