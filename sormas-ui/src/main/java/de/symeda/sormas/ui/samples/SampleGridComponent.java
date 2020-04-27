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

import java.util.HashMap;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

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
	private ComboBox testResultFilter;
	private ComboBox specimenConditionFilter;
	private ComboBox classificationFilter;
	private ComboBox diseaseFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox labFilter;
	TextField searchField;
	private Button resetButton;
	MenuBar bulkOperationsDropdown;
	private ComboBox relevanceStatusFilter;

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

		UserDto user = UserProvider.getCurrent().getUser();

		testResultFilter = new ComboBox();
		testResultFilter.setWidth(140, Unit.PIXELS);
		testResultFilter.setInputPrompt(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, PathogenTestDto.TEST_RESULT));
		testResultFilter.addItems((Object[])PathogenTestResultType.values());
		testResultFilter.addValueChangeListener(e -> {
			criteria.pathogenTestResult(((PathogenTestResultType)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});
		filterLayout.addComponent(testResultFilter);        

		specimenConditionFilter = new ComboBox();
		specimenConditionFilter.setWidth(140, Unit.PIXELS);
		specimenConditionFilter.setInputPrompt(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, SampleDto.SPECIMEN_CONDITION));
		specimenConditionFilter.addItems((Object[])SpecimenCondition.values());
		specimenConditionFilter.addValueChangeListener(e -> {
			criteria.specimenCondition(((SpecimenCondition)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});
		filterLayout.addComponent(specimenConditionFilter);        

		classificationFilter = new ComboBox();
		classificationFilter.setWidth(140, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
		classificationFilter.addItems((Object[])CaseClassification.values());
		classificationFilter.addValueChangeListener(e -> {
			criteria.caseClassification(((CaseClassification)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});
		filterLayout.addComponent(classificationFilter);        

		diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray());
		diseaseFilter.addValueChangeListener(e -> {
			criteria.disease(((Disease)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});
		filterLayout.addComponent(diseaseFilter);        

		regionFilter = new ComboBox();
		if (user.getRegion() == null) {
			regionFilter.setWidth(140, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
				criteria.region(region);
				samplesView.navigateTo(criteria);
			});
			filterLayout.addComponent(regionFilter);
		}

		districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		districtFilter.addValueChangeListener(e -> {
			criteria.district(((DistrictReferenceDto)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});

		if (user.getRegion() != null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else {
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
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
		filterLayout.addComponent(districtFilter);

		labFilter = new ComboBox();
		labFilter.setWidth(140, Unit.PIXELS);
		labFilter.setInputPrompt(I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.LAB));
		labFilter.addItems(FacadeProvider.getFacilityFacade().getAllActiveLaboratories(true));
		labFilter.addValueChangeListener(e -> {
			criteria.laboratory(((FacilityReferenceDto)e.getProperty().getValue()));
			samplesView.navigateTo(criteria);
		});
		filterLayout.addComponent(labFilter);

		searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptSamplesSearchField));
		searchField.addTextChangeListener(e -> {
			criteria.caseCodeIdLike(e.getText());
			grid.reload();
			
			//open sample if it's the only one
			if (grid.getItemCount() == 1) {
				ControllerProvider.getSampleController().navigateToData(grid.getFirstItem().getUuid());
			}
		});
		filterLayout.addComponent(searchField);

		resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		resetButton.setVisible(false);
		resetButton.addClickListener(event -> {
			ViewModelProviders.of(SamplesView.class).remove(SampleCriteria.class);
			samplesView.navigateTo(null);
		});
		filterLayout.addComponent(resetButton);

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
			Button statusAll = new Button(I18nProperties.getCaption(Captions.all), e -> processStatusChange(null));
			CssStyles.style(statusAll, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);
			buttonFilterLayout.addComponent(statusAll);
			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			Button notShippedButton = new Button(I18nProperties.getCaption(Captions.sampleNotShipped), e -> processStatusChange(NOT_SHIPPED));
			initializeStatusButton(notShippedButton, buttonFilterLayout, NOT_SHIPPED, I18nProperties.getCaption(Captions.sampleNotShipped));
			Button shippedButton = new Button(I18nProperties.getCaption(Captions.sampleShipped), e -> processStatusChange(SHIPPED));
			initializeStatusButton(shippedButton, buttonFilterLayout, SHIPPED, I18nProperties.getCaption(Captions.sampleShipped));
			Button receivedButton = new Button(I18nProperties.getCaption(Captions.sampleReceived), e -> processStatusChange(RECEIVED));
			initializeStatusButton(receivedButton, buttonFilterLayout, RECEIVED, I18nProperties.getCaption(Captions.sampleReceived));
			Button referredButton = new Button(I18nProperties.getCaption(Captions.sampleReferred), e -> processStatusChange(REFERRED));
			initializeStatusButton(referredButton, buttonFilterLayout, REFERRED, I18nProperties.getCaption(Captions.sampleReferred));
		}

		shipmentFilterLayout.addComponent(buttonFilterLayout);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW_ARCHIVED)) {
				relevanceStatusFilter = new ComboBox();
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

				bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getSampleController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), new Runnable() {
						public void run() {
							samplesView.navigateTo(criteria);
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);
				bulkOperationsDropdown.setVisible(samplesView.getViewConfiguration().isInEagerMode());

				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
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

		resetButton.setVisible(criteria.hasAnyFilterActive());

		updateStatusButtons();
		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		testResultFilter.setValue(criteria.getPathogenTestResult());
		specimenConditionFilter.setValue(criteria.getSpecimenCondition());
		classificationFilter.setValue(criteria.getCaseClassification());
		diseaseFilter.setValue(criteria.getDisease());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		labFilter.setValue(criteria.getLaboratory());
		searchField.setValue(criteria.getCaseCodeIdLike());

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

	private void initializeStatusButton(Button button, HorizontalLayout filterLayout, String status, String caption) {
		button.setData(status);
		CssStyles.style(button, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
		button.setCaptionAsHtml(true);
		filterLayout.addComponent(button);
		statusButtons.put(button, caption);
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
		return searchField;
	}

	public MenuBar getBulkOperationsDropdown() {
		return bulkOperationsDropdown;
	}

	public SampleCriteria getCriteria() {
		return criteria;
	}

}
