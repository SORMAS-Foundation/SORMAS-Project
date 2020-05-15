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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public abstract class AbstractContactGrid<IndexDTO extends ContactIndexDto> extends FilteredGrid<IndexDTO, ContactCriteria> {

	public static final String NUMBER_OF_VISITS = Captions.Contact_numberOfVisits;
	public static final String NUMBER_OF_PENDING_TASKS = Captions.columnNumberOfPendingTasks;
	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	Class viewClass;

	public <V extends View> AbstractContactGrid(Class<IndexDTO> beanType, ContactCriteria criteria, Class<V> viewClass) {
		super(beanType);

		this.viewClass = viewClass;

		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS));

		if (isInEagerMode()) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		initColumns();

		addItemClickListener(e -> {
			if ((e.getColumn() != null && ContactIndexDto.UUID.equals(e.getColumn().getId()))
					|| e.getMouseEventDetails().isDoubleClick()) {
				ControllerProvider.getContactController().navigateToData(e.getItem().getUuid());
			}
		});
	}

	protected void initColumns() {
		Column<IndexDTO, String> diseaseShortColumn = addColumn(entry ->
				DiseaseHelper.toString(entry.getDisease(), entry.getDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(ContactIndexDto.DISEASE);

		Column<IndexDTO, String> visitsColumn = addColumn(entry -> {
			if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(entry.getDisease())) {
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

		Column<IndexDTO, String> pendingTasksColumn = addColumn(entry ->
				String.format(I18nProperties.getCaption(Captions.formatSimpleNumberFormat),
						FacadeProvider.getTaskFacade().getPendingTaskCountByContact(entry.toReference())));
		pendingTasksColumn.setId(NUMBER_OF_PENDING_TASKS);
		pendingTasksColumn.setSortable(false);

		setColumns(getColumnList().toArray(String[]::new));
		if (!FacadeProvider.getConfigFacade().isGermanServer()) {
			getColumn(ContactIndexDto.CONTACT_CATEGORY).setHidden(true);
		}
		getColumn(ContactIndexDto.CONTACT_PROXIMITY).setWidth(200);
		((Column<ContactIndexDto, String>) getColumn(ContactIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ContactIndexDto, Date>) getColumn(ContactIndexDto.FOLLOW_UP_UNTIL))
				.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(I18nProperties.findPrefixCaptionWithDefault(column.getId(), column.getCaption(),
					ContactIndexDto.I18N_PREFIX, PersonDto.I18N_PREFIX, LocationDto.I18N_PREFIX));
		}
	}

	protected Stream<String> getColumnList() {
		return Stream.of(
				Stream.of(ContactIndexDto.UUID, DISEASE_SHORT, ContactIndexDto.CONTACT_CLASSIFICATION, ContactIndexDto.CONTACT_STATUS),
				getPersonColumns(),
				Stream.of(ContactIndexDto.CONTACT_CATEGORY, ContactIndexDto.CONTACT_PROXIMITY,
						ContactIndexDto.FOLLOW_UP_STATUS, ContactIndexDto.FOLLOW_UP_UNTIL,
						NUMBER_OF_VISITS,
						NUMBER_OF_PENDING_TASKS))
				.flatMap(s -> s);
	}

	protected Stream<String> getPersonColumns() {
		return Stream.of(ContactIndexDto.PERSON_FIRST_NAME, ContactIndexDto.PERSON_LAST_NAME);
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

		ViewConfiguration viewConfiguration = ViewModelProviders.of(viewClass).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		DataProvider<IndexDTO, ContactCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
				query -> getGridData(
						query.getFilter().orElse(null), query.getOffset(), query.getLimit(),
						query.getSortOrders().stream().map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
								.collect(Collectors.toList())).stream(),
				query -> (int) FacadeProvider.getContactFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<IndexDTO> dataProvider = DataProvider.fromStream(getGridData(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	protected abstract List<IndexDTO> getGridData(ContactCriteria contactCriteria, Integer first, Integer max, List<SortProperty> sortProperties);
}
