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

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventCriteriaDateType;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717DateType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateTypeHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

/**
 * Filters of the 7-1-7 summary: disease, region, district, relevance status and a period on an event or 7-1-7 date.
 */
@SuppressWarnings("serial")
public class Event717SummaryFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final String RELEVANCE_STATUS_FILTER = "relevanceStatusFilter";
	private static final String DATE_FILTER = "dateFilter";

	/**
	 * Created while the super constructor adds the fields, so it must not have an initializer.
	 */
	private ComboBox relevanceStatusFilter;
	private EpiWeekAndDateFilterComponent<CriteriaDateType> dateFilter;

	public Event717SummaryFilterForm() {
		super(EventCriteria.class, EventIndexDto.I18N_PREFIX, JurisdictionFieldConfig.of(LocationDto.REGION, LocationDto.DISTRICT, null));
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EventIndexDto.DISEASE,
			LocationDto.REGION,
			LocationDto.DISTRICT,
			RELEVANCE_STATUS_FILTER,
			DATE_FILTER };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));

		ComboBox regionField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.REGION, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION), 140));
		regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		ComboBox districtField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.DISTRICT, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT), 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		relevanceStatusFilter = createRelevanceStatusFilter();
		getContent().addComponent(relevanceStatusFilter, RELEVANCE_STATUS_FILTER);

		dateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			I18nProperties.getString(Strings.infoEvent717DateFilter),
			ArrayUtils.addAll(CriteriaDateTypeHelper.getTypes(EventCriteriaDateType.class, false), (CriteriaDateType[]) Event717DateType.values()),
			I18nProperties.getString(Strings.promptEventDateType),
			Event717DateType.DATE_OF_NOTIFICATION,
			this);
		getContent().addComponent(dateFilter, DATE_FILTER);
	}

	private static ComboBox createRelevanceStatusFilter() {

		ComboBox filter = ComboBoxHelper.createComboBoxV7();
		filter.setId(RELEVANCE_STATUS_FILTER);
		filter.setWidth(200, Unit.PIXELS);
		filter.setNullSelectionAllowed(false);
		filter.setTextInputAllowed(false);
		filter.addItem(EntityRelevanceStatus.ACTIVE);
		filter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.eventActiveEvents));
		if (UiUtil.permitted(UserRight.EVENT_VIEW_ARCHIVED)) {
			filter.addItem(EntityRelevanceStatus.ARCHIVED);
			filter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.eventArchivedEvents));
			filter.addItem(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED);
			filter.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(Captions.eventAllActiveAndArchivedEvents));
		}
		return filter;
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {

		if (LocationDto.REGION.equals(propertyId)) {
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, LocationDto.DISTRICT);
			} else {
				clearAndDisableFields(LocationDto.DISTRICT);
			}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EventCriteria criteria) {

		applyRegionFilterDependency(criteria.getRegion(), LocationDto.DISTRICT);

		relevanceStatusFilter.setValue(criteria.getRelevanceStatus());

		if (criteria.getEventDateType() != null && dateFilter.getDateTypeSelector().containsId(criteria.getEventDateType())) {
			dateFilter.getDateTypeSelector().setValue(criteria.getEventDateType());
		}
		dateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			dateFilter.getWeekFromFilter().setValue(criteria.getEventDateFrom() == null ? null : DateHelper.getEpiWeek(criteria.getEventDateFrom()));
			dateFilter.getWeekToFilter().setValue(criteria.getEventDateTo() == null ? null : DateHelper.getEpiWeek(criteria.getEventDateTo()));
		} else {
			dateFilter.getDateFromFilter().setValue(criteria.getEventDateFrom());
			dateFilter.getDateToFilter().setValue(criteria.getEventDateTo());
		}
	}

	/**
	 * Writes the filters that are not bound to the criteria, i.e. the relevance status and the period.
	 *
	 * @return false if the period is incomplete; the user is notified and the criteria are not changed.
	 */
	public boolean applyUnboundFilters() {

		EventCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) dateFilter.getDateFilterOptionFilter().getValue();
		Date fromDate;
		Date toDate;
		if (dateFilterOption == DateFilterOption.EPI_WEEK) {
			fromDate = DateHelper.getEpiWeekStart((EpiWeek) dateFilter.getWeekFromFilter().getValue());
			toDate = DateHelper.getEpiWeekEnd((EpiWeek) dateFilter.getWeekToFilter().getValue());
		} else {
			Date dateFrom = dateFilter.getDateFromFilter().getValue();
			fromDate = dateFrom != null ? DateHelper.getStartOfDay(dateFrom) : null;
			Date dateTo = dateFilter.getDateToFilter().getValue();
			toDate = dateTo != null ? DateHelper.getEndOfDay(dateTo) : null;
		}
		if ((fromDate == null) != (toDate == null)) {
			dateFilter.setNotificationsForMissingFilters();
			return false;
		}

		criteria.eventDateBetween(fromDate, toDate, (CriteriaDateType) dateFilter.getDateTypeSelector().getValue(), dateFilterOption);
		criteria.relevanceStatus((EntityRelevanceStatus) relevanceStatusFilter.getValue());
		return true;
	}
}
