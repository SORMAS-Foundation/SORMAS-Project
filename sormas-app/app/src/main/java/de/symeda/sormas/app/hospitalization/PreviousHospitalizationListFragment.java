package de.symeda.sormas.app.hospitalization;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.caze.CaseEditActivity;
import de.symeda.sormas.app.caze.CasesListArrayAdapter;
import de.symeda.sormas.app.caze.SyncCasesTask;

/**
 * Created by Martin on 13.08.2016.
 */
public class PreviousHospitalizationListFragment extends ListFragment {

    public static final String KEY_HOSPITALIZATION = "hospitalizationUuid";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            List<PreviousHospitalization> previousHospitalizations = new ArrayList<>();
            Bundle arguments = getArguments();
            if (arguments.containsKey(KEY_HOSPITALIZATION)) {
                Hospitalization hospitalization = DatabaseHelper.getHospitalizationDao().queryUuid(arguments.getString(KEY_HOSPITALIZATION));

                    previousHospitalizations = DatabaseHelper.getPreviousHospitalizationDao().getByHospitalization(hospitalization);

            }

            ArrayAdapter<PreviousHospitalization> listAdapter = (ArrayAdapter<PreviousHospitalization>)getListAdapter();
            listAdapter.clear();
            listAdapter.addAll(previousHospitalizations);

            final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
            if(refreshLayout != null) {
                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        SyncCasesTask.syncCases(getActivity().getSupportFragmentManager(), refreshLayout);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviousHospitalizationsListArrayAdapter adapter = new PreviousHospitalizationsListArrayAdapter(
                this.getActivity(),
                R.layout.cases_list_item);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Case caze = (Case)getListAdapter().getItem(position);
                showCaseEditView(caze);
            }
        });
    }

    public void showCaseEditView(Case caze) {
        Intent intent = new Intent(getActivity(), CaseEditActivity.class);
        intent.putExtra(CaseEditActivity.KEY_CASE_UUID, caze.getUuid());
        startActivity(intent);
    }
}
