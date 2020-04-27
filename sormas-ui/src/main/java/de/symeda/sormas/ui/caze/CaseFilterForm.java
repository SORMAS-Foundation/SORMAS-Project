package de.symeda.sormas.ui.caze;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
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
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.DateFilterOption;
import de.symeda.sormas.ui.utils.*;

import java.util.Date;

import static de.symeda.sormas.ui.utils.LayoutUtil.*;

public class CaseFilterForm extends AbstractFilterForm<CaseCriteria> {

	private static final String EXPAND_COLLAPSE_ID = "expandCollapse";
	private static final String RESET_BUTTON_ID = "reset";
	private static final String MORE_FILTERS_ID = "moreFilters";
	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String HTML_LAYOUT = div(filterLocs(CaseDataDto.CASE_ORIGIN, CaseDataDto.OUTCOME, CaseDataDto.DISEASE, CaseDataDto.CASE_CLASSIFICATION, CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE, EXPAND_COLLAPSE_ID, RESET_BUTTON_ID) +
			loc(MORE_FILTERS_ID));

	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(CaseCriteria.PRESENT_CONDITION, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.HEALTH_FACILITY, CaseDataDto.POINT_OF_ENTRY,
			CaseDataDto.SURVEILLANCE_OFFICER, CaseCriteria.REPORTING_USER_ROLE, CaseCriteria.REPORTING_USER_LIKE, CaseDataDto.QUARANTINE_TO) +
			filterLocsCss("vspace-3", CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES, CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY, CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA, CaseCriteria.EXCLUDE_SHARED_CASES) +
			loc(WEEK_AND_DATE_FILTER);

	private final CustomLayout moreFilters;

	protected CaseFilterForm() {
		super(CaseCriteria.class, CaseDataDto.I18N_PREFIX, null, true);

		moreFilters = new CustomLayout();
		moreFilters.setTemplateContents(MORE_FILTERS_HTML_LAYOUT);
		moreFilters.setVisible(false);
		getContent().addComponent(moreFilters, MORE_FILTERS_ID);

		addMoreFilters();

		addValueChangeListener(e -> updateFieldDependencies((CaseCriteria) e.getProperty().getValue()));
	}

	public void addResetHandler(Button.ClickListener resetHandler) {
		((Button) getContent().getComponent(RESET_BUTTON_ID)).addClickListener(resetHandler);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		if (!UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			addField(getContent(), FieldConfiguration.pixelSized(CaseDataDto.CASE_ORIGIN, 140));
		}
		addFields(FieldConfiguration.pixelSized(CaseDataDto.OUTCOME, 140),
				FieldConfiguration.pixelSized(CaseDataDto.DISEASE, 140),
				FieldConfiguration.pixelSized(CaseDataDto.CASE_CLASSIFICATION, 140)
		);

		TextField searchField = addField(FieldConfiguration.withCaptionAndPixelSized(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE, I18nProperties.getString(Strings.promptCasesSearchField), 200));
		searchField.setNullRepresentation("");

		String showMoreCaption = I18nProperties.getCaption(Captions.actionShowMoreFilters);
		Button expandCollapseFiltersButton = new Button(showMoreCaption, VaadinIcons.CHEVRON_DOWN);
		CssStyles.style(expandCollapseFiltersButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		expandCollapseFiltersButton.addClickListener(e -> {
			boolean isShowMore = expandCollapseFiltersButton.getCaption().equals(showMoreCaption);
			expandCollapseFiltersButton.setCaption(isShowMore ? I18nProperties.getCaption(Captions.actionShowLessFilters) : showMoreCaption);
			expandCollapseFiltersButton.setIcon(isShowMore ? VaadinIcons.CHEVRON_UP : VaadinIcons.CHEVRON_DOWN);

			if (isShowMore) {
				getContent().getComponent(MORE_FILTERS_ID).setVisible(true);
			} else {
				getContent().getComponent(MORE_FILTERS_ID).setVisible(false);
			}
		});

		getContent().addComponent(expandCollapseFiltersButton, EXPAND_COLLAPSE_ID);

		Button resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		getContent().addComponent(resetButton, RESET_BUTTON_ID);
	}

	public void addMoreFilters() {
		ComboBox presentConditionField = addField(moreFilters, FieldConfiguration.pixelSized(CaseCriteria.PRESENT_CONDITION, 140));
		presentConditionField.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));

		UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() == null) {
			addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.REGION, 140));
		}

		ComboBox districtField = addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.DISTRICT, 140));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		if (!UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
			ComboBox facilityField = addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.HEALTH_FACILITY, 140));
			facilityField.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			ComboBox pointOfEntryField = addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.POINT_OF_ENTRY, 140));
			pointOfEntryField.setDescription(I18nProperties.getDescription(Descriptions.descPointOfEntryFilter));
		}

		ComboBox officerField = addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.SURVEILLANCE_OFFICER, 140));
		if (user.getRegion() != null) {
			officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.SURVEILLANCE_OFFICER));
		}

		addField(moreFilters, FieldConfiguration.withCaptionAndPixelSized(CaseCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));

		TextField reportingUserField = addField(moreFilters, FieldConfiguration.pixelSized(CaseCriteria.REPORTING_USER_LIKE, 200));
		reportingUserField.setNullRepresentation("");
		reportingUserField.setInputPrompt(I18nProperties.getPrefixCaption(propertyI18nPrefix, CaseDataDto.REPORTING_USER));

		addField(moreFilters, FieldConfiguration.pixelSized(CaseDataDto.QUARANTINE_TO, 200));

		addField(moreFilters, CheckBox.class, FieldConfiguration.withCaptionAndStyle(CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES, I18nProperties.getCaption(Captions.caseFilterWithoutGeo), I18nProperties.getDescription(Descriptions.descCaseFilterWithoutGeo), CssStyles.CHECKBOX_FILTER_INLINE));

		if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
			addField(moreFilters, CheckBox.class, FieldConfiguration.withCaptionAndStyle(CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY, I18nProperties.getCaption(Captions.caseFilterPortHealthWithoutFacility), I18nProperties.getDescription(Descriptions.descCaseFilterPortHealthWithoutFacility), CssStyles.CHECKBOX_FILTER_INLINE));
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
			addField(moreFilters, CheckBox.class, FieldConfiguration.withCaptionAndStyle(CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA, I18nProperties.getCaption(Captions.caseFilterCasesWithCaseManagementData), I18nProperties.getDescription(Descriptions.descCaseFilterCasesWithCaseManagementData), CssStyles.CHECKBOX_FILTER_INLINE));
		}

		if (user.getRegion() != null || user.getDistrict() != null) {
			addField(moreFilters, CheckBox.class, FieldConfiguration.withCaptionAndStyle(CaseCriteria.EXCLUDE_SHARED_CASES, I18nProperties.getCaption(Captions.caseFilterExcludeSharedCases), I18nProperties.getDescription(Descriptions.descCaseFilterExcludeSharedCasesString), CssStyles.CHECKBOX_FILTER_INLINE));
		}

		moreFilters.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
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
					getField(CaseDataDto.HEALTH_FACILITY).setValue(null);
					getField(CaseDataDto.POINT_OF_ENTRY).setValue(null);
				}

				break;
			}
		}

	}

	@Override
	public void setValue(CaseCriteria criteria) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(criteria);

		HorizontalLayout dateFilterLayout = (HorizontalLayout) ((CustomLayout) getContent().getComponent(MORE_FILTERS_ID)).getComponent(WEEK_AND_DATE_FILTER);
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = (EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);
		weekAndDateFilter.getDateTypeSelector().setValue(criteria.getNewCaseDateType());
		Date newCaseDateFrom = criteria.getNewCaseDateFrom();
		Date newCaseDateTo = criteria.getNewCaseDateTo();
		// Reconstruct date/epi week choice
		if ((newCaseDateFrom != null && newCaseDateTo != null && (DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(newCaseDateFrom)).equals(newCaseDateFrom) && DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(newCaseDateTo)).equals(newCaseDateTo)))
				|| (newCaseDateFrom != null && DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(newCaseDateFrom)).equals(newCaseDateFrom))
				|| (newCaseDateTo != null && DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(newCaseDateTo)).equals(newCaseDateTo))) {
			weekAndDateFilter.getDateFilterOptionFilter().setValue(DateFilterOption.EPI_WEEK);
			weekAndDateFilter.getWeekFromFilter().setValue(DateHelper.getEpiWeek(newCaseDateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(DateHelper.getEpiWeek(newCaseDateTo));
		} else {
			weekAndDateFilter.getDateFilterOptionFilter().setValue(DateFilterOption.DATE);
			weekAndDateFilter.getDateFromFilter().setValue(criteria.getNewCaseDateFrom());
			weekAndDateFilter.getDateToFilter().setValue(criteria.getNewCaseDateTo());
		}

		boolean hasFilter = FieldHelper.streamFields(getContent())
				.filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter())
				.anyMatch(f -> !f.isEmpty());
		boolean hasExpandedFilter = FieldHelper.streamFields(moreFilters)
				.filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter())
				.anyMatch(f -> !f.isEmpty());

		getContent().getComponent(RESET_BUTTON_ID).setVisible(hasFilter);
		moreFilters.setVisible(hasExpandedFilter);

		updateFieldDependencies(criteria);
	}

	private void updateFieldDependencies(CaseCriteria criteria) {
		ComboBox districtField = (ComboBox) getField(CaseDataDto.DISTRICT);
		districtField.setEnabled(false);

		UserDto user = UserProvider.getCurrent().getUser();
		if (user.getRegion() != null && user.getDistrict() == null) {
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
			districtField.setEnabled(true);
		} else {
			RegionReferenceDto region = criteria.getRegion();

			if (region == null) {
				districtField.setEnabled(false);
			} else {
				districtField.setEnabled(true);
				districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			}
		}

		ComboBox facilityField = (ComboBox) getField(CaseDataDto.HEALTH_FACILITY);
		ComboBox pointOfEntryField = (ComboBox) getField(CaseDataDto.POINT_OF_ENTRY);

		DistrictReferenceDto district = criteria.getDistrict();
		if (district == null) {
			facilityField.setEnabled(false);
			pointOfEntryField.setEnabled(false);
		} else {
			facilityField.setEnabled(true);
			facilityField.addItems(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(district, true));

			pointOfEntryField.setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);
			pointOfEntryField.addItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), true));
		}

		getField(CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY).setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);
	}

	private HorizontalLayout buildWeekAndDateFilter() {
		Button applyButton = new Button(I18nProperties.getCaption(Captions.actionApplyDateFilter));
		applyButton.setId("applyDateFilter");

		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(applyButton, false, false, I18nProperties.getString(Strings.infoCaseDate), NewCaseDateType.class, I18nProperties.getString(Strings.promptNewCaseDateType), NewCaseDateType.MOST_RELEVANT);
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
				NewCaseDateType newCaseDateType = (NewCaseDateType) weekAndDateFilter.getDateTypeSelector().getValue();
				getValue().newCaseDateBetween(fromDate, toDate, newCaseDateType != null ? newCaseDateType : NewCaseDateType.MOST_RELEVANT);

				fireValueChange(true);
			} else {
				if (dateFilterOption == DateFilterOption.DATE) {
					Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingDateFilter),
							I18nProperties.getString(Strings.messageMissingDateFilter), Notification.Type.WARNING_MESSAGE, false);
					notification.setDelayMsec(-1);
					notification.show(Page.getCurrent());
				} else {
					Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingEpiWeekFilter),
							I18nProperties.getString(Strings.messageMissingEpiWeekFilter), Notification.Type.WARNING_MESSAGE, false);
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

