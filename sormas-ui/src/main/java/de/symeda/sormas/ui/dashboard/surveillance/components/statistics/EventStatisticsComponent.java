package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;

public class EventStatisticsComponent extends DiseaseSectionStatisticsComponent {

	private final DashboardStatisticsCountElement eventStatusConfirmed;
	private final DashboardStatisticsCountElement eventStatusCluster;
	private final DashboardStatisticsCountElement eventStatusPossible;
	private final DashboardStatisticsCountElement eventStatusScreening;
	private final DashboardStatisticsCountElement eventStatusNotAnEvent;

	public EventStatisticsComponent() {
		super(Captions.dashboardNewEvents);

		// Count layout
		eventStatusCluster = new DashboardStatisticsCountElement(EventStatus.CLUSTER.toString(), CountElementStyle.CRITICAL);
		eventStatusConfirmed = new DashboardStatisticsCountElement(EventStatus.EVENT.toString(), CountElementStyle.IMPORTANT);
		eventStatusPossible = new DashboardStatisticsCountElement(EventStatus.SIGNAL.toString(), CountElementStyle.RELEVANT);
		eventStatusScreening = new DashboardStatisticsCountElement(EventStatus.SCREENING.toString(), CountElementStyle.NEUTRAL);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement(EventStatus.DROPPED.toString(), CountElementStyle.POSITIVE);
		buildCountLayout(eventStatusCluster, eventStatusConfirmed, eventStatusPossible, eventStatusScreening, eventStatusNotAnEvent);
	}

	public void update(Map<EventStatus, Long> events) {
		updateTotalLabel(((Long) events.values().stream().mapToLong(Long::longValue).sum()).toString());

		eventStatusCluster.updateCountLabel(events.getOrDefault(EventStatus.CLUSTER, 0L).toString());
		eventStatusConfirmed.updateCountLabel(events.getOrDefault(EventStatus.EVENT, 0L).toString());
		eventStatusPossible.updateCountLabel(events.getOrDefault(EventStatus.SIGNAL, 0L).toString());
		eventStatusScreening.updateCountLabel(events.getOrDefault(EventStatus.SCREENING, 0L).toString());
		eventStatusNotAnEvent.updateCountLabel(events.getOrDefault(EventStatus.DROPPED, 0L).toString());
	}
}
