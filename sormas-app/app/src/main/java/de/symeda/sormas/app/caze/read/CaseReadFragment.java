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

package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.databinding.DialogClassificationRulesLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentCaseReadLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CaseReadFragment extends BaseReadFragment<FragmentCaseReadLayoutBinding, Case, Case> {

    private Case record;

    public static CaseReadFragment newInstance(Case activityRootData) {
        return newInstance(CaseReadFragment.class, null, activityRootData);
    }

    private void setUpFieldVisibilities(FragmentCaseReadLayoutBinding contentBinding) {
        setVisibilityByDisease(CaseDataDto.class, record.getDisease(), contentBinding.mainContent);
        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);
        InfrastructureHelper.initializePointOfEntryDetailsFieldVisibility(contentBinding.caseDataPointOfEntry, contentBinding.caseDataPointOfEntryDetails);

        // Vaccination date
        if (isVisibleAllowed(CaseDataDto.class, record.getDisease(), contentBinding.caseDataVaccination)) {
            setVisibleWhen(contentBinding.caseDataVaccinationDate, contentBinding.caseDataVaccination, Vaccination.VACCINATED);
        }
        if (isVisibleAllowed(CaseDataDto.class, record.getDisease(), contentBinding.caseDataSmallpoxVaccinationReceived)) {
            setVisibleWhen(contentBinding.caseDataVaccinationDate, contentBinding.caseDataSmallpoxVaccinationReceived, YesNoUnknown.YES);
        }

        // Pregnancy
        if (record.getPerson().getSex() != Sex.FEMALE) {
            contentBinding.caseDataPregnant.setVisibility(GONE);
        }

        // Port Health fields
        if (UserRole.isPortHealthUser(ConfigProvider.getUser().getUserRoles())) {
            contentBinding.caseDataCaseOrigin.setVisibility(GONE);
            contentBinding.healthFacilityFieldsLayout.setVisibility(GONE);
        } else {
            if (record.getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY) {
                if (record.getHealthFacility() == null) {
                    contentBinding.healthFacilityFieldsLayout.setVisibility(GONE);
                    contentBinding.caseDataHealthFacilityDetails.setVisibility(GONE);
                }
            } else {
                contentBinding.pointOfEntryFieldsLayout.setVisibility(GONE);
            }
        }

        // Button panel
        DiseaseClassificationCriteria diseaseClassificationCriteria = DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(record.getDisease());
        if (diseaseClassificationCriteria == null || !diseaseClassificationCriteria.hasAnyCriteria()) {
            contentBinding.caseButtonsPanel.setVisibility(GONE);
        }

        if (!ConfigProvider.isGermanServer()) {
            contentBinding.caseDataExternalID.setVisibility(GONE);
        } else {
            contentBinding.caseDataEpidNumber.setVisibility(GONE);
        }

        if (!ConfigProvider.isGermanServer()) {
            contentBinding.caseDataQuarantineOrderedVerbally.setVisibility(GONE);
            contentBinding.caseDataQuarantineOrderedVerballyDate.setVisibility(GONE);
            contentBinding.caseDataQuarantineOrderedOfficialDocument.setVisibility(GONE);
            contentBinding.caseDataQuarantineOrderedOfficialDocumentDate.setVisibility(GONE);
        }
    }

    private void setUpControlListeners(FragmentCaseReadLayoutBinding contentBinding) {
        contentBinding.showClassificationRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InfoDialog classificationDialog = new InfoDialog(CaseReadFragment.this.getContext(), R.layout.dialog_classification_rules_layout, null);
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
}