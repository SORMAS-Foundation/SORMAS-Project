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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.samples.SampleListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ContactDataView extends AbstractContactView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String EDIT_LOC = "edit";
	public static final String CASE_LOC = "case";
	public static final String CASE_BUTTONS_LOC = "caseButtons";
	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";

	public ContactDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(
				LayoutUtil.fluidColumnLoc(8, 0, 12, 0, EDIT_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_BUTTONS_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC)
				);

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		Boolean isInJurisdiction = FacadeProvider.getContactFacade().isContactEditAllowed(getContactRef().getUuid());
		CommitDiscardWrapperComponent<?> editComponent = ControllerProvider.getContactController()
				.getContactDataEditComponent(getContactRef().getUuid(), isInJurisdiction);
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, EDIT_LOC);

		ContactDto contactDto = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
		if (contactDto.getCaze() != null) {
			layout.addComponent(createCaseInfoLayout(contactDto.getCaze().getUuid()), CASE_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_REASSIGN_CASE)) {
			HorizontalLayout buttonsLayout = new HorizontalLayout();
			buttonsLayout.setSpacing(true);

			Button chooseCaseButton = ButtonHelper.createButton(
					contactDto.getCaze() == null ? Captions.contactChooseSourceCase : Captions.contactChangeCase, null, ValoTheme.BUTTON_PRIMARY, CssStyles.VSPACE_2);
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
										((ContactDataForm) editComponent.getWrappedComponent()).setSourceCase(selectedCase);
										ContactDto contactToChange = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
										contactToChange.setCaze(selectedCase.toReference());
										FacadeProvider.getContactFacade().saveContact(contactToChange);
										layout.addComponent(createCaseInfoLayout(selectedCase.getUuid()),CASE_LOC);
										removeCaseButton.setVisible(true);
										chooseCaseButton.setCaption(I18nProperties.getCaption(Captions.contactChangeCase));
										ControllerProvider.getContactController().navigateToData(contactDto.getUuid());
										new Notification(null,I18nProperties.getString(Strings.messageContactCaseChanged),Type.TRAY_NOTIFICATION, false).show(Page.getCurrent());
									}
								});
							}
						});

			});
			removeCaseButton.addClickListener(e -> {
				if (contactDto.getRegion() == null || contactDto.getDistrict() == null) {
					// Ask user to fill in a region and district before removing the source case
					VaadinUiUtil.showSimplePopupWindow(I18nProperties.getString(Strings.headingContactDataNotComplete), I18nProperties.getString(Strings.messageSetContactRegionAndDistrict));
				} else {
					VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingRemoveCaseFromContact),
							new Label(I18nProperties.getString(Strings.confirmationContactSourceCaseDiscardUnsavedChanges)),
							I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 480, confirmed -> {
								if (confirmed) {
									editComponent.discard();
									layout.removeComponent(CASE_LOC);
									((ContactDataForm) editComponent.getWrappedComponent()).setSourceCase(null);
									ContactDto contactToChange = FacadeProvider.getContactFacade()
											.getContactByUuid(getContactRef().getUuid());
									contactToChange.setCaze(null);
									FacadeProvider.getContactFacade().saveContact(contactToChange);
									removeCaseButton.setVisible(false);
									chooseCaseButton
									.setCaption(I18nProperties.getCaption(Captions.contactChooseSourceCase));
									ControllerProvider.getContactController().navigateToData(contactDto.getUuid());
									new Notification(null, I18nProperties.getString(Strings.messageContactCaseRemoved),
											Type.TRAY_NOTIFICATION, false).show(Page.getCurrent());
								}
							});
				}
			});

			layout.addComponent(buttonsLayout, CASE_BUTTONS_LOC);
		}

		TaskListComponent taskList = new TaskListComponent(TaskContext.CONTACT, getContactRef());
		taskList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(taskList, TASKS_LOC);

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)) {
			VerticalLayout sampleLocLayout = new VerticalLayout();
			sampleLocLayout.setMargin(false);
			sampleLocLayout.setSpacing(false);

			SampleListComponent sampleList = new SampleListComponent(getContactRef());
			sampleList.addStyleName(CssStyles.SIDE_COMPONENT);
			sampleLocLayout.addComponent(sampleList);

			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
				sampleLocLayout.addComponent(new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChanges), ContentMode.HTML));
			}

			layout.addComponent(sampleLocLayout, SAMPLES_LOC);

		}

		setContactEditPermission(container);
	}

	private CaseInfoLayout createCaseInfoLayout(String caseUuid) {
		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);
		return caseInfoLayout;
	}

}
