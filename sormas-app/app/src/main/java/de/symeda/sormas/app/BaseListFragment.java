package de.symeda.sormas.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.symeda.sormas.app.core.IListActivityAdapterDataObserverCommunicator;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.ListAdapterDataObserver;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.adapter.databinding.ISetOnListItemClickListener;
import de.symeda.sormas.app.core.adapter.databinding.OnListItemClickListener;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.util.Bundler;

public abstract class BaseListFragment<TListAdapter extends RecyclerView.Adapter> extends BaseFragment implements IListActivityAdapterDataObserverCommunicator, OnListItemClickListener {

    private AsyncTask jobTask;
    private BaseListActivity baseListActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private TListAdapter adapter;
    private Enum listFilter;

    protected static <TFragment extends BaseListFragment> TFragment newInstance(Class<TFragment> fragmentClass, Bundle data, Enum listFilter) {
        data = new Bundler(data).setListFilter(listFilter).get();
        TFragment fragment = newInstance(fragmentClass, data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) savedInstanceState = getArguments();
        listFilter = new Bundler(savedInstanceState).getListFilter();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        new Bundler(outState).setListFilter(listFilter);
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

        jobTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void doInBackground(final TaskResultHolder resultHolder) {
                prepareFragmentData();
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                getBaseActivity().hidePreloader();
            }
        }.executeOnThreadPool();

        return view;
    }

    protected abstract void prepareFragmentData();

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

        subHeadingHandler.updateSubHeadingTitle();
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

    public Enum getListFilter() { return listFilter; }

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

    protected abstract int getEmptyListEntityResId();

    protected boolean canAddToList() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }
}
