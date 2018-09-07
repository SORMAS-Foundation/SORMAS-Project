package de.symeda.sormas.app.component.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButton;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.databinding.DialogRootLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public abstract class AbstractDialog implements NotificationContext {

    public static final String TAG = AbstractDialog.class.getSimpleName();

    private FragmentActivity activity;
    private int rootLayoutId;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private DialogRootLayoutBinding rootBinding;
    private ViewDataBinding contentBinding;
    private ViewDataBinding buttonPanelBinding;
    private int contentLayoutResourceId;
    private int buttonPanelLayoutResourceId;
    private DialogViewConfig config;
    private boolean liveValidationDisabled;

    private Callback positiveCallback;
    private Callback negativeCallback;
    private Callback deleteCallback;
    private Callback cancelCallback;
    private Callback createCallback;

    public AbstractDialog(final FragmentActivity activity, int rootLayoutId, int contentLayoutResourceId,
                          int buttonPanelLayoutResourceId, int headingResourceId, int subHeadingResourceId) {

        this.builder = new AlertDialog.Builder(activity);
        this.activity = activity;
        this.rootLayoutId = rootLayoutId;
        this.contentLayoutResourceId = contentLayoutResourceId;
        this.buttonPanelLayoutResourceId = buttonPanelLayoutResourceId;

        Resources resources = activity.getResources();

        String heading = resources.getString(headingResourceId);
        String subHeading = resources.getString(subHeadingResourceId);
        String positiveLabel = resources.getString(getPositiveButtonText());
        String negativeLabel = resources.getString(getNegativeButtonText());
        String deleteLabel = resources.getString(getDeleteButtonText());
        String cancelLabel = resources.getString(getCancelButtonText());
        String createLabel = resources.getString(getCreateButtonText());

        this.config = new DialogViewConfig(heading, subHeading, positiveLabel, negativeLabel, deleteLabel, cancelLabel, createLabel);
    }

    protected View getRoot() {
        return rootBinding.getRoot();
    }

    protected FragmentActivity getActivity() {
        return activity;
    }

    private DialogRootLayoutBinding bindRootLayout(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final DialogRootLayoutBinding binding = DataBindingUtil.inflate(inflater, this.rootLayoutId, null, false);
        final String layoutName = context.getResources().getResourceEntryName(this.rootLayoutId);

        // Bind required variables to layout
        bindDialog(binding, layoutName);
        bindConfig(binding, layoutName);

        // Hide notification frame on click
        binding.notificationFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });

        // Inflate dialog content
        binding.dialogContent.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                contentBinding = DataBindingUtil.bind(inflated);
                String layoutName = context.getResources().getResourceEntryName(contentLayoutResourceId);
                bindConfig(contentBinding, layoutName);
                setBindingVariable(context, contentBinding, layoutName);
            }
        });

        ViewStub dialogContent = binding.dialogContent.getViewStub();
        dialogContent.setLayoutResource(contentLayoutResourceId);
        dialogContent.inflate();

        // Inflate dialog button panel
        binding.dialogButtonPanel.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                buttonPanelBinding = DataBindingUtil.bind(inflated);
                String layoutName = context.getResources().getResourceEntryName(buttonPanelLayoutResourceId);
                bindConfig(buttonPanelBinding, layoutName);
                bindDialog(buttonPanelBinding, layoutName);
            }
        });

        ViewStub buttonPanel = binding.dialogButtonPanel.getViewStub();
        buttonPanel.setLayoutResource(buttonPanelLayoutResourceId);
        buttonPanel.inflate();

        builder.setView(binding.getRoot());

        return binding;
    }

    private void bindDialog(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.dialog, this)) {
            Log.e(TAG, "There is no variable 'dialog' in layout " + layoutName);
        }
    }

    private void bindConfig(final ViewDataBinding binding, String layoutName) {
        if (!binding.setVariable(BR.config, this.config)) {
            Log.e(TAG, "There is no variable 'config' in layout " + layoutName);
        }
    }

    // Abstract methods

    protected abstract void setBindingVariable(Context context, ViewDataBinding binding, String layoutName);

    protected abstract void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding contentBinding, ViewDataBinding buttonPanelBinding);

    // Instance methods

    public void show() {
        this.rootBinding = bindRootLayout(activity);
        setNotificationContextForPropertyFields((ViewGroup) rootBinding.getRoot());
        initializeContentView(rootBinding, contentBinding, buttonPanelBinding);

        ControlButton positiveButton = getPositiveButton();
        if (positiveButton != null) {
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveCallback != null) {
                        positiveCallback.call();
                    }
                }
            });
        }

        ControlButton negativeButton = getNegativeButton();
        if (negativeButton != null) {
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeCallback != null) {
                        negativeCallback.call();
                    }
                }
            });
        }

        ControlButton createButton = getCreateButton();
        if (createButton != null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (createCallback != null) {
                        createCallback.call();
                    }
                }
            });
        }

        ControlButton cancelButton = getCancelButton();
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelCallback != null) {
                        cancelCallback.call();
                    }
                }
            });
        }

        ControlButton deleteButton = getDeleteButton();
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteCallback != null) {
                        deleteCallback.call();
                    }
                }
            });
        }

        dialog = builder.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public Context getContext() {
        return this.activity;
    }

    public FragmentManager getFragmentManager() {
        return this.activity.getSupportFragmentManager();
    }

    public ControlButton getPositiveButton() {
        if (buttonPanelBinding == null) {
            return null;
        }

        View buttonPanelRootView = buttonPanelBinding.getRoot();

        if (buttonPanelRootView == null) {
            return null;
        }

        return buttonPanelRootView.findViewById(R.id.button_positive);
    }

    public ControlButton getNegativeButton() {
        if (buttonPanelBinding == null) {
            return null;
        }

        View buttonPanelRootView = buttonPanelBinding.getRoot();

        if (buttonPanelRootView == null) {
            return null;
        }

        return buttonPanelRootView.findViewById(R.id.button_negative);
    }

    public ControlButton getCreateButton() {
        if (buttonPanelBinding == null) {
            return null;
        }

        View buttonPanelRootView = buttonPanelBinding.getRoot();

        if (buttonPanelRootView == null) {
            return null;
        }

        return buttonPanelRootView.findViewById(R.id.button_create);
    }

    public ControlButton getCancelButton() {
        if (buttonPanelBinding == null) {
            return null;
        }

        View buttonPanelRootView = buttonPanelBinding.getRoot();

        if (buttonPanelRootView == null) {
            return null;
        }

        return buttonPanelRootView.findViewById(R.id.button_cancel);
    }

    public ControlButton getDeleteButton() {
        if (buttonPanelBinding == null) {
            return null;
        }

        View buttonPanelRootView = buttonPanelBinding.getRoot();

        if (buttonPanelRootView == null) {
            return null;
        }

        return buttonPanelRootView.findViewById(R.id.button_delete);
    }

    public boolean isPositiveButtonVisible() {
        return true;
    }

    public boolean isNegativeButtonVisible() {
        return true;
    }

    public boolean isCancelButtonVisible() {
        return false;
    }

    public boolean isCreateButtonVisible() {
        return false;
    }

    public boolean isDeleteButtonVisible() {
        return false;
    }

    public boolean isHeadingVisible() {
        return true;
    }

    public boolean isHeadingCentered() {
        return false;
    }

    public boolean isButtonPanelVisible() {
        return isPositiveButtonVisible() || isNegativeButtonVisible() || isCancelButtonVisible()
                || isCreateButtonVisible() || isDeleteButtonVisible();
    }

    public boolean isRounded() {
        return false;
    }

    public boolean isPositiveButtonIconOnly() {
        return false;
    }

    public boolean isNegativeButtonIconOnly() {
        return false;
    }

    public ControlButtonType getPositiveButtonType() {
        return ControlButtonType.PRIMARY;
    }

    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.SECONDARY;
    }

    public ControlButtonType getDeleteButtonType() {
        return ControlButtonType.DANGER;
    }

    public ControlButtonType getCancelButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

    public ControlButtonType getCreateButtonType() {
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

    public void setLiveValidationDisabled(boolean liveValidationDisabled) {
        if (this.liveValidationDisabled != liveValidationDisabled) {
            this.liveValidationDisabled = liveValidationDisabled;
            applyLiveValidationDisabledToChildren();
        }
    }

    private void applyLiveValidationDisabledToChildren() {
        if (contentBinding == null) return;
        ViewGroup root = (ViewGroup) contentBinding.getRoot();
        ControlPropertyEditField.applyLiveValidationDisabledToChildren(root, liveValidationDisabled);
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

    public DialogViewConfig getConfig() {
        return config;
    }

    public void setPositiveCallback(Callback positiveCallback) {
        this.positiveCallback = positiveCallback;
    }

    public void setNegativeCallback(Callback negativeCallback) {
        this.negativeCallback = negativeCallback;
    }

    public void setDeleteCallback(Callback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public void setCancelCallback(Callback cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public void setCreateCallback(Callback createCallback) {
        this.createCallback = createCallback;
    }

    public void callPositiveCallback() {
        positiveCallback.call();
    }

    public void callNegativeCallback() {
        negativeCallback.call();
    }

    public void callDeleteCallback() {
        deleteCallback.call();
    }

    public void callCancelCallback() {
        cancelCallback.call();
    }

    public void callCreateCallback() {
        createCallback.call();
    }

}
