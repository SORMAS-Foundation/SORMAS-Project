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

import com.vaadin.navigator.View;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventHelper;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class EventActionsGrid extends FilteredGrid<EventActionIndexDto, EventCriteria> {

	public static final String EVENT_DATE = Captions.singleDayEventDate;
	public static final String ACTION_LAST_MODIFIED_BY_OR_CREATOR = "actionLastModifiedByOrCreator";
	public static final String EVENT_EVOLUTION_DATE = Captions.singleDayEventEvolutionDate;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	@SuppressWarnings("unchecked")
	public <V extends View> EventActionsGrid(EventCriteria eventCriteria, Class<V> viewClass) {

		super(EventActionIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		setLazyDataProvider();
		setCriteria(eventCriteria);

		Language userLanguage = I18nProperties.getUserLanguage();

		createDeletionReasonColumn();

		List<String> columnIds = new ArrayList<>(
			Arrays.asList(
				EventActionIndexDto.EVENT_UUID,
				EventActionIndexDto.EVENT_TITLE,
				createDiseaseColumn(this),
				EventActionIndexDto.EVENT_DISEASE_VARIANT,
				EventActionIndexDto.EVENT_IDENTIFICATION_SOURCE,
				createEventDateColumn(this),
				createEventEvolutionDateColumn(this),
				EventActionIndexDto.EVENT_STATUS,
				EventActionIndexDto.EVENT_RISK_LEVEL,
				EventActionIndexDto.EVENT_INVESTIGATION_STATUS,
				EventActionIndexDto.EVENT_MANAGEMENT_STATUS,
				EventActionIndexDto.EVENT_REPORTING_USER,
				EventActionIndexDto.EVENT_RESPONSIBLE_USER,
				EventActionIndexDto.ACTION_TITLE,
				EventActionIndexDto.ACTION_CREATION_DATE,
				EventActionIndexDto.ACTION_CHANGE_DATE,
				EventActionIndexDto.ACTION_DATE,
				EventActionIndexDto.ACTION_STATUS,
				EventActionIndexDto.ACTION_PRIORITY,
				createLastModifiedByOrCreatorColumn(this)));

		if (UiUtil.permitted(UserRight.EVENT_DELETE)) {
			columnIds.add(DELETE_REASON_COLUMN);
		}

		setColumns(columnIds.toArray(new String[columnIds.size()]));

		((Column<EventActionIndexDto, String>) getColumn(EventActionIndexDto.EVENT_UUID)).setRenderer(new UuidRenderer());
		((Column<EventActionIndexDto, Date>) getColumn(EventActionIndexDto.ACTION_CREATION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<EventActionIndexDto, Date>) getColumn(EventActionIndexDto.ACTION_CHANGE_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<EventActionIndexDto, Date>) getColumn(EventActionIndexDto.ACTION_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		for (Column<EventActionIndexDto, ?> column : getColumns()) {
			String columnId = column.getId();
			column.setCaption(I18nProperties.getPrefixCaption(EventActionIndexDto.I18N_PREFIX, columnId, column.getCaption()));
		}

		addItemClickListener(
			new ShowDetailsListener<>(EventActionIndexDto.EVENT_UUID, e -> ControllerProvider.getEventController().navigateToData(e.getEventUuid())));
	}

	private String createDiseaseColumn(FilteredGrid<EventActionIndexDto, EventCriteria> grid) {
		Column<EventActionIndexDto, String> diseaseShortColumn =
			grid.addColumn(event -> DiseaseHelper.toString(event.getEventDisease(), event.getEventDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(EventActionIndexDto.EVENT_DISEASE);

		return DISEASE_SHORT;
	}

	private String createEventDateColumn(FilteredGrid<EventActionIndexDto, EventCriteria> grid) {
		Column<EventActionIndexDto, String> eventDateColumn =
			grid.addColumn(event -> EventHelper.buildEventDateString(event.getEventStartDate(), event.getEventEndDate()));
		eventDateColumn.setId(EVENT_DATE);
		eventDateColumn.setSortProperty(EventActionIndexDto.EVENT_START_DATE);
		eventDateColumn.setSortable(true);

		return EVENT_DATE;
	}

	private String createLastModifiedByOrCreatorColumn(FilteredGrid<EventActionIndexDto, EventCriteria> grid) {

		grid.addColumn(event -> {
			if (event.getActionLastModifiedBy() != null && event.getActionLastModifiedBy().getUuid() != null) {
				return event.getActionLastModifiedBy();
			} else {
				return event.getActionCreatorUser();
			}
		})
			.setId(ACTION_LAST_MODIFIED_BY_OR_CREATOR)
			.setSortProperty(EventActionIndexDto.ACTION_LAST_MODIFIED_BY)
			.setCaption(I18nProperties.getPrefixCaption(EventActionIndexDto.I18N_PREFIX, EventActionIndexDto.ACTION_LAST_MODIFIED_BY));

		return ACTION_LAST_MODIFIED_BY_OR_CREATOR;
	}

	private String createEventEvolutionDateColumn(FilteredGrid<EventActionIndexDto, EventCriteria> grid) {
		Column<EventActionIndexDto, String> eventDateColumn = grid.addColumn(event -> DateFormatHelper.formatDate(event.getEventEvolutionDate()));
		eventDateColumn.setId(EVENT_EVOLUTION_DATE);
		eventDateColumn.setSortProperty(EventActionIndexDto.EVENT_EVOLUTION_DATE);
		eventDateColumn.setSortable(true);

		return EVENT_EVOLUTION_DATE;
	}

	private void createDeletionReasonColumn() {
		if (UiUtil.permitted(UserRight.EVENT_DELETE)) {
			Column<EventActionIndexDto, String> deleteColumn = addColumn(entry -> {
				if (entry.getDeletionReason() != null) {
					return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
				} else {
					return "-";
				}
			});
			deleteColumn.setId(DELETE_REASON_COLUMN);
			deleteColumn.setSortable(false);
			deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));
		}
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getActionFacade()::getEventActionList, FacadeProvider.getActionFacade()::countEventActions);
	}
}
