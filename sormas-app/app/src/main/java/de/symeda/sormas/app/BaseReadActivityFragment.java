package de.symeda.sormas.app;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IActivityRootDataRequestor;
import de.symeda.sormas.app.core.ICallback;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Orson on 11/12/2017.
 */

public abstract class BaseReadActivityFragment<TBinding extends ViewDataBinding, TData, TActivityRootData extends AbstractDomainObject> extends BaseFragment {

    public static final String TAG = BaseReadActivityFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseReadActivity baseReadActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private IActivityCommunicator activityCommunicator;
    private TBinding contentViewStubBinding;
    private ViewDataBinding rootBinding;
    private View contentViewStubRoot;
    private boolean beforeLayoutBindingAsyncReturn;
    private boolean skipAfterLayoutBinding = false;
    private TActivityRootData activityRootData;
    private View rootView;
    private IActivityRootDataRequestor activityRootDataRequestor;
    private int onResumeExecCount = 0;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveUserRightState(outState, editOrCreateUserRight);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();
        editOrCreateUserRight = (UserRight) getUserRightArg(arguments);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        //View view = inflater.inflate(this.getReadLayout(), container, false);
        if (getActivity() instanceof BaseReadActivity) {
            this.baseReadActivity = (BaseReadActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The read activity for fragment must implement BaseReadActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; "
                    + "implement IUpdateSubHeadingTitle");
        }

        if (getActivity() instanceof IActivityRootDataRequestor) {
            this.activityRootDataRequestor = (IActivityRootDataRequestor) this.getActivity();
        } else {
            throw new NotImplementedException("The read activity for fragment must implement IActivityRootDataRequestor");
        }

        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        rootView = rootBinding.getRoot();

        if (getReadLayout() > 0) {
            final ViewStub vsChildFragmentFrame = (ViewStub)rootView.findViewById(R.id.vsChildFragmentFrame);

            vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    //onLayoutBindingHelper(stub, inflated);

                    contentViewStubBinding = DataBindingUtil.bind(inflated);
                    String layoutName = getResources().getResourceEntryName(getReadLayout());
                    setRootNotificationBindingVariable(contentViewStubBinding, layoutName);
                    onLayoutBinding(contentViewStubBinding);
                    contentViewStubRoot = contentViewStubBinding.getRoot();

                    if (includeFabNonOverlapPadding()) {
                        int lp = contentViewStubRoot.getPaddingLeft();
                        int rp = contentViewStubRoot.getPaddingRight();
                        int tp = contentViewStubRoot.getPaddingTop();
                        int bp = (int)getResources().getDimension(R.dimen.fabNonOverlapPaddingBottom);

                        contentViewStubRoot.setPadding(lp, tp, rp, bp);

                        ViewGroup.LayoutParams params = contentViewStubRoot.getLayoutParams();
                        params.height = MATCH_PARENT;
                    }

                    if (makeHeightMatchParent()) {
                        contentViewStubRoot.getLayoutParams().height = MATCH_PARENT;
                    } else {
                        contentViewStubRoot.getLayoutParams().height = WRAP_CONTENT;
                    }
                }
            });

            vsChildFragmentFrame.setLayoutResource(getReadLayout());


            beforeLayoutBindingAsyncReturn = false;
            ITaskExecutor executor = TaskExecutorFor.job(new IJobDefinition() {
                @Override
                public void preExecute(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    getActivityCommunicator().showPreloader();
                    getActivityCommunicator().hideFragmentView();
                }


                @Override
                public void execute(BoolResult resultStatus, final TaskResultHolder resultHolder) {
                    onBeforeLayoutBinding(savedInstanceState, resultHolder, null, false);
                }
            });

            jobTask = executor.execute(new ITaskResultCallback() {
                @Override
                public void taskResult(BoolResult resultStatus, TaskResultHolder resultHolder) {
                    getActivityCommunicator().hidePreloader();
                    getActivityCommunicator().showFragmentView();

                    if (resultHolder == null)
                        return;

                    beforeLayoutBindingAsyncReturn = onBeforeLayoutBinding(savedInstanceState, resultHolder, resultStatus, true);

                    View dialogContentInflated = vsChildFragmentFrame.inflate();

                    contentViewStubBinding.addOnRebindCallback(new OnRebindCallback() {
                        @Override
                        public void onBound(ViewDataBinding binding) {
                            super.onBound(binding);

                            //After
                            //onAfterLayoutBindingHelper(contentViewStubBinding);
                            if (!skipAfterLayoutBinding)
                                onAfterLayoutBinding(contentViewStubBinding);

                            skipAfterLayoutBinding = false;

                            getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
                        }
                    });
                }
            });
        } else {
            throw new ExceptionInInitializerError("Child layout not specified");
        }

        return rootView;
    }

    public void showEmptyListHintWithAdd(List list, int entityNameResId) {
        showEmptyListHint(list, R.string.hint_no_records_found_add_new, entityNameResId);
    }

    public void showEmptyListHint(List list, int entityNameResId) {
        showEmptyListHint(list, R.string.hint_no_records_found, entityNameResId);
    }

    private void showEmptyListHint(List list, int stringFormatResId, int entityNameResId) {
        boolean isListEmpty = false;

        if (rootView == null)
            return;

        TextView emptyListHintView = (TextView)rootView.findViewById(R.id.emptyListHint);

        if (emptyListHintView == null)
            return;

        emptyListHintView.setVisibility(View.GONE);

        if (list == null || list.size() <= 0)
            isListEmpty = true;

        if (!isListEmpty)
            return;

        Resources r = getResources();

        String format = r.getString(stringFormatResId);

        emptyListHintView.setText(String.format(format, r.getString(entityNameResId)));
        emptyListHintView.setVisibility(View.VISIBLE);
    }

    public void requestLayoutRebind() {
        onLayoutBinding(contentViewStubBinding);
    }

    public abstract boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete);

    public abstract void onLayoutBinding(TBinding contentBinding);

    public abstract void onAfterLayoutBinding(TBinding contentBinding);

    protected abstract void updateUI(TBinding contentBinding, TData data);

    protected void setRootNotificationBindingVariable(final ViewDataBinding binding, String layoutName) {
        /*if (!binding.setVariable(de.symeda.sormas.app.BR.showNotificationCallback, this)) {
            Log.e(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(de.symeda.sormas.app.BR.hideNotificationCallback, this)) {
            Log.e(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
        }*/
    }

    public boolean includeFabNonOverlapPadding() {
        return true;
    }

    public boolean makeHeightMatchParent() {
        return false;
    }

    /*public boolean hasBeforeLayoutBindingAsyncReturned() {
        return beforeLayoutBindingAsyncReturn;
    }*/

    @Override
    public final void onResume() {
        super.onResume();

        if (onResumeExecCount > 0) {
            this.activityRootDataRequestor.requestActivityRootData(new ICallback<TActivityRootData>() {
                @Override
                public void result(TActivityRootData result) {
                    setActivityRootData(result);
                    onPageResume(getContentBinding(), beforeLayoutBindingAsyncReturn);
                }
            });
        }



        //getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());

        /*final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)this.getView().findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBaseReadActivity().synchronizeData(SynchronizeDataAsync.SyncMode.ChangesOnly, true, false, refreshLayout, null);
            }
        });*/

        onResumeExecCount = onResumeExecCount + 1;
    }

    public abstract void onPageResume(TBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn);

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    public int getRootReadLayout() {
        return R.layout.fragment_root_read_layout;
    }

    public abstract int getReadLayout();

    public IUpdateSubHeadingTitle getSubHeadingHandler() {
        return this.subHeadingHandler;
    }

    public BaseReadActivity getBaseReadActivity() {
        return this.baseReadActivity;
    }

    protected String getSubHeadingTitle() {
        return null;
    }

    protected void setActivityRootData(TActivityRootData activityRootData) {
        this.activityRootData = activityRootData;
    }

    protected TActivityRootData getActivityRootData() {
        return this.activityRootData;
    }

    public abstract TData getPrimaryData();

    //TODO: We don't need this anymore
    //public abstract TBinding getBinding();

    public ViewDataBinding getRootBinding() {
        return rootBinding;
    }

    public TBinding getContentBinding() {
        return contentViewStubBinding;
    }

    //public abstract View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected void setActivityCommunicator(IActivityCommunicator activityCommunicator) {
        this.activityCommunicator = activityCommunicator;
    }

    public IActivityCommunicator getActivityCommunicator() {
        return this.activityCommunicator;
    }

    protected static <TFragment extends BaseReadActivityFragment, TCapsule extends INavigationCapsule>
    TFragment newInstance(IActivityCommunicator activityCommunicator, Class<TFragment> f, TCapsule dataCapsule, AbstractDomainObject activityRootData) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();

        fragment.setActivityCommunicator(activityCommunicator);

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }


        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        String sampleMaterial = dataCapsule.getSampleMaterial();
        String personUuid = dataCapsule.getPersonUuid();
        String caseUuid = dataCapsule.getCaseUuid();
        String eventUuid = dataCapsule.getEventUuid();
        String taskUuid = dataCapsule.getTaskUuid();
        String contactUuid = dataCapsule.getContactUuid();
        String sampleUuid = dataCapsule.getSampleUuid();
        Disease disease = dataCapsule.getDisease();
        boolean isForVisit = dataCapsule.isForVisit();
        boolean isVisitCooperative = dataCapsule.isVisitCooperative();
        UserRight userRight = dataCapsule.getUserRight();
        //AbstractDomainObject record = dataCapsule.getRecord();

        bundle.putString(ConstantHelper.KEY_DATA_UUID, dataUuid);
        bundle.putString(ConstantHelper.KEY_PERSON_UUID, personUuid);
        bundle.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        bundle.putString(ConstantHelper.KEY_SAMPLE_MATERIAL, sampleMaterial);
        bundle.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        bundle.putString(ConstantHelper.KEY_TASK_UUID, taskUuid);
        bundle.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        bundle.putString(ConstantHelper.KEY_SAMPLE_UUID, sampleUuid);
        bundle.putSerializable(ConstantHelper.ARG_DISEASE, disease);
        bundle.putBoolean(ConstantHelper.ARG_FOR_VISIT, isForVisit);
        bundle.putBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE, isVisitCooperative);
        bundle.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);

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
        fragment.setActivityRootData(activityRootData);
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

    protected <E extends Enum<E>> E getArgByElaboratorKey(Bundle arguments, String key) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(key)) {
                e = (E) arguments.getSerializable(key);
            }
        }

        return e;
    }

    protected String getEventUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_EVENT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_EVENT_UUID);
            }
        }

        return result;
    }

    protected String getTaskUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_TASK_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_TASK_UUID);
            }
        }

        return result;
    }

    protected String getContactUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_CONTACT_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CONTACT_UUID);
            }
        }

        return result;
    }

    protected String getCaseUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_CASE_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_CASE_UUID);
            }
        }

        return result;
    }

    protected String getSampleUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_SAMPLE_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_SAMPLE_UUID);
            }
        }

        return result;
    }

    protected Disease getDiseaseArg(Bundle arguments) {
        Disease result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_DISEASE)) {
                result = (Disease) arguments.getSerializable(ConstantHelper.ARG_DISEASE);
            }
        }

        return result;
    }

    protected boolean getForVisitArg(Bundle arguments) {
        boolean result = false;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_FOR_VISIT)) {
                result = (boolean) arguments.getBoolean(ConstantHelper.ARG_FOR_VISIT);
            }
        }

        return result;
    }

    protected boolean getVisitCooperativeArg(Bundle arguments) {
        boolean result = false;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_VISIT_COOPERATIVE)) {
                result = (boolean) arguments.getBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE);
            }
        }

        return result;
    }

    protected UserRight getUserRightArg(Bundle arguments) {
        UserRight e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT)) {
                e = (UserRight) arguments.getSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT);
            }
        }

        return e;
    }

    protected String getSampleMaterialArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_SAMPLE_MATERIAL)) {
                result = (String) arguments.getString(ConstantHelper.KEY_SAMPLE_MATERIAL);
            }
        }

        return result;
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

    protected void SaveEventUuidState(Bundle outState, String eventUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        }
    }

    protected void SaveTaskUuidState(Bundle outState, String taskUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_TASK_UUID, taskUuid);
        }
    }

    protected void SaveContactUuidState(Bundle outState, String contactUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        }
    }

    protected void SaveCaseUuidState(Bundle outState, String caseUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        }
    }

    protected void SaveSampleUuidState(Bundle outState, String sampleUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_SAMPLE_UUID, sampleUuid);
        }
    }

    protected void SaveDiseaseState(Bundle outState, Disease disease) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_DISEASE, disease);
        }
    }

    protected void SaveForVisitState(Bundle outState, boolean isForVisit) {
        if (outState != null) {
            outState.putBoolean(ConstantHelper.ARG_FOR_VISIT, isForVisit);
        }
    }

    protected void SaveVisitCooperativeState(Bundle outState, boolean isVisitCooperative) {
        if (outState != null) {
            outState.putBoolean(ConstantHelper.ARG_VISIT_COOPERATIVE, isVisitCooperative);
        }
    }

    protected void SaveUserRightState(Bundle outState, UserRight userRight) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);
        }
    }

    protected void SaveSampleMaterialState(Bundle outState, String sampleMaterial) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_SAMPLE_MATERIAL, sampleMaterial);
        }
    }

    public boolean showEditAction() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }

    protected void updateUI() {
        this.skipAfterLayoutBinding = true;
        if (!getContentBinding().setVariable(BR.data, getPrimaryData())) {
            Log.e(TAG, "There is no variable 'data' in layout " + getReadLayout());
        }

        updateUI(getContentBinding(), getPrimaryData());
    }

}
