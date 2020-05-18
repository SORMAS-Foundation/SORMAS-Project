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
package de.symeda.sormas.ui.samples;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.*;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;

import java.util.HashMap;
import de.symeda.sormas.api.sample.SampleCriteria;

@SuppressWarnings("serial")
public class SampleGridComponent extends VerticalLayout {

	private static final String NOT_SHIPPED = "notShipped";
	private static final String SHIPPED = "shipped";
	private static final String RECEIVED = "received";
	private static final String REFERRED = "referred";

	private SampleCriteria criteria;

	private SampleGrid grid;
	private SamplesView samplesView;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filter
	private SampleGridFilterForm filterForm;
	MenuBar bulkOperationsDropdown;
	private ComboBox relevanceStatusFilter;
	private ComboBox sampleTypeFilter;

	private VerticalLayout gridLayout;

	private Label viewTitleLabel;
	private String originalViewTitle;

	public SampleGridComponent(Label viewTitleLabel, SamplesView samplesView) {
		setSizeFull();
		setMargin(false);

		this.viewTitleLabel = viewTitleLabel;
		this.samplesView = samplesView;
		originalViewTitle = viewTitleLabel.getValue();

		criteria = ViewModelProviders.of(SamplesView.class).get(SampleCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}
		if (criteria.getSampleSearchType() == null) {
			criteria.sampleSearchType(SampleSearchType.ALL);
		}
		grid = new SampleGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createShipmentFilterBar());
		gridLayout.addComponent(grid);
		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

		styleGridLayout(gridLayout);
		gridLayout.setMargin(true);

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		filterForm = new SampleGridFilterForm();
		filterForm.addValueChangeListener(e -> {
			if(!samplesView.navigateTo(criteria, false)){
				filterForm.updateResetButtonState();
				grid.reload();

				//open sample if it's the only one
				if (grid.getItemCount() == 1) {
					ControllerProvider.getSampleController().navigateToData(grid.getFirstItem().getUuid());
					Notification.show(I18nProperties.getString(Strings.messageSampleOpened) + " \"" + criteria.getCaseCodeIdLike() + "\"", Type.WARNING_MESSAGE);
				}
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(SamplesView.class).remove(SampleCriteria.class);
			samplesView.navigateTo(null, true);
		});

		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createShipmentFilterBar() {
		HorizontalLayout shipmentFilterLayout = new HorizontalLayout();
		shipmentFilterLayout.setMargin(false);
		shipmentFilterLayout.setSpacing(true);
		shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);
		shipmentFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		buttonFilterLayout.setSpacing(true);
		{
			Button statusAll = ButtonHelper.createButton(Captions.all, e -> processStatusChange(null), ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);

			buttonFilterLayout.addComponent(statusAll);

			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			createAndAddStatusButton(Captions.sampleNotShipped, NOT_SHIPPED, buttonFilterLayout);
			createAndAddStatusButton(Captions.sampleShipped, SHIPPED, buttonFilterLayout);
			createAndAddStatusButton(Captions.sampleReceived, RECEIVED, buttonFilterLayout);
			createAndAddStatusButton(Captions.sampleReferred, REFERRED, buttonFilterLayout);
		}

		shipmentFilterLayout.addComponent(buttonFilterLayout);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW_ARCHIVED)) {
				relevanceStatusFilter = new ComboBox();
				relevanceStatusFilter.setId("relevanceStatusFilter");
				relevanceStatusFilter.setWidth(140, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.sampleActiveSamples));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.sampleArchivedSamples));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.sampleAllSamples));
				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					samplesView.navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);

				bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
							ControllerProvider.getSampleController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), new Runnable() {
								public void run() {
									samplesView.navigateTo(criteria);
								}
							});
						})
				);

				bulkOperationsDropdown.setVisible(samplesView.getViewConfiguration().isInEagerMode());

				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}

			sampleTypeFilter = new ComboBox();
			sampleTypeFilter.setWidth(140, Unit.PERCENTAGE);
			sampleTypeFilter.setId("sampleTypeFilter");
			sampleTypeFilter.setNullSelectionAllowed(false);
			sampleTypeFilter.addItems((Object[]) SampleSearchType.values());
			sampleTypeFilter.setItemCaption(SampleSearchType.ALL, I18nProperties.getEnumCaption(SampleSearchType.ALL));
			sampleTypeFilter.setItemCaption(SampleSearchType.CASE, I18nProperties.getEnumCaption(SampleSearchType.CASE));
			sampleTypeFilter.setItemCaption(SampleSearchType.CONTACT, I18nProperties.getEnumCaption(SampleSearchType.CONTACT));
			sampleTypeFilter.addValueChangeListener(e -> {
				criteria.sampleSearchType(((SampleSearchType)e.getProperty().getValue()));
				samplesView.navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(sampleTypeFilter);
		}
		shipmentFilterLayout.addComponent(actionButtonsLayout);
		shipmentFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		shipmentFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return shipmentFilterLayout;
	}

	public void reload(ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	public SampleGrid getGrid() {
		return grid;
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		samplesView.setApplyingCriteria(true);

		updateStatusButtons();

		if (sampleTypeFilter != null) {
			sampleTypeFilter.setValue(criteria.getSampleSearchType());
		}

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		samplesView.setApplyingCriteria(false);
	}

	private void processStatusChange(String status) {
		if (NOT_SHIPPED.equals(status)) {
			criteria.shipped(false);
			criteria.received(null);
			criteria.referred(null);
		} else if (SHIPPED.equals(status)) {
			criteria.shipped(true);
			criteria.received(null);
			criteria.referred(null);
		} else if (RECEIVED.equals(status)) {
			criteria.shipped(null);
			criteria.received(true);
			criteria.referred(null);
		} else if (REFERRED.equals(status)) {
			criteria.shipped(null);
			criteria.received(null);
			criteria.referred(true);
		} else {
			criteria.shipped(null);
			criteria.received(null);
			criteria.referred(null);
		}

		samplesView.navigateTo(criteria);
	}

	private void createAndAddStatusButton(String captionKey, String status, HorizontalLayout filterLayout) {
		Button button = ButtonHelper.createButton(captionKey, e -> processStatusChange(status),
				ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);

		button.setData(status);
		button.setCaptionAsHtml(true);

		filterLayout.addComponent(button);

		statusButtons.put(button, button.getCaption());
	}

	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if ((NOT_SHIPPED.equals(b.getData()) && criteria.getShipped() == Boolean.FALSE)
					|| (SHIPPED.equals(b.getData()) && criteria.getShipped() == Boolean.TRUE)
					|| (RECEIVED.equals(b.getData()) && criteria.getReceived() == Boolean.TRUE)
					|| (REFERRED.equals(b.getData()) && criteria.getReferred() == Boolean.TRUE)) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

	public TextField getSearchField() {
		return filterForm.getSearchField();
	}

	public MenuBar getBulkOperationsDropdown() {
		return bulkOperationsDropdown;
	}

	public SampleCriteria getCriteria() {
		return criteria;
	}

}
