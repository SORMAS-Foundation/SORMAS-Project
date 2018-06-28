package de.symeda.sormas.app;

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

import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.IActivityCommunicator;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.async.IJobDefinition;
import de.symeda.sormas.app.core.async.ITaskExecutor;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskExecutorFor;
import de.symeda.sormas.app.core.async.TaskResultHolder;

/**
 * Created by Orson on 24/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public abstract class BaseReportActivityFragment<TBinding extends ViewDataBinding, TData> extends BaseFragment {

    private final static String TAG = BaseReportActivityFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseReportActivity baseReportActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private NotificationContext notificationCommunicator;
    private ViewDataBinding rootBinding;
    private View rootView;
    private TBinding contentViewStubBinding;
    private View contentViewStubRoot;
    private boolean beforeLayoutBindingAsyncReturn;
    private boolean skipAfterLayoutBinding = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        if (getActivity() instanceof BaseReportActivity) {
            this.baseReportActivity = (BaseReportActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The list activity for fragment must implement BaseReportActivity");
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

        //Inflate Root
        rootBinding = DataBindingUtil.inflate(inflater, getRootLayoutResId(), container, false);
        rootView = rootBinding.getRoot();

        if (getLayoutResId() > 0) {
            final ViewStub vsChildFragmentFrame = (ViewStub)rootView.findViewById(R.id.vsChildFragmentFrame);

            vsChildFragmentFrame.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub stub, View inflated) {
                    //onLayoutBindingHelper(stub, inflated);

                    contentViewStubBinding = DataBindingUtil.bind(inflated);
                    String layoutName = getResources().getResourceEntryName(getLayoutResId());
//                    setRootNotificationBindingVariable(contentViewStubBinding, layoutName);
                    onLayoutBinding(contentViewStubBinding);
                    contentViewStubRoot = contentViewStubBinding.getRoot();
                }
            });

            vsChildFragmentFrame.setLayoutResource(getLayoutResId());


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

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }

    public int getRootLayoutResId() {
        return R.layout.fragment_root_report_layout;
    }


    public IUpdateSubHeadingTitle getSubHeadingHandler() {
        return this.subHeadingHandler;
    }

    public BaseReportActivity getBaseReportActivity() {
        return this.baseReportActivity;
    }


    protected String getSubHeadingTitle() {
        return null;
    }

    protected ViewDataBinding getRootBinding() {
        return rootBinding;
    }

    protected TBinding getContentBinding() {
        return contentViewStubBinding;
    }

    protected static <TFragment extends BaseReportActivityFragment> TFragment newInstance(IActivityCommunicator activityCommunicator, Class<TFragment> f)  {
        TFragment fragment = null;

        try {
            fragment = f.newInstance();
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        fragment.setActivityCommunicator(activityCommunicator);

        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    protected void updateUI() {
        this.skipAfterLayoutBinding = true;
        if (!getContentBinding().setVariable(BR.data, getPrimaryData())) {
            Log.e(TAG, "There is no variable 'data' in layout " + getLayoutResId());
        }

        updateUI(getContentBinding(), getPrimaryData());
    }


    protected abstract boolean onBeforeLayoutBinding(Bundle savedInstanceState, TaskResultHolder resultHolder, BoolResult resultStatus, boolean executionComplete);

    protected abstract void onLayoutBinding(TBinding contentBinding);

    protected abstract void onAfterLayoutBinding(TBinding contentBinding);

    protected abstract void updateUI(TBinding contentBinding, TData data);

    protected abstract TData getPrimaryData();

    protected abstract int getLayoutResId();



    public void requestLayoutRebind() {
        onLayoutBinding(contentViewStubBinding);
    }


//    private void setRootNotificationBindingVariable(final ViewDataBinding binding, String layoutName) {
//        if (!binding.setVariable(de.symeda.sormas.app.BR.showNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
//        }
//
//        if (!binding.setVariable(de.symeda.sormas.app.BR.hideNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
//        }
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }

}
