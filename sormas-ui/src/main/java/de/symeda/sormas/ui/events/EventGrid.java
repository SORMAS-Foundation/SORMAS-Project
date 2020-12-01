/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("serial")
public class EventGrid extends FilteredGrid<EventIndexDto, EventCriteria> {

	public static final String EVENT_DATE = Captions.singleDayEventDate;
	public static final String INFORMATION_SOURCE = Captions.Event_informationSource;
	public static final String NUMBER_OF_PENDING_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	@SuppressWarnings("unchecked")
	public <V extends View> EventGrid(EventCriteria criteria, Class<V> viewClass) {

		super(EventIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		Column<EventIndexDto, String> diseaseShortColumn = addColumn(entry -> DiseaseHelper.toString(entry.getDisease(), entry.getDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(EventIndexDto.DISEASE);

		Column<EventIndexDto, String> informationSourceColumn = addColumn(
			event -> event.getSrcType() == EventSourceType.HOTLINE_PERSON
				? buildSourcePersonText(event)
				: event.getSrcType() == EventSourceType.MEDIA_NEWS ? buildSourceMediaText(event) : "");
		informationSourceColumn.setId(INFORMATION_SOURCE);
		informationSourceColumn.setSortable(false);

		Language userLanguage = I18nProperties.getUserLanguage();

		boolean tasksFeatureEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT);
		if (tasksFeatureEnabled) {
			Column<EventIndexDto, String> pendingTasksColumn = addColumn(
					entry -> String.format(
							I18nProperties.getCaption(Captions.formatSimpleNumberFormat),
							FacadeProvider.getTaskFacade().getPendingTaskCountByEvent(entry.toReference())));
			pendingTasksColumn.setId(NUMBER_OF_PENDING_TASKS);
			pendingTasksColumn.setSortable(false);
		}

		List<String> columnIds = new ArrayList(Arrays.asList(
			EventIndexDto.UUID,
			EventIndexDto.EVENT_STATUS,
			EventIndexDto.EVENT_INVESTIGATION_STATUS,
			createEventDateColumn(this, userLanguage),
			DISEASE_SHORT,
			EventIndexDto.EVENT_TITLE,
			EventIndexDto.EVENT_LOCATION,
			EventIndexDto.SRC_TYPE,
			INFORMATION_SOURCE,
			EventIndexDto.REPORT_DATE_TIME,
			NUMBER_OF_PENDING_TASKS,
			EventIndexDto.PARTICIPANT_COUNT));

		if (!tasksFeatureEnabled) {
			columnIds.remove(NUMBER_OF_PENDING_TASKS);
		}

		setColumns(columnIds.toArray(new String[columnIds.size()]));

		getColumn(EventIndexDto.PARTICIPANT_COUNT).setSortable(false);

		((Column<EventIndexDto, String>) getColumn(EventIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<EventIndexDto, Date>) getColumn(EventIndexDto.REPORT_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column<EventIndexDto, ?> column : getColumns()) {
			String columnId = column.getId();
			column.setCaption(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, columnId, column.getCaption()));
			column.setStyleGenerator(
				FieldAccessColumnStyleGenerator
					.getDefault(getBeanType(), INFORMATION_SOURCE.equals(columnId) ? EventIndexDto.SRC_FIRST_NAME : columnId));
		}

		addItemClickListener(new ShowDetailsListener<>(EventIndexDto.UUID, e -> ControllerProvider.getEventController().navigateToData(e.getUuid())));
	}

	public static String createEventDateColumn(FilteredGrid<EventIndexDto, EventCriteria> grid, Language userLanguage) {
		Column<EventIndexDto, String> eventDateColumn = grid.addColumn(event -> {
			Date startDate = event.getStartDate();
			Date endDate = event.getEndDate();

			if (startDate == null) {
				return "";
			} else if (endDate == null) {
				return DateHelper.formatLocalDate(startDate, userLanguage);
			} else {
				return String
					.format("%s - %s", DateHelper.formatLocalDate(startDate, userLanguage), DateHelper.formatLocalDate(endDate, userLanguage));
			}
		});
		eventDateColumn.setId(EVENT_DATE);
		eventDateColumn.setSortProperty(EventDto.START_DATE);
		eventDateColumn.setSortable(true);

		return EVENT_DATE;
	}

	private String buildSourcePersonText(EventIndexDto event) {
		String srcFirstName = event.getSrcFirstName();
		String srcLastName = event.getSrcLastName();
		String srcTelNo = event.getSrcTelNo();

		if (FieldAccessHelper.isAllInaccessible(srcFirstName, srcLastName, srcTelNo)) {
			return I18nProperties.getCaption(Captions.inaccessibleValue);
		}

		return (srcFirstName != null ? srcFirstName : "") + " " + (srcLastName != null ? srcLastName : "")
			+ (srcTelNo != null && !srcTelNo.isEmpty() ? " (" + srcTelNo + ")" : "");
	}

	private String buildSourceMediaText(EventIndexDto event) {
		String srcMediaWebsite = event.getSrcMediaWebsite();
		String srcMediaName = event.getSrcMediaName();

		if (FieldAccessHelper.isAllInaccessible(srcMediaWebsite, srcMediaName)) {
			return I18nProperties.getCaption(Captions.inaccessibleValue);
		}

		return (srcMediaWebsite != null ? srcMediaWebsite : "") + " " + (srcMediaName != null ? "(" + srcMediaName + ")" : "");
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(EventsView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<EventIndexDto, EventCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getEventFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getEventFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<EventIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getEventFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}
}
