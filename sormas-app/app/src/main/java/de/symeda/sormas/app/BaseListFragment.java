package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.symeda.sormas.app.core.IListActivityAdapterDataObserverCommunicator;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.ListAdapterDataObserver;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

public abstract class BaseListFragment<TListAdapter extends RecyclerView.Adapter> extends BaseFragment implements IListActivityAdapterDataObserverCommunicator, OnListItemClickListener {

    private BaseListActivity baseListActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private TListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getRootListLayout(), container, false);

        if (getActivity() instanceof BaseListActivity) {
            this.baseListActivity = (BaseListActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The list activity for fragment must implement BaseListActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
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

        String format = canAddToList() ? getResources().getString(R.string.hint_no_records_found_add_new) : getResources().getString(R.string.hint_no_records_found);
        getEmptyListView(view).setText(String.format(format, getResources().getString(getEmptyListEntityResId()).toLowerCase()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final SwipeRefreshLayout swiperefresh = (SwipeRefreshLayout) this.getView().findViewById(R.id.swiperefresh);
        if (swiperefresh != null) {
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBaseActivity().synchronizeChangedData();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public TextView getEmptyListView() {
        return getEmptyListView(getView());
    }

    private TextView getEmptyListView(View rootView) {
        return (TextView)rootView.findViewById(R.id.empty_list_hint);
    }

    @Override
    public View getListView() {
        return this.getView().findViewById(R.id.swiperefresh);
    }

    public IUpdateSubHeadingTitle getSubHeadingHandler() {
        return this.subHeadingHandler;
    }

    public abstract void cancelTaskExec();

    protected abstract int getEmptyListEntityResId();

    protected boolean canAddToList() {
        return false;
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

    protected SearchBy getSearchStrategyArg(Bundle arguments) {
        SearchBy e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_SEARCH_STRATEGY)) {
                e = (SearchBy) arguments.getSerializable(ConstantHelper.ARG_SEARCH_STRATEGY);
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

    protected void saveSearchStrategyState(Bundle outState, SearchBy status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, status);
        }
    }

    protected <E extends Enum<E>> void saveFilterStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_FILTER_STATUS, status);
        }
    }

    protected <E extends Enum<E>> void savePageStatusState(Bundle outState, E status) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_STATUS, status);
        }
    }

    protected void saveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    protected static <TFragment extends BaseListFragment, TCapsule extends IListNavigationCapsule> TFragment newInstance(Class<TFragment> f, TCapsule dataCapsule) {
        TFragment fragment;
        try {
            fragment = f.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        SearchBy searchBy = dataCapsule.getSearchStrategy();
        int activeMenuKey = dataCapsule.getActiveMenuKey();

        bundle.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, dataCapsule.getFilterStatus().getValue());

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        bundle.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, searchBy);

        fragment.setArguments(bundle);
        return fragment;
    }
}
