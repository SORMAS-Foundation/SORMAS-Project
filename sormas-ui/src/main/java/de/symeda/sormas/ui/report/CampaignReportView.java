package de.symeda.sormas.ui.report;


import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.infrastructure.community.CommunityDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesGrid;
import de.symeda.sormas.ui.configuration.infrastructure.CommunitiesView;
import de.symeda.sormas.ui.configuration.infrastructure.components.SearchField;
import de.symeda.sormas.ui.user.UserGrid;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.V7GridExportStreamResource;


public class CampaignReportView extends AbstractView {

	public static final String VIEW_NAME = "reports";
	private static final long serialVersionUID = -3533557348144005469L;

	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private UserReportGrid grid;
	private Button syncButton;
	
	// Filter
		private SearchField searchField;
		private ComboBox areaFilter;
		private ComboBox regionFilter;
		private ComboBox districtFilter;
		private ComboBox relevanceStatusFilter;
		private Button resetButton;

		private HorizontalLayout filterLayout;
	private VerticalLayout gridLayout;
	

	private RowCount rowsCount;
	
	private CommunityCriteriaNew criteria;
	
	public CampaignReportView() {
		
		
		super(VIEW_NAME);
		
		//addHeaderComponent("");
		HorizontalLayout layt = new HorizontalLayout();
		TabSheet tabsheet = new TabSheet();
		layt.addComponent(tabsheet);
		layt.setSizeFull();
		tabsheet.setHeightFull();

		
		criteria = ViewModelProviders.of(CommunitiesView.class)
				.get(CommunityCriteriaNew.class, new CommunityCriteriaNew().country(FacadeProvider.getCountryFacade().getServerCountry()));
			if (criteria.getRelevanceStatus() == null) {
				criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
			}

		
		//Second TAB
		gridLayout = new VerticalLayout();
		
		
		grid = new UserReportGrid(criteria);
		
		rowsCount = new RowCount(Strings.labelNumberOfCommunities, grid.getItemCount());
		gridLayout.addComponent(rowsCount);
		
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(grid);
		
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		
		

		Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null,
				ValoTheme.BUTTON_PRIMARY);
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		addHeaderComponent(exportButton);

		StreamResource streamResource = GridExportStreamResource.createStreamResource("", "", grid,
				ExportEntityName.USERS, UserReportGrid.EDIT_BTN_ID);
		FileDownloader fileDownloaderx = new FileDownloader(streamResource);
		fileDownloaderx.extend(exportButton);

		

	//	addComponent(gridLayout);
	//	gridLayout = new VerticalLayout();
		
		gridLayout.setCaption("User Analysis");
		tabsheet.addTab(gridLayout);
		
//		gridLayout = new VerticalLayout();
//		gridLayout.setCaption("Pivot Table");
//		tabsheet.addTab(gridLayout);
		
		// Create the first tab
		gridLayout = new VerticalLayout();
		tabsheet.addTab(gridLayout, "Aggregate Report");
		
		
		
		layt.setStyleName("backgroudBrown");
		addComponent(layt);

	}

	private HorizontalLayout createFilterBar() {

		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		searchField = new SearchField();
		searchField.addTextChangeListener(e -> {
			criteria.nameLike(e.getText());
			grid.reload();
		});
		filterLayout.addComponent(searchField);
		
		areaFilter = ComboBoxHelper.createComboBoxV7();
		areaFilter.setId(RegionDto.AREA);
		areaFilter.setWidth(140, Unit.PIXELS);
		areaFilter.setCaption(I18nProperties.getPrefixCaption(RegionDto.I18N_PREFIX, RegionDto.AREA));
		areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		//areaFilter.setValue("East");
	
		criteria.fromUrlParams("area=W5R34K-APYPCA-4GZXDO-IVJWKGIM");
		
		areaFilter.addValueChangeListener(e -> {
			AreaReferenceDto area = (AreaReferenceDto) e.getProperty().getValue();
			criteria.area(area);
			navigateTo(criteria);
			FieldHelper
				.updateItems(regionFilter, area != null ? FacadeProvider.getRegionFacade().getAllActiveByArea(area.getUuid()) : null);
			//grid.reload();
		});
		filterLayout.addComponent(areaFilter);

		regionFilter = ComboBoxHelper.createComboBoxV7();
		regionFilter.setId(DistrictDto.REGION);
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setCaption(I18nProperties.getPrefixCaption(DistrictDto.I18N_PREFIX, DistrictDto.REGION));
		regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
			criteria.region(region);
			navigateTo(criteria);
			FieldHelper
				.updateItems(districtFilter, region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : null);
			//grid.reload();
		});
		filterLayout.addComponent(regionFilter);

		districtFilter = ComboBoxHelper.createComboBoxV7();
		districtFilter.setId(CommunityDto.DISTRICT);
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setCaption(I18nProperties.getPrefixCaption(CommunityDto.I18N_PREFIX, CommunityDto.DISTRICT));
		districtFilter.addValueChangeListener(e -> {
			criteria.district((DistrictReferenceDto) e.getProperty().getValue());
			grid.reload();
		});
		filterLayout.addComponent(districtFilter);

		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CommunitiesView.class).remove(CommunityCriteriaNew.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);
		resetButton.setVisible(false);

		filterLayout.addComponent(resetButton);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(220, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.communityActiveCommunities));
				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.communityArchivedCommunities));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.communityAllCommunities));
				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);

			
			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		return filterLayout;
	}

	public static StreamResource createGridExportStreamResourcsse(List<String> lst, String fln) {

			return new V7GridExportStreamResource( lst,  fln);
		}

	@Override
	public void enter(ViewChangeEvent event) {

	//	super.enter(event);
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

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		searchField.setValue(criteria.getNameLike());
		areaFilter.setValue(criteria.getArea());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());

		applyingCriteria = false;
	}

}
