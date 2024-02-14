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
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
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
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentImmunizationEditLayoutBinding;
import de.symeda.sormas.app.immunization.read.ImmunizationSearchCaseDialog;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
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
	private List<Item> facilityTypeList;
	private List<Item> facilityTypeGroupList;
	private List<Item> countries;
	private Consumer<MeansOfImmunization> meansOfImmunizationChange;
	private MeansOfImmunization currentMeansOfImmunization;

	public static ImmunizationEditFragment newInstance(Immunization activityRootData, Consumer<MeansOfImmunization> meansOfImmunizationChange) {
		ImmunizationEditFragment immunizationEditFragment = newInstanceWithFieldCheckers(
			ImmunizationEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		immunizationEditFragment.setMeansOfImmunizationChange(meansOfImmunizationChange);

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

		currentMeansOfImmunization = record.getMeansOfImmunization();

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

		facilityTypeGroupList = DataUtils.toItems(Arrays.asList(FacilityTypeGroup.values()), true);
		facilityTypeList =
			record.getFacilityType() != null ? DataUtils.toItems(FacilityType.getTypes(record.getFacilityType().getFacilityTypeGroup())) : null;

		DatabaseHelper.getImmunizationDao().initVaccinations(record);
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
	}

	@Override
	public void onAfterLayoutBinding(final FragmentImmunizationEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(ImmunizationDto.class, contentBinding.mainContent);

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
		contentBinding.immunizationPositiveTestResultDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationStartDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationEndDate.initializeDateField(getFragmentManager());
		ValidationHelper.initDateIntervalValidator(contentBinding.immunizationStartDate, contentBinding.immunizationEndDate);
		contentBinding.immunizationValidFrom.initializeDateField(getFragmentManager());
		contentBinding.immunizationValidUntil.initializeDateField(getFragmentManager());
		ValidationHelper.initDateIntervalValidator(contentBinding.immunizationValidFrom, contentBinding.immunizationValidUntil);
		contentBinding.immunizationLastInfectionDate.initializeDateField(getFragmentManager());

		updateMeansOfImmunizationFields(contentBinding);

		contentBinding.immunizationMeansOfImmunization.addValueChangedListener(e -> {
			MeansOfImmunization meansOfImmunization = (MeansOfImmunization) e.getValue();

			if (currentMeansOfImmunization != meansOfImmunization) {
				if ((meansOfImmunization == MeansOfImmunization.OTHER || meansOfImmunization == MeansOfImmunization.RECOVERY)
					&& CollectionUtils.isNotEmpty(record.getVaccinations())) {
					removeVaccinationsConfirmation(contentBinding);
				} else {
					updateMeansOfImmunizationFields(contentBinding);
					updateImmunizationStatus(contentBinding);
				}
			}
		});

		contentBinding.overwriteImmunizationManagementStatusCheckBox
			.addValueChangedListener(e -> contentBinding.immunizationImmunizationManagementStatus.setEnabled(Boolean.TRUE.equals(e.getValue())));

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

		if (record.getMeansOfImmunization() == MeansOfImmunization.RECOVERY
			|| record.getMeansOfImmunization() == MeansOfImmunization.VACCINATION_RECOVERY) {
			if (record.getRelatedCase() != null) {
				contentBinding.linkCase.setVisibility(View.GONE);
			} else {
				contentBinding.openLinkedCase.setVisibility(View.GONE);
			}
		} else {
			contentBinding.immunizationRecoveryLayout.setVisibility(View.GONE);
			contentBinding.linkCase.setVisibility(View.GONE);
			contentBinding.openLinkedCase.setVisibility(View.GONE);
		}

		if(!isFieldAccessible(ImmunizationDto.class, contentBinding.immunizationHealthFacility)){
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityTypeGroup);
		}
		contentBinding.immunizationReportingUser.setPseudonymized(record.isPseudonymized());
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}

	public void setMeansOfImmunizationChange(Consumer<MeansOfImmunization> meansOfImmunizationChange) {
		this.meansOfImmunizationChange = meansOfImmunizationChange;
	}

	private void setUpControlListeners(FragmentImmunizationEditLayoutBinding contentBinding) {

		contentBinding.linkCase.setOnClickListener(v -> linkRecoveryImmunizationToCaseSearchCaseIncluded(record));

		contentBinding.openLinkedCase.setOnClickListener(v -> CaseReadActivity.startActivity(getActivity(), record.getRelatedCase().getUuid(), true));
	}

	private void updateMeansOfImmunizationFields(FragmentImmunizationEditLayoutBinding contentBinding) {
		MeansOfImmunization meansOfImmunizationValue = (MeansOfImmunization) contentBinding.immunizationMeansOfImmunization.getValue();

		boolean isVaccination = MeansOfImmunization.isVaccination(meansOfImmunizationValue);
		contentBinding.immunizationVaccinationLayout.setVisibility(isVaccination ? View.VISIBLE : View.GONE);
		contentBinding.immunizationNumberOfDoses.setEnabled(isVaccination);
		if (!isVaccination) {
			contentBinding.immunizationNumberOfDoses.setValue(null);
		}

		boolean isRecovery = MeansOfImmunization.isRecovery(meansOfImmunizationValue);
		contentBinding.immunizationRecoveryLayout.setVisibility(isRecovery ? View.VISIBLE : View.GONE);
		contentBinding.immunizationRecoveryDate.setEnabled(isRecovery);
		contentBinding.immunizationPositiveTestResultDate.setEnabled(isRecovery);
		if (isRecovery) {
			boolean isCaseLinked = record.getRelatedCase() != null;
			contentBinding.openLinkedCase.setVisibility(isCaseLinked ? View.VISIBLE : View.GONE);
			contentBinding.linkCase.setVisibility(isCaseLinked ? View.GONE : View.VISIBLE);
		} else {
			contentBinding.immunizationRecoveryDate.setValue(null);
			contentBinding.immunizationPositiveTestResultDate.setValue(null);
		}

		meansOfImmunizationChange.accept(meansOfImmunizationValue);
		currentMeansOfImmunization = meansOfImmunizationValue;
	}

	private void updateImmunizationStatus(FragmentImmunizationEditLayoutBinding contentBinding) {
		if (Boolean.TRUE.equals(contentBinding.overwriteImmunizationManagementStatusCheckBox.getValue())) {
			return;
		}

		MeansOfImmunization meansOfImmunizationValue = (MeansOfImmunization) contentBinding.immunizationMeansOfImmunization.getValue();

		if (meansOfImmunizationValue == MeansOfImmunization.OTHER || meansOfImmunizationValue == MeansOfImmunization.RECOVERY) {
			contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.COMPLETED);
			contentBinding.overwriteImmunizationManagementStatusCheckBox.setValue(false);
			contentBinding.immunizationImmunizationManagementStatus.setEnabled(false);
		}

		if (MeansOfImmunization.isVaccination(meansOfImmunizationValue)) {
			ImmunizationStatus immunizationStatus = (ImmunizationStatus) contentBinding.immunizationImmunizationStatus.getValue();
			if (immunizationStatus != ImmunizationStatus.NOT_ACQUIRED && immunizationStatus != ImmunizationStatus.EXPIRED) {
				Integer numberOfDoses = null;
				try {
					numberOfDoses = Integer.valueOf(contentBinding.immunizationNumberOfDoses.getValue());
				} catch (Exception e) {
					numberOfDoses = null;
				}
				if (numberOfDoses != null && CollectionUtils.isNotEmpty(record.getVaccinations())) {
					if (record.getVaccinations().size() >= numberOfDoses) {
						contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.COMPLETED);
					} else {
						contentBinding.immunizationImmunizationManagementStatus.setValue(ImmunizationManagementStatus.ONGOING);
					}
				}
			}
		}
	}

	private void removeVaccinationsConfirmation(FragmentImmunizationEditLayoutBinding contentBinding) {
		ConfirmationDialog dialog = new ConfirmationDialog(
			getActivity(),
			I18nProperties.getString(Strings.headingDeleteVaccinations),
			I18nProperties.getString(Strings.messageDeleteImmunizationVaccinations),
			R.string.action_confirm,
			R.string.action_cancel);
		dialog.setPositiveCallback(() -> {
			record.setVaccinations(new ArrayList<>());
			updateMeansOfImmunizationFields(contentBinding);
			updateImmunizationStatus(contentBinding);
		});
		dialog.setNegativeCallback(() -> {
			contentBinding.immunizationMeansOfImmunization.setValue(currentMeansOfImmunization);
			dialog.dismiss();
		});
		dialog.show();
	}

	private void linkRecoveryImmunizationToCaseSearchCaseIncluded(Immunization immunization) {

		ImmunizationSearchCaseDialog.searchCaseToLinkImmunization(ImmunizationEditActivity.getActiveActivity(), caseSearchField -> {
			CaseCriteria criteria = new CaseCriteria();
			criteria.setPerson(immunization.getPerson());
			criteria.setDisease(immunization.getDisease());
			criteria.setOutcome(CaseOutcome.RECOVERED);

			criteria.setTextFilter(caseSearchField);

			List<Case> cases = DatabaseHelper.getCaseDao().queryByCriteria(criteria, 0, 1);

			if (cases != null && !cases.isEmpty() && cases.get(0) != null) {
				Case foundCase = cases.get(0);
				immunization.setRelatedCase(foundCase);
				List<Sample> samples = DatabaseHelper.getSampleDao().queryByCase(foundCase);
				PathogenTest relevantPathogenTest = null;
				for (Sample sample : samples) {
					List<PathogenTest> pathogenTests = DatabaseHelper.getSampleTestDao().queryBySample(sample);

					for (PathogenTest pathogenTest : pathogenTests) {
						if (pathogenTest.getTestedDisease().equals(foundCase.getDisease())
							&& PathogenTestResultType.POSITIVE.equals(pathogenTest.getTestResult())
							&& (relevantPathogenTest == null || relevantPathogenTest.getTestDateTime().before(pathogenTest.getTestDateTime()))) {
							relevantPathogenTest = pathogenTest;
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
			} else {
				NotificationHelper.showNotification(
					ImmunizationEditActivity.getActiveActivity(),
					NotificationType.WARNING,
					I18nProperties.getString(Strings.messageNoCaseFoundToLinkImmunization));
			}
		});
	}
}
