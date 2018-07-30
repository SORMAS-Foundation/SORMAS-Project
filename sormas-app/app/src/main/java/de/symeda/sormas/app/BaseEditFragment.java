package de.symeda.sormas.app;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.util.SoftKeyboardHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class BaseEditFragment<TBinding extends ViewDataBinding, TData, TActivityRootData extends AbstractDomainObject> extends BaseFragment {

    public static final String TAG = BaseEditFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseEditActivity baseEditActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private NotificationContext notificationCommunicator;

    private TBinding contentViewStubBinding;
    private View contentViewStubRoot;
    private ViewDataBinding rootBinding;

    private boolean skipAfterLayoutBinding = false;
    private TActivityRootData activityRootData;
    private View rootView;
    private boolean liveValidationDisabled;

    protected static <TFragment extends BaseEditFragment> TFragment newInstance(Class<TFragment> fragmentClass, Bundle data, AbstractDomainObject activityRootData) {
        TFragment fragment = newInstance(fragmentClass, data);
        fragment.setActivityRootData(activityRootData);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        SoftKeyboardHelper.hideKeyboard(getActivity(), this);
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

        super.onCreateView(inflater, container, savedInstanceState);

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootEditLayout(), container, false);
        rootView = rootBinding.getRoot();

        if (getActivityRootData() == null) {
            // may happen when android tries to re-create old fragments for an activity
            return rootView;
        }

        final ViewStub vsChildFragmentFrame = (ViewStub) rootView.findViewById(R.id.vsChildFragmentFrame);
        vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                //onLayoutBindingHelper(stub, inflated);

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

                if (makeHeightMatchParent()) {
                    contentViewStubRoot.getLayoutParams().height = MATCH_PARENT;
                } else {
                    contentViewStubRoot.getLayoutParams().height = WRAP_CONTENT;
                }

                ViewGroup root = (ViewGroup) getContentBinding().getRoot();
                setNotificationContextForPropertyFields(root);
            }
        });

        vsChildFragmentFrame.setLayoutResource(getEditLayout());

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

                if (taskResult.getResultStatus().isFailed())
                    return;

                vsChildFragmentFrame.inflate();
            }
        }.executeOnThreadPool();

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
        if (contentViewStubBinding != null) {
            onLayoutBinding(contentViewStubBinding);
        }
    }

    public int getRootEditLayout() {
        return R.layout.fragment_root_edit_layout;
    }

    public abstract int getEditLayout();

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

    protected abstract void prepareFragmentData();

    protected abstract void onLayoutBinding(TBinding contentBinding);

    protected void onAfterLayoutBinding(TBinding contentBinding) {
    }

    public void disableLiveValidation(boolean disableLiveValidation) {
        ViewGroup root = (ViewGroup) getContentBinding().getRoot();
        disableLiveValidationForAllChildren(root, disableLiveValidation);
        liveValidationDisabled = disableLiveValidation;
    }

    private static void disableLiveValidationForAllChildren(ViewGroup parent, boolean disableLiveValidation) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ((ControlPropertyEditField) child).setLiveValidationDisabled(disableLiveValidation);
            } else if (child instanceof ViewGroup) {
                disableLiveValidationForAllChildren((ViewGroup) child, disableLiveValidation);
            }
        }
    }

    public void setNotificationContextForPropertyFields(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ((ControlPropertyEditField) child).setNotificationContext(notificationCommunicator);
            } else if (child instanceof ViewGroup) {
                setNotificationContextForPropertyFields((ViewGroup) child);
            }
        }
    }

    public boolean isShowSaveAction() {
        return true;
    }

    public boolean isShowAddAction() {
        return false;
    }

    public void setLiveValidationDisabled(boolean liveValidationDisabled) {
        this.liveValidationDisabled = liveValidationDisabled;
    }

    public boolean isLiveValidationDisabled() {
        return liveValidationDisabled;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }
}
