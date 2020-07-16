package de.symeda.sormas.ui.caze;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.NewCaseDateType;
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

import java.util.Date;
import java.util.stream.Stream;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

public class CaseFilterForm extends AbstractFilterForm<CaseCriteria> {

	private static final long serialVersionUID = -8326451364091398731L;

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	//@formatter:off
	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(CaseCriteria.PRESENT_CONDITION,
			CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY, CaseDataDto.HEALTH_FACILITY,
			CaseDataDto.POINT_OF_ENTRY, CaseDataDto.SURVEILLANCE_OFFICER, CaseCriteria.REPORTING_USER_ROLE,
			CaseCriteria.REPORTING_USER_LIKE, CaseDataDto.QUARANTINE_TO)
			+ filterLocsCss("vspace-3", CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES,
					CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY, CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA,
					CaseCriteria.EXCLUDE_SHARED_CASES, CaseCriteria.WITHOUT_RESPONSIBLE_OFFICER)
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
		addFields(
			FieldConfiguration.pixelSized(CaseDataDto.OUTCOME, 140),
			FieldConfiguration.pixelSized(CaseDataDto.DISEASE, 140),
			FieldConfiguration.pixelSized(CaseDataDto.CASE_CLASSIFICATION, 140));

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

		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(CaseCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));

		TextField reportingUserField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.REPORTING_USER_LIKE, 200));
		reportingUserField.setNullRepresentation("");
		reportingUserField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, CaseDataDto.REPORTING_USER));

		Field<?> quarantineTo = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.QUARANTINE_TO, 200));
		quarantineTo.removeAllValidators();

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
			}

			break;
		}
		case CaseDataDto.DISTRICT: {
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(district, criteria.getDistrict())) {
				getField(CaseDataDto.COMMUNITY).setValue(null);
				getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
				getField(CaseDataDto.POINT_OF_ENTRY).setValue(null);
			}

			break;
		}
		case CaseDataDto.COMMUNITY:
			CommunityReferenceDto community = (CommunityReferenceDto) event.getProperty().getValue();
			if (!DataHelper.equal(community, criteria.getCommunity())) {
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

		if (user.getDistrict() != null && user.getCommunity() == null) {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(user.getDistrict().getUuid()));
			communityField.setEnabled(true);
		} else if (criteria.getDistrict() != null) {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(criteria.getDistrict().getUuid()));
			communityField.setEnabled(true);
		} else {
			communityField.setEnabled(false);
		}

		ComboBox facilityField = (ComboBox) getField(CaseDataDto.HEALTH_FACILITY);
		ComboBox pointOfEntryField = (ComboBox) getField(CaseDataDto.POINT_OF_ENTRY);

		DistrictReferenceDto district = criteria.getDistrict();
		CommunityReferenceDto community = criteria.getCommunity();

		if (district == null) {
			communityField.setEnabled(false);
			if (facilityField != null) {
				facilityField.setEnabled(false);
			}
			if (pointOfEntryField != null) {
				pointOfEntryField.setEnabled(false);
			}
		} else {
			communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));

			if (facilityField != null) {
				facilityField.setEnabled(true);
				if (community == null) {
					facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(district, true));
				} else {
					facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByCommunity(community, true));
				}
			}

			if (pointOfEntryField != null) {
				pointOfEntryField.setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);
				pointOfEntryField.addItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), true));
			}
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
}
