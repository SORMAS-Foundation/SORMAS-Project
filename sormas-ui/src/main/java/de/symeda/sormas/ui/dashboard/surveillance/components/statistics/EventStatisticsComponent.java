package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final Label eventCountLabel;
	private final DashboardStatisticsCountElement eventStatusConfirmed;
	private final DashboardStatisticsCountElement eventStatusCluster;
	private final DashboardStatisticsCountElement eventStatusPossible;
	private final DashboardStatisticsCountElement eventStatusScreening;
	private final DashboardStatisticsCountElement eventStatusNotAnEvent;

	public EventStatisticsComponent() {
		// Header
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setMargin(false);
		headerLayout.setSpacing(false);

		// count
		eventCountLabel = new Label();
		CssStyles.style(
			eventCountLabel,
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_XXXLARGE,
			CssStyles.LABEL_BOLD,
			CssStyles.VSPACE_4,
			CssStyles.VSPACE_TOP_NONE);
		headerLayout.addComponent(eventCountLabel);
		// title
		Label titleLabel = new Label(I18nProperties.getCaption(Captions.dashboardNewEvents));
		CssStyles.style(titleLabel, CssStyles.H2, CssStyles.HSPACE_LEFT_4);
		headerLayout.addComponent(titleLabel);

		addComponent(headerLayout);

		// Count layout
		eventStatusCluster = new DashboardStatisticsCountElement(EventStatus.CLUSTER.toString(), CountElementStyle.CRITICAL);
		eventStatusConfirmed = new DashboardStatisticsCountElement(EventStatus.EVENT.toString(), CountElementStyle.IMPORTANT);
		eventStatusPossible = new DashboardStatisticsCountElement(EventStatus.SIGNAL.toString(), CountElementStyle.RELEVANT);
		eventStatusScreening = new DashboardStatisticsCountElement(EventStatus.SCREENING.toString(), CountElementStyle.NEUTRAL);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement(EventStatus.DROPPED.toString(), CountElementStyle.POSITIVE);
		buildCountLayout(eventStatusCluster, eventStatusConfirmed, eventStatusPossible, eventStatusScreening, eventStatusNotAnEvent);
	}

	public void update(Map<EventStatus, Long> events) {
		eventCountLabel.setValue(((Long) events.values().stream().mapToLong(Long::longValue).sum()).toString());

		eventStatusCluster.updateCountLabel(events.getOrDefault(EventStatus.CLUSTER, 0L).toString());
		eventStatusConfirmed.updateCountLabel(events.getOrDefault(EventStatus.EVENT, 0L).toString());
		eventStatusPossible.updateCountLabel(events.getOrDefault(EventStatus.SIGNAL, 0L).toString());
		eventStatusScreening.updateCountLabel(events.getOrDefault(EventStatus.SCREENING, 0L).toString());
		eventStatusNotAnEvent.updateCountLabel(events.getOrDefault(EventStatus.DROPPED, 0L).toString());
	}
}
