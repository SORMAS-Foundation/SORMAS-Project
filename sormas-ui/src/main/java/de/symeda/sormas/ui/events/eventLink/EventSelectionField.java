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

import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventCriteriaDateType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventHelper;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
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
	private final boolean allowCreation;
	private final Consumer<EventCriteria> setDefaultFilters;
	EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
	Button applyButton;
	HorizontalLayout weekAndDateFilterLayout;

	public EventSelectionField(Disease disease, String infoPickOrCreateEvent, Consumer<EventCriteria> setDefaultFilters) {
		this.setDefaultFilters = setDefaultFilters;
		this.searchField = new TextField();
		this.infoPickOrCreateEvent = infoPickOrCreateEvent;
		this.allowCreation = true;

		this.criteria = new EventCriteria();
		criteria.setDisease(disease);
		criteria.setUserFilterIncluded(false);
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		if (setDefaultFilters != null) {
			setDefaultFilters.accept(criteria);
		}

		this.weekAndDateFilterLayout = buildWeekAndDateFilter();
		initializeGrid();
	}

	public static EventSelectionField forSuperordinateEvent(EventDto eventDto, Set<String> excludedUuids) {

		EventSelectionField eventSelectionField = new EventSelectionField(
			eventDto.getDisease(),
			I18nProperties.getString(Strings.infoPickOrCreateSuperordinateEventForEvent),
			eventCriteria -> {
				eventCriteria.setExcludedUuids(excludedUuids);
				eventCriteria.eventDateBetween(
					null,
					EventHelper.getStartOrEndDate(eventDto.getStartDate(), eventDto.getEndDate()),
					null,
					DateFilterOption.DATE);
			});

		eventSelectionField.weekAndDateFilter.getDateToFilter().addValueChangeListener(valueChangeEvent -> {
			Date selectedToDate = eventSelectionField.weekAndDateFilter.getDateToFilter().getValue();
			prepareSuperordinateFilters(eventDto, eventSelectionField, selectedToDate, eventSelectionField.weekAndDateFilter.getDateToFilter());
		});

		eventSelectionField.weekAndDateFilter.getWeekToFilter().addValueChangeListener(valueChangeEvent -> {
			EpiWeek epiWeek = (EpiWeek) eventSelectionField.weekAndDateFilter.getWeekToFilter().getValue();
			Date epiWeekEndDate = DateHelper.getEpiWeekEnd(epiWeek);
			prepareSuperordinateFilters(eventDto, eventSelectionField, epiWeekEndDate, eventSelectionField.weekAndDateFilter.getWeekToFilter());
		});

		return eventSelectionField;
	}

	private static void prepareSuperordinateFilters(
		EventDto eventDto,
		EventSelectionField eventSelectionField,
		Date selectedToDate,
		AbstractComponent component) {
		boolean isSelectedDateAfterEventDate =
			selectedToDate == null || selectedToDate.before(EventHelper.getStartOrEndDate(eventDto.getStartDate(), eventDto.getEndDate()));
		eventSelectionField.applyButton.setEnabled(isSelectedDateAfterEventDate);
		if (isSelectedDateAfterEventDate) {
			component.setComponentError(null);
		} else {
			component.setComponentError(new ErrorMessage() {

				@Override
				public ErrorLevel getErrorLevel() {
					return ErrorLevel.ERROR;
				}

				@Override
				public String getFormattedHtmlMessage() {
					return I18nProperties.getValidationError(Validations.eventSuperordinateEventToDateFilterValidation);
				}
			});
		}
	}

	public static EventSelectionField forSubordinateEvent(EventDto eventDto, Set<String> excludedUuids) {

		EventSelectionField eventSelectionField = new EventSelectionField(
			eventDto.getDisease(),
			I18nProperties.getString(Strings.infoPickOrCreateSuperordinateEventForEvent),
			eventCriteria -> {
				eventCriteria.setExcludedUuids(excludedUuids);
				eventCriteria.eventDateBetween(
					EventHelper.getStartOrEndDate(eventDto.getStartDate(), eventDto.getEndDate()),
					null,
					null,
					DateFilterOption.DATE);
				eventCriteria.setHasNoSuperordinateEvent(Boolean.TRUE);
			});

		eventSelectionField.weekAndDateFilter.getDateFromFilter().addValueChangeListener(valueChangeEvent -> {
			Date selectedFromDate = eventSelectionField.weekAndDateFilter.getDateFromFilter().getValue();
			prepareSubordinateFilters(eventDto, eventSelectionField, selectedFromDate, eventSelectionField.weekAndDateFilter.getDateFromFilter());
		});

		eventSelectionField.weekAndDateFilter.getWeekFromFilter().addValueChangeListener(valueChangeEvent -> {
			EpiWeek epiWeek = (EpiWeek) eventSelectionField.weekAndDateFilter.getWeekFromFilter().getValue();
			Date epiWeekStartDate = DateHelper.getEpiWeekStart(epiWeek);
			prepareSubordinateFilters(eventDto, eventSelectionField, epiWeekStartDate, eventSelectionField.weekAndDateFilter.getWeekFromFilter());
		});

		return eventSelectionField;
	}

	private static void prepareSubordinateFilters(
		EventDto eventDto,
		EventSelectionField eventSelectionField,
		Date selectedFromDate,
		AbstractComponent component) {
		boolean isSelectedDateBeforeEventDate = true;
		Date startOfEndEventDate = EventHelper.getStartOrEndDate(eventDto.getStartDate(), eventDto.getEndDate());
		if (startOfEndEventDate != null) {
			isSelectedDateBeforeEventDate = selectedFromDate == null || selectedFromDate.after(startOfEndEventDate);
		}
		eventSelectionField.applyButton.setEnabled(isSelectedDateBeforeEventDate);
		if (isSelectedDateBeforeEventDate) {
			component.setComponentError(null);
		} else {
			component.setComponentError(new ErrorMessage() {

				@Override
				public ErrorLevel getErrorLevel() {
					return ErrorLevel.ERROR;
				}

				@Override
				public String getFormattedHtmlMessage() {
					return I18nProperties.getValidationError(Validations.eventSubordinateEventFromDateFilterValidation);
				}
			});
		}
	}

	public EventSelectionField(Set<String> excludedUuids) {
		this.setDefaultFilters = null;
		this.searchField = new TextField();
		this.infoPickOrCreateEvent = I18nProperties.getString(Strings.infoPickOrCreateEventGroupForEvent);
		this.allowCreation = false;

		this.criteria = new EventCriteria();
		criteria.setExcludedUuids(excludedUuids);
		criteria.setUserFilterIncluded(true);
		criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		this.weekAndDateFilterLayout = buildWeekAndDateFilter();
		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoPickOrCreateEvent));
	}

	private void addSelectEventRadioGroup() {
		// No need to display the select radio if creation is not allowed
		if (!allowCreation) {
			return;
		}
		rbSelectEvent = new RadioButtonGroup<>();
		rbSelectEvent.setItems(SELECT_EVENT);
		rbSelectEvent.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.eventSelect));
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
			if (e.getAllSelectedItems().size() > 0 && rbCreateEvent != null) {
				rbCreateEvent.setValue(null);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getAllSelectedItems().isEmpty());
			}
		});
	}

	private void addCreateEventRadioGroup() {
		if (!allowCreation) {
			return;
		}

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
		filterLayout.addComponent(weekAndDateFilterLayout);

		mainLayout.addComponent(filterLayout);
		addSelectEventRadioGroup();
		mainLayout.addComponent(eventGrid);
		addCreateEventRadioGroup();

		if (rbSelectEvent != null) {
			rbSelectEvent.setValue(SELECT_EVENT);
		}

		return mainLayout;
	}

	@Override
	protected void doSetValue(EventIndexDto newValue) {
		if (rbSelectEvent != null) {
			rbSelectEvent.setValue(SELECT_EVENT);
		}

		if (newValue != null) {
			eventGrid.select(newValue);
		}
	}

	@Override
	public EventIndexDto getValue() {

		if (eventGrid != null) {
			return eventGrid.getSelectedItems().stream().findFirst().orElse(null);
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

		applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, null);

		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateTo));

		applyButton.addClickListener(e -> {

			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate = null;
			Date toDate = null;
			if (dateFilterOption == DateFilterOption.DATE) {
				if (weekAndDateFilter.getDateFromFilter().getValue() != null) {
					fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				}
				if (weekAndDateFilter.getDateToFilter().getValue() != null) {
					toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
				}
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}

			if (setDefaultFilters != null) {
				EventCriteria defaultCriteria = new EventCriteria();
				setDefaultFilters.accept(defaultCriteria);
				fromDate = fromDate == null ? defaultCriteria.getEventDateFrom() : fromDate;
				toDate = toDate == null ? defaultCriteria.getEventDateTo() : toDate;
			}

			applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			criteria.eventDateBetween(fromDate, toDate, EventCriteriaDateType.EVENT_DATE, dateFilterOption);
			eventGrid.setCriteria(criteria);
			eventGrid.getSelectedItems();
		});

		Button resetButton = ButtonHelper.createButton(Captions.caseEventsResetDateFilter, null);

		resetButton.addClickListener(e -> {

			weekAndDateFilter.getDateFromFilter().setValue(null);
			weekAndDateFilter.getDateToFilter().setValue(null);
			weekAndDateFilter.getWeekFromFilter().setValue(null);
			weekAndDateFilter.getWeekToFilter().setValue(null);

			criteria.freeText(null);
			if (setDefaultFilters != null) {
				setDefaultFilters.accept(criteria);
			} else {
				criteria.eventDateBetween(null, null, null, DateFilterOption.DATE);
			}

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
