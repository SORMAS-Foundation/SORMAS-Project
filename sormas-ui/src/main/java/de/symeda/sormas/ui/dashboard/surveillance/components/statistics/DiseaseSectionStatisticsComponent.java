package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseSectionStatisticsComponent extends DashboardStatisticsSubComponent {

	private final Label totalLabel;

	public DiseaseSectionStatisticsComponent(String titleCaption) {
		this(titleCaption, null, null);
	}

	public DiseaseSectionStatisticsComponent(String titleCaption, String description) {
		this(titleCaption, description, null);
	}

	public DiseaseSectionStatisticsComponent(String titleCaption, String description, String infoIconText) {
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);

		// count
		totalLabel = new Label();
		CssStyles.style(
			totalLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		if (StringUtils.isNotBlank(description)) {
			totalLabel.setDescription(I18nProperties.getDescription(description));
		}
		headerLayout.addComponent(totalLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(titleCaption));
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(titleLabel);

		if (StringUtils.isNotBlank(infoIconText)) {
			Label infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			CssStyles.style(infoIcon, CssStyles.LABEL_LARGE, CssStyles.LABEL_SECONDARY, "statistics-info-label", CssStyles.HSPACE_LEFT_4);
			infoIcon.setDescription(infoIconText, ContentMode.HTML);
			headerLayout.addComponent(infoIcon);
		}

		addComponent(headerLayout);
	}

	protected void updateTotalLabel(String value) {
		totalLabel.setValue(value);
	}

	protected void buildCountLayout(DashboardStatisticsCountElement... dashboardStatisticsCountElements) {
		CssLayout countLayout = createCountLayout(true);
		for (DashboardStatisticsCountElement dashboardStatisticsCountElement : dashboardStatisticsCountElements) {
			addComponentToCountLayout(countLayout, dashboardStatisticsCountElement);
		}
		addComponent(countLayout);
	}
}
