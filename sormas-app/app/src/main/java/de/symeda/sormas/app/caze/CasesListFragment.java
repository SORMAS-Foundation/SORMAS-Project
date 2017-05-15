package de.symeda.sormas.app.caze;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.util.ConnectionHelper;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Martin on 13.08.2016.
 */
public class CasesListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Case> cases;
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_FILTER_STATUS)) {
            InvestigationStatus filterStatus = (InvestigationStatus)arguments.getSerializable(ARG_FILTER_STATUS);
            cases = DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, filterStatus, Case.REPORT_DATE, false);
        } else {
            cases = DatabaseHelper.getCaseDao().queryForAll(Case.REPORT_DATE, false);
        }

        ArrayAdapter<Case> listAdapter = (ArrayAdapter<Case>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(cases);

        if (listAdapter.getCount() == 0) {
            this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
            this.getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        } else {
            this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
            this.getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        }

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionHelper.isConnectedToInternet(getContext())) {
                    SyncCasesTask.syncCasesWithCallback(getContext(), getActivity().getSupportFragmentManager(), new SyncCallback() {
                        @Override
                        public void call(boolean syncFailed) {
                            refreshLayout.setRefreshing(false);
                            if (!syncFailed) {
                                Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_sync_success, Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_sync_error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    refreshLayout.setRefreshing(false);
                    Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CasesListArrayAdapter adapter = new CasesListArrayAdapter(
                this.getActivity(),                       // Context for the activity.
                R.layout.cases_list_item);    // Layout to use (create)

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
        intent.putExtra(CaseEditActivity.CASE_SUBTITLE, caze.toString());
        startActivity(intent);
    }
}
