package de.symeda.sormas.app.visit.edit;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.OnRecyclerViewReadyListener;
import de.symeda.sormas.app.databinding.FragmentContactEditSymptomsInfoLayoutBinding;
import de.symeda.sormas.app.shared.VisitFormNavigationCapsule;
import de.symeda.sormas.app.symptom.OnSymptomStateChangeListener;
import de.symeda.sormas.app.symptom.Symptom;
import de.symeda.sormas.app.symptom.SymptomFormListAdapter;
import de.symeda.sormas.app.util.DataUtils;


public class VisitEditSymptomsFragment extends BaseEditFragment<FragmentContactEditSymptomsInfoLayoutBinding, Visit, Visit> {

    private static final float DEFAULT_BODY_TEMPERATURE = 37.0f;
    private Visit record;
    private Symptoms symptoms;
    private SymptomFormListAdapter symptomAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<Item> bodyTempList;
    private List<Item> tempSourceList;
    private List<Symptom> symptomList;
    private List<Symptom> yesSymptomList;

    private OnRecyclerViewReadyListener mOnRecyclerViewReadyListener;
    private IEntryItemOnClickListener clearAllCallback;
    private IEntryItemOnClickListener setAllToNoCallback;

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_symptom_information);
    }

    @Override
    public Visit getPrimaryData() {
        if (symptoms != null)
            record.setSymptoms(symptoms);
        return record;
    }

    @Override
    protected void prepareFragmentData(Bundle savedInstanceState) {
        record = getActivityRootData();
        symptoms = record.getSymptoms();
        symptomList = Symptom.makeSymptoms(record.getDisease()).loadState(symptoms);
        bodyTempList = getTemperatures(false);
        tempSourceList = DataUtils.getEnumItems(TemperatureSource.class, false);
        yesSymptomList = new ArrayList<Symptom>();
    }

    @Override
    public void onLayoutBinding(FragmentContactEditSymptomsInfoLayoutBinding contentBinding) {

        setupCallback();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        symptomAdapter = new SymptomFormListAdapter(this.getActivity(), (NotificationContext) getActivity(), R.layout.row_edit_symptom_list_item_layout, symptomList, getFragmentManager());
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

        contentBinding.setData(symptoms);
        contentBinding.setClearAllCallback(clearAllCallback);
        contentBinding.setSetAllToNoCallback(setAllToNoCallback);
    }

    @Override
    public void onAfterLayoutBinding(FragmentContactEditSymptomsInfoLayoutBinding contentBinding) {
        contentBinding.symptomsOnsetDate.initializeDateField(getFragmentManager());

        contentBinding.symptomsTemperature.initializeSpinner(bodyTempList, DEFAULT_BODY_TEMPERATURE);

        contentBinding.symptomsTemperatureSource.initializeSpinner(tempSourceList);

        contentBinding.symptomsOnsetSymptom.initializeSpinner(DataUtils.toItems(null, true));
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_contact_edit_symptoms_info_layout;
    }

    @Override
    public boolean isShowSaveAction() {
        return true;
    }

    @Override
    public boolean isShowAddAction() {
        return false;
    }

    private void setupCallback() {

        this.mOnRecyclerViewReadyListener = new OnRecyclerViewReadyListener() {
            @Override
            public void onLayoutReady() {
                //getContentBinding().spnFirstSymptoms.reload();
//                getContentBinding().spnFirstSymptoms.notifyDataChanged();
            }
        };

        clearAllCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                //Toast.makeText(getContext(), "Clear All", Toast.LENGTH_SHORT).show();
                for (Symptom symptom : symptomList) {
                    symptom.setState(SymptomState.UNKNOWN);
                }

                symptomAdapter.notifyDataSetChanged();
            }
        };

        setAllToNoCallback = new IEntryItemOnClickListener() {
            @Override
            public void onClick(View v, Object item) {
                //Toast.makeText(getContext(), "Set All to No", Toast.LENGTH_SHORT).show();
                for (Symptom symptom : symptomList) {
                    symptom.setState(SymptomState.NO);
                }

                symptomAdapter.notifyDataSetChanged();
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
        for (Symptom s : list) {
            if (s.getName() == symptom.getName())
                return s;
        }

        return null;
    }

    private List<Item> getTemperatures(boolean withNull) {
        List<Item> temperature = new ArrayList<>();

        if (withNull)
            temperature.add(new Item("", null));

        for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
            temperature.add(new Item(SymptomsHelper.getTemperatureString(temperatureValue), temperatureValue));
        }

        return temperature;
    }

    public static VisitEditSymptomsFragment newInstance(VisitFormNavigationCapsule capsule, Visit activityRootData) {
        return newInstance(VisitEditSymptomsFragment.class, capsule, activityRootData);
    }
}
