/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.directory;

import static de.symeda.sormas.ui.utils.LayoutUtil.divCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Date;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDateType;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class AefiFilterForm extends AbstractFilterForm<AefiCriteria> {

	private static final String ACTION_BUTTONS_ID = "actionButtons";
	private static final String MORE_FILTERS_ID = "moreFilters";
	private static final String WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML = loc(WEEK_AND_DATE_FILTER);

	public AefiFilterForm() {
		super(
			AefiCriteria.class,
			AefiCriteria.I18N_PREFIX,
			JurisdictionFieldConfig.of(AefiCriteria.REGION, AefiCriteria.DISTRICT, AefiCriteria.COMMUNITY));
	}

	@Override
	protected String createHtmlLayout() {
		return divCss(
			"",
			filterLocs(ArrayUtils.addAll(getMainFilterLocators(), ACTION_BUTTONS_ID)) + locCss(CssStyles.VSPACE_TOP_NONE, MORE_FILTERS_ID));

	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			AefiCriteria.DISEASE,
			AefiCriteria.PERSON_LIKE,
			AefiCriteria.AEFI_TYPE,
			AefiCriteria.VACCINE_NAME,
			AefiCriteria.REGION,
			AefiCriteria.DISTRICT,
			AefiCriteria.COMMUNITY,
			AefiCriteria.OUTCOME };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(AefiCriteria.DISEASE, 140));

		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(AefiCriteria.PERSON_LIKE, I18nProperties.getString(Strings.promptPersonsSearchField), 200));
		searchField.setNullRepresentation("");

		addFields(FieldConfiguration.pixelSized(AefiCriteria.AEFI_TYPE, 140), FieldConfiguration.pixelSized(AefiCriteria.VACCINE_NAME, 140));

		if (currentUserDto().getRegion() == null) {
			ComboBox regionFilter = addField(getContent(), FieldConfiguration.pixelSized(AefiCriteria.REGION, 140));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		ComboBox districtFilter = addField(getContent(), FieldConfiguration.pixelSized(AefiCriteria.DISTRICT, 140));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		if (currentUserDto().getDistrict() != null) {
			districtFilter.setVisible(false);
		}

		addField(getContent(), FieldConfiguration.pixelSized(AefiCriteria.COMMUNITY, 140));

		addField(FieldConfiguration.pixelSized(AefiCriteria.OUTCOME, 140));
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {

		super.applyDependenciesOnFieldChange(propertyId, event);

		final AefiCriteria criteria = getValue();

		final ComboBox facilityTypeGroupField = getField(AefiCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(AefiCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(AefiCriteria.HEALTH_FACILITY);

		final UserDto user = currentUserDto();
		final DistrictReferenceDto currentDistrict =
			user.getDistrict() != null ? user.getDistrict() : (DistrictReferenceDto) districtFilter.getValue();

		switch (propertyId) {
		case AefiCriteria.REGION: {
			final RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : (RegionReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(region, criteria.getRegion())) {
				if (region != null) {
					enableFields(districtFilter);
					FieldHelper.updateItems(districtFilter, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
				} else {
					clearAndDisableFields(districtFilter);
				}
				clearAndDisableFields(communityFilter, facilityField, facilityTypeField, facilityTypeGroupField);
			}

			break;
		}
		case AefiCriteria.DISTRICT: {
			final DistrictReferenceDto newDistrict = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(newDistrict, criteria.getDistrict())) {
				if (newDistrict != null) {
					enableFields(communityFilter, facilityTypeGroupField);

					clearAndDisableFields(facilityField);
					if (facilityTypeGroupField != null) {
						if (facilityTypeGroupField.getValue() != null && facilityTypeField.getValue() != null) {
							FieldHelper.updateItems(
								facilityField,
								FacadeProvider.getFacilityFacade()
									.getActiveFacilitiesByDistrictAndType(newDistrict, (FacilityType) facilityTypeField.getValue(), true, false));
							enableFields(facilityField);
						} else {
							FieldHelper.updateEnumData(facilityTypeGroupField, FacilityTypeGroup.getAccomodationGroups());
						}
					}

					FieldHelper.updateItems(communityFilter, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(newDistrict.getUuid()));
				} else {
					clearAndDisableFields(communityFilter, facilityField, facilityTypeField, facilityTypeGroupField);
				}
			}

			break;
		}
		case AefiCriteria.COMMUNITY: {
			CommunityReferenceDto community = (CommunityReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(community, criteria.getCommunity())) {
				if (facilityField != null) {
					facilityField.setValue(null);
				}

				final FacilityType facilityType = facilityTypeField != null ? (FacilityType) facilityTypeField.getValue() : null;

				if (facilityType == null && facilityField != null) {
					facilityField.removeAllItems();
				} else if (facilityField != null) {
					if (community == null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(currentDistrict, facilityType, true, false));
					} else {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					}
				}
			}
			break;
		}
		case AefiCriteria.FACILITY_TYPE_GROUP: {
			FacilityTypeGroup typeGroup = (FacilityTypeGroup) event.getProperty().getValue();
			if (!DataHelper.equal(typeGroup, criteria.getFacilityTypeGroup())) {
				if (typeGroup != null) {
					enableFields(AefiCriteria.FACILITY_TYPE);
					FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(typeGroup));
					facilityField.setValue(null);
				} else {
					clearAndDisableFields(facilityTypeField, facilityField);
				}
			}

			break;
		}
		case AefiCriteria.FACILITY_TYPE: {
			FacilityType facilityType = (FacilityType) event.getProperty().getValue();
			if (!DataHelper.equal(facilityType, criteria.getFacilityType())) {
				if (facilityType == null) {
					clearAndDisableFields(facilityField);
				} else {
					enableFields(facilityField);
					facilityField.setValue(null);

					CommunityReferenceDto community = (CommunityReferenceDto) communityFilter.getValue();
					if (community != null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					} else if (currentDistrict != null) {
						FieldHelper.updateItems(
							facilityField,
							FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(currentDistrict, facilityType, true, false));
					}
				}
			}
			break;
		}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(AefiCriteria criteria) {

		final UserDto user = currentUserDto();

		UserProvider currentUserProvider = UserProvider.getCurrent();
		final JurisdictionLevel userJurisdictionLevel = currentUserProvider != null ? UserProvider.getCurrent().getJurisdictionLevel() : null;

		final ComboBox facilityTypeGroupField = getField(AefiCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(AefiCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(AefiCriteria.HEALTH_FACILITY);

		// Disable all fields
		clearAndDisableFields(districtFilter, communityFilter, facilityTypeGroupField, facilityTypeField, facilityField);

		// Get initial field values according to user and criteria
		final RegionReferenceDto region = user.getRegion() == null ? criteria.getRegion() : user.getRegion();
		final DistrictReferenceDto district = user.getDistrict() == null ? criteria.getDistrict() : user.getDistrict();
		final CommunityReferenceDto community = user.getCommunity() == null ? criteria.getCommunity() : user.getCommunity();
		final FacilityTypeGroup facilityTypeGroup = criteria.getFacilityTypeGroup();
		final FacilityType facilityType = criteria.getFacilityType();

		// district
		if (region != null) {
			enableFields(districtFilter);
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			// community
			if (district != null) {
				districtFilter.setValue(district);
				communityFilter.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
				enableFields(communityFilter);
				if (community != null) {
					communityFilter.setValue(community);
				}
			} else {
				clearAndDisableFields(communityFilter);
			}
		} else {
			clearAndDisableFields(districtFilter, communityFilter);
		}

		// facility
		if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			facilityField.setValue(user.getHealthFacility());
			disableFields(facilityTypeGroupField, facilityTypeField, facilityField);
		} else if (facilityTypeGroupField != null && district != null) {
			enableFields(facilityTypeGroupField);
			FieldHelper.updateEnumData(facilityTypeGroupField, FacilityTypeGroup.getAccomodationGroups());
			if (facilityTypeGroup != null) {
				facilityTypeGroupField.setValue(facilityTypeGroup);
				enableFields(facilityTypeField);
				FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(facilityTypeGroup));
				if (facilityType != null) {
					facilityTypeField.setValue(facilityType);
					enableFields(facilityField);
					if (community != null) {
						facilityField
							.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
					} else {
						facilityField
							.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, facilityType, true, false));
					}
				} else {
					disableFields(facilityField);
				}
			} else {
				disableFields(facilityTypeField);
			}
		}

		// Disable fields according to user & jurisdiction
		if (userJurisdictionLevel == JurisdictionLevel.DISTRICT) {
			clearAndDisableFields(districtFilter);
		} else if (userJurisdictionLevel == JurisdictionLevel.COMMUNITY) {
			clearAndDisableFields(districtFilter, communityFilter);
		} else if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			clearAndDisableFields(districtFilter, communityFilter, facilityTypeGroupField, facilityTypeField, facilityField);
		}

		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<AefiDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<AefiDateType>) dateFilterLayout.getComponent(0);

		AefiDateType aefiDateType = criteria.getAefiDateType();
		weekAndDateFilter.getDateTypeSelector().setValue(aefiDateType);
		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date dateFrom = criteria.getFromDate();
		Date dateTo = criteria.getToDate();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<AefiDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<AefiDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent<AefiDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			null,
			AefiDateType.values(),
			I18nProperties.getString(Strings.promptAefiDateType),
			null,
			this);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptAefiEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptAefiEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptAefiDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptAefiDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<AefiDateType> weekAndDateFilter) {
		AefiCriteria criteria = getValue();

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
		if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
			criteria.setDateFilterOption(dateFilterOption);
			AefiDateType AefiDateType = (AefiDateType) weekAndDateFilter.getDateTypeSelector().getValue();
			criteria.setAefiDateType(AefiDateType);
			criteria.setFromDate(fromDate);
			criteria.setToDate(toDate);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}
}
