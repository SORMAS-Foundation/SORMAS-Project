package de.symeda.sormas.ui.dashboard.campaigns;

import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.AREA;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.COMMUNITY;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.DISTRICT;
import static de.symeda.sormas.api.campaign.CampaignJurisdictionLevel.REGION;

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
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
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
		this.campaignFilter = ComboBoxHelper.createComboBoxV7();
		this.regionFilter = ComboBoxHelper.createComboBoxV7();
		this.districtFilter = ComboBoxHelper.createComboBoxV7();
		this.areaFilter = ComboBoxHelper.createComboBoxV7();
		this.campaignJurisdictionGroupByFilter = ComboBoxHelper.createComboBoxV7();

		setSpacing(true);
		setWidthFull();
		setMargin(new MarginInfo(true, true, false, true));

		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();

		final CampaignJurisdictionLevel campaignJurisdictionLevel =
			CampaignJurisdictionLevel.getByJurisdictionLevel(UserProvider.getCurrent().getJurisdictionLevel());
		dashboardDataProvider.setCampaignJurisdictionLevelGroupBy(getJurisdictionBelow(campaignJurisdictionLevel));

		createCampaignFilter();
		createJurisdictionFilters(campaignJurisdictionLevel);

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
	private void createJurisdictionFilters(CampaignJurisdictionLevel campaignJurisdictionLevel) {
		final UserDto user = UiUtil.getUser();
		final RegionReferenceDto userRegion = user.getRegion();
		final AreaReferenceDto userArea = userRegion != null ? FacadeProvider.getRegionFacade().getByUuid(userRegion.getUuid()).getArea() : null;
		final DistrictReferenceDto userDistrict = user.getDistrict();

		dashboardDataProvider.setArea(userArea);
		areaFilter.setCaption(I18nProperties.getCaption(Captions.Campaign_area));
		areaFilter.setWidth(200, Unit.PIXELS);
		areaFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllAreas));
		areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		areaFilter.addValueChangeListener(e -> {
			final Object value = areaFilter.getValue();
			dashboardDataProvider.setArea((AreaReferenceDto) value);
			updateFiltersBasedOnArea(value);
			dashboardView.refreshDashboard();
		});
		addComponent(areaFilter);
		dashboardDataProvider.setArea((AreaReferenceDto) areaFilter.getValue());

		dashboardDataProvider.setRegion(userRegion);
		regionFilter.setCaption(I18nProperties.getCaption(Captions.Campaign_region));
		regionFilter.setWidth(200, Unit.PIXELS);
		regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptAllRegions));
		regionFilter.addValueChangeListener(e -> {
			final Object value = regionFilter.getValue();
			dashboardDataProvider.setRegion((RegionReferenceDto) value);
			updateFiltersBasedOnRegion(value);
			dashboardView.refreshDashboard();
		});
		addComponent(regionFilter);
		dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());

		dashboardDataProvider.setDistrict(userDistrict);
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

		campaignJurisdictionGroupByFilter.setCaption(I18nProperties.getCaption(Captions.campaignDiagramGroupBy));
		campaignJurisdictionGroupByFilter.setWidth(200, Unit.PIXELS);

		switch (campaignJurisdictionLevel) {
		case AREA:
			campaignJurisdictionGroupByFilter.addItems(AREA, REGION, DISTRICT);
			break;
		case REGION:
			campaignJurisdictionGroupByFilter.addItems(REGION, DISTRICT, COMMUNITY);
			break;
		case DISTRICT:
			campaignJurisdictionGroupByFilter.addItems(DISTRICT, COMMUNITY);
			break;
		case COMMUNITY:
			campaignJurisdictionGroupByFilter.addItems(COMMUNITY);
			break;
		}

		campaignJurisdictionGroupByFilter.setValue(getJurisdictionBelow(campaignJurisdictionLevel));
		campaignJurisdictionGroupByFilter.setNullSelectionAllowed(false);
		campaignJurisdictionGroupByFilter.addValueChangeListener(e -> {
			final CampaignJurisdictionLevel currentValue = (CampaignJurisdictionLevel) campaignJurisdictionGroupByFilter.getValue();
			dashboardDataProvider.setCampaignJurisdictionLevelGroupBy(currentValue);
			dashboardView.refreshDashboard();
		});
		addComponent(campaignJurisdictionGroupByFilter);

		if (userRegion != null) {
			areaFilter.setValue(userArea);
			regionFilter.setValue(userRegion);
			areaFilter.setEnabled(false);
			regionFilter.setEnabled(false);
			if (userDistrict != null) {
				districtFilter.setValue(userDistrict);
				districtFilter.setEnabled(false);
			}
		} else if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			areaFilter.setVisible(false);
			regionFilter.setVisible(false);
			districtFilter.setVisible(false);
			campaignJurisdictionGroupByFilter.setVisible(false);
		}
	}

	private CampaignJurisdictionLevel getJurisdictionBelow(CampaignJurisdictionLevel campaignJurisdictionLevel) {

		switch (campaignJurisdictionLevel) {
		case AREA:
			return AREA;
		case REGION:
			return DISTRICT;
		case DISTRICT:
		case COMMUNITY:
			return COMMUNITY;
		}
		return campaignJurisdictionLevel;
	}

	private void updateFiltersBasedOnRegion(Object value) {
		if (value != null) {
			districtFilter.removeAllItems();
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(((RegionReferenceDto) value).getUuid()));
			campaignJurisdictionGroupByFilter.setValue(DISTRICT);
			campaignJurisdictionGroupByFilter.removeItem(AREA);
			campaignJurisdictionGroupByFilter.addItems(REGION, DISTRICT, COMMUNITY);
		} else {
			districtFilter.clear();
			districtFilter.removeAllItems();
			campaignJurisdictionGroupByFilter.setValue(REGION);
			campaignJurisdictionGroupByFilter.removeItem(COMMUNITY);
			campaignJurisdictionGroupByFilter.addItems(AREA, REGION, DISTRICT);
		}
	}

	private void updateFiltersBasedOnArea(Object value) {
		if (value != null) {
			regionFilter.removeAllItems();
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByArea(((AreaReferenceDto) value).getUuid()));
			regionFilter.setEnabled(true);
			campaignJurisdictionGroupByFilter.addItems(AREA, REGION, DISTRICT);
			campaignJurisdictionGroupByFilter.setValue(REGION);
			campaignJurisdictionGroupByFilter.removeItem(COMMUNITY);
		} else {
			regionFilter.clear();
			regionFilter.removeAllItems();
			districtFilter.clear();
			districtFilter.removeAllItems();
			campaignJurisdictionGroupByFilter.setValue(AREA);
			campaignJurisdictionGroupByFilter.removeItem(DISTRICT);
			campaignJurisdictionGroupByFilter.removeItem(COMMUNITY);
			campaignJurisdictionGroupByFilter.addItems(AREA, REGION);
		}
	}

	private void updateFiltersBasedOnDistrict(Object value) {
		if (value != null) {
			campaignJurisdictionGroupByFilter.addItems(DISTRICT, COMMUNITY);
			campaignJurisdictionGroupByFilter.setValue(COMMUNITY);
			campaignJurisdictionGroupByFilter.removeItem(AREA);
			campaignJurisdictionGroupByFilter.removeItem(REGION);
		} else {
			campaignJurisdictionGroupByFilter.removeItem(AREA);
			campaignJurisdictionGroupByFilter.addItems(REGION, DISTRICT, COMMUNITY);
			campaignJurisdictionGroupByFilter.setValue(DISTRICT);
		}
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}
}
