/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.caze.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.person.edit.PersonValidator;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class CaseNewFragment extends BaseEditFragment<FragmentCaseNewLayoutBinding, Case, Case> {

	public static final String TAG = CaseNewFragment.class.getSimpleName();

	private Case record;

	private List<Item> yearList;
	private List<Item> monthList;
	private List<Item> sexList;
	private List<Item> presentConditionList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
	private List<Item> plagueTypeList;
	private List<Item> dengueFeverTypeList;
	private List<Item> rabiesTypeList;
	private List<Item> initialResponsibleDistricts;
	private List<Item> initialResponsibleCommunities;
	private List<Item> initialRegions;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> initialFacilities;
	private List<Item> initialPointsOfEntry;
	private List<Item> facilityOrHomeList;
	private List<Item> facilityTypeGroupList;

	public static CaseNewFragment newInstance(Case activityRootData) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundle().get(), activityRootData);
	}

	static CaseNewFragment newInstanceFromContact(Case activityRootData, String contactUuid) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundleWithContact(contactUuid).get(), activityRootData);
	}

	static CaseNewFragment newInstanceFromEventParticipant(Case activityRootData, String eventParticipantUuid) {
		return newInstance(CaseNewFragment.class, CaseNewActivity.buildBundleWithEventParticipant(eventParticipantUuid).get(), activityRootData);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_new_case);
	}

	@Override
	public Case getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getDisease() != null && !diseases.contains(record.getDisease())) {
			diseaseList.add(DataUtils.toItem(record.getDisease()));
		}
		List<DiseaseVariant> diseaseVariants =
			DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, record.getDisease());
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
		dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
		rabiesTypeList = DataUtils.getEnumItems(RabiesType.class, true);

		yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
		monthList = DataUtils.getMonthItems(true);

		sexList = DataUtils.getEnumItems(Sex.class, true);
		presentConditionList = DataUtils.getEnumItems(PresentCondition.class, true);

		initialResponsibleDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialResponsibleCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getRegion());
		initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getDistrict());
		initialFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getResponsibleDistrict(), record.getResponsibleCommunity(), record.getFacilityType());
		initialPointsOfEntry = InfrastructureDaoHelper.loadPointsOfEntry(record.getResponsibleDistrict());

		facilityOrHomeList = DataUtils.toItems(TypeOfPlace.FOR_CASES, true);
		facilityTypeGroupList = DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups(), true);
	}

	@Override
	public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setCaseOriginClass(CaseOrigin.class);

		PersonValidator
			.initializeBirthDateValidation(contentBinding.personBirthdateYYYY, contentBinding.personBirthdateMM, contentBinding.personBirthdateDD);

		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);

		Facility initialHealthFacility = record.getHealthFacility();

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFields(
			contentBinding.caseDataResponsibleRegion,
			initialRegions,
			record.getResponsibleRegion(),
			contentBinding.caseDataResponsibleDistrict,
			initialResponsibleDistricts,
			record.getResponsibleDistrict(),
			contentBinding.caseDataResponsibleCommunity,
			initialResponsibleCommunities,
			record.getResponsibleCommunity());

		InfrastructureFieldsDependencyHandler.instance.initializeRegionFieldListeners(
			contentBinding.caseDataResponsibleRegion,
			contentBinding.caseDataResponsibleDistrict,
			record.getResponsibleDistrict(),
			contentBinding.caseDataResponsibleCommunity,
			record.getResponsibleCommunity(),
			contentBinding.caseDataFacilityType,
			contentBinding.caseDataHealthFacility,
			initialHealthFacility,
			null,
			null,
			() -> Boolean.TRUE.equals(contentBinding.caseDataDifferentPlaceOfStayJurisdiction.getValue()));

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			record,
			contentBinding.caseDataRegion,
			initialRegions,
			record.getRegion(),
			contentBinding.caseDataDistrict,
			initialDistricts,
			record.getDistrict(),
			contentBinding.caseDataCommunity,
			initialCommunities,
			record.getCommunity(),
			contentBinding.facilityOrHome,
			facilityOrHomeList,
			contentBinding.facilityTypeGroup,
			facilityTypeGroupList,
			contentBinding.caseDataFacilityType,
			null,
			contentBinding.caseDataHealthFacility,
			initialFacilities,
			record.getHealthFacility(),
			contentBinding.caseDataHealthFacilityDetails,
			contentBinding.caseDataPointOfEntry,
			initialPointsOfEntry,
			record.getPointOfEntry(),
			false,
			() -> Boolean.FALSE.equals(contentBinding.caseDataDifferentPlaceOfStayJurisdiction.getValue()));

		// trigger responsible jurisdiction change handlers removing place of stay region/district/community
		contentBinding.caseDataDifferentPlaceOfStayJurisdiction.addValueChangedListener(f -> {
			if (Boolean.FALSE.equals(f.getValue())) {
				InfrastructureFieldsDependencyHandler.instance.handleCommunityChange(
					contentBinding.caseDataResponsibleCommunity,
					contentBinding.caseDataResponsibleDistrict,
					contentBinding.caseDataHealthFacility,
					contentBinding.caseDataFacilityType,
					initialHealthFacility);
			}
		});

		contentBinding.caseDataDisease.initializeSpinner(diseaseList, DiseaseConfigurationCache.getInstance().getDefaultDisease());
		contentBinding.caseDataDiseaseVariant.initializeSpinner(diseaseVariantList);

		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
		contentBinding.caseDataHumanRabiesType.initializeSpinner(rabiesTypeList);
		contentBinding.caseDataReportDate.initializeDateField(getFragmentManager());
		contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

		contentBinding.personBirthdateDD.initializeSpinner(new ArrayList<>());
		contentBinding.personBirthdateMM.initializeSpinner(
			monthList,
			field -> DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) contentBinding.personBirthdateYYYY.getValue(),
				(Integer) field.getValue()));
		contentBinding.personBirthdateYYYY.initializeSpinner(
			yearList,
			field -> DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) field.getValue(),
				(Integer) contentBinding.personBirthdateMM.getValue()));

		int year = Calendar.getInstance().get(Calendar.YEAR);
		contentBinding.personBirthdateYYYY.setSelectionOnOpen(year - 35);

		contentBinding.personSex.initializeSpinner(sexList);

		contentBinding.personPresentCondition.initializeSpinner(presentConditionList);

		contentBinding.facilityOrHome.addValueChangedListener(e -> {
			if (e.getValue() == TypeOfPlace.FACILITY) {
				contentBinding.facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
				contentBinding.caseDataFacilityType.setValue(FacilityType.HOSPITAL);
				User user = ConfigProvider.getUser();
				if (!user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY)) {
					contentBinding.caseDataHealthFacility.setValue(null);
				}
			}
		});
		contentBinding.caseDataDisease.addValueChangedListener(e -> {
			contentBinding.rapidCaseEntryCheckBox.setVisibility(
				e.getValue() != null && ((CaseNewActivity) getActivity()).getLineListingDiseases().contains(e.getValue()) ? VISIBLE : GONE);
			updateDiseaseVariantsField(contentBinding);
			updatePresentConditionField(contentBinding);
		});
	}

	@Override
	public void onAfterLayoutBinding(final FragmentCaseNewLayoutBinding contentBinding) {
		InfrastructureDaoHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
		InfrastructureDaoHelper
			.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataExternalID.setVisibility(GONE);
			contentBinding.caseDataExternalToken.setVisibility(GONE);
		} else {
			contentBinding.caseDataEpidNumber.setVisibility(GONE);
		}

		contentBinding.caseDataResponsibleRegion.setEnabled(false);
		contentBinding.caseDataResponsibleRegion.setRequired(false);
		contentBinding.caseDataResponsibleDistrict.setEnabled(false);
		contentBinding.caseDataResponsibleDistrict.setRequired(false);

		User user = ConfigProvider.getUser();

		if (user.getPointOfEntry() == null) {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.FACILITY);
		}

		if (user.hasJurisdictionLevel(JurisdictionLevel.HEALTH_FACILITY)) {
			// Hospital Informants are not allowed to create cases in another health facility
			contentBinding.caseDataCommunity.setEnabled(false);
			contentBinding.caseDataCommunity.setRequired(false);
			contentBinding.caseDataHealthFacility.setEnabled(false);
			contentBinding.caseDataHealthFacility.setRequired(false);
			contentBinding.facilityOrHome.setEnabled(false);
			contentBinding.facilityTypeGroup.setEnabled(false);
			contentBinding.caseDataFacilityType.setEnabled(false);
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setEnabled(false);
			contentBinding.caseDataDifferentPlaceOfStayJurisdiction.setVisibility(GONE);
		}

		if (user.getPointOfEntry() != null) {
			contentBinding.caseDataPointOfEntry.setEnabled(false);
			contentBinding.caseDataPointOfEntry.setRequired(false);
		}

		if (user.hasJurisdictionLevel(JurisdictionLevel.COMMUNITY)) {
			// Community Informants are not allowed to create cases in another community
			contentBinding.caseDataCommunity.setEnabled(false);
			contentBinding.caseDataCommunity.setRequired(false);
		}

		// Disable personal details and disease fields when case is created from contact
		// or event person
		Bundler bundler = new Bundler(getArguments());
		if (bundler.getContactUuid() != null || bundler.getEventParticipantUuid() != null) {
			contentBinding.caseDataFirstName.setEnabled(false);
			contentBinding.caseDataLastName.setEnabled(false);
			contentBinding.personSex.setEnabled(false);
			contentBinding.personBirthdateYYYY.setEnabled(false);
			contentBinding.personBirthdateMM.setEnabled(false);
			contentBinding.personBirthdateDD.setEnabled(false);
			contentBinding.caseDataDisease.setEnabled(false);
			contentBinding.caseDataDiseaseDetails.setEnabled(false);
			contentBinding.caseDataPlagueType.setEnabled(false);
			contentBinding.caseDataDengueFeverType.setEnabled(false);
			contentBinding.caseDataHumanRabiesType.setEnabled(false);
		}

		// Set up port health visibilities
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.caseDataDisease.setVisibility(GONE);
			contentBinding.facilityOrHome.setVisibility(GONE);
			contentBinding.caseDataCommunity.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.facilityTypeGroup.setRequired(false);
			contentBinding.caseDataFacilityType.setRequired(false);
			contentBinding.caseDataHealthFacility.setRequired(false);
			contentBinding.caseDataHealthFacilityDetails.setRequired(false);
		} else if (DatabaseHelper.getPointOfEntryDao().hasActiveEntriesInDistrict()) {
			if (record.getCaseOrigin() == CaseOrigin.IN_COUNTRY) {
				contentBinding.caseDataPointOfEntry.setRequired(false);
				contentBinding.caseDataPointOfEntry.setVisibility(GONE);
			} else {
				contentBinding.caseDataHealthFacility.setRequired(false);
			}
			contentBinding.caseDataCaseOrigin.addValueChangedListener(e -> {
				if (e.getValue() == CaseOrigin.IN_COUNTRY) {
					contentBinding.caseDataPointOfEntry.setVisibility(GONE);
					contentBinding.caseDataPointOfEntry.setRequired(false);
					contentBinding.caseDataPointOfEntry.setValue(null);
					contentBinding.caseDataHealthFacility.setRequired(true);
				} else {
					contentBinding.caseDataPointOfEntry.setVisibility(VISIBLE);
					contentBinding.caseDataHealthFacility.setRequired(false);
					contentBinding.caseDataPointOfEntry.setRequired(true);
				}
			});
		} else {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.caseDataPointOfEntry.setVisibility(GONE);
		}
	}

	private void updateDiseaseVariantsField(FragmentCaseNewLayoutBinding contentBinding) {
		List<DiseaseVariant> diseaseVariants =
			DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, record.getDisease());
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.caseDataDiseaseVariant.setSpinnerData(diseaseVariantList);
		contentBinding.caseDataDiseaseVariant.setValue(null);
		contentBinding.caseDataDiseaseVariant.setVisibility(diseaseVariants.isEmpty() ? GONE : VISIBLE);
	}

	private void updatePresentConditionField(FragmentCaseNewLayoutBinding contentBinding) {
		Disease diseaseValue = (Disease) contentBinding.caseDataDisease.getValue();
		PresentCondition presentConditionValue = (PresentCondition) contentBinding.personPresentCondition.getValue();
		List<Item> items;
		if (diseaseValue == null) {
			items = DataUtils.getEnumItems(PresentCondition.class, true);
		} else {
			items = DataUtils.getEnumItems(PresentCondition.class, true, FieldVisibilityCheckers.withDisease(diseaseValue));
		}
		if (presentConditionValue != null) {
			Item currentValueItem = new Item(presentConditionValue.toString(), presentConditionValue);
			if (!items.contains(currentValueItem)) {
				items.add(currentValueItem);
			}
		}
		contentBinding.personPresentCondition.initializeSpinner(items);
		if (presentConditionValue != null) {
			contentBinding.personPresentCondition.setValue(presentConditionValue);
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_new_layout;
	}

	void updateForRapidCaseEntry(Case lastCase) {
		setLiveValidationDisabled(true);

		record = getActivityRootData();
		record.setResponsibleRegion(lastCase.getResponsibleRegion());
		record.setResponsibleDistrict(lastCase.getResponsibleDistrict());
		record.setResponsibleCommunity(lastCase.getResponsibleCommunity());
		record.setRegion(lastCase.getRegion());
		record.setDistrict(lastCase.getDistrict());
		record.setCommunity(lastCase.getCommunity());
		record.setFacilityType(lastCase.getFacilityType());
		record.setHealthFacility(lastCase.getHealthFacility());
		record.setHealthFacilityDetails(lastCase.getHealthFacilityDetails());
		record.setPointOfEntry(lastCase.getPointOfEntry());
		record.setPointOfEntryDetails(lastCase.getPointOfEntryDetails());
		record.setReportDate(lastCase.getReportDate());
		record.setDisease(lastCase.getDisease());
		record.setDiseaseVariant(lastCase.getDiseaseVariant());
		record.setDiseaseDetails(lastCase.getDiseaseDetails());
		record.setCaseOrigin(lastCase.getCaseOrigin());

		getContentBinding().setData(record);
	}
}
