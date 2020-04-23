/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.caze.edit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseSurveillanceType;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentCaseNewLayoutBinding;
import de.symeda.sormas.app.util.Bundler;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class CaseNewFragment extends BaseEditFragment<FragmentCaseNewLayoutBinding, Case, Case> {

    public static final String TAG = CaseNewFragment.class.getSimpleName();

    private Case record;

    private List<Item> yearList;
    private List<Item> monthList;
    private List<Item> sexList;
    private List<Item> presentConditionList;
    private List<Item> diseaseList;
    private List<Item> plagueTypeList;
    private List<Item> dengueFeverTypeList;
    private List<Item> rabiesTypeList;
    private List<Item> initialRegions;
    private List<Item> initialDistricts;
    private List<Item> initialCommunities;
    private List<Item> initialFacilities;
    private List<Item> initialPointsOfEntry;

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
        plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
        dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
        rabiesTypeList = DataUtils.getEnumItems(RabiesType.class, true);

        yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
        monthList = DataUtils.getMonthItems(true);

        sexList = DataUtils.getEnumItems(Sex.class, true);
        presentConditionList = DataUtils.getEnumItems(PresentCondition.class, true);

        initialRegions = InfrastructureHelper.loadRegions();
        initialDistricts = InfrastructureHelper.loadDistricts(record.getRegion());
        initialCommunities = InfrastructureHelper.loadCommunities(record.getDistrict());
        initialFacilities = InfrastructureHelper.loadFacilities(record.getDistrict(), record.getCommunity());
        initialPointsOfEntry = InfrastructureHelper.loadPointsOfEntry(record.getDistrict());
    }

    @Override
    public void onLayoutBinding(FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.setData(record);
        contentBinding.setCaseOriginClass(CaseOrigin.class);
        contentBinding.setCaseSurveillanceTypeClass(CaseSurveillanceType.class);

        contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
        contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);

        InfrastructureHelper.initializeFacilityFields(
                contentBinding.caseDataRegion, initialRegions, record.getRegion(),
                contentBinding.caseDataDistrict, initialDistricts, record.getDistrict(),
                contentBinding.caseDataCommunity, initialCommunities, record.getCommunity(),
                contentBinding.caseDataHealthFacility, initialFacilities, record.getHealthFacility(),
                contentBinding.caseDataPointOfEntry, initialPointsOfEntry, record.getPointOfEntry());

        contentBinding.caseDataDisease.initializeSpinner(diseaseList, DiseaseConfigurationCache.getInstance().getDefaultDisease());
        contentBinding.caseDataDisease.addValueChangedListener(e -> {
            contentBinding.rapidCaseEntryCheckBox.setVisibility(
                    e.getValue() != null && ((CaseNewActivity) getActivity()).getLineListingDiseases().contains(e.getValue()) ? VISIBLE : GONE);
        });

        contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
        contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
        contentBinding.caseDataHumanRabiesType.initializeSpinner(rabiesTypeList);
        contentBinding.caseDataReportDate.initializeDateField(getFragmentManager());
        contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

        contentBinding.personBirthdateDD.initializeSpinner(new ArrayList<>());
        contentBinding.personBirthdateMM.initializeSpinner(monthList, field -> {
            DataUtils.updateListOfDays(contentBinding.personBirthdateDD, (Integer) contentBinding.personBirthdateYYYY.getValue(), (Integer) field.getValue());
        });
        contentBinding.personBirthdateYYYY.initializeSpinner(yearList, field -> {
            DataUtils.updateListOfDays(contentBinding.personBirthdateDD, (Integer) field.getValue(), (Integer) contentBinding.personBirthdateMM.getValue());
        });

        int year = Calendar.getInstance().get(Calendar.YEAR);
        contentBinding.personBirthdateYYYY.setSelectionOnOpen(year-35);

        contentBinding.personSex.initializeSpinner(sexList);

        contentBinding.personPresentCondition.initializeSpinner(presentConditionList);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentCaseNewLayoutBinding contentBinding) {
        contentBinding.caseDataSurveillanceType.setValue(CaseSurveillanceType.ROUTINE);

        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
        InfrastructureHelper.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

        contentBinding.caseDataRegion.setEnabled(false);
        contentBinding.caseDataRegion.setRequired(false);
        contentBinding.caseDataDistrict.setEnabled(false);
        contentBinding.caseDataDistrict.setRequired(false);

        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.HOSPITAL_INFORMANT) && user.getHealthFacility() != null) {
            // Hospital Informants are not allowed to create cases in another health facility
            contentBinding.caseDataCommunity.setEnabled(false);
            contentBinding.caseDataCommunity.setRequired(false);
            contentBinding.caseDataHealthFacility.setEnabled(false);
            contentBinding.caseDataHealthFacility.setRequired(false);
        }

        if (user.hasUserRole(UserRole.POE_INFORMANT) && user.getPointOfEntry() != null) {
            contentBinding.caseDataPointOfEntry.setEnabled(false);
            contentBinding.caseDataPointOfEntry.setRequired(false);
        }

        if (user.hasUserRole(UserRole.COMMUNITY_INFORMANT) && user.getCommunity() != null) {
            // Community Informants are not allowed to create cases in another community
            contentBinding.caseDataCommunity.setEnabled(false);
            contentBinding.caseDataCommunity.setRequired(false);
        }

        // Disable first and last name and disease fields when case is created from contact
        // or event person
        Bundler bundler = new Bundler(getArguments());
        if (bundler.getContactUuid() != null || bundler.getEventParticipantUuid() != null) {
            contentBinding.caseDataFirstName.setEnabled(false);
            contentBinding.caseDataLastName.setEnabled(false);
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
            contentBinding.healthFacilityFieldsLayout.setVisibility(GONE);
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

    @Override
    public int getEditLayout() {
        return R.layout.fragment_case_new_layout;
    }

    void updateForRapidCaseEntry(Case lastCase) {
        setLiveValidationDisabled(true);

        record = getActivityRootData();
        record.setRegion(lastCase.getRegion());
        record.setDistrict(lastCase.getDistrict());
        record.setCommunity(lastCase.getCommunity());
        record.setHealthFacility(lastCase.getHealthFacility());
        record.setHealthFacilityDetails(lastCase.getHealthFacilityDetails());
        record.setPointOfEntry(lastCase.getPointOfEntry());
        record.setPointOfEntryDetails(lastCase.getPointOfEntryDetails());
        record.setReportDate(lastCase.getReportDate());
        record.setDisease(lastCase.getDisease());
        record.setDiseaseDetails(lastCase.getDiseaseDetails());
        record.setCaseOrigin(lastCase.getCaseOrigin());
        record.setSurveillanceType(lastCase.getSurveillanceType());

        getContentBinding().setData(record);
    }

}
