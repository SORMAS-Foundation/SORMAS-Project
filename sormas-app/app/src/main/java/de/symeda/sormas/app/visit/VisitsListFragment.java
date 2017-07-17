package de.symeda.sormas.app.visit;

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

import java.util.List;

import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.AbstractSormasActivity;
import de.symeda.sormas.app.AbstractTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.SyncCallback;

public class VisitsListFragment extends ListFragment {

    public static final String KEY_CONTACT_UUID = "contactUuid";

    private String contactUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_layout, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        contactUuid = getArguments().getString(Contact.UUID);
        final Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        syncVisits(contact);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((AbstractSormasActivity)getActivity()).synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, refreshLayout);
            }
        });

        ((VisitsListArrayAdapter)getListAdapter()).updateUnreadIndicator();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        VisitsListArrayAdapter adapter = new VisitsListArrayAdapter(
                this.getActivity(),             // Context for the activity.
                R.layout.visits_list_item);     // Layout to use (create)

        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View viewClicked,
                    int position, long id) {
                Visit visit = (Visit) getListAdapter().getItem(position);
                showVisitEditView(visit);
            }
        });
    }

    public void showVisitEditView(Visit visit) {
        Intent intent = new Intent(getActivity(), VisitEditActivity.class);
        intent.putExtra(Visit.UUID, visit.getUuid());
        intent.putExtra(KEY_CONTACT_UUID, contactUuid);
        startActivity(intent);
    }

    private void syncVisits(final Contact contact) {
        List<Visit> visits = DatabaseHelper.getVisitDao().getByContact(contact);
        ArrayAdapter<Visit> listAdapter = (ArrayAdapter<Visit>)getListAdapter();
        listAdapter.clear();
        listAdapter.addAll(visits);

        if (listAdapter.getCount() == 0) {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.VISIBLE);
            getView().findViewById(android.R.id.list).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.empty_list_hint).setVisibility(View.GONE);
            getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
        }
    }

}
