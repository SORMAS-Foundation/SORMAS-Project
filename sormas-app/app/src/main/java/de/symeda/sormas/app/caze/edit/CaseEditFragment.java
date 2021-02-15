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

import android.webkit.WebView;

import androidx.fragment.app.FragmentActivity;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.ContactTracingContactType;
import de.symeda.sormas.api.caze.CovidTestReason;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.HospitalWardType;
import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.caze.ReportingType;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.disease.DiseaseVariant;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.ConfirmationDialog;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.DialogClassificationRulesLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CaseEditFragment extends BaseEditFragment<FragmentCaseEditLayoutBinding, Case, Case> {

	public static final String TAG = CaseEditFragment.class.getSimpleName();

	private Case record;

	// Enum lists

	private List<Item> caseClassificationList;
	private List<Item> caseIdentificationSourceList;
	private List<Item> caseOutcomeList;
	private List<Item> vaccinationInfoSourceList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
	private List<Item> plagueTypeList;
	private List<Item> dengueFeverTypeList;
	private List<Item> humanRabiesTypeList;
	private List<Item> hospitalWardTypeList;
	private List<Item> initialRegions;
	private List<Item> allDistricts;
	private List<Item> initialDistricts;
	private List<Item> initialCommunities;
	private List<Item> initialFacilities;
	private List<Item> quarantineList;
	private List<Item> reportingTypeList;
	private List<Item> facilityOrHomeList;
	private List<Item> facilityTypeGroupList;
	private List<Item> quarantineReasonList;
	private List<Item> endOfIsolationReasonList;
	private List<Item> covidTestReasonList;
	private List<Item> contactTracingContactTypeList;
	private List<Item> infectionSettingList;
	private List<Item> vaccineList;
	private List<Item> vaccineManufacturerList;

	// Static methods

	public static CaseEditFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));
	}

	// Instance methods

	private void setUpFieldVisibilities(final FragmentCaseEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(CaseDataDto.class, contentBinding.mainContent);
		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
		InfrastructureHelper
			.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

		if (!isFieldAccessible(CaseDataDto.class, contentBinding.caseDataCommunity)) {
			contentBinding.caseDataRegion.setEnabled(false);
			contentBinding.caseDataDistrict.setEnabled(false);
		}

		// Vaccination
		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataVaccineName)) {
			setVisibleWhen(contentBinding.caseDataVaccineName, contentBinding.caseDataVaccination, Vaccination.VACCINATED);
		}
		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataVaccineManufacturer)) {
			setVisibleWhen(contentBinding.caseDataVaccineManufacturer, contentBinding.caseDataVaccination, Vaccination.VACCINATED);
		}

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataSmallpoxVaccinationReceived)) {
			setVisibleWhen(contentBinding.caseDataLastVaccinationDate, contentBinding.caseDataSmallpoxVaccinationReceived, YesNoUnknown.YES);
		}

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataVaccine)) {
			setVisibleWhen(contentBinding.caseDataVaccine, contentBinding.caseDataVaccination, Vaccination.VACCINATED);
		}

		// Pregnancy
		if (record.getPerson().getSex() != Sex.FEMALE) {
			contentBinding.caseDataPregnant.setVisibility(GONE);
			contentBinding.caseDataPostpartum.setVisibility(GONE);
		}

		// Smallpox vaccination scar image
		contentBinding.caseDataSmallpoxVaccinationScar.getViewTreeObserver()
			.addOnGlobalLayoutListener(
				() -> contentBinding.smallpoxVaccinationScarImg.setVisibility(contentBinding.caseDataSmallpoxVaccinationScar.getVisibility()));

		// Port Health fields
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.facilityOrHomeLayout.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else {
			if (record.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY) {
				if (record.getHealthFacility() == null) {
					contentBinding.facilityOrHomeLayout.setVisibility(GONE);
					contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
					contentBinding.caseDataHealthFacility.setVisibility(GONE);
					contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
				}
			} else {
				contentBinding.pointOfEntryFieldsLayout.setVisibility(GONE);
			}
		}

		// Button panel
		DiseaseClassificationCriteria classificationCriteria = DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(record.getDisease());
		if (classificationCriteria == null || !classificationCriteria.hasAnyCriteria()) {
			contentBinding.showClassificationRules.setVisibility(GONE);
		}
		if (!ConfigProvider.hasUserRight(UserRight.CASE_REFER_FROM_POE)
			|| record.getCaseOrigin() != CaseOrigin.POINT_OF_ENTRY
			|| record.getHealthFacility() != null) {
			contentBinding.referCaseFromPoe.setVisibility(GONE);
		}
		if (contentBinding.showClassificationRules.getVisibility() == GONE && contentBinding.referCaseFromPoe.getVisibility() == GONE) {
			contentBinding.caseButtonsPanel.setVisibility(GONE);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			contentBinding.caseDataExternalID.setVisibility(GONE);
			contentBinding.caseDataExternalToken.setVisibility(GONE);
			contentBinding.caseDataReportingType.setVisibility(GONE);
			contentBinding.caseDataClinicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(GONE);
		} else {
			contentBinding.caseDataEpidNumber.setVisibility(GONE);
		}

		contentBinding.caseDataQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE);
		contentBinding.caseDataQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE);
	}

	private void setUpButtonListeners(FragmentCaseEditLayoutBinding contentBinding) {

		contentBinding.referCaseFromPoe.setOnClickListener(e -> {
			final CaseEditActivity activity = (CaseEditActivity) CaseEditFragment.this.getActivity();
			activity.saveData(caze -> {
				final Case caseClone = (Case) caze.clone();
				final ReferCaseFromPoeDialog referCaseFromPoeDialog = new ReferCaseFromPoeDialog(BaseActivity.getActiveActivity(), caze);
				referCaseFromPoeDialog.setPositiveCallback(() -> {
					record = caseClone;
					requestLayoutRebind();
				});
				referCaseFromPoeDialog.show();
			});
		});

		contentBinding.showClassificationRules.setOnClickListener(v -> {
			final InfoDialog classificationDialog =
				new InfoDialog(CaseEditFragment.this.getContext(), R.layout.dialog_classification_rules_layout, null);
			WebView classificationView = ((DialogClassificationRulesLayoutBinding) classificationDialog.getBinding()).content;
			classificationView.loadData(DiseaseClassificationAppHelper.buildDiseaseClassificationHtml(record.getDisease()), "text/html", "utf-8");
			classificationDialog.show();
		});
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_information);
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
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getDiseaseVariantDao().getAllByDisease(record.getDisease());
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		if (record.getDiseaseVariant() != null && !diseaseVariants.contains(record.getDiseaseVariant())) {
			diseaseVariantList.add(DataUtils.toItem(record.getDiseaseVariant()));
		}

		caseClassificationList = DataUtils.getEnumItems(CaseClassification.class, true);
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			caseClassificationList.remove(new Item<>(CaseClassification.CONFIRMED_NO_SYMPTOMS.toString(), CaseClassification.CONFIRMED_NO_SYMPTOMS));
			caseClassificationList
				.remove(new Item<>(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS.toString(), CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS));
		}
		caseIdentificationSourceList = DataUtils.getEnumItems(CaseIdentificationSource.class, true);
		caseOutcomeList = DataUtils.getEnumItems(CaseOutcome.class, true);
		vaccinationInfoSourceList = DataUtils.getEnumItems(VaccinationInfoSource.class, true);
		plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
		dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
		humanRabiesTypeList = DataUtils.getEnumItems(RabiesType.class, true);
		hospitalWardTypeList = DataUtils.getEnumItems(HospitalWardType.class, true);
		quarantineList = DataUtils.getEnumItems(QuarantineType.class, true);
		reportingTypeList = DataUtils.getEnumItems(ReportingType.class, true);

		initialRegions = InfrastructureHelper.loadRegions();
		allDistricts = InfrastructureHelper.loadAllDistricts();
		initialDistricts = InfrastructureHelper.loadDistricts(record.getRegion());
		initialCommunities = InfrastructureHelper.loadCommunities(record.getDistrict());
		initialFacilities = InfrastructureHelper.loadFacilities(record.getDistrict(), record.getCommunity(), record.getFacilityType());
		facilityOrHomeList = DataUtils.toItems(TypeOfPlace.getTypesOfPlaceForCases(), true);
		facilityTypeGroupList = DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups(), true);

		quarantineReasonList = DataUtils.getEnumItems(QuarantineReason.class, true);
		endOfIsolationReasonList = DataUtils.getEnumItems(EndOfIsolationReason.class, true);
		covidTestReasonList = DataUtils.getEnumItems(CovidTestReason.class, true);
		contactTracingContactTypeList = DataUtils.getEnumItems(ContactTracingContactType.class, true);
		infectionSettingList = DataUtils.getEnumItems(InfectionSetting.class, true);
		vaccineList = DataUtils.getEnumItems(Vaccine.class, true);
		vaccineManufacturerList = DataUtils.getEnumItems(VaccineManufacturer.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
		setUpButtonListeners(contentBinding);

		// Case classification warning state
		if (ConfigProvider.hasUserRight(UserRight.CASE_CLASSIFY)) {
			contentBinding.caseDataCaseClassification.addValueChangedListener(field -> {

				final CaseClassification caseClassification = (CaseClassification) field.getValue();
				if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
					getContentBinding().caseDataCaseClassification.enableWarningState(R.string.validation_soft_case_classification);
				} else {
					getContentBinding().caseDataCaseClassification.disableWarningState();
				}

				CaseValidator.initializeGermanCaseClassificationValidation(record, caseClassification, getContentBinding());
			});
		}

		FragmentActivity thisActivity = this.getActivity();
		contentBinding.caseDataDisease.addValueChangedListener(new ValueChangeListener() {

			Disease currentDisease = record.getDisease();

			@Override
			public void onChange(ControlPropertyField field) {
				if (this.currentDisease != null && contentBinding.caseDataDisease.getValue() != currentDisease) {

					int headingResId = R.string.heading_change_case_disease;
					int subHeadingResId = R.string.message_change_case_disease;
					int positiveButtonTextResId = R.string.action_change_case_disease;
					int negativeButtonTextResId = R.string.action_cancel;

					ConfirmationDialog dlg =
						new ConfirmationDialog(thisActivity, headingResId, subHeadingResId, positiveButtonTextResId, negativeButtonTextResId);
					dlg.setCancelable(false);
					dlg.setNegativeCallback(() -> contentBinding.caseDataDisease.setValue(currentDisease));
					dlg.setPositiveCallback(() -> {
						this.currentDisease = null;

						updateDiseaseVariantsField(contentBinding);
					});
					dlg.show();
				} else if (this.currentDisease == null) {
					// It means the disease were already changed
					updateDiseaseVariantsField(contentBinding);
				}
			}
		});

		contentBinding.setData(record);
		contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
		contentBinding.setVaccinationClass(Vaccination.class);
		contentBinding.setTrimesterClass(Trimester.class);

		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

		InfrastructureHelper.initializeFacilityFields(
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
			false);

		if (record.getCaseOrigin() != CaseOrigin.POINT_OF_ENTRY && isFieldAccessible(CaseDataDto.class, contentBinding.caseDataHealthFacility)) {
			contentBinding.caseDataHealthFacility.setRequired(true);
		}

		contentBinding.caseDataQuarantine.addValueChangedListener(e -> {
			boolean visible = QuarantineType.HOME.equals(contentBinding.caseDataQuarantine.getValue())
				|| QuarantineType.INSTITUTIONELL.equals(contentBinding.caseDataQuarantine.getValue());
			if (visible) {
				if (ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
					|| ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
					contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(VISIBLE);
					contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(VISIBLE);
				}
			} else {
				contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
				contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
				contentBinding.caseDataQuarantineExtended.setVisibility(GONE);
				contentBinding.caseDataQuarantineReduced.setVisibility(GONE);
			}
		});
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedVerballyDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSent.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSentDate.setVisibility(GONE);
		}

		contentBinding.caseDataQuarantineExtended.setEnabled(false);
		contentBinding.caseDataQuarantineReduced.setEnabled(false);

		contentBinding.caseDataQuarantineTo.addValueChangedListener(new ValueChangeListener() {

			private Date currentQuarantineTo = record.getQuarantineTo();
			private boolean currentQuarantineExtended = record.isQuarantineExtended();
			private boolean currentQuarantineReduced = record.isQuarantineReduced();

			@Override
			public void onChange(ControlPropertyField e) {
				Date newQuarantineTo = (Date) e.getValue();

				if (newQuarantineTo == null) {
					contentBinding.caseDataQuarantineExtended.setValue(false);
					contentBinding.caseDataQuarantineReduced.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.after(currentQuarantineTo)) {
					extendQuarantine();
				} else if (!currentQuarantineExtended) {
					contentBinding.caseDataQuarantineExtended.setValue(false);
				}
				if (currentQuarantineTo != null && newQuarantineTo != null && newQuarantineTo.before(currentQuarantineTo)) {
					reduceQuarantine();
				} else if (!currentQuarantineReduced) {
					contentBinding.caseDataQuarantineReduced.setValue(false);
				}
			}

			private void extendQuarantine() {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_extend_quarantine,
					R.string.confirmation_extend_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.caseDataQuarantineExtended.setValue(true);
					contentBinding.caseDataQuarantineReduced.setValue(false);
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.caseDataQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}

			private void reduceQuarantine() {
				final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
					getActivity(),
					R.string.heading_reduce_quarantine,
					R.string.confirmation_reduce_quarantine,
					R.string.yes,
					R.string.no);

				confirmationDialog.setPositiveCallback(() -> {
					contentBinding.caseDataQuarantineExtended.setValue(false);
					contentBinding.caseDataQuarantineReduced.setValue(true);
				});
				confirmationDialog.setNegativeCallback(() -> contentBinding.caseDataQuarantineTo.setValue(currentQuarantineTo));
				confirmationDialog.show();
			}
		});

		contentBinding.caseDataQuarantineExtended
			.addValueChangedListener(e -> contentBinding.caseDataQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE));
		contentBinding.caseDataQuarantineReduced
			.addValueChangedListener(e -> contentBinding.caseDataQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE));

		contentBinding.caseDataVaccineName.addValueChangedListener(new ValueChangeListener() {

			private Vaccine currentVaccine = record.getVaccineName();

			@Override
			public void onChange(ControlPropertyField e) {
				Vaccine vaccine = (Vaccine) e.getValue();

				if (currentVaccine != vaccine) {
					contentBinding.caseDataVaccineManufacturer.setValue(vaccine != null ? vaccine.getManufacturer() : null);
					currentVaccine = vaccine;
				}
			}
		});

		CaseValidator.initializeProhibitionToWorkIntervalValidator(contentBinding);
		ValidationHelper
			.initIntegerValidator(contentBinding.caseDataVaccinationDoses, I18nProperties.getValidationError(Validations.vaccineDosesFormat), 1, 10);
		ValidationHelper.initDateIntervalValidator(contentBinding.caseDataFirstVaccinationDate, contentBinding.caseDataLastVaccinationDate);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentCaseEditLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);
		if (ConfigProvider.getUser().getHealthFacility() != null || ConfigProvider.getUser().getCommunity() != null) {
			contentBinding.caseDataDistrictLevelDate.setEnabled(false);
		}

		// Initialize ControlSpinnerFields
		contentBinding.caseDataDisease.initializeSpinner(diseaseList);
		contentBinding.caseDataDiseaseVariant.initializeSpinner(diseaseVariantList);
		contentBinding.caseDataCaseClassification.initializeSpinner(caseClassificationList);
		contentBinding.caseDataCaseIdentificationSource.initializeSpinner(caseIdentificationSourceList);
		contentBinding.caseDataOutcome.initializeSpinner(caseOutcomeList);
		contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
		contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
		contentBinding.caseDataRabiesType.initializeSpinner(humanRabiesTypeList);
		contentBinding.caseDataNotifyingClinic.initializeSpinner(hospitalWardTypeList);
		contentBinding.caseDataVaccinationInfoSource.initializeSpinner(vaccinationInfoSourceList);
		contentBinding.caseDataQuarantine.initializeSpinner(quarantineList);
		contentBinding.caseDataReportingDistrict.initializeSpinner(allDistricts);

		// Initialize ControlDateFields
		contentBinding.caseDataReportDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataOutcomeDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataFirstVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataLastVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataDistrictLevelDate.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineFrom.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineTo.initializeDateField(getFragmentManager());
		contentBinding.caseDataQuarantineOrderedVerballyDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataQuarantineOfficialOrderSentDate.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataReportingType.initializeSpinner(reportingTypeList);

		// Replace classification user field with classified by field when case has been classified automatically
		if (contentBinding.getData().getClassificationDate() != null && contentBinding.getData().getClassificationUser() == null) {
			contentBinding.caseDataClassificationUser.setVisibility(GONE);
			contentBinding.caseDataClassifiedBy.setVisibility(VISIBLE);
			contentBinding.caseDataClassifiedBy.setValue(getResources().getString(R.string.system));
		}

		if (record.getHealthFacility() == null) {
			contentBinding.facilityOrHomeLayout.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else if (FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())) {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.HOME);
		} else {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.FACILITY);
			contentBinding.facilityTypeGroup.setValue(record.getFacilityType().getFacilityTypeGroup());
		}

		// Swiss fields
		contentBinding.caseDataQuarantineReasonBeforeIsolation.initializeSpinner(quarantineReasonList);
		contentBinding.caseDataEndOfIsolationReason.initializeSpinner(endOfIsolationReasonList);

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataCovidTestReason)) {
			contentBinding.caseDataCovidTestReasonDivider.setVisibility(VISIBLE);
			contentBinding.caseDataCovidTestReason.initializeSpinner(covidTestReasonList);
		}
		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactType)
			|| isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactDate)) {
			contentBinding.caseDataContactTracingDivider.setVisibility(VISIBLE);
			contentBinding.caseDataContactTracingFirstContactHeading.setVisibility(VISIBLE);

			contentBinding.caseDataContactTracingFirstContactType.initializeSpinner(contactTracingContactTypeList);
			contentBinding.caseDataContactTracingFirstContactDate.initializeDateField(getChildFragmentManager());
		}

		// end swiss fields

		contentBinding.caseDataInfectionSetting.initializeSpinner(infectionSettingList);
		contentBinding.caseDataProhibitionToWorkFrom.initializeDateField(getChildFragmentManager());
		contentBinding.caseDataProhibitionToWorkUntil.initializeDateField(getChildFragmentManager());

		// vaccination
		contentBinding.caseDataVaccineName.initializeSpinner(vaccineList);
		contentBinding.caseDataVaccineManufacturer.initializeSpinner(vaccineManufacturerList);

		// reinfection
		contentBinding.caseDataPreviousInfectionDate.initializeDateField(getChildFragmentManager());
	}

	private void updateDiseaseVariantsField(FragmentCaseEditLayoutBinding contentBinding) {
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getDiseaseVariantDao().getAllByDisease(record.getDisease());
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.caseDataDiseaseVariant.setSpinnerData(diseaseVariantList);
		contentBinding.caseDataDiseaseVariant.setValue(null);
		contentBinding.caseDataDiseaseVariant.setVisibility(diseaseVariants.isEmpty() ? GONE : VISIBLE);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}
}
