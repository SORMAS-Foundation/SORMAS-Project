package de.symeda.sormas.ui.report;


import com.vaadin.flow.component.UI;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

import java.net.URI;
import java.util.List;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
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
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
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


public class CampaignReportTabSheets extends VerticalLayout implements View {
	
	private static final long serialVersionUID = -3533557348144005469L;

	public static final String ACTIVE_FILTER = I18nProperties.getString(Strings.active);
	public static final String INACTIVE_FILTER = I18nProperties.getString(Strings.inactive);

	private UserReportGrid grid;
	private Button syncButton;
	//check the diagram definition Selector
	// Filter
//		private SearchField searchField;
		private ComboBox areaFilter;
		private ComboBox regionFilter;
		private ComboBox districtFilter;
//		private ComboBox relevanceStatusFilter;
		private Button resetButton;
		protected boolean applyingCriteria;

		private HorizontalLayout filterLayout;
		//private VerticalLayout gridLayout;
	

	private RowCount rowsCount;
	
	private CommunityCriteriaNew criteria;
	
	public CampaignReportTabSheets(CommunityCriteriaNew criteriax, FormAccess formAccess) {
			criteria = criteriax;
			grid = new UserReportGrid(criteriax, formAccess);	
			
			
			this.addComponent(createFilterBar());
			
			
			this.addComponent(grid);
			this.setHeightFull();
			this.setMargin(false);
			this.setSpacing(false);
			this.setSizeFull();
			this.setExpandRatio(grid, 1);
			this.setStyleName("crud-main-layout");
			
			
		
		Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null,
				ValoTheme.BUTTON_PRIMARY);
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		this.addComponent(exportButton);

		StreamResource streamResource = GridExportStreamResource.createStreamResource("", "", grid,
				ExportEntityName.USERS, UserReportGrid.EDIT_BTN_ID);
		FileDownloader fileDownloaderx = new FileDownloader(streamResource); 
		fileDownloaderx.extend(exportButton);
		
		//extractUrl();

	}
	
	@SuppressWarnings("deprecation")
	private HorizontalLayout createFilterBar() {
		
		final UserDto user = UserProvider.getCurrent().getUser();
		criteria.area(user.getArea());// .setArea(user.getArea());
		criteria.region(user.getRegion());// .setRegion(user.getRegion());
		criteria.district(user.getDistrict()); // .setDistrict(user.getDistrict());
//		criteria.setCommunity(null); // set to null 

		filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

//		searchField = new SearchField();
//		searchField.addTextChangeListener(e -> {
//			criteria.nameLike(e.getText());
//			grid.reload();
//		});
//		filterLayout.addComponent(searchField);
		
		
		areaFilter = ComboBoxHelper.createComboBoxV7();
		areaFilter.setId(RegionDto.AREA);
		
		
		if (user.getArea() == null) {
		
		areaFilter.setWidth(140, Unit.PIXELS);
		areaFilter.setCaption(I18nProperties.getPrefixCaption(RegionDto.I18N_PREFIX, RegionDto.AREA));
		areaFilter.setInputPrompt(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.AREA));
		areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
	
		
		//if (criteria.getArea() == null) {
			
			if (areaFilter.getValue() == null) {
			
			criteria.fromUrlParams("area=W5R34K-APYPCA-4GZXDO-IVJWKGIM");
		}
			
			
		
		areaFilter.addValueChangeListener(e -> {

			AreaReferenceDto area = (AreaReferenceDto) e.getProperty().getValue();
		
			if (!DataHelper.equal(area, criteria.getArea())) {
				criteria.region(null);
				criteria.area(area);
			}
			criteria.area(area);
			//navigateTo(criteria);
			grid.reload();
			FieldHelper
				.updateItems(regionFilter, area != null ? FacadeProvider.getRegionFacade().getAllActiveByArea(area.getUuid()) : null);

		});
	
		
		filterLayout.addComponent(areaFilter);
	
	}

		regionFilter = ComboBoxHelper.createComboBoxV7();
		regionFilter.setId(DistrictDto.REGION);
		
		
		if (user.getRegion() == null) {
		regionFilter.setWidth(140, Unit.PIXELS);
		regionFilter.setInputPrompt(I18nProperties.getCaption(Captions.region));
		regionFilter.setCaption(I18nProperties.getPrefixCaption(DistrictDto.I18N_PREFIX, DistrictDto.REGION));
		
		if(user.getArea() != null) {
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(user.getArea().getUuid()));
		}else {
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}
		
		
		regionFilter.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
//			criteria.region(region);
//			navigateTo(criteria);
			FieldHelper
				.updateItems(districtFilter, region != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()) : null);
//			//grid.reload();
//		});
//		filterLayout.addComponent(regionFilter);
//		
//		
			if (!DataHelper.equal(region, criteria.getRegion())) {
				criteria.district(null);
			}

			criteria.region(region);
			//navigateTo(criteria);
			grid.reload();
		});
		filterLayout.addComponent(regionFilter);
	}
		
		
		districtFilter = ComboBoxHelper.createComboBoxV7();
		districtFilter.setId(CommunityDto.DISTRICT);
		if(user.getDistrict() == null) {
		
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getCaption(Captions.district));
		districtFilter.setCaption(I18nProperties.getPrefixCaption(CommunityDto.I18N_PREFIX, CommunityDto.DISTRICT));
		
		if(user.getRegion() != null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
		}
		
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			criteria.district(district);
//			navigateTo(criteria);
//			criteria.district((DistrictReferenceDto) e.getProperty().getValue());
			grid.reload();
		});
		filterLayout.addComponent(districtFilter);
		}
		resetButton = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(CommunitiesView.class).remove(CommunityCriteriaNew.class);
			navigateTo(null);
			//grid.reload();
		}, CssStyles.FORCE_CAPTION);
		resetButton.setVisible(true);

		filterLayout.addComponent(resetButton);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
//			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW)) {
//				relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
//				relevanceStatusFilter.setId("relevanceStatus");
//				relevanceStatusFilter.setWidth(220, Unit.PERCENTAGE);
//				relevanceStatusFilter.setNullSelectionAllowed(false);
//				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
//				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.communityActiveCommunities));
//				relevanceStatusFilter
//					.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.communityArchivedCommunities));
//				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.communityAllCommunities));
//				relevanceStatusFilter.addValueChangeListener(e -> {
//					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
//					navigateTo(criteria);
//					//grid.reload();
//				});
//				actionButtonsLayout.addComponent(relevanceStatusFilter);
//
//			
//			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		//extractUrl();
		
		return filterLayout;
	}

	public static StreamResource createGridExportStreamResourcsse(List<String> lst, String fln) {

			return new V7GridExportStreamResource( lst,  fln);
		}

	//@Override
	public void extractUrl() {
		URI location = Page.getCurrent().getLocation();
		String uri = location.toString();

		String params = uri.trim();
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

//		if (relevanceStatusFilter != null) {
//			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
//		}
//		searchField.setValue(criteria.getNameLike());
		areaFilter.setValue(criteria.getArea());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());

		applyingCriteria = false;
	}
	
	public boolean navigateTo(BaseCriteria criteria) {
		return navigateTo(criteria, true);
	}

	public boolean navigateTo(BaseCriteria criteria, boolean force) {
		if (applyingCriteria) {
			return false;
		}
		applyingCriteria = true;

		Navigator navigator = SormasUI.get().getNavigator();

		String state = navigator.getState();
		String newState = buildNavigationState(state, criteria);

		boolean didNavigate = false;
		if (!newState.equals(state) || force) {
			navigator.navigateTo(newState);

			didNavigate = true;
		}
		
		applyingCriteria = false;

		return didNavigate;
	}

	public static String buildNavigationState(String currentState, BaseCriteria criteria) {

		String newState = currentState;
		int paramsIndex = newState.lastIndexOf('?');
		if (paramsIndex >= 0) {
			newState = newState.substring(0, paramsIndex);
		}

		if (criteria != null) {
			String params = criteria.toUrlParams();
			if (!DataHelper.isNullOrEmpty(params)) {
				if (newState.charAt(newState.length() - 1) != '/') {
					newState += "/";
				}

				newState += "?" + params;
			}
		}

		return newState;
	}

}
