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

package de.symeda.sormas.app.caze.read;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseConfirmationBasis;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.UserRole;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.databinding.DialogClassificationRulesLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseReadLayoutBinding;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;

public class CaseReadFragment extends BaseReadFragment<FragmentCaseReadLayoutBinding, Case, Case> {

	private Case record;
	private CaseConfirmationBasis caseConfirmationBasis;

	public static CaseReadFragment newInstance(Case activityRootData) {
		CaseReadFragment caseReadFragment = newInstanceWithFieldCheckers(
			CaseReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		return caseReadFragment;
	}

	private void setUpFieldVisibilities(FragmentCaseReadLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(CaseDataDto.class, contentBinding.mainContent);
		InfrastructureDaoHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
		InfrastructureDaoHelper
			.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

		// Port Health fields
		if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
			contentBinding.caseDataCaseOrigin.setVisibility(GONE);
			contentBinding.facilityOrHome.setVisibility(GONE);
			contentBinding.caseDataCommunity.setVisibility(GONE);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else {
			if (record.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY) {
				if (record.getHealthFacility() == null) {
					contentBinding.facilityOrHome.setVisibility(GONE);
					contentBinding.caseDataCommunity.setVisibility(GONE);
					contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
					contentBinding.caseDataHealthFacility.setVisibility(GONE);
					contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
				}
			} else {
				contentBinding.pointOfEntryFieldsLayout.setVisibility(GONE);
			}
		}

		// Button panel
		DiseaseClassificationCriteria diseaseClassificationCriteria =
			DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(record.getDisease());
		if (diseaseClassificationCriteria == null || !diseaseClassificationCriteria.hasAnyCriteria()) {
			contentBinding.caseButtonsPanel.setVisibility(GONE);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataExternalID.setVisibility(GONE);
		} else {
			contentBinding.caseDataEpidNumber.setVisibility(GONE);
		}

		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedVerballyDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
			contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSent.setVisibility(GONE);
			contentBinding.caseDataQuarantineOfficialOrderSentDate.setVisibility(GONE);
		}

		updateCaseConfirmationFields(contentBinding);

		contentBinding.caseDataQuarantineExtended.setVisibility(record.isQuarantineExtended() ? VISIBLE : GONE);
		contentBinding.caseDataQuarantineReduced.setVisibility(record.isQuarantineReduced() ? VISIBLE : GONE);

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactType)
			|| isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataContactTracingFirstContactDate)) {
			contentBinding.caseDataContactTracingDivider.setVisibility(VISIBLE);
			contentBinding.caseDataContactTracingFirstContactHeading.setVisibility(VISIBLE);
		}

		if (isVisibleAllowed(CaseDataDto.class, contentBinding.caseDataDiseaseVariant)) {
			contentBinding.caseDataDiseaseVariant.setVisibility(record.getDiseaseVariant() != null ? VISIBLE : GONE);
		}
	}

	private void updateCaseConfirmationFields(FragmentCaseReadLayoutBinding contentBinding) {

		Disease disease = record.getDisease();
		boolean extendedClassification = DiseaseConfigurationCache.getInstance().usesExtendedClassification(disease);
		if (extendedClassification) {
			boolean extendedClassificationMulti = DiseaseConfigurationCache.getInstance().usesExtendedClassificationMulti(disease);
			if (extendedClassificationMulti) {
				contentBinding.caseDataClinicalConfirmation.setVisibility(VISIBLE);
				contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(VISIBLE);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(VISIBLE);
				contentBinding.caseDataCaseConfirmationBasis.setVisibility(GONE);
			} else {
				contentBinding.caseDataClinicalConfirmation.setVisibility(GONE);
				contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(GONE);
				contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(GONE);
				contentBinding.caseDataCaseConfirmationBasis
					.setVisibility(record.getCaseClassification() == CaseClassification.CONFIRMED ? VISIBLE : GONE);
			}
		} else {
			contentBinding.caseDataClinicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataEpidemiologicalConfirmation.setVisibility(GONE);
			contentBinding.caseDataLaboratoryDiagnosticConfirmation.setVisibility(GONE);
			contentBinding.caseDataCaseConfirmationBasis.setVisibility(GONE);
		}
	}

	private void setUpControlListeners(FragmentCaseReadLayoutBinding contentBinding) {
		contentBinding.showClassificationRules.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final InfoDialog classificationDialog =
					new InfoDialog(CaseReadFragment.this.getContext(), R.layout.dialog_classification_rules_layout, null);
				WebView classificationView = ((DialogClassificationRulesLayoutBinding) classificationDialog.getBinding()).content;
				classificationView.loadData(DiseaseClassificationAppHelper.buildDiseaseClassificationHtml(record.getDisease()), "text/html", "utf-8");
				classificationDialog.show();
			}
		});
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);

		if (record.getClinicalConfirmation() == YesNoUnknown.YES) {
			contentBinding.setSingleClassification(CaseConfirmationBasis.CLINICAL_CONFIRMATION);
		} else if (record.getEpidemiologicalConfirmation() == YesNoUnknown.YES) {
			contentBinding.setSingleClassification(CaseConfirmationBasis.EPIDEMIOLOGICAL_CONFIRMATION);
		} else if (record.getLaboratoryDiagnosticConfirmation() == YesNoUnknown.YES) {
			contentBinding.setSingleClassification(CaseConfirmationBasis.LABORATORY_DIAGNOSTIC_CONFIRMATION);
		}
	}

	@Override
	public void onAfterLayoutBinding(FragmentCaseReadLayoutBinding contentBinding) {
		setUpFieldVisibilities(contentBinding);

		// Replace classification user field with classified by field when case has been classified automatically
		if (contentBinding.getData().getClassificationDate() != null && contentBinding.getData().getClassificationUser() == null) {
			contentBinding.caseDataClassificationUser.setVisibility(GONE);
			contentBinding.caseDataClassifiedBy.setVisibility(VISIBLE);
			contentBinding.caseDataClassifiedBy.setValue(getResources().getString(R.string.system));
		}

		if(!isFieldAccessible(CaseDataDto.class, contentBinding.caseDataHealthFacility)){
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityOrHome);
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityTypeGroup);
		} else if (record.getHealthFacility() == null) {
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
			contentBinding.caseDataHealthFacility.setVisibility(GONE);
			contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
		} else if (FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())) {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.HOME);
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
		} else {
			contentBinding.facilityOrHome.setValue(TypeOfPlace.FACILITY);
			contentBinding.facilityTypeGroup.setValue(record.getFacilityType().getFacilityTypeGroup());
		}

		contentBinding.caseDataReportingUser.setPseudonymized(record.isPseudonymized());
		contentBinding.caseDataSurveillanceOfficer.setPseudonymized(record.isPseudonymized());
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_information);
	}

	@Override
	public Case getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_case_read_layout;
	}

	public CaseConfirmationBasis getCaseConfirmationBasis() {
		return caseConfirmationBasis;
	}

	public void setCaseConfirmationBasis(CaseConfirmationBasis caseConfirmationBasis) {
		this.caseConfirmationBasis = caseConfirmationBasis;
	}
}
