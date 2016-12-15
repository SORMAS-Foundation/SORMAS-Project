package de.symeda.sormas.app.caze;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin on 13.08.2016.
 */
public class CasesListFragment extends ListFragment {

    public static final String ARG_FILTER_STATUS = "filterStatus";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cases_list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Case> cases;
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_FILTER_STATUS)) {
            InvestigationStatus filterStatus = (InvestigationStatus)arguments.getSerializable(ARG_FILTER_STATUS);
            cases = DatabaseHelper.getCaseDao().queryForEq(Case.INVESTIGATION_STATUS, filterStatus);
        } else {
            cases = DatabaseHelper.getCaseDao().queryForAll();
        }

        ArrayAdapter<Case> listAdapter = (ArrayAdapter<Case>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(cases);
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
        startActivity(intent);
    }
}
