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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.campaign.diagram.CampaignDashboardElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.CssStyles;

public class CampaignDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	public static final String GRID_CONTAINER = "grid-container";

	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;

	private List<VerticalLayout> campaignDashboardDiagramComponents = new ArrayList<>();
	private List<String> campaignDashboardDiagramStyles = new ArrayList<>();

	private GridTemplateAreaCreator gridTemplateAreaCreator = new GridTemplateAreaCreator();

	public CampaignDashboardView() {
		super(VIEW_NAME);

		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider);
		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setMargin(false);

		dashboardSwitcher.setValue(DashboardType.CAMPAIGNS);
		dashboardSwitcher.addValueChangeListener(e -> navigateToDashboardView(e));

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoCampaignsDashboard));
		dashboardLayout.setHeightUndefined();
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
		dataProvider.refreshData();

		final VerticalLayout tabLayout = new VerticalLayout();
		tabLayout.setSizeFull();
		tabLayout.setMargin(new MarginInfo(false, false, false, false));
		tabLayout.setSpacing(false);
		campaignDashboardDiagramComponents.add(tabLayout);
		dashboardLayout.addComponent(tabLayout);

		final OptionGroup tabSwitcher = new OptionGroup();
		final VerticalLayout tabSwitcherLayout = new VerticalLayout(tabSwitcher);
		tabSwitcherLayout.setMargin(new MarginInfo(false, false, false, true));
		tabSwitcherLayout.setSpacing(false);
		tabLayout.addComponent(tabSwitcherLayout);

		final Map<String, Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>>> campaignFormDataTabMap =
			groupCampaignFormDataByTab(dataProvider.getCampaignFormDataMap());
		final List<String> tabs = new ArrayList<>(campaignFormDataTabMap.keySet());
		tabs.forEach(tabId -> {
			tabSwitcher.addItem(tabId);
			tabSwitcher.setItemCaption(tabId, tabId);
		});
		if (!(tabs.size() > 1)) {
			tabSwitcherLayout.setVisible(false);
		}
		CssStyles.style(tabSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);

		tabSwitcher.addValueChangeListener(e -> {
			String tabId = (String) e.getProperty().getValue();
			tabLayout.iterator().forEachRemaining(component -> {
				if (tabId.equals(component.getId())) {
					component.setVisible(true);
				} else if (component.getId() != null) {
					component.setVisible(false);
				}
			});
		});

		final Page.Styles styles = page.getStyles();

		campaignFormDataTabMap.forEach((tabId, campaignFormDataMap) -> {

			final VerticalLayout diagramsWrapper = new VerticalLayout();
			diagramsWrapper.setMargin(new MarginInfo(false, true, false, true));
			diagramsWrapper.setId(tabId);
			diagramsWrapper.setSizeFull();

			final CssLayout diagramsLayout = new CssLayout();
			diagramsLayout.setId(tabId);
			diagramsLayout.setSizeFull();
			final String gridCssClass = tabId.replaceAll("[^a-zA-Z]+", "") + generateRandomString() + GRID_CONTAINER;
			final List<CampaignDashboardElement> dashboardElements = campaignFormDataMap.keySet()
				.stream()
				.map(campaignDashboardDiagramDto -> campaignDashboardDiagramDto.getCampaignDashboardElement())
				.collect(Collectors.toList());

			styles.add(createDiagramGridStyle(gridCssClass, dashboardElements));
			diagramsLayout.setStyleName(gridCssClass);

			campaignFormDataMap.forEach((campaignDashboardDiagramDto, diagramData) -> {
				final CampaignDiagramDefinitionDto campaignDiagramDefinitionDto = campaignDashboardDiagramDto.getCampaignDiagramDefinitionDto();
				final String diagramId = campaignDiagramDefinitionDto.getDiagramId();
				final String diagramCssClass = diagramId + generateRandomString();
				final CampaignDashboardDiagramComponent diagramComponent =
					new CampaignDashboardDiagramComponent(campaignDiagramDefinitionDto, diagramData);
				styles.add(createDiagramStyle(diagramCssClass, diagramId));
				diagramComponent.setStyleName(diagramCssClass);
				diagramsLayout.addComponent(diagramComponent);
			});
			diagramsWrapper.addComponent(diagramsLayout);

			diagramsWrapper.setVisible(false);
			tabLayout.addComponent(diagramsWrapper);
		});

		tabSwitcher.setValue(tabs.isEmpty() ? StringUtils.EMPTY : tabs.get(0));
	}

	private void cleanupDashboard(Page page) {
		campaignDashboardDiagramComponents
			.forEach(campaignDashboardDiagramComponents -> dashboardLayout.removeComponent(campaignDashboardDiagramComponents));
		campaignDashboardDiagramComponents.clear();
		campaignDashboardDiagramStyles.forEach(s -> page.getJavaScript().execute(removeStyles(s)));
		campaignDashboardDiagramStyles.clear();
	}

	private String generateRandomString() {
		return UUID.randomUUID().toString().substring(0, 6);
	}

	private String createDiagramGridStyle(String gridCssClass, List<CampaignDashboardElement> dashboardElements) {
		String s = "." + gridCssClass;
		campaignDashboardDiagramStyles.add(s);
		return s + "{ display: grid; grid-gap:10px; grid-auto-columns: 1fr; grid-auto-rows: 1fr;" + " grid-template-areas:"
			+ gridTemplateAreaCreator.createGridTemplate(dashboardElements) + "; }";
	}

	private String createDiagramStyle(String diagramCssClass, String diagramId) {
		String s = "." + diagramCssClass;
		campaignDashboardDiagramStyles.add(s);
		final String style = s + "{ grid-area: " + diagramId + "; }";
		return style;
	}

	private String removeStyles(String styleInnerText) {
		return "var diagramStyles = document.getElementsByTagName('style');  for (var i=0; i < diagramStyles.length; i++) {"
			+ "if (diagramStyles[i].innerText.startsWith(\"" + styleInnerText
			+ "\" )) { diagramStyles[i].parentNode.removeChild(diagramStyles[i]);}}";
	}

	private Map<String, Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>>> groupCampaignFormDataByTab(
		Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> campaignFormDataMap) {
		final Map<String, Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>>> campaignFormDataTabMap = new HashMap<>();

		campaignFormDataMap.forEach((campaignDashboardDiagramDto, campaignDiagramDataDtos) -> {
			final String tabId = campaignDashboardDiagramDto.getCampaignDashboardElement().getTabId();
			if (campaignFormDataTabMap.containsKey(tabId)) {
				campaignFormDataTabMap.get(tabId).put(campaignDashboardDiagramDto, campaignDiagramDataDtos);
			} else {
				final Map<CampaignDashboardDiagramDto, List<CampaignDiagramDataDto>> dashboardDiagramDtoListHashMap = new HashMap<>();
				dashboardDiagramDtoListHashMap.put(campaignDashboardDiagramDto, campaignDiagramDataDtos);
				campaignFormDataTabMap.put(tabId, dashboardDiagramDtoListHashMap);
			}
		});
		return campaignFormDataTabMap;
	}
}
