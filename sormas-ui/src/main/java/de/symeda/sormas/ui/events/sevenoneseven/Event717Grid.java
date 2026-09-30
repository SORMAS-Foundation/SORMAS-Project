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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717IndexDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessCalculator;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

/**
 * 7-1-7 view of the event directory: the timeliness of the assessed events, laid out and colored like the "Assess 7-1-7 results"
 * sheet of the 7-1-7 data consolidation spreadsheet. Cells hold plain text so that the basic export stays readable.
 */
@SuppressWarnings("serial")
public class Event717Grid extends FilteredGrid<Event717IndexDto, EventCriteria> {

	private static final String DISEASE_SHORT = Captions.columnDiseaseShort;
	private static final String STYLE_NOT_APPLICABLE = "event717-na";

	@SuppressWarnings("unchecked")
	public Event717Grid(EventCriteria eventCriteria) {

		super(Event717IndexDto.class);
		setSizeFull();

		setLazyDataProvider(FacadeProvider.getEvent717AssessmentFacade()::getIndexList, FacadeProvider.getEvent717AssessmentFacade()::count);
		setCriteria(eventCriteria);

		addColumn(dto -> DiseaseHelper.toString(dto.getEventDisease(), dto.getEventDiseaseDetails())).setId(DISEASE_SHORT)
			.setSortProperty(Event717IndexDto.EVENT_DISEASE);
		replaceColumn(Event717IndexDto.DATE_OF_EMERGENCE, dto -> DateFormatHelper.formatDate(dto.getDateOfEmergence()));

		List<String> columnIds = new ArrayList<>();
		columnIds.add(Event717IndexDto.EVENT_UUID);
		columnIds.add(Event717IndexDto.EVENT_TITLE);
		columnIds.add(DISEASE_SHORT);
		columnIds.add(Event717IndexDto.DATE_OF_EMERGENCE);
		columnIds.add(Event717IndexDto.REGION);
		columnIds.add(Event717IndexDto.DISTRICT);
		columnIds.add(Event717IndexDto.COMMUNITY);
		columnIds.add(addIntervalColumn(Event717IndexDto.DETECTION_DAYS, Event717IndexDto::getDetectionDays, Event717IndexDto::getDetectionStatus));
		columnIds.add(
			addIntervalColumn(Event717IndexDto.NOTIFICATION_DAYS, Event717IndexDto::getNotificationDays, Event717IndexDto::getNotificationStatus));
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			columnIds.add(addEarlyResponseActionColumn(action));
		}
		columnIds.add(addIntervalColumn(Event717IndexDto.RESPONSE_DAYS, Event717IndexDto::getResponseDays, Event717IndexDto::getResponseStatus));
		columnIds.add(addTimelinessStatusColumn());

		setColumns(columnIds.toArray(new String[0]));

		((Column<Event717IndexDto, String>) getColumn(Event717IndexDto.EVENT_UUID)).setRenderer(new UuidRenderer());

		for (Column<Event717IndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(Event717IndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
		}
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			// the full action name is too long for the column header
			getColumn(Event717IndexDto.getEarlyResponseActionDaysProperty(action)).setDescriptionGenerator(dto -> action.toString());
		}

		addItemClickListener(
			new ShowDetailsListener<>(
				Event717IndexDto.EVENT_UUID,
				dto -> SormasUI.get().getNavigator().navigateTo(Event717AssessmentView.VIEW_NAME + "/" + dto.getEventUuid())));
	}

	/**
	 * Replaces the column that was generated for the bean property, so the property name can be kept as column id and sort property.
	 */
	private Column<Event717IndexDto, String> replaceColumn(String propertyId, Function<Event717IndexDto, String> valueProvider) {

		if (getColumn(propertyId) != null) {
			removeColumn(propertyId);
		}
		Column<Event717IndexDto, String> column = addColumn(valueProvider::apply);
		column.setId(propertyId);
		column.setSortProperty(propertyId);
		return column;
	}

	private String addIntervalColumn(
		String propertyId,
		Function<Event717IndexDto, Integer> daysProvider,
		Function<Event717IndexDto, Event717TimelinessStatus> statusProvider) {

		Column<Event717IndexDto, String> column =
			replaceColumn(propertyId, dto -> formatInterval(daysProvider.apply(dto), statusProvider.apply(dto)));
		column.setStyleGenerator(dto -> getStatusStyle(statusProvider.apply(dto)));
		column.setDescriptionGenerator(dto -> statusProvider.apply(dto) != null ? statusProvider.apply(dto).toString() : null);
		return propertyId;
	}

	private String addEarlyResponseActionColumn(Event717EarlyResponseAction action) {

		String propertyId = Event717IndexDto.getEarlyResponseActionDaysProperty(action);
		Column<Event717IndexDto, String> column = replaceColumn(propertyId, dto -> {
			if (dto.isEarlyResponseActionNotApplicable(action)) {
				return Event717TimelinessCalculator.NOT_APPLICABLE;
			}
			return formatInterval(dto.getEarlyResponseActionDays(action), getEarlyResponseActionStatus(dto, action));
		});
		column.setStyleGenerator(
			dto -> dto.isEarlyResponseActionNotApplicable(action) ? STYLE_NOT_APPLICABLE : getStatusStyle(getEarlyResponseActionStatus(dto, action)));
		return propertyId;
	}

	private String addTimelinessStatusColumn() {

		Column<Event717IndexDto, String> column = replaceColumn(
			Event717IndexDto.TIMELINESS_STATUS,
			dto -> dto.getTimelinessStatus() != null ? dto.getTimelinessStatus().toString() : "");
		column.setStyleGenerator(dto -> getStatusStyle(dto.getTimelinessStatus()));
		return Event717IndexDto.TIMELINESS_STATUS;
	}

	private static Event717TimelinessStatus getEarlyResponseActionStatus(Event717IndexDto dto, Event717EarlyResponseAction action) {
		return Event717TimelinessCalculator.calculateEarlyResponseActionStatus(dto.getEarlyResponseActionDays(action));
	}

	private static String formatInterval(Integer days, Event717TimelinessStatus status) {

		if (status == null) {
			return "";
		}
		switch (status) {
		case WITHIN_TARGET:
		case OVER_TARGET:
			return Event717TimelinessCalculator.formatDays(days);
		case DATA_ERROR:
			return "! " + Event717TimelinessCalculator.formatDays(days);
		default:
			return status.toString();
		}
	}

	private static String getStatusStyle(Event717TimelinessStatus status) {

		if (status == null) {
			return null;
		}
		switch (status) {
		case WITHIN_TARGET:
			return "event717-met";
		case OVER_TARGET:
			return "event717-not-met";
		case DATA_ERROR:
			return "event717-data-error";
		default:
			return "event717-missing";
		}
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
