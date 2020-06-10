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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictIndexDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class InfrastructureController {

	public InfrastructureController() {

	}

	public void createHealthFacility(boolean laboratory) {
		CommitDiscardWrapperComponent<FacilityEditForm> createComponent = getFacilityEditComponent(null, laboratory);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editHealthFacility(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityEditForm> editComponent =
			getFacilityEditComponent(facility, facility.getType() == FacilityType.LABORATORY);
		String caption = I18nProperties.getString(Strings.edit) + " " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createRegion() {
		CommitDiscardWrapperComponent<RegionEditForm> createComponent = getRegionEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editRegion(String uuid) {
		RegionDto region = FacadeProvider.getRegionFacade().getRegionByUuid(uuid);
		CommitDiscardWrapperComponent<RegionEditForm> editComponent = getRegionEditComponent(region);
		String caption = I18nProperties.getString(Strings.edit) + " " + region.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createDistrict() {
		CommitDiscardWrapperComponent<DistrictEditForm> createComponent = getDistrictEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editDistrict(String uuid) {
		DistrictDto district = FacadeProvider.getDistrictFacade().getDistrictByUuid(uuid);
		CommitDiscardWrapperComponent<DistrictEditForm> editComponent = getDistrictEditComponent(district);
		String caption = I18nProperties.getString(Strings.edit) + " " + district.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createCommunity() {
		CommitDiscardWrapperComponent<CommunityEditForm> createComponent = getCommunityEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editCommunity(String uuid) {
		CommunityDto community = FacadeProvider.getCommunityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<CommunityEditForm> editComponent = getCommunityEditComponent(community);
		String caption = I18nProperties.getString(Strings.edit) + " " + community.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createPointOfEntry() {
		CommitDiscardWrapperComponent<PointOfEntryForm> component = getPointOfEntryEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editPointOfEntry(String uuid) {

		PointOfEntryDto pointOfEntry = FacadeProvider.getPointOfEntryFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<PointOfEntryForm> component = getPointOfEntryEditComponent(pointOfEntry);
		String caption = I18nProperties.getString(Strings.edit) + " " + pointOfEntry.getName();
		VaadinUiUtil.showModalPopupWindow(component, caption);
	}

	private CommitDiscardWrapperComponent<FacilityEditForm> getFacilityEditComponent(FacilityDto facility, boolean laboratory) {

		boolean isNew = facility == null;
		FacilityEditForm editForm = new FacilityEditForm(isNew, laboratory);
		if (isNew) {
			facility = FacilityDto.build();
			if (laboratory) {
				facility.setType(FacilityType.LABORATORY);
			}
		}

		editForm.setValue(facility);

		final CommitDiscardWrapperComponent<FacilityEditForm> editView = new CommitDiscardWrapperComponent<FacilityEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getFacilityFacade().saveFacility(editForm.getValue());
				if (laboratory) {
					Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(LaboratoriesView.VIEW_NAME);
				} else {
					Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
					SormasUI.get().getNavigator().navigateTo(HealthFacilitiesView.VIEW_NAME);
				}
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				facility.isArchived(),
				facility.getUuid(),
				InfrastructureType.FACILITY,
				facility.getType());
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<RegionEditForm> getRegionEditComponent(RegionDto region) {

		boolean isNew = region == null;
		RegionEditForm editForm = new RegionEditForm(isNew);
		if (isNew) {
			region = RegionDto.build();
		}

		editForm.setValue(region);

		final CommitDiscardWrapperComponent<RegionEditForm> editView = new CommitDiscardWrapperComponent<RegionEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getRegionFacade().saveRegion(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(RegionsView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(editView, region.isArchived(), region.getUuid(), InfrastructureType.REGION, null);
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<DistrictEditForm> getDistrictEditComponent(DistrictDto district) {

		boolean isNew = district == null;
		DistrictEditForm editForm = new DistrictEditForm(isNew);
		if (isNew) {
			district = DistrictDto.build();
		}

		editForm.setValue(district);

		final CommitDiscardWrapperComponent<DistrictEditForm> editView = new CommitDiscardWrapperComponent<DistrictEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getDistrictFacade().saveDistrict(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(DistrictsView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(editView, district.isArchived(), district.getUuid(), InfrastructureType.DISTRICT, null);
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<CommunityEditForm> getCommunityEditComponent(CommunityDto community) {

		boolean isNew = community == null;
		CommunityEditForm editForm = new CommunityEditForm(isNew);
		if (isNew) {
			community = CommunityDto.build();
		}

		editForm.setValue(community);

		final CommitDiscardWrapperComponent<CommunityEditForm> editView = new CommitDiscardWrapperComponent<CommunityEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getCommunityFacade().saveCommunity(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(editView, community.isArchived(), community.getUuid(), InfrastructureType.COMMUNITY, null);
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<PointOfEntryForm> getPointOfEntryEditComponent(PointOfEntryDto pointOfEntry) {

		boolean isNew = pointOfEntry == null;
		PointOfEntryForm form = new PointOfEntryForm(isNew);
		if (isNew) {
			pointOfEntry = PointOfEntryDto.build();
		}

		form.setValue(pointOfEntry);

		final CommitDiscardWrapperComponent<PointOfEntryForm> view = new CommitDiscardWrapperComponent<PointOfEntryForm>(
			form,
			UserProvider.getCurrent().hasUserRight(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			form.getFieldGroup());
		view.addCommitListener(() -> {
			FacadeProvider.getPointOfEntryFacade().save(form.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(PointsOfEntryView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(view, pointOfEntry.isArchived(), pointOfEntry.getUuid(), InfrastructureType.POINT_OF_ENTRY, null);
		}

		return view;
	}

	private void extendEditComponentWithArchiveButton(
		CommitDiscardWrapperComponent<?> component,
		boolean isArchived,
		String uuid,
		InfrastructureType infrastructureType,
		FacilityType facilityType) {

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_ARCHIVE)) {
			Button archiveButton = ButtonHelper.createButton(isArchived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				if (!isArchived) {
					if (InfrastructureType.REGION.equals(infrastructureType)
						&& FacadeProvider.getRegionFacade().isUsedInOtherInfrastructureData(Arrays.asList(uuid))
						|| InfrastructureType.DISTRICT.equals(infrastructureType)
							&& FacadeProvider.getDistrictFacade().isUsedInOtherInfrastructureData(Arrays.asList(uuid))
						|| InfrastructureType.COMMUNITY.equals(infrastructureType)
							&& FacadeProvider.getCommunityFacade().isUsedInOtherInfrastructureData(Arrays.asList(uuid))) {
						showArchivingNotPossibleWindow(infrastructureType, false);
						return;
					}
				} else {
					if (InfrastructureType.DISTRICT.equals(infrastructureType)
						&& FacadeProvider.getDistrictFacade().hasArchivedParentInfrastructure(Arrays.asList(uuid))
						|| InfrastructureType.COMMUNITY.equals(infrastructureType)
							&& FacadeProvider.getCommunityFacade().hasArchivedParentInfrastructure(Arrays.asList(uuid))
						|| InfrastructureType.FACILITY.equals(infrastructureType)
							&& FacadeProvider.getFacilityFacade().hasArchivedParentInfrastructure(Arrays.asList(uuid))
						|| InfrastructureType.POINT_OF_ENTRY.equals(infrastructureType)
							&& FacadeProvider.getPointOfEntryFacade().hasArchivedParentInfrastructure(Arrays.asList(uuid))) {
						showDearchivingNotPossibleWindow(infrastructureType, facilityType, false);
						return;
					}
				}

				component.commit();
				archiveOrDearchiveInfrastructure(!isArchived, uuid, infrastructureType, facilityType);
			}, ValoTheme.BUTTON_LINK);

			component.getButtonsPanel().addComponentAsFirst(archiveButton);
			component.getButtonsPanel().setComponentAlignment(archiveButton, Alignment.BOTTOM_LEFT);
		}
	}

	private void showArchivingNotPossibleWindow(InfrastructureType infrastructureType, boolean bulkArchiving) {

		final String contentText;
		switch (infrastructureType) {
		case REGION:
			contentText =
				I18nProperties.getString(bulkArchiving ? Strings.messageRegionsArchivingNotPossible : Strings.messageRegionArchivingNotPossible);
			break;
		case DISTRICT:
			contentText =
				I18nProperties.getString(bulkArchiving ? Strings.messageDistrictsArchivingNotPossible : Strings.messageDistrictArchivingNotPossible);
			break;
		case COMMUNITY:
			contentText = I18nProperties
				.getString(bulkArchiving ? Strings.messageCommunitiesArchivingNotPossible : Strings.messageCommunityArchivingNotPossible);
			break;
		default:
			throw new IllegalArgumentException(infrastructureType.name());
		}
		VaadinUiUtil.showSimplePopupWindow(I18nProperties.getString(Strings.headingArchivingNotPossible), contentText);
	}

	private void showDearchivingNotPossibleWindow(InfrastructureType infrastructureType, FacilityType facilityType, boolean bulkArchiving) {

		final String contentText;
		switch (infrastructureType) {
		case DISTRICT:
			contentText = I18nProperties
				.getString(bulkArchiving ? Strings.messageDistrictsDearchivingNotPossible : Strings.messageDistrictDearchivingNotPossible);
			break;
		case COMMUNITY:
			contentText = I18nProperties
				.getString(bulkArchiving ? Strings.messageCommunitiesDearchivingNotPossible : Strings.messageCommunityDearchivingNotPossible);
			break;
		case FACILITY:
			if (FacilityType.LABORATORY.equals(facilityType)) {
				contentText = I18nProperties
					.getString(bulkArchiving ? Strings.messageLaboratoriesDearchivingNotPossible : Strings.messageLaboratoryDearchivingNotPossible);
			} else {
				contentText = I18nProperties
					.getString(bulkArchiving ? Strings.messageFacilitiesDearchivingNotPossible : Strings.messageFacilityDearchivingNotPossible);
			}
			break;
		case POINT_OF_ENTRY:
			contentText = I18nProperties
				.getString(bulkArchiving ? Strings.messagePointsOfEntryDearchivingNotPossible : Strings.messagePointOfEntryDearchivingNotPossible);
			break;
		default:
			throw new IllegalArgumentException(infrastructureType.name());
		}
		VaadinUiUtil.showSimplePopupWindow(I18nProperties.getString(Strings.headingDearchivingNotPossible), contentText);
	}

	private void archiveOrDearchiveInfrastructure(
		boolean archive,
		String entityUuid,
		InfrastructureType infrastructureType,
		FacilityType facilityType) {

		Label contentLabel = new Label();
		final String notificationMessage;
		switch (infrastructureType) {
		case REGION:
			contentLabel.setValue(I18nProperties.getString(archive ? Strings.confirmationArchiveRegion : Strings.confirmationDearchiveRegion));
			notificationMessage = I18nProperties.getString(archive ? Strings.messageRegionArchived : Strings.messageRegionDearchived);
			break;
		case DISTRICT:
			contentLabel.setValue(I18nProperties.getString(archive ? Strings.confirmationArchiveDistrict : Strings.confirmationDearchiveDistrict));
			notificationMessage = I18nProperties.getString(archive ? Strings.messageDistrictArchived : Strings.messageDistrictDearchived);
			break;
		case COMMUNITY:
			contentLabel.setValue(I18nProperties.getString(archive ? Strings.confirmationArchiveCommunity : Strings.confirmationDearchiveCommunity));
			notificationMessage = I18nProperties.getString(archive ? Strings.messageCommunityArchived : Strings.messageCommunityDearchived);
			break;
		case FACILITY:
			if (FacilityType.LABORATORY.equals(facilityType)) {
				contentLabel
					.setValue(I18nProperties.getString(archive ? Strings.confirmationArchiveLaboratory : Strings.confirmationDearchiveLaboratory));
				notificationMessage = I18nProperties.getString(archive ? Strings.messageLaboratoryArchived : Strings.messageLaboratoryDearchived);
			} else {
				contentLabel
					.setValue(I18nProperties.getString(archive ? Strings.confirmationArchiveFacility : Strings.confirmationDearchiveFacility));
				notificationMessage = I18nProperties.getString(archive ? Strings.messageFacilityArchived : Strings.messageFacilityDearchived);
			}
			break;
		case POINT_OF_ENTRY:
			contentLabel
				.setValue(I18nProperties.getString(archive ? Strings.confirmationArchivePointOfEntry : Strings.confirmationDearchivePointOfEntry));
			notificationMessage = I18nProperties.getString(archive ? Strings.messagePointOfEntryArchived : Strings.messagePointOfEntryDearchived);
			break;
		default:
			throw new IllegalArgumentException(infrastructureType.name());
		}

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(archive ? Strings.headingConfirmArchiving : Strings.headingConfirmDearchiving),
			contentLabel,
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			e -> {
				if (e.booleanValue()) {
					switch (infrastructureType) {
					case REGION:
						if (archive) {
							FacadeProvider.getRegionFacade().archive(entityUuid);
						} else {
							FacadeProvider.getRegionFacade().dearchive(entityUuid);
						}
						SormasUI.get().getNavigator().navigateTo(RegionsView.VIEW_NAME);
						break;
					case DISTRICT:
						if (archive) {
							FacadeProvider.getDistrictFacade().archive(entityUuid);
						} else {
							FacadeProvider.getDistrictFacade().dearchive(entityUuid);
						}
						SormasUI.get().getNavigator().navigateTo(DistrictsView.VIEW_NAME);
						break;
					case COMMUNITY:
						if (archive) {
							FacadeProvider.getCommunityFacade().archive(entityUuid);
						} else {
							FacadeProvider.getCommunityFacade().dearchive(entityUuid);
						}
						SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME);
						break;
					case FACILITY:
						if (archive) {
							FacadeProvider.getFacilityFacade().archive(entityUuid);
						} else {
							FacadeProvider.getFacilityFacade().dearchive(entityUuid);
						}
						if (FacilityType.LABORATORY.equals(facilityType)) {
							SormasUI.get().getNavigator().navigateTo(LaboratoriesView.VIEW_NAME);
						} else {
							SormasUI.get().getNavigator().navigateTo(HealthFacilitiesView.VIEW_NAME);
						}
						break;
					case POINT_OF_ENTRY:
						if (archive) {
							FacadeProvider.getPointOfEntryFacade().archive(entityUuid);
						} else {
							FacadeProvider.getPointOfEntryFacade().dearchive(entityUuid);
						}
						SormasUI.get().getNavigator().navigateTo(PointsOfEntryView.VIEW_NAME);
						break;
					default:
						throw new IllegalArgumentException(infrastructureType.name());
					}

					Notification.show(notificationMessage, Type.ASSISTIVE_NOTIFICATION);
				}
			});
	}

	@SuppressWarnings("unchecked")
	public void archiveOrDearchiveAllSelectedItems(
		boolean archive,
		Collection<?> selectedRows,
		InfrastructureType infrastructureType,
		FacilityType facilityType,
		Runnable callback) {

		// Check that at least one entry is selected
		if (selectedRows.isEmpty()) {
			new Notification(
				I18nProperties.getString(Strings.headingNoRowsSelected),
				I18nProperties.getString(Strings.messageNoRowsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Check if archiving/dearchiving is allowed concerning the hierarchy
		if (InfrastructureType.REGION.equals(infrastructureType)
			&& FacadeProvider.getRegionFacade()
				.isUsedInOtherInfrastructureData(selectedRows.stream().map(row -> ((RegionIndexDto) row).getUuid()).collect(Collectors.toSet()))
			|| InfrastructureType.DISTRICT.equals(infrastructureType)
				&& FacadeProvider.getDistrictFacade()
					.isUsedInOtherInfrastructureData(selectedRows.stream().map(row -> ((DistrictIndexDto) row).getUuid()).collect(Collectors.toSet()))
			|| InfrastructureType.COMMUNITY.equals(infrastructureType)
				&& FacadeProvider.getCommunityFacade()
					.isUsedInOtherInfrastructureData(selectedRows.stream().map(row -> ((CommunityDto) row).getUuid()).collect(Collectors.toSet()))) {
			showArchivingNotPossibleWindow(infrastructureType, true);
			return;
		}
		if (InfrastructureType.DISTRICT.equals(infrastructureType)
			&& FacadeProvider.getDistrictFacade()
				.hasArchivedParentInfrastructure(selectedRows.stream().map(row -> ((DistrictIndexDto) row).getUuid()).collect(Collectors.toSet()))
			|| InfrastructureType.COMMUNITY.equals(infrastructureType)
				&& FacadeProvider.getCommunityFacade()
					.hasArchivedParentInfrastructure(selectedRows.stream().map(row -> ((CommunityDto) row).getUuid()).collect(Collectors.toSet()))
			|| InfrastructureType.FACILITY.equals(infrastructureType)
				&& FacadeProvider.getFacilityFacade()
					.hasArchivedParentInfrastructure(selectedRows.stream().map(row -> ((FacilityDto) row).getUuid()).collect(Collectors.toSet()))
			|| InfrastructureType.POINT_OF_ENTRY.equals(infrastructureType)
				&& FacadeProvider.getPointOfEntryFacade()
					.hasArchivedParentInfrastructure(
						selectedRows.stream().map(row -> ((PointOfEntryDto) row).getUuid()).collect(Collectors.toSet()))) {
			showDearchivingNotPossibleWindow(infrastructureType, facilityType, false);
			return;
		}

		final String confirmationMessage;
		final String notificationMessage;
		switch (infrastructureType) {
		case REGION:
			confirmationMessage = archive
				? I18nProperties.getString(Strings.confirmationArchiveRegions)
				: I18nProperties.getString(Strings.confirmationDearchiveRegions);
			notificationMessage =
				archive ? I18nProperties.getString(Strings.messageRegionsArchived) : I18nProperties.getString(Strings.messageRegionsDearchived);
			break;
		case DISTRICT:
			confirmationMessage = archive
				? I18nProperties.getString(Strings.confirmationArchiveDistricts)
				: I18nProperties.getString(Strings.confirmationDearchiveDistricts);
			notificationMessage =
				archive ? I18nProperties.getString(Strings.messageDistrictsArchived) : I18nProperties.getString(Strings.messageDistrictsDearchived);
			break;
		case COMMUNITY:
			confirmationMessage = archive
				? I18nProperties.getString(Strings.confirmationArchiveCommunities)
				: I18nProperties.getString(Strings.confirmationDearchiveCommunities);
			notificationMessage = archive
				? I18nProperties.getString(Strings.messageCommunitiesArchived)
				: I18nProperties.getString(Strings.messageCommunitiesDearchived);
			break;
		case FACILITY:
			if (FacilityType.LABORATORY.equals(facilityType)) {
				confirmationMessage = archive
					? I18nProperties.getString(Strings.confirmationArchiveLaboratories)
					: I18nProperties.getString(Strings.confirmationDearchiveLaboratories);
				notificationMessage = archive
					? I18nProperties.getString(Strings.messageLaboratoriesArchived)
					: I18nProperties.getString(Strings.messageLaboratoriesDearchived);
			} else {
				confirmationMessage = archive
					? I18nProperties.getString(Strings.confirmationArchiveFacilities)
					: I18nProperties.getString(Strings.confirmationDearchiveFacilities);
				notificationMessage = archive
					? I18nProperties.getString(Strings.messageFacilitiesArchived)
					: I18nProperties.getString(Strings.messageFacilitiesDearchived);
			}
			break;
		case POINT_OF_ENTRY:
			confirmationMessage = archive
				? I18nProperties.getString(Strings.confirmationArchivePointsOfEntry)
				: I18nProperties.getString(Strings.confirmationDearchivePointsOfEntry);
			notificationMessage = archive
				? I18nProperties.getString(Strings.messagePointsOfEntryArchived)
				: I18nProperties.getString(Strings.messagePointsOfEntryDearchived);
			break;
		default:
			throw new IllegalArgumentException(infrastructureType.name());
		}

		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingConfirmArchiving),
			new Label(String.format(confirmationMessage, selectedRows.size())),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			null,
			e -> {
				if (e.booleanValue()) {

					switch (infrastructureType) {
					case REGION:
						for (RegionIndexDto selectedRow : (Collection<RegionIndexDto>) selectedRows) {
							if (archive) {
								FacadeProvider.getRegionFacade().archive(selectedRow.getUuid());
							} else {
								FacadeProvider.getRegionFacade().dearchive(selectedRow.getUuid());
							}
						}
						break;
					case DISTRICT:
						for (DistrictIndexDto selectedRow : (Collection<DistrictIndexDto>) selectedRows) {
							if (archive) {
								FacadeProvider.getDistrictFacade().archive(selectedRow.getUuid());
							} else {
								FacadeProvider.getDistrictFacade().dearchive(selectedRow.getUuid());
							}
						}
						break;
					case COMMUNITY:
						for (CommunityDto selectedRow : (Collection<CommunityDto>) selectedRows) {
							if (archive) {
								FacadeProvider.getCommunityFacade().archive(selectedRow.getUuid());
							} else {
								FacadeProvider.getCommunityFacade().dearchive(selectedRow.getUuid());
							}
						}
						break;
					case FACILITY:
						for (FacilityDto selectedRow : (Collection<FacilityDto>) selectedRows) {
							if (archive) {
								FacadeProvider.getFacilityFacade().archive(selectedRow.getUuid());
							} else {
								FacadeProvider.getFacilityFacade().dearchive(selectedRow.getUuid());
							}
						}
						break;
					case POINT_OF_ENTRY:
						for (PointOfEntryDto selectedRow : (Collection<PointOfEntryDto>) selectedRows) {
							if (archive) {
								FacadeProvider.getPointOfEntryFacade().archive(selectedRow.getUuid());
							} else {
								FacadeProvider.getPointOfEntryFacade().dearchive(selectedRow.getUuid());
							}
						}
						break;
					default:
						throw new IllegalArgumentException(infrastructureType.name());
					}

					callback.run();
					Notification.show(notificationMessage, Type.ASSISTIVE_NOTIFICATION);
				}
			});
	}
}
