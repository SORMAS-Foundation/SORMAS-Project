package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDateType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class ContactsFilterForm extends AbstractFilterForm<ContactCriteria> {

	private static final long serialVersionUID = -88229997565902191L;

	private static final String DISTRICT_INFO_LABEL_ID = "infoContactsViewRegionDistrictFilter";
	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String CHECKBOX_STYLE = CssStyles.CHECKBOX_FILTER_INLINE + " " + CssStyles.VSPACE_3;

	private static final String MORE_FILTERS_HTML = filterLocs(
		ContactCriteria.REGION,
		ContactCriteria.DISTRICT,
		ContactCriteria.COMMUNITY,
		DISTRICT_INFO_LABEL_ID,
		ContactCriteria.CONTACT_OFFICER,
		ContactCriteria.REPORTING_USER_ROLE,
		ContactCriteria.FOLLOW_UP_UNTIL_TO,
		ContactCriteria.SYMPTOM_JOURNAL_STATUS,
		ContactCriteria.VACCINATION_STATUS,
		ContactCriteria.RELATION_TO_CASE,
		ContactCriteria.BIRTHDATE_YYYY,
		ContactCriteria.BIRTHDATE_MM,
		ContactCriteria.BIRTHDATE_DD)
		+ filterLocs(
			ContactCriteria.RETURNING_TRAVELER,
			ContactCriteria.QUARANTINE_TYPE,
			ContactDto.QUARANTINE_TO,
			ContactCriteria.QUARANTINE_ORDERED_VERBALLY,
			ContactCriteria.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			ContactCriteria.QUARANTINE_NOT_ORDERED,
			ContactCriteria.ONLY_QUARANTINE_HELP_NEEDED,
			ContactCriteria.ONLY_HIGH_PRIORITY_CONTACTS,
			ContactCriteria.WITH_EXTENDED_QUARANTINE,
			ContactCriteria.WITH_REDUCED_QUARANTINE,
			ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE,
			ContactCriteria.ONLY_CONTACTS_FROM_OTHER_INSTANCES,
			ContactCriteria.INCLUDE_CONTACTS_FROM_OTHER_JURISDICTIONS)

		+ loc(WEEK_AND_DATE_FILTER);

	protected ContactsFilterForm() {
		super(
			ContactCriteria.class,
			ContactIndexDto.I18N_PREFIX,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			ContactIndexDto.CONTACT_CLASSIFICATION,
			ContactIndexDto.DISEASE,
			ContactCriteria.DISEASE_VARIANT,
			ContactIndexDto.CASE_CLASSIFICATION,
			ContactIndexDto.CONTACT_CATEGORY,
			ContactIndexDto.FOLLOW_UP_STATUS,
			ContactCriteria.CONTACT_OR_CASE_LIKE,
			ContactCriteria.PERSON_LIKE,
			ContactCriteria.EVENT_LIKE };
	}

	@Override
	protected String createMoreFiltersHtmlLayout() {
		return MORE_FILTERS_HTML;
	}

	@Override
	protected void addFields() {

		addField(FieldConfiguration.pixelSized(ContactIndexDto.CONTACT_CLASSIFICATION, 140));
		addField(FieldConfiguration.pixelSized(ContactIndexDto.DISEASE, 140));
		addField(
			FieldConfiguration
				.withCaptionAndPixelSized(ContactCriteria.DISEASE_VARIANT, I18nProperties.getCaption(Captions.Contact_cazeDiseaseVariant), 140),
			ComboBox.class);

		ComboBox caseClassificationField = addField(FieldConfiguration.pixelSized(ContactIndexDto.CASE_CLASSIFICATION, 140));
		caseClassificationField.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_CLASSIFICATION));

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			addField(FieldConfiguration.pixelSized(ContactIndexDto.CONTACT_CATEGORY, 140));
		}

		addField(FieldConfiguration.pixelSized(ContactIndexDto.FOLLOW_UP_STATUS, 140));

		TextField searchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(ContactCriteria.CONTACT_OR_CASE_LIKE, I18nProperties.getString(Strings.promptContactsSearchField), 200));
		searchField.setNullRepresentation("");

		TextField personLikeField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(ContactCriteria.PERSON_LIKE, I18nProperties.getString(Strings.promptRelatedPersonLikeField), 200));
		personLikeField.setNullRepresentation("");

		TextField eventSearchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(ContactCriteria.EVENT_LIKE, I18nProperties.getString(Strings.promptCaseOrContactEventSearchField), 200));
		if (UiUtil.permitted(UserRight.EVENT_VIEW)) {
			eventSearchField.setNullRepresentation("");
		} else {
			eventSearchField.setVisible(false);
		}
	}

	@Override
	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		UserDto user = currentUserDto();

		if (user.getRegion() == null) {
			ComboBox regionField = addField(
				moreFiltersContainer,
				FieldConfiguration.withCaptionAndPixelSized(
					ContactCriteria.REGION,
					I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.REGION_UUID),
					240));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
		}

		ComboBox districtField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.DISTRICT,
				I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISTRICT_UUID),
				240));
		districtField.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));

		ComboBox communityField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.COMMUNITY,
				I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.COMMUNITY_UUID),
				240));

		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoContactsViewRegionDistrictFilter), ContentMode.HTML);
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY, AbstractFilterForm.FILTER_ITEM_STYLE);
		moreFiltersContainer.addComponent(infoLabel, DISTRICT_INFO_LABEL_ID);

		ComboBox officerField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.CONTACT_OFFICER,
				I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID),
				140));
		officerField.addItems(fetchContactResponsiblesByRegion(currentUserDto().getRegion()));
		ComboBox reportedByField = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(ContactCriteria.REPORTING_USER_ROLE, I18nProperties.getString(Strings.reportedBy), 140));
		reportedByField.addItems(FacadeProvider.getUserRoleFacade().getAllActiveAsReference());
		Field<?> followUpUntilTo = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.FOLLOW_UP_UNTIL_TO,
				I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.FOLLOW_UP_UNTIL),
				200));
		followUpUntilTo.removeAllValidators();

		if (FacadeProvider.getConfigFacade().isExternalJournalActive()) {
			addField(
				moreFiltersContainer,
				FieldConfiguration.withCaptionAndPixelSized(
					ContactCriteria.SYMPTOM_JOURNAL_STATUS,
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SYMPTOM_JOURNAL_STATUS),
					240));
		}
		addField(moreFiltersContainer, FieldConfiguration.pixelSized(ContactCriteria.VACCINATION_STATUS, 140));

		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.RELATION_TO_CASE,
				I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.RELATION_TO_CASE),
				200));

		addField(moreFiltersContainer, ComboBox.class, FieldConfiguration.pixelSized(ContactCriteria.RETURNING_TRAVELER, 200));
		addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactCriteria.QUARANTINE_TYPE,
				I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE),
				140));

		Field<?> quarantineTo = addField(
			moreFiltersContainer,
			FieldConfiguration.withCaptionAndPixelSized(
				ContactDto.QUARANTINE_TO,
				I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_TO),
				140));
		quarantineTo.removeAllValidators();

		addBirthDateFields(moreFiltersContainer, ContactCriteria.BIRTHDATE_YYYY, ContactCriteria.BIRTHDATE_MM, ContactCriteria.BIRTHDATE_DD);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					ContactCriteria.QUARANTINE_ORDERED_VERBALLY,
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_ORDERED_VERBALLY),
					null,
					CHECKBOX_STYLE));
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					ContactCriteria.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
					I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
					null,
					CHECKBOX_STYLE));
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					ContactCriteria.QUARANTINE_NOT_ORDERED,
					I18nProperties.getCaption(Captions.contactQuarantineNotOrdered),
					null,
					CHECKBOX_STYLE));
		}

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.ONLY_QUARANTINE_HELP_NEEDED,
				I18nProperties.getCaption(Captions.contactOnlyQuarantineHelpNeeded),
				null,
				CHECKBOX_STYLE));
		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.ONLY_HIGH_PRIORITY_CONTACTS,
				I18nProperties.getCaption(Captions.contactOnlyHighPriorityContacts),
				null,
				CHECKBOX_STYLE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.WITH_EXTENDED_QUARANTINE,
				I18nProperties.getCaption(Captions.contactOnlyWithExtendedQuarantine),
				I18nProperties.getDescription(Descriptions.descContactOnlyWithExtendedQuarantine),
				CHECKBOX_STYLE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.WITH_REDUCED_QUARANTINE,
				I18nProperties.getCaption(Captions.contactOnlyWithReducedQuarantine),
				I18nProperties.getDescription(Descriptions.descContactOnlyWithReducedQuarantine),
				CHECKBOX_STYLE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE,
				I18nProperties.getCaption(Captions.contactOnlyWithSharedEventWithSourceCase),
				null,
				CHECKBOX_STYLE)).setVisible(UiUtil.permitted(UserRight.EVENT_VIEW));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				ContactCriteria.ONLY_CONTACTS_FROM_OTHER_INSTANCES,
				I18nProperties.getCaption(Captions.contactOnlyFromOtherInstances),
				null,
				CHECKBOX_STYLE));

		final JurisdictionLevel userJurisdictionLevel = UserProvider.getCurrent().getJurisdictionLevel();
		if (userJurisdictionLevel != JurisdictionLevel.NATION && userJurisdictionLevel != JurisdictionLevel.NONE) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					ContactCriteria.INCLUDE_CONTACTS_FROM_OTHER_JURISDICTIONS,
					I18nProperties.getCaption(Captions.contactIncludeContactsFromOtherJurisdictions),
					I18nProperties.getDescription(Descriptions.descContactIncludeContactsFromOtherJurisdictions),
					CHECKBOX_STYLE));
		}

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		switch (propertyId) {
		case ContactCriteria.REGION: {
			RegionReferenceDto region = (RegionReferenceDto) event.getProperty().getValue();
			if (region != null) {
				applyRegionFilterDependency(region, ContactCriteria.DISTRICT);
				clearAndDisableFields(ContactCriteria.COMMUNITY);
			} else {
				clearAndDisableFields(ContactCriteria.DISTRICT, ContactCriteria.COMMUNITY);
			}
			populateContactResponsiblesForRegion(region);
			break;
		}
		case ContactCriteria.DISTRICT: {
			DistrictReferenceDto district = (DistrictReferenceDto) event.getProperty().getValue();
			if (district != null) {
				applyDistrictDependency(district, ContactCriteria.COMMUNITY);
			} else {
				clearAndDisableFields(ContactCriteria.COMMUNITY);
			}
			populateContactResponsiblesForDistrict(district);
			break;
		}
		case ContactIndexDto.DISEASE: {
			Disease disease = (Disease) event.getProperty().getValue();
			ComboBox diseaseVariantField = getField(ContactCriteria.DISEASE_VARIANT);
			if (disease != null) {
				List<DiseaseVariant> diseaseVariants =
					FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
				FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
				diseaseVariantField.setEnabled(!diseaseVariants.isEmpty());
			} else {
				diseaseVariantField.setValue(null);
				diseaseVariantField.setEnabled(false);
			}
		}
		case ContactCriteria.FOLLOW_UP_UNTIL_TO: {
			getValue().followUpUntilToPrecise(event.getProperty().getValue() != null);
			break;
		}
		case ContactCriteria.EVENT_LIKE: {
			String eventLike = (String) event.getProperty().getValue();
			if (StringUtils.isBlank(eventLike)) {
				clearAndDisableFields(ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE);
			} else {
				enableFields(ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE);
			}
			break;
		}
		case ContactCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(ContactCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(ContactCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ContactCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		}
	}

	@Override
	protected void applyDependenciesOnNewValue(ContactCriteria newValue) {

		final RegionReferenceDto region = newValue.getRegion();
		final DistrictReferenceDto district = newValue.getDistrict();
		applyRegionAndDistrictFilterDependency(region, ContactCriteria.DISTRICT, district, ContactCriteria.COMMUNITY);

		final UserDto user = currentUserDto();

		ComboBox officerField = getField(ContactCriteria.CONTACT_OFFICER);
		if (user.getRegion() != null) {
			officerField.addItems(
				FacadeProvider.getUserFacade().getUsersByRegionAndRights(user.getRegion(), newValue.getDisease(), UserRight.CONTACT_RESPONSIBLE));
		} else if (region != null) {
			officerField
				.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRights(region, newValue.getDisease(), UserRight.CONTACT_RESPONSIBLE));
		} else {
			officerField.removeAllItems();
		}
		ComboBox birthDateDD = getField(ContactCriteria.BIRTHDATE_DD);
		if (getField(ContactCriteria.BIRTHDATE_YYYY).getValue() != null && getField(ContactCriteria.BIRTHDATE_MM).getValue() != null) {
			birthDateDD.addItems(
				DateHelper.getDaysInMonth(
					(Integer) getField(ContactCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(ContactCriteria.BIRTHDATE_YYYY).getValue()));
			birthDateDD.setEnabled(true);
		} else {
			birthDateDD.clear();
			birthDateDD.setEnabled(false);
		}

		// Date/Epi week filter
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		ContactDateType contactDateType = newValue.getReportDateFrom() != null
			? ContactDateType.REPORT_DATE
			: newValue.getLastContactDateFrom() != null ? ContactDateType.LAST_CONTACT_DATE : null;
		weekAndDateFilter.getDateTypeSelector().setValue(contactDateType);
		weekAndDateFilter.getDateFilterOptionFilter().setValue(newValue.getDateFilterOption());
		Date dateFrom = contactDateType == ContactDateType.REPORT_DATE
			? newValue.getReportDateFrom()
			: contactDateType == ContactDateType.LAST_CONTACT_DATE ? newValue.getLastContactDateFrom() : null;
		Date dateTo = contactDateType == ContactDateType.REPORT_DATE
			? newValue.getReportDateTo()
			: contactDateType == ContactDateType.LAST_CONTACT_DATE ? newValue.getLastContactDateTo() : null;

		if (DateFilterOption.EPI_WEEK.equals(newValue.getDateFilterOption())) {
			weekAndDateFilter.getWeekFromFilter().setValue(dateFrom == null ? null : DateHelper.getEpiWeek(dateFrom));
			weekAndDateFilter.getWeekToFilter().setValue(dateTo == null ? null : DateHelper.getEpiWeek(dateTo));
		} else {
			weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
			weekAndDateFilter.getDateToFilter().setValue(dateTo);
		}

		if (StringUtils.isBlank(newValue.getEventLike())) {
			clearAndDisableFields(ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE);
		} else {
			enableFields(ContactCriteria.ONLY_CONTACTS_SHARING_EVENT_WITH_SOURCE_CASE);
		}

		ComboBox diseaseField = getField(ContactIndexDto.DISEASE);
		ComboBox diseaseVariantField = getField(ContactCriteria.DISEASE_VARIANT);
		Disease disease = (Disease) diseaseField.getValue();
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

	@Override
	@SuppressWarnings("rawtypes")
	protected Stream<Field> streamFieldsForEmptyCheck(CustomLayout layout) {
		HorizontalLayout dateFilterLayout = (HorizontalLayout) getMoreFiltersContainer().getComponent(WEEK_AND_DATE_FILTER);
		@SuppressWarnings("unchecked")
		EpiWeekAndDateFilterComponent<NewCaseDateType> weekAndDateFilter =
			(EpiWeekAndDateFilterComponent<NewCaseDateType>) dateFilterLayout.getComponent(0);

		return super.streamFieldsForEmptyCheck(layout).filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter());
	}

	private HorizontalLayout buildWeekAndDateFilter() {

		EpiWeekAndDateFilterComponent<ContactDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			null,
			ContactDateType.values(),
			I18nProperties.getString(Strings.promptContactDateType),
			null,
			this);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<ContactDateType> weekAndDateFilter) {
		ContactCriteria criteria = getValue();

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
			ContactDateType contactDateType = (ContactDateType) weekAndDateFilter.getDateTypeSelector().getValue();
			if (contactDateType == ContactDateType.LAST_CONTACT_DATE) {
				criteria.lastContactDateBetween(fromDate, toDate);
				criteria.reportDateBetween(null, null);
			} else {
				criteria.reportDateBetween(fromDate, toDate);
				criteria.lastContactDateBetween(null, null);
			}
			criteria.dateFilterOption(dateFilterOption);
		} else {
			weekAndDateFilter.setNotificationsForMissingFilters();
		}
	}

	private void populateContactResponsiblesForRegion(RegionReferenceDto regionReferenceDto) {
		List<UserReferenceDto> items =
			fetchContactResponsiblesByRegion(regionReferenceDto != null ? regionReferenceDto : currentUserDto().getRegion());
		populateContactResponsibles(items);
	}

	private void populateContactResponsiblesForDistrict(DistrictReferenceDto districtReferenceDto) {
		if (districtReferenceDto != null) {
			Disease selectedDisease = (Disease) getField(ContactIndexDto.DISEASE).getValue();
			List<UserReferenceDto> items =
				FacadeProvider.getUserFacade().getUserRefsByDistrict(districtReferenceDto, selectedDisease, UserRight.CONTACT_RESPONSIBLE);
			populateContactResponsibles(items);
		} else {
			final ComboBox regionField = getField(ContactCriteria.REGION);
			if (regionField != null) {
				populateContactResponsiblesForRegion((RegionReferenceDto) regionField.getValue());
			}
		}
	}

	private void populateContactResponsibles(List<UserReferenceDto> items) {
		final ComboBox officerField = getField(ContactCriteria.CONTACT_OFFICER);
		officerField.removeAllItems();
		officerField.addItems(items);
	}

	private List<UserReferenceDto> fetchContactResponsiblesByRegion(RegionReferenceDto regionReferenceDto) {
		Disease selectedDisease = (Disease) getField(ContactIndexDto.DISEASE).getValue();
		return FacadeProvider.getUserFacade().getUsersByRegionAndRights(regionReferenceDto, selectedDisease, UserRight.CONTACT_RESPONSIBLE);
	}
}
