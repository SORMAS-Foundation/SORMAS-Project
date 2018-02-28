package de.symeda.sormas.app;

import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import de.symeda.sormas.app.component.OnHideInputErrorListener;
import de.symeda.sormas.app.component.OnShowInputErrorListener;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.INotificationCommunicator;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationType;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

/**
 * Created by Orson on 22/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class BaseEditActivityFragment<TBinding extends ViewDataBinding> extends Fragment
        implements OnShowInputErrorListener, OnHideInputErrorListener {

    public static final String TAG = BaseEditActivityFragment.class.getSimpleName();

    private BaseEditActivity baseEditActivity;
    private IUpdateSubHeadingTitle communicator;
    private INotificationCommunicator notificationCommunicator;
    private TBinding contentViewStubBinding;
    private ViewDataBinding rootBinding; //FragmentRootEditLayoutBinding

    private View contentViewStubRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof BaseEditActivity) {
            this.baseEditActivity = (BaseEditActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The list activity for fragment must implement BaseEditActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.communicator = (IUpdateSubHeadingTitle) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; "
                    + "implement IUpdateSubHeadingTitle");
        }

        if (getActivity() instanceof INotificationCommunicator) {
            this.notificationCommunicator = (INotificationCommunicator) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support showNotification; "
                    + "implement INotificationCommunicator");
        }


        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootEditLayout(), container, false);
        View rootView = rootBinding.getRoot();

        /*
        Content View
         */

        if (getEditLayout() > 0) {

            ViewStub vsChildFragmentFrame = (ViewStub)rootView.findViewById(R.id.vsChildFragmentFrame);
            //ViewStubProxy vsChildFragmentFrameProxy = new ViewStubProxy(vsChildFragmentFrame);

            //rootBinding.vsChildFragmentFrame
            vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    contentViewStubBinding = DataBindingUtil.bind(inflated);
                    String layoutName = getResources().getResourceEntryName(getEditLayout());
                    setRootNotificationBindingVariable(contentViewStubBinding, layoutName);
                    onLayoutBinding(stub, inflated, contentViewStubBinding);
                    contentViewStubRoot = contentViewStubBinding.getRoot();

                    if (includeFabNonOverlapPadding()) {
                        int lp = contentViewStubRoot.getPaddingLeft();
                        int rp = contentViewStubRoot.getPaddingRight();
                        int tp = contentViewStubRoot.getPaddingTop();
                        int bp = (int)getResources().getDimension(R.dimen.fabNonOverlapPaddingBottom);

                        contentViewStubRoot.setPadding(lp, tp, rp, bp);
                    }
                }
            });

            //ViewStub dialogContent = rootBinding.vsChildFragmentFrame.getViewStub();
            //dialogContent.setLayoutResource(getEditLayout());
            vsChildFragmentFrame.setLayoutResource(getEditLayout());

            //Before
            onBeforeLayoutBinding(savedInstanceState);

            //View dialogContentInflated = dialogContent.inflate();
            View dialogContentInflated = vsChildFragmentFrame.inflate();

            contentViewStubBinding.addOnRebindCallback(new OnRebindCallback() {
                @Override
                public void onBound(ViewDataBinding binding) {
                    super.onBound(binding);

                    //After
                    onAfterLayoutBinding(contentViewStubBinding);


                }
            });

        } else {
            throw new ExceptionInInitializerError("Child layout not specified");

        }

        return rootView;



        /*ViewGroup fragmentRoot = (ViewGroup)LayoutInflater.from(getContext()).inflate(getRootEditLayout(),
                container, false);

        childFragmentFrame = (ViewGroup)fragmentRoot.findViewById(R.id.childFragmentFrame);

        //Hooks
        //--------------------------------------------------------------------------------------
        onBeforeLayoutBinding(inflater, childFragmentFrame, savedInstanceState);
        binding = onLayoutBinding(inflater, childFragmentFrame, savedInstanceState, getEditLayout());
        //--------------------------------------------------------------------------------------

        binding.setVariable(BR.showNotificationCallback, this);
        binding.setVariable(BR.hideNotificationCallback, this);

        onAfterLayoutBinding(binding);
        //onAfterLayoutBinding(binding);

        return fragmentRoot;*/
    }

    protected void setRootNotificationBindingVariable(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.showNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.hideNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //getCommunicator().updateSubHeadingTitle(getSubHeadingTitle());

        /*final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, refreshLayout, null);
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


    public int getRootEditLayout() {
        return R.layout.fragment_root_edit_layout;
    }

    public abstract int getEditLayout();

    public boolean includeFabNonOverlapPadding() {
        return true;
    }

    public IUpdateSubHeadingTitle getCommunicator() {
        return this.communicator;
    }

    public BaseEditActivity getBaseEditActivity() {
        return this.baseEditActivity;
    }

    protected abstract String getSubHeadingTitle();

    public abstract AbstractDomainObject getData();

    public ViewDataBinding getRootBinding() {
        return rootBinding;
    }

    public TBinding getContentBinding() {
        return contentViewStubBinding;
    }

    public abstract void onBeforeLayoutBinding(Bundle savedInstanceState);

    public abstract void onLayoutBinding(ViewStub stub, View inflated, TBinding contentBinding);

    public abstract void onAfterLayoutBinding(TBinding binding);

    //public abstract View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected static <TFragment extends BaseEditActivityFragment, TCapsule extends INavigationCapsule> TFragment newInstance(Class<TFragment> f, TCapsule dataCapsule) throws IllegalAccessException, java.lang.InstantiationException {
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

    protected <E extends AbstractDomainObject> E getPageRecordArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_PAGE_RECORD)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_RECORD);
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

    protected <E extends AbstractDomainObject> void SavePageRecord(Bundle outState, E record) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_PAGE_RECORD, record);
        }
    }

    protected void SaveRecordUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_DATA_UUID, recordUuid);
        }
    }

    @Override
    public void onInputErrorShowing(View v, String message, boolean errorState) {
        notificationCommunicator.showNotification(NotificationType.ERROR, message);
    }

    @Override
    public void onInputErrorHiding(View v, boolean errorState) {
        notificationCommunicator.hideNotification();
    }

    public boolean showSaveAction() {
        return true;
    }

    public boolean showAddAction() {
        return false;
    }
}
