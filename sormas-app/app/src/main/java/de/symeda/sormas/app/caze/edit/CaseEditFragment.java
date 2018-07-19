package de.symeda.sormas.app.caze.edit;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.FragmentCaseEditLayoutBinding;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class CaseEditFragment extends BaseEditFragment<FragmentCaseEditLayoutBinding, Case, Case> {

    public static final String TAG = CaseEditFragment.class.getSimpleName();

    private Case record;

    // Enum lists

    private List<Item> caseClassificationList;
    private List<Item> caseOutcomeList;
    private List<Item> vaccinationInfoSourceList;
    private List<Item> plagueTypeList;
    private List<Item> dengueFeverTypeList;

    // Instance methods

    private void setUpFieldVisibilities(final FragmentCaseEditLayoutBinding contentBinding) {
        setVisibilityByDisease(CaseDataDto.class, contentBinding.getData().getDisease(), contentBinding.mainContent);
        InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

        // Vaccination date
        if (isVisibleAllowed(CaseDataDto.class, contentBinding.getData().getDisease(), contentBinding.caseDataVaccination)) {
            setVisibleWhen(contentBinding.caseDataVaccinationDate, contentBinding.caseDataVaccination, Vaccination.VACCINATED);
        }
        if (isVisibleAllowed(CaseDataDto.class, contentBinding.getData().getDisease(), contentBinding.caseDataSmallpoxVaccinationReceived)) {
            setVisibleWhen(contentBinding.caseDataVaccinationDate, contentBinding.caseDataSmallpoxVaccinationReceived, YesNoUnknown.YES);
        }

        // Pregnancy
        if (record.getPerson().getSex() != Sex.FEMALE) {
            contentBinding.caseDataPregnant.setVisibility(View.GONE);
        }

        // Smallpox vaccination scar image
        contentBinding.caseDataSmallpoxVaccinationScar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentBinding.smallpoxVaccinationScarImg.setVisibility(contentBinding.caseDataSmallpoxVaccinationScar.getVisibility());
            }
        });

    }

    private void setUpButtonListeners(FragmentCaseEditLayoutBinding contentBinding) {
        contentBinding.transferCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CaseEditActivity activity = (CaseEditActivity) CaseEditFragment.this.getActivity();
                activity.saveData(new Consumer<Case>() {
                    @Override
                    public void accept(Case caze) {
                        final MoveCaseDialog moveCaseDialog = new MoveCaseDialog(BaseActivity.getActiveActivity(), caze);
                        moveCaseDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                            @Override
                            public void onOkClick(View v, Object item, View viewRoot) {
                                record = (Case) item;
                                requestLayoutRebind();
                                moveCaseDialog.dismiss();
                            }
                        });
                        moveCaseDialog.show(null);
                    }
                });
            }
        });
    }

    public static CaseEditFragment newInstance(CaseFormNavigationCapsule capsule, Case activityRootData) {
        return newInstance(CaseEditFragment.class, capsule, activityRootData);
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
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();

        caseClassificationList = DataUtils.getEnumItems(CaseClassification.class, false);
        caseOutcomeList = DataUtils.getEnumItems(CaseOutcome.class, false);
        vaccinationInfoSourceList = DataUtils.getEnumItems(VaccinationInfoSource.class, true);
        plagueTypeList = DataUtils.getEnumItems(PlagueType.class, true);
        dengueFeverTypeList = DataUtils.getEnumItems(DengueFeverType.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentCaseEditLayoutBinding contentBinding) {
        setUpButtonListeners(contentBinding);

        // Epid number warning state
        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_CHANGE_EPID_NUMBER)) {
            contentBinding.caseDataEpidNumber.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    String value = (String) field.getValue();
                    if (value.trim().isEmpty()) {
                        getContentBinding().caseDataEpidNumber.enableWarningState((NotificationContext) getActivity(),
                                R.string.validation_soft_case_epid_number_empty);
                    } else if (value.matches(DataHelper.getEpidNumberRegexp())) {
                        getContentBinding().caseDataEpidNumber.disableWarningState();
                    } else {
                        getContentBinding().caseDataEpidNumber.enableWarningState((NotificationContext) getActivity(),
                                R.string.validation_soft_case_epid_number);
                    }
                }
            });
        }

        // Case classification warning state
        if (ConfigProvider.getUser().hasUserRight(UserRight.CASE_CLASSIFY)) {
            contentBinding.caseDataCaseClassification.addValueChangedListener(new ValueChangeListener() {
                @Override
                public void onChange(ControlPropertyField field) {
                    CaseClassification caseClassification = (CaseClassification) field.getValue();
                    if (caseClassification == CaseClassification.NOT_CLASSIFIED) {
                        getContentBinding().caseDataCaseClassification.enableWarningState((NotificationContext) getActivity(),
                                R.string.validation_soft_case_classification);
                    } else {
                        getContentBinding().caseDataCaseClassification.disableWarningState();
                    }
                }
            });
        }

        contentBinding.setData(record);
        contentBinding.setYesNoUnknownClass(YesNoUnknown.class);
        contentBinding.setVaccinationClass(Vaccination.class);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentCaseEditLayoutBinding contentBinding) {
        setUpFieldVisibilities(contentBinding);

        // Initialize ControlSpinnerFields
        contentBinding.caseDataCaseClassification.initializeSpinner(caseClassificationList);
        contentBinding.caseDataOutcome.initializeSpinner(caseOutcomeList);
        contentBinding.caseDataPlagueType.initializeSpinner(plagueTypeList);
        contentBinding.caseDataDengueFeverType.initializeSpinner(dengueFeverTypeList);
        contentBinding.caseDataVaccinationInfoSource.initializeSpinner(vaccinationInfoSourceList);

        // Initialize ControlDateFields
        contentBinding.caseDataOutcomeDate.initializeDateField(getFragmentManager());
        contentBinding.caseDataVaccinationDate.initializeDateField(getFragmentManager());
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
    public boolean isShowAddAction() {
        return false;
    }

}
