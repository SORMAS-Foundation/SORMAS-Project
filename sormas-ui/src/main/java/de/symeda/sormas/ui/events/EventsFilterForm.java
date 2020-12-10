package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventsFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	private static final String EVENT_WEEK_AND_DATE_FILTER = "eventWeekDateFilter";
	private static final String ACTION_WEEK_AND_DATE_FILTER = "actionWeekDateFilter";

	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(
		EventDto.SRC_TYPE,
		LocationDto.REGION,
		LocationDto.DISTRICT,
		LocationDto.COMMUNITY,
		EventDto.TYPE_OF_PLACE,
		EventDto.EVENT_INVESTIGATION_STATUS) + loc(EVENT_WEEK_AND_DATE_FILTER) + loc(ACTION_WEEK_AND_DATE_FILTER);

	private final boolean hideEventStatusFilter;
	private final boolean hideActionFilters;

	protected EventsFilterForm(boolean hideEventStatusFilter, boolean hideActionFilters) {
		super(EventCriteria.class, EventIndexDto.I18N_PREFIX);
		this.hideEventStatusFilter = hideEventStatusFilter;
		this.hideActionFilters = hideActionFilters;

		updateFields();
	}

	private void updateFields() {
		if (hideActionFilters) {
			getEpiWeekAndDateComponent(ACTION_WEEK_AND_DATE_FILTER).setVisible(false);
		}
		if (hideEventStatusFilter) {
			getField(EventCriteria.EVENT_STATUS).setVisible(false);
		}
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			EventCriteria.EVENT_STATUS,
			EventIndexDto.DISEASE,
			EventCriteria.REPORTING_USER_ROLE,
			EventCriteria.SURVEILLANCE_OFFICER,
			EventCriteria.FREE_TEXT };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventCriteria.EVENT_STATUS, 140));
		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(EventCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		ComboBox officerField = addField(FieldConfiguration.pixelSized(EventCriteria.SURVEILLANCE_OFFICER, 140));
		RegionReferenceDto userRegion = UserProvider.getCurrent().getUser().getRegion();
		if (userRegion != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(userRegion, UserRole.SURVEILLANCE_OFFICER));
		}
		TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(EventCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEventsSearchField), 200));
		searchField.setNullRepresentation("");
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		addFields(
			moreFiltersContainer,
			FieldConfiguration.pixelSized(EventDto.SRC_TYPE, 140),
			FieldConfiguration.pixelSized(EventDto.TYPE_OF_PLACE, 140),
			FieldConfiguration.pixelSized(EventDto.EVENT_INVESTIGATION_STATUS, 140));

		ComboBox regionField = addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.REGION, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION), 140));
		regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		ComboBox districtField = addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.DISTRICT, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT), 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		ComboBox communityField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				LocationDto.COMMUNITY,
				I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY),
				140));
		communityField.setDescription(I18nProperties.getDescription(Descriptions.descCommunityFilter));

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.EVENT), EVENT_WEEK_AND_DATE_FILTER);
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.ACTION), ACTION_WEEK_AND_DATE_FILTER);
	}

	private HorizontalLayout buildWeekAndDateFilter(EventCriteria.DateType dateType) {

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		switch (dateType) {
		case EVENT:
			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateTo));
			break;
		case ACTION:
			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionDateTo));
			break;
		}
		addApplyHandler(e -> onApplyClick(weekAndDateFilter, dateType));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter, EventCriteria.DateType dateType) {
		EventCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
		Date fromDate, toDate;
		if (dateFilterOption == DateFilterOption.DATE) {
			Date dateFrom = weekAndDateFilter.getDateFromFilter().getValue();
			fromDate = dateFrom != null ? DateHelper.getStartOfDay(dateFrom) : null;
			Date dateTo = weekAndDateFilter.getDateToFilter().getValue();
			toDate = dateFrom != null ? DateHelper.getEndOfDay(dateTo) : null;
		} else {
			fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
			toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
		}
		weekAndDateFilter.setVisible(false);

		if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
			criteria.dateBetween(dateType, fromDate, toDate, dateFilterOption);
		} else {
			if (dateFilterOption == DateFilterOption.DATE) {
				Notification notification = new Notification(
					I18nProperties.getString(Strings.headingMissingDateFilter),
					I18nProperties.getString(Strings.messageMissingDateFilter),
					Notification.Type.WARNING_MESSAGE,
					false);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			} else {
				Notification notification = new Notification(
					I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
					I18nProperties.getString(Strings.messageMissingEpiWeekFilter),
					Notification.Type.WARNING_MESSAGE,
					false);
				notification.setDelayMsec(-1);
				notification.show(Page.getCurrent());
			}
		}
	}

	private EpiWeekAndDateFilterComponent<?> getEpiWeekAndDateComponent(String location) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(location);
		return (EpiWeekAndDateFilterComponent<?>) dateFilterLayout.getComponent(0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		Set<Component> dateFilterOptionComponents = Sets.newHashSet(
			getEpiWeekAndDateComponent(EVENT_WEEK_AND_DATE_FILTER).getDateFilterOptionFilter(),
			getEpiWeekAndDateComponent(ACTION_WEEK_AND_DATE_FILTER).getDateFilterOptionFilter());

		return super.streamFieldsForEmptyCheck(layout).filter(f -> !dateFilterOptionComponents.contains(f));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case LocationDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region == null) {
				clearAndDisableFields(LocationDto.DISTRICT, LocationDto.COMMUNITY);
			} else {
				applyRegionFilterDependency(region, LocationDto.DISTRICT);
				clearAndDisableFields(LocationDto.COMMUNITY);
			}
			break;
		case LocationDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district == null) {
				clearAndDisableFields(LocationDto.COMMUNITY);
			} else {
				applyDistrictDependency(district, LocationDto.COMMUNITY);
			}
			break;
		}
	}

	@Override
	protected void applyRegionFilterDependency(RegionReferenceDto region, String districtFieldId) {
		final ComboBox districtField = getField(districtFieldId);
		if (region != null) {
			FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			districtField.setEnabled(true);
		} else {
			districtField.setEnabled(false);
		}
	}

	@Override
	protected void applyDistrictDependency(DistrictReferenceDto district, String communityFieldId) {
		final ComboBox communityField = getField(communityFieldId);
		if (district != null) {
			FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
			communityField.setEnabled(true);
		} else {
			communityField.setEnabled(false);
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EventCriteria criteria) {

		applyDateDependencyOnNewValue(
			EVENT_WEEK_AND_DATE_FILTER,
			criteria.getDateFilterOption(),
			criteria.getEventDateFrom(),
			criteria.getEventDateTo());

		applyDateDependencyOnNewValue(
			ACTION_WEEK_AND_DATE_FILTER,
			criteria.getActionChangeDateFilterOption(),
			criteria.getActionChangeDateFrom(),
			criteria.getActionChangeDateTo());

		RegionReferenceDto region = criteria.getRegion();
		DistrictReferenceDto district = criteria.getDistrict();
		applyRegionAndDistrictFilterDependency(region, LocationDto.DISTRICT, district, LocationDto.COMMUNITY);
	}

	private void applyDateDependencyOnNewValue(String componentId, DateFilterOption dateFilterOption, Date dateFrom, Date dateTo) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(componentId);
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter;
		weekAndDateFilter = (EpiWeekAndDateFilterComponent<DateFilterOption>) dateFilterLayout.getComponent(0);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(dateFilterOption);

		if (DateFilterOption.EPI_WEEK.equals(dateFilterOption)) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}
}
