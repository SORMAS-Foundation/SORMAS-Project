/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.events.EventDataView;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class EnvironmentController {

	public void registerViews(Navigator navigator) {
		navigator.addView(EnvironmentsView.VIEW_NAME, EnvironmentsView.class);
		navigator.addView(EnvironmentDataView.VIEW_NAME, EnvironmentDataView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<EnvironmentCreateForm> environmentCreateComponent = getEnvironmentCreateComponent();
		if (environmentCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(environmentCreateComponent, I18nProperties.getString(Strings.headingCreateNewEnvironment));
		}

	}

	public void create(EventDto eventDto) {
		CommitDiscardWrapperComponent<EnvironmentCreateForm> environmentCreateComponent = getEnvironmentCreateComponent(eventDto);
		if (environmentCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(environmentCreateComponent, I18nProperties.getString(Strings.headingCreateNewEnvironment));
		}
	}

	public void selectOrCreateEnvironment(EventDto eventDto) {
		EnvironmentSelectionField selectionField = new EnvironmentSelectionField();
		selectionField.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EnvironmentSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EnvironmentIndexDto selectedIndexEnvironment = selectionField.getValue();
			if (selectedIndexEnvironment != null) {
				EnvironmentCriteria criteria = new EnvironmentCriteria();
				criteria.setEvent(eventDto.toReference());
				List<EnvironmentIndexDto> eventEnvironments = FacadeProvider.getEnvironmentFacade().getEnvironmentsByEvent(criteria);
				if (!eventEnvironments.contains(selectedIndexEnvironment)) {
					EnvironmentDto selectedEnvironment =
						FacadeProvider.getEnvironmentFacade().getEnvironmentByUuid(selectedIndexEnvironment.getUuid());
					selectedEnvironment.addEventReference(eventDto.toReference());
					FacadeProvider.getEnvironmentFacade().save(selectedEnvironment);
					if (eventDto != null) {
						String page = EventDataView.VIEW_NAME + "/" + eventDto.getUuid();
						pageNavigate(false, page);
					} else {
						navigateToData(EventDataView.VIEW_NAME);
					}
					Notification.show(I18nProperties.getString(Strings.messageEnvironmentLinkedToEvent), Notification.Type.TRAY_NOTIFICATION);
				} else {
					Notification notification = new Notification(
						I18nProperties.getString(Strings.messageEnvironmentAlreadyLinkedToEvent),
						"",
						Notification.Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.show(Page.getCurrent());
				}

			} else {
				create(eventDto);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEnvironment));
	}

	public void navigateToData(String eventUuid) {
		navigateToData(eventUuid, false);
	}

	public void navigateToData(String eventUuid, boolean openTab) {

		String navigationState = EnvironmentDataView.VIEW_NAME + "/" + eventUuid;
		pageNavigate(openTab, navigationState);
	}

	private void pageNavigate(boolean openTab, String navigationState) {
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

	public CommitDiscardWrapperComponent<EnvironmentCreateForm> getEnvironmentCreateComponent() {
		UserProvider curentUser = UiUtil.getCurrentUserProvider();

		if (curentUser != null) {
			EnvironmentCreateForm createForm;
			createForm = new EnvironmentCreateForm();
			final EnvironmentDto environment = EnvironmentDto.build(curentUser.getUser());
			createForm.setValue(environment);
			final CommitDiscardWrapperComponent<EnvironmentCreateForm> editView =
				new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.ENVIRONMENT_CREATE), createForm.getFieldGroup());

			editView.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					EnvironmentDto dto = createForm.getValue();
					FacadeProvider.getEnvironmentFacade().save(dto);
					Notification.show(I18nProperties.getString(Strings.messageEnvironmentCreated), Notification.Type.WARNING_MESSAGE);

					navigateToEnvironment(dto.getUuid());
				}
			});
			return editView;
		}

		return null;

	}

	public CommitDiscardWrapperComponent<EnvironmentCreateForm> getEnvironmentCreateComponent(EventDto eventDto) {
		UserProvider curentUser = UiUtil.getCurrentUserProvider();

		if (curentUser != null) {
			EnvironmentCreateForm createForm;
			createForm = new EnvironmentCreateForm();
			final EnvironmentDto environment = EnvironmentDto.build(curentUser.getUser());
			environment.addEventReference(eventDto.toReference());
			createForm.setValue(environment);
			final CommitDiscardWrapperComponent<EnvironmentCreateForm> editView =
				new CommitDiscardWrapperComponent<>(createForm, UiUtil.permitted(UserRight.ENVIRONMENT_CREATE), createForm.getFieldGroup());
			editView.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					EnvironmentDto environmentDto = createForm.getValue();
					environmentDto.addEventReference(eventDto.toReference());
					FacadeProvider.getEnvironmentFacade().save(environmentDto);
					Notification.show(I18nProperties.getString(Strings.messageEnvironmentCreated), Notification.Type.WARNING_MESSAGE);
					navigateToEnvironment(environmentDto.getUuid());
				}
			});
			return editView;
		}
		return null;

	}

	public void navigateToEnvironment(String uuid) {
		navigateToView(EnvironmentDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	private static void saveEnvironment(EnvironmentDto environment) {
		FacadeProvider.getEnvironmentFacade().save(environment);

		Notification.show(I18nProperties.getString(Strings.messageEnvironmentSaved), Notification.Type.WARNING_MESSAGE);
		SormasUI.refreshView();
	}

	public CommitDiscardWrapperComponent<EnvironmentDataForm> getEnvironmentDataEditComponent(
		String environmentUuid,
		UserRight editUserRight,
		boolean isEditAllowed) {

		EnvironmentDto environmentDto = FacadeProvider.getEnvironmentFacade().getEnvironmentByUuid(environmentUuid);
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getEnvironmentFacade().getAutomaticDeletionInfo(environmentUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getEnvironmentFacade().getManuallyDeletionInfo(environmentUuid);

		EnvironmentDataForm environmentDataForm = new EnvironmentDataForm(
			environmentDto.isPseudonymized(),
			environmentDto.isInJurisdiction(),
			UiUtil.permitted(isEditAllowed, editUserRight));
		environmentDataForm.setValue(environmentDto);

		CommitDiscardWrapperComponent<EnvironmentDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(environmentDataForm, true, environmentDataForm.getFieldGroup());

		editComponent.addCommitListener(() -> {
			if (!environmentDataForm.getFieldGroup().isModified()) {
				EnvironmentDto dto = environmentDataForm.getValue();

				final UserDto user = UiUtil.getUser();
				final RegionReferenceDto userRegion = user.getRegion();
				final DistrictReferenceDto userDistrict = user.getDistrict();
				final RegionReferenceDto environmentRegion = dto.getLocation().getRegion();
				final DistrictReferenceDto environmentDistrict = dto.getLocation().getDistrict();
				final boolean outsideJurisdiction = (!DataHelper.isSame(dto.getReportingUser(), user)
					&& (userRegion != null && !DataHelper.isSame(userRegion, environmentRegion)
						|| userDistrict != null && !DataHelper.isSame(userDistrict, environmentDistrict)));

				if (outsideJurisdiction) {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingEnvironmentJurisdictionUpdated),
						new Label(I18nProperties.getString(Strings.messageEnvironmentJurisdictionUpdated)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						500,
						confirmed -> {
							if (Boolean.TRUE.equals(confirmed)) {
								saveEnvironment(dto);
							}
						});
				} else {
					saveEnvironment(dto);
				}

			}
		});

		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, environmentDto.isDeleted(), EnvironmentDto.I18N_PREFIX));

		if (environmentDto.isDeleted()) {
			editComponent.getWrappedComponent().getField(EnvironmentDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(EnvironmentDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(EnvironmentDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		if (UiUtil.getUserRoles().stream().anyMatch(userRoleDto -> !userRoleDto.isRestrictAccessToAssignedEntities())
			|| DataHelper.equal(environmentDto.getResponsibleUser(), UiUtil.getUserReference())) {
			// Initialize 'Delete' button
			if (UiUtil.permitted(UserRight.ENVIRONMENT_DELETE)) {
				editComponent.addDeleteWithReasonOrRestoreListener(
					EnvironmentsView.VIEW_NAME,
					null,
					I18nProperties.getString(Strings.entityEnvironment),
					environmentDto.getUuid(),
					FacadeProvider.getEnvironmentFacade());
			}

			// Initialize 'Archive' button
			if (UiUtil.permitted(UserRight.ENVIRONMENT_ARCHIVE)) {
				ControllerProvider.getArchiveController()
					.addArchivingButton(
						environmentDto,
						ArchiveHandlers.forEnvironment(),
						editComponent,
						() -> navigateToEnvironment(environmentDto.getUuid()));
			}
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.ENVIRONMENT_EDIT,
			null,
			UserRight.ENVIRONMENT_DELETE,
			UserRight.ENVIRONMENT_ARCHIVE,
			FacadeProvider.getEnvironmentFacade().getEditPermissionType(environmentDto.getUuid()),
			true);

		return editComponent;
	}

	public TitleLayout getEnvironmentViewTitleLayout(String uuid) {
		EnvironmentDto environmentDto = findEnvironment(uuid);

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(environmentDto.getUuid());

		StringBuilder mainRowText = new StringBuilder(environmentDto.getEnvironmentName());
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);

		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private EnvironmentDto findEnvironment(String uuid) {
		return FacadeProvider.getEnvironmentFacade().getEnvironmentByUuid(uuid);
	}

	public void unlinkEnvironment(EnvironmentIndexDto environmentIndex, String eventUuid) {
		FacadeProvider.getEnvironmentFacade().unlinkEnvironment(environmentIndex, eventUuid);
		Notification.show(I18nProperties.getString(Strings.messageEventUnlinkedFromEnvironment), Notification.Type.TRAY_NOTIFICATION);
	}
}
