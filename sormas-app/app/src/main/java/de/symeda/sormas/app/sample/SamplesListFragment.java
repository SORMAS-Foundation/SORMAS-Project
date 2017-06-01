package de.symeda.sormas.app.sample;

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

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Mate Strysewske on 06.02.2017.
 */

public class SamplesListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";
    public static final String KEY_CASE_UUID = "caseUuid";

    private String parentCaseUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Sample> samples;
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_FILTER_STATUS)) {
            ShipmentStatus filterStatus = (ShipmentStatus)arguments.getSerializable(ARG_FILTER_STATUS);
            samples = DatabaseHelper.getSampleDao().queryForEq(Sample.SHIPMENT_STATUS, filterStatus);
        } else {
            if(arguments.containsKey(KEY_CASE_UUID)) {
                parentCaseUuid = (String) arguments.get(KEY_CASE_UUID);
                final CaseDao caseDao = DatabaseHelper.getCaseDao();
                final Case caze = caseDao.queryUuid(parentCaseUuid);
                if (caze != null) {
                    samples = DatabaseHelper.getSampleDao().queryForCase(caze);
                } else {
                    samples = new ArrayList<>();
                }
            } else {
                samples = DatabaseHelper.getSampleDao().queryForAll();
            }
        }

        ArrayAdapter<Sample> listAdapter = (ArrayAdapter<Sample>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(samples);

        if (parentCaseUuid != null) {
            if (listAdapter.getCount() == 0) {
                this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
                this.getView().findViewById(android.R.id.list).setVisibility(View.GONE);
            } else {
                this.getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
                this.getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
            }
        }

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (RetroProvider.isConnected()) {
                    SyncSamplesTask.syncSamplesWithCallback(getContext(), getActivity().getSupportFragmentManager(), new SyncCallback() {
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

        SamplesListArrayAdapter adapter = new SamplesListArrayAdapter(
                this.getActivity(),
                R.layout.samples_list_item);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sample sample = (Sample)getListAdapter().getItem(position);
                showEditView(sample);
            }
        });
    }

    public void showEditView(Sample sample) {
        Intent intent = new Intent(getActivity(), SampleEditActivity.class);
        intent.putExtra(SampleEditActivity.KEY_SAMPLE_UUID, sample.getUuid());
        startActivity(intent);
    }

}
