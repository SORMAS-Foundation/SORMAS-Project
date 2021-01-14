package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	public static final String GRID_CONTAINER = "grid-container";

	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;

	private List<VerticalLayout> campaignDashboardTabComponents = new ArrayList<>();
	private List<String> campaignDashboardDiagramStyles = new ArrayList<>();
	private Component currentSubTabsWrapper;
	private Component currentDiagramsWrapper;

	private Map<CampaignReferenceDto, String> lastTabIdForCampaign = new HashMap<>();
	private Map<CampaignReferenceDto, Map<String, String>> lastSubTabIdForTabIdAndCampaign = new HashMap<>();

	public CampaignDashboardView() {
		super(VIEW_NAME);

		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider);
		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setMargin(false);

		dashboardSwitcher.setValue(DashboardType.CAMPAIGNS);
		dashboardSwitcher.addValueChangeListener(e -> navigateToDashboardView(e));

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoCampaignsDashboard));
		dashboardLayout.setExpandRatio(filterLayout, 0);
		dashboardLayout.setSizeFull();
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		refreshDashboard();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void refreshDashboard() {

		final Page page = Page.getCurrent();
		cleanupDashboard(page);
		dataProvider.refreshDashboardData();

		final VerticalLayout tabLayout = new VerticalLayout();
		tabLayout.setSizeFull();
		tabLayout.setMargin(new MarginInfo(false, false, false, false));
		tabLayout.setSpacing(false);
		campaignDashboardTabComponents.add(tabLayout);
		dashboardLayout.addComponent(tabLayout);
		dashboardLayout.setExpandRatio(tabLayout, 1);

		final OptionGroup tabSwitcher = new OptionGroup();
		final VerticalLayout tabSwitcherLayout = new VerticalLayout(tabSwitcher);
		tabSwitcherLayout.setMargin(new MarginInfo(false, false, false, true));
		tabSwitcherLayout.setSpacing(false);
		tabLayout.addComponent(tabSwitcherLayout);
		tabLayout.setExpandRatio(tabSwitcherLayout, 0);

		final List<String> tabs = new ArrayList<>(dataProvider.getTabIds());
		tabs.forEach(tabId -> {
			tabSwitcher.addItem(tabId);
			tabSwitcher.setItemCaption(tabId, tabId);
		});
		if (!(tabs.size() > 1)) {
			tabSwitcherLayout.setVisible(false);
		}
		final String lastTabId = lastTabIdForCampaign.get(dataProvider.getCampaign());
		tabSwitcher.setValue(tabs.isEmpty() ? StringUtils.EMPTY : lastTabId != null ? lastTabId : tabs.get(0));

		CssStyles.style(tabSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);

		final VerticalLayout subTabLayout = new VerticalLayout();
		subTabLayout.setSizeFull();
		subTabLayout.setMargin(new MarginInfo(false, false, false, false));
		subTabLayout.setSpacing(false);
		campaignDashboardTabComponents.add(subTabLayout);
		tabLayout.addComponent(subTabLayout);
		tabLayout.setExpandRatio(subTabLayout, 1);

		tabSwitcher.addValueChangeListener(e -> {
			final String tabId = (String) e.getProperty().getValue();
			subTabLayout.removeComponent(currentDiagramsWrapper);
			subTabLayout.removeComponent(currentSubTabsWrapper);
			lastTabIdForCampaign.put(dataProvider.getCampaign(), tabId);
			refreshSubTabs(page, tabId, subTabLayout);
		});
		refreshSubTabs(page, (String) tabSwitcher.getValue(), subTabLayout);
	}

	@SuppressWarnings("deprecation")
	private void refreshSubTabs(Page page, String tabId, VerticalLayout subTabLayout) {

		final List<String> subTabs = new ArrayList<>(dataProvider.getSubTabIds(tabId));
		final SubMenu subTabSwitcher = new SubMenu();

		final VerticalLayout subTabSwitcherLayout = new VerticalLayout(subTabSwitcher);
		subTabSwitcherLayout.setMargin(new MarginInfo(false, false, false, true));
		subTabSwitcherLayout.setSpacing(false);
		subTabSwitcherLayout.setId("subTabsOf" + tabId);
		currentSubTabsWrapper = subTabSwitcherLayout;
		subTabLayout.addComponent(subTabSwitcherLayout);
		subTabLayout.setExpandRatio(subTabSwitcherLayout, 0);
		subTabSwitcherLayout.addStyleNames("statistics-sublayout", CssStyles.VSPACE_3);

		subTabs.forEach(subTabId -> subTabSwitcher.addView(subTabId, subTabId, (e) -> {
			subTabLayout.removeComponent(currentDiagramsWrapper);
			if (lastSubTabIdForTabIdAndCampaign.containsKey(dataProvider.getCampaign())) {
				lastSubTabIdForTabIdAndCampaign.get(dataProvider.getCampaign()).put(tabId, subTabId);
			} else {
				final HashMap<String, String> subTabMap = new HashMap<>();
				subTabMap.put(tabId, subTabId);
				lastSubTabIdForTabIdAndCampaign.put(dataProvider.getCampaign(), subTabMap);
			}
			refreshDiagrams(page, subTabLayout, tabId, subTabId);
		}));
		if (!(subTabs.size() > 1)) {
			subTabSwitcherLayout.setVisible(false);
		}
		final Map<String, String> subTabMap = lastSubTabIdForTabIdAndCampaign.get(dataProvider.getCampaign());
		final String lastSubTabId = subTabMap != null ? subTabMap.get(tabId) : null;
		final String activeSubTab = subTabs.isEmpty() ? StringUtils.EMPTY : lastSubTabId != null ? lastSubTabId : subTabs.get(0);
		subTabSwitcher.setActiveView(activeSubTab);

		refreshDiagrams(page, subTabLayout, tabId, activeSubTab);
	}

	private void refreshDiagrams(Page page, VerticalLayout layout, String tabId, String subTabId) {
		final Page.Styles styles = page.getStyles();

		dataProvider.refreshDiagramsData(tabId, subTabId);

		Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> campaignFormDataMap = dataProvider.getCampaignFormDataMap();

		if (campaignFormDataMap != null && !campaignFormDataMap.isEmpty()) {
			final List<CampaignDashboardElement> dashboardElements =
				campaignFormDataMap.keySet().stream().map(CampaignDashboardDiagramDto::getCampaignDashboardElement).collect(Collectors.toList());

			final GridTemplateAreaCreator gridTemplateAreaCreator = new GridTemplateAreaCreator(dashboardElements);

			final VerticalLayout diagramsWrapper = new VerticalLayout();
			diagramsWrapper.setMargin(new MarginInfo(false, true, false, true));
			diagramsWrapper.setId(tabId + "_" + subTabId);
			currentDiagramsWrapper = diagramsWrapper;

			diagramsWrapper.setWidth(
				dashboardElements.size() == 1 && gridTemplateAreaCreator.getGridColumns() == 1 ? gridTemplateAreaCreator.getWidthsSum() : 100,
				Unit.PERCENTAGE);
			diagramsWrapper.setHeight(gridTemplateAreaCreator.getGridContainerHeight(), Unit.PERCENTAGE);

			final CssLayout diagramsLayout = new CssLayout();
			diagramsLayout.setSizeFull();
			final String gridCssClass = (tabId + subTabId).replaceAll("[^a-zA-Z]+", "") + generateRandomString() + GRID_CONTAINER;

			styles.add(
				createDiagramGridStyle(
					gridCssClass,
					gridTemplateAreaCreator.getFormattedGridTemplate(),
					gridTemplateAreaCreator.getGridRows(),
					gridTemplateAreaCreator.getGridColumns()));
			diagramsLayout.setStyleName(gridCssClass);

			campaignFormDataMap.forEach((campaignDashboardDiagramDto, diagramData) -> {
				final CampaignDiagramDefinitionDto campaignDiagramDefinitionDto = campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto();
				final String diagramId = campaignDiagramDefinitionDto.getDiagramId();
				final String diagramCssClass = diagramId + generateRandomString();

				final CampaignDashboardDiagramComponent diagramComponent = new CampaignDashboardDiagramComponent(
					campaignDiagramDefinitionDto,
					diagramData,
					dataProvider.getCampaignFormTotalsMap().get(campaignDashboardDiagramDto),
					campaignDiagramDefinitionDto.isPercentageDefault(),
					dataProvider.getCampaignJurisdictionLevelGroupBy());
				styles.add(createDiagramStyle(diagramCssClass, diagramId));
				diagramComponent.setStyleName(diagramCssClass);

				diagramsLayout.addComponent(diagramComponent);
			});
			diagramsWrapper.addComponent(diagramsLayout);

			diagramsWrapper.setVisible(true);
			layout.addComponent(diagramsWrapper);
			layout.setExpandRatio(diagramsWrapper, 1);
		}
	}

	private void cleanupDashboard(Page page) {
		campaignDashboardTabComponents
			.forEach(campaignDashboardDiagramComponents -> dashboardLayout.removeComponent(campaignDashboardDiagramComponents));
		campaignDashboardTabComponents.clear();
		campaignDashboardDiagramStyles.forEach(s -> page.getJavaScript().execute(removeStyles(s)));
		campaignDashboardDiagramStyles.clear();
	}

	private String generateRandomString() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	private String createDiagramGridStyle(String gridCssClass, String gridAreasTemplate, int rows, int columns) {
		final String s = "." + gridCssClass;
		campaignDashboardDiagramStyles.add(s);
		return s + "{ display: grid; grid-gap:1%; grid-auto-columns: " + (100 / columns - 1) + "%; grid-auto-rows: " + (100 / rows - 1)
			+ "%; grid-template-areas:" + gridAreasTemplate + "; }";
	}

	private String createDiagramStyle(String diagramCssClass, String diagramId) {
		final String s = "." + diagramCssClass;
		campaignDashboardDiagramStyles.add(s);
		return s + "{ grid-area: " + diagramId + "; }";
	}

	private String removeStyles(String styleInnerText) {
		return "var diagramStyles = document.getElementsByTagName('style');  for (var i=0; i < diagramStyles.length; i++) {"
			+ "if (diagramStyles[i].innerText.startsWith(\"" + styleInnerText
			+ "\" )) { diagramStyles[i].parentNode.removeChild(diagramStyles[i]);}}";
	}
}
