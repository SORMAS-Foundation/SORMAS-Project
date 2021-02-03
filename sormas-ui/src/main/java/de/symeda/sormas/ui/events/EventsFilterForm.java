package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventsFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	private static final String EVENT_WEEK_AND_DATE_FILTER = "eventWeekDateFilter";
	private static final String EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER = "eventSignalEvolutionWeekDateFilter";
	private static final String ACTION_WEEK_AND_DATE_FILTER = "actionWeekDateFilter";
	private static final String FACILITY_TYPE_GROUP_FILTER = "facilityTypeGroupFilter";

	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(
		EventDto.SRC_TYPE,
		LocationDto.REGION,
		LocationDto.DISTRICT,
		LocationDto.COMMUNITY,
		EventDto.TYPE_OF_PLACE,
		FACILITY_TYPE_GROUP_FILTER,
		LocationDto.FACILITY_TYPE,
		LocationDto.FACILITY,
		EventDto.EVENT_INVESTIGATION_STATUS) +
		loc(EVENT_WEEK_AND_DATE_FILTER) +
		loc(EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER) +
		loc(ACTION_WEEK_AND_DATE_FILTER);

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
			getEpiWeekAndDateComponent(ACTION_WEEK_AND_DATE_FILTER).getParent().setVisible(false);
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
			EventCriteria.RESPONSIBLE_USER,
			EventCriteria.RESPONSIBLE_USER_ROLE,
			EventCriteria.FREE_TEXT };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventCriteria.EVENT_STATUS, 140));
		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));
		addField(FieldConfiguration.withCaptionAndPixelSized(EventCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		addField(FieldConfiguration.pixelSized(EventCriteria.RESPONSIBLE_USER, 140));

		ComboBox responsibleUserRoleField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventCriteria.RESPONSIBLE_USER_ROLE,
				I18nProperties.getString(Strings.promptEventResponsibleUserRoleField),
				170));
		FieldHelper.updateEnumData(responsibleUserRoleField, Arrays.asList(UserRole.SURVEILLANCE_OFFICER, UserRole.SURVEILLANCE_SUPERVISOR));

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
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.EVENT_SIGNAL_EVOLUTION), EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER);
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.ACTION), ACTION_WEEK_AND_DATE_FILTER);

		ComboBox facilityTypeGroupField = new ComboBox();
		facilityTypeGroupField.setId(FACILITY_TYPE_GROUP_FILTER);
		facilityTypeGroupField.setInputPrompt(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE_GROUP));
		facilityTypeGroupField
			.addStyleNames(ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_CAPTION_INLINE, CssStyles.OPTIONGROUP_GRID_LAYOUT);
		facilityTypeGroupField.setWidth(140, Unit.PIXELS);
		facilityTypeGroupField.addItems((Object[]) FacilityTypeGroup.values());
		facilityTypeGroupField.setVisible(false);
		moreFiltersContainer.addComponent(facilityTypeGroupField, FACILITY_TYPE_GROUP_FILTER);

		ComboBox facilityTypeField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				LocationDto.FACILITY_TYPE,
				I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.FACILITY_TYPE),
				140));
		facilityTypeField.setVisible(false);

		ComboBox facilityField = addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.FACILITY, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.FACILITY), 140));
		facilityField.setEnabled(false);
		facilityField.setVisible(false);

		facilityTypeGroupField.addValueChangeListener(
			e -> FieldHelper.updateEnumData(
				facilityTypeField,
				facilityTypeGroupField.getValue() != null
					? FacilityType.getTypes((FacilityTypeGroup) facilityTypeGroupField.getValue())
					: Arrays.stream(FacilityType.values()).collect(Collectors.toList())));

		facilityTypeField.addValueChangeListener(e -> {
			final FacilityType facilityType = (FacilityType) facilityTypeField.getValue();
			if (facilityType != null) {
				final UserDto user = UserProvider.getCurrent().getUser();
				final CommunityReferenceDto community =
					user.getCommunity() != null ? user.getCommunity() : (CommunityReferenceDto) communityField.getValue();

				facilityField.setEnabled(true);
				if (community != null) {
					FieldHelper.updateItems(
						facilityField,
						FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
				} else {
					FieldHelper.updateItems(
						facilityField,
						FacadeProvider.getFacilityFacade()
							.getActiveFacilitiesByDistrictAndType(
								user.getDistrict() != null ? user.getDistrict() : (DistrictReferenceDto) districtField.getValue(),
								facilityType,
								true,
								false));
				}
			} else {
				facilityField.removeAllItems();
				facilityField.setEnabled(false);
			}
		});
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
		case EVENT_SIGNAL_EVOLUTION:
			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventSignalEvolutionEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventSignalEvolutionEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEvolutionDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEvolutionDateTo));
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
			getEpiWeekAndDateComponent(EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER).getDateFilterOptionFilter(),
			getEpiWeekAndDateComponent(ACTION_WEEK_AND_DATE_FILTER).getDateFilterOptionFilter());

		return super.streamFieldsForEmptyCheck(layout).filter(f -> !dateFilterOptionComponents.contains(f));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case LocationDto.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, LocationDto.DISTRICT);
				clearAndDisableFields(LocationDto.COMMUNITY);
			} else {
				clearAndDisableFields(LocationDto.DISTRICT, LocationDto.COMMUNITY);
			}
			updateResponsibleUserFieldItems();
			applyFacilityFieldsDependencies();
			break;
		case LocationDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district != null) {
				applyDistrictDependency(district, LocationDto.COMMUNITY);
			} else {
				clearAndDisableFields(LocationDto.COMMUNITY);
			}
			updateResponsibleUserFieldItems();
			applyFacilityFieldsDependencies();
			break;
		case EventDto.TYPE_OF_PLACE:
			applyFacilityFieldsDependencies();
			break;
		case EventCriteria.RESPONSIBLE_USER_ROLE:
			updateResponsibleUserFieldItems();
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
			EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER,
			criteria.getEvolutionDateFilterOption(),
			criteria.getEventEvolutionDateFrom(),
			criteria.getEventEvolutionDateTo());

		applyDateDependencyOnNewValue(
			ACTION_WEEK_AND_DATE_FILTER,
			criteria.getActionChangeDateFilterOption(),
			criteria.getActionChangeDateFrom(),
			criteria.getActionChangeDateTo());

		RegionReferenceDto region = criteria.getRegion();
		DistrictReferenceDto district = criteria.getDistrict();
		applyRegionAndDistrictFilterDependency(region, LocationDto.DISTRICT, district, LocationDto.COMMUNITY);

		applyFacilityFieldsDependencies(criteria.getTypeOfPlace(), criteria.getDistrict(), criteria.getCommunity());
		updateResponsibleUserFieldItems(criteria.getResponsibleUserRole(), criteria.getDistrict(), criteria.getRegion());
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

	private void updateResponsibleUserFieldItems() {
		updateResponsibleUserFieldItems(
			(UserRole) getField(EventCriteria.RESPONSIBLE_USER_ROLE).getValue(),
			(DistrictReferenceDto) getField(EventCriteria.DISTRICT).getValue(),
			(RegionReferenceDto) getField(EventCriteria.REGION).getValue());
	}

	private void updateResponsibleUserFieldItems(UserRole responsibleUserRole, DistrictReferenceDto district, RegionReferenceDto region) {
		final UserRole[] responsibleUserRoles = Optional.ofNullable(responsibleUserRole)
			.map(
				userRole -> new UserRole[] {
					userRole })
			.orElse(
				new UserRole[] {
					UserRole.SURVEILLANCE_OFFICER,
					UserRole.SURVEILLANCE_SUPERVISOR });

		final Set<UserReferenceDto> responsibleUsers = new LinkedHashSet<>();
		if (district != null && Arrays.asList(responsibleUserRoles).contains(UserRole.SURVEILLANCE_OFFICER)) {
			responsibleUsers.addAll(FacadeProvider.getUserFacade().getUserRefsByDistrict(district, false, UserRole.SURVEILLANCE_OFFICER));
		}
		responsibleUsers.addAll(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region, responsibleUserRoles));

		FieldHelper.updateItems((ComboBox) getField(EventCriteria.RESPONSIBLE_USER), new ArrayList<>(responsibleUsers));
	}

	private void applyFacilityFieldsDependencies() {
		applyFacilityFieldsDependencies(
			(TypeOfPlace) getField(EventDto.TYPE_OF_PLACE).getValue(),
			(DistrictReferenceDto) getField(LocationDto.DISTRICT).getValue(),
			(CommunityReferenceDto) getField(LocationDto.COMMUNITY).getValue());
	}

	private void applyFacilityFieldsDependencies(
		TypeOfPlace typeOfPlace,
		DistrictReferenceDto districtReferenceDto,
		CommunityReferenceDto communityReferenceDto) {

		final UserDto user = UserProvider.getCurrent().getUser();
		final boolean visible = typeOfPlace == TypeOfPlace.FACILITY
			&& ((user.getCommunity() != null || communityReferenceDto != null) || (user.getDistrict() != null || districtReferenceDto != null));
		final ComboBox facilityField = getField(LocationDto.FACILITY);
		final ComboBox facilityTypeField = getField(LocationDto.FACILITY_TYPE);
		final ComboBox facilityTypeGroupField = (ComboBox) getMoreFiltersContainer().getComponent(FACILITY_TYPE_GROUP_FILTER);
		if (!visible) {
			facilityField.clear();
			facilityTypeField.clear();
			facilityTypeGroupField.clear();
		}
		facilityField.setVisible(visible);
		facilityTypeField.setVisible(visible);
		facilityTypeGroupField.setVisible(visible);
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}
}
