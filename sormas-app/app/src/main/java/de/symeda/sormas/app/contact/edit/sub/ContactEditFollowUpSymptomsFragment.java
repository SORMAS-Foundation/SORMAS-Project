package de.symeda.sormas.app.contact.edit.sub;

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
import de.symeda.sormas.app.BaseEditActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.TeboSpinner;
import de.symeda.sormas.app.contact.ContactFormFollowUpNavigationCapsule;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.OnRecyclerViewReadyListener;
import de.symeda.sormas.app.core.async.ITaskResultHolderIterator;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.databinding.FragmentContactEditSymptomsInfoLayoutBinding;
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

public class ContactEditFollowUpSymptomsFragment extends BaseEditActivityFragment<FragmentContactEditSymptomsInfoLayoutBinding, Symptoms> {

    private static final float DEFAULT_BODY_TEMPERATURE = 37.0f;
    private String recordUuid;
    private VisitStatus pageStatus;
    private Symptoms record;
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

        SavePageStatusState(outState, pageStatus);
        SaveRecordUuidState(outState, recordUuid);
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
        return null;
    }

    @Override
    public Symptoms getPrimaryData() {
        return record;
    }

    @Override
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        if (!executionComplete) {
            Visit visit = null;
            Symptoms symptom = null;
            if (recordUuid == null || recordUuid.isEmpty()) {
                visit = DatabaseHelper.getVisitDao().build();
                symptom = DatabaseHelper.getSymptomsDao().build();
            } else {
                visit = DatabaseHelper.getVisitDao().queryUuid(recordUuid);
                if (visit != null) {
                    //symptom = DatabaseHelper.getSymptomsDao().queryUuid(visit.getSymptoms().getUuid());
                    symptom = visit.getSymptoms();
                }
            }

            //resultHolder.forItem().add(visit);
            resultHolder.forItem().add(symptom); //TODO: Do we need this

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
        symptomAdapter = new SymptomFormListAdapter(this.getActivity(), R.layout.row_edit_symptom_list_item_layout, symptomList);
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


        contentBinding.setData(record);
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
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_symptoms_info_layout;
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

    public static ContactEditFollowUpSymptomsFragment newInstance(IActivityCommunicator activityCommunicator, ContactFormFollowUpNavigationCapsule capsule)
            throws java.lang.InstantiationException, IllegalAccessException {
        return newInstance(activityCommunicator, ContactEditFollowUpSymptomsFragment.class, capsule);
    }
}
