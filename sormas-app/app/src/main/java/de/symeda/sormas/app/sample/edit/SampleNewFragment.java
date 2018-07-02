package de.symeda.sormas.app.sample.edit;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleSource;
import de.symeda.sormas.api.sample.SampleTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.OnLinkClickListener;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.controls.ControlSwitchField;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.ISaveable;
import de.symeda.sormas.app.core.YesNo;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.FragmentSampleNewLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.ErrorReportingHelper;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Orson on 29/03/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class SampleNewFragment extends BaseEditActivityFragment<FragmentSampleNewLayoutBinding, Sample, Sample> implements ISaveable {

    private AsyncTask onResumeTask;
    private AsyncTask saveSample;

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
                SampleMaterial material = (SampleMaterial)value;

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

                if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_LABORATORY_UUID)) {
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

    }

    @Override
    protected void updateUI(FragmentSampleNewLayoutBinding contentBinding, Sample sample) {
        contentBinding.spnSampleMaterial.setValue(sample.getSampleMaterial(), true);
        contentBinding.spnTestType.setValue(sample.getSuggestedTypeOfTest(), true);
        contentBinding.spnLaboratory.setValue(sample.getLab(), true);
    }

    @Override
    public void onPageResume(FragmentSampleNewLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
        if (!hasBeforeLayoutBindingAsyncReturn)
            return;

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
                    Sample sample = getActivityRootData();

                    resultHolder.forItem().add(sample);
                }
            };
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
        return R.layout.fragment_sample_new_layout;
    }

    private void setupCallback() {


    }

    @Override
    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    @Override
    public boolean showSaveAction() {
        return true;
    }

    @Override
    public boolean showAddAction() {
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
//            getContentBinding().dtpShipmentDate.setVisibility(View.VISIBLE);
//            getContentBinding().txtShipmentDetails.setVisibility(View.VISIBLE);
//            getContentBinding().sampleReceivedLayout.setVisibility(View.VISIBLE);
//        } else {
//            getContentBinding().dtpShipmentDate.setVisibility(View.GONE);
//            getContentBinding().txtShipmentDetails.setVisibility(View.GONE);
//            getContentBinding().sampleReceivedLayout.setVisibility(View.GONE);
//        }
//    }

    public static SampleNewFragment newInstance(IActivityCommunicator activityCommunicator, SampleFormNavigationCapsule capsule, Sample activityRootData) {
        return newInstance(activityCommunicator, SampleNewFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);

        if (saveSample != null && !saveSample.isCancelled())
            saveSample.cancel(true);
    }

    @Override
    public void save(final NotificationContext nContext) {
        final Sample sampleToSave = getActivityRootData();

        if (sampleToSave == null)
            throw new IllegalArgumentException("sampleToSave is null");

        if (sampleToSave.getReportingUser() == null) {
            sampleToSave.setReportingUser(ConfigProvider.getUser());
        }
        if (sampleToSave.getReportDateTime() == null) {
            sampleToSave.setReportDateTime(new Date());
        }

        // TODO: re-enable validation
//        SampleValidator.clearErrorsForSampleData(getContentBinding());
//        if (!SampleValidator.validateSampleData(nContext, sampleToSave, getContentBinding())) {
//            return;
//        }

        try {
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                private String saveUnsuccessful;

                @Override
                public void onPreExecute() {
                    //getActivityCommunicator().showPreloader();
                    //getActivityCommunicator().hideFragmentView();

                    saveUnsuccessful = String.format(getResources().getString(R.string.snackbar_save_error), getResources().getString(R.string.entity_sample));
                }

                @Override
                public void execute(TaskResultHolder resultHolder) {
                    try {
                        SampleDao sampleDao = DatabaseHelper.getSampleDao();
                        sampleDao.saveAndSnapshot(sampleToSave);
                    } catch (DaoException e) {
                        Log.e(getClass().getName(), "Error while trying to save sample", e);
                        resultHolder.setResultStatus(new BoolResult(false, saveUnsuccessful));
                        ErrorReportingHelper.sendCaughtException(tracker, e, sampleToSave, true);
                    }
                }
            };
            saveSample = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    //getActivityCommunicator().hidePreloader();
                    //getActivityCommunicator().showFragmentView();

                    if (resultHolder == null){
                        return;
                    }

                    if (!resultStatus.isSuccess()) {
                        NotificationHelper.showNotification(nContext, NotificationType.ERROR, resultStatus.getMessage());
                        return;
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, "Sample " + DataHelper.getShortUuid(sampleToSave.getUuid()) + " saved");
                    }

                    if (RetroProvider.isConnected()) {
                        SynchronizeDataAsync.callWithProgressDialog(SynchronizeDataAsync.SyncMode.Changes, getContext(), new SyncCallback() {
                            @Override
                            public void call(boolean syncFailed, String syncFailedMessage) {
                                if (syncFailed) {
                                    NotificationHelper.showNotification(nContext, NotificationType.WARNING, String.format(getResources().getString(R.string.snackbar_sync_error_saved), getResources().getString(R.string.entity_sample)));
                                } else {
                                    NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));
                                }
                                getActivity().finish();
                            }
                        });
                    } else {
                        NotificationHelper.showNotification(nContext, NotificationType.SUCCESS, String.format(getResources().getString(R.string.snackbar_save_success), getResources().getString(R.string.entity_sample)));
                        getActivity().finish();
                    }

                }
            });
        } catch (Exception ex) {
            //getActivityCommunicator().hidePreloader();
            //getActivityCommunicator().showFragmentView();
        }

    }
}
