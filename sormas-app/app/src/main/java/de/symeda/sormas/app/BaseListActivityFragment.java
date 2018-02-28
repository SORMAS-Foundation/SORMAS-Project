package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.core.IListActivityAdapterDataObserverCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.ListAdapterDataObserver;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.SearchStrategy;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.ConstantHelper;

/**
 * Created by Orson on 02/12/2017.
 */

public abstract class BaseListActivityFragment<TListAdapter extends RecyclerView.Adapter> extends Fragment implements IListActivityAdapterDataObserverCommunicator, OnListItemClickListener {

    private BaseListActivity baseListActivity;
    private IUpdateSubHeadingTitle communicator;
    private TListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getRootListLayout(), container, false);

        if (getActivity() instanceof BaseListActivity) {
            this.baseListActivity = (BaseListActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The list activity for fragment must implement BaseListActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.communicator = (IUpdateSubHeadingTitle) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; "
                    + "implement IUpdateSubHeadingTitle");
        }

        this.adapter = getNewListAdapter();
        this.adapter.registerAdapterDataObserver(new ListAdapterDataObserver(this));

        if (this.adapter instanceof ISetOnListItemClickListener) {
            ((ISetOnListItemClickListener)this.adapter).setOnListItemClickListener(this);
        } else {
            throw new NotImplementedException("setOnListItemClickListener is not supported by the adapter; " +
                "implement ISetOnListItemClickListener");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseListActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, refreshLayout, null);
            }
        });
    }

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    public int getRootListLayout() {
        return R.layout.fragment_root_list_layout;
    }

    public abstract TListAdapter getNewListAdapter();

    public abstract void onListItemClick(View view, int position, Object item);

    public TListAdapter getListAdapter() {
        return this.adapter;
    }

    @Override
    public int getListAdapterSize() {
        return this.adapter.getItemCount();
    }

    @Override
    public View getEmptyListView() {
        return this.getView().findViewById(R.id.empty_list_hint);
    }

    @Override
    public View getListView() {
        return this.getView().findViewById(R.id.swiperefresh);
    }

    public IUpdateSubHeadingTitle getCommunicator() {
        return this.communicator;
    }

    public BaseListActivity getBaseListActivity() {
        return this.baseListActivity;
    }






    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected SearchStrategy getSearchStrategyArg(Bundle arguments) {
        SearchStrategy e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_SEARCH_STRATEGY)) {
                e = (SearchStrategy) arguments.getSerializable(ConstantHelper.ARG_SEARCH_STRATEGY);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getPageStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_PAGE_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getArgByElaboratorKey(Bundle arguments, String key) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(key)) {
                e = (E) arguments.getSerializable(key);
            }
        }

        return e;
    }



    protected void SaveSearchStrategyState(Bundle outState, SearchStrategy status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected <E extends Enum<E>> void SaveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected <E extends Enum<E>> void SavePageStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_STATUS, status);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected static <TFragment extends BaseListActivityFragment, TCapsule extends IListNavigationCapsule> TFragment newInstance(Class<TFragment> f, TCapsule dataCapsule) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        SearchStrategy searchStrategy = dataCapsule.getSearchStrategy();

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, dataCapsule.getFilterStatus().getValue());

        bundle.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, searchStrategy);

        fragment.setArguments(bundle);
        return fragment;
    }
}
