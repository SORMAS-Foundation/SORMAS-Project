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

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.contact.AbstractContactGrid;
import de.symeda.sormas.ui.contact.ContactGrid;
import de.symeda.sormas.ui.contact.importer.CaseContactsImportLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.ContactDownloadUtil;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class CaseContactsView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";
	private static final long serialVersionUID = -1L;
	private final ContactCriteria criteria;
	private final ViewConfiguration viewConfiguration;

	private ContactGrid grid;

	//Filters
	private ComboBox classificationFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox officerFilter;
	private TextField personLikeField;
	private TextField searchField;
	private Button resetButton;
	private Button applyButton;

	private DetailSubComponentWrapper gridLayout;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public CaseContactsView() {

		super(VIEW_NAME, false);
		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		viewConfiguration.setInEagerMode(true);
		criteria = ViewModelProviders.of(CaseContactsView.class).get(ContactCriteria.class);
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setSizeUndefined();

		classificationFilter = ComboBoxHelper.createComboBoxV7();
		classificationFilter.setWidth(240, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
		classificationFilter.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
		classificationFilter.addValueChangeListener(e -> criteria.setContactClassification((ContactClassification) e.getProperty().getValue()));
		topLayout.addComponent(classificationFilter);

		UserDto user = UserProvider.getCurrent().getUser();
		regionFilter = ComboBoxHelper.createComboBoxV7();
		if (user.getRegion() == null) {
			regionFilter.setWidth(240, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.REGION_UUID));
			regionFilter.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.REGION_UUID));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
				if (region != null) {
					officerFilter.addItems(
						FacadeProvider.getUserFacade().getUsersByRegionAndRights(region, criteria.getDisease(), UserRight.CONTACT_RESPONSIBLE));
				} else {
					officerFilter.removeAllItems();
				}
				criteria.region(region);
			});
			topLayout.addComponent(regionFilter);
		}

		districtFilter = ComboBoxHelper.createComboBoxV7();
		districtFilter.setWidth(240, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISTRICT_UUID));
		districtFilter.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISTRICT_UUID));
		districtFilter.addValueChangeListener(e -> criteria.district((DistrictReferenceDto) e.getProperty().getValue()));

		if (user.getRegion() != null && user.getDistrict() == null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else {
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
				districtFilter.removeAllItems();
				if (region != null) {
					districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
					districtFilter.setEnabled(true);
				} else {
					districtFilter.setEnabled(false);
				}
			});
			districtFilter.setEnabled(false);
		}
		topLayout.addComponent(districtFilter);

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoContactsViewRegionDistrictFilter), ContentMode.HTML);
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		topLayout.addComponent(infoLabel);

		officerFilter = ComboBoxHelper.createComboBoxV7();
		officerFilter.setWidth(240, Unit.PIXELS);
		officerFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
		officerFilter.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
		officerFilter.addValueChangeListener(e -> criteria.setContactOfficer((UserReferenceDto) e.getProperty().getValue()));
		if (user.getRegion() != null) {
			officerFilter.addItems(
				FacadeProvider.getUserFacade().getUsersByRegionAndRights(user.getRegion(), criteria.getDisease(), UserRight.CONTACT_RESPONSIBLE));
		}
		topLayout.addComponent(officerFilter);

		searchField = new TextField();
		searchField.setWidth(150, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptContactsSearchField));
		searchField.setDescription(I18nProperties.getString(Strings.promptContactsSearchField));
		searchField.addTextChangeListener(e -> criteria.setContactOrCaseLike(e.getText()));
		topLayout.addComponent(searchField);

		personLikeField = new TextField();
		personLikeField.setWidth(150, Unit.PIXELS);
		personLikeField.setNullRepresentation("");
		personLikeField.setInputPrompt(I18nProperties.getString(Strings.promptRelatedPersonLikeField));
		personLikeField.setDescription(I18nProperties.getString(Strings.promptRelatedPersonLikeField));
		personLikeField.addTextChangeListener(e -> criteria.setPersonLike(e.getText()));
		topLayout.addComponent(personLikeField);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CaseContactsView.class).remove(ContactCriteria.class);
			navigateTo(null);
		});
		resetButton.setVisible(false);
		topLayout.addComponent(resetButton);

		applyButton = ButtonHelper.createButton(Captions.actionApplyFilters, event -> navigateTo(criteria));
		applyButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		applyButton.setVisible(false);
		topLayout.addComponent(applyButton);

		classificationFilter.addValueChangeListener(e -> updateApplyResetButtons());
		regionFilter.addValueChangeListener(e -> updateApplyResetButtons());
		officerFilter.addValueChangeListener(e -> updateApplyResetButtons());
		districtFilter.addValueChangeListener(e -> updateApplyResetButtons());
		searchField.addValueChangeListener(e -> updateApplyResetButtons());
		personLikeField.addValueChangeListener(e -> updateApplyResetButtons());

		return topLayout;
	}

	public HorizontalLayout createStatusFilterBar(boolean isEditAllowed) {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
			criteria.contactStatus(null);
			navigateTo(criteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (ContactStatus status : ContactStatus.values()) {
			Button statusButton = ButtonHelper.createButton(status.toString(), e -> {
				criteria.contactStatus(status);
				navigateTo(criteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			statusButton.setData(status);
			statusButton.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}
		statusFilterLayout.setExpandRatio(statusFilterLayout.getComponent(statusFilterLayout.getComponentCount() - 1), 1);

		if (isEditAllowed) {
			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

				MenuBar bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, selectedItem -> {
						ControllerProvider.getContactController()
							.showBulkContactDataEditComponent(grid.asMultiSelect().getSelectedItems(), getCaseRef().getUuid(), grid);
					}, UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkCancelFollowUp), VaadinIcons.CLOSE, selectedItem -> {
						ControllerProvider.getContactController()
							.cancelFollowUpOfAllSelectedItems(grid.asMultiSelect().getSelectedItems(), getCaseRef().getUuid(), grid);
					}, UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkLostToFollowUp), VaadinIcons.UNLINK, selectedItem -> {
						ControllerProvider.getContactController()
							.setAllSelectedItemsToLostToFollowUp(grid.asMultiSelect().getSelectedItems(), getCaseRef().getUuid(), grid);
					}, UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
						ControllerProvider.getContactController()
							.deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), (AbstractContactGrid<?>) grid);
					}, UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_DELETE)));

				statusFilterLayout.addComponent(bulkOperationsDropdown);
				statusFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
				statusFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_IMPORT)) {
				Button importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
					Window popupWindow = VaadinUiUtil.showPopupWindow(
						new CaseContactsImportLayout(FacadeProvider.getCaseFacade().getCaseDataByUuid(criteria.getCaze().getUuid())));
					popupWindow.setCaption(I18nProperties.getString(Strings.headingImportCaseContacts));
					popupWindow.addCloseListener(c -> {
						grid.reload();
					});
				}, ValoTheme.BUTTON_PRIMARY);

				statusFilterLayout.addComponent(importButton);
				statusFilterLayout.setComponentAlignment(importButton, Alignment.MIDDLE_RIGHT);
				if (!UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
					statusFilterLayout.setExpandRatio(importButton, 1);
				}
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {
				VerticalLayout exportLayout = new VerticalLayout();
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(200, Unit.PIXELS);

				PopupButton exportButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);

				statusFilterLayout.addComponent(exportButton);
				statusFilterLayout.setComponentAlignment(exportButton, Alignment.MIDDLE_RIGHT);
				if (!UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
					statusFilterLayout.setExpandRatio(exportButton, 1);
				}
				StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
					grid,
					() -> viewConfiguration.isInEagerMode() ? this.grid.asMultiSelect().getSelectedItems() : null,
					ExportEntityName.CONTACTS);
				addExportButton(streamResource, exportButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Descriptions.descExportButton);

				StreamResource extendedExportStreamResource =
					ContactDownloadUtil.createContactExportResource(grid.getCriteria(), this::getSelectedRows, null);
				addExportButton(
					extendedExportStreamResource,
					exportButton,
					exportLayout,
					VaadinIcons.FILE_TEXT,
					Captions.exportDetailed,
					Descriptions.descDetailedExportButton);

				Button btnCustomExport = ButtonHelper.createIconButton(Captions.exportCustom, VaadinIcons.FILE_TEXT, e -> {
					ControllerProvider.getCustomExportController().openContactExportWindow(grid.getCriteria(), this::getSelectedRows);
				}, ValoTheme.BUTTON_PRIMARY);
				btnCustomExport.setDescription(I18nProperties.getString(Strings.infoCustomExport));
				btnCustomExport.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(btnCustomExport);

				// Warning if no filters have been selected
				Label warningLabel = new Label(I18nProperties.getString(Strings.infoExportNoFilters));
				warningLabel.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(warningLabel);
				warningLabel.setVisible(false);

				exportButton.addClickListener(e -> warningLabel.setVisible(!criteria.hasAnyFilterActive()));
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE)) {
				final CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(this.getCaseRef().getUuid());
				final ExpandableButton lineListingButton =
					new ExpandableButton(Captions.lineListing).expand(e -> ControllerProvider.getContactController().openLineListingWindow(caseDto));

				statusFilterLayout.addComponent(lineListingButton);

				final Button newButton = ButtonHelper.createIconButtonWithCaption(
					Captions.contactNewContact,
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, Captions.contactNewContact),
					VaadinIcons.PLUS_CIRCLE,
					e -> ControllerProvider.getContactController().create(this.getCaseRef()),
					ValoTheme.BUTTON_PRIMARY);

				statusFilterLayout.addComponent(newButton);
				statusFilterLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
			}
		}

		statusFilterLayout.addStyleName("top-bar");
		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	private Set<String> getSelectedRows() {
		return viewConfiguration.isInEagerMode()
			? this.grid.asMultiSelect().getSelectedItems().stream().map(ContactIndexDto::getUuid).collect(Collectors.toSet())
			: Collections.emptySet();
	}

	@Override
	protected void initView(String params) {

		criteria.caze(getCaseRef());

		if (grid == null) {
			grid = new ContactGrid(criteria, getClass(), ViewConfiguration.class);
			gridLayout = new DetailSubComponentWrapper(() -> null);
			gridLayout.addComponent(createFilterBar());
			gridLayout.addComponent(createStatusFilterBar(isEditAllowed()));
			gridLayout.addComponent(grid);
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.setSizeFull();
			gridLayout.setExpandRatio(grid, 1);

			if (viewConfiguration.isInEagerMode()) {
				grid.setEagerDataProvider();
			}

			grid.addDataSizeChangeListener(e -> updateStatusButtons());

			setSubComponent(gridLayout);
		}

		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();

		grid.reload();
		if (!FacadeProvider.getCaseFacade().hasCurrentUserSpecialAccess(getCaseRef())) {
			setEditPermission(gridLayout);
		}
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getCaseFacade().isEditContactAllowed(getReference().getUuid()).equals(EditPermissionType.ALLOWED);
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();

		classificationFilter.removeAllItems();
		classificationFilter.addItems((Object[]) ContactClassification.values());
		classificationFilter.setValue(criteria.getContactClassification());

		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		personLikeField.setValue(criteria.getPersonLike());
		searchField.setValue(criteria.getContactOrCaseLike());
		officerFilter.setValue(criteria.getContactOfficer());

		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getContactStatus()) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
		}
	}

	private void updateApplyResetButtons() {
		boolean hasFilters = hasFilters();
		resetButton.setVisible(hasFilters);
		applyButton.setVisible(hasFilters);
		if (!hasFilters) {
			navigateTo(null);
		}
	}

	private boolean hasFilters() {
		return !classificationFilter.isEmpty()
			|| !regionFilter.isEmpty()
			|| !districtFilter.isEmpty()
			|| !officerFilter.isEmpty()
			|| !personLikeField.isEmpty()
			|| !searchField.isEmpty();
	}
}
