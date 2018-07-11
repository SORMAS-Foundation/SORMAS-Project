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
import de.symeda.sormas.app.core.INavigationCapsule;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.enumeration.IStatusElaborator;
import de.symeda.sormas.app.util.ConstantHelper;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class BaseReadFragment<TBinding extends ViewDataBinding, TData, TActivityRootData extends AbstractDomainObject> extends BaseFragment {

    public static final String TAG = BaseReadFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseReadActivity baseReadActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private TBinding contentViewStubBinding;
    private ViewDataBinding rootBinding;
    private View contentViewStubRoot;
    private boolean beforeLayoutBindingAsyncReturn;
    private boolean skipAfterLayoutBinding = false;
    private TActivityRootData activityRootData;
    private View rootView;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveUserRightState(outState, editOrCreateUserRight);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = (savedInstanceState != null) ? savedInstanceState : getArguments();
        editOrCreateUserRight = (UserRight) getUserRightArg(arguments);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

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

        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootReadLayout(), container, false);
        rootView = rootBinding.getRoot();

        final ViewStub vsChildFragmentFrame = (ViewStub) rootView.findViewById(R.id.vsChildFragmentFrame);
        vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {

                contentViewStubBinding = DataBindingUtil.bind(inflated);
                contentViewStubBinding.addOnRebindCallback(new OnRebindCallback() {
                    @Override
                    public void onBound(ViewDataBinding binding) {
                        super.onBound(binding);

                        if (!skipAfterLayoutBinding)
                            onAfterLayoutBinding(contentViewStubBinding);
                        skipAfterLayoutBinding = false;

                        getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
                    }
                });
                onLayoutBinding(contentViewStubBinding);
                contentViewStubRoot = contentViewStubBinding.getRoot();

                if (includeFabNonOverlapPadding()) {
                    int lp = contentViewStubRoot.getPaddingLeft();
                    int rp = contentViewStubRoot.getPaddingRight();
                    int tp = contentViewStubRoot.getPaddingTop();
                    int bp = (int) getResources().getDimension(R.dimen.fabNonOverlapPaddingBottom);

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
        jobTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void doInBackground(final TaskResultHolder resultHolder) {
                prepareFragmentData(savedInstanceState);
                onBeforeLayoutBinding(savedInstanceState, resultHolder, null, false);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                getBaseActivity().hidePreloader();

                if (taskResult.getResultStatus().isFailed())
                    return;

                beforeLayoutBindingAsyncReturn = onBeforeLayoutBinding(savedInstanceState, taskResult.getResult(), taskResult.getResultStatus(), true);

                vsChildFragmentFrame.inflate();

                contentViewStubBinding.addOnRebindCallback(new OnRebindCallback() {
                    @Override
                    public void onBound(ViewDataBinding binding) {
                        super.onBound(binding);

                        if (!skipAfterLayoutBinding)
                            onAfterLayoutBinding(contentViewStubBinding);
                        skipAfterLayoutBinding = false;

                        getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
                    }
                });
            }
        }.executeOnThreadPool();


        return rootView;
    }

    public void showEmptyListHint(List list, int entityNameResId) {
        showEmptyListHint(list, R.string.hint_no_records_found, entityNameResId);
    }

    private void showEmptyListHint(List list, int stringFormatResId, int entityNameResId) {
        boolean isListEmpty = false;

        if (rootView == null)
            return;

        TextView emptyListHintView = (TextView) rootView.findViewById(R.id.emptyListHint);

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

    // TODO make abstract
    protected void prepareFragmentData(Bundle savedInstanceState) {
    }

    /**
     * @Deprecated Use prepareFragmentData and onLayoutBinding isntead
     */
    @Deprecated
    public boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete) {
        return true;
    }

    public abstract void onLayoutBinding(TBinding contentBinding);

    // TODO: Add comment on when to use the layout binding methods
    public void onAfterLayoutBinding(TBinding contentBinding) {
    }

    protected void updateUI(TBinding contentBinding, TData data) {
    }

    public boolean includeFabNonOverlapPadding() {
        return false;
    }

    public boolean makeHeightMatchParent() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(getActivity(), this);
    }

    @Deprecated
    public void onPageResume(TBinding contentBinding, boolean hasBeforeLayoutBindingAsyncReturn) {
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

    public ViewDataBinding getRootBinding() {
        return rootBinding;
    }

    public TBinding getContentBinding() {
        return contentViewStubBinding;
    }

    protected static <TFragment extends BaseReadFragment, TCapsule extends INavigationCapsule> TFragment newInstance(Class<TFragment> f, TCapsule dataCapsule, AbstractDomainObject activityRootData) {
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

        String dataUuid = dataCapsule.getRecordUuid();
        IStatusElaborator filterStatus = dataCapsule.getFilterStatus();
        IStatusElaborator pageStatus = dataCapsule.getPageStatus();
        int activeMenuKey = dataCapsule.getActiveMenuKey();
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

        fragment.setArguments(bundle);
        fragment.setActivityRootData(activityRootData);
        return fragment;
    }

    protected String getRecordUuidArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_DATA_UUID)) {
                result = (String) arguments.getString(ConstantHelper.KEY_DATA_UUID);
            }
        }

        return result;
    }

    protected <E extends Enum<E>> E getFilterStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.ARG_FILTER_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_FILTER_STATUS);
            }
        }

        return e;
    }

    protected <E extends Enum<E>> E getPageStatusArg(Bundle arguments) {
        E e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.ARG_PAGE_STATUS)) {
                e = (E) arguments.getSerializable(ConstantHelper.ARG_PAGE_STATUS);
            }
        }

        return e;
    }

    protected UserRight getUserRightArg(Bundle arguments) {
        UserRight e = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT)) {
                e = (UserRight) arguments.getSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT);
            }
        }

        return e;
    }

    protected String getSampleMaterialArg(Bundle arguments) {
        String result = null;
        if (arguments != null && !arguments.isEmpty()) {
            if (arguments.containsKey(ConstantHelper.KEY_SAMPLE_MATERIAL)) {
                result = (String) arguments.getString(ConstantHelper.KEY_SAMPLE_MATERIAL);
            }
        }

        return result;
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

    protected void saveUserRightState(Bundle outState, UserRight userRight) {
        if (outState != null) {
            outState.putSerializable(ConstantHelper.ARG_EDIT_OR_CREATE_USER_RIGHT, userRight);
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
