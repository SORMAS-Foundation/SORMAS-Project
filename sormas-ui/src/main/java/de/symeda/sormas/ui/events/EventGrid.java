/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.navigator.View;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupsIndexDto;
import de.symeda.sormas.api.event.EventHelper;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.groups.EventGroupsValueProvider;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class EventGrid extends FilteredGrid<EventIndexDto, EventCriteria> {

	public static final String EVENT_DATE = Captions.singleDayEventDate;
	public static final String EVENT_EVOLUTION_DATE = Captions.Event_evolutionDate;
	public static final String INFORMATION_SOURCE = Captions.Event_informationSource;
	public static final String NUMBER_OF_PENDING_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	private DataProviderListener<EventIndexDto> dataProviderListener;

	@SuppressWarnings("unchecked")
	public <V extends View> EventGrid(EventCriteria criteria, Class<V> viewClass) {

		super(EventIndexDto.class);
		setSizeFull();

		EventsViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(EventsViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		boolean eventGroupsFeatureEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_GROUPS);
		boolean externalSurveillanceToolShareEnabled = FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled();

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

		boolean showPendingTasks = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW);
		if (showPendingTasks) {
			Column<EventIndexDto, String> pendingTasksColumn = addColumn(
				entry -> String.format(
					I18nProperties.getCaption(Captions.formatSimpleNumberFormat),
					FacadeProvider.getTaskFacade().getPendingTaskCountByEvent(entry.toReference())));
			pendingTasksColumn.setId(NUMBER_OF_PENDING_TASKS);
			pendingTasksColumn.setSortable(false);
		}

		boolean specificRiskEnabled = FacadeProvider.getCustomizableEnumFacade().hasEnumValues(CustomizableEnumType.SPECIFIC_EVENT_RISK, null);

		List<String> columnIds = new ArrayList<>(
			Arrays.asList(
				EventIndexDto.UUID,
				EventIndexDto.EXTERNAL_ID,
				EventIndexDto.EXTERNAL_TOKEN,
				EventIndexDto.INTERNAL_TOKEN,
				EventIndexDto.EVENT_STATUS,
				EventIndexDto.RISK_LEVEL));

		if (specificRiskEnabled) {
			columnIds.add(EventIndexDto.SPECIFIC_RISK);
		}

		columnIds.addAll(
			Arrays.asList(
				EventIndexDto.EVENT_INVESTIGATION_STATUS,
				EventIndexDto.EVENT_MANAGEMENT_STATUS,
				EventIndexDto.EVENT_IDENTIFICATION_SOURCE,
				createEventDateColumn(this),
				createEventEvolutionDateColumn(this),
				DISEASE_SHORT,
				EventIndexDto.DISEASE_VARIANT,
				EventIndexDto.EVENT_TITLE));

		if (eventGroupsFeatureEnabled) {
			columnIds.add(EventIndexDto.EVENT_GROUPS);
		}

		columnIds.addAll(
			Arrays.asList(
				EventIndexDto.REGION,
				EventIndexDto.DISTRICT,
				EventIndexDto.COMMUNITY,
				EventIndexDto.ADDRESS,
				EventIndexDto.SRC_TYPE,
				INFORMATION_SOURCE,
				EventIndexDto.REPORT_DATE_TIME,
				EventIndexDto.REPORTING_USER,
				EventIndexDto.RESPONSIBLE_USER));

		if (externalSurveillanceToolShareEnabled) {
			columnIds.addAll(
				Arrays.asList(
					EventIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE,
					EventIndexDto.SURVEILLANCE_TOOL_STATUS,
					EventIndexDto.SURVEILLANCE_TOOL_SHARE_COUNT));
		}

		if (showPendingTasks) {
			columnIds.add(NUMBER_OF_PENDING_TASKS);
		}

		columnIds.addAll(
			Arrays.asList(
				EventIndexDto.PARTICIPANT_COUNT,
				EventIndexDto.CASE_COUNT,
				EventIndexDto.DEATH_COUNT,
				EventIndexDto.CONTACT_COUNT,
				EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT));

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_DELETE)) {
			Column<EventIndexDto, String> deleteColumn = addColumn(entry -> {
				if (entry.getDeletionReason() != null) {
					return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
				} else {
					return "-";
				}
			});
			deleteColumn.setId(DELETE_REASON_COLUMN);
			deleteColumn.setSortable(false);
			deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));
			columnIds.add(DELETE_REASON_COLUMN);
		}

		setColumns(columnIds.toArray(new String[columnIds.size()]));

		getColumn(EventIndexDto.PARTICIPANT_COUNT).setSortable(false);
		getColumn(EventIndexDto.CASE_COUNT).setSortable(false);
		getColumn(EventIndexDto.DEATH_COUNT).setSortable(false);
		getColumn(EventIndexDto.CONTACT_COUNT).setSortable(false);
		getColumn(EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT).setSortable(false);

		if (externalSurveillanceToolShareEnabled) {
			Column<EventIndexDto, Date> shareDateColumn = ((Column<EventIndexDto, Date>) getColumn(EventIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE));
			shareDateColumn.setSortable(false);
			shareDateColumn.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

			getColumn(EventIndexDto.SURVEILLANCE_TOOL_SHARE_COUNT).setSortable(false);
			getColumn(EventIndexDto.SURVEILLANCE_TOOL_STATUS).setSortable(false);
			getColumn(EventIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE).setSortable(false);
		}

		if (eventGroupsFeatureEnabled) {
			Column<EventIndexDto, EventGroupsIndexDto> eventGroupsColumn =
				(Column<EventIndexDto, EventGroupsIndexDto>) getColumn(EventIndexDto.EVENT_GROUPS);
			eventGroupsColumn.setSortable(false);
			eventGroupsColumn.setRenderer(new EventGroupsValueProvider(), new HtmlRenderer());

			addItemClickListener(e -> {
				if (e.getColumn() != null && EventIndexDto.EVENT_GROUPS.equals(e.getColumn().getId())) {
					EventGroupsIndexDto eventGroups = e.getItem().getEventGroups();
					if (eventGroups != null && eventGroups.getEventGroup() != null) {
						ControllerProvider.getEventGroupController().navigateToData(eventGroups.getEventGroup().getUuid());
					}
				}
			});
		}

		setContactCountMethod(EventContactCountMethod.ALL); // Count all contacts by default

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

		getColumn(EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT)
			.setCaption(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.CONTACT_COUNT));

		addItemClickListener(new ShowDetailsListener<>(EventIndexDto.UUID, e -> ControllerProvider.getEventController().navigateToData(e.getUuid())));

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(EventIndexDto.REGION).setHidden(true);
			getColumn(EventIndexDto.DISTRICT).setHidden(true);
			getColumn(EventIndexDto.COMMUNITY).setHidden(true);
		}
	}

	public static String createEventDateColumn(FilteredGrid<EventIndexDto, EventCriteria> grid) {
		Column<EventIndexDto, String> eventDateColumn =
			grid.addColumn(event -> EventHelper.buildEventDateString(event.getStartDate(), event.getEndDate()));
		eventDateColumn.setId(EVENT_DATE);
		eventDateColumn.setSortProperty(EventDto.START_DATE);
		eventDateColumn.setSortable(true);

		return EVENT_DATE;
	}

	public static String createEventEvolutionDateColumn(FilteredGrid<EventIndexDto, EventCriteria> grid) {
		Column<EventIndexDto, String> eventDateColumn = grid.addColumn(event -> DateFormatHelper.formatDate(event.getEvolutionDate()));
		eventDateColumn.setId(EVENT_EVOLUTION_DATE);
		eventDateColumn.setSortProperty(EventDto.EVOLUTION_DATE);
		eventDateColumn.setSortable(true);

		return EVENT_EVOLUTION_DATE;
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

	public void setContactCountMethod(EventContactCountMethod method) {
		getColumn(EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT).setHidden(method == EventContactCountMethod.ALL);
		getColumn(EventIndexDto.CONTACT_COUNT).setHidden(method == EventContactCountMethod.SOURCE_CASE_IN_EVENT);
		if (method == EventContactCountMethod.BOTH_METHODS) {
			getColumn(EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT)
				.setCaption(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT));
		} else {
			getColumn(EventIndexDto.CONTACT_COUNT_SOURCE_IN_EVENT)
				.setCaption(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.CONTACT_COUNT));
		}
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		EventsViewConfiguration viewConfiguration = ViewModelProviders.of(EventsView.class).get(EventsViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getEventFacade()::getIndexList, FacadeProvider.getEventFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(FacadeProvider.getEventFacade()::getIndexList);
	}
}
