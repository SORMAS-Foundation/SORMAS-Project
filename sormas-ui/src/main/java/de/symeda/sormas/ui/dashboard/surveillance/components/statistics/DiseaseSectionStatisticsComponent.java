package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import com.vaadin.ui.CssLayout;

import de.symeda.sormas.ui.dashboard.components.DashboardHeadingComponent;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;

public class DiseaseSectionStatisticsComponent extends DashboardStatisticsSubComponent {

	private final DashboardHeadingComponent heading;

	public DiseaseSectionStatisticsComponent(String titleCaption, String infoIconText) {
		heading = new DashboardHeadingComponent(titleCaption, infoIconText);
		addComponent(heading);
	}

//	public DiseaseSectionStatisticsComponent(String titleCaption, String description) {
//		this(titleCaption, description, null);
//	}

//	public DiseaseSectionStatisticsComponent(String titleCaption, String description, String infoIconText) {
//		// Header
//		HorizontalLayout headerLayout = new HorizontalLayout();
//		headerLayout.setMargin(false);
//		headerLayout.setSpacing(false);
//
//		// count
//		totalLabel = new Label();
//		CssStyles.style(
//			totalLabel,
//			CssStyles.LABEL_PRIMARY,
//			CssStyles.LABEL_XXXLARGE,
//			CssStyles.LABEL_BOLD,
//			CssStyles.VSPACE_4,
//			CssStyles.VSPACE_TOP_NONE);
//		if (StringUtils.isNotBlank(description)) {
//			totalLabel.setDescription(I18nProperties.getDescription(description));
//		}
//		headerLayout.addComponent(totalLabel);
//		// title
//		Label titleLabel = new Label(I18nProperties.getCaption(titleCaption));
//		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
//		headerLayout.addComponent(titleLabel);
//
//		if (StringUtils.isNotBlank(infoIconText)) {
//			Label infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
//			CssStyles.style(infoIcon, CssStyles.LABEL_LARGE, CssStyles.LABEL_SECONDARY, "statistics-info-label", CssStyles.HSPACE_LEFT_4);
//			infoIcon.setDescription(infoIconText, ContentMode.HTML);
//			headerLayout.addComponent(infoIcon);
//		}
//
//		addComponent(headerLayout);
//	}

	public DiseaseSectionStatisticsComponent(String titleCaption, String description, String infoIconText) {
		this(titleCaption, infoIconText);
		heading.setTotalLabelDescription(description);
	}

	protected void updateTotalLabel(String value) {
		heading.updateTotalLabel(value);
	}

	public void hideHeading() {
		heading.setVisible(false);
	}

	protected void buildCountLayout(DashboardStatisticsCountElement... dashboardStatisticsCountElements) {
		CssLayout countLayout = createCountLayout(true);
		for (DashboardStatisticsCountElement dashboardStatisticsCountElement : dashboardStatisticsCountElements) {
			addComponentToCountLayout(countLayout, dashboardStatisticsCountElement);
		}
		addComponent(countLayout);
	}
}
