package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import de.symeda.sormas.app.core.IActivityCommunicator;
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

/**
 * Created by Orson on 02/12/2017.
 */

public abstract class BaseListActivityFragment<TListAdapter extends RecyclerView.Adapter> extends BaseFragment implements IListActivityAdapterDataObserverCommunicator, OnListItemClickListener {

    private BaseListActivity baseListActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private TListAdapter adapter;
    private ProgressBar preloader;
    private IActivityCommunicator activityCommunicator;

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


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

    public IUpdateSubHeadingTitle getSubHeadingHandler() {
        return this.subHeadingHandler;
    }

    public IActivityCommunicator getActivityCommunicator() {
        return this.activityCommunicator;
    }


    public BaseListActivity getBaseListActivity() {
        return this.baseListActivity;
    }


    public abstract void cancelTaskExec();





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

    protected <E extends Enum<E>> E getArgByElaboratorKey(Bundle arguments, String key) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(key)) {
                e = (E) arguments.getSerializable(key);
            }
        }

        return e;
    }



    protected void SaveSearchStrategyState(Bundle outState, SearchBy status) {
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

    protected void setActivityCommunicator(IActivityCommunicator activityCommunicator) {
        this.activityCommunicator = activityCommunicator;
    }

    protected static <TFragment extends BaseListActivityFragment, TCapsule extends IListNavigationCapsule> TFragment newInstance(IActivityCommunicator activityCommunicator, Class<TFragment> f, TCapsule dataCapsule) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();

        fragment.setActivityCommunicator(activityCommunicator);

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        SearchBy searchBy = dataCapsule.getSearchStrategy();

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, dataCapsule.getFilterStatus().getValue());

        bundle.putSerializable(ConstantHelper.ARG_SEARCH_STRATEGY, searchBy);

        fragment.setArguments(bundle);
        return fragment;
    }

    public boolean showNewAction() {
        return false;
    }
}
