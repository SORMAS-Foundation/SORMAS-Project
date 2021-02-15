/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.eventLink;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventHelper;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EventSelectionField extends CustomField<EventIndexDto> {

	public static final String SELECT_EVENT = "selectEvent";
	public static final String CREATE_EVENT = "createEvent";

	private VerticalLayout mainLayout;
	private EventSelectionGrid eventGrid;
	private final String infoPickOrCreateEvent;

	private RadioButtonGroup<String> rbSelectEvent;
	private RadioButtonGroup<String> rbCreateEvent;
	private Consumer<Boolean> selectionChangeCallback;
	private final TextField searchField;
	private final EventCriteria criteria;

	public EventSelectionField(Disease disease, String infoPickOrCreateEvent) {
		this.searchField = new TextField();
		this.infoPickOrCreateEvent = infoPickOrCreateEvent;

		this.criteria = new EventCriteria();
		criteria.setDisease(disease);
		criteria.setUserFilterIncluded(false);
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);

		initializeGrid();
	}

	public EventSelectionField(EventDto event, Set<String> excludedUuids, boolean selectSuperordinateEvent) {
		this.searchField = new TextField();
		this.infoPickOrCreateEvent = I18nProperties.getString(Strings.infoPickOrCreateSuperordinateEventForEvent);

		this.criteria = new EventCriteria();
		criteria.setDisease(event.getDisease());
		criteria.setExcludedUuids(excludedUuids);
		criteria.setUserFilterIncluded(false);
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);

		if (!selectSuperordinateEvent) {
			criteria.eventDateFrom(EventHelper.getStartOrEndDate(event.getStartDate(), event.getEndDate()));
			// Users are not allowed to select a subordinate event that already has a superordinate event
			criteria.setHasNoSuperordinateEvent(Boolean.TRUE);
		} else {
			criteria.eventDateTo(EventHelper.getStartOrEndDate(event.getStartDate(), event.getEndDate()));
		}

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoPickOrCreateEvent));
	}

	private void addSelectEventRadioGroup() {
		rbSelectEvent = new RadioButtonGroup<>();
		rbSelectEvent.setItems(SELECT_EVENT);
		rbSelectEvent.setItemCaptionGenerator((item) -> {
			return I18nProperties.getCaption(Captions.eventSelect);
		});
		CssStyles.style(rbSelectEvent, CssStyles.VSPACE_NONE);
		rbSelectEvent.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateEvent.setValue(null);
				eventGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(eventGrid.getSelectedItems().size() > 0);

				}
			}
		});

		mainLayout.addComponent(rbSelectEvent);
	}

	public void initializeGrid() {

		eventGrid = new EventSelectionGrid(criteria);
		eventGrid.addSelectionListener(e -> {
			if (e.getAllSelectedItems().size() > 0) {
				rbCreateEvent.setValue(null);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getAllSelectedItems().isEmpty());
			}
		});
	}

	private void addCreateEventRadioGroup() {
		rbCreateEvent = new RadioButtonGroup<>();
		rbCreateEvent.setItems(CREATE_EVENT);
		rbCreateEvent.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.eventNewEvent));
		rbCreateEvent.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectEvent.setValue(null);
				eventGrid.deselectAll();
				eventGrid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreateEvent);
	}

	@Override
	protected Component initContent() {

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterLayout.addComponent(createFilterBar());
		filterLayout.addComponent(buildWeekAndDateFilter());

		mainLayout.addComponent(filterLayout);
		addSelectEventRadioGroup();
		mainLayout.addComponent(eventGrid);
		addCreateEventRadioGroup();

		rbSelectEvent.setValue(SELECT_EVENT);

		return mainLayout;
	}

	@Override
	protected void doSetValue(EventIndexDto newValue) {
		rbSelectEvent.setValue(SELECT_EVENT);

		if (newValue != null) {
			eventGrid.select(newValue);
		}
	}

	@Override
	public EventIndexDto getValue() {

		if (eventGrid != null) {
			EventIndexDto value = eventGrid.getSelectedItems().stream().findFirst().orElse(null);
			return value;
		}

		return null;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		filterLayout.setSizeUndefined();

		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);

		searchField.setCaption(I18nProperties.getString(Strings.promptEventsSearchField));

		searchField.addValueChangeListener(e -> updateGrid(e.getValue()));

		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	private void updateGrid(String freeText) {
		criteria.setFreeText(freeText);

		eventGrid.setCriteria(criteria);
		eventGrid.getSelectedItems();
	}

	public HorizontalLayout buildWeekAndDateFilter() {

		Button applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, null);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateTo));

		applyButton.addClickListener(e -> {

			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate, toDate;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}

			if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
				applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
				criteria.eventDateBetween(fromDate, toDate, dateFilterOption);

			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					Notification notification = new Notification(
						I18nProperties.getString(Strings.headingMissingDateFilter),
						I18nProperties.getString(Strings.messageMissingDateFilter),
						Notification.Type.WARNING_MESSAGE,
						false);
					notification.setDelayMsec(-1);
					notification.show(Page.getCurrent());
				} else {
					Notification notification = new Notification(
						I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
						I18nProperties.getString(Strings.messageMissingEpiWeekFilter),
						Notification.Type.WARNING_MESSAGE,
						false);
					notification.setDelayMsec(-1);
					notification.show(Page.getCurrent());
				}
			}
			eventGrid.setCriteria(criteria);
			eventGrid.getSelectedItems();
		});

		Button resetButton = ButtonHelper.createButton(Captions.caseEventsResetDateFilter, null);

		resetButton.addClickListener(e -> {

			weekAndDateFilter.getDateFromFilter().setValue(null);
			weekAndDateFilter.getDateToFilter().setValue(null);
			criteria.eventDateBetween(null, null, DateFilterOption.DATE);

			eventGrid.setCriteria(criteria);
			eventGrid.getSelectedItems();
		});

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);
		dateFilterRowLayout.addComponent(applyButton);
		dateFilterRowLayout.addComponent(resetButton);

		return dateFilterRowLayout;
	}

	public EventSelectionGrid getEventGrid() {
		return eventGrid;
	}
}
