package de.symeda.sormas.ui.immunization.components.filter;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDateType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
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

public class ImmunizationFilterForm extends AbstractFilterForm<ImmunizationCriteria> {

	private static final String WEEK_AND_DATE_FILTER = "weekAndDateFilter";

	private static final String MORE_FILTERS_HTML = filterLocs(
		ImmunizationCriteria.REGION,
		ImmunizationCriteria.DISTRICT,
		ImmunizationCriteria.COMMUNITY,
		ImmunizationCriteria.FACILITY_TYPE_GROUP,
		ImmunizationCriteria.FACILITY_TYPE,
		ImmunizationCriteria.HEALTH_FACILITY,
		ImmunizationCriteria.ONLY_PERSONS_WITH_OVERDUE_IMMUNIZATION) + loc(WEEK_AND_DATE_FILTER);

	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox communityFilter;

	public ImmunizationFilterForm() {
		super(ImmunizationCriteria.class, ImmunizationCriteria.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {
		return new String[] {
			ImmunizationCriteria.DISEASE,
			ImmunizationCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
			ImmunizationCriteria.BIRTHDATE_YYYY,
			ImmunizationCriteria.BIRTHDATE_MM,
			ImmunizationCriteria.BIRTHDATE_DD,
			ImmunizationCriteria.MEANS_OF_IMMUNIZATION,
			ImmunizationCriteria.IMMUNIZATION_MANAGEMENT_STATUS,
			ImmunizationCriteria.IMMUNIZATION_STATUS };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {
		addField(FieldConfiguration.pixelSized(ImmunizationCriteria.DISEASE, 140));

		final TextField searchField = addField(
			FieldConfiguration.withCaptionAndPixelSized(
				ImmunizationCriteria.NAME_ADDRESS_PHONE_EMAIL_LIKE,
				I18nProperties.getString(Strings.promptPersonsSearchField),
				200));
		searchField.setNullRepresentation("");

		addBirthDateFields(getContent(), ImmunizationCriteria.BIRTHDATE_YYYY, ImmunizationCriteria.BIRTHDATE_MM, ImmunizationCriteria.BIRTHDATE_DD);

		addFields(
			FieldConfiguration.pixelSized(ImmunizationCriteria.MEANS_OF_IMMUNIZATION, 140),
			FieldConfiguration.pixelSized(ImmunizationCriteria.IMMUNIZATION_MANAGEMENT_STATUS, 140),
			FieldConfiguration.pixelSized(ImmunizationCriteria.IMMUNIZATION_STATUS, 140));
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		if (currentUserDto().getRegion() == null) {
			regionFilter = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.REGION, 140));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		districtFilter = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.DISTRICT, 140));
		districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
		if (currentUserDto().getDistrict() != null) {
			districtFilter.setVisible(false);
		}

		communityFilter = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.COMMUNITY, 140));

		ComboBox typeGroup = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.FACILITY_TYPE_GROUP, 140));
		typeGroup.setInputPrompt(I18nProperties.getCaption(Captions.Facility_typeGroup));
		typeGroup.removeAllItems();
		FieldHelper.updateEnumData(typeGroup, FacilityTypeGroup.getAccomodationGroups());

		ComboBox type = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.FACILITY_TYPE, 140));
		type.setInputPrompt(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
		type.removeAllItems();

		ComboBox facilityField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(ImmunizationCriteria.HEALTH_FACILITY, 140));
		facilityField.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ImmunizationCriteria.ONLY_PERSONS_WITH_OVERDUE_IMMUNIZATION,
				I18nProperties.getCaption(Captions.immunizationOnlyPersonsWithOverdueImmunization),
				null,
				CssStyles.CHECKBOX_FILTER_INLINE));

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {

		super.applyDependenciesOnFieldChange(propertyId, event);

		final ImmunizationCriteria criteria = getValue();

		final ComboBox facilityTypeGroupField = getField(ImmunizationCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(ImmunizationCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(ImmunizationCriteria.HEALTH_FACILITY);

		final UserDto user = currentUserDto();
		final DistrictReferenceDto currentDistrict =
			user.getDistrict() != null ? user.getDistrict() : (DistrictReferenceDto) districtFilter.getValue();

		switch (propertyId) {
		case ImmunizationCriteria.REGION: {
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
		case ImmunizationCriteria.DISTRICT: {
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
		case ImmunizationCriteria.COMMUNITY: {
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
		case ImmunizationCriteria.FACILITY_TYPE_GROUP: {
			FacilityTypeGroup typeGroup = (FacilityTypeGroup) event.getProperty().getValue();
			if (!DataHelper.equal(typeGroup, criteria.getFacilityTypeGroup())) {
				if (typeGroup != null) {
					enableFields(ImmunizationCriteria.FACILITY_TYPE);
					FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(typeGroup));
					facilityField.setValue(null);
				} else {
					clearAndDisableFields(facilityTypeField, facilityField);
				}
			}

			break;
		}
		case ImmunizationCriteria.FACILITY_TYPE: {
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
		case ImmunizationCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(ImmunizationCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(ImmunizationCriteria criteria) {

		final UserDto user = currentUserDto();

		UserProvider currentUserProvider = UserProvider.getCurrent();
		final JurisdictionLevel userJurisdictionLevel = currentUserProvider != null ? UserProvider.getCurrent().getJurisdictionLevel() : null;

		final ComboBox facilityTypeGroupField = getField(ImmunizationCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(ImmunizationCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(ImmunizationCriteria.HEALTH_FACILITY);

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

		ComboBox birthDateDD = getField(ImmunizationCriteria.BIRTHDATE_DD);
		if (getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue() != null && getField(ImmunizationCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ImmunizationCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}

		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<ImmunizationDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<ImmunizationDateType>) dateFilterLayout.getComponent(0);

		ImmunizationDateType immunizationDateType = criteria.getImmunizationDateType();
		weekAndDateFilter.getDateTypeSelector().setValue(immunizationDateType);
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
		EpiWeekAndDateFilterComponent<ImmunizationDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<ImmunizationDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent<ImmunizationDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			null,
			ImmunizationDateType.values(),
			I18nProperties.getString(Strings.promptImmunizationDateType),
			null,
			this);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptImmunizationEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptImmunizationEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptImmunizationDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptImmunizationDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<ImmunizationDateType> weekAndDateFilter) {
		ImmunizationCriteria criteria = getValue();

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
			ImmunizationDateType immunizationDateType = (ImmunizationDateType) weekAndDateFilter.getDateTypeSelector().getValue();
			criteria.setImmunizationDateType(immunizationDateType);
			criteria.setFromDate(fromDate);
			criteria.setToDate(toDate);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}

	private void hideAndFillJurisdictionFilters() {

		regionFilter.setVisible(false);
		regionFilter.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		districtFilter.setVisible(false);
		districtFilter.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		communityFilter.setVisible(false);
		communityFilter.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	@Override
	public void setValue(ImmunizationCriteria newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		if (newFieldValue != null
			&& (regionFilter.isVisible() || districtFilter.isVisible() || communityFilter.isVisible())
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFilters();
		}
	}
}
