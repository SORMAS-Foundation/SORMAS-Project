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

import java.util.Date;
import java.util.List;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentImmunizationEditLayoutBinding;
import de.symeda.sormas.app.immunization.read.ImmunizationSearchCaseDialog;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;

public class ImmunizationEditFragment extends BaseEditFragment<FragmentImmunizationEditLayoutBinding, Immunization, Immunization> {

	private Immunization record;

	private List<Item> diseaseList;
	private List<Item> immunizationStatusList;
	private List<Item> meansOfImmunizationList;
	private List<Item> immunizationManagementStatusList;

	private List<Item> initialRegions;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> initialFacilities;
	private List<Item> facilityOrHomeList;
	private List<Item> facilityTypeGroupList;
	private List<Item> countries;

	public static ImmunizationEditFragment newInstance(Immunization activityRootData) {
		ImmunizationEditFragment immunizationEditFragment = newInstanceWithFieldCheckers(
			ImmunizationEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		return immunizationEditFragment;
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_immunization_edit_layout;
	}

	@Override
	public Immunization getPrimaryData() {
		return record;
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_immunization_information);
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

		countries = InfrastructureDaoHelper.loadCountries();
		initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		initialDistricts = InfrastructureDaoHelper.loadDistricts(record.getResponsibleRegion());
		initialCommunities = InfrastructureDaoHelper.loadCommunities(record.getResponsibleDistrict());
		initialFacilities =
			InfrastructureDaoHelper.loadFacilities(record.getResponsibleDistrict(), record.getResponsibleCommunity(), record.getFacilityType());

		facilityOrHomeList = DataUtils.toItems(TypeOfPlace.FOR_CASES, true);
		facilityTypeGroupList = DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups(), true);
	}

	@Override
	protected void onLayoutBinding(FragmentImmunizationEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
		setUpControlListeners(contentBinding);

		contentBinding.immunizationCountry.initializeSpinner(countries);

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			record,
			contentBinding.immunizationResponsibleRegion,
			initialRegions,
			record.getResponsibleRegion(),
			contentBinding.immunizationResponsibleDistrict,
			initialDistricts,
			record.getResponsibleDistrict(),
			contentBinding.immunizationResponsibleCommunity,
			initialCommunities,
			record.getResponsibleCommunity(),
			null,
			facilityOrHomeList,
			contentBinding.facilityTypeGroup,
			facilityTypeGroupList,
			contentBinding.immunizationFacilityType,
			null,
			contentBinding.immunizationHealthFacility,
			initialFacilities,
			record.getHealthFacility(),
			contentBinding.immunizationHealthFacilityDetails,
			null,
			null,
			null,
			false,
			() -> false);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentImmunizationEditLayoutBinding contentBinding) {

		InfrastructureDaoHelper.initializeHealthFacilityDetailsFieldVisibility(
			contentBinding.immunizationHealthFacility,
			contentBinding.immunizationHealthFacilityDetails);

		// Initialize ControlSpinnerFields
		contentBinding.immunizationDisease.initializeSpinner(diseaseList);
		contentBinding.immunizationImmunizationStatus.initializeSpinner(immunizationStatusList);
		contentBinding.immunizationImmunizationStatus.setEnabled(false);
		contentBinding.immunizationImmunizationManagementStatus.initializeSpinner(immunizationManagementStatusList);
		contentBinding.immunizationMeansOfImmunization.initializeSpinner(meansOfImmunizationList);

		// Initialize ControlDateFields
		contentBinding.immunizationReportDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationRecoveryDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationStartDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationEndDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationValidFrom.initializeDateField(getFragmentManager());
		contentBinding.immunizationValidUntil.initializeDateField(getFragmentManager());
		contentBinding.immunizationLastInfectionDate.initializeDateField(getFragmentManager());

		contentBinding.immunizationMeansOfImmunization.addValueChangedListener(e -> {
			if (e.getValue() == MeansOfImmunization.OTHER || e.getValue() == MeansOfImmunization.RECOVERY) {
				contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.COMPLETED);
				contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);
			}
			if (e.getValue() == MeansOfImmunization.VACCINATION || e.getValue() == MeansOfImmunization.VACCINATION_RECOVERY) {
				contentBinding.immunizationVaccinationLayout.setVisibility(View.VISIBLE);
				contentBinding.immunizationNumberOfDoses.setEnabled(true);
			} else {
				contentBinding.immunizationVaccinationLayout.setVisibility(View.GONE);
			}
			if (e.getValue() == MeansOfImmunization.RECOVERY || e.getValue() == MeansOfImmunization.VACCINATION_RECOVERY) {
				contentBinding.immunizationRecoveryLayout.setVisibility(View.VISIBLE);
				contentBinding.immunizationRecoveryDate.setEnabled(true);
				contentBinding.immunizationPositiveTestResultDate.setEnabled(true);

				if (record.getRelatedCase() != null) {
					contentBinding.linkCase.setVisibility(View.GONE);
				} else {
					contentBinding.openLinkedCase.setVisibility(View.GONE);
				}

			} else {
				contentBinding.immunizationRecoveryLayout.setVisibility(View.GONE);
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

		contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);

		if (!(record.getHealthFacility() == null
			|| (record.getHealthFacility() != null && FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())))) {
			final FacilityType facilityType = record.getFacilityType();
			if (facilityType != null) {
				contentBinding.facilityTypeGroup.setValue(facilityType.getFacilityTypeGroup());
			}
		}
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}

	private void setUpControlListeners(FragmentImmunizationEditLayoutBinding contentBinding) {

		contentBinding.linkCase.setOnClickListener((v) -> {
			linkRecoveryImmunizationToCaseSearchCaseIncluded(record);
		});

		contentBinding.openLinkedCase.setOnClickListener(v -> CaseReadActivity.startActivity(getActivity(), record.getRelatedCase().getUuid(), true));
	}

	private void linkRecoveryImmunizationToCaseSearchCaseIncluded(Immunization immunization){

		ImmunizationSearchCaseDialog.searchCaseToLinkImmunization(ImmunizationEditActivity.getActiveActivity(), caseSearchField -> {
					CaseCriteria criteria = new CaseCriteria();
					criteria.setPerson(immunization.getPerson());
					criteria.setDisease(immunization.getDisease());
					criteria.setOutcome(CaseOutcome.RECOVERED);

					criteria.setTextFilter(caseSearchField);

					List<Case> cases = DatabaseHelper.getCaseDao().queryByCriteria(criteria, 0, 1);

					if (cases != null && !cases.isEmpty() && cases.get(0)!= null){
					Case foundCase = cases.get(0);
						immunization.setRelatedCase(foundCase);
						List<Sample> samples = DatabaseHelper.getSampleDao().queryByCase(foundCase);
						PathogenTest relevantPathogenTest = null;
						for (Sample sample : samples) {
							List<PathogenTest> pathogenTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);

							for (PathogenTest pathogenTest : pathogenTests) {
								if (pathogenTest.getTestedDisease().equals(foundCase.getDisease()) && PathogenTestResultType.POSITIVE.equals(pathogenTest.getTestResult())) {
									if (relevantPathogenTest == null) {
										relevantPathogenTest = pathogenTest;
									} else if (relevantPathogenTest.getTestDateTime().before(pathogenTest.getTestDateTime())) {
										relevantPathogenTest = pathogenTest;
									}
								}
							}
						}
						if (relevantPathogenTest != null) {
							Date latestPositiveTestResultDate = relevantPathogenTest.getTestDateTime();

							if (latestPositiveTestResultDate != null) {
								immunization.setPositiveTestResultDate(latestPositiveTestResultDate);
							}

							Date onsetDate = foundCase.getSymptoms().getOnsetDate();
							if (onsetDate != null) {
								immunization.setLastInfectionDate(onsetDate);
							}

							Date outcomeDate = foundCase.getOutcomeDate();
							if (outcomeDate != null) {
								immunization.setRecoveryDate(outcomeDate);
							}
						}
						final ImmunizationEditActivity activity = (ImmunizationEditActivity) ImmunizationEditFragment.this.getActivity();
						activity.saveData();
					}else {
						NotificationHelper.showNotification(ImmunizationEditActivity.getActiveActivity(), NotificationType.WARNING, getString(R.string.message_no_Case_found_to_link_immunization));
					}
				}
		);
	}
}
