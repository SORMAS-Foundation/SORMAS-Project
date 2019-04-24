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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class ContactGrid extends FilteredGrid<ContactIndexDto, ContactCriteria> {

	public static final String NUMBER_OF_VISITS = Captions.Contact_numberOfVisits;
	public static final String NUMBER_OF_PENDING_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	@SuppressWarnings("unchecked")
	public ContactGrid() {
		super(ContactIndexDto.class);
		setSizeFull();

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}
		
		DataProvider<ContactIndexDto,ContactCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> FacadeProvider.getContactFacade().getIndexList(
						UserProvider.getCurrent().getUuid(), query.getFilter().orElse(null), query.getOffset(), query.getLimit(), 
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
							.collect(Collectors.toList())).stream(),
				query -> {
					return (int)FacadeProvider.getContactFacade().count(
						UserProvider.getCurrent().getUuid(), query.getFilter().orElse(null));
				});
		setDataProvider(dataProvider);

		Column<ContactIndexDto, String> diseaseShortColumn = addColumn(entry -> 
			DiseaseHelper.toString(entry.getCaseDisease(), entry.getCaseDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(ContactIndexDto.CASE_DISEASE);

		Column<ContactIndexDto, String> visitsColumn = addColumn(entry -> {
			if (DiseaseHelper.hasContactFollowUp(entry.getCaseDisease())) {
				int numberOfVisits = FacadeProvider.getVisitFacade().getNumberOfVisits(entry.toReference(), null);
				int numberOfRequiredVisits = ContactLogic.getNumberOfRequiredVisitsSoFar(entry.getReportDateTime(), entry.getFollowUpUntil());
				int numberOfMissedVisits = numberOfRequiredVisits - numberOfVisits;
				// Set number of missed visits to 0 when more visits than expected have been done
				if (numberOfMissedVisits < 0) {
					numberOfMissedVisits = 0;
				}
				return String.format(I18nProperties.getCaption(Captions.formatNumberOfVisitsFormat),
						numberOfVisits, numberOfMissedVisits);
			} else {
				return "-";
			}

		});
		visitsColumn.setId(NUMBER_OF_VISITS);
		visitsColumn.setSortable(false);
		
		Column<ContactIndexDto, String> pendingTasksColumn = addColumn(entry -> 
			String.format(I18nProperties.getCaption(Captions.formatSimpleNumberFormat), 
				FacadeProvider.getTaskFacade().getPendingTaskCountByContact(entry.toReference())));
		pendingTasksColumn.setId(NUMBER_OF_PENDING_TASKS);
		pendingTasksColumn.setSortable(false);

		setColumns(ContactIndexDto.UUID, DISEASE_SHORT, ContactIndexDto.CONTACT_CLASSIFICATION, ContactIndexDto.CONTACT_STATUS,
				ContactIndexDto.PERSON, ContactIndexDto.CONTACT_PROXIMITY,
				ContactIndexDto.FOLLOW_UP_STATUS, NUMBER_OF_VISITS, NUMBER_OF_PENDING_TASKS);
		getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
		((Column<ContactIndexDto, String>)getColumn(ContactIndexDto.UUID)).setRenderer(new UuidRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(
					ContactIndexDto.I18N_PREFIX, column.getId().toString(), column.getCaption()));
		}
		
		addItemClickListener(e ->  {
			if ((e.getColumn() != null && CaseIndexDto.UUID.equals(e.getColumn().getId()))
					|| e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getContactController().navigateToData(e.getItem().getUuid());
			}
		});
	}
	
	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (getCriteria().getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP) {
			this.getColumn(NUMBER_OF_VISITS).setHidden(true);
		} else {
			this.getColumn(NUMBER_OF_VISITS).setHidden(false);
		}

		getDataProvider().refreshAll();
	}
}


