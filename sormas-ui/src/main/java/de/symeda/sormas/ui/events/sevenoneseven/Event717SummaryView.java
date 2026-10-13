/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.events.sevenoneseven;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.EventsView;
import de.symeda.sormas.ui.events.EventsViewConfiguration;
import de.symeda.sormas.ui.events.EventsViewType;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * 7-1-7 performance of the assessed events, opened from the 7-1-7 view of the event directory. It has its own filters, which are
 * kept for the session but not put into the URL, because the event criteria only parse event date types from it.
 */
@SuppressWarnings("serial")
public class Event717SummaryView extends AbstractView {

	public static final String VIEW_NAME = EventsView.VIEW_NAME + "/717summary";

	private final EventCriteria criteria;
	private final Event717SummaryFilterForm filterForm;
	private final Event717SummaryLayout summaryLayout;

	public Event717SummaryView() {

		super(VIEW_NAME);

		// the page scrolls as a whole, like the dashboards
		setHeightUndefined();
		CssStyles.style(getViewTitleLabel(), CssStyles.PAGE_TITLE);

		criteria = ViewModelProviders.of(Event717SummaryView.class).get(EventCriteria.class, createDefaultCriteria());

		Button backButton = ButtonHelper.createIconButton(Captions.event717BackToDirectory, VaadinIcons.ARROW_LEFT, e -> {
			ViewModelProviders.of(EventsView.class).get(EventsViewConfiguration.class).setViewType(EventsViewType.EVENT_717);
			SormasUI.get().getNavigator().navigateTo(EventsView.VIEW_NAME);
		});
		addHeaderComponent(backButton);

		if (UiUtil.permitted(UserRight.EVENT_EXPORT)) {
			Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);
			exportButton.setDescription(I18nProperties.getString(Strings.infoEvent717SummaryExport));
			new FileDownloader(Event717SummaryLayout.createExportResource(() -> criteria)).extend(exportButton);
			addHeaderComponent(exportButton);
		}

		filterForm = new Event717SummaryFilterForm();
		filterForm.addApplyHandler(e -> {
			if (filterForm.applyUnboundFilters()) {
				refresh();
			}
		});
		filterForm.addResetHandler(e -> {
			resetCriteria();
			filterForm.setValue(criteria);
			refresh();
		});

		summaryLayout = new Event717SummaryLayout();

		VerticalLayout content = new VerticalLayout(filterForm, summaryLayout);
		content.setWidth(100, Unit.PERCENTAGE);
		content.setMargin(true);
		content.setSpacing(false);
		addComponent(content);
	}

	public static boolean isAvailable() {
		return Event717AssessmentView.isAvailable();
	}

	private static EventCriteria createDefaultCriteria() {

		EventCriteria defaultCriteria = new EventCriteria();
		defaultCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		// like the event directory, the period applies to the event date unless another date type is selected
		defaultCriteria.eventDateBetween(null, null, null, DateFilterOption.DATE);
		return defaultCriteria;
	}

	private void resetCriteria() {

		EventCriteria defaults = createDefaultCriteria();
		criteria.setDisease(null);
		criteria.region(null);
		criteria.district(null);
		criteria.relevanceStatus(defaults.getRelevanceStatus());
		criteria.eventDateBetween(null, null, defaults.getEventDateType(), defaults.getDateFilterOption());
	}

	private void refresh() {
		summaryLayout.refresh(criteria);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		filterForm.setValue(criteria);
		refresh();
	}
}
