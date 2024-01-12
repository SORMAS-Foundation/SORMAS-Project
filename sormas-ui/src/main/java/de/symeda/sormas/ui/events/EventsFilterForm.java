/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Sets;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventCriteriaDateType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.SpecificRisk;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateTypeHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class EventsFilterForm extends AbstractFilterForm<EventCriteria> {

	private static final long serialVersionUID = -1166745065032487009L;

	private static final String EVENT_WEEK_AND_DATE_FILTER = "eventWeekDateFilter";
	private static final String EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER = "eventSignalEvolutionWeekDateFilter";
	private static final String ACTION_CHANGE_WEEK_AND_DATE_FILTER = "actionChangeWeekDateFilter";
	private static final String ACTION_WEEK_AND_DATE_FILTER = "actionWeekDateFilter";
	private static final String FACILITY_TYPE_GROUP_FILTER = "facilityTypeGroupFilter";
	private static final String RESPONSIBLE_USER_INFO = "responsibleUserInfo";

	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(
		EventDto.SRC_TYPE,
		LocationDto.REGION,
		LocationDto.DISTRICT,
		LocationDto.COMMUNITY,
		EventDto.TYPE_OF_PLACE,
		FACILITY_TYPE_GROUP_FILTER,
		LocationDto.FACILITY_TYPE,
		LocationDto.FACILITY,
		EventCriteria.RESPONSIBLE_USER,
		RESPONSIBLE_USER_INFO,
		EventDto.EVENT_INVESTIGATION_STATUS,
		EventDto.EVENT_MANAGEMENT_STATUS,
		EventDto.EVENT_IDENTIFICATION_SOURCE)
		+ filterLocsCss(
			VSPACE_3,
			EventCriteria.ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL,
			EventCriteria.ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL,
			EventCriteria.ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL)
		+ loc(EVENT_WEEK_AND_DATE_FILTER)
		+ loc(EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER)
		+ loc(ACTION_CHANGE_WEEK_AND_DATE_FILTER)
		+ loc(ACTION_WEEK_AND_DATE_FILTER);

	private final boolean hideEventStatusFilter;
	private final boolean hideActionFilters;
	private ComboBox regionField;
	private ComboBox districtField;
	private ComboBox communityField;

	protected EventsFilterForm(boolean hideEventStatusFilter, boolean hideActionFilters) {
		super(
			EventCriteria.class,
			EventIndexDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		this.hideEventStatusFilter = hideEventStatusFilter;
		this.hideActionFilters = hideActionFilters;

		updateFields();
	}

	private void updateFields() {
		if (hideActionFilters) {
			getEpiWeekAndDateComponent(ACTION_CHANGE_WEEK_AND_DATE_FILTER).getParent().setVisible(false);
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
			EventCriteria.RISK_LEVEL,
			EventCriteria.SPECIFIC_RISK,
			EventIndexDto.DISEASE,
			EventIndexDto.DISEASE_VARIANT,
			EventCriteria.REPORTING_USER_ROLE,
			EventCriteria.FREE_TEXT,
			EventCriteria.FREE_TEXT_EVENT_PARTICIPANTS,
			EventCriteria.FREE_TEXT_EVENT_GROUPS };
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(EventCriteria.EVENT_STATUS, 140));
		addField(FieldConfiguration.pixelSized(EventCriteria.RISK_LEVEL, 140));

		List<SpecificRisk> specificRisks = FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.SPECIFIC_EVENT_RISK, null);
		if (!specificRisks.isEmpty()) {
			ComboBox specificRiskField = addField(FieldConfiguration.pixelSized(EventCriteria.SPECIFIC_RISK, 140), ComboBox.class);
			FieldHelper.updateItems(specificRiskField, specificRisks);
		}

		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE, 140));
		addField(FieldConfiguration.pixelSized(EventIndexDto.DISEASE_VARIANT, 140), ComboBox.class);

		ComboBox reportedByField = addField(
			FieldConfiguration.withCaptionAndPixelSized(EventCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		reportedByField.addItems(FacadeProvider.getUserRoleFacade().getAllActiveAsReference());

		TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(EventCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEventsSearchField), 200));
		searchField.setNullRepresentation("");

		TextField searchFieldEventParticipants = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventCriteria.FREE_TEXT_EVENT_PARTICIPANTS,
				I18nProperties.getString(Strings.promptEventsSearchFieldEventParticipants),
				200));
		searchFieldEventParticipants.setNullRepresentation("");

		TextField searchFieldEventGroups = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				EventCriteria.FREE_TEXT_EVENT_GROUPS,
				I18nProperties.getString(Strings.promptEventsSearchFieldEventGroups),
				200));
		searchFieldEventGroups.setNullRepresentation("");
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		boolean isExternalShareEnabled = FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled();

		addFields(
			moreFiltersContainer,
			FieldConfiguration.pixelSized(EventDto.SRC_TYPE, 140),
			FieldConfiguration.pixelSized(EventDto.TYPE_OF_PLACE, 140),
			FieldConfiguration.pixelSized(EventDto.EVENT_INVESTIGATION_STATUS, 140),
			FieldConfiguration.pixelSized(EventDto.EVENT_MANAGEMENT_STATUS, 140),
			FieldConfiguration.pixelSized(EventDto.EVENT_IDENTIFICATION_SOURCE, 140));

		regionField = addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.REGION, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION), 140));
		regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		districtField = addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(LocationDto.DISTRICT, I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT), 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		communityField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				LocationDto.COMMUNITY,
				I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY),
				140));
		communityField.setDescription(I18nProperties.getDescription(Descriptions.descCommunityFilter));

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.EVENT, isExternalShareEnabled), EVENT_WEEK_AND_DATE_FILTER);
		moreFiltersContainer.addComponent(
			buildWeekAndDateFilter(EventCriteria.DateType.EVENT_SIGNAL_EVOLUTION, isExternalShareEnabled),
			EVENT_SIGNAL_EVOLUTION_WEEK_AND_DATE_FILTER);
		moreFiltersContainer
			.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.ACTION_CHANGE, isExternalShareEnabled), ACTION_CHANGE_WEEK_AND_DATE_FILTER);
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(EventCriteria.DateType.ACTION, isExternalShareEnabled), ACTION_WEEK_AND_DATE_FILTER);

		ComboBox facilityTypeGroupField = ComboBoxHelper.createComboBoxV7();
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

		addField(moreFiltersContainer, FieldConfiguration.pixelSized(EventCriteria.RESPONSIBLE_USER, 140));

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoEventResponsibleUserFilter), ContentMode.TEXT);
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		moreFiltersContainer.addComponent(infoLabel, RESPONSIBLE_USER_INFO);

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

		if (isExternalShareEnabled) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					EventCriteria.ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.eventFilterOnlyEventsNotSharedWithExternalSurvTool),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));

			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					EventCriteria.ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.eventFilterOnlyEventsSharedWithExternalSurvTool),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));

			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					EventCriteria.ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.eventFilterOnlyEventsChangedSinceLastSharedWithExternalSurvTool),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));
		}
	}

	private HorizontalLayout buildWeekAndDateFilter(EventCriteria.DateType dateType, boolean isExternalShareEnabled) {

		final EpiWeekAndDateFilterComponent<?> weekAndDateFilter;

		switch (dateType) {
		case EVENT:
			weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
				false,
				false,
				null,
				CriteriaDateTypeHelper.getTypes(EventCriteriaDateType.class, isExternalShareEnabled),
				I18nProperties.getString(Strings.promptEventDateType),
				null,
				this);

			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventDateTo));
			break;
		case EVENT_SIGNAL_EVOLUTION:
			weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventSignalEvolutionEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventSignalEvolutionEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEvolutionDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEventEvolutionDateTo));
			break;
		case ACTION_CHANGE:
			weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionChangeEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionChangeEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionChangeDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionChangeDateTo));
			break;
		case ACTION:
			weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptActionDateTo));
			break;

		default:
			throw new RuntimeException("Unknown event date filter type");
		}
		addApplyHandler(e -> onApplyClick(weekAndDateFilter, dateType));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<?> weekAndDateFilter, EventCriteria.DateType dateType) {
		EventCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
		CriteriaDateType eventDateType = (CriteriaDateType) weekAndDateFilter.getDateTypeSelector().getValue();

		Date fromDate, toDate;
		if (dateFilterOption == DateFilterOption.DATE) {
			Date dateFrom = weekAndDateFilter.getDateFromFilter().getValue();
			fromDate = dateFrom != null ? DateHelper.getStartOfDay(dateFrom) : null;
			Date dateTo = weekAndDateFilter.getDateToFilter().getValue();
			toDate = dateTo != null ? DateHelper.getEndOfDay(dateTo) : null;
		} else {
			fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
			toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
		}

		if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
			criteria.dateBetween(dateType, fromDate, toDate, eventDateType, dateFilterOption);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
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
			getEpiWeekAndDateComponent(ACTION_CHANGE_WEEK_AND_DATE_FILTER).getDateFilterOptionFilter(),
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
			applyFacilityFieldsDependencies();
			updateResponsibleUserFieldItems();
			break;
		case LocationDto.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district != null) {
				applyDistrictDependency(district, LocationDto.COMMUNITY);
			} else {
				clearAndDisableFields(LocationDto.COMMUNITY);
			}
			applyFacilityFieldsDependencies();
			updateResponsibleUserFieldItems();
			break;
		case EventDto.TYPE_OF_PLACE:
			applyFacilityFieldsDependencies();
			break;
		case CaseDataDto.DISEASE:
			Disease disease = (Disease) event.getProperty().getValue();
			applyDiseaseFilterDependency(disease);
		}
	}

	@Override
	protected void applyRegionFilterDependency(RegionReferenceDto region, String districtFieldId) {
		if (region != null) {
			FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			districtField.setEnabled(true);
		} else {
			districtField.setEnabled(false);
		}
	}

	@Override
	protected void applyDistrictDependency(DistrictReferenceDto district, String communityFieldId) {
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
			ACTION_CHANGE_WEEK_AND_DATE_FILTER,
			criteria.getActionChangeDateFilterOption(),
			criteria.getActionChangeDateFrom(),
			criteria.getActionChangeDateTo());

		applyDateDependencyOnNewValue(
			ACTION_WEEK_AND_DATE_FILTER,
			criteria.getActionDateFilterOption(),
			criteria.getActionDateFrom(),
			criteria.getActionDateTo());

		RegionReferenceDto region = criteria.getRegion();
		DistrictReferenceDto district = criteria.getDistrict();
		applyRegionAndDistrictFilterDependency(region, LocationDto.DISTRICT, district, LocationDto.COMMUNITY);

		applyFacilityFieldsDependencies(criteria.getTypeOfPlace(), criteria.getDistrict(), criteria.getCommunity());

		updateResponsibleUserFieldItems(criteria.getDistrict(), criteria.getRegion());

		ComboBox diseaseField = getField(CaseDataDto.DISEASE);
		Disease disease = (Disease) diseaseField.getValue();
		applyDiseaseFilterDependency(disease);
	}

	private void applyDiseaseFilterDependency(Disease disease) {
		ComboBox diseaseVariantField = getField(CaseDataDto.DISEASE_VARIANT);
		if (disease == null) {
			FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
			FieldHelper.setEnabled(false, diseaseVariantField);
		} else {
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			FieldHelper.setEnabled(CollectionUtils.isNotEmpty(diseaseVariants), diseaseVariantField);
		}
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

	private void applyFacilityFieldsDependencies() {
		applyFacilityFieldsDependencies(
			(TypeOfPlace) getField(EventDto.TYPE_OF_PLACE).getValue(),
			(DistrictReferenceDto) districtField.getValue(),
			(CommunityReferenceDto) communityField.getValue());
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

	private void updateResponsibleUserFieldItems() {
		updateResponsibleUserFieldItems((DistrictReferenceDto) districtField.getValue(), (RegionReferenceDto) regionField.getValue());
	}

	private void updateResponsibleUserFieldItems(DistrictReferenceDto district, RegionReferenceDto region) {
		final List<UserReferenceDto> items = new ArrayList<>();
		Disease selectedDisease = (Disease) getField(EventIndexDto.DISEASE).getValue();
		items.addAll(FacadeProvider.getUserFacade().getUsersByRegionAndRights(region, selectedDisease, UserRight.EVENT_RESPONSIBLE));
		if (district != null) {
			items.addAll(FacadeProvider.getUserFacade().getUserRefsByDistrict(district, selectedDisease, UserRight.EVENT_RESPONSIBLE));
		}
		FieldHelper.updateItems((ComboBox) getField(EventCriteria.RESPONSIBLE_USER), items);
	}

	private void hideAndFillJurisdictionFilters() {

		regionField.setVisible(false);
		regionField.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		districtField.setVisible(false);
		districtField.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		communityField.setVisible(false);
		communityField.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	@Override
	public void setValue(EventCriteria newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (newFieldValue != null
			&& (regionField.isVisible() || districtField.isVisible() || communityField.isVisible())
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFilters();
		}
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}
}
