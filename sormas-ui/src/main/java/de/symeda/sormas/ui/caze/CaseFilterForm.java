package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Date;
import java.util.stream.Stream;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CaseFilterForm extends AbstractFilterForm<CaseCriteria> {

	private static final long serialVersionUID = -8326451364091398731L;

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	//@formatter:off
	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(CaseCriteria.PRESENT_CONDITION,
			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseCriteria.FACILITY_TYPE_GROUP, CaseCriteria.FACILITY_TYPE, CaseDataDto.HEALTH_FACILITY,
			CaseDataDto.POINT_OF_ENTRY, CaseDataDto.SURVEILLANCE_OFFICER, CaseCriteria.REPORTING_USER_ROLE,
			CaseCriteria.REPORTING_USER_LIKE, CaseDataDto.QUARANTINE_TO, CaseCriteria.FOLLOW_UP_UNTIL_TO,
			CaseCriteria.BIRTHDATE_YYYY,
			CaseCriteria.BIRTHDATE_MM,
			CaseCriteria.BIRTHDATE_DD)			
			+ filterLocsCss("vspace-3", CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES,
					CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY, CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA,
					CaseCriteria.EXCLUDE_SHARED_CASES, CaseCriteria.WITHOUT_RESPONSIBLE_OFFICER, CaseCriteria.WITH_EXTENDED_QUARANTINE)
			+ loc(WEEK_AND_DATE_FILTER);
	//@formatter:on

	protected CaseFilterForm() {
		super(CaseCriteria.class, CaseDataDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			CaseDataDto.CASE_ORIGIN,
			CaseDataDto.OUTCOME,
			CaseDataDto.DISEASE,
			CaseDataDto.CASE_CLASSIFICATION,
			CaseDataDto.FOLLOW_UP_STATUS,
			CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		if (!UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			addField(getContent(), FieldConfiguration.pixelSized(CaseDataDto.CASE_ORIGIN, 140));
		}
		addFields(FieldConfiguration.pixelSized(CaseDataDto.OUTCOME, 140), FieldConfiguration.pixelSized(CaseDataDto.DISEASE, 140));

		if (isGermanServer()) {
			addField(FieldConfiguration.pixelSized(CaseDataDto.CASE_CLASSIFICATION, 140));
		} else {
			final ComboBox caseClassification = addField(CaseDataDto.CASE_CLASSIFICATION, ComboBox.class);
			caseClassification.setWidth(140, Sizeable.Unit.PIXELS);
			caseClassification.removeItem(CaseClassification.CONFIRMED_NO_SYMPTOMS);
			caseClassification.removeItem(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);
		}
		addFields(FieldConfiguration.pixelSized(CaseDataDto.FOLLOW_UP_STATUS, 140));

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE, I18nProperties.getString(Strings.promptCasesSearchField), 200));
		searchField.setNullRepresentation("");
	}

	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		ComboBox presentConditionField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.PRESENT_CONDITION, 140));
		presentConditionField.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));

		UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() == null) {
			ComboBox regionField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.REGION, 140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		}

		ComboBox districtField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.DISTRICT, 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.COMMUNITY, 140));

		if (!UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {

			ComboBox typeGroup = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.FACILITY_TYPE_GROUP, 140));
			typeGroup.setInputPrompt(I18nProperties.getCaption(Captions.Facility_typeGroup));
			typeGroup.removeAllItems();
			FieldHelper.updateEnumData(typeGroup, FacilityTypeGroup.getAccomodationGroups());

			ComboBox type = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.FACILITY_TYPE, 140));
			type.setInputPrompt(I18nProperties.getPrefixCaption(FacilityDto.I18N_PREFIX, FacilityDto.TYPE));
			type.removeAllItems();

			ComboBox facilityField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.HEALTH_FACILITY, 140));
			facilityField.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			ComboBox pointOfEntryField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.POINT_OF_ENTRY, 140));
			pointOfEntryField.setDescription(I18nProperties.getDescription(Descriptions.descPointOfEntryFilter));
		}

		ComboBox officerField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.SURVEILLANCE_OFFICER, 140));
		if (user.getRegion() != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.SURVEILLANCE_OFFICER));
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_FOLLOWUP)) {
			Field<?> followUpUntilTo = addField(
					moreFiltersContainer,
					FieldConfiguration.withCaptionAndPixelSized(
							CaseCriteria.FOLLOW_UP_UNTIL_TO,
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.FOLLOW_UP_UNTIL),
							200));
			followUpUntilTo.removeAllValidators();
		}

		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(CaseCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));

		TextField reportingUserField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.REPORTING_USER_LIKE, 200));
		reportingUserField.setNullRepresentation("");
		reportingUserField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, CaseDataDto.REPORTING_USER));

		Field<?> quarantineTo = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.QUARANTINE_TO, 200));
		quarantineTo.removeAllValidators();
		ComboBox birthDateYYYY = addField(moreFiltersContainer, CaseCriteria.BIRTHDATE_YYYY, ComboBox.class);
		birthDateYYYY.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_YYYY));
		birthDateYYYY.setWidth(140, Unit.PIXELS);
		birthDateYYYY.addItems(DateHelper.getYearsToNow());
		birthDateYYYY.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID_TOSTRING);
		ComboBox birthDateMM = addField(moreFiltersContainer, CaseCriteria.BIRTHDATE_MM, ComboBox.class);
		birthDateMM.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_MM));
		birthDateMM.setWidth(140, Unit.PIXELS);
		birthDateMM.addItems(DateHelper.getMonthsInYear());
		ComboBox birthDateDD = addField(moreFiltersContainer, CaseCriteria.BIRTHDATE_DD, ComboBox.class);
		birthDateDD.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.BIRTH_DATE_DD));
		birthDateDD.setWidth(140, Unit.PIXELS);
		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES,
				I18nProperties.getCaption(Captions.caseFilterWithoutGeo),
				I18nProperties.getDescription(Descriptions.descCaseFilterWithoutGeo),
				CssStyles.CHECKBOX_FILTER_INLINE));

		if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY,
					I18nProperties.getCaption(Captions.caseFilterPortHealthWithoutFacility),
					I18nProperties.getDescription(Descriptions.descCaseFilterPortHealthWithoutFacility),
					CssStyles.CHECKBOX_FILTER_INLINE));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA,
					I18nProperties.getCaption(Captions.caseFilterCasesWithCaseManagementData),
					I18nProperties.getDescription(Descriptions.descCaseFilterCasesWithCaseManagementData),
					CssStyles.CHECKBOX_FILTER_INLINE));
		}

		if (user.getRegion() != null || user.getDistrict() != null) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.EXCLUDE_SHARED_CASES,
					I18nProperties.getCaption(Captions.caseFilterExcludeSharedCases),
					I18nProperties.getDescription(Descriptions.descCaseFilterExcludeSharedCasesString),
					CssStyles.CHECKBOX_FILTER_INLINE));
		}

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.WITHOUT_RESPONSIBLE_OFFICER,
				I18nProperties.getCaption(Captions.caseFilterWithoutResponsibleOfficer),
				I18nProperties.getDescription(Descriptions.descCaseFilterWithoutResponsibleOfficer),
				CssStyles.CHECKBOX_FILTER_INLINE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.WITH_EXTENDED_QUARANTINE,
				I18nProperties.getCaption(Captions.caseFilterWithExtendedQuarantine),
				I18nProperties.getDescription(Descriptions.descCaseFilterWithExtendedQuarantine),
				CssStyles.CHECKBOX_FILTER_INLINE));

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		CaseCriteria criteria = getValue();

		switch (propertyId) {
		case CaseDataDto.REGION: {
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(region, criteria.getRegion())) {
				getField(CaseDataDto.DISTRICT).setValue(null);
				getField(CaseDataDto.COMMUNITY).setValue(null);
				getField(CaseCriteria.FACILITY_TYPE_GROUP).setValue(null);
				getField(CaseCriteria.FACILITY_TYPE).setValue(null);
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
				getField(CaseDataDto.POINT_OF_ENTRY).setValue(null);
			}

			break;
		}
		case CaseDataDto.DISTRICT: {
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(district, criteria.getDistrict())) {
				getField(CaseDataDto.COMMUNITY).setValue(null);
				getField(CaseCriteria.FACILITY_TYPE_GROUP).setValue(null);
				getField(CaseCriteria.FACILITY_TYPE).setValue(null);
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
				getField(CaseDataDto.POINT_OF_ENTRY).setValue(null);
			}

			break;
		}
		case CaseDataDto.COMMUNITY: {
			CommunityReferenceDto community = (CommunityReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(community, criteria.getCommunity())) {
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
			}
			break;
		}
		case CaseCriteria.FACILITY_TYPE_GROUP: {
			FacilityTypeGroup typeGroup = (FacilityTypeGroup) event.getProperty().getValue();
			if (!DataHelper.equal(typeGroup, criteria.getFacilityTypeGroup())) {
				getField(CaseCriteria.FACILITY_TYPE).setValue(null);
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
			}
			break;
		}
		case CaseCriteria.FACILITY_TYPE:
			FacilityType type = (FacilityType) event.getProperty().getValue();
			if (!DataHelper.equal(type, criteria.getFacilityType())) {
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {

		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	@Override
	protected void applyDependenciesOnNewValue(CaseCriteria criteria) {

		ComboBox districtField = (ComboBox) getField(CaseDataDto.DISTRICT);
		districtField.setEnabled(false);

		ComboBox communityField = (ComboBox) getField(CaseDataDto.COMMUNITY);
		districtField.setEnabled(false);

		UserDto user = UserProvider.getCurrent().getUser();

		if (user.getRegion() != null) {
			if (user.getDistrict() == null) {
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
				districtField.setEnabled(true);
			}
		} else {
			RegionReferenceDto region = criteria.getRegion();

			if (region == null) {
				districtField.setEnabled(false);
			} else {
				districtField.setEnabled(true);
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			}
		}

		ComboBox typeGroupField = (ComboBox) getField(CaseCriteria.FACILITY_TYPE_GROUP);
		ComboBox typeField = (ComboBox) getField(CaseCriteria.FACILITY_TYPE);

		if (user.getDistrict() != null && user.getCommunity() == null) {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(user.getDistrict().getUuid()));
			communityField.setEnabled(true);
			typeGroupField.setEnabled(true);
		} else if (criteria.getDistrict() != null) {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(criteria.getDistrict().getUuid()));
			communityField.setEnabled(true);
			typeGroupField.setEnabled(true);
		} else {
			communityField.setEnabled(false);
			typeGroupField.setEnabled(false);
			typeField.setEnabled(false);
		}

		ComboBox facilityField = (ComboBox) getField(CaseDataDto.HEALTH_FACILITY);
		ComboBox pointOfEntryField = (ComboBox) getField(CaseDataDto.POINT_OF_ENTRY);

		DistrictReferenceDto district = criteria.getDistrict();
		FacilityTypeGroup typeGroup = criteria.getFacilityTypeGroup();
		FacilityType type = criteria.getFacilityType();
		CommunityReferenceDto community = criteria.getCommunity();

		if (district == null) {
			communityField.setEnabled(false);
			typeGroupField.setEnabled(false);
			typeField.setEnabled(false);
			if (facilityField != null) {
				facilityField.setEnabled(false);
			}
			if (pointOfEntryField != null) {
				pointOfEntryField.setEnabled(false);
			}
		} else {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));

			if (facilityField != null && type != null) {
				facilityField.setEnabled(true);
				if (community == null) {
					facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, type, true, false));
				} else {
					facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, type, true, false));
				}
			}

			if (pointOfEntryField != null) {
				pointOfEntryField.setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);
				pointOfEntryField.addItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), true));
			}
		}

		if (district != null && typeGroup != null) {
			FacilityType facilityType = (FacilityType) typeField.getValue();
			typeField.removeAllItems();
			typeField.setEnabled(true);
			FieldHelper.updateEnumData(typeField, FacilityType.getAccommodationTypes(typeGroup));
			typeField.setValue(facilityType);
		} else {
			typeField.setEnabled(false);
		}

		if (district != null && type != null && facilityField != null) {
			facilityField.removeAllItems();
			facilityField.setEnabled(true);
			if (community == null) {
				facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, type, true, false));
			} else {
				facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, type, true, false));
			}
		} else {
			facilityField.setEnabled(false);
		}

		getField(CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY).setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);

		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		weekAndDateFilter.getDateTypeSelector().setValue(criteria.getNewCaseDateType());
		weekAndDateFilter.getDateFilterOptionFilter().setValue(criteria.getDateFilterOption());
		Date newCaseDateFrom = criteria.getNewCaseDateFrom();
		Date newCaseDateTo = criteria.getNewCaseDateTo();
		if (newCaseDateFrom != null && newCaseDateTo != null) {
			if (DateFilterOption.EPI_WEEK.equals(criteria.getDateFilterOption())) {
				weekAndDateFilter.getWeekFromFilter().setValue(DateHelper.getEpiWeek(newCaseDateFrom));
				weekAndDateFilter.getWeekToFilter().setValue(DateHelper.getEpiWeek(newCaseDateTo));
			} else {
				weekAndDateFilter.getDateFromFilter().setValue(criteria.getNewCaseDateFrom());
				weekAndDateFilter.getDateToFilter().setValue(criteria.getNewCaseDateTo());
			}
		}
		ComboBox birthDateDD = (ComboBox) getField(CaseCriteria.BIRTHDATE_DD);
		if (getField(CaseCriteria.BIRTHDATE_YYYY).getValue() != null && getField(CaseCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(CaseCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(CaseCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		Button applyButton = ButtonHelper.createButton(Captions.actionApplyDateFilter, null);

		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			applyButton,
			false,
			false,
			I18nProperties.getString(Strings.infoCaseDate),
			NewCaseDateType.class,
			I18nProperties.getString(Strings.promptNewCaseDateType),
			NewCaseDateType.MOST_RELEVANT);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptDateTo));

		applyButton.addClickListener(e -> {
			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			Date fromDate, toDate;
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
				toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}
			if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
				applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
				CaseCriteria criteria = getValue();
				NewCaseDateType newCaseDateType = (NewCaseDateType) weekAndDateFilter.getDateTypeSelector().getValue();

				criteria.newCaseDateBetween(fromDate, toDate, newCaseDateType != null ? newCaseDateType : NewCaseDateType.MOST_RELEVANT);
				criteria.dateFilterOption(dateFilterOption);

				fireValueChange(true);
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
		});

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);
		dateFilterRowLayout.addComponent(applyButton);

		return dateFilterRowLayout;
	}

	public void disableSearchAndReportingUser() {
		getField(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE).setEnabled(false);
		getField(CaseCriteria.REPORTING_USER_LIKE).setEnabled(false);
	}

	public void enableSearchAndReportingUser() {
		getField(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE).setEnabled(true);
		getField(CaseCriteria.REPORTING_USER_LIKE).setEnabled(true);
	}

	@Override
	public void setValue(CaseCriteria newCriteria) throws ReadOnlyException, Converter.ConversionException {

		super.setValue(newCriteria);
		ComboBox typeField = (ComboBox) getField(CaseCriteria.FACILITY_TYPE);
		if (newCriteria.getFacilityType() != null && typeField != null) {
			typeField.setValue(newCriteria.getFacilityType());
			ComboBox facilityField = (ComboBox) getField(CaseDataDto.HEALTH_FACILITY);
			if (newCriteria.getHealthFacility() != null && facilityField != null) {
				facilityField.setValue(newCriteria.getHealthFacility());
			}
		}
	}
}
