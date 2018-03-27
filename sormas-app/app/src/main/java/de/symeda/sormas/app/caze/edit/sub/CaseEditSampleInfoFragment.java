package de.symeda.sormas.app.caze.edit.sub;

import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.OnTeboSwitchCheckedChangeListener;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.component.TeboSwitch;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentSampleEditLayoutBinding;
import de.symeda.sormas.app.sample.edit.SampleEditActivity;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 16/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class CaseEditSampleInfoFragment extends BaseEditActivityFragment<FragmentSampleEditLayoutBinding, Sample, Sample> implements OnTeboSwitchCheckedChangeListener {

    private AsyncTask onResumeTask;
    private String caseUuid = null;
    private String recordUuid = null;
    private ShipmentStatus pageStatus;
    private Sample record;
    private int mLastCheckedId = -1;

    private List<Item> sampleMaterialList;
    private List<Item> testTypeList;
    private List<Facility> labList;

    private OnLinkClickListener referralLinkCallback;
    private SampleTest mostRecentTest;
    private IEntryItemOnClickListener onRecentTestItemClickListener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (ShipmentStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.heading_level4_1_case_sample_info);
    }

    @Override
    public Sample getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            SampleTest sampleTest = null;
            Sample sample = getActivityRootData();

            if (sample != null) {
                if (sample.isUnreadOrChildUnread())
                    DatabaseHelper.getSampleDao().markAsRead(sample);

                sampleTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);
            }

            resultHolder.forItem().add(sample);
            resultHolder.forItem().add(sampleTest);

            resultHolder.forOther().add(DataUtils.getEnumItems(SampleMaterial.class, false));
            resultHolder.forOther().add(DataUtils.getEnumItems(SampleTestType.class, false));

            resultHolder.forList().add(DatabaseHelper.getFacilityDao().getLaboratories());
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator listIterator = resultHolder.forList().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record =  itemIterator.next();

            if (record == null)
                getActivity().finish();

            if (itemIterator.hasNext())
                mostRecentTest = itemIterator.next();

            if (listIterator.hasNext())
                labList =  listIterator.next();

            if (otherIterator.hasNext())
                sampleMaterialList =  otherIterator.next();

            if (otherIterator.hasNext())
                testTypeList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentSampleEditLayoutBinding contentBinding) {
        if (!record.isShipped()) {
            contentBinding.dtpShipmentDate.setVisibility(View.GONE);
            contentBinding.txtShipmentDetails.setVisibility(View.GONE);
        }

        if (record.getSampleMaterial() == SampleMaterial.OTHER) {
            contentBinding.txtOtherSample.setVisibility(View.INVISIBLE);
        }

        //TODO: Add sample sample source
        if (record.getAssociatedCase().getDisease() != Disease.AVIAN_INFLUENCA) {
            //contentBinding.sampleSampleSource.setVisibility(View.GONE);
        }

        //TODO: Add sampleReceivedLayout
        if (recordUuid != null) {
            if (record.isReceived()) {
                //contentBinding.sampleReceivedLayout.setVisibility(View.VISIBLE);
            }
        }

        if (recordUuid != null) {
            if (record.getSpecimenCondition() != SpecimenCondition.NOT_ADEQUATE) {
                contentBinding.recentTestLayout.setVisibility(View.VISIBLE);
                if (mostRecentTest != null) {
                    contentBinding.spnTestType.setVisibility(View.VISIBLE);
                    //contentBinding.sampleTestResult.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.sampleNoRecentTestText.setVisibility(View.VISIBLE);
                }
            }
        }

        if (recordUuid != null) {
            if (record.getReferredTo() != null) {
                final Sample referredSample = record.getReferredTo();
                contentBinding.txtReferredTo.setVisibility(View.VISIBLE);
                contentBinding.txtReferredTo.setValue(getActivity().getResources().getString(R.string.sample_referred_to) + " " + referredSample.getLab().toString() + " " + "\u279D");
                //contentBinding.txtReferredTo.setPaintFlags(record.sampleReferredTo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                //contentBinding.txtReferredTo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        }

        //TODO: Set required hints for sample data
        //SampleValidator.setRequiredHintsForSampleData(contentBinding);

        if (recordUuid != null) {
            if (!ConfigProvider.getUser().getUuid().equals(record.getReportingUser().getUuid())) {
                contentBinding.txtSampleCode.setEnabled(false);
                contentBinding.dtpDateAndTimeOfSampling.setEnabled(false);
                contentBinding.spnSampleMaterial.setEnabled(false);
                contentBinding.txtOtherSample.setEnabled(false);
                contentBinding.spnTestType.setEnabled(false);
                contentBinding.spnLaboratory.setEnabled(false);
                contentBinding.swhShipped.setEnabled(false);
                contentBinding.dtpShipmentDate.setEnabled(false);
                contentBinding.txtShipmentDetails.setEnabled(false);
            }
        }


        contentBinding.setData(record);
        contentBinding.setCaze(record.getAssociatedCase());
        contentBinding.setLab(record.getLab());
        contentBinding.setResults(getTestResults());
        contentBinding.setRecentTestItemClickCallback(onRecentTestItemClickListener);

        contentBinding.setYesNoClass(YesNo.class);
        contentBinding.setShippedYesCallback(this);
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
                SampleMaterial material = (SampleMaterial)value;

                if (material == SampleMaterial.OTHER) {
                    contentBinding.txtOtherSample.setVisibility(View.VISIBLE);
                } else {
                    contentBinding.txtOtherSample.setVisibility(View.INVISIBLE);
                    contentBinding.txtOtherSample.setValue("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contentBinding.spnLaboratory.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
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
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.dtpDateAndTimeOfSampling.initialize(getFragmentManager());
        contentBinding.dtpShipmentDate.initialize(getFragmentManager());
    }

    @Override
    protected void updateUI(FragmentSampleEditLayoutBinding contentBinding, Sample sample) {

    }

    @Override
    public void onPageResume(FragmentSampleEditLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    SampleTest sampleTest = null;
                    Sample sample = getActivityRootData();

                    if (sample != null) {
                        if (sample.isUnreadOrChildUnread())
                            DatabaseHelper.getSampleDao().markAsRead(sample);

                        sampleTest = DatabaseHelper.getSampleTestDao().queryMostRecentBySample(sample);
                    }

                    resultHolder.forItem().add(sample);
                    resultHolder.forItem().add(sampleTest);
                }
            });
            onResumeTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();

                    if (itemIterator.hasNext())
                        record = itemIterator.next();

                    if (itemIterator.hasNext())
                        mostRecentTest = itemIterator.next();

                    if (record != null)
                        requestLayoutRebind();
                    else {
                        getActivity().finish();
                    }
                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_sample_edit_layout;
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
                String sampleMaterial = record.getSampleMaterialText();

                SampleFormNavigationCapsule dataCapsule = (SampleFormNavigationCapsule)new SampleFormNavigationCapsule(getContext(),
                        record.getUuid(), pageStatus)
                        .setSampleMaterial(sampleMaterial);
                SampleEditActivity.goToActivity(getActivity(), dataCapsule);
            }
        };
    }

    private ObservableArrayList getTestResults() {
        ObservableArrayList results = new ObservableArrayList();

        if (mostRecentTest != null)
            results.add(mostRecentTest);

        return results;
    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public static CaseEditSampleInfoFragment newInstance(IActivityCommunicator activityCommunicator, SampleFormNavigationCapsule capsule, Sample activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, CaseEditSampleInfoFragment.class, capsule, activityRootData);
    }

    @Override
    public void onCheckedChanged(TeboSwitch teboSwitch, Object checkedItem, int checkedId) {
        if (checkedId < 0)
            return;

        if (mLastCheckedId == checkedId) {
            return;
        }

        mLastCheckedId = checkedId;

        YesNo result = ((YesNo)checkedItem);

        if (result == YesNo.YES) {
            getContentBinding().dtpShipmentDate.setVisibility(View.VISIBLE);
            getContentBinding().txtShipmentDetails.setVisibility(View.VISIBLE);
            getContentBinding().divShippingStatusTop.setVisibility(View.VISIBLE);
            getContentBinding().divShippingStatusBottom.setVisibility(View.VISIBLE);
        } else {
            getContentBinding().dtpShipmentDate.setVisibility(View.GONE);
            getContentBinding().txtShipmentDetails.setVisibility(View.GONE);
            getContentBinding().divShippingStatusTop.setVisibility(View.GONE);
            getContentBinding().divShippingStatusBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
