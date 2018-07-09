package de.symeda.sormas.app.sample.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;

public class SampleNewFragment extends BaseEditFragment<FragmentSampleNewLayoutBinding, Sample, Sample> {

    private AsyncTask onResumeTask;

    private String caseUuid = null;
    private String recordUuid = null;
    private String sampleMaterial = null;
    private ShipmentStatus pageStatus;
    private Sample record;
    private IEntryItemOnClickListener onRecentTestItemClickListener;
    private int mLastCheckedId = -1;

    private List<Item> sampleMaterialList;
    private List<Item> testTypeList;
    private List<Item> sampleSourceList;
    private List<Facility> labList;

    private OnLinkClickListener referralLinkCallback;
    private SampleTest mostRecentTest;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
        saveCaseUuidState(outState, caseUuid);
        saveSampleMaterialState(outState, sampleMaterial);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        caseUuid = getCaseUuidArg(arguments);
        sampleMaterial = getSampleMaterialArg(arguments);
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_new_sample);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Sample sample = getActivityRootData();

            resultHolder.forItem().add(sample);

            resultHolder.forOther().add(DataUtils.getEnumItems(SampleMaterial.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(SampleTestType.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(SampleSource.class, false));

            resultHolder.forList().add(DatabaseHelper.getFacilityDao().getLaboratories(true));
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (listIterator.hasNext())
                labList =  listIterator.next();

            if (otherIterator.hasNext())
                sampleMaterialList =  otherIterator.next();

            if (otherIterator.hasNext())
                testTypeList =  otherIterator.next();

            if (otherIterator.hasNext())
                sampleSourceList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentSampleNewLayoutBinding contentBinding) {
        //TODO: Set required hints for sample data
        //SampleValidator.setRequiredHintsForSampleData(contentBinding);``

//        contentBinding.setShippedYesCallback(this);
        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());

    }

    @Override
    public void onAfterLayoutBinding(final FragmentSampleNewLayoutBinding contentBinding) {
        contentBinding.sampleSuggestedTypeOfTest.initializeSpinner(testTypeList);

        contentBinding.sampleSampleSource.initializeSpinner(sampleSourceList);

        contentBinding.sampleSampleMaterial.initializeSpinner(sampleMaterialList, null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                SampleMaterial material = (SampleMaterial) field.getValue();

                if (material == SampleMaterial.OTHER) {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleSampleMaterialText.setVisibility(View.INVISIBLE);
                    contentBinding.sampleSampleMaterialText.setValue("");
                }
            }
        });

        contentBinding.sampleLab.initializeSpinner(DataUtils.toItems(labList), null, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Facility laboratory = (Facility) field.getValue();

                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
                    contentBinding.sampleLabDetails.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleLabDetails.setVisibility(View.GONE);
                    contentBinding.sampleLabDetails.setValue("");
                }
            }
        });

        contentBinding.sampleSampleDateTime.setFragmentManager(getFragmentManager());
        contentBinding.sampleShipmentDate.setFragmentManager(getFragmentManager());

    }

    @Override
    protected void updateUI(FragmentSampleNewLayoutBinding contentBinding, Sample sample) {
        contentBinding.sampleSampleMaterial.setValue(sample.getSampleMaterial());
        contentBinding.sampleSuggestedTypeOfTest.setValue(sample.getSuggestedTypeOfTest());
        contentBinding.sampleLab.setValue(sample.getLab());
    }

    public void onPageResume(FragmentSampleNewLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getBaseActivity().showPreloader();
                    //
                }

                @Override
                public void doInBackground(TaskResultHolder resultHolder) {
                    Sample sample = getActivityRootData();

                    resultHolder.forItem().add(sample);
                }
            };
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getBaseActivity().hidePreloader();
                    //getBaseActivity().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getBaseActivity().hidePreloader();
            //getBaseActivity().showFragmentView();
        }

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_new_layout;
    }

    private void setupCallback() {


    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

//    @Override
//    public void onCheckedChanged(ControlSwitchField teboSwitch, Object checkedItem, int checkedId) {
//        if (checkedId < 0)
//            return;
//
//        if (mLastCheckedId == checkedId) {
//            return;
//        }
//
//        mLastCheckedId = checkedId;
//
//        YesNo result = ((YesNo)checkedItem);
//
//        if (result == YesNo.YES) {
//            //record.setShipmentDate(new Date());
//            getContentBinding().sampleShipmentDate.setVisibility(View.VISIBLE);
//            getContentBinding().txtShipmentDetails.setVisibility(View.VISIBLE);
//            getContentBinding().sampleReceivedLayout.setVisibility(View.VISIBLE);
//        } else {
//            getContentBinding().sampleShipmentDate.setVisibility(View.GONE);
//            getContentBinding().txtShipmentDetails.setVisibility(View.GONE);
//            getContentBinding().sampleReceivedLayout.setVisibility(View.GONE);
//        }
//    }

    public static SampleNewFragment newInstance(SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(SampleNewFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
