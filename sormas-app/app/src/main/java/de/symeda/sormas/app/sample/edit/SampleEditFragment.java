package de.symeda.sormas.app.sample.edit;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.validation.SampleValidator;

public class SampleEditFragment extends BaseEditFragment<FragmentSampleEditLayoutBinding, Sample, Sample> {

    private Sample record;
    private SampleTest mostRecentTest;

    private IEntryItemOnClickListener onRecentTestItemClickListener;

    private List<Item> sampleMaterialList;
    private List<Item> testTypeList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    private OnLinkClickListener referralLinkCallback;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_sample_information);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        mostRecentTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(record);

        labList = DatabaseHelper.getFacilityDao().getLaboratories(true);
        sampleMaterialList = DataUtils.getEnumItems(SampleMaterial.class, false);
        testTypeList = DataUtils.getEnumItems(SampleTestType.class, false);
        sampleSourceList = DataUtils.getEnumItems(SampleSource.class, false);
    }

    @Override
    public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
        if (record == null)
            return;

        setupCallback();

        if (!record.isShipped()) {
            contentBinding.dtpShipmentDate.setVisibility(View.GONE);
            contentBinding.txtShipmentDetails.setVisibility(View.GONE);
        }

        if (record.getSampleMaterial() == SampleMaterial.OTHER) {
            contentBinding.txtSampleMaterialText.setVisibility(View.INVISIBLE);
        }

        if (record.getLab().getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
            contentBinding.txtLaboratoryDetails.setVisibility(View.VISIBLE);
        }

        if (record.getAssociatedCase().getDisease() != Disease.NEW_INFLUENCA) {
            contentBinding.spnSampleSource.setVisibility(View.GONE);
        }

        if (record.isReceived()) {
            contentBinding.sampleReceivedLayout.setVisibility(View.VISIBLE);
        }

        if (record.getSpecimenCondition() != SpecimenCondition.NOT_ADEQUATE) {
            contentBinding.recentTestLayout.setVisibility(View.VISIBLE);
            if (mostRecentTest != null) {
                contentBinding.spnTestType.setVisibility(View.VISIBLE);
                //contentBinding.sampleTestResult.setVisibility(View.VISIBLE);
            } else {
                contentBinding.sampleNoRecentTestText.setVisibility(View.VISIBLE);
            }
        }

        // only show referred to field when there is a referred sample
        if (record.getReferredTo() != null) {
            final Sample referredSample = record.getReferredTo();
            contentBinding.txtReferredTo.setVisibility(View.VISIBLE);
            contentBinding.txtReferredTo.setValue(getActivity().getResources().getString(R.string.sample_referred_to) + " " + referredSample.getLab().toString() + " " + "\u279D");
        } else {
            contentBinding.txtReferredTo.setVisibility(View.GONE);
        }

        //TODO: Set required hints for sample data
        SampleValidator.setRequiredHintsForSampleData(contentBinding);

        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());
        contentBinding.setResults(getTestResults());
        contentBinding.setRecentTestItemClickCallback(onRecentTestItemClickListener);

        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setReferralLinkCallback(referralLinkCallback);
    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleEditLayoutBinding contentBinding) {
        contentBinding.spnTestType.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (testTypeList.size() > 0) ? DataUtils.addEmptyItem(testTypeList)
                        : testTypeList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnSampleSource.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (sampleSourceList.size() > 0) ? DataUtils.addEmptyItem(sampleSourceList)
                        : sampleSourceList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnSampleMaterial.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (sampleMaterialList.size() > 0) ? DataUtils.addEmptyItem(sampleMaterialList)
                        : sampleMaterialList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                SampleMaterial material = (SampleMaterial) value;

                if (material == SampleMaterial.OTHER) {
                    contentBinding.txtSampleMaterialText.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.txtSampleMaterialText.setVisibility(View.INVISIBLE);
                    contentBinding.txtSampleMaterialText.setValue("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnLaboratory.initialize(new TeboSpinner.ISpinnerInitConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (labList.size() > 0) ? DataUtils.toItems(labList)
                        : DataUtils.toItems(labList, false);
            }

            @Override
            public void onItemSelected(TeboSpinner view, Object value, int position, long id) {
                Facility laboratory = (Facility) value;

                if (laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.txtLaboratoryDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.txtLaboratoryDetails.setVisibility(View.GONE);
                    contentBinding.txtLaboratoryDetails.setValue("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.dtpDateAndTimeOfSampling.setFragmentManager(getFragmentManager());
        contentBinding.dtpShipmentDate.setFragmentManager(getFragmentManager());

        //TODO: Properly disable Tebo controls
        if (!ConfigProvider.getUser().getUuid().equals(record.getReportingUser().getUuid())) {
            contentBinding.txtSampleCode.changeVisualState(VisualState.DISABLED);
            contentBinding.dtpDateAndTimeOfSampling.changeVisualState(VisualState.DISABLED);
            contentBinding.dtpDateAndTimeOfSampling.changeVisualState(VisualState.DISABLED);
            contentBinding.spnSampleMaterial.changeVisualState(VisualState.DISABLED);
            contentBinding.txtSampleMaterialText.changeVisualState(VisualState.DISABLED);
            contentBinding.spnTestType.changeVisualState(VisualState.DISABLED);
            contentBinding.spnLaboratory.changeVisualState(VisualState.DISABLED);
            contentBinding.txtLaboratoryDetails.changeVisualState(VisualState.DISABLED);
            contentBinding.swhShipped.changeVisualState(VisualState.DISABLED);
            contentBinding.dtpShipmentDate.changeVisualState(VisualState.DISABLED);
            contentBinding.txtShipmentDetails.changeVisualState(VisualState.DISABLED);
        }
    }

//    @Override
//    protected void updateUI(FragmentSampleEditLayoutBinding contentBinding, Sample sample) {
//        contentBinding.spnSampleMaterial.setValue(sample.getSampleMaterial(), true);
//        contentBinding.spnTestType.setValue(sample.getSuggestedTypeOfTest(), true);
//        contentBinding.spnLaboratory.setValue(sample.getLab(), true);
//    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
    }

    private ObservableArrayList getTestResults() {
        ObservableArrayList results = new ObservableArrayList();
        if (mostRecentTest != null)
            results.add(mostRecentTest);
        return results;
    }

    private void setupCallback() {
        onRecentTestItemClickListener = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {

            }
        };

        referralLinkCallback = new OnLinkClickListener() {
            @Override
            public void onClick(View v, Object item) {
                if (record != null && record.getReferredTo() != null) {
                    Sample referredSample = record.getReferredTo();
                    String sampleMaterial = record.getSampleMaterialText();
                    ShipmentStatus pageStatus = (ShipmentStatus)getBaseEditActivity().getPageStatus();

                    SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(getContext(), referredSample.getUuid(), pageStatus)
                            .setSampleMaterial(sampleMaterial);
                    SampleEditActivity.goToActivity(getActivity(), dataCapsule);
                }
            }
        };
    }

    public static SampleEditFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleEditFragment.class, capsule, activityRootData);
    }
}
