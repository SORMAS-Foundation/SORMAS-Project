package de.symeda.sormas.app.hospitalization;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.caze.CasesListArrayAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.databinding.CaseHospitalizationFragmentLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

public class HospitalizationTab extends FormTab {

    public static final String KEY_CASE_UUID = "caseUuid";
    private ArrayAdapter adapter;

    private CaseHospitalizationFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.case_hospitalization_fragment_layout, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new PreviousHospitalizationsListArrayAdapter(
                this.getActivity(),
                R.layout.previous_hospitalizations_list_item);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                PreviousHospitalization previousHospitalization = (PreviousHospitalization)getListAdapter().getItem(position);
                //showCaseEditView(caze);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        try {

            final String caseUuid = getArguments().getString(HospitalizationTab.KEY_CASE_UUID);
            final Case caze = DatabaseHelper.getCaseDao().queryUuid(caseUuid);
            if(caze.getHealthFacility()!=null){
                ((LabelField) getView().findViewById(R.id.hospitalization_healthFacility)).setValue(caze.getHealthFacility().toString());
            }


            final String hospitalizationUuid = getArguments().getString(Hospitalization.UUID);
            if(hospitalizationUuid != null) {
                final Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(hospitalizationUuid);
                binding.setHospitalization(hospitalization);

                List<PreviousHospitalization> previousHospitalizations = DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization);
                ArrayAdapter<PreviousHospitalization> listAdapter = (ArrayAdapter<PreviousHospitalization>)getListAdapter();
                listAdapter.clear();
                listAdapter.addAll(previousHospitalizations);


            } else {
                // TODO: check if it ok this way
                binding.setHospitalization(new Hospitalization());
            }

            getListView().setAdapter(getListAdapter());
            setListViewHeightBasedOnChildren(getListView());


            binding.hospitalizationAdmissionDate.initialize(this);
            binding.hospitalizationDischargeDate.initialize(this);
            binding.hospitalization1isolationDate.initialize(this);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            View childView = listView.getAdapter().getView(i, null, listView);
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight+= childView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * listAdapter.getCount());
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public AbstractDomainObject getData() {
        return binding.getHospitalization();
    }

    private ListView getListView() {
        return (ListView)getView().findViewById(R.id.hospitalization_prevHospitalizations_list);
    }

    public ArrayAdapter getListAdapter() {
        return adapter;
    }
}
