package de.symeda.sormas.app.component.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.TextView;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.OnHideInputErrorListener;
import de.symeda.sormas.app.component.OnShowInputErrorListener;
import de.symeda.sormas.app.component.TeboButtonType;
import de.symeda.sormas.app.core.NotificationType;
import de.symeda.sormas.app.databinding.DialogRootLayoutBinding;

/**
 * Created by Orson on 02/02/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */



public abstract class BaseTeboAlertDialog implements de.symeda.sormas.app.component.dialog.IDialogDismissOnClickListener,
        de.symeda.sormas.app.component.dialog.IDialogOkOnClickListener, de.symeda.sormas.app.component.dialog.IDialogDeleteOnClickListener, OnShowInputErrorListener, OnHideInputErrorListener {

    public static final String TAG = BaseTeboAlertDialog.class.getSimpleName();

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

    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.NegativeOnClickListener onNegativeClickListener;
    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener onPositiveClickListener;
    private de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.DeleteOnClickListener onDeleteClickListener;

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

        this.config = new de.symeda.sormas.app.component.dialog.DialogViewConfig(heading, subHeading, positiveLabel, negativeLabel, deleteLabel);
    }

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public void onOkClick(View v, Object item) {
        View viewRoot = getRoot();

        onOkClicked(v, item, viewRoot, contentViewStubBinding);

        if (onPositiveClickListener != null)
            onPositiveClickListener.onOkClick(v, item, viewRoot);
    }

    @Override
    public void onInputErrorShowing(View v, String message, boolean errorState) {
        if (rootBinding.notificationFrame == null)
            return;

        if (rootBinding.tvNotificationMessage == null)
            return;


        //TransitionManager.beginDelayedTransition(rootBinding.notificationFrame);
        //Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
        //rootBinding.notificationFrame.startAnimation(expandIn);

        NotificationType type = NotificationType.ERROR;

        LayerDrawable drawable = (LayerDrawable) activity.getResources().getDrawable(R.drawable.background_notification_dialog);
        int backgroundColor = activity.getResources().getColor(type.getInverseBackgroundColor());
        int textColor = activity.getResources().getColor(type.getInverseTextColor());


        //backgroundRowItem = (LayerDrawable) ContextCompat.getDrawable(this.layout.getContext(), R.drawable.background_list_activity_row);

        Drawable backgroundLayer = drawable.findDrawableByLayerId(R.id.backgroundLayer);
        backgroundLayer.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_OVER);
        //unreadListItemIndicator.setTint(this.layout.getContext().getResources().getColor(R.color.unreadIcon));
        //drawable.setAlpha(50);

        rootBinding.tvNotificationMessage.setTextColor(textColor);
        rootBinding.tvNotificationMessage.setText(message);

        rootBinding.notificationFrame.setBackground(drawable);
        rootBinding.notificationFrame.setVisibility(View.VISIBLE);
        //binding.notificationFrame.animate().translationY(0);

    }

    @Override
    public void onInputErrorHiding(View v, boolean errorState) {

    }




    @Override
    public void onDismissClick(View v, Object item) {
        dialog.dismiss();

        View viewRoot = getRoot();

        onDismissClicked(v, item, viewRoot, contentViewStubBinding);

        if (onNegativeClickListener != null)
            onNegativeClickListener.onDismissClick(v, item, viewRoot);
    }
    @Override
    public void onDeleteClick(View v, Object item) {
        final ConfirmationDialog confirmationDialog = new ConfirmationDialog(this.activity, R.string.heading_confirmation_dialog,
                R.string.heading_sub_confirmation_notification_dialog_delete, "", "");

        confirmationDialog.setOnPositiveClickListener(new de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object item, View viewRoot) {
                onDeleteClicked(v, item, viewRoot, contentViewStubBinding);

                if (onDeleteClickListener != null)
                    onDeleteClickListener.onDeleteClick(v, item, viewRoot);

                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.show();

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
        bindNotificationCallbacks(context, binding, layoutName);
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
                String layoutName = context.getResources().getResourceEntryName(contentLayoutResourceId);
                bindConfig(context, contentViewStubBinding, layoutName);
                bindNotificationCallbacks(context, contentViewStubBinding, layoutName);
                setBindingVariable(context, contentViewStubBinding, layoutName);
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
                bindDialog(context, btnPanelViewStubBinding, layoutName);
                bindConfig(context, btnPanelViewStubBinding, layoutName);
                bindButtonCallbacks(context, btnPanelViewStubBinding, layoutName);
                setBindingVariable(context, btnPanelViewStubBinding, layoutName);
            }
        });

        ViewStub buttonPanel = binding.vsDialogButtonPanel.getViewStub();
        buttonPanel.setLayoutResource(btnPanelLayoutResourceId);
        View buttonPanelInflated = buttonPanel.inflate();


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
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    private void bindConfig(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.config, this.config)) {
            Log.w(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    private void bindButtonCallbacks(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.dismissCallback, this)) {
            Log.w(TAG, "There is no variable 'dismissCallback' in layout " + layoutName);
        }

        /*if (!binding.setVariable(BR.showNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.hideNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
        }*/

        if (!binding.setVariable(BR.okCallback, this)) {
            Log.w(TAG, "There is no variable 'okCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.deleteCallback, this)) {
            Log.w(TAG, "There is no variable 'deleteCallback' in layout " + layoutName);
        }
    }

    private void bindNotificationCallbacks(final Context context, final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.showNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'showNotificationCallback' in layout " + layoutName);
        }

        if (!binding.setVariable(BR.hideNotificationCallback, this)) {
            Log.w(TAG, "There is no variable 'hideNotificationCallback' in layout " + layoutName);
        }
    }

    private void setBackground(ViewDataBinding binding) {
        if (binding == null)
            return;

        View rootView = binding.getRoot();

        if (rootView == null)
            return;

        View baseLayout = rootView.findViewById(R.id.base_layout);

        int backgroundResourceId = getBackground();

        if (backgroundResourceId > 0)
            baseLayout.setBackground(this.activity.getResources().getDrawable(backgroundResourceId));
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

    protected abstract void onOkClicked(View v, Object item, View rootView, ViewDataBinding contentBinding);

    protected abstract void onDismissClicked(View v, Object item, View rootView, ViewDataBinding contentBinding);

    protected abstract void onDeleteClicked(View v, Object item, View rootView, ViewDataBinding contentBinding);

    protected abstract void setBindingVariable(Context context, ViewDataBinding binding, String layoutName);

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

    public AlertDialog show() {
        this.rootBinding = bindRootLayout(activity);
        initializeContentView(rootBinding, contentViewStubBinding, btnPanelViewStubBinding);
        dialog = builder.show();


        float width = getWidth();
        float height = getHeight();

        if (width <= 0)
            width = WindowManager.LayoutParams.WRAP_CONTENT;

        if (height <= 0)
            height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout((int) width, (int) height);

        return dialog;
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public Context getContext() {
        return this.activity;
    }

    public FragmentManager getFragmentManager() {
        return this.activity.getSupportFragmentManager();
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

    public boolean isHeadingCentered() {
        return false;
    }

    public boolean isButtonPanelVisible() {
        return isOkButtonVisible() || isDismissButtonVisible() || isDeleteButtonVisible();
    }

    public boolean isRounded() {
        return false;
    }

    public boolean iconOnlyButtons() {
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

    public TeboButtonType okButtonType() {
        return TeboButtonType.BTN_PRIMARY;
    }

    public TeboButtonType dismissButtonType() {
        return TeboButtonType.BTN_SECONDARY;
    }

    public TeboButtonType deleteButtonType() {
        return TeboButtonType.BTN_DANGER;
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

    // </editor-fold>
}
