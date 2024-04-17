/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.events;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CaseUuidRenderer;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.PathogenTestResultTypeRenderer;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class EventParticipantsGrid extends FilteredGrid<EventParticipantIndexDto, EventParticipantCriteria> {

	private static final String CASE_ID = Captions.EventParticipant_caseUuid;
	private static final String NO_CASE_CREATE = null;

	public EventParticipantsGrid(EventParticipantCriteria criteria) {

		super(EventParticipantIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantsViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		Column<EventParticipantIndexDto, String> caseIdColumn = addColumn(entry -> {
			if (entry.getCaseUuid() != null) {
				return entry.getCaseUuid();
			}

			boolean isInJurisdiction = entry.getInJurisdiction();
			if (!isInJurisdiction) {
				return NO_CASE_CREATE;
			}

			return "";
		});
		caseIdColumn.setId(CASE_ID);
		caseIdColumn.setSortProperty(EventParticipantIndexDto.CASE_UUID);
		caseIdColumn.setRenderer(new CaseUuidRenderer(uuid -> {
			// '!=' check is ok because the converter returns the constant when no case creation is allowed

			return NO_CASE_CREATE != uuid && UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_CREATE);
		}));

		Column<EventParticipantIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		Language userLanguage = I18nProperties.getUserLanguage();
		String[] columns = new String[] {};
		columns = ArrayUtils.addAll(
			columns,
			EventParticipantIndexDto.UUID,
			EventParticipantIndexDto.PERSON_UUID,
			EventParticipantIndexDto.FIRST_NAME,
			EventParticipantIndexDto.LAST_NAME,
			EventParticipantIndexDto.SEX,
			EventParticipantIndexDto.APPROXIMATE_AGE,
			EventParticipantIndexDto.INVOLVEMENT_DESCRIPTION);

		if (UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_VIEW)) {
			columns = ArrayUtils.add(columns, CASE_ID);
		}

		columns = ArrayUtils.addAll(
			columns,
			EventParticipantIndexDto.CONTACT_COUNT,
			SampleIndexDto.PATHOGEN_TEST_RESULT,
			SampleIndexDto.SAMPLE_DATE_TIME,
			EventParticipantIndexDto.VACCINATION_STATUS,
			DELETE_REASON_COLUMN);

		setColumns(columns);

		((Column<EventParticipantIndexDto, Date>) getColumn(SampleIndexDto.SAMPLE_DATE_TIME))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<EventParticipantIndexDto, String>) getColumn(EventParticipantIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<EventParticipantIndexDto, String>) getColumn(EventParticipantIndexDto.PERSON_UUID)).setRenderer(new UuidRenderer());

		final Column<EventParticipantIndexDto, PathogenTestResultType> pathogenTestResultColumn =
			(Column<EventParticipantIndexDto, PathogenTestResultType>) getColumn(SampleIndexDto.PATHOGEN_TEST_RESULT);
		pathogenTestResultColumn.setRenderer(new PathogenTestResultTypeRenderer());
		for (Column<EventParticipantIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(EventParticipantIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}
		getColumn(SampleIndexDto.PATHOGEN_TEST_RESULT)
			.setCaption(I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.PATHOGEN_TEST_RESULT));
		getColumn(SampleIndexDto.PATHOGEN_TEST_RESULT).setSortable(false);
		getColumn(SampleIndexDto.SAMPLE_DATE_TIME)
			.setCaption(I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.SAMPLE_DATE_TIME));
		getColumn(SampleIndexDto.SAMPLE_DATE_TIME).setSortable(false);

		getColumn(EventParticipantIndexDto.CONTACT_COUNT).setSortable(false);

		if (UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_VIEW)) {
			addItemClickListener(new ShowDetailsListener<>(CASE_ID, false, e -> {
				if (e.getCaseUuid() != null) {
					ControllerProvider.getCaseController().navigateToCase(e.getCaseUuid());
				} else if (e.getInJurisdiction()) {
					EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(e.getUuid());
					ControllerProvider.getCaseController().createFromEventParticipant(eventParticipant);
				}
			}));
		}
		addItemClickListener(new ShowDetailsListener<>(EventParticipantIndexDto.PERSON_UUID, e -> {
			if (UiUtil.enabled(FeatureType.PERSON_MANAGEMENT)) {
				ControllerProvider.getPersonController().navigateToPerson(e.getPersonUuid());
			} else {
				ControllerProvider.getEventParticipantController().navigateToData(e.getUuid());
			}
		}));
		addItemClickListener(
			new ShowDetailsListener<>(
				EventParticipantIndexDto.UUID,
				e -> ControllerProvider.getEventParticipantController().navigateToData(e.getUuid())));
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getEventParticipantFacade()::getIndexList, FacadeProvider.getEventParticipantFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(FacadeProvider.getEventParticipantFacade()::getIndexList);
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantsViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}
}
