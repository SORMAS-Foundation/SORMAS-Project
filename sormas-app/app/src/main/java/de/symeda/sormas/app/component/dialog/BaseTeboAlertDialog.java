package de.symeda.sormas.app.component.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.BoolResult;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.DefaultAsyncTask;
import de.symeda.sormas.app.core.async.ITaskResultCallback;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogRootLayoutBinding;


public abstract class BaseTeboAlertDialog implements de.symeda.sormas.app.component.dialog.IDialogDismissOnClickListener,
        de.symeda.sormas.app.component.dialog.IDialogOkOnClickListener, de.symeda.sormas.app.component.dialog.IDialogDeleteOnClickListener,
        IDialogCancelOnClickListener, IDialogCreateOnClickListener, NotificationContext {

    public static final String TAG = BaseTeboAlertDialog.class.getSimpleName();

    private AsyncTask dialogTask;
    private FragmentActivity activity;
    private int rootLayoutId;
    AlertDialog.Builder builder;
    private AlertDialog dialog;
    private DialogRootLayoutBinding rootBinding;
    private ViewDataBinding contentViewStubBinding;
    private ViewDataBinding btnPanelViewStubBinding;
    private int contentLayoutResourceId;
    private int btnPanelLayoutResourceId;
    private de.symeda.sormas.app.component.dialog.DialogViewConfig config;
    private boolean liveValidationDisabled;

    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.NegativeOnClickListener onNegativeClickListener;
    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener onPositiveClickListener;
    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.DeleteOnClickListener onDeleteClickListener;
    private TeboAlertDialogInterface.CancelOnClickListener onCancelClickListener;
    private TeboAlertDialogInterface.CreateOnClickListener onCreateClickListener;

    public BaseTeboAlertDialog(final FragmentActivity activity, int rootLayoutId, int contentLayoutResourceId,
                               int btnPanelLayoutResourceId, int headingResId, String subHeading) {

        this.builder = new AlertDialog.Builder(activity);
        this.activity = activity;
        this.rootLayoutId = rootLayoutId;
        this.contentLayoutResourceId = contentLayoutResourceId;
        this.btnPanelLayoutResourceId = btnPanelLayoutResourceId;


        String heading;

        if (headingResId <= 0)
            heading = activity.getResources().getString(R.string.heading_dialog_placeholder);
        else
            heading = activity.getResources().getString(headingResId);


        if (subHeading == null || subHeading.isEmpty())
            subHeading = activity.getResources().getString(R.string.heading_sub_dialog_placeholder);


        Resources resources = activity.getResources();

        String positiveLabel = resources.getString(getPositiveButtonText());
        String negativeLabel = resources.getString(getNegativeButtonText());
        String deleteLabel = resources.getString(getDeleteButtonText());
        String cancelLabel = resources.getString(getCancelButtonText());
        String createLabel = resources.getString(getCreateButtonText());

        this.config = new de.symeda.sormas.app.component.dialog.DialogViewConfig(heading, subHeading, positiveLabel, negativeLabel, deleteLabel, cancelLabel, createLabel);
    }

    public BaseTeboAlertDialog(final FragmentActivity activity, int rootLayoutId, int contentLayoutResourceId,
                               int btnPanelLayoutResourceId, int headingResId, int subHeadingResId) {

        this.builder = new AlertDialog.Builder(activity);
        this.activity = activity;
        this.rootLayoutId = rootLayoutId;
        this.contentLayoutResourceId = contentLayoutResourceId;
        this.btnPanelLayoutResourceId = btnPanelLayoutResourceId;


        String heading, subHeading;

        if (headingResId <= 0)
            heading = activity.getResources().getString(R.string.heading_dialog_placeholder);
        else
            heading = activity.getResources().getString(headingResId);


        if (subHeadingResId <= 0)
            subHeading = activity.getResources().getString(R.string.heading_sub_dialog_placeholder);
        else
            subHeading = activity.getResources().getString(subHeadingResId);


        Resources resources = activity.getResources();

        String positiveLabel = resources.getString(getPositiveButtonText());
        String negativeLabel = resources.getString(getNegativeButtonText());
        String deleteLabel = resources.getString(getDeleteButtonText());
        String cancelLabel = resources.getString(getCancelButtonText());
        String createLabel = resources.getString(getCreateButtonText());

        this.config = new de.symeda.sormas.app.component.dialog.DialogViewConfig(heading, subHeading, positiveLabel, negativeLabel, deleteLabel, cancelLabel, createLabel);
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public void onOkClick(View v, Object item) {
        View viewRoot = getRoot();

        onOkClicked(v, item, viewRoot, contentViewStubBinding, new Callback.IAction() {
            private View _v;
            private Object _item;
            private View _viewRoot;

            @Override
            public void call(Object result) {
                if (onPositiveClickListener != null)
                    onPositiveClickListener.onOkClick(this._v, (result != null)? result : this._item, this._viewRoot);
            }

            private Callback.IAction init(View v, Object item, View viewRoot) {
                this._v = v;
                this._item = item;
                this._viewRoot = viewRoot;

                return this;
            }
        }.init(v, item, viewRoot));

    }

    @Override
    public void onCancelClick(View v, Object item) {
        View viewRoot = getRoot();

        onCancelClicked(v, item, viewRoot, contentViewStubBinding, new Callback.IAction() {
            private View _v;
            private Object _item;
            private View _viewRoot;

            @Override
            public void call(Object result) {
                if (onCancelClickListener != null)
                    onCancelClickListener.onCancelClick(this._v, (result != null)? result : this._item, this._viewRoot);
            }

            private Callback.IAction init(View v, Object item, View viewRoot) {
                this._v = v;
                this._item = item;
                this._viewRoot = viewRoot;

                return this;
            }
        }.init(v, item, viewRoot));

    }

    @Override
    public void onCreateClick(View v, Object item) {
        View viewRoot = getRoot();

        onCreateClicked(v, item, viewRoot, contentViewStubBinding, new Callback.IAction() {
            private View _v;
            private Object _item;
            private View _viewRoot;

            @Override
            public void call(Object result) {
                if (onCreateClickListener != null)
                    onCreateClickListener.onCreateClick(this._v, (result != null)? result : this._item, this._viewRoot);
            }

            private Callback.IAction init(View v, Object item, View viewRoot) {
                this._v = v;
                this._item = item;
                this._viewRoot = viewRoot;

                return this;
            }
        }.init(v, item, viewRoot));

    }

//    @Override
//    public void onShowInputErrorShowing(View v, String message, boolean errorState) {
//        if (rootBinding.notificationFrame == null)
//            return;
//
//        if (rootBinding.tvNotificationMessage == null)
//            return;
//
//
//        //TransitionManager.beginDelayedTransition(rootBinding.notificationFrame);
//        //Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
//        //rootBinding.notificationFrame.startAnimation(expandIn);
//
//        NotificationType type = NotificationType.ERROR;
//
//        LayerDrawable drawable = (LayerDrawable) activity.getResources().getDrawable(R.drawable.background_notification_dialog).mutate();
//        int backgroundColor = activity.getResources().getColor(type.getInverseBackgroundColor());
//        int textColor = activity.getResources().getColor(type.getInverseTextColor());
//
//
//        //backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(this.layout.getContext(), R.drawable.background_list_activity_row);
//
//        Drawable backgroundLayer = drawable.findDrawableByLayerId(R.id.backgroundLayer);
//        backgroundLayer.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_OVER);
//        //unreadListItemIndicator.setTint(this.layout.getContext().getResources().getColor(R.color.unreadIcon));
//        //drawable.setAlpha(50);
//
//        rootBinding.tvNotificationMessage.setTextColor(textColor);
//        rootBinding.tvNotificationMessage.setText(message);
//
//        rootBinding.notificationFrame.setBackground(drawable);
//        rootBinding.notificationFrame.setVisibility(View.VISIBLE);
//        //binding.notificationFrame.animate().translationY(0);
//
//    }
//
//    @Override
//    public void onInputErrorHiding(View v, boolean errorState) {
//
//    }

    @Override
    public void onDismissClick(View v, Object item) {
        dialog.dismiss();

        View viewRoot = getRoot();

        onDismissClicked(v, item, viewRoot, contentViewStubBinding, new Callback.IAction() {
            private View _v;
            private Object _item;
            private View _viewRoot;

            @Override
            public void call(Object result) {
                if (onNegativeClickListener != null)
                    onNegativeClickListener.onDismissClick(this._v, (result != null)? result : this._item, this._viewRoot);
            }

            private Callback.IAction init(View v, Object item, View viewRoot) {
                this._v = v;
                this._item = item;
                this._viewRoot = viewRoot;

                return this;
            }
        }.init(v, item, viewRoot));
    }
    @Override
    public void onDeleteClick(View v, final Object item) {
        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(this.activity, R.string.heading_confirmation_dialog,
                R.string.heading_sub_confirmation_notification_dialog_delete);

        confirmationDialog.setOnPositiveClickListener(new de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object confirmationItem, View viewRoot) {
                confirmationDialog.dismiss();

                onDeleteClicked(v, item, viewRoot, contentViewStubBinding, new Callback.IAction() {
                    private View _v;
                    private Object _item;
                    private View _viewRoot;

                    @Override
                    public void call(Object result) {
                        if (onDeleteClickListener != null)
                            onDeleteClickListener.onDeleteClick(this._v, (result != null)? result : this._item, this._viewRoot);


                    }

                    private Callback.IAction init(View v, Object item, View viewRoot) {
                        this._v = v;
                        this._item = item;
                        this._viewRoot = viewRoot;

                        return this;
                    }
                }.init(v, item, viewRoot));

            }
        });

        confirmationDialog.show(null);

    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Protected Methods">

    protected View getRoot() {
        return rootBinding.getRoot();
    }

    protected FragmentActivity getActivity() {
        return activity;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private DialogRootLayoutBinding bindRootLayout(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final DialogRootLayoutBinding binding = DataBindingUtil.inflate(inflater, this.rootLayoutId, null, false);
        final String layoutName = context.getResources().getResourceEntryName(this.rootLayoutId);
        View rootView = binding.getRoot();

        setBackground(binding);
        setBackgroundColor(binding);

        bindDialog(context, binding, layoutName);
        bindConfig(context, binding, layoutName);
//        bindNotificationCallbacks(context, binding, layoutName);
        bindButtonCallbacks(context, binding, layoutName);
        setBindingVariable(context, binding, layoutName);

        binding.notificationFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
                //rootBinding.notificationFrame.startAnimation(expandIn);
                v.setVisibility(View.GONE);

            }
        });


        /*
        Content View
         */
        binding.vsDialogContent.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                contentViewStubBinding = DataBindingUtil.bind(inflated);
                recieveViewDataBinding(context, contentViewStubBinding);
                String layoutName = context.getResources().getResourceEntryName(contentLayoutResourceId);
                bindConfig(context, contentViewStubBinding, layoutName);
//                bindNotificationCallbacks(context, contentViewStubBinding, layoutName);
                setBindingVariable(context, contentViewStubBinding, layoutName);

                ViewGroup root = (ViewGroup) contentViewStubBinding.getRoot();
                setNotificationContextForPropertyFields(root);

            }
        });

        ViewStub dialogContent = binding.vsDialogContent.getViewStub();
        dialogContent.setLayoutResource(contentLayoutResourceId);
        View dialogContentInflated = dialogContent.inflate();


        /*
        Button Panel View
         */
        binding.vsDialogButtonPanel.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                btnPanelViewStubBinding = DataBindingUtil.bind(inflated);
                String layoutName = context.getResources().getResourceEntryName(btnPanelLayoutResourceId);
                bindConfig(context, btnPanelViewStubBinding, layoutName);
                setBindingVariable(context, btnPanelViewStubBinding, layoutName);
                bindDialog(context, btnPanelViewStubBinding, layoutName);
                bindButtonCallbacks(context, btnPanelViewStubBinding, layoutName);
            }
        });


        if (btnPanelLayoutResourceId > 0) {
            ViewStub buttonPanel = binding.vsDialogButtonPanel.getViewStub();
            buttonPanel.setLayoutResource(btnPanelLayoutResourceId);
            View buttonPanelInflated = buttonPanel.inflate();
        }


        //Show or Hide Buttons
        /*if (!isDismissButtonVisible() && !isDismissButtonVisible()) {

        } else {
            binding.btnDismiss.setVisibility(isDismissButtonVisible() ? View.VISIBLE : View.GONE);
            binding.btnOk.setVisibility(isDismissButtonVisible() ? View.VISIBLE : View.GONE);
        }*/



        builder.setView(rootView);
        //setView(this.rootBinding.getRoot());

        return binding;
    }

    private void bindDialog(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.dialog, this)) {
            Log.e(TAG, "There is no variable 'dialog' in layout " + layoutName);
        }
    }

    private void bindConfig(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.config, this.config)) {
            Log.e(TAG, "There is no variable 'config' in layout " + layoutName);
        }
    }

    private void bindButtonCallbacks(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.dismissCallback, this)) {
            Log.e(TAG, "There is no variable 'dismissCallback' in layout " + layoutName);
        }

        /*if (!binding.setVariable(BR.showNotificationCallback, this)) {
            Log.e(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.hideNotificationCallback, this)) {
            Log.e(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
        }*/

        if (!binding.setVariable(BR.okCallback, this)) {
            Log.e(TAG, "There is no variable 'okCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.deleteCallback, this)) {
            Log.e(TAG, "There is no variable 'deleteCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.cancelCallback, this)) {
            Log.e(TAG, "There is no variable 'cancelCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.createCallback, this)) {
            Log.e(TAG, "There is no variable 'createCallback' in layout " + layoutName);
        }
    }

//    private void bindNotificationCallbacks(final Context context, final ViewDataBinding binding, String layoutName) {
//        if (!binding.setVariable(BR.showNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
//        }
//
//        if (!binding.setVariable(BR.hideNotificationCallback, this)) {
//            Log.e(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
//        }
//    }

    private void setBackground(ViewDataBinding binding) {
        if (binding == null)
            return;

        View rootView = binding.getRoot();

        if (rootView == null)
            return;

        View baseLayout = rootView.findViewById(R.id.base_layout);

        int backgroundResourceId = getBackground();

        if (backgroundResourceId > 0)
            baseLayout.setBackground(this.activity.getResources().getDrawable(backgroundResourceId).mutate());
    }

    private void setBackgroundColor(ViewDataBinding binding) {
        if (binding == null)
            return;

        View rootView = binding.getRoot();

        if (rootView == null)
            return;

        View baseLayout = rootView.findViewById(R.id.base_layout);


        int colorResourceId = getBackgroundColor();

        if (colorResourceId > 0)
            baseLayout.setBackgroundColor(this.activity.getResources().getColor(colorResourceId));
    }

    private void setHeadingColor(ViewDataBinding binding) {
        if (binding == null)
            return;

        View rootView = binding.getRoot();

        if (rootView == null)
            return;

        TextView dialogHeading = (TextView) rootView.findViewById(R.id.dialogHeading);


        int colorResourceId = getHeadingColor();

        if (colorResourceId > 0)
            dialogHeading.setTextColor(this.activity.getResources().getColor(colorResourceId));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Abstract Methods">

    protected abstract void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback);

    protected abstract void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback);

    protected abstract void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback);

    protected void onCancelClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {

    }

    protected void onCreateClicked(View v, Object item, View rootView, ViewDataBinding contentBinding, Callback.IAction callback) {

    }

    protected abstract void recieveViewDataBinding(Context context, ViewDataBinding binding);

    protected abstract void setBindingVariable(Context context, ViewDataBinding binding, String layoutName);

    protected abstract void prepareDialogData();

    protected abstract void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding);

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">

    public void setOnNegativeClickListener(de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.NegativeOnClickListener onNegativeClickListener) {
        this.onNegativeClickListener = onNegativeClickListener;
    }

    public void setOnPositiveClickListener(de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
    }

    public void setOnDeleteClickListener(de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.DeleteOnClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setOnCancelClickListener(TeboAlertDialogInterface.CancelOnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setOnCreateClickListener(TeboAlertDialogInterface.CreateOnClickListener onCreateClickListener) {
        this.onCreateClickListener = onCreateClickListener;
    }

    //AlertDialog
    public void show(final Callback.IAction<AlertDialog> callback) {
        this.rootBinding = bindRootLayout(activity);

        dialogTask = new DefaultAsyncTask(getContext()) {
            @Override
            protected void onPreExecute() {
                // TODO show pre loader
            }

            @Override
            protected void doInBackground(TaskResultHolder resultHolder) {
                prepareDialogData();
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                super.onPostExecute(taskResult);

                if (taskResult.getResultStatus().isSuccess()) {
                    initializeContentView(rootBinding, contentViewStubBinding, btnPanelViewStubBinding);
                    dialog = builder.show();

                    float width = getWidth();
                    float height = getHeight();

                    if (width <= 0)
                        width = WindowManager.LayoutParams.WRAP_CONTENT;

                    if (height <= 0)
                        height = WindowManager.LayoutParams.WRAP_CONTENT;

                    dialog.getWindow().setLayout((int) width, (int) height);

                    if (callback != null)
                        callback.call(dialog);
                }
            }
        }.executeOnThreadPool();
    }

    public void dismiss() {
        if (dialogTask != null && !dialogTask.isCancelled())
            dialogTask.cancel(true);

        if (dialog != null)
            dialog.dismiss();
    }

    public Context getContext() {
        return this.activity;
    }

    public FragmentManager getFragmentManager() {
        return this.activity.getSupportFragmentManager();
    }

    public ControlButton getOkButton() {
        if (btnPanelViewStubBinding == null)
            return null;

        View btnPanelRootView = btnPanelViewStubBinding.getRoot();

        if (btnPanelRootView == null)
            return null;

        ControlButton btn = (ControlButton) btnPanelRootView.findViewById(R.id.btnOk);

        return btn;
    }

    public ControlButton getDismissButton() {
        if (btnPanelViewStubBinding == null)
            return null;

        View btnPanelRootView = btnPanelViewStubBinding.getRoot();

        if (btnPanelRootView == null)
            return null;

        ControlButton btn = (ControlButton) btnPanelRootView.findViewById(R.id.btnDismiss);

        return btn;
    }

    public ControlButton getDeleteButton() {
        if (btnPanelViewStubBinding == null)
            return null;

        View btnPanelRootView = btnPanelViewStubBinding.getRoot();

        if (btnPanelRootView == null)
            return null;

        ControlButton btn = (ControlButton) btnPanelRootView.findViewById(R.id.btnDelete);

        return btn;
    }

    public ControlButton getCancelButton() {
        if (btnPanelViewStubBinding == null)
            return null;

        View btnPanelRootView = btnPanelViewStubBinding.getRoot();

        if (btnPanelRootView == null)
            return null;

        ControlButton btn = (ControlButton) btnPanelRootView.findViewById(R.id.btnCancel);

        return btn;
    }

    public ControlButton getCreateButton() {
        if (btnPanelViewStubBinding == null)
            return null;

        View btnPanelRootView = btnPanelViewStubBinding.getRoot();

        if (btnPanelRootView == null)
            return null;

        ControlButton btn = (ControlButton) btnPanelRootView.findViewById(R.id.btnCreate);

        return btn;
    }

    public boolean isOkButtonVisible() {
        return true;
    }

    public boolean isDismissButtonVisible() {
        return true;
    }

    public boolean isDeleteButtonVisible() {
        return false;
    }

    public boolean isCancelButtonVisible() {
        return true;
    }

    public boolean isCreateButtonVisible() {
        return true;
    }

    public boolean isHeadingVisible() {
        return true;
    }

    public boolean isHeadingCentered() {
        return false;
    }

    public boolean isButtonPanelVisible() {
        return isOkButtonVisible() || isDismissButtonVisible() || isDeleteButtonVisible();
    }

    public boolean isRounded() {
        return false;
    }

    public boolean iconOnlyOkButtons() {
        return false;
    }

    public boolean iconOnlyDismissButtons() {
        return false;
    }

    public boolean iconOnlyDeleteButtons() {
        return false;
    }

    public boolean iconOnlyCancelButtons() {
        return false;
    }

    public boolean iconOnlyCreateButtons() {
        return false;
    }

    public int getBackground() {
        return -1;
    }

    public int getBackgroundColor() {
        return -1;
    }

    public int getHeadingColor() {
        return -1;
    }

    public float getWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public float getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public ControlButtonType okButtonType() {
        return ControlButtonType.PRIMARY;
    }

    public ControlButtonType dismissButtonType() {
        return ControlButtonType.SECONDARY;
    }

    public ControlButtonType deleteButtonType() {
        return ControlButtonType.DANGER;
    }

    public ControlButtonType cancelButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    public ControlButtonType createButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    public int getPositiveButtonText() {
        return R.string.action_ok;
    }

    public int getNegativeButtonText() {
        return R.string.action_dismiss;
    }

    public int getDeleteButtonText() {
        return R.string.action_delete;
    }

    public int getCancelButtonText() {
        return R.string.action_cancel;
    }

    public int getCreateButtonText() {
        return R.string.action_create;
    }

    @Override
    public View getRootView() {
        return rootBinding.getRoot();
    }

    public void showNotification(View v, String message, boolean errorState) {
        if (rootBinding.notificationFrame == null)
            return;

        if (rootBinding.tvNotificationMessage == null)
            return;

        NotificationType type = NotificationType.ERROR;

        int backgroundColor = activity.getResources().getColor(type.getInverseBackgroundColor());
        int textColor = activity.getResources().getColor(type.getInverseTextColor());

        LayerDrawable drawable = (LayerDrawable) activity.getResources().getDrawable(R.drawable.background_full_width_border);
        Drawable backgroundLayer = drawable.findDrawableByLayerId(R.id.backgroundLayer);
        backgroundLayer.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_OVER);

        rootBinding.tvNotificationMessage.setTextColor(textColor);
        rootBinding.tvNotificationMessage.setText(message);

        rootBinding.notificationFrame.setBackground(drawable);
        rootBinding.notificationFrame.setVisibility(View.VISIBLE);

    }

    public boolean isLiveValidationDisabled() {
        return liveValidationDisabled;
    }

    public void setLiveValidationDisabled(boolean liveValidationDisabled) {
        this.liveValidationDisabled = liveValidationDisabled;
    }

    public void disableLiveValidation(boolean disableLiveValidation) {
        ViewGroup root = (ViewGroup) contentViewStubBinding.getRoot();
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

    private void setNotificationContextForPropertyFields(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ControlPropertyEditField) {
                ((ControlPropertyEditField) child).setNotificationContext(this);
            } else if (child instanceof ViewGroup) {
                setNotificationContextForPropertyFields((ViewGroup) child);
            }
        }
    }

    // </editor-fold>
}
