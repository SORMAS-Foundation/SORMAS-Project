/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.component.VisualState;
import de.symeda.sormas.app.component.dialog.AbstractDialog;
import de.symeda.sormas.app.core.NotificationContext;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.util.ResultCallback;

public abstract class ControlPropertyEditField<T> extends ControlPropertyField<T> {

	// Views

	private TextView labelRequired;
	private TextView labelSoftRequired;
	private TextView labelError;
	private TextView labelWarning;

	// Attributes

	protected String hint;
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
	private ResultCallback<Boolean> validationCallback;

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

	public abstract void setHint(String hint);

	// Instance methods

	private void initializePropertyEditField(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ControlPropertyEditField, 0, 0);

			try {
				hint = a.getString(R.styleable.ControlPropertyEditField_hint);
				required = a.getBoolean(R.styleable.ControlPropertyEditField_required, false);
				softRequired = a.getBoolean(R.styleable.ControlPropertyEditField_softRequired, false);
			} finally {
				a.recycle();
			}
		}
	}

	public void enableErrorState(String errorMessage) {
		this.hasError = true;
		this.errorMessage = errorMessage;

		changeErrorState();
	}

	public void enableErrorState(int messageResourceId) {
		String message = "";

		if (messageResourceId != -1) {
			message = getResources().getString(messageResourceId);
		}

		enableErrorState(message);
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

	public void enableWarningState(int messageResourceId) {
		String message = "";
		if (messageResourceId != -1) {
			message = getResources().getString(messageResourceId);
		}

		enableWarningState(message);
	}

	public void enableWarningState(String message) {
		// Error has priority over warning
		if (hasError) {
			return;
		}

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
			if (notificationContext instanceof AbstractDialog) {
				NotificationHelper.showDialogNotification(notificationContext, NotificationType.ERROR, errorMessage);
			} else {
				NotificationHelper.showNotification(notificationContext, NotificationType.ERROR, errorMessage);
			}
		}
	}

	protected void showWarningNotification() {
		if (hasWarning && notificationContext != null && warningMessage != null) {
			if (notificationContext instanceof AbstractDialog) {
				NotificationHelper.showDialogNotification(notificationContext, NotificationType.WARNING, warningMessage);
			} else {
				NotificationHelper.showNotification(notificationContext, NotificationType.WARNING, warningMessage);
			}
		}
	}

	protected void hideNotification() {
		// TODO Ideally only hide notifications shown by this field
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

	/**
	 * @return true if an error is set, false if not
	 */
	public boolean setErrorIfEmpty() {
		if (!required || !isEnabled()) {
			return false;
		}

		if (getValue() == null || (this instanceof ControlTextEditField && ((String) getValue()).isEmpty())) {
			enableErrorState(R.string.validation_error_required);
			return true;
		}

		return false;
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
			labelError.setVisibility(GONE);
			labelSoftRequired.setVisibility(GONE);
			labelRequired.setVisibility(GONE);

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

		labelRequired = this.findViewById(R.id.required_indicator);
		labelSoftRequired = this.findViewById(R.id.soft_required_indicator);
		labelError = this.findViewById(R.id.error_indicator);
		labelWarning = this.findViewById(R.id.warning_indicator);
		setRequired(required);
		setSoftRequired(softRequired);
		setWarning(hasWarning);

		if (labelRequired != null) {
			labelRequired.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (notificationContext != null && errorMessage != null) {
						showErrorNotification();
					}
				}
			});
		}

		if (labelError != null) {
			labelError.setVisibility(GONE);
			labelError.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (notificationContext != null && errorMessage != null) {
						showErrorNotification();
					}
				}
			});
		}

		if (labelWarning != null) {
			labelWarning.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (notificationContext != null && warningMessage != null) {
						showWarningNotification();
					}
				}
			});
		}

		// Validation
		addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				((ControlPropertyEditField) field).disableErrorState();
				if (!liveValidationDisabled) {
					((ControlPropertyEditField) field).setErrorIfEmpty();

					if (validationCallback != null) {
						validationCallback.call();
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
		super.setEnabled(enabled);
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
			disableErrorState();
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

		if (userEditRight != null && !ConfigProvider.hasUserRight(userEditRight)) {
			changeVisualState(VisualState.DISABLED);
		}
	}

	public VisualState getVisualState() {
		return visualState;
	}

	public boolean isHasError() {
		return hasError;
	}

	public boolean isLiveValidationDisabled() {
		return liveValidationDisabled;
	}

	public void setLiveValidationDisabled(boolean liveValidationDisabled) {
		if (this.liveValidationDisabled != liveValidationDisabled) {
			this.liveValidationDisabled = liveValidationDisabled;
			if (liveValidationDisabled && hasError) {
				disableErrorState();
			}
		}
	}

	public static void applyLiveValidationDisabledToChildren(ViewGroup parent, boolean liveValidationDisabled) {
		if (parent == null)
			return;
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ControlPropertyEditField) {
				((ControlPropertyEditField) child).setLiveValidationDisabled(liveValidationDisabled);
			} else if (child instanceof ViewGroup) {
				applyLiveValidationDisabledToChildren((ViewGroup) child, liveValidationDisabled);
			}
		}
	}

	public ResultCallback<Boolean> getValidationCallback() {
		return validationCallback;
	}

	public void setValidationCallback(ResultCallback<Boolean> validationCallback) {
		this.validationCallback = validationCallback;
	}

	public void setNotificationContext(NotificationContext notificationContext) {
		this.notificationContext = notificationContext;
	}

	@BindingAdapter("hint")
	public static void setHint(ControlTextEditField view, String hint) {
		view.setHint(hint);
	}
}
