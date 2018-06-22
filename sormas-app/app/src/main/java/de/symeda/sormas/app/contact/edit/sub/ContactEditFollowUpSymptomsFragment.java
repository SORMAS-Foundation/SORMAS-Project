package de.symeda.sormas.app.contact.edit.sub;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.TeboSpinner;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnRecyclerViewReadyListener;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.shared.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.symptom.OnSymptomStateChangeListener;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.symptom.SymptomFormListAdapter;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Orson on 13/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class ContactEditFollowUpSymptomsFragment extends BaseEditActivityFragment<FragmentContactEditSymptomsInfoLayoutBinding, Visit, Visit> {

    private static final float DEFAULT_BODY_TEMPERATURE = 37.0f;
    private AsyncTask onResumeTask;
    private String recordUuid;
    private VisitStatus pageStatus;
    private Visit record;
    private Symptoms symptom;
    private SymptomFormListAdapter symptomAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<Item> bodyTempList;
    private List<Item> tempSourceList;
    private List<Symptom> symptomList;
    private List<Symptom> yesSymptomList;

    private OnRecyclerViewReadyListener mOnRecyclerViewReadyListener;
    private IEntryItemOnClickListener clearAllCallback;
    private IEntryItemOnClickListener setAllToNoCallback;
    private IEntryItemOnClickListener onAddressLinkClickedCallback;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        savePageStatusState(outState, pageStatus);
        saveRecordUuidState(outState, recordUuid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();

        recordUuid = getRecordUuidArg(arguments);
        pageStatus = (VisitStatus) getPageStatusArg(arguments);
    }

    @Override
    protected String getSubHeadingTitle() {
        Resources r = getResources();
        return r.getString(R.string.caption_symptom_information);
    }

    @Override
    public Visit getPrimaryData() {
        if (symptom != null)
            record.setSymptoms(symptom);

        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Symptoms _symptom = null;
            Visit visit = getActivityRootData();

            if (visit != null) {
                if (visit.isUnreadOrChildUnread())
                    DatabaseHelper.getVisitDao().markAsRead(visit);

                //symptom = DatabaseHelper.getSymptomsDao().queryUuid(visit.getSymptoms().getUuid());
                _symptom = visit.getSymptoms();
            } else {
                _symptom = DatabaseHelper.getSymptomsDao().build();
            }

            resultHolder.forItem().add(visit);
            resultHolder.forItem().add(_symptom); //TODO: Do we need this

            resultHolder.forOther().add(Symptom.makeSymptoms(visit.getDisease()).loadState(symptom));
            resultHolder.forOther().add(getTemperatures(false));
            resultHolder.forOther().add(DataUtils.getEnumItems(TemperatureSource.class, false));
            resultHolder.forOther().add(new ArrayList<Symptom>());
        } else {
            ITaskResultHolderIterator itemIterator = resultHolder.forItem().iterator();
            ITaskResultHolderIterator otherIterator = resultHolder.forOther().iterator();

            //Item Data
            if (itemIterator.hasNext())
                record = itemIterator.next();

            if (itemIterator.hasNext())
                symptom = itemIterator.next();

            if (otherIterator.hasNext())
                symptomList =  otherIterator.next();

            if (otherIterator.hasNext())
                bodyTempList =  otherIterator.next();

            if (otherIterator.hasNext())
                tempSourceList =  otherIterator.next();

            if (otherIterator.hasNext())
                yesSymptomList =  otherIterator.next();

            setupCallback();
        }

        return true;
    }

    @Override
    public void onLayoutBinding(FragmentContactEditSymptomsInfoLayoutBinding contentBinding) {
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        symptomAdapter = new SymptomFormListAdapter(this.getActivity(), (NotificationContext)getActivity(), R.layout.row_edit_symptom_list_item_layout, symptomList, getFragmentManager());
        symptomAdapter.setOnSymptomStateChangeListener(new OnSymptomStateChangeListener() {
            @Override
            public void onChange(final Symptom symptom, SymptomState state) {
                if (state == SymptomState.YES) {
                    yesSymptomList.add(symptom);
                }

                if (state == SymptomState.NO || state == SymptomState.UNKNOWN) {
                    Symptom result = findSymptom(yesSymptomList, symptom);

                    if (result == null)
                        return;

                    yesSymptomList.remove(result);
                }

                getContentBinding().spnFirstSymptoms.notifyDataChanged();
            }
        });


        contentBinding.recyclerViewForList.setAdapter(symptomAdapter);
        contentBinding.recyclerViewForList.setLayoutManager(linearLayoutManager);
        contentBinding.recyclerViewForList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                executeRecyclerViewReadyCallback();
            }
        });

        symptomAdapter.notifyDataSetChanged();

        //For Visit
        contentBinding.txtSymptomaticLocation.setVisibility(View.GONE);

        contentBinding.setData(symptom);
        contentBinding.setClearAllCallback(clearAllCallback);
        contentBinding.setSetAllToNoCallback(setAllToNoCallback);
        contentBinding.setAddressLinkCallback(onAddressLinkClickedCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditSymptomsInfoLayoutBinding contentBinding) {
        contentBinding.dtpSymptomOnset.initialize(getFragmentManager());

        contentBinding.spnBodyTemperature.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return DEFAULT_BODY_TEMPERATURE;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return bodyTempList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnBodyTemperatureSource.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (tempSourceList.size() > 0) ? DataUtils.addEmptyItem(tempSourceList)
                        : tempSourceList;
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });

        contentBinding.spnFirstSymptoms.initialize(new TeboSpinner.ISpinnerInitSimpleConfig() {
            @Override
            public Object getSelectedValue() {
                return null;
            }

            @Override
            public List<Item> getDataSource(Object parentValue) {
                return (yesSymptomList.size() > 0) ? DataUtils.toItems(yesSymptomList)
                        : DataUtils.toItems(yesSymptomList, false);
            }

            @Override
            public VisualState getInitVisualState() {
                return null;
            }
        });
    }

    @Override
    protected void updateUI(FragmentContactEditSymptomsInfoLayoutBinding contentBinding, Visit visit) {

    }

    @Override
    public void onPageResume(FragmentContactEditSymptomsInfoLayoutBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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
                    Symptoms _symptom = null;
                    Visit visit = getActivityRootData();

                    if (visit != null) {
                        if (visit.isUnreadOrChildUnread())
                            DatabaseHelper.getVisitDao().markAsRead(visit);

                        //symptom = DatabaseHelper.getSymptomsDao().queryUuid(visit.getSymptoms().getUuid());
                        _symptom = visit.getSymptoms();
                    } else {
                        _symptom = DatabaseHelper.getSymptomsDao().build();
                    }

                    resultHolder.forItem().add(visit);
                    resultHolder.forItem().add(_symptom); //TODO: Do we need this
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
                        symptom = itemIterator.next();

                    if (record != null && symptom != null)
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
        return R.layout.fragment_contact_edit_symptoms_info_layout;
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

    private void setupCallback() {

        this.mOnRecyclerViewReadyListener = new OnRecyclerViewReadyListener() {
            @Override
            public void onLayoutReady() {
                //getContentBinding().spnFirstSymptoms.reload();
                getContentBinding().spnFirstSymptoms.notifyDataChanged();
            }
        };

        clearAllCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                //Toast.makeText(getContext(), "Clear All", Toast.LENGTH_SHORT).show();
                for (Symptom symptom: symptomList) {
                    symptom.setState(SymptomState.UNKNOWN);
                }

                symptomAdapter.notifyDataSetChanged();
            }
        };

        setAllToNoCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                //Toast.makeText(getContext(), "Set All to No", Toast.LENGTH_SHORT).show();
                for (Symptom symptom: symptomList) {
                    symptom.setState(SymptomState.NO);
                }

                symptomAdapter.notifyDataSetChanged();
            }
        };

        onAddressLinkClickedCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                final Location location = new Location();
                final LocationDialog locationDialog = new LocationDialog(AbstractSormasActivity.getActiveActivity(), location);

                locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                    @Override
                    public void onOkClick(View v, Object item, View viewRoot) {
                        getContentBinding().txtSymptomaticLocation.setValue(location.toString());
                        symptom.setPatientIllLocation(location.toString());

                        locationDialog.dismiss();
                    }
                });

                locationDialog.show(null);
            }
        };
    }

    private void executeRecyclerViewReadyCallback() {
        if (mOnRecyclerViewReadyListener != null) {
            mOnRecyclerViewReadyListener.onLayoutReady();
        }
        mOnRecyclerViewReadyListener = null;
    }

    private Symptom findSymptom(List<Symptom> list, Symptom symptom) {
        for (Symptom s: list) {
            if (s.getName() == symptom.getName())
                return s;
        }

        return null;
    }

    private List<Item> getTemperatures(boolean withNull) {
        List<Item> temperature = new ArrayList<>();

        if (withNull)
            temperature.add(new Item("",null));

        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue),temperatureValue));
        }

        return temperature;
    }

    public static ContactEditFollowUpSymptomsFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormFollowUpNavigationCapsule capsule, Visit activityRootData)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactEditFollowUpSymptomsFragment.class, capsule, activityRootData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (onResumeTask != null && !onResumeTask.isCancelled())
            onResumeTask.cancel(true);
    }
}
