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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.jurisdiction.EventParticipantJurisdictionHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CaseUuidRenderer;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class EventParticipantsGrid extends FilteredGrid<EventParticipantIndexDto, EventParticipantCriteria> {

	private static final String CASE_ID = Captions.EventParticipant_caseUuid;
	private static final String NO_CASE_CREATE = null;

	public EventParticipantsGrid(EventParticipantCriteria criteria) {

		super(EventParticipantIndexDto.class);
		setSizeFull();

		setInEagerMode(true);
		setCriteria(criteria);
		setEagerDataProvider();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}

		Column<EventParticipantIndexDto, String> caseIdColumn = addColumn(entry -> {
			if (entry.getCaseUuid() != null) {
				return entry.getCaseUuid();
			}

			boolean isInJurisdiction = FieldAccessColumnStyleGenerator.callJurisdictionChecker(
				EventParticipantJurisdictionHelper::isInJurisdictionOrOwned,
				UserProvider.getCurrent().getUser(),
				entry.getJurisdiction());
			if (!isInJurisdiction) {
				return NO_CASE_CREATE;
			}

			return "";
		});
		caseIdColumn.setId(CASE_ID);
		caseIdColumn.setSortProperty(EventParticipantIndexDto.CASE_UUID);
		caseIdColumn.setRenderer(
			new CaseUuidRenderer(
				uuid -> {
					// '!=' check is ok because the converter returns the constant when no case creation is allowed
					return NO_CASE_CREATE != uuid;
				}));

		setColumns(
			EventParticipantIndexDto.UUID,
			EventParticipantIndexDto.PERSON_UUID,
			EventParticipantIndexDto.FIRST_NAME,
			EventParticipantIndexDto.LAST_NAME,
			EventParticipantIndexDto.SEX,
			EventParticipantIndexDto.APPROXIMATE_AGE,
			EventParticipantIndexDto.INVOLVEMENT_DESCRIPTION,
			CASE_ID,
			EventParticipantIndexDto.CONTACT_COUNT);

		((Column<EventParticipantIndexDto, String>) getColumn(EventParticipantIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<EventParticipantIndexDto, String>) getColumn(EventParticipantIndexDto.PERSON_UUID)).setRenderer(new UuidRenderer());

		for (Column<EventParticipantIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(EventParticipantIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));

			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));

		}

		addItemClickListener(new ShowDetailsListener<>(CASE_ID, false, e -> {
			if (e.getCaseUuid() != null) {
				ControllerProvider.getCaseController().navigateToCase(e.getCaseUuid());
			} else {
				EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(e.getUuid());
				ControllerProvider.getCaseController().createFromEventParticipant(eventParticipant);
			}
		}));
		addItemClickListener(
			new ShowDetailsListener<>(
				EventParticipantIndexDto.UUID,
				e -> ControllerProvider.getEventParticipantController().navigateToData(e.getUuid())));
	}

	public void setEagerDataProvider() {
		ListDataProvider<EventParticipantIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getEventParticipantFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
		setEagerDataProvider();
	}
}
