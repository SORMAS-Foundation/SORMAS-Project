package de.symeda.sormas.ui.dashboard.campaigns;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.CampaignPhase;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignDashboardFilterLayout extends HorizontalLayout {

	private CampaignDashboardView dashboardView;
	private CampaignDashboardDataProvider dashboardDataProvider;

	private Label infoLabel;

	private ComboBox campaignFilter;
	private ComboBox areaFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox campaignJurisdictionGroupByFilter;

	private OptionGroup campaignPhaseSelector;

	public CampaignDashboardFilterLayout(CampaignDashboardView dashboardView, CampaignDashboardDataProvider dashboardDataProvider) {

		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.campaignFilter = new ComboBox();
		this.regionFilter = new ComboBox();
		this.districtFilter = new ComboBox();
		this.areaFilter = new ComboBox();
		this.campaignJurisdictionGroupByFilter = new ComboBox();

		setSpacing(true);
		setWidthFull();
		setMargin(new MarginInfo(true, true, false, true));

		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();

		final UserDto user = UserProvider.getCurrent().getUser();
		final CampaignJurisdictionLevel usersSubJurisdition =
				CampaignJurisdictionLevel.getByJurisdictionLevel(UserRole.getJurisdictionLevel(user.getUserRoles()));
		dashboardDataProvider.setCampaignJurisdictionLevelGroupBy(usersSubJurisdition);

		createCampaignFilter();
		createJurisdictionFilters(usersSubJurisdition);

		campaignPhaseSelector = new OptionGroup();
		campaignPhaseSelector.setDescription(I18nProperties.getPrefixDescription(CampaignDto.I18N_PREFIX, "campaignPhase"));
		CssStyles.style(campaignPhaseSelector, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		campaignPhaseSelector.addItems(CampaignPhase.values());
		campaignPhaseSelector.setValue(CampaignPhase.INTRA);
		campaignPhaseSelector.setEnabled(false);
		addComponent(campaignPhaseSelector);
		setExpandRatio(campaignPhaseSelector, 1);
		setComponentAlignment(campaignPhaseSelector, Alignment.MIDDLE_RIGHT);
	}

	private void createCampaignFilter() {
		campaignFilter.setRequired(true);
		campaignFilter.setNullSelectionAllowed(false);
		campaignFilter.setCaption(I18nProperties.getCaption(Captions.Campaign));
		campaignFilter.setWidth(200, Unit.PIXELS);
		campaignFilter.setInputPrompt(I18nProperties.getString(Strings.promptCampaign));
		campaignFilter.addItems(FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference().toArray());
		campaignFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
			dashboardView.refreshDashboard();
		});
		addComponent(campaignFilter);

		final CampaignReferenceDto lastStartedCampaign = dashboardDataProvider.getLastStartedCampaign();
		if (lastStartedCampaign != null) {
			campaignFilter.setValue(lastStartedCampaign);
		}
		dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
	}

	@SuppressWarnings("deprecation")
	private void createJurisdictionFilters(CampaignJurisdictionLevel usersSubJurisdition) {
		final UserDto user = UserProvider.getCurrent().getUser();
		final RegionReferenceDto userRegion = user.getRegion();
		final AreaReferenceDto userArea =
			userRegion != null ? FacadeProvider.getRegionFacade().getRegionByUuid(userRegion.getUuid()).getArea() : null;
		final DistrictReferenceDto userDistrict = user.getDistrict();

		dashboardDataProvider.setArea(userArea);
		if (userArea == null && userRegion == null) {
			updateFiltersBasedOnArea(areaFilter.getValue());
			areaFilter.setCaption(I18nProperties.getCaption(Captions.Campaign_area));
			areaFilter.setWidth(200, Unit.PIXELS);
			areaFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllAreas));
			areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
			areaFilter.addValueChangeListener(e -> {
				final Object value = areaFilter.getValue();
				updateFiltersBasedOnArea(value);
				dashboardDataProvider.setArea((AreaReferenceDto) value);
				dashboardView.refreshDashboard();
			});
			addComponent(areaFilter);
			dashboardDataProvider.setArea((AreaReferenceDto) areaFilter.getValue());
		}

		dashboardDataProvider.setRegion(userRegion);
		if (userRegion == null) {
			updateFiltersBasedOnRegion(regionFilter.getValue());
			regionFilter.setCaption(I18nProperties.getCaption(Captions.Campaign_region));
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllRegions));
			regionFilter.addValueChangeListener(e -> {
				final Object value = regionFilter.getValue();
				updateFiltersBasedOnRegion(value);
				dashboardDataProvider.setRegion((RegionReferenceDto) value);
				dashboardView.refreshDashboard();
			});
			addComponent(regionFilter);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}

		dashboardDataProvider.setDistrict(userDistrict);
		if (userRegion != null || userDistrict == null) {
			districtFilter.setCaption(I18nProperties.getCaption(Captions.Campaign_district));
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllDistricts));
			if (userRegion != null) {
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(userRegion.getUuid()));
			}
			districtFilter.addValueChangeListener(e -> {
				final Object value = districtFilter.getValue();
				updateFiltersBasedOnDistrict(value);
				dashboardDataProvider.setDistrict((DistrictReferenceDto) value);
				dashboardView.refreshDashboard();
			});
			addComponent(districtFilter);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}

		campaignJurisdictionGroupByFilter.setCaption(I18nProperties.getCaption(Captions.campaignDiagramGroupBy));
		campaignJurisdictionGroupByFilter.setWidth(200, Unit.PIXELS);
		campaignJurisdictionGroupByFilter.addItems(CampaignJurisdictionLevel.values());
		campaignJurisdictionGroupByFilter.setValue(usersSubJurisdition);
		campaignJurisdictionGroupByFilter.setNullSelectionAllowed(false);
		campaignJurisdictionGroupByFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setCampaignJurisdictionLevelGroupBy((CampaignJurisdictionLevel) campaignJurisdictionGroupByFilter.getValue());
			dashboardView.refreshDashboard();
		});
		addComponent(campaignJurisdictionGroupByFilter);
	}

	private void updateFiltersBasedOnRegion(Object value) {
		if (value != null) {
			districtFilter.setEnabled(true);
			districtFilter.removeAllItems();
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(((RegionReferenceDto) value).getUuid()));
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.DISTRICT);
		} else {
			districtFilter.setEnabled(false);
			districtFilter.clear();
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.REGION);
		}
	}

	private void updateFiltersBasedOnArea(Object value) {
		if (value != null) {
			regionFilter.removeAllItems();
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(((AreaReferenceDto) value).getUuid()));
			regionFilter.setEnabled(true);
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.REGION);
		} else {
			regionFilter.setEnabled(false);
			regionFilter.clear();
			districtFilter.setEnabled(false);
			districtFilter.clear();
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.AREA);
		}
	}

	private void updateFiltersBasedOnDistrict(Object value) {
		if (value != null) {
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.COMMUNITY);
		} else {
			campaignJurisdictionGroupByFilter.setValue(CampaignJurisdictionLevel.DISTRICT);
		}
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}
}
