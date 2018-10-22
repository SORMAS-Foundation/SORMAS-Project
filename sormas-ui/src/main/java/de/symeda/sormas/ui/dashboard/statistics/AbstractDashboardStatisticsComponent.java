package de.symeda.sormas.ui.dashboard.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public abstract class AbstractDashboardStatisticsComponent extends VerticalLayout {

	protected static final String FIRST_LOC = "firstLoc";
	protected static final String SECOND_LOC = "secondLoc";
	protected static final String THIRD_LOC = "thirdLoc";
	protected static final String FOURTH_LOC = "fourthLoc";

	protected final DashboardDataProvider dashboardDataProvider;

	protected CustomLayout subComponentsLayout;
	protected DashboardStatisticsSubComponent firstComponent;
	protected DashboardStatisticsSubComponent secondComponent;
	protected DashboardStatisticsSubComponent thirdComponent;
	protected DashboardStatisticsSubComponent fourthComponent;
	private Button showMoreButton;
	private Button showLessButton;

	protected Disease previousDisease;
	protected Disease currentDisease;

	public AbstractDashboardStatisticsComponent(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		this.setWidth(100, Unit.PERCENTAGE);
		this.setMargin(new MarginInfo(true, true, false, true));

		subComponentsLayout = new CustomLayout();
		subComponentsLayout.setTemplateContents(
				LayoutUtil.fluidRow(
						LayoutUtil.fluidColumnLoc(3, 0, 6, 0, FIRST_LOC), 
						LayoutUtil.fluidColumnLoc(3, 0, 6, 0, SECOND_LOC),
						LayoutUtil.fluidColumnLoc(3, 0, 6, 0, THIRD_LOC),
						LayoutUtil.fluidColumnLoc(3, 0, 6, 0, FOURTH_LOC)));
		subComponentsLayout.setWidth(100, Unit.PERCENTAGE);

		addFirstComponent();
		addSecondComponent();
		addThirdComponent();
		addFourthComponent();

		addComponent(subComponentsLayout);

		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			if (DiseaseHelper.getAllDiseasesWithFollowUp().size() > 6) {
				addShowMoreAndLessButtons();
			}
		} else if (Disease.values().length > 6) {
			addShowMoreAndLessButtons();
		}
	}

	protected abstract void addFirstComponent();
	protected abstract void addSecondComponent();
	protected abstract void addThirdComponent();
	protected abstract void addFourthComponent();

	protected abstract void updateFirstComponent(int visibleDiseasesCount);
	protected abstract void updateSecondComponent(int visibleDiseasesCount);
	protected abstract void updateThirdComponent(int visibleDiseasesCount);
	protected abstract void updateFourthComponent(int visibleDiseasesCount);

	protected abstract int getNormalHeight();
	protected abstract int getFullHeight();
	protected abstract int getFilteredHeight();

	public void updateStatistics(Disease disease) {
		previousDisease = currentDisease;
		currentDisease = disease;

		if (showMoreButton != null && showLessButton != null) {
			if (currentDisease != null) {
				showMoreButton.setVisible(false);
				showLessButton.setVisible(false);
			} else if (!showLessButton.isVisible() && !showMoreButton.isVisible()) {
				if (!showMoreButton.isVisible() && !showLessButton.isVisible()) {
					showMoreButton.setVisible(true);
				}
			}
		}

		int visibleDiseasesCount = currentDisease == null ? (isFullMode() ? Disease.values().length : 6) : 0; 
		updateFirstComponent(visibleDiseasesCount);
		updateSecondComponent(visibleDiseasesCount);
		updateThirdComponent(visibleDiseasesCount);
		updateFourthComponent(visibleDiseasesCount);

		// fixed height makes sure they stack correct when screen gets smaller
		if (isFullMode()) {
			firstComponent.setHeight(getFullHeight(), Unit.PIXELS);
			secondComponent.setHeight(getFullHeight(), Unit.PIXELS);
			thirdComponent.setHeight(getFullHeight(), Unit.PIXELS);
			fourthComponent.setHeight(getFullHeight(), Unit.PIXELS);
		} else if (currentDisease == null) {
			firstComponent.setHeight(getNormalHeight(), Unit.PIXELS);
			secondComponent.setHeight(getNormalHeight(), Unit.PIXELS);
			thirdComponent.setHeight(getNormalHeight(), Unit.PIXELS);
			fourthComponent.setHeight(getNormalHeight(), Unit.PIXELS);
		} else {
			firstComponent.setHeight(getFilteredHeight(), Unit.PIXELS);
			secondComponent.setHeight(getFilteredHeight(), Unit.PIXELS);
			thirdComponent.setHeight(getFilteredHeight(), Unit.PIXELS);
			fourthComponent.setHeight(getFilteredHeight(), Unit.PIXELS);
		}
	}

	private void addShowMoreAndLessButtons() {
		showMoreButton = new Button("Show All Diseases", FontAwesome.CHEVRON_DOWN);
		CssStyles.style(showMoreButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.VSPACE_3);
		showLessButton = new Button("Show First 6 Diseases", FontAwesome.CHEVRON_UP);
		CssStyles.style(showLessButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.VSPACE_3);

		showMoreButton.addClickListener(e -> {
			showMoreButton.setVisible(false);
			showLessButton.setVisible(true);
			updateStatistics(currentDisease);
		});

		showLessButton.addClickListener(e -> {
			showLessButton.setVisible(false);
			showMoreButton.setVisible(true);
			updateStatistics(currentDisease);
		});

		addComponent(showMoreButton);
		addComponent(showLessButton);
		setComponentAlignment(showMoreButton, Alignment.MIDDLE_CENTER);
		setComponentAlignment(showLessButton, Alignment.MIDDLE_CENTER);
		showLessButton.setVisible(false);
	}

	private boolean isFullMode() {
		return showLessButton != null && showLessButton.isVisible();
	}

	protected List<Map.Entry<Disease, Integer>> createSortedDiseaseList(Map<Disease, Integer> diseaseMap) {
		List<Map.Entry<Disease, Integer>> sortedDiseaseList = new ArrayList<>(diseaseMap.entrySet());
		Collections.sort(sortedDiseaseList, new Comparator<Map.Entry<Disease, Integer>>() {
			public int compare(Map.Entry<Disease, Integer> e1, Map.Entry<Disease, Integer> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedDiseaseList;
	}

	public int calculateGrowth(int currentCount, int previousCount) {
		return currentCount == 0 ?
				(previousCount > 0 ? -100 : 0) : 
					previousCount == 0 ? 
							(currentCount > 0 ? Integer.MIN_VALUE : 0) : 
								Math.round(((currentCount - previousCount * 1.0f) / previousCount) * 100.0f);
	}

}
