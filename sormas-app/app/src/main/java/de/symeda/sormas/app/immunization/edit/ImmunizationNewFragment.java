/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.FragmentImmunizationNewLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class ImmunizationNewFragment extends BaseEditFragment<FragmentImmunizationNewLayoutBinding, Immunization, Immunization> {

	private Immunization record;

	private List<Item> yearList;
	private List<Item> monthList;
	private List<Item> sexList;

	private List<Item> diseaseList;
	private List<Item> immunizationStatusList;
	private List<Item> meansOfImmunizationList;
	private List<Item> immunizationManagementStatusList;

	private List<Item> initialResponsibleRegions;
	private List<Item> initialResponsibleDistricts;
	private List<Item> initialResponsibleCommunities;

	private List<Item> initialFacilities;
	private List<Item> facilityTypeList;
	private List<Item> facilityTypeGroupList;

	public static ImmunizationNewFragment newInstance(Immunization activityRootData) {
		return newInstance(ImmunizationNewFragment.class, ImmunizationNewActivity.buildBundle().get(), activityRootData);
	}

	public static ImmunizationNewFragment newInstanceFromCase(Immunization activityRootData, String caseUuid) {
		return newInstance(ImmunizationNewFragment.class, ImmunizationNewActivity.buildBundleWithCase(caseUuid).get(), activityRootData);
	}

	public static ImmunizationNewFragment newInstanceFromContact(Immunization activityRootData, String contactUuid) {
		return newInstance(ImmunizationNewFragment.class, ImmunizationNewActivity.buildBundleWithContact(contactUuid).get(), activityRootData);
	}

	public static ImmunizationNewFragment newInstanceFromEventParticipant(Immunization activityRootData, String eventParticipantUuid) {
		return newInstance(ImmunizationNewFragment.class, ImmunizationNewActivity.buildBundleWithEvent(eventParticipantUuid).get(), activityRootData);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_new_immunization);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_immunization_new_layout;
	}

	@Override
	public Immunization getPrimaryData() {
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

		immunizationStatusList = DataUtils.getEnumItems(ImmunizationStatus.class, true);
		meansOfImmunizationList = DataUtils.getEnumItems(MeansOfImmunization.class, true);
		immunizationManagementStatusList = DataUtils.getEnumItems(ImmunizationManagementStatus.class, false);

		initialResponsibleRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialResponsibleDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialResponsibleCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());

		yearList = DataUtils.toItems(DateHelper.getYearsToNow(), true);
		monthList = DataUtils.getMonthItems(true);

		sexList = DataUtils.getEnumItems(Sex.class, true);

		initialFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getResponsibleDistrict(), record.getResponsibleCommunity(), record.getFacilityType());

		facilityTypeGroupList = DataUtils.toItems(Arrays.asList(FacilityTypeGroup.values()), true);
		facilityTypeList =
			record.getFacilityType() != null ? DataUtils.toItems(FacilityType.getTypes(record.getFacilityType().getFacilityTypeGroup())) : null;
	}

	@Override
	protected void onLayoutBinding(FragmentImmunizationNewLayoutBinding contentBinding) {
		contentBinding.setData(record);

		contentBinding.setYesNoUnknownClass(YesNoUnknown.class);

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			record,
			contentBinding.immunizationResponsibleRegion,
			initialResponsibleRegions,
			record.getResponsibleRegion(),
			contentBinding.immunizationResponsibleDistrict,
			initialResponsibleDistricts,
			record.getResponsibleDistrict(),
			contentBinding.immunizationResponsibleCommunity,
			initialResponsibleCommunities,
			record.getResponsibleCommunity(),
			null,
			null,
			contentBinding.facilityTypeGroup,
			facilityTypeGroupList,
			contentBinding.immunizationFacilityType,
			facilityTypeList,
			contentBinding.immunizationHealthFacility,
			initialFacilities,
			record.getHealthFacility(),
			contentBinding.immunizationHealthFacilityDetails,
			true);

		// Initialize ControlSpinnerFields
		contentBinding.immunizationDisease.initializeSpinner(diseaseList);
		contentBinding.immunizationImmunizationStatus.initializeSpinner(immunizationStatusList);
		contentBinding.immunizationImmunizationStatus.setEnabled(false);
		contentBinding.immunizationImmunizationManagementStatus.initializeSpinner(immunizationManagementStatusList);
		contentBinding.immunizationMeansOfImmunization.initializeSpinner(meansOfImmunizationList);

		contentBinding.personBirthdateDD.initializeSpinner(new ArrayList<>());
		contentBinding.personBirthdateMM.initializeSpinner(monthList, field -> {
			DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) contentBinding.personBirthdateYYYY.getValue(),
				(Integer) field.getValue());
		});
		contentBinding.personBirthdateYYYY.initializeSpinner(yearList, field -> {
			DataUtils.updateListOfDays(
				contentBinding.personBirthdateDD,
				(Integer) field.getValue(),
				(Integer) contentBinding.personBirthdateMM.getValue());
		});
		int year = Calendar.getInstance().get(Calendar.YEAR);
		contentBinding.personBirthdateYYYY.setSelectionOnOpen(year - 35);
		contentBinding.personSex.initializeSpinner(sexList);

		// Initialize ControlDateFields
		contentBinding.immunizationReportDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationStartDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationEndDate.initializeDateField(getFragmentManager());
		ValidationHelper.initDateIntervalValidator(contentBinding.immunizationStartDate, contentBinding.immunizationEndDate);
		contentBinding.immunizationValidFrom.initializeDateField(getFragmentManager());
		contentBinding.immunizationValidUntil.initializeDateField(getFragmentManager());
		ValidationHelper.initDateIntervalValidator(contentBinding.immunizationValidFrom, contentBinding.immunizationValidUntil);

		ValidationHelper
			.initIntegerValidator(contentBinding.immunizationNumberOfDoses, I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10);
	}

	@Override
	protected void onAfterLayoutBinding(FragmentImmunizationNewLayoutBinding contentBinding) {

		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.immunizationHealthFacility,
			contentBinding.immunizationHealthFacilityDetails);

		contentBinding.immunizationMeansOfImmunization.addValueChangedListener(e -> {
			if (e.getValue() == MeansOfImmunization.OTHER || e.getValue() == MeansOfImmunization.RECOVERY) {
				contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.COMPLETED);
				contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);
			}
			if (e.getValue() == MeansOfImmunization.VACCINATION || e.getValue() == MeansOfImmunization.VACCINATION_RECOVERY) {
				contentBinding.immunizationNumberOfDoses.setVisibility(View.VISIBLE);
				contentBinding.immunizationNumberOfDoses.setEnabled(true);
			} else {
				contentBinding.immunizationNumberOfDoses.setVisibility(View.GONE);
			}
		});

		contentBinding.overwriteImmunizationManagementStatusCheckBox.addValueChangedListener(e -> {
			if (Boolean.TRUE.equals(e.getValue())) {
				contentBinding.immunizationImmunizationManagementStatus.setEnabled(true);
			} else {
				contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);
			}
		});

		contentBinding.immunizationImmunizationManagementStatus.addValueChangedListener(e -> {
			if (e.getValue() == ImmunizationManagementStatus.SCHEDULED || e.getValue() == ImmunizationManagementStatus.ONGOING) {
				contentBinding.immunizationImmunizationStatus.setValue(ImmunizationStatus.PENDING);
			}
			if (e.getValue() == ImmunizationManagementStatus.COMPLETED) {
				contentBinding.immunizationImmunizationStatus.setValue(ImmunizationStatus.ACQUIRED);
			}
			if (e.getValue() == ImmunizationManagementStatus.CANCELED) {
				contentBinding.immunizationImmunizationStatus.setValue(ImmunizationStatus.NOT_ACQUIRED);
			}
		});

		contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.SCHEDULED);
		contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);
		contentBinding.immunizationNumberOfDoses.setVisibility(View.GONE);

		if (!(record.getHealthFacility() == null
			|| (record.getHealthFacility() != null && FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())))) {
			final FacilityType facilityType = record.getFacilityType();
			if (facilityType != null) {
				contentBinding.facilityTypeGroup.setValue(facilityType.getFacilityTypeGroup());
			}
		}
	}
}
