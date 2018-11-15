package de.symeda.sormas.app.caze.read;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationAppHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
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

        // Button panel
        if (!DatabaseHelper.getDiseaseClassificationCriteriaDao().getByDisease(record.getDisease()).hasAnyCriteria()) {
            contentBinding.caseButtonsPanel.setVisibility(GONE);
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
            contentBinding.caseDataClassifiedBy.setValue("System");
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