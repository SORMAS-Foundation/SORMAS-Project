package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.INotificationContext;
import de.symeda.sormas.app.core.VibrationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;

/**
 * Created by Orson on 28/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public abstract class EditTeboPropertyField<T> extends TeboPropertyField<T> {

    private VisualState state;
    private INotificationContext communicator;
    private boolean errorState;
    private String errorMessage;
    private OnInputErrorListener onInputErrorListener;
    private OnShowInputErrorListener onShowInputErrorListener;
    private OnHideInputErrorListener onHideInputErrorListener;

    private TextView lblRequired;
    private TextView lblSoftRequired;

    private String hint;
    private boolean required;

    public EditTeboPropertyField(Context context) {
        super(context);
        initializePropertyFieldViews(context, null, 0);
    }

    public EditTeboPropertyField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePropertyFieldViews(context, attrs, 0);
    }

    public EditTeboPropertyField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializePropertyFieldViews(context, attrs, defStyle);
    }

    private void initializePropertyFieldViews(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.EditTeboPropertyField,
                    0, 0);

            try {
                hint = a.getString(R.styleable.EditTeboPropertyField_hint);
                required = a.getBoolean(R.styleable.EditTeboPropertyField_required, false);
            } finally {
                a.recycle();
            }
        }
    }




    public void setVisualState(VisualState state) {
        if (state == VisualState.ERROR)
            return;

        this.state = state;
        changeVisualState(state);
    }

    public VisualState getVisualState() {
        return this.state;
    }

    public boolean inErrorState() {
        return errorState;
    }



    public void enableErrorState(INotificationContext communicator, int messageResId) {
        String message = "";

        if (messageResId != -1) {
            message  = getResources().getString(messageResId);
        }

        this.communicator = communicator;
        this.errorState = true;
        this.errorMessage = message;
        //this.txtControlInput.setError(this.errorMessage);

        if (this.onInputErrorListener != null)
            this.onInputErrorListener.onInputErrorChange(this, this.errorMessage, errorState);

    }

    public void enableErrorState(INotificationContext communicator, String message) {
        this.communicator = communicator;
        this.errorState = true;
        this.errorMessage = message;
        //this.txtControlInput.setError(this.errorMessage);

        if (this.onInputErrorListener != null)
            this.onInputErrorListener.onInputErrorChange(this, this.errorMessage, errorState);

    }

    public void enableErrorState(INotificationContext communicator, int messageResId, boolean showNotification) {
        String message = "";

        if (messageResId != -1) {
            message  = getResources().getString(messageResId);
        }

        this.communicator = communicator;
        this.errorState = true;
        this.errorMessage = message;
        //this.txtControlInput.setError(this.errorMessage);

        if (showNotification) {

        }

        if (this.onInputErrorListener != null)
            this.onInputErrorListener.onInputErrorChange(this, this.errorMessage, errorState);

    }

    public void enableErrorState(INotificationContext communicator, String message, boolean showNotification) {
        this.communicator = communicator;
        this.errorState = true;
        this.errorMessage = message;
        //this.txtControlInput.setError(this.errorMessage);

        if (showNotification) {

        }

        if (this.onInputErrorListener != null)
            this.onInputErrorListener.onInputErrorChange(this, this.errorMessage, errorState);

    }

    public void disableErrorState(INotificationContext communicator) {
        this.communicator = communicator;
        this.errorState = false;
        this.errorMessage = "";
        //this.txtControlInput.setError(null);


        if (this.onInputErrorListener != null)
            this.onInputErrorListener.onInputErrorChange(this, this.errorMessage, errorState);

    }

    protected void showNotification() {
        if (!errorState || communicator == null || errorMessage == null || errorMessage.isEmpty())
            return;

        NotificationHelper.showNotification(communicator, NotificationType.ERROR, errorMessage);

        if (onShowInputErrorListener != null) {
            onShowInputErrorListener.onShowInputErrorShowing(this, errorMessage, errorState);
        }
    }

    protected void showNotification(boolean showNotification) {
        if (!errorState || communicator == null || errorMessage == null || errorMessage.isEmpty())
            return;


        if (showNotification) {
            NotificationHelper.showNotification(communicator, NotificationType.ERROR, errorMessage);
        }

        if (onShowInputErrorListener != null) {
            onShowInputErrorListener.onShowInputErrorShowing(this, errorMessage, errorState);
        }
    }

    protected void hideNotification() {
        if (communicator == null)
            return;

        NotificationHelper.hideNotification(communicator);

        if (onHideInputErrorListener != null) {
            onHideInputErrorListener.onInputErrorHiding(this, errorState);
        }
    }

    private void setOnInputErrorListener(OnInputErrorListener listener) {
        this.onInputErrorListener = listener;
    }

    public void setOnShowInputErrorListener(OnShowInputErrorListener listener) {
        this.onShowInputErrorListener = listener;
    }

    public void setOnHideInputErrorListener(OnHideInputErrorListener listener) {
        this.onHideInputErrorListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        lblRequired = (TextView) this.findViewById(R.id.lblRequired);
        lblSoftRequired = (TextView) this.findViewById(R.id.lblSoftRequired);
        setRequired(required);

        setOnInputErrorListener(new OnInputErrorListener() {
            @Override
            public void onInputErrorChange(View v, String message, boolean errorState) {
                if(!v.isEnabled())
                    return;

                if (errorState) {
                    VibrationHelper.onInputFieldError();
                    changeVisualState(VisualState.ERROR);

                    showNotification();
                } else if(v.isFocused()) {
                    changeVisualState(VisualState.FOCUSED);
                    hideNotification();
                } else {
                    changeVisualState(VisualState.NORMAL);
                    hideNotification();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //Set Hint
        setHint(hint);
    }

    public void changeVisualState(VisualState state) {
        changeVisualState(state, null);
    }

    public abstract void changeVisualState(VisualState state, UserRight editOrCreateUserRight);

    protected abstract void setHint(String hint);

    @Override
    public int getCaptionColor() {
        return getResources().getColor(R.color.controlTextColor);
    }

    public void setRequired(boolean value) {
        if(lblRequired == null)
            return;

        if(lblSoftRequired == null)
            return;

        lblRequired.setVisibility((value)? VISIBLE : GONE);
        lblSoftRequired.setVisibility(GONE);
    }

    public void setSoftRequired(boolean value) {
        if(lblRequired == null)
            return;

        if(lblSoftRequired == null)
            return;

        lblRequired.setVisibility(GONE);
        lblSoftRequired.setVisibility((value)? VISIBLE : GONE);
    }

    public boolean isRequired() {
        if(lblRequired == null)
            return false;

        return (lblRequired.getVisibility() == VISIBLE)? true : false;
    }
}
