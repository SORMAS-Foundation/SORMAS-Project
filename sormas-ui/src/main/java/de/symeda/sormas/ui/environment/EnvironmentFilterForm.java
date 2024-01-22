/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;

public class EnvironmentFilterForm extends AbstractFilterForm<EnvironmentCriteria> {

	private static final long serialVersionUID = -6926654547113049125L;

	private static final String EPI_WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML_LAYOUT = loc(EPI_WEEK_AND_DATE_FILTER)
		+ filterLocs(
			EnvironmentCriteria.GPS_LAT_FROM,
			EnvironmentCriteria.GPS_LAT_TO,
			EnvironmentCriteria.GPS_LON_FROM,
			EnvironmentCriteria.GPS_LON_TO);

	public EnvironmentFilterForm() {
		super(
			EnvironmentCriteria.class,
			EnvironmentDto.I18N_PREFIX,
			JurisdictionFieldConfig.of(EnvironmentCriteria.REGION, EnvironmentCriteria.DISTRICT, EnvironmentCriteria.COMMUNITY));
	}

	private static UserRight[] getResponsibleUserRights() {
		return new UserRight[] {};
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			EnvironmentCriteria.FREE_TEXT,
			EnvironmentCriteria.REGION,
			EnvironmentCriteria.DISTRICT,
			EnvironmentCriteria.COMMUNITY,
			EnvironmentCriteria.ENVIRONMENT_MEDIA,
			EnvironmentCriteria.RESPONSIBLE_USER };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		UserDto user = currentUserDto();

		addField(
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentCriteria.FREE_TEXT, I18nProperties.getString(Strings.promptEnvironmentFreeTextSearch), 200),
			TextField.class);

		if (user.getRegion() == null) {
			ComboBox regionField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentCriteria.REGION,
					I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.REGION),
					140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		if (user.getDistrict() == null) {
			ComboBox districtField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentCriteria.DISTRICT,
					I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.DISTRICT),
					140));
			districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
			districtField.setEnabled(false);
		}

		if (user.getCommunity() == null) {
			ComboBox communityField = addField(
				FieldConfiguration.withCaptionAndPixelSized(
					EnvironmentCriteria.COMMUNITY,
					I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, LocationDto.COMMUNITY),
					140));
			communityField.setDescription(I18nProperties.getDescription(Descriptions.descCommunityFilter));
			communityField.setEnabled(false);
		}

		addField(FieldConfiguration.pixelSized(EnvironmentCriteria.ENVIRONMENT_MEDIA, 140));

		ComboBox responsibleUserCombo = addField(FieldConfiguration.pixelSized(EnvironmentCriteria.RESPONSIBLE_USER, 140));
		responsibleUserCombo.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRights(user.getRegion(), null, getResponsibleUserRights()));
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {
		moreFiltersContainer.addComponent(buildEpiWeekAndDateFilter(), EPI_WEEK_AND_DATE_FILTER);

		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentCriteria.GPS_LAT_FROM, I18nProperties.getString(Strings.promptEnvironmentLatFrom), 140));
		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentCriteria.GPS_LAT_TO, I18nProperties.getString(Strings.promptEnvironmentLatTo), 140));
		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentCriteria.GPS_LON_FROM, I18nProperties.getString(Strings.promptEnvironmentLonFrom), 140));
		addField(
			moreFiltersContainer,
			FieldConfiguration
				.withCaptionAndPixelSized(EnvironmentCriteria.GPS_LON_TO, I18nProperties.getString(Strings.promptEnvironmentLonTo), 140));
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case EnvironmentCriteria.REGION:
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, EnvironmentCriteria.DISTRICT);
				clearAndDisableFields(EnvironmentCriteria.COMMUNITY);
			} else {
				clearAndDisableFields(EnvironmentCriteria.DISTRICT, EnvironmentCriteria.COMMUNITY);
			}

			populateResponsibleUsersForRegion(region);

			break;
		case EnvironmentCriteria.DISTRICT:
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district != null) {
				applyDistrictDependency(district, EnvironmentCriteria.COMMUNITY);
			} else {
				clearAndDisableFields(EnvironmentCriteria.COMMUNITY);
			}

			populateResponsibleUsersForDistrict(district);

			break;
		default:
			break;
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(EnvironmentCriteria criteria) {
		super.applyDependenciesOnNewValue(criteria);

		UserDto user = currentUserDto();

		RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : criteria.getRegion();
		if (region != null && user.getDistrict() == null) {
			applyRegionFilterDependency(region, EnvironmentCriteria.DISTRICT);
			populateResponsibleUsersForRegion(region);
		}

		DistrictReferenceDto district = user.getDistrict() != null ? user.getDistrict() : criteria.getDistrict();
		if (district != null && user.getCommunity() == null) {
			applyDistrictDependency(district, EnvironmentCriteria.COMMUNITY);
			populateResponsibleUsersForDistrict(district);
		}

		((ComboBox) getField(EnvironmentCriteria.RESPONSIBLE_USER)).setValue(criteria.getResponsibleUser());

		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date sampleDateFrom = criteria.getReportDateFrom();
		Date sampleDateTo = criteria.getReportDateTo();

		if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(sampleDateFrom == null ? null : DateHelper.getEpiWeek(sampleDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(sampleDateTo == null ? null : DateHelper.getEpiWeek(sampleDateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(sampleDateFrom);
			weekAndDateFilter.getDateToFilter().setValue(sampleDateTo);
		}
	}

	private EpiWeekAndDateFilterComponent buildEpiWeekAndDateFilter() {
		EpiWeekAndDateFilterComponent<DateFilterOption> epiWeekAndDateFilter = new EpiWeekAndDateFilterComponent<>(false, false, null, this);

		epiWeekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentEpiWeekFrom));
		epiWeekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentEpiWeekTo));
		epiWeekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentDateFrom));
		epiWeekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptEnvironmentDateTo));

		addApplyHandler(e -> onApplyClick(epiWeekAndDateFilter));

		return epiWeekAndDateFilter;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter) {
		EnvironmentCriteria criteria = getValue();

		DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
		final Date fromDate;
		final Date toDate;
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
			criteria.reportDateBetween(fromDate, toDate, dateFilterOption);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}

	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<DateFilterOption> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<DateFilterOption>) getMoreFiltersContainer().getComponent(EPI_WEEK_AND_DATE_FILTER);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private void populateResponsibleUsersForDistrict(DistrictReferenceDto districtReferenceDto) {
		if (districtReferenceDto != null) {
			List<UserReferenceDto> items =
				FacadeProvider.getUserFacade().getUserRefsByDistrict(districtReferenceDto, null, getResponsibleUserRights());
			populateResponsibleUsers(items);
		} else {
			final ComboBox regionField = getField(EnvironmentCriteria.REGION);
			if (regionField != null) {
				populateResponsibleUsersForRegion((RegionReferenceDto) regionField.getValue());
			}
		}
	}

	private void populateResponsibleUsersForRegion(RegionReferenceDto regionReferenceDto) {
		List<UserReferenceDto> items = FacadeProvider.getUserFacade()
			.getUsersByRegionAndRights(
				regionReferenceDto != null ? regionReferenceDto : currentUserDto().getRegion(),
				null,
				getResponsibleUserRights());

		populateResponsibleUsers(items);
	}

	private void populateResponsibleUsers(List<UserReferenceDto> items) {
		final ComboBox responsibleUsersField = getField(EnvironmentCriteria.RESPONSIBLE_USER);
		responsibleUsersField.removeAllItems();
		responsibleUsersField.addItems(items);
	}
}
