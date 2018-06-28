package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;

public abstract class ControlPropertyEditField<T> extends ControlPropertyField<T> {

    // Views

    private TextView labelRequired;
    private TextView labelSoftRequired;
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

    public abstract void changeVisualState(VisualState state);

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

    public void enableErrorState(NotificationContext notificationContext, int messageResourceId) {
        String message = "";

        if (messageResourceId != -1) {
            message = getResources().getString(messageResourceId);
        }

        this.notificationContext = notificationContext;
        this.hasError = true;
        this.errorMessage = message;

        changeErrorState();
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
            NotificationHelper.showNotification(notificationContext, NotificationType.ERROR, errorMessage);
        }
    }

    protected void showWarningNotification() {
        if (hasWarning && notificationContext != null && warningMessage != null) {
            NotificationHelper.showNotification(notificationContext, NotificationType.WARNING, warningMessage);
        }
    }

    protected void hideNotification() {
        if (notificationContext != null) {
            NotificationHelper.hideNotification(notificationContext);
        }
    }

    public void setRequired(boolean required) {
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
        } else if (this.isFocused()) {
            changeVisualState(VisualState.FOCUSED);
            hideNotification();
        } else {
            changeVisualState(VisualState.NORMAL);
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

        labelWarning.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationContext != null && warningMessage != null) {
                    showWarningNotification();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setHint(hint);
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

}