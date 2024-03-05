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

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.ArchiveHandlers.InfrastructureArchiveHandler;
import de.symeda.sormas.ui.utils.ArchiveMessages;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class InfrastructureController {

	public InfrastructureController() {

	}

	public void createFacility() {
		CommitDiscardWrapperComponent<FacilityEditForm> createComponent = getFacilityEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewFacility));
	}

	public void editFacility(String uuid) {
		FacilityDto facility = FacadeProvider.getFacilityFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<FacilityEditForm> editComponent = getFacilityEditComponent(facility);
		String caption = I18nProperties.getString(Strings.edit) + " " + facility.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createArea() {
		CommitDiscardWrapperComponent<AreaEditForm> createComponent = getAreaEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editArea(String uuid) {
		AreaDto area = FacadeProvider.getAreaFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<AreaEditForm> editComponent = getAreaEditComponent(area);
		String caption = I18nProperties.getString(Strings.edit) + " " + area.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createContinent() {
		CommitDiscardWrapperComponent<ContinentEditForm> createComponent = getContinentEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editContinent(String uuid) {
		ContinentDto continentDto = FacadeProvider.getContinentFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<ContinentEditForm> editComponent = getContinentEditComponent(continentDto);
		String caption = I18nProperties.getString(Strings.headingEditContinent);
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createSubcontinent() {
		CommitDiscardWrapperComponent<SubcontinentEditForm> createComponent = getSubcontinentEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editSubcontinent(String uuid) {
		SubcontinentDto subcontinentDto = FacadeProvider.getSubcontinentFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<SubcontinentEditForm> editComponent = getSubcontinentEditComponent(subcontinentDto);
		String caption = I18nProperties.getString(Strings.headingEditSubcontinent);
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createCountry() {
		CommitDiscardWrapperComponent<CountryEditForm> createComponent = getCountryEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editCountry(String uuid) {
		CountryDto country = FacadeProvider.getCountryFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<CountryEditForm> editComponent = getCountryEditComponent(country);
		String caption = I18nProperties.getString(Strings.headingEditCountry);
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createRegion() {
		CommitDiscardWrapperComponent<RegionEditForm> createComponent = getRegionEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editRegion(String uuid) {
		RegionDto region = FacadeProvider.getRegionFacade().getByUuid(uuid);
		CommitDiscardWrapperComponent<RegionEditForm> editComponent = getRegionEditComponent(region);
		String caption = I18nProperties.getString(Strings.edit) + " " + region.getName();
		VaadinUiUtil.showModalPopupWindow(editComponent, caption);
	}

	public void createDistrict() {
		CommitDiscardWrapperComponent<DistrictEditForm> createComponent = getDistrictEditComponent(null);
		VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateEntry));
	}

	public void editDistrict(String uuid) {
		DistrictDto district = FacadeProvider.getDistrictFacade().getByUuid(uuid);
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

	private CommitDiscardWrapperComponent<FacilityEditForm> getFacilityEditComponent(FacilityDto facility) {

		boolean isNew = facility == null;
		FacilityEditForm editForm = new FacilityEditForm(isNew);
		if (isNew) {
			facility = FacilityDto.build();
		}

		editForm.setValue(facility);

		final CommitDiscardWrapperComponent<FacilityEditForm> editView = new CommitDiscardWrapperComponent<FacilityEditForm>(
			editForm,
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getFacilityFacade().save(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(FacilitiesView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				facility,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getFacilityFacade(), ArchiveMessages.FACILITY),
				() -> SormasUI.get().getNavigator().navigateTo(FacilitiesView.VIEW_NAME));
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<AreaEditForm> getAreaEditComponent(AreaDto area) {
		boolean isNew = area == null;
		AreaEditForm editForm = new AreaEditForm(isNew);
		if (isNew) {
			area = AreaDto.build();
		}

		editForm.setValue(area);

		final CommitDiscardWrapperComponent<AreaEditForm> editComponent = new CommitDiscardWrapperComponent<>(
			editForm,
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editComponent.addCommitListener(() -> {
			FacadeProvider.getAreaFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(AreasView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editComponent,
				area,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getAreaFacade(), ArchiveMessages.AREA),
				() -> SormasUI.get().getNavigator().navigateTo(AreasView.VIEW_NAME));
		}

		return editComponent;
	}

	private CommitDiscardWrapperComponent<ContinentEditForm> getContinentEditComponent(ContinentDto continent) {
		boolean isNew = continent == null;
		ContinentEditForm editForm = new ContinentEditForm(isNew);
		if (isNew) {
			continent = ContinentDto.build();
		}

		editForm.setValue(continent);

		final CommitDiscardWrapperComponent<ContinentEditForm> editView = new CommitDiscardWrapperComponent<>(
			editForm,
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			FacadeProvider.getContinentFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(ContinentsView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				continent,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getContinentFacade(), ArchiveMessages.CONTINENT),
				() -> SormasUI.get().getNavigator().navigateTo(ContinentsView.VIEW_NAME));
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<SubcontinentEditForm> getSubcontinentEditComponent(SubcontinentDto subcontinent) {
		boolean isNew = subcontinent == null;
		SubcontinentEditForm editForm = new SubcontinentEditForm(isNew);
		if (isNew) {
			subcontinent = SubcontinentDto.build();
		}

		editForm.setValue(subcontinent);

		final CommitDiscardWrapperComponent<SubcontinentEditForm> editView = new CommitDiscardWrapperComponent<>(
			editForm,
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			FacadeProvider.getSubcontinentFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(SubcontinentsView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				subcontinent,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getSubcontinentFacade(), ArchiveMessages.SUBCONTINENT),
				() -> SormasUI.get().getNavigator().navigateTo(SubcontinentsView.VIEW_NAME));
		}

		return editView;
	}

	private CommitDiscardWrapperComponent<CountryEditForm> getCountryEditComponent(CountryDto country) {

		boolean isNew = country == null;
		CountryEditForm editForm = new CountryEditForm(isNew);
		if (isNew) {
			country = CountryDto.build();
		}

		editForm.setValue(country);

		final CommitDiscardWrapperComponent<CountryEditForm> editView = new CommitDiscardWrapperComponent<>(
			editForm,
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			FacadeProvider.getCountryFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(CountriesView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				country,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getCountryFacade(), ArchiveMessages.COUNTRY),
				() -> SormasUI.get().getNavigator().navigateTo(CountriesView.VIEW_NAME));
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
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(() -> {
			FacadeProvider.getRegionFacade().save(editForm.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(RegionsView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				region,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getRegionFacade(), ArchiveMessages.REGION),
				() -> SormasUI.get().getNavigator().navigateTo(RegionsView.VIEW_NAME));
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
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getDistrictFacade().save(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(DistrictsView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				district,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getDistrictFacade(), ArchiveMessages.DISTRICT),
				() -> SormasUI.get().getNavigator().navigateTo(DistrictsView.VIEW_NAME));
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
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				FacadeProvider.getCommunityFacade().save(editForm.getValue());
				Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
				SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME);
			}
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				editView,
				community,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getCommunityFacade(), ArchiveMessages.COMMUNITY),
				() -> SormasUI.get().getNavigator().navigateTo(CommunitiesView.VIEW_NAME));
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
			UiUtil.permitted(isNew ? UserRight.INFRASTRUCTURE_CREATE : UserRight.INFRASTRUCTURE_EDIT),
			form.getFieldGroup());
		view.addCommitListener(() -> {
			FacadeProvider.getPointOfEntryFacade().save(form.getValue());
			Notification.show(I18nProperties.getString(Strings.messageEntryCreated), Type.ASSISTIVE_NOTIFICATION);
			SormasUI.get().getNavigator().navigateTo(PointsOfEntryView.VIEW_NAME);
		});

		if (!isNew) {
			extendEditComponentWithArchiveButton(
				view,
				pointOfEntry,
				ArchiveHandlers.forInfrastructure(FacadeProvider.getPointOfEntryFacade(), ArchiveMessages.POINT_OF_ENTRY),
				() -> SormasUI.get().getNavigator().navigateTo(PointsOfEntryView.VIEW_NAME));
		}

		return view;
	}

	private <T extends InfrastructureDto, F extends InfrastructureFacade<T, ?, ?, ?>> void extendEditComponentWithArchiveButton(
		CommitDiscardWrapperComponent<?> component,
		T entity,
		InfrastructureArchiveHandler<T, F> archiveHandler,
		Runnable callback) {

		if (UiUtil.permitted(UserRight.INFRASTRUCTURE_ARCHIVE)) {
			ControllerProvider.getArchiveController().addArchivingButton(entity, archiveHandler, component, callback, true);
		}
	}

	public <T extends HasUuid & Serializable, F extends InfrastructureFacade<?, T, ?, ?>> void archiveOrDearchiveAllSelectedItems(
		boolean archive,
		InfrastructureArchiveHandler<?, F> archiveHandler,
		FilteredGrid<T, ?> grid,
		Runnable reloadGrid,
		Runnable callback) {

		Set<T> selectedRows = grid.asMultiSelect().getSelectedItems();

		if (archive) {
			ControllerProvider.getArchiveController()
				.archiveSelectedItems(selectedRows, archiveHandler, bulkOperationCallback(grid, reloadGrid, callback));
		} else {
			ControllerProvider.getArchiveController()
				.dearchiveSelectedItems(selectedRows, archiveHandler, bulkOperationCallback(grid, reloadGrid, callback));
		}
	}

	private <T> Consumer<List<T>> bulkOperationCallback(FilteredGrid<T, ?> grid, Runnable reloadGrid, Runnable noEntriesRemainingCallback) {
		return remaining -> {
			reloadGrid.run();
			if (CollectionUtils.isNotEmpty(remaining)) {
				grid.asMultiSelect().selectItems((T[]) remaining.toArray());
			} else {
				noEntriesRemainingCallback.run();
			}
		};
	}
}
