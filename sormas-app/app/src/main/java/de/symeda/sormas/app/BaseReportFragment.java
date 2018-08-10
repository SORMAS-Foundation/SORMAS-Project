package de.symeda.sormas.app;

import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import de.symeda.sormas.app.core.IUpdateSubHeadingTitle;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;

public abstract class BaseReportFragment<TBinding extends ViewDataBinding> extends BaseFragment {

    private final static String TAG = BaseReportFragment.class.getSimpleName();

    private AsyncTask jobTask;
    private BaseReportActivity baseReportActivity;
    private IUpdateSubHeadingTitle subHeadingHandler;
    private NotificationContext notificationCommunicator;
    private ViewDataBinding rootBinding;
    private View rootView;
    private TBinding contentViewStubBinding;
    private boolean skipAfterLayoutBinding = false;

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
                        skipAfterLayoutBinding = true;

                        getSubHeadingHandler().updateSubHeadingTitle(getSubHeadingTitle());
                    }
                });
                onLayoutBinding(contentViewStubBinding);
            }
        });

        vsChildFragmentFrame.setLayoutResource(getReportLayout());

        jobTask = new DefaultAsyncTask(getContext()) {
            @Override
            public void onPreExecute() {
                getBaseActivity().showPreloader();
            }

            @Override
            public void doInBackground(final TaskResultHolder resultHolder) {
                prepareFragmentData(savedInstanceState);
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

    protected abstract void prepareFragmentData(Bundle savedInstanceState);

    protected abstract void onLayoutBinding(TBinding contentBinding);

    protected void onAfterLayoutBinding(TBinding contentBinding) {

    }

    protected abstract int getReportLayout();

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jobTask != null && !jobTask.isCancelled())
            jobTask.cancel(true);
    }

}
