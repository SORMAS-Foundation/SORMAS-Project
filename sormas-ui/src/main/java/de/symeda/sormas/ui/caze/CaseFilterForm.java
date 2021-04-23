package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.filterLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.disease.DiseaseVariantReferenceDto;
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
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFilterOption;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateTypeHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractFilterForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldConfiguration;
import de.symeda.sormas.ui.utils.FieldHelper;

public class CaseFilterForm extends AbstractFilterForm<CaseCriteria> {

	private static final long serialVersionUID = -8326451364091398731L;

	private static final String WEEK_AND_DATE_FILTER = "moreFilters";

	private static final String MORE_FILTERS_HTML_LAYOUT = filterLocs(
		CaseCriteria.PRESENT_CONDITION,
		CaseDataDto.REGION,
		CaseDataDto.DISTRICT,
		CaseDataDto.COMMUNITY,
		CaseCriteria.FACILITY_TYPE_GROUP,
		CaseCriteria.FACILITY_TYPE,
		CaseDataDto.HEALTH_FACILITY,
		CaseDataDto.POINT_OF_ENTRY,
		CaseDataDto.SURVEILLANCE_OFFICER,
		CaseCriteria.REPORTING_USER_ROLE,
		CaseCriteria.REPORTING_USER_LIKE,
		CaseDataDto.QUARANTINE_TO,
		CaseCriteria.FOLLOW_UP_UNTIL_TO,
		ContactCriteria.SYMPTOM_JOURNAL_STATUS,
		CaseCriteria.BIRTHDATE_YYYY,
		CaseCriteria.BIRTHDATE_MM,
		CaseCriteria.BIRTHDATE_DD)
		+ filterLocs(
			CaseCriteria.MUST_HAVE_NO_GEO_COORDINATES,
			CaseCriteria.MUST_BE_PORT_HEALTH_CASE_WITHOUT_FACILITY,
			CaseCriteria.MUST_HAVE_CASE_MANAGEMENT_DATA,
			CaseCriteria.WITHOUT_RESPONSIBLE_OFFICER,
			CaseCriteria.WITH_EXTENDED_QUARANTINE,
			CaseCriteria.WITH_REDUCED_QUARANTINE,
			CaseCriteria.ONLY_CASES_WITH_EVENTS,
			CaseCriteria.ONLY_CONTACTS_FROM_OTHER_INSTANCES,
			CaseCriteria.ONLY_CASES_WITH_REINFECTION,
			CaseCriteria.INCLUDE_CASES_FROM_OTHER_JURISDICTIONS)
		+ filterLocsCss(
			VSPACE_3,
			CaseCriteria.ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL,
			CaseCriteria.ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL,
			CaseCriteria.ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL)
		+ loc(WEEK_AND_DATE_FILTER);

	protected CaseFilterForm() {
		super(CaseCriteria.class, CaseDataDto.I18N_PREFIX);
	}

	@Override
	protected String[] getMainFilterLocators() {

		return new String[] {
			CaseDataDto.CASE_ORIGIN,
			CaseDataDto.OUTCOME,
			CaseDataDto.DISEASE,
			CaseDataDto.DISEASE_VARIANT,
			CaseDataDto.CASE_CLASSIFICATION,
			CaseDataDto.FOLLOW_UP_STATUS,
			CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE,
			CaseCriteria.EVENT_LIKE };
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

		addField(FieldConfiguration.pixelSized(CaseDataDto.DISEASE_VARIANT, 140), ComboBox.class);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
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

		TextField eventSearchField = addField(
			FieldConfiguration
				.withCaptionAndPixelSized(CaseCriteria.EVENT_LIKE, I18nProperties.getString(Strings.promptCaseOrContactEventSearchField), 200));
		eventSearchField.setNullRepresentation("");
	}

	public void addMoreFilters(CustomLayout moreFiltersContainer) {

		ComboBox presentConditionField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseCriteria.PRESENT_CONDITION, 140));
		presentConditionField.setInputPrompt(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));

		UserDto user = currentUserDto();
		ComboBox regionField = null;
		if (user.getRegion() == null) {
			regionField = addField(moreFiltersContainer, FieldConfiguration.pixelSized(CaseDataDto.REGION, 140));
			regionField.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());
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
		officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.SURVEILLANCE_OFFICER));

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_FOLLOWUP)) {
			Field<?> followUpUntilTo = addField(
				moreFiltersContainer,
				FieldConfiguration.withCaptionAndPixelSized(
					CaseCriteria.FOLLOW_UP_UNTIL_TO,
					I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.FOLLOW_UP_UNTIL),
					200));
			followUpUntilTo.removeAllValidators();
			addField(
				moreFiltersContainer,
				FieldConfiguration.withCaptionAndPixelSized(
					CaseCriteria.SYMPTOM_JOURNAL_STATUS,
					I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SYMPTOM_JOURNAL_STATUS),
					240));
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

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.WITH_REDUCED_QUARANTINE,
				I18nProperties.getCaption(Captions.caseFilterWithReducedQuarantine),
				I18nProperties.getDescription(Descriptions.descCaseFilterWithReducedQuarantine),
				CssStyles.CHECKBOX_FILTER_INLINE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.ONLY_CASES_WITH_EVENTS,
				I18nProperties.getCaption(Captions.caseFilterRelatedToEvent),
				I18nProperties.getDescription(Descriptions.descCaseFilterRelatedToEvent),
				CssStyles.CHECKBOX_FILTER_INLINE));

		addField(
			moreFiltersContainer,
			CheckBox.class,
			FieldConfiguration.withCaptionAndStyle(
				CaseCriteria.ONLY_CONTACTS_FROM_OTHER_INSTANCES,
				I18nProperties.getCaption(Captions.caseFilterOnlyFromOtherInstances),
				I18nProperties.getDescription(Descriptions.descCaseFilterOnlyFromOtherInstances),
				CssStyles.CHECKBOX_FILTER_INLINE));

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			addField(
					moreFiltersContainer,
					CheckBox.class,
					FieldConfiguration.withCaptionAndStyle(
							CaseCriteria.ONLY_CASES_WITH_REINFECTION,
							I18nProperties.getCaption(Captions.caseFilterCasesWithReinfection),
							I18nProperties.getDescription(Descriptions.descCaseFilterCasesWithReinfection),
							CssStyles.CHECKBOX_FILTER_INLINE));
		}

		final JurisdictionLevel userJurisdictionLevel = UserRole.getJurisdictionLevel(UserProvider.getCurrent().getUserRoles());
		if (userJurisdictionLevel != JurisdictionLevel.NATION && userJurisdictionLevel != JurisdictionLevel.NONE) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.INCLUDE_CASES_FROM_OTHER_JURISDICTIONS,
					I18nProperties.getCaption(Captions.caseFilterInludeCasesFromOtherJurisdictions),
					I18nProperties.getDescription(Descriptions.descCaseFilterIncludeCasesFromOtherJurisdictions),
					CssStyles.CHECKBOX_FILTER_INLINE));
		}

		boolean isExternalShareEnabled = FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled();
		if (isExternalShareEnabled) {
			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.ONLY_ENTITIES_NOT_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.caseFilterOnlyCasesNotSharedWithExternalSurvTool),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));

			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.ONLY_ENTITIES_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.caseFilterOnlyCasesSharedWithExternalSurvToo),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));

			addField(
				moreFiltersContainer,
				CheckBox.class,
				FieldConfiguration.withCaptionAndStyle(
					CaseCriteria.ONLY_ENTITIES_CHANGED_SINCE_LAST_SHARED_WITH_EXTERNAL_SURV_TOOL,
					I18nProperties.getCaption(Captions.caseFilterOnlyCasesChangedSinceLastSharedWithExternalSurvTool),
					null,
					CssStyles.CHECKBOX_FILTER_INLINE));
		}

		moreFiltersContainer.addComponent(buildWeekAndDateFilter(isExternalShareEnabled), WEEK_AND_DATE_FILTER);
	}

	@Override
	protected void applyDependenciesOnFieldChange(String propertyId, Property.ValueChangeEvent event) {
		super.applyDependenciesOnFieldChange(propertyId, event);

		final CaseCriteria criteria = getValue();

		final ComboBox regionField = getField(CaseDataDto.REGION);
		final ComboBox districtField = getField(CaseDataDto.DISTRICT);
		final ComboBox communityField = getField(CaseDataDto.COMMUNITY);
		final ComboBox facilityTypeGroupField = getField(CaseCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(CaseCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(CaseDataDto.HEALTH_FACILITY);
		final ComboBox pointOfEntryField = getField(CaseDataDto.POINT_OF_ENTRY);
		final ComboBox caseOriginField = getField(CaseDataDto.CASE_ORIGIN);

		final UserDto user = currentUserDto();
		final DistrictReferenceDto currentDistrict =
			user.getDistrict() != null ? user.getDistrict() : (DistrictReferenceDto) districtField.getValue();
		final CaseOrigin currentCaseOrigin =
			caseOriginField != null ? (CaseOrigin) getField(CaseDataDto.CASE_ORIGIN).getValue() : CaseOrigin.POINT_OF_ENTRY;

		switch (propertyId) {
		case CaseDataDto.REGION: {
			final RegionReferenceDto region = user.getRegion() != null ? user.getRegion() : (RegionReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(region, criteria.getRegion())) {
				final ComboBox officerField = getField(CaseDataDto.SURVEILLANCE_OFFICER);
				officerField.removeAllItems();
				if (region != null) {
					enableFields(districtField);
					FieldHelper.updateItems(districtField, FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));

					disableFields(pointOfEntryField);
					clearAndDisableFields(communityField, facilityField, facilityTypeField, facilityTypeGroupField);

					addOfficers(officerField, region);
				} else {
					clearAndDisableFields(districtField, communityField, facilityField, facilityTypeField, facilityTypeGroupField);
					disableFields(pointOfEntryField);
					addOfficers(officerField, user.getRegion());
				}
			}

			break;
		}
		case CaseDataDto.DISTRICT: {
			final DistrictReferenceDto newDistrict = (DistrictReferenceDto) event.getProperty().getValue();

			if (!DataHelper.equal(newDistrict, criteria.getDistrict())) {
				final ComboBox officerField = getField(CaseDataDto.SURVEILLANCE_OFFICER);
				officerField.removeAllItems();
				if (newDistrict != null) {
					enableFields(communityField, facilityTypeGroupField);

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

					FieldHelper.updateItems(communityField, FacadeProvider.getCommunityFacade().getAllActiveByDistrict(newDistrict.getUuid()));

					if (pointOfEntryField != null && currentCaseOrigin == CaseOrigin.POINT_OF_ENTRY) {
						pointOfEntryField.setEnabled(true);
						FieldHelper.updateItems(
							pointOfEntryField,
							FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(newDistrict.getUuid(), true));
					}

					officerField.addItems(FacadeProvider.getUserFacade().getUserRefsByDistrict(newDistrict, false, UserRole.SURVEILLANCE_OFFICER));
				} else {
					clearAndDisableFields(communityField, pointOfEntryField, facilityField, facilityTypeField, facilityTypeGroupField);

					final RegionReferenceDto region = regionField != null ? (RegionReferenceDto) regionField.getValue() : null;
					addOfficers(officerField, region != null ? region : user.getRegion());
				}
			}

			break;
		}
		case CaseDataDto.COMMUNITY: {
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
		case CaseCriteria.FACILITY_TYPE_GROUP: {
			FacilityTypeGroup typeGroup = (FacilityTypeGroup) event.getProperty().getValue();
			if (!DataHelper.equal(typeGroup, criteria.getFacilityTypeGroup())) {
				if (typeGroup != null) {
					enableFields(CaseDataDto.FACILITY_TYPE);
					FieldHelper.updateEnumData(facilityTypeField, FacilityType.getAccommodationTypes(typeGroup));
					facilityField.setValue(null);
				} else {
					clearAndDisableFields(facilityTypeField, facilityField);
				}
			}

			break;
		}
		case CaseCriteria.FACILITY_TYPE: {
			FacilityType facilityType = (FacilityType) event.getProperty().getValue();
			if (!DataHelper.equal(facilityType, criteria.getFacilityType())) {
				if (facilityType == null) {
					clearAndDisableFields(facilityField);
				} else {
					enableFields(facilityField);
					facilityField.setValue(null);

					CommunityReferenceDto community = (CommunityReferenceDto) communityField.getValue();
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
		case CaseCriteria.BIRTHDATE_MM: {
			Integer birthMM = (Integer) event.getProperty().getValue();

			ComboBox birthDayDD = getField(CaseCriteria.BIRTHDATE_DD);
			birthDayDD.setEnabled(birthMM != null);
			FieldHelper.updateItems(
				birthDayDD,
				DateHelper.getDaysInMonth(
					(Integer) getField(CaseCriteria.BIRTHDATE_MM).getValue(),
					(Integer) getField(CaseCriteria.BIRTHDATE_YYYY).getValue()));

			break;
		}
		case CaseDataDto.CASE_ORIGIN: {
			if (pointOfEntryField != null) {
				CaseOrigin caseOrigin = (CaseOrigin) event.getProperty().getValue();
				if (caseOrigin == CaseOrigin.POINT_OF_ENTRY && currentDistrict != null) {
					pointOfEntryField.setEnabled(true);
					FieldHelper.updateItems(
						pointOfEntryField,
						FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(currentDistrict.getUuid(), true));
				} else {
					clearAndDisableFields(CaseDataDto.POINT_OF_ENTRY);
				}
			}
			break;
		}
		case CaseDataDto.DISEASE: {
			ComboBox field = getField(CaseDataDto.DISEASE_VARIANT);
			Disease disease = (Disease) event.getProperty().getValue();
			if (disease == null) {
				FieldHelper.updateItems(field, Collections.emptyList());
				FieldHelper.setEnabled(false, field);
			} else {
				List<DiseaseVariantReferenceDto> diseaseVariants = FacadeProvider.getDiseaseVariantFacade().getAllByDisease(disease);
				FieldHelper.updateItems(field, diseaseVariants);
				FieldHelper.setEnabled(!diseaseVariants.isEmpty(), field);
			}
		}
		}
	}

	private void addOfficers(ComboBox officerField, RegionReferenceDto region) {
		officerField.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region, UserRole.SURVEILLANCE_OFFICER));
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

		final UserDto user = currentUserDto();
		final JurisdictionLevel userJurisdictionLevel = UserRole.getJurisdictionLevel(UserProvider.getCurrent().getUserRoles());

		final ComboBox districtField = getField(CaseDataDto.DISTRICT);
		final ComboBox communityField = getField(CaseDataDto.COMMUNITY);
		final ComboBox facilityTypeGroupField = getField(CaseCriteria.FACILITY_TYPE_GROUP);
		final ComboBox facilityTypeField = getField(CaseCriteria.FACILITY_TYPE);
		final ComboBox facilityField = getField(CaseDataDto.HEALTH_FACILITY);
		final ComboBox pointOfEntryField = getField(CaseDataDto.POINT_OF_ENTRY);

		// Disable all fields
		clearAndDisableFields(districtField, communityField, facilityTypeGroupField, facilityTypeField, facilityField, pointOfEntryField);

		// Get initial field values according to user and criteria
		final RegionReferenceDto region = user.getRegion() == null ? criteria.getRegion() : user.getRegion();
		final DistrictReferenceDto district = user.getDistrict() == null ? criteria.getDistrict() : user.getDistrict();
		final CommunityReferenceDto community = user.getCommunity() == null ? criteria.getCommunity() : user.getCommunity();
		final FacilityTypeGroup facilityTypeGroup = criteria.getFacilityTypeGroup();
		final FacilityType facilityType = criteria.getFacilityType();

		// district
		if (region != null) {
			enableFields(districtField);
			districtField.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			// community
			if (district != null) {
				districtField.setValue(district);
				communityField.addItems(FacadeProvider.getCommunityFacade().getAllActiveByDistrict(district.getUuid()));
				enableFields(communityField);
				if (community != null) {
					communityField.setValue(community);
				}
			} else {
				clearAndDisableFields(communityField);
			}
		} else {
			clearAndDisableFields(districtField, communityField);
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

		if (pointOfEntryField != null && district != null) {
			pointOfEntryField.setEnabled(criteria.getCaseOrigin() != CaseOrigin.IN_COUNTRY);
			pointOfEntryField.addItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), true));
		}

		// Disable fields according to user & jurisdiction
		if (userJurisdictionLevel == JurisdictionLevel.DISTRICT) {
			clearAndDisableFields(districtField);
		} else if (userJurisdictionLevel == JurisdictionLevel.COMMUNITY) {
			clearAndDisableFields(districtField, communityField);
		} else if (userJurisdictionLevel == JurisdictionLevel.HEALTH_FACILITY) {
			clearAndDisableFields(districtField, communityField, facilityTypeGroupField, facilityTypeField, facilityField);
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
		ComboBox birthDateDD = getField(CaseCriteria.BIRTHDATE_DD);
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

		ComboBox diseaseField = getField(CaseDataDto.DISEASE);
		ComboBox diseaseVariantField = getField(CaseDataDto.DISEASE_VARIANT);
		Disease disease = (Disease) diseaseField.getValue();
		if (disease == null) {
			FieldHelper.updateItems(diseaseVariantField, Collections.emptyList());
			FieldHelper.setEnabled(false, diseaseVariantField);
		} else {
			List<DiseaseVariantReferenceDto> diseaseVariants = FacadeProvider.getDiseaseVariantFacade().getAllByDisease(disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			FieldHelper.setEnabled(!diseaseVariants.isEmpty(), diseaseVariantField);
		}
	}

	private HorizontalLayout buildWeekAndDateFilter(boolean isExternalShareEnabled) {

		EpiWeekAndDateFilterComponent<CriteriaDateType> weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(
			false,
			false,
			I18nProperties.getString(Strings.infoCaseDate),
			CriteriaDateTypeHelper.getTypes(NewCaseDateType.class, isExternalShareEnabled),
			I18nProperties.getString(Strings.promptNewCaseDateType),
			null,
			this);
		weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesEpiWeekFrom));
		weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesEpiWeekTo));
		weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptCasesDateFrom));
		weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptDateTo));

		addApplyHandler(e -> onApplyClick(weekAndDateFilter));

		HorizontalLayout dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();

		dateFilterRowLayout.addComponent(weekAndDateFilter);

		return dateFilterRowLayout;
	}

	private void onApplyClick(EpiWeekAndDateFilterComponent<CriteriaDateType> weekAndDateFilter) {
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
			CaseCriteria criteria = getValue();
			CriteriaDateType newCaseDateType = (CriteriaDateType) weekAndDateFilter.getDateTypeSelector().getValue();

			criteria.newCaseDateBetween(fromDate, toDate, newCaseDateType != null ? newCaseDateType : NewCaseDateType.MOST_RELEVANT);
			criteria.dateFilterOption(dateFilterOption);
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

	public void disableSearchAndReportingUser() {
		getField(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE).setEnabled(false);
		getField(CaseCriteria.EVENT_LIKE).setEnabled(false);
		getField(CaseCriteria.REPORTING_USER_LIKE).setEnabled(false);
	}

	public void enableSearchAndReportingUser() {
		getField(CaseCriteria.NAME_UUID_EPID_NUMBER_LIKE).setEnabled(true);
		getField(CaseCriteria.EVENT_LIKE).setEnabled(true);
		getField(CaseCriteria.REPORTING_USER_LIKE).setEnabled(true);
	}

	@Override
	public void setValue(CaseCriteria newCriteria) throws ReadOnlyException, Converter.ConversionException {

		super.setValue(newCriteria);
		ComboBox typeField = getField(CaseCriteria.FACILITY_TYPE);
		if (newCriteria.getFacilityType() != null && typeField != null) {
			typeField.setValue(newCriteria.getFacilityType());
			ComboBox facilityField = getField(CaseDataDto.HEALTH_FACILITY);
			if (newCriteria.getHealthFacility() != null && facilityField != null) {
				facilityField.setValue(newCriteria.getHealthFacility());
			}
		}
	}
}
