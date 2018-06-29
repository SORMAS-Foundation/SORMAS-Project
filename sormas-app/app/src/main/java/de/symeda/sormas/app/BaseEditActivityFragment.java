package de.symeda.sormas.app;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.IActivityRootDataRequestor;
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Orson on 22/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class BaseEditActivityFragment<TBinding extends ViewDataBinding, TData, TActivityRootData extends AbstractDomainObject> extends BaseFragment {

    public static final String TAG = BaseEditActivityFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseEditActivity baseEditActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private NotificationContext notificationCommunicator;
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

        saveUserRightState(outState, editOrCreateUserRight);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getArguments();
        editOrCreateUserRight = (UserRight) getUserRightArg(arguments);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        if (getActivity() instanceof BaseEditActivity) {
            this.baseEditActivity = (BaseEditActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The edit activity for fragment must implement BaseEditActivity");
        }

        if (getActivity() instanceof IUpdateSubHeadingTitle) {
            this.subHeadingHandler = (IUpdateSubHeadingTitle) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support updateSubHeadingTitle; "
                    + "implement IUpdateSubHeadingTitle");
        }

        if (getActivity() instanceof NotificationContext) {
            this.notificationCommunicator = (NotificationContext) this.getActivity();
        } else {
            throw new NotImplementedException("Activity for fragment does not support showErrorNotification; "
                    + "implement NotificationContext");
        }

        if (getActivity() instanceof IActivityRootDataRequestor) {
            this.activityRootDataRequestor = (IActivityRootDataRequestor) this.getActivity();
        } else {
            throw new NotImplementedException("The edit activity for fragment must implement IActivityRootDataRequestor");
        }


        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootEditLayout(), container, false);
        rootView = rootBinding.getRoot();

        if (getEditLayout() > 0) {
            final ViewStub vsChildFragmentFrame = (ViewStub)rootView.findViewById(R.id.vsChildFragmentFrame);

            vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    //onLayoutBindingHelper(stub, inflated);

                    contentViewStubBinding = DataBindingUtil.bind(inflated);
                    String layoutName = getResources().getResourceEntryName(getEditLayout());
//                    setRootNotificationBindingVariable(contentViewStubBinding, layoutName);
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

                    //onLayoutBinding(contentViewStubBinding);
                }
            });

            vsChildFragmentFrame.setLayoutResource(getEditLayout());


            beforeLayoutBindingAsyncReturn = false;
            DefaultAsyncTask executor = new DefaultAsyncTask(getContext()) {
                @Override
                public void onPreExecute() {
                    getActivityCommunicator().showPreloader();
                    getActivityCommunicator().hideFragmentView();
                }


                @Override
                public void execute(final TaskResultHolder resultHolder) {
                    onBeforeLayoutBinding(savedInstanceState, resultHolder, null, false);
                }
            };

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

//    protected void setRootNotificationBindingVariable(final ViewDataBinding binding, String layoutName) {
//        if (!binding.setVariable(de.symeda.sormas.app.BR.showNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
//        }
//
//        if (!binding.setVariable(de.symeda.sormas.app.BR.hideNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
//        }
//    }

    @Override
    public final void onResume() {
        super.onResume();

        if (onResumeExecCount > 0) {
            this.activityRootDataRequestor.requestActivityRootData(new Callback.IAction<TActivityRootData>() {
                @Override
                public void call(TActivityRootData result) {

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
                getBaseEditActivity().synchronizeData(SynchronizeDataAsync.SyncMode.Changes, true, false, refreshLayout, null);
            }
        });*/

        onResumeExecCount = onResumeExecCount + 1;
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(getActivity(), this);
    }

    public abstract void onPageResume(TBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn);

    public int getRootEditLayout() {
        return R.layout.fragment_root_edit_layout;
    }

    public abstract int getEditLayout();

    public boolean includeFabNonOverlapPadding() {
        return true;
    }

    public boolean makeHeightMatchParent() {
        return false;
    }

    public IUpdateSubHeadingTitle getSubHeadingHandler() {
        return this.subHeadingHandler;
    }

    public BaseEditActivity getBaseEditActivity() {
        return this.baseEditActivity;
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

    public ViewDataBinding getRootBinding() {
        return rootBinding;
    }

    public TBinding getContentBinding() {
        return contentViewStubBinding;
    }

    protected abstract boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete);

    protected abstract void onLayoutBinding(TBinding contentBinding);

    protected abstract void onAfterLayoutBinding(TBinding contentBinding);

    protected abstract void updateUI(TBinding contentBinding, TData data);

    protected static <TFragment extends BaseEditActivityFragment, TCapsule extends INavigationCapsule> TFragment newInstance(IActivityCommunicator activityCommunicator, Class<TFragment> f, TCapsule dataCapsule, AbstractDomainObject activityRootData)  {
        TFragment fragment;
        try {
            fragment = f.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        fragment.setActivityCommunicator(activityCommunicator);

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }


        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        int activeMenuKey = dataCapsule.getActiveMenuKey();
        String sampleMaterial = dataCapsule.getSampleMaterial();
        String caseUuid = dataCapsule.getCaseUuid();
        String personUuid = dataCapsule.getPersonUuid();
        String eventUuid = dataCapsule.getEventUuid();
        String taskUuid = dataCapsule.getTaskUuid();
        String contactUuid = dataCapsule.getContactUuid();
        String sampleUuid = dataCapsule.getSampleUuid();
        Disease disease = dataCapsule.getDisease();
        boolean isForVisit = dataCapsule.isForVisit();
        boolean isVisitCooperative = dataCapsule.isVisitCooperative();
        UserRight userRight = dataCapsule.getUserRight();
        //AbstractDomainObject record = dataCapsule.getRecord();

        bundle.putInt(ConstantHelper.KEY_ACTIVE_MENU, activeMenuKey);
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

    protected String getPersonUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if(arguments.containsKey(ConstantHelper.KEY_PERSON_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_PERSON_UUID);
            }
        }

        return result;
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

    protected void savePersonUuidState(Bundle outState, String recordUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_PERSON_UUID, recordUuid);
        }
    }

    protected void saveEventUuidState(Bundle outState, String eventUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_EVENT_UUID, eventUuid);
        }
    }

    protected void saveContactUuidState(Bundle outState, String contactUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CONTACT_UUID, contactUuid);
        }
    }

    protected void saveCaseUuidState(Bundle outState, String caseUuid) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_CASE_UUID, caseUuid);
        }
    }

    protected void saveUserRightState(Bundle outState, UserRight userRight) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);
        }
    }

    protected void saveSampleMaterialState(Bundle outState, String sampleMaterial) {
        if (outState != null) {
            outState.putString(ConstantHelper.KEY_SAMPLE_MATERIAL, sampleMaterial);
        }
    }

//    @Override
//    public void onShowInputErrorShowing(View v, String message, boolean errorState) {
//        //notificationCommunicator.showErrorNotification(NotificationType.ERROR, message);
//        NotificationHelper.showErrorNotification((NotificationContext)getActivity(), NotificationType.ERROR, message);
//    }
//
//    @Override
//    public void onInputErrorHiding(View v, boolean errorState) {
//        //notificationCommunicator.hideNotification();
//        NotificationHelper.hideNotification((NotificationContext)getActivity());
//    }

    public boolean showSaveAction() {
        return true;
    }

    public boolean showAddAction() {
        return false;
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
            Log.e(TAG, "There is no variable 'data' in layout " + getEditLayout());
        }

        updateUI(getContentBinding(), getPrimaryData());
    }
}
