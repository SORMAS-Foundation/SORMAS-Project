package de.symeda.sormas.ui.dashboard.campaigns;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.UserProvider;

public class CampaignDashboardFilterLayout extends HorizontalLayout {

	private CampaignDashboardView dashboardView;
	private CampaignDashboardDataProvider dashboardDataProvider;

	private Label infoLabel;

	private ComboBox campaignFilter;
	private ComboBox areaFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;

	public CampaignDashboardFilterLayout(CampaignDashboardView dashboardView, CampaignDashboardDataProvider dashboardDataProvider) {

		this.dashboardView = dashboardView;
		this.dashboardDataProvider = dashboardDataProvider;
		this.campaignFilter = new ComboBox();
		this.regionFilter = new ComboBox();
		this.districtFilter = new ComboBox();
		this.areaFilter = new ComboBox();

		setSpacing(true);
		setSizeUndefined();
		setMargin(new MarginInfo(true, true, false, true));

		infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();

		createCampaignFilter();
		createJurisdictionFilters();
	}

	private void createCampaignFilter() {
		campaignFilter.setWidth(200, Unit.PIXELS);
		campaignFilter.setInputPrompt(I18nProperties.getString(Strings.promptCampaign));
		campaignFilter.addItems(FacadeProvider.getCampaignFacade().getAllCampaignsAsReference().toArray());
		campaignFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
			dashboardView.refreshDashboard();
		});
		addComponent(campaignFilter);
		dashboardDataProvider.setCampaign((CampaignReferenceDto) campaignFilter.getValue());
	}

	@SuppressWarnings("deprecation")
	private void createJurisdictionFilters() {
		// Region filter
		setFilterVisibilitiesBasedOnArea(areaFilter.getValue());
		if (UserProvider.getCurrent().getUser().getArea() == null) {
			areaFilter.setWidth(200, Unit.PIXELS);
			areaFilter.setInputPrompt(I18nProperties.getString(Strings.promptArea));
			areaFilter.addItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
			areaFilter.addValueChangeListener(e -> {
				final Object value = areaFilter.getValue();
				dashboardDataProvider.setArea((AreaReferenceDto) value);
				dashboardView.refreshDashboard();
				setFilterVisibilitiesBasedOnArea(value);
			});
			addComponent(areaFilter);
			dashboardDataProvider.setArea((AreaReferenceDto) areaFilter.getValue());
		}

		// Region filter
		setFilterVisibilitiesBasedOnRegion(regionFilter.getValue());
		if (UserProvider.getCurrent().getUser().getRegion() == null) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getString(Strings.promptRegion));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
			regionFilter.addValueChangeListener(e -> {
				final Object value = regionFilter.getValue();
				dashboardDataProvider.setRegion((RegionReferenceDto) value);
				dashboardView.refreshDashboard();
				setFilterVisibilitiesBasedOnRegion(value);
			});
			addComponent(regionFilter);
			dashboardDataProvider.setRegion((RegionReferenceDto) regionFilter.getValue());
		}

		// District filter
		if (UserProvider.getCurrent().getUser().getRegion() != null && UserProvider.getCurrent().getUser().getDistrict() == null) {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getString(Strings.promptDistrict));
			districtFilter
				.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(UserProvider.getCurrent().getUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
				dashboardView.refreshDashboard();
			});
			addComponent(districtFilter);
			dashboardDataProvider.setDistrict((DistrictReferenceDto) districtFilter.getValue());
		}
	}

	private void setFilterVisibilitiesBasedOnRegion(Object value) {
		if (value != null) {
			districtFilter.setVisible(true);
		} else {
			districtFilter.setVisible(false);
			districtFilter.clear();
		}
	}

	private void setFilterVisibilitiesBasedOnArea(Object value) {
		if (value != null) {
			regionFilter.setVisible(true);
			districtFilter.setVisible(true);
		} else {
			regionFilter.setVisible(false);
			districtFilter.setVisible(false);
			regionFilter.clear();
			districtFilter.clear();
		}
	}

	public void setInfoLabelText(String text) {
		infoLabel.setDescription(text);
	}
}
