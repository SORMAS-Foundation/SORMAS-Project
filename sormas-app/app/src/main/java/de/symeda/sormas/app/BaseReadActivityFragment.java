package de.symeda.sormas.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 11/12/2017.
 */

public abstract class BaseReadActivityFragment<TBinding> extends Fragment {

    private BaseReadActivity baseReadActivity;
    private IUpdateSubHeadingTitle communicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(this.getRootReadLayout(), container, false);
        if (getActivity() instanceof BaseReadActivity) {
            this.baseReadActivity = (BaseReadActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The list activity for fragment must implement BaseReadActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.communicator = (IUpdateSubHeadingTitle) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; "
                    + "implement IUpdateSubHeadingTitle");
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();




        //getCommunicator().updateSubHeadingTitle(getSubHeadingTitle());

        /*final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, refreshLayout, null);
            }
        });*/
    }

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    public abstract int getRootReadLayout();

    public IUpdateSubHeadingTitle getCommunicator() {
        return this.communicator;
    }

    public BaseReadActivity getBaseReadActivity() {
        return this.baseReadActivity;
    }

    protected abstract String getSubHeadingTitle();

    public abstract AbstractDomainObject getData();

    public abstract TBinding getBinding();

    //public abstract View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected static <TFragment extends BaseReadActivityFragment, TCapsule extends INavigationCapsule>
    TFragment newInstance(Class<TFragment> f, TCapsule dataCapsule) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }


        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        //AbstractDomainObject record = dataCapsule.getRecord();

        bundle.putString(ConstantHelper.KEY_DATA_UUID, dataUuid);

        if (filterStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_FILTER_STATUS, dataCapsule.getFilterStatus().getValue());

        if (pageStatus != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_STATUS, pageStatus.getValue());

        /*if (record != null)
            bundle.putSerializable(ConstantHelper.ARG_PAGE_RECORD, record);*/

        /*for (IStatusElaborator e: dataCapsule.getOtherStatus()) {
            if (e != null)
                bundle.putSerializable(e.getStatekey(), e.getValue());
        }*/

        fragment.setArguments(bundle);
        return fragment;
    }

    public abstract Object getRecord();


    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
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

}
