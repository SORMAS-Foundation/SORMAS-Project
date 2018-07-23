package de.symeda.sormas.app.component.controls;

import android.app.Notification;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.BaseTeboAlertDialog;
import de.symeda.sormas.app.core.Callback;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;

public abstract class ControlPropertyEditField<T> extends ControlPropertyField<T> {

    // Views

    private TextView labelRequired;
    private TextView labelSoftRequired;
    private TextView labelError;
    private TextView labelWarning;

    // Attributes

    private String hint;
    protected boolean required;
    private boolean softRequired;

    // Other fields

    private UserRight userEditRight;
    protected VisualState visualState;
    private NotificationContext notificationContext;
    protected boolean hasError;
    protected boolean hasWarning;
    private String errorMessage;
    private String warningMessage;
    private boolean liveValidationDisabled;
    private Callback.IAction<NotificationContext> validationCallback;

    // Constructors

    public ControlPropertyEditField(Context context) {
        super(context);
        initializePropertyEditField(context, null);
    }

    public ControlPropertyEditField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePropertyEditField(context, attrs);
    }

    public ControlPropertyEditField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePropertyEditField(context, attrs);
    }

    // Abstract methods

    protected abstract void changeVisualState(VisualState state);

    protected abstract void setHint(String hint);

    // Instance methods

    private void initializePropertyEditField(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ControlPropertyEditField,
                    0, 0);

            try {
                hint = a.getString(R.styleable.ControlPropertyEditField_hint);
                required = a.getBoolean(R.styleable.ControlPropertyEditField_required, false);
                softRequired = a.getBoolean(R.styleable.ControlPropertyEditField_softRequired, false);
            } finally {
                a.recycle();
            }
        }
    }

    public void enableErrorState(NotificationContext notificationContext, String errorMessage) {
        if (notificationContext == null) {
            notificationContext = (NotificationContext) getContext();
        }

        this.notificationContext = notificationContext;
        this.hasError = true;
        this.errorMessage = errorMessage;

        changeErrorState();

    }

    public void enableErrorState(NotificationContext notificationContext, int messageResourceId) {
        String message = "";

        if (messageResourceId != -1) {
            message = getResources().getString(messageResourceId);
        }

        enableErrorState(notificationContext, message);
    }

    public void disableErrorState() {
        this.hasError = false;
        this.errorMessage = null;

        // Re-enable warning state if a warning is present
        if (hasWarning) {
            setWarning(true);
        }

        changeErrorState();
    }

    public void enableWarningState(NotificationContext notificationContext, int messageResourceId) {
        // Error has priority over warning
        if (hasError) {
            return;
        }

        if (notificationContext == null) {
            notificationContext = (NotificationContext) getContext();
        }

        String message = "";

        if (messageResourceId != -1) {
            message = getResources().getString(messageResourceId);
        }

        this.notificationContext = notificationContext;
        this.hasWarning = true;
        this.warningMessage = message;

        changeWarningState();
    }

    public void disableWarningState() {
        this.hasWarning = false;
        this.warningMessage = null;

        changeWarningState();
    }

    /**
     * Displays the error notification if the field is focused and has an error. Displays the
     * warning notification if the field is focused and has a warning, but no error.
     */
    protected void showOrHideNotifications(boolean hasFocus) {
        if (hasError) {
            if (hasFocus) {
                showErrorNotification();
            }
        } else {
            hideNotification();

            if (hasWarning) {
                if (hasFocus) {
                    showWarningNotification();
                }
            } else {
                hideNotification();
            }
        }
    }

    protected void showErrorNotification() {
        if (hasError && notificationContext != null && errorMessage != null) {
            if (notificationContext instanceof BaseTeboAlertDialog) {
                NotificationHelper.showDialogNotification(notificationContext, NotificationType.ERROR, errorMessage);
            } else {
                NotificationHelper.showNotification(notificationContext, NotificationType.ERROR, errorMessage);
            }
        }
    }

    protected void showWarningNotification() {
        if (hasWarning && notificationContext != null && warningMessage != null) {
            if (notificationContext instanceof BaseTeboAlertDialog) {
                NotificationHelper.showDialogNotification(notificationContext, NotificationType.ERROR, errorMessage);
            } else {
                NotificationHelper.showNotification(notificationContext, NotificationType.ERROR, errorMessage);
            }
        }
    }

    protected void hideNotification() {
        if (notificationContext != null) {
            NotificationHelper.hideNotification(notificationContext);
        }
    }

    public void setRequired(boolean required) {
        this.required = required;

        if (labelRequired != null) {
            if (required) {
                labelRequired.setVisibility(VISIBLE);

                if (labelSoftRequired != null) {
                    labelSoftRequired.setVisibility(GONE);
                }
            } else {
                labelRequired.setVisibility(GONE);
            }
        }
    }

    public void setErrorIfEmpty(NotificationContext notificationContext) {
        if (!required) {
            return;
        }

        if (getValue() == null
                || (this instanceof ControlTextEditField
                && ((String) getValue()).isEmpty())) {
            enableErrorState(notificationContext, (R.string.validation_error_required));
        } else {
            disableErrorState();
        }
    }

    public void setSoftRequired(boolean softRequired) {
        if (labelSoftRequired != null) {
            if (softRequired && !required) {
                labelSoftRequired.setVisibility(VISIBLE);
            } else {
                labelSoftRequired.setVisibility(GONE);
            }
        }
    }

    public void setWarning(boolean warning) {
        if (labelWarning != null) {
            if (warning) {
                labelWarning.setVisibility(VISIBLE);
            } else {
                labelWarning.setVisibility(GONE);
            }
        }
    }

    private void changeErrorState() {
        if (!this.isEnabled()) {
            return;
        }

        if (hasError) {
            changeVisualState(VisualState.ERROR);
            labelError.setVisibility(VISIBLE);
            labelSoftRequired.setVisibility(GONE);
            labelRequired.setVisibility(GONE);
        } else if (this.isFocused()) {
            changeVisualState(VisualState.FOCUSED);
            labelError.setVisibility(GONE);
            if (required) {
                labelRequired.setVisibility(VISIBLE);
            } else if (softRequired) {
                labelSoftRequired.setVisibility(VISIBLE);
            }
            hideNotification();
        } else {
            changeVisualState(VisualState.NORMAL);
            labelError.setVisibility(GONE);
            if (required) {
                labelRequired.setVisibility(VISIBLE);
            } else if (softRequired) {
                labelSoftRequired.setVisibility(VISIBLE);
            }
            hideNotification();
        }
    }

    private void changeWarningState() {
        if (!this.isEnabled()) {
            return;
        }

        if (hasWarning) {
            setWarning(true);
        } else {
            setWarning(false);
            hideNotification();
        }
    }

    // Overrides

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        labelRequired = (TextView) this.findViewById(R.id.required_indicator);
        labelSoftRequired = (TextView) this.findViewById(R.id.soft_required_indicator);
        labelError = (TextView) this.findViewById(R.id.error_indicator);
        labelError.setVisibility(GONE);
        labelWarning = (TextView) this.findViewById(R.id.warning_indicator);
        setRequired(required);
        setSoftRequired(softRequired);
        setWarning(hasWarning);

        labelRequired.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationContext != null && errorMessage != null) {
                    showErrorNotification();
                }
            }
        });

        labelError.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationContext != null && errorMessage != null) {
                    showErrorNotification();
                }
            }
        });

        labelWarning.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationContext != null && warningMessage != null) {
                    showWarningNotification();
                }
            }
        });

        // Validation
        addValueChangedListener(new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                if (!liveValidationDisabled) {
                    ((ControlPropertyEditField) field).setErrorIfEmpty(notificationContext);

                    if (validationCallback != null) {
                        validationCallback.call(notificationContext);
                    }
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setHint(hint);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            if (hasError) {
                changeVisualState(VisualState.ERROR);
            } else if (isFocused()) {
                changeVisualState(VisualState.FOCUSED);
            } else {
                changeVisualState(VisualState.NORMAL);
            }
        } else {
            changeVisualState(VisualState.DISABLED);
        }
    }

    // Data binding, getters & setters

    public String getHint() {
        return hint;
    }

    public UserRight getUserEditRight() {
        return userEditRight;
    }

    public void setUserEditRight(UserRight userEditRight) {
        this.userEditRight = userEditRight;

        if (userEditRight != null && !ConfigProvider.getUser().hasUserRight(userEditRight)) {
            changeVisualState(VisualState.DISABLED);
        }
    }

    public VisualState getVisualState() {
        return visualState;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setLiveValidationDisabled(boolean liveValidationDisabled) {
        this.liveValidationDisabled = liveValidationDisabled;
    }

    public Callback.IAction<NotificationContext> getValidationCallback() {
        return validationCallback;
    }

    public void setValidationCallback(Callback.IAction<NotificationContext> validationCallback) {
        this.validationCallback = validationCallback;
    }

}