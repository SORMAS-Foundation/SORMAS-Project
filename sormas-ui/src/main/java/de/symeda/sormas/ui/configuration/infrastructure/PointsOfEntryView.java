package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

public class PointsOfEntryView extends AbstractConfigurationView {
	
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/pointsofentry";
	
	private PointOfEntryCriteria criteria;
	
	// Filters
	private TextField searchField;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox typeFilter;
	private ComboBox activeFilter;
	private Button resetButton;
	
	private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	protected PointsOfEntryGrid grid;
	protected Button createButton;
	protected Button importButton;
	protected Button exportButton;
	
	public PointsOfEntryView() {
		super(VIEW_NAME);
		
		criteria = ViewModelProviders.of(PointsOfEntryView.class).get(PointOfEntryCriteria.class);
		
		grid = new PointsOfEntryGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		Button exportButton = new Button(I18nProperties.getCaption(Captions.export));
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		exportButton.setIcon(VaadinIcons.TABLE);
		addHeaderComponent(exportButton);

		StreamResource streamResource = new GridExportStreamResource(grid, "sormas_pointsofentry", "sormas_pointsofentry_" + DateHelper.formatDateForExport(new Date()) + ".csv", PointsOfEntryGrid.EDIT_BTN_ID);
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton = new Button(I18nProperties.getCaption(Captions.actionNewEntry));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(
					e -> ControllerProvider.getInfrastructureController().createPointOfEntry());
			addHeaderComponent(createButton);
		}

		addComponent(gridLayout);
	}
	
	private HorizontalLayout createFilterBar() {
		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setNullRepresentation("");
		searchField.setInputPrompt(I18nProperties.getString(Strings.promptSearch));
		searchField.addTextChangeListener(e -> {
			criteria.nameLike(e.getText());
			grid.reload();
		});
		CssStyles.style(searchField, CssStyles.FORCE_CAPTION);
		filterLayout.addComponent(searchField);

		regionFilter = new ComboBox();
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.REGION));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			criteria.region(region);
			navigateTo(criteria);
			FieldHelper.updateItems(districtFilter,
					region != null ? FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()) : null);
		});
		filterLayout.addComponent(regionFilter);

		districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.DISTRICT));
		districtFilter.addValueChangeListener(e -> {
			criteria.district((DistrictReferenceDto) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(districtFilter);
		
		typeFilter = new ComboBox();
		typeFilter.setWidth(140, Unit.PIXELS);
		typeFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.POINT_OF_ENTRY_TYPE));
		typeFilter.addItems(PointOfEntryType.values());
		typeFilter.addValueChangeListener(e -> {
			criteria.type((PointOfEntryType) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(typeFilter);
		
		activeFilter = new ComboBox();
		activeFilter.setWidth(140, Unit.PIXELS);
		activeFilter.setCaption(I18nProperties.getPrefixCaption(PointOfEntryDto.I18N_PREFIX, PointOfEntryDto.ACTIVE));
		activeFilter.addItem(Boolean.TRUE);
		activeFilter.addItem(Boolean.FALSE);
		activeFilter.setItemCaption(Boolean.TRUE, DataHelper.parseBoolean(Boolean.TRUE));
		activeFilter.setItemCaption(Boolean.FALSE, DataHelper.parseBoolean(Boolean.FALSE));
		activeFilter.addValueChangeListener(e -> {
			criteria.active((Boolean) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(activeFilter);
		
		resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		resetButton.setVisible(false);
		CssStyles.style(resetButton, CssStyles.FORCE_CAPTION);
		resetButton.addClickListener(event -> {
			ViewModelProviders.of(PointsOfEntryView.class).remove(PointOfEntryCriteria.class);
			navigateTo(null);
		});
		filterLayout.addComponent(resetButton);

		return filterLayout;
	}	

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		searchField.setValue(criteria.getNameLike());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		typeFilter.setValue(criteria.getType());
		activeFilter.setValue(criteria.getActive());
		
		applyingCriteria = false;
	}	

}
