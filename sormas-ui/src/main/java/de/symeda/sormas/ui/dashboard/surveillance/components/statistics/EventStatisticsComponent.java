package de.symeda.sormas.ui.dashboard.surveillance.components.statistics;

import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.statistics.CountElementStyle;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsCountElement;
import de.symeda.sormas.ui.dashboard.statistics.DashboardStatisticsSubComponent;
import de.symeda.sormas.ui.utils.CssStyles;

public class EventStatisticsComponent extends DashboardStatisticsSubComponent {

	private final Label eventCountLabel;
	private final DashboardStatisticsCountElement eventStatusConfirmed;
	private final DashboardStatisticsCountElement eventStatusPossible;
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
		CssLayout countLayout = createCountLayout(true);
		eventStatusConfirmed = new DashboardStatisticsCountElement(EventStatus.EVENT.toString(), CountElementStyle.CRITICAL);
		addComponentToCountLayout(countLayout, eventStatusConfirmed);
		eventStatusPossible = new DashboardStatisticsCountElement(EventStatus.SIGNAL.toString(), CountElementStyle.IMPORTANT);
		addComponentToCountLayout(countLayout, eventStatusPossible);
		eventStatusNotAnEvent = new DashboardStatisticsCountElement(EventStatus.DROPPED.toString(), CountElementStyle.POSITIVE);
		addComponentToCountLayout(countLayout, eventStatusNotAnEvent);
		addComponent(countLayout);
	}

	public void update(Map<EventStatus, Long> events) {
		eventCountLabel.setValue(events.values().stream().collect(Collectors.summingLong(Long::longValue)).toString());

		eventStatusConfirmed.updateCountLabel(events.getOrDefault(EventStatus.EVENT, 0L).toString());
		eventStatusPossible.updateCountLabel(events.getOrDefault(EventStatus.SIGNAL, 0L).toString());
		eventStatusNotAnEvent.updateCountLabel(events.getOrDefault(EventStatus.DROPPED, 0L).toString());
	}
}
