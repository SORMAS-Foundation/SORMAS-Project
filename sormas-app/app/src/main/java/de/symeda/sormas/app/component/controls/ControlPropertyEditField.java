package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.VibrationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;

public abstract class ControlPropertyEditField<T> extends ControlPropertyField<T> {

    // Views

    private TextView labelRequired;
    private TextView labelSoftRequired;
    private TextView labelMinorError;

    // Attributes

    private String hint;
    private boolean required;
    private boolean softRequired;

    // Other fields

    private UserRight userRight;
    protected VisualState visualState;
    private NotificationContext notificationContext;
    protected boolean hasError;
    protected boolean hasMinorError;
    private String errorMessage;
    private String minorErrorMessage;

    // Constructors

    public ControlPropertyEditField(Context context) {
        super(context);
        initializePropertyFieldViews(context, null);
    }

    public ControlPropertyEditField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePropertyFieldViews(context, attrs);
    }

    public ControlPropertyEditField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePropertyFieldViews(context, attrs);
    }

    // Abstract methods

    public abstract void changeVisualState(VisualState state);

    protected abstract void setHint(String hint);

    // Instance methods

    private void initializePropertyFieldViews(Context context, AttributeSet attrs) {
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

        // Re-enable minor error state if a minor error is present
        if (hasMinorError) {
            setMinorError(true);
        }

        changeErrorState();
    }

    public void enableMinorErrorState(NotificationContext notificationContext, int messageResourceId) {
        // Error has priority over minor error
        if (hasError) {
            return;
        }

        String message = "";

        if (messageResourceId != -1) {
            message = getResources().getString(messageResourceId);
        }

        this.notificationContext = notificationContext;
        this.hasMinorError = true;
        this.minorErrorMessage = message;

        changeMinorErrorState();
    }

    public void disableMinorErrorState() {
        this.hasMinorError = false;
        this.minorErrorMessage = null;

        changeMinorErrorState();
    }

    protected void showErrorNotification() {
        if (hasError && notificationContext != null && errorMessage != null) {
            NotificationHelper.showNotification(notificationContext, NotificationType.ERROR, errorMessage);
        }
    }

    protected void hideErrorNotification() {
        if (notificationContext != null) {
            NotificationHelper.hideNotification(notificationContext);
        }
    }

    protected void showMinorErrorNotification() {
        if (hasMinorError && notificationContext != null && minorErrorMessage != null) {
            NotificationHelper.showNotification(notificationContext, NotificationType.WARNING, minorErrorMessage);
        }
    }

    protected void hideMinorErrorNotification() {
        if (notificationContext != null) {
            NotificationHelper.hideDialogNotification(notificationContext);
        }
    }

    public void setRequired(boolean required) {
        if (labelRequired != null) {
            if (required) {
                labelRequired.setVisibility(VISIBLE);

                if (labelSoftRequired != null) {
                    labelSoftRequired.setVisibility(GONE);
                }

                if (labelMinorError != null) {
                    labelMinorError.setVisibility(GONE);
                }
            } else {
                labelRequired.setVisibility(GONE);
            }
        }
    }

    public void setSoftRequired(boolean value) {
        if (labelSoftRequired != null) {
            if (value && !required && !hasMinorError) {
                labelSoftRequired.setVisibility(VISIBLE);
            } else {
                labelSoftRequired.setVisibility(GONE);
            }
        }

    }

    public void setMinorError(boolean value) {
        if (labelMinorError != null) {
            if (value && !required) {
                labelMinorError.setVisibility(VISIBLE);

                if (labelSoftRequired != null) {
                    labelSoftRequired.setVisibility(GONE);
                }
            } else {
                labelMinorError.setVisibility(GONE);
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
            hideErrorNotification();
        } else {
            changeVisualState(VisualState.NORMAL);
            hideErrorNotification();
        }
    }

    private void changeMinorErrorState() {
        if (!this.isEnabled()) {
            return;
        }

        if (hasMinorError) {
            setMinorError(true);
        } else {
            setMinorError(false);
            hideMinorErrorNotification();
        }
    }

    // Overrides

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        labelRequired = (TextView) this.findViewById(R.id.required_indicator);
        labelSoftRequired = (TextView) this.findViewById(R.id.soft_required_indicator);
        labelMinorError = (TextView) this.findViewById(R.id.minor_error_indicator);
        setRequired(required);
        setSoftRequired(softRequired);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setHint(hint);
    }

    // Data binding, getters & setters

    public UserRight getUserRight() {
        return userRight;
    }

    public void setUserRight(UserRight userRight) {
        this.userRight = userRight;

        if (userRight != null && !ConfigProvider.getUser().hasUserRight(userRight)) {
            changeVisualState(VisualState.DISABLED);
        }
    }

}